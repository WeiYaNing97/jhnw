package com.sgcc.sql.test;

import com.sgcc.sql.domain.Constant;
import com.sgcc.sql.util.CustomConfigurationUtil;
import com.sgcc.sql.util.MyUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test {
    public static void main(String[] args) {

    }

    /**
     * 查看交换机错误包数量
     * @param information
     * @return
     */
    public static List<String> getParameters(String information) {
        String[] keyword = {"input errors","output errors","CRC,","CRC:","RxErrorPkts","TxErrorPkts"};
        List<String> keyList = new ArrayList<>();
        for (String key:keyword){
            if (information.toUpperCase().indexOf(key.toUpperCase())!=-1){
                keyList.add(key);
            }
        }
        List<String> returnList = new ArrayList<>();
        for (String key:keyList){
            switch (key){
                case "input errors":
                case "output errors":
                case "CRC,":
                    String[] inputoutputCRC = MyUtils.splitIgnoreCase(information, key);
                    String[] inputoutputCRCsplit = inputoutputCRC[0].split(",");
                    returnList.add(inputoutputCRCsplit[inputoutputCRCsplit.length-1]+key);
                    break;
                case "CRC:":
                    String[] CRC = MyUtils.splitIgnoreCase(information, key);
                    String[] CRCplit = CRC[1].split(",");
                    returnList.add(key + CRCplit[0]);
                    break;
                case "RxErrorPkts":
                case "TxErrorPkts":
                    String[] rxtx = information.split(":");
                    returnList.add(rxtx[0] +" : "+  rxtx[1]);
                    break;
            }
        }
        return returnList;
    }
}
