package com.sgcc.share.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 异常告警信息对象 abnormal_alarm_information
 * 
 * @author ruoyi
 * @date 2024-02-26
 */
public class AbnormalAlarmInformation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 交换机ip */
    @Excel(name = "交换机ip")
    private String switchIp;

    /** 问题类型 */
    @Excel(name = "问题类型")
    private String questionType;

    /** 问题信息 */
    @Excel(name = "问题信息")
    private String questionInformation;

    /** 登录名称 */
    @Excel(name = "登录名称")
    private String userName;

    public void setSwitchIp(String switchIp) 
    {
        this.switchIp = switchIp;
    }

    public String getSwitchIp() 
    {
        return switchIp;
    }
    public void setQuestionType(String questionType) 
    {
        this.questionType = questionType;
    }

    public String getQuestionType() 
    {
        return questionType;
    }
    public void setQuestionInformation(String questionInformation) 
    {
        this.questionInformation = questionInformation;
    }

    public String getQuestionInformation() 
    {
        return questionInformation;
    }
    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("switchIp", getSwitchIp())
            .append("questionType", getQuestionType())
            .append("questionInformation", getQuestionInformation())
            .append("userName", getUserName())
            .append("createTime", getCreateTime())
            .toString();
    }
}
