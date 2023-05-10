package com.sgcc.sql.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 问题描述对象 problem_describe
 * 
 * @author ruoyi
 * @date 2022-05-26
 */
public class ProblemDescribe extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 问题描述ID */
    private Long id;

    /** 问题描述 */
    @Excel(name = "问题描述")
    private String problemDescribe;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setProblemDescribe(String problemDescribe) 
    {
        this.problemDescribe = problemDescribe;
    }

    public String getProblemDescribe() 
    {
        return problemDescribe;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("problemDescribe", getProblemDescribe())
            .toString();
    }
}
