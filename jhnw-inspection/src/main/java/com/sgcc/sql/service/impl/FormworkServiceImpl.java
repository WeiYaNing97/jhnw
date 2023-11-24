package com.sgcc.sql.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.sql.mapper.FormworkMapper;
import com.sgcc.sql.domain.Formwork;
import com.sgcc.sql.service.IFormworkService;

/**
 * 问题模板Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-03-13
 */
@Service
public class FormworkServiceImpl implements IFormworkService 
{
    @Autowired
    private FormworkMapper formworkMapper;

    /**
     * 查询问题模板
     * 
     * @param id 问题模板主键
     * @return 问题模板
     */
    @Override
    public Formwork selectFormworkById(Long id)
    {
        return formworkMapper.selectFormworkById(id);
    }

    @Override
    public List<Formwork> selectFormworkByIds(Long[] ids) {
        return formworkMapper.selectFormworkByIds(ids);
    }

    /**
     * 查询问题模板列表
     * 
     * @param formwork 问题模板
     * @return 问题模板
     */
    @Override
    public List<Formwork> selectFormworkList(Formwork formwork)
    {
        return formworkMapper.selectFormworkList(formwork);
    }

    /**
     * 新增问题模板
     * 
     * @param formwork 问题模板
     * @return 结果
     */
    @Override
    public int insertFormwork(Formwork formwork)
    {
        return formworkMapper.insertFormwork(formwork);
    }

    /**
     * 修改问题模板
     * 
     * @param formwork 问题模板
     * @return 结果
     */
    @Override
    public int updateFormwork(Formwork formwork)
    {
        return formworkMapper.updateFormwork(formwork);
    }

    /**
     * 批量删除问题模板
     * 
     * @param ids 需要删除的问题模板主键
     * @return 结果
     */
    @Override
    public int deleteFormworkByIds(Long[] ids)
    {
        return formworkMapper.deleteFormworkByIds(ids);
    }

    /**
     * 删除问题模板信息
     * 
     * @param id 问题模板主键
     * @return 结果
     */
    @Override
    public int deleteFormworkById(Long id)
    {
        return formworkMapper.deleteFormworkById(id);
    }

    /**
     * @Description 根据问题ID  like模糊查询模板索引 查询模板数据
     * @desc
     * @param id
     * @return
     */
    @Override
    public List<String> selectFormworkByLikeFormworkIndex(String id) {
        return formworkMapper.selectFormworkByLikeFormworkIndex(id);
    }
}
