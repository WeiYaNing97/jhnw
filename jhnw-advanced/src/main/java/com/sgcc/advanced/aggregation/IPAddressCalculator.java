package com.sgcc.advanced.aggregation;


import com.sgcc.advanced.domain.IPBlock;
import com.sgcc.advanced.domain.IPCalculator;
import com.sgcc.share.util.MyUtils;

/** ip地址计算器
 * 自己根据ip格式编写的 与 GPT查询结果一样 都不能 /31 和 /32*/
public class IPAddressCalculator {



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
