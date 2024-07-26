package com.sgcc.advanced.domain;


import java.util.List;

public class IPAddresses {
    private String ipStart;
    private String ipEnd;

    private List<IPCalculator> ipCalculatorList;

    public String getIpStart() {
        return ipStart;
    }

    public void setIpStart(String ipStart) {
        this.ipStart = ipStart;
    }

    public String getIpEnd() {
        return ipEnd;
    }

    public void setIpEnd(String ipEnd) {
        this.ipEnd = ipEnd;
    }

    public List<IPCalculator> getIpCalculatorList() {
        return ipCalculatorList;
    }

    public void setIpCalculatorList(List<IPCalculator> ipCalculatorList) {
        this.ipCalculatorList = ipCalculatorList;
    }

    @Override
    public String toString() {
        return "IPAddresses{" +
                "ipStart='" + ipStart + '\'' +
                ", ipEnd='" + ipEnd + '\'' +
                ", ipCalculatorList=" + ipCalculatorList +
                '}';
    }
}
