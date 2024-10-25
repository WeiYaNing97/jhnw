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
import java.util.stream.Collectors;

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

        nonRelationalDataTable.setNonRelationalId(new StringBuffer(new MyUtils().getID( (returnRecord.getId() +"").substring(0,4) , null)));
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
        StringBuffer nonRelationalDataTableId = returnRecord.getCurrentReturnLog();
        int i = nonRelationalDataTableMapper.deleteNonRelationalDataTableByNonRelationalId(nonRelationalDataTableId.toString());
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
            nonRelationalDataTableIds.add(record.getCurrentReturnLog().toString());
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
            nonRelationalDataTableIds.add(record.getCurrentReturnLog().toString());
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
            nonRelationalDataTableIds.add(record.getCurrentReturnLog().toString());
        }
        String[] dynamicInformationArray = nonRelationalDataTableIds.toArray(new String[nonRelationalDataTableIds.size()]);
        int i = nonRelationalDataTableMapper.deleteNonRelationalDataTableByNonRelationalIds(dynamicInformationArray);
        if (i<0){
            return i;
        }

        return returnRecordMapper.deleteReturnRecord();
    }

    @Override
    public int deleteReturnRecordByTime(String data) {
        // 根据时间查询退货记录列表
        List<ReturnRecord> returnRecords = returnRecordMapper.selectReturnRecordByTime(data);
        if (returnRecords.size()==0){
            // 如果查询结果为空，直接返回0
            return 0;
        }

        // 存储非关系型数据表ID的列表
        List<String> nonRelationalDataTableIds =new ArrayList<>();
        for (ReturnRecord record:returnRecords){
            // 将每条退货记录中的当前退货日志ID添加到列表中
            nonRelationalDataTableIds.add(record.getCurrentReturnLog().toString());
        }
        // 将非关系型数据表ID列表转换为数组
        String[] dynamicInformationArray = nonRelationalDataTableIds.toArray(new String[nonRelationalDataTableIds.size()]);

        // 根据非关系型数据表ID数组删除对应的非关系型数据表记录
        int i = nonRelationalDataTableMapper.deleteNonRelationalDataTableByNonRelationalIds(dynamicInformationArray);
        if (i<0){
            // 如果删除失败，返回删除结果
            return i;
        }

        // 将退货记录列表中的ID提取出来
        List<Long> collect = returnRecords.stream().map(ReturnRecord::getId).collect(Collectors.toList());
        // 将ID列表转换为数组
        Long[] ids = collect.toArray(new Long[collect.size()]);

        // 根据ID数组删除退货记录
        return returnRecordMapper.deleteReturnRecordByIds(ids);
    }
}
