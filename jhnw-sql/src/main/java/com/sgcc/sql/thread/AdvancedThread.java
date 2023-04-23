package com.sgcc.sql.thread;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.sql.controller.SwitchInteraction;
import com.sgcc.sql.parametric.SwitchParameters;
import com.sgcc.sql.senior.LuminousAttenuation;
import com.sgcc.sql.senior.OSPFFeatures;
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
    // 为线程命名
    public AdvancedThread(String threadName,
                          SwitchParameters switchParameters, List<String> functionName,
                      CountDownLatch countDownLatch, ExecutorService fixedThreadPool) {

        super(threadName);
        this.functionName = functionName;
        this.countDownLatch = countDownLatch;
        this.fixedThreadPool = fixedThreadPool;
        this.switchParameters = switchParameters;

    }

    @Override
    public void run() {
        //将exes转换为ThreadPoolExecutor,ThreadPoolExecutor有方法 getActiveCount()可以得到当前活动线程数
        int threadCount = ((ThreadPoolExecutor)fixedThreadPool).getActiveCount();
        System.err.println("活跃线程数："+threadCount);

        AjaxResult basicInformationList_ajaxResult = SwitchInteraction.connectSwitchObtainBasicInformation(switchParameters);
        //AjaxResult basicInformationList_ajaxResult = getBasicInformationList(user_String,user_Object);   //getBasicInformationList
        if (!(basicInformationList_ajaxResult.get("msg").equals("未定义该交换机获取基本信息命令及分析"))) {
            this.switchParameters = (SwitchParameters) basicInformationList_ajaxResult.get("data");
        }

        for (String function:functionName){
            switch (function){
                case "OSPF":
                    OSPFFeatures.getOSPFValues(switchParameters);
                    break;
                case "光衰":
                    LuminousAttenuation luminousAttenuation = new LuminousAttenuation();
                    luminousAttenuation.obtainLightDecay(switchParameters);
                    break;
            }
        }



        //将exes转换为ThreadPoolExecutor,ThreadPoolExecutor有方法 getActiveCount()可以得到当前活动线程数
        threadCount = ((ThreadPoolExecutor)fixedThreadPool).getActiveCount();
        System.err.println("活跃线程数："+threadCount);
    }

}
