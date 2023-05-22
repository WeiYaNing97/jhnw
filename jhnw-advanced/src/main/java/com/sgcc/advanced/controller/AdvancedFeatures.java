package com.sgcc.advanced.controller;

import com.alibaba.fastjson.JSON;
import com.sgcc.advanced.thread.AdvancedThreadPool;
import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.bean.BeanUtils;
import com.sgcc.share.controller.SwitchLoginInformation;
import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.PathHelper;
import com.sgcc.share.webSocket.WebSocketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api("高级功能")
@RestController
@RequestMapping("/advanced/AdvancedFeatures")
@Transactional(rollbackFor = Exception.class)
public class AdvancedFeatures {

    @ApiOperation("高级功能接口")
    @PostMapping("/advancedFunction/{scanNum}/{functionName}")
    @MyLog(title = "高级功能", businessType = BusinessType.OTHER)
    public String advancedFunction(@RequestBody List<String> switchInformation, @PathVariable Long scanNum, @PathVariable List<String> functionName) {//待测
        String simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        // 预设多线程参数 Object[] 中的参数格式为： {mode,ip,name,password,port}
        List<SwitchParameters> switchParametersList = new ArrayList<>();
        /*将字符串格式的用户登录信息 转化为json格式的登录信息*/
        for (String information:switchInformation){
            SwitchLoginInformation switchLoginInformation = JSON.parseObject(information, SwitchLoginInformation.class);
            SwitchParameters switchParameters = new SwitchParameters();
            switchParameters.setLoginUser(SecurityUtils.getLoginUser());
            switchParameters.setScanningTime(simpleDateFormat);
            BeanUtils.copyBeanProp(switchParameters,switchLoginInformation);
            switchParameters.setPort(Integer.valueOf(switchLoginInformation.getPort()).intValue());
            //以多线程中的格式 存放数组中
            //连接方式，ip，用户名，密码，端口号
            switchParametersList.add(switchParameters);
        }
        //线程池

        ParameterSet parameterSet = new ParameterSet();
        parameterSet.setSwitchParameters(switchParametersList);
        parameterSet.setLoginUser(SecurityUtils.getLoginUser());
        parameterSet.setThreadCount(Integer.valueOf(scanNum+"").intValue());
        try {
            /*高级功能线程池*/
            AdvancedThreadPool.switchLoginInformations(parameterSet,functionName);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebSocketService.sendMessage(parameterSet.getLoginUser().getUsername(),"接收："+"扫描结束\r\n");
        try {
            PathHelper.writeDataToFile("接收："+"扫描结束\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "扫描结束";
    }

}
