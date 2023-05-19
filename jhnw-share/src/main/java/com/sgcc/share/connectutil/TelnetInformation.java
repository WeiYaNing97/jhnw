package com.sgcc.share.connectutil;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年06月23日 13:59
 */
public class TelnetInformation {

    public String ip;
    public TelnetClient telnetClient;
    public InputStream inputStream;
    public OutputStream outputStream;
    public Thread outputThread;
    public  String returnInformation;

    public int num;
    public char[] bytes;


    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public char[] getBytes() {
        return bytes;
    }

    public void setBytes(char[] bytes) {
        this.bytes = bytes;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public TelnetClient getTelnetClient() {
        return telnetClient;
    }

    public void setTelnetClient(TelnetClient telnetClient) {
        this.telnetClient = telnetClient;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public Thread getOutputThread() {
        return outputThread;
    }

    public void setOutputThread(Thread outputThread) {
        this.outputThread = outputThread;
    }

    public String getReturnInformation() {
        return returnInformation;
    }

    public void setReturnInformation(String returnInformation) {
        this.returnInformation = returnInformation;
    }

    @Override
    public String toString() {
        return "TelnetInformation{" +
                "ip='" + ip + '\'' +
                ", telnetClient=" + telnetClient +
                ", inputStream=" + inputStream +
                ", outputStream=" + outputStream +
                ", outputThread=" + outputThread +
                ", returnInformation='" + returnInformation + '\'' +
                ", num=" + num +
                ", bytes=" + Arrays.toString(bytes) +
                '}';
    }
}