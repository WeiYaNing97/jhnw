package com.sgcc.share.method;

import com.sgcc.share.util.PathHelper;
import com.sgcc.share.webSocket.WebSocketService;

import java.io.IOException;

/**
 * @program: jhnw
 * @description: 告警异常信息方法
 * @author:
 * @create: 2024-02-21 10:20
 **/
public class AbnormalAlarmInformationMethod {

    public static void afferent(String name,String categories,String information) {

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
    }

}
