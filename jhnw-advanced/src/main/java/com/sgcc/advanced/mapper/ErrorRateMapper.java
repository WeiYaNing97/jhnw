package com.sgcc.advanced.mapper;


import com.sgcc.advanced.domain.ErrorRate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 误码率Mapper接口
 * 
 * @author ruoyi
 * @date 2023-05-09
 */
public interface ErrorRateMapper 
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
     * 删除误码率
     * 
     * @param id 误码率主键
     * @return 结果
     */
    public int deleteErrorRateById(Long id);

    /**
     * 批量删除误码率
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteErrorRateByIds(Long[] ids);

    /**
    * @Description 根据IP获取数据集合
    * @author charles
    * @createTime 2024/1/2 9:08
    * @desc
    * @param ip
     * @return
    */
    List<ErrorRate> selectPojoListByIP(@Param("ip") String ip);
}
