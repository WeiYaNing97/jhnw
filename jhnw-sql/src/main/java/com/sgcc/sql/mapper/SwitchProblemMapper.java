package com.sgcc.sql.mapper;

import com.sgcc.sql.domain.SwitchProblem;
import com.sgcc.sql.domain.SwitchProblemVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 交换机问题Mapper接口
 * 
 * @author ruoyi
 * @date 2021-12-28
 */
public interface SwitchProblemMapper 
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
     * 删除交换机问题
     * 
     * @param id 交换机问题主键
     * @return 结果
     */
    public int deleteSwitchProblemById(Long id);

    /**
     * 批量删除交换机问题
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSwitchProblemByIds(Long[] ids);

    List<SwitchProblemVO> selectUnresolvedProblemInformationByData(@Param("currentTime")String currentTime, @Param("userName")String userName,@Param("phonenumber")String phonenumber);

    SwitchProblem selectSwitchProblemByValueId(@Param("valueId") Long valueId);
}
