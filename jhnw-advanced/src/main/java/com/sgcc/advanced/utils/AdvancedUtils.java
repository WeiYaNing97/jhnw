package com.sgcc.advanced.utils;
import com.alibaba.fastjson.JSON;
import com.sgcc.advanced.domain.*;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.bean.BeanUtils;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.share.parametric.SwitchParameters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 运行分析模块的方法类
 */
public class AdvancedUtils {


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
     * @Description 获取字符串中的IP集合
     * @author charles
     * @createTime 2023/12/22 16:41
     * @desc
     * @param input
     * @return
     */
    public static List<String> extractIPAddresses(String input) {
        List<String> ipAddresses = new ArrayList<>();
        Pattern pattern = Pattern.compile(
                "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            ipAddresses.add(matcher.group());
        }
        return ipAddresses;
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

    /**
     * 将用户登录信息列表转换为SwitchParameters列表。
     *
     * @param switchInformation 用户登录信息列表
     * @return 转换后的SwitchParameters列表
     */
    public static List<SwitchParameters> convertSwitchInformation(List<String> switchInformation,String scanMark) {
        List<SwitchParameters> switchParametersList = new ArrayList<>();
        // 设置当前时间格式为"yyyy-MM-dd HH:mm:ss"
        String simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        for (String information : switchInformation) {
            // 解析用户登录信息为SwitchLoginInformation对象
            SwitchLoginInformation switchLoginInformation = JSON.parseObject(information, SwitchLoginInformation.class);
            // 创建SwitchParameters对象
            SwitchParameters switchParameters = new SwitchParameters();
            LoginUser loginUser = SecurityUtils.getLoginUser();
            // 设置扫描标记
            switchParameters.setScanMark(loginUser.getUsername()+scanMark);
            // 设置登录用户信息
            switchParameters.setLoginUser(loginUser);
            // 设置扫描时间
            switchParameters.setScanningTime(simpleDateFormat);
            // 将SwitchLoginInformation对象的属性复制到SwitchParameters对象中
            BeanUtils.copyBeanProp(switchParameters, switchLoginInformation);
            // 将端口号转换为整数并设置到SwitchParameters对象中
            switchParameters.setPort(Integer.valueOf(switchLoginInformation.getPort()).intValue());
            // 将转换后的SwitchParameters对象添加到列表中
            switchParametersList.add(switchParameters);
        }
        // 返回转换后的SwitchParameters列表
        return switchParametersList;
    }
}
