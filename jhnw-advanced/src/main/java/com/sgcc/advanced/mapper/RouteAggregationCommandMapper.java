package com.sgcc.advanced.mapper;

import java.util.Collection;
import java.util.List;
import com.sgcc.advanced.domain.RouteAggregationCommand;
import io.lettuce.core.dynamic.annotation.Param;

/**
 * 路由聚合命令Mapper接口
 * 
 * @author ruoyi
 * @date 2024-07-29
 */
public interface RouteAggregationCommandMapper 
{
    /**
     * 查询路由聚合命令
     * 
     * @param id 路由聚合命令主键
     * @return 路由聚合命令
     */
    public RouteAggregationCommand selectRouteAggregationCommandById(String id);

    /**
     * 查询路由聚合命令列表
     * 
     * @param routeAggregationCommand 路由聚合命令
     * @return 路由聚合命令集合
     */
    public List<RouteAggregationCommand> selectRouteAggregationCommandList(RouteAggregationCommand routeAggregationCommand);

    /**
     * 新增路由聚合命令
     * 
     * @param routeAggregationCommand 路由聚合命令
     * @return 结果
     */
    public int insertRouteAggregationCommand(RouteAggregationCommand routeAggregationCommand);

    /**
     * 修改路由聚合命令
     * 
     * @param routeAggregationCommand 路由聚合命令
     * @return 结果
     */
    public int updateRouteAggregationCommand(RouteAggregationCommand routeAggregationCommand);

    /**
     * 删除路由聚合命令
     * 
     * @param id 路由聚合命令主键
     * @return 结果
     */
    public int deleteRouteAggregationCommandById(String id);

    /**
     * 批量删除路由聚合命令
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRouteAggregationCommandByIds(String[] ids);


    Collection<? extends RouteAggregationCommand> selectRouteAggregationCommandListBySQL(@Param("fuzzySQL") String fuzzySQL);
}
