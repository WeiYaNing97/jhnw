package com.sgcc.sql.thread;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.domain.SwitchScanResult;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.webSocket.WebSocketService;
import com.sgcc.sql.controller.SolveProblemController;

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

            //将exes转换为ThreadPoolExecutor,ThreadPoolExecutor有方法 getActiveCount()可以得到当前活动线程数
            int threadCount = ((ThreadPoolExecutor)fixedThreadPool).getActiveCount();
            System.err.println("活跃线程数："+threadCount);

            SolveProblemController solveProblemController = new SolveProblemController();
            AjaxResult ajaxResult = solveProblemController.batchSolution(switchParameters,switchScanResults,problemIds);
            WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"scanThread:"+switchParameters.getIp()+":"+switchParameters.getThreadName());
            if (ajaxResult.get("msg").equals("未定义该交换机获取基本信息命令及分析")){
                System.err.println("\r\n未定义该交换机获取基本信息命令及分析\r\n");
            }else if (ajaxResult.get("msg").equals("交换机连接失败")){
                System.err.println("\r\n交换机连接失败\r\n");
            }else if (ajaxResult.get("msg").equals("未定义修复命令")){
                System.err.println("\r\n未定义修复命令\r\n");
            }else if (ajaxResult.get("msg").equals("修复结束")){
                System.err.println("\r\n修复结束\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RepairFixedThreadPool.removeThread(this.getName());
            countDownLatch.countDown();
        }
        //将exes转换为ThreadPoolExecutor,ThreadPoolExecutor有方法 getActiveCount()可以得到当前活动线程数
        int threadCount = ((ThreadPoolExecutor)fixedThreadPool).getActiveCount();
        System.err.println("活跃线程数："+threadCount);
    }

}
