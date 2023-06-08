package com.sgcc.advanced.thread;
import com.sgcc.advanced.controller.ErrorPackage;
import com.sgcc.advanced.controller.LuminousAttenuation;
import com.sgcc.advanced.controller.OSPFFeatures;
import com.sgcc.common.core.domain.AjaxResult;
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
    public static void switchLoginInformations(ParameterSet parameterSet, List<String> functionName) {
        ExecutorService executorService = Executors.newFixedThreadPool(parameterSet.getThreadCount(), new NamedThreadFactory());
        int i = 1;
        for (SwitchParameters switchParameters:parameterSet.getSwitchParameters()){
            executorService.execute(new Task(switchParameters,functionName));//mode, ip, name, password,configureCiphers, port, loginUser,time
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

        public Task(SwitchParameters switchParameters,List<String> functionName){
            this.switchParameters = switchParameters;
            this.functionName = functionName;
        }

        @Override
        public void run() {

            String threadName = getThreadName();
            switchParameters.setThreadName(threadName);
            ConnectToObtainInformation connectToObtainInformation = new ConnectToObtainInformation();
            AjaxResult basicInformationList_ajaxResult = connectToObtainInformation.connectSwitchObtainBasicInformation(switchParameters);
            //AjaxResult basicInformationList_ajaxResult = getBasicInformationList(user_String,user_Object);   //getBasicInformationList
            if (!(basicInformationList_ajaxResult.get("msg").equals("未定义该交换机获取基本信息命令及分析"))) {
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
                        case "误码率":
                            ErrorPackage errorPackage = new ErrorPackage();
                            errorPackage.getErrorPackage(switchParameters);
                            break;
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

            System.err.println("run()线程结束");

        }
    }
    /*线程命名*/
    public static String getThreadName() {
        Random random = new Random();
        String name = System.currentTimeMillis() + random.nextInt(100) +" ";
        return "threadname" + name;
    }
}
