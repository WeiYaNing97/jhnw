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
                // 写入文件  文件名：TelnetComponent.returnInformationFileName
                //System.err.print("\r\n"+stringBuilder.toString());
                fileCreationWrite(TelnetComponent.returnInformationFileName,stringBuilder.toString());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fileCreationWrite(String returnInformationFileName,String returnString){
        String lujing = "D:\\IdeaProjects\\github\\beifen\\jhnw\\jhnw-connect\\src\\main\\java\\com\\sgcc\\connect\\txt\\"+ returnInformationFileName +".txt";
        File file = new File(lujing);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(returnString);
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}