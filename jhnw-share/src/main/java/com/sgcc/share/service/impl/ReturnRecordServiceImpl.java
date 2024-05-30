package com.sgcc.share.service.impl;


import com.sgcc.common.utils.DateUtils;
import com.sgcc.share.domain.NonRelationalDataTable;
import com.sgcc.share.domain.ReturnRecord;
import com.sgcc.share.mapper.NonRelationalDataTableMapper;
import com.sgcc.share.mapper.ReturnRecordMapper;
import com.sgcc.share.service.IReturnRecordService;
import com.sgcc.share.util.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回信息Service业务层处理
 */
@Service
public class ReturnRecordServiceImpl implements IReturnRecordService
{
    @Autowired
    private ReturnRecordMapper returnRecordMapper;
    @Autowired
    private NonRelationalDataTableMapper nonRelationalDataTableMapper;

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

        /** 交换机返回结果 数据放入 非关系型数据表*/
        NonRelationalDataTable nonRelationalDataTable = new NonRelationalDataTable();

        nonRelationalDataTable.setNonRelationalId(new MyUtils().getID( (returnRecord.getId() +"").substring(0,4) , null));
        nonRelationalDataTable.setNonRelationalData( returnRecord.getCurrentReturnLog() );
        int i1 = nonRelationalDataTableMapper.insertNonRelationalDataTable(nonRelationalDataTable);
        if (i1<0){
            return i1;
        }
        returnRecord.setCurrentReturnLog(nonRelationalDataTable.getNonRelationalId());
        int i = returnRecordMapper.insertReturnRecord(returnRecord);
        if (i<0){
            return i;
        }
        return Integer.valueOf(returnRecord.getId().toString()).intValue();
    }

    /**
     * @method: 根据时间 查询pojo集合
     * @Param: [s]
     * @return: java.util.List<com.sgcc.sql.domain.ReturnRecord>
     */
    @Override
    public List<ReturnRecord> selectReturnRecordListByDataTime(String data) {

        return returnRecordMapper.selectReturnRecordListByDataTime(data);

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
        ReturnRecord returnRecord = returnRecordMapper.selectPojoSingleTableById(id);
        String nonRelationalDataTableId = returnRecord.getCurrentReturnLog();
        int i = nonRelationalDataTableMapper.deleteNonRelationalDataTableByNonRelationalId(nonRelationalDataTableId);
        if (i<0){
            return i;
        }
        return returnRecordMapper.deleteReturnRecordById(id);
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
        int i = deleteReturnRecordById(returnRecord.getId());
        if (i<0){
            return i;
        }

        return insertReturnRecord(returnRecord);
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
        List<ReturnRecord> returnRecords = returnRecordMapper.selectPojoSingleTableListById(ids);

        List<String> nonRelationalDataTableIds =new ArrayList<>();
        for (ReturnRecord record:returnRecords){
            nonRelationalDataTableIds.add(record.getCurrentReturnLog());
        }
        String[] dynamicInformationArray = nonRelationalDataTableIds.toArray(new String[nonRelationalDataTableIds.size()]);
        int i = nonRelationalDataTableMapper.deleteNonRelationalDataTableByNonRelationalIds(dynamicInformationArray);
        if (i<0){
            return i;
        }

        return returnRecordMapper.deleteReturnRecordByIds(ids);
    }

    @Override
    public int deleteReturnRecordByDate(String data) {
        String stringData = "create_time < '"+data+"'";
        List<ReturnRecord> returnRecords =returnRecordMapper.seletReturnRecordByDate(stringData);

        List<String> nonRelationalDataTableIds =new ArrayList<>();
        for (ReturnRecord record:returnRecords){
            nonRelationalDataTableIds.add(record.getCurrentReturnLog());
        }
        String[] dynamicInformationArray = nonRelationalDataTableIds.toArray(new String[nonRelationalDataTableIds.size()]);
        int i = nonRelationalDataTableMapper.deleteNonRelationalDataTableByNonRelationalIds(dynamicInformationArray);
        if (i<0){
            return i;
        }

        return returnRecordMapper.deleteReturnRecordByDate(stringData);
    }

    /*删除数据表所有数据*/
    @Override
    public int deleteReturnRecord() {
        List<ReturnRecord> returnRecords = returnRecordMapper.selectReturnRecordAllList();

        List<String> nonRelationalDataTableIds =new ArrayList<>();
        for (ReturnRecord record:returnRecords){
            nonRelationalDataTableIds.add(record.getCurrentReturnLog());
        }
        String[] dynamicInformationArray = nonRelationalDataTableIds.toArray(new String[nonRelationalDataTableIds.size()]);
        int i = nonRelationalDataTableMapper.deleteNonRelationalDataTableByNonRelationalIds(dynamicInformationArray);
        if (i<0){
            return i;
        }

        return returnRecordMapper.deleteReturnRecord();
    }
}
