package com.sgcc.sql.domain;

import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Arrays;

/**
 * 获取基本信息命令对象 basic_information
 */
public class BasicInformationVO extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 命令 */
    @Excel(name = "命令")
    private String[] command;

    /** 自定义 */
    @Excel(name = "自定义")
    private String custom;

    /** 返回分析id */
    @Excel(name = "返回分析id")
    private String problemId;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String[] getCommand() {
        return command;
    }

    public void setCommand(String[] command) {
        this.command = command;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public String getProblemId() {
        return problemId;
    }

    public void setProblemId(String problemId) {
        this.problemId = problemId;
    }

    @Override
    public String toString() {
        return "BasicInformationVO{" +
                "id=" + id +
                ", command=" + Arrays.toString(command) +
                ", custom='" + custom + '\'' +
                ", problemId='" + problemId + '\'' +
                '}';
    }
}
