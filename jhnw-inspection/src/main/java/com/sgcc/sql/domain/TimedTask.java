package com.sgcc.sql.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 定时任务对象 timed_task
 * 
 * @author ruoyi
 * @date 2024-04-07
 */
public class TimedTask extends BaseEntity
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "定时任务开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date timedTaskStartTime;

    /** 定时任务间隔时间 */
    @Excel(name = "定时任务间隔时间")
    private String timedTaskIntervalTime;

    /** 功能数组 */
    @Excel(name = "功能数组")
    private String functionArray;

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

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setTimedTaskName(String timedTaskName) 
    {
        this.timedTaskName = timedTaskName;
    }

    public String getTimedTaskName() 
    {
        return timedTaskName;
    }
    public void setTimedTaskParameters(String timedTaskParameters) 
    {
        this.timedTaskParameters = timedTaskParameters;
    }

    public String getTimedTaskParameters() 
    {
        return timedTaskParameters;
    }
    public void setTimedTaskStartTime(Date timedTaskStartTime)
    {
        this.timedTaskStartTime = timedTaskStartTime;
    }

    public Date getTimedTaskStartTime()
    {
        return timedTaskStartTime;
    }

    public void setTimedTaskStatus(Integer timedTaskStatus) 
    {
        this.timedTaskStatus = timedTaskStatus;
    }

    public Integer getTimedTaskStatus() 
    {
        return timedTaskStatus;
    }
    public void setCreatorName(String creatorName) 
    {
        this.creatorName = creatorName;
    }

    public String getCreatorName() 
    {
        return creatorName;
    }
    public void setCreatedOn(Date createdOn) 
    {
        this.createdOn = createdOn;
    }

    public Date getCreatedOn() 
    {
        return createdOn;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTimedTaskIntervalTime() {
        return timedTaskIntervalTime;
    }

    public void setTimedTaskIntervalTime(String timedTaskIntervalTime) {
        this.timedTaskIntervalTime = timedTaskIntervalTime;
    }

    public String getFunctionArray() {
        return functionArray;
    }

    public void setFunctionArray(String functionArray) {
        this.functionArray = functionArray;
    }

    @Override
    public String toString() {
        return "TimedTask{" +
                "id=" + id +
                ", timedTaskName='" + timedTaskName + '\'' +
                ", timedTaskParameters='" + timedTaskParameters + '\'' +
                ", timedTaskStartTime=" + timedTaskStartTime +
                ", timedTaskIntervalTime='" + timedTaskIntervalTime + '\'' +
                ", functionArray='" + functionArray + '\'' +
                ", timedTaskStatus=" + timedTaskStatus +
                ", creatorName='" + creatorName + '\'' +
                ", createdOn=" + createdOn +
                '}';
    }
}
