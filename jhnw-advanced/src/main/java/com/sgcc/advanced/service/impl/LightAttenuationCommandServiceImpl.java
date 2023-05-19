package com.sgcc.advanced.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.advanced.mapper.LightAttenuationCommandMapper;
import com.sgcc.advanced.domain.LightAttenuationCommand;
import com.sgcc.advanced.service.ILightAttenuationCommandService;

/**
 * 光衰命令Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-05-19
 */
@Service
public class LightAttenuationCommandServiceImpl implements ILightAttenuationCommandService 
{
    @Autowired
    private LightAttenuationCommandMapper lightAttenuationCommandMapper;

    /**
     * 查询光衰命令
     * 
     * @param id 光衰命令主键
     * @return 光衰命令
     */
    @Override
    public LightAttenuationCommand selectLightAttenuationCommandById(Long id)
    {
        return lightAttenuationCommandMapper.selectLightAttenuationCommandById(id);
    }

    /**
     * 查询光衰命令列表
     * 
     * @param lightAttenuationCommand 光衰命令
     * @return 光衰命令
     */
    @Override
    public List<LightAttenuationCommand> selectLightAttenuationCommandList(LightAttenuationCommand lightAttenuationCommand)
    {
        return lightAttenuationCommandMapper.selectLightAttenuationCommandList(lightAttenuationCommand);
    }

    /**
     * 新增光衰命令
     * 
     * @param lightAttenuationCommand 光衰命令
     * @return 结果
     */
    @Override
    public int insertLightAttenuationCommand(LightAttenuationCommand lightAttenuationCommand)
    {
        return lightAttenuationCommandMapper.insertLightAttenuationCommand(lightAttenuationCommand);
    }

    /**
     * 修改光衰命令
     * 
     * @param lightAttenuationCommand 光衰命令
     * @return 结果
     */
    @Override
    public int updateLightAttenuationCommand(LightAttenuationCommand lightAttenuationCommand)
    {
        return lightAttenuationCommandMapper.updateLightAttenuationCommand(lightAttenuationCommand);
    }

    /**
     * 批量删除光衰命令
     * 
     * @param ids 需要删除的光衰命令主键
     * @return 结果
     */
    @Override
    public int deleteLightAttenuationCommandByIds(Long[] ids)
    {
        return lightAttenuationCommandMapper.deleteLightAttenuationCommandByIds(ids);
    }

    /**
     * 删除光衰命令信息
     * 
     * @param id 光衰命令主键
     * @return 结果
     */
    @Override
    public int deleteLightAttenuationCommandById(Long id)
    {
        return lightAttenuationCommandMapper.deleteLightAttenuationCommandById(id);
    }
}
