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
            String portNumber = "端口号=:=是=:="+port+"=:=";

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
                    "  Total Error:                      987\n" +
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
                    "  Total Error:                      789\n" +
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

            valueList = getParameters(switchParameters,returnResults);

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
            if ((string.toUpperCase().indexOf(" UP ")!=-1)){
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
     * @param
     * @return
     */
    public List<String> getParameters(SwitchParameters switchParameters,String returnResults) {

        String[] returnResultssplit = returnResults.split("\r\n");

        Map<String, Object> deviceVersion = (Map<String, Object>) CustomConfigurationUtil.getValue("误码率",Constant.getProfileInformation());
        Set<String> keys = deviceVersion.keySet();

        String brand = null;
        for (String key:keys){
            if (switchParameters.getDeviceBrand().equalsIgnoreCase(key)){
                brand = key;
                break;
            }
        }
        deviceVersion = (Map<String, Object>) CustomConfigurationUtil.getValue("误码率."+brand,Constant.getProfileInformation());
        keys = deviceVersion.keySet();
        String model = null;
        String firmwareVersion = null;
        String subversionNumber = null;
        for (String key:keys){
            if (switchParameters.getDeviceModel().equalsIgnoreCase(key)){
                model = key;
            }
        }
        if (model!=null){
            deviceVersion = (Map<String, Object>) CustomConfigurationUtil.getValue("误码率."+brand+"."+ model,Constant.getProfileInformation());
            keys = deviceVersion.keySet();
            for (String key:keys){
                if (switchParameters.getDeviceModel().equalsIgnoreCase(key)){
                    firmwareVersion = key;
                }
            }
            if (firmwareVersion!=null){
                deviceVersion = (Map<String, Object>) CustomConfigurationUtil.getValue("误码率."+brand+"."+ model+"."+firmwareVersion,Constant.getProfileInformation());
                keys = deviceVersion.keySet();
                for (String key:keys){
                    if (switchParameters.getDeviceModel().equalsIgnoreCase(key)){
                        subversionNumber = key;
                    }
                }
            }
        }

        String condition = (brand==null?"":"."+brand)+(model==null?"":"."+model)+(firmwareVersion==null?"":"."+firmwareVersion)+(subversionNumber==null?"":"."+subversionNumber);
        deviceVersion = (Map<String, Object>) CustomConfigurationUtil.getValue("误码率"+condition,Constant.getProfileInformation());
        String input = null;
        String output = null;
        String crc = null;
        Set<String> strings = deviceVersion.keySet();
        HashMap<String,String> hashMap =new HashMap<>();
        HashMap<String, String> valueTotalError = getValueTotalError(returnResults);
        for (String key:strings){
            switch (key.toLowerCase()){
                case "input":
                    input = (String) deviceVersion.get(key);
                    hashMap.put("Input",input);
                    break;
                case "output":
                    output = (String) deviceVersion.get(key);
                    hashMap.put("Output",output);
                    break;
                case "crc":
                    crc = (String) deviceVersion.get(key);
                    hashMap.put("CRC",crc);
                    break;
            }
        }
        Set<String> keySet = hashMap.keySet();
        for (String key:keySet){
            if (MyUtils.containIgnoreCase(hashMap.get(key),"Total Error")){
                String value = valueTotalError.get(key);
                String placeholdersContaining = getPlaceholdersContaining(value, hashMap.get(key));
                if (placeholdersContaining!=null){
                    hashMap.put(key,placeholdersContaining);
                }
            }else if (!(MyUtils.containIgnoreCase(hashMap.get(key),"Total Error"))){
                for (String str:returnResultssplit){
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

        String[] informationSplit = information.split("\r\n");
        List<String> valueList = new ArrayList<>();
        for (int number = 0;number<informationSplit.length;number++){
            if (MyUtils.containIgnoreCase(informationSplit[number],"Input")
            && !(MyUtils.containIgnoreCase(informationSplit[number],"Total Error"))){

                valueList.add("Input");
            }else if (MyUtils.containIgnoreCase(informationSplit[number],"Output")
            && !(MyUtils.containIgnoreCase(informationSplit[number],"Total Error"))){

                valueList.add("Output");
            }else if (MyUtils.containIgnoreCase(informationSplit[number],"Total Error")){

                valueList.add(informationSplit[number]);
            }
        }

        HashMap<String,String> returnMap = new HashMap<>();

        for (int i =0 ;i<valueList.size();i++){
            if (MyUtils.containIgnoreCase(valueList.get(i),"Total Error")){
                if (MyUtils.containIgnoreCase(valueList.get(i),"Input")){
                    returnMap.put("Input",valueList.get(i));
                }else if (MyUtils.containIgnoreCase(valueList.get(i),"Output")){
                    returnMap.put("Output",valueList.get(i));
                }else {
                    returnMap.put(valueList.get(i-1),valueList.get(i));
                }
            }
        }

        return returnMap;
    }


    /**
     * 根据配置文件的取值信息 取参数值
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
        /*1*/
        String str1Str = str1.replaceAll("\\d", "");
        String str2Str = str2.trim().replace("$", "");
        /*2*/
        if (str1Str.indexOf(str2Str) ==-1){
            return null;
        }
        /*3  占位符位置 */
        Integer num = null;
        String[] $str2 = str2.split(" ");
        for (int number = 0; number<$str2.length; number++){
            if ($str2[number].equalsIgnoreCase("$")){
                num = number;
            }
        }

        /*4*/
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
            /* 5 */
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
}
