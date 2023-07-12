package com.sgcc.advanced.controller;
import com.sgcc.advanced.domain.ErrorRate;
import com.sgcc.advanced.domain.ErrorRateCommand;
import com.sgcc.advanced.service.IErrorRateCommandService;
import com.sgcc.advanced.service.IErrorRateService;
import com.sgcc.advanced.utils.ScreeningMethod;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.controller.SwitchScanResultController;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.switchboard.SwitchIssueEcho;
import com.sgcc.share.util.*;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.webSocket.WebSocketService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Api("误码率功能")
@RestController
@RequestMapping("/advanced/ErrorPackage")
@Transactional(rollbackFor = Exception.class)
public class ErrorPackage {
    @Autowired
    private IErrorRateService errorRateService;
    @Autowired
    private IErrorRateCommandService errorRateCommandService;

    public AjaxResult getErrorPackage(SwitchParameters switchParameters) {

        /*1：获取配置文件关于 误码率问题的 符合交换机品牌的命令的 配置信息*/
        ErrorRateCommand errorRateCommand = new ErrorRateCommand();
        errorRateCommand.setBrand(switchParameters.getDeviceBrand());
        errorRateCommand.setSwitchType(switchParameters.getDeviceModel());
        errorRateCommand.setFirewareVersion(switchParameters.getFirmwareVersion());
        errorRateCommand.setSubVersion(switchParameters.getSubversionNumber());
        errorRateCommandService = SpringBeanUtil.getBean(IErrorRateCommandService.class);
        List<ErrorRateCommand> errorRateCommandList = errorRateCommandService.selectErrorRateCommandListBySQL(errorRateCommand);

        /*2：当 配置文件误码率问题的命令 为空时 进行 日志写入*/
        if (MyUtils.isCollectionEmpty(errorRateCommandList)){
            try {
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"异常:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        "问题为:误码率功能未定义获取端口号的命令\r\n");
                PathHelper.writeDataToFileByName(
                        "IP地址为:"+switchParameters.getIp()+","+
                         "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                         "问题为:误码率功能未定义获取端口号的命令\r\n"
                        , "问题日志");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("IP地址为:"+switchParameters.getIp()+","+"问题为:误码率功能未定义获取端口号的命令\r\n");
        }
        /**
         * 从errorRateCommandList中 获取四项基本最详细的数据
         */
        errorRateCommand = ScreeningMethod.ObtainPreciseEntityClassesErrorRateCommand(errorRateCommandList);
        String portNumberCommand = errorRateCommand.getGetPortCommand();

        /**
         * 3：配置文件误码率问题的命令 不为空时，执行交换机命令，返回交换机返回信息
         */
        ExecuteCommand executeCommand = new ExecuteCommand();
        String returnString = executeCommand.executeScanCommandByCommand(switchParameters, portNumberCommand);

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
                "Ethernet0/0/0        UP  auto    A      T    1\n" +
                "Eth-Trunk20.2000     UP   1G(a)   F(a)   T    1\n" +
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

            try {
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"异常:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        "问题为:误码率功能获取端口号命令错误,需要重新定义\r\n");
                PathHelper.writeDataToFileByName(
                        "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:误码率功能获取端口号命令错误,需要重新定义\r\n"
                        , "问题日志");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("IP地址为:"+switchParameters.getIp()+","+"问题为:误码率功能获取端口号命令错误,需要重新定义\r\n");

        }

        /*5：如果交换机返回信息不为 null说明命令执行正常, 则继续 根据交换机返回信息获取误码率端口号*/
        List<String> portList = ObtainUPStatusPortNumber(returnString);

        /*6：获取光衰端口号方法返回集合判断是否为空，说明没有端口号为开启状态 UP，是则进行*/
        if (MyUtils.isCollectionEmpty(portList)){
            // todo 关于没有端口号为UP状态 的错误代码库

            try {
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"异常:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        "问题为:误码率功能无UP状态端口号,是否需要CRT检查异常\r\n");
                PathHelper.writeDataToFileByName(
                        "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:误码率功能无UP状态端口号,是否需要CRT检查异常\r\n"
                        , "问题日志");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("IP地址为:"+switchParameters.getIp()+","+"问题为:误码率功能无UP状态端口号,是否需要CRT检查异常\r\n");

        }

        /*7：如果交换机端口号为开启状态 UP 不为空 则需要查看是否需要转义：
        GE转译为GigabitEthernet  才能执行获取交换机端口号光衰参数命令*/
        String conversion = errorRateCommand.getConversion();
        if (conversion != null){
            String[] conversionSplit = conversion.split(";");
            for (String convers:conversionSplit){
                /* 转译 分割为 字符串数组*/
                String[] conversSplit = convers.split(":");
                for (int num=0;num<portList.size();num++){
                    /* getFirstLetters 获取字符串开头字母部分
                    * 判断 是否与转译相同
                    * 如果相同 则 进行转译  */
                    if (MyUtils.getFirstLetters(portList.get(num)).trim().equals(conversSplit[0])){
                        portList.set(num,portList.get(num).replace(conversSplit[0],conversSplit[1]));
                    }
                }
            }
        }


        /*获取误码率参数命令*/
        String errorPackageCommand = errorRateCommand.getGetParameterCommand();
        /*获取到误码率参数 map集合*/
        HashMap<String, Object> errorPackageParameters = getErrorPackageParameters(switchParameters, portList, errorPackageCommand);


        if (errorPackageParameters == null){

            try {
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"异常:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        "问题为:误码率功能所有UP状态端口皆未获取到误码率参数\r\n");
                PathHelper.writeDataToFileByName(
                        "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:误码率功能所有UP状态端口皆未获取到误码率参数\r\n"
                        , "问题日志");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return AjaxResult.error("所有端口都没有获取到误码率参数");
        }


        /*获取交换机四项基本信息ID*/
        Long switchID = FunctionalMethods.getSwitchParametersId(switchParameters);
        errorRateService = SpringBeanUtil.getBean(IErrorRateService.class);//解决 多线程 service 为null问题
        /*获取误码率参数集合中的Key
        * key值为端口号*/
        Set<String> strings = errorPackageParameters.keySet();


        for (String port:strings){

            ErrorRate errorRate = new ErrorRate();
            errorRate.setSwitchIp(switchParameters.getIp());
            errorRate.setSwitchId(switchID);
            errorRate.setPort(port);
            /**
             * 查询误码率列表
             */
            List<ErrorRate> list = errorRateService.selectErrorRateList(errorRate);
            ErrorRate primaryErrorRate = new ErrorRate();

            if (!(MyUtils.isCollectionEmpty(list))){
                primaryErrorRate = list.get(0);
            }

            /* 获取该端口号的 错误包参数 */
            List<String> errorPackageValue = (List<String>) errorPackageParameters.get(port);
            for (String error:errorPackageValue){
                if (MyUtils.containIgnoreCase(error,"input") || MyUtils.containIgnoreCase(error,"Rx") ){
                    errorRate.setInputErrors(MyUtils.StringTruncationMatcherValue(error).equals("")?"0":MyUtils.StringTruncationMatcherValue(error));
                }
                if (MyUtils.containIgnoreCase(error,"output") || MyUtils.containIgnoreCase(error,"Tx") ){
                    errorRate.setOutputErrors(MyUtils.StringTruncationMatcherValue(error).equals("")?"0":MyUtils.StringTruncationMatcherValue(error));
                }
                if (MyUtils.containIgnoreCase(error,"crc")){
                    errorRate.setCrc(MyUtils.StringTruncationMatcherValue(error).equals("")?"0":MyUtils.StringTruncationMatcherValue(error));
                }
                if (MyUtils.containIgnoreCase(error,"Description")){
                    errorRate.setDescription(error);
                }
            }

            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("ProblemName","误码率");

            try {
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"系统信息:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        " 端口号"+port+ errorRate.getDescription() +
                        " input:"+errorRate.getInputErrors()+" "+
                        " output:"+errorRate.getOutputErrors()+" "+
                        " crc:"+errorRate.getCrc()+"\r\n");
                PathHelper.writeDataToFileByName(
                        "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                " 端口号"+port+ errorRate.getDescription() +
                                " input:"+errorRate.getInputErrors()+" "+
                                " output:"+errorRate.getOutputErrors()+" "+
                                " crc:"+errorRate.getCrc()+"\r\n"
                        , "错误包");
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (primaryErrorRate.getId() == null){
                /* 数据库中没有历史数据 可以直接插入 */
                int i = errorRateService.insertErrorRate(errorRate);
                hashMap.put("IfQuestion","无问题");
                //continue;
            }else {
                int num = 0;
                if ((primaryErrorRate.getInputErrors() !=null && errorRate.getInputErrors() !=null) &&
                        (!primaryErrorRate.getInputErrors().equals(errorRate.getInputErrors()))){
                    num++;
                }
                if ((primaryErrorRate.getOutputErrors() !=null && errorRate.getOutputErrors() !=null) &&
                        (!primaryErrorRate.getOutputErrors().equals(errorRate.getOutputErrors()))){
                    num++;
                }
                if ((primaryErrorRate.getCrc() !=null && errorRate.getCrc() !=null) &&
                        (!primaryErrorRate.getCrc().equals(errorRate.getCrc()))){
                    num++;
                }
                if (num>0){
                    errorRate.setId(primaryErrorRate.getId());
                    int i = errorRateService.updateErrorRate(errorRate);
                    hashMap.put("IfQuestion","有问题");
                }else if (num == 0){
                    errorRate.setId(primaryErrorRate.getId());
                    int i = errorRateService.updateErrorRate(errorRate);
                    hashMap.put("IfQuestion","无问题");
                    //continue;
                }
            }

            // =:= 是自定义分割符
            String portNumber = "端口号=:=是=:="+port+" "+ errorRate.getDescription() +"=:=";

            String InputErrors = primaryErrorRate.getInputErrors()!=null?"input=:=是=:=input原:"+primaryErrorRate.getInputErrors()+",input现:"+errorRate.getInputErrors()+"=:="
                    :"input=:=是=:="+errorRate.getInputErrors()+"=:=";
            String OutputErrors = primaryErrorRate.getOutputErrors()!=null?"output=:=是=:=output原:"+primaryErrorRate.getOutputErrors()+",output现:"+errorRate.getOutputErrors()+"=:="
                    :"output=:=是=:="+errorRate.getOutputErrors()+"=:=";
            String Crc = primaryErrorRate.getCrc()!=null?"crc=:=是=:=crc原:"+primaryErrorRate.getCrc()+",crc现:"+errorRate.getCrc()
                    :"crc=:=是=:="+errorRate.getCrc();
            String parameterString = portNumber +" "+ InputErrors+" "+OutputErrors+" "+Crc;

            if (parameterString.endsWith("=:=")){
                parameterString = parameterString.substring(0,parameterString.length()-3);
            }

            hashMap.put("parameterString",parameterString);

            SwitchScanResultController switchScanResultController = new SwitchScanResultController();
            Long insertId = switchScanResultController.insertSwitchScanResult(switchParameters, hashMap);
            SwitchIssueEcho switchIssueEcho = new SwitchIssueEcho();
            switchIssueEcho.getSwitchScanResultListByData(switchParameters.getLoginUser().getUsername(),insertId);
        }
        return null;
    }

    /**
     * 获取到误码率参数
     * @param switchParameters
     * @param portNumber
     * @param errorPackageCommand
     * @return
     */
    public HashMap<String,Object> getErrorPackageParameters(SwitchParameters switchParameters,List<String> portNumber,String errorPackageCommand) {
        /*创建 返回对象 List<String>*/
        HashMap<String,Object> hashMap = new HashMap<>();
        /*端口号集合 需要检测各端口号的误码率参数*/
        for (String port:portNumber){
            List<String> valueList = new ArrayList<>();
            /*替换端口号 得到完整的 获取端口号误码率参数命令 */
            String FullCommand = errorPackageCommand.replaceAll("端口号",port);
            /**
             * 交换机执行命令 并返回结果
             */
            ExecuteCommand executeCommand = new ExecuteCommand();
            String returnResults = executeCommand.executeScanCommandByCommand(switchParameters, FullCommand);

            /*returnResults = "GigabitEthernet1/0/1 current state : UP\n" +
                    "Line protocol current state : UP\n" +
                    "Last line protocol up time : 2022-10-26 17:38:19\n" +
                    "Description:TO_AnPingZhan_NE40EX8_G1/1/2\n" +
                    "Route Port,The Maximum Transmit Unit is 1500\n" +
                    "Internet Address is 11.36.97.9/30\n" +
                    "IP Sending Frames' Format is PKTFMT_ETHNT_2, Hardware address is 0819-a6f2-be6c\n" +
                    "The Vendor PN is LTD1314-BC+-H3C\n" +
                    "The Vendor Name is Hisense\n" +
                    "Port BW: 1G, Transceiver max BW: 1G, Transceiver Mode: SingleMode\n" +
                    "WaveLength: 1310nm, Transmission Distance: 40km\n" +
                    "Rx Power: -15.90dBm, Tx Power: 0.38dBm\n" +
                    "Loopback:none, full-duplex mode, negotiation: disable, Pause Flowcontrol:Receive Enable and Send Enable\n" +
                    "Last physical up time : 2022-10-26 17:38:19\n" +
                    "Last physical down time : 2022-10-26 17:38:06\n" +
                    "Statistics last cleared:never\n" +
                    "Last 300 seconds input rate: 2728 bits/sec, 1 packets/sec\n" +
                    "Last 300 seconds output rate: 1284152 bits/sec, 891 packets/sec\n" +
                    "Input: 14898796154 bytes, 46662204 packets\n" +
                    "Output: 2812600098295 bytes, 4616344671 packets\n" +
                    "Input:\n" +
                    "Unicast: 35727172 packets, Multicast: 10927967 packets\n" +
                    "Broadcast: 7065 packets, JumboOctets: 41876 packets\n" +
                    "CRC: 0 packets, Symbol: 0 packets\n" +
                    "Overrun: 0 packets, InRangeLength: 0 packets\n" +
                    "LongPacket: 0 packets, Jabber: 0 packets, Alignment: 0 packets\n" +
                    "Fragment: 0 packets, Undersized Frame: 0 packets\n" +
                    "RxPause: 0 packets\n" +
                    "Output:\n" +
                    "Unicast: 4606248991 packets, Multicast: 10085097 packets\n" +
                    "Broadcast: 10583 packets, JumboOctets: 374322636 packets\n" +
                    "Lost: 0 packets, Overflow: 0 packets, Underrun: 0 packets\n" +
                    "System: 0 packets, Overruns: 0 packets\n" +
                    "TxPause: 0 packets\n" +
                    "Unknown Vlan: 0 packets";
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
                            "问题为:误码率功能"+port+"端口获取误码率参数命令错误,请重新定义\r\n");
                    PathHelper.writeDataToFileByName(
                            "IP地址为:"+switchParameters.getIp()+","+
                                    "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                    "问题为:误码率功能"+port+"端口获取误码率参数命令错误,请重新定义\r\n"
                            , "问题日志");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }

            /*查看交换机误码率数量*/
            valueList = getParameters(switchParameters,returnResults);
            /*获取 描述：Description:*/
            String description = getDescription(returnResults);

            if (description!=null){
                valueList.add("Description:"+description);
            }

            if (MyUtils.isCollectionEmpty(valueList)){
                /*  端口未获取到误码率 */
                try {
                    String subversionNumber = switchParameters.getSubversionNumber();
                    if (subversionNumber!=null){
                        subversionNumber = "、"+subversionNumber;
                    }
                    WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"异常:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题为:误码率功能"+port+"端口未获取到误码率\r\n");
                    PathHelper.writeDataToFileByName(
                            "IP地址为:"+switchParameters.getIp()+","+
                                    "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                    "问题为:误码率功能"+port+"端口未获取到误码率\r\n"
                            , "问题日志");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                continue;
            }else {
                hashMap.put(port,valueList);
            }
        }
        return hashMap;
    }


    /**
     * 根据交换机返回信息获取获取UP状态端口号
     *
     * @param returnString
     * @return
     */
    public static List<String> ObtainUPStatusPortNumber(String returnString) {
        /* 按行分割 交换机返回信息行信息 字符串数组*/
        String[] returnStringSplit = returnString.split("\r\n");

        /*遍历 交换机行信息字符串数组
        *  判断 交换机返回行信息是否包含 UP（状态）
        *  是 则存放入端口待取集合*/
        List<String> strings = new ArrayList<>();
        for (String string:returnStringSplit){
            if ((string.toUpperCase().indexOf(" UP ")!=-1)){
                strings.add(string.trim());
            }
        }
        /*判断端口待取集合是否为空*/
        if (MyUtils.isCollectionEmpty(strings)){
            return null;
        }

        /*遍历端口待取集合 执行取值方法 获取端口号*/
        List<String> port = new ArrayList<>();
        for (String information:strings){
            /*根据 UP 截取端口号 */
            String terminalSlogan = FunctionalMethods.getTerminalSlogan(information);
            if (terminalSlogan != null){
                port.add(terminalSlogan);
            }
        }
        return port;
    }

    /**
     * 查看交换机误码率数量
     * @param
     * @return
     */
    public List<String> getParameters(SwitchParameters switchParameters,String returnResults) {
        /* 获取配置文件 关于 误码率 的配置信息*/
        Map<String, Object> deviceVersion = (Map<String, Object>) CustomConfigurationUtil.getValue("误码率",Constant.getProfileInformation());
        /*获取 key值
        * key值为 ： 描述 或者 品牌名*/
        Set<String> keys = deviceVersion.keySet();

        /* 交换机品牌 默认为 null*/
        String brand = null;

        /* 遍历交换机 品牌名 */
        for (String key:keys){
            /* 获取交换机信息实体类中的 设备品牌*/
            String deviceBrand = switchParameters.getDeviceBrand();

            /* 判断交换机基本信息品牌名 是否与 配置文件key值(交换机品牌名) 相等 忽略大小写
            * 如果相等则 将配置文件中的 key(交换机品牌名) 赋值给 交换机品牌brand*/
            if (deviceBrand.equalsIgnoreCase(key)){
                brand = key;
                break;

                /* 如果不相等 则 判断是否为  Huawei  或者 Quidway
                 * 如果 为 "Huawei或Quidway"  则判断 key交换机品牌 是否 等于 "Quidway或Huawei"
                 * 如果相等则 将配置文件中的 key(交换机品牌名) 赋值给 交换机品牌brand*/

            }else if (deviceBrand.equalsIgnoreCase("Huawei") || deviceBrand.equalsIgnoreCase("Quidway")){
                if (deviceBrand.equalsIgnoreCase("Huawei") && "Quidway".equalsIgnoreCase(key)){
                    brand = key;
                    break;
                }else if (deviceBrand.equalsIgnoreCase("Quidway") && "Huawei".equalsIgnoreCase(key)){
                    brand = key;
                    break;
                }
            }
        }
        /*根据 交换机品牌名 获取交换机误码率信息 */
        deviceVersion = (Map<String, Object>) CustomConfigurationUtil.getValue("误码率."+brand,Constant.getProfileInformation());
        keys = deviceVersion.keySet();

        /*型号*/
        String model = null;
        /*版本*/
        String firmwareVersion = null;
        /*子版本*/
        String subversionNumber = null;
        /*遍历 误码率 品牌下的 key*/
        for (String key:keys){
            /*如果 交换机型号switchParameters.getDeviceModel() 与 配置文件中 key匹配
            * 则 配置文件key 赋值 型号model */
            if (switchParameters.getDeviceModel().equalsIgnoreCase(key)){
                model = key;
                break;
            }
        }

        /*如果 型号 model 不为 null
        * 则可以根据 品牌和型号 获取 误码率信息*/
        if ( model!=null ){

            deviceVersion = (Map<String, Object>) CustomConfigurationUtil.getValue("误码率."+brand+"."+ model,Constant.getProfileInformation());
            keys = deviceVersion.keySet();

            /*遍历 误码率 品牌、型号 下的 key*/
            for (String key:keys){
                /*如果 交换机型号switchParameters.getFirmwareVersion() 与 配置文件中 key匹配
                 * 则 配置文件key 赋值 型号 firmwareVersion */
                if (switchParameters.getFirmwareVersion().equalsIgnoreCase(key)){
                    firmwareVersion = key;
                    break;
                }
            }

            /*如果 型号 firmwareVersion 不为 null
             * 则可以根据 品牌和型号 获取 误码率信息*/
            if (firmwareVersion!=null){

                deviceVersion = (Map<String, Object>) CustomConfigurationUtil.getValue("误码率."+brand+"."+ model+"."+firmwareVersion,Constant.getProfileInformation());
                keys = deviceVersion.keySet();

                /*遍历 误码率 品牌、型号、版本 下的 key*/
                for (String key:keys){
                    /*如果 交换机型号switchParameters.getSubversionNumber() 与 配置文件中 key匹配
                     * 则 配置文件key 赋值 型号 subversionNumber */
                    if (switchParameters.getSubversionNumber().equalsIgnoreCase(key)){
                        subversionNumber = key;
                        break;
                    }
                }

            }

        }


        /* 动态查询条件
        *  一定有 品牌 brand
        * 型号、版本、子版本 如果为 null的 则为""空字符， 如果不为 null 则为  "."+属性值 */
        String condition = brand +(model==null?"":"."+model)+(firmwareVersion==null?"":"."+firmwareVersion)+(subversionNumber==null?"":"."+subversionNumber);
        deviceVersion = (Map<String, Object>) CustomConfigurationUtil.getValue("误码率"+condition,Constant.getProfileInformation());
        Set<String> strings = deviceVersion.keySet();

        /*key值 ：
        *   Input: $ input errors
            Output: $ output errors
            CRC: $ CRC,
        */

        /* 获取三个参数的关键词 并存储再 hashMap 集合中*/
        HashMap<String,String> hashMap =new HashMap<>();
        for (String key:strings){
            switch (key.toLowerCase()){
                case "input":
                    String input = (String) deviceVersion.get(key);
                    hashMap.put("Input",input);
                    break;
                case "output":
                    String output = (String) deviceVersion.get(key);
                    hashMap.put("Output",output);
                    break;
                case "crc":
                    String crc = (String) deviceVersion.get(key);
                    hashMap.put("CRC",crc);
                    break;
            }
        }


        /* 获取 hashMap 的 key值 */
        Set<String> keySet = hashMap.keySet();


        /* 遍历 hashMap 的 key值
        * 获取关键词是否包含 Total Error
        * 如果存在则通过 Total Error 先获取到 ： Input 和 Output*/
        HashMap<String, String> valueTotalError = new HashMap<>();
        for (String key:keySet){
            if (MyUtils.containIgnoreCase(hashMap.get(key),"Total Error")){
                /* 通过 Total Error 先获取到 ： Input 和 Output */
                valueTotalError = getValueTotalError(returnResults);
            }
        }

        /*遍历 hashMap 的 key值
        * 获取对应的参数值*/
        for (String key:keySet){
            /*key值的关键词 包含 "Total Error" */
            if (MyUtils.containIgnoreCase(hashMap.get(key),"Total Error")){

                /*如果根据 "Total Error" 获取行信息为 空 则取下一参数*/
                if (valueTotalError.size()==0){
                    continue;
                }

                /*根据 key 获取关键词 */
                String value = valueTotalError.get(key);
                if (value == null){
                    continue;
                }

                /*根据配置文件的取值信息 取参数值*/
                String placeholdersContaining = getPlaceholdersContaining(value, hashMap.get(key));
                if (placeholdersContaining!=null){
                    hashMap.put(key,placeholdersContaining);
                }

                /*如果关键词不包含 "Total Error"
                * */
            }else if (!(MyUtils.containIgnoreCase(hashMap.get(key),"Total Error"))){
                /* 按行分割 交换机返回信息每行信息 为字符串数组*/
                String[] returnResultssplit = returnResults.split("\r\n");

                /*遍历 交换机返回信息行信息 字符串数组*/
                for (String str:returnResultssplit){
                    /*根据配置文件的取值信息 取参数值*/
                    String placeholdersContaining = getPlaceholdersContaining(str, hashMap.get(key));
                    if (placeholdersContaining!=null){
                        hashMap.put(key,placeholdersContaining);
                        break;
                    }

                }
            }
        }

        List<String> stringList = new ArrayList<>();
        for (String key:keySet){
            stringList.add(key + ":" +hashMap.get(key));
        }

        return stringList;

    }


    /**
     * 当取词关键词有 Total Error 时的取词逻辑 需要区分时Input的Total Error，还是Output的Total Error
     *
     * 实现逻辑：按行分割成字符串 包含 Input、Output、Total Error的都放入集合
     * 然后遍历集合，将Total Error元素取出来，如果Total Error元素行包含Input 或 Output则放入HashMap<String,String>
     *     如果包含Input 或 Output 则取前一个元素，看是Input 还是 Output
     * @param information
     * @return
     */
    public static HashMap<String,String> getValueTotalError(String information) {
        /* 按行  分割  交换机返回信息字符串数组 */
        String[] informationSplit = information.split("\r\n");
        /* 遍历交换机返回信息 行信息 */
        List<String> valueList = new ArrayList<>();
        for (int number = 0; number<informationSplit.length; number++){

            /*判断 行信息 包含 "Input" 则 在list集合中 存储 Input*/
            if (MyUtils.containIgnoreCase(informationSplit[number],"Input")){ //&& !(MyUtils.containIgnoreCase(informationSplit[number],"Total Error"))
                valueList.add("Input");
            }

            /*判断 行信息 包含 "Output" 则 在list集合中 存储 Output*/
            if (MyUtils.containIgnoreCase(informationSplit[number],"Output")){//&& !(MyUtils.containIgnoreCase(informationSplit[number],"Total Error"))
                valueList.add("Output");
            }

            /*判断 行信息 包含 "Total Error" 则 在list集合中 存储 行信息*/
            if (MyUtils.containIgnoreCase(informationSplit[number],"Total Error")){
                valueList.add(informationSplit[number]);
            }

        }

        /* "Total Error" 与  "Input"、"Output" 的map对应关系 */
        HashMap<String,String> returnMap = new HashMap<>();
        /*循环遍历 存储包含"Total Error"、"Input"、"Output" 信息的集合 */
        for (int i =0 ;i<valueList.size();i++){
            /*判断 集合元素是否包含 "Total Error"

            * 如果包含 则 判断是否包含 "Input" 或者 "Output"
            * 存在着则 存入map集合 key值为 "Input" 或者 "Output"
            *
            * 如果 不包含 "Input" 及 "Output"  且 "Total Error"不为 0 元素
             则取出 前一元素 作为 key值*/
            if (MyUtils.containIgnoreCase(valueList.get(i),"Total Error")){
                if (MyUtils.containIgnoreCase(valueList.get(i),"Input")){
                    returnMap.put("Input",valueList.get(i));
                }else if (MyUtils.containIgnoreCase(valueList.get(i),"Output")){
                    returnMap.put("Output",valueList.get(i));
                }else if (i != 0){
                    returnMap.put(valueList.get(i-1),valueList.get(i));
                }
            }

        }

        return returnMap;
    }


    /**
     * 根据配置文件的取值信息 取参数值
     *
     * 逻辑：因为取词为数字 占位符为：$ 。
     * 1：首先将交换机返回信息数字替换为"",将配置文件中的占位符$替换为""
     * 2：如果str1Str 不包含 str2Str 则返回null
     * 3：将配置文件的关键词按空格转换为字符串数组，获取$ 的下标、
     * 4：如果关键词的$位置在开头和结尾，则可以根据去掉$的关键词分割交换机返回信息。然后（$开头：第一元素的最后一位。$结尾：第二元素的第一位。）获取参数
     * 5：关键词根据 $ 分割为数组，按照元素分割交换机返回信息。
     * @param str1
     * @param str2
     * @return
     */
    public static String getPlaceholdersContaining(String str1 , String str2) {
        /*1 首先将交换机返回信息数字替换为"",将配置文件中的占位符$替换为""*/
        String str1Str = str1.replaceAll("\\d", "");
        String str2Str = str2.trim().replace("$", "");
        /*2 如果str1Str 不包含 str2Str 则返回null*/
        if (str1Str.indexOf(str2Str) ==-1){
            return null;
        }
        /*3  占位符位置
        * 将配置文件的关键词按空格转换为字符串数组，获取$ 的下标、*/
        Integer num = null;
        String[] $str2 = str2.split(" ");
        for (int number = 0; number<$str2.length; number++){
            if ($str2[number].equalsIgnoreCase("$")){
                num = number;
            }
        }

        /*4 如果关键词的$位置在开头和结尾，则可以根据去掉$的关键词分割交换机返回信息。
        然后（$开头：第一元素的最后一位。$结尾：第二元素的第一位。）获取参数
         */
        if (num == 0){
            // $在关键词的开头
            String[] str1Split = str1.split(str2Str.trim());
            // 开头等于二，关键词在中间 $代表的参数会在第一个数组元素中
            // 开头等于 一，关键词在结尾 $代表的参数在唯一一个数组元素中
            /*关键词在中间 或者在结尾*/
            if (str1Split.length==2 || str1Split.length==1){
                String[] parameterArray = str1Split[0].trim().split(" ");
                /*获取数组的最后一个元素*/
                String value = parameterArray[parameterArray.length-1].trim();
                /*去掉结尾的 . 或 ，*/
                value = FunctionalMethods.judgeResultWordSelection(value);
                /*检查元素是否为纯数字*/
                if (MyUtils.determineWhetherAStringIsAPureNumber(value)){
                    return value;
                }
            }else {
                /*如果有多个 关键词，则需要遍历获取，直到获取到数字为止*/
                for (String returnstr1:str1Split){
                    String[] parameterArray = returnstr1.trim().split(" ");
                    /*获取数组的最后一个元素*/
                    String value = parameterArray[parameterArray.length-1].trim();
                    /*去掉结尾的 . 或 ，*/
                    value = FunctionalMethods.judgeResultWordSelection(value);
                    /*检查元素是否为纯数字*/
                    if (MyUtils.determineWhetherAStringIsAPureNumber(value)){
                        return value;
                    }
                }
                /*如果未取出，则返回null*/
                return null;
            }

        }else if (num == $str2.length-1){
            // $在关键词的结尾
            String[] str1Split = str1.split(str2Str.trim());
            //结尾等于二，关键词在中间 $代表的参数会在第二个数组元素中
            // 结尾等于 一，关键词在开头 $代表的参数在唯一一个数组元素中
            if (str1Split.length==2){
                String[] parameterArray = str1Split[1].trim().split(" ");
                /*获取数组的最后一个元素*/
                String value = parameterArray[0].trim();
                value = FunctionalMethods.judgeResultWordSelection(value);
                if (MyUtils.determineWhetherAStringIsAPureNumber(value)){
                    return value;
                }
            }else if (str1Split.length==1){
                String[] parameterArray = str1Split[0].trim().split(" ");
                /*获取数组的最后一个元素*/
                String value = parameterArray[0].trim();
                value = FunctionalMethods.judgeResultWordSelection(value);
                if (MyUtils.determineWhetherAStringIsAPureNumber(value)){
                    return value;
                }
            }else {
                /*如果有多个 关键词，则需要遍历获取，直到获取到数字为止*/
                for (String returnstr1:str1Split){
                    String[] parameterArray = returnstr1.trim().split(" ");
                    /*获取数组的第一个元素*/
                    String value = parameterArray[0].trim();
                    /*去掉结尾的 . 或 ，*/
                    value = FunctionalMethods.judgeResultWordSelection(value);
                    /*检查元素是否为纯数字*/
                    if (MyUtils.determineWhetherAStringIsAPureNumber(value)){
                        return value;
                    }
                }
                /*如果未取出，则返回null*/
                return null;
            }
        }else {
            /* 5  关键词根据 $ 分割为数组，按照元素分割交换机返回信息。*/
            // 中间
            String[] str2Split = str2.trim().split(" \\$ ");
            if (str2Split.length==2){

                String[] split = str1.split(str2Split[0]);
                /*第一关键词在中间位置 长度为2*/
                /*第一关键词在开头位置 长度为1*/
                /*此时只有 一个 匹配到第一关键词的可以直接截取第二关键词*/
                if (split.length == 2 || split.length == 1){
                    /*根据第二关键词分割数组*/
                    /*因为要获取的是数字，则此时第一个元素为要获取的参数*/
                    String[] split2 = split[split.length - 1].split(str2Split[1]);
                    String value = split2[0].trim();
                    value = FunctionalMethods.judgeResultWordSelection(value);
                    if (MyUtils.determineWhetherAStringIsAPureNumber(value)){
                        return value;
                    }
                }else {
                    /*有多个第一关键词*/
                    /*遍历所有第一关键词 数组元素*/
                    for (String string:str2Split){
                        String[] split2 = string.split(str2Split[1]);
                        String value = split2[0].trim();
                        value = FunctionalMethods.judgeResultWordSelection(value);
                        if (MyUtils.determineWhetherAStringIsAPureNumber(value)){
                            return value;
                        }
                    }
                }
            }
        }
        return null;
    }



    /**
     * 获取 描述：Description:
     *
     * 忽略大小写判断返回信息是否包含 Description:  如果不包含返回 null
     * 如果包含，则按行分割，然后逐行判断是否包含 Description:
     * 如果包含 则返回 Description 属性值
     * @param information
     * @return
     */
    public static String getDescription(String information) {
        /* 配置文件中 获取 Description 关键词
        * 关键词 根据 ； 转化为 关键词 字符串 数组*/
        String descriptionValue = (String) CustomConfigurationUtil.getValue("误码率.描述", Constant.getProfileInformation());
        String[] descriptionSplit = descriptionValue.split(";");
        for (String description:descriptionSplit){

            /* 判断交换机返回信息 是否包含 Description关键词
            * 如果不包含 则 跳出当前循环 进行下一个 循环*/
            if (information.trim().toLowerCase().indexOf(description.toLowerCase()) == -1){
                continue;
            }

            /*按行分割 获得交换机返回信息 数组*/
            String[] informationSplit = information.split("\r\n");

            /*遍历 交换机返回信息数组
            * 判断 Description关键词 在那一行 */
            for (String string:informationSplit){
                /*交换机返回信息行信息中 判断是否包含 Description关键词
                * 如果包含 则 != -1
                * 如果不包含 则 = -1*/
                /* 如果行信息中 以"Description :" 开头 则 i=0 */
                int i = string.trim().toLowerCase().indexOf(description.toLowerCase());
                if (i!=-1){
                    /*将交换机行信息中 “:” 修改为 “ ： ”   例如："Description: ZhiNengFuZhu" 修改为："Description : ZhiNengFuZhu"
                    * repaceWhiteSapce  方法为： 多个连续空格 改为 多个单空格
                    */
                    string = MyUtils.repaceWhiteSapce(string.replace(":"," : "));
                    /* 根据 "Description :" 分割数组*/
                    String[] strings = MyUtils.splitIgnoreCase(string, description+" :");
                    if (strings.length == 0){
                        return null;
                    }
                    /* 分割数组 要小于3个元素*/
                    if (strings.length < 3){
                        /*当交换机返回信息以"Description :"开头， 即 i==0  且  第一个元素是 ""
                        * 数组应为 ["","ZhiNengFuZhu"]
                        * 返回第二的元素*/
                        if (strings[0].equalsIgnoreCase("") && (i == 0)){
                            return strings[1].trim();
                        }else {
                            return strings[0].trim();
                        }
                    }
                }
            }

        }
        return null;
    }



}
