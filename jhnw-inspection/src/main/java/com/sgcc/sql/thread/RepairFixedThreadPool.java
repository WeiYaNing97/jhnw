package com.sgcc.sql.thread;
import com.sgcc.share.domain.SwitchScanResult;
import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 修复多线程
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年07月29日 15:36
 */
public class RepairFixedThreadPool {

    // 用来存储线程名称的map
    public static Map threadNameMap = new HashMap();
    /**
     * newFixedThreadPool submit submit
     */
    public void Solution(ParameterSet parameterSet, List<List<SwitchScanResult>> problemList, List<String> problemIdStrings) throws InterruptedException {
        // 用于计数线程是否执行完成
        CountDownLatch countDownLatch = new CountDownLatch(parameterSet.getSwitchParameters().size());
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(parameterSet.getThreadCount());
        for (int i = 0 ; i<parameterSet.getSwitchParameters().size() ; i++){
            List<SwitchScanResult> switchScanResultList = problemList.get(i);
            List<SwitchScanResult> switchScanResults = new ArrayList<>();
            for (SwitchScanResult switchScanResult:switchScanResultList){
                // 查看 扫描出的问题 是否有问题
                if (!(switchScanResult.getIfQuestion().equals("无问题"))){
                    switchScanResults.add(switchScanResult);
                }
            }
            if (switchScanResults.size() != 0){
                // 如果有问题 查询对应交换机登录信息
                SwitchParameters SwitchParameters = parameterSet.getSwitchParameters().get(i);
                // 所有问题
                List<String> problemIds = problemIdStrings;
                String threadName = getThreadName(i);
                threadNameMap.put(threadName, threadName);
                SwitchParameters.setThreadName(threadName);
                fixedThreadPool.execute(new RepairFixedThread(threadName,SwitchParameters,switchScanResults,problemIds,countDownLatch,fixedThreadPool));
            }
        }
        countDownLatch.await();
        fixedThreadPool.shutdown();
    }

    public static void removeThread(String i) {
        threadNameMap.remove(i);
        System.out.println("删除线程Thread" + i + ", Hash表的Size：" + threadNameMap.size());
    }
    public static String getThreadName(int i) {
        String name = System.currentTimeMillis() + new Random().nextInt(100) +" ";
        return "threadname" + name;
    }

}