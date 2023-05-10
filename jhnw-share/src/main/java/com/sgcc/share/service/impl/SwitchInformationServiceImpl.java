package com.sgcc.share.service.impl;


import com.sgcc.common.utils.DateUtils;
import com.sgcc.share.domain.SwitchInformation;
import com.sgcc.share.mapper.SwitchInformationMapper;
import com.sgcc.share.service.ISwitchInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 交换机四项基本信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-05-09
 */
@Service
public class SwitchInformationServiceImpl implements ISwitchInformationService
{
    @Autowired
    private SwitchInformationMapper switchInformationMapper;

    /**
     * 查询交换机四项基本信息
     * 
     * @param id 交换机四项基本信息主键
     * @return 交换机四项基本信息
     */
    @Override
    public SwitchInformation selectSwitchInformationById(Long id)
    {
        return switchInformationMapper.selectSwitchInformationById(id);
    }

    /**
     * 查询交换机四项基本信息列表
     * 
     * @param switchInformation 交换机四项基本信息
     * @return 交换机四项基本信息
     */
    @Override
    public List<SwitchInformation> selectSwitchInformationList(SwitchInformation switchInformation)
    {
        return switchInformationMapper.selectSwitchInformationList(switchInformation);
    }

    /**
     * 新增交换机四项基本信息
     * 
     * @param switchInformation 交换机四项基本信息
     * @return 结果
     */
    @Override
    public int insertSwitchInformation(SwitchInformation switchInformation)
    {
        switchInformation.setCreateTime(DateUtils.getNowDate());
        return switchInformationMapper.insertSwitchInformation(switchInformation);
    }

    /**
     * 修改交换机四项基本信息
     * 
     * @param switchInformation 交换机四项基本信息
     * @return 结果
     */
    @Override
    public int updateSwitchInformation(SwitchInformation switchInformation)
    {
        switchInformation.setUpdateTime(DateUtils.getNowDate());
        return switchInformationMapper.updateSwitchInformation(switchInformation);
    }

    /**
     * 批量删除交换机四项基本信息
     * 
     * @param ids 需要删除的交换机四项基本信息主键
     * @return 结果
     */
    @Override
    public int deleteSwitchInformationByIds(Long[] ids)
    {
        return switchInformationMapper.deleteSwitchInformationByIds(ids);
    }

    /**
     * 删除交换机四项基本信息信息
     * 
     * @param id 交换机四项基本信息主键
     * @return 结果
     */
    @Override
    public int deleteSwitchInformationById(Long id)
    {
        return switchInformationMapper.deleteSwitchInformationById(id);
    }
}
