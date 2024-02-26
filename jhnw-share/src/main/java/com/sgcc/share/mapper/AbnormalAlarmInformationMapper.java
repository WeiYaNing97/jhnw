package com.sgcc.share.mapper;

import java.util.List;
import com.sgcc.share.domain.AbnormalAlarmInformation;

/**
 * 异常告警信息Mapper接口
 * 
 * @author ruoyi
 * @date 2024-02-26
 */
public interface AbnormalAlarmInformationMapper 
{
    /**
     * 查询异常告警信息
     * 
     * @param switchIp 异常告警信息主键
     * @return 异常告警信息
     */
    public AbnormalAlarmInformation selectAbnormalAlarmInformationBySwitchIp(String switchIp);

    /**
     * 查询异常告警信息列表
     * 
     * @param abnormalAlarmInformation 异常告警信息
     * @return 异常告警信息集合
     */
    public List<AbnormalAlarmInformation> selectAbnormalAlarmInformationList(AbnormalAlarmInformation abnormalAlarmInformation);

    /**
     * 新增异常告警信息
     * 
     * @param abnormalAlarmInformation 异常告警信息
     * @return 结果
     */
    public int insertAbnormalAlarmInformation(AbnormalAlarmInformation abnormalAlarmInformation);

    /**
     * 修改异常告警信息
     * 
     * @param abnormalAlarmInformation 异常告警信息
     * @return 结果
     */
    public int updateAbnormalAlarmInformation(AbnormalAlarmInformation abnormalAlarmInformation);

    /**
     * 删除异常告警信息
     * 
     * @param switchIp 异常告警信息主键
     * @return 结果
     */
    public int deleteAbnormalAlarmInformationBySwitchIp(String switchIp);

    /**
     * 批量删除异常告警信息
     * 
     * @param switchIps 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAbnormalAlarmInformationBySwitchIps(String[] switchIps);
}
