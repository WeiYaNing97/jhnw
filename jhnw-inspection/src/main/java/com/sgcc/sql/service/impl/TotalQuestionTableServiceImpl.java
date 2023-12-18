package com.sgcc.sql.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.sgcc.share.util.FunctionalMethods;
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
    * @method: 根据 问题描述表ID  查询 问题表实体类
    * @Param: [id]
    * @return: com.sgcc.sql.domain.TotalQuestionTable
    */
    @Override
    public TotalQuestionTable selectPojoByproblemDescribeId(Long id) {
        return totalQuestionTableMapper.selectPojoByproblemDescribeId(id);
    }





    /**
     * @method: 查询问题及命令列表
     * @Param: [totalQuestionTable]
     * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
     */
    @Override
    public List<TotalQuestionTable> selectTotalQuestionTabletypeProblemList(TotalQuestionTable totalQuestionTable) {

        List<TotalQuestionTable> pojo = new ArrayList<>();
        pojo.addAll(totalQuestionTableMapper.selectTotalQuestionTabletypeProblemList(totalQuestionTable));

        String equivalence = FunctionalMethods.getEquivalence(totalQuestionTable.getBrand());
        if (equivalence != null){
            totalQuestionTable.setBrand(equivalence);
            pojo.addAll(totalQuestionTableMapper.selectTotalQuestionTabletypeProblemList(totalQuestionTable));
        }

        return pojo;
    }

    /**
    * @Description 品牌列表
    * @author charles
    * @createTime 2023/12/18 12:01
    * @desc
    * @param totalQuestionTable
     * @return
    */
    @Override
    public List<TotalQuestionTable> selectTotalQuestionTablebrandList(TotalQuestionTable totalQuestionTable) {

        List<TotalQuestionTable> pojo = new ArrayList<>();
        pojo.addAll(totalQuestionTableMapper.selectTotalQuestionTablebrandList(totalQuestionTable));

        String equivalence = FunctionalMethods.getEquivalence(totalQuestionTable.getBrand());
        if (equivalence != null){
            totalQuestionTable.setBrand(equivalence);
            pojo.addAll(totalQuestionTableMapper.selectTotalQuestionTablebrandList(totalQuestionTable));
        }


        return pojo;
    }

    /**
     * @method: 型号列表
     * @Param: [totalQuestionTable]
     * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
     */
    @Override
    public List<TotalQuestionTable> selectTotalQuestionTabletypelist(TotalQuestionTable totalQuestionTable) {
        List<TotalQuestionTable> pojo = new ArrayList<>();
        pojo.addAll(totalQuestionTableMapper.selectTotalQuestionTabletypelist(totalQuestionTable));

        String equivalence = FunctionalMethods.getEquivalence(totalQuestionTable.getBrand());
        if (equivalence != null){
            totalQuestionTable.setBrand(equivalence);
            pojo.addAll(totalQuestionTableMapper.selectTotalQuestionTabletypelist(totalQuestionTable));
        }
        return pojo;
    }

    /**
     * @method: 内部固件版本
     * @Param: [totalQuestionTable]
     * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
     */
    @Override
    public List<TotalQuestionTable> selectTotalQuestionTablefirewareVersionlist(TotalQuestionTable totalQuestionTable) {
        List<TotalQuestionTable> pojo = new ArrayList<>();
        pojo.addAll(totalQuestionTableMapper.selectTotalQuestionTablefirewareVersionlist(totalQuestionTable));

        String equivalence = FunctionalMethods.getEquivalence(totalQuestionTable.getBrand());
        if (equivalence != null){
            totalQuestionTable.setBrand(equivalence);
            pojo.addAll(totalQuestionTableMapper.selectTotalQuestionTablefirewareVersionlist(totalQuestionTable));
        }
        return pojo;
    }

    /**
     * @method: 子版本号
     * @Param: [totalQuestionTable]
     * @return: java.util.List<com.sgcc.sql.domain.TotalQuestionTable>
     */
    @Override
    public List<TotalQuestionTable> selectTotalQuestionTablesubVersionlist(TotalQuestionTable totalQuestionTable) {

        List<TotalQuestionTable> pojo = new ArrayList<>();
        pojo.addAll(totalQuestionTableMapper.selectTotalQuestionTablesubVersionlist(totalQuestionTable));

        String equivalence = FunctionalMethods.getEquivalence(totalQuestionTable.getBrand());
        if (equivalence != null){
            totalQuestionTable.setBrand(equivalence);
            pojo.addAll(totalQuestionTableMapper.selectTotalQuestionTablesubVersionlist(totalQuestionTable));
        }

        return pojo;
    }
    @Override
    public List<TotalQuestionTable> fuzzyTotalQuestionTableList(TotalQuestionTable totalQuestionTable) {
        List<TotalQuestionTable> pojo = new ArrayList<>();
        pojo.addAll(totalQuestionTableMapper.fuzzyTotalQuestionTableList(totalQuestionTable));

        String equivalence = FunctionalMethods.getEquivalence(totalQuestionTable.getBrand());
        if (equivalence != null){
            totalQuestionTable.setBrand(equivalence);
            pojo.addAll(totalQuestionTableMapper.fuzzyTotalQuestionTableList(totalQuestionTable));
        }
        return pojo;
    }

    /*查询可扫描问题*/
    @Override
    public List<TotalQuestionTable> queryScannableQuestionsList(TotalQuestionTable totalQuestionTable) {
        List<TotalQuestionTable> pojo = new ArrayList<>();
        pojo.addAll(totalQuestionTableMapper.queryScannableQuestionsList(totalQuestionTable));

        String equivalence = FunctionalMethods.getEquivalence(totalQuestionTable.getBrand());
        if (equivalence != null){
            totalQuestionTable.setBrand(equivalence);
            pojo.addAll(totalQuestionTableMapper.queryScannableQuestionsList(totalQuestionTable));
        }
        return pojo;
    }

    @Override
    public List<TotalQuestionTable> queryVagueScannableQuestionsList(TotalQuestionTable totalQuestionTable) {
        List<TotalQuestionTable> totalQuestionTables = new ArrayList<>();
        totalQuestionTables.addAll(queryVagueScannableQuestionsListEquivalence(totalQuestionTable));
        String equivalence = FunctionalMethods.getEquivalence(totalQuestionTable.getBrand());
        if (equivalence != null){
            totalQuestionTable.setBrand(equivalence);
            totalQuestionTables.addAll(queryVagueScannableQuestionsListEquivalence(totalQuestionTable));
        }
        return totalQuestionTables;
    }


    public List<TotalQuestionTable> queryVagueScannableQuestionsListEquivalence(TotalQuestionTable totalQuestionTable) {
        //and (type = #{type} or type = '*')
        String typeSQL = "";
        if (totalQuestionTable.getType() != null  && totalQuestionTable.getType() != ""){
            String type = totalQuestionTable.getType();
            typeSQL = "and (LOWER(type) = LOWER(\'" + type +"\') OR type = '*' OR ";

            List<String> stringCollection = ServiceImplUtils.getStringCollection(type);
            for (String typeString:stringCollection){
                typeSQL = typeSQL + "LOWER(type) = LOWER(\'" + typeString+"*\')" +" OR ";
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

        String sql = "where LOWER(brand) = LOWER(\'" + totalQuestionTable.getBrand() + "\')";
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
        List<TotalQuestionTable> totalQuestionTables = new ArrayList<>();
        totalQuestionTables.addAll(queryAdvancedFeaturesListEquivalence(totalQuestionTable));
        String equivalence = FunctionalMethods.getEquivalence(totalQuestionTable.getBrand());
        if (equivalence != null){
            totalQuestionTable.setBrand(equivalence);
            totalQuestionTables.addAll(queryAdvancedFeaturesListEquivalence(totalQuestionTable));
        }
        return totalQuestionTables;
    }


    public List<TotalQuestionTable> queryAdvancedFeaturesListEquivalence(TotalQuestionTable totalQuestionTable) {
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
        List<TotalQuestionTable> totalQuestionTables = new ArrayList<>();
        totalQuestionTables.addAll(queryAdvancedFeaturesListBytemProNameEquivalence(totalQuestionTable));
        String equivalence = FunctionalMethods.getEquivalence(totalQuestionTable.getBrand());
        if (equivalence != null){
            totalQuestionTable.setBrand(equivalence);
            totalQuestionTables.addAll(queryAdvancedFeaturesListBytemProNameEquivalence(totalQuestionTable));
        }
        return totalQuestionTables;
    }

    public List<TotalQuestionTable> queryAdvancedFeaturesListBytemProNameEquivalence(TotalQuestionTable totalQuestionTable) {
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

        List<TotalQuestionTableVO> pojo = new ArrayList<>();
        pojo.addAll(totalQuestionTableMapper.fuzzyQueryListBymybatis(totalQuestionTable));

        String equivalence = FunctionalMethods.getEquivalence(totalQuestionTable.getBrand());
        if (equivalence != null){
            totalQuestionTable.setBrand(equivalence);
            pojo.addAll(totalQuestionTableMapper.fuzzyQueryListBymybatis(totalQuestionTable));
        }
        return pojo;
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
    public List<TotalQuestionTable> selectTotalQuestionTableListInsert(TotalQuestionTable totalQuestionTable) {

        List<TotalQuestionTable> pojo = new ArrayList<>();
        pojo.addAll(totalQuestionTableMapper.selectTotalQuestionTableListInsert(totalQuestionTable));

        String equivalence = FunctionalMethods.getEquivalence(totalQuestionTable.getBrand());
        if (equivalence != null){
            totalQuestionTable.setBrand(equivalence);
            pojo.addAll(totalQuestionTableMapper.selectTotalQuestionTableListInsert(totalQuestionTable));
        }

        return pojo;
    }

    /*删除数据表所有数据*/
    @Override
    public int deleteTotalQuestionTable() {
        return totalQuestionTableMapper.deleteTotalQuestionTable();
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
        List<TotalQuestionTable> pojo = new ArrayList<>();
        pojo.addAll(totalQuestionTableMapper.selectTotalQuestionTableList(totalQuestionTable));

        String equivalence = FunctionalMethods.getEquivalence(totalQuestionTable.getBrand());
        if (equivalence != null){
            totalQuestionTable.setBrand(equivalence);
            pojo.addAll(totalQuestionTableMapper.selectTotalQuestionTableList(totalQuestionTable));
        }

        return pojo;
    }


    /**
    * @Description 导出数据库 查询所有
    * @author charles
    * @createTime 2023/10/24 19:38
    * @desc
    * @param
     * @return
    */
    @Override
    public List<TotalQuestionTable> scanningSQLselectTotalQuestionTableList() {
        return totalQuestionTableMapper.selectTotalQuestionTableList(null);
    }

}
