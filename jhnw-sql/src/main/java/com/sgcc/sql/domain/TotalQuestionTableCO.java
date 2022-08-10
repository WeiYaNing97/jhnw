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
public class TotalQuestionTableCO extends BaseEntity
{

    /** 问题种类 */
    @Excel(name = "问题种类")
    private String typeProblem;

    private List<TotalQuestionTableVO> totalQuestionTableVOList;

    public String getTypeProblem() {
        return typeProblem;
    }

    public void setTypeProblem(String typeProblem) {
        this.typeProblem = typeProblem;
    }

    public List<TotalQuestionTableVO> getTotalQuestionTableVOList() {
        return totalQuestionTableVOList;
    }

    public void setTotalQuestionTableVOList(List<TotalQuestionTableVO> totalQuestionTableVOList) {
        this.totalQuestionTableVOList = totalQuestionTableVOList;
    }

    @Override
    public String toString() {
        return "TotalQuestionTableCO{" +
                "typeProblem='" + typeProblem + '\'' +
                ", totalQuestionTableVOList=" + totalQuestionTableVOList +
                '}';
    }
}
