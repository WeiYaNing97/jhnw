package com.sgcc.advanced.domain;

public class ExternalIP {
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

    @Override
    public String toString() {
        return "ExternalIP{" +
                "destinationMask='" + destinationMask + '\'' +
                ", NextHop='" + NextHop + '\'' +
                ", Interface='" + Interface + '\'' +
                '}';
    }
}
