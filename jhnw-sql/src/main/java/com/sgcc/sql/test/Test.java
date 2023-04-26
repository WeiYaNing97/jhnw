package com.sgcc.sql.test;

import com.sgcc.sql.controller.Configuration;
import com.sgcc.sql.controller.SwitchScanResultController;
import com.sgcc.sql.senior.LuminousAttenuation;
import com.sgcc.sql.senior.OSPFFeatures;
import com.sgcc.sql.util.MyUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Test {
    public static void main(String[] args) {
        List<String> str = new ArrayList<>();
        str.add("10.122.114.89 1 Full/BDR - 00:00:39 10.122.114.89 GigabitEthernet 9/2");
        str.add("10.122.114.89 2 Full/BDR - 00:00:39 10.122.114.89 GigabitEthernet 9/2");
        str.add("10.122.114.89 3 Full/BDR - 00:00:39 10.122.114.89 GigabitEthernet 9/2");
        str.add("10.122.114.89 4 Full/BDR - 00:00:39 10.122.114.89 GigabitEthernet 9/2");
        str.add("10.122.114.89 5 Full/BDR - 00:00:39 10.122.114.89 GigabitEthernet 9/2");
        str.add("10.122.114.89 6 7 8 9 Full/BDR - 00:00:39 10.122.114.89 GigabitEthernet 9/2");
        List<String> stringList = OSPFFeatures.removOspfSpaceCharacter(7, str);
        for (String string:stringList){
            System.err.println(string);
        }
    }
}
