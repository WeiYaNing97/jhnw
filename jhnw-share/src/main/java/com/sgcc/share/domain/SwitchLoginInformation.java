package com.sgcc.share.domain;

/**
 * 交换机登录信息 实体类
 * 用于交换机扫描时 交换机登录信息的转换
 */
public class SwitchLoginInformation {
    public String ip;
    public String name;
    public String password;
    public String configureCiphers; //配置密码
    public String mode;
    public String port;
    public String row_index;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfigureCiphers() {
        return configureCiphers;
    }

    public void setConfigureCiphers(String configureCiphers) {
        this.configureCiphers = configureCiphers;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getRow_index() {
        return row_index;
    }

    public void setRow_index(String row_index) {
        this.row_index = row_index;
    }

    @Override
    public String toString() {
        return "SwitchLoginInformation{" +
                "ip='" + ip + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", configureCiphers='" + configureCiphers + '\'' +
                ", mode='" + mode + '\'' +
                ", port='" + port + '\'' +
                ", row_index='" + row_index + '\'' +
                '}';
    }
}
