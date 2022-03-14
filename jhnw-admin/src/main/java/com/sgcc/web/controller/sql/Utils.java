package com.sgcc.web.controller.sql;

import com.sgcc.connect.translate.TranSlate;
import com.sgcc.sql.service.IProblemScanLogicService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2021年12月17日 10:06
 */
public class Utils {

    @Autowired
    private IProblemScanLogicService problemScanLogicService;

    /**
     * @method: 修整字符串
     * @Param: [resultString] 交换机返回信息
     * @E-mail: WeiYaNing97@163.com
     *
     * 去除多余 "\r\n" 连续空格
     */
    public static String trimString(String resultString){
        resultString = resultString.replace("\r\n\r\n"," "+"\r\n"+" ");
        resultString = resultString.replace(" \r\n \r\n"," "+"\r\n"+" ");
        resultString = resultString.replace("\r\n"," "+"\r\n"+" ");
        resultString = resultString.replace("\r\n\r\n"," "+"\r\n"+" ");
        resultString = resultString.replace(" \r\n \r\n"," "+"\r\n"+" ");
        resultString = resultString.replace("\r\n"," "+"\r\n"+" ");
        resultString = repaceWhiteSapce(resultString);
        resultString = resultString.trim();
        return resultString;
    }

    /**
     * @method: 多个连续空格 改为 多个单空格
     * @Param: [original]
     * @return: java.lang.String
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
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
    * @method: 判断是否为错误命令 或是否执行成功
    * @Param: [str] 交换机返回信息
    * @return: boolean  判断命令是否错误 错误为false 正确为true
    * @E-mail: WeiYaNing97@163.com
    */
    public static boolean judgmentError(String str){
        List<String> list = new ArrayList<>();
        list.add("% Unrecognized command");
        list.add("% Ambiguous command");
        list.add("% Incomplete command");
        list.add("不是内部或外部命令");
        list.add("不是可运行的程序或批处理文件");
        for (String string:list){
            if (str.indexOf(string) != -1){
                return false;
            }
        }
        return true;
    }

    /**
     * @method: 比较版本号
     * @Param: [remove_content, compare, content] //交换机版本号  比较方法   数据库版本号
     * 如果 交换机版本号  比较方法   数据库版本号 则返回 true
     * @return: boolean
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public static boolean compareVersion(String remove_content,String compare,String content){
        boolean compare_size = false;
        switch (compare){
            case ">":
                compare_size = compareVersionNumber(remove_content, content);
                return compare_size;
            case "<":
                compare_size = compareVersionNumber(content, remove_content);
                return compare_size;
            case "=":
                compare_size = remove_content.equals(content);
                return compare_size;
            case ">=":
                compare_size = compareVersionNumber(remove_content, content) || remove_content.equals(content);
                return compare_size;
            case "<=":
                compare_size = compareVersionNumber(content, remove_content) || remove_content.equals(content);
                return compare_size;
            case "!=":
                compare_size = !(remove_content.equals(content));
                return compare_size;
        }
        return compare_size;
    }

    /**
     * @method: 比较系统版本号大小
     * 如果 str1 > str2 返回 true
     * 如果 str1 < str2 返回 false
     * @Param: [str1, str2]
     * @return: boolean
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public static boolean compareVersionNumber(String str1,String str2){
        String[] split1 = str1.split("\\.");
        String[] split2 = str2.split("\\.");
        int j;
        if (split1.length < split2.length){
            j=split1.length;
        }else if (split2.length < split1.length){
            j=split2.length;
        }else{
            j=split1.length;
        }
        for (int i=0;i<j;i++){
            int i1 = Integer.valueOf(split1[i]).intValue();
            int i2 = Integer.valueOf(split2[i]).intValue();
            if (i1>i2){
                return true;
            }else if (i1<i2){
                return false;
            }
        }

        if (split1.length < split2.length){
            return false;
        }else if (split2.length < split1.length){
            return true;
        }

        return false;
    }


    /**
     * @method: 取词
     *         //取词方法
     * @Param: [action, returnString, matchContent, integer, length]
     * 提取方法 ：取词 取版本  返回信息的一行 提取关键字 位置 长度WLs
     * @return: java.lang.String
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public static String wordSelection(String action,String returnString,String matchContent,Integer integer,String length){
        switch (action){
            case "取版本":
                return Global.firmwareVersion;
            case "取词":
                integer = integer - 1 ;
                String substring = length.substring(length.length() - 1, length.length());
                int word_length = Integer.valueOf(length.substring(0, length.length() - 1)).intValue();
                String return_string = "";
                switch (substring){
                    // 取词和取字符串
                    case "w":
                    case "W":
                    case "s":
                    case "S":
                        String get_word="";
                        get_word="";
                        String returnString_string = " "+returnString+" ";
                        if (returnString_string.indexOf(" "+matchContent+" ")!=-1){
                            String[] split_String = returnString.split(" "+matchContent+" ");
                            String[] split_w = split_String[1].split(" ");
                            if (split_w.length<integer.intValue()+word_length){
                                return null;
                            }
                            int number = integer.intValue();
                            for (int num = 0;num<word_length;num++){
                                get_word += split_w[number]+" ";
                                number++;
                                return_string += get_word.trim();
                            }
                        }
                        if (return_string.length()>0){
                            return return_string;
                        }else {
                            return null;
                        }
                        // 取字母
                    case "l":
                    case "L":
                        String takeLetters = "" ;
                        int nol=0;
                        takeLetters = "";
                        int string_position=returnString.indexOf(matchContent);
                        if (string_position!=-1){
                            int word_position = string_position+matchContent.length()+integer.intValue();
                            if (word_position+word_length>returnString.length()){
                                takeLetters = "";
                            }else {
                                takeLetters = returnString.substring(word_position, word_position + word_length);
                            }
                            return_string +=nol +":"+takeLetters.trim()+",";
                        }
                }
                if (return_string.length()>0){
                    return return_string.substring(0,return_string.length()-1);
                }else {
                    return null;
                }
        }
        return null;
    }

    /**
     * @method: 匹配
     * @Param: [matchType, returnString, matchString]
     * @return: boolean
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public static boolean matchAnalysis(String matchType,String returnString,String matchString){
        switch(matchType){
            case "精确匹配" :
                int indexPosition = returnString.indexOf(matchString);
                if (indexPosition!=-1){//模糊匹配
                    String frontPosition = " ";
                    String rearPosition =" ";
                    if (indexPosition>1||indexPosition==1){
                        frontPosition = returnString.charAt(indexPosition-1)+"";
                    }
                    if (indexPosition+matchString.length()<returnString.length()){
                        rearPosition = returnString.charAt(indexPosition+matchString.length())+"";
                    }

                    if (frontPosition.equals(" ") && rearPosition.equals(" ")){
                        return true;
                    }else {
                        return false;
                    }
                }else {
                    return false;
                }
            case "模糊匹配" :
                if (returnString.indexOf(matchString)!=-1){
                    return true;
                }else {
                    return false;
                }
            case "不存在" :
                if (returnString.indexOf(matchString)!=-1){
                    return false;
                }else {
                    return true;
                }
            default :
                return false;
        }
    }

    public static String getCurrentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间

        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记

        Date date = new Date();// 获取当前时间
        return sdf.format(date);// 输出已经格式化的现在时间(24小时制)
    }

    /**
     获取精确到毫秒的时间戳
     * @param date
     * @return
     **/
    public static Long getTimestamp(Date date){
        if (null == date) {
            return (long) 0;
        }
        String timestamp = String.valueOf(date.getTime());
        return Long.valueOf(timestamp);
    }

    /**
    * @method: 去除登录信息
    * @Param: [switchInformation]
    * @return: java.lang.String
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    public static String removeLoginInformation(String switchInformation){
        //交换机返回信息 按行分割为 字符串数组
        String[] switchInformation_array = switchInformation.split("\r\n");

        for (int number=0;number<switchInformation_array.length;number++){
            String information = switchInformation_array[number];
            if (information!=null && !information.equals("")){
                String loginInformationAuthentication = loginInformationAuthentication(switchInformation_array[number]);
                switchInformation_array[number] = loginInformationAuthentication.trim();
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int number=0;number<switchInformation_array.length;number++){
            stringBuilder.append(switchInformation_array[number]);
            stringBuilder.append("\r\n");
        }
        return stringBuilder.toString();
    }

    /**
    * @method: 鉴别返回信息
    * @Param: [switchInformation]
    * @return: java.lang.String
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    public static String loginInformationAuthentication(String switchInformation){
        //交换机返回信息 按行分割为 字符串数组
        switchInformation = switchInformation.trim();
        String iInformation_substring = switchInformation.substring(0, 1);
        //判断是否是首字母是%
        //判断是否包含 SHELL
        //判断是否包含 /LOGIN /LOGOUT
        if (iInformation_substring.equalsIgnoreCase("%")){
            int Include_SHELL = switchInformation.indexOf("SHELL");

            if (Include_SHELL !=- 1 &&
                    ( switchInformation.indexOf("LOGIN") != -1 ||  switchInformation.indexOf("LOGOUT") != -1)){

                //确认存在登录信息
                //%Apr  4 03:00:49:885 2000 H3C SHELL/5/LOGIN:- 1 - admin(192.168.1.98) in unit1 login
                //%Apr  4 03:04:03:302 2000 H3C SHELL/5/LOGOUT:- 1 - admin(192.168.1.98) in unit1 logout
                //%Apr  4 03:06:17:306 2000 H3C SHELL/5/LOGOUT:interface Ethernet1/0/2
                //%Apr  4 03:06:17:306 2000 H3C SHELL/5/LOGOUT:interface Ethernet1/0/2
                String[] login_return_Information = new String[2];
                //存在 logout || login 删除多
                if (switchInformation.indexOf("in unit")!=-1 &&
                        (switchInformation.indexOf("logout")!=-1 || switchInformation.indexOf("login")!=-1 )){

                    if (switchInformation.indexOf("logout")!=-1){
                        String[] switchInformation_logouts = switchInformation.split("logout");
                        login_return_Information[0] = switchInformation_logouts[0] +"logout";
                        if (switchInformation_logouts.length>1){
                            login_return_Information[1] = switchInformation_logouts[1];
                        }else {
                            login_return_Information[1] = "";
                        }
                    }else if (switchInformation.indexOf("login")!=-1){
                        String[] switchInformation_logouts = switchInformation.split("login");
                        login_return_Information[0] = switchInformation_logouts[0] +"login";
                        if (switchInformation_logouts.length>1){
                            login_return_Information[1] = switchInformation_logouts[1];
                        }else {
                            login_return_Information[1] = "";
                        }
                    }
                    //不存在 logout || login 删除少
                }else {
                    if (switchInformation.indexOf("LOGIN")!=-1){
                        String[] switchInformation_logouts = switchInformation.split("LOGIN:");
                        login_return_Information[0] = switchInformation_logouts[0] +"LOGIN:";
                        if (switchInformation_logouts.length>1){
                            login_return_Information[1] = switchInformation_logouts[1];
                        }else {
                            login_return_Information[1] = "";
                        }
                    }else if (switchInformation.indexOf("LOGOUT")!=-1){
                        String[] switchInformation_logouts = switchInformation.split("LOGOUT:");
                        login_return_Information[0] = switchInformation_logouts[0] +"LOGOUT:";
                        if (switchInformation_logouts.length>1){
                            login_return_Information[1] = switchInformation_logouts[1];
                        }else {
                            login_return_Information[1] = "";
                        }
                    }
                }
                //登录信息
                String login_Information= login_return_Information[0];

                TranSlate.tranSlate(login_Information);

                String return_Information= login_return_Information[1];
                if (return_Information !=null || !return_Information.equals("")){
                    return return_Information;
                }
                return "";
            }
        }

        return switchInformation;
    }



    /**
    * @method: 读取字符串
    * @Param: [file]
    * @return: java.lang.String
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    public static String readFileContent(File file) {

        try {

            Thread.sleep(3*1000);
            file.createNewFile();
            FileInputStream fileInput = null;
            fileInput = new FileInputStream(file);
            int index=-1;
            StringBuilder stringBuilder = new StringBuilder();
            while ((index = fileInput.read())!=-1) {
                stringBuilder.append((char)index);
            }
            //System.err.print(stringBuilder.toString());
            return stringBuilder.toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
    * @method: 写入文件
    * @Param: [returnInformationFileName, returnString]
    * @return: void
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    public void fileCreationWrite(String returnInformationFileName,String returnString){
        String lujing = "F:\\"+ returnInformationFileName +".txt";
        File file = new File(lujing);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(returnString);
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}