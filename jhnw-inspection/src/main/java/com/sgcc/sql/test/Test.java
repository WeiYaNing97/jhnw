package com.sgcc.sql.test;

import com.sgcc.share.util.MyUtils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Test {
    public static void main(String[] args) {
        /*Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                String datetoString = MyUtils.getDatetoString(new Date());
                System.err.println(datetoString);
            }
        }, 1000, 6000);*/
        /*int exception = getException();
        System.err.println(exception);*/

        Throwable t = new Throwable();
        StackTraceElement[] frames = t.getStackTrace();
        for (StackTraceElement frame : frames);
    }

    public static int getException() {
        for (int i= 0; i<5;i++){
            if (i == 2){
                try {
                    throw new FileFormatException("测试成功","日志文件");
                } catch (FileFormatException e) {

                    Throwable se = new FileFormatException ("database error");
                    se.initCause(e);
                    try {
                        throw se;
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();

                        //e.printStackTrace();
                        // 获取异常信息
                        String errorMessage = throwable.toString(); // 获取异常信息字符串
                        System.out.println("throwable: "+errorMessage);
                        StackTraceElement[] stackTraceElements = throwable.getStackTrace(); // 获取异常堆栈信息
                        for (StackTraceElement stackTraceElement:stackTraceElements){
                            System.out.println("throwable: "+stackTraceElement.toString());
                        }

                    }


                    //e.printStackTrace();
                    // 获取异常信息
                    String errorMessage = e.toString(); // 获取异常信息字符串
                    System.out.println(errorMessage);
                    StackTraceElement[] stackTraceElements = e.getStackTrace(); // 获取异常堆栈信息
                    for (StackTraceElement stackTraceElement:stackTraceElements){
                        System.out.println(stackTraceElement.toString());
                    }
                }
            }
            if (i == 4){
                return i;
            }
        }
        return 100;
    }
}
