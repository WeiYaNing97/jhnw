package com.sgcc.share.service;

import java.util.List;
import com.sgcc.share.domain.ErrorCodeTable;

/**
 * 错误代码Service接口
 * 
 * @author ruoyi
 * @date 2024-09-27
 */
public interface IErrorCodeTableService 
{
    /**
     * 查询错误代码
     * 
     * @param errorCodeNumber 错误代码主键
     * @return 错误代码
     */
    public ErrorCodeTable selectErrorCodeTableByErrorCodeNumber(String errorCodeNumber);

    /**
     * 查询错误代码列表
     * 
     * @param errorCodeTable 错误代码
     * @return 错误代码集合
     */
    public List<ErrorCodeTable> selectErrorCodeTableList(ErrorCodeTable errorCodeTable);

    /**
     * 新增错误代码
     * 
     * @param errorCodeTable 错误代码
     * @return 结果
     */
    public int insertErrorCodeTable(ErrorCodeTable errorCodeTable);

    /**
     * 修改错误代码
     * 
     * @param errorCodeTable 错误代码
     * @return 结果
     */
    public int updateErrorCodeTable(ErrorCodeTable errorCodeTable);

    /**
     * 批量删除错误代码
     * 
     * @param errorCodeNumbers 需要删除的错误代码主键集合
     * @return 结果
     */
    public int deleteErrorCodeTableByErrorCodeNumbers(String[] errorCodeNumbers);

    /**
     * 删除错误代码信息
     * 
     * @param errorCodeNumber 错误代码主键
     * @return 结果
     */
    public int deleteErrorCodeTableByErrorCodeNumber(String errorCodeNumber);
}
