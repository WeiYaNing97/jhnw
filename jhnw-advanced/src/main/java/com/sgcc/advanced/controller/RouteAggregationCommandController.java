package com.sgcc.advanced.controller;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hpsf.Array;
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
 * @date 2024-07-29
 */
@Api(tags = "路由聚合命令管理")
@RestController
@RequestMapping("/advanced/RouteAggregationCommand")
public class RouteAggregationCommandController extends BaseController
{
    @Autowired
    private IRouteAggregationCommandService routeAggregationCommandService;

    /**
     * 查询路由聚合命令列表
     */
    @ApiOperation("查询路由聚合命令列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键ID", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "brand", value = "品牌", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "switchType", value = "型号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "internalCommand", value = "获取内部宣告地址命令", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "internalKeywords", value = "内部关键字", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "externalCommand", value = "获取外部宣告地址命令", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "externalKeywords", value = "外部关键字", dataTypeClass = String.class, required = true)
    })
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
    @ApiOperation("导出路由聚合命令列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键ID", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "brand", value = "品牌", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "switchType", value = "型号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "internalCommand", value = "获取内部宣告地址命令", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "internalKeywords", value = "内部关键字", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "externalCommand", value = "获取外部宣告地址命令", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "externalKeywords", value = "外部关键字", dataTypeClass = String.class, required = true)
    })
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
    @ApiOperation("获取路由聚合命令详细信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键ID", dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('advanced:RouteAggregationCommand:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(routeAggregationCommandService.selectRouteAggregationCommandById(id));
    }

    /**
     * 新增路由聚合命令
     */
    @ApiOperation("新增路由聚合命令")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键ID", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "brand", value = "品牌", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "switchType", value = "型号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "internalCommand", value = "获取内部宣告地址命令", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "internalKeywords", value = "内部关键字", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "externalCommand", value = "获取外部宣告地址命令", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "externalKeywords", value = "外部关键字", dataTypeClass = String.class, required = true)
    })
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
    @ApiOperation("修改路由聚合命令")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键ID", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "brand", value = "品牌", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "switchType", value = "型号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "internalCommand", value = "获取内部宣告地址命令", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "internalKeywords", value = "内部关键字", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "externalCommand", value = "获取外部宣告地址命令", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "externalKeywords", value = "外部关键字", dataTypeClass = String.class, required = true)
    })
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
    @ApiOperation("删除路由聚合命令")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "ids", value = "主键ID", dataTypeClass = Array.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('advanced:RouteAggregationCommand:remove')")
    @Log(title = "路由聚合命令", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(routeAggregationCommandService.deleteRouteAggregationCommandByIds(ids));
    }
}
