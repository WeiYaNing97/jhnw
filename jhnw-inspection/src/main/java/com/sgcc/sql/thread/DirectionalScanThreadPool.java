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

    List<DirectionalScanThread> directionalScanThreadList = new ArrayList<>();

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
            directionalScanThreadList.add(directionalScanThread);
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

    /**
     * 终止所有扫描线程。
     *
     * 该方法遍历scanThreadList中的所有扫描线程（ScanThread），并调用每个线程的termination方法来终止它们。
     * 这通常用于在不再需要这些线程时释放资源。
     */
    public  void terminationScanThread() {
        // 遍历scanThreadList中的所有扫描线程（ScanThread）
        for (DirectionalScanThread directionalScanThread : directionalScanThreadList) {
            // 调用每个线程的termination方法来终止线程
            directionalScanThread.termination();
        }
    }
}