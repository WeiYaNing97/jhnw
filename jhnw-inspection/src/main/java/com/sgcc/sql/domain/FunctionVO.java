package com.sgcc.sql.domain;

import java.util.List;

/**
 * @program: jhnw
 * @description: 属性功能表
 * @author:
 * @create: 2024-04-23 17:05
 **/
public class FunctionVO {
    /** 范式种类 */
    private Long id;
    private String label;
    private Integer level;
    private List<FunctionName> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<FunctionName> getChildren() {
        return children;
    }

    public void setChildren(List<FunctionName> children) {
        this.children = children;
    }
}
