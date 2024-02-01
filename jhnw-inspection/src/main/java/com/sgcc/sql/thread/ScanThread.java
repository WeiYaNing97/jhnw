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
 * 扫描功能 线程
 */
/*Inspection Completed*/
public class ScanThread extends Thread  {

    SwitchParameters switchParameters = null;
    // 用于计数线程是否执行完成
    CountDownLatch countDownLatch = null;
    ExecutorService fixedThreadPool = null;
    boolean isRSA = true;
    // 为线程命名
    public ScanThread(String threadName,
                      SwitchParameters switchParameters,
                      CountDownLatch countDownLatch,ExecutorService fixedThreadPool,boolean isRSA) {
        super(threadName);
        /* 线程计数*/
        this.countDownLatch = countDownLatch;
        this.fixedThreadPool = fixedThreadPool;

        this.switchParameters = switchParameters;
        this.isRSA = isRSA;
    }

    @Override
    public void run() {
        try {
            /*与交换机交互方法类*/
            SwitchInteraction switchInteraction = new SwitchInteraction();

            //扫描方法 logInToGetBasicInformation
            switchInteraction.logInToGetBasicInformation(switchParameters,null,null,isRSA);

            /*扫描交换机过程中要求要有一个旋转的圆圈，用于取消圆圈旋转*/
            WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"scanThread:"+switchParameters.getIp()+":"+switchParameters.getThreadName());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ScanFixedThreadPool.removeThread(this.getName());
            countDownLatch.countDown();
        }
    }
}
