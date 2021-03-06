package com.sgcc.sql.service;

import java.util.List;
import com.sgcc.sql.domain.TotalQuestionTable;

/**
 * 问题及命令Service接口
 * 
 * @author 韦亚宁
 * @date 2021-12-14
 */
public interface ITotalQuestionTableService 
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
     * 批量删除问题及命令
     * 
     * @param ids 需要删除的问题及命令主键集合
     * @return 结果
     */
    public int deleteTotalQuestionTableByIds(Long[] ids);

    /**
     * 删除问题及命令信息
     * 
     * @param id 问题及命令主键
     * @return 结果
     */
    public int deleteTotalQuestionTableById(Long id);


    /**
    * @method: 查询问题及命令列表
    * @Param: [totalQuestionTable]
    * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    List<TotalQuestionTable> selectTotalQuestionTabletypeProblemList(TotalQuestionTable totalQuestionTable);

    /**
    * @method: 品牌列表
    * @Param: [totalQuestionTable]
    * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    List<TotalQuestionTable> selectTotalQuestionTablebrandList(TotalQuestionTable totalQuestionTable);

    /**
    * @method: 型号列表
    * @Param: [totalQuestionTable]
    * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    List<TotalQuestionTable> selectTotalQuestionTabletypelist(TotalQuestionTable totalQuestionTable);

    /**
    * @method: 内部固件版本
    * @Param: [totalQuestionTable]
    * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    List<TotalQuestionTable> selectTotalQuestionTablefirewareVersionlist(TotalQuestionTable totalQuestionTable);

    /**
    * @method: 子版本号
    * @Param: [totalQuestionTable]
    * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    List<TotalQuestionTable> selectTotalQuestionTablesubVersionlist(TotalQuestionTable totalQuestionTable);

    /**
    * @method: 根据 问题描述表ID  查询 问题表实体类
    * @Param: [id]
    * @return: com.sgcc.sql.domain.TotalQuestionTable
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    TotalQuestionTable selectPojoByproblemDescribeId(Long id);

}
