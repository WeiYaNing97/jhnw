package com.sgcc.sql.service;

import java.util.List;
import com.sgcc.sql.domain.JhTest;

/**
 * 前端 扫描页Service接口
 * 
 * @author 王浩伟
 * @date 2022-06-01
 */
public interface IJhTestService 
{
    /**
     * 查询前端 扫描页
     * 
     * @param id 前端 扫描页主键
     * @return 前端 扫描页
     */
    public JhTest selectJhTestById(Long id);

    /**
     * 查询前端 扫描页列表
     * 
     * @param jhTest 前端 扫描页
     * @return 前端 扫描页集合
     */
    public List<JhTest> selectJhTestList(JhTest jhTest);

    /**
     * 新增前端 扫描页
     * 
     * @param jhTest 前端 扫描页
     * @return 结果
     */
    public int insertJhTest(JhTest jhTest);

    /**
     * 修改前端 扫描页
     * 
     * @param jhTest 前端 扫描页
     * @return 结果
     */
    public int updateJhTest(JhTest jhTest);

    /**
     * 批量删除前端 扫描页
     * 
     * @param ids 需要删除的前端 扫描页主键集合
     * @return 结果
     */
    public int deleteJhTestByIds(Long[] ids);

    /**
     * 删除前端 扫描页信息
     * 
     * @param id 前端 扫描页主键
     * @return 结果
     */
    public int deleteJhTestById(Long id);
}
