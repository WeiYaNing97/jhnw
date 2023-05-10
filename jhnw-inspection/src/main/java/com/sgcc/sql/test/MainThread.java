package com.sgcc.sql.test;

import java.util.HashMap;

import java.util.Map;

import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;

import java.util.concurrent.TimeUnit;

public class MainThread {

    public static final int THREADPOOL_SIZE = 5;
    // 生成固定大小的线程池
    public static ExecutorService exec = Executors.newFixedThreadPool(THREADPOOL_SIZE);
    // 用来存储线程名称的map
    public static Map threadNameMap = new HashMap();
    public static void main(String[] args) {
    // 向线程池中插入 10 个线程，但是线程池只允许最大 5 个线程，所以其他 5 个线程等待中
        for (int i = 0; i < THREADPOOL_SIZE + 5; i++) {
            String threadName = getThreadName(i);
            threadNameMap.put(threadName, threadName);
            exec.execute(new JobThread(threadName, i));
        }
        System.out.println("Hash表的Size：" + threadNameMap.size());
        try {
            System.out.println("主线程睡一会！");
            TimeUnit.SECONDS.sleep(3);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("醒了！");
        }
    // 下面的这几个用来删除线程池里面的线程
    removeThread(0);
    removeThread(1);
    removeThread(2);
    }

    public static void removeThread(int i) {
        threadNameMap.remove(getThreadName(i));
        System.out.println("删除线程Thread" + i + ", Hash表的Size：" + threadNameMap.size());
    }
    public static String getThreadName(int i) {
        return "threadname"+i;
    }
}
