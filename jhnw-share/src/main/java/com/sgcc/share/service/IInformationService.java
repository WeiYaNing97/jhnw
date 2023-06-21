package com.sgcc.share.service;

import java.util.List;
import com.sgcc.share.domain.Information;

/**
 * 交换机信息Service接口
 * 
 * @author ruoyi
 * @date 2023-03-07
 */
public interface IInformationService 
{
    /**
     * 查询交换机信息
     * 
     * @param id 交换机信息主键
     * @return 交换机信息
     */
    public Information selectInformationById(Long id);

    /**
     * 查询交换机信息列表
     * 
     * @param information 交换机信息
     * @return 交换机信息集合
     */
    public List<Information> selectInformationList(Information information);

    /**
     * 新增交换机信息
     * 
     * @param information 交换机信息
     * @return 结果
     */
    public int insertInformation(Information information);

    /**
     * 修改交换机信息
     * 
     * @param information 交换机信息
     * @return 结果
     */
    public int updateInformation(Information information);

    /**
     * 批量删除交换机信息
     * 
     * @param ids 需要删除的交换机信息主键集合
     * @return 结果
     */
    public int deleteInformationByIds(Long[] ids);

    /**
     * 删除交换机信息信息
     * 
     * @param id 交换机信息主键
     * @return 结果
     */
    public int deleteInformationById(Long id);

    /**
     * 查询交换机品牌信息 去重处理
     */
    List<String> selectDeviceBrandList();

    List<String> selectDeviceModelList(String brand);

    List<Information> selectDeviceModelListByArray(String[] brands);
}
