package com.sgcc.share.service.impl;

import java.util.List;
import com.sgcc.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.share.mapper.AbnormalAlarmInformationMapper;
import com.sgcc.share.domain.AbnormalAlarmInformation;
import com.sgcc.share.service.IAbnormalAlarmInformationService;

/**
 * 异常告警信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-02-26
 */
@Service
public class AbnormalAlarmInformationServiceImpl implements IAbnormalAlarmInformationService 
{
    @Autowired
    private AbnormalAlarmInformationMapper abnormalAlarmInformationMapper;

    /**
     * 查询异常告警信息
     * 
     * @param switchIp 异常告警信息主键
     * @return 异常告警信息
     */
    @Override
    public AbnormalAlarmInformation selectAbnormalAlarmInformationBySwitchIp(String switchIp)
    {
        return abnormalAlarmInformationMapper.selectAbnormalAlarmInformationBySwitchIp(switchIp);
    }

    /**
     * 查询异常告警信息列表
     * 
     * @param abnormalAlarmInformation 异常告警信息
     * @return 异常告警信息
     */
    @Override
    public List<AbnormalAlarmInformation> selectAbnormalAlarmInformationList(AbnormalAlarmInformation abnormalAlarmInformation)
    {
        return abnormalAlarmInformationMapper.selectAbnormalAlarmInformationList(abnormalAlarmInformation);
    }

    /**
     * 新增异常告警信息
     * 
     * @param abnormalAlarmInformation 异常告警信息
     * @return 结果
     */
    @Override
    public int insertAbnormalAlarmInformation(AbnormalAlarmInformation abnormalAlarmInformation)
    {
        abnormalAlarmInformation.setCreateTime(DateUtils.getNowDate());
        return abnormalAlarmInformationMapper.insertAbnormalAlarmInformation(abnormalAlarmInformation);
    }

    /**
     * 修改异常告警信息
     * 
     * @param abnormalAlarmInformation 异常告警信息
     * @return 结果
     */
    @Override
    public int updateAbnormalAlarmInformation(AbnormalAlarmInformation abnormalAlarmInformation)
    {
        return abnormalAlarmInformationMapper.updateAbnormalAlarmInformation(abnormalAlarmInformation);
    }

    /**
     * 批量删除异常告警信息
     * 
     * @param switchIps 需要删除的异常告警信息主键
     * @return 结果
     */
    @Override
    public int deleteAbnormalAlarmInformationBySwitchIps(String[] switchIps)
    {
        return abnormalAlarmInformationMapper.deleteAbnormalAlarmInformationBySwitchIps(switchIps);
    }

    /**
     * 删除异常告警信息信息
     * 
     * @param switchIp 异常告警信息主键
     * @return 结果
     */
    @Override
    public int deleteAbnormalAlarmInformationBySwitchIp(String switchIp)
    {
        return abnormalAlarmInformationMapper.deleteAbnormalAlarmInformationBySwitchIp(switchIp);
    }

    @Override
    public int deleteAbnormalAlarmInformationByTime(String data) {
        return abnormalAlarmInformationMapper.deleteAbnormalAlarmInformationByTime(data);
    }
}
