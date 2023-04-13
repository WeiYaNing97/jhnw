package com.sgcc.sql.thread;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.sql.controller.SwitchInteraction;
import com.sgcc.sql.parametric.SwitchParameters;
import com.sgcc.sql.test.MainThread;
import com.sgcc.sql.util.PathHelper;
import com.sgcc.sql.webSocket.WebSocketService;

import java.io.IOException;
import java.util.concurrent.*;

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

            //将exes转换为ThreadPoolExecutor,ThreadPoolExecutor有方法 getActiveCount()可以得到当前活动线程数
            int threadCount = ((ThreadPoolExecutor)fixedThreadPool).getActiveCount();
            System.err.println("活跃线程数："+threadCount);

            SwitchInteraction switchInteraction = new SwitchInteraction();
            //扫描方法 logInToGetBasicInformation
            //传参 ：mode连接方式, ip 地址, name 用户名, password 密码, port 端口号，loginUser 登录人信息，time 扫描时间
            // List<TotalQuestionTable> totalQuestionTables  用于 专项扫描
            // 扫描一台交换机 的 所以问题

            AjaxResult ajaxResult = switchInteraction.logInToGetBasicInformation(switchParameters,null);

            WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"scanThread:"+switchParameters.getIp());

            if (ajaxResult.get("msg").equals("交换机连接失败")){
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"风险:"+switchParameters.getIp() +"问题:交换机连接失败\r\n");
                try {
                    PathHelper.writeDataToFile("风险:"+switchParameters.getIp() +"问题:交换机连接失败\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (ajaxResult.get("msg").equals("未定义该交换机获取基本信息命令及分析")){
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"风险:"+switchParameters.getIp() + "问题:未定义该交换机获取基本信息命令及分析\r\n");
                try {
                    PathHelper.writeDataToFile("风险:"+switchParameters.getIp() + "问题:未定义该交换机获取基本信息命令及分析\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ScanFixedThreadPool.removeThread(this.getName());
            countDownLatch.countDown();
        }
        //将exes转换为ThreadPoolExecutor,ThreadPoolExecutor有方法 getActiveCount()可以得到当前活动线程数
        int threadCount = ((ThreadPoolExecutor)fixedThreadPool).getActiveCount();
        System.err.println("活跃线程数："+threadCount);
    }

}
