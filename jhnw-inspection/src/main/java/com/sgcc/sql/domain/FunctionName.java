package com.sgcc.sql.domain;

/**
 * @program: jhnw
 * @description:
 * @author:
 * @create: 2024-04-23 17:08
 **/
public class FunctionName {
    /** 问题ID */
    private String id;

    /** 范式名称 */
    private String label;

    private Integer level;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
