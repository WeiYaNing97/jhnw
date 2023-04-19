package com.sgcc.sql.senior;

import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.sql.parametric.ParameterSet;
import com.sgcc.sql.parametric.SwitchParameters;
import com.sgcc.sql.thread.AdvancedThreadPool;
import com.sgcc.sql.thread.ScanFixedThreadPool;
import com.sgcc.sql.util.PathHelper;
import com.sgcc.sql.util.RSAUtils;
import com.sgcc.sql.webSocket.WebSocketService;
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
@RequestMapping("/sql/AdvancedFeatures")
@Transactional(rollbackFor = Exception.class)
public class AdvancedFeatures {

    @ApiOperation("高级功能接口")
    @PostMapping("/advancedFunction/{scanNum}/{functionName}")
    @MyLog(title = "高级功能", businessType = BusinessType.OTHER)
    public String advancedFunction(@RequestBody List<String> switchInformation, @PathVariable Long scanNum,@PathVariable List<String> functionName) {//待测
        String simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        // 预设多线程参数 Object[] 中的参数格式为： {mode,ip,name,password,port}
        List<SwitchParameters> switchParametersList = new ArrayList<>();
        for (String information:switchInformation){
            // information  : {"ip":"192.168.1.100","name":"admin","password":"admin","mode":"ssh","port":"22"}
            // 去除花括号得到： "ip":"192.168.1.100","name":"admin","password":"admin","mode":"ssh","port":"22"
            information = information.replace("{","");
            information = information.replace("}","");
            /*以逗号分割 获取到 集合 集合为：
                information_split.get(0)  "ip":"192.168.1.100"
                information_split.get(1)  "name":"admin"
                information_split.get(2)  "password":"admin"
                information_split.get(3)  "mode":"ssh"
                information_split.get(4)  "port":"22"
            */
            String[] information_split = information.split(",");
            // 四个参数 设默认值
            SwitchParameters switchParameters = new SwitchParameters();
            switchParameters.setLoginUser(SecurityUtils.getLoginUser());
            switchParameters.setScanningTime(simpleDateFormat);

            for (String string:information_split){
                // string  参数为  ip:192.168.1.100  或  name:admin 或 password:admin 或 mode:ssh 或 port:22
                string = string.replace("\"","");
                // 以 ： 分割 得到
                /*
                string_split[0] ip           string_split[1] 192.168.1.100
                string_split[0] name         string_split[1] admin
                string_split[0] password     string_split[1] admin
                string_split[0] mode         string_split[1] ssh
                string_split[0] port         string_split[1] 22
                */
                String[] string_split = string.split(":");
                switch (string_split[0]){
                    case "ip" :  switchParameters.setIp(string_split[1]);
                        break;
                    case "name" :  switchParameters.setName(string_split[1]);
                        break;
                    case "password" :
                        String ciphertext = string_split[1];
                        String ciphertextString = RSAUtils.decryptFrontEndCiphertext(ciphertext);
                        switchParameters.setPassword(ciphertextString);
                        break;
                    case "configureCiphers" :
                        String configureCiphersciphertext = string_split[1];
                        String configureCiphersciphertextString = RSAUtils.decryptFrontEndCiphertext(configureCiphersciphertext);
                        switchParameters.setConfigureCiphers(configureCiphersciphertextString);
                        break;

                    case "mode" :  switchParameters.setMode(string_split[1]);
                        break;
                    case "port" :  switchParameters.setPort(Integer.valueOf(string_split[1]).intValue());
                        break;
                }
            }
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
