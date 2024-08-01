package com.sgcc.advanced.domain;

public class ExternalIPCalculator {

    public String destinationMask;
    public String proto;
    public String PreCost;
    public String NextHop;
    public String Interface;

    /*第一个可用:*/
    private String firstAvailable;
    /*最后可用*/
    private String finallyAvailable;


    public String getDestinationMask() {
        return destinationMask;
    }

    public void setDestinationMask(String destinationMask) {
        this.destinationMask = destinationMask;
    }

    public String getNextHop() {
        return NextHop;
    }

    public void setNextHop(String nextHop) {
        NextHop = nextHop;
    }

    public String getInterface() {
        return Interface;
    }

    public void setInterface(String anInterface) {
        Interface = anInterface;
    }

    public String getProto() {
        return proto;
    }

    public void setProto(String proto) {
        this.proto = proto;
    }

    public String getPreCost() {
        return PreCost;
    }

    public void setPreCost(String preCost) {
        PreCost = preCost;
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
        return "ExternalIP{" +
                "destinationMask='" + destinationMask + '\'' +
                ", proto='" + proto + '\'' +
                ", PreCost='" + PreCost + '\'' +
                ", NextHop='" + NextHop + '\'' +
                ", Interface='" + Interface + '\'' +
                ", firstAvailable='" + firstAvailable + '\'' +
                ", finallyAvailable='" + finallyAvailable + '\'' +
                '}';
    }
}
