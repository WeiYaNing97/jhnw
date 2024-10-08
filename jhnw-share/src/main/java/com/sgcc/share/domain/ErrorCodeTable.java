package com.sgcc.share.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 错误代码对象 error_code_table
 * 
 * @author ruoyi
 * @date 2024-10-08
 */
public class ErrorCodeTable extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 错误代码库主键 */
    private Long errorCodeId;

    /** 错误代码号 */
    @Excel(name = "错误代码号")
    private String errorCodeNumber;

    /** 错误代码信息 */
    @Excel(name = "错误代码信息")
    private String errorCodeInformation;

    /** 错误解决办法 */
    @Excel(name = "错误解决办法")
    private String errorSolution;

    public void setErrorCodeId(Long errorCodeId) 
    {
        this.errorCodeId = errorCodeId;
    }

    public Long getErrorCodeId() 
    {
        return errorCodeId;
    }
    public void setErrorCodeNumber(String errorCodeNumber) 
    {
        this.errorCodeNumber = errorCodeNumber;
    }

    public String getErrorCodeNumber() 
    {
        return errorCodeNumber;
    }
    public void setErrorCodeInformation(String errorCodeInformation) 
    {
        this.errorCodeInformation = errorCodeInformation;
    }

    public String getErrorCodeInformation() 
    {
        return errorCodeInformation;
    }
    public void setErrorSolution(String errorSolution) 
    {
        this.errorSolution = errorSolution;
    }

    public String getErrorSolution() 
    {
        return errorSolution;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("errorCodeId", getErrorCodeId())
            .append("errorCodeNumber", getErrorCodeNumber())
            .append("errorCodeInformation", getErrorCodeInformation())
            .append("errorSolution", getErrorSolution())
            .append("createTime", getCreateTime())
            .toString();
    }
}
