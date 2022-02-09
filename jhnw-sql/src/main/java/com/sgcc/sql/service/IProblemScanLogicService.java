package com.sgcc.sql.service;

import com.sgcc.sql.domain.ProblemScanLogic;

import java.util.List;

/**
 * 问题扫描逻辑Service接口
 * 
 * @author ruoyi
 * @date 2021-12-20
 */
public interface IProblemScanLogicService 
{
    /**
     * 查询问题扫描逻辑
     * 
     * @param id 问题扫描逻辑主键
     * @return 问题扫描逻辑
     */
    public ProblemScanLogic selectProblemScanLogicById(Long id);

    /**
     * 查询问题扫描逻辑列表
     * 
     * @param problemScanLogic 问题扫描逻辑
     * @return 问题扫描逻辑集合
     */
    public List<ProblemScanLogic> selectProblemScanLogicList(ProblemScanLogic problemScanLogic);

    /**
     * 新增问题扫描逻辑
     * 
     * @param problemScanLogic 问题扫描逻辑
     * @return 结果
     */
    public int insertProblemScanLogic(ProblemScanLogic problemScanLogic);

    /**
     * 修改问题扫描逻辑
     * 
     * @param problemScanLogic 问题扫描逻辑
     * @return 结果
     */
    public int updateProblemScanLogic(ProblemScanLogic problemScanLogic);

    /**
     * 批量删除问题扫描逻辑
     * 
     * @param ids 需要删除的问题扫描逻辑主键集合
     * @return 结果
     */
    public int deleteProblemScanLogicByIds(Long[] ids);

    /**
     * 删除问题扫描逻辑信息
     * 
     * @param id 问题扫描逻辑主键
     * @return 结果
     */
    public int deleteProblemScanLogicById(Long id);
}
