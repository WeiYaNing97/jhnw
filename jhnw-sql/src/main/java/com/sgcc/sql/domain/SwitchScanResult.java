package com.sgcc.sql.domain;

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

    /** 品牌 */
    @Excel(name = "品牌")
    private String brand;

    /** 型号 */
    @Excel(name = "型号")
    private String switchType;

    /** 内部固件版本 */
    @Excel(name = "内部固件版本")
    private String firewareVersion;

    /** 子版本号 */
    @Excel(name = "子版本号")
    private String subVersion;

    /** 交换机姓名 */
    @Excel(name = "交换机姓名")
    private String switchName;

    /** 交换机密码 */
    @Excel(name = "交换机密码")
    private String switchPassword;

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
    private Long problemDescribeId;

    /** 是否有问题 */
    @Excel(name = "是否有问题")
    private String ifQuestion;

    /** 命令索引 */
    @Excel(name = "命令索引")
    private String comId;

    /** 参数索引 */
    @Excel(name = "参数索引")
    private Long valueId;

    /** 动态信息 */
    @Excel(name = "动态信息")
    private String dynamicInformation;

    /** 是否解决 */
    @Excel(name = "是否解决")
    private String resolved;

    /** 登录名称 */
    @Excel(name = "登录名称")
    private String userName;

    /** 登录手机号 */
    @Excel(name = "登录手机号")
    private String phonenumber;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSwitchIp(String switchIp) 
    {
        this.switchIp = switchIp;
    }

    public String getSwitchIp() 
    {
        return switchIp;
    }
    public void setBrand(String brand) 
    {
        this.brand = brand;
    }

    public String getBrand() 
    {
        return brand;
    }
    public void setSwitchType(String switchType) 
    {
        this.switchType = switchType;
    }

    public String getSwitchType() 
    {
        return switchType;
    }
    public void setFirewareVersion(String firewareVersion) 
    {
        this.firewareVersion = firewareVersion;
    }

    public String getFirewareVersion() 
    {
        return firewareVersion;
    }
    public void setSubVersion(String subVersion) 
    {
        this.subVersion = subVersion;
    }

    public String getSubVersion() 
    {
        return subVersion;
    }
    public void setSwitchName(String switchName) 
    {
        this.switchName = switchName;
    }

    public String getSwitchName() 
    {
        return switchName;
    }
    public void setSwitchPassword(String switchPassword) 
    {
        this.switchPassword = switchPassword;
    }

    public String getSwitchPassword() 
    {
        return switchPassword;
    }
    public void setLoginMethod(String loginMethod) 
    {
        this.loginMethod = loginMethod;
    }

    public String getLoginMethod() 
    {
        return loginMethod;
    }
    public void setPortNumber(Integer portNumber) 
    {
        this.portNumber = portNumber;
    }

    public Integer getPortNumber() 
    {
        return portNumber;
    }
    public void setProblemId(String problemId) 
    {
        this.problemId = problemId;
    }

    public String getProblemId() 
    {
        return problemId;
    }
    public void setTypeProblem(String typeProblem) 
    {
        this.typeProblem = typeProblem;
    }

    public String getTypeProblem() 
    {
        return typeProblem;
    }
    public void setTemProName(String temProName) 
    {
        this.temProName = temProName;
    }

    public String getTemProName() 
    {
        return temProName;
    }
    public void setProblemName(String problemName) 
    {
        this.problemName = problemName;
    }

    public String getProblemName() 
    {
        return problemName;
    }
    public void setRemarks(String remarks) 
    {
        this.remarks = remarks;
    }

    public String getRemarks() 
    {
        return remarks;
    }
    public void setProblemDescribeId(Long problemDescribeId) 
    {
        this.problemDescribeId = problemDescribeId;
    }

    public Long getProblemDescribeId() 
    {
        return problemDescribeId;
    }
    public void setIfQuestion(String ifQuestion) 
    {
        this.ifQuestion = ifQuestion;
    }

    public String getIfQuestion() 
    {
        return ifQuestion;
    }
    public void setComId(String comId) 
    {
        this.comId = comId;
    }

    public String getComId() 
    {
        return comId;
    }
    public void setValueId(Long valueId) 
    {
        this.valueId = valueId;
    }

    public Long getValueId() 
    {
        return valueId;
    }
    public void setDynamicInformation(String dynamicInformation) 
    {
        this.dynamicInformation = dynamicInformation;
    }

    public String getDynamicInformation() 
    {
        return dynamicInformation;
    }
    public void setResolved(String resolved) 
    {
        this.resolved = resolved;
    }

    public String getResolved() 
    {
        return resolved;
    }
    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }
    public void setPhonenumber(String phonenumber) 
    {
        this.phonenumber = phonenumber;
    }

    public String getPhonenumber() 
    {
        return phonenumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("switchIp", getSwitchIp())
            .append("brand", getBrand())
            .append("switchType", getSwitchType())
            .append("firewareVersion", getFirewareVersion())
            .append("subVersion", getSubVersion())
            .append("switchName", getSwitchName())
            .append("switchPassword", getSwitchPassword())
            .append("loginMethod", getLoginMethod())
            .append("portNumber", getPortNumber())
            .append("problemId", getProblemId())
            .append("typeProblem", getTypeProblem())
            .append("temProName", getTemProName())
            .append("problemName", getProblemName())
            .append("remarks", getRemarks())
            .append("problemDescribeId", getProblemDescribeId())
            .append("ifQuestion", getIfQuestion())
            .append("comId", getComId())
            .append("valueId", getValueId())
            .append("dynamicInformation", getDynamicInformation())
            .append("resolved", getResolved())
            .append("createTime", getCreateTime())
            .append("userName", getUserName())
            .append("phonenumber", getPhonenumber())
            .toString();
    }
}
