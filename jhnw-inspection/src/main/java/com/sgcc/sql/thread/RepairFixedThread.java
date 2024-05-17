package com.sgcc.sql.thread;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.domain.SwitchScanResult;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.PathHelper;
import com.sgcc.share.webSocket.WebSocketService;
import com.sgcc.sql.controller.SolveProblemController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class RepairFixedThread extends Thread {

    SwitchParameters switchParameters = null;
    List<SwitchScanResult> switchScanResults = null;
    List<String> problemIds = null;
    // 用于计数线程是否执行完成
    CountDownLatch countDownLatch = null;
    ExecutorService fixedThreadPool = null;
    // 为线程命名

    /**
    * @Description 修复交换机线程
    * @author charles
    * @createTime 2024/5/8 15:48
    * @desc
    * @param threadName	线程名
     * @param SwitchParameters	交换机登录信息
     * @param switchScanResults	 交换机问题集合
     * @param problemIds	所有问题ID
     * @param countDownLatch	线程池计数器
     * @param fixedThreadPool	线程池
     * @return
    */
    public RepairFixedThread(String threadName,
                             SwitchParameters SwitchParameters,
                             List<SwitchScanResult> switchScanResults,List<String> problemIds,
                             CountDownLatch countDownLatch,ExecutorService fixedThreadPool) {
        super(threadName);
        this.switchParameters = SwitchParameters;
        this.switchScanResults = switchScanResults;
        this.problemIds = problemIds;
        this.countDownLatch = countDownLatch;
        this.fixedThreadPool = fixedThreadPool;
    }

    @Override
    public void run() {
        try {

            /** 修复交换机问题方法*/
            SolveProblemController solveProblemController = new SolveProblemController();
            /**
             * @param SwitchParameters	交换机登录信息
             * @param switchScanResults	 交换机问题集合
             * @param problemIds	所有问题ID
            */
            solveProblemController.batchSolution(switchParameters,switchScanResults,problemIds);

            WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"scanThread:"+switchParameters.getIp()+":"+switchParameters.getThreadName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RepairFixedThreadPool.removeThread(this.getName());
            countDownLatch.countDown();
        }
    }

}
