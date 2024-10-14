package com.sgcc.advanced.thread;

import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AdvancedThreadPool {

    // 用来存储线程名称的map
    public static Map threadNameMap = new HashMap();

    /**
     * 运行分析线程池
     *
     * @param parameterSet 参数集合
     * @param functionName 需要分析的函数列表
     * @param isRSA 是否使用RSA算法
     * @throws InterruptedException 如果等待线程中断，则抛出 InterruptedException 异常
     */
    public static void switchLoginInformations(ParameterSet parameterSet, List<String> functionName,boolean isRSA) throws InterruptedException {
        // 创建一个CountDownLatch对象，用于计数线程是否执行完成
        // 计数器的初始值为参数集合中SwitchParameters的数量
        // 用于计数线程是否执行完成
        CountDownLatch countDownLatch = new CountDownLatch(parameterSet.getSwitchParameters().size());
        // 创建一个固定大小的线程池
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(parameterSet.getThreadCount());

        int i = 1;
        for (SwitchParameters switchParameters:parameterSet.getSwitchParameters()){
            // 获取线程名
            String threadName = getThreadName(i);
            // 将线程名设置给SwitchParameters对象
            switchParameters.setThreadName(threadName);
            i++;

            // 将线程名作为键，线程名作为值，放入threadNameMap中
            threadNameMap.put(threadName, threadName);

            // 创建一个AdvancedThread对象，并将其提交给线程池执行
            // AdvancedThread的构造函数中包含了线程名、SwitchParameters对象、函数列表、CountDownLatch对象、线程池对象和是否使用RSA算法的参数
            fixedThreadPool.execute(new AdvancedThread(threadName,switchParameters,functionName,countDownLatch,fixedThreadPool,isRSA));//mode, ip, name, password,configureCiphers, port, loginUser,time
        }

        // 等待所有线程执行完成
        countDownLatch.await();

        // 关闭线程池
        fixedThreadPool.shutdown();
    }


    /**
     * 从线程名称映射中删除指定线程名称的线程。
     *
     * @param threadname 要删除的线程名称
     * @return 无返回值
     */
    public static void removeThread(String threadname) {
        // 从线程名称映射中删除指定线程名称的线程
        threadNameMap.remove(threadname);
        // 输出删除线程的信息及Hash表的当前大小
        System.err.println("删除线程Thread" + threadname + ", Hash表的Size：" + threadNameMap.size());
    }


    /**
     * 生成线程名称
     *
     * @param i 线程编号
     * @return 线程名称，格式为 "threadname" + 时间戳 + 随机数
     */
    /*线程命名*/
    public static String getThreadName(int i) {
        // 生成当前时间戳和随机数，拼接成字符串name
        String name = System.currentTimeMillis() + new Random().nextInt(100) +" ";
        // 返回线程名称，格式为 "threadname" + name
        return "threadname" + name;
    }


}
