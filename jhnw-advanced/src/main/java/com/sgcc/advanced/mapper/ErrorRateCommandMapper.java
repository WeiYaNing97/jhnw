package com.sgcc.advanced.mapper;

import java.util.List;

import com.sgcc.advanced.controller.ErrorPackage;
import com.sgcc.advanced.domain.ErrorRateCommand;
import org.apache.ibatis.annotations.Param;

/**
 * 错误包 命令Mapper接口
 * 
 * @author ruoyi
 * @date 2023-05-19
 */
public interface ErrorRateCommandMapper 
{
    /**
     * 查询 错误包 命令
     * 
     * @param id 错误包 命令主键
     * @return 错误包 命令
     */
    public ErrorRateCommand selectErrorRateCommandById(String id);

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
     * @param errorRateCommand 错误包命令
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
     * 删除 错误包 命令
     * 
     * @param id 错误包 命令主键
     * @return 结果
     */
    public int deleteErrorRateCommandById(String id);

    /**
     * 批量删除 错误包 命令
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteErrorRateCommandByIds(String[] ids);

    List<ErrorRateCommand> selectErrorRateCommandListBySQL(@Param("fuzzySQL") String fuzzySQL);

}
