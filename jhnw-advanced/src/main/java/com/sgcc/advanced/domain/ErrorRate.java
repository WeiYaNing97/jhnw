package com.sgcc.advanced.domain;

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


    /** 交换机ip */
    @Excel(name = "交换机ip")
    private String switchIp;



    /** 交换机四项基本信息表ID索引 */
    @Excel(name = "交换机四项基本信息表ID索引")
    private Long switchId;

    /** 端口号 */
    @Excel(name = "端口号")
    private String port;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getSwitchIp() {
        return switchIp;
    }

    public void setSwitchIp(String switchIp) {
        this.switchIp = switchIp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ErrorRate{" +
                "id=" + id +
                ", switchIp='" + switchIp + '\'' +
                ", switchId=" + switchId +
                ", port='" + port + '\'' +
                ", description='" + description + '\'' +
                ", inputErrors='" + inputErrors + '\'' +
                ", outputErrors='" + outputErrors + '\'' +
                ", crc='" + crc + '\'' +
                '}';
    }
}
