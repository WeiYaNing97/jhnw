package com.sgcc.sql.domain;

import com.sgcc.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @program: jhnw
 * @description: 属性功能表
 * @author:
 * @create: 2024-04-23 17:05
 **/
public class FunctionVO {
    /** 范式种类 */
    private String typeProblem;
    private List<FunctionName> functionNames;

    public String getTypeProblem() {
        return typeProblem;
    }

    public void setTypeProblem(String typeProblem) {
        this.typeProblem = typeProblem;
    }

    public List<FunctionName> getFunctionNames() {
        return functionNames;
    }

    public void setFunctionNames(List<FunctionName> functionNames) {
        this.functionNames = functionNames;
    }
}
