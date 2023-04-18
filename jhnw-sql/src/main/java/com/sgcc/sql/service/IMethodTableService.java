package com.sgcc.sql.service;

import java.util.List;
import com.sgcc.sql.domain.MethodTable;

/**
 * 方法Service接口
 * 
 * @author ruoyi
 * @date 2023-04-13
 */
public interface IMethodTableService 
{
    /**
     * 查询方法
     * 
     * @param id 方法主键
     * @return 方法
     */
    public MethodTable selectMethodTableById(String id);

    /**
     * 查询方法列表
     * 
     * @param methodTable 方法
     * @return 方法集合
     */
    public List<MethodTable> selectMethodTableList(MethodTable methodTable);

    /**
     * 新增方法
     * 
     * @param methodTable 方法
     * @return 结果
     */
    public int insertMethodTable(MethodTable methodTable);

    /**
     * 修改方法
     * 
     * @param methodTable 方法
     * @return 结果
     */
    public int updateMethodTable(MethodTable methodTable);

    /**
     * 批量删除方法
     * 
     * @param ids 需要删除的方法主键集合
     * @return 结果
     */
    public int deleteMethodTableByIds(String[] ids);

    /**
     * 删除方法信息
     * 
     * @param id 方法主键
     * @return 结果
     */
    public int deleteMethodTableById(String id);
}
