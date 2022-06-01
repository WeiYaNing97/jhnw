package com.sgcc.sql.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.sql.mapper.SolveQuestionMapper;
import com.sgcc.sql.domain.SolveQuestion;
import com.sgcc.sql.service.ISolveQuestionService;

/**
 * 前端 解决问题Service业务层处理
 * 
 * @author 王浩伟
 * @date 2022-06-01
 */
@Service
public class SolveQuestionServiceImpl implements ISolveQuestionService 
{
    @Autowired
    private SolveQuestionMapper solveQuestionMapper;

    /**
     * 查询前端 解决问题
     * 
     * @param commond 前端 解决问题主键
     * @return 前端 解决问题
     */
    @Override
    public SolveQuestion selectSolveQuestionByCommond(String commond)
    {
        return solveQuestionMapper.selectSolveQuestionByCommond(commond);
    }

    /**
     * 查询前端 解决问题列表
     * 
     * @param solveQuestion 前端 解决问题
     * @return 前端 解决问题
     */
    @Override
    public List<SolveQuestion> selectSolveQuestionList(SolveQuestion solveQuestion)
    {
        return solveQuestionMapper.selectSolveQuestionList(solveQuestion);
    }

    /**
     * 新增前端 解决问题
     * 
     * @param solveQuestion 前端 解决问题
     * @return 结果
     */
    @Override
    public int insertSolveQuestion(SolveQuestion solveQuestion)
    {
        return solveQuestionMapper.insertSolveQuestion(solveQuestion);
    }

    /**
     * 修改前端 解决问题
     * 
     * @param solveQuestion 前端 解决问题
     * @return 结果
     */
    @Override
    public int updateSolveQuestion(SolveQuestion solveQuestion)
    {
        return solveQuestionMapper.updateSolveQuestion(solveQuestion);
    }

    /**
     * 批量删除前端 解决问题
     * 
     * @param commonds 需要删除的前端 解决问题主键
     * @return 结果
     */
    @Override
    public int deleteSolveQuestionByCommonds(String[] commonds)
    {
        return solveQuestionMapper.deleteSolveQuestionByCommonds(commonds);
    }

    /**
     * 删除前端 解决问题信息
     * 
     * @param commond 前端 解决问题主键
     * @return 结果
     */
    @Override
    public int deleteSolveQuestionByCommond(String commond)
    {
        return solveQuestionMapper.deleteSolveQuestionByCommond(commond);
    }
}
