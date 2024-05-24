package com.sgcc.share.mapper;

import com.sgcc.share.domain.ReturnRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 返回信息Mapper接口
 */
public interface ReturnRecordMapper 
{
    /**
     * 查询返回信息
     * 
     * @param id 返回信息主键
     * @return 返回信息
     */
    public ReturnRecord selectReturnRecordById(Long id);

    /**
     * 查询返回信息表 单表数据
     *
     * @param id 返回信息主键
     * @return 返回信息
     */
    ReturnRecord selectPojoSingleTableById(Long id);

    /**
     * 查询返回信息表 单表数据集合
     *
     * @param ids 返回信息主键数组
     * @return 返回信息
     */
    List<ReturnRecord> selectPojoSingleTableListById(Long[] ids);

    /**
     * 查询返回信息列表
     * 
     * @param returnRecord 返回信息
     * @return 返回信息集合
     */
    public List<ReturnRecord> selectReturnRecordList(ReturnRecord returnRecord);


    /** 查询数据表所有信息 */
    List<ReturnRecord> selectReturnRecordAllList();



    /**
     * 新增返回信息
     * 
     * @param returnRecord 返回信息
     * @return 结果
     */
    public int insertReturnRecord(ReturnRecord returnRecord);

    /**
     * 修改返回信息
     * 
     * @param returnRecord 返回信息
     * @return 结果
     */
    public int updateReturnRecord(ReturnRecord returnRecord);

    /**
     * 删除返回信息
     * 
     * @param id 返回信息主键
     * @return 结果
     */
    public int deleteReturnRecordById(Long id);

    /**
     * 批量删除返回信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteReturnRecordByIds(Long[] ids);

    /**
    * @method: 根据时间 查询pojo集合
    * @Param: [data]
    * @return: java.util.List<com.sgcc.sql.domain.ReturnRecord>
    */
    List<ReturnRecord> selectReturnRecordListByDataTime(String data);
    List<ReturnRecord> seletReturnRecordByDate(@Param("stringData") String stringData);

    int deleteReturnRecordByDate(@Param("stringData") String stringData);

    /*删除数据表所有数据*/
    int deleteReturnRecord();
}
