package com.sgcc.sql.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.sql.mapper.JhTestMapper;
import com.sgcc.sql.domain.JhTest;
import com.sgcc.sql.service.IJhTestService;

/**
 * 前端 扫描页Service业务层处理
 * 
 * @author 王浩伟
 * @date 2022-06-01
 */
@Service
public class JhTestServiceImpl implements IJhTestService 
{
    @Autowired
    private JhTestMapper jhTestMapper;

    /**
     * 查询前端 扫描页
     * 
     * @param id 前端 扫描页主键
     * @return 前端 扫描页
     */
    @Override
    public JhTest selectJhTestById(Long id)
    {
        return jhTestMapper.selectJhTestById(id);
    }

    /**
     * 查询前端 扫描页列表
     * 
     * @param jhTest 前端 扫描页
     * @return 前端 扫描页
     */
    @Override
    public List<JhTest> selectJhTestList(JhTest jhTest)
    {
        return jhTestMapper.selectJhTestList(jhTest);
    }

    /**
     * 新增前端 扫描页
     * 
     * @param jhTest 前端 扫描页
     * @return 结果
     */
    @Override
    public int insertJhTest(JhTest jhTest)
    {
        return jhTestMapper.insertJhTest(jhTest);
    }

    /**
     * 修改前端 扫描页
     * 
     * @param jhTest 前端 扫描页
     * @return 结果
     */
    @Override
    public int updateJhTest(JhTest jhTest)
    {
        return jhTestMapper.updateJhTest(jhTest);
    }

    /**
     * 批量删除前端 扫描页
     * 
     * @param ids 需要删除的前端 扫描页主键
     * @return 结果
     */
    @Override
    public int deleteJhTestByIds(Long[] ids)
    {
        return jhTestMapper.deleteJhTestByIds(ids);
    }

    /**
     * 删除前端 扫描页信息
     * 
     * @param id 前端 扫描页主键
     * @return 结果
     */
    @Override
    public int deleteJhTestById(Long id)
    {
        return jhTestMapper.deleteJhTestById(id);
    }
}
