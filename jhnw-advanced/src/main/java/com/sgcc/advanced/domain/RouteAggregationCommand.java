package com.sgcc.advanced.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 路由聚合命令对象 route_aggregation_command
 * 
 * @author ruoyi
 * @date 2024-07-29
 */
public class RouteAggregationCommand extends BaseEntity
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

    /** 获取内部宣告地址命令 */
    @Excel(name = "获取内部宣告地址命令")
    private String internalCommand;

    /** 内部关键字 */
    @Excel(name = "内部关键字")
    private String internalKeywords;

    /** 获取外部宣告地址命令 */
    @Excel(name = "获取外部宣告地址命令")
    private String externalCommand;

    /** 内部关键字 */
    @Excel(name = "内部关键字")
    private String externalKeywords;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
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
    public void setInternalCommand(String internalCommand) 
    {
        this.internalCommand = internalCommand;
    }

    public String getInternalCommand() 
    {
        return internalCommand;
    }
    public void setInternalKeywords(String internalKeywords) 
    {
        this.internalKeywords = internalKeywords;
    }

    public String getInternalKeywords() 
    {
        return internalKeywords;
    }
    public void setExternalCommand(String externalCommand) 
    {
        this.externalCommand = externalCommand;
    }

    public String getExternalCommand() 
    {
        return externalCommand;
    }
    public void setExternalKeywords(String externalKeywords) 
    {
        this.externalKeywords = externalKeywords;
    }

    public String getExternalKeywords() 
    {
        return externalKeywords;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("brand", getBrand())
            .append("switchType", getSwitchType())
            .append("firewareVersion", getFirewareVersion())
            .append("subVersion", getSubVersion())
            .append("internalCommand", getInternalCommand())
            .append("internalKeywords", getInternalKeywords())
            .append("externalCommand", getExternalCommand())
            .append("externalKeywords", getExternalKeywords())
            .toString();
    }
}
