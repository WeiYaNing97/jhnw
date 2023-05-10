package com.sgcc.share.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 交换机错误对象 switch_error
 * 
 * @author ruoyi
 * @date 2022-08-16
 */
public class SwitchError extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 错误主键 */
    private Long errorId;

    /** 交换机品牌 */
    @Excel(name = "交换机品牌")
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

    /** 错误关键字 */
    @Excel(name = "错误关键字")
    private String errorKeyword;

    /** 错误名称 */
    @Excel(name = "错误名称")
    private String errorName;

    public void setErrorId(Long errorId) 
    {
        this.errorId = errorId;
    }

    public Long getErrorId() 
    {
        return errorId;
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
    public void setErrorKeyword(String errorKeyword) 
    {
        this.errorKeyword = errorKeyword;
    }

    public String getErrorKeyword() 
    {
        return errorKeyword;
    }
    public void setErrorName(String errorName) 
    {
        this.errorName = errorName;
    }

    public String getErrorName() 
    {
        return errorName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("errorId", getErrorId())
            .append("brand", getBrand())
            .append("switchType", getSwitchType())
            .append("firewareVersion", getFirewareVersion())
            .append("subVersion", getSubVersion())
            .append("errorKeyword", getErrorKeyword())
            .append("errorName", getErrorName())
            .toString();
    }
}
