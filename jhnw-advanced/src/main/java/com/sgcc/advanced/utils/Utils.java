package com.sgcc.advanced.utils;
import com.sgcc.advanced.domain.LightAttenuationComparison;

public class Utils {

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

}
