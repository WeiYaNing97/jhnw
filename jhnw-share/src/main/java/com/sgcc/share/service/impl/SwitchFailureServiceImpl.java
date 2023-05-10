package com.sgcc.share.service.impl;


import com.sgcc.share.domain.SwitchFailure;
import com.sgcc.share.mapper.SwitchFailureMapper;
import com.sgcc.share.service.ISwitchFailureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<SwitchFailure> selectSwitchFailureListByPojo(SwitchFailure switchFailure) {
        List<SwitchFailure> switchFailures = switchFailureMapper.selectSwitchFailureListByPojo(switchFailure);
        if (switchFailures == null){
            return null;
        }
        return switchFailures;
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

    /*删除数据表所有数据*/
    @Override
    public int deleteSwitchFailureByFailure() {
        return switchFailureMapper.deleteSwitchFailureByFailure();
    }
}
