package com.sgcc.share.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 交换机扫描结果对象 switch_scan_result
 * 
 * @author ruoyi
 * @date 2022-08-26
 */
public class SwitchScanResult extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 交换机ip */
    @Excel(name = "交换机ip")
    private String switchIp;

    /** 交换机四项基本信息表ID索引 */
    @Excel(name = "交换机四项基本信息表ID索引")
    private Long switchId;

    /** 交换机名 */
    @Excel(name = "交换机名")
    private String switchName;

    /** 交换机密码 */
    @Excel(name = "交换机密码")
    private String switchPassword;

    /** 交换机配置密码 */
    @Excel(name = "交换机配置密码")
    private String configureCiphers;

    /** 登录方式 */
    @Excel(name = "登录方式")
    private String loginMethod;

    /** 登录端口号 */
    @Excel(name = "登录端口号")
    private Integer portNumber;

    /** 问题索引 */
    @Excel(name = "问题索引")
    private String problemId;

    /** 问题种类 */
    @Excel(name = "问题种类")
    private String typeProblem;

    /** 范本问题名称 */
    @Excel(name = "范本问题名称")
    private String temProName;

    /** 问题名称 */
    @Excel(name = "问题名称")
    private String problemName;

    /** 备注 */
    @Excel(name = "备注")
    private String remarks;

    /** 问题详细说明和指导索引 */
    @Excel(name = "问题详细说明和指导索引")
    private String problemDescribeId;

    /** 是否有问题 */
    @Excel(name = "是否有问题")
    private String ifQuestion;

    /** 命令索引 */
    @Excel(name = "命令索引")
    private String comId;


    /** 动态信息是否为指针 */
    @Excel(name = "动态信息是否为指针")
    private Integer pointer;

    /** 动态信息 */
    @Excel(name = "动态信息")
    private StringBuffer dynamicInformation;


    /** 登录名称 */
    @Excel(name = "登录名称")
    private String userName;

    /** 登录手机号 */
    @Excel(name = "登录手机号")
    private String phonenumber;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(String loginMethod) {
        this.loginMethod = loginMethod;
    }

    public Integer getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(Integer portNumber) {
        this.portNumber = portNumber;
    }

    public String getProblemId() {
        return problemId;
    }

    public void setProblemId(String problemId) {
        this.problemId = problemId;
    }

    public String getTypeProblem() {
        return typeProblem;
    }

    public void setTypeProblem(String typeProblem) {
        this.typeProblem = typeProblem;
    }

    public String getTemProName() {
        return temProName;
    }

    public void setTemProName(String temProName) {
        this.temProName = temProName;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getProblemDescribeId() {
        return problemDescribeId;
    }

    public void setProblemDescribeId(String problemDescribeId) {
        this.problemDescribeId = problemDescribeId;
    }

    public String getIfQuestion() {
        return ifQuestion;
    }

    public void setIfQuestion(String ifQuestion) {
        this.ifQuestion = ifQuestion;
    }

    public String getComId() {
        return comId;
    }

    public void setComId(String comId) {
        this.comId = comId;
    }

    public StringBuffer getDynamicInformation() {
        return dynamicInformation;
    }

    public void setDynamicInformation(StringBuffer dynamicInformation) {
        this.dynamicInformation = dynamicInformation;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }


    public String getConfigureCiphers() {
        return configureCiphers;
    }

    public void setConfigureCiphers(String configureCiphers) {
        this.configureCiphers = configureCiphers;
    }

    public Long getSwitchId() {
        return switchId;
    }

    public void setSwitchId(Long switchId) {
        this.switchId = switchId;
    }

    public Integer getPointer() {
        return pointer;
    }

    public void setPointer(Integer pointer) {
        this.pointer = pointer;
    }

    @Override
    public String toString() {
        return "SwitchScanResult{" +
                "id=" + id +
                ", switchIp='" + switchIp + '\'' +
                ", switchId=" + switchId +
                ", switchName='" + switchName + '\'' +
                ", switchPassword='" + switchPassword + '\'' +
                ", configureCiphers='" + configureCiphers + '\'' +
                ", loginMethod='" + loginMethod + '\'' +
                ", portNumber=" + portNumber +
                ", problemId='" + problemId + '\'' +
                ", typeProblem='" + typeProblem + '\'' +
                ", temProName='" + temProName + '\'' +
                ", problemName='" + problemName + '\'' +
                ", remarks='" + remarks + '\'' +
                ", problemDescribeId='" + problemDescribeId + '\'' +
                ", ifQuestion='" + ifQuestion + '\'' +
                ", comId='" + comId + '\'' +
                ", pointer='" + pointer + '\'' +
                ", dynamicInformation='" + dynamicInformation + '\'' +
                ", userName='" + userName + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                '}';
    }
}
