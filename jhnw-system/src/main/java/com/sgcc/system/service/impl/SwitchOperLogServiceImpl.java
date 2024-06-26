package com.sgcc.system.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.system.mapper.SwitchOperLogMapper;
import com.sgcc.system.domain.SwitchOperLog;
import com.sgcc.system.service.ISwitchOperLogService;

/**
 * 操作日志记录Service业务层处理
 */
@Service
public class SwitchOperLogServiceImpl implements ISwitchOperLogService 
{
    @Autowired
    private SwitchOperLogMapper switchOperLogMapper;

    /**
     * 查询操作日志记录
     * 
     * @param operId 操作日志记录主键
     * @return 操作日志记录
     */
    @Override
    public SwitchOperLog selectSwitchOperLogByOperId(Long operId)
    {
        return switchOperLogMapper.selectSwitchOperLogByOperId(operId);
    }

    /**
     * 查询操作日志记录列表
     * 
     * @param switchOperLog 操作日志记录
     * @return 操作日志记录
     */
    @Override
    public List<SwitchOperLog> selectSwitchOperLogList(SwitchOperLog switchOperLog)
    {
        return switchOperLogMapper.selectSwitchOperLogList(switchOperLog);
    }

    /**
     * 新增操作日志记录
     * 
     * @param switchOperLog 操作日志记录
     * @return 结果
     */
    @Override
    public int insertSwitchOperLog(SwitchOperLog switchOperLog)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String ScanningTime = simpleDateFormat.format(new Date());
        return switchOperLogMapper.insertSwitchOperLog(switchOperLog);
    }

    /**
     * 修改操作日志记录
     * 
     * @param switchOperLog 操作日志记录
     * @return 结果
     */
    @Override
    public int updateSwitchOperLog(SwitchOperLog switchOperLog)
    {
        return switchOperLogMapper.updateSwitchOperLog(switchOperLog);
    }

    /**
     * 批量删除操作日志记录
     * 
     * @param operIds 需要删除的操作日志记录主键
     * @return 结果
     */
    @Override
    public int deleteSwitchOperLogByOperIds(Long[] operIds)
    {
        return switchOperLogMapper.deleteSwitchOperLogByOperIds(operIds);
    }

    /**
     * 删除操作日志记录信息
     * 
     * @param operId 操作日志记录主键
     * @return 结果
     */
    @Override
    public int deleteSwitchOperLogByOperId(Long operId)
    {
        return switchOperLogMapper.deleteSwitchOperLogByOperId(operId);
    }

    @Override
    public int deleteSwitchOperLogByTime(String data) {
        return switchOperLogMapper.deleteSwitchOperLogByTime(data);
    }
}
