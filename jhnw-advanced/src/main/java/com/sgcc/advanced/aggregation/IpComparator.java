package com.sgcc.advanced.aggregation;

import com.sgcc.advanced.domain.IPCalculator;

import java.util.Comparator;

public class IpComparator implements Comparator<IPCalculator> {

    /**
     * 比较两个IPCalculator对象。
     * <p>
     * 该方法通过将IPCalculator对象中的IP地址转换为整数（使用自定义的ipToInt方法），
     * 然后比较这两个整数来确定两个IPCalculator对象的排序顺序。
     * <p>
     * 如果o1的IP地址小于o2的IP地址，则返回负数；
     * 如果o1的IP地址等于o2的IP地址，则返回0；
     * 如果o1的IP地址大于o2的IP地址，则返回正数。
     *
     * @param o1 要比较的第一个IPCalculator对象
     * @param o2 要比较的第二个IPCalculator对象
     * @return 一个整数，表示两个对象的比较结果
     */
    @Override
    public int compare(IPCalculator o1, IPCalculator o2) {
        return ipToInt(o1.getIp()) - ipToInt(o2.getIp());
    }

    /**
     * 将点分十进制的IP地址字符串转换为整数。
     *
     * <p>此方法首先使用正则表达式"\\."将IP地址字符串（如"192.168.1.1"）分割成四个部分，
     * 然后将这四个部分（代表IP地址的各个字节）分别转换为整数，并通过左移操作（<<）
     * 将它们组合成一个整数。每个字节的整数向左移动的位置取决于它在IP地址中的位置，
     * 第一个字节（最高位）左移24位，第二个字节左移16位，以此类推。</p>
     *
     * @param ipAddress 要转换的点分十进制IP地址字符串。
     * @return 转换后的整数，代表该IP地址。
     */
    private int ipToInt(String ipAddress) {
        // 使用"."作为分隔符，将IP地址字符串分割成四个部分
        String[] parts = ipAddress.split("\\.");
        // 初始化结果为0，用于存储转换后的整数
        int result = 0;
        // 遍历分割后的字符串数组，每个元素代表IP地址中的一个字节
        for (int i = 0; i < parts.length; i++) {
            /*
            *将当前字节的字符串转换为整数
            int byteValue = Integer.parseInt(parts[i]);
            根据字节在IP地址中的位置，计算其应该左移的位数（24 - i * 8），并将该字节的值左移后累加到结果中
            result += byteValue << (24 - i * 8);
            * */
            result += Integer.parseInt(parts[i]) << (24 - i * 8);
        }
        // 返回转换后的整数结果
        return result;
    }
}
