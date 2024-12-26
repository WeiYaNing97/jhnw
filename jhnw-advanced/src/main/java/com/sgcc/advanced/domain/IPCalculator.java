package com.sgcc.advanced.domain;


/**
 * IP地址段范围
 */
public class IPCalculator {

    /*ip*/
    private String ip;
    /*掩码*/
    private String mask;

    /*第一个可用:*/
    private String firstAvailable;
    /*最后可用*/
    private String finallyAvailable;


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }


    public String getFirstAvailable() {
        return firstAvailable;
    }

    public void setFirstAvailable(String firstAvailable) {
        this.firstAvailable = firstAvailable;
    }

    public String getFinallyAvailable() {
        return finallyAvailable;
    }

    public void setFinallyAvailable(String finallyAvailable) {
        this.finallyAvailable = finallyAvailable;
    }

    @Override
    public String toString() {
        return "IPCalculator{" +
                "ip='" + ip + '\'' +
                ", mask='" + mask + '\'' +
                ", firstAvailable='" + firstAvailable + '\'' +
                ", finallyAvailable='" + finallyAvailable + '\'' +
                '}';
    }
}
