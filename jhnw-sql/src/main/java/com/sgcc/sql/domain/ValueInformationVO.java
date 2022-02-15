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

    //前端需要ID
    public Long hproblemId;
    //参数ID  = id
    public Long parameterId;

    /** 是否显示 */
    @Excel(name = "是否显示")
    private String exhibit;

    /** 动态信息名称 */
    @Excel(name = "动态信息名称")
    private String dynamicVname;

    /** 动态信息 */
    @Excel(name = "动态信息")
    private String dynamicInformation;

    public Long getHproblemId() {
        return hproblemId;
    }

    public void setHproblemId(Long hproblemId) {
        this.hproblemId = hproblemId;
    }

    public Long getParameterId() {
        return parameterId;
    }

    public void setParameterId(Long parameterId) {
        this.parameterId = parameterId;
    }

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

    @Override
    public String toString() {
        return "ValueInformationVO{" +
                "hproblemId=" + hproblemId +
                ", parameterId=" + parameterId +
                ", exhibit='" + exhibit + '\'' +
                ", dynamicVname='" + dynamicVname + '\'' +
                ", dynamicInformation='" + dynamicInformation + '\'' +
                '}';
    }
}
