package com.sgcc.sql.mapper;

import java.util.List;
import com.sgcc.sql.domain.Formwork;

/**
 * 问题模板Mapper接口
 * 
 * @author ruoyi
 * @date 2023-03-13
 */
public interface FormworkMapper 
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
     * 删除问题模板
     * 
     * @param id 问题模板主键
     * @return 结果
     */
    public int deleteFormworkById(Long id);

    /**
     * 批量删除问题模板
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFormworkByIds(Long[] ids);

    /**
     * @Description 根据问题ID  like模糊查询模板索引 查询模板数据
     * @desc
     * @param id
     * @return
     */
    List<String> selectFormworkByLikeFormworkIndex(String id);
}
