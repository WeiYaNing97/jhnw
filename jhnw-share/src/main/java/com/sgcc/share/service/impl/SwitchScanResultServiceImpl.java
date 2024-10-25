package com.sgcc.share.service.impl;

import com.sgcc.share.domain.*;
import com.sgcc.share.mapper.NonRelationalDataTableMapper;
import com.sgcc.share.mapper.SwitchScanResultMapper;
import com.sgcc.share.service.ISwitchScanResultService;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 交换机扫描结果Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-08-26
 */
@Service
public class SwitchScanResultServiceImpl implements ISwitchScanResultService
{
    @Autowired
    private SwitchScanResultMapper switchScanResultMapper;
    @Autowired
    private NonRelationalDataTableMapper nonRelationalDataTableMapper;

    /**
     * 查询交换机扫描结果
     * 
     * @param id 交换机扫描结果主键
     * @return 交换机扫描结果
     */
    @Override
    public SwitchScanResult selectSwitchScanResultById(Long id)
    {
        SwitchScanResult switchScanResult = switchScanResultMapper.selectSwitchScanResultById(id);

        if ( switchScanResult.getPointer() == 1 ){
            NonRelationalDataTable nonRelationalDataTable = nonRelationalDataTableMapper.selectNonRelationalDataTableByNonRelationalId(switchScanResult.getDynamicInformation().toString());
            if ( nonRelationalDataTable != null){
                switchScanResult.setDynamicInformation(nonRelationalDataTable.getNonRelationalData());
            }
        }

        return switchScanResult;
    }

    /**
     * @Description 根据IP查询交换机扫描结果表最新数据
     * @param ip	 交换机IP
     * @return
     */
    @Override
    public SwitchScanResult getTheLatestDataByIP(String ip) {

        /* 根据IP查询交换机扫描结果表最新数据 */
        SwitchScanResult theLatestDataByIP = switchScanResultMapper.getTheLatestDataByIP(ip);
        /* 如果查询结果为 null 则直接返回 null*/
        if (theLatestDataByIP == null){
            return theLatestDataByIP;
        }

        /* 如果 参数长度为23 则可能是非关系型数据表ID，则查询数据*/
        if ( theLatestDataByIP.getPointer() == 1 ){
            NonRelationalDataTable nonRelationalDataTable = nonRelationalDataTableMapper.selectNonRelationalDataTableByNonRelationalId(theLatestDataByIP.getDynamicInformation().toString());
            if ( nonRelationalDataTable != null){
                theLatestDataByIP.setDynamicInformation(nonRelationalDataTable.getNonRelationalData());
            }
        }

        return theLatestDataByIP;
    }

    /**
     * 查询交换机扫描结果列表
     * 
     * @param switchScanResult 交换机扫描结果
     * @return 交换机扫描结果
     */
    @Override
    public List<SwitchScanResult> selectSwitchScanResultList(SwitchScanResult switchScanResult)
    {
        List<SwitchScanResult> switchScanResults = switchScanResultMapper.selectSwitchScanResultList(switchScanResult);
        for (SwitchScanResult pojo:switchScanResults){

            if ( switchScanResult.getPointer() == 1 ){
                NonRelationalDataTable nonRelationalDataTable = nonRelationalDataTableMapper.selectNonRelationalDataTableByNonRelationalId(pojo.getDynamicInformation().toString());
                if ( nonRelationalDataTable != null){
                    pojo.setDynamicInformation(nonRelationalDataTable.getNonRelationalData());
                }
            }

        }
        return switchScanResults;
    }

    /**
     * 新增交换机扫描结果
     * 
     * @param switchScanResult 交换机扫描结果
     * @return 结果
     */
    @Override
    public int insertSwitchScanResult(SwitchScanResult switchScanResult)
    {
        if (switchScanResult.getDynamicInformation().length() >= 255){
            NonRelationalDataTable nonRelationalDataTable = new NonRelationalDataTable();
            MyUtils myUtils = new MyUtils();

            String keyword = (String) CustomConfigurationUtil.getValue("configuration.problemCode."+switchScanResult.getTypeProblem(), Constant.getProfileInformation());

            nonRelationalDataTable.setNonRelationalId(new StringBuffer(myUtils.getID( keyword , null)));  /*  (switchScanResult.getId()+"").substring(0,4)  */
            nonRelationalDataTable.setNonRelationalData(switchScanResult.getDynamicInformation());
            nonRelationalDataTableMapper.insertNonRelationalDataTable(nonRelationalDataTable);

            switchScanResult.setPointer(1);/* 是指针 */
            switchScanResult.setDynamicInformation(nonRelationalDataTable.getNonRelationalId());
        }

        int i = switchScanResultMapper.insertSwitchScanResult(switchScanResult);
        if (i>0){
            return switchScanResult.getId().intValue();
        }else {
            return i;
        }
    }

    /**
     * 删除交换机扫描结果信息
     * 
     * @param id 交换机扫描结果主键
     * @return 结果
     */
    @Override
    public int deleteSwitchScanResultById(Long id)
    {
        SwitchScanResult switchScanResult = switchScanResultMapper.selectSwitchScanResultById(id);

        if ( switchScanResult.getPointer() == 1 ){
            NonRelationalDataTable nonRelationalDataTable = nonRelationalDataTableMapper.selectNonRelationalDataTableByNonRelationalId(switchScanResult.getDynamicInformation().toString());
            if ( nonRelationalDataTable != null){
                int i = nonRelationalDataTableMapper.deleteNonRelationalDataTableByNonRelationalId(switchScanResult.getDynamicInformation().toString());
                if (i>0){
                    return switchScanResultMapper.deleteSwitchScanResultById(id);
                }
            }
        }

        return switchScanResultMapper.deleteSwitchScanResultById(id);
    }

    @Override
    public List<SwitchProblemVO> selectSwitchProblemVOListByIds(Long[] ids) {
        List<SwitchProblemVO> switchProblemVOS = switchScanResultMapper.selectSwitchProblemVOListByIds(ids);

        for (SwitchProblemVO switchProblemVO:switchProblemVOS){
            List<SwitchProblemCO> switchProblemCOList = switchProblemVO.getSwitchProblemCOList();
            for (SwitchProblemCO switchProblemCO:switchProblemCOList){

                if ( switchProblemCO.getPointer() == 1 ){
                    NonRelationalDataTable nonRelationalDataTable = nonRelationalDataTableMapper.selectNonRelationalDataTableByNonRelationalId(switchProblemCO.getDynamicInformation());
                    if ( nonRelationalDataTable != null){
                        switchProblemCO.setDynamicInformation(nonRelationalDataTable.getNonRelationalData().toString());
                    }
                }
            }
        }

        return switchProblemVOS;
    }

    @Override
    public List<SwitchProblemVO> selectSwitchScanResultListByIds(Long[] id) {
        List<SwitchProblemVO> switchProblemVOS = switchScanResultMapper.selectSwitchScanResultListByIds(id);

        for (SwitchProblemVO switchProblemVO:switchProblemVOS){
            List<SwitchProblemCO> switchProblemCOList = switchProblemVO.getSwitchProblemCOList();
            for (SwitchProblemCO switchProblemCO:switchProblemCOList){

                if ( switchProblemCO.getPointer() == 1 ){
                    NonRelationalDataTable nonRelationalDataTable = nonRelationalDataTableMapper.selectNonRelationalDataTableByNonRelationalId(switchProblemCO.getDynamicInformation());
                    if ( nonRelationalDataTable != null){
                        switchProblemCO.setDynamicInformation(nonRelationalDataTable.getNonRelationalData().toString());
                    }
                }
            }
        }

        return switchProblemVOS;
    }

    @Override
    public List<SwitchScanResult> selectSwitchScanResultByIds(Long[] ids) {
        List<SwitchScanResult> switchScanResults = switchScanResultMapper.selectSwitchScanResultByIds(ids);
        for (SwitchScanResult pojo:switchScanResults){

            if ( pojo.getPointer() == 1 ){
                NonRelationalDataTable nonRelationalDataTable = nonRelationalDataTableMapper.selectNonRelationalDataTableByNonRelationalId(pojo.getDynamicInformation().toString());
                if ( nonRelationalDataTable != null){
                    pojo.setDynamicInformation(nonRelationalDataTable.getNonRelationalData());
                }
            }
        }
        return switchScanResults;
    }

    @Override
    public List<SwitchProblemVO> selectSwitchScanResultListByDataAndUserName(String loginTime, String loginName) {
        List<SwitchProblemVO> switchProblemVOS = switchScanResultMapper.selectSwitchScanResultListByDataAndUserName(loginTime, loginName);

        for (SwitchProblemVO switchProblemVO:switchProblemVOS){
            List<SwitchProblemCO> switchProblemCOList = switchProblemVO.getSwitchProblemCOList();
            for (SwitchProblemCO switchProblemCO:switchProblemCOList){

                if ( switchProblemCO.getPointer() == 1 ){
                    NonRelationalDataTable nonRelationalDataTable = nonRelationalDataTableMapper.selectNonRelationalDataTableByNonRelationalId(switchProblemCO.getDynamicInformation());
                    if ( nonRelationalDataTable != null){
                        switchProblemCO.setDynamicInformation(nonRelationalDataTable.getNonRelationalData().toString());
                    }
                }
            }
        }

        return switchProblemVOS;
    }

    @Override
    public SwitchProblemVO selectSwitchScanResultListById(Long longId) {
        SwitchProblemVO switchProblemVO = switchScanResultMapper.selectSwitchScanResultListById(longId);
        List<SwitchProblemCO> switchProblemCOList = switchProblemVO.getSwitchProblemCOList();
        for (SwitchProblemCO switchProblemCO:switchProblemCOList){

            if ( switchProblemCO.getPointer() == 1 ){
                NonRelationalDataTable nonRelationalDataTable = nonRelationalDataTableMapper.selectNonRelationalDataTableByNonRelationalId(switchProblemCO.getDynamicInformation());
                if ( nonRelationalDataTable != null){
                    switchProblemCO.setDynamicInformation(nonRelationalDataTable.getNonRelationalData().toString());
                }
            }
        }
        return switchProblemVO;
    }

    @Override
    public List<SwitchScanResult> selectSwitchScanResultByDataAndUserName(String loginTime, String loginName) {
        List<SwitchScanResult> switchScanResults = switchScanResultMapper.selectSwitchScanResultByDataAndUserName(loginTime, loginName);
        for (SwitchScanResult pojo:switchScanResults){

            if ( pojo.getPointer() == 1 ){
                NonRelationalDataTable nonRelationalDataTable = nonRelationalDataTableMapper.selectNonRelationalDataTableByNonRelationalId(pojo.getDynamicInformation().toString());
                if ( nonRelationalDataTable != null){
                    pojo.setDynamicInformation(nonRelationalDataTable.getNonRelationalData());
                }
            }
        }
        return switchScanResults;
    }

    /*删除数据表所有数据*/
    @Override
    public int deleteSwitchScanResult() {
        List<String> dynamicInformationList = switchScanResultMapper.selectDynamicInformation();
        String[] dynamicInformationArray = dynamicInformationList.toArray(new String[dynamicInformationList.size()]);
        int i = nonRelationalDataTableMapper.deleteNonRelationalDataTableByNonRelationalIds(dynamicInformationArray);
        if (i>=0){
            return switchScanResultMapper.deleteSwitchScanResult();
        }
        return i;
    }

    /*获取交换机问题扫描结果不同时间条数*/
    @Override
    public int selectCountByName(String username) {
        return switchScanResultMapper.selectCountByName(username);
    }

    @Override
    public List<SwitchScanResult> selectSwitchScanResultListPages(String userName,int number) {
        List<SwitchScanResult> switchScanResults = switchScanResultMapper.selectSwitchScanResultListPages(userName, number);
        for (SwitchScanResult pojo:switchScanResults){

            if ( pojo.getPointer() == 1 ){
                NonRelationalDataTable nonRelationalDataTable = nonRelationalDataTableMapper.selectNonRelationalDataTableByNonRelationalId(pojo.getDynamicInformation().toString());
                if ( nonRelationalDataTable != null){
                    pojo.setDynamicInformation(nonRelationalDataTable.getNonRelationalData());
                }
            }
        }
        return switchScanResults;
    }

    /**
    * @Description 根据交换机 IP 修改交换机名称、密码、配置密码
     *
     * 不涉及非关系型数据表查询问题
     *
     * <if test="switchName != null and switchName != ''">switch_name = #{switchName},</if>
     *             <if test="switchPassword != null and switchPassword != ''">switch_password = #{switchPassword},</if>
     *             <if test="configureCiphers != null and configureCiphers != ''">configureCiphers = #{configureCiphers},</if>
    * @author charles
    * @createTime 2024/5/23 9:22
    * @desc
    * @param null
     * @return
    */
    @Override
    public int updateLoginInformationByIP(SwitchScanResult pojo) {
        return switchScanResultMapper.updateLoginInformationByIP(pojo);
    }



    /**
     * 批量删除交换机扫描结果
     *
     * @param ids 需要删除的交换机扫描结果主键
     * @return 结果
     */
    @Override
    public int deleteSwitchScanResultByIds(Long[] ids)
    {
        List<SwitchScanResult> switchScanResults = switchScanResultMapper.selectSwitchScanResultByIds(ids);

        List<String> dynamicInformationList = new ArrayList<>();
        for (SwitchScanResult pojo:switchScanResults){
            dynamicInformationList.add(pojo.getDynamicInformation().toString());
        }

        String[] dynamicInformationArray = dynamicInformationList.toArray(new String[dynamicInformationList.size()]);

        int i = nonRelationalDataTableMapper.deleteNonRelationalDataTableByNonRelationalIds(dynamicInformationArray);

        if (i>=0){
            return switchScanResultMapper.deleteSwitchScanResultByIds(ids);
        }
        return i;
    }


    /**
    * @Description 根据IP 范式名称 和 开始时间、结束时间 删除数据
    * @author charles
    * @createTime 2024/5/14 15:57
    * @desc
    * @param ip
     * @param temProName
     * @param startTime
     * @param endTime
     * @return
    */
    @Override
    public int deleteSwitchScanResultByIPAndTime(String ip, String temProName, String startTime, String endTime) {
        List<SwitchScanResult> pojoList = switchScanResultMapper.selectSwitchScanResultByIPAndTime( ip,  temProName,  startTime,  endTime);
        List<Long> ids = new ArrayList<>();
        for (SwitchScanResult switchScanResult:pojoList){
            ids.add(switchScanResult.getId());
        }
        Long[] iPArray = ids.toArray(new Long[ids.size()]);
        return deleteSwitchScanResultByIds(iPArray);
    }


    /**
     * 修改交换机扫描结果
     *
     * @param switchScanResult 交换机扫描结果
     * @return 结果
     */
    @Override
    public int updateSwitchScanResult(SwitchScanResult switchScanResult)
    {
        // 根据交换机扫描结果的ID删除原有记录
        int i = deleteSwitchScanResultById(switchScanResult.getId());
        if (i>0){
            // 插入新的交换机扫描结果
            int i1 = insertSwitchScanResult(switchScanResult);
            // 返回插入结果
            return i1;
        }
        // 返回删除结果
        return i;
    }


    /**
     * 根据时间删除交换机扫描结果
     *
     * @param data 时间数据
     * @return 返回删除成功的记录数
     */
    @Override
    public int deleteSwitchScanResultByTime(String data) {
        // 根据时间查询交换机扫描结果列表
        List<SwitchScanResult> pojoList = switchScanResultMapper.selectSwitchScanResultByTime(data);
        if (pojoList.size() == 0){
            // 如果列表为空，则返回0
            return 0;
        }

        // 创建一个用于存储交换机扫描结果ID的列表
        List<Long> ids = new ArrayList<>();
        // 遍历交换机扫描结果列表，将ID添加到列表中
        for (SwitchScanResult switchScanResult:pojoList){
            ids.add(switchScanResult.getId());
        }
        // 将ID列表转换为ID数组
        Long[] iPArray = ids.toArray(new Long[ids.size()]);
        // 根据ID数组删除交换机扫描结果，并返回删除成功的记录数
        return deleteSwitchScanResultByIds(iPArray);
    }
}
