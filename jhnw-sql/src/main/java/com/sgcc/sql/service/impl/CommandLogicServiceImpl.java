package com.sgcc.sql.service.impl;

import com.sgcc.sql.domain.CommandLogic;
import com.sgcc.sql.mapper.CommandLogicMapper;
import com.sgcc.sql.service.ICommandLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 命令逻辑Service业务层处理
 * 
 * @author 韦亚宁
 * @date 2021-12-14
 */
@Service
public class CommandLogicServiceImpl implements ICommandLogicService 
{
    @Autowired
    private CommandLogicMapper commandLogicMapper;

    /**
     * 查询命令逻辑
     * 
     * @param id 命令逻辑主键
     * @return 命令逻辑
     */
    @Override
    public CommandLogic selectCommandLogicById(String id)
    {
        return commandLogicMapper.selectCommandLogicById(id);
    }

    /**
     * 查询命令逻辑列表
     * 
     * @param commandLogic 命令逻辑
     * @return 命令逻辑
     */
    @Override
    public List<CommandLogic> selectCommandLogicList(CommandLogic commandLogic)
    {
        return commandLogicMapper.selectCommandLogicList(commandLogic);
    }

    /**
     * 新增命令逻辑
     * 
     * @param commandLogic 命令逻辑
     * @return 结果
     */
    @Override
    public int insertCommandLogic(CommandLogic commandLogic)
    {
        return commandLogicMapper.insertCommandLogic(commandLogic);
    }

    /**
     * 修改命令逻辑
     * 
     * @param commandLogic 命令逻辑
     * @return 结果
     */
    @Override
    public int updateCommandLogic(CommandLogic commandLogic)
    {
        return commandLogicMapper.updateCommandLogic(commandLogic);
    }

    /**
     * 批量删除命令逻辑
     * 
     * @param ids 需要删除的命令逻辑主键
     * @return 结果
     */
    @Override
    public int deleteCommandLogicByIds(String[] ids)
    {
        return commandLogicMapper.deleteCommandLogicByIds(ids);
    }

    /**
     * 删除命令逻辑信息
     * 
     * @param id 命令逻辑主键
     * @return 结果
     */
    @Override
    public int deleteCommandLogicById(String id)
    {
        return commandLogicMapper.deleteCommandLogicById(id);
    }
}
