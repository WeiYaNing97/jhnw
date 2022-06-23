package com.sgcc.connect.util;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2021年09月02日 9:54
 */
import com.jcraft.jsch.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class SshConnect implements Runnable {
    protected Logger logger = LogManager.getLogger();

    //退格
    private static final String BACKSPACE = new String(new byte[] { 8 });
    //ESC
    private static final String ESC = new String(new byte[] { 27 });
    //空格
    private static final String BLANKSPACE = new String(new byte[] { 32 });
    //回车
    private static final String ENTER = new String(new byte[] { 13 });
    //某些设备回显数据中的控制字符
    private static final String[] PREFIX_STRS = { BACKSPACE + "+" + BLANKSPACE + "+" + BACKSPACE + "+",
            "(" + ESC + "\\[\\d+[A-Z]" + BLANKSPACE + "*)+" };
    private int sleepTime = 1000;
    //连接超时(单次命令总耗时)
    private int timeout = 40000;
    //保存当前命令的回显信息
    protected StringBuffer currEcho;
    //保存所有的回显信息
    protected StringBuffer totalEcho;
    private String ip;
    private int port;
    private String endEcho = "#,?,>,:,]";
    private String moreEcho = "---- More ----";
    private String moreCmd = BLANKSPACE;
    private JSch jsch = null;
    private Session session;
    private Channel channel;
    private boolean quit;
    private HashMap<String,SshInformation> switchInformation = new HashMap<>();

    public HashMap<String,SshInformation> setSwitchInformation(String ip){
        SshInformation sshInformation = new SshInformation();
        sshInformation.setLogger(this.logger);
        sshInformation.setBACKSPACE(this.BACKSPACE);
        sshInformation.setESC(this.ESC);
        sshInformation.setBLANKSPACE(this.BLANKSPACE);
        sshInformation.setENTER(this.ENTER);
        sshInformation.setPREFIX_STRS(this.PREFIX_STRS);
        sshInformation.setSleepTime(this.sleepTime);
        sshInformation.setTimeout(this.timeout);
        sshInformation.setCurrEcho(null);
        sshInformation.setTotalEcho(null);
        sshInformation.setIp(this.ip);
        sshInformation.setPort(22);
        sshInformation.setEndEcho("#,?,>,:,]");
        sshInformation.setMoreEcho("---- More ----");
        sshInformation.setMoreCmd(sshInformation.getBLANKSPACE());
        sshInformation.setJsch(null);
        sshInformation.setSession(null);
        sshInformation.setChannel(null);
        sshInformation.setQuit(false);
        switchInformation.put(ip,sshInformation);
        return switchInformation;
    }

    @Override
    public void run() {
        InputStream is;
        try {
            is = channel.getInputStream();
            String ip = null;
            SshInformation sshInformation = null;
            for (Map.Entry<String,SshInformation> entry : switchInformation.entrySet()){
                ip = entry.getKey();
                sshInformation = entry.getValue();
                if (sshInformation.getChannel().equals(channel)){
                    break;
                }
            }
            //读取服务器执行命令后返回信息
            String echo = readOneEcho(is);
            while (echo != null) {
                sshInformation.getCurrEcho().append(echo);
                String[] lineStr = echo.split("\\n");
                if (lineStr != null && lineStr.length > 0) {
                    String lastLineStr = lineStr[lineStr.length - 1];
                    if (lastLineStr != null && lastLineStr.indexOf(sshInformation.getMoreEcho()) > 0) {
                        sshInformation.getTotalEcho().append(echo.replace(lastLineStr, ""));
                    } else {
                        sshInformation.getTotalEcho().append(echo);
                    }
                }
                echo = readOneEcho(is);
            }
            switchInformation.put(ip,sshInformation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
    * @method: 读取服务器执行命令后返回信息
    * @Param: [instr]
    * @return: java.lang.String
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    protected String readOneEcho(InputStream instr) {
        byte[] buff = new byte[1024];
        int ret_read = 0;
        try {
            ret_read = instr.read(buff);
        } catch (IOException e) {
            return null;
        }
        if (ret_read > 0) {
            String result = new String(buff, 0, ret_read);
            for (String PREFIX_STR : PREFIX_STRS) {
                result = result.replaceFirst(PREFIX_STR, "");
            }
            return result;
        } else {
            return null;
        }
    }

    public SshConnect(String ip, int port, String endEcho, String moreEcho) {
        HashMap<String, SshInformation> stringSshInformationHashMap = setSwitchInformation(ip);
        SshInformation sshInformation = stringSshInformationHashMap.get(ip);
        sshInformation.setIp(ip);
        sshInformation.setPort(port);

        if (endEcho != null) {
            sshInformation.setEndEcho(endEcho);
        }
        if (moreEcho != null) {
            sshInformation.setMoreEcho(moreEcho);
        }

        sshInformation.setTotalEcho(new StringBuffer());
        sshInformation.setCurrEcho(new StringBuffer());
        switchInformation.put(ip,sshInformation);
    }

    public void close() {
        if (session != null) {
            session.disconnect();
        }
        if (channel != null) {
            channel.disconnect();
        }
    }

    /***
    * @method: 连接SSH
    * @Param: [cmds] cmds[0]:用户名 cmds[1] 密码
    * @return: boolean
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    public boolean login(String ip,String[] cmds) {
        SshInformation sshInformation = switchInformation.get(ip);

        //用户名 密码
        String user = cmds[0];
        String passWord = cmds[1];
        //ssh连接工具包 创建工具类
        sshInformation.setJsch(new JSch());
        try {
            //@method: jsch 获取会话
            //@Param: [user 用户名, this.ip IP地址, this.port 端口号]
            session = sshInformation.getJsch().getSession(user, sshInformation.getIp(), sshInformation.getPort());
            sshInformation.setSession(session);
            //输入密码
            sshInformation.getSession().setPassword(passWord);
            UserInfo ui = new SSHUserInfo() {
                public void showMessage(String message) {
                }
                public boolean promptYesNo(String message) {
                    return true;
                }
            };
            sshInformation.getSession().setUserInfo(ui);
            sshInformation.getSession().connect(30000);

            channel = sshInformation.getSession().openChannel("shell");
            sshInformation.setChannel(channel);
            //jscn 连接 linux 解决高亮显示乱码问题
            ((ChannelShell) sshInformation.getChannel()).setPtyType("dumb");
            //((ChannelShell) channel).setPty(false);
            sshInformation.getChannel().connect(3000);
            switchInformation.put(ip,sshInformation);

            new Thread(this).start();
            try {
                Thread.sleep(sshInformation.getSleepTime());
            } catch (Exception e) {
            }

            return true;
        } catch (JSchException e) {
            switchInformation.put(ip,sshInformation);
            return false;
        }
    }

    /***
    * @method: 向服务器发送命令
    * @Param: [command 命令, sendEnter]
    * @return: void
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    protected String sendCommand(String ip,String command, boolean sendEnter) {
        SshInformation sshInformation = switchInformation.get(ip);
        try {

            OutputStream os = sshInformation.getChannel().getOutputStream();
            os.write(command.getBytes());
            os.flush();

            if (sshInformation.isQuit()){
                StringBuffer stringBufferCurrEcho = new StringBuffer("遗失对主机的连接。");
                sshInformation.setCurrEcho(stringBufferCurrEcho);
                return "遗失对主机的连接。";
            }
            if (sendEnter) {
                sshInformation.setCurrEcho(new StringBuffer());
                os.write(sshInformation.getENTER().getBytes());
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        switchInformation.put(ip,sshInformation);
        return sshInformation.getCurrEcho().toString();
    }

    //测试此字符串是否以指定的后缀结尾
    protected boolean containsEchoEnd(String ip,String echo) {
        SshInformation sshInformation = switchInformation.get(ip);
        boolean contains = false;

        //如果 结尾标识符 为空 返回 错误
        //endEcho = "#,?,>,:";
        if (sshInformation.getEndEcho() == null || sshInformation.getEndEcho().trim().equals("")) {
            return contains;
        }

        //结尾标识符数组
        String[] eds = sshInformation.getEndEcho().split(",");

        //遍历结尾标识符数组
        for (String ed : eds) {
            //测试此字符串是否以指定的后缀结尾。
            if (echo.trim().endsWith(ed)) {
                contains = true;
                break;
            }
        }

        return contains;
    }

    /***
    * @method: 执行单个命令
    * @Param: [command, ifEnter]
    * @return: java.lang.String
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    private String runCommand(String ip ,String command,String notFinished,boolean ifEnter) {
        SshInformation sshInformation = switchInformation.get(ip);

        if (notFinished!=null && notFinished!=""){
            sshInformation.setMoreEcho(notFinished);
        }

        sshInformation.setCurrEcho(new StringBuffer());
        //向服务器发送命令
        String str = sendCommand(ip,command, ifEnter);

        if (str =="遗失对主机的连接。"){
            return str;
        }
        int time = 0;
        //endEcho 不存在 为空
        //endEcho = "#,?,>,:";
        if (sshInformation.getEndEcho() == null || sshInformation.getEndEcho().equals("")) {
            while (sshInformation.getCurrEcho().toString().equals("")) {
                try {
                    Thread.sleep(sshInformation.getSleepTime());
                    time += sshInformation.getSleepTime();
                    //连接超时(单次命令总耗时)
                    if (time >= sshInformation.getTimeout()) {
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            //endEcho 存在 不为空
            // containsEchoEnd 测试此字符串是否以指定的后缀结尾   是 true   否 false
            while (!containsEchoEnd(ip,sshInformation.getCurrEcho().toString())) {
                try {
                    Thread.sleep(sshInformation.getSleepTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time += sshInformation.getSleepTime();
                //连接超时(单次命令总耗时)
                if (time >= sshInformation.getTimeout()) {
                    break;
                }

                //接收交换机返回信息 转换为字符串数组
                String[] lineStrs = sshInformation.getCurrEcho().toString().split("\\n");


                //接收交换机返回信息不为空
                if (lineStrs != null && lineStrs.length > 0) {
                    //private String moreEcho = "---- More ----";
                    //接收交换机返回信息的最后信息 lineStrs[lineStrs.length - 1]
                    //接收交换机返回信息的最后信息 是否是 "---- More ----" 是 则没有返回完全
                    if ((sshInformation.getMoreEcho() != null && lineStrs[lineStrs.length - 1] != null
                            && lineStrs[lineStrs.length - 1].contains(sshInformation.getMoreEcho())  || lineStrs[lineStrs.length - 1].trim().contains("#"))) {
                        //private String moreCmd = BLANKSPACE;
                        //空格  private static final String BLANKSPACE = new String(new byte[] { 32 });
                        String command1 = sendCommand(ip, sshInformation.getMoreCmd(), false);
                        StringBuffer stringBuffer = new StringBuffer(command1);
                        sshInformation.setCurrEcho(stringBuffer);
                        time = 0;
                        continue;
                    }
                }
                switchInformation.put(ip,sshInformation);
                return sshInformation.getCurrEcho().toString();
            }
        }
        switchInformation.put(ip,sshInformation);
        return sshInformation.getCurrEcho().toString();
    }


    /***
    * @method: 接收命令数组，发送命令（单个）
    * @Param: [cmds 命令, othernEenterCmds]
    * @return: java.lang.String
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    public String batchCommand(String ip,String[] cmds,String notFinished, int[] othernEenterCmds,boolean quit) {
        SshInformation sshInformation = switchInformation.get(ip);

        sshInformation.setQuit(quit);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < cmds.length; i++) {
            String cmd = cmds[i];
            //判空
            if (cmd.equals("")) {
                continue;
            }
            boolean ifInputEnter = false;
            if (othernEenterCmds != null) {
                for (int c : othernEenterCmds) {
                    if (c == i) {
                        ifInputEnter = true;
                        break;
                    }
                }
            }

            // 命令结尾加上 "\n"
            cmd += (char) 10;
            //执行单个命令
            String resultEcho = runCommand(ip,cmd, notFinished,ifInputEnter);
            sb.append(resultEcho);
        }
        sshInformation.setCurrEcho(sb);
        switchInformation.put(ip,sshInformation);
        return sshInformation.getCurrEcho().toString();
    }


    public String executive(String ip,String[] cmds,String notFinished, int[] othernEenterCmds) {
        if (cmds == null || cmds.length < 3) {
            logger.error("{} ssh cmds is null", this.ip);
            return null;
        }
        if (login(ip,cmds)) {
            return batchCommand(ip,cmds,notFinished,othernEenterCmds,quit);
        }
        logger.error("{} ssh login error", this.ip);
        return null;
    }


    private abstract class SSHUserInfo implements UserInfo, UIKeyboardInteractive {
        public String getPassword() {
            return null;
        }
        public boolean promptYesNo(String str) {
            return true;
        }
        public String getPassphrase() {
            return null;
        }
        public boolean promptPassphrase(String message) {
            return true;
        }
        public boolean promptPassword(String message) {
            return true;
        }
        public void showMessage(String message) {
        }
        public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt,
                                                  boolean[] echo) {
            return null;
        }
    }
}