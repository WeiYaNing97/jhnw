package com.sgcc.sql.controller;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.connect.method.SshMethod;
import com.sgcc.connect.method.TelnetSwitchMethod;
import com.sgcc.connect.util.SpringBeanUtil;
import com.sgcc.connect.util.SshConnect;
import com.sgcc.connect.util.TelnetComponent;
import com.sgcc.sql.domain.CommandLogic;
import com.sgcc.sql.domain.ReturnRecord;
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.sql.service.ICommandLogicService;
import com.sgcc.sql.service.IReturnRecordService;
import com.sgcc.sql.service.ITotalQuestionTableService;
import com.sgcc.sql.util.MyUtils;
import com.sgcc.sql.util.PathHelper;
import com.sgcc.sql.webSocket.WebSocketService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 光衰
 */
public class luminousAttenuation {

    @Autowired
    private static ITotalQuestionTableService totalQuestionTableService;
    @Autowired
    private static ICommandLogicService commandLogicService;
    @Autowired
    private static IReturnRecordService returnRecordService;
    @ApiOperation("获取光衰参数")
    public static AjaxResult obtainLightDecay(Map<String,String> user_String, Map<String,Object> user_Object) {
        AjaxResult ajaxResult = new AjaxResult();
        TotalQuestionTable totalQuestionTable = new TotalQuestionTable();
        totalQuestionTable.setBrand(user_String.get("deviceBrand"));
        totalQuestionTable.setType(user_String.get("deviceModel"));
        totalQuestionTable.setFirewareVersion(user_String.get("firmwareVersion"));
        totalQuestionTable.setSubVersion(user_String.get("subversionNumber"));
        totalQuestionTable.setTemProName("光衰端口号");
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.queryAdvancedFeaturesList(totalQuestionTable);
        TotalQuestionTable totalQuestionTablePojo = new TotalQuestionTable();
        if (totalQuestionTables.size()==0){
            /*查询获取光衰端口号命令失败*/
            try {
                PathHelper.writeDataToFileByName("IP地址:"+user_String.get("ip")+"查询获取光衰端口号命令失败","光衰");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("IP地址:"+user_String.get("ip")+"查询获取光衰端口号命令失败");
        }else if (totalQuestionTables.size()==1){
            totalQuestionTablePojo = totalQuestionTables.get(0);
        }else {
            totalQuestionTables = MyUtils.ObtainPreciseEntityClasses(totalQuestionTables);
            totalQuestionTablePojo = totalQuestionTables.get(0);
        }
        String commandId = totalQuestionTablePojo.getCommandId();
        commandId = commandId.substring(2,commandId.length());
        commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
        CommandLogic commandLogic = commandLogicService.selectCommandLogicById(commandId);
        String command = commandLogic.getCommand();
        String returnString = ObtainReturnResultsByCommand(user_String, user_Object, command);


        List<String> port = getPort(returnString);
        if (port.size() == 0){
            /*没有端口号为开启状态*/
            try {
                PathHelper.writeDataToFileByName("IP地址:"+user_String.get("ip")+"无UP状态端口号","光衰");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("IP地址:"+user_String.get("ip")+"未获取到UP状态端口号");
        }
        HashMap<String, String> getparameter = getparameter(port, user_String, user_Object);
        if (getparameter == null){
            /*未获取到光衰参数*/
            try {
                PathHelper.writeDataToFileByName("IP地址:"+user_String.get("ip")+"未获取到光衰参数","光衰");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("IP地址:"+user_String.get("ip")+"未获取到光衰参数");
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (String str:port){
            stringBuffer.append("IP地址:"+user_String.get("ip")+"端口号:"+str+"TX:"+getparameter.get(str+"TX")+"RX:"+getparameter.get(str+"RX")+"\r\n");
        }

        try {
            PathHelper.writeDataToFileByName(stringBuffer.toString(),"光衰");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return AjaxResult.success();
    }

    /*获取端口号*/
    public static List<String> getPort(String returnString) {
        returnString = MyUtils.trimString(returnString);
        String[] returnStringSplit = returnString.split("\r\n");
        List<String> strings = new ArrayList<>();
        for (String string:returnStringSplit){
            if ((string.toUpperCase().indexOf(" UP ")!=-1) && string.indexOf("/")!=-1){
                strings.add(string.trim());
            }
        }
        List<String> port = new ArrayList<>();
        for (String string:strings){
            String terminalSlogan = getTerminalSlogan(string);
            if (terminalSlogan != null){
                port.add(terminalSlogan);
            }
        }
        return port;
    }

    /* 根据 UP 截取端口号   */
    public static String getTerminalSlogan(String information){
        String[] informationSplit = information.toUpperCase().split(" UP ");
        information = null;
        for (String string:informationSplit){
            if (string.indexOf("/")!=-1){
                String[] string_split = string.split(" ");
                for (int num = 0;num < string_split.length;num++){
                    if (string_split[num].indexOf("/")!=-1){
                        if (MyUtils.judgeContainsStr(string_split[num])){
                            return string_split[num];
                        }else {
                            /*例如：  GigabitEthernet 2/1 */
                            information =string_split[num];
                            return string_split[--num] +" "+ information ;
                        }
                    }
                }
            }
        }
        return information;
    }

    /* 根据端口号 和 数据库中部定义的 获取光衰信息命令
    * */
    public static HashMap<String,String> getparameter(List<String> portNumber, Map<String,String> user_String, Map<String,Object> user_Object) {

        TotalQuestionTable totalQuestionTable = new TotalQuestionTable();
        totalQuestionTable.setBrand(user_String.get("deviceBrand"));
        totalQuestionTable.setType(user_String.get("deviceModel"));
        totalQuestionTable.setFirewareVersion(user_String.get("firmwareVersion"));
        totalQuestionTable.setSubVersion(user_String.get("subversionNumber"));
        totalQuestionTable.setTemProName("光衰");
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.queryAdvancedFeaturesList(totalQuestionTable);
        TotalQuestionTable totalQuestionTablePojo = new TotalQuestionTable();
        if (totalQuestionTables.size()==0){
            return null;
        }else if (totalQuestionTables.size()==1){
            totalQuestionTablePojo = totalQuestionTables.get(0);
        }else {
            totalQuestionTables = MyUtils.ObtainPreciseEntityClasses(totalQuestionTables);
            totalQuestionTablePojo = totalQuestionTables.get(0);
        }
        String commandId = totalQuestionTablePojo.getCommandId();
        commandId = commandId.substring(2,commandId.length());
        commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
        CommandLogic commandLogic = commandLogicService.selectCommandLogicById(commandId);
        String command = commandLogic.getCommand();
        HashMap<String,String> hashMap = new HashMap<>();
        for (String port:portNumber){
            String com = command.replaceAll("端口号",port);
            System.err.println("获取光衰参数命令:"+com);
            String returnResults = ObtainReturnResultsByCommand(user_String, user_Object, com);

            HashMap<String, String> values = getDecayValues(returnResults);
            hashMap.put(port+"TX",values.get("TX"));
            hashMap.put(port+"RX",values.get("RX"));
        }
        return hashMap;
    }


    /* 执行命令*/
    public static String ObtainReturnResultsByCommand(Map<String,String> user_String, Map<String,Object> user_Object,String command) {

        //四个参数 赋值
        SshConnect sshConnect = (SshConnect) user_Object.get("sshConnect");
        SshMethod connectMethod = (SshMethod) user_Object.get("connectMethod");
        TelnetComponent telnetComponent = (TelnetComponent) user_Object.get("telnetComponent");
        TelnetSwitchMethod telnetSwitchMethod = (TelnetSwitchMethod) user_Object.get("telnetSwitchMethod");
        //获取登录系统用户信息
        LoginUser loginUser = (LoginUser)user_Object.get("loginUser");
        String userName = loginUser.getUsername();
        //basicInformation : display device manuinfo,display ver
        //连接方式 ssh telnet
        String way = user_String.get("mode");
        //目前获取基本信息命令是多个命令是由,号分割的，
        // 所以需要根据, 来分割。例如：display device manuinfo,display ver

        /*H3C*/
        String commandString =""; //预设交换机返回结果

        //创建 存储交换机返回数据 实体类
        ReturnRecord returnRecord = new ReturnRecord();
        int insert_Int = 0; //交换机返回结果插入数据库ID
        returnRecord.setUserName(userName);
        returnRecord.setSwitchIp(user_String.get("ip"));
        returnRecord.setBrand(user_String.get("deviceBrand"));
        returnRecord.setType(user_String.get("deviceModel"));
        returnRecord.setFirewareVersion(user_String.get("firmwareVersion"));
        returnRecord.setSubVersion(user_String.get("subversionNumber"));
        // 执行命令赋值
        String commandtrim = command.trim();
        returnRecord.setCurrentCommLog(commandtrim);


        //根据 连接方法 判断 实际连接方式
        //并发送命令 接受返回结果
        boolean deviceBrand = true;
        do {
            deviceBrand = true;
            if (way.equalsIgnoreCase("ssh")){
                //  WebSocket 传输 命令
                WebSocketService.sendMessage(userName,user_String.get("ip")+"发送:"+command+"\r\n");
                try {
                    PathHelper.writeDataToFile(user_String.get("ip")+"发送:"+command+"\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                commandString = connectMethod.sendCommand(user_String.get("ip"),sshConnect,command,user_String.get("notFinished"));
            }else if (way.equalsIgnoreCase("telnet")){
                //  WebSocket 传输 命令
                WebSocketService.sendMessage(userName,user_String.get("ip")+"发送:"+command);
                try {
                    PathHelper.writeDataToFile(user_String.get("ip")+"发送:"+command+"\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                commandString = telnetSwitchMethod.sendCommand(user_String.get("ip"),telnetComponent,command,user_String.get("notFinished"));
            }

            //  WebSocket 传输 交换机返回结果
            returnRecord.setCurrentReturnLog(commandString);
            //粗略查看是否存在 故障
            // 存在故障返回 false 不存在故障返回 true
            boolean switchfailure = MyUtils.switchfailure(user_String, commandString);
            // 存在故障返回 false
            if (!switchfailure){
                // 交换机返回结果 按行 分割成 交换机返回信息数组
                String[] commandStringSplit = commandString.split("\r\n");
                // 遍历交换机返回信息数组
                for (String returnString:commandStringSplit){
                    // 查看是否存在 故障
                    // 存在故障返回 false 不存在故障返回 true
                    deviceBrand = MyUtils.switchfailure(user_String, returnString);
                    // 存在故障返回 false
                    if (!deviceBrand){

                        System.err.println("\r\n"+user_String.get("ip") + "\r\n故障:"+returnString+"\r\n");

                        WebSocketService.sendMessage(userName,"故障:"+user_String.get("ip") + ":"+returnString+"\r\n");

                        try {
                            PathHelper.writeDataToFile("故障:"+user_String.get("ip") + ":"+returnString+"\r\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        returnRecord.setCurrentIdentifier(user_String.get("ip") + "出现故障:"+returnString);

                        if (way.equalsIgnoreCase("ssh")){
                            connectMethod.sendCommand(user_String.get("ip"),sshConnect," ",user_String.get("notFinished"));
                        }else if (way.equalsIgnoreCase("telnet")){
                            telnetSwitchMethod.sendCommand(user_String.get("ip"),telnetComponent," ",user_String.get("notFinished"));
                        }
                    }
                }
            }

            returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
            insert_Int = returnRecordService.insertReturnRecord(returnRecord);

            if (insert_Int <= 0){
                //传输登陆人姓名 及问题简述
                WebSocketService.sendMessage(loginUser.getUsername(),"错误："+"交换机返回信息插入失败\r\n");
                try {
                    //插入问题简述及问题路径
                    PathHelper.writeDataToFile("错误："+"交换机返回信息插入失败\r\n"
                            +"方法com.sgcc.web.controller.sql.SwitchInteraction.getBasicInformationList");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }while (!deviceBrand);

        returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
        returnRecord = returnRecordService.selectReturnRecordById(Integer.valueOf(insert_Int).longValue());

        //去除其他 交换机登录信息
        commandString = MyUtils.removeLoginInformation(commandString);

        //交换机返回信息 修整字符串  去除多余 "\r\n" 连续空格 为插入数据美观
        commandString = MyUtils.trimString(commandString);

        //交换机返回信息 按行分割为 字符串数组
        String[] commandString_split = commandString.split("\r\n");

        // 返回日志内容
        String current_return_log = "";
        if (commandString_split.length !=1 ){

            current_return_log = commandString.substring(0, commandString.length() - commandString_split[commandString_split.length - 1].length() - 2).trim();
            returnRecord.setCurrentReturnLog(current_return_log);

            //返回日志前后都有\r\n 可以改为 current_return_log.endsWith("\r\n");

            String current_return_log_substring_end = current_return_log.substring(current_return_log.length() - 2, current_return_log.length());
            if (!current_return_log_substring_end.equals("\r\n")){
                current_return_log = current_return_log+"\r\n";
            }

            String current_return_log_substring_start = current_return_log.substring(0, 2);
            if (!current_return_log_substring_start.equals("\r\n")){
                current_return_log = "\r\n"+current_return_log;
            }

        }

        WebSocketService.sendMessage(userName,user_String.get("ip")+"接收:"+current_return_log+"\r\n");

        try {
            PathHelper.writeDataToFile(user_String.get("ip")+"接收:"+current_return_log+"\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //当前标识符 如：<H3C> [H3C]
        String current_identifier = commandString_split[commandString_split.length - 1].trim();
        returnRecord.setCurrentIdentifier(current_identifier);
        //当前标识符前后都没有\r\n
        String current_identifier_substring_end = current_identifier.substring(current_identifier.length() - 2, current_identifier.length());
        if (current_identifier_substring_end.equals("\r\n")){
            current_identifier = current_identifier.substring(0,current_identifier.length()-2);
        }
        String current_identifier_substring_start = current_identifier.substring(0, 2);
        if (current_identifier_substring_start.equals("\r\n")){
            current_identifier = current_identifier.substring(2,current_identifier.length());
        }

        WebSocketService.sendMessage(userName,user_String.get("ip")+"接收:"+current_identifier+"\r\n");
        try {
            PathHelper.writeDataToFile(user_String.get("ip")+"接收:"+current_identifier+"\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //存储交换机返回数据 插入数据库
        returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
        int update = returnRecordService.updateReturnRecord(returnRecord);

        return commandString;
    }

    /*get 光衰 参数*/
    public static HashMap<String,String> getDecayValues(String string) {

        string = MyUtils.trimString(string);
        String[] Line_split = string.split("\r\n");

        String txpower = null;
        String rxpower = null;

        List<String> keyValueList = new ArrayList<>();
        for (int number = 0 ;number<Line_split.length;number++) {
            int tx = Line_split[number].toUpperCase().indexOf("TX POWER");
            int rx = Line_split[number].toUpperCase().indexOf("RX POWER");

            int num = 0 ;
            if (tx!=-1 && rx!=-1){
                /*都包含*/
                if (tx > rx){
                    num = 1;
                }else if (tx < rx){
                    num = -1;
                }
                keyValueList.add(Line_split[number]);
            }else if (tx ==-1 && rx ==-1){
                /* 都不包含*/
                continue;
            }

            if (num == 0 && (tx != -1 || rx != -1)){
                /* 包含 TX 或者 RX */
                /*key : value*/
                keyValueList.add(Line_split[number]);

            }else {
                /*两个都包含 则 两个参数值在一行*/
                String nextrow = Line_split[number+1];
                String[] values = nextrow.split(" ");
                int j = 1;
                for (int i = 0 ;i < values.length;i++){
                    if (num == 1){
                        /*RX   TX*/
                        if (values[i].indexOf("-")!=-1 && j==1){
                            rxpower = values[i];
                            j=2;
                        }else if (values[i].indexOf("-")!=-1 && j==2){
                            txpower = values[i];
                        }
                    }else if (num == -1){
                        /*TX  RX*/
                        if (values[i].indexOf("-")!=-1 && j==1){
                            txpower = values[i];
                            j=2;
                        }else if (values[i].indexOf("-")!=-1 && j==2){
                            rxpower = values[i];
                        }
                    }
                    if (txpower != null && rxpower != null){
                        break;
                    }
                }
                break;
            }
        }

        /*key ： value 格式*/
        if (keyValueList.size()!=0){

            if (keyValueList.size() > 2){
                List<String> keylist = new ArrayList<>();
                for (String key:keyValueList){//Current
                    if (key.toUpperCase().indexOf("CURRENT")!=-1){
                        keylist.add(key);
                    }
                }

                if (keylist.size() > 1){
                    keyValueList = keylist;
                }
            }


            /*只有两行 则 直接取值*/
            if (keyValueList.size() >1){
                for (String keyvalue:keyValueList){
                    if (keyvalue.toUpperCase().indexOf("RX POWER") != -1){
                        keyvalue = keyvalue.replaceAll(":"," : ");
                        keyvalue = MyUtils.repaceWhiteSapce(keyvalue);
                        String[] split = keyvalue.split(":");
                        if (split.length >1){
                            rxpower = split[1].indexOf("-") !=-1 ?split[1]:null;
                        }
                    }
                    if (keyvalue.toUpperCase().indexOf("TX POWER") != -1){
                        keyvalue = keyvalue.replaceAll(":"," : ");
                        keyvalue = MyUtils.repaceWhiteSapce(keyvalue);
                        String[] split = keyvalue.split(":");
                        if (split.length >1){
                            txpower = split[1].indexOf("-") !=-1 ?split[1]:null;
                        }
                    }
                    if (txpower != null && rxpower != null){
                        String[] txpowersplit = txpower.split(", Warning");
                        String[] rxpowersplit = rxpower.split(", Warning");
                        txpower = txpowersplit[0];
                        rxpower = rxpowersplit[0];
                        break;
                    }
                }
            }
        }
        HashMap<String,String> hashMap = new HashMap<>();
        if (txpower != null && rxpower != null){
            hashMap.put("TX",txpower);
            hashMap.put("RX",rxpower);
        }
        return hashMap;
    }
}
