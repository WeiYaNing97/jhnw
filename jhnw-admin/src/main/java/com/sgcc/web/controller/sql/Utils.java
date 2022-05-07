package com.sgcc.web.controller.sql;

import com.sgcc.connect.translate.TranSlate;
import com.sgcc.sql.service.IProblemScanLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

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
    * @method: 判断是否为错误命令 或是否执行成功  简单判断
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
     * @Param: [remove_content, compare, content] //交换机版本号  比较
     * 如果 交换机版本号  比较方法   数据库版本号 则返回 true
     * @return: boolean
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("compareVersion")
    public static boolean compareVersion(String remove_content,String compare){

        String getParameters = compare;
        getParameters = getParameters.replace("<=",":");
        getParameters = getParameters.replace(">=",":");
        getParameters = getParameters.replace("!=",":");
        getParameters = getParameters.replace("<",":");
        getParameters = getParameters.replace(">",":");
        getParameters = getParameters.replace("=",":");
        String[] parameter = getParameters.split(":");

        String getComparisonNumber = compare;
        getComparisonNumber = getComparisonNumber.replace(parameter[0],":");
        getComparisonNumber = getComparisonNumber.replace(parameter[1],":");
        if (parameter.length == 3){
            getComparisonNumber = getComparisonNumber.replace(parameter[2],":");
        }
        getComparisonNumber = getComparisonNumber.substring(1,getComparisonNumber.length()-1);
        String[] comparisonNumber = getComparisonNumber.split(":");

        List<String[]> compareList = new ArrayList<>();
        if (comparisonNumber.length ==1){
            String[] compareArray = new String[3];
            compareArray[0] = parameter[0];
            compareArray[1] = comparisonNumber[0];
            compareArray[2] = parameter[1];
            compareList.add(compareArray);
        }else if (comparisonNumber.length ==2){

            String[] compareArray1 = new String[3];
            compareArray1[0] = parameter[0];
            compareArray1[1] = comparisonNumber[0];
            compareArray1[2] = parameter[1];
            compareList.add(compareArray1);

            String[] compareArray2 = new String[3];
            compareArray2[0] = parameter[1];
            compareArray2[1] = comparisonNumber[1];
            compareArray2[2] = parameter[2];
            compareList.add(compareArray2);

        }

        boolean compare_size = false;
        for (String[] compareArray:compareList){
            switch (compareArray[1]){
                case ">":
                    compare_size = compareVersionNumber(compareArray[0], compareArray[2]);
                    return compare_size;
                case "<":
                    compare_size = compareVersionNumber(compareArray[0], compareArray[2]);
                    return compare_size;
                case "=":
                    compare_size = compareArray[0].equals(compareArray[2]);
                    return compare_size;
                case ">=":
                    compare_size = compareVersionNumber(compareArray[0], compareArray[2]) || compareArray[0].equals(compareArray[2]);
                    return compare_size;
                case "<=":
                    compare_size = compareVersionNumber(compareArray[0], compareArray[2]) || compareArray[0].equals(compareArray[2]);
                    return compare_size;
                case "!=":
                    compare_size = !(compareArray[0].equals(compareArray[2]));
                    return compare_size;
            }
        }

        return true;
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
     * @Param: [action提取方法 ：取词 取版本,
     * returnString 返回信息的一行,
     * matchContent 提取关键字,
     * integer 位置, length 长度WLs]
     * 提取方法 ：取词 取版本  返回信息的一行 提取关键字 位置 长度WLs
     * @return: java.lang.String
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public static String wordSelection(String returnString,String matchContent,Integer integer,String length){
        integer = integer - 1 ;
        // 获取 W、L、S
        String substring = length.substring(length.length() - 1, length.length());
        //获取取值长度
        int word_length = Integer.valueOf(length.substring(0, length.length() - 1)).intValue();
        String return_string = "";
        switch (substring){
            // 取词和取字符串
            case "w":
            case "W":
            case "s":
            case "S":
                String get_word = "";
                get_word = "";
                String returnString_string = " "+returnString+" ";
                if (returnString_string.indexOf(" "+matchContent+" ")!=-1){
                    String[] split_String = returnString.split(" "+matchContent+" ");
                    String[] split_w = split_String[1].split(" ");
                    //提取关键字后面的单词数组长度  应大于  提取关键字后面的取值位置 加 取词长度
                    if (split_w.length<integer.intValue()+word_length){
                        return null;
                    }
                    //取词位置
                    int number = integer.intValue();
                    for (int num = 0;num<word_length;num++){
                        get_word = split_w[number]+" ";
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
                takeLetters = "";
                //模糊匹配 位置
                int string_position=returnString.indexOf(matchContent);
                if (string_position!=-1){
                    int word_position = string_position+matchContent.length()+integer.intValue();
                    if (word_position+word_length>returnString.length()){
                        takeLetters = "";
                    }else {
                        takeLetters = returnString.substring(word_position, word_position + word_length);
                    }
                    return_string =takeLetters.trim();
                }
        }
        if (return_string.length()>0){
            return return_string;
        }else {
            return null;
        }
    }

    /**
     * @method: 匹配方法
     * @Param: [matchType  精确匹配  模糊匹配  不存在
     * returnString  交换机返回信息
     * matchString]  分析表 关键字 --- 匹配的信息
     * @return: boolean
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public static boolean matchAnalysis(String matchType,String returnString,String matchString){
        switch(matchType){
            case "精确匹配" :
                //先模糊匹配 看是否存在
                int indexPosition = returnString.indexOf(matchString);
                if (indexPosition!=-1){//模糊匹配

                    String frontPosition = " ";
                    String rearPosition =" ";

                    /*if (indexPosition>1||indexPosition==1){
                        frontPosition = returnString.charAt(indexPosition-1)+"";
                    }
                    if (indexPosition+matchString.length()<returnString.length()){
                        rearPosition = returnString.charAt(indexPosition+matchString.length())+"";
                    }

                    if (frontPosition.equals(" ") && rearPosition.equals(" ")){
                        return true;
                    }else {
                        return false;
                    }*/
                    if ((frontPosition+returnString+rearPosition).indexOf(frontPosition+matchString+rearPosition) != -1){
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
        // 因为登录信息 会另起一行 登录信息 会在行首
        String[] switchInformation_array = switchInformation.split("\r\n");
        //循环遍历 按行分析 是否存在登录信息
        for (int number=0;number<switchInformation_array.length;number++){

            String information = switchInformation_array[number];
            if (information!=null && !information.equals("")){
                String loginInformationAuthentication = loginInformationAuthentication(switchInformation_array[number]);
                switchInformation_array[number] = loginInformationAuthentication.trim();
            }
        }
        //因为 之前 按行分割了
        //返回字符串
        StringBuilder stringBuilder = new StringBuilder();
        for (int number=0;number<switchInformation_array.length;number++){
            stringBuilder.append(switchInformation_array[number]);
            stringBuilder.append("\r\n");
        }
        return stringBuilder.toString();
    }

    /**
    * @method: 鉴别返回信息是否包含 登录信息  有则去除  并且 返回登录信息后信息
    * @Param: [switchInformation]
    * @return: java.lang.String
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    public static String loginInformationAuthentication(String switchInformation){
        //交换机返回信息 按行分割为 字符串数组
        switchInformation = switchInformation.trim();
        //因为登录信息 会另起一行 所以 登录信息 会是 % 开头
        String iInformation_substring = switchInformation.substring(0, 1);
        /*判断是否是首字母是% 或者包含 %
        判断是否包含 SHELL
        判断是否包含 /LOGIN /LOGOUT*/

        //判断是否是首字母是% 或者包含 %
        if (iInformation_substring.equalsIgnoreCase("%") || switchInformation.indexOf("%")!=-1 ){
            //判断是否包含 SHELL
            //判断是否包含 /LOGIN /LOGOUT
            int Include_SHELL = switchInformation.indexOf("SHELL");
            if (Include_SHELL !=- 1 &&
                    ( switchInformation.indexOf("LOGIN") != -1 ||  switchInformation.indexOf("LOGOUT") != -1)){

                //确认存在登录信息
                //%Apr  4 03:00:49:885 2000 H3C SHELL/5/LOGIN:- 1 - admin(192.168.1.98) in unit1 login
                //%Apr  4 03:04:03:302 2000 H3C SHELL/5/LOGOUT:- 1 - admin(192.168.1.98) in unit1 logout
                //%Apr  4 03:06:17:306 2000 H3C SHELL/5/LOGOUT:interface Ethernet1/0/2
                //%Apr  4 03:06:17:306 2000 H3C SHELL/5/LOGOUT:interface Ethernet1/0/2
                String[] login_return_Information = new String[2];
                /*存在 in unit  logout || in unit  login 删除多*/
                if (switchInformation.indexOf("in unit")!=-1 &&
                        (switchInformation.indexOf("logout")!=-1 || switchInformation.indexOf("login")!=-1 )){


                    //存在 in unit  logout || in unit  login 删除多
                    //%Apr  4 03:00:49:885 2000 H3C SHELL/5/LOGIN:- 1 - admin(192.168.1.98) in unit1 login
                    //%Apr  4 03:04:03:302 2000 H3C SHELL/5/LOGOUT:- 1 - admin(192.168.1.98) in unit1 logout
                    if (switchInformation.indexOf("logout")!=-1){
                        //%Apr  4 03:04:03:302 2000 H3C SHELL/5/LOGOUT:- 1 - admin(192.168.1.98) in unit1 logout

                        /*根据 ：logout分割 则 需要去除 前面一部分*/
                        String[] switchInformation_logouts = switchInformation.split("logout");
                        //switchInformation_logouts[0] : %Apr  4 03:04:03:302 2000 H3C SHELL/5/LOGOUT:- 1 - admin(192.168.1.98) in unit1
                        login_return_Information[0] = switchInformation_logouts[0] +"logout";
                        //login_return_Information[0] :  %Apr  4 03:04:03:302 2000 H3C SHELL/5/LOGOUT:- 1 - admin(192.168.1.98) in unit1 logout
                        if (switchInformation_logouts.length>1){
                            login_return_Information[1] = switchInformation_logouts[1];   //switchInformation_logouts[1] 是 in unit1 logout 后面的信息 一直到下一个  \r\n
                        }else {
                            login_return_Information[1] = "";
                        }
                    }else if (switchInformation.indexOf("login")!=-1){
                        //%Apr  4 03:00:49:885 2000 H3C SHELL/5/LOGIN:- 1 - admin(192.168.1.98) in unit1 login

                        /*根据 ：login分割 则 需要去除 前面一部分*/
                        String[] switchInformation_logins = switchInformation.split("login");
                        //switchInformation_logouts[0]  :  %Apr  4 03:00:49:885 2000 H3C SHELL/5/LOGIN:- 1 - admin(192.168.1.98) in unit1
                        login_return_Information[0] = switchInformation_logins[0] +"login";
                        //login_return_Information[0]  :   %Apr  4 03:00:49:885 2000 H3C SHELL/5/LOGIN:- 1 - admin(192.168.1.98) in unit1 login
                        if (switchInformation_logins.length>1){
                            login_return_Information[1] = switchInformation_logins[1]; //switchInformation_logouts[1] 是 in unit1 login 后面的信息 一直到下一个  \r\n
                        }else {
                            login_return_Information[1] = "";
                        }
                    }
                    //不存在 logout || login 删除少
                    //%Apr  4 03:06:17:306 2000 H3C SHELL/5/LOGOUT:
                    //%Apr  4 03:06:17:306 2000 H3C SHELL/5/LOGOUT:
                }else {
                    if (switchInformation.indexOf("LOGIN")!=-1){
                        //根据   LOGIN:   分割
                        String[] switchInformation_logouts = switchInformation.split("LOGIN:");
                        //switchInformation_logouts[0]   :   %Apr  4 03:00:49:885 2000 H3C SHELL/5/
                        login_return_Information[0] = switchInformation_logouts[0] +"LOGIN:";
                        //login_return_Information[0]   :    %Apr  4 03:00:49:885 2000 H3C SHELL/5/LOGIN:
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
                //登录信息翻译
                TranSlate.tranSlate(login_Information);

                //登录信息 后面信息  需要返回
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
    public static String readFileContent(String returnInformationFileName) {

        try {
            File f = new File(".");
            String absolutePath = f.getAbsolutePath();
            absolutePath = absolutePath.substring(0, absolutePath.length() - 1);
            absolutePath = absolutePath+"jhnw-connect\\src\\main\\java\\com\\sgcc\\connect\\txt\\"+returnInformationFileName+".txt";
            File file = new File(absolutePath);

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
    @RequestMapping("/fileCreationWrite")
    public static void fileCreationWrite(String returnInformationFileName,String returnString){
        //获取项目当前路径
        File f = new File(".");
        String absolutePath = f.getAbsolutePath();
        absolutePath = absolutePath.substring(0, absolutePath.length() - 1);
        //编辑文件存储位置
        absolutePath = absolutePath+"jhnw-connect\\src\\main\\java\\com\\sgcc\\connect\\txt\\"+returnInformationFileName+".txt";
        File file = new File(absolutePath);
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