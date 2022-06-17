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

    /**
    * @method: 查询问题及命令列表
    * @Param: [totalQuestionTable]
    * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @Override
    public List<TotalQuestionTable> selectTotalQuestionTabletypeProblemList(TotalQuestionTable totalQuestionTable) {
        return totalQuestionTableMapper.selectTotalQuestionTabletypeProblemList(totalQuestionTable);
    }

    /**
    * @method: 品牌列表
    * @Param: [totalQuestionTable]
    * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @Override
    public List<TotalQuestionTable> selectTotalQuestionTablebrandList(TotalQuestionTable totalQuestionTable) {
        return totalQuestionTableMapper.selectTotalQuestionTablebrandList(totalQuestionTable);
    }

    /**
    * @method: 型号列表
    * @Param: [totalQuestionTable]
    * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @Override
    public List<TotalQuestionTable> selectTotalQuestionTabletypelist(TotalQuestionTable totalQuestionTable) {
        return totalQuestionTableMapper.selectTotalQuestionTabletypelist(totalQuestionTable);
    }

    /**
    * @method: 内部固件版本
    * @Param: [totalQuestionTable]
    * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @Override
    public List<TotalQuestionTable> selectTotalQuestionTablefirewareVersionlist(TotalQuestionTable totalQuestionTable) {
        return totalQuestionTableMapper.selectTotalQuestionTablefirewareVersionlist(totalQuestionTable);
    }

    /**
    * @method: 子版本号
    * @Param: [totalQuestionTable]
    * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @Override
    public List<TotalQuestionTable> selectTotalQuestionTablesubVersionlist(TotalQuestionTable totalQuestionTable) {
        return totalQuestionTableMapper.selectTotalQuestionTablesubVersionlist(totalQuestionTable);
    }

    /**
    * @method: 根据 问题描述表ID  查询 问题表实体类
    * @Param: [id]
    * @return: com.sgcc.sql.domain.TotalQuestionTable
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @Override
    public TotalQuestionTable selectPojoByproblemDescribeId(Long id) {
        return totalQuestionTableMapper.selectPojoByproblemDescribeId(id);
    }

}
