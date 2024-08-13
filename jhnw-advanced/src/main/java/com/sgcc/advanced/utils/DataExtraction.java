package com.sgcc.advanced.utils;

import com.sgcc.share.domain.Constant;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.MyUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DataExtraction {

    public static List<HashMap<String, Object>> tableDataExtraction(List<String> switchReturnsInformation, Map<String,String> keywordS) {
        // 获取表头、标题栏及位置
        /** 获取表头、标题栏 及 位置 */
        String tableHeader = keywordS.get("TableHeader");
        tableHeader = MyUtils.normalizePunctuation(tableHeader);
        List<String> tableHeader_name = Arrays.stream(tableHeader.split(",")).collect(Collectors.toList());
        tableHeader = tableHeader.replace(",", " ");
        int HeaderLineNumber = 0;
        for (int i=0; i<switchReturnsInformation.size(); i++) {
            if ( switchReturnsInformation.get(i).equals(tableHeader) ) {
                HeaderLineNumber = i;
                break;
            }
        }
        // 特征集合
        /* 特征集合 */
        List<String> characteristicList = new ArrayList<>();
        String[] firstRowOfDataSplit = switchReturnsInformation.get(HeaderLineNumber+1).split(" ");
        for (int i = 0; i < firstRowOfDataSplit.length; i++) {
            characteristicList.add(obtainDataFeatures(firstRowOfDataSplit[i]));
        }
        String characteristicList_join = String.join(",", characteristicList);
        List<String> tableRowList = new ArrayList<>();
        for (int i = HeaderLineNumber+1; i < switchReturnsInformation.size(); i++) {
            String[] rowOfDataSplit = switchReturnsInformation.get(i).split(" ");
            List<String> rowFeatures_List = new ArrayList<>();
            for (int j = 0; j < rowOfDataSplit.length; j++) {
                rowFeatures_List.add(obtainDataFeatures(rowOfDataSplit[j]));
            }

            String elementToCheck = "未知";
            boolean allMatch = rowFeatures_List.stream().allMatch(element -> element.equals(elementToCheck));
            if (allMatch) {
                continue;
            }

            String rowFeatures_List_join = String.join(",", rowFeatures_List);

            if (characteristicList_join.indexOf(rowFeatures_List_join) != -1) {
                if (characteristicList_join.equals(rowFeatures_List_join)){
                    tableRowList.add(String.join(",", Arrays.stream(rowOfDataSplit).collect(Collectors.toList())));
                }else {
                    int i1 = characteristicList_join.indexOf(rowFeatures_List_join);
                    String startsubstring = characteristicList_join.substring(0, i1);
                    int i2 = MyUtils.countCharWithRegex(startsubstring, ',');
                    String start = "";
                    for (int k = 0; k < i2; k++) {
                        start += "&,";
                    }
                    String endsubstring = characteristicList_join.substring(i1+rowFeatures_List_join.length());
                    int i3 = MyUtils.countCharWithRegex(endsubstring, ',');
                    String end = "";
                    for (int k = 0; k < i3; k++) {
                        end += ",&";
                    }
                    tableRowList.add(start + String.join(",", Arrays.stream(rowOfDataSplit).collect(Collectors.toList())) + end);
                }
            }
        }

        List<HashMap<String, Object>> tableDataList = new ArrayList<>();
        for (int i = 0; i < tableRowList.size(); i++) {
            HashMap<String, Object> tableData = new HashMap<>();
            String[] rowOfDataSplit = tableRowList.get(i).split(",");
            Map<String,Object> map_name_value = new HashMap<>();
            for (int j = 0; j < rowOfDataSplit.length; j++) {
                map_name_value.put(tableHeader_name.get(j),rowOfDataSplit[j]);
            }

            for (String key: map_name_value.keySet()) {
                String keyByValue = getKeyByValue(keywordS, key);
                if (keyByValue != null) {
                    tableData.put(keyByValue, map_name_value.get(key));
                }
            }
            tableDataList.add(tableData);
        }
        return tableDataList;
    }

    /**
     * 根据给定的字符串信息获取数据特征。
     *
     * @param information 待分析的字符串信息
     * @return 返回分析后的数据特征字符串，可能是 IPCIDR、IP、端口号、纯数字 或 未知
     */
    public static String obtainDataFeatures(String information) {

        /*判断字符串中有几个 IPCIDR 和 IP 特征数据,并返回ip数据*/
        if (MyUtils.findIPCIDRs(information).size()!=0){
            if (information.indexOf("/")!=-1){
                return "IPCIDR";
            }else {
                return "IP";
            }
        }

        /*判断字符串是否为端口号
        * 判断是否以端口号关键词开始 并且包含数字*/
        String deviceVersion = (String) CustomConfigurationUtil.getValue("obtainPortNumber.keyword", Constant.getProfileInformation());
        List<String> deviceVersionList = Arrays.stream(deviceVersion.split(" ")).collect(Collectors.toList());
        Collections.sort(deviceVersionList, Comparator.comparingInt(String::length).reversed());
        for (String keyword: deviceVersionList) {
            /*判断是否以端口号关键词开始 并且包含数字*/
            if (information.toLowerCase().startsWith(keyword.toLowerCase())
                    && MyUtils.containDigit(information)) {
                return "端口号";
            }
        }

        /*判断字符串是否为全数字*/
        if (MyUtils.allIsNumeric(information)){
            return "纯数字";
        }

        return "未知";
    }

    /**
     * 根据给定的值获取Map中对应的键
     *
     * @param map 给定的Map集合
     * @param value 给定的值
     * @param <K> Map集合中键的类型
     * @param <V> Map集合中值的类型
     * @return 如果找到与给定值相等的entry，则返回对应的键；否则返回null
     */
    public static <K, V> K getKeyByValue(Map<K, V> map, V value) {
        // 使用Java 8的Stream API遍历Map的entry集合
        return map.entrySet().stream()
                // 过滤出与给定值相等的entry
                .filter(entry -> Objects.equals(value, entry.getValue()))
                // 获取entry的键
                .map(Map.Entry::getKey)
                // 找到第一个符合条件的键
                .findFirst()
                // 如果没有找到，返回null
                .orElse(null); // 如果没有找到，返回null
    }


    public static void main(String[] args) {
        String s = "network 10.123.239.0 0.0.0.255 area 0.0.0.0";
        List<String> $1 = getTheMeaningOfPlaceholders(s, "network $ 0.0.0.255 area $");
        $1.forEach(System.out::println);
    }

    /**
     * 获取占位符的含义
     *
     * @param input 输入字符串
     * @param keyword 关键词
     * @return 占位符的含义，如果行信息不包含关键词则返回null
     */
    public static List<String> getTheMeaningOfPlaceholders(String input,String keyword) {
        // 将关键词和输入字符串中的冒号替换为" : "，并将多余的空格替换为单个空格，并去除首尾空格
        keyword = keyword.replace(":"," : ").replaceAll("\\s+"," ").trim();
        input = input.replace(":"," : ").replaceAll("\\s+"," ").trim();

        // 1 首先将交换机返回信息数字替换为"",
        // 将配置文件中的占位符$替换为""
        String[] keyword_split = keyword.trim().split("\\$+");

        // 2 如果 行信息 不包含 关键词 则返回null
        for (String key:keyword_split){
            if (input.indexOf(key.trim()) ==-1){
                return new ArrayList<>();
            }
        }

        List<String> splitkeywords = splitkeywords(keyword);

        List<String> returnValue = new ArrayList<>();

        for (String splitkeyword:splitkeywords){

            if (splitkeyword.startsWith("$")){
                // 替换掉$ 方便匹配
                splitkeyword = splitkeyword.replace("$","").trim();

                // 第一次出现的位置
                int index = input.indexOf(splitkeyword);
                String substring = input.substring(0, index);
                returnValue.add(substring);
            }else if (splitkeyword.endsWith("$")){
                splitkeyword = splitkeyword.replace("$","").trim();

                // 最后一次出现位置
                int lastIndex = input.indexOf(splitkeyword);
                String substring = input.substring(lastIndex + splitkeyword.length(), input.length());
                returnValue.add(substring);
            }else {
                // $ 出现在中间
                String[] $split = splitkeyword.split("\\$");

                // 第一次出现的位置
                int start = input.indexOf($split[0].trim());
                int end = input.lastIndexOf($split[1].trim());

                if ( start+$split[0].length() < end ){
                    int s = start + $split[0].length();
                    String substring = input.substring(s , end).trim();;
                    returnValue.add(substring);
                }
            }
        }

        return returnValue;
    }


    /**
     * 将包含"$"的关键字拆分成多个子关键字并返回列表
     *
     * @param keyword 包含"$"的关键字
     * @return 拆分后的子关键字列表
     */
    public static List<String> splitkeywords (String keyword) {

        // 使用"$"将关键字拆分成数组
        String[] keyword_split = keyword.split("\\$");

        // 如果拆分后的数组第一个元素为空字符串，则忽略它
        if (keyword_split.length > 0 && keyword_split[0].isEmpty()) {
            // 忽略第一个空字符串
            keyword_split = Arrays.copyOfRange(keyword_split, 1, keyword_split.length);
        }

        // 初始化关键字列表
        List<String> keyList = new ArrayList<>();

        // 获取"$"在关键字中的位置列表
        List<Integer> substringPositions = MyUtils.getSubstringPositions(keyword, "$");

        // 遍历"$"的位置列表
        for (int i = 0; i < substringPositions.size(); i++) {
            // 如果"$"的位置是0，说明"$"在关键字的最前面
            if (substringPositions.get(i) == 0) {
                keyList.add("$" + keyword_split[0]);

            // 如果"$"的位置是关键字长度减1，说明"$"在关键字的最后面
            } else if (substringPositions.get(i) == keyword.length() - 1) {
                keyList.add(keyword_split[keyword_split.length - 1] + "$");

            // 其他情况
            } else {
                // 获取"$"之前的子字符串
                String substringStart = keyword.substring(0, substringPositions.get(i));
                // 获取"$"之后的子字符串
                String substringEnd = keyword.substring(substringPositions.get(i) + 1);

                // 将"$"之前的子字符串按"$"拆分
                String[] splitStart = substringStart.split("\\$");
                // 将"$"之后的子字符串按"$"拆分
                String[] splitEnd = substringEnd.split("\\$");

                // 将拆分后的两个子字符串的最后一部分拼接上"$"后添加到关键字列表中
                keyList.add(splitStart[splitStart.length - 1] + "$" + splitEnd[splitEnd.length - 1]);
            }
        }

        // 返回关键字列表
        return keyList;
    }

}
