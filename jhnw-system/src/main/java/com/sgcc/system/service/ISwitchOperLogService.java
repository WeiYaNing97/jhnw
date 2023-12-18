package com.sgcc.system.service;

import java.util.List;
import com.sgcc.system.domain.SwitchOperLog;

/**
 * 操作日志记录Service接口
 */
public interface ISwitchOperLogService 
{
    /**
     * 查询操作日志记录
     * 
     * @param operId 操作日志记录主键
     * @return 操作日志记录
     */
    public SwitchOperLog selectSwitchOperLogByOperId(Long operId);

    /**
     * 查询操作日志记录列表
     * 
     * @param switchOperLog 操作日志记录
     * @return 操作日志记录集合
     */
    public List<SwitchOperLog> selectSwitchOperLogList(SwitchOperLog switchOperLog);

    /**
     * 新增操作日志记录
     * 
     * @param switchOperLog 操作日志记录
     * @return 结果
     */
    public int insertSwitchOperLog(SwitchOperLog switchOperLog);

    /**
     * 修改操作日志记录
     * 
     * @param switchOperLog 操作日志记录
     * @return 结果
     */
    public int updateSwitchOperLog(SwitchOperLog switchOperLog);

    /**
     * 批量删除操作日志记录
     * 
     * @param operIds 需要删除的操作日志记录主键集合
     * @return 结果
     */
    public int deleteSwitchOperLogByOperIds(Long[] operIds);

    /**
     * 删除操作日志记录信息
     * 
     * @param operId 操作日志记录主键
     * @return 结果
     */
    public int deleteSwitchOperLogByOperId(Long operId);
}
