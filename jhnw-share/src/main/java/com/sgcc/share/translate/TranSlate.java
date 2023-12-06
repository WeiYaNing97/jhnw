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

    public static String tranSlate(String returnString) {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);

        String query = returnString;
        String transResult = api.getTransResult(query, "auto", "zh");
        String[] transResult_split = transResult.split("\"dst\":\"");
        try {
            Thread.sleep(1*1000); //因为 百度翻译开发工具 免费版 只能 1s 翻译 一次 索引 线程休眠 1S
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String s = transResult_split[1];
        String[] transResult_split_split = transResult_split[1].split("\"}]}");
        String unicodeToString = unicodeToString(transResult_split_split[0]);
        System.err.print("\r\n翻译:\r\n"+unicodeToString+"\r\n");
        return unicodeToString;
    }

    /*
     * unicode编码转中文
     */
    public static String unicodeToString(String str) {

        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }
}