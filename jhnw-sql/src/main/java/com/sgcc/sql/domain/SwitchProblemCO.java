package com.sgcc.sql.domain;

import com.sgcc.common.annotation.Excel;

import java.util.Date;
import java.util.List;

/**
 * 交换机问题对象 switch_problem
 * 
 * @author ruoyi
 * @date 2021-12-28
 */
public class SwitchProblemCO
{
    public Long hproblemId;

    public Long questionId;

    /** 登录方式 */
    @Excel(name = "登录方式")
    private String loginMethod;

    /** 登录端口号 */
    @Excel(name = "登录端口号")
    private String portNumber;

    /** 问题索引 */
    @Excel(name = "问题索引")
    public String problemId;

    /** 是否有问题 */
    @Excel(name = "是否有问题")
    private String ifQuestion;

    /** 命令索引 */
    @Excel(name = "命令索引")
    public String comId;

    /** 参数索引 */
    @Excel(name = "参数索引")
    public Long valueId;

    /** 是否解决 */
    @Excel(name = "是否解决")
    public String resolved;

    /** 问题名称 */
    @Excel(name = "问题名称")
    private String problemName;

    /** 扫描时间 */
    @Excel(name = "扫描时间")
    private Date createTime;

    public List<ValueInformationVO> valueInformationVOList;

    public String getProblemId() {
        return problemId;
    }

    public void setProblemId(String problemId) {
        this.problemId = problemId;
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

    public Long getValueId() {
        return valueId;
    }

    public void setValueId(Long valueId) {
        this.valueId = valueId;
    }

    public String getResolved() {
        return resolved;
    }

    public void setResolved(String resolved) {
        this.resolved = resolved;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }



    public List<ValueInformationVO> getValueInformationVOList() {
        return valueInformationVOList;
    }

    public void setValueInformationVOList(List<ValueInformationVO> valueInformationVOList) {
        this.valueInformationVOList = valueInformationVOList;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getHproblemId() {
        return hproblemId;
    }

    public void setHproblemId(Long hproblemId) {
        this.hproblemId = hproblemId;
    }


    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
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
        return "SwitchProblemCO{" +
                "hproblemId=" + hproblemId +
                ", questionId=" + questionId +
                ", loginMethod='" + loginMethod + '\'' +
                ", portNumber='" + portNumber + '\'' +
                ", problemId='" + problemId + '\'' +
                ", ifQuestion='" + ifQuestion + '\'' +
                ", comId='" + comId + '\'' +
                ", valueId=" + valueId +
                ", resolved='" + resolved + '\'' +
                ", problemName='" + problemName + '\'' +
                ", createTime=" + createTime +
                ", valueInformationVOList=" + valueInformationVOList +
                '}';
    }
}
