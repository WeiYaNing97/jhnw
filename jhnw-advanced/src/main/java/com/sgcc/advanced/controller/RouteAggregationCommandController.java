package com.sgcc.advanced.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sgcc.common.annotation.Log;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.advanced.domain.RouteAggregationCommand;
import com.sgcc.advanced.service.IRouteAggregationCommandService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 路由聚合命令Controller
 * 
 * @author ruoyi
 * @date 2024-07-18
 */
@RestController
@RequestMapping("/advanced/RouteAggregationCommand")
public class RouteAggregationCommandController extends BaseController
{
    @Autowired
    private IRouteAggregationCommandService routeAggregationCommandService;

    /**
     * 查询路由聚合命令列表
     */
    @PreAuthorize("@ss.hasPermi('advanced:RouteAggregationCommand:list')")
    @GetMapping("/list")
    public TableDataInfo list(RouteAggregationCommand routeAggregationCommand)
    {

        startPage();
        List<RouteAggregationCommand> list = routeAggregationCommandService.selectRouteAggregationCommandList(routeAggregationCommand);
        return getDataTable(list);
    }

    /**
     * 导出路由聚合命令列表
     */
    @PreAuthorize("@ss.hasPermi('advanced:RouteAggregationCommand:export')")
    @Log(title = "路由聚合命令", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(RouteAggregationCommand routeAggregationCommand)
    {
        List<RouteAggregationCommand> list = routeAggregationCommandService.selectRouteAggregationCommandList(routeAggregationCommand);
        ExcelUtil<RouteAggregationCommand> util = new ExcelUtil<RouteAggregationCommand>(RouteAggregationCommand.class);
        return util.exportExcel(list, "路由聚合命令数据");
    }

    /**
     * 获取路由聚合命令详细信息
     */
    @PreAuthorize("@ss.hasPermi('advanced:RouteAggregationCommand:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(routeAggregationCommandService.selectRouteAggregationCommandById(id));
    }

    /**
     * 新增路由聚合命令
     */
    @PreAuthorize("@ss.hasPermi('advanced:RouteAggregationCommand:add')")
    @Log(title = "路由聚合命令", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RouteAggregationCommand routeAggregationCommand)
    {
        return toAjax(routeAggregationCommandService.insertRouteAggregationCommand(routeAggregationCommand));
    }

    /**
     * 修改路由聚合命令
     */
    @PreAuthorize("@ss.hasPermi('advanced:RouteAggregationCommand:edit')")
    @Log(title = "路由聚合命令", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RouteAggregationCommand routeAggregationCommand)
    {
        return toAjax(routeAggregationCommandService.updateRouteAggregationCommand(routeAggregationCommand));
    }

    /**
     * 删除路由聚合命令
     */
    @PreAuthorize("@ss.hasPermi('advanced:RouteAggregationCommand:remove')")
    @Log(title = "路由聚合命令", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(routeAggregationCommandService.deleteRouteAggregationCommandByIds(ids));
    }
}