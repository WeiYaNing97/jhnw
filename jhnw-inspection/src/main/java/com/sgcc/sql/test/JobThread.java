package com.sgcc.sql.test;

import java.util.concurrent.TimeUnit;

public class JobThread extends Thread {

// 为线程命名

    public JobThread(String name,long threadId) {
        super(name);
    }

    @Override
    public void run() {
    // 如果主线程包含这个线程就一直运行
        while (MainThread.threadNameMap.containsKey(this.getName())) {
            try {
                System.out.println("线程名称：-----" + this.getName());
                TimeUnit.SECONDS.sleep(4);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("***************线程结束，线程名称：*********" + this.getName());
    }
}
