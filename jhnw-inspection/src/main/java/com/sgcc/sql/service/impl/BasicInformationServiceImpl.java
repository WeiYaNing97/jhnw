package com.sgcc.sql.service.impl;

import com.sgcc.sql.domain.BasicInformation;
import com.sgcc.sql.mapper.BasicInformationMapper;
import com.sgcc.sql.service.IBasicInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 获取基本信息命令Service业务层处理
 *
 */
@Service
public class BasicInformationServiceImpl implements IBasicInformationService 
{
    @Autowired
    private BasicInformationMapper basicInformationMapper;

    /**
     * 查询获取基本信息命令
     * 
     * @param id 获取基本信息命令主键
     * @return 获取基本信息命令
     */
    @Override
    public BasicInformation selectBasicInformationById(Long id)
    {
        return basicInformationMapper.selectBasicInformationById(id);
    }

    /**
     * 查询获取基本信息命令列表
     * 
     * @param basicInformation 获取基本信息命令
     * @return 获取基本信息命令
     */
    @Override
    public List<BasicInformation> selectBasicInformationList(BasicInformation basicInformation)
    {
        return basicInformationMapper.selectBasicInformationList(basicInformation);
    }

    /**
     * 新增获取基本信息命令
     * 
     * @param basicInformation 获取基本信息命令
     * @return 结果
     */
    @Override
    public int insertBasicInformation(BasicInformation basicInformation)
    {
        return basicInformationMapper.insertBasicInformation(basicInformation);
    }

    /**
     * 修改获取基本信息命令
     * 
     * @param basicInformation 获取基本信息命令
     * @return 结果
     */
    @Override
    public int updateBasicInformation(BasicInformation basicInformation)
    {
        return basicInformationMapper.updateBasicInformation(basicInformation);
    }

    /**
     * 批量删除获取基本信息命令
     * 
     * @param ids 需要删除的获取基本信息命令主键
     * @return 结果
     */
    @Override
    public int deleteBasicInformationByIds(Long[] ids)
    {
        return basicInformationMapper.deleteBasicInformationByIds(ids);
    }

    /**
     * 删除获取基本信息命令信息
     * 
     * @param id 获取基本信息命令主键
     * @return 结果
     */
    @Override
    public int deleteBasicInformationById(Long id)
    {
        return basicInformationMapper.deleteBasicInformationById(id);
    }

    /*删除数据表所有数据*/
    @Override
    public int deleteBasicInformation() {
        return basicInformationMapper.deleteBasicInformation();
    }

}
