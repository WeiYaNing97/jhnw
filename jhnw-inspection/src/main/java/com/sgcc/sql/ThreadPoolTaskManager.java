package com.sgcc.sql;

import java.util.concurrent.*;

public class ThreadPoolTaskManager {

    private final ExecutorService executor;
    private volatile boolean isShutdown = false;

    public ThreadPoolTaskManager(int poolSize) {
        this.executor = Executors.newFixedThreadPool(poolSize);
    }

    public void startTasks() {
        for (int i = 0; i < 1000; i++) {
            executor.submit(new SafeInterruptibleTask());
        }
    }

    public void shutdown() {
        if (!isShutdown) {
            isShutdown = true;
            // 尝试优雅地关闭线程池
            System.err.println("尝试优雅地关闭线程池");
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    // 如果超时则强制关闭
                    System.err.println("如果超时则强制关闭");
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                System.err.println("shutdown 恢复中断状态");
                executor.shutdownNow();
                Thread.currentThread().interrupt(); // 恢复中断状态
            }
        }
    }

    private class SafeInterruptibleTask implements Runnable {
        @Override
        public void run() {
            while (!isShutdown && !Thread.currentThread().isInterrupted()) {
                try {
                    // 模拟耗时操作
                    Thread.sleep(1000);
                    methodA();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // 恢复中断状态
                    break; // 跳出循环
                }
            }
        }

        private void methodA() {
            if (isShutdown || Thread.currentThread().isInterrupted()) return;
            System.err.println("methodA");
            // 调用其他方法...
            methodB();
        }

        private void methodB() {
            if (isShutdown || Thread.currentThread().isInterrupted()) return;
            System.err.println("methodB");
            // 调用其他方法...
            methodC();
        }

        private void methodC() {
            System.err.println("methodC");
            if (isShutdown || Thread.currentThread().isInterrupted()) return;
            // 执行具体业务逻辑
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolTaskManager manager = new ThreadPoolTaskManager(1000);
        manager.startTasks();

        // 模拟一段时间后停止任务
        Thread.sleep(5000);
        manager.shutdown();
    }
}