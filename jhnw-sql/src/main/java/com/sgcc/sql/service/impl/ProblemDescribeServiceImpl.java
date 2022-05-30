package com.sgcc.sql.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.sql.mapper.ProblemDescribeMapper;
import com.sgcc.sql.domain.ProblemDescribe;
import com.sgcc.sql.service.IProblemDescribeService;

/**
 * 问题描述Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-05-26
 */
@Service
public class ProblemDescribeServiceImpl implements IProblemDescribeService 
{
    @Autowired
    private ProblemDescribeMapper problemDescribeMapper;

    /**
     * 查询问题描述
     * 
     * @param id 问题描述主键
     * @return 问题描述
     */
    @Override
    public ProblemDescribe selectProblemDescribeById(Long id)
    {
        return problemDescribeMapper.selectProblemDescribeById(id);
    }

    /**
     * 查询问题描述列表
     * 
     * @param problemDescribe 问题描述
     * @return 问题描述
     */
    @Override
    public List<ProblemDescribe> selectProblemDescribeList(ProblemDescribe problemDescribe)
    {
        return problemDescribeMapper.selectProblemDescribeList(problemDescribe);
    }

    /**
     * 新增问题描述
     * 
     * @param problemDescribe 问题描述
     * @return 结果
     */
    @Override
    public int insertProblemDescribe(ProblemDescribe problemDescribe)
    {
        return problemDescribeMapper.insertProblemDescribe(problemDescribe);
    }

    /**
     * 修改问题描述
     * 
     * @param problemDescribe 问题描述
     * @return 结果
     */
    @Override
    public int updateProblemDescribe(ProblemDescribe problemDescribe)
    {
        return problemDescribeMapper.updateProblemDescribe(problemDescribe);
    }

    /**
     * 批量删除问题描述
     * 
     * @param ids 需要删除的问题描述主键
     * @return 结果
     */
    @Override
    public int deleteProblemDescribeByIds(Long[] ids)
    {
        return problemDescribeMapper.deleteProblemDescribeByIds(ids);
    }

    /**
     * 删除问题描述信息
     * 
     * @param id 问题描述主键
     * @return 结果
     */
    @Override
    public int deleteProblemDescribeById(Long id)
    {
        return problemDescribeMapper.deleteProblemDescribeById(id);
    }
}
