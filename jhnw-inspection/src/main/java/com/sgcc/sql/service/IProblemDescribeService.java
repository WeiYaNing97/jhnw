package com.sgcc.sql.service;

import java.util.List;
import com.sgcc.sql.domain.ProblemDescribe;

/**
 * 问题描述Service接口
 * 
 * @author ruoyi
 * @date 2022-05-26
 */
public interface IProblemDescribeService 
{
    /**
     * 查询问题描述
     * 
     * @param id 问题描述主键
     * @return 问题描述
     */
    public ProblemDescribe selectProblemDescribeById(String id);

    /**
     * 查询问题描述列表
     * 
     * @param problemDescribe 问题描述
     * @return 问题描述集合
     */
    public List<ProblemDescribe> selectProblemDescribeList(ProblemDescribe problemDescribe);

    /**
     * 新增问题描述
     * 
     * @param problemDescribe 问题描述
     * @return 结果
     */
    public int insertProblemDescribe(ProblemDescribe problemDescribe);

    /**
     * 修改问题描述
     * 
     * @param problemDescribe 问题描述
     * @return 结果
     */
    public int updateProblemDescribe(ProblemDescribe problemDescribe);

    /**
     * 批量删除问题描述
     * 
     * @param ids 需要删除的问题描述主键集合
     * @return 结果
     */
    public int deleteProblemDescribeByIds(Long[] ids);

    /**
     * 删除问题描述信息
     * 
     * @param id 问题描述主键
     * @return 结果
     */
    public int deleteProblemDescribeById(Long id);
}
