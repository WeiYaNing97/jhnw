package com.sgcc.sql.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.sql.mapper.SwitchFailureMapper;
import com.sgcc.sql.domain.SwitchFailure;
import com.sgcc.sql.service.ISwitchFailureService;

/**
 * 交换机故障Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-07-26
 */
@Service
public class SwitchFailureServiceImpl implements ISwitchFailureService 
{
    @Autowired
    private SwitchFailureMapper switchFailureMapper;

    /**
     * 查询交换机故障
     * 
     * @param failureId 交换机故障主键
     * @return 交换机故障
     */
    @Override
    public SwitchFailure selectSwitchFailureByFailureId(Long failureId)
    {
        return switchFailureMapper.selectSwitchFailureByFailureId(failureId);
    }

    /**
     * 查询交换机故障列表
     * 
     * @param switchFailure 交换机故障
     * @return 交换机故障
     */
    @Override
    public List<SwitchFailure> selectSwitchFailureList(SwitchFailure switchFailure)
    {
        return switchFailureMapper.selectSwitchFailureList(switchFailure);
    }

    /**
     * 新增交换机故障
     * 
     * @param switchFailure 交换机故障
     * @return 结果
     */
    @Override
    public int insertSwitchFailure(SwitchFailure switchFailure)
    {
        return switchFailureMapper.insertSwitchFailure(switchFailure);
    }

    /**
     * 修改交换机故障
     * 
     * @param switchFailure 交换机故障
     * @return 结果
     */
    @Override
    public int updateSwitchFailure(SwitchFailure switchFailure)
    {
        return switchFailureMapper.updateSwitchFailure(switchFailure);
    }

    /**
     * 批量删除交换机故障
     * 
     * @param failureIds 需要删除的交换机故障主键
     * @return 结果
     */
    @Override
    public int deleteSwitchFailureByFailureIds(Long[] failureIds)
    {
        return switchFailureMapper.deleteSwitchFailureByFailureIds(failureIds);
    }

    /**
     * 删除交换机故障信息
     * 
     * @param failureId 交换机故障主键
     * @return 结果
     */
    @Override
    public int deleteSwitchFailureByFailureId(Long failureId)
    {
        return switchFailureMapper.deleteSwitchFailureByFailureId(failureId);
    }
}
