package com.sgcc.advanced.controller;
import com.sgcc.advanced.domain.LightAttenuationCommand;
import com.sgcc.advanced.domain.LightAttenuationComparison;
import com.sgcc.advanced.service.ILightAttenuationCommandService;
import com.sgcc.advanced.service.ILightAttenuationComparisonService;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.controller.SwitchScanResultController;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.switchboard.SwitchIssueEcho;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.FunctionalMethods;
import com.sgcc.share.util.MyUtils;
import com.sgcc.share.util.PathHelper;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.webSocket.WebSocketService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 光衰功能
 */
@Api("光衰功能")
@RestController
@RequestMapping("/advanced/LuminousAttenuation")
@Transactional(rollbackFor = Exception.class)
public class LuminousAttenuation {

    @Autowired
    private ILightAttenuationComparisonService lightAttenuationComparisonService;
    @Autowired
    private ILightAttenuationCommandService lightAttenuationCommandService;

    /**
     * 光衰功能接口
     * @param switchParameters
     * @return
     */
    public AjaxResult obtainLightDecay(SwitchParameters switchParameters) {
        /*1：获取配置文件关于 光衰问题的 符合交换机品牌的命令的 配置信息*/
        LightAttenuationCommand lightAttenuationCommand = new LightAttenuationCommand();

        /**
         * 如果是 路由器 则在品牌后面添加路由器
         */
        if (switchParameters.getRouterFlag().equals("交换机")){
            lightAttenuationCommand.setBrand(switchParameters.getDeviceBrand());
        }else {
            lightAttenuationCommand.setBrand(switchParameters.getDeviceBrand()+"路由器");
        }


        lightAttenuationCommand.setSwitchType(switchParameters.getDeviceModel());
        lightAttenuationCommand.setFirewareVersion(switchParameters.getFirmwareVersion());
        lightAttenuationCommand.setSubVersion(switchParameters.getSubversionNumber());
        lightAttenuationCommandService = SpringBeanUtil.getBean(ILightAttenuationCommandService.class);
        List<LightAttenuationCommand> lightAttenuationCommandList = lightAttenuationCommandService.selectLightAttenuationCommandList(lightAttenuationCommand);

        /*2：当 配置文件光衰问题的命令 为空时 进行 日志写入*/
        if (MyUtils.isCollectionEmpty(lightAttenuationCommandList)){
            // todo 关于交换机获取端口号命令 的错误代码库  缺少传输给前端的信息
            try {
                PathHelper.writeDataToFileByName("IP地址:"+switchParameters.getIp()+"未定义"+switchParameters.getDeviceBrand()+"交换机获取端口号命令\r\n","光衰");
                return AjaxResult.error("未定义"+switchParameters.getDeviceBrand()+"交换机获取端口号命令");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        lightAttenuationCommand = getpojo(lightAttenuationCommandList);
        String command = lightAttenuationCommand.getGetPortCommand();

        /**
         * 3：配置文件光衰问题的命令 不为空时，执行交换机命令，返回交换机返回信息
         */
        String returnString = FunctionalMethods.executeScanCommandByCommand(switchParameters, command);

        /*returnString = "The brief information of interface(s) under route mode:\n" +
                "Link: ADM - administratively down; Stby - standby\n" +
                "Protocol: (s) - spoofing\n" +
                "Interface            Link Protocol Main IP         Description\n" +
                "Loop114              UP   UP(s)    10.122.114.208\n" +
                "M-E0/0/0             DOWN DOWN     --\n" +
                "NULL0                UP   UP(s)    --\n" +
                "Vlan3                UP   UP       10.98.138.147\n" +
                "Vlan4                UP   UP       10.98.139.239\n" +
                "Vlan6                UP   UP       10.98.138.2\n" +
                "Vlan7                UP   UP       10.98.136.13\n" +
                "Vlan50               UP   UP       100.1.2.252\n" +
                "Vlan200              UP   UP       10.98.137.71\n" +
                "Vlan2000             UP   UP       10.98.138.195   to-shiju\n" +
                "Vlan2001             UP   UP       10.122.119.161\n" +
                "\n" +
                "The brief information of interface(s) under bridge mode:\n" +
                "Link: ADM - administratively down; Stby - standby\n" +
                "Speed or Duplex: (a)/A - auto; H - half; F - full\n" +
                "Type: A - access; T - trunk; H - hybrid\n" +
                "Interface            Link Speed   Duplex Type PVID Description\n" +
                "BAGG1                UP   2G(a)   F(a)   T    1    To_HX_S7506E\n" +
                "GE0/0/1              UP   1G(a)   F(a)   T    1\n" +
                "GE0/0/2              ADM  auto    A      T    1\n" +
                "GE0/0/3              UP   1G(a)   F(a)   T    1\n" +
                "GE0/0/4              ADM  auto    A      T    1\n" +
                "GE0/0/5              UP   1G(a)   F(a)   T    1\n" +
                "GE0/0/6              ADM  auto    A      T    1\n" +
                "GE0/0/7              UP   1G(a)   F(a)   T    1    To_AnBeiSuo_S5720_G0/0/49\n" +
                "GE0/0/8              ADM  auto    A      A    1\n" +
                "GE0/0/9              ADM  auto    A      A    1\n" +
                "GE0/0/10             ADM  auto    A      A    1\n" +
                "GE0/0/11             DOWN 1G      F      T    1    To_ZhuLouJiFang2_XG0/0/3\n" +
                "GE0/0/12             UP   1G(a)   F(a)   T    1    To_HX_S7506E\n" +
                "GE0/0/13             ADM  auto    A      A    1\n" +
                "GE0/0/14             ADM  auto    A      A    1\n" +
                "GE0/0/15             ADM  auto    A      A    1\n" +
                "GE0/0/16             DOWN 1G      F      T    1    to_fajianbu_S3448\n" +
                "GE0/0/17             ADM  auto    A      A    1\n" +
                "GE0/0/18             ADM  auto    A      A    1\n" +
                "GE0/0/19             ADM  auto    A      A    1\n" +
                "GE0/0/20             ADM  auto    A      A    1\n" +
                "GE0/0/21             ADM  auto    A      A    1\n" +
                "GE0/0/22             ADM  auto    A      A    1\n" +
                "GE0/0/23             ADM  auto    A      A    1\n" +
                "GE0/0/24             ADM  auto    A      T    1    to_fajianbu_S3448\n" +
                "GE0/0/25             DOWN auto    A      T    1\n" +
                "GE0/0/26             DOWN auto    A      T    1\n" +
                "GE0/0/27             DOWN auto    A      T    1\n" +
                "GE0/0/28             DOWN auto    A      T    1\n" +
                "GE0/0/29             DOWN auto    A      T    1\n" +
                "GE0/0/30             UP   1G(a)   F(a)   T    1    To_HX_S7506E\n" +
                "GE0/0/31             UP   1G(a)   F(a)   A    2001 To_ShiJu\n" +
                "GE0/0/32             ADM  auto    A      A    200  To_HX_S7506E";
        returnString = MyUtils.trimString(returnString);*/





        /*4: 如果交换机返回信息为 null 则 命令错误，交换机返回错误信息*/
        if (returnString == null){
            // todo 关于交换机返回错误信息 的错误代码库
            WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"系统信息:"+switchParameters.getIp()+":获取端口号命令错误,请重新定义\r\n");
            try {
                PathHelper.writeDataToFileByName(switchParameters.getIp()+":获取端口号命令错误,请重新定义\r\n","光衰");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("IP地址:"+switchParameters.getIp()+"获取端口号命令错误,请重新定义");
        }

        /*5：如果交换机返回信息不为 null说明命令执行正常, 则继续 根据交换机返回信息获取获取光衰端口号*/
        List<String> port = ObtainUPStatusPortNumber(returnString);
        for (String str:port){
            System.err.println("提取到的端口号"+str);
        }
        /*6：获取光衰端口号方法返回集合判断是否为空，说明没有端口号为开启状态 UP，是则进行*/
        if (MyUtils.isCollectionEmpty(port)){
            // todo 关于没有端口号为UP状态 的错误代码库
            try {
                PathHelper.writeDataToFileByName("IP地址:"+switchParameters.getIp()+"无UP状态端口号\r\n","光衰");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("IP地址:"+switchParameters.getIp()+"未获取到UP状态端口号");
        }




        /*7：如果交换机端口号为开启状态 UP 不为空 则需要查看是否需要转义：
        GE转译为GigabitEthernet  才能执行获取交换机端口号光衰参数命令*/
        String conversion = lightAttenuationCommand.getConversion();
        String[] conversionSplit = conversion.split(";");
        for (String convers:conversionSplit){
            String[] conversSplit = convers.split(":");
            for (int num=0;num<port.size();num++){
                if (MyUtils.getFirstLetters(port.get(num)).trim().equals(conversSplit[0])){
                    port.set(num,port.get(num).replace(conversSplit[0],conversSplit[1]));
                }
            }
        }

        /*8：根据 up状态端口号 及交换机信息 获取光衰参数  lightAttenuationCommand.getGetParameterCommand()*/
        HashMap<String, Double> getparameter = getparameter(port, switchParameters,lightAttenuationCommand.getGetParameterCommand());
        /*9：获取光衰参数为空*/
        if (MyUtils.isMapEmpty(getparameter)){
            // todo 关于未获取到光衰参数 的错误代码库
            try {
                PathHelper.writeDataToFileByName("IP地址:"+switchParameters.getIp()+"未获取到光衰参数\r\n","光衰");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("IP地址:"+switchParameters.getIp()+"未获取到光衰参数");
        }

        /*10:获取光衰参数不为空*/
        try {
            for (String portstr:port){
                // todo  根据光衰参数阈值  的代码库 回显和日志
                String lightAttenuationInformation = "IP地址:"+switchParameters.getIp()+
                        "端口号:"+portstr+"TX:"+getparameter.get(portstr+"TX")+
                                      "RX:"+getparameter.get(portstr+"RX");
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),
                        "系统信息:"+switchParameters.getIp()+":"+"光衰:"+ lightAttenuationInformation+"\r\n");
                PathHelper.writeDataToFileByName(lightAttenuationInformation+"\r\n","光衰");
                SwitchScanResultController switchScanResultController = new SwitchScanResultController();
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("ProblemName","光衰");
                LightAttenuationComparison lightAttenuationComparison = new LightAttenuationComparison();
                lightAttenuationComparison.setSwitchIp(switchParameters.getIp());


                /*获取交换机四项基本信息ID*/
                lightAttenuationComparison.setSwitchId(FunctionalMethods.getSwitchParametersId(switchParameters));


                lightAttenuationComparison.setPort(portstr);
                lightAttenuationComparisonService = SpringBeanUtil.getBean(ILightAttenuationComparisonService.class);
                List<LightAttenuationComparison> lightAttenuationComparisons = lightAttenuationComparisonService.selectLightAttenuationComparisonList(lightAttenuationComparison);

                /*当光衰参数不为空时  光衰参数存入 光衰比较表*/
                if (getparameter.get(portstr+"TX") != null && getparameter.get(portstr+"RX") != null){
                    average(switchParameters,getparameter,portstr);
                }else {
                    continue;
                }
                if (MyUtils.isCollectionEmpty(lightAttenuationComparisons)){
                    hashMap.put("IfQuestion","无问题");
                }else {
                    lightAttenuationComparison = lightAttenuationComparisons.get(0);
                    if (lightAttenuationComparison.getRxRatedDeviation()!=null && lightAttenuationComparison.getTxRatedDeviation()!=null){
                        hashMap.put("IfQuestion",meanJudgmentProblem(lightAttenuationComparison));
                    }
                }

                hashMap.put("parameterString","端口号=:=是=:="+portstr+"=:=光衰参数=:=是=:=" +
                        "TX:"+getparameter.get(portstr+"TX")+
                        "  RX:"+getparameter.get(portstr+"RX"));
                Long insertId = switchScanResultController.insertSwitchScanResult(switchParameters, hashMap);
                SwitchIssueEcho switchIssueEcho = new SwitchIssueEcho();
                switchIssueEcho.getSwitchScanResultListByData(switchParameters.getLoginUser().getUsername(),insertId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return AjaxResult.success();
    }


    public static String meanJudgmentProblem(LightAttenuationComparison lightAttenuationComparison) {
        double rxLatestNumber = MyUtils.stringToDouble(lightAttenuationComparison.getRxLatestNumber());
        double txLatestNumber = MyUtils.stringToDouble(lightAttenuationComparison.getTxLatestNumber());
        double rxStartValue = MyUtils.stringToDouble(lightAttenuationComparison.getRxStartValue());
        double txStartValue = MyUtils.stringToDouble(lightAttenuationComparison.getTxStartValue());
        DecimalFormat df = new DecimalFormat("#.0000");
        Double rxfiberAttenuation = Math.abs(rxLatestNumber - rxStartValue);
        Double txfiberAttenuation = Math.abs(txLatestNumber - txStartValue);
        rxfiberAttenuation = MyUtils.stringToDouble(df.format(rxfiberAttenuation));
        txfiberAttenuation = MyUtils.stringToDouble(df.format(txfiberAttenuation));
        if (rxfiberAttenuation>MyUtils.stringToDouble(lightAttenuationComparison.getRxRatedDeviation()) || txfiberAttenuation>MyUtils.stringToDouble(lightAttenuationComparison.getTxRatedDeviation())){
            return "有问题";
        }
        return "无问题";
    }

    /**
     * 根据交换机返回信息获取获取UP状态端口号
     * @param returnString
     * @return
     */
    public static List<String> ObtainUPStatusPortNumber(String returnString) {
        String[] returnStringSplit = returnString.split("\r\n");
        List<String> strings = new ArrayList<>();
        for (String string:returnStringSplit){
            /*包含 交换机返回行信息转化为大写 UP状态  不能为COPPER铜缆的  并且该行带有“/”的 存放入端口待取集合*/
            if ((string.toUpperCase().indexOf(" UP ")!=-1) && string.indexOf("/")!=-1 && (string.toUpperCase().indexOf("COPPER") == -1)){
                strings.add(string.trim());
            }
        }
        /*判断端口待取集合是否为空*/
        if (MyUtils.isCollectionEmpty(strings)){
            return null;
        }
        List<String> port = new ArrayList<>();
        /*遍历端口待取集合 执行取值方法 获取端口号*/
        for (String information:strings){
            /*根据 UP 截取端口号*/
            String terminalSlogan = getTerminalSlogan(information);
            if (terminalSlogan != null && !(MyUtils.getFirstLetters(terminalSlogan).equalsIgnoreCase("Eth"))){
                port.add(terminalSlogan);
            }
        }
        return port;
    }


    /**
     * 根据 UP 截取端口号
     * @param information
     * @return
     */
    public static String getTerminalSlogan(String information){
        /*GigabitEthernet 9/1 up routed Full 1000M fiber*/
        /*根据UP分割字符串*/
        /*交换机信息 根据 up(忽略大小写) 分割*/
        String[] informationSplit = MyUtils.splitIgnoreCase(information," UP ");
        /*遍历数组包含/的为端口号 但不能确定端口号是否完全
        * 此时需要判断提取到的端口号是否包含字母
        * 包含则为完全端口号 否则为不完全端口号，需要加前面的GigabitEthernet*/
        for (String string:informationSplit){
            if (string.indexOf("/")!=-1){
                String[] string_split = string.split(" ");
                for (int num = 0;num < string_split.length;num++){
                    if (string_split[num].indexOf("/")!=-1){
                        /*判断提取到的端口号是否包含字母*/
                        if (MyUtils.judgeContainsStr(string_split[num])){
                            /*包含则为完全端口号 否则为不完全端口号*/
                            return string_split[num];
                        }else {
                            /*例如：  GigabitEthernet 2/1 */
                            /*否则为不完全端口号，需要加前面的GigabitEthernet*/
                            information =string_split[num];
                            return string_split[--num] +" "+ information ;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据 up状态端口号 及交换机信息 获取光衰参数
     * @param portNumber 端口号
     * @param switchParameters 交换机信息类
     * @return
     */
    public HashMap<String,Double> getparameter(List<String> portNumber,SwitchParameters switchParameters,String command) {
        /*获取配置信息中 符合品牌的 获取基本信息的 获取光衰参数的 命令*/


        /*创建 返回对象 HashMap*/
        HashMap<String,Double> hashMap = new HashMap<>();
        /*端口号集合 需要检测各端口号的光衰参数*/
        for (String port:portNumber){
            /*替换端口号 得到完整的 获取端口号光衰参数命令 */
            String FullCommand = command.replaceAll("端口号",port);
            /**
             * 交换机执行命令 并返回结果
             */
            String returnResults = FunctionalMethods.executeScanCommandByCommand(switchParameters, FullCommand);
            /*returnResults = "GigabitEthernet0/0/12 transceiver diagnostic information:\n" +
                    "  Current diagnostic parameters:\n" +
                    "    Temp(¡ãC)  Voltage(V)  Bias(mA)  RX power(dBm)  TX power(dBm)\n" +
                    "    39        3.31        8.92      -6.88          -6.18\n" +
                    "\n" +
                    "<AnPingJu_H3C_7503E>";
            returnResults = MyUtils.trimString(returnResults);*/


            if (returnResults == null){
                // todo 获取光衰参数命令错误代码库
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"系统信息:"+switchParameters.getIp()+":获取光衰参数命令错误,请重新定义\r\n");
                try {
                    PathHelper.writeDataToFileByName(switchParameters.getIp()+":获取光衰参数命令错误,请重新定义\r\n","光衰");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }

            /*提取光衰参数*/
            HashMap<String, Double> values = getDecayValues(returnResults,switchParameters);

            if (values == null){
                // todo 为提取到光衰参数
                continue;
            }

            hashMap.put(port+"TX",values.get("TX"));
            hashMap.put(port+"RX",values.get("RX"));
        }
        return hashMap;
    }


    /**
     * 提取光衰参数
     * @param string
     * @return
     */
    public static HashMap<String,Double> getDecayValues(String string,SwitchParameters switchParameters) {
        /*切割成行信息*/
        String[] Line_split = string.split("\r\n");


        /*自定义 光衰参数默认给个 100*/
        double txpower = 1;
        double rxpower = 1;

        List<String> keyValueList = new ArrayList<>();
        for (int number = 0 ;number<Line_split.length;number++) {
            /* 获取 TX POWER 和 RX POWER 的位置
            * 当其中一个值不为 -1时 则为key：value格式
            * 如果全不为 -1时 则是 两个光衰参数在同一行 的格式*/
            int tx = Line_split[number].toUpperCase().indexOf("TX POWER");
            int rx = Line_split[number].toUpperCase().indexOf("RX POWER");
            /* 设置 光衰参数的格式 预设为0key：value格式   为1是RX、TX  为-1时TX、RX*/
            int num = 0 ;
            if (tx!=-1 && rx!=-1){
                /*如果全不为 -1时 则是 两个光衰参数在同一行 的格式*/
                if (tx > rx){
                    num = 1;
                }else if (tx < rx){
                    num = -1;
                }
            }else if (tx ==-1 && rx ==-1){
                /* RX、TX 都不包含 则进入下一循环*/
                continue;
            }

            if (num == 0 && (tx != -1 || rx != -1)){
                /* 包含 TX 或者 RX */
                /*key : value*/
                keyValueList.add(Line_split[number]);
            }else {
                /*两个都包含 则 两个参数值在一行*/
                String nextrow = Line_split[number+1];
                /*字符串截取double值*/
                List<Double> values = MyUtils.StringTruncationDoubleValue(nextrow);
                values = values.stream()
                        .filter(i -> i < 0)
                        .collect(Collectors.toList());
                if (values.size()!=2){
                    /*光衰参数行有多余2个负数 无法去除*/
                    // todo 光衰参数取值失败 光衰参数行有多于2个负数 错误代码
                    return null;
                }
                if (num == 1){
                    /*RX   TX*/
                    rxpower = values.get(0);
                    txpower = values.get(1);
                }else if (num == -1){
                    /*TX  RX*/
                    txpower = values.get(0);
                    rxpower = values.get(1);
                }
                break;
            }
        }

        /*key ： value 格式*/
        if (keyValueList.size()!=0){
            /*当包含 TX POWER 或 RX POWER 的数多余2条是 要再次筛选  光衰参数信息 或是阈值信息*/
            if (keyValueList.size() > 2){
                /*存储再次筛选后的 行信息*/
                List<String> keylist = new ArrayList<>();
                for (int num = 0 ; num<keyValueList.size();num++){

                    /*Current Rx Power(dBM)                 :-11.87
                      Default Rx Power High Threshold(dBM)  :-2.00
                      Default Rx Power Low  Threshold(dBM)  :-23.98
                      Current Tx Power(dBM)                 :-2.80
                      Default Tx Power High Threshold(dBM)  :1.00
                      Default Tx Power Low  Threshold(dBM)  :-6.00*/
                    /*遇到包含 CURRENT 是则存储行信息集合
                    * 下面两行是否包含HIGH 和 LOW */
                    if (MyUtils.containIgnoreCase(keyValueList.get(num),"CURRENT")){
                        keylist.add(keyValueList.get(num));
                        if (MyUtils.containIgnoreCase(keyValueList.get(num+1),"HIGH") || MyUtils.containIgnoreCase(keyValueList.get(num+1),"LOW")){
                            keylist.add(keyValueList.get(num+1));
                        }
                        if (MyUtils.containIgnoreCase(keyValueList.get(num+2),"HIGH") || MyUtils.containIgnoreCase(keyValueList.get(num+2),"LOW")){
                            keylist.add(keyValueList.get(num+2));
                        }
                    }
                }
                if (keylist.size() > 1){
                    keyValueList = keylist;
                }
            }


            /*遍历 行信息集合*/
            for (String keyvalue:keyValueList){
                /*当 行信息包含 RX 说明是 RX数据*/
                if (MyUtils.containIgnoreCase(keyvalue,"RX")){
                    if (MyUtils.containIgnoreCase(keyvalue,"RX POWER")){
                        /*获取负数值 如果一个则是光衰 如果三个则是包含阈值*/
                        List<Double> doubleList = MyUtils.StringTruncationDoubleValue(keyvalue);
                        if (doubleList.size()==1){
                            rxpower = doubleList.get(0);
                        }else if (doubleList.size()==3){
                            rxpower = doubleList.get(0);
                        }else {
                            // todo 光衰参数取值失败 光衰参数行负数数量不正确 错误代码
                            return null;
                        }
                    }else if (MyUtils.containIgnoreCase(keyvalue,"HIGH")){
                        List<Double> doubleList = MyUtils.StringTruncationDoubleValue(keyvalue);
                        if (doubleList.size()==1){
                            rxpower = doubleList.get(0);
                        }
                    }else if (MyUtils.containIgnoreCase(keyvalue,"LOW")){
                        List<Double> doubleList = MyUtils.StringTruncationDoubleValue(keyvalue);
                        if (doubleList.size()==1){
                            rxpower = doubleList.get(0);
                        }
                    }
                }
                /*当 行信息包含 TX 说明是 TX数据*/
                if (MyUtils.containIgnoreCase(keyvalue,"TX")){
                    if (MyUtils.containIgnoreCase(keyvalue,"TX POWER")){
                        /*获取负数值 如果一个则是光衰 如果三个则是包含阈值*/
                        List<Double> doubleList = MyUtils.StringTruncationDoubleValue(keyvalue);
                        if (doubleList.size()==1){
                            txpower = doubleList.get(0);
                        }else if (doubleList.size()==3){
                            txpower = doubleList.get(0);
                        }else {
                            // todo 光衰参数取值失败 光衰参数行负数数量不正确 错误代码
                            return null;
                        }
                    }else if (MyUtils.containIgnoreCase(keyvalue,"HIGH")){
                        List<Double> doubleList = MyUtils.StringTruncationDoubleValue(keyvalue);
                        if (doubleList.size()==1){
                            txpower = doubleList.get(0);
                        }
                    }else if (MyUtils.containIgnoreCase(keyvalue,"LOW")){
                        List<Double> doubleList = MyUtils.StringTruncationDoubleValue(keyvalue);
                        if (doubleList.size()==1){
                            txpower = doubleList.get(0);
                        }
                    }
                }
            }

        }
        if (Double.valueOf(txpower).doubleValue() == 1 || Double.valueOf(rxpower).doubleValue() == 1)
            return null;
        HashMap<String,Double> hashMap = new HashMap<>();
        hashMap.put("TX",Double.valueOf(txpower).doubleValue() == 1?null:txpower);
        hashMap.put("RX",Double.valueOf(rxpower).doubleValue() == 1?null:rxpower);
        return hashMap;
    }

    /**
     * 光衰参数存入 光衰比较表
     * @param switchParameters
     * @param hashMap
     * @param port
     * @return
     */
    public List<LightAttenuationComparison> average(SwitchParameters switchParameters,HashMap<String,Double> hashMap,String port) {
        LightAttenuationComparison lightAttenuationComparison = new LightAttenuationComparison();
        lightAttenuationComparison.setSwitchIp(switchParameters.getIp());
        /*获取交换机四项基本信息ID*/
        lightAttenuationComparison.setSwitchId(FunctionalMethods.getSwitchParametersId(switchParameters));
        lightAttenuationComparison.setPort(port);
        lightAttenuationComparisonService = SpringBeanUtil.getBean(ILightAttenuationComparisonService.class);
        List<LightAttenuationComparison> lightAttenuationComparisons = lightAttenuationComparisonService.selectLightAttenuationComparisonList(lightAttenuationComparison);

        String rx = hashMap.get(port+"RX") + "";
        String tx = hashMap.get(port+"TX") + "";
        if (MyUtils.isCollectionEmpty(lightAttenuationComparisons)){
            /*需要新插入信息*/
            lightAttenuationComparison = new LightAttenuationComparison();
            lightAttenuationComparison.setSwitchIp(switchParameters.getIp());
            /*获取交换机四项基本信息ID*/
            lightAttenuationComparison.setSwitchId(FunctionalMethods.getSwitchParametersId(switchParameters));
            lightAttenuationComparison.setPort(port);
            lightAttenuationComparison.setNumberParameters(1);
            lightAttenuationComparison.setRxLatestNumber(rx);
            lightAttenuationComparison.setRxAverageValue(rx);
            lightAttenuationComparison.setRxStartValue(rx);
            lightAttenuationComparison.setTxLatestNumber(tx);
            lightAttenuationComparison.setTxAverageValue(tx);
            lightAttenuationComparison.setTxStartValue(tx);

            lightAttenuationComparison.setRxRatedDeviation(""+CustomConfigurationUtil.getValue("光衰.rxRatedDeviation",Constant.getProfileInformation()));
            lightAttenuationComparison.setTxRatedDeviation(""+ CustomConfigurationUtil.getValue("光衰.txRatedDeviation",Constant.getProfileInformation()));

            lightAttenuationComparisonService.insertLightAttenuationComparison(lightAttenuationComparison);
        }else {
            lightAttenuationComparison = lightAttenuationComparisons.get(0);
            lightAttenuationComparison.setRxLatestNumber(rx);
            double rxAverageValue = updateAverage(lightAttenuationComparison.getNumberParameters(), MyUtils.stringToDouble(lightAttenuationComparison.getRxAverageValue()), MyUtils.stringToDouble(rx));
            lightAttenuationComparison.setRxAverageValue(rxAverageValue+"");
            lightAttenuationComparison.setTxLatestNumber(tx);
            double txAverageValue = updateAverage(lightAttenuationComparison.getNumberParameters(), MyUtils.stringToDouble(lightAttenuationComparison.getTxAverageValue()), MyUtils.stringToDouble(tx));
            lightAttenuationComparison.setTxAverageValue("" + txAverageValue);
            lightAttenuationComparison.setNumberParameters(lightAttenuationComparison.getNumberParameters()+1);
            lightAttenuationComparisonService.updateLightAttenuationComparison(lightAttenuationComparison);
        }
        return null;
    }

    /**
     * 保留四位小数
     * @param numberParameters
     * @param avg
     * @param newParameter
     * @return
     */
    public static double updateAverage(int numberParameters ,double avg, double newParameter) {
        DecimalFormat df = new DecimalFormat("#.0000");
        String result = df.format((newParameter + numberParameters * avg) / (numberParameters + 1));

        return MyUtils.stringToDouble(result);
    }

    public static LightAttenuationCommand getpojo(List<LightAttenuationCommand> pojoList) {
        LightAttenuationCommand lightAttenuationCommand = new LightAttenuationCommand();
        int sum = 0;
        for (LightAttenuationCommand pojo:pojoList){
            int num = 0 ;
            if (!(pojo.getBrand().equals("*"))){
                ++num;
            }
            if (!(pojo.getSwitchType().equals("*"))){
                ++num;
            }
            if (!(pojo.getFirewareVersion().equals("*"))){
                ++num;
            }
            if (!(pojo.getSubVersion().equals("*"))){
                ++num;
            }
            if (sum<num){
                sum = num;
                lightAttenuationCommand = pojo;
            }else if (sum == num && (pojo.getSwitchType().equals("*")) && (pojo.getSubVersion().equals("*"))){
                lightAttenuationCommand = pojo;
            }
        }
        return lightAttenuationCommand;
    }
}
