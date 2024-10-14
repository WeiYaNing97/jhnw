package com.sgcc.share.translate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @date 2022年03月03日 16:51
 */
public class TranSlate {
    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20220303001108393";
    private static final String SECURITY_KEY = "VSyU1yTacUdFKzymL8Ga";

        /**
         * 翻译给定的字符串
         *
         * @param returnString 待翻译的字符串
         * @return 翻译后的字符串
         */
    public static String tranSlate(String returnString) {
        // 实例化TransApi对象，传入APP_ID和SECURITY_KEY
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);

        // 待翻译的字符串
        String query = returnString;
        // 调用TransApi的getTransResult方法获取翻译结果
        String transResult = api.getTransResult(query, "auto", "zh");
        // 根据特定格式分割翻译结果字符串
        String[] transResult_split = transResult.split("\"dst\":\"");

        // 因为 百度翻译开发工具 免费版 只能 1s 翻译 一次，所以需要线程休眠1秒
        try {
            // 线程休眠1秒
            Thread.sleep(1*1000); //因为 百度翻译开发工具 免费版 只能 1s 翻译 一次 索引 线程休眠 1S
        } catch (InterruptedException e) {
            // 打印异常堆栈信息
            e.printStackTrace();
        }

        // 获取分割后的翻译结果字符串
        String s = transResult_split[1];
        // 根据特定格式再次分割翻译结果字符串
        String[] transResult_split_split = transResult_split[1].split("\"}]}");

        // 调用unicodeToString方法将unicode编码的字符串转换为普通字符串
        String unicodeToString = unicodeToString(transResult_split_split[0]);

        // 打印翻译结果到标准错误输出
        System.err.print("\r\n翻译:\r\n"+unicodeToString+"\r\n");

        // 返回翻译后的字符串
        return unicodeToString;
    }


        /**
         * 将unicode编码的字符串转换为普通字符串
         *
         * @param str unicode编码的字符串
         * @return 转换后的普通字符串
         */
    public static String unicodeToString(String str) {

        // 编译正则表达式，匹配以 "\\u" 开头，后面跟4个十六进制数字的字符串
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        // 创建匹配器
        Matcher matcher = pattern.matcher(str);
        // 声明字符变量
        char ch;
        // 使用循环查找匹配项
        while (matcher.find()) {
            // 将匹配到的十六进制数字字符串转换为字符
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            // 将原字符串中的匹配项替换为对应的字符
            str = str.replace(matcher.group(1), ch + "");
        }
        // 返回转换后的字符串
        return str;
    }

}