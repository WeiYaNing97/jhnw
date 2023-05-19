package com.sgcc.advanced.mapper;

import java.util.List;
import com.sgcc.advanced.domain.ErrorRateCommand;

/**
 * 误码率命令Mapper接口
 * 
 * @author ruoyi
 * @date 2023-05-19
 */
public interface ErrorRateCommandMapper 
{
    /**
     * 查询误码率命令
     * 
     * @param id 误码率命令主键
     * @return 误码率命令
     */
    public ErrorRateCommand selectErrorRateCommandById(Long id);

    /**
     * 查询误码率命令列表
     * 
     * @param errorRateCommand 误码率命令
     * @return 误码率命令集合
     */
    public List<ErrorRateCommand> selectErrorRateCommandList(ErrorRateCommand errorRateCommand);

    /**
     * 新增误码率命令
     * 
     * @param errorRateCommand 误码率命令
     * @return 结果
     */
    public int insertErrorRateCommand(ErrorRateCommand errorRateCommand);

    /**
     * 修改误码率命令
     * 
     * @param errorRateCommand 误码率命令
     * @return 结果
     */
    public int updateErrorRateCommand(ErrorRateCommand errorRateCommand);

    /**
     * 删除误码率命令
     * 
     * @param id 误码率命令主键
     * @return 结果
     */
    public int deleteErrorRateCommandById(Long id);

    /**
     * 批量删除误码率命令
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteErrorRateCommandByIds(Long[] ids);
}
