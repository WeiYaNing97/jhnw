package com.sgcc.sql.domain;

import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 命令逻辑对象 command_logic
 * 
 * @author 韦亚宁
 * @date 2021-12-14
 */
public class CommandLogicVO extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键索引 */
    private String onlyIndex;

    /** 状态 */
    @Excel(name = "状态")
    private String trueFalse;

    /** pageIndex */
    @Excel(name = "命令行号")
    private String pageIndex;

    /** 命令 */
    @Excel(name = "命令")
    private String command;

    /** 返回结果验证id */
    @Excel(name = "返回结果验证id")
    private String resultCheckId;

    /** 下一ID */
    @Excel(name = "下一ID")
    private String nextIndex;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getOnlyIndex() {
        return onlyIndex;
    }

    public void setOnlyIndex(String onlyIndex) {
        this.onlyIndex = onlyIndex;
    }

    public String getTrueFalse() {
        return trueFalse;
    }

    public void setTrueFalse(String trueFalse) {
        this.trueFalse = trueFalse;
    }

    public String getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(String pageIndex) {
        this.pageIndex = pageIndex;
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

    public String getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(String nextIndex) {
        this.nextIndex = nextIndex;
    }

    @Override
    public String toString() {
        return "CommandLogicVO{" +
                "onlyIndex='" + onlyIndex + '\'' +
                ", trueFalse='" + trueFalse + '\'' +
                ", pageIndex='" + pageIndex + '\'' +
                ", command='" + command + '\'' +
                ", resultCheckId='" + resultCheckId + '\'' +
                ", nextIndex='" + nextIndex + '\'' +
                '}';
    }
}
