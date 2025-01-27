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
        // 获取运行分析配置值
        String operationalAnalysis = (String) CustomConfigurationUtil.getValue("运行分析", Constant.getProfileInformation());
        // 将运行分析字符串分割成列表
        List<String> operationalAnalysisList = Arrays.asList(operationalAnalysis.split(" "));
        boolean isContains = false;
        // 检查问题名是否在运行分析列表中
        if (categories !=null && operationalAnalysisList.contains(categories)){
            isContains = true;
        }
        // 创建WebSocketService实例
        WebSocketService webSocketService = new WebSocketService();
        // 如果用户名和问题名都不为空
        if (name != null && categories != null){
            //传输登陆人姓名 及问题简述
            webSocketService.sendMessage(name,information);
            try {
                if (isContains){
                    // 创建PathHelper实例
                    PathHelper pathHelper = new PathHelper();
                    // 根据高级特性名写入数据到文件
                    pathHelper.writeDataToFileByAdvancedFeatureName(categories,information);
                }else {
                    // 插入问题简述及问题路径（按问题名分类）
                    PathHelper pathHelper = new PathHelper();
                    pathHelper.writeDataToFileByName(categories,information);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        // 如果用户名不为空，但问题名为空
        }else if (name != null && categories == null){
            //传输登陆人姓名 及问题简述
            webSocketService.sendMessage(name,information);
            try {
                // 插入问题简述及问题路径（不分类）
                PathHelper pathHelper = new PathHelper();
                pathHelper.writeDataToFile(information);
            } catch (IOException e) {
                e.printStackTrace();
            }

        // 如果用户名为空，但问题名不为空
        }else if (name == null && categories != null){
            webSocketService.sendMessageAll(information);
            try {
                if (isContains){
                    // 创建PathHelper实例
                    PathHelper pathHelper = new PathHelper();
                    // 根据高级特性名写入数据到文件
                    pathHelper.writeDataToFileByAdvancedFeatureName(categories,information);
                }else {
                    // 插入问题简述及问题路径（按问题名分类）
                    PathHelper pathHelper = new PathHelper();
                    pathHelper.writeDataToFileByName(categories,information);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        // 如果用户名和问题名都为空
        }else if (name == null && categories == null){
            webSocketService.sendMessageAll(information);
            try {
                // 插入问题简述及问题路径（不分类）
                PathHelper pathHelper = new PathHelper();
                pathHelper.writeDataToFile(information);
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
