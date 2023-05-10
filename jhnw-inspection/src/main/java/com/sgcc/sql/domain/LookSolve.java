package com.sgcc.sql.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 前端 回显解决问题命令页面对象 look_solve
 * 
 * @author 王浩伟
 * @date 2022-06-01
 */
public class LookSolve extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 命令 */
    @Excel(name = "命令")
    private String command;

    /** 参数 */
    @Excel(name = "参数")
    private String comvalue;

    public void setCommand(String command) 
    {
        this.command = command;
    }

    public String getCommand() 
    {
        return command;
    }
    public void setComvalue(String comvalue) 
    {
        this.comvalue = comvalue;
    }

    public String getComvalue() 
    {
        return comvalue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("command", getCommand())
            .append("comvalue", getComvalue())
            .toString();
    }
}
