package com.sgcc.share.connectutil;
import org.apache.commons.net.telnet.TelnetClient;
import org.springframework.stereotype.Component;
import java.io.*;
import java.util.HashMap;

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

    /*存在内存风险*/
    public static HashMap<String,TelnetInformation> switchInformation = new HashMap<>();

    private HashMap<String,TelnetInformation> setSwitchInformation(String ip){
        TelnetInformation telnetInformation = new TelnetInformation();
        telnetInformation.setIp(ip);
        telnetInformation.setTelnetClient(null);
        telnetInformation.setInputStream(null);
        telnetInformation.setOutputStream(null);
        telnetInformation.setOutputThread(null);
        telnetInformation.setReturnInformation(null);
        telnetInformation.setNum(0);
        telnetInformation.setBytes(new char[1024]);
        switchInformation.put(ip,telnetInformation);
        return switchInformation;
    }

    /**
     * @return void
     * @Author MRC
     * @Description 打开telnet 连接
     * @Date 16:46 2019/12/26
     * @Param [user, pass]
     **/
    public void openSession(String ip,Integer port) throws IOException, InterruptedException {
        HashMap<String, TelnetInformation> stringTelnetInformationHashMap = setSwitchInformation(ip);
        TelnetInformation telnetInformation = stringTelnetInformationHashMap.get(ip);

        if (telnetInformation.getOutputThread() != null) {
            //关闭旧的
            telnetInformation.getOutputThread().interrupt();
        }
        telnetClient = new TelnetClient();
        telnetClient.connect(ip,port);
        telnetInformation.setTelnetClient(telnetClient);
        inputStream = telnetInformation.getTelnetClient().getInputStream();
        telnetInformation.setInputStream(inputStream);
        outputStream = telnetInformation.getTelnetClient().getOutputStream();
        telnetInformation.setOutputStream(outputStream);
        try {
            Thread.sleep(2*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        outputThread = new InputPrintThread(telnetInformation.getInputStream());
        //守护线程
        outputThread.setDaemon(true);
        telnetInformation.setOutputThread(outputThread);
        switchInformation.put(ip,telnetInformation);
        outputThread.start();
        telnetInformation.setOutputThread(outputThread);
        switchInformation.put(ip,telnetInformation);
    }

    /**
     * @return java.lang.String
     * @Author MRC
     * @Description 发送命令
     * @Date 16:53 2019/12/26
     * @Param [send]
     **/
    public String sendCommand(String ip,String send) throws IOException {
        TelnetInformation telnetInformation = switchInformation.get(ip);
        telnetInformation.setReturnInformation("");
        //加入换行符
         send = send + "\n";
        if (null == telnetInformation.getTelnetClient()) {
            return "连接已关闭";
        }
        telnetInformation.getOutputStream().write(send.getBytes());
        telnetInformation.getOutputStream().flush();
        try {
            Thread.sleep(2*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        telnetInformation.setReturnInformation(removeGarbledCode(telnetInformation.getReturnInformation()));
        String returnInformation = telnetInformation.getReturnInformation().trim();
        if (returnInformation.startsWith(send));{
            returnInformation = returnInformation.substring(send.length(),returnInformation.length());
        }
        return returnInformation;
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
        telnetComponent.sendCommand("192.168.1.1","admin");
        telnetComponent.closeSession();
    }


    /**
    * @method: 去除交换机返回信息 字符串中的 乱码（高亮）
    * @Param: [returnInformation]
    * @return: java.lang.String
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    public String removeGarbledCode(String returnInformation){
        char[] returnInformationChars = returnInformation.toCharArray();
        char charOne = (char)27;
        char charTwo = (char)91;
        for (int number=0;number<returnInformationChars.length;number++){

            if (returnInformationChars[number] == (charOne)){
                if (number+1<=returnInformationChars.length){

                    if (returnInformationChars[number+1] == (charTwo)){
                        String  returnInformationSubstring = returnInformation.substring(number,returnInformation.length());
                        String[]  returnInformationSplit1 = returnInformationSubstring.split(" ");
                        String returnInformationString = returnInformationSplit1[0];
                        String replace = returnInformation.replace(returnInformationString, "");
                        return replace;
                    }
                }
            }
        }
        return returnInformation;
    }
}