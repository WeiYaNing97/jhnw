package com.sgcc.advanced.domain;

import java.util.List;

public class OspfEnum {

    /** 邻居ID */
    public List<String> neighborID;
    /** 脉波重复间隔 */
    public List<String> pri ;
    /** 状态 */
    public List<String> state ;
    /** 停滞时间 */
    public List<String> deadTime ;
    /** 住址 */
    public List<String> address ;
    /** 端口号 */
    public List<String> portNumber ;
    /** BFD状态 */
    public List<String> BFDState ;

    public List<String> getNeighborID() {
        return neighborID;
    }

    public void setNeighborID(List<String> neighborID) {
        this.neighborID = neighborID;
    }

    public List<String> getPri() {
        return pri;
    }

    public void setPri(List<String> pri) {
        this.pri = pri;
    }

    public List<String> getState() {
        return state;
    }

    public void setState(List<String> state) {
        this.state = state;
    }

    public List<String> getDeadTime() {
        return deadTime;
    }

    public void setDeadTime(List<String> deadTime) {
        this.deadTime = deadTime;
    }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }

    public List<String> getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(List<String> portNumber) {
        this.portNumber = portNumber;
    }

    public List<String> getBFDState() {
        return BFDState;
    }

    public void setBFDState(List<String> BFDState) {
        this.BFDState = BFDState;
    }
}
