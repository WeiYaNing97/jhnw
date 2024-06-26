package com.sgcc.advanced.service;

import com.sgcc.advanced.domain.LightAttenuationComparison;

import java.util.List;

/**
 * 光衰平均值比较Service接口
 * 
 * @author ruoyi
 * @date 2023-05-06
 */
public interface ILightAttenuationComparisonService 
{
    /**
     * 查询光衰平均值比较
     * 
     * @param id 光衰平均值比较主键
     * @return 光衰平均值比较
     */
    public LightAttenuationComparison selectLightAttenuationComparisonById(Long id);

    /**
     * 查询光衰平均值比较列表
     * 
     * @param lightAttenuationComparison 光衰平均值比较
     * @return 光衰平均值比较集合
     */
    public List<LightAttenuationComparison> selectLightAttenuationComparisonList(LightAttenuationComparison lightAttenuationComparison);

    /**
     * 新增光衰平均值比较
     * 
     * @param lightAttenuationComparison 光衰平均值比较
     * @return 结果
     */
    public int insertLightAttenuationComparison(LightAttenuationComparison lightAttenuationComparison);

    /**
     * 修改光衰平均值比较
     * 
     * @param lightAttenuationComparison 光衰平均值比较
     * @return 结果
     */
    public int updateLightAttenuationComparison(LightAttenuationComparison lightAttenuationComparison);

    /**
     * 批量删除光衰平均值比较
     * 
     * @param ids 需要删除的光衰平均值比较主键集合
     * @return 结果
     */
    public int deleteLightAttenuationComparisonByIds(Long[] ids);

    /**
     * 删除光衰平均值比较信息
     * 
     * @param id 光衰平均值比较主键
     * @return 结果
     */
    public int deleteLightAttenuationComparisonById(Long id);

    /**
    * @Description 根据IP获得光衰参数
    */
    List<LightAttenuationComparison> selectPojoListByIP(String ip);

}
