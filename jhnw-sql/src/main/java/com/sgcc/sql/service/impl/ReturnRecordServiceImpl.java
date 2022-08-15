package com.sgcc.sql.service.impl;

import com.sgcc.common.utils.DateUtils;
import com.sgcc.sql.domain.ReturnRecord;
import com.sgcc.sql.mapper.ReturnRecordMapper;
import com.sgcc.sql.service.IReturnRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 返回信息Service业务层处理
 * 
 * @author 韦亚宁
 * @date 2021-12-22
 */
@Service
public class ReturnRecordServiceImpl implements IReturnRecordService 
{
    @Autowired
    private ReturnRecordMapper returnRecordMapper;

    /**
     * 查询返回信息
     * 
     * @param id 返回信息主键
     * @return 返回信息
     */
    @Override
    public ReturnRecord selectReturnRecordById(Long id)
    {
        return returnRecordMapper.selectReturnRecordById(id);
    }

    /**
     * 查询返回信息列表
     * 
     * @param returnRecord 返回信息
     * @return 返回信息
     */
    @Override
    public List<ReturnRecord> selectReturnRecordList(ReturnRecord returnRecord)
    {
        return returnRecordMapper.selectReturnRecordList(returnRecord);
    }

    /**
     * 新增返回信息
     * 
     * @param returnRecord 返回信息
     * @return 结果
     */
    @Override
    public int insertReturnRecord(ReturnRecord returnRecord)
    {
        returnRecord.setCreateTime(DateUtils.getNowDate());
        int i = returnRecordMapper.insertReturnRecord(returnRecord);
        return Integer.valueOf(returnRecord.getId().toString()).intValue();
    }

    /**
     * 修改返回信息
     * 
     * @param returnRecord 返回信息
     * @return 结果
     */
    @Override
    public int updateReturnRecord(ReturnRecord returnRecord)
    {
        return returnRecordMapper.updateReturnRecord(returnRecord);
    }

    /**
     * 批量删除返回信息
     * 
     * @param ids 需要删除的返回信息主键
     * @return 结果
     */
    @Override
    public int deleteReturnRecordByIds(Long[] ids)
    {
        return returnRecordMapper.deleteReturnRecordByIds(ids);
    }

    /**
     * 删除返回信息信息
     * 
     * @param id 返回信息主键
     * @return 结果
     */
    @Override
    public int deleteReturnRecordById(Long id)
    {
        return returnRecordMapper.deleteReturnRecordById(id);
    }

    /**
    * @method: 根据时间 查询pojo集合
    * @Param: [s]
    * @return: java.util.List<com.sgcc.sql.domain.ReturnRecord>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @Override
    public List<ReturnRecord> selectReturnRecordListByDataTime(String data) {

        return returnRecordMapper.selectReturnRecordListByDataTime(data);

    }

    @Override
    public int deleteReturnRecordByDate(String data) {
        String stringData = "create_time < '"+data+"'";
        return returnRecordMapper.deleteReturnRecordByDate(stringData);
    }
}
