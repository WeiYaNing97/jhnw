package com.sgcc.advanced.controller;

import com.sgcc.advanced.domain.OSPFPojo;
import com.sgcc.advanced.domain.Ospf;
import com.sgcc.advanced.domain.OspfCommand;
import com.sgcc.advanced.domain.OspfEnum;
import com.sgcc.advanced.service.IOspfCommandService;
import com.sgcc.advanced.utils.DataExtraction;
import com.sgcc.advanced.utils.ScreeningMethod;
import com.sgcc.advanced.utils.Utils;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.controller.SwitchScanResultController;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.switchboard.SwitchIssueEcho;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.ExecuteCommand;
import com.sgcc.share.util.MyUtils;
import com.sgcc.share.util.PathHelper;
import com.sgcc.share.webSocket.WebSocketService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * OSPF 功能
 */
@Api(tags = "OSPF功能管理")
@RestController
@RequestMapping("/advanced/OSPFFeatures")
@Transactional(rollbackFor = Exception.class)
public class OSPFFeatures {

    @Autowired
    private IOspfCommandService ospfCommandService;

    /**
     * ospf 功能接口
     * @param switchParameters
     */
    public void getOSPFValues(SwitchParameters switchParameters) {
        /*查询OSPF 命令集合*/
        OspfCommand ospfCommand = new OspfCommand();
        ospfCommand.setBrand(switchParameters.getDeviceBrand());
        ospfCommand.setSwitchType(switchParameters.getDeviceModel());
        ospfCommand.setFirewareVersion(switchParameters.getFirmwareVersion());
        ospfCommand.setSubVersion(switchParameters.getSubversionNumber());

        /*查询 符合交换机基本信息的 OSPF命令集合*/
        ospfCommandService = SpringBeanUtil.getBean(IOspfCommandService.class);
        List<OspfCommand> ospfCommandList = ospfCommandService.selectOspfCommandListBySQL(ospfCommand);

        /*OSPF命令集合为空  则中止OSPF高级共功能*/
        if (MyUtils.isCollectionEmpty(ospfCommandList)){
            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }

            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题为:未定义该交换机OSPF命令\r\n");

            return;
        }

        /*通过四项基本欸的精确度 筛选最精确的OSPF命令*/
        ospfCommand = ScreeningMethod.ObtainPreciseEntityClassesOspfCommand(ospfCommandList);

        /**
         * 根据交换机信息类  执行交换命令
         */
        ExecuteCommand executeCommand = new ExecuteCommand();
        String command = ospfCommand.getGetParameterCommand();
        String commandReturn = executeCommand.executeScanCommandByCommand(switchParameters,command);

        commandReturn = this.commandPortReturn;
        commandReturn = MyUtils.trimString(commandReturn);


        /*执行命令返回结果为null 则是命令执行错误*/
        if (commandReturn == null){
            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }

            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题为:ospf功能命令错误,请重新定义\r\n");

            return;
        }

        /*原方法：根据交换机返回信息提取OSPF数据*/
        /*行数据*/
        //String[] returnStringSplit = commandReturn.split("\r\n");
        /*List<String> collect = Arrays.stream(commandReturn.split("\r\n")).collect(Collectors.toList());
        AjaxResult ospfListByString = getOspfListByString(collect);*/

        List<OSPFPojo> pojoList =  getOSPFPojo(commandReturn,switchParameters);

        if (pojoList.size() == 0){

            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }

            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题为:ospf功能信息提取失败\r\n");

            return;
        }


        for (OSPFPojo ospf:pojoList){

            try {
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }

                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "ospf",
                        "系统信息:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        "问题为:ospf功能IP:"+ospf.getIp()+"端口号:"+ospf.getPort()+"状态:"+ospf.getState()+"\r\n");


                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("ProblemName","OSPF");
                if (ospf.toString().toUpperCase().indexOf("FULL")!=-1){
                    hashMap.put("IfQuestion","无问题");
                    /*continue;*/
                }else {
                    hashMap.put("IfQuestion","有问题");
                }

                /*自定义分隔符*/
                String customDelimiter = null;
                Object customDelimiterObject =  CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());
                if (customDelimiterObject instanceof String){
                    customDelimiter = (String) customDelimiterObject;
                }

                // =:= 是自定义分割符
                hashMap.put("parameterString","功能"+customDelimiter+"是"+customDelimiter+"OSPF"+customDelimiter+"参数"+customDelimiter+"是"+customDelimiter+"地址:"+ospf.getIp()+"状态:"+ospf.getState()+"端口号:"+ospf.getPort());

                SwitchScanResultController switchScanResultController = new SwitchScanResultController();

                Long insertId = switchScanResultController.insertSwitchScanResult(switchParameters, hashMap);
                SwitchIssueEcho switchIssueEcho = new SwitchIssueEcho();
                switchIssueEcho.getSwitchScanResultListByData(switchParameters.getLoginUser().getUsername(),insertId);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    /**
     * 根据输入信息和交换机参数获取OSPFPojo对象列表
     *
     * @param information 输入信息
     * @param switchParameters 交换机参数
     * @return OSPFPojo对象列表
     */
    public static List<OSPFPojo> getOSPFPojo(String information,SwitchParameters switchParameters) {

        /**
         *根据 "obtainPortNumber.keyword" 在配置文件中 获取端口号关键词
         * Eth-Trunk Ethernet GigabitEthernet GE BAGG Eth
         * 根据空格分割为 关键词数组*/
        String deviceVersion = null;
        Object deviceVersionObject =  CustomConfigurationUtil.getValue("obtainPortNumber.keyword", Constant.getProfileInformation());
        if (deviceVersionObject instanceof String){
            deviceVersion = (String) deviceVersionObject;
        }
        String[] keywords = deviceVersion.trim().split(" ");

        /** 遍历配置文件定义的关于交换机端口号的特征 关键词
         * 如果 交换机返回结果包含 特征关键词 则 保存特征关键词 */
        List<String> keys = new ArrayList<>();
        for (String key:keywords){
            if (MyUtils.containIgnoreCase(information,key)){
                keys.add(key);
            }
        }

        /** 如果交换机返回信息中 不包含配置文件定义的 关于交换机端口号的特征 关键词
         * 则 返回空集合*/
        if (keys.size() == 0){
            return new ArrayList<>();
        }

        /**
         * 交换机返回信息 按行分割
         * 遍历交换机返回的信息行信息数组
         * 遍历特征关键词
         *  将端口号中有空格的情况 去除空格
         *  例如： GigabitEthernet 1/0/0  替换成  GigabitEthernet1/0/0*/
        List<String> string_split = Arrays.asList(information.split("\r\n"));
        for (String key:keys){
            for (int i = 0 ; i < string_split.size() ; i++){
                /* 判断一个字符串是否包含另一个字符串(忽略大小写) */
                if (MyUtils.containIgnoreCase(string_split.get(i),key+" ")){
                    string_split.set(i,string_split.get(i).replace(key+" ",key));
                }
            }
        }

        /*String input = null;
        for (String str:string_split){
            *//*获取字符串中的IP集合*//*
            List<String> stringList = extractIPAddresses(str);
            if (stringList.size() == 2 && str.toLowerCase().indexOf("full")!=-1){
                input = str.trim();
            }
        }

        *//* todo 如果未获取到连接正常OSPF数据，则返回空集合并告警。*//*
        if (input == null){
            return new ArrayList<>();
        }

        String[] split = input.split("\\s+");

        *//*获取数组各元素的意义*//*
        List<String> stringList = obtainParameterMeanings(split);

        *//*获取 IP、端口号、状态  数组下标*//*
        int ip = stringList.indexOf("IP");
        int port = stringList.indexOf("端口");
        int state = stringList.indexOf("状态");

        *//*获取 全文 与 含有full数据 按空格分割后 数组长度相等的行*//*
        List<String[]> arrayList = new ArrayList<>();
        for (String str:string_split){
            String[] rowsplit = str.trim().split("\\s+");
            if (rowsplit.length == split.length){
                arrayList.add(rowsplit);
            }
        }

        *//*筛选与含有full数据长度相等的行集合
         * 条件数 full数据 IP、端口号、未知（非IP、端口、状态）对应的列 数据特征要一样
         * 如果一样 则是OSPF数据
         * 如果不一样 则过滤掉*//*
        List<String[]> stateList = new ArrayList<>();
        for (String[] array:arrayList){
            boolean isInput = true;
            for (int i = 0 ; i < array.length ; i++){
                String string = stringList.get(i);

                *//** 有两个IP 无效IP、IP*//*
                if (string.indexOf("IP") != -1){
                    if (isIP(array[i])){
                        continue;
                    }else {
                        isInput = false;
                        break;
                    }
                }

                else if (string.equals("端口")){
                    if (isAlphanumeric(array[i],keys)){
                        continue;
                    }else {
                        isInput = false;
                        break;
                    }
                }

                else if (string.equals("未知")){
                    // 未知的列 可能是IP、端口、状态
                    boolean isIp = isIP(array[i]);
                    boolean isPort = isAlphanumeric(array[i],keys);
                    boolean isState = array[i].toLowerCase().indexOf("full")!=-1;
                    // 判断是否为IP、端口、状态
                    if ( isIp || isPort || isState ){
                        isInput = false;
                        break;
                    }else {
                        continue;
                    }

                }
            }
            if (isInput){
                stateList.add(array);
            }
        }

        *//*根据下标 到筛选后的集合中提取数据 赋值给OSPFPojo *//*
        List<OSPFPojo> ospfPojos = new ArrayList<>();
        for (String[] array:stateList){
            OSPFPojo ospfPojo = new OSPFPojo();
            ospfPojo.setIp(array[ip]);
            ospfPojo.setPort(array[port]);
            ospfPojo.setState(array[state]);
            ospfPojos.add(ospfPojo);
        }*/

        List<OSPFPojo> ospfPojos = getOSPFParameters(string_split,switchParameters.getDeviceBrand());
        return ospfPojos;
    }

    /**
     * 根据给定的字符串列表和设备品牌获取OSPF配置参数，并返回OSPFPojo列表
     *
     * @param stringSplit 包含OSPF配置参数的字符串列表
     * @param deviceBrand 设备品牌
     * @return 包含OSPF配置参数的OSPFPojo列表
     */
    private static List<OSPFPojo> getOSPFParameters(List<String> stringSplit, String deviceBrand) {
        // 获取OSPF配置参数键值对
        Map<String,String> key_value = (Map<String,String>)CustomConfigurationUtil.getValue("OSPF." + deviceBrand + ".R_table", Constant.getProfileInformation());
        // 创建OSPFPojo列表
        List<OSPFPojo> ospfPojos = new ArrayList<>();
        // 如果键值对为空，则返回空列表
        if (key_value == null){
            return ospfPojos;
        }
        // 根据键值对提取表格数据
        List<HashMap<String, Object>> hashMapList = DataExtraction.tableDataExtraction(stringSplit, key_value);
        // 遍历表格数据
        for (HashMap<String, Object> hashMap:hashMapList){
            // 创建OSPFPojo对象
            OSPFPojo ospfPojo = new OSPFPojo();
            // 设置IP地址
            ospfPojo.setIp(hashMap.get("neighborID").toString());
            // 设置端口号
            ospfPojo.setPort(hashMap.get("portNumber").toString());
            // 设置状态
            ospfPojo.setState(hashMap.get("state").toString());
            // 将OSPFPojo对象添加到列表中
            ospfPojos.add(ospfPojo);
        }

        // 返回OSPFPojo列表
        return ospfPojos;
    }


    /**
     * @Description 获取字符串中的IP集合
     * @author charles
     * @createTime 2023/12/22 16:41
     * @desc
     * @param input
     * @return
     */
    public static List<String> extractIPAddresses(String input) {
        List<String> ipAddresses = new ArrayList<>();
        Pattern pattern = Pattern.compile(
                "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            ipAddresses.add(matcher.group());
        }
        return ipAddresses;
    }

    /**
     * 根据Full状态的信息 获取数组中各元素的意义。
     *
     * <p>该方法遍历给定的字符串数组，对每个元素进行一系列检查，以确定其意义，并将结果存储在一个列表中返回。</p>
     *
     * <p>检查逻辑如下：</p>
     * <ul>
     *     <li>如果元素是IP地址（通过调用isIP方法判断），则进一步判断是否为无效IP（即包含"0.0.0."）。如果是无效IP，则将该元素的意义标记为"无效IP"；否则，标记为"IP"。</li>
     *     <li>如果元素只包含字母和数字（通过调用isAlphanumeric方法判断，此处假设该方法能识别端口格式），则将该元素的意义标记为"端口"。</li>
     *     <li>如果元素（不区分大小写）包含"full"，则将该元素的意义标记为"状态"。</li>
     *     <li>如果上述条件都不满足，则将该元素的意义标记为"未知"。</li>
     * </ul>
     *
     * @param values 待分析的字符串数组。
     * @return 包含数组中所有元素意义的列表。列表中的每个元素都是对应原数组元素的意义描述（"IP"、"无效IP"、"端口"、"状态"或"未知"）。
     */
    public static List<String> obtainParameterMeanings(String[] values) {
        List<String> meanings = new ArrayList<>(); // 创建一个列表用于存储各元素的意义
        for (String value : values) { // 遍历数组中的每个元素
            if (isIP(value)) { // 判断元素是否为IP地址
                if (value.indexOf("0.0.0.") != -1) { // 如果IP地址包含"0.0.0."，则视为无效IP
                    meanings.add("无效IP"); // 将该元素的意义标记为"无效IP"
                } else {
                    meanings.add("IP"); // 否则，将该元素的意义标记为"IP"
                }
            } else if (isAlphanumeric(value,null)) { // 判断元素是否 同时包含 字母和数字
                meanings.add("端口"); // 将该元素的意义标记为"端口"（假设这里的isAlphanumeric方法能识别端口格式）
            } else if (value.toLowerCase().indexOf("full") != -1) { // 判断元素（不区分大小写）是否包含"full"
                meanings.add("状态"); // 将该元素的意义标记为"状态"
            } else {
                meanings.add("未知"); // 如果上述条件都不满足，则将该元素的意义标记为"未知"
            }
        }
        return meanings; // 返回包含所有元素意义的列表
    }


    /*判断字符串是否是IP*/
    public static boolean isIP(String ip) {
        String regex = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
        return Pattern.matches(regex, ip);
    }

    /*判断字符串是否同时包含 端口号特征关键词 与 数字
    * 或者 判断字符串是否同时包含 字母 与 数字*/
    public static boolean isAlphanumeric(String str ,List<String> keys) {
        // 如果关键词列表不为空
        if (keys!=null){
            // 遍历关键词列表
            for (String key:keys){
                // 如果字符串中包含关键词（不区分大小写）且字符串中包含数字
                if (str.toLowerCase().indexOf(key.toLowerCase())!=-1 && str.matches(".*\\d.*")){
                    // 返回true
                    return true;
                }else {
                    // 继续下一个循环
                    continue;
                }
            }
        }else {
            // 如果关键词列表为空，则判断字符串是否同时包含字母和数字
            return str.matches(".*[a-zA-Z].*") && str.matches(".*\\d.*");
        }

        // 默认返回false
        return false;
    }


    public static String commandPortReturn = "OSPF Process 1 with Router ID 11.37.96.2\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "192.168.1.100 GigabitEthernet1/0/0 11.37.96.1 Full\n" +
            "0.0.0.0 GigabitEthernet1/0/1 11.37.96.5 Full\n" +
            "0.0.0.0 GigabitEthernet1/0/6 11.37.96.54 Full\n" +
            "0.0.0.0 GigabitEthernet1/0/8 11.37.96.73 Full\n" +
            "0.0.0.0 GigabitEthernet8/0/17 11.37.96.55 Full\n" +
            "0.0.0.0 Eth-Trunk20.2000 11.37.96.159 Full\n" +
            "0.0.0.0 GigabitEthernet1/0/17 11.37.96.41 Full\n" +
            "0.0.0.0 GigabitEthernet8/0/14 11.37.96.8 Full\n" +
            "0.0.0.2 GigabitEthernet8/0/2 11.37.96.134 Full\n" +
            "0.0.0.2 GigabitEthernet1/0/3 11.37.96.59 Full\n" +
            "0.0.0.2 GigabitEthernet1/0/4 11.37.96.60 Full\n" +
            "0.0.0.2 GigabitEthernet1/0/5 11.37.96.57 Full\n" +
            "0.0.0.2 GigabitEthernet8/0/22 11.37.96.61 Full\n" +
            "0.0.0.2 GigabitEthernet1/0/10 11.37.96.40 Full\n" +
            "0.0.0.2 GigabitEthernet1/0/15 11.37.96.12 not\n" +
            "----------- --------------- ----------------------- ---------------------------\n" +
            "Total Peer(s): 15\n" +
            "\n" +
            "OSPF Process 11 with Router ID 30.9.98.241\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 GigabitEthernet1/0/13 30.8.1.3 Full\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1\n" +
            "\n" +
            "OSPF Process 25 with Router ID 10.122.119.49\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 GigabitEthernet8/0/19 10.122.119.18 Full\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1\n" +
            "\n" +
            "OSPF Process 310 with Router ID 28.36.127.5\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 Eth-Trunk20.2011 28.36.127.6 Init\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1\n" +
            "\n" +
            "OSPF Process 311 with Router ID 29.36.191.5\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 Eth-Trunk20.2012 29.36.191.6 Init\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1\n" +
            "\n" +
            "OSPF Process 312 with Router ID 30.9.127.5\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 0\n" +
            "\n" +
            "OSPF Process 313 with Router ID 27.36.127.5\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 0\n" +
            "\n" +
            "OSPF Process 314 with Router ID 6.40.0.45\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 Eth-Trunk20.2015 6.40.0.46 Full\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1\n" +
            "\n" +
            "OSPF Process 315 with Router ID 13.40.0.45\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 Eth-Trunk20.2016 13.40.0.46 Init\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1\n" +
            "\n" +
            "OSPF Process 316 with Router ID 172.16.193.45\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 Eth-Trunk20.2017 172.16.193.46 Full\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1\n" +
            "\n" +
            "OSPF Process 317 with Router ID 172.16.0.45\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 Eth-Trunk20.2018 172.16.0.46 Full\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1\n" +
            "\n" +
            "OSPF Process 318 with Router ID 7.36.1.45\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 Eth-Trunk20.2019 7.36.1.46 Full\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1\n" +
            "\n" +
            "OSPF Process 65534 with Router ID 128.75.212.73\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 DCN-Serial1/0/0:0 128.79.235.137 Full\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1";
}
