package com.sgcc.share.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.share.mapper.NonRelationalDataTableMapper;
import com.sgcc.share.domain.NonRelationalDataTable;
import com.sgcc.share.service.INonRelationalDataTableService;

/**
 * 非关系型数据 存储长数据字段Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-05-22
 */
@Service
public class NonRelationalDataTableServiceImpl implements INonRelationalDataTableService 
{
    @Autowired
    private NonRelationalDataTableMapper nonRelationalDataTableMapper;

    /**
     * 查询非关系型数据 存储长数据字段
     * 
     * @param nonRelationalId 非关系型数据 存储长数据字段主键
     * @return 非关系型数据 存储长数据字段
     */
    @Override
    public NonRelationalDataTable selectNonRelationalDataTableByNonRelationalId(String nonRelationalId)
    {
        return nonRelationalDataTableMapper.selectNonRelationalDataTableByNonRelationalId(nonRelationalId);
    }

    /**
     * 查询非关系型数据 存储长数据字段列表
     * 
     * @param nonRelationalDataTable 非关系型数据 存储长数据字段
     * @return 非关系型数据 存储长数据字段
     */
    @Override
    public List<NonRelationalDataTable> selectNonRelationalDataTableList(NonRelationalDataTable nonRelationalDataTable)
    {
        return nonRelationalDataTableMapper.selectNonRelationalDataTableList(nonRelationalDataTable);
    }

    /**
     * 新增非关系型数据 存储长数据字段
     * 
     * @param nonRelationalDataTable 非关系型数据 存储长数据字段
     * @return 结果
     */
    @Override
    public int insertNonRelationalDataTable(NonRelationalDataTable nonRelationalDataTable)
    {
        return nonRelationalDataTableMapper.insertNonRelationalDataTable(nonRelationalDataTable);
    }

    /**
     * 修改非关系型数据 存储长数据字段
     * 
     * @param nonRelationalDataTable 非关系型数据 存储长数据字段
     * @return 结果
     */
    @Override
    public int updateNonRelationalDataTable(NonRelationalDataTable nonRelationalDataTable)
    {
        return nonRelationalDataTableMapper.updateNonRelationalDataTable(nonRelationalDataTable);
    }

    /**
     * 批量删除非关系型数据 存储长数据字段
     * 
     * @param nonRelationalIds 需要删除的非关系型数据 存储长数据字段主键
     * @return 结果
     */
    @Override
    public int deleteNonRelationalDataTableByNonRelationalIds(String[] nonRelationalIds)
    {
        return nonRelationalDataTableMapper.deleteNonRelationalDataTableByNonRelationalIds(nonRelationalIds);
    }

    /**
     * 删除非关系型数据 存储长数据字段信息
     * 
     * @param nonRelationalId 非关系型数据 存储长数据字段主键
     * @return 结果
     */
    @Override
    public int deleteNonRelationalDataTableByNonRelationalId(String nonRelationalId)
    {
        return nonRelationalDataTableMapper.deleteNonRelationalDataTableByNonRelationalId(nonRelationalId);
    }
}
