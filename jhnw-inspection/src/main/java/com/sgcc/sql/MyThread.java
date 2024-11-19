package com.sgcc.sql;

public class MyThread extends Thread{

    boolean flag = false;

    public int number = 0;

    public MyThread(int number){
        this.number = number;
    }


    @Override
    public void run() {
        System.err.println("ThreadName ：" + this.number);
        myTest(flag);
    }

    public void updateFlag(){
        flag = true;
    }

    public void myTest(boolean f) {
        int i = 1;
        while (i <= 10000000){

            if(f){
                System.err.println(f+"===========================================================");
                return;
            }

            try {
                Thread.sleep(100);
                System.out.println(this.number  + "ThreadName ："+Thread.currentThread().getName() + "，i = " + i++);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}