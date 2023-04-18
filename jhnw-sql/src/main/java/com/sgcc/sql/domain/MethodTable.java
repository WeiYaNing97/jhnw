package com.sgcc.sql.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 方法对象 method_table
 * 
 * @author ruoyi
 * @date 2023-04-13
 */
public class MethodTable extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private String id;

    /** 方法名 */
    @Excel(name = "方法名")
    private String methodName;

    /** 使用参数名称 */
    @Excel(name = "使用参数名称")
    private String afferent;

    /** 返回参数名称 */
    @Excel(name = "返回参数名称")
    private String regain;

    /** 下一命令ID */
    @Excel(name = "下一命令ID")
    private String endIndex;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setMethodName(String methodName) 
    {
        this.methodName = methodName;
    }

    public String getMethodName() 
    {
        return methodName;
    }
    public void setAfferent(String afferent) 
    {
        this.afferent = afferent;
    }

    public String getAfferent() 
    {
        return afferent;
    }
    public void setRegain(String regain) 
    {
        this.regain = regain;
    }

    public String getRegain() 
    {
        return regain;
    }
    public void setEndIndex(String endIndex) 
    {
        this.endIndex = endIndex;
    }

    public String getEndIndex() 
    {
        return endIndex;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("methodName", getMethodName())
            .append("afferent", getAfferent())
            .append("regain", getRegain())
            .append("endIndex", getEndIndex())
            .toString();
    }
}
