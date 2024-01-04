package com.sgcc.advanced.service;


import com.sgcc.advanced.domain.ErrorRate;

import java.util.List;

/**
 * 误码率Service接口
 * 
 * @author ruoyi
 * @date 2023-05-09
 */
public interface IErrorRateService 
{
    /**
     * 查询误码率
     * 
     * @param id 误码率主键
     * @return 误码率
     */
    public ErrorRate selectErrorRateById(Long id);

    /**
     * 查询误码率列表
     * 
     * @param errorRate 误码率
     * @return 误码率集合
     */
    public List<ErrorRate> selectErrorRateList(ErrorRate errorRate);

    /**
     * 新增误码率
     * 
     * @param errorRate 误码率
     * @return 结果
     */
    public int insertErrorRate(ErrorRate errorRate);

    /**
     * 修改误码率
     * 
     * @param errorRate 误码率
     * @return 结果
     */
    public int updateErrorRate(ErrorRate errorRate);

    /**
     * 批量删除误码率
     * 
     * @param ids 需要删除的误码率主键集合
     * @return 结果
     */
    public int deleteErrorRateByIds(Long[] ids);

    /**
     * 删除误码率信息
     * 
     * @param id 误码率主键
     * @return 结果
     */
    public int deleteErrorRateById(Long id);

    /**
    * @Description 根据IP获取数据集合
    * @author charles
    * @createTime 2024/1/2 9:07
    * @desc
    * @param ip
     * @return
    */
    List<ErrorRate> selectPojoListByIP(String ip);
}
