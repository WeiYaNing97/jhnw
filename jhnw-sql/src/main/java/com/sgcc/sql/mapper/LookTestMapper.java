package com.sgcc.sql.mapper;

import java.util.List;
import com.sgcc.sql.domain.LookTest;

/**
 * 前端 回显定义问题页面Mapper接口
 * 
 * @author 王浩伟
 * @date 2022-06-01
 */
public interface LookTestMapper 
{
    /**
     * 查询前端 回显定义问题页面
     * 
     * @param id 前端 回显定义问题页面主键
     * @return 前端 回显定义问题页面
     */
    public LookTest selectLookTestById(Long id);

    /**
     * 查询前端 回显定义问题页面列表
     * 
     * @param lookTest 前端 回显定义问题页面
     * @return 前端 回显定义问题页面集合
     */
    public List<LookTest> selectLookTestList(LookTest lookTest);

    /**
     * 新增前端 回显定义问题页面
     * 
     * @param lookTest 前端 回显定义问题页面
     * @return 结果
     */
    public int insertLookTest(LookTest lookTest);

    /**
     * 修改前端 回显定义问题页面
     * 
     * @param lookTest 前端 回显定义问题页面
     * @return 结果
     */
    public int updateLookTest(LookTest lookTest);

    /**
     * 删除前端 回显定义问题页面
     * 
     * @param id 前端 回显定义问题页面主键
     * @return 结果
     */
    public int deleteLookTestById(Long id);

    /**
     * 批量删除前端 回显定义问题页面
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLookTestByIds(Long[] ids);
}
