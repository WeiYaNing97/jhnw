package com.sgcc.connect.util;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年06月21日 14:29
 */
public class SshInformation {

    public Logger logger;
    //退格
    public String BACKSPACE;
    //ESC
    public String ESC;
    //空格
    public String BLANKSPACE;
    //回车
    public String ENTER;
    //某些设备回显数据中的控制字符
    public String[] PREFIX_STRS ;
    public int sleepTime;
    //连接超时(单次命令总耗时)
    public int timeout;
    //保存当前命令的回显信息
    public StringBuffer currEcho;
    //保存所有的回显信息
    public StringBuffer totalEcho;
    public String ip;
    public int port;
    public String endEcho;
    public String moreEcho;
    public String moreCmd;
    public JSch jsch;
    public Session session;
    public Channel channel;
    public boolean quit;

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public String getBACKSPACE() {
        return BACKSPACE;
    }

    public void setBACKSPACE(String BACKSPACE) {
        this.BACKSPACE = BACKSPACE;
    }

    public String getESC() {
        return ESC;
    }

    public void setESC(String ESC) {
        this.ESC = ESC;
    }

    public String getBLANKSPACE() {
        return BLANKSPACE;
    }

    public void setBLANKSPACE(String BLANKSPACE) {
        this.BLANKSPACE = BLANKSPACE;
    }

    public String getENTER() {
        return ENTER;
    }

    public void setENTER(String ENTER) {
        this.ENTER = ENTER;
    }

    public String[] getPREFIX_STRS() {
        return PREFIX_STRS;
    }

    public void setPREFIX_STRS(String[] PREFIX_STRS) {
        this.PREFIX_STRS = PREFIX_STRS;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public StringBuffer getCurrEcho() {
        return currEcho;
    }

    public void setCurrEcho(StringBuffer currEcho) {
        this.currEcho = currEcho;
    }

    public StringBuffer getTotalEcho() {
        return totalEcho;
    }

    public void setTotalEcho(StringBuffer totalEcho) {
        this.totalEcho = totalEcho;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getEndEcho() {
        return endEcho;
    }

    public void setEndEcho(String endEcho) {
        this.endEcho = endEcho;
    }

    public String getMoreEcho() {
        return moreEcho;
    }

    public void setMoreEcho(String moreEcho) {
        this.moreEcho = moreEcho;
    }

    public String getMoreCmd() {
        return moreCmd;
    }

    public void setMoreCmd(String moreCmd) {
        this.moreCmd = moreCmd;
    }

    public JSch getJsch() {
        return jsch;
    }

    public void setJsch(JSch jsch) {
        this.jsch = jsch;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public boolean isQuit() {
        return quit;
    }

    public void setQuit(boolean quit) {
        this.quit = quit;
    }


    @Override
    public String toString() {
        return "SshInformation{" +
                "logger=" + logger +
                ", BACKSPACE='" + BACKSPACE + '\'' +
                ", ESC='" + ESC + '\'' +
                ", BLANKSPACE='" + BLANKSPACE + '\'' +
                ", ENTER='" + ENTER + '\'' +
                ", PREFIX_STRS=" + Arrays.toString(PREFIX_STRS) +
                ", sleepTime=" + sleepTime +
                ", timeout=" + timeout +
                ", currEcho=" + currEcho +
                ", totalEcho=" + totalEcho +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", endEcho='" + endEcho + '\'' +
                ", moreEcho='" + moreEcho + '\'' +
                ", moreCmd='" + moreCmd + '\'' +
                ", jsch=" + jsch +
                ", session=" + session +
                ", channel=" + channel +
                ", quit=" + quit +
                '}';
    }
}