package com.sgcc.sql;

import com.alibaba.fastjson.JSON;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.sql.domain.AnalyzeConvertJson;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @program: jhnw
 * @description:
 * @author:
 * @create: 2024-01-05 17:11
 **/
public class test {
    public static void main(String[] args) {
        String input = "abc123def456";
        int firstNumber = getFirstNumberFromString(input);
        System.out.println(firstNumber);
    }

    public static int getFirstNumberFromString(String input) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        } else {
            return -1; // 如果没有找到数字，返回-1或其他默认值
        }
    }
}
