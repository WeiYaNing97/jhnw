package com.sgcc.sql.service.impl;

import java.util.List;

import com.sgcc.share.util.ServiceImplUtils;
import com.sgcc.sql.domain.TotalQuestionTableVO;
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

    @Override
    public int insertTotalQuestionTableImport(TotalQuestionTable totalQuestionTable) {
        return totalQuestionTableMapper.insertTotalQuestionTableImport(totalQuestionTable);
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

    @Override
    public List<TotalQuestionTable> fuzzyTotalQuestionTableList(TotalQuestionTable totalQuestionTable) {
        return totalQuestionTableMapper.fuzzyTotalQuestionTableList(totalQuestionTable);
    }

    /*查询可扫描问题*/
    @Override
    public List<TotalQuestionTable> queryScannableQuestionsList(TotalQuestionTable totalQuestionTable) {
        return totalQuestionTableMapper.queryScannableQuestionsList(totalQuestionTable);
    }

    @Override
    public List<TotalQuestionTable> queryVagueScannableQuestionsList(TotalQuestionTable totalQuestionTable) {
        //and (type = #{type} or type = '*')
        String typeSQL = "";
        if (totalQuestionTable.getType() != null  && totalQuestionTable.getType() != ""){
            String type = totalQuestionTable.getType();
            typeSQL = "and (type = \'" + type +"\' OR type = '*' OR ";
            List<String> stringCollection = ServiceImplUtils.getStringCollection(type);
            for (String typeString:stringCollection){
                typeSQL = typeSQL + "type = "+"\'" + typeString+"*\'" +" OR ";
            }
            char[] chars = typeSQL.toCharArray();
            typeSQL = "";
            for (int i=0 ;i<chars.length-4;i++ ){
                typeSQL = typeSQL + chars[i];
            }
            typeSQL = typeSQL +")";
        }
        //and (fireware_version = #{firewareVersion} or fireware_version = '*')
        String firewareVersionSQL = "";
        if (totalQuestionTable.getFirewareVersion() != null  && totalQuestionTable.getFirewareVersion() != ""){
            String firewareVersion = totalQuestionTable.getFirewareVersion();
            firewareVersionSQL = "and (fireware_version = \'"+ firewareVersion +"\' OR fireware_version = '*' OR ";
            List<String> stringCollection = ServiceImplUtils.getStringCollection(firewareVersion);
            for (String typeString:stringCollection){
                firewareVersionSQL = firewareVersionSQL + "fireware_version = "+"\'" + typeString+"*\'" +" OR ";
            }
            char[] chars = firewareVersionSQL.toCharArray();
            firewareVersionSQL = "";
            for (int i=0 ;i<chars.length-4;i++ ){
                firewareVersionSQL = firewareVersionSQL + chars[i];
            }
            firewareVersionSQL = firewareVersionSQL +")";
        }
        //and (sub_version = #{subVersion} or sub_version = '*')
        String subVersionSQL = "";
        if (totalQuestionTable.getSubVersion() != null  && totalQuestionTable.getSubVersion() != ""){
            String subVersion = totalQuestionTable.getSubVersion();
            subVersionSQL = "and (sub_version = \'" + subVersion + "\' OR sub_version = '*' OR ";
            List<String> stringCollection = ServiceImplUtils.getStringCollection(subVersion);
            for (String typeString:stringCollection){
                subVersionSQL = subVersionSQL + "sub_version = "+"\'" + typeString+"*\'" +" OR ";
            }
            char[] chars = subVersionSQL.toCharArray();
            subVersionSQL = "";
            for (int i=0 ;i<chars.length-4;i++ ){
                subVersionSQL = subVersionSQL + chars[i];
            }
            subVersionSQL = subVersionSQL +")";
        }

        String sql = "where brand = \'" + totalQuestionTable.getBrand() + "\' ";
        if (totalQuestionTable.getType() != null && totalQuestionTable.getType() != ""){
            sql = sql + typeSQL;
        }
        if (totalQuestionTable.getFirewareVersion() != null && totalQuestionTable.getFirewareVersion() != ""){
            sql = sql + firewareVersionSQL;
        }
        if (totalQuestionTable.getSubVersion() != null && totalQuestionTable.getSubVersion() != ""){
            sql = sql + subVersionSQL;
        }
        sql = sql + "and type_problem != \'高级功能\'";
        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableMapper.queryVagueScannableQuestionsList(sql + " ORDER BY type_problem,tem_pro_name,problem_name");
        return totalQuestionTables;
    }

    @Override
    public List<TotalQuestionTable> queryAdvancedFeaturesList(TotalQuestionTable totalQuestionTable) {
        //and (type = #{type} or type = '*')
        String typeSQL = "";
        if (totalQuestionTable.getType() != null  && totalQuestionTable.getType() != ""){
            String type = totalQuestionTable.getType();
            typeSQL = "and (type = \'" + type +"\' OR type = '*' OR ";
            List<String> stringCollection = ServiceImplUtils.getStringCollection(type);
            for (String typeString:stringCollection){
                typeSQL = typeSQL + "type = "+"\'" + typeString+"*\'" +" OR ";
            }
            char[] chars = typeSQL.toCharArray();
            typeSQL = "";
            for (int i=0 ;i<chars.length-4;i++ ){
                typeSQL = typeSQL + chars[i];
            }
            typeSQL = typeSQL +")";
        }
        //and (fireware_version = #{firewareVersion} or fireware_version = '*')
        String firewareVersionSQL = "";
        if (totalQuestionTable.getFirewareVersion() != null  && totalQuestionTable.getFirewareVersion() != ""){
            String firewareVersion = totalQuestionTable.getFirewareVersion();
            firewareVersionSQL = "and (fireware_version = \'"+ firewareVersion +"\' OR fireware_version = '*' OR ";
            List<String> stringCollection = ServiceImplUtils.getStringCollection(firewareVersion);
            for (String typeString:stringCollection){
                firewareVersionSQL = firewareVersionSQL + "fireware_version = "+"\'" + typeString+"*\'" +" OR ";
            }
            char[] chars = firewareVersionSQL.toCharArray();
            firewareVersionSQL = "";
            for (int i=0 ;i<chars.length-4;i++ ){
                firewareVersionSQL = firewareVersionSQL + chars[i];
            }
            firewareVersionSQL = firewareVersionSQL +")";
        }
        //and (sub_version = #{subVersion} or sub_version = '*')
        String subVersionSQL = "";
        if (totalQuestionTable.getSubVersion() != null  && totalQuestionTable.getSubVersion() != ""){
            String subVersion = totalQuestionTable.getSubVersion();
            subVersionSQL = "and (sub_version = \'" + subVersion + "\' OR sub_version = '*' OR ";
            List<String> stringCollection = ServiceImplUtils.getStringCollection(subVersion);
            for (String typeString:stringCollection){
                subVersionSQL = subVersionSQL + "sub_version = "+"\'" + typeString+"*\'" +" OR ";
            }
            char[] chars = subVersionSQL.toCharArray();
            subVersionSQL = "";
            for (int i=0 ;i<chars.length-4;i++ ){
                subVersionSQL = subVersionSQL + chars[i];
            }
            subVersionSQL = subVersionSQL +")";
        }

        String sql = "where brand = \'" + totalQuestionTable.getBrand() + "\' ";
        if (totalQuestionTable.getType() != null && totalQuestionTable.getType() != ""){
            sql = sql + typeSQL;
        }
        if (totalQuestionTable.getFirewareVersion() != null && totalQuestionTable.getFirewareVersion() != ""){
            sql = sql + firewareVersionSQL;
        }
        if (totalQuestionTable.getSubVersion() != null && totalQuestionTable.getSubVersion() != ""){
            sql = sql + subVersionSQL;
        }
        sql = sql + "and type_problem = \'高级功能\'";
        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableMapper.queryVagueScannableQuestionsList(sql + " ORDER BY type_problem,tem_pro_name,problem_name");
        return totalQuestionTables;
    }

    @Override
    public List<TotalQuestionTable> queryAdvancedFeaturesListBytemProName(TotalQuestionTable totalQuestionTable) {
        //and (type = #{type} or type = '*')
        String typeSQL = "";
        if (totalQuestionTable.getType() != null  && totalQuestionTable.getType() != ""){
            String type = totalQuestionTable.getType();
            typeSQL = "and (type = \'" + type +"\' OR type = '*' OR ";
            List<String> stringCollection = ServiceImplUtils.getStringCollection(type);
            for (String typeString:stringCollection){
                typeSQL = typeSQL + "type = "+"\'" + typeString+"*\'" +" OR ";
            }
            char[] chars = typeSQL.toCharArray();
            typeSQL = "";
            for (int i=0 ;i<chars.length-4;i++ ){
                typeSQL = typeSQL + chars[i];
            }
            typeSQL = typeSQL +")";
        }
        //and (fireware_version = #{firewareVersion} or fireware_version = '*')
        String firewareVersionSQL = "";
        if (totalQuestionTable.getFirewareVersion() != null  && totalQuestionTable.getFirewareVersion() != ""){
            String firewareVersion = totalQuestionTable.getFirewareVersion();
            firewareVersionSQL = "and (fireware_version = \'"+ firewareVersion +"\' OR fireware_version = '*' OR ";
            List<String> stringCollection = ServiceImplUtils.getStringCollection(firewareVersion);
            for (String typeString:stringCollection){
                firewareVersionSQL = firewareVersionSQL + "fireware_version = "+"\'" + typeString+"*\'" +" OR ";
            }
            char[] chars = firewareVersionSQL.toCharArray();
            firewareVersionSQL = "";
            for (int i=0 ;i<chars.length-4;i++ ){
                firewareVersionSQL = firewareVersionSQL + chars[i];
            }
            firewareVersionSQL = firewareVersionSQL +")";
        }
        //and (sub_version = #{subVersion} or sub_version = '*')
        String subVersionSQL = "";
        if (totalQuestionTable.getSubVersion() != null  && totalQuestionTable.getSubVersion() != ""){
            String subVersion = totalQuestionTable.getSubVersion();
            subVersionSQL = "and (sub_version = \'" + subVersion + "\' OR sub_version = '*' OR ";
            List<String> stringCollection = ServiceImplUtils.getStringCollection(subVersion);
            for (String typeString:stringCollection){
                subVersionSQL = subVersionSQL + "sub_version = "+"\'" + typeString+"*\'" +" OR ";
            }
            char[] chars = subVersionSQL.toCharArray();
            subVersionSQL = "";
            for (int i=0 ;i<chars.length-4;i++ ){
                subVersionSQL = subVersionSQL + chars[i];
            }
            subVersionSQL = subVersionSQL +")";
        }

        String sql = "where brand = \'" + totalQuestionTable.getBrand() + "\' ";
        if (totalQuestionTable.getType() != null && totalQuestionTable.getType() != ""){
            sql = sql + typeSQL;
        }
        if (totalQuestionTable.getFirewareVersion() != null && totalQuestionTable.getFirewareVersion() != ""){
            sql = sql + firewareVersionSQL;
        }
        if (totalQuestionTable.getSubVersion() != null && totalQuestionTable.getSubVersion() != ""){
            sql = sql + subVersionSQL;
        }
        sql = sql + "and type_problem = \'高级功能\'" + "and tem_pro_name = \'"+totalQuestionTable.getTemProName()+"\'";
        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableMapper.queryVagueScannableQuestionsList(sql + " ORDER BY type_problem,tem_pro_name,problem_name");
        return totalQuestionTables;
    }


    /*根据ID数组查询集合*/
    @Override
    public List<TotalQuestionTable> selectTotalQuestionTableByIds(Long[] ids) {
        return totalQuestionTableMapper.selectTotalQuestionTableByIds(ids);
    }

    @Override
    public List<TotalQuestionTableVO> fuzzyQueryListBymybatis(TotalQuestionTable totalQuestionTable) {
        List<TotalQuestionTableVO> totalQuestionTableVOS = totalQuestionTableMapper.fuzzyQueryListBymybatis(totalQuestionTable);
        return totalQuestionTableVOS;
    }

    /*根据问题种类查询范本问题名称*/
    @Override
    public List<TotalQuestionTable> selectTotalQuestionTabletypeProblemListBytypeProblem(String typeProblem) {
        return totalQuestionTableMapper.selectTotalQuestionTabletypeProblemListBytypeProblem(typeProblem);
    }

    /*根据问题种类查询范本问题名称*/
    @Override
    public List<String> selectTemProNamelistBytypeProblem(String typeProblem) {
        return totalQuestionTableMapper.selectTemProNamelistBytypeProblem(typeProblem);
    }

    @Override
    public List<TotalQuestionTable> selectTotalQuestionTableListInsert(TotalQuestionTable pojo) {
        return totalQuestionTableMapper.selectTotalQuestionTableListInsert(pojo);
    }

    /*删除数据表所有数据*/
    @Override
    public int deleteTotalQuestionTable() {
        return totalQuestionTableMapper.deleteTotalQuestionTable();
    }

}
