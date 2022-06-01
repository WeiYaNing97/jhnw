package com.sgcc.sql.service;

import java.util.List;
import com.sgcc.sql.domain.JhTest1;

/**
 * 前端 定义Service接口
 * 
 * @author 王浩伟
 * @date 2022-06-01
 */
public interface IJhTest1Service 
{
    /**
     * 查询前端 定义
     * 
     * @param id 前端 定义主键
     * @return 前端 定义
     */
    public JhTest1 selectJhTest1ById(Long id);

    /**
     * 查询前端 定义列表
     * 
     * @param jhTest1 前端 定义
     * @return 前端 定义集合
     */
    public List<JhTest1> selectJhTest1List(JhTest1 jhTest1);

    /**
     * 新增前端 定义
     * 
     * @param jhTest1 前端 定义
     * @return 结果
     */
    public int insertJhTest1(JhTest1 jhTest1);

    /**
     * 修改前端 定义
     * 
     * @param jhTest1 前端 定义
     * @return 结果
     */
    public int updateJhTest1(JhTest1 jhTest1);

    /**
     * 批量删除前端 定义
     * 
     * @param ids 需要删除的前端 定义主键集合
     * @return 结果
     */
    public int deleteJhTest1ByIds(Long[] ids);

    /**
     * 删除前端 定义信息
     * 
     * @param id 前端 定义主键
     * @return 结果
     */
    public int deleteJhTest1ById(Long id);
}
