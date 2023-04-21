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
    @Excel(name = "主键")
    private String id;

    /** 状态 */
    @Excel(name = "状态")
    private String state;

    /** 命令行号 */
    @Excel(name = "命令行号")
    private String cLine;


    /** 命令 */
    @Excel(name = "命令")
    private String command;

    /** 返回结果验证id */
    @Excel(name = "验证类型")
    private String resultCheckId;

    /** 返回分析id */
    @Excel(name = "分析索引")
    private String problemId;

    /** 命令结束索引 */
    @Excel(name = "下一命令")
    private String endIndex;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getcLine() {
        return cLine;
    }

    public void setcLine(String cLine) {
        this.cLine = cLine;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getResultCheckId() {
        return resultCheckId;
    }

    public void setResultCheckId(String resultCheckId) {
        this.resultCheckId = resultCheckId;
    }

    public String getProblemId() {
        return problemId;
    }

    public void setProblemId(String problemId) {
        this.problemId = problemId;
    }

    public String getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(String endIndex) {
        this.endIndex = endIndex;
    }

    /*导入*/
    public String getCLine() {
        return cLine;
    }
    public void setCLine(String cLine) {
        this.cLine = cLine;
    }


    @Override
    public String toString() {
        return "CommandLogic{" +
                "id='" + id + '\'' +
                ", state='" + state + '\'' +
                ", cLine='" + cLine + '\'' +
                ", command='" + command + '\'' +
                ", resultCheckId='" + resultCheckId + '\'' +
                ", problemId='" + problemId + '\'' +
                ", endIndex='" + endIndex + '\'' +
                '}';
    }
}
