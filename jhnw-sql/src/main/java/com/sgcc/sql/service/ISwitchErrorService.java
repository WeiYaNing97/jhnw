package com.sgcc.sql.service;

import java.util.List;
import com.sgcc.sql.domain.SwitchError;

/**
 * 交换机错误Service接口
 * 
 * @author ruoyi
 * @date 2022-08-16
 */
public interface ISwitchErrorService 
{
    /**
     * 查询交换机错误
     * 
     * @param errorId 交换机错误主键
     * @return 交换机错误
     */
    public SwitchError selectSwitchErrorByErrorId(Long errorId);

    /**
     * 查询交换机错误列表
     * 
     * @param switchError 交换机错误
     * @return 交换机错误集合
     */
    public List<SwitchError> selectSwitchErrorList(SwitchError switchError);

    /**
     * 新增交换机错误
     * 
     * @param switchError 交换机错误
     * @return 结果
     */
    public int insertSwitchError(SwitchError switchError);

    /**
     * 修改交换机错误
     * 
     * @param switchError 交换机错误
     * @return 结果
     */
    public int updateSwitchError(SwitchError switchError);

    /**
     * 批量删除交换机错误
     * 
     * @param errorIds 需要删除的交换机错误主键集合
     * @return 结果
     */
    public int deleteSwitchErrorByErrorIds(Long[] errorIds);

    /**
     * 删除交换机错误信息
     * 
     * @param errorId 交换机错误主键
     * @return 结果
     */
    public int deleteSwitchErrorByErrorId(Long errorId);

    /*查询交换机错误列表*/
    List<SwitchError> selectSwitchErrorListByPojo(SwitchError switchError);
}
