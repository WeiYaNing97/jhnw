package com.sgcc.sql.util;

import java.util.ArrayList;
import java.util.List;

public class ServiceImplUtils {
    /**
     * 获取字符串数组
     * n
     * nu
     * nul
     * null
     * @param value
     * @return
     */
    public static List<String> getStringCollection(String value) {
        if (value.indexOf(".") == -1){
            if (value == null || value.equals("")){
                return new ArrayList<>();
            }
            char[] chars = value.toCharArray();
            Integer integer = chars.length;
            List<String> valueString = new ArrayList<>();
            if (integer<0){
                return null;
            }
            valueString.add(chars[0]+"");
            for (int i = 1 ; i < integer;i++){
                valueString.add(valueString.get(i-1) + chars[i]);
            }
            return valueString;
        }else if (value.indexOf(".") != -1){
            String[] split = value.split("\\.");
            List<String> valueString = new ArrayList<>();
            for (int num = 0 ; num < split.length ; num++){
                valueString.add(split[num]);
            }
            return valueString;
        }
        return null;
    }
}
