package com.sgcc.sql.service;

import com.sgcc.share.domain.SwitchProblemVO;
import com.sgcc.sql.domain.SwitchProblem;

import java.util.Date;
import java.util.List;

/**
 * 交换机问题Service接口
 * 
 * @author ruoyi
 * @date 2021-12-28
 */
public interface ISwitchProblemService 
{
    /**
     * 查询交换机问题
     * 
     * @param id 交换机问题主键
     * @return 交换机问题
     */
    public SwitchProblem selectSwitchProblemById(Long id);

    /**
     * 查询交换机问题列表
     * 
     * @param switchProblem 交换机问题
     * @return 交换机问题集合
     */
    public List<SwitchProblem> selectSwitchProblemList(SwitchProblem switchProblem);

    /**
     * 新增交换机问题
     * 
     * @param switchProblem 交换机问题
     * @return 结果
     */
    public int insertSwitchProblem(SwitchProblem switchProblem);

    /**
     * 修改交换机问题
     * 
     * @param switchProblem 交换机问题
     * @return 结果
     */
    public int updateSwitchProblem(SwitchProblem switchProblem);

    /**
     * 批量删除交换机问题
     * 
     * @param ids 需要删除的交换机问题主键集合
     * @return 结果
     */
    public int deleteSwitchProblemByIds(Long[] ids);

    /**
     * 删除交换机问题信息
     * 
     * @param id 交换机问题主键
     * @return 结果
     */
    public int deleteSwitchProblemById(Long id);

    SwitchProblem selectSwitchProblemByValueId(Long valueId);

    //根据名字 和 扫描时间
    List<SwitchProblemVO> selectUnresolvedProblemInformationByDataAndUserName(String currentTime, String userName);
    //根据 问题ID List
    List<SwitchProblemVO> selectUnresolvedProblemInformationByIds(Long[] id);

    List<SwitchProblem> selectSwitchProblemByDate(String data);
    /*根据ID集合 查询所有  SwitchProblem 数据*/
    List<SwitchProblem> selectPojoByIds(Long[] ids);
}
