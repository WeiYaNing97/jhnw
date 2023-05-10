package com.sgcc.sql.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 问题模板对象 formwork
 * 
 * @author ruoyi
 * @date 2023-03-13
 */
public class Formwork extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 模板名称 */
    @Excel(name = "模板名称")
    private String formworkName;

    /** 模板索引 */
    @Excel(name = "模板索引")
    private String formworkIndex;

    /** 预留1 */
    @Excel(name = "预留1")
    private String reserve;

    /** 预留2 */
    @Excel(name = "预留2")
    private String reservetwo;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setFormworkName(String formworkName) 
    {
        this.formworkName = formworkName;
    }

    public String getFormworkName() 
    {
        return formworkName;
    }
    public void setFormworkIndex(String formworkIndex) 
    {
        this.formworkIndex = formworkIndex;
    }

    public String getFormworkIndex() 
    {
        return formworkIndex;
    }
    public void setReserve(String reserve) 
    {
        this.reserve = reserve;
    }

    public String getReserve() 
    {
        return reserve;
    }
    public void setReservetwo(String reservetwo) 
    {
        this.reservetwo = reservetwo;
    }

    public String getReservetwo() 
    {
        return reservetwo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("formworkName", getFormworkName())
            .append("formworkIndex", getFormworkIndex())
            .append("reserve", getReserve())
            .append("reservetwo", getReservetwo())
            .toString();
    }
}
