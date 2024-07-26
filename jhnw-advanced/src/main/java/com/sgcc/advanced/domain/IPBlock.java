package com.sgcc.advanced.domain;


public class IPBlock {
    String ip;
    int prefix;

    public IPBlock() {}

    public IPBlock(String ipAndPrefix) {
        // 将输入的IP地址和前缀通过"/"进行分割，得到IP地址和前缀的字符串数组
        String[] parts = ipAndPrefix.split("/");
        // 将IP地址字符串赋值给成员变量ip
        this.ip = parts[0];
        // 将前缀字符串转换为整数类型，并赋值给成员变量prefix
        this.prefix = Integer.parseInt(parts[1]);
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPrefix() {
        return prefix;
    }

    public void setPrefix(int prefix) {
        this.prefix = prefix;
    }

    public String getCIDR() {
        return ip+"/"+prefix;
    }

    @Override
    public String toString() {
        return "IPBlock{" +
                "ip='" + ip + '\'' +
                ", prefix=" + prefix +
                '}';
    }
}
