package com.sgcc.sql.service.impl;

import java.util.List;
import com.sgcc.common.utils.DateUtils;
import com.sgcc.sql.domain.SwitchProblemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.sql.mapper.SwitchScanResultMapper;
import com.sgcc.sql.domain.SwitchScanResult;
import com.sgcc.sql.service.ISwitchScanResultService;

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

    /**
     * 查询交换机扫描结果
     * 
     * @param id 交换机扫描结果主键
     * @return 交换机扫描结果
     */
    @Override
    public SwitchScanResult selectSwitchScanResultById(Long id)
    {
        return switchScanResultMapper.selectSwitchScanResultById(id);
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
        return switchScanResultMapper.selectSwitchScanResultList(switchScanResult);
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
        return switchScanResultMapper.insertSwitchScanResult(switchScanResult);
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
        return switchScanResultMapper.updateSwitchScanResult(switchScanResult);
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
        return switchScanResultMapper.deleteSwitchScanResultByIds(ids);
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
        return switchScanResultMapper.deleteSwitchScanResultById(id);
    }

    @Override
    public List<SwitchProblemVO> selectSwitchProblemVOListByIds(Long[] ids) {
        return switchScanResultMapper.selectSwitchProblemVOListByIds(ids);
    }

    @Override
    public List<SwitchProblemVO> selectSwitchScanResultListByIds(Long[] id) {
        return switchScanResultMapper.selectSwitchScanResultListByIds(id);
    }

    @Override
    public List<SwitchScanResult> selectSwitchScanResultByIds(Long[] ids) {
        return switchScanResultMapper.selectSwitchScanResultByIds(ids);
    }

    @Override
    public List<SwitchProblemVO> selectSwitchScanResultListByDataAndUserName(String loginTime, String loginName) {
        return switchScanResultMapper.selectSwitchScanResultListByDataAndUserName(loginTime,loginName);
    }

    @Override
    public SwitchProblemVO selectSwitchScanResultListById(Long longId) {
        return switchScanResultMapper.selectSwitchScanResultListById(longId);
    }

    @Override
    public List<SwitchScanResult> selectSwitchScanResultByDataAndUserName(String loginTime, String loginName) {
        return switchScanResultMapper.selectSwitchScanResultByDataAndUserName(loginTime,loginName);
    }

    /*删除数据表所有数据*/
    @Override
    public int deleteSwitchScanResult() {
        return switchScanResultMapper.deleteSwitchScanResult();
    }

    /*获取交换机问题扫描结果不同时间条数*/
    @Override
    public int selectCountByName(String username) {
        return switchScanResultMapper.selectCountByName(username);
    }

    @Override
    public List<SwitchScanResult> selectSwitchScanResultListPages(String userName,int number) {
        return switchScanResultMapper.selectSwitchScanResultListPages(userName,number);
    }
}
