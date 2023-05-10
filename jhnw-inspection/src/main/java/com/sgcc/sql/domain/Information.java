package com.sgcc.sql.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 交换机信息对象 information
 * 
 * @author ruoyi
 * @date 2023-03-07
 */
public class Information extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 交换机信息id */
    private Long id;

    /** 交换机品牌 */
    @Excel(name = "交换机品牌")
    private String deviceBrand;

    /** 交换机型号 */
    @Excel(name = "交换机型号")
    private String deviceModel;

    /** 预留字段1 */
    @Excel(name = "预留字段1")
    private String reserve1;

    /** 预留字段2 */
    @Excel(name = "预留字段2")
    private String reserve2;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setDeviceBrand(String deviceBrand) 
    {
        this.deviceBrand = deviceBrand;
    }

    public String getDeviceBrand() 
    {
        return deviceBrand;
    }
    public void setDeviceModel(String deviceModel) 
    {
        this.deviceModel = deviceModel;
    }

    public String getDeviceModel() 
    {
        return deviceModel;
    }
    public void setReserve1(String reserve1) 
    {
        this.reserve1 = reserve1;
    }

    public String getReserve1() 
    {
        return reserve1;
    }
    public void setReserve2(String reserve2) 
    {
        this.reserve2 = reserve2;
    }

    public String getReserve2() 
    {
        return reserve2;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deviceBrand", getDeviceBrand())
            .append("deviceModel", getDeviceModel())
            .append("reserve1", getReserve1())
            .append("reserve2", getReserve2())
            .toString();
    }
}
