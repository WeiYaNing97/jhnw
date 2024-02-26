package com.sgcc.share.method;

import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.domain.AbnormalAlarmInformation;
import com.sgcc.share.service.IAbnormalAlarmInformationService;
import com.sgcc.share.service.IReturnRecordService;
import com.sgcc.share.util.PathHelper;
import com.sgcc.share.webSocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * @program: jhnw
 * @description: 告警异常信息方法
 * @author:
 * @create: 2024-02-21 10:20
 **/
public class AbnormalAlarmInformationMethod {

    @Autowired
    private static IAbnormalAlarmInformationService abnormalAlarmInformationService;

    public static void afferent(String ip,String name,String categories,String information) {

        if (name != null && categories != null){

            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(name,information);
            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFileByName(categories,information);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if (name != null && categories == null){

            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(name,information);
            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFile(information);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if (name == null && categories != null){

            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFileByName(categories,information);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if (name == null && categories == null){

            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFile(information);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        AbnormalAlarmInformation abnormalAlarmInformation = new AbnormalAlarmInformation();
        abnormalAlarmInformation.setSwitchIp(ip);
        abnormalAlarmInformation.setUserName(name);
        abnormalAlarmInformation.setQuestionType(categories);
        abnormalAlarmInformation.setQuestionInformation(information);

        abnormalAlarmInformationService = SpringBeanUtil.getBean(IAbnormalAlarmInformationService.class);
        abnormalAlarmInformationService.insertAbnormalAlarmInformation(abnormalAlarmInformation);


    }

}
