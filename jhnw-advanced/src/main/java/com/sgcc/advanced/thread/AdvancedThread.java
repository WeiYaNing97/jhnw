package com.sgcc.advanced.thread;
import com.sgcc.advanced.controller.ErrorPackage;
import com.sgcc.advanced.controller.LuminousAttenuation;
import com.sgcc.advanced.controller.OSPFFeatures;
import com.sgcc.common.core.domain.AjaxResult;
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
    //高级功能名称列表
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

        try {
            PathHelper.writeDataToFileByName("IP:"+switchParameters.getIp()+" 开始时间：" + "\r\n","线程");
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*int threadCount = ((ThreadPoolExecutor)fixedThreadPool).getActiveCount();
        System.err.println("活跃线程数："+threadCount);*/
        ConnectToObtainInformation connectToObtainInformation = new ConnectToObtainInformation();
        AjaxResult basicInformationList_ajaxResult = connectToObtainInformation.connectSwitchObtainBasicInformation(switchParameters,isRSA);

        //AjaxResult basicInformationList_ajaxResult = getBasicInformationList(user_String,user_Object);   //getBasicInformationList
        if (!(basicInformationList_ajaxResult.get("msg").equals("未定义该交换机获取基本信息命令及分析"))
        && !(basicInformationList_ajaxResult.get("msg").equals("交换机连接失败"))) {
            this.switchParameters = (SwitchParameters) basicInformationList_ajaxResult.get("data");
            for (String function:functionName){
                switch (function){
                    case "OSPF":
                        OSPFFeatures ospfFeatures = new OSPFFeatures();
                        ospfFeatures.getOSPFValues(switchParameters);
                        break;
                        /*try {
                            PathHelper.writeDataToFileByName(switchParameters.getIp()+" OSPF开始时间","高级功能的开始结束");
                            OSPFFeatures ospfFeatures = new OSPFFeatures();
                            ospfFeatures.getOSPFValues(switchParameters);
                            PathHelper.writeDataToFileByName(switchParameters.getIp()+" OSPF结束时间","高级功能的开始结束");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            break;
                        }*/
                    case "光衰":
                        LuminousAttenuation luminousAttenuation = new LuminousAttenuation();
                        luminousAttenuation.obtainLightDecay(switchParameters);
                        break;
                        /*try {
                            PathHelper.writeDataToFileByName(switchParameters.getIp()+" 光衰开始时间","高级功能的开始结束");
                            LuminousAttenuation luminousAttenuation = new LuminousAttenuation();
                            luminousAttenuation.obtainLightDecay(switchParameters);
                            PathHelper.writeDataToFileByName(switchParameters.getIp()+" 光衰结束时间","高级功能的开始结束");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            break;
                        }*/
                    case "误码率":
                        ErrorPackage errorPackage = new ErrorPackage();
                        errorPackage.getErrorPackage(switchParameters);
                        break;
                        /*try {
                            PathHelper.writeDataToFileByName(switchParameters.getIp()+" 误码率开始时间","高级功能的开始结束");
                            ErrorPackage errorPackage = new ErrorPackage();
                            errorPackage.getErrorPackage(switchParameters);
                            PathHelper.writeDataToFileByName(switchParameters.getIp()+" 误码率结束时间","高级功能的开始结束");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            break;
                        }*/
                }
            }

        }else {
            try {
                // todo 高级功能线程 未定义该交换机获取基本信息命令及分析
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"系统信息:"+switchParameters.getIp() +"基本信息："+
                        "未定义该交换机获取基本信息命令及分析\r\n");
                PathHelper.writeDataToFileByName("系统信息:"+switchParameters.getIp()+"成功基本信息："+
                        "未定义该交换机获取基本信息命令及分析\r\n","基本信息");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!(basicInformationList_ajaxResult.get("msg").equals("交换机连接失败"))){
            /*关闭连接交换机*/
            if (switchParameters.getMode().equalsIgnoreCase("ssh")){
                switchParameters.getConnectMethod().closeConnect(switchParameters.getSshConnect());
            }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
                switchParameters.getTelnetSwitchMethod().closeSession(switchParameters.getTelnetComponent());
            }
        }

        AdvancedThreadPool.removeThread(this.getName());
        countDownLatch.countDown();

        try {
            PathHelper.writeDataToFileByName("IP:"+switchParameters.getIp()+" 结束时间：" + "\r\n","线程");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将exes转换为ThreadPoolExecutor,ThreadPoolExecutor有方法 getActiveCount()可以得到当前活动线程数
        /*threadCount = ((ThreadPoolExecutor)fixedThreadPool).getActiveCount();
        System.err.println("活跃线程数："+threadCount);*/
    }
}
