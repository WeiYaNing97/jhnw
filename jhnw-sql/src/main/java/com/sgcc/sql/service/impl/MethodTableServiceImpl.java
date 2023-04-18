package com.sgcc.sql.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.sql.mapper.MethodTableMapper;
import com.sgcc.sql.domain.MethodTable;
import com.sgcc.sql.service.IMethodTableService;

/**
 * 方法Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-04-13
 */
@Service
public class MethodTableServiceImpl implements IMethodTableService 
{
    @Autowired
    private MethodTableMapper methodTableMapper;

    /**
     * 查询方法
     * 
     * @param id 方法主键
     * @return 方法
     */
    @Override
    public MethodTable selectMethodTableById(String id)
    {
        return methodTableMapper.selectMethodTableById(id);
    }

    /**
     * 查询方法列表
     * 
     * @param methodTable 方法
     * @return 方法
     */
    @Override
    public List<MethodTable> selectMethodTableList(MethodTable methodTable)
    {
        return methodTableMapper.selectMethodTableList(methodTable);
    }

    /**
     * 新增方法
     * 
     * @param methodTable 方法
     * @return 结果
     */
    @Override
    public int insertMethodTable(MethodTable methodTable)
    {
        return methodTableMapper.insertMethodTable(methodTable);
    }

    /**
     * 修改方法
     * 
     * @param methodTable 方法
     * @return 结果
     */
    @Override
    public int updateMethodTable(MethodTable methodTable)
    {
        return methodTableMapper.updateMethodTable(methodTable);
    }

    /**
     * 批量删除方法
     * 
     * @param ids 需要删除的方法主键
     * @return 结果
     */
    @Override
    public int deleteMethodTableByIds(String[] ids)
    {
        return methodTableMapper.deleteMethodTableByIds(ids);
    }

    /**
     * 删除方法信息
     * 
     * @param id 方法主键
     * @return 结果
     */
    @Override
    public int deleteMethodTableById(String id)
    {
        return methodTableMapper.deleteMethodTableById(id);
    }
}
