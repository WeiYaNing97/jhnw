package com.sgcc.share.service;

import java.util.List;
import com.sgcc.share.domain.NonRelationalDataTable;

/**
 * 非关系型数据 存储长数据字段Service接口
 * 
 * @author ruoyi
 * @date 2024-05-22
 */
public interface INonRelationalDataTableService 
{
    /**
     * 查询非关系型数据 存储长数据字段
     * 
     * @param nonRelationalId 非关系型数据 存储长数据字段主键
     * @return 非关系型数据 存储长数据字段
     */
    public NonRelationalDataTable selectNonRelationalDataTableByNonRelationalId(String nonRelationalId);

    /**
     * 查询非关系型数据 存储长数据字段列表
     * 
     * @param nonRelationalDataTable 非关系型数据 存储长数据字段
     * @return 非关系型数据 存储长数据字段集合
     */
    public List<NonRelationalDataTable> selectNonRelationalDataTableList(NonRelationalDataTable nonRelationalDataTable);

    /**
     * 新增非关系型数据 存储长数据字段
     * 
     * @param nonRelationalDataTable 非关系型数据 存储长数据字段
     * @return 结果
     */
    public int insertNonRelationalDataTable(NonRelationalDataTable nonRelationalDataTable);

    /**
     * 修改非关系型数据 存储长数据字段
     * 
     * @param nonRelationalDataTable 非关系型数据 存储长数据字段
     * @return 结果
     */
    public int updateNonRelationalDataTable(NonRelationalDataTable nonRelationalDataTable);

    /**
     * 批量删除非关系型数据 存储长数据字段
     * 
     * @param nonRelationalIds 需要删除的非关系型数据 存储长数据字段主键集合
     * @return 结果
     */
    public int deleteNonRelationalDataTableByNonRelationalIds(String[] nonRelationalIds);

    /**
     * 删除非关系型数据 存储长数据字段信息
     * 
     * @param nonRelationalId 非关系型数据 存储长数据字段主键
     * @return 结果
     */
    public int deleteNonRelationalDataTableByNonRelationalId(String nonRelationalId);
}
