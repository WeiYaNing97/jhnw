package com.sgcc.sql;
import java.util.concurrent.*;

public class MyTest {


    public static void main(String[] args) {

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);

        // 创建一个新线程
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                try {
                    System.out.println("Working... " + i);
                    Thread.sleep(1000); // 模拟耗时操作
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // 重新设置中断标志
                    System.out.println("Thread was interrupted.");
                    return;
                }
            }
        });

        // 输出线程的初始状态
        System.out.println("Initial state: " + thread.getState());

        // 启动线程
        thread.start();
        fixedThreadPool.execute(thread);

        // 在主线程中循环检查子线程的状态
        while (thread.isAlive()) {
            Thread.State state = thread.getState();
            System.out.println("Current state: " + state);

            if (state == Thread.State.TERMINATED) {
                break; // 如果线程已经终止，则退出循环
            }

            try {
                Thread.sleep(500); // 减少输出频率
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Final state: " + thread.getState());
    }

    public static void main1(String[] args) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1; i++) { // 注意这里应该是i < 1而不是i <= 0，否则循环不会执行
            CountDownLatch latch = new CountDownLatch(1);
            MyThread2 myThread = new MyThread2(i, latch);
            fixedThreadPool.execute(myThread);

            // 等待子线程开始执行
            try {
                latch.await(); // 等待子线程开始执行
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 获取并打印线程的运行状态
            System.out.println("线程状态：" + myThread.getState());
        }
        fixedThreadPool.shutdown();
    }
}

class MyThread2 extends Thread {
    private boolean flag = false;
    public int number = 0;
    private final CountDownLatch latch;

    public MyThread2(int number, CountDownLatch latch) {
        this.number = number;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            /*latch.countDown(); // 通知主线程子线程已开始执行*/
            System.out.println(Thread.currentThread().getName() + " is running...");
            System.out.println("flag = " + flag);
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + " is end...");
        latch.countDown(); // 通知主线程子线程已执行完毕
    }

    public void updateFlag() {
        flag = true;
    }
}