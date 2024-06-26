package com.sgcc.share.method;

import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.domain.AbnormalAlarmInformation;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.service.IAbnormalAlarmInformationService;
import com.sgcc.share.service.IReturnRecordService;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.PathHelper;
import com.sgcc.share.webSocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @program: jhnw
 * @description: 告警异常信息方法
 * @author:
 * @create: 2024-02-21 10:20
 **/
public class AbnormalAlarmInformationMethod {

    @Autowired
    private static IAbnormalAlarmInformationService abnormalAlarmInformationService;

    /**
     * 将交换机异常告警信息插入到数据库并发送WebSocket消息
     *
     * @param ip 交换机IP
     * @param name 系统登录用户名
     * @param categories 问题名
     * @param information 问题参数
     * @throws IOException 写入文件异常
     */
    public static void afferent(String ip,String name,String categories,String information) {

        String operationalAnalysis = (String) CustomConfigurationUtil.getValue("运行分析", Constant.getProfileInformation());

        List<String> operationalAnalysisList = Arrays.asList(operationalAnalysis.split(" "));

        boolean isContains = false;
        if (categories !=null && operationalAnalysisList.contains(categories)){
            isContains = true;
        }

        if (name != null && categories != null){
            // 如果用户名和问题名都不为空
            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(name,information);
            try {
                if (isContains){
                    PathHelper pathHelper = new PathHelper();
                    pathHelper.writeDataToFileByAdvancedFeatureName(categories,information);
                }else {
                    // 插入问题简述及问题路径（按问题名分类）
                    PathHelper.writeDataToFileByName(categories,information);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if (name != null && categories == null){
            // 如果用户名不为空，但问题名为空
            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(name,information);
            try {
                // 插入问题简述及问题路径（不分类）
                PathHelper.writeDataToFile(information);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if (name == null && categories != null){

            WebSocketService.sendMessageAll(information);
            // 如果用户名为空，但问题名不为空
            try {
                if (isContains){
                    PathHelper pathHelper = new PathHelper();
                    pathHelper.writeDataToFileByAdvancedFeatureName(categories,information);
                }else {
                    // 插入问题简述及问题路径（按问题名分类）
                    PathHelper.writeDataToFileByName(categories,information);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if (name == null && categories == null){

            WebSocketService.sendMessageAll(information);
            // 如果用户名和问题名都为空
            try {
                // 插入问题简述及问题路径（不分类）
                PathHelper.writeDataToFile(information);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // 创建一个异常告警信息对象
        AbnormalAlarmInformation abnormalAlarmInformation = new AbnormalAlarmInformation();
        // 设置交换机IP
        abnormalAlarmInformation.setSwitchIp(ip);
        // 设置用户名
        abnormalAlarmInformation.setUserName(name);
        // 设置问题名
        abnormalAlarmInformation.setQuestionType(categories);
        // 设置问题参数
        abnormalAlarmInformation.setQuestionInformation(information);

        // 获取AbnormalAlarmInformationService的Bean实例
        abnormalAlarmInformationService = SpringBeanUtil.getBean(IAbnormalAlarmInformationService.class);
        // 将异常告警信息插入到数据库
        abnormalAlarmInformationService.insertAbnormalAlarmInformation(abnormalAlarmInformation);

    }

}
