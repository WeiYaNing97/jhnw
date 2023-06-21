package com.sgcc.share.mapper;

import java.util.List;
import com.sgcc.share.domain.Information;

/**
 * 交换机信息Mapper接口
 * 
 * @author ruoyi
 * @date 2023-03-07
 */
public interface InformationMapper 
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
     * 删除交换机信息
     * 
     * @param id 交换机信息主键
     * @return 结果
     */
    public int deleteInformationById(Long id);

    /**
     * 批量删除交换机信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteInformationByIds(Long[] ids);

    List<String> selectDeviceBrandList();

    List<String> selectDeviceModelList(String deviceBrand);

    List<Information> selectDeviceModelListByArray(String[] brands);
}
