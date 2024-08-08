package com.sgcc.advanced.controller;

import com.sgcc.advanced.domain.ErrorRate;
import com.sgcc.advanced.service.IErrorRateService;
import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.utils.poi.ExcelUtil;
import io.swagger.annotations.Api;
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
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.core.page.TableDataInfo;

import java.util.List;

/**
 * 错误包Controller
 * @author ruoyi
 * @date 2023-05-09
 */
@Api("错误包Controller")
@RestController
@RequestMapping("/advanced/rate")
public class ErrorRateController extends BaseController
{
    @Autowired
    private IErrorRateService errorRateService;
    /**
     * 查询错误包列表
     */
    @ApiOperation("查询错误包列表")
    @PreAuthorize("@ss.hasPermi('advanced:rate:list')")
    @GetMapping("/list")
    public TableDataInfo list(ErrorRate errorRate)
    {
        startPage();
        List<ErrorRate> list = errorRateService.selectErrorRateList(errorRate);
        return getDataTable(list);
    }
    /**
     * 导出错误包列表
     */
    @ApiOperation("导出错误包列表")
    @PreAuthorize("@ss.hasPermi('advanced:rate:export')")
    @MyLog(title = "错误包", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(ErrorRate errorRate)
    {
        List<ErrorRate> list = errorRateService.selectErrorRateList(errorRate);
        ExcelUtil<ErrorRate> util = new ExcelUtil<ErrorRate>(ErrorRate.class);
        return util.exportExcel(list, "错误包数据");
    }

    /**
     * 获取错误包详细信息
     */
    @ApiOperation("获取错误包详细信息")
    @PreAuthorize("@ss.hasPermi('advanced:rate:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(errorRateService.selectErrorRateById(id));
    }

    /**
     * 新增错误包
     */
    @ApiOperation("新增错误包")
    @PreAuthorize("@ss.hasPermi('advanced:rate:add')")
    @MyLog(title = "错误包", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ErrorRate errorRate)
    {
        return toAjax(errorRateService.insertErrorRate(errorRate));
    }

    /**
     * 修改错误包
     */
    @ApiOperation("修改错误包")
    @PreAuthorize("@ss.hasPermi('advanced:rate:edit')")
    @MyLog(title = "错误包", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ErrorRate errorRate)
    {
        return toAjax(errorRateService.updateErrorRate(errorRate));
    }

    /**
     * 删除错误包
     */
    @ApiOperation("删除错误包")
    @PreAuthorize("@ss.hasPermi('advanced:rate:remove')")
    @MyLog(title = "错误包", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(errorRateService.deleteErrorRateByIds(ids));
    }
}
