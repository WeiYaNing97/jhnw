package com.sgcc.sql.service;

import com.sgcc.sql.domain.CommandLogic;

import java.util.List;

/**
 * 命令逻辑Service接口
 */
public interface ICommandLogicService 
{
    /**
     * 查询命令逻辑
     * 
     * @param id 命令逻辑主键
     * @return 命令逻辑
     */
    public CommandLogic selectCommandLogicById(String id);

    /**
     * 查询命令逻辑列表
     * 
     * @param commandLogic 命令逻辑
     * @return 命令逻辑集合
     */
    public List<CommandLogic> selectCommandLogicList(CommandLogic commandLogic);

    /**
     * 新增命令逻辑
     * 
     * @param commandLogic 命令逻辑
     * @return 结果
     */
    public int insertCommandLogic(CommandLogic commandLogic);
    int insertCommandLogicImport(CommandLogic commandLogic);

    /**
     * 修改命令逻辑
     * 
     * @param commandLogic 命令逻辑
     * @return 结果
     */
    public int updateCommandLogic(CommandLogic commandLogic);

    /**
     * 批量删除命令逻辑
     * 
     * @param ids 需要删除的命令逻辑主键集合
     * @return 结果
     */
    public int deleteCommandLogicByIds(String[] ids);

    /**
     * 删除命令逻辑信息
     * 
     * @param id 命令逻辑主键
     * @return 结果
     */
    public int deleteCommandLogicById(String id);

    /*删除数据表所有数据*/
    int deleteCommandLogic();

}
