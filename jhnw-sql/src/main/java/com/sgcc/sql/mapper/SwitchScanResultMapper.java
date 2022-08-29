package com.sgcc.sql.mapper;

import java.util.List;

import com.sgcc.sql.domain.SwitchProblemVO;
import com.sgcc.sql.domain.SwitchScanResult;
import org.apache.ibatis.annotations.Param;

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

    List<SwitchProblemVO> selectSwitchProblemVOListByName(String userName);

    List<SwitchProblemVO> selectSwitchScanResultListByIds(Long[] id);

    List<SwitchScanResult> selectSwitchScanResultByIds(Long[] ids);

    List<SwitchProblemVO> selectSwitchScanResultListByDataAndUserName(@Param("currentTime") String loginTime,@Param("userName") String loginName);

    List<SwitchScanResult> selectSwitchScanResultByDataAndUserName(@Param("currentTime") String loginTime,@Param("userName") String loginName);
}
