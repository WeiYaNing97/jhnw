package com.sgcc.sql.controller;

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
import com.sgcc.sql.domain.SwitchFailure;
import com.sgcc.sql.service.ISwitchFailureService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 交换机故障Controller
 * 
 * @author ruoyi
 * @date 2022-07-26
 */
@RestController
@RequestMapping("/sql/switch_failure")
public class SwitchFailureController extends BaseController
{
    @Autowired
    private ISwitchFailureService switchFailureService;

    /**
     * 查询交换机故障列表
     */
    @PreAuthorize("@ss.hasPermi('sql:switch_failure:list')")
    @GetMapping("/list")
    public TableDataInfo list(SwitchFailure switchFailure)
    {
        startPage();
        List<SwitchFailure> list = switchFailureService.selectSwitchFailureList(switchFailure);
        return getDataTable(list);
    }

    /**
     * 导出交换机故障列表
     */
    @PreAuthorize("@ss.hasPermi('sql:switch_failure:export')")
    @Log(title = "交换机故障", businessType = BusinessType.EXPORT)
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
    @PreAuthorize("@ss.hasPermi('sql:switch_failure:query')")
    @GetMapping(value = "/{failureId}")
    public AjaxResult getInfo(@PathVariable("failureId") Long failureId)
    {
        return AjaxResult.success(switchFailureService.selectSwitchFailureByFailureId(failureId));
    }

    /**
     * 新增交换机故障
     */
    @PreAuthorize("@ss.hasPermi('sql:switch_failure:add')")
    @Log(title = "交换机故障", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SwitchFailure switchFailure)
    {
        return toAjax(switchFailureService.insertSwitchFailure(switchFailure));
    }

    /**
     * 修改交换机故障
     */
    @PreAuthorize("@ss.hasPermi('sql:switch_failure:edit')")
    @Log(title = "交换机故障", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SwitchFailure switchFailure)
    {
        return toAjax(switchFailureService.updateSwitchFailure(switchFailure));
    }

    /**
     * 删除交换机故障
     */
    @PreAuthorize("@ss.hasPermi('sql:switch_failure:remove')")
    @Log(title = "交换机故障", businessType = BusinessType.DELETE)
	@DeleteMapping("/{failureIds}")
    public AjaxResult remove(@PathVariable Long[] failureIds)
    {
        return toAjax(switchFailureService.deleteSwitchFailureByFailureIds(failureIds));
    }
}
