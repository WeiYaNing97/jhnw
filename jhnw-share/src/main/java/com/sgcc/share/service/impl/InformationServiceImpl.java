package com.sgcc.share.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.share.mapper.InformationMapper;
import com.sgcc.share.domain.Information;
import com.sgcc.share.service.IInformationService;

/**
 * 交换机信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-03-07
 */
@Service
public class InformationServiceImpl implements IInformationService 
{
    @Autowired
    private InformationMapper informationMapper;

    /**
     * 查询交换机信息
     * 
     * @param id 交换机信息主键
     * @return 交换机信息
     */
    @Override
    public Information selectInformationById(Long id)
    {
        return informationMapper.selectInformationById(id);
    }

    /**
     * 查询交换机信息列表
     * 
     * @param information 交换机信息
     * @return 交换机信息
     */
    @Override
    public List<Information> selectInformationList(Information information)
    {
        return informationMapper.selectInformationList(information);
    }

    /**
     * 新增交换机信息
     * 
     * @param information 交换机信息
     * @return 结果
     */
    @Override
    public int insertInformation(Information information)
    {
        return informationMapper.insertInformation(information);
    }

    /**
     * 修改交换机信息
     * 
     * @param information 交换机信息
     * @return 结果
     */
    @Override
    public int updateInformation(Information information)
    {
        return informationMapper.updateInformation(information);
    }

    /**
     * 批量删除交换机信息
     * 
     * @param ids 需要删除的交换机信息主键
     * @return 结果
     */
    @Override
    public int deleteInformationByIds(Long[] ids)
    {
        return informationMapper.deleteInformationByIds(ids);
    }

    /**
     * 删除交换机信息信息
     * 
     * @param id 交换机信息主键
     * @return 结果
     */
    @Override
    public int deleteInformationById(Long id)
    {
        return informationMapper.deleteInformationById(id);
    }

    @Override
    public List<String> selectDeviceBrandList() {
        return informationMapper.selectDeviceBrandList();
    }

    @Override
    public List<String> selectDeviceModelList(String brand) {
        return informationMapper.selectDeviceModelList(brand);
    }
}
