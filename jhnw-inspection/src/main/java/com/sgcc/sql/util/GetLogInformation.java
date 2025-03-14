package com.sgcc.sql.util;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.sgcc.advanced.domain.ErrorRate;
import com.sgcc.advanced.domain.LightAttenuationComparison;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.PathHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.*;

/**
 * @program: jhnw
 * @description: 获取日志信息
 * @author:
 * @create: 2024-06-24 16:39
 **/
@Api("获取日志文件数据")
@RestController
@RequestMapping("/GetLogInformation")
public class GetLogInformation {

    /**
     * 获取光衰数据
     *
     * @param ip 交换机IP地址，必填项
     * @param port 端口，必填项
     * @return 无返回值，通过控制台输出光衰数据
     */
    @ApiOperation(value = "获取光衰数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ip", value = "交换机IP地址",dataTypeClass = String.class,required = true),
            @ApiImplicitParam(name = "port", value = "端口",dataTypeClass = String.class,required = true)
    })
    @GetMapping("/getOperationalAnalysisLogData")
    public static void obtainOperationalAnalysisLogDataInformation(String ip ,String port) {/*String ip ,String port*/

        /*String ip = null;
        String port = null;*/
        /*String ip = "192.168.1.100";
        String port = "GigabitEthernet1/21";*/

        PathHelper pathHelper = new PathHelper();
        String logPath = (String) CustomConfigurationUtil.getValue("configuration.logPath", Constant.getProfileInformation());

        // 拼接完整路径
        String path = "";
        if (logPath != null && !logPath.equals("")){
            // 如果日志路径不为空，则拼接完整路径
            path = logPath + "\\" + "光衰.txt";
        }else {
            // 如果日志路径为空，则使用默认路径
            path = pathHelper.logPath + ".txt";
        }

        List<String> strings = pathHelper.ReadFileContent(path);
        List<LightAttenuationComparison> lightAttenuationComparisons = new ArrayList<>();
        for (String s : strings) {
            if (s.indexOf("LightAttenuationComparison")!=-1){
                String[] split = s.split("LightAttenuationComparison");
                if (split.length > 1) {

                    String[] timeSplit = s.split("\\]");
                    timeSplit[0] = timeSplit[0].substring(1);

                    LightAttenuationComparison switchLoginInformation = JSON.parseObject(split[1], LightAttenuationComparison.class);
                    if (ip!=null && port!=null) {
                        if (switchLoginInformation.getSwitchIp().equals(ip)
                                && switchLoginInformation.getPort().equals(port)) {
                            out.println(timeSplit[0] +" RX: "+switchLoginInformation.getRxLatestNumber()+" TX: "+switchLoginInformation.getTxLatestNumber());
                            lightAttenuationComparisons.add(switchLoginInformation);
                        }
                    }else if (ip!=null ){
                        if (switchLoginInformation.getSwitchIp().equals(ip)) {
                            out.println(timeSplit[0] +" RX: "+switchLoginInformation.getRxLatestNumber()+" TX: "+switchLoginInformation.getTxLatestNumber());
                            lightAttenuationComparisons.add(switchLoginInformation);
                        }
                    }else{
                        out.println(timeSplit[0] +" RX: "+switchLoginInformation.getRxLatestNumber()+" TX: "+switchLoginInformation.getTxLatestNumber());
                        lightAttenuationComparisons.add(switchLoginInformation);
                    }
                }
            }
        }
        lightAttenuationComparisons.stream().forEach(System.out::println);

        retrieveErrorPacketLogDataInformation(ip,port);
    }

    /**
     * 获取错误包数据
     *
     * @param ip 交换机IP地址，必填项
     * @param port 端口，必填项
     * @return 无返回值，通过控制台输出错误包数据
     */
    @ApiOperation(value = "获取错误包数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ip", value = "交换机IP地址",dataTypeClass = String.class,required = true),
            @ApiImplicitParam(name = "port", value = "端口",dataTypeClass = String.class,required = true)
    })
    @GetMapping("/retrieveErrorPacketLogDataInformation")
    public static void retrieveErrorPacketLogDataInformation(String ip ,String port) {/* String ip ,String port */

        /*String ip = null;
        String port = null;*/
        /*String ip = "192.168.1.100";
        String port = "GigabitEthernet0/0/5";*/

        PathHelper pathHelper = new PathHelper();
        String logPath = (String) CustomConfigurationUtil.getValue("configuration.logPath", Constant.getProfileInformation());

        // 拼接完整路径
        String path = "";
        if (logPath != null && !logPath.equals("")){
            // 如果日志路径不为空，则拼接完整路径
            path = logPath + "\\" + "错误包.txt";
        }else {
            // 如果日志路径为空，则使用默认路径
            path = pathHelper.logPath + ".txt";
        }

        List<String> strings = pathHelper.ReadFileContent(path);
        List<ErrorRate> errorRateList = new ArrayList<>();
        for (String s : strings) {
            if (s.indexOf("ErrorRate")!=-1){
                String[] split = s.split("ErrorRate");
                if (split.length > 1) {

                    String[] timeSplit = s.split("\\]");
                    timeSplit[0] = timeSplit[0].substring(1);

                    ErrorRate errorRate = JSON.parseObject(split[1], ErrorRate.class);

                    if (ip!=null && port!=null) {
                        if (errorRate.getSwitchIp().equals(ip)
                                && errorRate.getPort().equals(port)) {
                            out.println(timeSplit[0] +" Input: "+errorRate.getInputErrors()+" Output: "+errorRate.getOutputErrors() +" CRC: "+errorRate.getCrc());
                            errorRateList.add(errorRate);
                        }
                    }else if (ip!=null ){
                        if (errorRate.getSwitchIp().equals(ip)) {
                            out.println(timeSplit[0] +" Input: "+errorRate.getInputErrors()+" Output: "+errorRate.getOutputErrors() +" CRC: "+errorRate.getCrc());
                            errorRateList.add(errorRate);
                        }
                    }else{
                        out.println(timeSplit[0] +" Input: "+errorRate.getInputErrors()+" Output: "+errorRate.getOutputErrors() +" CRC: "+errorRate.getCrc());
                        errorRateList.add(errorRate);
                    }
                }
            }
        }
        errorRateList.stream().forEach(System.out::println);
    }
}
