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
     * 高级功能线程池
     *
     */
    public static void switchLoginInformations(ParameterSet parameterSet, List<String> functionName,boolean isRSA) throws InterruptedException {
        // 用于计数线程是否执行完成
        CountDownLatch countDownLatch = new CountDownLatch(parameterSet.getSwitchParameters().size());
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(parameterSet.getThreadCount());

        int i = 1;
        for (SwitchParameters switchParameters:parameterSet.getSwitchParameters()){
            /*获取线程名*/
            String threadName = getThreadName(i);
            switchParameters.setThreadName(threadName);
            i++;
            /*加入map*/
            threadNameMap.put(threadName, threadName);
            fixedThreadPool.execute(new AdvancedThread(threadName,switchParameters,functionName,countDownLatch,fixedThreadPool,isRSA));//mode, ip, name, password,configureCiphers, port, loginUser,time
        }

        countDownLatch.await();
        /*关闭线程池*/
        fixedThreadPool.shutdown();
    }

    public static void removeThread(String threadname) {
        threadNameMap.remove(threadname);
        System.err.println("删除线程Thread" + threadname + ", Hash表的Size：" + threadNameMap.size());
    }
    /*线程命名*/
    public static String getThreadName(int i) {
        String name = System.currentTimeMillis() + new Random().nextInt(100) +" ";
        return "threadname" + name;
    }
}
