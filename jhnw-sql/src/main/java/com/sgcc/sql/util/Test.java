package com.sgcc.sql.util;

import com.sgcc.sql.controller.Configuration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Test {
    public static void main(String[] args) {
        String device ="GigabitEthernet2/12";
        int firstIndexNumberOfStr = MyUtils.findFirstIndexNumberOfStr(device);
        System.err.println(firstIndexNumberOfStr);
        device = device.substring(firstIndexNumberOfStr,device.length());
        System.err.println(device);
    }
}
