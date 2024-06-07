package com.sgcc.share.util;

import java.util.ArrayList;
import java.util.List;

public class ServiceImplUtils {
    /**
     * 获取字符串数组
     * n
     * nu
     * nul
     * null
     *
     * @param value 字符串
     * @return 字符串列表
     */
    public static List<String> getStringCollection(String value) {
        // 如果字符串中不包含"."
        if (value.indexOf(".") == -1){
            // 如果字符串为空或者null
            if (value == null || value.equals("")){
                // 返回一个空的ArrayList
                return new ArrayList<>();
            }
            // 将字符串转换为字符数组
            char[] chars = value.toCharArray();
            // 获取字符数组的长度
            Integer integer = chars.length;
            // 创建一个空的ArrayList用于存储字符串
            List<String> valueString = new ArrayList<>();
            // 如果字符数组长度小于0（实际上不可能发生，因为长度总是非负的）
            if (integer<0){
                // 返回一个空的ArrayList
                return new ArrayList<>();
            }
            // 将字符数组的第一个字符转换为字符串并添加到valueString中
            valueString.add(chars[0]+"");
            // 遍历字符数组，从第二个字符开始
            for (int i = 1 ; i < integer;i++){
                // 将前一个字符串和当前字符拼接后添加到valueString中
                valueString.add(valueString.get(i-1) + chars[i]);
            }
            // 返回valueString
            return valueString;
        // 如果字符串中包含"."
        }else if (value.indexOf(".") != -1){
            // 使用"."将字符串分割为字符串数组
            String[] split = value.split("\\.");
            // 创建一个空的ArrayList用于存储字符串
            List<String> valueString = new ArrayList<>();
            // 用于拼接关键词的字符串
            String keywords ="";
            // 遍历分割后的字符串数组
            for (int num = 0 ; num < split.length ; num++){
                // 将当前字符串添加到keywords中，并在末尾添加"."
                keywords = keywords + split[num]+".";
                // 将keywords中除最后一个字符外的部分添加到valueString中
                valueString.add(keywords.substring(0,keywords.length()-1));
            }
            // 返回valueString
            return valueString;
        }
        // 如果以上条件都不满足，返回一个空的ArrayList
        return new ArrayList<>();
    }
}
