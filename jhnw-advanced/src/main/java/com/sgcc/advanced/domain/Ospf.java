package com.sgcc.advanced.domain;

import com.sgcc.common.core.domain.BaseEntity;

public class Ospf extends BaseEntity {


    private String  neighborID;
    private String  pri;
    private String  state;
    private String  deadTime;
    private String  address;
    private String  portNumber;
    private String  BFDState;

    public String getNeighborID() {
        return neighborID;
    }

    public String getPri() {
        return pri;
    }

    public String getState() {
        return state;
    }

    public String getDeadTime() {
        return deadTime;
    }

    public String getAddress() {
        return address;
    }

    public String getPortNumber() {
        return portNumber;
    }

    public String getBFDState() {
        return BFDState;
    }

    public void setNeighborID(String neighborID) {
        this.neighborID = neighborID;
    }

    public void setPri(String pri) {
        this.pri = pri;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setDeadTime(String deadTime) {
        this.deadTime = deadTime;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

    public void setBFDState(String BFDState) {
        this.BFDState = BFDState;
    }

    @Override
    public String toString() {
        return "Ospf{" +
                "neighborID='" + neighborID + '\'' +
                ", pri='" + pri + '\'' +
                ", state='" + state + '\'' +
                ", deadTime='" + deadTime + '\'' +
                ", address='" + address + '\'' +
                ", portNumber='" + portNumber + '\'' +
                ", BFDState='" + BFDState + '\'' +
                '}';
    }
}
