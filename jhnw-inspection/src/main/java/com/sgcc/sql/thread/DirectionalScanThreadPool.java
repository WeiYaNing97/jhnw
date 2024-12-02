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


    /**
     * newFixedThreadPool submit submit
     */
    public void switchLoginInformations(ParameterSet parameterSet, List<TotalQuestionTable> totalQuestionTables,List<String> advancedName,boolean isRSA) throws InterruptedException {
        // 用于计数线程是否执行完成
        CountDownLatch countDownLatch = new CountDownLatch(parameterSet.getSwitchParameters().size());
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(parameterSet.getThreadCount());

        int i=0;
        for (SwitchParameters switchParameters:parameterSet.getSwitchParameters()){
            String threadName = getThreadName(i);
            i++;

            switchParameters.setThreadName(threadName);
            DirectionalScanThread directionalScanThread = new DirectionalScanThread(threadName, switchParameters, totalQuestionTables, advancedName, countDownLatch, fixedThreadPool, isRSA);

            fixedThreadPool.execute(directionalScanThread);

        }

        countDownLatch.await();
        /*关闭线程池*/
        fixedThreadPool.shutdown();

    }

    public static String getThreadName(int i) {
        String name = System.currentTimeMillis() + new Random().nextInt(100) +" ";
        return "threadname" + name;
    }

}