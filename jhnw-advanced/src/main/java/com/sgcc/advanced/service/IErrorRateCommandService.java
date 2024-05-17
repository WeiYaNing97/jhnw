package com.sgcc.advanced.service;

import java.util.List;

import com.sgcc.advanced.controller.ErrorPackage;
import com.sgcc.advanced.domain.ErrorRateCommand;

/**
 * 错误包 命令Service接口
 * 
 * @author ruoyi
 * @date 2023-05-19
 */
public interface IErrorRateCommandService 
{
    /**
     * 查询 错误包 命令
     * 
     * @param id 错误包 命令主键
     * @return 错误包 命令
     */
    public ErrorRateCommand selectErrorRateCommandById(Long id);

    /**
     * 查询 错误包 命令列表
     * 
     * @param errorRateCommand 错误包 命令
     * @return 错误包 命令集合
     */
    public List<ErrorRateCommand> selectErrorRateCommandList(ErrorRateCommand errorRateCommand);

    /**
     * 新增 错误包 命令
     * 
     * @param errorRateCommand 错误包 命令
     * @return 结果
     */
    public int insertErrorRateCommand(ErrorRateCommand errorRateCommand);

    /**
     * 修改 错误包 命令
     * 
     * @param errorRateCommand 错误包 命令
     * @return 结果
     */
    public int updateErrorRateCommand(ErrorRateCommand errorRateCommand);

    /**
     * 批量删除 错误包 命令
     * 
     * @param ids 需要删除的 错误包 命令主键集合
     * @return 结果
     */
    public int deleteErrorRateCommandByIds(Long[] ids);

    /**
     * 删除 错误包 命令信息
     * 
     * @param id 错误包 命令主键
     * @return 结果
     */
    public int deleteErrorRateCommandById(Long id);

    List<ErrorRateCommand> selectErrorRateCommandListBySQL(ErrorRateCommand errorRateCommand);

}
