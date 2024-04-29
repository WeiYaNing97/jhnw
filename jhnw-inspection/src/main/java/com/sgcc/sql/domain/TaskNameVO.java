package com.sgcc.sql.domain;

import java.util.List;

/**
 * @program: jhnw
 * @description: 呈现给前端的勾选的定时任务名称
 * @author:
 * @create: 2024-04-25 10:48
 **/
public class TaskNameVO {
    private Long id;
    private String label;
    private List<TaskNameVO> children;

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

    public List<TaskNameVO> getChildren() {
        return children;
    }

    public void setChildren(List<TaskNameVO> children) {
        this.children = children;
    }


}