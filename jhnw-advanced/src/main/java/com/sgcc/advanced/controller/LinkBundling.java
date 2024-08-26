package com.sgcc.advanced.controller;

import com.sgcc.advanced.aggregation.IPAddressCalculator;
import com.sgcc.advanced.aggregation.IPAddressUtils;
import com.sgcc.advanced.domain.IPCalculator;

/**
 * 链路捆绑
 */
public class LinkBundling {

    public static void main(String[] args) {
        boolean three_layers = LinkBundlingAnalysis("192.168.0.1", "192.168.0.0/24");
        if (three_layers) {
            System.out.println("三层链路捆绑");
        } else {
            /* 厂站掉线 三层链路也有可能成为 两层链路 */
            System.out.println("两层链路捆绑");

        }
    }


    /**
     * 链路捆绑分析
     * @param ip 给定的IP地址
     * @param ipCIDR CIDR范围的字符串表示
     * @return 如果给定的IP地址在CIDR范围内则返回true，否则返回false
     */
    public static boolean LinkBundlingAnalysis(String ip,String ipCIDR) {
        // 创建一个IPCalculator对象，用于计算CIDR范围的起始和结束IP地址
        IPCalculator calculator = IPAddressCalculator.Calculator(ipCIDR);
        // 判断给定的IP地址是否在CIDR范围内
        if (IPAddressUtils.isIPInRange(ip, calculator.getFirstAvailable(),calculator.getFinallyAvailable())) {
            // 如果在范围内，则返回true
            return true;
        }
        // 如果不在范围内，则返回false
        return false;
    }

}
