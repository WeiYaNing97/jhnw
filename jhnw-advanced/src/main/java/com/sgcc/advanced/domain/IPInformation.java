package com.sgcc.advanced.domain;


public class IPInformation {
    private String ip;
    /*掩码*/
    private String mask;
    private String area;

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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "IPInformation{" +
                "ip='" + ip + '\'' +
                ", mask='" + mask + '\'' +
                ", area='" + area + '\'' +
                '}';
    }
}
