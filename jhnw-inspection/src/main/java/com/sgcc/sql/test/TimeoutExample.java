package com.sgcc.sql.test;

import java.util.concurrent.*;

public class TimeoutExample {
    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        // 提交要执行的方法，并设置超时时间为2秒
        ScheduledFuture<?> future = executor.schedule(() -> {
            // 执行的方法逻辑
            System.out.println("执行方法");
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("执行方法结束");
        }, 1, TimeUnit.SECONDS);


        try {
            // 等待任务执行结果，同时设置超时时间为2秒
            future.get(20, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            System.out.println("方法执行超时，启动另外一个方法");
            anotherMethod();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }


    }
    static void anotherMethod() {
        // 启动另外一个方法的逻辑
        System.out.println("启动了另外一个方法");
    }
}
