package com.sgcc.sql.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 获取基本信息命令对象 basic_information
 * 
 * @author 韦亚宁
 * @date 2021-12-21
 */
public class BasicInformation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 命令 */
    @Excel(name = "命令")
    private String command;

    /** 返回分析id */
    @Excel(name = "返回分析id")
    private String problemId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCommand(String command) 
    {
        this.command = command;
    }

    public String getCommand() 
    {
        return command;
    }
    public void setProblemId(String problemId) 
    {
        this.problemId = problemId;
    }

    public String getProblemId() 
    {
        return problemId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("command", getCommand())
            .append("problemId", getProblemId())
            .toString();
    }
}
