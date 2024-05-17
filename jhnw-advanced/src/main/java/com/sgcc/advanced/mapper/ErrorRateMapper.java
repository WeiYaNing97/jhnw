package com.sgcc.advanced.mapper;


import com.sgcc.advanced.domain.ErrorRate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 错误包 Mapper接口
 * 
 * @author ruoyi
 * @date 2023-05-09
 */
public interface ErrorRateMapper 
{
    /**
     * 查询 错误包
     * 
     * @param id 错误包 主键
     * @return 错误包
     */
    public ErrorRate selectErrorRateById(Long id);

    /**
     * 查询 错误包 列表
     * 
     * @param errorRate 错误包
     * @return 错误包 集合
     */
    public List<ErrorRate> selectErrorRateList(ErrorRate errorRate);

    /**
     * 新增 错误包
     * 
     * @param errorRate 错误包
     * @return 结果
     */
    public int insertErrorRate(ErrorRate errorRate);

    /**
     * 修改 错误包
     * 
     * @param errorRate 错误包
     * @return 结果
     */
    public int updateErrorRate(ErrorRate errorRate);

    /**
     * 删除 错误包
     * 
     * @param id 错误包 主键
     * @return 结果
     */
    public int deleteErrorRateById(Long id);

    /**
     * 批量删除 错误包
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
