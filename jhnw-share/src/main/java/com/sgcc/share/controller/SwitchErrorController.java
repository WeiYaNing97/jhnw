package com.sgcc.share.controller;
import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.domain.SwitchError;
import com.sgcc.share.service.ISwitchErrorService;
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
import com.sgcc.common.core.page.TableDataInfo;

import java.util.List;

/**
 * 交换机错误Controller
 */
@Api(tags = "交换机错误信息管理")
@RestController
@RequestMapping("/share/switch_error")
public class SwitchErrorController extends BaseController
{
    @Autowired
    private ISwitchErrorService switchErrorService;

    /**
     * 查询交换机错误列表
     */
    @ApiOperation("模糊查询交换机错误信息列表(型号、版本、子版本 可以为*)")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "errorId", value = "错误主键", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "brand", value = "交换机品牌", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "switchType", value = "型号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "errorKeyword", value = "错误关键字", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "errorName", value = "错误名称", dataTypeClass = String.class, required = true)
    })
    @GetMapping("/switchErrorList")
    public List<SwitchError> selectSwitchErrorListByPojo(SwitchError switchError)
    {
        switchErrorService = SpringBeanUtil.getBean(ISwitchErrorService.class);
        return switchErrorService.selectSwitchErrorListByPojo(switchError);
    }

    /**
     * 查询交换机错误列表
     */
    @ApiOperation("查询交换机错误信息列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "errorId", value = "错误主键", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "brand", value = "交换机品牌", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "switchType", value = "型号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "errorKeyword", value = "错误关键字", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "errorName", value = "错误名称", dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:switch_error:list')")
    @GetMapping("/list")
    public TableDataInfo list(SwitchError switchError)
    {
        startPage();
        List<SwitchError> list = switchErrorService.selectSwitchErrorList(switchError);
        return getDataTable(list);
    }

    /**
     * 导出交换机错误列表
     */
    @ApiOperation("导出交换机错误信息列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "errorId", value = "错误主键", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "brand", value = "交换机品牌", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "switchType", value = "型号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "errorKeyword", value = "错误关键字", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "errorName", value = "错误名称", dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:switch_error:export')")
    @MyLog(title = "导出交换机错误信息列表", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SwitchError switchError)
    {
        List<SwitchError> list = switchErrorService.selectSwitchErrorList(switchError);
        ExcelUtil<SwitchError> util = new ExcelUtil<SwitchError>(SwitchError.class);
        return util.exportExcel(list, "交换机错误数据");
    }

    /**
     * 获取交换机错误详细信息
     */
    @ApiOperation("获取交换机错误详细信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "errorId", value = "错误主键", dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:switch_error:query')")
    @GetMapping(value = "/{errorId}")
    public AjaxResult getInfo(@PathVariable("errorId") String errorId)
    {
        return AjaxResult.success(switchErrorService.selectSwitchErrorByErrorId(errorId));
    }

    /**
     * 新增交换机错误
     */
    @ApiOperation("新增交换机错误信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "errorId", value = "错误主键", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "brand", value = "交换机品牌", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "switchType", value = "型号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "errorKeyword", value = "错误关键字", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "errorName", value = "错误名称", dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:switch_error:add')")
    @MyLog(title = "新增交换机错误信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SwitchError switchError)
    {
        return toAjax(switchErrorService.insertSwitchError(switchError));
    }

    /**
     * 修改交换机错误
     */
    @ApiOperation("修改交换机错误信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "errorId", value = "错误主键", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "brand", value = "交换机品牌", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "switchType", value = "型号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "errorKeyword", value = "错误关键字", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "errorName", value = "错误名称", dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:switch_error:edit')")
    @MyLog(title = "修改交换机错误信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SwitchError switchError)
    {
        return toAjax(switchErrorService.updateSwitchError(switchError));
    }

    /**
     * 删除交换机错误
     */
    @ApiOperation("删除交换机错误信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "errorIds", value = "错误主键", dataTypeClass = Array.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:switch_error:remove')")
    @MyLog(title = "删除交换机错误信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{errorIds}")
    public AjaxResult remove(@PathVariable String[] errorIds)
    {
        return toAjax(switchErrorService.deleteSwitchErrorByErrorIds(errorIds));
    }
}
