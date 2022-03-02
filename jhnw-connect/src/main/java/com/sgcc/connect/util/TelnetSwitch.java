package com.sgcc.connect.util;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.SocketException;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2021年09月27日 15:43
 */
public class TelnetSwitch {
    /**
     * 2017-12-25下午5:45:38
     * cwy
     *
     */

    private TelnetClient telnet = null;

    private String prompt = "#$>]";//#$>]
    private String loginPrompt = "login";
    private String usernamePrompt = "Username:";
    private String passwordPrompt = "Password:";

    private InputStream in;
    private PrintStream out;

    public TelnetSwitch(String host, int port) {
        if(telnet == null) {
            telnet = new TelnetClient("vt200");
            try {
                telnet.setDefaultTimeout(10000);
                telnet.connect(host,port);  //建立一个连接,默认端口是23
                this.in = telnet.getInputStream();
                this.out = new PrintStream(telnet.getOutputStream());
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 登录到远程机器中<br>
     * 说明：在登录前，先确认输入用户名的提示符，如果不是Username：，需要设置该值，使用setUsernamePrompt(prompt)；<br>
     *       第二，确认输入密码时的提示符，如果不是Password：，需要设置该值,使用setPasswordPrompt(prompt)；<br>
     *       第三，确认登录后查看是否有登录后的提示信息：如：%Apr 17 04:26:32:256 2000 Quidway SHELL/5/LOGIN:- 1 - admin(191.168.2.227) in unit1 login <br>
     *             如果末尾不是login,需要指定最后一个单词，使用setLoginPrompt(prompt)。
     如果没有登录提示，设置setLoginPrompt(null);
     *       第四，执行命令时，如果提示符不是 #、$、>、]中的一个，也需要指定最后一个符号，使用setPrompt(prompt).
     */
    public boolean login(String username, String password, String prompt) {
        //处理命令行的提示字符
        if(prompt != null && !"".equals(prompt)) {
            this.prompt = prompt;
        }
        String string1 = readUntil(this.usernamePrompt);
        write(username);
        String string2 = readUntil(this.passwordPrompt);
        write(password);
        String string3 = readUntil(this.prompt);
        if(this.loginPrompt != null)
            readUntil(this.loginPrompt);
        if (string1 !=null&&string2 !=null&&string3!=null)
            return true;
        return false;
    }

    /** * 读取分析结果 * * @param pattern * @return */
    public String readUntil(String pattern) {
        StringBuffer sb = new StringBuffer();

        try {
            int len = 0;
            while((len = this.in.read()) != -1) {
                sb.append((char)len);
                if(pattern.indexOf((char)len) != -1 || sb.toString().endsWith(pattern)) {
                    return new String(sb.toString().getBytes("ISO8859-1"), "utf8");
                }
            }
        } catch (IOException e) {
        }

        return "";
    }
    /** * 读取分析结果 * * @param pattern * @return */
    public String readUntilMethod(String pattern) {
        char endChar = pattern.charAt(pattern.length() - 1);
        char chr;
        String string = "";
        int i = 0;
        try {
            while (true) {
                i = i + 1;
                chr = (char)in.read();
                int available = in.available();
                string = string + chr;
                if ((available == 0 && (chr == endChar||chr == ']')||string.endsWith("---- More ----"))){
                    String s = new String(string.getBytes("ISO8859-1"), "GBK");// 编码转换，解决中文乱码问题
                    return s;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /** * 写操作 * * @param value */
    public void write(String value) {
        try {
            out.println(value);
            out.flush();
        } catch (Exception e) {
        }
    }

    /** * 向目标发送命令字符串 * * @param command * @return */
    public String sendCommand(String command) {
        try {
            write(command);
            String s = readUntilMethod(prompt + "");
            return s;
        } catch (Exception e) {
        }
        return "";
    }

    /** * 关闭连接 */
    public void disconnect() {
        try {
            telnet.disconnect();
        } catch (Exception e) {
        }
    }

    /**
     * 关闭打开的连接
     * @param telnet
     */
    public void close(TelnetClient telnet) {
        if(telnet != null) {
            try {
                telnet.disconnect();
            } catch (IOException e) {
            }
        }

        if(this.telnet != null) {
            try {
                this.telnet.disconnect();
            } catch (IOException e) {
            }
        }
    }


    /**
     * @return the prompt
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * @param prompt the prompt to set
     */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    /**
     * @return the usernamePrompt
     */
    public String getUsernamePrompt() {
        return usernamePrompt;
    }

    /**
     * @param usernamePrompt the usernamePrompt to set
     */
    public void setUsernamePrompt(String usernamePrompt) {
        this.usernamePrompt = usernamePrompt;
    }

    /**
     * @return the passwordPrompt
     */
    public String getPasswordPrompt() {
        return passwordPrompt;
    }

    /**
     * @param passwordPrompt the passwordPrompt to set
     */
    public void setPasswordPrompt(String passwordPrompt) {
        this.passwordPrompt = passwordPrompt;
    }

    /**
     * @return the loginPrompt
     */
    public String getLoginPrompt() {
        return loginPrompt;
    }

    /**
     * @param loginPrompt the loginPrompt to set
     */
    public void setLoginPrompt(String loginPrompt) {
        this.loginPrompt = loginPrompt;
    }
}