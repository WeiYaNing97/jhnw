package com.sgcc.sql.service;

import java.util.List;
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.sql.domain.TotalQuestionTableVO;

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

    /*根据实体类 模糊查询 实体类集合*/
    List<TotalQuestionTable> fuzzyTotalQuestionTableList(TotalQuestionTable totalQuestionTable);

    /*查询可扫描问题       1        */
    List<TotalQuestionTable> queryScannableQuestionsList(TotalQuestionTable totalQuestionTable);
    /*查询可扫描问题       带*  S*        */
    List<TotalQuestionTable> queryVagueScannableQuestionsList(TotalQuestionTable totalQuestionTable);

    /*根据ID数组查询集合*/
    List<TotalQuestionTable> selectTotalQuestionTableByIds(Long[] ids);

    /*根据实体类 模糊查询 实体类集合 fuzzyQueryListBymybatis*/
    List<TotalQuestionTableVO> fuzzyQueryListBymybatis(TotalQuestionTable totalQuestionTable);

    /*根据问题种类查询范本问题名称*/
    List<TotalQuestionTable> selectTotalQuestionTabletypeProblemListBytypeProblem(String typeProblem);

    List<String> selectTemProNamelistBytypeProblem(String typeProblem);

    List<TotalQuestionTable> selectTotalQuestionTableListInsert(TotalQuestionTable pojo);

    /*删除数据表所有数据*/
    int deleteTotalQuestionTable();
}
