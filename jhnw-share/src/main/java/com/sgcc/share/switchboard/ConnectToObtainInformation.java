package com.sgcc.share.switchboard;

import cn.hutool.core.util.StrUtil;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.connectutil.SshConnect;
import com.sgcc.share.connectutil.TelnetComponent;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.domain.Information;
import com.sgcc.share.domain.SwitchScanResult;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.method.SshMethod;
import com.sgcc.share.method.TelnetSwitchMethod;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.service.IInformationService;
import com.sgcc.share.service.ISwitchScanResultService;
import com.sgcc.share.util.*;
import com.sgcc.share.webSocket.WebSocketService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ConnectToObtainInformation {
    @Autowired
    private IInformationService informationService;
    @Autowired
    private ISwitchScanResultService switchScanResultService;


    /**
     * 连接交换机并获取基本信息
     *
     * @param switchParameters 交换机参数对象，包含IP、用户名、密码等信息
     * @param isRSA          是否使用RSA加密，如果为true，则对用户名和密码进行RSA解密
     * @return AjaxResult对象，包含操作结果和数据
     */
    public AjaxResult connectSwitchObtainBasicInformation(SwitchParameters switchParameters,boolean isRSA) {
        /**
         * 1.判断 交换机用户名和密码是否为空，如果为空，则从扫描结果表中获取交换机登录信息，
         * 否则使用前端提交的交换机用户名和密码信息，判断交换机信息是否经过RSA加密，如果为true，则对用户名和密码进行RSA解密，否则不进行解密操作
         * 并设置到交换机参数对象中
         */
        /* 前端提交交换机用户名、密码 有一项 为空，按扫描结果表的登录信息登录交换机*/
        if (switchParameters.getName() == null
                || switchParameters.getName().equals("")
                || switchParameters.getPassword() == null
                || switchParameters.getPassword().equals("")){
            // 获取SpringBean工具类实例
            switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);
            // 根据交换机IP获取最新的扫描结果
            SwitchScanResult theLatestDataByIP = switchScanResultService.getTheLatestDataByIP(switchParameters.getIp());
            if(theLatestDataByIP == null){
                // 返回交换机登录信息获取失败的Ajax结果
                return AjaxResult.error("交换机登录信息获取失败");
            }
            // 用户名密码
            // 设置交换机名称为扫描结果中的交换机名称
            switchParameters.setName(theLatestDataByIP.getSwitchName());
            // 数据库中保存的登录信息需要对扫描结果中的交换机密码进行解密
            switchParameters.setPassword(EncryptUtil.desaltingAndDecryption(theLatestDataByIP.getSwitchPassword()));
            if (theLatestDataByIP.getConfigureCiphers()!=null){
                // 对扫描结果中的交换机配置密码进行解密
                switchParameters.setConfigureCiphers(EncryptUtil.desaltingAndDecryption(theLatestDataByIP.getConfigureCiphers()));
            }
        }else {
            /**
             * 使用前端提交的交换机用户名和密码信息，判断交换机信息是否经过RSA加密，如果为true，则对用户名和密码进行RSA解密，否则不进行解密操作 并设置到交换机参数对象中
             */
            /* 判断 交换机信息 是否经过了RSA加密 */
            if ( isRSA && switchParameters.getName() != null && switchParameters.getPassword() != null){
                /* RSA 解密 */
                // 对交换机密码进行RSA解密
                switchParameters.setPassword(RSAUtils.decryptFrontEndCiphertext(switchParameters.getPassword()));
                // 对交换机配置密码进行RSA解密
                switchParameters.setConfigureCiphers(RSAUtils.decryptFrontEndCiphertext(switchParameters.getConfigureCiphers()));
            }
        }

        /**
         * 2.调用requestConnect方法连接交换机，并获取结果，
         *      如果返回为 交换机连接失败 则连接交换机失败，
         *      否则连接成功，并获取返回参数 data
         */
        // 连接交换机 requestConnect：
        AjaxResult requestConnect_ajaxResult = null;
        // 循环三次，如果连接交换机失败，则再次尝试两次
        for (int number = 0; number < 3 ; number++){
            // 调用requestConnect方法连接交换机，并获取结果
            requestConnect_ajaxResult = requestConnect(switchParameters);
            if (!(requestConnect_ajaxResult.get("msg").equals("交换机连接失败"))){
                //连接交换机成功 退出循环
                break;
            }
        }

        /**
         * 3.如果返回为 交换机连接失败 则连接交换机失败
         */
        //如果返回为 交换机连接失败 则连接交换机失败
        if(requestConnect_ajaxResult.get("msg").equals("交换机连接失败")){
            List<String> loginError = (List<String>) requestConnect_ajaxResult.get("loginError");
            if (loginError != null){
                for (int number = 1;number<loginError.size();number++){
                    // 调用AbnormalAlarmInformationMethod类的afferent方法记录异常信息
                    String loginErrorString = loginError.get(number);
                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "交换机连接",
                            "异常:"+switchParameters.getIp()+loginErrorString+"\r\n");
                }
            }
            // 返回交换机连接失败的Ajax结果
            return AjaxResult.error("交换机连接失败");
        }


        /**
         * 4.如果返回为 操作成功 则获取返回参数 data 并调用getBasicInformationCurrency方法获取交换机基本信息并返回
         */
        //解析返回参数 data
        switchParameters = (SwitchParameters) requestConnect_ajaxResult.get("data");
        //如果连接成功
        if(requestConnect_ajaxResult.get("msg").equals("操作成功")){


            /**
             * 5.调用getBasicInformationCurrency方法获取交换机基本信息
             * */
            /* 获取交换机基本信息 */
            AjaxResult basicInformationList_ajaxResult = getBasicInformationCurrency(switchParameters);
            return basicInformationList_ajaxResult;
        }
        // 返回交换机连接失败的Ajax结果
        return AjaxResult.error("交换机连接失败");
    }


    /*Inspection Completed*/
    /**
    * @Description 连接交换机方法
    * @desc
    * @param switchParameters
     * @return
    */
    @GetMapping("requestConnect")
    public AjaxResult requestConnect(SwitchParameters switchParameters) {
        /***
         * 1.判断连接交换机协议为SSH还是telnet
         *      如果为SSH则调用SshMethod类的requestConnect方法连接交换机，并获取返回参数
         *      如果为telnet则调用TelnetSwitchMethod类的requestConnect方法连接交换机，并获取返回参数
         *      返回参数为：交换机连接失败  或   交换机连接成功
         */
        //设定连接结果，默认交换机连接失败为 false
        boolean is_the_connection_successful =false;
        List<Object> objects = null;
        /*连接方式 为 SSH*/
        if (switchParameters.getMode().equalsIgnoreCase("ssh")){
            // 如果为SSH则调用SshMethod类的requestConnect方法连接交换机，并获取返回参数
            //创建ssh连接方法
            SshMethod connectMethod = new SshMethod();
            //连接ssh 成功为 true  失败为  false
            /*为 true 时 返回 SshConnect JSCH的 使用方法类*/
            objects = connectMethod.requestConnect(switchParameters.getIp(),switchParameters.getPort(),switchParameters.getName(),switchParameters.getPassword());
            /* 判断交换机是否连接成功 成功*/
            if ((boolean) objects.get(0) == true){
                /*(JSCH 使用方法类)*/
                if ( (SshConnect) objects.get(1) != null){
                    SshConnect sshConnect =  (SshConnect)objects.get(1);
                    switchParameters.setSshConnect(sshConnect);/*JSCH的使用方法类*/
                    switchParameters.setConnectMethod(connectMethod);/*ssh的使用方法类*/
                    // 交换机连接成功为 true
                    is_the_connection_successful = true;
                }
            }else {
                /* 判断交换机是否连接成功 失败 */
                /* 集合的最后一个元素 为 连接失败的原因 */
                if (objects.get(objects.size()-1) instanceof String){
                    /*告警、异常信息写入*/
                    /*连接失败的原因*/
                    String sshVersion = (String) objects.get(objects.size()-1);
                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                            "风险:"+"ip:"+ sshVersion+"\r\n");
                }
            }
        /*连接方式 为 telnet*/
        }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
            //如果为telnet则调用TelnetSwitchMethod类的requestConnect方法连接交换机，并获取返回参数
            //创建telnet连接方法
            TelnetSwitchMethod telnetSwitchMethod = new TelnetSwitchMethod();
            //连接telnet 成功为 true  失败为  false
            TelnetComponent telnetComponent = telnetSwitchMethod.requestConnect(switchParameters.getIp(),switchParameters.getPort(),switchParameters.getName(),switchParameters.getPassword(), null);
            objects.add(telnetComponent);// 交换机连接的返回信息
            if (telnetComponent!=null){
                switchParameters.setTelnetComponent(telnetComponent);
                switchParameters.setTelnetSwitchMethod(telnetSwitchMethod);
                // 交换机连接成功为 true
                is_the_connection_successful = true;
            }
        }




        /**
         * 2.如果连接成功，则调用enable方法配置密码并返回结果
         * 否则返回结果为 交换机连接失败
         */
        // 判断交换机是否连接成功 成功为 true  失败为  false
        /* is_the_connection_successful 交换机连接成功*/
        if(is_the_connection_successful){
            //enable配置返回交换机连接失败或交换机连接成功
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
     * 启用交换机功能 enable
     *
     * 该方法首先通过SSH或Telnet方式连接到交换机，发送回车命令获取交换机的返回结果，
     * 根据返回结果判断是否需要发送enable命令以启用交换机功能。
     * 如果返回结果以">"结尾，则需要发送enable命令，并根据后续返回结果判断连接是否成功。
     * 如果返回结果以"#"结尾，则直接判断连接成功。
     *
     * @param switchParameters 交换机参数对象，包含连接模式、IP地址、SSH连接对象、Telnet组件等信息
     * @return 交换机连接状态字符串，成功返回"交换机连接成功"，失败返回"交换机连接失败"
     */
    public String enable(SwitchParameters switchParameters) {
        /**
         * 1.发送命令 回车 获取交换机及返回结果 并判断交换机返回结果是否为空，
         * 为空则返回"交换机连接失败"，
         * 不为空则发送enable命令查看返回结果
         */
        /*交换机返回结果*/
        StringBuffer returnString = null;
        /* 执行 回车命令 获取交换机及返回结果*/
        if (switchParameters.getMode().equalsIgnoreCase("ssh")) {
            returnString = switchParameters.getConnectMethod().sendCommand(switchParameters.getIp(), switchParameters.getSshConnect(), "\r", null);
        } else if (switchParameters.getMode().equalsIgnoreCase("telnet")) {
            List<String> stringList = switchParameters.getTelnetSwitchMethod().sendCommand(switchParameters.getIp(), switchParameters.getTelnetComponent(), "\r", null);
            if (stringList.size() != 0) {
                StringBuffer stringBuffer = new StringBuffer();
                for (String string : stringList) {
                    stringBuffer.append(string).append("\r\n");
                }
                returnString = StringBufferUtils.substring(stringBuffer, 0, stringBuffer.length() - 2);
            }
        }
        // 如果交换机返回结果为空，则返回"交换机连接失败"
        if (returnString == null) {
            return "交换机连接失败";
        }



        /**
         * 2.
         *   1）判断 交换机返回结果给标识符 是否 以> 结尾
         *      如果 以">" 结尾，则发送 enable 命令 查看返回结果
         *         2)判断交换机返回结果是否为空，为空则返回"交换机连接失败"，
         *         2)判断交换机返回结果是否包含command 并且不包含 % ，不包含则返回"交换机连接成功"，
         *         3)判断交换机返回结果是否包含":",包含则发送配置密码
         *              todo 输入配置密码的返回结果，需要满足的条件现不能确定，需要和网络工程师确认
         *
         *      如果 以"#" 结尾，则直接返回 交换机连接成功
         *   1）如果以"#"结尾，则直接返回 交换机连接成功
         */
        // 获取交换机返回结果的最后一个字符
        String returnString_end = returnString.charAt(returnString.length() - 1) + "";
        /*判断 交换机返回结果给标识符 是否 以> 结尾*/
        /*思科交换机返回信息是 #  不需要发送 enable*/
        if (returnString_end.equals(">")) {
            /*发送 enable 命令 查看返回结果 */
            if (switchParameters.getMode().equalsIgnoreCase("ssh")) {
                returnString = switchParameters.getConnectMethod().sendCommand(switchParameters.getIp(), switchParameters.getSshConnect(), "enable", null);
            } else if (switchParameters.getMode().equalsIgnoreCase("telnet")) {
                List<String> stringList = switchParameters.getTelnetSwitchMethod().sendCommand(switchParameters.getIp(), switchParameters.getTelnetComponent(), "enable", null);
                if (stringList.size() != 0) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (String string : stringList) {
                        stringBuffer.append(string).append("\r\n");
                    }
                    returnString = StringBufferUtils.substring(stringBuffer, 0, stringBuffer.length() - 2);
                }
            }
            /*判断交换机返回结果是否为空*/
            if (returnString == null) {
                return "交换机连接失败";
            } else {
                String substring = returnString.substring(returnString.length() - 1, returnString.length());
                if (returnString.indexOf("command") != -1 && returnString.indexOf("%") != -1) {
                    return "交换机连接成功";
                } else if (substring.equalsIgnoreCase("#")) {
                    return "交换机连接成功";
                } else if (returnString.indexOf(":") != -1) {
                    /* 输入 配置密码*/
                    if (switchParameters.getMode().equalsIgnoreCase("ssh")) {
                        SshMethod connectMethod = switchParameters.getConnectMethod();
                        SshConnect sshConnect = switchParameters.getSshConnect();
                        returnString = connectMethod.sendCommand(switchParameters.getIp(), sshConnect, switchParameters.getConfigureCiphers(), null);
                    } else if (switchParameters.getMode().equalsIgnoreCase("telnet")) {
                        List<String> stringList = switchParameters.getTelnetSwitchMethod().sendCommand(switchParameters.getIp(), switchParameters.getTelnetComponent(), switchParameters.getConfigureCiphers(), null);
                        if (stringList.size() != 0) {
                            StringBuffer stringBuffer = new StringBuffer();
                            for (String string : stringList) {
                                stringBuffer.append(string).append("\r\n");
                            }
                            returnString = StringBufferUtils.substring(stringBuffer, 0, stringBuffer.length() - 2);
                        }
                    }
                    // todo 输入配置密码的返回结果，需要满足的条件现不能确定，需要和网络工程师确认
                    return "交换机连接成功";
                }
            }
            /*思科交换机返回信息是 #  不需要发送 enable*/
        } else if ((returnString.charAt(returnString.length() - 1) + "").equals("#")) {
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
     *
     * 通过null 查询初所有会的交换机基本信息的命令字符串集合
     * 遍历交换机基本信息的命令字符串集合 通过 扫描分析方法 获得所有提取信息
     * 通过返回提取的信息，给基本属性赋值
     *
     * 成功则返回基本信息 否则 遍历下一条 交换机基本信息的命令字符串集合信息
     */
    @ApiOperation("通用获取交换机基本信息")
    public AjaxResult getBasicInformationCurrency(SwitchParameters switchParameters) {
        /**
         * 1.提取配置文件中获取交换机基本信息的命令字符串。例如：show version;display version
         *      目前获取基本信息命令是多个命令是由;号分割的， 所以需要根据;来分割。例如：show version;display version
         */
        String[] commandsplit = ( (String) CustomConfigurationUtil.getValue("BasicInformation.getBrandCommand", Constant.getProfileInformation())).split(";");
        List<String> commandString = new ArrayList<>(); //预设交换机返回结果
        //遍历数据表命令 分割得到的 命令数组
        for (String command:commandsplit){

            /*根据交换机登录信息 与 具体命令，执行并返回交换机返回信息
             * 返回结果
             * 如果交换机返回信息错误，则返回信息为 null*/
            ExecuteCommand executeCommand = new ExecuteCommand();
            commandString = executeCommand.executeScanCommandByCommand(switchParameters, command);
            // 中断检查
            if (commandString == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
              return null;
            } else if (commandString == null || commandString.size() == 0){
                continue;
            }

            /**
             * 根据交换机返回结果 获取 交换机基本信息
             */
            HashMap<String, String> hashMap = analyzeStringToGetBasicInformation(commandString);
            if (hashMap.get("pinpai")!=null
                    && hashMap.get("xinghao")!=null
                    && hashMap.get("banben")!=null){
                hashMap.put("pinpai", hashMap.get("pinpai") );
                hashMap.put("xinghao", hashMap.get("xinghao") );
                hashMap.put("banben", removeSpecialSymbols(hashMap.get("banben")) );
                hashMap.put("zibanben", removeSpecialSymbols(hashMap.get("zibanben")) );
                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "基本信息",
                            "系统信息:"+switchParameters.getIp() +
                                    "基本信息:"+
                            "设备品牌:"+hashMap.get("pinpai")+
                            "设备型号:"+hashMap.get("xinghao")+
                            "内部固件版本:"+hashMap.get("banben")+
                            "子版本号:"+hashMap.get("zibanben")+"\r\n");
                switchParameters.setDeviceBrand(hashMap.get("pinpai"));
                switchParameters.setDeviceModel(hashMap.get("xinghao"));
                switchParameters.setFirmwareVersion(hashMap.get("banben"));
                switchParameters.setSubversionNumber(hashMap.get("zibanben"));
                return AjaxResult.success(switchParameters);
            }else {
                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "基本信息",
                            "异常:"+switchParameters.getIp() +
                                    "基本信息:"+
                            "设备品牌:"+hashMap.get("pinpai")+
                            "设备型号:"+hashMap.get("xinghao")+
                            "内部固件版本:"+hashMap.get("banben")+
                            "子版本号:"+hashMap.get("zibanben")+"\r\n");
            }
        }
        return AjaxResult.error("未定义该交换机获取基本信息命令及分析");
    }


    /*Inspection Completed*/
    /**
     * 通过 配置文件 获取取交换机基本信息规则
     * 根据交换机返回结果 获取 交换机基本信息
     * @return  返回  交换机基本信息
     * 品牌和型号应为一个单词 中间不包含空格
     */
    public HashMap<String,String> analyzeStringToGetBasicInformation(List<String> returns_String_List) {

        /*查询 数据库 交换机信息表 品牌名 集合*/
        informationService = SpringBeanUtil.getBean(IInformationService.class);
        /*  select distinct device_brand from information  */
        /* 该表 在定义交换机问题的时候 会同步交换机基本信息到 information表*/
        List<String> brandList = informationService.selectDeviceBrandList();
        StringBuffer stringBuffer = new StringBuffer();
        for (String returns_String:returns_String_List){
            stringBuffer.append(returns_String.trim()).append("\r\n");
        }
        /* 定义交换机返回信息中 包含的品牌名称 集合*/
        List<String> brands = new ArrayList<>();
        /*遍历品牌 例如：H3C*/
        for (String brandString:brandList){
            /* 遍历交换机返回 单词数组*/
            for (String word:returns_String_List){
                /*判断 是否 包含 品牌 */
                if (MyUtils.containIgnoreCase(word,brandString)){
                    brands.add(brandString);
                    break;
                }
            }
        }
        /* 如果定义的交换机品牌名称集合为空 即没有提取到交换机返回信息中的品牌信息 返回空集合*/
        if (MyUtils.isCollectionEmpty(brands)){
            return new HashMap<>();
        }
        /* 自动生成代码查询参数要求是数组 ，通过stream方法 将集合转化为数组 */
        String[] brandArray = brands.stream().toArray(String[]::new);
        /* 定义交换机返回信息中 包含的型号 集合*/
        List<Information> brand_model = new ArrayList<>();
        /* 根据匹配到的交换机品牌 查询 数据库 交换机信息表 型号 集合*/
        List<Information> informationList = informationService.selectDeviceModelListByArray(brandArray);
        for (Information information:informationList){
            for (String word:returns_String_List){
                /*判断是否包含型号*/
                if (MyUtils.containIgnoreCase(word,information.getDeviceModel())){
                    brand_model.add(information);
                }
            }
        }
        /* 如果定义的交换机 型号集合为空 即没有提取到交换机返回信息中的 型号信息 */
        if (MyUtils.isCollectionEmpty(brand_model)){
            /* 遍历型号信息 例如：S2152*/
            for (Information information:informationList){
                /* 全文匹配 */
                if (StringBufferUtils.stringBufferContainString(stringBuffer,information.getDeviceModel())){
                    brand_model.add(information);
                }
            }
        }
        /* 如果型号为空 则 返回空集合*/
        if (MyUtils.isCollectionEmpty(brand_model)){
            return new HashMap<>();
        }
        String firmwareVersion = null;
        String subversionNo = null;

        /** 设备版本  获取交换机版本关键字 */
        String deviceVersion = (String) CustomConfigurationUtil.getValue("BasicInformation.deviceVersion",Constant.getProfileInformation());
        /** 设备子版本 获取交换机子版本关键字*/
        String deviceSubversion = (String) CustomConfigurationUtil.getValue("BasicInformation.deviceSubversion",Constant.getProfileInformation());
        /*获取交换机版本关键字按;分割成关键词数组*/
        /*yml 配置文件中 多个值之间用;隔开*/
        List<String> deviceVersionSplit = Arrays.stream(deviceVersion.split(";")).collect(Collectors.toList());
        Collections.sort(deviceVersionSplit, Comparator.comparingInt(String::length).reversed());

        /* 遍历获取版本关键字*/
        for (String version:deviceVersionSplit){
            /* 判断一个字符串是否包含另一个字符串(忽略大小写) */
            if (!StringBufferUtils.stringBufferContainString(stringBuffer,version)){
                continue;
            }
            /* 遍历行信息*/
            for (int number = 0 ; number < returns_String_List.size(); number++){
                /*获取版本号关键词位置*/
                int rowposition = returns_String_List.get(number).toUpperCase().indexOf(version.toUpperCase());
                /*如果 rowposition 不为 -1 则说明包含 交换机返回信息的 number 行信息
                * 包含 版本号关键词位置*/
                if (rowposition != -1){
                    /* 截取 版本号关键词 后面的信息 例如：  5.20.99, Release 1106*/
                    String row = returns_String_List.get(number).substring( rowposition + version.length() ,returns_String_List.get(number).length() );
                    /**
                    * 接下来 通过子版本关键字 获取 版本和子版本中间的 数据
                    */
                    /* 子版本的关键词 分为 子版本号关键词数组*/
                    String[] deviceSubversionSplit = deviceSubversion.split(";");
                    /*遍历子版本关键词*/
                    for (int num = 0; num<deviceSubversionSplit.length; num++){
                        /*获取子版本关键词位置*/
                        int columnposition = row.toUpperCase().indexOf(deviceSubversionSplit[num].toUpperCase());
                        /*如果存在子版本关键词 则截取 版本关键词与子版本关键字中间位置信息  例如 ： "5.20.99, "  */
                        if (columnposition != -1){
                            firmwareVersion = row.substring(0,columnposition);
                            /*截取完 版本关键词 与 子版本关键字 中间位置信息
                            * 再判断 是否包含 , 如果包含 ， 则截取 ， 前信息*/
                            int position = firmwareVersion.indexOf(",");
                            if (position != -1){
                                firmwareVersion = firmwareVersion.substring(0,position);
                            }
                        }
                        /*如果 版本不为空 则结束循环*/
                        if (firmwareVersion!=null){
                            break;
                        }
                    }
                    /*如果 版本不为空 则结束循环*/
                    if (firmwareVersion!=null){
                        break;
                    }else {
                        /*如果 版本为空 则说明没有子版本关键词
                        * 则值判断,因素*/
                        int position = row.indexOf(",");
                        if (position != -1) {
                            firmwareVersion = row.substring(0, position);
                        }
                    }
                    /*如果 版本不为空 则结束循环*/
                    if (firmwareVersion!=null){
                        break;
                    }
                    if (firmwareVersion == null && MyUtils.thereAreNumbers(row)){
                        firmwareVersion = row.trim();
                        break;
                    }
                }else {
                    /*不包含版本信息*/
                    continue;
                }
            }
            if (firmwareVersion!=null){
                break;
            }
        }

        firmwareVersion = firmwareVersion.trim();
        /* Software Version : Version 3.0 RELEASE 0033 */
        while (firmwareVersion.startsWith(":") || firmwareVersion.toUpperCase().startsWith("Version".toUpperCase())){
            if (firmwareVersion.startsWith(":")){
                firmwareVersion = firmwareVersion.substring(1,firmwareVersion.length()).trim();
            }
            if (firmwareVersion.toUpperCase().startsWith("Version".toUpperCase())){
                firmwareVersion = firmwareVersion.substring("Version".length(),firmwareVersion.length()).trim();
            }
        }

        /** 设备子版本 */
        String[] deviceSubversionSplit =deviceSubversion.split(";");
        for (String version:deviceSubversionSplit){
            if (!StringBufferUtils.stringBufferContainString(stringBuffer,version)){
                continue;
            }
            for (int number = 0 ; number < returns_String_List.size(); number++){
                /* 获取版本号位置 */
                int rowposition = returns_String_List.get(number).toUpperCase().indexOf(version.toUpperCase());
                if (rowposition != -1){
                    /*判断是是否包含, 和 子版本关键词*/
                    subversionNo = returns_String_List.get(number).substring(rowposition + version.length(), returns_String_List.get(number).length());
                    int i = subversionNo.indexOf(",");
                    if (i != -1){
                        subversionNo = subversionNo.substring(0,i);
                    }
                    subversionNo = subversionNo.trim();
                    if (subversionNo.startsWith(":")){
                        subversionNo = subversionNo.substring(1,subversionNo.length()).trim();
                    }
                    if (subversionNo.startsWith("(") && subversionNo.endsWith(")")){
                        subversionNo = subversionNo.substring(1,subversionNo.length()-1).trim();
                    }
                }
                if (subversionNo!=null){
                    break;
                }
            }
            if (subversionNo!=null){
                break;
            }
        }
        if (firmwareVersion != null && subversionNo == null && firmwareVersion.indexOf("(")!=-1 && firmwareVersion.indexOf(")")!=-1){
            int i = firmwareVersion.indexOf("(");
            int j = firmwareVersion.indexOf(")");
            subversionNo = firmwareVersion.substring(i+1, j);
        }

        /* 创建返回对象 */
        HashMap<String,String> map = new HashMap<>();
        map.put("pinpai",brand_model.get(0).getDeviceBrand().equalsIgnoreCase("Quidway")?"Huawei":brand_model.get(0).getDeviceBrand());
        map.put("xinghao",brand_model.get(0).getDeviceModel());
        map.put("banben",firmwareVersion);
        map.put("zibanben",subversionNo);
        return map;
    }






    /*Inspection Completed*/
    /**
    * @Description 去除开头和结尾的 . ,
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
