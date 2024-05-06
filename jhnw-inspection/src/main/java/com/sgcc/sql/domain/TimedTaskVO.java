package com.sgcc.sql.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sgcc.advanced.utils.CustomDateDeserializer;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;
import java.util.Date;
import java.util.List;

/**
 * @program: jhnw
 * @description: 定时任务VO
 * @author:
 * @create: 2024-04-09 11:25
 **/
public class TimedTaskVO extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 定时任务编号 */
    private Long id;

    /** 定时任务名称 */
    @Excel(name = "定时任务名称")
    private String timedTaskName;

    /** 定时任务参数 */
    @Excel(name = "定时任务参数")
    private String timedTaskParameters;

    /** 定时任务开始时间 */
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "定时任务开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date timedTaskStartTime;

    /** 定时任务间隔时间 */
    @Excel(name = "定时任务间隔时间")
    private String timedTaskIntervalTime;


    /** 所有功能集合 */
    @Excel(name = "所有功能集合")
    private List<FunctionVO> functionalTree;

    /** 选择功能集合 */
    @Excel(name = "选择功能集合")
    private List<String> selectFunctions;

    /** 功能集合 */
    @Excel(name = "功能集合")
    private List<String> functionName;

    /** 定时任务开启状态 */
    @Excel(name = "定时任务开启状态")
    private Integer timedTaskStatus;

    /** 定时任务创建人姓名 */
    @Excel(name = "定时任务创建人姓名")
    private String creatorName;

    /** 定时任务创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "定时任务创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdOn;


    private boolean selectFunctionWindow;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTimedTaskName() {
        return timedTaskName;
    }

    public void setTimedTaskName(String timedTaskName) {
        this.timedTaskName = timedTaskName;
    }

    public String getTimedTaskParameters() {
        return timedTaskParameters;
    }

    public void setTimedTaskParameters(String timedTaskParameters) {
        this.timedTaskParameters = timedTaskParameters;
    }

    public Date getTimedTaskStartTime() {
        return timedTaskStartTime;
    }

    public void setTimedTaskStartTime(Date timedTaskStartTime) {
        this.timedTaskStartTime = timedTaskStartTime;
    }

    public String getTimedTaskIntervalTime() {
        return timedTaskIntervalTime;
    }

    public void setTimedTaskIntervalTime(String timedTaskIntervalTime) {
        this.timedTaskIntervalTime = timedTaskIntervalTime;
    }

    public Integer getTimedTaskStatus() {
        return timedTaskStatus;
    }

    public void setTimedTaskStatus(Integer timedTaskStatus) {
        this.timedTaskStatus = timedTaskStatus;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public List<String> getSelectFunctions() {
        return selectFunctions;
    }

    public void setSelectFunctions(List<String> selectFunctions) {
        this.selectFunctions = selectFunctions;
    }

    public boolean isSelectFunctionWindow() {
        return selectFunctionWindow;
    }

    public void setSelectFunctionWindow(boolean selectFunctionWindow) {
        this.selectFunctionWindow = selectFunctionWindow;
    }

    public List<FunctionVO> getFunctionalTree() {
        return functionalTree;
    }

    public void setFunctionalTree(List<FunctionVO> functionalTree) {
        this.functionalTree = functionalTree;
    }

    public List<String> getFunctionName() {
        return functionName;
    }

    public void setFunctionName(List<String> functionName) {
        this.functionName = functionName;
    }
}