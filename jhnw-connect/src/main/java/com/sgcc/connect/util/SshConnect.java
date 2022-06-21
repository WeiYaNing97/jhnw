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

    @Override
    public void run() {
        InputStream is;
        try {
            is = channel.getInputStream();
            //读取服务器执行命令后返回信息
            String echo = readOneEcho(is);
            while (echo != null) {
                currEcho.append(echo);
                String[] lineStr = echo.split("\\n");
                if (lineStr != null && lineStr.length > 0) {
                    String lastLineStr = lineStr[lineStr.length - 1];
                    if (lastLineStr != null && lastLineStr.indexOf(moreEcho) > 0) {
                        totalEcho.append(echo.replace(lastLineStr, ""));
                    } else {
                        totalEcho.append(echo);
                    }
                }
                echo = readOneEcho(is);
            }
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
        this.ip = ip;
        this.port = port;
        if (endEcho != null) {
            this.endEcho = endEcho;
        }
        if (moreEcho != null) {
            this.moreEcho = moreEcho;
        }
        totalEcho = new StringBuffer();
        currEcho = new StringBuffer();
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
    public boolean login(String[] cmds) {
        //用户名 密码
        String user = cmds[0];
        String passWord = cmds[1];
        //ssh连接工具包 创建工具类
        jsch = new JSch();

        try {
            //@method: jsch 获取会话
            //@Param: [user 用户名, this.ip IP地址, this.port 端口号]
            session = jsch.getSession(user, this.ip, this.port);
            //输入密码
            session.setPassword(passWord);
            UserInfo ui = new SSHUserInfo() {
                public void showMessage(String message) {
                }
                public boolean promptYesNo(String message) {
                    return true;
                }
            };
            session.setUserInfo(ui);
            session.connect(30000);
            channel = session.openChannel("shell");
            //jscn 连接 linux 解决高亮显示乱码问题
            ((ChannelShell) channel).setPtyType("dumb");
            //((ChannelShell) channel).setPty(false);
            channel.connect(3000);
            new Thread(this).start();
            try {
                Thread.sleep(sleepTime);
            } catch (Exception e) {
            }
            return true;
        } catch (JSchException e) {
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
    protected String sendCommand(String command, boolean sendEnter) {
        try {
            OutputStream os = channel.getOutputStream();
            os.write(command.getBytes());
            os.flush();
            if (quit){
                currEcho.append("遗失对主机的连接。");
                return "遗失对主机的连接。";
            }
            if (sendEnter) {
                currEcho = new StringBuffer();
                os.write(ENTER.getBytes());
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    //测试此字符串是否以指定的后缀结尾
    protected boolean containsEchoEnd(String echo) {
        boolean contains = false;
        //如果 结尾标识符 为空 返回 错误
        //endEcho = "#,?,>,:";
        if (endEcho == null || endEcho.trim().equals("")) {
            return contains;
        }
        //结尾标识符数组
        String[] eds = endEcho.split(",");
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
    private String runCommand(String command,String notFinished,boolean ifEnter) {

        if (notFinished!=null && notFinished!=""){
            moreEcho = notFinished;
        }

        this.currEcho = new StringBuffer();
        //向服务器发送命令
        String str = sendCommand(command, ifEnter);
        if (str =="遗失对主机的连接。"){
            return str;
        }
        int time = 0;
        //endEcho 不存在 为空
        //endEcho = "#,?,>,:";
        if (endEcho == null || endEcho.equals("")) {
            while (currEcho.toString().equals("")) {
                try {
                    Thread.sleep(sleepTime);
                    time += sleepTime;
                    //连接超时(单次命令总耗时)
                    if (time >= timeout) {
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            //endEcho 存在 不为空
            // containsEchoEnd 测试此字符串是否以指定的后缀结尾   是 true   否 false
            while (!containsEchoEnd(currEcho.toString())) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time += sleepTime;
                //连接超时(单次命令总耗时)
                if (time >= timeout) {
                    break;
                }

                //接收交换机返回信息 转换为字符串数组
                String[] lineStrs = currEcho.toString().split("\\n");


                //接收交换机返回信息不为空
                if (lineStrs != null && lineStrs.length > 0) {
                    //private String moreEcho = "---- More ----";
                    //接收交换机返回信息的最后信息 lineStrs[lineStrs.length - 1]
                    //接收交换机返回信息的最后信息 是否是 "---- More ----" 是 则没有返回完全
                    if (moreEcho != null && lineStrs[lineStrs.length - 1] != null
                            && lineStrs[lineStrs.length - 1].contains(moreEcho)) {
                        //private String moreCmd = BLANKSPACE;
                        //空格  private static final String BLANKSPACE = new String(new byte[] { 32 });
                        sendCommand(moreCmd, false);
                        currEcho.append("\n");
                        time = 0;
                        continue;
                    }
                }
                return currEcho.toString();
            }
        }
        return currEcho.toString();
    }


    /***
    * @method: 接收命令数组，发送命令（单个）
    * @Param: [cmds 命令, othernEenterCmds]
    * @return: java.lang.String
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    public String batchCommand(String[] cmds,String notFinished, int[] othernEenterCmds,boolean quit) {
        this.quit=quit;
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
            String resultEcho = runCommand(cmd, notFinished,ifInputEnter);
            sb.append(resultEcho);
        }
        return currEcho.toString();
    }


    public String executive(String[] cmds,String notFinished, int[] othernEenterCmds) {
        if (cmds == null || cmds.length < 3) {
            logger.error("{} ssh cmds is null", this.ip);
            return null;
        }
        if (login(cmds)) {
            return batchCommand(cmds,notFinished,othernEenterCmds,quit);
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