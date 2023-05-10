package com.sgcc.share.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;
import java.util.Date;
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

    /** 系统登录用户名称 */
    @Excel(name = "系统登录用户名称")
    private String userName;

    /** 交换机ip */
    @Excel(name = "交换机ip")
    private String switchIp;

    /** 品牌 */
    @Excel(name = "品牌")
    private String brand;

    /** 型号 */
    @Excel(name = "型号")
    private String type;

    /** 内部固件版本 */
    @Excel(name = "内部固件版本")
    private String firewareVersion;

    /** 子版本号 */
    @Excel(name = "子版本号")
    private String subVersion;

    /** 当前通信日志 */
    @Excel(name = "当前通信日志")
    private String currentCommLog;

    /** 当前返回日志  */
    @Excel(name = "当前返回日志 ")
    private String currentReturnLog;

    /** 当前标识符 */
    @Excel(name = "当前标识符")
    private String currentIdentifier;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

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


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getSwitchIp() {
        return switchIp;
    }

    public void setSwitchIp(String switchIp) {
        this.switchIp = switchIp;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    @Override
    public String toString() {
        return "ReturnRecord{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", switchIp='" + switchIp + '\'' +
                ", brand='" + brand + '\'' +
                ", type='" + type + '\'' +
                ", firewareVersion='" + firewareVersion + '\'' +
                ", subVersion='" + subVersion + '\'' +
                ", currentCommLog='" + currentCommLog + '\'' +
                ", currentReturnLog='" + currentReturnLog + '\'' +
                ", currentIdentifier='" + currentIdentifier + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
