package com.sgcc.sql.thread;

import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.WorkThreadMonitor;
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
    public void switchLoginInformations(ParameterSet parameterSet, List<TotalQuestionTable> totalQuestionTables, List<String> advancedName, boolean isRSA) throws InterruptedException {
        // 判断是否停止
        if (WorkThreadMonitor.getShutdown_Flag(parameterSet.getSwitchParameters().get(0).getScanMark())) {
            return;
        }

        // 用于计数线程是否执行完成
        CountDownLatch countDownLatch = new CountDownLatch(parameterSet.getSwitchParameters().size());
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(parameterSet.getThreadCount());

        int i = 0;
        for (SwitchParameters switchParameters : parameterSet.getSwitchParameters()) {
            String threadName = getThreadName(i);
            i++;
            // 设置线程名称
            switchParameters.setThreadName(threadName);
            // 创建DirectionalScanThread线程
            DirectionalScanThread directionalScanThread = new DirectionalScanThread(threadName, switchParameters, totalQuestionTables, advancedName, countDownLatch, fixedThreadPool, isRSA);
            // 执行线程
            fixedThreadPool.execute(directionalScanThread);
        }

        // 等待所有线程执行完成
        countDownLatch.await();
        /*关闭线程池*/
        // 关闭线程池
        fixedThreadPool.shutdown();

    }


    public static String getThreadName(int i) {
        String name = System.currentTimeMillis() + new Random().nextInt(100) +" ";
        return "threadname" + name;
    }

}