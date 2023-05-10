package com.sgcc.share.mapper;

import com.sgcc.share.domain.ReturnRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 返回信息Mapper接口
 * 
 * @author 韦亚宁
 * @date 2021-12-22
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
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    List<ReturnRecord> selectReturnRecordListByDataTime(String data);

    int deleteReturnRecordByDate(@Param("stringData") String stringData);

    /*删除数据表所有数据*/
    int deleteReturnRecord();
}
