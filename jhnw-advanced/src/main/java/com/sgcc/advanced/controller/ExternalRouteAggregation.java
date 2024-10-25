package com.sgcc.advanced.controller;

import com.sgcc.advanced.aggregation.IPAddressCalculator;
import com.sgcc.advanced.aggregation.IPAddressUtils;
import com.sgcc.advanced.domain.ExternalIPAddresses;
import com.sgcc.advanced.domain.ExternalIPCalculator;
import com.sgcc.advanced.domain.IPCalculator;
import com.sgcc.advanced.utils.DataExtraction;
import com.sgcc.share.controller.SwitchScanResultController;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.switchboard.SwitchIssueEcho;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.MyUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ExternalRouteAggregation {

    /**
     * 获取外部IP计算器列表
     *
     * @param switchParameters 交换机参数对象
     * @param switchReturnsexternalInformation 交换机返回的外部信息
     * @param externalKeywords 外部关键词
     * @return 返回外部IP计算器列表
     */
    public static List<ExternalIPCalculator> getExternalIPCalculatorList(SwitchParameters switchParameters,
                                                                         List<String> switchReturnsexternalInformation_List,
                                                                         String externalKeywords) {
        // 获取路由 OSPF、直连、静态 的关键词
        List<String> protos = new ArrayList<>();
        protos.addAll(Arrays.stream(externalKeywords.split("/")).collect(Collectors.toList()));
        // 使用Lambda表达式和Comparator对字符串按长度从长到短排序
        // 排序protos列表，根据字符串的长度从长到短进行排序
        Collections.sort(protos, Comparator.comparingInt(String::length).reversed());

        // 将交换机返回的外部信息按换行符分割成字符串列表
        // 从配置中获取路由聚合配置信息
        HashMap<String,Object> keyMap = (HashMap<String,Object>) CustomConfigurationUtil.getValue("路由聚合."+switchParameters.getDeviceBrand(), Constant.getProfileInformation());
        // 获取keyMap的所有键，存储到keyList列表中
        List<String> keyList = keyMap.keySet().stream().collect(Collectors.toList());
        // 初始化外部IP计算器列表
        List<ExternalIPCalculator> externalIPList = new ArrayList<>();

        /* 符合表格格式 */
        // 如果keyList中存在"R_table"，则使用getTableExternalIPList方法获取外部IP计算器列表
        if (keyList.indexOf("R_table")!=-1){
            externalIPList = getTableExternalIPList(switchReturnsexternalInformation_List,switchParameters);
        }else {
            /* 不符合表格格式 */
            // 如果不符合表格格式，则使用getExternalIPList方法获取外部IP计算器列表
            externalIPList = getExternalIPList(switchReturnsexternalInformation_List, protos,switchParameters);
        }
        // 返回外部IP计算器列表
        return externalIPList;
    }


    /**
     * 外部路由聚合方法
     *
     * @param switchParameters 交换机参数对象
     * @param switchReturnsexternalInformation 交换机返回的外部信息
     * @param externalKeywords 外部关键词
     * @return 无返回值
     */
    public static void externalRouteAggregation(SwitchParameters switchParameters,
                                                List<String> external_List,
                                                String externalKeywords){

        List<ExternalIPCalculator> externalIPList = getExternalIPCalculatorList(switchParameters, external_List, externalKeywords);

        Map<String, List<ExternalIPCalculator>> proto_collect = externalIPList.stream().collect(Collectors.groupingBy(ExternalIPCalculator::getProto));

        Set<String> protoSet = proto_collect.keySet();
        for (String proto : protoSet) {
            System.err.println(proto+"==========================================");
            List<ExternalIPCalculator> proto_externalIPS = proto_collect.get(proto);
            Map<String, List<ExternalIPCalculator>> NextHop_collect = proto_externalIPS.stream().collect(Collectors.groupingBy(ExternalIPCalculator::getNextHop));

            Set<String> NextHopSet = NextHop_collect.keySet();
            for (String NextHop : NextHopSet) {
                System.err.println("=========================================="+NextHop);
                List<ExternalIPCalculator> NextHop_externalIPS = NextHop_collect.get(NextHop);

                for (ExternalIPCalculator NextHop_externalIP : NextHop_externalIPS) {
                    // 根据目标掩码计算第一个可用IP和最后一个可用IP
                    IPCalculator calculator = IPAddressCalculator.Calculator(NextHop_externalIP.getDestinationMask());
                    NextHop_externalIP.setFirstAvailable(calculator.getFirstAvailable());
                    NextHop_externalIP.setFinallyAvailable(calculator.getFinallyAvailable());
                }

                // 将NextHop_externalIPS进行地址范围拼接
                List<ExternalIPAddresses> externalIPAddressesList = IPAddressUtils.ExternalSplicingAddressRange(NextHop_externalIPS);


                List<List<String>> returnList = new ArrayList<>();
                for (ExternalIPAddresses externalIPAddresses : externalIPAddressesList) {
                    System.err.println(externalIPAddresses.getIpStart()+" "+externalIPAddresses.getIpEnd());
                    List<ExternalIPCalculator> externalIPCalculatorList = externalIPAddresses.getExternalIPCalculatorList();
                    externalIPCalculatorList.forEach(System.err::println);
                    System.err.println("===================聚合 ：=======================");
                    // 获取网络号
                    List<String> aggregatedRouteList = IPAddressUtils.getNetworkNumber(externalIPAddresses.getIpStart(), externalIPAddresses.getIpEnd());
                    aggregatedRouteList.forEach(System.err::println);
                    System.err.println("================================================\r\n");



                    List<String> preAggregationRouteList = externalIPCalculatorList.stream()
                            .map(x -> x.getDestinationMask() + "[" + x.getFirstAvailable() + " - " + x.getFinallyAvailable() + "]")
                            .collect(Collectors.toList());

                    List<String> returnString = new ArrayList<>();
                    returnString.add("原始网络号:");
                    returnString.addAll(preAggregationRouteList);
                    returnString.add("聚合网络号:");
                    returnString.addAll(aggregatedRouteList);
                    returnString.stream().forEach(System.err::println);
                    System.err.println("==============================================================");
                    returnList.add(returnString);


                    HashMap<String,String> hashMap = new HashMap<>();
                    if (preAggregationRouteList.size() == aggregatedRouteList.size()) {
                        hashMap.put("IfQuestion","无问题");
                    }else {
                        hashMap.put("IfQuestion","有问题");
                    }

                    SwitchScanResultController switchScanResultController = new SwitchScanResultController();
                    hashMap.put("ProblemName","外部路由聚合");
                    hashMap.put("parameterString",String.join("\r\n" , returnString));
                    Long insertId = switchScanResultController.insertSwitchScanResult(switchParameters, hashMap);
                    SwitchIssueEcho switchIssueEcho = new SwitchIssueEcho();
                    switchIssueEcho.getSwitchScanResultListByData(switchParameters.getLoginUser().getUsername(),insertId);
                }
            }

            System.err.println("\r\n\r\n");
        }
    }

    private static List<ExternalIPCalculator> getTableExternalIPList(List<String> returnInformationList,SwitchParameters switchParameters) {
        HashMap<String,String> keyMap = (HashMap<String,String>) CustomConfigurationUtil.getValue("路由聚合."+"H3C.R_table", Constant.getProfileInformation());
        if (keyMap == null){
            return new ArrayList<>();
        }
        List<HashMap<String, Object>> stringObjectHashMapList = DataExtraction.tableDataExtraction(returnInformationList, keyMap, switchParameters);
        if (stringObjectHashMapList.size() == 0){
            return new ArrayList<>();
        }

        List<String> keyList = keyMap.keySet().stream().collect(Collectors.toList());

        String ipCIDR ="";
        String proto = "";
        String Pre = "";
        String Cost = "";
        String NextHop = "";
        String Interface = "";
        List<ExternalIPCalculator> externalIPList = new ArrayList<>();

        for (HashMap<String, Object> stringObjectHashMap : stringObjectHashMapList){
            ExternalIPCalculator externalIP = new ExternalIPCalculator();
            for (String key:keyList){
                String value = (String) stringObjectHashMap.get(key);
                if (value != null){
                    switch (key){
                        case "Destination/Mask":
                            if (!value.equals("&")){
                                ipCIDR = value;
                            }
                            break;
                        case "protocol":
                            if (!value.equals("&")){
                                proto = value;
                            }
                            break;
                        case "Pre_Def_priority":
                            if (!value.equals("&")){
                                Pre = value;
                            }
                            break;
                        case "Cost":
                            if (!value.equals("&")){
                                Cost = value;
                            }
                            break;
                        case "NextHop":
                            if (!value.equals("&")){
                                NextHop = value;
                            }
                            break;
                        case "Interface":
                            if (!value.equals("&")){
                                Interface = value;
                            }
                            break;
                    }
                }
            }
            externalIP.setDestinationMask(ipCIDR);
            externalIP.setProto(proto);
            externalIP.setPreCost("["+Pre+"/"+Cost+"]");
            externalIP.setNextHop(NextHop);
            externalIP.setInterface(Interface);
            externalIPList.add(externalIP);
        }
        return externalIPList;
    }


    /**
     * 根据交换机返回的信息列表和协议列表获取外部IP列表
     *
     * @param returnInformationList 交换机返回的信息列表
     * @param protos 协议列表
     * @return 外部IP列表
     */
    public static List<ExternalIPCalculator> getExternalIPList(List<String> returnInformationList,
                                                               List<String> protos,
                                                               SwitchParameters switchParameters) {
        List<ExternalIPCalculator> externalIPList = new ArrayList<>();

        /*协议*/
        String proto = "";
        /*IP CIDR格式*/
        String CIDR = "";
        /*优先级 Cost 值*/
        String Pre_Cost = "";

        /* 交换机返回信息 行数据*/
        for (int i = 0; i < returnInformationList.size(); i++) {

            /* 交换机返回结果每行信息*/
            String returnInformation_split_i = returnInformationList.get(i);

            /* 获取 端口号*/
            String port = MyUtils.includePortNumberKeywords(returnInformation_split_i);
            /* 当端口号不为空*/
            if (port == null) {
                continue;
            }

            /* 判断字符串中有几个 IPCIDR 和 IP 特征数据,并返回ip数据 */
            List<String> ipcidRs = MyUtils.findIPCIDRs(returnInformation_split_i);
            /* IP特征数据集合长度不为0*/
            if (ipcidRs.size() == 0) {
                continue;
            }


            /* 当端口号不为空 并且 IP特征数据集合长度不为0 的情况下继续进行 */

            /* 创建 外部IP数据 实体类*/
            ExternalIPCalculator externalIP = new ExternalIPCalculator();
            /* 设置端口号*/
            externalIP.setInterface(port);

            for (String ipcidR : ipcidRs) {
                if (ipcidR.indexOf("/")!=-1){
                    /* 获取 IP CIDR 格式数据
                    * 如果有新的值 复制新的值
                    * 如果没新的值 使用原有值*/

                    CIDR = ipcidR;
                }else {
                    /* 设置 IP 格式数据*/
                    externalIP.setNextHop(ipcidR);
                }
            }
            /* 设置 IP CIDR 格式数据*/
            externalIP.setDestinationMask(CIDR);

            /* 获取协议 */
            String newProto = findProto(returnInformation_split_i, protos);
            /* 获取协议 数据
             * 如果有新的值 复制新的值
             * 如果没新的值 使用原有值*/
            if (newProto != null) {
                proto = newProto;
            }
            externalIP.setProto(proto);

            /* 有的优先级、Cost值的格式有 [110/1] 情况*/
            if (returnInformation_split_i.indexOf("[")!=-1
                    &&returnInformation_split_i.indexOf("]")!=-1) {
                /* 获取优先级、Cost值*/
                /* 交换机返回行信息 行信息的元素数组*/
                String[] returnInformation_line = returnInformation_split_i.split(" ");

                /* 判断该字符串 是否包含 “[”、“]”、“/”、数字 */
                for (String returnInformation_lineI : returnInformation_line) {
                    if (returnInformation_lineI.indexOf("[")!=-1
                            &&returnInformation_lineI.indexOf("]")!=-1
                            &&returnInformation_lineI.indexOf("/")!=-1
                            &&MyUtils.containDigit(returnInformation_lineI)) {
                        Pre_Cost = returnInformation_lineI;
                        break;
                    }
                }

            }else {
                /* 获取优先级、Cost值*/
                List<String> returnInformation_line = Arrays.stream(returnInformation_split_i.split(" ")).collect(Collectors.toList());

                List<Integer> Pre_Cost_List = new ArrayList<>();
                for(int j=0;j<returnInformation_line.size();j++){
                    if(MyUtils.allIsNumeric(returnInformation_line.get(j))){
                        Pre_Cost_List.add(Integer.parseInt(returnInformation_line.get(j)));
                    }
                }

                /* 其他情况默认包含两个纯数字 并且两个数字挨着*/
                if(Pre_Cost_List.size() == 2 &&
                        ( returnInformation_split_i.indexOf(Pre_Cost_List.get(0) + " "+ Pre_Cost_List.get(1)) != -1)){
                    Pre_Cost = "["+Pre_Cost_List.get(0)+"/"+Pre_Cost_List.get(1)+"]";
                }else if(Pre_Cost_List.size() != 0){

                    //  异常处理 获取优先级、Cost值不正确
                    String subversionNumber = switchParameters.getSubversionNumber();
                    if (subversionNumber!=null){
                        subversionNumber = "、"+subversionNumber;
                    }
                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "路由聚合",
                            "系统信息:" +
                                    "IP地址为:"+switchParameters.getIp()+","+
                                    "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                    "问题为:路由聚合"+ port +"端口号获取到的优先级、Cost值不正确\r\n");
                    //程序问题
                    AbnormalAlarmInformationMethod.afferent(null,switchParameters.getLoginUser().getUsername()
                            ,"错误代码",
                            "OPSA0004");


                    return new ArrayList<>();
                }

            }

            externalIP.setPreCost(Pre_Cost);

            if (externalIP.getNextHop() != null) {
                externalIPList.add(externalIP);
            }

        }
        return externalIPList;
    }

    /**
     * 在给定的字符串中查找协议。
     *
     * @param returnInformationSplitI 要搜索的字符串
     * @param protos 协议列表
     * @return 如果找到匹配的协议，则返回该协议；否则返回null
     */
    private static String findProto(String returnInformationSplitI, List<String> protos) {
        // 遍历协议列表
        for (String proto : protos) {
            // 判断当前行信息是否包含协议（忽略大小写）
            if (MyUtils.containIgnoreCase(" "+returnInformationSplitI+" "," "+proto+" ")
                    // 如果当前行信息包含协议（在协议前后有空格）或者当前行信息以协议开头（协议后有空格）
                    || returnInformationSplitI.startsWith(proto+" ")) {
                // 如果包含，则返回该协议
                return proto;
            }
        }
        // 如果遍历完所有协议都不匹配，则返回null
        return null;
    }




}
