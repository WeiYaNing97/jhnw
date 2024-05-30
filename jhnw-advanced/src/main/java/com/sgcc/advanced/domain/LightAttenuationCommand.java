package com.sgcc.advanced.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 光衰命令对象 light_attenuation_command
 * 
 * @author ruoyi
 * @date 2023-05-19
 */
public class LightAttenuationCommand extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private String id;

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

    /** 获取up端口号命令 */
    @Excel(name = "获取up端口号命令")
    private String getPortCommand;

    /** 获取光衰参数命令 */
    @Excel(name = "获取光衰参数命令")
    private String getParameterCommand;

    /** 转译 */
    @Excel(name = "转译")
    private String conversion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    public void setGetPortCommand(String getPortCommand) 
    {
        this.getPortCommand = getPortCommand;
    }

    public String getGetPortCommand() 
    {
        return getPortCommand;
    }
    public void setGetParameterCommand(String getParameterCommand) 
    {
        this.getParameterCommand = getParameterCommand;
    }

    public String getGetParameterCommand() 
    {
        return getParameterCommand;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getConversion() {
        return conversion;
    }

    public void setConversion(String conversion) {
        this.conversion = conversion;
    }

    @Override
    public String toString() {
        return "LightAttenuationCommand{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", switchType='" + switchType + '\'' +
                ", firewareVersion='" + firewareVersion + '\'' +
                ", subVersion='" + subVersion + '\'' +
                ", getPortCommand='" + getPortCommand + '\'' +
                ", getParameterCommand='" + getParameterCommand + '\'' +
                ", conversion='" + conversion + '\'' +
                '}';
    }
}
