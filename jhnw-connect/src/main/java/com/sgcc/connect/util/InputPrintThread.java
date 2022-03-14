package com.sgcc.connect.util;

import java.io.*;

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
        this.inputStream = inputStream;
    }

    @Override
    public void run() {

        int num = 0;

        char[] bytes = new char[1024];

        int w =0;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        try {
            //这里会发生阻塞，通过websocket推送进行
            while (!interrupted() && (num = inputStreamReader.read(bytes)) != -1) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < num; i++) {
                    char ab = bytes[i];
                    //System.err.print(ab);
                    stringBuilder.append(ab);
                    //WebSocketServer.sendInfo(ab + "", SocketIdEnum.TELNET.getValue());
                }

                TelnetComponent.returnInformation = TelnetComponent.returnInformation + stringBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}