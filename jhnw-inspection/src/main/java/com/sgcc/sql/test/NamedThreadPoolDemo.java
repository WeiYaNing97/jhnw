package com.sgcc.sql.test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
public class NamedThreadPoolDemo {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5, new NamedThreadFactory());
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Task());
        }
        executorService.shutdown();
    }
    static class NamedThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            Random random = new Random();
            thread.setName("Thread-" + System.currentTimeMillis() + random.nextInt(100));
            return thread;
        }
    }
    static class Task implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Thread.currentThread().setName("11111111111111111111");
            System.out.println(Thread.currentThread().getName() + " is running");
        }
    }
}
