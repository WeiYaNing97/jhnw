package com.sgcc.share.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 非关系型数据 存储长数据字段对象 non_relational_data_table
 * 
 * @author ruoyi
 * @date 2024-05-22
 */
public class NonRelationalDataTable extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 非关系型数据ID */
    @Excel(name = "非关系型数据ID")
    private String nonRelationalId;

    /** 非关系型数据 */
    @Excel(name = "非关系型数据")
    private String nonRelationalData;

    public void setNonRelationalId(String nonRelationalId) 
    {
        this.nonRelationalId = nonRelationalId;
    }

    public String getNonRelationalId() 
    {
        return nonRelationalId;
    }
    public void setNonRelationalData(String nonRelationalData) 
    {
        this.nonRelationalData = nonRelationalData;
    }

    public String getNonRelationalData() 
    {
        return nonRelationalData;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("nonRelationalId", getNonRelationalId())
            .append("nonRelationalData", getNonRelationalData())
            .toString();
    }
}
