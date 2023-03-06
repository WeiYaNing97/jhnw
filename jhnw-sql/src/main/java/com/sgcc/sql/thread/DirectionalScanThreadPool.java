package com.sgcc.sql.thread;

import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.sql.domain.TotalQuestionTable;
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
    public static void switchLoginInformations(List<Object[]> objects, List<TotalQuestionTable> totalQuestionTables, String ScanningTime, LoginUser login, int threads) throws InterruptedException {

        // 用于计数线程是否执行完成
        CountDownLatch countDownLatch = new CountDownLatch(objects.size());
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threads);
        int i=0;
        for (Object[] objects3:objects){
            String mode = (String)objects3[0];
            String ip = (String)objects3[1];
            String name = (String)objects3[2];
            String password = (String)objects3[3];
            String configureCiphers = (String) objects3[4];
            int port = (int) objects3[5];
            LoginUser loginUser = login;
            String time = ScanningTime;
            String threadName = getThreadName(i);
            i++;
            threadNameMap.put(threadName, threadName);

            fixedThreadPool.execute(new DirectionalScanThread(threadName,mode, ip, name, password,configureCiphers, port, loginUser,time,totalQuestionTables,countDownLatch,fixedThreadPool));
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