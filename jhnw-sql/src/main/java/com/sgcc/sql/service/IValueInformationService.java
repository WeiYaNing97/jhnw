package com.sgcc.sql.service;

import com.sgcc.sql.domain.ValueInformation;
import com.sgcc.sql.domain.ValueInformationVO;

import java.util.List;

/**
 * 取值信息存储Service接口
 * 
 * @author ruoyi
 * @date 2021-12-28
 */
public interface IValueInformationService 
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
     * 批量删除取值信息存储
     * 
     * @param ids 需要删除的取值信息存储主键集合
     * @return 结果
     */
    public int deleteValueInformationByIds(Long[] ids);

    /**
     * 删除取值信息存储信息
     * 
     * @param id 取值信息存储主键
     * @return 结果
     */
    public int deleteValueInformationById(Long id);

    /**
    * @method: 根据第一个参数id 查询参数列表
    * @Param: [id]
    * @return: java.util.List<com.sgcc.sql.domain.ValueInformationVO>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    List<ValueInformationVO> selectValueInformationVOListByID(Long id);

    List<ValueInformation> selectValueInformationListByID(Long valueId);
}
