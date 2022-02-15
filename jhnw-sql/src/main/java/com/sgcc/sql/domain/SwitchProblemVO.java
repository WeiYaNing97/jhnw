package com.sgcc.sql.domain;

import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 交换机问题对象 switch_problem
 * 
 * @author ruoyi
 * @date 2021-12-28
 */
public class SwitchProblemVO extends BaseEntity
{
    public Long hproblemId;

    /** 交换机ip */
    @Excel(name = "交换机ip")
    public String switchIp;

    /** 交换机姓名 */
    @Excel(name = "交换机姓名")
    public String switchName;

    /** 交换机密码 */
    @Excel(name = "交换机密码")
    public String switchPassword;

    /** 问题种类 */
    @Excel(name = "问题种类")
    private String typeProblem;


    public List<SwitchProblemCO> switchProblemCOList;

    public String getTypeProblem() {
        return typeProblem;
    }

    public void setTypeProblem(String typeProblem) {
        this.typeProblem = typeProblem;
    }

    public String getSwitchIp() {
        return switchIp;
    }

    public void setSwitchIp(String switchIp) {
        this.switchIp = switchIp;
    }

    public String getSwitchName() {
        return switchName;
    }

    public void setSwitchName(String switchName) {
        this.switchName = switchName;
    }

    public String getSwitchPassword() {
        return switchPassword;
    }

    public void setSwitchPassword(String switchPassword) {
        this.switchPassword = switchPassword;
    }

    public List<SwitchProblemCO> getSwitchProblemCOList() {
        return switchProblemCOList;
    }

    public void setSwitchProblemCOList(List<SwitchProblemCO> switchProblemCOList) {
        this.switchProblemCOList = switchProblemCOList;
    }

    public Long getHproblemId() {
        return hproblemId;
    }

    public void setHproblemId(Long hproblemId) {
        this.hproblemId = hproblemId;
    }


    @Override
    public String toString() {
        return "SwitchProblemVO{" +
                "hproblemId=" + hproblemId +
                ", switchIp='" + switchIp + '\'' +
                ", switchName='" + switchName + '\'' +
                ", switchPassword='" + switchPassword + '\'' +
                ", typeProblem='" + typeProblem + '\'' +
                ", switchProblemCOList=" + switchProblemCOList +
                '}';
    }
}
