package com.sgcc.web.controller.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年01月04日 10:25
 */
public class ThreadTest {

    public static void main(String[] args) {
        Integer integer = 5;
        String[] strings ={"ssh","192.168.1.100","admin","admin","22"};
        List<String[]> strs =new ArrayList<>();
        strs.add(strings);
        //使用工具类准备一个线程池对象
        ExecutorService es = Executors.newFixedThreadPool(integer);
        for (int number=0;number<strs.size();number++){
            MyThread myThread = new MyThread(strs.get(number));
            myThread.start();
        }
        es.shutdownNow();
    }
}