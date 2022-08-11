package com.sgcc.sql.mapper;

import java.util.List;
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.sql.domain.TotalQuestionTableVO;
import org.apache.ibatis.annotations.Param;

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
    public int deleteTotalQuestionTableById(@Param("id") Long id);

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
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    public List<TotalQuestionTable> selectTotalQuestionTabletypeProblemList(TotalQuestionTable totalQuestionTable);

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

    TotalQuestionTable selectPojoByproblemDescribeId(@Param("problemDescribeId") Long id);

    List<TotalQuestionTable> fuzzyTotalQuestionTableList(TotalQuestionTable totalQuestionTable);

    /*查询可扫描问题*/
    List<TotalQuestionTable> queryScannableQuestionsList(TotalQuestionTable totalQuestionTable);

    /*根据ID数组查询集合*/
    List<TotalQuestionTable> selectTotalQuestionTableByIds(Long[] ids);

    /*根据实体类 模糊查询 实体类集合 fuzzyQueryListBymybatis*/
    List<TotalQuestionTableVO> fuzzyQueryListBymybatis(TotalQuestionTable totalQuestionTable);

    /*根据问题种类查询范本问题名称*/
    List<TotalQuestionTable> selectTotalQuestionTabletypeProblemListBytypeProblem(String typeProblem);
}
