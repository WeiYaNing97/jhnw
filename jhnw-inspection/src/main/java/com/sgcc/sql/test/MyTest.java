package com.sgcc.sql.test;

import com.alibaba.fastjson.JSON;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.sql.domain.ProblemScanLogic;
import com.sgcc.sql.domain.ProblemScanLogicCO;
import org.junit.Test;

import javax.lang.model.element.NestingKind;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: jhnw
 * @description: ProblemScanLogic 转化为 Json
 * @author:
 * @create: 2023-10-18 10:04
 **/
public class MyTest {

    public static void main(String[] args) {
        List<Integer> integerList = new ArrayList<>();
        integerList.add(1);integerList.add(1);integerList.add(1);integerList.add(1);integerList.add(1);integerList.add(1);integerList.add(1);
        integerList.add(1);integerList.add(1);integerList.add(1);integerList.add(1);integerList.add(1);integerList.add(1);integerList.add(1);
        integerList.add(1);integerList.add(1);integerList.add(1);integerList.add(1);integerList.add(1);integerList.add(1);integerList.add(1);
        integerList.add(2);integerList.add(2);integerList.add(2);integerList.add(2);integerList.add(2);integerList.add(2);integerList.add(2);
        integerList.add(2);integerList.add(2);integerList.add(2);integerList.add(2);integerList.add(2);integerList.add(2);integerList.add(2);
        integerList.add(2);integerList.add(2);integerList.add(2);integerList.add(2);integerList.add(2);integerList.add(2);integerList.add(2);

        Set<Integer> collect = integerList.stream().collect(Collectors.toSet());
        for (Integer integer:collect){
            System.err.println(integer);
        }
    }

}
