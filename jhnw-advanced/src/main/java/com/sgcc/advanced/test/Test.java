package com.sgcc.advanced.test;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @program: jhnw
 * @description:
 * @author:
 * @create: 2023-12-29 11:37
 **/
public class Test extends TimerTask {

    private  Timer timer = null;
    public Test(Timer timer){
        this.timer = timer;
    }

    @Override
    public void run() {
        ThreadTest.i = ThreadTest.i+1;
        Thread thread = ThreadTest.map.get(ThreadTest.i);
        System.out.println(ThreadTest.i +" = "+thread);
        if (thread!=null){
            thread.interrupt();
        }
        System.out.println("Test.run()" + ThreadTest.i);
        if (thread !=null && ! thread.isInterrupted()){
            this.timer.cancel(); // 取消定时任务
            System.out.println("=============线程终止了================");
        }
    }

}
