package com.sgcc.sql.domain;

import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 问题及命令对象 total_question_table
 * 
 * @author 韦亚宁
 * @date 2021-12-14
 */
public class TotalQuestionTableVO extends BaseEntity
{

    /** 问题种类 */
    @Excel(name = "问题种类")
    private String typeProblem;
    /** 问题名称 */
    @Excel(name = "问题名称")
    private String temProName;

    private List<TotalQuestionTable> totalQuestionTableList;

    public String getTemProName() {
        return temProName;
    }

    public void setTemProName(String temProName) {
        this.temProName = temProName;
    }

    public String getTypeProblem() {
        return typeProblem;
    }

    public void setTypeProblem(String typeProblem) {
        this.typeProblem = typeProblem;
    }

    public List<TotalQuestionTable> getTotalQuestionTableList() {
        return totalQuestionTableList;
    }

    public void setTotalQuestionTableList(List<TotalQuestionTable> totalQuestionTableList) {
        this.totalQuestionTableList = totalQuestionTableList;
    }

    @Override
    public String toString() {
        return "TotalQuestionTableVO{" +
                "typeProblem='" + typeProblem + '\'' +
                ", temProName='" + temProName + '\'' +
                ", totalQuestionTableList=" + totalQuestionTableList +
                '}';
    }
}
