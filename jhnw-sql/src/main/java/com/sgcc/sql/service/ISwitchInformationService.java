package com.sgcc.sql.service;

import java.util.List;
import com.sgcc.sql.domain.SwitchInformation;

/**
 * 交换机四项基本信息Service接口
 * 
 * @author ruoyi
 * @date 2023-05-09
 */
public interface ISwitchInformationService 
{
    /**
     * 查询交换机四项基本信息
     * 
     * @param id 交换机四项基本信息主键
     * @return 交换机四项基本信息
     */
    public SwitchInformation selectSwitchInformationById(Long id);

    /**
     * 查询交换机四项基本信息列表
     * 
     * @param switchInformation 交换机四项基本信息
     * @return 交换机四项基本信息集合
     */
    public List<SwitchInformation> selectSwitchInformationList(SwitchInformation switchInformation);

    /**
     * 新增交换机四项基本信息
     * 
     * @param switchInformation 交换机四项基本信息
     * @return 结果
     */
    public int insertSwitchInformation(SwitchInformation switchInformation);

    /**
     * 修改交换机四项基本信息
     * 
     * @param switchInformation 交换机四项基本信息
     * @return 结果
     */
    public int updateSwitchInformation(SwitchInformation switchInformation);

    /**
     * 批量删除交换机四项基本信息
     * 
     * @param ids 需要删除的交换机四项基本信息主键集合
     * @return 结果
     */
    public int deleteSwitchInformationByIds(Long[] ids);

    /**
     * 删除交换机四项基本信息信息
     * 
     * @param id 交换机四项基本信息主键
     * @return 结果
     */
    public int deleteSwitchInformationById(Long id);
}
