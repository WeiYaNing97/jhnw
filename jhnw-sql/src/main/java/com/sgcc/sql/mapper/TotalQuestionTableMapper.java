package com.sgcc.sql.mapper;

import java.util.List;
import com.sgcc.sql.domain.TotalQuestionTable;

/**
 * 问题及命令Mapper接口
 * 
 * @author 韦亚宁
 * @date 2021-12-14
 */
public interface TotalQuestionTableMapper 
{
    /**
     * 查询问题及命令
     * 
     * @param id 问题及命令主键
     * @return 问题及命令
     */
    public TotalQuestionTable selectTotalQuestionTableById(Long id);

    /**
     * 查询问题及命令列表
     * 
     * @param totalQuestionTable 问题及命令
     * @return 问题及命令集合
     */
    public List<TotalQuestionTable> selectTotalQuestionTableList(TotalQuestionTable totalQuestionTable);

    /**
     * 新增问题及命令
     * 
     * @param totalQuestionTable 问题及命令
     * @return 结果
     */
    public int insertTotalQuestionTable(TotalQuestionTable totalQuestionTable);

    /**
     * 修改问题及命令
     * 
     * @param totalQuestionTable 问题及命令
     * @return 结果
     */
    public int updateTotalQuestionTable(TotalQuestionTable totalQuestionTable);

    /**
     * 删除问题及命令
     * 
     * @param id 问题及命令主键
     * @return 结果
     */
    public int deleteTotalQuestionTableById(Long id);

    /**
     * 批量删除问题及命令
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTotalQuestionTableByIds(Long[] ids);
}
