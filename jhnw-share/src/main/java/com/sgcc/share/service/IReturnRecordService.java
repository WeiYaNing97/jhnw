package com.sgcc.share.service;


import com.sgcc.share.domain.ReturnRecord;

import java.util.List;

/**
 * 返回信息Service接口
 */
public interface IReturnRecordService 
{
    /**
     * 查询返回信息
     * 
     * @param id 返回信息主键
     * @return 返回信息
     */
    public ReturnRecord selectReturnRecordById(Long id);

    /**
     * 查询返回信息列表
     * 
     * @param returnRecord 返回信息
     * @return 返回信息集合
     */
    public List<ReturnRecord> selectReturnRecordList(ReturnRecord returnRecord);

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
     * 批量删除返回信息
     * 
     * @param ids 需要删除的返回信息主键集合
     * @return 结果
     */
    public int deleteReturnRecordByIds(Long[] ids);

    /**
     * 删除返回信息信息
     * 
     * @param id 返回信息主键
     * @return 结果
     */
    public int deleteReturnRecordById(Long id);

    /**
    * @method: 根据时间 查询pojo集合
    * @Param: [s]
    * @return: java.util.List<com.sgcc.sql.domain.ReturnRecord>
    */
    List<ReturnRecord> selectReturnRecordListByDataTime(String data);

    int deleteReturnRecordByDate(String data);

    /*删除数据表所有数据*/
    int deleteReturnRecord();

    /**
     * 根据时间删除返回记录
     *
     * @param data 数据字符串
     * @return 删除记录的结果，成功返回0，失败返回-1
     */
    int deleteReturnRecordByTime(String data);
}
