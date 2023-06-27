package com.sgcc.advanced.controller;
import com.sgcc.advanced.domain.LightAttenuationCommand;
import com.sgcc.advanced.domain.LightAttenuationComparison;
import com.sgcc.advanced.service.ILightAttenuationCommandService;
import com.sgcc.advanced.service.ILightAttenuationComparisonService;
import com.sgcc.advanced.utils.ScreeningMethod;
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

        lightAttenuationCommand.setBrand(switchParameters.getDeviceBrand());
        lightAttenuationCommand.setSwitchType(switchParameters.getDeviceModel());
        lightAttenuationCommand.setFirewareVersion(switchParameters.getFirmwareVersion());
        lightAttenuationCommand.setSubVersion(switchParameters.getSubversionNumber());
        lightAttenuationCommandService = SpringBeanUtil.getBean(ILightAttenuationCommandService.class);
        List<LightAttenuationCommand> lightAttenuationCommandList = lightAttenuationCommandService.selectLightAttenuationCommandListBySQL(lightAttenuationCommand);

        /*2：当 配置文件光衰问题的命令 为空时 进行 日志写入*/
        if (MyUtils.isCollectionEmpty(lightAttenuationCommandList)){
            try {
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"异常:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        "问题为:光衰功能未定义获取端口号命令\r\n");
                PathHelper.writeDataToFileByName(
                        "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:光衰功能未定义获取端口号命令\r\n"
                        , "问题日志");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("未定义"+switchParameters.getDeviceBrand()+"交换机获取端口号命令");
        }

        /**
         * 从lightAttenuationCommandList中 获取四项基本最详细的数据
         */
        lightAttenuationCommand = ScreeningMethod.ObtainPreciseEntityClassesLightAttenuationCommand(lightAttenuationCommandList);
        String command = lightAttenuationCommand.getGetPortCommand();

        /**
         * 3：配置文件光衰问题的命令 不为空时，执行交换机命令，返回交换机返回信息
         */
        String returnString = FunctionalMethods.executeScanCommandByCommand(switchParameters, command);

        /*returnString = "PHY: Physical\n" +
                "*down: administratively down\n" +
                "^down: standby\n" +
                "(l): loopback\n" +
                "(s): spoofing\n" +
                "(b): BFD down\n" +
                "(e): EFM down\n" +
                "(d): Dampening Suppressed\n" +
                "InUti/OutUti: input utility/output utility\n" +
                "Interface PHY Protocol InUti OutUti inErrors outErrors\n" +
                "Aux0/0/1 *down down 0% 0% 0 0\n" +
                "GigabitEthernet0/0/0 *down down 0% 0% 0 0\n" +
                "GigabitEthernet1/0/0 *down down 0% 0% 0 0\n" +
                "GigabitEthernet1/0/1 up up 0.01% 0.17% 0 0\n" +
                "GigabitEthernet1/0/2 up up 0.41% 0.19% 0 0\n" +
                "GigabitEthernet1/0/3 up up 0.01% 0.01% 0 0\n" +
                "GigabitEthernet1/0/4 up up 0.01% 0.01% 0 0\n" +
                "GigabitEthernet1/0/5 up up 0.01% 0.01% 0 0\n" +
                "GigabitEthernet1/0/6 up up 0.01% 0.01% 0 0\n" +
                "GigabitEthernet1/0/7 *down down 0% 0% 0 0\n" +
                "GigabitEthernet1/1/0 up up 0.36% 0.41% 0 0\n" +
                "GigabitEthernet1/1/1 up up 0.01% 0.01% 0 0\n" +
                "GigabitEthernet1/1/2 *down down 0% 0% 0 0\n" +
                "GigabitEthernet1/1/3 *down down 0% 0% 0 0\n" +
                "GigabitEthernet1/1/4 *down down 0% 0% 0 0\n" +
                "GigabitEthernet1/1/5 *down down 0% 0% 0 0\n" +
                "GigabitEthernet1/1/6 *down down 0% 0% 0 0\n" +
                "GigabitEthernet1/1/7 *down down 0% 0% 0 0\n" +
                "LoopBack0 up up(s) 0% 0% 0 0\n" +
                "LoopBack1 up up(s) 0% 0% 0 0\n" +
                "LoopBack2 up up(s) 0% 0% 0 0\n" +
                "NULL0 up up(s) 0% 0% 0 0\n" +
                "Vlanif609 up up -- -- 0 0\n" +
                "Vlanif1000 up up -- -- 0 0\n" +
                "Vlanif1200 down down -- -- 0 0\n" +
                "Vlanif2001 up up -- -- 0 0\n" +
                "Vlanif2003 up up -- -- 0 0\n" +
                "Vlanif2008 down down -- -- 0 0\n" +
                "Vlanif2010 up up -- -- 0 0";
        returnString = MyUtils.trimString(returnString);*/

        /*4: 如果交换机返回信息为 null 则 命令错误，交换机返回错误信息*/
        if (returnString == null){

            try {
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"异常:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        "问题为:光衰功能获取端口号命令错误,需要重新定义\r\n");
                PathHelper.writeDataToFileByName(
                        "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:光衰功能获取端口号命令错误,需要重新定义\r\n"
                        , "问题日志");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("IP地址为:"+switchParameters.getIp()+","+"问题为:光衰功能获取端口号命令错误,需要重新定义\r\n");
        }

        /**
         * 5：如果交换机返回信息不为 null说明命令执行正常,
         * 则继续 根据交换机返回信息获取获取光衰端口号
         */
        List<String> port = ObtainUPStatusPortNumber(returnString);

        /*6：获取光衰端口号方法返回集合判断是否为空，说明没有端口号为开启状态 UP，是则进行*/
        if (MyUtils.isCollectionEmpty(port)){
            // todo 关于没有端口号为UP状态 的错误代码库
            try {
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"异常:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        "问题为:光衰功能无UP状态端口号\r\n");
                PathHelper.writeDataToFileByName(
                        "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:光衰功能无UP状态端口号\r\n"
                        , "问题日志");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("IP地址为:"+switchParameters.getIp()+","+"问题为:光衰功能无UP状态端口号\r\n");
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

        /**
         * 8：根据 up状态端口号 及交换机信息
         * 获取光衰参数  lightAttenuationCommand.getGetParameterCommand()
         */
        HashMap<String, Double> getparameter = getparameter(port, switchParameters,lightAttenuationCommand.getGetParameterCommand());
        /*9：获取光衰参数为空*/
        if (MyUtils.isMapEmpty(getparameter)){
            try {
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"异常:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        "问题为:光衰功能所有UP状态端口皆未获取到光衰参数\r\n");
                PathHelper.writeDataToFileByName(
                        "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:光衰功能所有UP状态端口皆未获取到光衰参数\r\n"
                        , "问题日志");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("IP地址:"+switchParameters.getIp()+"未获取到光衰参数");
        }

        /*10:获取光衰参数不为空*/
        try {
            for (String portstr:port){
                // todo  根据光衰参数阈值  的代码库 回显和日志
                try {
                    String subversionNumber = switchParameters.getSubversionNumber();
                    if (subversionNumber!=null){
                        subversionNumber = "、"+subversionNumber;
                    }
                    WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"系统信息:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题为:光衰功能端口号:"+portstr+" TX:"+getparameter.get(portstr+"TX")+" RX:"+getparameter.get(portstr+"RX")+"\r\n");
                    PathHelper.writeDataToFileByName(
                            "IP地址为:"+switchParameters.getIp()+","+
                                    "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                    "问题为:光衰功能端口号:"+portstr+" TX:"+getparameter.get(portstr+"TX")+" RX:"+getparameter.get(portstr+"RX")+"\r\n"
                            , "光衰");
                } catch (IOException e) {
                    e.printStackTrace();
                }


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
        } catch (Exception e) {
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
            if ((string.toUpperCase().indexOf(" UP ")!=-1)  && (string.toUpperCase().indexOf("COPPER") == -1)){
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
        /**
         * 获取端口号
         */
        String deviceVersion = (String) CustomConfigurationUtil.getValue("obtainPortNumber.keyword",Constant.getProfileInformation());
        String[] keywords = deviceVersion.trim().split(" ");

        /*GigabitEthernet 9/1 up routed Full 1000M fiber*/
        /*根据UP分割字符串*/
        /*交换机信息 根据 up(忽略大小写) 分割*/
        String[] informationSplit = MyUtils.splitIgnoreCase(information," UP ");
        /*遍历数组包含/的为端口号 但不能确定端口号是否完全
        * 此时需要判断提取到的端口号是否包含字母
        * 包含则为完全端口号 否则为不完全端口号，需要加前面的GigabitEthernet*/
        for (String string:informationSplit){

            String[] string_split = string.split(" ");
            for (int num = 0;num < string_split.length;num++){
                for (String keyword:keywords){
                    if (string_split[num].toUpperCase().startsWith(keyword.toUpperCase().toUpperCase())){
                        /*判断提取到的端口号是否包含字母*/
                        if (MyUtils.judgeContainsStr(string_split[num])){
                            /*包含则为完全端口号 否则为不完全端口号*/
                            return string_split[num];
                        }else {
                            /*例如：  GigabitEthernet 2/1 */
                            /*否则为不完全端口号，需要加后面的GigabitEthernet*/
                            return string_split[num] +" "+ string_split[num++] ;
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


            /*returnResults = "GigabitEthernet2/0/3 current state : UP\n" +
                    "Line protocol current state : UP\n" +
                    "Last line protocol up time : 2023-05-25 13:38:08\n" +
                    "Description:TO_GuCheng_NE40E-X8_GE1/0/2\n" +
                    "Route Port,The Maximum Transmit Unit is 1500\n" +
                    "Internet Address is 11.36.97.122/30\n" +
                    "IP Sending Frames' Format is PKTFMT_ETHNT_2, Hardware address is 0819-a6f2-be66\n" +
                    "The Vendor PN is PT7620-51-3W\n" +
                    "The Vendor Name is NEOPHOTONICS\n" +
                    "Port BW: 1G, Transceiver max BW: 1G, Transceiver Mode: SingleMode\n" +
                    "WaveLength: 1550nm, Transmission Distance: 80km\n" +
                    "Rx Power: -14.79dBm, Tx Power: 2.46dBm\n" +
                    "Loopback:none, full-duplex mode, negotiation: disable, Pause Flowcontrol:Receive Enable and Send Enable\n" +
                    "Last physical up time : 2023-05-25 13:38:08\n" +
                    "Last physical down time : 2023-05-25 13:33:09\n" +
                    "Statistics last cleared:never\n" +
                    "Last 300 seconds input rate: 6000 bits/sec, 5 packets/sec\n" +
                    "Last 300 seconds output rate: 60808 bits/sec, 28 packets/sec\n" +
                    "Input: 59169256274248 bytes, 57573548284 packets\n" +
                    "Output: 667773712175 bytes, 1890058654 packets\n" +
                    "Input:\n" +
                    "Unicast: 57531373844 packets, Multicast: 42145421 packets\n" +
                    "Broadcast: 29019 packets, JumboOctets: 14044738164 packets\n" +
                    "CRC: 93 packets, Symbol: 0 packets\n" +
                    "Overrun: 0 packets, InRangeLength: 0 packets\n" +
                    "LongPacket: 0 packets, Jabber: 0 packets, Alignment: 0 packets\n" +
                    "Fragment: 0 packets, Undersized Frame: 1902 packets\n" +
                    "RxPause: 0 packets\n" +
                    "Output:\n" +
                    "Unicast: 1841427885 packets, Multicast: 48579685 packets\n" +
                    "Broadcast: 51084 packets, JumboOctets: 84001642 packets\n" +
                    "Lost: 0 packets, Overflow: 0 packets, Underrun: 0 packets\n" +
                    "System: 0 packets, Overruns: 0 packets\n" +
                    "TxPause: 0 packets\n" +
                    "Unknown Vlan: 0 packets\n";
            returnResults = MyUtils.trimString(returnResults);*/


            if (returnResults == null){
                try {
                    String subversionNumber = switchParameters.getSubversionNumber();
                    if (subversionNumber!=null){
                        subversionNumber = "、"+subversionNumber;
                    }
                    WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"异常:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题为:光衰功能"+port+"端口获取光衰参数命令错误,请重新定义\r\n");
                    PathHelper.writeDataToFileByName(
                            "IP地址为:"+switchParameters.getIp()+","+
                                    "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                    "问题为:光衰功能"+port+"端口获取光衰参数命令错误,请重新定义\r\n"
                            , "问题日志");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }

            /**
             * 提取光衰参数
             */
            HashMap<String, Double> values = getDecayValues(returnResults,switchParameters);

            if (values == null){
                try {
                    String subversionNumber = switchParameters.getSubversionNumber();
                    if (subversionNumber!=null){
                        subversionNumber = "、"+subversionNumber;
                    }
                    WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"异常:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题为:光衰功能"+port+"端口号未获取到光衰参数\r\n");
                    PathHelper.writeDataToFileByName(
                            "IP地址为:"+switchParameters.getIp()+","+
                                    "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                    "问题为:光衰功能"+port+"端口号未获取到光衰参数\r\n"
                            , "问题日志");
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                /*错误信息预定义 用于前端显示*/
                String parameterInformation = "";
                /*如果两个都包含 则可能是在本行，或者是下一行 需要判断:*/
                String nextrow = Line_split[number];
                parameterInformation = nextrow;
                /*两个都包含 则 两个参数值在一行*/
                if (nextrow.indexOf(":") == -1){
                    nextrow = Line_split[number+1];
                    parameterInformation = parameterInformation +"\r\n"+ nextrow+"\r\n";
                }
                /*字符串截取double值*/
                List<Double> values = MyUtils.StringTruncationDoubleValue(nextrow);
                List<Double> valueList = values.stream()
                        .filter(i -> i < 0)
                        .collect(Collectors.toList());

                if (valueList.size()!=2){
                    /*光衰参数行有少于2个数值 无法取出*/
                    if (values.size()<2){
                        try {
                            String subversionNumber = switchParameters.getSubversionNumber();
                            if (subversionNumber!=null){
                                subversionNumber = "、"+subversionNumber;
                            }
                            WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"异常:" +
                                    "IP地址为:"+switchParameters.getIp()+","+
                                    "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                    "问题为:光衰功能光衰参数一行数值个数不为2,无法取出光衰参数," +
                                    "光衰参数行信息:"+parameterInformation+"\r\n");
                            PathHelper.writeDataToFileByName(
                                    "IP地址为:"+switchParameters.getIp()+","+
                                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                            "问题为:光衰功能光衰参数一行数值个数不为2,无法取出光衰参数," +
                                            "光衰参数行信息:"+parameterInformation+"\r\n"
                                    , "问题日志");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                    int nu =0;
                    for (int i = 0;i<values.size();i++){
                        if (values.get(i) < 0 ){
                            nu = i ;
                        }
                    }
                    if (num == 1){
                        /*RX   TX*/
                        rxpower = values.get(nu);
                        txpower = values.get(nu+1);
                    }else if (num == -1){
                        /*TX  RX*/
                        txpower = values.get(nu-1);
                        rxpower = values.get(nu);
                    }
                    break;
                }
                if (num == 1){
                    /*RX   TX*/
                    rxpower = valueList.get(0);
                    txpower = valueList.get(1);
                }else if (num == -1){
                    /*TX  RX*/
                    txpower = valueList.get(0);
                    rxpower = valueList.get(1);
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
                            try {
                                String subversionNumber = switchParameters.getSubversionNumber();
                                if (subversionNumber!=null){
                                    subversionNumber = "、"+subversionNumber;
                                }
                                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"异常:" +
                                        "IP地址为:"+switchParameters.getIp()+","+
                                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                        "问题为:光衰功能光衰参数行数值数量不正确,无法取出光衰参数," +
                                        "光衰参数行信息:"+keyvalue+"\r\n");
                                PathHelper.writeDataToFileByName(
                                        "IP地址为:"+switchParameters.getIp()+","+
                                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                                "问题为:光衰功能光衰参数行数值数量不正确,无法取出光衰参数," +
                                                "光衰参数行信息:"+keyvalue+"\r\n"
                                        , "问题日志");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            return null;
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
                            try {
                                String subversionNumber = switchParameters.getSubversionNumber();
                                if (subversionNumber!=null){
                                    subversionNumber = "、"+subversionNumber;
                                }
                                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"异常:" +
                                        "IP地址为:"+switchParameters.getIp()+","+
                                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                        "问题为:光衰功能光衰参数行负数数量不正确,无法取出光衰参数," +
                                        "光衰参数行信息:"+keyvalue+"\r\n");
                                PathHelper.writeDataToFileByName(
                                        "IP地址为:"+switchParameters.getIp()+","+
                                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                                "问题为:光衰功能光衰参数行负数数量不正确,无法取出光衰参数," +
                                                "光衰参数行信息:"+keyvalue+"\r\n"
                                        , "问题日志");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
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
