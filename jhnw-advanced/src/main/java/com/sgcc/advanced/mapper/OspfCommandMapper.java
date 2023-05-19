package com.sgcc.advanced.mapper;

import java.util.List;
import com.sgcc.advanced.domain.OspfCommand;

/**
 * OSPF命令Mapper接口
 * 
 * @author ruoyi
 * @date 2023-05-19
 */
public interface OspfCommandMapper 
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
     * 删除OSPF命令
     * 
     * @param id OSPF命令主键
     * @return 结果
     */
    public int deleteOspfCommandById(Long id);

    /**
     * 批量删除OSPF命令
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOspfCommandByIds(Long[] ids);
}
