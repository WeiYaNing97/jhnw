package com.sgcc.advanced.utils;
import com.sgcc.advanced.domain.LightAttenuationComparison;
import com.sgcc.advanced.domain.OspfEnum;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.util.CustomConfigurationUtil;

import java.util.Arrays;
import java.util.Map;
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

}
