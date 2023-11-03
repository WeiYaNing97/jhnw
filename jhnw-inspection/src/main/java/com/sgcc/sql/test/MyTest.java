package com.sgcc.sql.test;

import com.alibaba.fastjson.JSON;
import com.sgcc.sql.domain.ProblemScanLogic;
import com.sgcc.sql.domain.ProblemScanLogicCO;
import org.junit.Test;

import java.util.*;

/**
 * @program: jhnw
 * @description: ProblemScanLogic 转化为 Json
 * @author:
 * @create: 2023-10-18 10:04
 **/
public class MyTest {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        list1.add("1");
        list1.add("1");
        list1.add("2");
        list1.add("3");
        list1.add("4");
        list1.add("5");
        list1.add("6");
        list2.add("1");
        list2.add("1");
        list2.add("2");
        list2.add("3");
        list2.add("4");
        list2.add("5");
        list2.add("6");
        list.addAll(list1);
        list.addAll(list2);
        list.add("7");
        list.add("8");
        list.add("8");
        list.add("9");

        String[] commandLogicId = new String[0];
        if (list != null){
            commandLogicId = list.stream().distinct().toArray(String[]::new);
        }
        for (String pojo:commandLogicId){
            System.err.println(pojo);
        }
    }
}
