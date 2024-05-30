package com.sgcc.share.util;


import ch.qos.logback.core.util.FileUtil;
import com.sgcc.common.config.RuoYiConfig;
import com.sgcc.share.domain.Constant;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @date 2022年08月02日 14:42
 */
public class MyUtils {
    public static String isEmpty = "Is_Empty";

    /* ============================== 汉字 开始 ==============================*/
    /**
    * @Description根据正则表达式判断字符是否为汉字 */
    public static boolean isContainChinese( String str) {
        String regex = "[\u4e00-\u9fa5]"; //汉字的Unicode取值范围
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(str);
        return match.find();
    }
    /* ============================== 汉字 结束 ==============================*/
    /* ============================== 字母 开始 ==============================*/
    /**
     * @Description该方法主要使用正则表达式来判断字符串中是否包含字母
     * @author fenggaopan 2015年7月21日 上午9:49:40
     * @param cardNum 待检验的原始卡号
     * @return 返回是否包含
     */
    public static boolean judgeContainsStr(String cardNum) {
        String regex=".*[a-zA-Z]+.*";
        Matcher m = Pattern.compile(regex).matcher(cardNum);
        return m.matches();
    }
    /**
     * 获取字符串开头字母部分
     * @param str
     * @return
     */
    public static String getFirstLetters(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isLetter(c)) {
                sb.append(c);
            } else {
                break;
            }
        }
        return sb.toString().trim();
    }
    /* ============================== 字母 结束 ==============================*/
    /* ============================== 数字 开始 ==============================*/
    /**
     * @Description查询字符串中首个数字出现的位置
     * @param str 查询的字符串
     * @return 若存在，返回位置索引，否则返回-1；
     */
    public static int findFirstIndexNumberOfStr(String str){
        int i = -1;
        Matcher matcher = Pattern.compile("[0-9]").matcher(str);
        if(matcher.find()) {
            i = matcher.start();
        }
        return i;
    }
    /**
     * @Description 判断字符串是否包含数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile(".*[0-9].*");
        return pattern.matcher(str).matches();
    }
    /**
     * @Description 判断字符串中是否包含数字
     * @author charles
     * @createTime 2023/12/22 8:45
     * @desc
     * @param source
     * @return
     */
    public static boolean containDigit(String source) {
        char ch;
        for(int i=0; i<source.length();i++){
            ch = source.charAt(i);
            if(ch >= '0' && ch <= '9') {
                return true;
            }
        }
        return false;
    }
    /**
     * @Description 判断字符串是否为全数字
     * @author charles
     * @createTime 2023/12/22 8:41
     * @desc
     * @param str
     * @return
     */
    public static boolean allIsNumeric(String str){
        //使用正则表达式判断字符串是否全由数字组成
        return str.matches("\\d+");
    }
    /**
     * 判断字符串集合元素是否都包含数字
     * @param stringList
     * @return
     */
    public static boolean thereAreNumbersInTheSet(List<String> stringList) {
        for (String string:stringList){
            /*如果不包含数字 则返回false */
            if (!containDigit(string)){
                return false;
            }
        }
        return true;
    }
    /* 判断字符串元素是否包含数字*/
    public static boolean thereAreNumbers(String string) {
        /*如果不包含数字 则返回false */
        return containDigit(string);
    }
    /**
     * 判断字符串元素是否为纯数字
     * @param str
     * @return
     */
    public static boolean determineWhetherAStringIsAPureNumber(String str) {
        if (str.matches("\\d+")) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 字符串截取 数字字符串
     * @return
     */
    public static String StringTruncationMatcherValue(String args) {
        Pattern pattern = Pattern.compile("[0-9]");
        Matcher matcher = pattern.matcher(args);
        String str = "";
        while (matcher.find()) {
            str += matcher.group();
        }
        return str;
    }
    /**
     * 要在Java中获取字符串开头的数字部分，可以使用正则表达式。 */
    public static String getNumberPartAtBeginning(String input) {
        Pattern pattern = Pattern.compile("^\\d+");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
    /**
     * 要在Java中获取字符串第一个数值数字部分，可以使用正则表达式。 */
    public static String getEncodingID(String input) {
        String[] split = input.split(",");
        String str = split[0];
        if (str.startsWith("\'") || str.startsWith("\"")){
            str = str.substring(1,str.length());
        }
        if (str.endsWith("\'") || str.endsWith("\"")){
            str = str.substring(0,str.length()-1);
        }
        return str;
    }
    /* ============================== 数字 结束 ==============================*/
    /* ============================== 实体类 开始 ==============================*/
    /**
     * @Description 实体类转化为 字符串
     * @param object
     * @return
     */
    public static String getEntityClassString(Object object) {
        //获取实体类 返回的是一个数组 数组的数据就是实体类中的字段
        Field[] fields = object.getClass().getDeclaredFields();
        String sumString = "";
        for (int i = 0; i < fields.length; i++) {
            //有的字段是用private修饰的 将他设置为可读
            fields[i].setAccessible(true);
            try {
                // 输出属性名和属性值
                String nameKey = fields[i].getName();
                Object value = fields[i].get(object);
                if (nameKey.equalsIgnoreCase("serialVersionUID")){
                    continue;
                }
                sumString = sumString  + nameKey + ":\"" + (value == null?isEmpty:value)+"\",";
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return sumString.substring(0,sumString.length()-1);
    }
    /* ============================== 实体类 结束 ==============================*/
    /* ============================== 字符串、文章 开始 ==============================*/
    /**
     * @Description 文章字段 换行符 由"\r"  "\n"  "\r\n" 规范成 "\r\n"   并且 连续多空格转化为单空格
     * @param article
     * @return
     */
    public static String trimString(String article) {
        // 读取文章并按行存入数组
        String[] lines = article.split("\\r?\\n|\\r"); // 使用正则表达式按照任意一种换行符进行分割
        /*字符串数组以\r\n拼接成字符串*/
        for (int num = 0 ;num<lines.length;num++){
            /*  为了保存 ^对应命令的位置 因此不能够进行 多空格转变为单空格  */
            if (lines[num].indexOf("^")!=-1 && lines[num].indexOf("^down") ==-1){
                continue;
            }
            lines[num] = repaceWhiteSapce(lines[num]);
        }
        return String.join("\r\n", lines);
    }
    /**
     * @Description 多个连续空格 改为 多个单空格
     * @author charles
     * @createTime 2023/12/22 8:44
     * @desc
     * @param original
     * @return
     */
    public static String repaceWhiteSapce(String original){
        StringBuilder sb = new StringBuilder();
        boolean isFirstSpace = false;//标记是否是第一个空格
        // original = original.trim();//如果考虑开头和结尾有空格的情形
        char c;
        for(int i = 0; i < original.length(); i++){
            c = original.charAt(i);
            if(c == ' ' || c == '\t')//遇到空格字符时,先判断是不是第一个空格字符
            {
                if(!isFirstSpace)
                {
                    sb.append(c);
                    isFirstSpace = true;
                }
            }
            else{//遇到非空格字符时
                sb.append(c);
                isFirstSpace = false;
            }
        }
        return sb.toString();
    }
    /**
     * @Description 多个连续的相同字符串，改为单个字符串
     * @desc
     * @param input	 原始字符串      例如："这是  一个  包含EEEEEE多个           连续空格  的字符串";
     * @param string 连续的相同字符串例如：“EEE”；
     * @return  修改后的字符串       例如：“这是  一个  包含EEE多个           连续空格  的字符串”；
     */
    public static String replaceMultipleStringsWithSingleString(String input,String string) {
        return input.replaceAll(string+"+", string);
    }
    /**
     * @Description   去掉字符串首尾的非数字非字母部分。
     * removeNonAlphanumeric方法使用正则表达式^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$
     * @author charles
     * @createTime 2023/12/22 8:48
     * @desc
     * @param str
     * @return
     */
    public static String removeNonAlphanumeric(String str) {
        return str.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");
    }
    /**
     * @Description 子字符串在父字符串中出现的所有位置
     * @author charles
     * @createTime 2023/12/21 19:20
     * @desc
     * @param parent
     * @param child
     * @return
     */
    public static List<Integer> getSubstringPositions(String parent, String child) {
        List<Integer> positions = new ArrayList<>();
        int index = parent.indexOf(child);
        while (index != -1) {
            positions.add(index);
            index = parent.indexOf(child, index + 1);
        }
        return positions;
    }
    /**
     * 根据字符串 根据字符串（忽略大小写）分割为字符串数组
     *
     * "(?i)"表示忽略大小写
     *
     * @param string  被分割字符串
     * @param str   分割关键词
     * @return
     */
    public static String[] splitIgnoreCase(String string,String str){

        return string.split("(?i)"+str);

    }
    /**
     * 判断一个字符串是否包含另一个字符串(忽略大小写)
     * @param primary
     * @param keywords
     * @return
     */
    public static boolean containIgnoreCase(String primary,String keywords) {
        return primary.toUpperCase().indexOf(keywords.toUpperCase())!=-1;
    }
    /* ============================== 字符串、文章 结束 ==============================*/
    /* ============================== 时间 开始 ==============================*/
    /**
     * @Description 获取时间 格式自定
     * @author charles
     * @createTime 2023/12/22 8:42
     *
     * @desc
     * Java时间设为二十四小时制和十二小时制的区别：
     *     1) 二十四小时制： “yyyy-MM-dd HH:mm:ss”
     *     2)十二小时制： “"yyyy-MM-dd hh:mm:ss"”
     *     例如：type  =  "yyyy-MM-dd hh:mm:ss"
     *
     * @return
     */
    public static String getDate(String type){
        String time = new SimpleDateFormat(type).format(new Date());
        return time;
    }
    /**
     * @Description 根据Date 获取时间 yyyy-MM-dd hh:mm:ss 格式字符串*/
    public static String getDatetoString(Date dateTime){
        String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dateTime);
        return time;
    }
    /**
     * @Description 根据yyyy-MM-dd hh:mm:ss 格式字符串 获取时间Date*/
    public static Date getStringtoData(String time){
        Date parse = null;
        try {
            parse = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(time);
            return parse;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }
    /**
     * @Description 时间排序 由大到小
     * @author charles
     * @createTime 2023/12/22 8:43
     * @desc
     * @param dateList
     * @return
     */
    public static List<Date> sortDate(List<Date> dateList){
        dateList.sort((a1,a2) ->{
            return a1.compareTo(a2);
        });
        return dateList;
    }
    /**
     * @Description 时间由大到小排序
     * @author charles
     * @createTime 2023/12/22 8:45
     * @desc
     * @param dateList
     * @return
     */
    public static List<Date> sort(List<Date> dateList){
        dateList.sort((a1,a2) ->{
            return a1.compareTo(a2);
        });
        return dateList;
    }
    /* ============================== 时间 结束 ==============================*/
    /* ============================== 数组 开始 ==============================*/
    /**
     * @Description 删除数组空文本返回新数组
     * @author charles
     * @createTime 2023/12/22 8:45
     * @desc
     * @param strArray
     * @return
     */
    public static String[] removeArrayEmptyTextBackNewArray(String[] strArray) {
        List<String> strList= Arrays.asList(strArray);
        List<String> strListNew=new ArrayList<>();
        for (int i = 0; i <strList.size(); i++) {
            if (strList.get(i)!=null&&!strList.get(i).equals("")){
                strListNew.add(strList.get(i));
            }
        }
        String[] strNewArray = strListNew.toArray(new String[strListNew.size()]);
        return   strNewArray;
    }
    /**
     * 数组去掉最后一个元素
     * 要去掉Java数组的最后一个元素，可以创建一个新的数组，并将原数组中除最后一个元素之外的所有元素复制到新数组中。
     * @param array
     * @return
     */
    public static String[] RemoveLastElement(String[] array) {
        // 原始数组 array;
        // 创建一个新数组，长度为原数组长度减1
        String[] newArray = new String[array.length - 1];
        // 将原数组中除最后一个元素外的所有元素复制到新数组中
        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = array[i];
        }
        return newArray;
    }
    /* ============================== 数组 结束 ==============================*/
    /* ============================== 集合 开始 ==============================*/
    /**
     * @Description  查看字符串集合A中存在，但字符串集合B中不存在的部分
     * @author charles
     * @createTime 2024/1/4 10:48
     * @desc
     * @param listA
     * @param listB
     * @return
     */
    public static List<String> findDifference(List<String> listA, List<String> listB) {
        HashSet<String> setA = new HashSet<>(listA);
        HashSet<String> setB = new HashSet<>(listB);
        setA.removeAll(setB);
        return new ArrayList<>(setA);
    }
    /**
     * 判断集合是否为空， 为空返回true
     * 集合判空
     * @param collection
     * @return
     */
    public static boolean isCollectionEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty() || collection.size() == 0;
    }
    /**
     * 判断Map集合是否为空， 为空返回true
     * @param map
     * @return
     */
    public static boolean isMapEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty() || map.size() == 0;
    }
    /* ============================== 集合 结束 ==============================*/
    /* ============================== Double 开始 ==============================*/
    /**
     * 字符串 转化为  Double
     * @param str
     * @return
     */
    public static double stringToDouble(String str) throws NumberFormatException {
        return Double.parseDouble(str);
    }
    /**
     * 字符串截取double值
     * @param input
     * @return
     */
    public static List<Double> StringTruncationDoubleValue(String input) {
        List<Double> doubleList = new ArrayList<>();
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            double value = Double.parseDouble(matcher.group());
            doubleList.add(value);
        }
        return doubleList;
    }
    /**
     * 判断 double值 是否在阈值内
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
    /* ============================== Double 结束 ==============================*/
    /* ============================== Integer 开始 ==============================*/
    /**
     * 字符串截取 Integer 值
     * @return
     */
    public static List<Integer> extractInts(String str) {
        List<Integer> intList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            intList.add(Integer.parseInt(matcher.group()));
        }
        return intList;
    }
    /* ============================== Integer 结束 ==============================*/
    /* ============================== IP 开始 ==============================*/
    /**
     * @Description 判断内容是否存在 IP特征
     * @desc
     * @param input
     * @return
     */
    public static boolean containsIPAddress(String input) {
        String ipPattern = "(\\d{1,3}\\.){3}\\d{1,3}"; // IP地址的正则表达式
        Pattern pattern = Pattern.compile(ipPattern);
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }
    /* ============================== IP 结束 ==============================*/





    public static String getID(String problemCode,String id) {
        String regionalCode = (CustomConfigurationUtil.getValue("configuration.regionalCode", Constant.getProfileInformation())).toString();

        switch (regionalCode.length()){
            case 1:
                regionalCode ="000"+regionalCode;
                break;
            case 2:
                regionalCode ="00"+regionalCode;
                break;
            case 3:
                regionalCode ="0"+regionalCode;
                break;
        }

        String re = "";
        if (id == null){
            re = problemCode + regionalCode +System.currentTimeMillis();
        }else {
            re = problemCode + regionalCode + id;
        }
        return re;
    }

    /** 判断Id 是否为 */
    public static boolean encodeID(String id) {
        if (id.length() != 21){
            return false;
        }else {
            return true;
        }

        /*自定义分隔符*/
        /*Map<String, Object> customDelimiter = (Map<String, Object>) CustomConfigurationUtil.getValue("configuration.problemCode", Constant.getProfileInformation());
        Collection<Object> customDelimitervalues = customDelimiter.values();
        List<String> values = customDelimitervalues.stream().map(Object::toString).collect(Collectors.toList());

        for (String value:values){
            if (id.startsWith( value )){
                return true;
            }
        }
        return false;*/
    }




    /**
     * 获取相对路径
     * @param filePath  文件名称
     * @return
     * @throws URISyntaxException
     */
    public static String getRelativePath(String filePath) throws URISyntaxException {
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        URL url = classLoader.getResource("");
        File file = new File(url.toURI());
        return file.getAbsolutePath() + "/" + filePath;
    }
    public static String getProjectPath() {
        String projectPath = System.getProperty("user.dir");

        /*开发环境：E:\ideaProject\jhnw*/
        /*部署环境：E:\ideaProject\jhnw\jhnw-admin\target*/
        /*if (projectPath.endsWith("\\jhnw")){
            projectPath = projectPath.substring(0,projectPath.length() - ("\\jhnw".length()) );
        }*/
        return projectPath;
    }
    public static String fileWrite(List<String> documentContent,String fileName) {
        String absoluteFile = getAbsoluteFile(fileName + ".txt");
        try {
            /* 写入Txt文件 */
            //生成的文件在工程的根目录下面
            File writename = new File(absoluteFile); // 相对路径，如果没有则要建立一个新的output.txt文件
            writename.createNewFile(); // 创建新文件
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            for (String pojo:documentContent){
                out.write(pojo+"\r\n"); // \r\n即为换行
            }
            out.flush(); // 把缓存区内容压入文件
            out.close(); // 最后记得关闭文件
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName +".txt";
    }
    /**
     * @Description 获取下载路径
     * @author charles
     * @createTime 2023/12/22 8:41
     * @desc
     * @param filename
     * @return
     */
    public static String getAbsoluteFile(String filename) {
        /*只适用于RuoYi  此为 配置文件配置信息*/
        String downloadPath = RuoYiConfig.getDownloadPath() + filename;
        File desc = new File(downloadPath);
        if (!desc.getParentFile().exists())
        {
            desc.getParentFile().mkdirs();
        }
        return downloadPath;
    }
    /**
     * @Description 进度条
     * @author charles
     * @createTime 2023/12/22 8:44
     * @desc
     * @param number1
     * @param number2
     * @return
     */
    public static String progressBar(double number1,double number2) {
        return (int)(number2/number1*100)+"%";
    }
    /**
     * @Description 字符串调整方法，当字符串中存在":"，且":"的前一字符不为" ",下一字符为" "时，则将":"替换成" :"。
     * @author charles
     * @createTime 2023/12/20 15:55
     * @desc
     * @param input
     * @return
     */
    public static String adjustColon(String input) {
        /*Pattern pattern = Pattern.compile("(?<! ):");
        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll(" :");*/
        return input.replaceAll("(?<=\\S):(?=\\s)|(?<=\\s):(?=\\S)", " : ").replaceAll("\\s+"," ").trim();
    }
    /**
     * 去除字符串尾部非字符串部分
     * @param str
     * @return
     */
    public static String removeNonNumericSuffix(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        int index = str.length() - 1;
        while (index >= 0 && !Character.isDigit(str.charAt(index))) {
            index--;
        }
        return str.substring(0, index + 1);
    }




}