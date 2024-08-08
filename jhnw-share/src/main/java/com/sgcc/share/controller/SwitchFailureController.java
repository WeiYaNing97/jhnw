package com.sgcc.share.controller;

import java.util.List;

import com.sgcc.common.annotation.MyLog;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.domain.SwitchFailure;
import com.sgcc.share.service.ISwitchFailureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 交换机故障Controller
 * @author ruoyi
 * @date 2022-07-26
 */
@Api(tags = "交换机故障信息管理")
@RestController
@RequestMapping("/share/switch_failure")
public class SwitchFailureController extends BaseController
{
    @Autowired
    private ISwitchFailureService switchFailureService;

    /**
     * 查询交换机故障列表
     */
    @ApiOperation("查询交换机故障信息列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "failureId", value = "故障编号", dataType = "String"),
            @ApiImplicitParam(name = "brand", value = "交换机品牌", dataType = "String"),
            @ApiImplicitParam(name = "switchType", value = "型号", dataType = "String"),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataType = "String"),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataType = "String"),
            @ApiImplicitParam(name = "failureKeyword", value = "故障关键字", dataType = "String"),
            @ApiImplicitParam(name = "failureName", value = "故障名称", dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('sql:switch_failure:list')")
    @GetMapping("/list")
    public TableDataInfo list(SwitchFailure switchFailure)
    {
        startPage();
        List<SwitchFailure> list = switchFailureService.selectSwitchFailureList(switchFailure);
        return getDataTable(list);
    }


    /**
     * 根据品牌查询交换机故障列表
     */
    @ApiOperation("模糊查询交换机故障信息列表(型号、版本、子版本 可以为*)")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "failureId", value = "故障编号", dataType = "String"),
            @ApiImplicitParam(name = "brand", value = "交换机品牌", dataType = "String"),
            @ApiImplicitParam(name = "switchType", value = "型号", dataType = "String"),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataType = "String"),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataType = "String"),
            @ApiImplicitParam(name = "failureKeyword", value = "故障关键字", dataType = "String"),
            @ApiImplicitParam(name = "failureName", value = "故障名称", dataType = "String")
    })
    public List<SwitchFailure> selectSwitchFailureListByPojo(SwitchFailure switchFailure) {
        switchFailureService = SpringBeanUtil.getBean(ISwitchFailureService.class);
        return switchFailureService.selectSwitchFailureListByPojo(switchFailure);
    }


    /**
     * 导出交换机故障列表
     */
    @ApiOperation("导出交换机故障信息列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "failureId", value = "故障编号", dataType = "String"),
            @ApiImplicitParam(name = "brand", value = "交换机品牌", dataType = "String"),
            @ApiImplicitParam(name = "switchType", value = "型号", dataType = "String"),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataType = "String"),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataType = "String"),
            @ApiImplicitParam(name = "failureKeyword", value = "故障关键字", dataType = "String"),
            @ApiImplicitParam(name = "failureName", value = "故障名称", dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('sql:switch_failure:export')")
    @MyLog(title = "导出交换机故障信息列表", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SwitchFailure switchFailure)
    {
        List<SwitchFailure> list = switchFailureService.selectSwitchFailureList(switchFailure);
        ExcelUtil<SwitchFailure> util = new ExcelUtil<SwitchFailure>(SwitchFailure.class);
        return util.exportExcel(list, "交换机故障数据");
    }

    /**
     * 获取交换机故障详细信息
     */
    @ApiOperation("获取交换机故障详细信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "failureId", value = "故障编号", dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('sql:switch_failure:query')")
    @GetMapping(value = "/{failureId}")
    public AjaxResult getInfo(@PathVariable("failureId") String failureId)
    {
        return AjaxResult.success(switchFailureService.selectSwitchFailureByFailureId(failureId));
    }

    /**
     * 新增交换机故障
     */
    @ApiOperation("新增交换机故障信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "failureId", value = "故障编号", dataType = "String"),
            @ApiImplicitParam(name = "brand", value = "交换机品牌", dataType = "String"),
            @ApiImplicitParam(name = "switchType", value = "型号", dataType = "String"),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataType = "String"),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataType = "String"),
            @ApiImplicitParam(name = "failureKeyword", value = "故障关键字", dataType = "String"),
            @ApiImplicitParam(name = "failureName", value = "故障名称", dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('sql:switch_failure:add')")
    @MyLog(title = "新增交换机故障信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SwitchFailure switchFailure)
    {
        return toAjax(switchFailureService.insertSwitchFailure(switchFailure));
    }

    /**
     * 修改交换机故障
     */
    @ApiOperation("修改交换机故障信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "failureId", value = "故障编号", dataType = "String"),
            @ApiImplicitParam(name = "brand", value = "交换机品牌", dataType = "String"),
            @ApiImplicitParam(name = "switchType", value = "型号", dataType = "String"),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataType = "String"),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataType = "String"),
            @ApiImplicitParam(name = "failureKeyword", value = "故障关键字", dataType = "String"),
            @ApiImplicitParam(name = "failureName", value = "故障名称", dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('sql:switch_failure:edit')")
    @MyLog(title = "修改交换机故障信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SwitchFailure switchFailure)
    {
        return toAjax(switchFailureService.updateSwitchFailure(switchFailure));
    }

    /**
     * 删除交换机故障
     */
    @ApiOperation("删除交换机故障信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "failureIds", value = "故障编号", dataType = "String[]")
    })
    @PreAuthorize("@ss.hasPermi('sql:switch_failure:remove')")
    @MyLog(title = "删除交换机故障信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{failureIds}")
    public AjaxResult remove(@PathVariable String[] failureIds)
    {
        return toAjax(switchFailureService.deleteSwitchFailureByFailureIds(failureIds));
    }
}
