package com.sgcc.web.controller.sql;

import com.sgcc.sql.domain.SwitchError;
import com.sgcc.sql.domain.SwitchFailure;

import org.springframework.web.bind.annotation.RequestMapping;
import java.util.*;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2021年12月17日 10:06
 */
public class Utils {

    /**
    * @method: 进度条
    * @Param: [总数, 完成数]
    * @return: java.lang.String
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    public static String progressBar(double number1,double number2) {
        return (int)(number2/number1*100)+"%";
    }

    /**
     *
     * TODO 修整字符串 去除多余 "\r\n" 连续空格
     *
     * a
     *
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
     * TODO 判断是否为错误命令
     *
     * a
     *
    * @method: 判断是否为错误命令 或是否执行成功  简单判断
    * @Param: [str] 交换机返回信息
    * @return: boolean  判断命令是否错误 错误为false 正确为true
    * @E-mail: WeiYaNing97@163.com
    */
    public static boolean judgmentError(Map<String,String> user_String,String str){

        String deviceBrand = user_String.get("deviceBrand");
        String deviceModel = user_String.get("deviceModel");
        String firmwareVersion = user_String.get("firmwareVersion");
        String subversionNumber = user_String.get("subversionNumber");

        SwitchError switchError = new SwitchError();
        switchError.setBrand(deviceBrand);
        switchError.setSwitchType(deviceModel);
        switchError.setFirewareVersion(firmwareVersion);
        switchError.setSubVersion(subversionNumber);

        SwitchErrorController switchErrorController = new SwitchErrorController();
        List<SwitchError> switchErrors = switchErrorController.selectSwitchErrorListByPojo(switchError);

        for (SwitchError pojo:switchErrors){
            if (str.indexOf(pojo.getErrorKeyword()) != -1){
                return false;
            }
        }
        return true;
    }


    /**
     * todo 判断是否故障
     *
     * a
     *
     *
     * @method: 判断是否故障
     * @Param: [str] 交换机返回信息
     * @return: boolean  判断命令是否故障 故障为false 正常为true
     * @E-mail: WeiYaNing97@163.com
     */
    /*
    #Apr  3 06:17:59:728 2000 H3C DEV/2/FAN STATE CHANGE TO FAILURE:- 1 -
    Trap 1.3.6.1.4.1.2011.2.23.1.12.1.6: fan ID is 1

    %Apr  3 06:17:59:729 2000 H3C DEV/5/DEV_LOG:- 1 -
    Fan 1 failed
     */
    public static boolean switchfailure(Map<String,String> user_String,String switchInformation){

        String deviceBrand = user_String.get("deviceBrand");
        String deviceModel = user_String.get("deviceModel");
        String firmwareVersion = user_String.get("firmwareVersion");
        String subversionNumber = user_String.get("subversionNumber");

        SwitchFailure switchFailure = new SwitchFailure();
        switchFailure.setBrand(deviceBrand);
        switchFailure.setSwitchType(deviceModel);
        switchFailure.setFirewareVersion(firmwareVersion);
        switchFailure.setSubVersion(subversionNumber);

        SwitchFailureController switchFailureController = new SwitchFailureController();
        List<SwitchFailure> switchFailures = switchFailureController.selectSwitchFailureListByPojo(switchFailure);

        for (SwitchFailure pojo:switchFailures){
            //包含 返回 false
            if (switchInformation.indexOf(pojo.getFailureKeyword()) !=-1){
                return false;
            }
        }
        return true;
    }




    /**
     * @method: 比较
     * @Param: [remove_content, compare, content] //交换机版本号  比较
     * 如果 交换机版本号  比较方法   数据库版本号 则返回 true
     * @return: boolean
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("compareVersion")
    public static boolean compareVersion(Map<String,String> user_String, String compare,String current_Round_Extraction_String){

        String[] current_Round_Extraction_split = current_Round_Extraction_String.split("=:=");
        Map<String,String> value_String = new HashMap<>();
        if(!(current_Round_Extraction_String.equals(""))){
            for (int number = 0 ; number<current_Round_Extraction_split.length ; number = number +3){

                value_String.put(current_Round_Extraction_split[number],current_Round_Extraction_split[number+2]);

            }
        }

        //由 汉字 转化为 单词
        if (compare.indexOf("品牌")!=-1){
            compare = compare.replace("品牌",user_String.get("deviceBrand"));
        }else if (compare.indexOf("型号")!=-1){
            compare = compare.replace("型号",user_String.get("deviceModel"));
        }else if (compare.indexOf("固件版本")!=-1){
            compare = compare.replace("固件版本",user_String.get("firmwareVersion"));
        }else if (compare.indexOf("子版本")!=-1){
            compare = compare.replace("子版本",user_String.get("subversionNumber"));
        }

        // 获取比较参数
        String getParameters = compare;
        getParameters = getParameters.replace("<=",":");
        getParameters = getParameters.replace(">=",":");
        getParameters = getParameters.replace("!=",":");
        getParameters = getParameters.replace("<",":");
        getParameters = getParameters.replace(">",":");
        getParameters = getParameters.replace("=",":");
        String[] parameter = getParameters.split(":");

        String getComparisonNumber = compare;
        //参数一 替换 ：
        getComparisonNumber = getComparisonNumber.replace(parameter[0],":");
        //参数二 替换 ：
        getComparisonNumber = getComparisonNumber.replace(parameter[1],":");
        //如果参数数组长度为3 则有三个参数
        if (parameter.length == 3){
            //参数三 替换 ：
            getComparisonNumber = getComparisonNumber.replace(parameter[2],":");
        }
        //假设有三个参数 ： 5.20.99<固件版本<5.20.100   5.20.98<5.20.99<5.20.100
        //替换 “:”后 ： :<:<:
        //截取后 去掉了前后 得到：     <:<
        getComparisonNumber = getComparisonNumber.substring(1,getComparisonNumber.length()-1);
        //comparisonNumber : [<,<]
        String[] comparisonNumber = getComparisonNumber.split(":");

        List<String[]> compareList = new ArrayList<>();
        //comparisonNumber.length ==1  有 一个比较
        if (comparisonNumber.length ==1){
            String[] compareArray = new String[3];

            String value1 = value_String.get(parameter[0]);
            String value2 = value_String.get(parameter[1]);
            if (value1 != null){
                parameter[0] = value1;
            }
            if (value2 != null){
                parameter[1] = value2;
            }

            compareArray[0] = parameter[0];
            compareArray[1] = comparisonNumber[0];
            compareArray[2] = parameter[1];
            compareList.add(compareArray);

            //comparisonNumber.length == 2  有 两个比较
        }else if (comparisonNumber.length ==2){
            String value0 = value_String.get(parameter[0]);
            String value1 = value_String.get(parameter[1]);
            String value2 = value_String.get(parameter[2]);
            if (value0 != null){
                parameter[0] = value0;
            }
            if (value1 != null){
                parameter[1] = value1;
            }
            if (value2 != null){
                parameter[2] = value2;
            }


            //第一组比较
            String[] compareArray1 = new String[3];
            compareArray1[0] = parameter[0];
            compareArray1[1] = comparisonNumber[0];
            compareArray1[2] = parameter[1];
            compareList.add(compareArray1);
            //第二组比较
            String[] compareArray2 = new String[3];
            compareArray2[0] = parameter[1];
            compareArray2[1] = comparisonNumber[1];
            compareArray2[2] = parameter[2];
            compareList.add(compareArray2);

        }

        boolean compare_size;
        //循环 比较数组
        for (String[] compareArray:compareList){
            switch (compareArray[1]){
                case ">":
                    //如果 str1 > str2  返回 true
                    //如果 str1 < str2  返回 false
                    compare_size = compareVersionNumber(compareArray[0], compareArray[2]);
                    //如果 str1 < str2  返回 false
                    // !false 则会进入
                    if (!compare_size){
                        return false;
                    }
                    break;
                case "<":
                    //如果 str1 > str2  返回 true
                    //如果 str1 < str2  返回 false
                    compare_size = compareVersionNumber(compareArray[0], compareArray[2]);
                    //如果 str1 > str2  返回 true 则会进入
                    if (compare_size){
                        return false;
                    }
                    break;
                case "=":
                    //相等 为  true
                    compare_size = compareArray[0].equals(compareArray[2]);
                    //不相等 则 false
                    //!false 则会进入 返回 false
                    if (!compare_size){
                        return false;
                    }
                    break;
                case ">=":
                    //如果 str1 > str2  返回 true
                    //如果 str1 < str2  返回 false
                    //如果 str1 == str2  返回 true
                    compare_size = compareVersionNumber(compareArray[0], compareArray[2]) || compareArray[0].equals(compareArray[2]);
                    //如果 str1 < str2  返回 false
                    // !false  会进入
                    if (!compare_size){
                        return false;
                    }
                    break;
                case "<=":
                    //如果 str1 > str2  返回 false
                    //如果 str1 < str2  返回 true
                    //如果 str1 == str2  返回 true
                    compare_size = !(compareVersionNumber(compareArray[0], compareArray[2])) || compareArray[0].equals(compareArray[2]);
                    if (!compare_size){
                        return false;
                    }
                    break;
                case "!=":
                    //如果 str1 == str2  返回 false
                    compare_size = !(compareArray[0].equals(compareArray[2]));
                    if (!compare_size){
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

    /**
     * @method: 比较系统版本号大小 参数一是否大于参数二
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
        //取得最短数组长度 只比较大版本
        if (split1.length < split2.length){
            j=split1.length;
        }else if (split2.length < split1.length){
            j=split2.length;
        }else{
            j=split1.length;
        }
        //比较大版本
        for (int i=0;i<j;i++){
            int i1 = Integer.valueOf(split1[i]).intValue();
            int i2 = Integer.valueOf(split2[i]).intValue();
            if (i1>i2){
                return true;
            }else if (i1<i2){
                return false;
            }
        }
        //大版本一致
        //比较长度 长度长的 是大版本
        if (split1.length < split2.length){
            return false;
        }else if (split2.length < split1.length){
            return true;
        }

        return false;
    }

    /**
     * TODO 取词逻辑
     *
     *
     * a
     *
     * @method: 取词 按位置取词
     * //取词方法
     * @Param: [     action提取方法 ：取词 取版本, returnString 返回信息的一行, matchContent 提取关键字, integer 位置, length 长度WLs]
     * 提取方法 ：取词 取版本  返回信息的一行 提取关键字 位置 长度WLs
     * @return: java.lang.String
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public static String wordSelection(String returnString,String matchContent,String relativePosition_line,int integer,String length){
        // 获取 W、L、S
        String substring = length.substring(length.length() - 1, length.length());//取词类型
        //获取取值长度
        int word_length = Integer.valueOf(length.substring(0, length.length() - 1)).intValue();//取词长度
        //预设返回值
        String return_string = "";

        returnString = repaceWhiteSapce(returnString); // 连续空格改为 1 个空格

        switch (substring){
            // 取词和取字符串
            case "w":
            case "W":
            case "s":
            case "S":
                //以matchContent 为参照  获取位置 因为后期转化为数组，关键词为 [0]
                String get_word = "";
                get_word = "";

                String returnString_trim = returnString.trim(); //交换机返回 信息 去除 前后空格

                String[] split_String = returnString_trim.split(" ");

                if (!(relativePosition_line.equals("0"))){
                    matchContent = "";
                }

                if (!(matchContent.equals(""))){
                    int num = 0;
                    for ( ; num <split_String.length ; num++){
                        if (split_String[num].equals(matchContent)) {
                            break;
                        }
                    }

                    integer = integer + num;
                }else if (matchContent.equals("")){
                    integer = integer - 1 ;
                }

                //提取关键字后面的单词数组长度  应大于  提取关键字后面的取值位置 加 取词长度  6
                if ((split_String.length - word_length)  <  integer){
                    return null;
                }

                //取词位置
                int number = integer;

                for (int num = 0;num<word_length;num++){
                    get_word = split_String[number]+" ";
                    number++;
                    return_string += get_word;
                }
                System.err.println("W："+return_string);
                if (return_string.length()>0){
                    return return_string.trim();
                }else {
                    return null;
                }

                //取字母
            case "l":
            case "L":
                String split = returnString.split(matchContent)[0];
                Integer splitInteger =split.length();
                int start = integer + splitInteger;
                if (start+word_length > returnString.length()){
                    return null;
                }
                return_string = returnString.substring(start, start+word_length);
                System.err.println("L："+return_string);

        }
        if (return_string.length()>0){
            return return_string.trim();
        }else {
            return null;
        }
    }

    /**
     * @method: 取词     (依靠关键词)
     *         //取词方法
     * @Param: [     action提取方法 ：取词 取版本, returnString 返回信息的一行, matchContent 提取关键字, integer 位置, length 长度WLs]
     * 提取方法 ：取词 取版本  返回信息的一行 提取关键字 位置 长度WLs
     * @return: java.lang.String
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public static String wordSelection1(String returnString,String matchContent,Integer integer,String length){
        // 获取 W、L、S
        String substring = length.substring(length.length() - 1, length.length());
        //获取取值长度
        int word_length = Integer.valueOf(length.substring(0, length.length() - 1)).intValue();
        //预设返回值
        String return_string = "";

        switch (substring){
            // 取词和取字符串
            case "w":
            case "W":
            case "s":
            case "S":
                //以matchContent 为参照  获取位置 因为后期转化为数组，关键词后第一位为 [0]
                integer = integer - 1 ;

                String get_word = "";
                get_word = "";
                String returnString_string = " "+returnString.trim()+" ";
                if (returnString_string.indexOf(" "+matchContent+" ")!=-1){
                    String[] split_String = returnString_string.split(" "+matchContent+" ");
                    String[] split_w = split_String[1].split(" ");
                    //提取关键字后面的单词数组长度  应大于  提取关键字后面的取值位置 加 取词长度
                    if (split_w.length<integer.intValue()+word_length){
                        return null;
                    }
                    //取词位置
                    int number = integer.intValue();
                    for (int num = 0;num<word_length;num++){
                        get_word = " "+split_w[number]+" ";
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
                    //string_position+matchContent.length() 取词关键字 最后一位字母位置
                    int num =string_position+matchContent.length();
                    int word_position = string_position+matchContent.length()+integer.intValue();
                    String removeKeywords = returnString.substring(word_position,returnString.length()).trim();
                    if (word_length>removeKeywords.length()){
                        takeLetters = "";
                    }else {
                        takeLetters = removeKeywords.substring(0,word_length);
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
     * todo 匹配方法
     *
     *
     * @method: 匹配方法
     * matched : 精确匹配  information_line_n：交换机返回信息行  matchContent：数据库 关键词
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
     * todo 去除登录信息
     *
     * a
     *
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
        if (switchInformation.length()<1){
            return switchInformation;
        }
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
                //TranSlate.tranSlate(login_Information);

                System.err.println("\r\n登录信息==\r\n"+login_Information);

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

    public static List<Date> sort(List<Date> dateList){
        dateList.sort((a1,a2) ->{
            return a1.compareTo(a2);
        });
        return dateList;
    }


    /*判断 字符串 最后一位 是否为 . 或者 ,*/
    public static String judgeResultWordSelection(String wordSelectionResult) {
        String last = wordSelectionResult.substring(wordSelectionResult.length() - 1, wordSelectionResult.length());
        String[] character = {",","."};
        for (String judgeChar:character){
            if (last.equals(judgeChar)){
                wordSelectionResult = wordSelectionResult.substring(0, wordSelectionResult.length()-1);
                break;
            }
        }
        return wordSelectionResult;
    }

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

    public static Integer filterAccurately(String value1,String value2) {
        boolean value1Boolean = value1.indexOf("*")!=-1;
        boolean value2Boolean = value2.indexOf("*")!=-1;
        int value1Length = value1.length();
        int value2Length = value2.length();
        if (value1Boolean && value2Boolean){
            /*如果两个都含有 * 取最长的*/
           if (value1Length<value2Length){
               return 2;
           }else if (value1Length>value2Length){
               return 1;
           }else if (value1Length == value2Length){
                return 0;
            }
        }else {
            /*两个 至少有一个没含有 * */
            if (value1Boolean || value2Boolean){
                /*有一个含有 * 返回 没有*的*/
                if (value1Boolean){
                    return 2;
                }
                if (value2Boolean){
                    return 1;
                }
            }
        }
        return 0;
    }


    public static void main(String[] args) {
        List<String> problemIdList = new ArrayList<>();
        problemIdList.add("1");
        problemIdList.add("12");
        problemIdList.add("13");
        problemIdList.add("14");
        problemIdList.add("15");
        problemIdList.add("16");

        String[] ids = problemIdList.toArray(new String[problemIdList.size()]);

        for (String id:ids){
            System.err.println(id);
        }
    }

}