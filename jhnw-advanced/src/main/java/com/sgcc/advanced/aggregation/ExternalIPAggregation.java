package com.sgcc.advanced.aggregation;

import com.sgcc.advanced.domain.ExternalIP;
import com.sgcc.share.util.MyUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ExternalIPAggregation {
    public static void main(String[] args){

        String returnInformation = "OSPF E2  10.122.117.208/29 [110/20] via 10.122.114.86, 75d,18:10:16, GigabitEthernet 9/1\n" +
                "O E2  10.122.117.216/29 [110/20] via 10.122.114.86, 75d,18:10:16, GigabitEthernet 9/1\n" +
                "O E2  10.122.117.224/29 [110/20] via 10.122.114.86, 75d,18:10:16, GigabitEthernet 9/1\n" +
                "O E2  10.122.117.232/29 [110/20] via 10.122.114.86, 75d,18:10:16, GigabitEthernet 9/1\n" +
                "O E2  10.122.117.240/29 [110/20] via 10.122.114.86, 75d,18:10:16, GigabitEthernet 9/1\n" +
                "C E2  10.122.117.248/29 [110/20] via 10.122.114.86, 75d,18:10:16, GigabitEthernet 9/1\n" +
                "O     10.122.119.4/30 [110/3] via 10.122.114.86, 12d,23:14:47, GigabitEthernet 9/1\n" +
                "OSPF     10.122.119.8/30 [110/3] via 10.122.114.86, 12d,23:14:47, GigabitEthernet 9/1\n" +
                "O     10.122.119.12/30 [110/4] via 10.122.114.86, 12d,23:14:47, GigabitEthernet 9/1\n" +
                "S     10.122.119.16/30 [110/102] via 10.122.114.86, 12d,23:14:47, GigabitEthernet 9/1\n" +
                "O     10.122.119.20/30 [110/103] via 10.122.114.86, 12d,23:14:47, GigabitEthernet 9/1\n" +
                "O_ASE     10.122.119.24/29 [110/2] via 10.122.114.86, 12d,23:14:47, GigabitEthernet 9/1\n" +
                "O     10.122.119.32/29 [110/3] via 10.122.114.86, 127d,14:44:44, GigabitEthernet 9/1\n" +
                "                       [110/3] via 10.122.114.90, 127d,14:44:44, GigabitEthernet 9/2\n" +
                "O     10.122.119.48/29 [110/12] via 10.122.114.86, 116d,00:06:03, GigabitEthernet 9/1\n" +
                "                       [110/12] via 10.122.114.90, 116d,00:06:03, GigabitEthernet 9/2\n" +
                "O     10.122.119.56/29 [110/2] via 10.122.114.86, 202d,17:28:24, GigabitEthernet 9/1\n" +
                "O     10.122.119.64/30 [110/3] via 10.122.114.86, 12d,23:14:47, GigabitEthernet 9/1\n" +
                "O     10.122.119.68/30 [110/4] via 10.122.114.86, 12d,23:14:47, GigabitEthernet 9/1\n" +
                "O     10.122.119.80/29 [110/4] via 10.122.114.86, 12d,23:14:47, GigabitEthernet 9/1";

        String text = "The IP addresses are 192.168.1.1/24 and 2001:db8::1/32, but not 192.168.1.256/24 or 2001:db8::g/128.";
        //String deviceVersion = (String) CustomConfigurationUtil.getValue("obtainPortNumber.keyword", Constant.getProfileInformation());

        String deviceVersion ="GigabitEthernet GE FastEthernet Ten-GigabitEthernet Ethernet Eth-Trunk XGigabitEthernet Trunking BAGG Eth FastEthernet SFP USB InLoop";

        String[] deviceVersion_split = deviceVersion.split(" ");

        String[] returnInformation_split = returnInformation.split("\n");

        List<ExternalIP> externalIPList = new ArrayList<>();


        /** 获取路由 OSPF、直连、静态 的关键词*/
        String keyword = "OSPF/O/O_ASE/C/S";

        List<String> protos = new ArrayList<>();
        protos.addAll(Arrays.stream(keyword.split("/")).collect(Collectors.toList()));

        // 使用Lambda表达式和Comparator对字符串按长度从长到短排序
        Collections.sort(protos, Comparator.comparingInt(String::length).reversed());

        /* O_INTRA */
        String proto = "";
        /* 10.98.130.0/24 */
        String CIDR = "";
        /* 10.98.136.13 */
        String ip = "";

        /** 遍历交换机返回结果 */
        for (int i = 0; i < returnInformation_split.length; i++) {
            /* proto 是否改变*/
            boolean change = false;
            /* 交换机返回结果每行信息*/
            String returnInformation_split_i = returnInformation_split[i];
            /* 遍历关键词 判断当前行信息是否包含 关键词*/
            for (String protoelement : protos) {
                /* 判断当前行信息是否包含 关键词 */
                if (MyUtils.containIgnoreCase(returnInformation_split_i,protoelement)) {
                    /* 如果包含当前行信息 关键词
                    * 再判断当前行信息 是否有以关键词开头的 词语
                    * 因为遇到过 O*E2 情况存在*/
                    String[] returnInformation_split_line = returnInformation_split_i.split(" ");
                    for (String line:returnInformation_split_line){

                        if (line.toLowerCase().equals(protoelement.toLowerCase())
                                || line.toLowerCase().startsWith(protoelement.toLowerCase())){
                            /* 如果有以关键词开头的 词语 则 说明状态为 获取到的关键词*/
                            proto = protoelement;
                            change = true;
                            break;
                        }
                    }
                }
                if (change) {
                    break;
                }
            }

            /* 判断字符串中有几个 IPCIDR 和 IP 特征数据,并返回ip数据 */
            List<String> ipcidRs = MyUtils.findIPCIDRs(returnInformation_split_i);

            /* 获取 端口号*/
            String port = MyUtils.includePortNumberKeywords(returnInformation_split_i, deviceVersion_split);

            for (String ipcidR : ipcidRs) {
                if (ipcidR.contains("/")) {
                    CIDR = ipcidR;
                }else {
                    ip = ipcidR;
                }
            }

            ExternalIP externalIP = new ExternalIP();
            externalIP.setProto(proto);
            externalIP.setDestinationMask(CIDR);
            externalIP.setNextHop(ip);
            externalIP.setInterface(port);

            externalIPList.add(externalIP);
        }

        externalIPList.stream().forEach(System.out::println);
    }




}
