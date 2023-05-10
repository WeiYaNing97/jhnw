package com.sgcc.advanced.service.impl;

import java.util.List;

import com.sgcc.advanced.domain.ErrorRate;
import com.sgcc.advanced.mapper.ErrorRateMapper;
import com.sgcc.advanced.service.IErrorRateService;
import com.sgcc.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 误码率Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-05-09
 */
@Service
public class ErrorRateServiceImpl implements IErrorRateService
{
    @Autowired
    private ErrorRateMapper errorRateMapper;

    /**
     * 查询误码率
     * 
     * @param id 误码率主键
     * @return 误码率
     */
    @Override
    public ErrorRate selectErrorRateById(Long id)
    {
        return errorRateMapper.selectErrorRateById(id);
    }

    /**
     * 查询误码率列表
     * 
     * @param errorRate 误码率
     * @return 误码率
     */
    @Override
    public List<ErrorRate> selectErrorRateList(ErrorRate errorRate)
    {
        return errorRateMapper.selectErrorRateList(errorRate);
    }

    /**
     * 新增误码率
     * 
     * @param errorRate 误码率
     * @return 结果
     */
    @Override
    public int insertErrorRate(ErrorRate errorRate)
    {
        errorRate.setCreateTime(DateUtils.getNowDate());
        return errorRateMapper.insertErrorRate(errorRate);
    }

    /**
     * 修改误码率
     * 
     * @param errorRate 误码率
     * @return 结果
     */
    @Override
    public int updateErrorRate(ErrorRate errorRate)
    {
        errorRate.setUpdateTime(DateUtils.getNowDate());
        return errorRateMapper.updateErrorRate(errorRate);
    }

    /**
     * 批量删除误码率
     * 
     * @param ids 需要删除的误码率主键
     * @return 结果
     */
    @Override
    public int deleteErrorRateByIds(Long[] ids)
    {
        return errorRateMapper.deleteErrorRateByIds(ids);
    }

    /**
     * 删除误码率信息
     * 
     * @param id 误码率主键
     * @return 结果
     */
    @Override
    public int deleteErrorRateById(Long id)
    {
        return errorRateMapper.deleteErrorRateById(id);
    }
}
