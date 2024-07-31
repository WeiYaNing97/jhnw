package com.sgcc.advanced.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sgcc.share.domain.Constant;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.FunctionalMethods;
import com.sgcc.share.util.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sgcc.advanced.mapper.RouteAggregationCommandMapper;
import com.sgcc.advanced.domain.RouteAggregationCommand;
import com.sgcc.advanced.service.IRouteAggregationCommandService;

/**
 * 路由聚合命令Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-07-29
 */
@Service
public class RouteAggregationCommandServiceImpl implements IRouteAggregationCommandService 
{
    @Autowired
    private RouteAggregationCommandMapper routeAggregationCommandMapper;

    /**
     * 查询路由聚合命令
     * 
     * @param id 路由聚合命令主键
     * @return 路由聚合命令
     */
    @Override
    public RouteAggregationCommand selectRouteAggregationCommandById(String id)
    {
        return routeAggregationCommandMapper.selectRouteAggregationCommandById(id);
    }

    /**
     * 查询路由聚合命令列表
     * 
     * @param routeAggregationCommand 路由聚合命令
     * @return 路由聚合命令
     */
    @Override
    public List<RouteAggregationCommand> selectRouteAggregationCommandList(RouteAggregationCommand routeAggregationCommand)
    {
        return routeAggregationCommandMapper.selectRouteAggregationCommandList(routeAggregationCommand);
    }

    /**
     * 新增路由聚合命令
     * 
     * @param routeAggregationCommand 路由聚合命令
     * @return 结果
     */
    @Override
    public int insertRouteAggregationCommand(RouteAggregationCommand routeAggregationCommand)
    {
        String regionalCode = (CustomConfigurationUtil.getValue("configuration.problemCode.日常巡检", Constant.getProfileInformation())).toString();
        String id = MyUtils.getID(regionalCode, null);

        routeAggregationCommand.setId(id);


        return routeAggregationCommandMapper.insertRouteAggregationCommand(routeAggregationCommand);
    }

    /**
     * 修改路由聚合命令
     * 
     * @param routeAggregationCommand 路由聚合命令
     * @return 结果
     */
    @Override
    public int updateRouteAggregationCommand(RouteAggregationCommand routeAggregationCommand)
    {
        return routeAggregationCommandMapper.updateRouteAggregationCommand(routeAggregationCommand);
    }

    /**
     * 批量删除路由聚合命令
     * 
     * @param ids 需要删除的路由聚合命令主键
     * @return 结果
     */
    @Override
    public int deleteRouteAggregationCommandByIds(String[] ids)
    {
        return routeAggregationCommandMapper.deleteRouteAggregationCommandByIds(ids);
    }

    /**
     * 删除路由聚合命令信息
     * 
     * @param id 路由聚合命令主键
     * @return 结果
     */
    @Override
    public int deleteRouteAggregationCommandById(String id)
    {
        return routeAggregationCommandMapper.deleteRouteAggregationCommandById(id);
    }


    @Override
    public List<RouteAggregationCommand> selectRouteAggregationCommandListBySQL(RouteAggregationCommand routeAggregationCommand) {
        List<RouteAggregationCommand> pojo = new ArrayList<>();
        pojo.addAll(selectRouteAggregationCommandListByEquivalence(routeAggregationCommand));

        String equivalence = FunctionalMethods.getEquivalence(routeAggregationCommand.getBrand());
        if (equivalence!=null){
            routeAggregationCommand.setBrand(equivalence);
            pojo.addAll(selectRouteAggregationCommandListByEquivalence(routeAggregationCommand));
        }

        return pojo;
    }

    private Collection<? extends RouteAggregationCommand> selectRouteAggregationCommandListByEquivalence(RouteAggregationCommand routeAggregationCommand) {

        String fuzzySQL = FunctionalMethods.getFuzzySQL(routeAggregationCommand.getBrand(), routeAggregationCommand.getSwitchType(),
                routeAggregationCommand.getFirewareVersion(), routeAggregationCommand.getSubVersion());

        return routeAggregationCommandMapper.selectRouteAggregationCommandListBySQL(fuzzySQL);

    }
}
