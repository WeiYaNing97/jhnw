package com.sgcc.share.switchboard;

import cn.hutool.core.util.StrUtil;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.connectutil.SshConnect;
import com.sgcc.share.connectutil.TelnetComponent;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.method.SshMethod;
import com.sgcc.share.method.TelnetSwitchMethod;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.service.IInformationService;
import com.sgcc.share.util.*;
import com.sgcc.share.webSocket.WebSocketService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ConnectToObtainInformation {

    @Autowired
    private IInformationService informationService;

    /**
     * 连接交换机 获取交换机基本信息
     * @param switchParameters
     * @return
     */
    public AjaxResult connectSwitchObtainBasicInformation(SwitchParameters switchParameters) {
        //连接交换机  requestConnect：
        AjaxResult requestConnect_ajaxResult = null;
        for (int number = 0; number <1 ; number++){
            switchParameters.setPassword(RSAUtils.decryptFrontEndCiphertext(switchParameters.getPassword()));
            switchParameters.setConfigureCiphers(RSAUtils.decryptFrontEndCiphertext(switchParameters.getConfigureCiphers()));
            requestConnect_ajaxResult = requestConnect(switchParameters);
            if (!(requestConnect_ajaxResult.get("msg").equals("交换机连接失败"))){
                break;
            }
        }


        //如果返回为 交换机连接失败 则连接交换机失败
        if(requestConnect_ajaxResult.get("msg").equals("交换机连接失败")){
            // todo 交换机连接失败 错误代码
            List<String> loginError = (List<String>) requestConnect_ajaxResult.get("loginError");
            if (loginError != null){
                for (int number = 1;number<loginError.size();number++){
                    String loginErrorString = loginError.get(number);
                    WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"风险:"+switchParameters.getIp()+loginErrorString+"\r\n");
                    try {
                        PathHelper.writeDataToFileByName(switchParameters.getIp()+"风险:"+loginErrorString+"\r\n","交换机连接");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return AjaxResult.error("交换机连接失败");
        }
        //解析返回参数 data
        switchParameters = (SwitchParameters) requestConnect_ajaxResult.get("data");
        //如果连接成功
        if(requestConnect_ajaxResult.get("msg").equals("操作成功")){
            //密码 MD5 加密
            String passwordDensificationAndSalt = EncryptUtil.densificationAndSalt(switchParameters.getPassword());
            switchParameters.setPassword(passwordDensificationAndSalt);//用户密码
            //密码 MD5 加密
            String configureCiphersDensificationAndSalt = EncryptUtil.densificationAndSalt(switchParameters.getConfigureCiphers());
            switchParameters.setConfigureCiphers(configureCiphersDensificationAndSalt);//用户密码
            //获取交换机基本信息
            AjaxResult basicInformationList_ajaxResult = getBasicInformationCurrency(switchParameters);
            return basicInformationList_ajaxResult;
        }
        return AjaxResult.error("交换机连接失败");
    }

    /**
     * 连接交换机方法
     */
    @GetMapping("requestConnect")
    public AjaxResult requestConnect(SwitchParameters switchParameters) {
        //设定连接结果 预设连接失败为 false
        boolean is_the_connection_successful =false;
        List<Object> objects = null;
        if (switchParameters.getMode().equalsIgnoreCase("ssh")){
            //创建ssh连接方法
            SshMethod connectMethod = new SshMethod();
            //连接ssh 成功为 true  失败为  false
            objects = connectMethod.requestConnect(switchParameters.getIp(),switchParameters.getPort(),switchParameters.getName(),switchParameters.getPassword());
            boolean loginBoolean = (boolean) objects.get(0);
            if (loginBoolean == true){
                SshConnect sshConnect =  (SshConnect)objects.get(1);
                switchParameters.setSshConnect(sshConnect);
                switchParameters.setConnectMethod(connectMethod);
                if (sshConnect!=null){
                    is_the_connection_successful = true;
                }
            }else {
                if (objects.get(objects.size()-1) instanceof String){
                    String sshVersion = (String) objects.get(objects.size()-1);
                    if (sshVersion.indexOf(switchParameters.getIp())!=-1){
                        WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"风险:"+"ip:"+ sshVersion+"\r\n");
                        try {
                            PathHelper.writeDataToFile("风险:"+"ip:"+ sshVersion+"\r\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
            //创建telnet连接方法
            TelnetSwitchMethod telnetSwitchMethod = new TelnetSwitchMethod();
            //连接telnet 成功为 true  失败为  false
            TelnetComponent telnetComponent = telnetSwitchMethod.requestConnect(switchParameters.getIp(),switchParameters.getPort(),switchParameters.getName(),switchParameters.getPassword(), null);
            if (telnetComponent!=null){
                switchParameters.setTelnetComponent(telnetComponent);
                switchParameters.setTelnetSwitchMethod(telnetSwitchMethod);
                is_the_connection_successful = true;
            }

        }

        /* is_the_connection_successful 交换机连接成功*/
        if(is_the_connection_successful){
            //enable 配置  返回 交换机连接失败  或   交换机连接成功
            String enable = enable(switchParameters);
            if (enable.equals("交换机连接成功")){
                return AjaxResult.success(switchParameters);
            }else {
                AjaxResult ajaxResult = new AjaxResult();
                ajaxResult.put("loginError",objects); // 交换机连接的返回信息
                ajaxResult.put("msg","交换机连接失败");
                return ajaxResult;
            }
        }else {
            AjaxResult ajaxResult = new AjaxResult();
            ajaxResult.put("loginError",objects); // 交换机连接的返回信息
            ajaxResult.put("msg","交换机连接失败");
            return ajaxResult;
        }
    }


    /**
     * 配置密码  enable 方法
     * @param
     * @return
     */
    public String enable(SwitchParameters switchParameters) {
        /*交换机返回结果*/
        String returnString = null;
        /* 执行 回车命令 获取交换机及返回结果*/
        if (switchParameters.getMode().equalsIgnoreCase("ssh")){
            returnString = switchParameters.getConnectMethod().sendCommand(switchParameters.getIp(),switchParameters.getSshConnect(),"\r",null);
        }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
            returnString = switchParameters.getTelnetSwitchMethod().sendCommand(switchParameters.getIp(), switchParameters.getTelnetComponent(), "\r", null);
        }
        if (returnString==null){
            return "交换机连接失败";
        }
        String trim = returnString.trim();
        /*判断 交换机返回结果给标识符 是否 以> 结尾*/
        /*思科交换机返回信息是 #  不需要发送 enable*/
        if (trim.endsWith(">")){
            /*发送 enable 命令 查看返回结果 */
            if (switchParameters.getMode().equalsIgnoreCase("ssh")){
                returnString = switchParameters.getConnectMethod().sendCommand(switchParameters.getIp(),switchParameters.getSshConnect(),"enable",null);
            }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
                returnString = switchParameters.getTelnetSwitchMethod().sendCommand(switchParameters.getIp(), switchParameters.getTelnetComponent(), "enable", null);
            }
            /*判断交换机返回结果是否为空*/
            if (returnString == null){
                return "交换机连接失败";
            }else {
                String substring = returnString.substring(returnString.length() - 1, returnString.length());
                if (returnString.indexOf("command")!=-1 && returnString.indexOf("%")!=-1 ){
                    return "交换机连接成功";
                }else if (substring.equalsIgnoreCase("#")){
                    return "交换机连接成功";
                }else if (returnString.indexOf(":")!=-1){
                    /* 输入 配置密码*/
                    if (switchParameters.getMode().equalsIgnoreCase("ssh")){
                        SshMethod connectMethod = switchParameters.getConnectMethod();
                        SshConnect sshConnect = switchParameters.getSshConnect();
                        returnString = connectMethod.sendCommand(switchParameters.getIp(),sshConnect,switchParameters.getConfigureCiphers(),null);
                    }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
                        returnString = switchParameters.getTelnetSwitchMethod().sendCommand(switchParameters.getIp(), switchParameters.getTelnetComponent(), switchParameters.getConfigureCiphers(), null);
                    }
                    return "交换机连接成功";
                }
            }
            /*思科交换机返回信息是 #  不需要发送 enable*/
        }else if (trim.substring(trim.length()-1,trim.length()).equals("#")){
            return "交换机连接成功";
        }
        return "交换机连接失败";
    }


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
    @ApiOperation("通用获取交换机基本信息")
    public AjaxResult getBasicInformationCurrency(SwitchParameters switchParameters) {

        //目前获取基本信息命令是多个命令是由,号分割的，
        // 所以需要根据, 来分割。例如：display device manuinfo,display ver
        /*H3C*/
        String[] commandsplit = ( (String) CustomConfigurationUtil.getValue("BasicInformation.getBrandCommand", Constant.getProfileInformation())).split(";");
        String commandString =""; //预设交换机返回结果
        //遍历数据表命令 分割得到的 命令数组
        for (String command:commandsplit){
            /*根据交换机信息类 与 具体命令，执行并返回交换机返回信息
             * 返回结果*/
            commandString = FunctionalMethods.executeScanCommandByCommand(switchParameters, command);
            if (commandString == null){
                continue;
            }
            /*根据交换机返回结果 获取 交换机基本信息*/
            HashMap<String, String> hashMap = analyzeStringToGetBasicInformation(commandString);

            if (hashMap.get("pinpai")!=null
                    && hashMap.get("xinghao")!=null
                    && hashMap.get("banben")!=null
                    && hashMap.get("routerFlag")!=null){

                hashMap.put("pinpai",hashMap.get("pinpai"));
                hashMap.put("xinghao",hashMap.get("xinghao"));
                hashMap.put("banben",removeSpecialSymbols(hashMap.get("banben")));
                hashMap.put("zibanben",removeSpecialSymbols(hashMap.get("zibanben")));
                hashMap.put("routerFlag",hashMap.get("routerFlag"));
                System.err.println("品牌:"+hashMap.get("pinpai")+"型号:"+hashMap.get("xinghao")+
                        "版本:"+hashMap.get("banben")+"子版本:"+hashMap.get("zibanben")+
                        "设备:"+hashMap.get("routerFlag"));

                try {
                    WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"系统信息:"+switchParameters.getIp() +"基本信息："+
                            "设备品牌："+hashMap.get("pinpai")+
                            "设备型号："+hashMap.get("xinghao")+
                            "内部固件版本："+hashMap.get("banben")+
                            "子版本号："+hashMap.get("zibanben")+
                            "设备："+hashMap.get("routerFlag")+"\r\n");
                    PathHelper.writeDataToFileByName("系统信息:"+switchParameters.getIp()+"成功基本信息："+
                            "设备品牌："+hashMap.get("pinpai")+
                            "设备型号："+hashMap.get("xinghao")+
                            "内部固件版本："+hashMap.get("banben")+
                            "子版本号："+hashMap.get("zibanben")+
                            "设备："+hashMap.get("routerFlag")+"\r\n","基本信息");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                switchParameters.setDeviceBrand(hashMap.get("pinpai"));
                switchParameters.setDeviceModel(hashMap.get("xinghao"));
                switchParameters.setFirmwareVersion(hashMap.get("banben"));
                switchParameters.setSubversionNumber(hashMap.get("zibanben"));
                switchParameters.setRouterFlag(hashMap.get("routerFlag"));
                return AjaxResult.success(switchParameters);
            }else {
                try {
                    PathHelper.writeDataToFileByName("系统信息:"+ switchParameters.getIp() +"失败基本信息："+
                            "设备品牌："+hashMap.get("pinpai")+
                            "设备型号："+hashMap.get("xinghao")+
                            "内部固件版本："+hashMap.get("banben")+
                            "子版本号："+hashMap.get("zibanben")+
                            "设备："+hashMap.get("routerFlag")+"\r\n","基本信息");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return AjaxResult.error("未定义该交换机获取基本信息命令及分析");
    }



    /**
     * 通过 配置文件 获取取交换机基本信息规则
     * 根据交换机返回结果 获取 交换机基本信息
     * @return  返回  交换机基本信息
     */
    public HashMap<String,String> analyzeStringToGetBasicInformation(String returns_String) {
        returns_String = returns_String.replaceAll("\r\n"," ");
        informationService = SpringBeanUtil.getBean(IInformationService.class);
        List<String> brandList = informationService.selectDeviceBrandList();
        String brand = null;
        String model = null;
        String firmwareVersion = null;
        String subversionNo = null;
        String router = "交换机";
        /* 创建返回对象 */
        HashMap<String,String> map = new HashMap<>();
        String[] return_word = returns_String.split(" ");
        /*遍历匹配 品牌  H3C*/
        for (String brandString:brandList){
            /*for (int number = 0 ; number < return_word.length; number++){
                if (brandString.equalsIgnoreCase(return_word[number])){
                    brand = brandString;
                    break;
                }
            }*/
            /*判断是否包含品牌*/
            if (MyUtils.containIgnoreCase(returns_String," "+brandString+" ")){
                brand = brandString;
                break;
            }
        }
        if (brand == null){
            return map;
        }
        /*匹配 型号 */
        List<String> modelList = informationService.selectDeviceModelList(brand);
        for (String modelString:modelList){
            /*原方法*/
            /*for (int number = 0 ; number < return_word.length; number++){
                if (modelString.equalsIgnoreCase(return_word[number])){
                    model = modelString;
                    break;
                }
            }*/
            /*判断是否包含型号*/
            if (MyUtils.containIgnoreCase(returns_String," "+modelString+" ")){
                model = modelString;
                break;
            }
        }
        /* 获取 设备是 路由器的标志*/
        String routerFlag = (String) CustomConfigurationUtil.getValue("BasicInformation.routerFlag",Constant.getProfileInformation());
        String[] flagSplit = routerFlag.toUpperCase().split(";");
        for (int number = 0 ; number < return_word.length; number++){
            for (String flag:flagSplit){
                /* 以 配置文件 属性值并且包含数字*/
                if (return_word[number].startsWith(flag)){
                    if (MyUtils.isNumeric(return_word[number])){
                        router = return_word[number];
                        break;
                    }
                }
            }
        }

        /** 设备版本 */
        /*yml 配置文件中 多个值之间用;隔开*/
        String deviceVersion = (String) CustomConfigurationUtil.getValue("BasicInformation.deviceVersion",Constant.getProfileInformation());
        String[] deviceVersionSplit =deviceVersion.split(";");
        /*遍历配置文件中的 属性值*/
        for (String version:deviceVersionSplit){
            /*判断是否包含配置文件中的 属性值
             * 如果不包含 则 下一循环*/
            if (!MyUtils.containIgnoreCase(returns_String," "+version+" ")){
                continue;
            }
            /*属性值可能是多个单词*/
            String[] versionSplit = version.split(" ");
            int versionNumber = versionSplit.length;
            for (int number = 0 ; number < return_word.length; number++){
                /*遍历 交换机返回信息的 单词数组
                 * 如果匹配到 则判断配置文件中 信息的单词数
                 * 如果是一个单词 则直接取下一个
                 * 如果是多个单词 则比较多个单词 是否匹配  匹配 则直接取下一个*/
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
        String deviceSubversion = (String) CustomConfigurationUtil.getValue("BasicInformation.deviceSubversion",Constant.getProfileInformation());
        String[] deviceSubversionSplit =deviceSubversion.split(";");
        for (String version:deviceSubversionSplit){
            /*判断是否包含配置文件中的 属性值
             * 如果不包含 则 下一循环*/
            if (!MyUtils.containIgnoreCase(returns_String," "+version+" ")){
                continue;
            }
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
        map.put("pinpai",brand);
        map.put("xinghao",model);
        map.put("banben",firmwareVersion);
        map.put("zibanben",subversionNo);
        map.put("routerFlag",router);
        if (model == null){

        }
        return map;
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
}
