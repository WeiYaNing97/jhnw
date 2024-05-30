package com.sgcc.share.service.impl;


import com.sgcc.share.domain.Constant;
import com.sgcc.share.domain.SwitchError;
import com.sgcc.share.mapper.SwitchErrorMapper;
import com.sgcc.share.service.ISwitchErrorService;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.FunctionalMethods;
import com.sgcc.share.util.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    public SwitchError selectSwitchErrorByErrorId(String errorId)
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
        List<SwitchError> pojo = new ArrayList<>();
        pojo.addAll(switchErrorMapper.selectSwitchErrorList(switchError));

        String equivalence = FunctionalMethods.getEquivalence(switchError.getBrand());
        if (equivalence!=null){
            switchError.setBrand(equivalence);
            pojo.addAll(switchErrorMapper.selectSwitchErrorList(switchError));
        }

        return pojo;
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

        String id = MyUtils.getID("CMER", null);
        switchError.setErrorId(id);

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
    public int deleteSwitchErrorByErrorIds(String[] errorIds)
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
    public int deleteSwitchErrorByErrorId(String errorId)
    {
        return switchErrorMapper.deleteSwitchErrorByErrorId(errorId);
    }

    /*查询交换机错误列表*/
    @Override
    public List<SwitchError> selectSwitchErrorListByPojo(SwitchError switchError) {

        List<SwitchError> pojo = new ArrayList<>();
        pojo.addAll(switchErrorMapper.selectSwitchErrorListByPojo(switchError));

        String equivalence = FunctionalMethods.getEquivalence(switchError.getBrand());
        if (equivalence!=null){
            switchError.setBrand(equivalence);
            pojo.addAll(switchErrorMapper.selectSwitchErrorListByPojo(switchError));
        }

        return pojo;

    }

    /*删除数据表所有数据*/
    @Override
    public int deleteSwitchErrorByError() {
        return switchErrorMapper.deleteSwitchErrorByError();
    }
}
