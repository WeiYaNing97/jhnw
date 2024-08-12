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

    @ApiOperation(value = "获取光衰数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ip", value = "交换机IP地址",dataTypeClass = String.class,required = true),
            @ApiImplicitParam(name = "port", value = "端口",dataTypeClass = String.class,required = true)
    })
    @GetMapping("/getOperationalAnalysisLogData")
    public static void obtainOperationalAnalysisLogDataInformation(String ip ,String port) {

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
        for (String s : strings) {
            if (s.indexOf(ip+")")!=-1 && s.indexOf(port)!=-1){
                String[] split = s.split("LightAttenuationComparison");
                if (split.length > 1) {

                    String[] timeSplit = s.split("\\]");
                    timeSplit[0] = timeSplit[0].substring(1);

                    LightAttenuationComparison switchLoginInformation = JSON.parseObject(split[1], LightAttenuationComparison.class);
                    if (switchLoginInformation.getSwitchIp().equals(ip)
                            && switchLoginInformation.getPort().equals(port)) {
                        out.println(timeSplit[0] +" RX: "+switchLoginInformation.getRxLatestNumber()+" TX: "+switchLoginInformation.getTxLatestNumber());
                    }
                }
            }
        }

    }

    @ApiOperation(value = "获取错误包数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ip", value = "交换机IP地址",dataTypeClass = String.class,required = true),
            @ApiImplicitParam(name = "port", value = "端口",dataTypeClass = String.class,required = true)
    })
    @GetMapping("/retrieveErrorPacketLogDataInformation")
    public static void retrieveErrorPacketLogDataInformation(String ip ,String port) {

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
        for (String s : strings) {
            if (s.indexOf(ip+")")!=-1 && s.indexOf(port)!=-1){
                String[] split = s.split("ErrorRate");
                if (split.length > 1) {

                    String[] timeSplit = s.split("\\]");
                    timeSplit[0] = timeSplit[0].substring(1);

                    ErrorRate errorRate = JSON.parseObject(split[1], ErrorRate.class);
                    if (errorRate.getSwitchIp().equals(ip)
                            && errorRate.getPort().equals(port)) {
                        out.println(timeSplit[0] +" Input: "+errorRate.getInputErrors()+" Output: "+errorRate.getOutputErrors() +" CRC: "+errorRate.getCrc());
                    }
                }
            }
        }

    }
}
