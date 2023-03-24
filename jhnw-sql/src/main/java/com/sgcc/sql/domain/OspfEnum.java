package com.sgcc.sql.domain;

import com.sgcc.sql.controller.Configuration;

public class OspfEnum {

    /** 邻居ID */
    public static String[] neighborID;
    /** 脉波重复间隔 */
    public static String[] pri ;
    /** 状态 */
    public static String[] state ;
    /** 停滞时间 */
    public static String[] deadTime ;
    /** 住址 */
    public static String[] address ;
    /** 端口号 */
    public static String[] portNumber ;
    /** BFD状态 */
    public static String[] BFDState ;


    public static void assignment() {
        /** 邻居ID */
        neighborID = Configuration.neighborID.split(";");
        /** 脉波重复间隔 */
        pri = Configuration.pri.split(";");
        /** 状态 */
        state = Configuration.state.split(";");
        /** 停滞时间 */
        deadTime = Configuration.deadTime.split(";");
        /** 住址 */
        address = Configuration.address.split(";");
        /** 端口号 */
        portNumber = Configuration.portNumber.split(";");
        /** BFD状态 */
        BFDState = Configuration.BFDState.split(";");
    }


    public static String enumeratorValues(String str) {
        for (String neighborID:neighborID){
            if (str.equalsIgnoreCase(neighborID)){
                return "neighborID";
            }
        }
        for (String pri:pri){
            if (str.equalsIgnoreCase(pri)){
                return "pri";
            }
        }
        for (String state:state){
            if (str.equalsIgnoreCase(state)){
                return "state";
            }
        }
        for (String deadTime:deadTime){
            if (str.equalsIgnoreCase(deadTime)){
                return "deadTime";
            }
        }
        for (String address:address){
            if (str.equalsIgnoreCase(address)){
                return "address";
            }
        }
        for (String portNumber:portNumber){
            if (str.equalsIgnoreCase(portNumber)){
                return "portNumber";
            }
        }
        for (String BFDState:BFDState){
            if (str.equalsIgnoreCase(BFDState)){
                return "BFDState";
            }
        }
        return null;
    }

}
