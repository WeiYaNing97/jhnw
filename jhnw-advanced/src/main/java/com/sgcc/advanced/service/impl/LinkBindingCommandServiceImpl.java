package com.sgcc.advanced.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sgcc.advanced.domain.RouteAggregationCommand;
import com.sgcc.share.util.FunctionalMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.advanced.mapper.LinkBindingCommandMapper;
import com.sgcc.advanced.domain.LinkBindingCommand;
import com.sgcc.advanced.service.ILinkBindingCommandService;

/**
 * 链路捆绑命令Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-09-09
 */
@Service
public class LinkBindingCommandServiceImpl implements ILinkBindingCommandService 
{
    @Autowired
    private LinkBindingCommandMapper linkBindingCommandMapper;

    /**
     * 查询链路捆绑命令
     * 
     * @param id 链路捆绑命令主键
     * @return 链路捆绑命令
     */
    @Override
    public LinkBindingCommand selectLinkBindingCommandById(Long id)
    {
        return linkBindingCommandMapper.selectLinkBindingCommandById(id);
    }

    /**
     * 查询链路捆绑命令列表
     * 
     * @param linkBindingCommand 链路捆绑命令
     * @return 链路捆绑命令
     */
    @Override
    public List<LinkBindingCommand> selectLinkBindingCommandList(LinkBindingCommand linkBindingCommand)
    {
        return linkBindingCommandMapper.selectLinkBindingCommandList(linkBindingCommand);
    }

    /**
     * 新增链路捆绑命令
     * 
     * @param linkBindingCommand 链路捆绑命令
     * @return 结果
     */
    @Override
    public int insertLinkBindingCommand(LinkBindingCommand linkBindingCommand)
    {
        return linkBindingCommandMapper.insertLinkBindingCommand(linkBindingCommand);
    }

    /**
     * 修改链路捆绑命令
     * 
     * @param linkBindingCommand 链路捆绑命令
     * @return 结果
     */
    @Override
    public int updateLinkBindingCommand(LinkBindingCommand linkBindingCommand)
    {
        return linkBindingCommandMapper.updateLinkBindingCommand(linkBindingCommand);
    }

    /**
     * 批量删除链路捆绑命令
     * 
     * @param ids 需要删除的链路捆绑命令主键
     * @return 结果
     */
    @Override
    public int deleteLinkBindingCommandByIds(Long[] ids)
    {
        return linkBindingCommandMapper.deleteLinkBindingCommandByIds(ids);
    }

    /**
     * 删除链路捆绑命令信息
     * 
     * @param id 链路捆绑命令主键
     * @return 结果
     */
    @Override
    public int deleteLinkBindingCommandById(Long id)
    {
        return linkBindingCommandMapper.deleteLinkBindingCommandById(id);
    }

    /**
     * 根据SQL语句查询LinkBindingCommand对象列表
     *
     * @param linkBindingCommand 查询条件，一个LinkBindingCommand对象
     * @return 返回一个List<LinkBindingCommand>类型的对象列表，包含查询结果
     */
    @Override
    public List<LinkBindingCommand> selectLinkBindingCommandListBySQL(LinkBindingCommand linkBindingCommand) {
        // 创建一个ArrayList用于存放查询结果
        List<LinkBindingCommand> pojo = new ArrayList<>();
        // 通过调用selectLinkBindingCommandListByEquivalence方法查询并添加结果到pojo中
        pojo.addAll(selectLinkBindingCommandListByEquivalence(linkBindingCommand));

        // 调用FunctionalMethods的getEquivalence方法获取设备品牌的等价值
        String equivalence = FunctionalMethods.getEquivalence(linkBindingCommand.getBrand());
        // 如果等价值不为空
        if (equivalence!=null){
            // 将设备品牌更新为等价值
            linkBindingCommand.setBrand(equivalence);
            // 再次通过调用selectLinkBindingCommandListByEquivalence方法查询并添加结果到pojo中
            pojo.addAll(selectLinkBindingCommandListByEquivalence(linkBindingCommand));
        }

        // 返回查询结果列表
        return pojo;
    }



    /**
     * 根据设备品牌、开关类型、固件版本和子版本号查询LinkBindingCommand对象列表
     *
     * @param linkBindingCommand 查询条件，一个LinkBindingCommand对象
     * @return 返回一个Collection<? extends LinkBindingCommand>类型的对象列表，包含查询结果
     */
    private Collection<? extends LinkBindingCommand> selectLinkBindingCommandListByEquivalence(LinkBindingCommand linkBindingCommand) {
        String fuzzySQL = FunctionalMethods.getFuzzySQL(linkBindingCommand.getBrand(), linkBindingCommand.getSwitchType(),
                linkBindingCommand.getFirewareVersion(), linkBindingCommand.getSubVersion());
        return linkBindingCommandMapper.selectLinkBindingCommandListBySQL(fuzzySQL);
    }
}
