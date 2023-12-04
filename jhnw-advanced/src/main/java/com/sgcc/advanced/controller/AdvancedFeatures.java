package com.sgcc.advanced.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.sgcc.advanced.thread.AdvancedThreadPool;
import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.core.domain.entity.SysUser;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.bean.BeanUtils;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.MyUtils;
import com.sgcc.share.util.PathHelper;
import com.sgcc.share.webSocket.WebSocketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Api("高级功能")
@RestController
@RequestMapping("/advanced/AdvancedFeatures")
@Transactional(rollbackFor = Exception.class)
@Component("advancedFeatures")
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
            AdvancedThreadPool advancedThreadPool = new AdvancedThreadPool();
            boolean isRSA = true;
            advancedThreadPool.switchLoginInformations(parameterSet, functionName,isRSA);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.err.println("扫描结束");
        WebSocketService.sendMessage(parameterSet.getLoginUser().getUsername(),"接收："+"扫描结束\r\n");
        try {
            PathHelper.writeDataToFile("接收："+"扫描结束\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date now = new Date();
        Date now_10 = new Date(now.getTime() + 600000); //10分钟后的时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
        String nowTime_10 = dateFormat.format(now_10);
        while (true){
            if (WebSocketService.userMap.get(parameterSet.getLoginUser().getUsername()) != null){
                WebSocketService.userMap.remove(parameterSet.getLoginUser().getUsername());
                return "扫描结束";
            }
            if (dateFormat.format(new Date(now.getTime())).compareTo(nowTime_10) >=0 ){
                return "扫描结束";
            }
        }
    }


    /**
    * @Description   todo 未完成  定时任务 功能基本昨晚，参数有问题
    * @author charles
    * @createTime 2023/11/8 13:46
    * @desc
    * @param
     * @return
    */
    public String advancedScheduledTasks(String nameList) {
        String simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.err.println("advancedScheduledTasks = " + simpleDateFormat);

        // 预设多线程参数 Object[] 中的参数格式为： {mode,ip,name,password,port}
        List<SwitchParameters> switchParametersList = new ArrayList<>();
        List<String> functionName =new ArrayList<>();
        String[] namesplit = nameList.split(";");
        for (String name:namesplit){
            functionName.add( name );
        }

        LoginUser loginUser = new LoginUser();
        SysUser user = new SysUser();
        user.setUserName("admin");
        loginUser.setUser(user);

        //List<SwitchLoginInformation> switchLoginInformations = readExcel();
        List<SwitchLoginInformation> switchLoginInformations = new ArrayList<>();
        SwitchLoginInformation pojo = new SwitchLoginInformation();
        pojo.setIp("192.168.1.100");
        pojo.setName("admin");
        pojo.setPassword("admin");
        pojo.setMode("ssh");
        pojo.setPort("22");
        switchLoginInformations.add(pojo);
        if (MyUtils.isCollectionEmpty(switchLoginInformations)){
            return "扫描结束";
        }
        /*将字符串格式的用户登录信息 转化为json格式的登录信息*/
        for (SwitchLoginInformation switchLoginInformation:switchLoginInformations){
            SwitchParameters switchParameters = new SwitchParameters();
            switchParameters.setLoginUser(loginUser);
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
        parameterSet.setLoginUser(loginUser);
        parameterSet.setThreadCount(Integer.valueOf(5+"").intValue());

        try {
            /*高级功能线程池*/
            AdvancedThreadPool advancedThreadPool = new AdvancedThreadPool();
            boolean isRSA = false;

            System.out.println(parameterSet);
            System.out.println(functionName);
            System.out.println(isRSA);

            advancedThreadPool.switchLoginInformations(parameterSet, functionName,isRSA);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.err.println("扫描结束");
        WebSocketService.sendMessage(parameterSet.getLoginUser().getUsername(),"接收："+"扫描结束\r\n");
        try {
            PathHelper.writeDataToFile("接收："+"扫描结束\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date now = new Date();
        Date now_10 = new Date(now.getTime() + 600000); //10分钟后的时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
        String nowTime_10 = dateFormat.format(now_10);
        while (true){
            if (WebSocketService.userMap.get(parameterSet.getLoginUser().getUsername()) != null){
                WebSocketService.userMap.remove(parameterSet.getLoginUser().getUsername());
                return "扫描结束";
            }
            if (dateFormat.format(new Date(now.getTime())).compareTo(nowTime_10) >=0 ){
                return "扫描结束";
            }
        }
    }

    /**
    * @Description  获取定时任务 获取交换机登录信息 集合
    * @author charles
    * @createTime 2023/11/8 13:47
    * @desc
    * @param
     * @return
    */
    public List<SwitchLoginInformation> readExcel() {
        //定时任务功能 文件存放目录
        String url = (String) CustomConfigurationUtil.getValue("configuration.timedTaskPath", Constant.getProfileInformation());
        if (url == null){
            return new ArrayList<>();
        }
        /*读取表格内容*/
        ExcelReader reader = ExcelUtil.getReader(url);
        List<Map<String,Object>>  readAllMap = new ArrayList<>();
        try {
            // 读取 Excel 文件的操作
            readAllMap = reader.readAll();
        } catch (Exception e) {
            // 异常处理逻辑
        } finally {
            reader.close();
        }

        List<SwitchLoginInformation> switchLoginInformations = new ArrayList<>();
        for (Map<String,Object> readMap:readAllMap){
            Set<Map.Entry<String, Object>> entries = readMap.entrySet();
            SwitchLoginInformation switchLoginInformation = new SwitchLoginInformation();
            for (Map.Entry<String, Object> entrie:entries){
                switch (entrie.getKey()){
                    case "设备ip":
                        switchLoginInformation.setIp(entrie.getValue().toString());
                        break;
                    case "用户名":
                        switchLoginInformation.setName(entrie.getValue().toString());
                        break;
                    case "密码":
                        switchLoginInformation.setPassword(entrie.getValue().toString());
                        break;
                    case "登录方式":
                        switchLoginInformation.setMode(entrie.getValue().toString());
                        break;
                    case "端口号":
                        switchLoginInformation.setPort(entrie.getValue().toString());
                        break;
                    case "配置密码":
                        switchLoginInformation.setConfigureCiphers(entrie.getValue() == null?null:entrie.getValue().toString());
                        break;
                }
            }
            switchLoginInformations.add(switchLoginInformation);
        }

        return switchLoginInformations;
    }
}
