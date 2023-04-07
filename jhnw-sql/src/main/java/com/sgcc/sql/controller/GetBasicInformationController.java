package com.sgcc.sql.controller;

import cn.hutool.core.util.StrUtil;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.connect.method.SshMethod;
import com.sgcc.connect.method.TelnetSwitchMethod;
import com.sgcc.connect.util.SpringBeanUtil;
import com.sgcc.connect.util.SshConnect;
import com.sgcc.connect.util.TelnetComponent;
import com.sgcc.sql.domain.ReturnRecord;
import com.sgcc.sql.service.*;
import com.sgcc.sql.util.MyUtils;
import com.sgcc.sql.util.PathHelper;
import com.sgcc.sql.webSocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetBasicInformationController {

    @Autowired
    private static IReturnRecordService returnRecordService;
    @Autowired
    private static IInformationService informationService;

    /**
     * @method: 通用获取交换机基本信息  多个命令依次执行 按，分割
     * @Param: [user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法,
     * user_Object ：
     * SshConnect ssh连接工具，connectMethod ssh连接,
     * TelnetComponent Telnet连接工具，telnetSwitchMethod telnet连接]
     * @E-mail: WeiYaNing97@163.com
     * 通过null 查询初所有会的交换机基本信息的命令字符串集合
     * 遍历交换机基本信息的命令字符串集合 通过 扫描分析方法 获得所有提取信息
     * 通过返回提取的信息，给基本属性赋值
     *
     * 成功则返回基本信息 否则 遍历下一条 交换机基本信息的命令字符串集合信息
     *
     */
    public static AjaxResult getBasicInformationCurrency(Map<String,String> user_String, Map<String,Object> user_Object) {
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
        String[] commandsplit = Configuration.getBrandCommand.split(";");
        String commandString =""; //预设交换机返回结果
        String return_sum = ""; //当前命令字符串总和 返回命令总和("\r\n"分隔)

        //遍历数据表命令 分割得到的 命令数组
        for (String command:commandsplit){
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

            //判断命令是否错误 错误为false 正确为true
            if (!MyUtils.judgmentError( user_String,commandString)){
                //如果返回信息错误 则结束当前命令，执行 遍历数据库下一条命令字符串(,)

                String[] returnString_split = commandString.split("\r\n");
                for (String string_split:returnString_split){
                    if (!MyUtils.judgmentError( user_String,string_split)){

                        System.err.println("\r\n"+user_String.get("ip")+ ":" +command+ "错误:"+string_split+"\r\n");
                        WebSocketService.sendMessage(userName,"风险:"+user_String.get("ip")+ ":" +command+ ":"+string_split+"\r\n");
                        try {
                            PathHelper.writeDataToFile("风险:"+user_String.get("ip")+ ":" +command+ ":"+string_split+"\r\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            continue;
                        }

                    }
                }

                continue;
            }

            //当前命令字符串 返回命令总和("\r\n"分隔)
            return_sum =  commandString+"\r\n";




            HashMap<String, String> hashMap = analyzeStringToGetBasicInformation(return_sum);

            System.err.println("==================================================================");
            System.err.println("品牌"+hashMap.get("pinpai"));
            System.err.println("品牌"+hashMap.get("pinpai")+"型号"+hashMap.get("xinghao"));
            System.err.println("品牌"+hashMap.get("pinpai")+"型号"+hashMap.get("xinghao")+"版本"+hashMap.get("banben"));
            System.err.println("品牌"+hashMap.get("pinpai")+"型号"+hashMap.get("xinghao")+"版本"+hashMap.get("banben")+"子版本"+hashMap.get("zibanben"));
            System.err.println("==================================================================");

            if (hashMap.get("pinpai")!=null
                    && hashMap.get("xinghao")!=null
                    && hashMap.get("banben")!=null
                    && hashMap.get("zibanben")!=null){

                hashMap.put("pinpai",removeSpecialSymbols(hashMap.get("pinpai")));
                hashMap.put("xinghao",removeSpecialSymbols(hashMap.get("xinghao")));
                hashMap.put("banben",removeSpecialSymbols(hashMap.get("banben")));
                hashMap.put("zibanben",removeSpecialSymbols(hashMap.get("zibanben")));


                //设备型号
                user_String.put("deviceModel",hashMap.get("xinghao"));
                //设备品牌
                user_String.put("deviceBrand",hashMap.get("pinpai"));
                //内部固件版本
                user_String.put("firmwareVersion",hashMap.get("banben"));
                //子版本号
                user_String.put("subversionNumber",hashMap.get("zibanben"));

                WebSocketService.sendMessage(userName,"系统信息:"+user_String.get("ip") +"基本信息："+
                        "设备品牌："+hashMap.get("pinpai")+
                        "设备型号："+hashMap.get("xinghao")+
                        "内部固件版本："+hashMap.get("banben")+
                        "子版本号："+hashMap.get("zibanben")+"\r\n");

                try {
                    PathHelper.writeDataToFileByName("系统信息:"+user_String.get("ip") +"成功基本信息："+
                            "设备品牌："+hashMap.get("pinpai")+
                            "设备型号："+hashMap.get("xinghao")+
                            "内部固件版本："+hashMap.get("banben")+
                            "子版本号："+hashMap.get("zibanben")+"\r\n","基本信息");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return AjaxResult.success(hashMap);
            }else {

                try {
                    PathHelper.writeDataToFileByName("系统信息:"+user_String.get("ip") +"失败基本信息："+
                            "设备品牌："+hashMap.get("pinpai")+
                            "设备型号："+hashMap.get("xinghao")+
                            "内部固件版本："+hashMap.get("banben")+
                            "子版本号："+hashMap.get("zibanben")+"\r\n","基本信息");

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }


        return AjaxResult.error("未定义该交换机获取基本信息命令及分析");

    }


    /**
     * 是否是以 固定字符 开头和结尾 是 则去除  （获取交换机基本信息用到）
     * @param args
     * @return
     */
    public static String removeSpecialSymbols(String args) {
        String[] specialSymbols = {".",","};
        for (String symbol:specialSymbols){
            boolean startWith = StrUtil.startWith(args, symbol);
            boolean endWith = StrUtil.endWith(args, symbol);
            if (startWith){
                args = args.substring(1,args.length());
            }
            if (endWith){
                args = args.substring(0,args.length()-1);
            }
        }
        return args;
    }


    /**
     * 通过 配置文件 获取取交换机基本信息规则
     * @param returns
     * @return  返回  交换机基本信息
     */
    public static HashMap<String,String> analyzeStringToGetBasicInformation(String returns) {

        String returns_String = MyUtils.trimString(returns);
        returns_String = returns_String.replaceAll("\r\n"," ");
        /*String equipmentBrand = Configuration.equipmentBrand;
        String equipmentModel = Configuration.equipmentModel;
        String[] equipmentBrandsplit = equipmentBrand.split(";");
        String[] equipmentModelsplit = equipmentModel.split(";");*/
        informationService = SpringBeanUtil.getBean(IInformationService.class);
        List<String> brandList = informationService.selectDeviceBrandList();

        String brand = "";
        String model = "";
        String firmwareVersion = "";
        String subversionNo = "";

        HashMap<String,String> map = new HashMap<>();
        map.put("pinpai",null);
        map.put("xinghao",null);
        map.put("banben",null);
        map.put("zibanben",null);

        String[] return_word = returns_String.trim().split(" ");


        for (String brandString:brandList){
            for (int number = 0 ; number < return_word.length; number++){
                if (brandString.equalsIgnoreCase(return_word[number])){
                    brand = brandString;
                    break;
                }
            }
        }

        if (!(brand.equals(""))){
            List<String> modelList = informationService.selectDeviceModelList(brand);
            for (String modelString:modelList){
                for (int number = 0 ; number < return_word.length; number++){

                    if (modelString.equalsIgnoreCase(return_word[number])){
                        model = modelString;
                        break;
                    }
                }
            }
        }

        /** 设备版本 */
        String deviceVersion = Configuration.deviceVersion;
        String[] deviceVersionSplit =deviceVersion.split(";");
        for (String version:deviceVersionSplit){
            String[] versionSplit = version.split(" ");
            int versionNumber = versionSplit.length;
            for (int number = 0 ; number < return_word.length; number++){
                if (return_word[number].equalsIgnoreCase(versionSplit[0])){
                    if (versionSplit.length == 1){
                        if (MyUtils.containDigit(return_word[number+1])){
                            firmwareVersion = return_word[number+1];
                            break;
                        }
                    }else {
                        String device = "";
                        for (int num = 0 ; num < versionNumber ; num++){
                            device = device + return_word[number + num] +" ";
                        }
                        device = device.trim();
                        if (version.equalsIgnoreCase(device)){
                            if (MyUtils.containDigit(return_word[number + (versionNumber-1) + 1])){
                                firmwareVersion =  return_word[number + (versionNumber-1) + 1];
                                break;
                            }
                        }
                    }
                }
            }

        }


        /** 设备子版本 */
        String deviceSubversion = Configuration.deviceSubversion;
        String[] deviceSubversionSplit =deviceSubversion.split(";");
        for (String version:deviceSubversionSplit){
            String[] versionSplit = version.split(" ");
            int versionNumber = versionSplit.length;
            for (int number = 0 ; number < return_word.length; number++){
                if (return_word[number].equalsIgnoreCase(versionSplit[0])){
                    if (versionSplit.length == 1){
                        if (MyUtils.containDigit(return_word[number+1])){
                            subversionNo = return_word[number+1];
                            break;
                        }

                    }else {
                        String device = "";
                        for (int num = 0 ; num < versionNumber ; num++){
                            device = device + return_word[number + num] +" ";
                        }
                        device = device.trim();
                        if (version.equalsIgnoreCase(device)){
                            if (MyUtils.containDigit(return_word[number + (versionNumber-1) + 1])){
                                subversionNo =  return_word[number + (versionNumber-1) + 1];
                                break;
                            }

                        }
                    }
                }
            }

        }

        map.put("pinpai",!(brand.equals(""))?brand:null);
        map.put("xinghao",!(model.equals(""))?model:null);
        map.put("banben",!(firmwareVersion.equals(""))?firmwareVersion:null);
        map.put("zibanben",!(subversionNo.equals(""))?subversionNo:null);

        if (model.equals("")){

        }

        return map;
    }


}
