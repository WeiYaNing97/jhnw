package com.sgcc.advanced.aggregation;

import com.sgcc.advanced.domain.ExternalIPAddresses;
import com.sgcc.advanced.domain.ExternalIPCalculator;
import com.sgcc.advanced.domain.IPCalculator;
import com.sgcc.share.util.MyUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ExternalRouteAggregation {


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

    public static void main(String[] args){
        /** 端口号特征关键字集合 */
        //String deviceVersion = (String) CustomConfigurationUtil.getValue("obtainPortNumber.keyword", Constant.getProfileInformation());
        String deviceVersion ="GigabitEthernet GE FastEthernet Ten-GigabitEthernet Ethernet Eth-Trunk XGigabitEthernet Trunking BAGG Eth FastEthernet SFP USB InLoop Method Vlan";
        List<String> deviceVersionList = Arrays.stream(deviceVersion.split(" ")).collect(Collectors.toList());
        Collections.sort(deviceVersionList, Comparator.comparingInt(String::length).reversed());

        /** 获取路由 OSPF、直连、静态 的关键词*/
        String keyword = "OSPF/O_INTRA/O/O_ASE/O_ASE2/C/S";
        List<String> protos = new ArrayList<>();
        protos.addAll(Arrays.stream(keyword.split("/")).collect(Collectors.toList()));
        // 使用Lambda表达式和Comparator对字符串按长度从长到短排序
        Collections.sort(protos, Comparator.comparingInt(String::length).reversed());
        List<String> returnInformationList = Arrays.stream(returnInformation.split("\n")).collect(Collectors.toList());

        List<ExternalIPCalculator> externalIPList = getExternalIPList(returnInformationList, deviceVersionList, protos);

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
                        IPCalculator calculator = IPAddressCalculator.Calculator(PreCost_externalIP.getDestinationMask());
                        PreCost_externalIP.setFirstAvailable(calculator.getFirstAvailable());
                        PreCost_externalIP.setFinallyAvailable(calculator.getFinallyAvailable());
                    }

                    List<ExternalIPAddresses> externalIPAddressesList = IPAddressUtils.ExternalSplicingAddressRange(PreCost_externalIPS);

                    for (ExternalIPAddresses externalIPAddresses : externalIPAddressesList) {
                        System.err.println(externalIPAddresses.getIpStart()+" "+externalIPAddresses.getIpEnd());
                        List<ExternalIPCalculator> externalIPCalculatorList = externalIPAddresses.getExternalIPCalculatorList();
                        externalIPCalculatorList.forEach(System.err::println);
                        System.err.println("===================聚合 ：=======================");
                        List<String> stringList = IPAddressUtils.getNetworkNumber(externalIPAddresses.getIpStart(), externalIPAddresses.getIpEnd());
                        stringList.forEach(System.err::println);
                        System.err.println("================================================\r\n");
                    }
                }
            }

            System.err.println("\r\n\r\n");
        }
    }


    public static List<ExternalIPCalculator> getExternalIPList(List<String> returnInformationList, List<String> deviceVersionList, List<String> protos) {
        List<ExternalIPCalculator> externalIPList = new ArrayList<>();

        String proto = "";
        String CIDR = "";
        String Pre_Cost = "";

        for (int i = 0; i < returnInformationList.size(); i++) {

            /* 交换机返回结果每行信息*/
            String returnInformation_split_i = returnInformationList.get(i);

            /* 获取 端口号*/
            String port = MyUtils.includePortNumberKeywords(returnInformation_split_i, deviceVersionList);

            /* 判断字符串中有几个 IPCIDR 和 IP 特征数据,并返回ip数据 */
            List<String> ipcidRs = MyUtils.findIPCIDRs(returnInformation_split_i);

            if (port != null && ipcidRs.size() != 0) {


                if (ipcidRs.size() != 0) {
                    ExternalIPCalculator externalIP = new ExternalIPCalculator();
                    externalIP.setInterface(port);

                    for (String ipcidR : ipcidRs) {
                        if (ipcidR.indexOf("/")!=-1){
                            CIDR = ipcidR;
                        }else {
                            externalIP.setNextHop(ipcidR);
                        }
                    }

                    externalIP.setDestinationMask(CIDR);
                    /* 获取协议 */
                    String newProto = findProto(returnInformation_split_i, protos);
                    if (newProto != null) {
                        proto = newProto;
                    }
                    externalIP.setProto(proto);

                    if (returnInformation_split_i.indexOf("[")!=-1
                            &&returnInformation_split_i.indexOf("]")!=-1) {
                        String[] returnInformation_line = returnInformation_split_i.split(" ");
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
                        String[] returnInformation_line = returnInformation_split_i.split(" ");
                        List<Integer> Pre_Cost_List = new ArrayList<>();

                        for(int j=0;j<returnInformation_line.length;j++){
                            if(MyUtils.allIsNumeric(returnInformation_line[j])){
                                Pre_Cost_List.add(Integer.parseInt(returnInformation_line[j]));
                            }
                        }
                        if(Pre_Cost_List.size() == 2){
                            Pre_Cost = "["+Pre_Cost_List.get(0)+"/"+Pre_Cost_List.get(1)+"]";
                        }else if(Pre_Cost_List.size() != 0){
                            // todo 异常处理
                        }

                    }

                    externalIP.setPreCost(Pre_Cost);

                    if (externalIP.getNextHop() != null) {
                        externalIPList.add(externalIP);
                    }
                }
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



}
