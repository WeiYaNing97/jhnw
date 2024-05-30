package com.sgcc.share.util;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.controller.SwitchErrorController;
import com.sgcc.share.controller.SwitchFailureController;
import com.sgcc.share.domain.*;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.service.IReturnRecordService;
import com.sgcc.share.service.ISwitchInformationService;
import com.sgcc.share.webSocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.*;

/**
 * 功能方法类
 */
public class FunctionalMethods {

    @Autowired
    private static ISwitchInformationService switchInformationService;
    /*获取交换机基本信息 有返回ID 没有插入并返回ID*/
    public static Long getSwitchParametersId(SwitchParameters switchParameters) {
        /**交换机四项基本信息对象*/
        SwitchInformation switchInformation = new SwitchInformation();
        /*四项基本信息*/
        switchInformation.setBrand(switchParameters.getDeviceBrand());
        switchInformation.setSwitchType(switchParameters.getDeviceModel());
        switchInformation.setFirewareVersion(switchParameters.getFirmwareVersion());
        /*当子版本为空时，字段赋值为 null
        * 考虑到 如果置空<null> 的话， 会查出大量前三个信息相同的数据来
        * 例如
        * 1： H3C S2152 5.20.99 1600
        * 2： H3C S2152 5.20.99 <null>*/
        switchInformation.setSubVersion(switchParameters.getSubversionNumber() == null?"null":switchParameters.getSubversionNumber());

        switchInformationService = SpringBeanUtil.getBean(ISwitchInformationService.class);//解决 多线程 service 为null问题
        List<SwitchInformation> switchInformationList = switchInformationService.selectSwitchInformationList(switchInformation);

        if (MyUtils.isCollectionEmpty(switchInformationList)){

            int i = switchInformationService.insertSwitchInformation(switchInformation);
            if (i>0){
                return switchInformation.getId();
            }
            return Long.valueOf(i).longValue();

        }else {
            return switchInformationList.get(0).getId();
        }

    }



    /**
     * 判断是否为错误命令
     * @method: 判断是否为错误命令 或是否执行成功  简单判断
     * @Param: [str] 交换机返回信息
     * @return: boolean  判断命令是否错误 错误为false 正确为true
     */
    public static boolean judgmentError(SwitchParameters switchParameters, String str){
        SwitchError switchError = new SwitchError();
        switchError.setBrand(switchParameters.getDeviceBrand());
        switchError.setSwitchType(switchParameters.getDeviceModel());
        switchError.setFirewareVersion(switchParameters.getFirmwareVersion());
        switchError.setSubVersion(switchParameters.getSubversionNumber());
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
     * @method: 判断是否故障
     * @Param: [str] 交换机返回信息
     * @return: boolean  判断命令是否故障 故障为false 正常为true
     */
    public static boolean switchfailure(SwitchParameters switchParameters, String switchInformation){

        SwitchFailure switchFailure = new SwitchFailure();

        switchFailure.setBrand(switchParameters.getDeviceBrand());
        switchFailure.setSwitchType(switchParameters.getDeviceModel());
        switchFailure.setFirewareVersion(switchParameters.getFirmwareVersion());
        switchFailure.setSubVersion(switchParameters.getSubversionNumber());

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
     * 比较
     * @param switchParameters 交换机登录信息
     * @param compare  分析表数据 比较关键词
     * @param current_Round_Extraction_String
     * @return
     */
    @RequestMapping("compareVersion")
    public static boolean compareVersion(SwitchParameters switchParameters, String compare,String current_Round_Extraction_String){
        /* 提取到的词 进行分割 */
        /*自定义分隔符*/
        String customDelimiter = (String) CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());

        String[] current_Round_Extraction_split = current_Round_Extraction_String.split(customDelimiter);

        Map<String,String> value_String = new HashMap<>();
        if(!(current_Round_Extraction_String.equals(""))){
            for (int number = 0 ; number<current_Round_Extraction_split.length ; number = number +3){
                /*1:参数名 2:是否显示 3:参数值*/
                value_String.put(current_Round_Extraction_split[number],current_Round_Extraction_split[number+2]);
            }
        }

        //由 汉字 转化为 单词
        if (compare.indexOf("品牌")!=-1){
            compare = compare.replace("品牌",switchParameters.getDeviceBrand());
        }else if (compare.indexOf("型号")!=-1){
            compare = compare.replace("型号",switchParameters.getDeviceModel());
        }else if (compare.indexOf("固件版本")!=-1){
            compare = compare.replace("固件版本",switchParameters.getFirmwareVersion());
        }else if (compare.indexOf("子版本")!=-1){
            compare = compare.replace("子版本",switchParameters.getSubversionNumber());
        }

        /** 提取参数*/
        String getParameters = compare;
        getParameters = getParameters.replace("<=",":");
        getParameters = getParameters.replace(">=",":");
        getParameters = getParameters.replace("!=",":");
        getParameters = getParameters.replace("<",":");
        getParameters = getParameters.replace(">",":");
        getParameters = getParameters.replace("=",":");
        String[] parameter = getParameters.split(":");

        /** 提取比较符号 这样做可以不打乱顺序*/
        String getComparisonNumber = compare;
        getComparisonNumber = getComparisonNumber.replace(parameter[0],":");//参数一 替换 ：
        getComparisonNumber = getComparisonNumber.replace(parameter[1],":");//参数二 替换 ：

        if (parameter.length == 3){//如果参数数组长度为3 则有三个参数
            getComparisonNumber = getComparisonNumber.replace(parameter[2],":");//参数三 替换 ：
        }

        //假设有三个参数 ： 5.20.98<5.20.99<5.20.100 替换 “:”后 ： :<:<:  截取后 去掉了前后 得到： <:<
        getComparisonNumber = getComparisonNumber.substring(1,getComparisonNumber.length()-1);

        //comparisonNumber 得到 [<,<]
        String[] comparisonNumber = getComparisonNumber.split(":");

        /** 获取比较数组的集合
         * 例如：
         * {[5.20.98,<,5.20.99],[5.20.99,<,5.20.100]}*/
        List<String[]> compareList = new ArrayList<>();
        if (comparisonNumber.length ==1){
            //comparisonNumber.length ==1  有 一个比较
            /* 如果是比较取词后的内容  例如比较 用户名 <= damin
            查找提取到的数据中 是否有对应的key
            如果 key 的 value值 不为null 则 由 value值 替换 key */
            String value1 = value_String.get(parameter[0]);
            String value2 = value_String.get(parameter[1]);
            if (value1 != null){
                parameter[0] = value1;
            }
            if (value2 != null){
                parameter[1] = value2;
            }

            /* 得到 [5.20.98 , < , 5.20.99] */
            String[] compareArray = new String[3];
            compareArray[0] = parameter[0];
            compareArray[1] = comparisonNumber[0];
            compareArray[2] = parameter[1];
            compareList.add(compareArray);

        }else if (comparisonNumber.length ==2){
            //comparisonNumber.length == 2  有 两个比较
            /* 如果是比较取词后的内容,查找提取到的数据中 是否有对应的key*/
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

        /**循环 比较数组的集合
         * 例如 ： {[5.20.98,<,5.20.99],[5.20.99,<,5.20.100]}
         * 因为需要循环遍历 数组 所以 只能当false的时候才能返回*/
        boolean compare_size;
        for (String[] compareArray:compareList){
            switch (compareArray[1]){
                case ">":
                    /*要求参数一大于参数二
                    * 要求方法返回true*/
                    compare_size = compareVersionNumber(compareArray[0], compareArray[2]);
                    if (compare_size){
                    }else {
                        return false;
                    }
                    break;
                case "<":
                    /*要求参数一小于参数二
                     * 要求方法返回 true
                     * 并且要求 参数一等于参数二的时候 方法返回true*/
                    compare_size = compareVersionNumber(compareArray[0], compareArray[2]) //如果 str1 > str2  返回 true
                            || compareArray[0].equals(compareArray[2]);  //如果 str1 = str2  返回 true
                    if (compare_size){
                        return false;
                    }
                    break;
                case "=":
                    /*要求 参数一 等于 参数二
                     * 要求 参数一 等于 参数二的时候返回 true*/
                    compare_size = compareArray[0].equals(compareArray[2]);
                    if (compare_size){
                    }else {
                        return false;
                    }
                    break;
                case ">=":
                    /*要求 参数一 等于 参数二
                     * 要求方法返回 true
                     * 要求 参数一 等于 参数二的时候返回 true*/
                    compare_size = compareVersionNumber(compareArray[0], compareArray[2]) || compareArray[0].equals(compareArray[2]);
                    if (compare_size){
                    }else {
                        return false;
                    }
                    break;
                case "<=":
                    /*要求 参数一 等于 参数二
                     * 要求方法返回 false*/
                    compare_size = compareVersionNumber(compareArray[0], compareArray[2]);
                    if (compare_size){
                        return false;
                    }
                    break;
                case "!=":
                    //如果 str1 == str2  compare_size 为 true
                    compare_size = compareArray[0].equals(compareArray[2]);
                    if (compare_size){
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
     * 如果 str1 <= str2 返回 false
     * @Param: [str1, str2]
     * @return: boolean
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
     * 取词操作  按位置取词
     * @param returnString  交换机返回信息行信息
     * @param matchContent 关键词
     * @param relativePosition_line  相对位置行
     * @param integer 位置
     * @param length 取词数量及取词类型
     * @return
     */
    public static String wordSelection(String returnString,String matchContent,String relativePosition_line,int integer,String length){
        // 获取 W单词、L字母、S字符串
        String substring = length.substring(length.length() - 1, length.length());//取词类型

        //获取取值长度
        int word_length = Integer.valueOf(length.substring(0, length.length() - 1)).intValue();//取词长度

        //预设返回值
        String return_string = "";
        /*交换机返回结果 获取到的时候 就已经修改了*/
        //returnString = MyUtils.repaceWhiteSapce(returnString); // 连续空格改为 1 个空格

        switch (substring){

            // 取词和取字符串
            case "w"://单词
            case "W"://单词

            case "s"://字符串
            case "S"://字符串

                //以matchContent 为参照  获取位置 因为后期转化为数组，关键词为 [0]
                String get_word = "";
                //String returnString_trim = returnString.trim(); //交换机返回 信息 去除 前后空格
                String[] split_String = returnString.trim().split(" ");

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
                if ((split_String.length - word_length)  <  integer || integer < 0){
                    return null;
                }

                //取词位置
                int number = integer;
                for (int num = 0;num<word_length;num++){
                    get_word = split_String[number]+" ";
                    number++;
                    return_string += get_word;
                }
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
                if (return_string.length()>0){
                    return return_string.trim();
                }else {
                    return null;
                }
        }

        return null;

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
     *去除登录信息
     * @method: 去除登录信息
     * @Param: [switchInformation]
     * @return: java.lang.String
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
                if (loginInformationAuthentication.indexOf("^") !=-1 ){
                    switchInformation_array[number] = loginInformationAuthentication;
                }else {
                    switchInformation_array[number] = loginInformationAuthentication.trim();
                }

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
     */
    public static String loginInformationAuthentication(String switchInformation){
        //交换机返回信息 按行分割为 字符串数组
        if (switchInformation.indexOf("^")!=-1){

        }else {
            switchInformation = switchInformation.trim();
        }
        //因为登录信息 会另起一行 所以 登录信息 会是 % 开头
        if (switchInformation.length()<1){
            return switchInformation;
        }
        /*判断是否是首字母是% 或者包含 %
        判断是否包含 SHELL
        判断是否包含 /LOGIN /LOGOUT*/

        //判断是否是首字母是% 或者包含 %
        if (switchInformation.startsWith("%") || switchInformation.indexOf("%")!=-1 ){
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


    /**
     * 判断 字符串 最后一位 是否为 . 或者 ,
     * @param wordSelectionResult
     * @return
     */
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


    public static void getPath() {
        String projectPath = System.getProperty("user.dir");
    }


    /**
     *  根据四项基本信息 获取 模糊查询SQL
     * @param brand
     * @param model
     * @param firmwareVersion
     * @param subversionNo
     * @return
     */
    public static String getFuzzySQL(String brand,String model,String firmwareVersion,String subversionNo) {

        //and (type = #{type} or type = '*')
        String typeSQL = "";
        if (model != null  && model != ""){
            String type = model;
            typeSQL = "and (LOWER(switch_type) = LOWER(\'" + type +"\') OR switch_type = '*' OR ";

            List<String> stringCollection = ServiceImplUtils.getStringCollection(type);
            for (String typeString:stringCollection){
                typeSQL = typeSQL + "LOWER(switch_type) = LOWER(\'" + typeString+"*\')" +" OR ";
            }

            char[] chars = typeSQL.toCharArray();
            typeSQL = "";
            for (int i=0 ;i<chars.length-4;i++ ){
                typeSQL = typeSQL + chars[i];
            }
            typeSQL = typeSQL +")";
        }
        //and (fireware_version = #{firewareVersion} or fireware_version = '*')
        String firewareVersionSQL = "";
        if (firmwareVersion != null  && firmwareVersion != ""){
            String firewareVersion = firmwareVersion;
            firewareVersionSQL = "and (fireware_version = \'"+ firewareVersion +"\' OR fireware_version = '*' OR ";
            List<String> stringCollection = ServiceImplUtils.getStringCollection(firewareVersion);
            for (String typeString:stringCollection){
                firewareVersionSQL = firewareVersionSQL + "fireware_version = "+"\'" + typeString+"*\'" +" OR ";
            }
            char[] chars = firewareVersionSQL.toCharArray();
            firewareVersionSQL = "";
            for (int i=0 ;i<chars.length-4;i++ ){
                firewareVersionSQL = firewareVersionSQL + chars[i];
            }
            firewareVersionSQL = firewareVersionSQL +")";
        }
        //and (sub_version = #{subVersion} or sub_version = '*')
        String subVersionSQL = "";
        if (subversionNo != null  && subversionNo != ""){
            String subVersion = subversionNo;
            subVersionSQL = "and (sub_version = \'" + subVersion + "\' OR sub_version = '*' OR ";
            List<String> stringCollection = ServiceImplUtils.getStringCollection(subVersion);
            for (String typeString:stringCollection){
                subVersionSQL = subVersionSQL + "sub_version = "+"\'" + typeString+"*\'" +" OR ";
            }
            char[] chars = subVersionSQL.toCharArray();
            subVersionSQL = "";
            for (int i=0 ;i<chars.length-4;i++ ){
                subVersionSQL = subVersionSQL + chars[i];
            }
            subVersionSQL = subVersionSQL +")";
        }


        String sql = "where LOWER(brand) = LOWER(\'" + brand + "\')";
        if (model != null && model != ""){
            sql = sql + typeSQL;
        }
        if (firmwareVersion != null && firmwareVersion != ""){
            sql = sql + firewareVersionSQL;
        }
        if (subversionNo != null && subversionNo != ""){
            sql = sql + subVersionSQL;
        }
        return sql;
    }


    /**等价
     * 输入 huawei 返回等价品牌名 ： Quidway
     * @param brand
     * @return
     */
    public static String getEquivalence(String brand) {
        if (brand == null){
            return null;
        }
        Map<String, Object> value = (Map<String, Object>) CustomConfigurationUtil.getValue("BasicInformation.equivalence", Constant.getProfileInformation());
        if (value == null){
            return null;
        }
        Set<String> strings = value.keySet();
        for (String key:strings){
            if (brand.equalsIgnoreCase(key)){
                return (String) value.get(key);
            }
            if (brand.equalsIgnoreCase((String) value.get(key))){
                return key;
            }
        }
        return null;
    }


    /**
    * @Description 根据 UP 截取端口号 并 去除带"."的子端口
    * @author charles
    * @createTime 2023/12/18 15:13
    * @desc
     *
     * 执行逻辑为从 yml文件 中获取端口号关键词。
     * 根据“ ”分割获得字符串数组，然后遍历字符串数组。
     * （根据空格再分割）判断字符串数组元素中是否包含交换机端口号关键词，如果包含关键词的判断是否包含数字，
     * 如果包含数字的端口号完全，不包含字母的做端口号不完全，需要添加下一元素
     *
     * 如果端口号不完全走，需要添加下元素。
     *
    * @param information
     * @return
    */
    public static String getTerminalSlogan(String information){
        /*
         *根据 "obtainPortNumber.keyword" 在配置文件中 获取端口号关键词
         * Eth-Trunk Ethernet GigabitEthernet GE BAGG Eth
         * 根据空格分割为 关键词数组*/
        String deviceVersion = (String) CustomConfigurationUtil.getValue("obtainPortNumber.keyword",Constant.getProfileInformation());
        String[] keywords = deviceVersion.trim().split(" ");

        /*GigabitEthernet 9/1 up routed Full 1000M fiber*/
        /*根据UP分割字符串*/
        /*交换机信息 根据 up(忽略大小写) 分割*/
        String[] informationSplit = MyUtils.splitIgnoreCase( information ," UP ");

        /*遍历数组  包含/的为端口号 但不能确定端口号是否完全
         * 此时需要判断提取到的端口号是否包含字母
         * 包含则为完全端口号 否则为不完全端口号，需要加前面的GigabitEthernet*/
        for (String string:informationSplit){

            /* 两种情况
            * GigabitEthernet 9/1
            * GigabitEthernet9/1 */
            String[] string_split = string.trim().split(" ");
            /* 遍历交换机信息数组 */
            for (int num = 0;num < string_split.length;num++){

                /* 遍历配置文件 获取端口号关键词
                *
                * 查看 是否以 关键字开头
                *
                * 如果 以关键字开头，则判断 是否包含数字
                * 如果包含数字 则端口号完全，
                * 如果不包含数字则端口号不全，数值部分在下一个数组元素
                *
                * 端口号不能存在.情况发生。为子端口号，例如 TenGigabitEthernet 5/51.231 为 TenGigabitEthernet 5/51 子端口
                * 子端口 不用检测*/
                for (String keyword:keywords){

                    /*判断数组元素 是否已 关键词为 首*/
                    if (string_split[num].toUpperCase().startsWith(keyword.toUpperCase())){

                        /*判断提取到的端口号是否包含数字
                         * 包含数字 则端口号完全 */
                        if (MyUtils.isNumeric(string_split[num])){
                            /*包含则为完全端口号 否则为不完全端口号*/
                            String port = string_split[num];
                            /* 端口号 不能存在 . 不能为子端口*/
                            if (port.indexOf(".")!=-1){
                                return null;
                            }
                            return port;

                            /* 否则 端口号 不全*/
                        }else {
                            /*例如：  GigabitEthernet 2/1
                             * 获取到的 不包含 2/1
                             * 则为不完全端口号，需要加后面的GigabitEthernet*/
                            String port = string_split[num] +" "+ string_split[num+1];
                            /* 端口号 不能存在 . 不能为子端口*/
                            if (port.indexOf(".")!=-1){
                                return null;
                            }
                            return port;
                        }
                    }

                }

            }
        }
        return null;
    }

}
