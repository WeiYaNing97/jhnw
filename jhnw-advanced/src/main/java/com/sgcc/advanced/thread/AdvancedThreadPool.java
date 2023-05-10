package com.sgcc.advanced.thread;

import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdvancedThreadPool {


    // 用来存储线程名称的map
    public static Map threadNameMap = new HashMap();

    /**
     * newFixedThreadPool submit submit
     */
    public static void switchLoginInformations(ParameterSet parameterSet, List<String> functionName) throws InterruptedException {

        // 用于计数线程是否执行完成
        CountDownLatch countDownLatch = new CountDownLatch(parameterSet.getSwitchParameters().size());
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(parameterSet.getThreadCount());

        int i = 1;
        for (SwitchParameters switchParameters:parameterSet.getSwitchParameters()){

            String threadName = getThreadName(i);
            switchParameters.setThreadName(threadName);
            i++;
            threadNameMap.put(threadName, threadName);

            fixedThreadPool.execute(new AdvancedThread(threadName,switchParameters,functionName,countDownLatch,fixedThreadPool));//mode, ip, name, password,configureCiphers, port, loginUser,time
        }
        countDownLatch.await();
    }


    public static void removeThread(String i) {
        threadNameMap.remove(i);
        System.out.println("删除线程Thread" + i + ", Hash表的Size：" + threadNameMap.size());
    }


    public static String getThreadName(int i) {
        return "threadname"+i;
    }



}
