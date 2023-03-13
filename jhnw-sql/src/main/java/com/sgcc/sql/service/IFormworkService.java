package com.sgcc.sql.service;

import java.util.List;
import com.sgcc.sql.domain.Formwork;

/**
 * 问题模板Service接口
 * 
 * @author ruoyi
 * @date 2023-03-13
 */
public interface IFormworkService 
{
    /**
     * 查询问题模板
     * 
     * @param id 问题模板主键
     * @return 问题模板
     */
    public Formwork selectFormworkById(Long id);
    List<Formwork> selectFormworkByIds(Long[] ids);

    /**
     * 查询问题模板列表
     * 
     * @param formwork 问题模板
     * @return 问题模板集合
     */
    public List<Formwork> selectFormworkList(Formwork formwork);

    /**
     * 新增问题模板
     * 
     * @param formwork 问题模板
     * @return 结果
     */
    public int insertFormwork(Formwork formwork);

    /**
     * 修改问题模板
     * 
     * @param formwork 问题模板
     * @return 结果
     */
    public int updateFormwork(Formwork formwork);

    /**
     * 批量删除问题模板
     * 
     * @param ids 需要删除的问题模板主键集合
     * @return 结果
     */
    public int deleteFormworkByIds(Long[] ids);

    /**
     * 删除问题模板信息
     * 
     * @param id 问题模板主键
     * @return 结果
     */
    public int deleteFormworkById(Long id);

}
