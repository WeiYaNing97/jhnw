package com.sgcc.advanced.mapper;

import java.util.List;
import com.sgcc.advanced.domain.LightAttenuationCommand;
import org.apache.ibatis.annotations.Param;

/**
 * 光衰命令Mapper接口
 * 
 * @author ruoyi
 * @date 2023-05-19
 */
public interface LightAttenuationCommandMapper 
{
    /**
     * 查询光衰命令
     * 
     * @param id 光衰命令主键
     * @return 光衰命令
     */
    public LightAttenuationCommand selectLightAttenuationCommandById(Long id);

    /**
     * 查询光衰命令列表
     * 
     * @param lightAttenuationCommand 光衰命令
     * @return 光衰命令集合
     */
    public List<LightAttenuationCommand> selectLightAttenuationCommandList(LightAttenuationCommand lightAttenuationCommand);

    /**
     * 新增光衰命令
     * 
     * @param lightAttenuationCommand 光衰命令
     * @return 结果
     */
    public int insertLightAttenuationCommand(LightAttenuationCommand lightAttenuationCommand);

    /**
     * 修改光衰命令
     * 
     * @param lightAttenuationCommand 光衰命令
     * @return 结果
     */
    public int updateLightAttenuationCommand(LightAttenuationCommand lightAttenuationCommand);

    /**
     * 删除光衰命令
     * 
     * @param id 光衰命令主键
     * @return 结果
     */
    public int deleteLightAttenuationCommandById(Long id);

    /**
     * 批量删除光衰命令
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLightAttenuationCommandByIds(Long[] ids);

    List<LightAttenuationCommand> selectLightAttenuationCommandListBySQL(@Param("fuzzySQL") String fuzzySQL);
}
