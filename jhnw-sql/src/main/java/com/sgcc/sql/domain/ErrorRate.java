package com.sgcc.sql.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 误码率对象 error_rate
 * 
 * @author ruoyi
 * @date 2023-05-09
 */
public class ErrorRate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 误码率主键 */
    private Long id;

    /** 交换机四项基本信息表ID索引 */
    @Excel(name = "交换机四项基本信息表ID索引")
    private Long switchId;

    /** 端口号 */
    @Excel(name = "端口号")
    private String port;

    /** input errors */
    @Excel(name = "input errors")
    private String inputErrors;

    /** output errors */
    @Excel(name = "output errors")
    private String outputErrors;

    /** crc */
    @Excel(name = "crc")
    private String crc;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSwitchId(Long switchId) 
    {
        this.switchId = switchId;
    }

    public Long getSwitchId() 
    {
        return switchId;
    }
    public void setPort(String port) 
    {
        this.port = port;
    }

    public String getPort() 
    {
        return port;
    }
    public void setInputErrors(String inputErrors)
    {
        this.inputErrors = inputErrors;
    }

    public String getInputErrors()
    {
        return inputErrors;
    }
    public void setOutputErrors(String outputErrors)
    {
        this.outputErrors = outputErrors;
    }

    public String getOutputErrors()
    {
        return outputErrors;
    }
    public void setCrc(String crc)
    {
        this.crc = crc;
    }

    public String getCrc()
    {
        return crc;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("switchId", getSwitchId())
            .append("port", getPort())
            .append("inputErrors", getInputErrors())
            .append("outputErrors", getOutputErrors())
            .append("crc", getCrc())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
