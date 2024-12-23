package com.sgcc.advanced.thread;

import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.WorkThreadMonitor;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AdvancedThreadPool {

    /**
     * 运行分析线程池
     *
     * @param parameterSet 参数集合
     * @param functionName 需要分析的函数列表
     * @param isRSA 是否使用RSA算法
     * @throws InterruptedException 如果等待线程中断，则抛出 InterruptedException 异常
     */
    public void switchLoginInformations(ParameterSet parameterSet, List<String> functionName,boolean isRSA) throws InterruptedException {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(parameterSet.getSwitchParameters().get(0).getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return;
        }

        /**
         * 1:创建一个CountDownLatch对象，用于计数线程是否执行完成: 计数器的初始值为参数集合中SwitchParameters的数量,即要登录的设备数量。
         *   创建线程池，参数为线程池线程数，前端传入的是线程池大小。
         */
        CountDownLatch countDownLatch = new CountDownLatch(parameterSet.getSwitchParameters().size());
        // 创建一个固定大小的线程池
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(parameterSet.getThreadCount());

        /**
         * 2:遍历参数集合中的SwitchParameters对象，为每个对象设置线程名和CountDownLatch对象。然后将其提交给线程池执行，并启动每个线程。
         */
        int i = 1;
        for (SwitchParameters switchParameters:parameterSet.getSwitchParameters()){
            // 获取线程名
            String threadName = getThreadName(i);
            // 将线程名设置给SwitchParameters对象
            switchParameters.setThreadName(threadName);
            i++;
            // 创建一个AdvancedThread对象，并将其提交给线程池执行
            // AdvancedThread的构造函数中包含了线程名、SwitchParameters对象、函数列表、CountDownLatch对象、线程池对象和是否使用RSA算法的参数
            AdvancedThread advancedThread = new AdvancedThread(threadName, switchParameters, functionName, countDownLatch, fixedThreadPool, isRSA);
            fixedThreadPool.execute(advancedThread);//mode, ip, name, password,configureCiphers, port, loginUser,time

        }

        /**
         * 3:等待所有线程执行完成。关闭线程池。
         */
        // 等待所有线程执行完成
        countDownLatch.await();
        // 关闭线程池
        fixedThreadPool.shutdown();
    }


    /**
     * 生成线程名称
     *
     * @param i 线程编号
     * @return 线程名称，格式为 "threadname" + 时间戳 + 随机数
     */
    /*线程命名*/
    public String getThreadName(int i) {
        // 生成当前时间戳和随机数，拼接成字符串name
        String name = System.currentTimeMillis() + new Random().nextInt(100) +" ";
        // 返回线程名称，格式为 "threadname" + name
        return "threadname" + name;
    }
}
