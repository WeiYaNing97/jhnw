package com.sgcc.share.mapper;

import com.sgcc.share.domain.SwitchProblemVO;
import com.sgcc.share.domain.SwitchScanResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 交换机扫描结果Mapper接口
 * 
 * @author ruoyi
 * @date 2022-08-26
 */
public interface SwitchScanResultMapper 
{
    /**
     * 查询交换机扫描结果
     * 
     * @param id 交换机扫描结果主键
     * @return 交换机扫描结果
     */
    public SwitchScanResult selectSwitchScanResultById(Long id);

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
     * 删除交换机扫描结果
     * 
     * @param id 交换机扫描结果主键
     * @return 结果
     */
    public int deleteSwitchScanResultById(Long id);

    /**
     * 批量删除交换机扫描结果
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSwitchScanResultByIds(Long[] ids);

    List<SwitchProblemVO> selectSwitchProblemVOListByIds(Long[] ids);

    List<SwitchProblemVO> selectSwitchScanResultListByIds(Long[] id);

    List<SwitchScanResult> selectSwitchScanResultByIds(Long[] ids);

    List<SwitchProblemVO> selectSwitchScanResultListByDataAndUserName(@Param("currentTime") String loginTime,@Param("userName") String loginName);
    SwitchProblemVO selectSwitchScanResultListById(@Param("id") Long longId);


    List<SwitchScanResult> selectSwitchScanResultByDataAndUserName(@Param("currentTime") String loginTime,@Param("userName") String loginName);

    /*删除数据表所有数据*/
    int deleteSwitchScanResult();

    /*获取交换机问题扫描结果不同时间条数*/
    int selectCountByName(@Param("username") String username);

    List<SwitchScanResult> selectSwitchScanResultListPages(@Param("userName") String userName,@Param("number") int number);

    int updateLoginInformationByIP(SwitchScanResult pojo);
}
