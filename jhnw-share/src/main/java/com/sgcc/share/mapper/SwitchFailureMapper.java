package com.sgcc.share.mapper;

import com.sgcc.share.domain.SwitchFailure;

import java.util.List;

/**
 * 交换机故障Mapper接口
 * 
 * @author ruoyi
 * @date 2022-07-26
 */
public interface SwitchFailureMapper 
{
    /**
     * 查询交换机故障
     * 
     * @param failureId 交换机故障主键
     * @return 交换机故障
     */
    public SwitchFailure selectSwitchFailureByFailureId(String failureId);

    /**
     * 查询交换机故障列表
     * 
     * @param switchFailure 交换机故障
     * @return 交换机故障集合
     */
    public List<SwitchFailure> selectSwitchFailureList(SwitchFailure switchFailure);

    List<SwitchFailure> selectSwitchFailureListByPojo(SwitchFailure switchFailure);

    /**
     * 新增交换机故障
     * 
     * @param switchFailure 交换机故障
     * @return 结果
     */
    public int insertSwitchFailure(SwitchFailure switchFailure);

    /**
     * 修改交换机故障
     * 
     * @param switchFailure 交换机故障
     * @return 结果
     */
    public int updateSwitchFailure(SwitchFailure switchFailure);

    /**
     * 删除交换机故障
     * 
     * @param failureId 交换机故障主键
     * @return 结果
     */
    public int deleteSwitchFailureByFailureId(String failureId);

    /**
     * 批量删除交换机故障
     * 
     * @param failureIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSwitchFailureByFailureIds(String[] failureIds);

    /*删除数据表所有数据*/
    int deleteSwitchFailureByFailure();
}
