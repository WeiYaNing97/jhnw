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
        String returnString = FunctionalMethods.executeScanCommandByCommand(switchParameters, portNumberCommand);

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
        String[] conversionSplit = conversion.split(";");
        for (String convers:conversionSplit){
            String[] conversSplit = convers.split(":");
            for (int num=0;num<portList.size();num++){
                if (MyUtils.getFirstLetters(portList.get(num)).trim().equals(conversSplit[0])){
                    portList.set(num,portList.get(num).replace(conversSplit[0],conversSplit[1]));
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
                    errorRate.setInputErrors(MyUtils.StringTruncationMatcherValue(error));
                }
                if (MyUtils.containIgnoreCase(error,"output") || MyUtils.containIgnoreCase(error,"Tx") ){
                    errorRate.setOutputErrors(MyUtils.StringTruncationMatcherValue(error));
                }
                if (MyUtils.containIgnoreCase(error,"crc")){
                    errorRate.setCrc(MyUtils.StringTruncationMatcherValue(error));
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
                        " 端口号"+port+
                        " input:"+errorRate.getInputErrors()+" "+
                        " output:"+errorRate.getOutputErrors()+" "+
                        " crc:"+errorRate.getCrc()+"\r\n");
                PathHelper.writeDataToFileByName(
                        "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                " 端口号"+port+
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
                continue;
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
                    continue;
                }
            }

            // =:= 是自定义分割符
            String InputErrors = primaryErrorRate.getInputErrors()!=null?"input=:=是=:=input原:"+primaryErrorRate.getInputErrors()+",input现:"+errorRate.getInputErrors()+"=:="
                    :"input=:=是=:="+errorRate.getInputErrors()+"=:=";
            String OutputErrors = primaryErrorRate.getOutputErrors()!=null?"output=:=是=:=output原:"+primaryErrorRate.getOutputErrors()+",output现:"+errorRate.getOutputErrors()+"=:="
                    :"output=:=是=:="+errorRate.getOutputErrors()+"=:=";
            String Crc = primaryErrorRate.getCrc()!=null?"crc=:=是=:=crc原:"+primaryErrorRate.getCrc()+",crc现:"+errorRate.getCrc()
                    :"crc=:=是=:="+errorRate.getCrc();
            String parameterString = InputErrors+" "+OutputErrors+" "+Crc;

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
            String returnResults = FunctionalMethods.executeScanCommandByCommand(switchParameters, FullCommand);

            /*returnResults = "Eth-Trunk1 current state : UP\n" +
                    "Line protocol current state : UP\n" +
                    "Description:To_HX_S7506E\n" +
                    "Switch Port, Link-type : trunk(configured),\n" +
                    "PVID :    1, Hash arithmetic : According to SIP-XOR-DIP,Maximal BW: 2G, Current BW: 2G, The Maximum Frame Length is 9216\n" +
                    "IP Sending Frames' Format is PKTFMT_ETHNT_2, Hardware address is f853-2982-8f10\n" +
                    "Current system time: 2023-06-20 16:43:57+08:00\n" +
                    "Last 300 seconds input rate 3010840 bits/sec, 699 packets/sec\n" +
                    "Last 300 seconds output rate 2708856 bits/sec, 489 packets/sec\n" +
                    "Input:  843042623 packets, 626161438059 bytes\n" +
                    "  Unicast:                  824884018,  Multicast:                     9101170\n" +
                    "  Broadcast:                  9057435,  Jumbo:                       294092915\n" +
                    "  Discard:                          0,  Pause:                               0\n" +
                    "  Frames:                           0\n" +
                    "\n" +
                    "  Total Error:                      0\n" +
                    "  CRC:                              0,  Giants:                              0\n" +
                    "  Jabbers:                          0,  Fragments:                           0\n" +
                    "  Runts:                            0,  DropEvents:                          0\n" +
                    "  Alignments:                       0,  Symbols:                             0\n" +
                    "  Ignoreds:                         0\n" +
                    "\n" +
                    "Output:  2997524508 packets, 539436712102 bytes\n" +
                    "  Unicast:                  528937482,  Multicast:                  2441058483\n" +
                    "  Broadcast:                 27528543,  Jumbo:                       121392927\n" +
                    "  Discard:                          0,  Pause:                               0\n" +
                    "\n" +
                    "  Total Error:                      0\n" +
                    "  Collisions:                       0,  ExcessiveCollisions:                 0\n" +
                    "  Late Collisions:                  0,  Deferreds:                           0\n" +
                    "  Buffers Purged:                   0\n" +
                    "\n" +
                    "    Input bandwidth utilization  : 0.15%\n" +
                    "    Output bandwidth utilization : 0.14%\n" +
                    "-----------------------------------------------------\n" +
                    "PortName                      Status      Weight\n" +
                    "-----------------------------------------------------\n" +
                    "GigabitEthernet2/0/12         UP          1\n" +
                    "GigabitEthernet1/0/30         UP          1\n" +
                    "-----------------------------------------------------\n" +
                    "The Number of Ports in Trunk : 2\n" +
                    "The Number of UP Ports in Trunk : 2";
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

            String[] returnResultssplit = returnResults.split("\r\n");
            for (String information:returnResultssplit){
                /*提取误码率参数*/
                List<String> value = getParameters(information);
                if (MyUtils.isCollectionEmpty(value)){
                    //information无误码率参数
                    continue;
                }else {
                    valueList.addAll(value);
                    continue;
                }
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
     * @param returnString
     * @return
     */
    public static List<String> ObtainUPStatusPortNumber(String returnString) {
        String[] returnStringSplit = returnString.split("\r\n");
        List<String> strings = new ArrayList<>();

        for (String string:returnStringSplit){
            /*包含 交换机返回行信息转化为大写 UP状态  并且该行带有“/”的 存放入端口待取集合*/
            if ((string.toUpperCase().indexOf(" UP ")!=-1) && string.indexOf("/")!=-1){
                strings.add(string.trim());
            }else if ((string.toUpperCase().indexOf(" UP ")!=-1)
                    && ( string.toUpperCase().startsWith("BAGG".toUpperCase()) || string.toUpperCase().startsWith("Eth-Trunk".toUpperCase()) )){
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
            String terminalSlogan = LuminousAttenuation.getTerminalSlogan(information);
            if (terminalSlogan != null){
                port.add(terminalSlogan);
            }
        }
        return port;
    }

    /**
     * 查看交换机误码率数量
     * @param information
     * @return
     */
    public List<String> getParameters(String information) {
        String[] keyword = {"input errors","output errors","CRC,","CRC:","RxErrorPkts","TxErrorPkts"};
        List<String> keyList = new ArrayList<>();
        for (String key:keyword){
            if (information.toUpperCase().indexOf(key.toUpperCase())!=-1){
                keyList.add(key);
            }
        }
        List<String> returnList = new ArrayList<>();
        for (String key:keyList){
            switch (key){
                case "input errors":
                case "output errors":
                case "CRC,":
                    String[] inputoutputCRC = MyUtils.splitIgnoreCase(information, key);
                    String[] inputoutputCRCsplit = inputoutputCRC[0].split(",");
                    returnList.add(inputoutputCRCsplit[inputoutputCRCsplit.length-1]+key);
                    break;
                case "CRC:":
                    String[] CRC = MyUtils.splitIgnoreCase(information, key);
                    String[] CRCplit = CRC[1].split(",");
                    returnList.add(key + CRCplit[0]);
                    break;
                case "RxErrorPkts":
                case "TxErrorPkts":
                    String[] rxtx = information.split(":");
                    returnList.add(rxtx[0] +" : "+  rxtx[1]);
                    break;
            }
        }
        return returnList;
    }

    public static ErrorRateCommand getpojo(List<ErrorRateCommand> pojoList) {
        ErrorRateCommand errorRateCommand = new ErrorRateCommand();
        int sum = 0;
        for (ErrorRateCommand pojo:pojoList){
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
                errorRateCommand = pojo;
            }else if (sum == num && (pojo.getSwitchType().equals("*")) && (pojo.getSubVersion().equals("*"))){
                errorRateCommand = pojo;
            }
        }
        return errorRateCommand;
    }

}
