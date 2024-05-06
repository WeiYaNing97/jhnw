package com.sgcc.sql.thread;

import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.sql.controller.SwitchInteraction;
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.share.webSocket.WebSocketService;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
* @Description 专项扫描 及 定时任务 多线程
* @author charles
* @createTime 2024/5/6 15:12
* @desc
 * @return
*/
public class DirectionalScanThread extends Thread  {

    SwitchParameters switchParameters = null;
    List<TotalQuestionTable> totalQuestionTables = null;
    List<String> advancedName = null;
    CountDownLatch countDownLatch = null;// 用于计数线程是否执行完成
    ExecutorService fixedThreadPool = null;
    boolean isRSA = true;

    // 为线程命名
    public DirectionalScanThread(String threadName,
                                 SwitchParameters switchParameters,
                                 List<TotalQuestionTable> totalQuestionTables,
                                 List<String> advancedName,
                                 CountDownLatch countDownLatch, ExecutorService fixedThreadPool,boolean isRSA) {
        super(threadName);
        this.switchParameters = switchParameters;
        this.totalQuestionTables = totalQuestionTables;
        this.advancedName = advancedName;
        this.countDownLatch = countDownLatch;
        this.fixedThreadPool = fixedThreadPool;
        this.isRSA = isRSA;
    }

    @Override
    public void run() {
        try {

            SwitchInteraction switchInteraction = new SwitchInteraction();
            //扫描方法 logInToGetBasicInformation  传参 ：mode连接方式, ip 地址, name 用户名, password 密码, port 端口号
            switchInteraction.logInToGetBasicInformation(switchParameters, totalQuestionTables,advancedName,isRSA);

            WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"scanThread:"+switchParameters.getIp()+":"+switchParameters.getThreadName());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DirectionalScanThreadPool.removeThread(this.getName());
            countDownLatch.countDown();
        }
        //将exes转换为ThreadPoolExecutor,ThreadPoolExecutor有方法 getActiveCount()可以得到当前活动线程数
        /*int threadCount = ((ThreadPoolExecutor)fixedThreadPool).getActiveCount();
        System.err.println("活跃线程数："+threadCount);*/
    }
}
