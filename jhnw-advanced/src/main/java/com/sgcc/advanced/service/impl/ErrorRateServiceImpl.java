package com.sgcc.advanced.service.impl;

import java.util.List;

import com.sgcc.advanced.domain.ErrorRate;
import com.sgcc.advanced.mapper.ErrorRateMapper;
import com.sgcc.advanced.service.IErrorRateService;
import com.sgcc.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 错误包 Service业务层处理
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
     * 查询 错误包
     * 
     * @param id 错误包 主键
     * @return 错误包
     */
    @Override
    public ErrorRate selectErrorRateById(Long id)
    {
        return errorRateMapper.selectErrorRateById(id);
    }

    /**
     * 查询 错误包 列表
     * 
     * @param errorRate 错误包
     * @return 错误包
     */
    @Override
    public List<ErrorRate> selectErrorRateList(ErrorRate errorRate)
    {
        return errorRateMapper.selectErrorRateList(errorRate);
    }

    /**
     * 新增 错误包
     * 
     * @param errorRate 错误包
     * @return 结果
     */
    @Override
    public int insertErrorRate(ErrorRate errorRate)
    {
        errorRate.setCreateTime(DateUtils.getNowDate());
        return errorRateMapper.insertErrorRate(errorRate);
    }

    /**
     * 修改 错误包
     * 
     * @param errorRate 错误包
     * @return 结果
     */
    @Override
    public int updateErrorRate(ErrorRate errorRate)
    {
        errorRate.setUpdateTime(DateUtils.getNowDate());
        return errorRateMapper.updateErrorRate(errorRate);
    }

    /**
     * 批量删除 错误包
     * 
     * @param ids 需要删除的 错误包 主键
     * @return 结果
     */
    @Override
    public int deleteErrorRateByIds(Long[] ids)
    {
        return errorRateMapper.deleteErrorRateByIds(ids);
    }

    /**
     * 删除 错误包 信息
     * 
     * @param id 错误包 主键
     * @return 结果
     */
    @Override
    public int deleteErrorRateById(Long id)
    {
        return errorRateMapper.deleteErrorRateById(id);
    }

    /**
    * @Description 根据IP获取数据集合
    * @author charles
    * @createTime 2024/1/2 9:08
    * @desc
    * @param ip
     * @return
    */
    @Override
    public List<ErrorRate> selectPojoListByIP(String ip) {
        return errorRateMapper.selectPojoListByIP( ip );
    }
}
