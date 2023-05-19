package com.sgcc.advanced.service;

import java.util.List;
import com.sgcc.advanced.domain.OspfCommand;

/**
 * OSPF命令Service接口
 * 
 * @author ruoyi
 * @date 2023-05-19
 */
public interface IOspfCommandService 
{
    /**
     * 查询OSPF命令
     * 
     * @param id OSPF命令主键
     * @return OSPF命令
     */
    public OspfCommand selectOspfCommandById(Long id);

    /**
     * 查询OSPF命令列表
     * 
     * @param ospfCommand OSPF命令
     * @return OSPF命令集合
     */
    public List<OspfCommand> selectOspfCommandList(OspfCommand ospfCommand);

    /**
     * 新增OSPF命令
     * 
     * @param ospfCommand OSPF命令
     * @return 结果
     */
    public int insertOspfCommand(OspfCommand ospfCommand);

    /**
     * 修改OSPF命令
     * 
     * @param ospfCommand OSPF命令
     * @return 结果
     */
    public int updateOspfCommand(OspfCommand ospfCommand);

    /**
     * 批量删除OSPF命令
     * 
     * @param ids 需要删除的OSPF命令主键集合
     * @return 结果
     */
    public int deleteOspfCommandByIds(Long[] ids);

    /**
     * 删除OSPF命令信息
     * 
     * @param id OSPF命令主键
     * @return 结果
     */
    public int deleteOspfCommandById(Long id);
}
