package com.sgcc.sql.thread;

import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 扫描 全部问题 多线程池
 */
/*Inspection Completed*/
public class ScanFixedThreadPool {

    List<ScanThread> scanThreadList = new ArrayList<>();

    /**
    * @Description 扫描功能线程池
    * @author charles
    * @createTime 2024/1/29 16:08
    * @desc
    * @param parameterSet	交换机登录信息集合
     * @param isRSA	 密码是否通过 RSA 解密
     * @return
    */
    public void switchLoginInformations(ParameterSet parameterSet,boolean isRSA) throws InterruptedException {
        // 用于计数线程是否执行完成
        CountDownLatch countDownLatch = new CountDownLatch(parameterSet.getSwitchParameters().size());
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(parameterSet.getThreadCount());

        int i = 1;
        for (SwitchParameters switchParameters:parameterSet.getSwitchParameters()){
            String threadName = getThreadName(i);
            switchParameters.setThreadName(threadName);
            i++;

            ScanThread scanThread = new ScanThread(threadName, switchParameters, countDownLatch, fixedThreadPool, isRSA);
            fixedThreadPool.execute(scanThread);//mode, ip, name, password,configureCiphers, port, loginUser,time
            scanThreadList.add(scanThread);
        }

        countDownLatch.await();
        /*关闭线程池*/
        fixedThreadPool.shutdown();

    }


    public String getThreadName(int i) {
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
        for (ScanThread scanThread : scanThreadList) {
            // 调用每个线程的termination方法来终止线程
            scanThread.termination();
        }
    }

}