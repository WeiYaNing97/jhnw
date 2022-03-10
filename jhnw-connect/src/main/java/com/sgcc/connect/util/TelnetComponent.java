package com.sgcc.connect.util;

import org.apache.commons.net.telnet.TelnetClient;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Copyright (C), 2015-2019
 * FileName: TelnetClient
 * Author:   MRC
 * Date:     2019/12/26 16:07
 * Description: telnet 客户端
 * History:
 */
@Component
public class TelnetComponent {

    private TelnetClient telnetClient = null;

    private InputStream inputStream;

    private OutputStream outputStream;

    private Thread outputThread;

    public static String returnInformationFileName;

    /**
     * @return void
     * @Author MRC
     * @Description 打开telnet 连接
     * @Date 16:46 2019/12/26
     * @Param [user, pass]
     **/
    public void openSession(String ip,Integer port) throws IOException, InterruptedException {

        if (outputThread != null) {
            //关闭旧的
            outputThread.interrupt();
        }

        telnetClient = new TelnetClient();
        telnetClient.connect(ip,port);

        inputStream = telnetClient.getInputStream();
        outputStream = telnetClient.getOutputStream();

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSzzZZ");//设置日期格式
        String format = df.format(new Date());// new Date()为获取当前系统时间
        returnInformationFileName = format +"";

        outputThread = new InputPrintThread(inputStream);
        //守护线程
        outputThread.setDaemon(true);
        outputThread.start();
    }

    /**
     * @return java.lang.String
     * @Author MRC
     * @Description 发送命令
     * @Date 16:53 2019/12/26
     * @Param [send]
     **/
    public String sendCommand(String send) throws IOException {

        //加入换行符
         send = send + "\n";

        if (null == telnetClient) {
            return "连接已关闭";
        }
        outputStream.write(send.getBytes());
        outputStream.flush();

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSzzZZ");//设置日期格式
        String format = df.format(new Date());// new Date()为获取当前系统时间
        returnInformationFileName = format +"";

        //根据 returnInformationFileName 为名的文件 读取出字符串
        return returnInformationFileName;
    }

    /**
     * @return void
     * @Author MRC
     * @Description 关闭telnet连接
     * @Date 16:46 2019/12/26
     * @Param []
     **/
    public void closeSession() throws IOException {

        if (telnetClient != null) {
            telnetClient.disconnect();

            //关闭后台thread
            outputThread.interrupt();

            telnetClient = null;
            //告知其进行中断
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        TelnetComponent telnetComponent = new TelnetComponent();

        telnetComponent.openSession("192.168.1.1",23);

        telnetComponent.sendCommand("admin");

        telnetComponent.closeSession();
    }


}