package com.sgcc.advanced.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 * @program: jhnw
 * @description:
 * @author:
 * @create: 2023-12-29 10:26
 **/
public class ThreadTest {
    public static Map<Integer,Thread> map = new HashMap<>();
    static int i = 0;
    public static void main(String[] args) {
        Thread thread = new Thread(()->{
            System.out.println(Thread.currentThread().isInterrupted());
            Timer timer = new Timer();
            long delay = 0; // 延迟时间，单位为毫秒
            long period = 300; // 执行间隔，单位为毫秒
            timer.schedule( new Test(timer), delay, period);
        });
        thread.start();
        map.put(50,thread);
    }

}