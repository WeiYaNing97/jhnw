package com.sgcc.sql.service.impl;

import com.sgcc.share.domain.SwitchProblemVO;
import com.sgcc.sql.domain.SwitchProblem;
import com.sgcc.sql.mapper.SwitchProblemMapper;
import com.sgcc.sql.service.ISwitchProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 交换机问题Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-12-28
 */
@Service
public class SwitchProblemServiceImpl implements ISwitchProblemService 
{
    @Autowired
    private SwitchProblemMapper switchProblemMapper;

    /**
     * 查询交换机问题
     * 
     * @param id 交换机问题主键
     * @return 交换机问题
     */
    @Override
    public SwitchProblem selectSwitchProblemById(Long id)
    {
        return switchProblemMapper.selectSwitchProblemById(id);
    }

    /**
     * 查询交换机问题列表
     * 
     * @param switchProblem 交换机问题
     * @return 交换机问题
     */
    @Override
    public List<SwitchProblem> selectSwitchProblemList(SwitchProblem switchProblem)
    {
        return switchProblemMapper.selectSwitchProblemList(switchProblem);
    }

    /**
     * 新增交换机问题
     * 
     * @param switchProblem 交换机问题
     * @return 结果
     */
    @Override
    public int insertSwitchProblem(SwitchProblem switchProblem)
    {
        return switchProblemMapper.insertSwitchProblem(switchProblem);
    }

    /**
     * 修改交换机问题
     * 
     * @param switchProblem 交换机问题
     * @return 结果
     */
    @Override
    public int updateSwitchProblem(SwitchProblem switchProblem)
    {
        return switchProblemMapper.updateSwitchProblem(switchProblem);
    }

    /**
     * 批量删除交换机问题
     * 
     * @param ids 需要删除的交换机问题主键
     * @return 结果
     */
    @Override
    public int deleteSwitchProblemByIds(Long[] ids)
    {
        return switchProblemMapper.deleteSwitchProblemByIds(ids);
    }

    /**
     * 删除交换机问题信息
     * 
     * @param id 交换机问题主键
     * @return 结果
     */
    @Override
    public int deleteSwitchProblemById(Long id)
    {
        return switchProblemMapper.deleteSwitchProblemById(id);
    }

    @Override
    public List<SwitchProblemVO> selectUnresolvedProblemInformationByDataAndUserName(String currentTime,String userName) {
        return switchProblemMapper.selectUnresolvedProblemInformationByDataAndUserName(currentTime,userName);
    }

    @Override
    public List<SwitchProblemVO> selectUnresolvedProblemInformationByIds(Long[] ids) {

        String sqlString = "where id in (";
        for (Long id:ids){
            sqlString +=  id +",";
        }
        sqlString = sqlString.substring(0,sqlString.length()-1);
        sqlString = sqlString +")";

        return switchProblemMapper.selectUnresolvedProblemInformationByIds(sqlString);
    }

    @Override
    public List<SwitchProblem> selectSwitchProblemByDate(String data) {
        String sqlString = "create_time < '"+data+"'";
        return switchProblemMapper.selectSwitchProblemByDate(sqlString);
    }

    @Override
    public SwitchProblem selectSwitchProblemByValueId(Long valueId) {
        return switchProblemMapper.selectSwitchProblemByValueId(valueId);
    }

    /*根据ID集合 查询所有  SwitchProblem 数据*/
    @Override
    public List<SwitchProblem> selectPojoByIds(Long[] ids) {
        return switchProblemMapper.selectPojoByIds(ids);
    }
}
