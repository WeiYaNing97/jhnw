package com.sgcc.advanced.mapper;

import java.util.Collection;
import java.util.List;
import com.sgcc.advanced.domain.LinkBindingCommand;
import io.lettuce.core.dynamic.annotation.Param;

/**
 * 链路捆绑命令Mapper接口
 * 
 * @author ruoyi
 * @date 2024-09-09
 */
public interface LinkBindingCommandMapper 
{
    /**
     * 查询链路捆绑命令
     * 
     * @param id 链路捆绑命令主键
     * @return 链路捆绑命令
     */
    public LinkBindingCommand selectLinkBindingCommandById(Long id);

    /**
     * 查询链路捆绑命令列表
     * 
     * @param linkBindingCommand 链路捆绑命令
     * @return 链路捆绑命令集合
     */
    public List<LinkBindingCommand> selectLinkBindingCommandList(LinkBindingCommand linkBindingCommand);

    /**
     * 新增链路捆绑命令
     * 
     * @param linkBindingCommand 链路捆绑命令
     * @return 结果
     */
    public int insertLinkBindingCommand(LinkBindingCommand linkBindingCommand);

    /**
     * 修改链路捆绑命令
     * 
     * @param linkBindingCommand 链路捆绑命令
     * @return 结果
     */
    public int updateLinkBindingCommand(LinkBindingCommand linkBindingCommand);

    /**
     * 删除链路捆绑命令
     * 
     * @param id 链路捆绑命令主键
     * @return 结果
     */
    public int deleteLinkBindingCommandById(Long id);

    /**
     * 批量删除链路捆绑命令
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLinkBindingCommandByIds(Long[] ids);

    Collection<? extends LinkBindingCommand> selectLinkBindingCommandListBySQL(@Param("fuzzySQL") String fuzzySQL);
}
