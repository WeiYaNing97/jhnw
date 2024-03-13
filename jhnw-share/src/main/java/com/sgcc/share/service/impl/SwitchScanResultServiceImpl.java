package com.sgcc.share.service.impl;

import com.sgcc.share.domain.SwitchProblemVO;
import com.sgcc.share.domain.SwitchScanResult;
import com.sgcc.share.mapper.SwitchScanResultMapper;
import com.sgcc.share.service.ISwitchScanResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * @Description 根据IP查询交换机扫描结果表最新数据
     * @param ip	 交换机IP
     * @return
     */
    @Override
    public SwitchScanResult getTheLatestDataByIP(String ip) {
        return switchScanResultMapper.getTheLatestDataByIP(ip);
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
        int i = switchScanResultMapper.insertSwitchScanResult(switchScanResult);
        if (i>0){
            return switchScanResult.getId().intValue();
        }else {
            return i;
        }
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

    @Override
    public int updateLoginInformationByIP(SwitchScanResult pojo) {
        return switchScanResultMapper.updateLoginInformationByIP(pojo);
    }
}
