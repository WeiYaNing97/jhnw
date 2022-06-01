package com.sgcc.sql.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 前端 解决问题对象 solve_question
 * 
 * @author 王浩伟
 * @date 2022-06-01
 */
public class SolveQuestion extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 命令 */
    @Excel(name = "命令")
    private String commond;

    /** 命令参数值 */
    @Excel(name = "命令参数值")
    private String comValue;

    public void setCommond(String commond) 
    {
        this.commond = commond;
    }

    public String getCommond() 
    {
        return commond;
    }
    public void setComValue(String comValue) 
    {
        this.comValue = comValue;
    }

    public String getComValue() 
    {
        return comValue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("commond", getCommond())
            .append("comValue", getComValue())
            .toString();
    }
}
