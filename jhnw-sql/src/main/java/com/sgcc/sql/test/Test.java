package com.sgcc.sql.test;

import com.sgcc.sql.controller.Configuration;
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

        String returnResults = "Current diagnostic parameters[AP:Average Power]:\\n\" +\n" +
                "                    \"Temp(Celsius)   Voltage(V)      Bias(mA)            RX power(dBm)       TX power(dBm)\\n\" +\n" +
                "                    \"37(OK)          3.36(OK)        15.91(OK)           -5.96(OK)[AP]       -6.04(OK)";

        HashMap<String, Double> decayValues = LuminousAttenuation.getDecayValues(returnResults);
        System.err.println();
    }
}
