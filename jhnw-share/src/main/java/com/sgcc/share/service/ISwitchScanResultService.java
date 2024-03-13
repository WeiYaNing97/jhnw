package com.sgcc.share.service;

import com.sgcc.share.domain.SwitchProblemVO;
import com.sgcc.share.domain.SwitchScanResult;

import java.util.List;

/**
 * 交换机扫描结果Service接口
 * 
 * @author ruoyi
 * @date 2022-08-26
 */
public interface ISwitchScanResultService 
{
    /**
     * 查询交换机扫描结果
     * 
     * @param id 交换机扫描结果主键
     * @return 交换机扫描结果
     */
    public SwitchScanResult selectSwitchScanResultById(Long id);

    /**
    * @Description 根据IP查询交换机扫描结果表最新数据
    * @param ip	 交换机IP
     * @return
    */
    SwitchScanResult getTheLatestDataByIP(String ip);

    /**
     * 查询交换机扫描结果列表
     * 
     * @param switchScanResult 交换机扫描结果
     * @return 交换机扫描结果集合
     */
    public List<SwitchScanResult> selectSwitchScanResultList(SwitchScanResult switchScanResult);

    /**
     * 新增交换机扫描结果
     * 
     * @param switchScanResult 交换机扫描结果
     * @return 结果
     */
    public int insertSwitchScanResult(SwitchScanResult switchScanResult);

    /**
     * 修改交换机扫描结果
     * 
     * @param switchScanResult 交换机扫描结果
     * @return 结果
     */
    public int updateSwitchScanResult(SwitchScanResult switchScanResult);

    /**
     * 批量删除交换机扫描结果
     * 
     * @param ids 需要删除的交换机扫描结果主键集合
     * @return 结果
     */
    public int deleteSwitchScanResultByIds(Long[] ids);

    /**
     * 删除交换机扫描结果信息
     * 
     * @param id 交换机扫描结果主键
     * @return 结果
     */
    public int deleteSwitchScanResultById(Long id);

    List<SwitchProblemVO> selectSwitchProblemVOListByIds(Long[] ids);

    List<SwitchProblemVO> selectSwitchScanResultListByIds(Long[] id);

    List<SwitchScanResult> selectSwitchScanResultByIds(Long[] id);

    List<SwitchProblemVO> selectSwitchScanResultListByDataAndUserName(String loginTime, String loginName);
    SwitchProblemVO selectSwitchScanResultListById(Long longId);


    List<SwitchScanResult> selectSwitchScanResultByDataAndUserName(String loginTime, String loginName);

    /*删除数据表所有数据*/
    int deleteSwitchScanResult();

    /*获取交换机问题扫描结果不同时间条数*/
    int selectCountByName(String username);

    List<SwitchScanResult> selectSwitchScanResultListPages(String userName,int number);

    /**
    * @Description 根据IP 更新最新一条 登录信息
    * @author charles
    * @createTime 2024/1/19 16:33
    * @desc
    * @param
     * @return
    */
    int updateLoginInformationByIP(SwitchScanResult pojo);
}
