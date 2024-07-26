package com.sgcc.advanced.aggregation;

import com.sgcc.advanced.domain.IPCalculator;

import java.util.Comparator;

public class IpComparator implements Comparator<IPCalculator> {

    @Override
    public int compare(IPCalculator o1, IPCalculator o2) {
        return ipToInt(o1.getIp()) - ipToInt(o2.getIp());
    }

    private int ipToInt(String ipAddress) {
        String[] parts = ipAddress.split("\\.");
        int result = 0;
        for (int i = 0; i < parts.length; i++) {
            result += Integer.parseInt(parts[i]) << (24 - i * 8);
        }
        return result;
    }
}
