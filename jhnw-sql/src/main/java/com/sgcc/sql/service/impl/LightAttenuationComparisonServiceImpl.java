package com.sgcc.sql.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.sql.mapper.LightAttenuationComparisonMapper;
import com.sgcc.sql.domain.LightAttenuationComparison;
import com.sgcc.sql.service.ILightAttenuationComparisonService;

/**
 * 光衰平均值比较Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-05-06
 */
@Service
public class LightAttenuationComparisonServiceImpl implements ILightAttenuationComparisonService 
{
    @Autowired
    private LightAttenuationComparisonMapper lightAttenuationComparisonMapper;

    /**
     * 查询光衰平均值比较
     * 
     * @param id 光衰平均值比较主键
     * @return 光衰平均值比较
     */
    @Override
    public LightAttenuationComparison selectLightAttenuationComparisonById(Long id)
    {
        return lightAttenuationComparisonMapper.selectLightAttenuationComparisonById(id);
    }

    /**
     * 查询光衰平均值比较列表
     * 
     * @param lightAttenuationComparison 光衰平均值比较
     * @return 光衰平均值比较
     */
    @Override
    public List<LightAttenuationComparison> selectLightAttenuationComparisonList(LightAttenuationComparison lightAttenuationComparison)
    {
        return lightAttenuationComparisonMapper.selectLightAttenuationComparisonList(lightAttenuationComparison);
    }

    /**
     * 新增光衰平均值比较
     * 
     * @param lightAttenuationComparison 光衰平均值比较
     * @return 结果
     */
    @Override
    public int insertLightAttenuationComparison(LightAttenuationComparison lightAttenuationComparison)
    {
        return lightAttenuationComparisonMapper.insertLightAttenuationComparison(lightAttenuationComparison);
    }

    /**
     * 修改光衰平均值比较
     * 
     * @param lightAttenuationComparison 光衰平均值比较
     * @return 结果
     */
    @Override
    public int updateLightAttenuationComparison(LightAttenuationComparison lightAttenuationComparison)
    {
        return lightAttenuationComparisonMapper.updateLightAttenuationComparison(lightAttenuationComparison);
    }

    /**
     * 批量删除光衰平均值比较
     * 
     * @param ids 需要删除的光衰平均值比较主键
     * @return 结果
     */
    @Override
    public int deleteLightAttenuationComparisonByIds(Long[] ids)
    {
        return lightAttenuationComparisonMapper.deleteLightAttenuationComparisonByIds(ids);
    }

    /**
     * 删除光衰平均值比较信息
     * 
     * @param id 光衰平均值比较主键
     * @return 结果
     */
    @Override
    public int deleteLightAttenuationComparisonById(Long id)
    {
        return lightAttenuationComparisonMapper.deleteLightAttenuationComparisonById(id);
    }
}
