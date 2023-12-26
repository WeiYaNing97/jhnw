package com.sgcc.advanced.test;

import com.sgcc.advanced.domain.OSPFPojo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: jhnw
 * @description: OSPFTest
 * @author:
 * @create: 2023-12-22 16:36
 **/
public class OSPFTest {

    static String string = "Neighbor ID     Pri   State           Dead Time   Address         Interface\n" +
            "10.122.114.230    1   FULL/DR         00:00:36    10.122.114.62   GigabitEthernet1/0/1\n" +
            "10.122.100.5      1   FULL/BDR        00:00:38    10.122.114.38   GigabitEthernet1/0/3";

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

    /*判断字符串是否是IP*/
    public static boolean isIP(String ip) {
        String regex = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
        return Pattern.matches(regex, ip);
    }

    /*判断字符串是否同时包含字母与数字*/
    public static boolean isAlphanumeric(String str) {

        return str.matches(".*[a-zA-Z].*") && str.matches(".*\\d.*");

    }

    /*获取数组各元素的意义*/
    public static List<String> obtainParameterMeanings(String[] values) {
        List<String> meanings = new ArrayList<>();
        for (String value:values){
            if (isIP(value)){
                if ( value.indexOf("0.0.0.") != -1 ){
                    meanings.add("无效IP");
                }else {
                    meanings.add("IP");
                }
            }else if (isAlphanumeric(value)){
                meanings.add("端口");
            }else if (value.toLowerCase().indexOf("full")!=-1){
                meanings.add("状态");
            }else {
                meanings.add("未知");
            }
        }
        return meanings;
    }

    public static void main(String[] args) {
        List<OSPFPojo> ospfPojo = getOSPFPojo(string);
        ospfPojo.stream().forEach(System.out::println);
    }

    public static List<OSPFPojo> getOSPFPojo(String information) {
        /*
         *根据 "obtainPortNumber.keyword" 在配置文件中 获取端口号关键词
         * Eth-Trunk Ethernet GigabitEthernet GE BAGG Eth
         * 根据空格分割为 关键词数组*/
        /*String deviceVersion = (String) CustomConfigurationUtil.getValue("obtainPortNumber.keyword", Constant.getProfileInformation());*/
        String deviceVersion = "GigabitEthernet GE FastEthernet Ten-GigabitEthernet Ethernet Eth-Trunk XGigabitEthernet Trunking BAGG" +
                " Eth FastEthernet SFP USB";
        String[] keywords = deviceVersion.trim().split(" ");

        List<String> keys = new ArrayList<>();

        for (String key:keywords){
            if (information.indexOf(" "+key+" ")!=-1){
                keys.add(key);
            }
        }

        List<String> string_split = Arrays.asList(information.split("\n"));

        for (String key:keys){
            for (int i = 0 ; i < string_split.size() ; i++){
                if (string_split.get(i).indexOf(" "+key+" ") != -1){
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
            System.err.println("取词失败");
        }

        String[] split = input.split("\\s+");

        /*获取数组各元素的意义*/
        List<String> stringList = obtainParameterMeanings(split);
        System.out.println(stringList);

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

        /*筛选 与含有full数据长度相等的行集合
         * 条件数 full数据 两个IP、一个端口号对应的列 数据特征要一样*/
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

                    boolean isIp = isIP(array[i]);
                    boolean isPort = isAlphanumeric(array[i]);
                    boolean isState = array[i].toLowerCase().indexOf("full")!=-1;

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

}
