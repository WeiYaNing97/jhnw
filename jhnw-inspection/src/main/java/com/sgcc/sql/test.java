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
    public static List<String> getOperator(String input) {
        String regex = "[<>=!]=|[<>=!]|[+\\-*/]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        List<String> stringList = new ArrayList<>();
        while (matcher.find()) {
            stringList.add(matcher.group());
        }
        return stringList;
    }

    public static void main(String[] args) {
        String input = " 5.20.99 <= 版本号 ";
        List<String> delimiters = getOperator(input);

        String regex = String.join("|", delimiters);
        String[] result = input.split(regex);

        List<String> parameters = new ArrayList<>();
        for (String s : result) {
            parameters.add(s.trim());
        }

        List<String> returnList = new ArrayList<>();
        for (int i = 0 ; i<parameters.size()-1; i++){
            returnList.add(parameters.get(i));
            returnList.add(delimiters.get(i));
        }
        returnList.add(parameters.get(parameters.size()-1));

        System.err.println(returnList);

    }

}
