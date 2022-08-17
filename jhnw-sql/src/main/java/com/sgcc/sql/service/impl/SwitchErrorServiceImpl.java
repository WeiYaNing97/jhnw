package com.sgcc.sql.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.sql.mapper.SwitchErrorMapper;
import com.sgcc.sql.domain.SwitchError;
import com.sgcc.sql.service.ISwitchErrorService;

/**
 * 交换机错误Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-08-16
 */
@Service
public class SwitchErrorServiceImpl implements ISwitchErrorService 
{
    @Autowired
    private SwitchErrorMapper switchErrorMapper;

    /**
     * 查询交换机错误
     * 
     * @param errorId 交换机错误主键
     * @return 交换机错误
     */
    @Override
    public SwitchError selectSwitchErrorByErrorId(Long errorId)
    {
        return switchErrorMapper.selectSwitchErrorByErrorId(errorId);
    }

    /**
     * 查询交换机错误列表
     * 
     * @param switchError 交换机错误
     * @return 交换机错误
     */
    @Override
    public List<SwitchError> selectSwitchErrorList(SwitchError switchError)
    {
        return switchErrorMapper.selectSwitchErrorList(switchError);
    }

    /**
     * 新增交换机错误
     * 
     * @param switchError 交换机错误
     * @return 结果
     */
    @Override
    public int insertSwitchError(SwitchError switchError)
    {
        return switchErrorMapper.insertSwitchError(switchError);
    }

    /**
     * 修改交换机错误
     * 
     * @param switchError 交换机错误
     * @return 结果
     */
    @Override
    public int updateSwitchError(SwitchError switchError)
    {
        return switchErrorMapper.updateSwitchError(switchError);
    }

    /**
     * 批量删除交换机错误
     * 
     * @param errorIds 需要删除的交换机错误主键
     * @return 结果
     */
    @Override
    public int deleteSwitchErrorByErrorIds(Long[] errorIds)
    {
        return switchErrorMapper.deleteSwitchErrorByErrorIds(errorIds);
    }

    /**
     * 删除交换机错误信息
     * 
     * @param errorId 交换机错误主键
     * @return 结果
     */
    @Override
    public int deleteSwitchErrorByErrorId(Long errorId)
    {
        return switchErrorMapper.deleteSwitchErrorByErrorId(errorId);
    }

    /*查询交换机错误列表*/
    @Override
    public List<SwitchError> selectSwitchErrorListByPojo(SwitchError switchError) {
        List<SwitchError> switchErrors = switchErrorMapper.selectSwitchErrorListByPojo(switchError);
        if (switchErrors == null){
            return null;
        }
        return switchErrors;
    }
}
