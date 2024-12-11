package com.sgcc.share.util;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.controller.SwitchErrorController;
import com.sgcc.share.controller.SwitchFailureController;
import com.sgcc.share.domain.*;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.service.IReturnRecordService;
import com.sgcc.share.service.ISwitchInformationService;
import com.sgcc.share.translate.TranSlate;
import com.sgcc.share.webSocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能方法类
 */
public class FunctionalMethods {

    @Autowired
    private static ISwitchInformationService switchInformationService;



    /**
     * 获取交换机基本信息，如果已有记录则返回对应ID，否则插入并返回新记录的ID
     *
     * @param switchParameters 交换机参数对象
     * @return 交换机基本信息记录的ID
     */
    /*获取交换机基本信息 有返回ID 没有插入并返回ID*/
    public static Long getSwitchParametersId(SwitchParameters switchParameters) {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }

        /**
         * 交换机四项基本信息对象
         */
        SwitchInformation switchInformation = new SwitchInformation();

        /**
         * 设置四项基本信息
         */
        switchInformation.setBrand(switchParameters.getDeviceBrand());
        switchInformation.setSwitchType(switchParameters.getDeviceModel());
        switchInformation.setFirewareVersion(switchParameters.getFirmwareVersion());

        /**
         * 当子版本为空时，字段赋值为 "null"
         * 考虑到如果置空为"null"的话，会查出大量前三个信息相同的数据来
         * 例如：
         * 1： H3C S2152 5.20.99 1600
         * 2： H3C S2152 5.20.99 <null>
         */
        switchInformation.setSubVersion(switchParameters.getSubversionNumber() == null?"null":switchParameters.getSubversionNumber());

        // 解决多线程 service 为null问题
        switchInformationService = SpringBeanUtil.getBean(ISwitchInformationService.class);//解决 多线程 service 为null问题

        // 查询交换机信息列表
        List<SwitchInformation> switchInformationList = switchInformationService.selectSwitchInformationList(switchInformation);

        if (MyUtils.isCollectionEmpty(switchInformationList)){
            // 插入交换机信息
            int i = switchInformationService.insertSwitchInformation(switchInformation);

            if (i>0){
                // 返回新插入的交换机信息的ID
                return switchInformation.getId();
            }

            // 返回插入结果的整数值
            return Long.valueOf(i).longValue();

        }else {
            // 返回查询到的第一条交换机信息的ID
            return switchInformationList.get(0).getId();
        }

    }



    /**
     * 判断是否为错误命令
     *
     * @param switchParameters 交换机参数对象
     * @param str 交换机返回信息
     * @return boolean 判断命令是否错误，错误为false，正确为true
     */
    public static boolean judgmentError(SwitchParameters switchParameters, StringBuffer str){
        // 创建SwitchError对象
        SwitchError switchError = new SwitchError();
        // 设置交换机品牌
        switchError.setBrand(switchParameters.getDeviceBrand());
        // 设置交换机型号
        switchError.setSwitchType(switchParameters.getDeviceModel());
        // 设置固件版本
        switchError.setFirewareVersion(switchParameters.getFirmwareVersion());
        // 设置子版本号
        switchError.setSubVersion(switchParameters.getSubversionNumber());

        // 创建SwitchErrorController对象
        SwitchErrorController switchErrorController = new SwitchErrorController();
        // 根据SwitchError对象查询错误列表
        List<SwitchError> switchErrors = switchErrorController.selectSwitchErrorListByPojo(switchError);

        // 遍历错误列表
        for (SwitchError pojo:switchErrors){
            // 判断交换机返回信息中是否包含错误关键字
            if (str.indexOf(pojo.getErrorKeyword()) != -1){
                // 包含错误关键字，返回false
                return false;
            }
        }

        // 遍历完错误列表后，未发现错误关键字，返回true
        return true;
    }



    /**
     * 判断是否故障
     *
     * @param switchParameters 交换机参数对象
     * @param switchInformation 交换机返回信息
     * @return boolean 判断命令是否故障，故障为false，正常为true
     */
    public static boolean switchfailure(SwitchParameters switchParameters, StringBuffer switchInformation){

        // 创建一个 SwitchFailure 对象
        SwitchFailure switchFailure = new SwitchFailure();

        // 设置交换机的品牌
        switchFailure.setBrand(switchParameters.getDeviceBrand());
        // 设置交换机的型号
        switchFailure.setSwitchType(switchParameters.getDeviceModel());
        // 设置交换机的固件版本
        switchFailure.setFirewareVersion(switchParameters.getFirmwareVersion());
        // 设置交换机的子版本号
        switchFailure.setSubVersion(switchParameters.getSubversionNumber());

        // 创建一个 SwitchFailureController 对象
        SwitchFailureController switchFailureController = new SwitchFailureController();
        // 通过 SwitchFailure 对象查询故障列表
        List<SwitchFailure> switchFailures = switchFailureController.selectSwitchFailureListByPojo(switchFailure);

        // 遍历故障列表
        for (SwitchFailure pojo:switchFailures){
            // 如果交换机返回信息中包含故障关键字，则返回 false
            //包含 返回 false
            if (switchInformation.indexOf(pojo.getFailureKeyword()) !=-1){
                return false;
            }
        }

        // 遍历完故障列表后，没有匹配到故障关键字，返回 true
        return true;
    }

    /**
     * 比较
     * @param switchParameters 交换机登录信息
     * @param compare  分析表数据 比较关键词
     * @param current_Round_Extraction_String
     * @return
     */
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
     * 比较两个系统版本号的大小，判断第一个版本号是否大于第二个版本号。
     *
     * @param str1 第一个版本号，格式为字符串类型，例如："1.2.3"
     * @param str2 第二个版本号，格式为字符串类型，例如："1.2.4"
     * @return 如果第一个版本号大于第二个版本号，则返回true；否则返回false。
     * 如果 str1 > str2 返回 true
     * 如果 str1 <= str2 返回 false
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
     *
     * @param date 日期对象
     * @return 精确到毫秒的时间戳
     **/
    public static Long getTimestamp(Date date){
        // 如果日期对象为null，则返回0
        if (null == date) {
            return (long) 0;
        }
        // 将日期对象转换为毫秒时间戳的字符串形式
        String timestamp = String.valueOf(date.getTime());
        // 将毫秒时间戳的字符串转换为Long类型并返回
        return Long.valueOf(timestamp);
    }


    /**
     *去除登录信息
     * @method: 去除登录信息
     * @Param: [switchInformation]
     * @return: java.lang.String
     */
    public static StringBuffer removeLoginInformation(StringBuffer switchInformation){
        //交换机返回信息 按行分割为 字符串数组
        // 因为登录信息 会另起一行 登录信息 会在行首
        List<String> switchInformation_List = StringBufferUtils.stringBufferSplit(switchInformation,"\r\n");

        //循环遍历 按行分析 是否存在登录信息
        for (int number=0;number<switchInformation_List.size();number++){

            String information = switchInformation_List.get(number);
            if (information!=null && !information.equals("")){
                // 调用loginInformationAuthentication方法处理当前行信息
                String loginInformationAuthentication = loginInformationAuthentication(switchInformation_List.get(number));
                if (loginInformationAuthentication.indexOf("^") !=-1 ){
                    // 如果处理后的信息包含"^"，则直接赋值给原数组对应位置
                    switchInformation_List.set(number,loginInformationAuthentication);
                }else {
                    // 否则去除处理后的信息两侧的空格，并赋值给原数组对应位置
                    switchInformation_List.set(number,loginInformationAuthentication);
                }

            }
        }

        // 因为之前按行分割了，所以需要将处理后的信息重新拼接成字符串返回
        //因为 之前 按行分割了
        //返回字符串
        StringBuffer stringBuffer = new StringBuffer();
        for (int number=0;number<switchInformation_List.size();number++){
            // 拼接每一行信息，并在末尾添加换行符
            stringBuffer.append(switchInformation_List.get(number));
            stringBuffer.append("\r\n");
        }
        return StringBufferUtils.substring(stringBuffer,0,stringBuffer.length()-2);
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
                /*TranSlate.tranSlate(login_Information);*/

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
    public static StringBuffer judgeResultWordSelection(StringBuffer wordSelectionResult) {
        // 获取字符串的最后一个字符
        String last = StringBufferUtils.substring(wordSelectionResult,wordSelectionResult.length() - 1, wordSelectionResult.length()).toString();
        // 定义包含逗号和点号的字符串数组
        String[] character = {",", "."};
        // 遍历数组中的每个字符
        for (String judgeChar : character) {
            // 如果最后一个字符与数组中的字符相等
            if (last.equals(judgeChar)) {
                // 截取除最后一个字符外的子字符串并赋值给原字符串
                wordSelectionResult = StringBufferUtils.substring(wordSelectionResult,0, wordSelectionResult.length() - 1);
                // 跳出循环
                break;
            }
        }
        // 返回处理后的字符串
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


    /**
     * 等价
     * 输入 huawei 返回等价品牌名 ： Quidway
     *
     * @param brand 品牌名
     * @return 等价品牌名
     */
    public static String getEquivalence(String brand) {
        // 如果品牌名为空，则返回null
        if (brand == null){
            return null;
        }
        // 从配置中获取等价关系映射
        Map<String, Object> value = (Map<String, Object>) CustomConfigurationUtil.getValue("BasicInformation.equivalence", Constant.getProfileInformation());
        // 如果等价关系映射为空，则返回null
        if (value == null){
            return null;
        }
        // 获取等价关系映射的键集合
        Set<String> strings = value.keySet();
        // 遍历键集合
        for (String key:strings){
            // 如果输入的品牌名与键相等（忽略大小写）
            if (brand.equalsIgnoreCase(key)){
                // 返回对应的值
                return (String) value.get(key);
            }
            // 如果输入的品牌名与值相等（忽略大小写）
            if (brand.equalsIgnoreCase((String) value.get(key))){
                // 返回键
                return key;
            }
        }
        // 如果没有找到匹配的等价品牌名，则返回null
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
        List<String> keywords = Arrays.stream(deviceVersion.trim().split(" ")).collect(Collectors.toList());

        // 使用Lambda表达式和Comparator对字符串按长度从长到短排序
        Collections.sort(keywords, Comparator.comparingInt(String::length).reversed());

        String keyword = "";
        for (String key:keywords){
            if (information.toLowerCase().indexOf(key.toLowerCase())!=-1){
                keyword = key ;
                break;
            }
        }

        if (keyword.equals("")){
            return null;
        }

        /*GigabitEthernet 9/1 up routed Full 1000M fiber*/
        information = caseInsensitiveReplace(information, keyword+" ", keyword);

        /*GigabitEthernet9/1 up routed Full 1000M fiber*/

        /*根据" "分割字符串*/
        /*交换机信息 " "分割*/
        String[] informationSplit = information.split(" ");

        /*遍历数组  包含/的为端口号 但不能确定端口号是否完全
         * 此时需要判断提取到的端口号是否包含字母
         * 包含则为完全端口号 否则为不完全端口号，需要加前面的GigabitEthernet*/
        for (String string:informationSplit){

            String[] string_split = string.trim().split(" ");

            /* 遍历交换机信息数组 */
            for (int num = 0;num < string_split.length;num++){

                /* 遍历配置文件 获取端口号关键词
                *
                * 查看 是否以 关键字开头
                *
                * 端口号不能存在.情况发生。为子端口号，例如 TenGigabitEthernet 5/51.231 为 TenGigabitEthernet 5/51 子端口
                * 子端口 不用检测*/
                /*判断数组元素 是否已 关键词为 首*/

                if (string_split[num].toUpperCase().startsWith(keyword.toUpperCase())){

                    String port = string_split[num];
                    /* 端口号 不能存在 . 不能为子端口*/
                    if (port.indexOf(".")!=-1){
                        return null;
                    }
                    return port;

                    /*//判断提取到的端口号是否包含数字
                    //包含数字 则端口号完全
                    if (MyUtils.isNumeric(string_split[num])){
                        //包含则为完全端口号 否则为不完全端口号
                        String port = string_split[num];
                        //端口号 不能存在 . 不能为子端口
                        if (port.indexOf(".")!=-1){
                            return null;
                        }
                        return port;

                        //否则 端口号 不全
                    }else {
                        //因为之前已经去除了端口号中的空格
                        // 例如：  GigabitEthernet 2/1
                        // 获取到的 不包含 2/1
                        // 则为不完全端口号，需要加后面的GigabitEthernet
                        String port = string_split[num] +" "+ string_split[num+1];
                        //端口号 不能存在 . 不能为子端口
                        if (port.indexOf(".")!=-1){
                            return null;
                        }
                        return port;
                    }*/

                }

            }
        }
        return null;
    }


    /**
     * 在给定的行数据中查找并返回第一个同时包含设备端口号关键字且包含数字的单词（按空格分隔）。
     *
     * <p>该方法接收两个参数：一个是要搜索的行数据（{@code lineData}），另一个是包含设备端口号关键字的字符串数组（{@code deviceVersion_split}）。
     * 它首先遍历设备版本关键字数组，对于每个关键字，检查行数据是否包含该关键字（不区分大小写）。
     * 如果行数据包含某个关键字，则进一步将行数据按空格拆分为单词数组，并遍历这些单词。
     * 对于每个单词，如果它同时包含给定的设备端口号关键字（不区分大小写）且包含至少一个数字，则立即返回该单词。</p>
     *
     * <p>如果行数据中不包含任何给定的设备端口号关键字，或者没有找到同时满足条件的单词，则返回{@code null}。</p>
     *
     * @param lineData 要搜索的行数据字符串。
     * @param deviceVersion_split 包含设备端口号关键字的字符串数组。
     * @return 第一个同时包含设备端口号关键字且包含数字的单词（按空格分隔），如果未找到则返回{@code null}。
     */
    public static String includePortNumberKeywords(String lineData,String[] deviceVersion_split) {
        for (String deviceVersion : deviceVersion_split) {
            // 检查行数据是否包含设备端口号关键字
            if (MyUtils.containIgnoreCase(lineData,deviceVersion)) {
                // 将设备端口号关键字后的空格去除
                lineData = caseInsensitiveReplace(lineData, deviceVersion+" ", deviceVersion);
                // 将行数据按空格拆分为单词数组
                String[] lineData_split = lineData.split(" ");
                for (int i = 0; i < lineData_split.length; i++) {
                    String split_i = lineData_split[i];
                    // 检查单词是否同时包含设备端口号关键字和数字
                    if (MyUtils.containIgnoreCase(split_i,deviceVersion) && MyUtils.containDigit(split_i)) {
                        // 返回第一个符合条件的单词
                        return split_i;
                    }
                }
            }
        }
        // 如果没有找到符合条件的单词，则返回null
        return null;
    }

    /**
     * 不区分大小写地替换字符串中的指定子串。
     *
     * <p>此方法接收三个字符串参数：输入字符串（{@code input}），要查找的子串（{@code find}），以及用于替换的子串（{@code replacement}）。
     * 它遍历输入字符串，查找所有与要查找的子串（不区分大小写）匹配的实例，并将它们替换为指定的替换子串。
     * 替换时，会考虑原始字符串中被替换部分的大小写情况，以确保替换后的字符串尽可能保持原始字符串的大小写特征。</p>
     *
     * <p>如果输入参数中的任何一个为{@code null}，则抛出{@link IllegalArgumentException}异常。</p>
     *
     * @param input 输入的原始字符串。
     * @param find 要在输入字符串中查找的子串（不区分大小写）。
     * @param replacement 替换找到的子串的字符串。
     * @return 替换后的字符串。
     * @throws IllegalArgumentException 如果输入参数中的任何一个为{@code null}。
     */
    public static String caseInsensitiveReplace(String input, String find, String replacement) {
        // 检查输入参数是否为null，如果是，则抛出异常
        if (input == null || find == null || replacement == null) {
            throw new IllegalArgumentException("Arguments cannot be null"); // 抛出异常，提示参数不能为空
        }

        int start = 0; // 初始化查找起始位置
        int end = 0; // 初始化当前查找结束位置
        StringBuilder builder = new StringBuilder(); // 使用StringBuilder来构建最终的字符串

        // 循环查找并替换
        while ((end = input.toLowerCase().indexOf(find.toLowerCase(), start)) != -1) {
            // 将从起始位置到当前查找结束位置之前的子字符串添加到StringBuilder中
            builder.append(input.substring(start, end));
            // 调用capitalizeReplacement方法处理替换逻辑，考虑原始字符串中find部分的字母大小写
            builder.append(capitalizeReplacement(input.substring(end, end + find.length()), replacement));
            // 更新下一次查找的起始位置
            start = end + find.length();
        }
        // 将最后一部分（可能没有被替换的部分）添加到StringBuilder中
        builder.append(input.substring(start));

        // 返回构建好的字符串
        return builder.toString();
    }

    /**
     * 根据原始字符串的首尾字符的大小写状态，调整替换字符串的首尾字符的大小写。
     *
     * <p>此方法首先检查原始字符串的首字符是否为大写，如果是，则将替换字符串的首字符也转换为大写。
     * 接着，如果原始字符串的末字符存在且为小写（且替换字符串长度大于1），则将替换字符串的末字符也转换为小写。
     * 这样做可以确保替换后的字符串在大小写方面与原始字符串的某些部分保持一致。</p>
     *
     * @param original 原始字符串，用于判断其首尾字符的大小写状态。
     * @param replacement 需要被调整大小写的替换字符串。
     * @return 调整后的替换字符串，其首尾字符的大小写状态可能与原始字符串的首尾字符保持一致。
     */
    private static String capitalizeReplacement(String original, String replacement) {
        // 检查原始字符串的第一个字符是否为大写
        boolean firstCharUpper = Character.isUpperCase(original.charAt(0));
        // 检查原始字符串的最后一个字符（如果存在）是否为小写
        boolean lastCharLower = original.length() > 1 && Character.isLowerCase(original.charAt(original.length() - 1));

        String result = replacement; // 初始化结果为替换字符串

        // 如果原始字符串的第一个字符是大写，则将替换字符串的第一个字符也转换为大写
        if (firstCharUpper) {
            result = Character.toUpperCase(result.charAt(0)) + result.substring(1);
        }

        // 如果原始字符串的最后一个字符是小写，并且替换字符串的长度大于1，
        // 则将替换字符串的最后一个字符也转换为小写
        if (lastCharLower && result.length() > 1) {
            result = result.substring(0, result.length() - 1) + Character.toLowerCase(result.charAt(result.length() - 1));
        }

        return result; // 返回处理后的结果字符串
    }
}
