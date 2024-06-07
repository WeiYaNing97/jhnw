package com.sgcc.share.connectutil;

import java.io.*;
import java.util.Map;

/**
 * Copyright (C), 2015-2019
 * FileName: InputPrintThread
 * Author:   MRC
 * Date:     2019/12/27 14:31
 * Description:
 * History:
 */
public class InputPrintThread extends Thread {

    private final InputStream inputStream;

    public InputPrintThread(InputStream inputStream) {
        // 将传入的输入流赋值给类的成员变量inputStream
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        String ip = null;
        TelnetInformation telnetInformation = null;
        for (Map.Entry<String,TelnetInformation> entry : TelnetComponent.switchInformation.entrySet()){
            ip = entry.getKey();
            telnetInformation = entry.getValue();
            if (telnetInformation.getInputStream().equals(inputStream)){
                break;
            }
        }
        try {
            // 遍历TelnetComponent.switchInformation中的每个条目，找到与inputStream匹配的telnetInformation
            // 这里会发生阻塞，通过websocket推送进行
            int num = telnetInformation.getNum();
            char[] bytes = telnetInformation.getBytes();
            while (!interrupted() && (num = inputStreamReader.read(bytes)) != -1) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < num; i++) {
                    char ab = bytes[i];
                    // 将读取到的字符添加到stringBuilder中
                    //System.err.print(ab);
                    stringBuilder.append(ab);
                    // 通过WebSocketServer将字符信息发送出去（注释掉）
                    //WebSocketServer.sendInfo(ab + "", SocketIdEnum.TELNET.getValue());
                }
                // 将读取到的字符信息添加到telnetInformation的返回信息中
                telnetInformation.setReturnInformation(telnetInformation.getReturnInformation() + stringBuilder.toString());
                // 更新TelnetComponent.switchInformation中的telnetInformation
                TelnetComponent.switchInformation.put(ip,telnetInformation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}