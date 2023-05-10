package com.sgcc.sql.service;

import com.sgcc.sql.domain.BasicInformation;

import java.util.List;

/**
 * 获取基本信息命令Service接口
 * 
 * @author 韦亚宁
 * @date 2021-12-21
 */
public interface IBasicInformationService 
{
    /**
     * 查询获取基本信息命令
     * 
     * @param id 获取基本信息命令主键
     * @return 获取基本信息命令
     */
    public BasicInformation selectBasicInformationById(Long id);

    /**
     * 查询获取基本信息命令列表
     * 
     * @param basicInformation 获取基本信息命令
     * @return 获取基本信息命令集合
     */
    public List<BasicInformation> selectBasicInformationList(BasicInformation basicInformation);

    /**
     * 新增获取基本信息命令
     * 
     * @param basicInformation 获取基本信息命令
     * @return 结果
     */
    public int insertBasicInformation(BasicInformation basicInformation);

    /**
     * 修改获取基本信息命令
     * 
     * @param basicInformation 获取基本信息命令
     * @return 结果
     */
    public int updateBasicInformation(BasicInformation basicInformation);

    /**
     * 批量删除获取基本信息命令
     * 
     * @param ids 需要删除的获取基本信息命令主键集合
     * @return 结果
     */
    public int deleteBasicInformationByIds(Long[] ids);

    /**
     * 删除获取基本信息命令信息
     * 
     * @param id 获取基本信息命令主键
     * @return 结果
     */
    public int deleteBasicInformationById(Long id);

    /**
     * 删除所有获取基本信息命令信息
     *
     * @return 结果
     */
    public int deleteBasicInformation();
}
