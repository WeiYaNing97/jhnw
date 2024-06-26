package com.sgcc.sql.domain;

import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 问题及命令对象 total_question_table
 *
 */
@ApiModel(value = "TotalQuestionTable", description = "交换机问题实体")
public class TotalQuestionTable extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键索引 */
    @Excel(name = "主键")
    @ApiModelProperty("主键")
    private String id;

    /** 品牌 */
    @Excel(name = "品牌")
    @ApiModelProperty("品牌")
    private String brand;

    /** 型号 */
    @Excel(name = "型号")
    @ApiModelProperty("型号")
    private String type;

    /** 内部固件版本 */
    @Excel(name = "内部固件版本")
    @ApiModelProperty("内部固件版本")
    private String firewareVersion;

    /** 子版本号 */
    @Excel(name = "子版本号")
    @ApiModelProperty("子版本号")
    private String subVersion;

    /** 未完成 */
    @Excel(name = "未完成标志")
    @ApiModelProperty("未完成标志")
    private String notFinished;

    /** 逻辑ID   对应  数据库中的  commandId  */
    /*因为该字段包含 命令ID 与 分析ID 故实体类中更改名称 */
    /* (是命令也可能是分析) */
    @Excel(name = "扫描索引")
    @ApiModelProperty("逻辑ID")
    private String logicalID;

    /** 范式种类 */
    @Excel(name = "范式分类")
    @ApiModelProperty("范式分类")
    private String typeProblem;


    /** 范式名称 */
    @Excel(name = "范式名称")
    @ApiModelProperty("范式名称")
    private String temProName;

    /** 问题名称 */
    @Excel(name = "自定义名称")
    @ApiModelProperty("自定义名称")
    private String problemName;

    /** 问题详细说明和指导索引 */
    @Excel(name = "问题详细说明和指导索引")
    @ApiModelProperty("问题详细说明和指导索引")
    private String problemDescribeId;

    /** 解决问题命令ID */
    @Excel(name = "解决问题命令索引")
    @ApiModelProperty("解决问题命令索引")
    private String problemSolvingId;

    /** 备注 */
    @Excel(name = "问题备注")
    @ApiModelProperty("问题备注")
    private String remarks;

    /** 是否必扫 */
    @Excel(name = "是否必扫")
    @ApiModelProperty("是否必扫")
    private Long requiredItems;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getNotFinished() {
        return notFinished;
    }

    public void setNotFinished(String notFinished) {
        this.notFinished = notFinished;
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

    public String getProblemSolvingId() {
        return problemSolvingId;
    }

    public void setProblemSolvingId(String problemSolvingId) {
        this.problemSolvingId = problemSolvingId;
    }

    public String getTemProName() {
        return temProName;
    }

    public void setTemProName(String temProName) {
        this.temProName = temProName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getRequiredItems() {
        return requiredItems;
    }

    public void setRequiredItems(Long requiredItems) {
        this.requiredItems = requiredItems;
    }

    public String getLogicalID() {
        return logicalID;
    }

    public void setLogicalID(String logicalID) {
        this.logicalID = logicalID;
    }

    public String getProblemDescribeId() {
        return problemDescribeId;
    }

    public void setProblemDescribeId(String problemDescribeId) {
        this.problemDescribeId = problemDescribeId;
    }

    @Override
    public String toString() {
        return "TotalQuestionTable{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", type='" + type + '\'' +
                ", firewareVersion='" + firewareVersion + '\'' +
                ", subVersion='" + subVersion + '\'' +
                ", notFinished='" + notFinished + '\'' +
                ", logicalID='" + logicalID + '\'' +
                ", typeProblem='" + typeProblem + '\'' +
                ", temProName='" + temProName + '\'' +
                ", problemName='" + problemName + '\'' +
                ", problemDescribeId=" + problemDescribeId +
                ", problemSolvingId='" + problemSolvingId + '\'' +
                ", remarks='" + remarks + '\'' +
                ", requiredItems=" + requiredItems +
                '}';
    }

    public String toJson() {
        return "{" +
                "id:" + id +
                ",brand:'" + brand + '\'' +
                ",type:'" + type + '\'' +
                ",firewareVersion:'" + firewareVersion + '\'' +
                ",subVersion:'" + subVersion + '\'' +
                ",notFinished:'" + notFinished + '\'' +
                ",logicalID:'" + logicalID + '\'' +
                ",typeProblem:'" + typeProblem + '\'' +
                ",temProName:'" + temProName + '\'' +
                ",problemName:'" + problemName + '\'' +
                ",problemDescribeId:" + problemDescribeId +
                ",problemSolvingId:'" + problemSolvingId + '\'' +
                ",remarks:'" + remarks + '\'' +
                ",requiredItems:" + requiredItems +
                '}';
    }
}
