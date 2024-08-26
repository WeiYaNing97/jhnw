package com.sgcc.advanced.utils;

import com.sgcc.share.domain.Constant;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.MyUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DataExtraction {

    /**
     * todo R_table提取数据方法缺点：
     * 如果要提取数据的行信息中中有省略数据，并且行信息中没有特征数据 则无法提取。
     */
    /* R_table */
    /**
     * 从交换机返回信息中提取表格数据
     *
     * @param switchReturnsInformation 交换机返回信息列表
     * @param keywordS                  关键字映射
     * @return 表格数据列表，每个元素为一个HashMap，包含键值对形式的表格数据
     */
    public static List<HashMap<String, Object>> tableDataExtraction(List<String> switchReturnsInformation, Map<String,String> keywordS) {
        // 获取表头、标题栏及位置
        String tableHeader = keywordS.get("TableHeader");
        // 将字符串中的中文标点符号替换为英文标点符号
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

            // 检查行特征是否全部为"未知"
            String elementToCheck = "未知";
            boolean allMatch1 = rowFeatures_List.stream().allMatch(element -> element.equals(elementToCheck));

            /*  todo 表格提取数据 怎样判断不是需提取数据
                当前信息行全为未知 并且 当前信息行数据要小于 标题数据
                的情况下 默认不满足条件
                如果当前行数 大于 标题属性数 直接结束*/
            if ( rowFeatures_List.size() > tableHeader_name.size()
                    || (allMatch1 && rowFeatures_List.size() != tableHeader_name.size())) {
                // todo 报错 提出数据可能出现异常 因为非正常跳出
                break;
            }

            String rowFeatures_List_join = String.join(",", rowFeatures_List);

            // 检查行特征是否存在于特征集合中,并且只有一个。
            List<Integer> substringPositions = MyUtils.getSubstringPositions(characteristicList_join, rowFeatures_List_join);
            if (substringPositions.size() != 1){

                if (substringPositions.size() > 1){/* 多个 */
                    // todo 应该报错 ：行信息：IP,端口号   标题属性： IP,端口号,纯数字,纯数字,IP,端口号  也会导致取值错误。

                }

                // todo 当前行特征与特征集合中特征不匹配，跳过   继续分析下一行
                continue;
            }

            if (characteristicList_join.equals(rowFeatures_List_join)){
                // 如果行特征等于特征集合，则直接添加到tableRowList中
                tableRowList.add(String.join(",", Arrays.stream(rowOfDataSplit).collect(Collectors.toList())));
            } else {
                // 获取特征集合中对应特征的起始索引和结束索引
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

                // 拼接特殊字符后添加到tableRowList中
                tableRowList.add(start + String.join(",", Arrays.stream(rowOfDataSplit).collect(Collectors.toList())) + end);
            }
        }

        List<HashMap<String, Object>> tableDataList = new ArrayList<>();
        for (int i = 0; i < tableRowList.size(); i++) {
            HashMap<String, Object> tableData = new HashMap<>();
            String[] rowOfDataSplit = tableRowList.get(i).split(",");
            Map<String,Object> map_name_value = new HashMap<>();
            for (int j = 0; j < rowOfDataSplit.length; j++) {
                // 将表头名称和数据值映射存储
                map_name_value.put(tableHeader_name.get(j),rowOfDataSplit[j]);
            }

            // 根据关键字映射更新tableData的键值
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

    /* L_list */
    /**
     * 获取占位符对应的含义
     *
     * @param input 输入字符串
     * @param keyword 关键字，其中包含了占位符
     * @return 一个映射表，将占位符映射到它们对应的含义；如果输入或关键词为空，或者关键词中的占位符在输入字符串中不存在，则返回空映射表
     */
    public static Map<String,String> getTheMeaningOfPlaceholders(String input,String keyword) {
        // 将关键词和输入字符串中的冒号替换为" : "，并将多余的空格替换为单个空格，并去除首尾空格
        keyword = keyword.replace(":"," : ").replaceAll("\\s+"," ").trim();
        input = input.replace(":"," : ").replaceAll("\\s+"," ").trim();

        // 将关键词按照"$数字"的形式进行拆分
        String[] keyword_split = keyword.trim().split("\\$\\d*");

        // 如果拆分后的数组第一个元素为空字符串，则忽略它
        if (keyword_split.length > 0 && keyword_split[0].isEmpty()) {
            // 忽略第一个空字符串
            keyword_split = Arrays.copyOfRange(keyword_split, 1, keyword_split.length);
        }

        // 检查拆分后的关键词是否全部存在于输入字符串中
        for (String key:keyword_split){
            if (input.indexOf(key.trim()) ==-1){
                return new HashMap<>();
            }
        }

        // 获取占位符列表
        List<String> placeholders = getPlaceholders(keyword);
        Map<String,String> returnMap = new HashMap<>();
        for (String placeholder:placeholders){
            String splitkeyword = splitkeywords(keyword,placeholder,placeholders);
            if (splitkeyword.startsWith(placeholder)){
                // 替换掉"$"方便匹配
                // 替换掉$ 方便匹配
                splitkeyword = splitkeyword.replace(placeholder,"").trim();

                // 第一次出现的位置
                int index = input.indexOf(splitkeyword);
                if (index > 0){
                    String substring = input.substring(0, index);
                    String[] split = substring.split(" ");
                    if (split.length > 0 && split[0].isEmpty()) {
                        // 忽略第一个空字符串
                        split = Arrays.copyOfRange(split, 1, split.length);
                    }
                    /* 占位符在关键词的前面 取出词 应该取末位*/
                    returnMap.put(placeholder,split[split.length-1]);
                }else {
                    // todo 配置文件关键词 设置错误 取词失败。
                }
            }else if (splitkeyword.endsWith(placeholder)){
                splitkeyword = splitkeyword.replace(placeholder,"").trim();

                // 最后一次出现位置
                int lastIndex = input.indexOf(splitkeyword);
                String substring = input.substring(lastIndex + splitkeyword.length(), input.length());
                String[] split = substring.split(" ");
                if (split.length > 0 && split[0].isEmpty()) {
                    // 忽略第一个空字符串
                    split = Arrays.copyOfRange(split, 1, split.length);
                }

                /* 占位符在关键词的后面 取出词 应该取首位*/
                returnMap.put(placeholder,split[0]);
            }else {
                String variable = "";
                List<Integer> integers = MyUtils.extractInts(placeholder);
                if (integers.size() == 1){
                    variable = integers.get(0)+"";
                }
                String delimiter  = "\\$" + variable;

                // "$" 出现在中间
                // $ 出现在中间
                String[] $split = splitkeyword.split(delimiter);
                if ($split.length > 0 && $split[0].isEmpty()) {
                    // 忽略第一个空字符串
                    $split = Arrays.copyOfRange($split, 1, $split.length);
                }


                // 第一次出现的位置
                int start = input.indexOf($split[0].trim());
                int end = input.lastIndexOf($split[1].trim());

                if ( start+$split[0].length() < end ){
                    int s = start + $split[0].length();
                    String substring = input.substring(s , end).trim();;
                    returnMap.put(placeholder,substring);
                }
            }
        }

        return returnMap;
    }

    /**
     * 拆分关键字，并返回特定格式的字符串
     *
     * @param keyword 待拆分的关键字字符串
     * @param placeholder 占位符
     * @return 返回拆分后并拼接特定格式的字符串
     */
    public static String splitkeywords (String keyword,String placeholder,List<String> placeholders) {
        // 将关键字按空格拆分成字符串数组
        String[] keyword_split = keyword.split(" ");

        // 如果拆分后的数组第一个元素为空字符串，则忽略它
        if (keyword_split.length > 0 && keyword_split[0].isEmpty()) {
            // 将数组的第一个元素忽略，并复制剩余元素到新的数组中
            keyword_split = Arrays.copyOfRange(keyword_split, 1, keyword_split.length);
        }

        List<String> keywordList = Arrays.stream(keyword_split).collect(Collectors.toList());

        // 如果关键字以占位符开头
        if (keyword.startsWith(placeholder)){

            if (placeholders.size() == 1){
                return keyword.trim();
            }else {
                String value = placeholders.get(1);
                int position = keywordList.indexOf(value);
                String returnPlaceholder = "";
                for (int i = 0;i<position;i++){
                    returnPlaceholder += keywordList.get(i) + " ";
                }
                return returnPlaceholder.trim();
            }

        // 如果关键字以占位符结尾
        }else if (keyword.endsWith(placeholder)){

            if (placeholders.size() == 1){
                return keyword.trim();
            }else {
                String value = placeholders.get(placeholders.size()-2);
                int position = keywordList.indexOf(value);
                String returnPlaceholder = "";
                for (int i = position + 1;i < keywordList.size();i++){
                    returnPlaceholder += keywordList.get(i) + " ";
                }
                return returnPlaceholder.trim();
            }

        // 如果关键字中包含占位符
        }else {
            if (placeholders.size() == 1){
                return keyword.trim();
            }else {
                // 获取占位符在关键字中的位置
                int position = keywordList.indexOf(placeholder);
                String start ="";
                String end = "";
                for (int i = position-1;i >= 0;i--){
                    if (placeholders.contains(keywordList.get(i))){
                        break;
                    }else {
                        start = keywordList.get(i) + " " + start;
                    }
                }
                for (int i = position+1;i < keywordList.size();i++){
                    if (placeholders.contains(keywordList.get(i))){
                        break;
                    }else {
                        end = end + " " +keywordList.get(i) ;
                    }
                }
                return start + placeholder + end;
            }
        }
    }

    /**
     * 获取字符串中所有的占位符（以$符号开头后跟数字）
     *
     * @param keyword 待处理的字符串
     * @return 包含所有占位符的列表，如果未找到占位符则返回空列表
     */
    public static List<String> getPlaceholders(String keyword) {
        // 创建一个正则表达式模式对象，用于匹配$符号后面的数字
        Pattern pattern = Pattern.compile("\\$\\d*");
        // 创建一个匹配器对象，对输入的字符串进行匹配
        Matcher matcher = pattern.matcher(keyword);
        // 创建一个列表，用于存储匹配到的结果
        List<String> list = new ArrayList<>();
        // 使用循环查找所有匹配项
        while (matcher.find()) {
            // 将匹配到的结果添加到列表中
            list.add(matcher.group());
        }
        // 返回存储匹配结果的列表
        return list;
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
        String deviceVersion = null;
        Object deviceVersionObject = CustomConfigurationUtil.getValue("obtainPortNumber.keyword", Constant.getProfileInformation());
        if (deviceVersionObject instanceof String) {
            deviceVersion = (String) deviceVersionObject;
        }

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
}
