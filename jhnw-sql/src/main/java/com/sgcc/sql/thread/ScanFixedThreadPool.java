package com.sgcc.sql.thread;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.sql.controller.SwitchInteraction;
import com.sgcc.sql.parametric.ParameterSet;
import com.sgcc.sql.parametric.SwitchParameters;
import com.sgcc.sql.util.PathHelper;
import com.sgcc.sql.webSocket.WebSocketService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 扫描 全部问题 多线程
 *
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年07月29日 15:36
 */
public class ScanFixedThreadPool {

    // 用来存储线程名称的map
    public static Map threadNameMap = new HashMap();

    /**
     * newFixedThreadPool submit submit
     */
    public static void switchLoginInformations(ParameterSet parameterSet) throws InterruptedException {

        // 用于计数线程是否执行完成
        CountDownLatch countDownLatch = new CountDownLatch(parameterSet.getSwitchParameters().size());
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(parameterSet.getThreadCount());

        int i = 1;
        for (SwitchParameters switchParameters:parameterSet.getSwitchParameters()){

            String threadName = getThreadName(i);
            switchParameters.setThreadName(threadName);
            i++;
            threadNameMap.put(threadName, threadName);

            fixedThreadPool.execute(new ScanThread(threadName,switchParameters,countDownLatch,fixedThreadPool));//mode, ip, name, password,configureCiphers, port, loginUser,time
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