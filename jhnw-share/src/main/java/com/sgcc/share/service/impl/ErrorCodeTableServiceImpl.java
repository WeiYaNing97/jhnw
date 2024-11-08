package com.sgcc.share.service.impl;

import java.util.List;
import com.sgcc.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.share.mapper.ErrorCodeTableMapper;
import com.sgcc.share.domain.ErrorCodeTable;
import com.sgcc.share.service.IErrorCodeTableService;

/**
 * 错误代码Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-10-08
 */
@Service
public class ErrorCodeTableServiceImpl implements IErrorCodeTableService 
{
    @Autowired
    private ErrorCodeTableMapper errorCodeTableMapper;

    /**
     * 查询错误代码
     * 
     * @param errorCodeId 错误代码主键
     * @return 错误代码
     */
    @Override
    public ErrorCodeTable selectErrorCodeTableByErrorCodeId(Long errorCodeId)
    {
        return errorCodeTableMapper.selectErrorCodeTableByErrorCodeId(errorCodeId);
    }

    /**
     * 查询错误代码列表
     * 
     * @param errorCodeTable 错误代码
     * @return 错误代码
     */
    @Override
    public List<ErrorCodeTable> selectErrorCodeTableList(ErrorCodeTable errorCodeTable)
    {
        return errorCodeTableMapper.selectErrorCodeTableList(errorCodeTable);
    }

    /**
     * 新增错误代码
     * 
     * @param errorCodeTable 错误代码
     * @return 结果
     */
    @Override
    public int insertErrorCodeTable(ErrorCodeTable errorCodeTable)
    {
        errorCodeTable.setCreateTime(DateUtils.getNowDate());
        return errorCodeTableMapper.insertErrorCodeTable(errorCodeTable);
    }

    /**
     * 修改错误代码
     * 
     * @param errorCodeTable 错误代码
     * @return 结果
     */
    @Override
    public int updateErrorCodeTable(ErrorCodeTable errorCodeTable)
    {
        return errorCodeTableMapper.updateErrorCodeTable(errorCodeTable);
    }

    /**
     * 批量删除错误代码
     * 
     * @param errorCodeIds 需要删除的错误代码主键
     * @return 结果
     */
    @Override
    public int deleteErrorCodeTableByErrorCodeIds(Long[] errorCodeIds)
    {
        return errorCodeTableMapper.deleteErrorCodeTableByErrorCodeIds(errorCodeIds);
    }

    /**
     * 删除错误代码信息
     * 
     * @param errorCodeId 错误代码主键
     * @return 结果
     */
    @Override
    public int deleteErrorCodeTableByErrorCodeId(Long errorCodeId)
    {
        return errorCodeTableMapper.deleteErrorCodeTableByErrorCodeId(errorCodeId);
    }
}
