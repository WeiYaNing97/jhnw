package com.sgcc.sql.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 返回信息对象 return_record
 * 
 * @author 韦亚宁
 * @date 2021-12-22
 */
public class ReturnRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 当前通信日志 */
    @Excel(name = "当前通信日志")
    private String currentCommLog;

    /** 当前返回日志  */
    @Excel(name = "当前返回日志 ")
    private String currentReturnLog;

    /** 当前标识符 */
    @Excel(name = "当前标识符")
    private String currentIdentifier;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCurrentCommLog(String currentCommLog) 
    {
        this.currentCommLog = currentCommLog;
    }

    public String getCurrentCommLog() 
    {
        return currentCommLog;
    }
    public void setCurrentReturnLog(String currentReturnLog) 
    {
        this.currentReturnLog = currentReturnLog;
    }

    public String getCurrentReturnLog() 
    {
        return currentReturnLog;
    }
    public void setCurrentIdentifier(String currentIdentifier) 
    {
        this.currentIdentifier = currentIdentifier;
    }

    public String getCurrentIdentifier() 
    {
        return currentIdentifier;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("currentCommLog", getCurrentCommLog())
            .append("currentReturnLog", getCurrentReturnLog())
            .append("currentIdentifier", getCurrentIdentifier())
            .append("createTime", getCreateTime())
            .toString();
    }
}
