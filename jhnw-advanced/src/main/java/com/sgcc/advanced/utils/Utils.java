package com.sgcc.advanced.utils;
import com.sgcc.advanced.domain.LightAttenuationComparison;
import com.sgcc.advanced.domain.OspfEnum;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.util.CustomConfigurationUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 不能写入共用模块的方法类
 */
public class Utils {
    /**
     * 光衰功能 比较参数是否改变
     * @param lightAttenuationComparison
     * @param pojo
     * @return
     */
    public static boolean LightAttenuationComparisonEquals(LightAttenuationComparison lightAttenuationComparison,LightAttenuationComparison pojo) {
        /*TX平均值*/
        if (!(lightAttenuationComparison.getTxAverageValue().equals(pojo.getTxAverageValue()))){ return false; }
        /*TX最新参数*/
        if (!(lightAttenuationComparison.getTxLatestNumber().equals(pojo.getTxLatestNumber()))){ return false; }
        /*RX平均值*/
        if (!(lightAttenuationComparison.getRxAverageValue().equals(pojo.getRxAverageValue()))){ return false; }
        /*收光最新参数*/
        if (!(lightAttenuationComparison.getRxLatestNumber().equals(pojo.getRxLatestNumber()))){ return false; }
        /*TX起始值(基准)*/
        if (!(lightAttenuationComparison.getTxStartValue().equals(pojo.getTxStartValue()))){ return false; }
        /*RX起始值(基准)*/
        if (!(lightAttenuationComparison.getRxStartValue().equals(pojo.getRxStartValue()))){ return false; }
        /*rx额定偏差*/
        if (lightAttenuationComparison.getRxRatedDeviation() == null && pojo.getRxRatedDeviation() == null){
        }else {
            if ((lightAttenuationComparison.getRxRatedDeviation() != null && pojo.getRxRatedDeviation() != null)
                    && !(lightAttenuationComparison.getRxRatedDeviation().equals(pojo.getRxRatedDeviation()))){
                return false;
            }
        }
        /*tx额定偏差*/
        if (lightAttenuationComparison.getTxRatedDeviation() == null && pojo.getTxRatedDeviation() == null){
        }else {
            if ((lightAttenuationComparison.getTxRatedDeviation() != null && pojo.getTxRatedDeviation() != null)
                    && !(lightAttenuationComparison.getTxRatedDeviation().equals(pojo.getTxRatedDeviation()))){
                return false;
            }
        }
        return true;
    }


    public static OspfEnum assignment() {
        Object escape = CustomConfigurationUtil.getValue("OSPF", Constant.getProfileInformation());
        Map<String,Object> escapeMap = (Map<String,Object>) escape;

        OspfEnum ospfEnum = new OspfEnum();
        /** 邻居ID */
        ospfEnum.setNeighborID(Arrays.stream(((String) escapeMap.get("neighborID")).split(";")).collect(Collectors.toList()));
        /** 脉波重复间隔 */
        ospfEnum.setPri(Arrays.stream(((String) escapeMap.get("pri")).split(";")).collect(Collectors.toList()));
        /** 状态 */
        ospfEnum.setState(Arrays.stream(((String) escapeMap.get("state")).split(";")).collect(Collectors.toList()));
        /** 停滞时间 */
        ospfEnum.setDeadTime(Arrays.stream(((String) escapeMap.get("deadTime")).split(";")).collect(Collectors.toList()));
        /** 住址 */
        ospfEnum.setAddress(Arrays.stream(((String) escapeMap.get("address")).split(";")).collect(Collectors.toList()));
        /** 端口号 */
        ospfEnum.setPortNumber(Arrays.stream(((String) escapeMap.get("portNumber")).split(";")).collect(Collectors.toList()));
        /** BFD状态 */
        ospfEnum.setBFDState(Arrays.stream(((String) escapeMap.get("BFDState")).split(";")).collect(Collectors.toList()));
        return ospfEnum;
    }

    /**
     * 字符串 匹配配置文件中配置的 标题名称
     * @param str
     * @return
     */
    public static String enumeratorValues(String str,OspfEnum ospfEnum) {
        for (String neighborID:ospfEnum.getNeighborID()){
            if (str.equalsIgnoreCase(neighborID)){
                return "neighborID";
            }
        }
        for (String pri:ospfEnum.getPri()){
            if (str.equalsIgnoreCase(pri)){
                return "pri";
            }
        }
        for (String state:ospfEnum.getState()){
            if (str.equalsIgnoreCase(state)){
                return "state";
            }
        }
        for (String deadTime:ospfEnum.getDeadTime()){
            if (str.equalsIgnoreCase(deadTime)){
                return "deadTime";
            }
        }
        for (String address:ospfEnum.getAddress()){
            if (str.equalsIgnoreCase(address)){
                return "address";
            }
        }
        for (String portNumber:ospfEnum.getPortNumber()){
            if (str.equalsIgnoreCase(portNumber)){
                return "portNumber";
            }
        }
        for (String BFDState:ospfEnum.getBFDState()){
            if (str.equalsIgnoreCase(BFDState)){
                return "BFDState";
            }
        }
        return null;
    }

    /**
     *  判断字符串中有几个IP特征数据,并返回ip数据
     *  注意：超过255 不是ip 不会显示。测试时注意
     * @param str
     * @return
     */
    public static List<String> findIPs(String str) {
        List<String> ips = new ArrayList<>();
        //String regex = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        Pattern pattern = Pattern.compile("(\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b)");
        //Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            ips.add(matcher.group());
        }

        return ips;
    }

    /**
     * 将IP地址和子网掩码转换为CIDR表示法的字符串。
     *
     * @param ipAddress IP地址，格式为xxx.xxx.xxx.xxx
     * @param subnetMask 子网掩码，格式为xxx.xxx.xxx.xxx
     * @return 返回CIDR表示法的IP地址，格式为xxx.xxx.xxx.xxx/xx
     * @throws IllegalArgumentException 如果IP地址或子网掩码格式无效，则抛出此异常
     */
    public static String convertToCIDR(String ipAddress, String subnetMask) {
        // 将IP地址和子网掩码分为四部分
        String[] ipParts = ipAddress.split("\\.");
        String[] maskParts = subnetMask.split("\\.");

        // 验证IP地址和子网掩码是否有效
        if (ipParts.length != 4 || maskParts.length != 4) {
            throw new IllegalArgumentException("Invalid IP address or subnet mask format.");
        }

        // 计算子网掩码中'1'的个数，从而得到网络前缀的长度
        int prefixLength = 0;
        for (int i = 0; i < 4; i++) {
            int maskPart = Integer.parseInt(maskParts[i]);
            prefixLength += Integer.bitCount(maskPart);
        }

        // 返回CIDR表示法的IP地址
        return ipAddress + "/" + prefixLength;
    }
}
