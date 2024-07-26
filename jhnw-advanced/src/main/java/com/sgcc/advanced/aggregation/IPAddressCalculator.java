package com.sgcc.advanced.aggregation;


import com.sgcc.advanced.domain.IPBlock;
import com.sgcc.advanced.domain.IPCalculator;
import com.sgcc.share.util.MyUtils;

/** ip地址计算器
*/
public class IPAddressCalculator {


    /**
     * 自定义IP地址计算器
     * <p>
     * 该方法不使用其他现成的IP地址计算器库，而是自行实现了IP地址的CIDR（无类别域间路由）计算逻辑。
     * 与常见的IP地址计算器不同，此方法在计算地址段范围时，不会忽略全0（网络地址）和全1（广播地址），
     * 而是将它们视为有效的主机地址之一。
     * <p>
     * 方法接收一个CIDR格式的字符串（如"192.168.1.0/24"），并基于这个输入，计算出该网络的第一台可用主机地址、
     * 最后一台可用主机地址以及完整的子网掩码，并将这些信息封装在IPCalculator对象中返回。
     *
     * @param ipCIDR CIDR格式的字符串，包含IP地址和子网掩码长度。
     * @return 封装了网络地址、子网掩码、第一台可用主机地址、最后一台可用主机地址等信息的IPCalculator对象。
     */
    public static IPCalculator Calculator(String ipCIDR) {
        // 创建IPBlock对象
        IPBlock ipBlock = new IPBlock(ipCIDR);
        // 创建IPCalculator对象
        IPCalculator ipCalculator = new IPCalculator();

        // 将IP地址转换为二进制字符串
        ipCalculator.setIp(MyUtils.getIPBinarySystem(ipBlock.getIp()));
        // 创建子网掩码的前部分，由1组成
        String maskPart1 = MyUtils.splicingStringsWithTheSameCharacter("1", Integer.valueOf(ipBlock.getPrefix()).intValue());
        // 计算主机数
        int hostNumber = 32 - ipBlock.getPrefix();

        // 创建由0组成的主机数部分
        String hostNumber0 = MyUtils.splicingStringsWithTheSameCharacter("0", hostNumber);
        // 创建由1组成的主机数部分
        String hostNumber1 = MyUtils.splicingStringsWithTheSameCharacter("1", hostNumber);
        // 设置子网掩码
        ipCalculator.setMask(maskPart1+hostNumber0);
        // 获取网络地址部分
        String iPNetworkSection = ipCalculator.getIp().substring(0, ipBlock.getPrefix());

        // 设置第一台可用的地址
        ipCalculator.setFirstAvailable(iPNetworkSection + hostNumber0 );
        // 设置最后一台可用的地址
        ipCalculator.setFinallyAvailable(iPNetworkSection + hostNumber1);

        // 将IP地址、子网掩码、网络号、第一个可用的主机号、最后一个可用的主机号、广播地址转换为点分十进制形式
        ipCalculator.setIp(MyUtils.obtainIPBasedOnBinary(ipCalculator.getIp()));
        ipCalculator.setMask(MyUtils.obtainIPBasedOnBinary(ipCalculator.getMask()));
        ipCalculator.setFirstAvailable(MyUtils.obtainIPBasedOnBinary(ipCalculator.getFirstAvailable()));
        ipCalculator.setFinallyAvailable(MyUtils.obtainIPBasedOnBinary(ipCalculator.getFinallyAvailable()));

        // 返回IPCalculator对象
        return ipCalculator;
    }

}
