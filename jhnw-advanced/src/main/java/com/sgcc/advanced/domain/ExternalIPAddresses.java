package com.sgcc.advanced.domain;


import java.util.List;

public class ExternalIPAddresses {
    private String ipStart;
    private String ipEnd;

    private List<ExternalIPCalculator> externalIPCalculatorList;

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

    public List<ExternalIPCalculator> getExternalIPCalculatorList() {
        return externalIPCalculatorList;
    }

    public void setExternalIPCalculatorList(List<ExternalIPCalculator> externalIPCalculatorList) {
        this.externalIPCalculatorList = externalIPCalculatorList;
    }

    @Override
    public String toString() {
        return "ExternalIPAddresses{" +
                "ipStart='" + ipStart + '\'' +
                ", ipEnd='" + ipEnd + '\'' +
                ", externalIPCalculatorList=" + externalIPCalculatorList +
                '}';
    }
}
