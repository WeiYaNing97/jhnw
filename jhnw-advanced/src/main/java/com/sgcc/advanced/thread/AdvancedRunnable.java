package com.sgcc.advanced.thread;
import com.sgcc.advanced.controller.ErrorPackage;
import com.sgcc.advanced.controller.LuminousAttenuation;
import com.sgcc.advanced.controller.OSPFFeatures;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.switchboard.ConnectToObtainInformation;
import com.sgcc.share.util.PathHelper;
import com.sgcc.share.webSocket.WebSocketService;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class AdvancedRunnable {
    public  void switchLoginInformations(ParameterSet parameterSet, List<String> functionName,boolean isRSA) {
        ExecutorService executorService = Executors.newFixedThreadPool(parameterSet.getThreadCount(), new NamedThreadFactory());
        int i = 1;
        for (SwitchParameters switchParameters:parameterSet.getSwitchParameters()){
            executorService.execute(new Task(switchParameters,functionName,isRSA));//mode, ip, name, password,configureCiphers, port, loginUser,time
        }
        executorService.shutdown();
        System.err.println("executorService.shutdown()线程结束");
    }
    static class NamedThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            Random random = new Random();
            thread.setName("Thread-" + System.currentTimeMillis() + random.nextInt(100));
            return thread;
        }
    }
    static class Task extends Thread {

        private SwitchParameters switchParameters = null;
        private List<String> functionName = null;
        boolean isRSA = false;

        public Task(SwitchParameters switchParameters,List<String> functionName,boolean isRSA){
            this.switchParameters = switchParameters;
            this.functionName = functionName;
            this.isRSA = isRSA;
        }

        @Override
        public void run() {

            String threadName = getThreadName();
            switchParameters.setThreadName(threadName);
            ConnectToObtainInformation connectToObtainInformation = new ConnectToObtainInformation();
            AjaxResult basicInformationList_ajaxResult = connectToObtainInformation.connectSwitchObtainBasicInformation(switchParameters, isRSA);


            //AjaxResult basicInformationList_ajaxResult = getBasicInformationList(user_String,user_Object);   //getBasicInformationList
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
                                    "基本信息："+
                                    basicInformationList_ajaxResult.get("msg")+"\r\n");

            }
            /*System.err.println("run()线程结束");*/
        }
    }
    /*线程命名*/
    public static String getThreadName() {
        Random random = new Random();
        String name = System.currentTimeMillis() + random.nextInt(100) +" ";
        return "threadname" + name;
    }
}
