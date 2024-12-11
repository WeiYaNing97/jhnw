package com.sgcc.sql.thread;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.switchboard.ConnectToObtainInformation;
import com.sgcc.share.util.WorkThreadMonitor;
import com.sgcc.sql.controller.SwitchInteraction;
import com.sgcc.share.webSocket.WebSocketService;
import com.sgcc.sql.domain.CommandReturn;
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.sql.util.InspectionMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
/**
 * @date 2023/7/27 15:05
 * @description 交换机登录信息获取线程
 * @Param threadName 线程名称
 * @Param switchParameters 交换机登录信息
 * @Param countDownLatch 线程计数器
 * @Param fixedThreadPool 线程池
 * @Param isRSA 是否RSA加密
 */
public class ScanThread extends Thread  {

    // 交换机登录信息对象
    SwitchParameters switchParameters = null;
    // 线程计数器对象，用于线程计数
    CountDownLatch countDownLatch = null;
    // 线程池对象，用于线程池
    ExecutorService fixedThreadPool = null;
    // 是否RSA加密  true:RSA加密  false:非RSA加密  默认为true;
    boolean isRSA = true;

    /**
     * 构造方法：初始化线程信息
     * @param threadName 线程名称
     * @param switchParameters 交换机登录信息
     * @param countDownLatch 线程计数器对象，用于线程计数
     * @param fixedThreadPool 线程池对象，用于线程池
     * @param isRSA 是否RSA加密  true:RSA加密  false:非RSA加密  默认为true;
     */
    public ScanThread(String threadName,
                      SwitchParameters switchParameters,
                      CountDownLatch countDownLatch,ExecutorService fixedThreadPool,boolean isRSA) {
        super(threadName);
        /* 线程计数*/
        this.countDownLatch = countDownLatch;
        this.fixedThreadPool = fixedThreadPool;
        this.switchParameters = switchParameters;
        this.isRSA = isRSA;
    }

    @Override
    public void run() {
        try {

            /**
             * 1.中断检查，如果未有终止扫描操作，
             *    则登录交换机 获取交换机基本信息
             */
            // 中断检查
            if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
                return;
            }
            /*连接交换机 获取交换机基本信息*/
            ConnectToObtainInformation connectToObtainInformation = new ConnectToObtainInformation();
            AjaxResult basicInformationList_ajaxResult = connectToObtainInformation.connectSwitchObtainBasicInformation(switchParameters,isRSA);
            /* 告警、异常信息写入*/
            if (basicInformationList_ajaxResult.get("msg").equals("交换机连接失败")
                    || basicInformationList_ajaxResult.get("msg").equals("未定义该交换机获取基本信息命令及分析")
                    || basicInformationList_ajaxResult.get("msg").equals("交换机登录信息获取失败")){
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                        "异常:" +
                                "IP地址为:"+switchParameters.getIp()+"。"+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+"。"+
                                "问题为:"+basicInformationList_ajaxResult.get("msg")+"。\r\n");
                return;
            }
            /*交换机登录信息 通过获取交换机基本信息 该实体类已经包含交换机基本信息*/
            switchParameters = (SwitchParameters) basicInformationList_ajaxResult.get("data");


            /**
             * 2.根据交换机四项基本信息，获取交换机可扫描的问题并执行分析操作
             *   返回AjaxResult 包含交换机可扫描的问题集合，
             *   如果返回AjaxResult长度为0 则未定义该交换机的问题信息。
             */
            SwitchInteraction switchInteraction = new SwitchInteraction();
            AjaxResult commandIdByInformation_ajaxResult = switchInteraction.commandIdByInformation(switchParameters);
            /* 返回AjaxResult长度为0 则未定义交换机问题*/
            if (commandIdByInformation_ajaxResult.size() == 0){
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                        "异常:IP地址为:"+switchParameters.getIp()+"。"+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+"。"+
                                "问题为:未定义交换机问题。\r\n");
            }
            /* 返回AjaxResult长度不为0 则赋值可扫描交换机问题集合 */
            List<TotalQuestionTable> totalQuestionTableList = (List<TotalQuestionTable>) commandIdByInformation_ajaxResult.get("data");


            /**
             * 3.筛选匹配度高的交换机问题。
             *   遍历交换机问题集合，并执行分析操作，
             */
            /*筛选匹配度高的交换机问题*/
            List<TotalQuestionTable> TotalQuestionTablePojoList = InspectionMethods.ObtainPreciseEntityClasses(totalQuestionTableList);
            for (TotalQuestionTable totalQuestionTable:TotalQuestionTablePojoList){
                //中断检查，如果未有终止扫描操作，则跳出循环，并关闭交换机连接，并跳出循环
                if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
                    break;
                }

                //判断修复问题ID是否为空，为空则告警、异常信息写入
                if (totalQuestionTable.getProblemSolvingId() == null
                        || totalQuestionTable.getProblemSolvingId().equals("null")){
                    //传输登陆人姓名 及问题简述
                    String subversionNumber = switchParameters.getSubversionNumber();
                    if (subversionNumber!=null){
                        subversionNumber = "、"+subversionNumber;
                    }
                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                            "异常:IP地址为:"+switchParameters.getIp()+"。"+
                                    "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+"。"+
                                    "问题ID为:"+totalQuestionTable.getId() +
                                    "问题分类:"+totalQuestionTable.getTypeProblem()+
                                    "问题名称为:"+totalQuestionTable.getTemProName()+":"+totalQuestionTable.getProblemName()
                                    +",未定义解决问题。\r\n"
                    );
                }

                //判断逻辑ID是否为命令类型，为命令类型则执行命令获取返回结果并分析返回结果
                if (totalQuestionTable.getLogicalID().indexOf("命令") != -1){
                    /* 交换机返回结果未结束标志符：   ---- More ----    */
                    switchParameters.setNotFinished(totalQuestionTable.getNotFinished());
                    //根据命令ID获取具体命令，执行
                    //返回  交换机返回信息 和  第一条分析ID
                    CommandReturn commandReturn = switchInteraction.executeScanCommandByCommandId(switchParameters,totalQuestionTable,totalQuestionTable.getLogicalID().replace("命令",""));
                    if (commandReturn == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
                        break;
                    }else if (commandReturn == null || !commandReturn.isSuccessOrNot()){
                        /*交换机返回错误信息处理
                         * 遍历下一个问题*/
                        continue;
                    }
                    //分析逻辑
                    String analysisReturnResults_String = switchInteraction.analysisReturnResults(switchParameters, totalQuestionTable,
                            commandReturn ,  "",  "");
                    if (analysisReturnResults_String == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
                        break;
                    }
                }else if (totalQuestionTable.getLogicalID().indexOf("分析") != -1){
                    //根据分析ID执行分析逻辑返回结果。返回结果为null且进行中断检查，如中断则跳出循环
                    CommandReturn commandReturn = new CommandReturn();
                    commandReturn.setReturnResults(new ArrayList<>());
                    commandReturn.setAnalysisID(totalQuestionTable.getLogicalID().replaceAll("分析",""));
                    //分析逻辑
                    String analysisReturnResults_String = switchInteraction.analysisReturnResults(switchParameters, totalQuestionTable,
                            commandReturn,  "",  "");
                    if (analysisReturnResults_String == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
                        break;
                    }
                }
            }


            /**
             * 4.关闭交换机连接,并结束扫描线程。
             *   线程计数减一
             */
            if (switchParameters.getMode().equalsIgnoreCase("ssh")){
                switchParameters.getConnectMethod().closeConnect(switchParameters.getSshConnect());
            }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
                switchParameters.getTelnetSwitchMethod().closeSession(switchParameters.getTelnetComponent());
            }
            /*扫描交换机过程中要求要有一个旋转的圆圈，用于取消圆圈旋转*/
            WebSocketService webSocketService = new WebSocketService();
            webSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"scanThread:"+switchParameters.getIp()+":"+switchParameters.getThreadName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }

}
