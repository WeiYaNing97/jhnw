package com.sgcc.advanced.aggregation;


import com.sgcc.advanced.domain.IPAddresses;
import com.sgcc.advanced.domain.IPCalculator;
import com.sgcc.advanced.domain.IPInformation;
import com.sgcc.share.util.MyUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;
public class IPAddressUtils {

    /**
     * 从给定的返回信息中提取IP信息，并返回IP信息列表
     *
     * @param returnInformation 包含IP信息的字符串
     * @return 包含提取出的IP信息的列表
     */
    public static List<IPInformation> getIPInformation(String returnInformation) {
        List<IPInformation> ipInformationList = new ArrayList<>();
        // 按照换行符将返回信息分割成数组
        String[] informationSplit = returnInformation.split("\r\n");

        for (String information:informationSplit){
            // 如果信息中包含"network"字符串
            // todo 获取地址关键字需要确定
            if (information.toLowerCase().indexOf("network".toLowerCase())!=-1){

                IPInformation ipInformation = new IPInformation();

                if (information.toLowerCase().indexOf("area".toLowerCase())!=-1){
                    // 获取地址关键字
                    String[] split = MyUtils.splitIgnoreCase(information,"area");
                    if (split.length == 2){
                        // 将地址关键字添加到结果列表中
                        List<String> iPs = MyUtils.findIPs(split[1]);
                        if (iPs.size() == 1){
                            ipInformation.setArea(iPs.get(0));
                        }
                    }
                    information = split[0];
                }

                // 查找信息中的IP地址
                // 判断字符串中有几个IP特征数据,并返回ip数据
                List<String> iPs = MyUtils.findIPs(information);

                if (iPs.size() == 2){
                    // 从最后一个IP地址开始遍历
                    for (int num = iPs.size()-1 ; num >= 0;num--){
                        // 将IP地址按"."分割成整数列表
                        List<Integer> collect = Arrays.stream(iPs.get(num).split("\\.")).map(Integer::parseInt).collect(Collectors.toList());
                        // 如果第一个整数为0，则将其替换为255减去原值  反子网掩码
                        if (collect.get(0) == 0){
                            collect.set(0,255-collect.get(0));
                            collect.set(1,255-collect.get(1));
                            collect.set(2,255-collect.get(2));
                            collect.set(3,255-collect.get(3));
                        }
                        // 将处理后的整数列表转换成IP地址格式，并替换原IP地址
                        iPs.set(num,collect.get(0)+"."+collect.get(1)+"."+collect.get(2)+"."+collect.get(3));
                    }

                    ipInformation.setIp(iPs.get(0));
                    ipInformation.setMask(iPs.get(1));
                }

                ipInformationList.add(ipInformation);
            }
        }
        return ipInformationList;
    }

    /**
     * 根据IP地址对IPCalculator实体类列表进行排序
     *
     * @param ipCalculatorList IPCalculator实体类列表
     * @return 排序后的IPCalculator实体类列表
     */
    public static List<IPCalculator> sortIPCalculator(List<IPCalculator> ipCalculatorList) {
        // 使用IpComparator对ipCalculatorList进行排序
        ipCalculatorList.sort(new IpComparator());
        // 返回排序后的ipCalculatorList
        return ipCalculatorList;
    }

    /**
     * 判断一个IP地址是否在指定的IP地址范围内
     *
     * @param ipToCheck 要检查的IP地址
     * @param startIP 起始IP地址
     * @param endIP 结束IP地址
     * @return 如果要检查的IP地址在指定的范围内，则返回true；否则返回false
     */
    public static boolean isIPInRange(String ipToCheck, String startIP, String endIP) {
        // 将起始IP地址转换为长整型
        long start = ipToLong(startIP);
        // 将结束IP地址转换为长整型
        long end = ipToLong(endIP);
        // 将要检查的IP地址转换为长整型
        long check = ipToLong(ipToCheck);

        // 判断要检查的IP地址是否在指定范围内
        return check >= start && check <= end;
    }

    /**
     * 将IP地址字符串转换为长整型
     *
     * @param ipAddress IP地址字符串
     * @return 转换后的长整型值
     * @throws IllegalArgumentException 如果输入的IP地址字符串无效
     */
    private static long ipToLong(String ipAddress) {
        try {
            // 通过InetAddress类的getByName方法将IP地址字符串转换为InetAddress对象，并获取其字节数组
            byte[] bytes = InetAddress.getByName(ipAddress).getAddress();
            long result = 0;
            // 遍历字节数组，将每个字节转换为长整型并拼接起来
            for (byte b : bytes) {
                // 将result左移8位，相当于将result乘以2的8次方
                result <<= 8;
                // 将当前字节b与0xFF进行位与运算，确保只保留低8位，然后与result进行位或运算，将结果保存到result中
                result |= (b & 0xFF);
            }
            // 返回转换后的长整型值
            return result;
        } catch (UnknownHostException e) {
            // 如果输入的IP地址字符串无效，则抛出IllegalArgumentException异常，并携带UnknownHostException异常作为原因
            throw new IllegalArgumentException("Invalid IP address", e);
        }
    }

    /**
     * 将长整型表示的IP地址转换为点分十进制格式的字符串
     *
     * @param ipAsLong 长整型表示的IP地址
     * @return 点分十进制格式的IP地址字符串
     * @throws RuntimeException 如果IP地址转换过程中发生错误
     */
    public static String longToIp(long ipAsLong) {
        // 创建一个长度为4的字节数组用于存储IP地址的各个部分
        byte[] bytes = new byte[4];
        // 遍历字节数组的每一个位置
        for (int i = 0; i < 4; ++i) {
            // 计算当前位置的偏移量
            int offset = (3 - i) * 8;
            // 将长整型表示的IP地址右移对应偏移量，然后与0xFF进行按位与运算，将结果存储在字节数组的当前位置
            bytes[i] = (byte) ((ipAsLong >> offset) & 0xFF);
        }

        try {
            // 将字节数组转换为InetAddress对象，并获取其主机地址（点分十进制格式的IP地址字符串）
            // return InetAddress.getByAddress(bytes).getHostAddress();
            return InetAddress.getByAddress(bytes).getHostAddress();
        } catch (UnknownHostException e) {
            // 如果转换过程中发生异常，则抛出RuntimeException
            throw new RuntimeException(e);
        }
    }

    /**
     * 将传入的IP计算器列表进行IP地址段的拼接，返回拼接后的IP地址段列表。
     *
     * @param ipCalculatorList IP计算器列表，每个IP计算器包含可用IP地址范围
     * @return 拼接后的IP地址段列表
     */
    public static List<IPAddresses> splicingAddressRange(List<IPCalculator> ipCalculatorList) {
        List<IPAddresses> ipAddressesList = new ArrayList<>();
        for (IPCalculator ipCalculator : ipCalculatorList) {
            IPAddresses ipAddresses = new IPAddresses();
            ipAddresses.setIpStart(ipCalculator.getFirstAvailable());
            ipAddresses.setIpEnd(ipCalculator.getFinallyAvailable());
            List<IPCalculator> ipCalculators = new ArrayList<>();
            ipCalculators.add(ipCalculator);
            ipAddresses.setIpCalculatorList(ipCalculators);
            ipAddressesList.add(ipAddresses);
        }

        // 标记是否需要继续合并IP地址段
        boolean flag = true;
        while (flag){
            flag = false;
            for (int i = ipAddressesList.size()-1; i >= 1; i--){
                // 判断当前IP地址段与前一个IP地址段是否有交集或连续
                if (isIPInRange(ipAddressesList.get(i-1).getIpStart(),
                        ipAddressesList.get(i).getIpStart(), ipAddressesList.get(i).getIpEnd())
                        || isIPInRange(ipAddressesList.get(i).getIpStart(),
                        ipAddressesList.get(i-1).getIpStart(), ipAddressesList.get(i-1).getIpEnd())
                        || determineIPContinuity(ipAddressesList.get(i-1).getIpEnd(), ipAddressesList.get(i).getIpStart())
                        || determineIPContinuity(ipAddressesList.get(i).getIpEnd(), ipAddressesList.get(i-1).getIpStart())) {
                    flag = true;

                    // 合并IP地址段
                    List<String> ipAddresses = new ArrayList<>();
                    ipAddresses.add(ipAddressesList.get(i).getIpStart());
                    ipAddresses.add(ipAddressesList.get(i).getIpEnd());
                    ipAddresses.add(ipAddressesList.get(i-1).getIpStart());
                    ipAddresses.add(ipAddressesList.get(i-1).getIpEnd());

                    // 获取合并后的起始和结束IP地址
                    String minIp = getMinIP(ipAddresses);
                    String maxIp = getMaxIP(ipAddresses);

                    // 创建新的IP地址段对象
                    IPAddresses pojo = new IPAddresses();
                    pojo.setIpStart(minIp);
                    pojo.setIpEnd(maxIp);
                    List<IPCalculator> ipCalculators = new ArrayList<>();
                    // 合并IP计算器列表
                    ipCalculators.addAll(ipAddressesList.get(i).getIpCalculatorList());
                    ipCalculators.addAll(ipAddressesList.get(i-1).getIpCalculatorList());
                    pojo.setIpCalculatorList(ipCalculators);

                    // 移除旧的IP地址段对象
                    ipAddressesList.remove(ipAddressesList.get(i));
                    ipAddressesList.remove(ipAddressesList.get(i-1));
                    // 添加新的IP地址段对象
                    ipAddressesList.add(pojo);
                }
            }
        }

        return ipAddressesList;
    }

    /**
     * 判断两个IP地址是否连续
     *
     * @param ip1 第一个IP地址，格式为点分十进制字符串
     * @param ip2 第二个IP地址，格式为点分十进制字符串
     * @return 如果两个IP地址连续，则返回true；否则返回false
     */
    public static boolean determineIPContinuity(String ip1, String ip2) {
        // 将第一个IP地址转换为长整型
        long l1 = ipToLong(ip1);
        // 将第二个IP地址转换为长整型
        long l2 = ipToLong(ip2);
        // 判断两个长整型表示的IP地址之间的差的绝对值是否为1
        // 如果是，则两个IP地址连续
        return Math.abs(l1 - l2) == 1l;
    }

    /**
     * 从给定的IP地址列表中获取最大的IP地址。
     *
     * @param ipAddresses 包含IP地址的字符串列表
     * @return 列表中的最大IP地址，如果不存在则返回null
     * @throws NullPointerException 如果传入的ipAddresses参数为null
     * @throws ClassCastException   如果ipAddresses列表中的某个元素无法转换为长整型表示的IP地址
     */
    public static String getMaxIP(List<String> ipAddresses) {
        // 使用Collections.max方法获取ipAddresses列表中的最大IP地址
        // 使用Comparator.comparing方法指定比较规则，通过IPAddressUtils的toLong方法将IP地址字符串转换为长整型进行比较
        return Collections.max(ipAddresses, Comparator.comparing(IPAddressUtils::toLong));
    }

    /**
     * 从给定的IP地址列表中获取最小的IP地址。
     *
     * @param ipAddresses 包含IP地址的字符串列表
     * @return 列表中最小的IP地址，如果不存在则返回null
     */
    public static String getMinIP(List<String> ipAddresses) {
        // 使用Collections.min方法获取列表中的最小IP地址
        // 通过Comparator.comparing方法指定比较器，将IP地址字符串转换为长整型进行比较
        return Collections.min(ipAddresses, Comparator.comparing(IPAddressUtils::toLong));
    }

    /**
     * 将IP地址字符串转换为长整型表示
     *
     * @param ipAddress IP地址字符串，格式为"xxx.xxx.xxx.xxx"
     * @return 转换后的长整型表示
     * @throws NumberFormatException 如果传入的字符串不符合IP地址的格式
     */
    public static long toLong(String ipAddress) {
        // 使用点号分割IP地址字符串
        String[] parts = ipAddress.split("\\.");
        long result = 0;
        // 遍历IP地址的每个部分
        for (int i = 0; i < parts.length; i++) {
            // 将当前部分转换为长整型，并根据位置进行位移和或运算
            result |= Long.parseLong(parts[i]) << (24 - (8 * i));
        }
        // 返回结果
        return result;
    }

    /**
     * 根据IP地址的整数表示形式获取子网掩码位数
     *
     * @param num IP地址的整数表示形式
     * @return 返回子网掩码的位数
     * @throws IllegalArgumentException 如果传入的参数不是有效的IP地址整数表示形式（即小于0或大于255.255.255.255对应的整数）
     *
     * 注意：此方法假设IP地址的整数表示形式为无符号整数，并且其值在0到2^32-1之间（包含）。
     * 子网掩码位数通过计算IP地址的二进制长度并减去该长度后加1得到。
     */
    public static Integer getTheNumberOfMasks(int num) {
        // 将整数num转换为二进制字符串
        String binaryString = Integer.toBinaryString(num);
        // 返回32减去二进制字符串的长度后加1的结果，即为子网掩码的位数
        return 32 - binaryString.length() + 1;
    }

    /**
     * 计算两个IP地址之间的地址数量（包括起始和结束IP地址）
     *
     * @param ip1 起始IP地址，格式为点分十进制字符串
     * @param ip2 结束IP地址，格式为点分十进制字符串
     * @return 返回起始和结束IP地址之间的地址数量（包括起始和结束IP地址），类型为Integer
     * @throws NumberFormatException 如果起始或结束IP地址的格式不正确
     */
    public static Integer numberOfAddresses(String ip1, String ip2) {
        // 将起始IP地址转换为长整型
        long l1 = ipToLong(ip1);
        // 将结束IP地址转换为长整型
        long l2 = ipToLong(ip2);

        // 计算起始和结束IP地址之间的地址数量（包括起始和结束IP地址）
        // 取绝对值表示无论l2比l1大还是小，结果都是正数
        // 加1表示包含起始和结束IP地址
        return (int) Math.abs(l2 - l1 + 1);
    }

    public static List<String> getNetworkNumber(IPAddresses ipAddress) {
        List<String> ipList = new ArrayList<>();
        String sign = ipAddress.getIpStart();
        String ipEnd = ipAddress.getIpEnd();
        // 遍历IP地址段
        while (ipToLong(sign) <= ipToLong(ipEnd)){
            // 如果起始IP和结束IP相等
            if (ipToLong(sign) == ipToLong(ipEnd)){
                // 将起始IP以"/32"的形式添加到列表中
                ipList.add(sign + "/32");

                // 创建一个IP地址计算器对象，并输出起始IP的范围
                /*IPCalculator calculator = IPAddressCalculator.Calculator(sign + "/32");
                System.out.println(sign + "/32"+"范围是: ["+calculator.getFirstAvailable()+","+calculator.getFinallyAvailable()+"]");*/

                // 跳出循环
                break;
            }
            // 计算起始IP和结束IP之间的地址数量
            Integer ipNumber = numberOfAddresses(sign, ipEnd);
            Integer mask = getTheNumberOfMasks(ipNumber);
            boolean isMask = true;
            String ip = "";
            while (isMask){
                // 将起始IP和子网掩码位数组合成IP地址字符串
                ip = sign + "/" + mask;
                // 创建一个IP地址计算器对象
                IPCalculator calculator = IPAddressCalculator.Calculator(ip);

                if (ipToLong(sign) == ipToLong(calculator.getFirstAvailable())){
                    /*System.out.println(ip+"范围是: ["+calculator.getFirstAvailable()+","+calculator.getFinallyAvailable()+"]");*/
                    /*System.out.println(calculator.toString());*/
                    isMask = false;
                    // 将当前IP地址范围的最后一个可用IP地址转换为长整型，并加1
                    ///*String finallyAvailable = calculator.getFinallyAvailable();*/
                    sign = longToIp(ipToLong(calculator.getFinallyAvailable()) + 1);
                }else {
                    mask++;
                }

            }
            // 将当前IP地址添加到列表中
            ipList.add(ip);
        }
        return ipList;
    }


    public static void main1(String[] args) {
        IPAddresses ipAddress =new IPAddresses();

        ipAddress.setIpStart("192.168.1.11");
        ipAddress.setIpEnd("192.168.1.193");

        System.err.println(ipAddress.getIpStart() +" ~ "+ ipAddress.getIpEnd());

        List<String> networkNumber = getNetworkNumber(ipAddress);
    }

    public static void main(String[] args) {
        String ip = "192.168.2.3";
        // 将IP地址转换为长整型
        long l = ipToLong(ip);
        System.out.println("IP地址：" + ip + "，对应的整数表示为：" + l);

        String ip1 = "192.168.1.3";
        // 将IP地址转换为长整型
        long l1 = ipToLong(ip1);
        System.out.println("IP地址：" + ip1 + "，对应的整数表示为：" + l1);

        System.err.println(Math.abs(l - l1));

        System.out.println(longToIp(l));
    }
}
