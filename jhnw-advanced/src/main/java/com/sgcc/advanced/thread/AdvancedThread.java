package com.sgcc.advanced.thread;
import com.sgcc.advanced.controller.*;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.switchboard.ConnectToObtainInformation;
import com.sgcc.share.util.PathHelper;
import com.sgcc.share.util.WorkThreadMonitor;
import com.sgcc.share.webSocket.WebSocketService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
public class AdvancedThread extends Thread {
    //运行分析名称列表
    List<String> functionName = null;
    SwitchParameters switchParameters = null;
    // 用于计数线程是否执行完成
    CountDownLatch countDownLatch = null;
    ExecutorService fixedThreadPool = null;
    boolean isRSA = true;

    /**
     * 线程类构造函数，用于初始化线程
     *
     * @param threadName 线程名称
     * @param switchParameters 切换参数
     * @param functionName 需要分析的函数列表
     * @param countDownLatch 计数器，用于等待所有线程执行完毕
     * @param fixedThreadPool 线程池
     * @param isRSA 是否使用RSA算法
     */
    public AdvancedThread(String threadName,
                          SwitchParameters switchParameters, List<String> functionName,
                      CountDownLatch countDownLatch, ExecutorService fixedThreadPool,boolean isRSA) {
        // 调用父类构造函数，传入线程名称
        super(threadName);
        // 将传入的函数列表赋值给当前对象的functionName属性
        this.functionName = functionName;
        // 将传入的计数器赋值给当前对象的countDownLatch属性
        this.countDownLatch = countDownLatch;
        // 将传入的线程池赋值给当前对象的fixedThreadPool属性
        this.fixedThreadPool = fixedThreadPool;
        // 将传入的切换参数赋值给当前对象的switchParameters属性
        this.switchParameters = switchParameters;
        // 将传入的isRSA值赋值给当前对象的isRSA属性
        this.isRSA = isRSA;
    }


    /**
     * 执行线程任务
     */
    @Override
    public void run() {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return;
        }


        /**
         * 1:连接交换机，并获取交换机基本信息
         */
        // 创建ConnectToObtainInformation对象
        ConnectToObtainInformation connectToObtainInformation = new ConnectToObtainInformation();
        // 调用connectSwitchObtainBasicInformation方法获取交换机基本信息
        AjaxResult basicInformationList_ajaxResult = connectToObtainInformation.connectSwitchObtainBasicInformation(switchParameters,isRSA);


        /**
         * 2:判断交换机是否连接成功、交换机基本信息获取是否获取成功
         *    如果交换机连接失败或交换机基本信息获取失败，则记录异常信息并继续执行，关闭交换机及连接
         *    如果交换机连接成功、交换机基本信息获取成功，则执行下一步
         *
         *    遍历需要执行的功能列表，进行中断检查，如果中断检查为false，并执行不同的方法
         */
        // 判断返回的信息是否表示获取基本信息命令未定义、交换机连接失败或交换机登录信息获取失败
        if ((basicInformationList_ajaxResult.get("msg").equals("未定义该交换机获取基本信息命令及分析"))
                || (basicInformationList_ajaxResult.get("msg").equals("交换机连接失败"))
                || (basicInformationList_ajaxResult.get("msg").equals("交换机登录信息获取失败"))) {
            // 如果获取基本信息失败，则记录异常信息
            //运行分析未定义该交换机获取基本信息命令及分析
            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "基本信息",
                    "系统信息:"+switchParameters.getIp() +
                            "基本信息:"+
                            basicInformationList_ajaxResult.get("msg")+"\r\n");
        } else {
            // 将返回的数据转换为SwitchParameters对象
            this.switchParameters = (SwitchParameters) basicInformationList_ajaxResult.get("data");
            // 遍历需要执行的功能列表
            for (String function:functionName){
                // 检查线程中断标志
                if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
                    // 如果线程中断标志为true，则直接跳出循环
                    break;
                }
                // 根据功能名称执行不同的方法
                switch (function){
                    case "OSPF":
                        // 创建OSPFFeatures对象并执行getOSPFValues方法
                        OSPFFeatures ospfFeatures = new OSPFFeatures();
                        ospfFeatures.getOSPFValues(switchParameters);
                        break;
                    case "光衰":
                        // 创建LuminousAttenuation对象并执行obtainLightDecay方法
                        LuminousAttenuation luminousAttenuation = new LuminousAttenuation();
                        luminousAttenuation.obtainLightDecay(switchParameters);
                        break;
                    case "错误包":
                        // 创建ErrorPackage对象并执行getErrorPackage方法
                        ErrorPackage errorPackage = new ErrorPackage();
                        errorPackage.getErrorPackage(switchParameters);
                        break;
                    case "路由聚合":
                        // 创建RouteAggregation对象并执行obtainAggregationResults方法
                        RouteAggregation routeAggregation = new RouteAggregation();
                        routeAggregation.obtainAggregationResults(switchParameters);
                        break;
                    case "链路捆绑":
                        // 创建LinkBundling对象并执行linkBindingInterface方法
                        LinkBundling linkBundling = new LinkBundling();
                        linkBundling.linkBindingInterface(switchParameters);
                        break;
                }
            }
        }


        /**
         * 3:关闭交换机连接，并减少计数器计数
         *      发送WebSocket消息，通知扫描完成,扫描交换机过程中要求要有一个旋转的圆圈，用于取消圆圈旋转
         *      减少计数器计数
         */
        if (!(basicInformationList_ajaxResult.get("msg").equals("交换机连接失败"))
                && !(basicInformationList_ajaxResult.get("msg").equals("交换机登录信息获取失败"))){
            if (switchParameters.getMode().equalsIgnoreCase("ssh")){
                // 如果是SSH连接，则调用closeConnect方法关闭连接
                switchParameters.getConnectMethod().closeConnect(switchParameters.getSshConnect());
            } else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
                // 如果是Telnet连接，则调用closeSession方法关闭会话
                switchParameters.getTelnetSwitchMethod().closeSession(switchParameters.getTelnetComponent());
            }
        }
        // 发送WebSocket消息，通知扫描完成,扫描交换机过程中要求要有一个旋转的圆圈，用于取消圆圈旋转
        WebSocketService webSocketService = new WebSocketService();
        webSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),
                "scanThread:"+switchParameters.getIp()+":"+switchParameters.getThreadName());
        // 减少计数器计数
        countDownLatch.countDown();
    }
    
}
