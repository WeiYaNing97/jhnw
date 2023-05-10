package com.sgcc.sql.mapper;

import com.sgcc.sql.domain.ValueInformation;

import java.util.List;

/**
 * 取值信息存储Mapper接口
 * 
 * @author ruoyi
 * @date 2021-12-28
 */
public interface ValueInformationMapper 
{
    /**
     * 查询取值信息存储
     * 
     * @param id 取值信息存储主键
     * @return 取值信息存储
     */
    public ValueInformation selectValueInformationById(Long id);

    /**
     * 查询取值信息存储列表
     * 
     * @param valueInformation 取值信息存储
     * @return 取值信息存储集合
     */
    public List<ValueInformation> selectValueInformationList(ValueInformation valueInformation);

    /**
     * 新增取值信息存储
     * 
     * @param valueInformation 取值信息存储
     * @return 结果
     */
    public int insertValueInformation(ValueInformation valueInformation);

    /**
     * 修改取值信息存储
     * 
     * @param valueInformation 取值信息存储
     * @return 结果
     */
    public int updateValueInformation(ValueInformation valueInformation);

    /**
     * 删除取值信息存储
     * 
     * @param id 取值信息存储主键
     * @return 结果
     */
    public int deleteValueInformationById(Long id);

    /**
     * 批量删除取值信息存储
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteValueInformationByIds(Long[] ids);

}
