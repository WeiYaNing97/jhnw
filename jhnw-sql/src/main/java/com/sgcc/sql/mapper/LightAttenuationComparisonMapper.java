package com.sgcc.sql.mapper;

import java.util.List;
import com.sgcc.sql.domain.LightAttenuationComparison;

/**
 * 光衰平均值比较Mapper接口
 * 
 * @author ruoyi
 * @date 2023-05-06
 */
public interface LightAttenuationComparisonMapper 
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
     * 删除光衰平均值比较
     * 
     * @param id 光衰平均值比较主键
     * @return 结果
     */
    public int deleteLightAttenuationComparisonById(Long id);

    /**
     * 批量删除光衰平均值比较
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLightAttenuationComparisonByIds(Long[] ids);
}