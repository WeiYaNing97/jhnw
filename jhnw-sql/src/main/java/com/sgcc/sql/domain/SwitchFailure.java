package com.sgcc.sql.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 交换机故障对象 switch_failure
 * 
 * @author ruoyi
 * @date 2022-07-26
 */
public class SwitchFailure extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 故障主键 */
    private Long failureId;

    /** 交换机品牌 */
    @Excel(name = "交换机品牌")
    private String brand;

    /** 故障关键字 */
    @Excel(name = "故障关键字")
    private String failureKeyword;

    /** 故障名称 */
    @Excel(name = "故障名称")
    private String failureName;

    public void setFailureId(Long failureId) 
    {
        this.failureId = failureId;
    }

    public Long getFailureId() 
    {
        return failureId;
    }
    public void setBrand(String brand) 
    {
        this.brand = brand;
    }

    public String getBrand() 
    {
        return brand;
    }
    public void setFailureKeyword(String failureKeyword) 
    {
        this.failureKeyword = failureKeyword;
    }

    public String getFailureKeyword() 
    {
        return failureKeyword;
    }
    public void setFailureName(String failureName) 
    {
        this.failureName = failureName;
    }

    public String getFailureName() 
    {
        return failureName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("failureId", getFailureId())
            .append("brand", getBrand())
            .append("failureKeyword", getFailureKeyword())
            .append("failureName", getFailureName())
            .toString();
    }
}
