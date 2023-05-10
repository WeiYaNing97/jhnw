package com.sgcc.sql.service;

import java.util.List;
import com.sgcc.sql.domain.SolveQuestion;

/**
 * 前端 解决问题Service接口
 * 
 * @author 王浩伟
 * @date 2022-06-01
 */
public interface ISolveQuestionService 
{
    /**
     * 查询前端 解决问题
     * 
     * @param commond 前端 解决问题主键
     * @return 前端 解决问题
     */
    public SolveQuestion selectSolveQuestionByCommond(String commond);

    /**
     * 查询前端 解决问题列表
     * 
     * @param solveQuestion 前端 解决问题
     * @return 前端 解决问题集合
     */
    public List<SolveQuestion> selectSolveQuestionList(SolveQuestion solveQuestion);

    /**
     * 新增前端 解决问题
     * 
     * @param solveQuestion 前端 解决问题
     * @return 结果
     */
    public int insertSolveQuestion(SolveQuestion solveQuestion);

    /**
     * 修改前端 解决问题
     * 
     * @param solveQuestion 前端 解决问题
     * @return 结果
     */
    public int updateSolveQuestion(SolveQuestion solveQuestion);

    /**
     * 批量删除前端 解决问题
     * 
     * @param commonds 需要删除的前端 解决问题主键集合
     * @return 结果
     */
    public int deleteSolveQuestionByCommonds(String[] commonds);

    /**
     * 删除前端 解决问题信息
     * 
     * @param commond 前端 解决问题主键
     * @return 结果
     */
    public int deleteSolveQuestionByCommond(String commond);
}
