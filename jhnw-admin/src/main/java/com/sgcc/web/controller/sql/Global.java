package com.sgcc.web.controller.sql;

import java.util.List;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2021年12月17日 10:08
 */
public class Global {

    //整个会话的标识
    public static String uuid;
    //登录用户IP
    public static String logIP;
    //登录用户
    public static String logUser;
    //登录用户 密码
    public static String logPassword;

    /** 设备品牌 */
    public static String deviceBrand;

    /** 设备型号 */
    public static String deviceModel;

    /** 内部固件版本 */
    public static String firmwareVersion;

    /** 子版本 */
    public static String subversionNumber;



    //行数记录
    public static Integer line_n = 0;
    //
    public static String[] return_information_array;
    //第一个关键字
    public static String firstKeyword;
    //关键字集合
    public static List<String> matchContentList;
    //提取信息集合
    public static List<String> extractInformationList;
}