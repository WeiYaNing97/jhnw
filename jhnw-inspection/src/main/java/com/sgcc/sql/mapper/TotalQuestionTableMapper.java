package com.sgcc.sql.mapper;

import java.util.List;
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.sql.domain.TotalQuestionTableVO;
import org.apache.ibatis.annotations.Param;

/**
 * 问题及命令Mapper接口
 */
public interface TotalQuestionTableMapper 
{
    /**
     * 查询问题及命令
     * 
     * @param id 问题及命令主键
     * @return 问题及命令
     */
    public TotalQuestionTable selectTotalQuestionTableById(String id);

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
    int insertTotalQuestionTableImport(TotalQuestionTable totalQuestionTable);

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
    public int deleteTotalQuestionTableById(@Param("id") String id);

    /**
     * 批量删除问题及命令
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTotalQuestionTableByIds(Long[] ids);

    /**
    * @method: 查询问题及命令列表
    * @Param: [totalQuestionTable]
    * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
    */
    public List<TotalQuestionTable> selectTotalQuestionTabletypeProblemList(TotalQuestionTable totalQuestionTable);

    /**
    * @method: 品牌列表
    * @Param: [totalQuestionTable]
    * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
    */
    List<TotalQuestionTable> selectTotalQuestionTablebrandList(TotalQuestionTable totalQuestionTable);

    /**
    * @method: 型号列表
    * @Param: [totalQuestionTable]
    * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
    */
    List<TotalQuestionTable> selectTotalQuestionTabletypelist(TotalQuestionTable totalQuestionTable);

    /**
    * @method: 内部固件版本
    * @Param: [totalQuestionTable]
    * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
    */
    List<TotalQuestionTable> selectTotalQuestionTablefirewareVersionlist(TotalQuestionTable totalQuestionTable);

    /**
    * @method: 子版本号
    * @Param: [totalQuestionTable]
    * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
    */
    List<TotalQuestionTable> selectTotalQuestionTablesubVersionlist(TotalQuestionTable totalQuestionTable);

    TotalQuestionTable selectPojoByproblemDescribeId(@Param("problemDescribeId") Long id);

    List<TotalQuestionTable> fuzzyTotalQuestionTableList(TotalQuestionTable totalQuestionTable);

    /*查询可扫描问题*/
    List<TotalQuestionTable> queryScannableQuestionsList(TotalQuestionTable totalQuestionTable);

    List<TotalQuestionTable> queryVagueScannableQuestionsList(@Param("sql") String sql);


    /*根据ID数组查询集合*/
    List<TotalQuestionTable> selectTotalQuestionTableByIds(Long[] ids);

    /*根据实体类 模糊查询 实体类集合 fuzzyQueryListBymybatis*/
    List<TotalQuestionTableVO> fuzzyQueryListBymybatis(TotalQuestionTable totalQuestionTable);

    /*根据问题种类查询范本问题名称*/
    List<TotalQuestionTable> selectTotalQuestionTabletypeProblemListBytypeProblem(String typeProblem);
    /*根据问题种类查询范本问题名称*/
    List<String> selectTemProNamelistBytypeProblem(String typeProblem);

    List<TotalQuestionTable> selectTotalQuestionTableListInsert(TotalQuestionTable pojo);
    /*删除数据表所有数据*/
    int deleteTotalQuestionTable();

    /* 根据范式名称 、 自定义名称获取 temProName;problemName 问题表数据 */
    List<TotalQuestionTable> selectTotalQuestionTableByName(@Param("temProName") String temProName,@Param("problemName") String problemName);
}
