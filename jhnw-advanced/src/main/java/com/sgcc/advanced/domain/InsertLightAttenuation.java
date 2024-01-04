package com.sgcc.advanced.domain;

/**
 * @program: jhnw
 * @description: 插入数据库结果
 * @author:
 * @create: 2023-12-28 15:33
 **/
public class InsertLightAttenuation {
    private int insertResults;
    private LightAttenuationComparison lightAttenuationComparison;

    public int getInsertResults() {
        return insertResults;
    }

    public void setInsertResults(int insertResults) {
        this.insertResults = insertResults;
    }

    public LightAttenuationComparison getLightAttenuationComparison() {
        return lightAttenuationComparison;
    }

    public void setLightAttenuationComparison(LightAttenuationComparison lightAttenuationComparison) {
        this.lightAttenuationComparison = lightAttenuationComparison;
    }
}
