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


        MyThreadtest myThreadtest = new MyThreadtest();
        myThreadtest.run();

        try {
            Thread.sleep(5000);
            myThreadtest.updateFlag();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class MyThreadtest implements Runnable {

    List<MyThread> myThreads = new ArrayList<>();

    @Override
    public void run() {
        MyThreadStart();
    }

    public void MyThreadStart() {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);

        for (int i = 0; i <= 1; i++) {
            MyThread myThread = new MyThread(i);
            fixedThreadPool.execute(myThread);
            myThreads.add(myThread);
        }

        fixedThreadPool.shutdown();
    }

    public void updateFlag() {
        System.err.println("==============================更新flag");
        for (MyThread myThread : myThreads) {
            myThread.updateFlag();
        }
    }
}