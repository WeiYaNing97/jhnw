package com.sgcc.advanced.domain;

/**
 * @program: jhnw
 * @description: OSPF实体类
 * @author:
 * @create: 2023-12-22 21:24
 **/
public class OSPFPojo {
    private String  ip;
    private String  state;
    private String  port;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "OSPFPojo{" +
                "ip='" + ip + '\'' +
                ", state='" + state + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}
