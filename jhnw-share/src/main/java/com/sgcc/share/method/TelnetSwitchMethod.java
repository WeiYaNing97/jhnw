package com.sgcc.share.method;
import com.sgcc.share.connectutil.TelnetComponent;
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
    //private static TelnetComponent telnetComponent;
    //public String moreEcho = "---- More ----";
    /**
     * @Author MRC
     * @Description //TODO 打开telnet连接
     * @Date 23:33 2019/12/27
     * @Param [ip, port]
     * @return com.alibaba.fastjson.JSONObject
     **/
    @GetMapping("requestConnect")
    public TelnetComponent requestConnect(String ip, Integer port, String name, String password, String end){
        TelnetComponent open = open(ip, port);
        try {
            Thread.sleep(5*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (open != null){
            String namecommand = sendCommand(ip,open,name,null);
            if (namecommand!=null){
                String passwordcommand = sendCommand(ip,open,password,null);
                if (passwordcommand!=null){
                    return open;
                }
            }
        }
        return null;
    }

    public TelnetComponent open(String ip,Integer port){
        TelnetComponent telnetComponent = null;
        try {
             telnetComponent = new TelnetComponent();
            telnetComponent.openSession(ip,port);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return telnetComponent;
    }

    @GetMapping("sendCommand")
    public String sendCommand(String ip,TelnetComponent telnetComponent,String common,String notFinished){
        String returnStringCommand = dispatchOrders(ip,telnetComponent,common,notFinished);
        String endIdentifier = "<>,[]";
        String[] endIdentifierSplit = endIdentifier.split(",");
        String returnString = "";
        for (String end:endIdentifierSplit){
            String substring = end.substring(0, 1);
            if (returnStringCommand.indexOf(substring) != -1){
                switch (substring){
                    case "[" :
                        substring = "\\[";
                        break;
                    case "+" :
                        substring = "\\+";
                        break;
                    case "\\" :
                        substring = "\\\\";
                        break;
                    case "^" :
                        substring = "\\^";
                        break;
                    case "?" :
                        substring = "\\?";
                        break;
                    case "*" :
                        substring = "\\*";
                        break;
                    case "." :
                        substring = "\\.";
                        break;
                    case "$" :
                        substring = "\\$";
                        break;
                    case "|" :
                        substring = "\\|";
                        break;
                    case "(" :
                        substring = "\\(";
                        break;
                    case ")" :
                        substring = "\\)";
                        break;
                    case "()" :
                        substring = "\\()";
                        break;
                }
                String[] split1 = returnStringCommand.split(substring);
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
    public String dispatchOrders(String ip,TelnetComponent telnetComponent,String common,String notFinished) {
        String moreEcho = "---- More ----";
        if (notFinished != null){
            moreEcho = notFinished;
        }
        if (null == common) {
            return "error";
        }
        try {
            String readFileContent = telnetComponent.sendCommand(ip,common);
            try {
                while (readFileContent.indexOf(moreEcho)!=-1){
                    readFileContent = readFileContent.replaceAll(moreEcho," ");
                    String sendCommon = dispatchOrders(ip,telnetComponent," ",moreEcho);
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
    public String closeSession(TelnetComponent telnetComponent) {
        try {
            telnetComponent.closeSession();
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    public static void fileDelete(File file){
            file.delete();
    }
}