package com.sgcc.sql.thread;

import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.WorkThreadMonitor;

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
     * @param isRSA	 isRSA = true; 交换机登录密码是否经过RSA加密  false为明文 true为密文
     * @return
    */
    public void switchLoginInformations(ParameterSet parameterSet,boolean isRSA) throws InterruptedException {
        /**
         * 1：创建线程数量
         *   创建线程池
         */
        // 用于计数线程是否执行完成
        CountDownLatch countDownLatch = new CountDownLatch(parameterSet.getSwitchParameters().size());
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(parameterSet.getThreadCount());

        /**
         * 2：创建线程并执行
         */
        int i = 1;
        for (SwitchParameters switchParameters:parameterSet.getSwitchParameters()){
            //如果该线程被标记为已完成，则跳出循环... 不能用return 跳出循环，需要关闭线程池
            if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
                break;
            }
            //线程命名
            String threadName = getThreadName(i);
            //设置线程名称
            switchParameters.setThreadName(threadName);
            //创建ScanThread线程并执行 并加入线程列表中，方便后面关闭线程池后结束程序。参数：线程名称，交换机登录信息，计数器，线程池，是否RSA
            ScanThread scanThread = new ScanThread(threadName, switchParameters, countDownLatch, fixedThreadPool, isRSA);
            fixedThreadPool.execute(scanThread);
            scanThreadList.add(scanThread);

            i++;//执行下一个线程
        }

        /**
         * 3：等待所有线程执行完成后，
         * 关闭线程池并结束执行程序后结束程序，否则程序将一直等待下去，造成死锁状态。
         */
        //等待线程计数为0，即所有线程执行完成后，关闭线程池，结束程序
        countDownLatch.await();
        //关闭线程池，结束程序
        fixedThreadPool.shutdown();
    }


    public String getThreadName(int i) {
        String name = System.currentTimeMillis() + new Random().nextInt(100) +" ";
        return "threadname" + name;
    }
}