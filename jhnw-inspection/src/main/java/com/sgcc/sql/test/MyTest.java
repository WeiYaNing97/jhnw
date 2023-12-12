package com.sgcc.sql.test;

import com.alibaba.fastjson.JSON;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.sql.domain.ProblemScanLogic;
import com.sgcc.sql.domain.ProblemScanLogicCO;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
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
        Thread thread = Thread.currentThread();
        thread.stop();
        thread.setName("测试");
        System.err.println("进入方法"+thread.getName()+"main");
        getName();
        getName2();
    }

    public static void getName() {
        Thread thread = Thread.currentThread();
        System.err.println("进入方法"+thread.getName()+"getName");
    }

    public static void getName2() {
        Thread thread = Thread.currentThread();
        System.err.println("进入方法"+thread.getName()+"getName2");
    }
}
