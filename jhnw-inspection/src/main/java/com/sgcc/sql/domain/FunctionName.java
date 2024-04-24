package com.sgcc.sql.domain;

import com.sgcc.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @program: jhnw
 * @description:
 * @author:
 * @create: 2024-04-23 17:08
 **/
public class FunctionName {
    /** 问题ID */
    private Long id;

    /** 范式名称 */
    private String temProNameProblemName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemProNameProblemName() {
        return temProNameProblemName;
    }

    public void setTemProNameProblemName(String temProNameProblemName) {
        this.temProNameProblemName = temProNameProblemName;
    }
}
