package com.sgcc.sql.domain;

import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

import java.util.Date;
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

    /** 登录方式 */
    @Excel(name = "登录方式")
    private String loginMethod;

    /** 登录端口号 */
    @Excel(name = "登录端口号")
    private String portNumber;

    /** 问题种类 */
    @Excel(name = "问题种类")
    private String typeProblem;

    /** 扫描时间 */
    @Excel(name = "扫描时间")
    private Date createTime;

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
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(String loginMethod) {
        this.loginMethod = loginMethod;
    }

    public String getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

    @Override
    public String toString() {
        return "SwitchProblemVO{" +
                "hproblemId=" + hproblemId +
                ", switchIp='" + switchIp + '\'' +
                ", switchName='" + switchName + '\'' +
                ", switchPassword='" + switchPassword + '\'' +
                ", loginMethod='" + loginMethod + '\'' +
                ", portNumber='" + portNumber + '\'' +
                ", typeProblem='" + typeProblem + '\'' +
                ", createTime=" + createTime +
                ", switchProblemCOList=" + switchProblemCOList +
                '}';
    }
}
