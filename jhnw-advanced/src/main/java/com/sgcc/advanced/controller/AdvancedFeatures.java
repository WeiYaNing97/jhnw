package com.sgcc.advanced.controller;
import com.alibaba.fastjson.JSON;
import com.sgcc.advanced.thread.AdvancedThreadPool;
import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.bean.BeanUtils;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.PathHelper;
import com.sgcc.share.webSocket.WebSocketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags = "运行分析")
@RestController
@RequestMapping("/advanced/AdvancedFeatures")
@Transactional(rollbackFor = Exception.class)
public class AdvancedFeatures {


    /**
     * 调用运行分析接口，根据扫描次数和功能名称，执行相应的操作。
     *
     * @param switchInformation 用户登录信息列表，格式为json字符串
     * @param scanNum           扫描次数
     * @param functionName      功能名称列表
     * @return 返回扫描结束字符串
     * @throws InterruptedException 线程中断异常
     * @throws IOException 文件写入异常
     */
    @ApiOperation("运行分析接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "switchInformation", value = "用户登录信息列表，格式为json字符串",dataTypeClass = List.class, required = true),
            @ApiImplicitParam(name = "scanNum", value = "扫描允许最大线程数",dataTypeClass = Long.class,required = true),
            @ApiImplicitParam(name = "functionName", value = "功能名称列表",dataTypeClass = List.class, required = true)
    })
    @PostMapping("/advancedFunction/{scanNum}/{functionName}")
    @MyLog(title = "运行分析", businessType = BusinessType.OTHER)
    public String advancedFunction(@RequestBody List<String> switchInformation, @PathVariable Long scanNum, @PathVariable List<String> functionName) {
        // 获取当前时间并格式化为字符串
        String simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // 初始化多线程参数列表
        // 预设多线程参数 Object[] 中的参数格式为： {mode,ip,name,password,port}
        List<SwitchParameters> switchParametersList = new ArrayList<>();

        // 遍历用户登录信息列表，转换为json格式的登录信息
        /*将字符串格式的用户登录信息 转化为json格式的登录信息*/
        for (String information:switchInformation){
            SwitchLoginInformation switchLoginInformation = JSON.parseObject(information, SwitchLoginInformation.class);
            SwitchParameters switchParameters = new SwitchParameters();
            switchParameters.setLoginUser(SecurityUtils.getLoginUser());
            switchParameters.setScanningTime(simpleDateFormat);
            BeanUtils.copyBeanProp(switchParameters,switchLoginInformation);
            switchParameters.setPort(Integer.valueOf(switchLoginInformation.getPort()).intValue());

            // 将多线程参数添加到列表中
            //连接方式，ip，用户名，密码，端口号
            switchParametersList.add(switchParameters);
        }

        // 创建线程池参数对象
        ParameterSet parameterSet = new ParameterSet();
        parameterSet.setSwitchParameters(switchParametersList);
        parameterSet.setLoginUser(SecurityUtils.getLoginUser());
        parameterSet.setThreadCount(Integer.valueOf(scanNum+"").intValue());

        try {
            // 调用高级功能线程池执行登录信息操作
            //boolean isRSA = true; //前端数据是否通过 RSA 加密后传入后端
            AdvancedThreadPool.switchLoginInformations(parameterSet, functionName,true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 打印扫描结束信息
        System.err.println("扫描结束");

        // 发送WebSocket消息，传输登录人姓名和问题简述
        //传输登陆人姓名 及问题简述
        WebSocketService.sendMessage(parameterSet.getLoginUser().getUsername(),"接收："+"扫描结束\r\n");

        try {
            // 将问题简述和问题路径写入文件
            PathHelper.writeDataToFile("接收："+"扫描结束\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 格式化当前时间加上10分钟后的时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime_10 = dateFormat.format(new Date(new Date().getTime() + 600000));

        // 等待条件满足后返回扫描结束字符串
        while (true){
            if (WebSocketService.userMap.get(parameterSet.getLoginUser().getUsername()) != null){
                // 如果WebSocket连接存在，则移除连接并返回扫描结束
                WebSocketService.userMap.remove(parameterSet.getLoginUser().getUsername());
                return "扫描结束";
            }
            if (dateFormat.format(new Date(new Date().getTime())).compareTo(nowTime_10) >=0 ){
                // 如果等待时间超过10分钟，则返回扫描结束
                return "扫描结束";
            }
        }
    }

}
