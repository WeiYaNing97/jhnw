package com.sgcc.sql.service.impl;

import java.util.HashSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.sql.mapper.ProblemScanLogicMapper;
import com.sgcc.sql.domain.ProblemScanLogic;
import com.sgcc.sql.service.IProblemScanLogicService;

/**
 * 问题扫描逻辑Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-02-14
 */
@Service
public class ProblemScanLogicServiceImpl implements IProblemScanLogicService 
{
    @Autowired
    private ProblemScanLogicMapper problemScanLogicMapper;

    /**
     * 查询问题扫描逻辑
     * 
     * @param id 问题扫描逻辑主键
     * @return 问题扫描逻辑
     */
    @Override
    public ProblemScanLogic selectProblemScanLogicById(String id)
    {
        return problemScanLogicMapper.selectProblemScanLogicById(id);
    }

    /**
     * 查询问题扫描逻辑列表
     *
     * @param problemScanLogic 问题扫描逻辑
     * @return 问题扫描逻辑
     */
    @Override
    public List<ProblemScanLogic> selectProblemScanLogicList(ProblemScanLogic problemScanLogic)
    {
        return problemScanLogicMapper.selectProblemScanLogicList(problemScanLogic);
    }

    /**
     * 新增问题扫描逻辑
     * 
     * @param problemScanLogic 问题扫描逻辑
     * @return 结果
     */
    @Override
    public int insertProblemScanLogic(ProblemScanLogic problemScanLogic)
    {
        return problemScanLogicMapper.insertProblemScanLogic(problemScanLogic);
    }

    /**
     * 修改问题扫描逻辑
     * 
     * @param problemScanLogic 问题扫描逻辑
     * @return 结果
     */
    @Override
    public int updateProblemScanLogic(ProblemScanLogic problemScanLogic)
    {
        return problemScanLogicMapper.updateProblemScanLogic(problemScanLogic);
    }

    /**
     * 批量删除问题扫描逻辑
     * 
     * @param ids 需要删除的问题扫描逻辑主键
     * @return 结果
     */
    @Override
    public int deleteProblemScanLogicByIds(String[] ids)
    {
        return problemScanLogicMapper.deleteProblemScanLogicByIds(ids);
    }

    /**
     * 删除问题扫描逻辑信息
     * 
     * @param id 问题扫描逻辑主键
     * @return 结果
     */
    @Override
    public int deleteProblemScanLogicById(String id)
    {
        return problemScanLogicMapper.deleteProblemScanLogicById(id);
    }
}
