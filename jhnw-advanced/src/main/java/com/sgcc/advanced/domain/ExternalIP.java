package com.sgcc.advanced.domain;

public class ExternalIP {

    public String proto;
    public String destinationMask;
    public String NextHop;
    public String Interface;

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

    @Override
    public String toString() {
        return "ExternalIP{" +
                "proto='" + proto + '\'' +
                ", destinationMask='" + destinationMask + '\'' +
                ", NextHop='" + NextHop + '\'' +
                ", Interface='" + Interface + '\'' +
                '}';
    }
}
