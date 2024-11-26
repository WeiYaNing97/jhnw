package com.sgcc.sql;

public class MyThread extends Thread{
    boolean flag = false;
    public int number = 0;
    public MyThread(int number){
        this.number = number;
    }
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " is running...");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + " is end...");
    }

    public void updateFlag(){
        flag = true;
    }

}