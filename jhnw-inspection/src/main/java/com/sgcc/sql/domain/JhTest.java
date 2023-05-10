package com.sgcc.sql.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 前端 扫描页对象 jh_test
 * 
 * @author 王浩伟
 * @date 2022-06-01
 */
public class JhTest extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** ip */
    @Excel(name = "ip")
    private String ip;

    /** 名字 */
    @Excel(name = "名字")
    private String name;

    /** 类型 */
    @Excel(name = "类型")
    private String type;

    /** 端口号 */
    @Excel(name = "端口号")
    private String port;

    /** 密码 */
    @Excel(name = "密码")
    private String password;

    /** 结束符 */
    @Excel(name = "结束符")
    private String end;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setIp(String ip) 
    {
        this.ip = ip;
    }

    public String getIp() 
    {
        return ip;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }
    public void setPort(String port) 
    {
        this.port = port;
    }

    public String getPort() 
    {
        return port;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }
    public void setEnd(String end) 
    {
        this.end = end;
    }

    public String getEnd() 
    {
        return end;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("ip", getIp())
            .append("name", getName())
            .append("type", getType())
            .append("port", getPort())
            .append("password", getPassword())
            .append("end", getEnd())
            .toString();
    }
}
