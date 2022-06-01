package com.sgcc.sql.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.sql.mapper.LookTestMapper;
import com.sgcc.sql.domain.LookTest;
import com.sgcc.sql.service.ILookTestService;

/**
 * 前端 回显定义问题页面Service业务层处理
 * 
 * @author 王浩伟
 * @date 2022-06-01
 */
@Service
public class LookTestServiceImpl implements ILookTestService 
{
    @Autowired
    private LookTestMapper lookTestMapper;

    /**
     * 查询前端 回显定义问题页面
     * 
     * @param id 前端 回显定义问题页面主键
     * @return 前端 回显定义问题页面
     */
    @Override
    public LookTest selectLookTestById(Long id)
    {
        return lookTestMapper.selectLookTestById(id);
    }

    /**
     * 查询前端 回显定义问题页面列表
     * 
     * @param lookTest 前端 回显定义问题页面
     * @return 前端 回显定义问题页面
     */
    @Override
    public List<LookTest> selectLookTestList(LookTest lookTest)
    {
        return lookTestMapper.selectLookTestList(lookTest);
    }

    /**
     * 新增前端 回显定义问题页面
     * 
     * @param lookTest 前端 回显定义问题页面
     * @return 结果
     */
    @Override
    public int insertLookTest(LookTest lookTest)
    {
        return lookTestMapper.insertLookTest(lookTest);
    }

    /**
     * 修改前端 回显定义问题页面
     * 
     * @param lookTest 前端 回显定义问题页面
     * @return 结果
     */
    @Override
    public int updateLookTest(LookTest lookTest)
    {
        return lookTestMapper.updateLookTest(lookTest);
    }

    /**
     * 批量删除前端 回显定义问题页面
     * 
     * @param ids 需要删除的前端 回显定义问题页面主键
     * @return 结果
     */
    @Override
    public int deleteLookTestByIds(Long[] ids)
    {
        return lookTestMapper.deleteLookTestByIds(ids);
    }

    /**
     * 删除前端 回显定义问题页面信息
     * 
     * @param id 前端 回显定义问题页面主键
     * @return 结果
     */
    @Override
    public int deleteLookTestById(Long id)
    {
        return lookTestMapper.deleteLookTestById(id);
    }
}
