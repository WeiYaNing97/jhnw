package com.sgcc.sql.thread;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.PathHelper;
import com.sgcc.sql.controller.SwitchInteraction;
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.share.webSocket.WebSocketService;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class DirectionalScanThread extends Thread  {

    SwitchParameters switchParameters = null;
    List<TotalQuestionTable> totalQuestionTables = null;
    List<String> advancedName = null;
    // 用于计数线程是否执行完成
    CountDownLatch countDownLatch = null;
    ExecutorService fixedThreadPool = null;
    // 为线程命名
    public DirectionalScanThread(String threadName,
                                 SwitchParameters switchParameters,List<TotalQuestionTable> totalQuestionTables,List<String> advancedName,
                                 CountDownLatch countDownLatch, ExecutorService fixedThreadPool) {
        super(threadName);
        this.switchParameters = switchParameters;
        this.totalQuestionTables = totalQuestionTables;
        this.advancedName = advancedName;
        this.countDownLatch = countDownLatch;
        this.fixedThreadPool = fixedThreadPool;
    }

    @Override
    public void run() {

        try {
            try {
                PathHelper.writeDataToFileByName("IP:"+switchParameters.getIp()+"开始时间：" + "\r\n","线程");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //将exes转换为ThreadPoolExecutor,ThreadPoolExecutor有方法 getActiveCount()可以得到当前活动线程数
            int threadCount = ((ThreadPoolExecutor)fixedThreadPool).getActiveCount();
            System.err.println("活跃线程数："+threadCount);
            SwitchInteraction switchInteraction = new SwitchInteraction();
            //扫描方法 logInToGetBasicInformation  传参 ：mode连接方式, ip 地址, name 用户名, password 密码, port 端口号
            AjaxResult ajaxResult = switchInteraction.logInToGetBasicInformation(switchParameters, totalQuestionTables,advancedName);
            WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"scanThread:"+switchParameters.getIp()+":"+switchParameters.getThreadName());
            if (ajaxResult.get("msg").equals("交换机连接失败")){
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"风险:"+switchParameters.getIp() + ":交换机连接失败\r\n");
                try {
                    PathHelper.writeDataToFile("风险:"+switchParameters.getIp() + ":交换机连接失败\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (ajaxResult.get("msg").equals("未定义该交换机获取基本信息命令及分析")){
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"风险:"+switchParameters.getIp() + ":未定义该交换机获取基本信息命令及分析\r\n");
                try {
                    PathHelper.writeDataToFile("风险:"+switchParameters.getIp() + ":未定义该交换机获取基本信息命令及分析\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //ip_information.put(ip+loginUser.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DirectionalScanThreadPool.removeThread(this.getName());
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
