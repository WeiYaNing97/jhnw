package com.sgcc.sql.domain;

import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 问题及命令对象 total_question_table
 * 
 * @author 韦亚宁
 * @date 2021-12-14
 */
public class TotalQuestionTable extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键索引 */
    private Long id;

    /** 品牌 */
    @Excel(name = "品牌")
    private String brand;

    /** 型号 */
    @Excel(name = "型号")
    private String type;

    /** 内部固件版本 */
    @Excel(name = "内部固件版本")
    private String firewareVersion;

    /** 内部固件版本 */
    @Excel(name = "内部固件版本")
    private String subVersion;

    /** 启动命令ID */
    @Excel(name = "启动命令ID")
    private Long commandId;

    /** 问题名称 */
    @Excel(name = "问题名称")
    private String problemName;

    /** 问题种类 */
    @Excel(name = "问题种类")
    private String typeProblem;

    /** 问题详细说明和指导索引 */
    @Excel(name = "问题详细说明和指导索引")
    private Long problemDescribeId;

    /** 是否循环 */
    @Excel(name = "是否循环")
    private String ifCycle;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFirewareVersion() {
        return firewareVersion;
    }

    public void setFirewareVersion(String firewareVersion) {
        this.firewareVersion = firewareVersion;
    }

    public String getSubVersion() {
        return subVersion;
    }

    public void setSubVersion(String subVersion) {
        this.subVersion = subVersion;
    }

    public Long getCommandId() {
        return commandId;
    }

    public void setCommandId(Long commandId) {
        this.commandId = commandId;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public String getTypeProblem() {
        return typeProblem;
    }

    public void setTypeProblem(String typeProblem) {
        this.typeProblem = typeProblem;
    }

    public Long getProblemDescribeId() {
        return problemDescribeId;
    }

    public void setProblemDescribeId(Long problemDescribeId) {
        this.problemDescribeId = problemDescribeId;
    }

    public String getIfCycle() {
        return ifCycle;
    }

    public void setIfCycle(String ifCycle) {
        this.ifCycle = ifCycle;
    }

    @Override
    public String toString() {
        return "TotalQuestionTable{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", type='" + type + '\'' +
                ", firewareVersion='" + firewareVersion + '\'' +
                ", subVersion='" + subVersion + '\'' +
                ", commandId=" + commandId +
                ", problemName='" + problemName + '\'' +
                ", typeProblem='" + typeProblem + '\'' +
                ", problemDescribeId=" + problemDescribeId +
                ", ifCycle='" + ifCycle + '\'' +
                '}';
    }
}
