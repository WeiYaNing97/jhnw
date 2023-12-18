package com.sgcc.sql.mapper;

import com.sgcc.sql.domain.BasicInformation;

import java.util.List;

/**
 * 获取基本信息命令Mapper接口
 *
 */
public interface BasicInformationMapper 
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
     * 删除获取基本信息命令
     * 
     * @param id 获取基本信息命令主键
     * @return 结果
     */
    public int deleteBasicInformationById(Long id);

    /**
     * 批量删除获取基本信息命令
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBasicInformationByIds(Long[] ids);

    /**
     * 删除所有获取基本信息命令
     *
     * @return 结果
     */
    public int deleteBasicInformation();
}
