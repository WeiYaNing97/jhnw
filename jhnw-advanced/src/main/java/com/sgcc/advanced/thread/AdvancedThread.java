package com.sgcc.advanced.thread;
import com.sgcc.advanced.controller.ErrorPackage;
import com.sgcc.advanced.controller.LuminousAttenuation;
import com.sgcc.advanced.controller.OSPFFeatures;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.switchboard.ConnectToObtainInformation;
import com.sgcc.share.util.PathHelper;
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

    // 为线程命名
    public AdvancedThread(String threadName,
                          SwitchParameters switchParameters, List<String> functionName,
                      CountDownLatch countDownLatch, ExecutorService fixedThreadPool,boolean isRSA) {
        super(threadName);
        this.functionName = functionName;
        this.countDownLatch = countDownLatch;
        this.fixedThreadPool = fixedThreadPool;
        this.switchParameters = switchParameters;
        this.isRSA = isRSA;
    }

    @Override
    public void run() {
        /*int threadCount = ((ThreadPoolExecutor)fixedThreadPool).getActiveCount();
        System.err.println("活跃线程数："+threadCount);*/

        ConnectToObtainInformation connectToObtainInformation = new ConnectToObtainInformation();
        AjaxResult basicInformationList_ajaxResult = connectToObtainInformation.connectSwitchObtainBasicInformation(switchParameters,isRSA);

        if (!(basicInformationList_ajaxResult.get("msg").equals("未定义该交换机获取基本信息命令及分析"))
        && !(basicInformationList_ajaxResult.get("msg").equals("交换机连接失败"))
        && !(basicInformationList_ajaxResult.get("msg").equals("交换机登录信息获取失败"))) {

            this.switchParameters = (SwitchParameters) basicInformationList_ajaxResult.get("data");
            for (String function:functionName){

                switch (function){

                    case "OSPF":
                        OSPFFeatures ospfFeatures = new OSPFFeatures();
                        ospfFeatures.getOSPFValues(switchParameters);
                        break;

                    case "光衰":
                        LuminousAttenuation luminousAttenuation = new LuminousAttenuation();
                        luminousAttenuation.obtainLightDecay(switchParameters);
                        break;

                    case "错误包":
                        ErrorPackage errorPackage = new ErrorPackage();
                        errorPackage.getErrorPackage(switchParameters);
                        break;
                }
            }

        }else {
                //运行分析未定义该交换机获取基本信息命令及分析
                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "基本信息",
                        "系统信息:"+switchParameters.getIp() +
                                "基本信息:"+
                                basicInformationList_ajaxResult.get("msg")+"\r\n");
        }

        if (!(basicInformationList_ajaxResult.get("msg").equals("交换机连接失败"))
                && !(basicInformationList_ajaxResult.get("msg").equals("交换机登录信息获取失败"))){
            /*关闭连接交换机*/
            if (switchParameters.getMode().equalsIgnoreCase("ssh")){
                switchParameters.getConnectMethod().closeConnect(switchParameters.getSshConnect());
            }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
                switchParameters.getTelnetSwitchMethod().closeSession(switchParameters.getTelnetComponent());
            }
        }

        /** 运行分析扫描 及 快照功能 旋转的圆圈，用于取消圆圈旋转 */
        /*扫描交换机过程中要求要有一个旋转的圆圈，用于取消圆圈旋转*/
        WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"scanThread:"+switchParameters.getIp()+":"+switchParameters.getThreadName());

        AdvancedThreadPool.removeThread(this.getName());
        countDownLatch.countDown();

        //将exes转换为ThreadPoolExecutor,ThreadPoolExecutor有方法 getActiveCount()可以得到当前活动线程数
        /*threadCount = ((ThreadPoolExecutor)fixedThreadPool).getActiveCount();
        System.err.println("活跃线程数："+threadCount);*/
    }
}
