package com.sgcc.connect.method;

import com.sgcc.connect.util.TelnetComponent;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

/**
 * Copyright (C), 2015-2019
 * FileName: TelnetController
 * Author:   MRC
 * Date:     2019/12/27 23:31
 * Description:
 * History:
 */
@RestController
@RequestMapping("/sql/telnet")
public class TelnetSwitchMethod {

    private static TelnetComponent telnetComponent;

    /**
     * @Author MRC
     * @Description //TODO 打开telnet连接
     * @Date 23:33 2019/12/27
     * @Param [ip, port]
     * @return com.alibaba.fastjson.JSONObject
     **/
    @GetMapping("requestConnect")
    public boolean requestConnect(String ip,Integer port,String name,String password,String end){
        boolean open = open(ip, port);
        try {
            Thread.sleep(5*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (open){
            String namecommand = sendCommand(name);
            if (namecommand!=null){
                String passwordcommand = sendCommand(password);
                if (passwordcommand!=null){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean open(String ip,Integer port){

        try {
            TelnetComponent telnet = new TelnetComponent();
            this.telnetComponent = telnet;
            telnetComponent.openSession(ip,port);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @GetMapping("sendCommand")
    public String sendCommand(String common){
        String returnStringCommand = dispatchOrders(common);

        String endIdentifier = "<>,[]";
        String[] endIdentifierSplit = endIdentifier.split(",");
        String returnString = "";
        for (String end:endIdentifierSplit){
            if (returnStringCommand.indexOf(end.substring(0,1)) != -1){
                String[] split1 = returnStringCommand.split(end.substring(0,1));
                String[] split2 = split1[split1.length-1].split(end.substring(1,2));
                String end_split = split2[0];
                String end_substring = end.substring(0,1) + end_split+end.substring(1,2);
                char[] returnStringCommandChars = returnStringCommand.toCharArray();
                for (char chars:returnStringCommandChars){
                    returnString = returnString +  chars;
                    if (returnString.indexOf(end_substring)!=-1){
                        return returnString;
                    }
                }
            }
        }
        return returnStringCommand;
    }


    /**
     * @Author MRC
     * @Description 发送命令
     * @Date 11:01 2019/12/28
     * @Param [common]
     * @return java.lang.String
     **/
    @GetMapping("dispatchOrders")
    public String dispatchOrders(String common) {

        if (null == common) {
            return "error";
        }
        try {
            String command = telnetComponent.sendCommand(common);
            try {

                File file = new File("F:\\"+ command +".txt");

                String readFileContent = readFileContent(file);

                while (readFileContent.indexOf("---- More ----")!=-1){
                    readFileContent = readFileContent.replaceAll("---- More ----"," ");
                    String sendCommon = dispatchOrders(" ");
                    readFileContent = readFileContent + sendCommon;
                }
                return readFileContent;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
        return null;
    }


    /**
     * @Author MRC
     * @Description 关闭telnet
     * @Date 10:54 2019/12/28
     * @Param []
     * @return java.lang.String
     **/
    @GetMapping("close")
    public String closeSession() {
        try {
            telnetComponent.closeSession();
        } catch (IOException e) {

            e.printStackTrace();
            return "error";
        }

        return "success";
    }

    public static String readFileContent(File file) {

        try {

            Thread.sleep(3*1000);
            file.createNewFile();
            FileInputStream fileInput = null;
            fileInput = new FileInputStream(file);
            int index=-1;
            StringBuilder stringBuilder = new StringBuilder();
            while ((index = fileInput.read())!=-1) {
                stringBuilder.append((char)index);
            }
            System.err.print(stringBuilder.toString());
            return stringBuilder.toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static void fileDelete(File file){
            file.delete();
    }
}