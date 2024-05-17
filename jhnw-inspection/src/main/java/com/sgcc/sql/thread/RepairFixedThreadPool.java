package com.sgcc.sql.thread;
import com.sgcc.share.domain.SwitchScanResult;
import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 修复逻辑 多线程
 */
public class RepairFixedThreadPool {

    // 用来存储线程名称的map
    public static Map threadNameMap = new HashMap();
    /**
    * @Description
    * @author charles
    * @createTime 2024/5/8 15:36
    * @desc
    * @param parameterSet	交换机登录信息集合
     * @param switchScanResultMap	 交换机IP 和 问题集合对应的 map集合
     * @param problemIdStrings	交换机所有扫描结果ID
     * @return
    */
    public static void Solution(ParameterSet parameterSet, HashMap<String,List<SwitchScanResult>> switchScanResultMap  , List<String> problemIdStrings) throws InterruptedException {

        // 用于计数线程是否执行完成
        CountDownLatch countDownLatch = new CountDownLatch(parameterSet.getSwitchParameters().size());
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(parameterSet.getThreadCount());

        /** 遍历交换机登录集合 */
        for (int i = 0 ; i<parameterSet.getSwitchParameters().size() ; i++){

            /* 根据交换机登录集合中的 登录信息 的 登录IP
            * 获取map集合中的 交换机问题集合*/
            List<SwitchScanResult> switchScanResultList = switchScanResultMap.get( parameterSet.getSwitchParameters().get(i).getIp() );

            /* 筛选交换机问题集合中 有问题、异常 的实体类数据 */
            List<SwitchScanResult> switchScanResults = new ArrayList<>();
            for (SwitchScanResult switchScanResult:switchScanResultList){
                // 查看 扫描出的问题 是否有问题
                if (switchScanResult.getIfQuestion().equals("有问题")){
                    switchScanResults.add(switchScanResult);
                }
            }

            /** 如果 异常交换机问题集合 元素长度不为0
             * 则进行修复逻辑*/
            if (switchScanResults.size() != 0){

                // 交换机登录集合中的 登录信息
                SwitchParameters SwitchParameters = parameterSet.getSwitchParameters().get(i);

                // 交换机所有扫描结果ID
                List<String> problemIds = problemIdStrings;

                /* 设置线程名*/
                String threadName = getThreadName(i);

                /*加入map*/
                threadNameMap.put(threadName, threadName);

                SwitchParameters.setThreadName(threadName);

                /**
                 * @Description 修复交换机线程
                 * @param threadName	线程名
                 * @param SwitchParameters	交换机登录信息
                 * @param switchScanResults	 交换机问题集合
                 * @param problemIds	所有问题ID
                 * @param countDownLatch	线程池计数器
                 * @param fixedThreadPool	线程池
                 * @return
                 */
                fixedThreadPool.execute(new RepairFixedThread(threadName,SwitchParameters,switchScanResults,problemIds,countDownLatch,fixedThreadPool));

            }

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