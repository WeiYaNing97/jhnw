package com.sgcc.sql.thread;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.PathHelper;
import com.sgcc.sql.controller.SwitchInteraction;
import com.sgcc.share.webSocket.WebSocketService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * 全部问题多线程
 */
public class ScanThread extends Thread  {

    SwitchParameters switchParameters = null;
    // 用于计数线程是否执行完成
    CountDownLatch countDownLatch = null;
    ExecutorService fixedThreadPool = null;
    // 为线程命名
    public ScanThread(String threadName,
                      SwitchParameters switchParameters,
                      CountDownLatch countDownLatch,ExecutorService fixedThreadPool) {
        super(threadName);
        this.countDownLatch = countDownLatch;
        this.fixedThreadPool = fixedThreadPool;
        this.switchParameters = switchParameters;
    }

    @Override
    public void run() {

        try {
            PathHelper.writeDataToFileByName("IP:"+switchParameters.getIp()+"开始时间：" + "\r\n","线程");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            //将exes转换为ThreadPoolExecutor,ThreadPoolExecutor有方法 getActiveCount()可以得到当前活动线程数
            int threadCount = ((ThreadPoolExecutor)fixedThreadPool).getActiveCount();
            System.err.println("活跃线程数："+threadCount);
            SwitchInteraction switchInteraction = new SwitchInteraction();
            //扫描方法 logInToGetBasicInformation
            AjaxResult ajaxResult = switchInteraction.logInToGetBasicInformation(switchParameters,null,null);
            WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"scanThread:"+switchParameters.getIp()+":"+switchParameters.getThreadName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ScanFixedThreadPool.removeThread(this.getName());
            countDownLatch.countDown();
        }

        try {
            PathHelper.writeDataToFileByName("IP:"+switchParameters.getIp()+"结束时间：" + "\r\n","线程");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //将exes转换为ThreadPoolExecutor,ThreadPoolExecutor有方法 getActiveCount()可以得到当前活动线程数
        int threadCount = ((ThreadPoolExecutor)fixedThreadPool).getActiveCount();
        System.err.println("活跃线程数："+threadCount);

    }

}
