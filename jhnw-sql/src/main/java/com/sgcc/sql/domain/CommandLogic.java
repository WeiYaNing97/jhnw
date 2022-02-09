package com.sgcc.sql.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 命令逻辑对象 command_logic
 * 
 * @author 韦亚宁
 * @date 2021-12-14
 */
public class CommandLogic extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键索引 */
    private Long id;

    /** 状态 */
    @Excel(name = "状态")
    private String state;

    /** 命令 */
    @Excel(name = "命令")
    private String command;

    /** 返回结果验证id */
    @Excel(name = "返回结果验证id")
    private Long resultCheckId;

    /** 返回分析id */
    @Excel(name = "返回分析id")
    private String problemId;

    /** 命令结束索引 */
    @Excel(name = "命令结束索引")
    private Long endIndex;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setState(String state) 
    {
        this.state = state;
    }

    public String getState() 
    {
        return state;
    }
    public void setCommand(String command) 
    {
        this.command = command;
    }

    public String getCommand() 
    {
        return command;
    }
    public void setResultCheckId(Long resultCheckId) 
    {
        this.resultCheckId = resultCheckId;
    }

    public Long getResultCheckId() 
    {
        return resultCheckId;
    }
    public void setProblemId(String problemId)
    {
        this.problemId = problemId;
    }

    public String getProblemId()
    {
        return problemId;
    }
    public void setEndIndex(Long endIndex) 
    {
        this.endIndex = endIndex;
    }

    public Long getEndIndex() 
    {
        return endIndex;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("state", getState())
            .append("command", getCommand())
            .append("resultCheckId", getResultCheckId())
            .append("problemId", getProblemId())
            .append("endIndex", getEndIndex())
            .toString();
    }
}
