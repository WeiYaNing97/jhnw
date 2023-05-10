package com.sgcc.sql.mapper;

import com.sgcc.share.domain.SwitchProblemVO;
import com.sgcc.sql.domain.SwitchProblem;
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

    List<SwitchProblemVO> selectUnresolvedProblemInformationByDataAndUserName(@Param("currentTime")String currentTime, @Param("userName")String userName);

    SwitchProblem selectSwitchProblemByValueId(@Param("valueId") Long valueId);

    List<SwitchProblemVO> selectUnresolvedProblemInformationByIds(@Param("ids") String ids);

    List<SwitchProblem> selectSwitchProblemByDate(@Param("sqlString") String sqlString);

    /*根据ID集合 查询所有  SwitchProblem 数据*/
    List<SwitchProblem> selectPojoByIds(Long[] ids);
}
