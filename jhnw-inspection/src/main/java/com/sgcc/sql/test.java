package com.sgcc.sql;

import com.alibaba.fastjson.JSON;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.sql.domain.AnalyzeConvertJson;
import com.sgcc.sql.thread.DirectionalScanThread;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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


        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        for (int i = 0; i <= 0; i++) {
            MyThread myThread = new MyThread(i);

            fixedThreadPool.execute(myThread);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Thread.State state = myThread.getState();
            System.out.println("100线程状态：" + state);


            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Thread.State state2 = myThread.getState();
            System.out.println("10000线程状态：" + state2);


        }
        fixedThreadPool.shutdown();
    }
}
