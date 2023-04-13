package com.sgcc.sql.thread;

import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.sql.parametric.ParameterSet;
import com.sgcc.sql.parametric.SwitchParameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 专项扫描多线程
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年08月09日 14:04
 */
public class DirectionalScanThreadPool {

    // 用来存储线程名称的map
    public static Map threadNameMap = new HashMap();

    /**
     * newFixedThreadPool submit submit
     */
    public static void switchLoginInformations(ParameterSet parameterSet,List<TotalQuestionTable> totalQuestionTables) throws InterruptedException {

        // 用于计数线程是否执行完成
        CountDownLatch countDownLatch = new CountDownLatch(parameterSet.getSwitchParameters().size());
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(parameterSet.getThreadCount());
        int i=0;
        for (SwitchParameters switchParameters:parameterSet.getSwitchParameters()){

            String threadName = getThreadName(i);
            i++;
            threadNameMap.put(threadName, threadName);
            switchParameters.setThreadName(threadName);
            fixedThreadPool.execute(new DirectionalScanThread(threadName,switchParameters,totalQuestionTables,countDownLatch,fixedThreadPool));
        }
        countDownLatch.await();
    }


    public static void removeThread(String i) {
        threadNameMap.remove(i);
        System.out.println("删除线程Thread" + i + ", Hash表的Size：" + threadNameMap.size());
    }
    public static String getThreadName(int i) {
        return "threadname"+i;
    }

}