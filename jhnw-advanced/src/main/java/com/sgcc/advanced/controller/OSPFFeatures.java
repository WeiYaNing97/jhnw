package com.sgcc.advanced.controller;

import com.sgcc.advanced.domain.OSPFPojo;
import com.sgcc.advanced.domain.Ospf;
import com.sgcc.advanced.domain.OspfCommand;
import com.sgcc.advanced.domain.OspfEnum;
import com.sgcc.advanced.service.IOspfCommandService;
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
@Api("OSPF功能相关")
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
        commandReturn = "OSPF Process 1 with Router ID 11.37.96.2\n" +
                "Peer Statistic Information\n" +
                "----------------------------------------------------------------------------\n" +
                "Area Id Interface Neighbor id State\n" +
                "0.0.0.0 GigabitEthernet1/0/0 11.37.96.1 Full\n" +
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

        List<OSPFPojo> pojoList =  getOSPFPojo(commandReturn);

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
                String customDelimiter = (String) CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());

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
     * 根据交换机返回信息提取OSPF数据
     * @return
     */
    public AjaxResult getOspfListByString(List<String> returnStringSplit) {
        HashMap<Integer,String> titleMap = new HashMap<>();
        for (int number = 0 ;number < returnStringSplit.size(); number++){
            /*判断一个字符串是否包含另一个字符串(忽略大小写)*/
            if (MyUtils.containIgnoreCase(returnStringSplit.get(number),"State")){
                /*标题行信息 赋值*/
                titleMap.put(number,returnStringSplit.get(number));
            }
        }
        List<Ospf> returnList = new ArrayList<>();
        Set<Integer> integers = titleMap.keySet();
        for (Integer integer:integers){

            /*OSPF数组*/
            List<String> valueList = new ArrayList<>();
            String NameLine = titleMap.get(integer);
            Ospf propertyValueSubscripts = null;

            /*标题的 标题名数*/
            int number = 0 ;
            if (NameLine == null){
                return AjaxResult.error("获取OSPF参数失败");
            }else {
                /*获取属性值下标*/
                List<Object> property = getPropertyValueSubscripts(NameLine);
                if (property == null){
                    return AjaxResult.error("获取OSPF参数失败");
                }
                propertyValueSubscripts = (Ospf) property.get(0);
                number = (Integer) property.get(1);
            }

            for (int num = integer+1 ;num < returnStringSplit.size();num++){
                valueList.add(returnStringSplit.get(num));
            }
            if(valueList.size() ==0){
                break;
            }

            /*标题下第一行 为参数行*/
            String value = valueList.get(0);
            /*获取参数行的列*/
            String[] value_split = value.split(" ");
            /*如果参数行的列数量 与 标题行的标题名数一致则 可以直接根据 下标取值*/
            int len = value_split.length;
            if (len != number){
                /*如果不一致 则需要去去除空格*/
                valueList = removOspfSpaceCharacter(number,valueList);
            }

            if (MyUtils.isCollectionEmpty(valueList)){
                continue;
            }else {
                /*根据参数下表提取对应参数*/
                List<Ospf> ospfList= getPojoList(propertyValueSubscripts,valueList,number);
                if (MyUtils.isCollectionEmpty(ospfList)){
                    continue;
                }else {
                    returnList.addAll(ospfList);
                }
            }

        }
        return AjaxResult.success(returnList);
    }

    /**
     * 根据参数下表提取对应参数
     * @param pojo
     * @param stringList
     * @param number
     * @return
     */
    public List<Ospf> getPojoList(Ospf pojo,List<String> stringList,int number) {
        List<Ospf> ospfList = new ArrayList<>();
        for (String pojoString:stringList){
            Ospf ospf = new Ospf();
            String[] pojoStringSplit = pojoString.split(" ");
            /*元素数不相等 或者 数据不包含IP 则结束遍历*/
            if (number != pojoStringSplit.length || !(MyUtils.containsIPAddress(pojoString))){/*添加了对数据的 IP 特征检测*/
                break;
            }
            if(pojo.getNeighborID() != null){
                ospf.setNeighborID(pojoStringSplit[Integer.valueOf(pojo.getNeighborID()).intValue()]);
            }
            if(pojo.getPri() != null){
                ospf.setPri(pojoStringSplit[Integer.valueOf(pojo.getPri()).intValue()]);
            }
            if(pojo.getState() != null){
                ospf.setState(pojoStringSplit[Integer.valueOf(pojo.getState()).intValue()]);
            }
            if(pojo.getDeadTime() != null){
                ospf.setDeadTime(pojoStringSplit[Integer.valueOf(pojo.getDeadTime()).intValue()]);
            }
            if(pojo.getAddress() != null){
                ospf.setAddress(pojoStringSplit[Integer.valueOf(pojo.getAddress()).intValue()]);
            }
            if(pojo.getPortNumber() != null){
                ospf.setPortNumber(pojoStringSplit[Integer.valueOf(pojo.getPortNumber()).intValue()]);
            }
            if(pojo.getBFDState() != null){
                ospf.setBFDState(pojoStringSplit[Integer.valueOf(pojo.getBFDState()).intValue()]);
            }
            ospfList.add(ospf);
        }
        return ospfList;
    }

    /**
     * 去除属性值中间空格  通过代码逻辑
     * @param strings
     * @return
     */
    public List<String> removOspfSpaceCharacter(int number,List<String> strings) {
        /*创建返回对象*/
        List<String> returnStringList = new ArrayList<>();
        /*遍历行信息*/
        for (String lineInformation:strings){
            /*针对以太网的处理*/
            String[] line_split = lineInformation.split(" ");
            for (int num = line_split.length-1 ;num >= 0; num--){
                /* 查看是否存在包含/的且只有数字的项
                * 有则比较是否和标题行数量一致
                * 一致则放入返回对象
                * 否则跳出循环 返回结果*/
                if (line_split[num].indexOf("/")!=-1 && !(MyUtils.judgeContainsStr(line_split[num].replaceAll("/","")))){
                    lineInformation = lineInformation.replaceAll(" "+line_split[num],line_split[num]);
                    /*一致则放入返回对象*/
                    if (lineInformation.split(" ").length == number){
                        returnStringList.add(lineInformation);
                        break;
                    }else {
                        /*否则跳出循环 返回结果*/
                        return returnStringList;
                    }
                }
            }
        }
        return returnStringList;
    }

    /**
     * 获取属性值下标
     * @param information
     * @return
     */
    public List<Object> getPropertyValueSubscripts(String information) {
        /*单词数组*/
        String[] string_split = information.trim().split(" ");
        String word = "";
        Ospf ospf = new Ospf();
        int number = 0;
        /*遍历单词数组 从前往后拼接匹配配置文件中配置的 标题名称*/
        for (int num = 0 ; num < string_split.length ; num++ ){
            word = word + " "+string_split[num];
            /*拼接匹配配置文件中配置的 标题名称
            * 返回配置文件中的key
            * 得到 标题名称 在数组中的下标*/
            OspfEnum ospfEnum = Utils.assignment();
            String enumeratorValues = Utils.enumeratorValues(word.trim(),ospfEnum);
            if (enumeratorValues != null){
                switch (enumeratorValues){
                    case "neighborID":
                        ospf.setNeighborID(number+"");
                        break;
                    case "pri":
                        ospf.setPri(number+"");
                        break;
                    case "state":
                        ospf.setState(number+"");
                        break;
                    case "deadTime":
                        ospf.setDeadTime(number+"");
                        break;
                    case "address":
                        ospf.setAddress(number+"");
                        break;
                    case "portNumber":
                        ospf.setPortNumber(number+"");
                        break;
                    case "BFDState":
                        ospf.setBFDState(number+"");
                        break;
                }
                number++;
                word = "";
            }
        }
        if (word.equals("")){
            List<Object> objects = new ArrayList<>();
            /* ospf 中存储的不是属性值 是下标值*/
            objects.add(ospf);
            objects.add(number);
            return objects;
        }
        return null;
    }

    /*================================第二种实现方法============================================*/

    /* 获取 OSPF实体类集合
    是整体类属性只有 IP 端口号 状态*/
    public static List<OSPFPojo> getOSPFPojo(String information) {

        /*
         *根据 "obtainPortNumber.keyword" 在配置文件中 获取端口号关键词
         * Eth-Trunk Ethernet GigabitEthernet GE BAGG Eth
         * 根据空格分割为 关键词数组*/
        String deviceVersion = (String) CustomConfigurationUtil.getValue("obtainPortNumber.keyword", Constant.getProfileInformation());
        String[] keywords = deviceVersion.trim().split(" ");

        List<String> keys = new ArrayList<>();

        for (String key:keywords){
            if (MyUtils.containIgnoreCase(information,key)){
                keys.add(key);
            }
        }

        List<String> string_split = Arrays.asList(information.split("\r\n"));

        for (String key:keys){
            for (int i = 0 ; i < string_split.size() ; i++){
                if (MyUtils.containIgnoreCase(string_split.get(i),key+" ")){
                    string_split.set(i,string_split.get(i).replace(key+" ",key));
                }
            }
        }

        String input = null;
        for (String str:string_split){
            List<String> stringList = extractIPAddresses(str);
            if (stringList.size() == 2 && str.toLowerCase().indexOf("full")!=-1){
                input = str.trim();
            }
        }

        if (input == null){
            return new ArrayList<>();
        }

        String[] split = input.split("\\s+");

        /*获取数组各元素的意义*/
        List<String> stringList = obtainParameterMeanings(split);

        /*获取 IP、端口号、状态  数组下标*/
        int ip = stringList.indexOf("IP");
        int port = stringList.indexOf("端口");
        int state = stringList.indexOf("状态");

        /*获取 全文 与 含有full数据 按空格分割后 数组长度相等的行*/
        List<String[]> arrayList = new ArrayList<>();
        for (String str:string_split){
            String[] rowsplit = str.trim().split("\\s+");
            if (rowsplit.length == split.length){
                arrayList.add(rowsplit);
            }
        }

        /*筛选与含有full数据长度相等的行集合
         * 条件数 full数据 IP、端口号、未知（非IP、端口、状态）对应的列 数据特征要一样
         * 如果一样 则是OSPF数据
         * 如果不一样 则过滤掉*/
        List<String[]> stateList = new ArrayList<>();
        for (String[] array:arrayList){
            boolean isInput = true;
            for (int i = 0 ; i < array.length ; i++){
                String string = stringList.get(i);

                if (string.equals("IP")){
                    if (isIP(array[i])){
                        continue;
                    }else {
                        isInput = false;
                        break;
                    }
                }

                else if (string.equals("端口")){
                    if (isAlphanumeric(array[i])){
                        continue;
                    }else {
                        isInput = false;
                        break;
                    }
                }

                else if (string.equals("未知")){
                    // 未知的列 可能是IP、端口、状态
                    boolean isIp = isIP(array[i]);
                    boolean isPort = isAlphanumeric(array[i]);
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

        /*根据下标 到筛选后的集合中提取数据 赋值给OSPFPojo */
        List<OSPFPojo> ospfPojos = new ArrayList<>();
        for (String[] array:stateList){
            OSPFPojo ospfPojo = new OSPFPojo();
            ospfPojo.setIp(array[ip]);
            ospfPojo.setPort(array[port]);
            ospfPojo.setState(array[state]);
            ospfPojos.add(ospfPojo);
        }

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
            } else if (isAlphanumeric(value)) { // 判断元素是否 同时包含 字母和数字
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

    /*判断字符串是否同时包含字母与数字*/
    public static boolean isAlphanumeric(String str) {

        return str.matches(".*[a-zA-Z].*") && str.matches(".*\\d.*");

    }

}
