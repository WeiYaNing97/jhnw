package com.sgcc.advanced.aggregation;

import com.sgcc.advanced.domain.ExternalIPAddresses;
import com.sgcc.advanced.domain.ExternalIPCalculator;
import com.sgcc.advanced.domain.IPCalculator;
import com.sgcc.advanced.utils.DataExtraction;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.MyUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ExternalRouteAggregation {

    public static void main(String[] args){

        // 获取路由 OSPF、直连、静态 的关键词
        String keyword = "OSPF/O_INTRA/O/O_ASE/O_ASE2/C/S";
        List<String> protos = new ArrayList<>();
        protos.addAll(Arrays.stream(keyword.split("/")).collect(Collectors.toList()));
        // 使用Lambda表达式和Comparator对字符串按长度从长到短排序
        Collections.sort(protos, Comparator.comparingInt(String::length).reversed());
        List<String> returnInformationList = Arrays.stream(MyUtils.trimString(returnInformation).split("\r\n")).collect(Collectors.toList());

        HashMap<String,Object> keyMap = (HashMap<String,Object>) CustomConfigurationUtil.getValue("路由聚合."+"H3C", Constant.getProfileInformation());
        List<String> keyList = keyMap.keySet().stream().collect(Collectors.toList());
        List<ExternalIPCalculator> externalIPList = new ArrayList<>();
        /* 符合表格格式 */
        if (keyList.indexOf("R_table")!=-1){
            externalIPList = getTableExternalIPList(returnInformationList);
        }else {
            /* 不符合表格格式 */
            externalIPList = getExternalIPList(returnInformationList, protos);
        }

        Map<String, List<ExternalIPCalculator>> proto_collect = externalIPList.stream().collect(Collectors.groupingBy(ExternalIPCalculator::getProto));

        Set<String> protoSet = proto_collect.keySet();
        for (String proto : protoSet) {
            System.err.println(proto+"==========================================");
            List<ExternalIPCalculator> proto_externalIPS = proto_collect.get(proto);
            Map<String, List<ExternalIPCalculator>> NextHop_collect = proto_externalIPS.stream().collect(Collectors.groupingBy(ExternalIPCalculator::getNextHop));

            Set<String> NextHopSet = NextHop_collect.keySet();
            for (String NextHop : NextHopSet) {
                System.err.println("====================="+NextHop+"=====================");
                List<ExternalIPCalculator> NextHop_externalIPS = NextHop_collect.get(NextHop);
                Map<String, List<ExternalIPCalculator>> PreCost_collect = NextHop_externalIPS.stream().collect(Collectors.groupingBy(ExternalIPCalculator::getPreCost));

                Set<String> PreCostSet = PreCost_collect.keySet();
                for (String PreCost : PreCostSet) {
                    System.err.println("=========================================="+PreCost);
                    List<ExternalIPCalculator> PreCost_externalIPS = PreCost_collect.get(PreCost);

                    for (ExternalIPCalculator PreCost_externalIP : PreCost_externalIPS) {
                        // 根据目标掩码计算第一个可用IP和最后一个可用IP
                        IPCalculator calculator = IPAddressCalculator.Calculator(PreCost_externalIP.getDestinationMask());
                        PreCost_externalIP.setFirstAvailable(calculator.getFirstAvailable());
                        PreCost_externalIP.setFinallyAvailable(calculator.getFinallyAvailable());
                    }

                    // 将PreCost_externalIPS进行地址范围拼接
                    List<ExternalIPAddresses> externalIPAddressesList = IPAddressUtils.ExternalSplicingAddressRange(PreCost_externalIPS);

                    for (ExternalIPAddresses externalIPAddresses : externalIPAddressesList) {
                        System.err.println(externalIPAddresses.getIpStart()+" "+externalIPAddresses.getIpEnd());
                        List<ExternalIPCalculator> externalIPCalculatorList = externalIPAddresses.getExternalIPCalculatorList();
                        externalIPCalculatorList.forEach(System.err::println);
                        System.err.println("===================聚合 ：=======================");
                        // 获取网络号
                        List<String> stringList = IPAddressUtils.getNetworkNumber(externalIPAddresses.getIpStart(), externalIPAddresses.getIpEnd());
                        stringList.forEach(System.err::println);
                        System.err.println("================================================\r\n");
                    }
                }
            }

            System.err.println("\r\n\r\n");
        }
    }

    private static List<ExternalIPCalculator> getTableExternalIPList(List<String> returnInformationList) {
        HashMap<String,String> keyMap = (HashMap<String,String>) CustomConfigurationUtil.getValue("路由聚合."+"H3C.R_table", Constant.getProfileInformation());
        List<HashMap<String, Object>> stringObjectHashMapList = DataExtraction.tableDataExtraction(returnInformationList, keyMap);
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
    public static List<ExternalIPCalculator> getExternalIPList(List<String> returnInformationList, List<String> protos) {
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
                    // todo 异常处理 优先级、Cost值不正确

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
            if (MyUtils.containIgnoreCase(returnInformationSplitI," "+proto+" ")
                    || returnInformationSplitI.startsWith(proto+" ")) {
                // 如果包含，则返回该协议
                return proto;
            }
        }
        // 如果遍历完所有协议都不匹配，则返回null
        return null;
    }


    static String returnInformation = "Destinations : 403      Routes : 2233\n" +
            "\n" +
            "Destination/Mask   Proto   Pre Cost        NextHop         Interface\n" +
            "0.0.0.0/0          O_ASE2  150 1           10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "0.0.0.0/32         Direct  0   0           127.0.0.1       InLoop0\n" +
            "10.0.0.0/8         O_ASE2  150 1           10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "10.10.0.0/16       O_ASE2  150 20          10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "10.13.0.0/16       O_ASE2  150 20          10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "10.19.0.0/16       O_ASE2  150 20          10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "10.98.128.0/19     O_ASE2  150 500         10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "10.98.128.0/24     O_INTRA 10  104         10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "10.98.128.1/32     O_INTRA 10  104         10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "10.98.129.0/24     O_INTRA 10  104         10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "10.98.129.1/32     O_INTRA 10  104         10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "10.98.130.0/24     O_INTRA 10  104         10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n"+
            "O     10.122.114.92/30 [110/2] via 10.122.114.90, 336d,22:11:17, GigabitEthernet 9/2\n" +
            "O     10.122.114.108/30 [110/2] via 10.122.114.86, 55d,15:58:41, GigabitEthernet 9/1\n" +
            "O E2  10.122.114.112/30 [110/20] via 10.122.114.86, 259d,14:47:59, GigabitEthernet 9/1\n" +
            "O     10.122.114.116/30 [110/3] via 10.122.114.86, 46d,15:14:52, GigabitEthernet 9/1\n" +
            "C     10.122.114.128/30 is directly connected, GigabitEthernet 9/14\n" +
            "C     10.122.114.129/32 is local host. \n" +
            "O     10.122.114.132/30 [110/2] via 10.122.114.90, 27d,17:27:30, GigabitEthernet 9/2\n" +
            "                        [110/2] via 10.122.114.130, 27d,17:27:30, GigabitEthernet 9/14\n" +
            "O     10.122.114.136/30 [110/2] via 10.122.114.86, 336d,22:11:17, GigabitEthernet 9/1\n" +
            "O E2  10.122.114.144/30 [110/1] via 10.122.114.86, 98d,11:28:16, GigabitEthernet 9/1\n" +
            "O E2  10.122.114.148/30 [110/1] via 10.122.114.86, 98d,11:28:16, GigabitEthernet 9/1\n" +
            "                        [110/1] via 10.122.114.90, 98d,11:28:16, GigabitEthernet 9/2";
}
