package com.sgcc.sql.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.sql.mapper.TotalQuestionTableMapper;
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.sql.service.ITotalQuestionTableService;

/**
 * 问题及命令Service业务层处理
 * 
 * @author 韦亚宁
 * @date 2021-12-14
 */
@Service
public class TotalQuestionTableServiceImpl implements ITotalQuestionTableService 
{
    @Autowired
    private TotalQuestionTableMapper totalQuestionTableMapper;

    /**
     * 查询问题及命令
     * 
     * @param id 问题及命令主键
     * @return 问题及命令
     */
    @Override
    public TotalQuestionTable selectTotalQuestionTableById(Long id)
    {
        return totalQuestionTableMapper.selectTotalQuestionTableById(id);
    }

    /**
     * 查询问题及命令列表
     * 
     * @param totalQuestionTable 问题及命令
     * @return 问题及命令
     */
    @Override
    public List<TotalQuestionTable> selectTotalQuestionTableList(TotalQuestionTable totalQuestionTable)
    {
        return totalQuestionTableMapper.selectTotalQuestionTableList(totalQuestionTable);
    }

    /**
     * 新增问题及命令
     * 
     * @param totalQuestionTable 问题及命令
     * @return 结果
     */
    @Override
    public int insertTotalQuestionTable(TotalQuestionTable totalQuestionTable)
    {
        return totalQuestionTableMapper.insertTotalQuestionTable(totalQuestionTable);
    }

    /**
     * 修改问题及命令
     * 
     * @param totalQuestionTable 问题及命令
     * @return 结果
     */
    @Override
    public int updateTotalQuestionTable(TotalQuestionTable totalQuestionTable)
    {
        return totalQuestionTableMapper.updateTotalQuestionTable(totalQuestionTable);
    }

    /**
     * 批量删除问题及命令
     * 
     * @param ids 需要删除的问题及命令主键
     * @return 结果
     */
    @Override
    public int deleteTotalQuestionTableByIds(Long[] ids)
    {
        return totalQuestionTableMapper.deleteTotalQuestionTableByIds(ids);
    }

    /**
     * 删除问题及命令信息
     * 
     * @param id 问题及命令主键
     * @return 结果
     */
    @Override
    public int deleteTotalQuestionTableById(Long id)
    {
        return totalQuestionTableMapper.deleteTotalQuestionTableById(id);
    }
}
