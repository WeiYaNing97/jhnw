package com.sgcc.sql.thread;

import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.sql.domain.TotalQuestionTable;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 专项扫描 及 定时任务 多线程
 */
public class DirectionalScanThreadPool {

    // 用来存储线程名称的map
    public static Map threadNameMap = new HashMap();

    /**
     * newFixedThreadPool submit submit
     */
    public static void switchLoginInformations(ParameterSet parameterSet, List<TotalQuestionTable> totalQuestionTables,List<String> advancedName,boolean isRSA) throws InterruptedException {
        // 用于计数线程是否执行完成
        CountDownLatch countDownLatch = new CountDownLatch(parameterSet.getSwitchParameters().size());
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(parameterSet.getThreadCount());

        int i=0;
        for (SwitchParameters switchParameters:parameterSet.getSwitchParameters()){
            String threadName = getThreadName(i);
            i++;
            /*添加入线程map集合*/
            threadNameMap.put(threadName, threadName);
            switchParameters.setThreadName(threadName);
            fixedThreadPool.execute(new DirectionalScanThread(threadName,switchParameters,totalQuestionTables, advancedName,countDownLatch,fixedThreadPool,isRSA));

        }

        countDownLatch.await();
        /*关闭线程池*/
        fixedThreadPool.shutdown();

    }


    public static void removeThread(String i) {
        threadNameMap.remove(i);
        System.err.println("删除线程Thread" + i + ", Hash表的Size：" + threadNameMap.size());
    }
    public static String getThreadName(int i) {
        String name = System.currentTimeMillis() + new Random().nextInt(100) +" ";
        return "threadname" + name;
    }

}