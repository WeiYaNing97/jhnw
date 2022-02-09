package com.sgcc.sql.domain;

import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * 取值信息存储对象 value_information
 * 
 * @author ruoyi
 * @date 2021-12-28
 */
public class ValueInformation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 是否显示 */
    @Excel(name = "是否显示")
    private String exhibit;

    /** 动态信息名称 */
    @Excel(name = "动态信息名称")
    private String dynamicVname;

    /** 动态信息 */
    @Excel(name = "动态信息")
    private String dynamicInformation;

    /** 动态信息 */
    @Excel(name = "动态信息(显示)")
    private String displayInformation;

    /** 下一信息ID */
    @Excel(name = "下一信息ID")
    private Long outId;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExhibit() {
        return exhibit;
    }

    public void setExhibit(String exhibit) {
        this.exhibit = exhibit;
    }


    public String getDynamicInformation() {
        return dynamicInformation;
    }

    public void setDynamicInformation(String dynamicInformation) {
        this.dynamicInformation = dynamicInformation;
    }

    public String getDisplayInformation() {
        return displayInformation;
    }

    public void setDisplayInformation(String displayInformation) {
        this.displayInformation = displayInformation;
    }

    public Long getOutId() {
        return outId;
    }

    public void setOutId(Long outId) {
        this.outId = outId;
    }

    public String getDynamicVname() {
        return dynamicVname;
    }

    public void setDynamicVname(String dynamicVname) {
        this.dynamicVname = dynamicVname;
    }

    @Override
    public String toString() {
        return "ValueInformation{" +
                "id=" + id +
                ", exhibit='" + exhibit + '\'' +
                ", dynamicVname='" + dynamicVname + '\'' +
                ", dynamicInformation='" + dynamicInformation + '\'' +
                ", displayInformation='" + displayInformation + '\'' +
                ", outId=" + outId +
                '}';
    }
}
