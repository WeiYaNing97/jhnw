package com.sgcc.sql.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.sql.mapper.JhTest1Mapper;
import com.sgcc.sql.domain.JhTest1;
import com.sgcc.sql.service.IJhTest1Service;

/**
 * 前端 定义Service业务层处理
 * 
 * @author 王浩伟
 * @date 2022-06-01
 */
@Service
public class JhTest1ServiceImpl implements IJhTest1Service 
{
    @Autowired
    private JhTest1Mapper jhTest1Mapper;

    /**
     * 查询前端 定义
     * 
     * @param id 前端 定义主键
     * @return 前端 定义
     */
    @Override
    public JhTest1 selectJhTest1ById(Long id)
    {
        return jhTest1Mapper.selectJhTest1ById(id);
    }

    /**
     * 查询前端 定义列表
     * 
     * @param jhTest1 前端 定义
     * @return 前端 定义
     */
    @Override
    public List<JhTest1> selectJhTest1List(JhTest1 jhTest1)
    {
        return jhTest1Mapper.selectJhTest1List(jhTest1);
    }

    /**
     * 新增前端 定义
     * 
     * @param jhTest1 前端 定义
     * @return 结果
     */
    @Override
    public int insertJhTest1(JhTest1 jhTest1)
    {
        return jhTest1Mapper.insertJhTest1(jhTest1);
    }

    /**
     * 修改前端 定义
     * 
     * @param jhTest1 前端 定义
     * @return 结果
     */
    @Override
    public int updateJhTest1(JhTest1 jhTest1)
    {
        return jhTest1Mapper.updateJhTest1(jhTest1);
    }

    /**
     * 批量删除前端 定义
     * 
     * @param ids 需要删除的前端 定义主键
     * @return 结果
     */
    @Override
    public int deleteJhTest1ByIds(Long[] ids)
    {
        return jhTest1Mapper.deleteJhTest1ByIds(ids);
    }

    /**
     * 删除前端 定义信息
     * 
     * @param id 前端 定义主键
     * @return 结果
     */
    @Override
    public int deleteJhTest1ById(Long id)
    {
        return jhTest1Mapper.deleteJhTest1ById(id);
    }
}
