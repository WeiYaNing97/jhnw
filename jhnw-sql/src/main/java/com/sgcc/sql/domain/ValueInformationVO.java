package com.sgcc.sql.domain;

import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 取值信息存储对象 value_information
 * 
 * @author ruoyi
 * @date 2021-12-28
 */
public class ValueInformationVO extends BaseEntity
{

    public Long hproblemId;

    /** 是否显示 */
    @Excel(name = "是否显示")
    private String exhibit;

    /** 动态信息名称 */
    @Excel(name = "动态信息名称")
    private String dynamicVname;

    /** 动态信息 */
    @Excel(name = "动态信息")
    private String dynamicInformation;


    public String getExhibit() {
        return exhibit;
    }

    public void setExhibit(String exhibit) {
        this.exhibit = exhibit;
    }

    public String getDynamicVname() {
        return dynamicVname;
    }

    public void setDynamicVname(String dynamicVname) {
        this.dynamicVname = dynamicVname;
    }

    public String getDynamicInformation() {
        return dynamicInformation;
    }

    public void setDynamicInformation(String dynamicInformation) {
        this.dynamicInformation = dynamicInformation;
    }

    public Long getHproblemId() {
        return hproblemId;
    }

    public void setHproblemId(Long hproblemId) {
        this.hproblemId = hproblemId;
    }

    @Override
    public String toString() {
        return "ValueInformationVO{" +
                "exhibit='" + exhibit + '\'' +
                ", dynamicVname='" + dynamicVname + '\'' +
                ", dynamicInformation='" + dynamicInformation + '\'' +
                ", hproblemId=" + hproblemId +
                '}';
    }
}
