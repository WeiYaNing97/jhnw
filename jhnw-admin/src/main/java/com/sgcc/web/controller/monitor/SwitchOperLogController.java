package com.sgcc.web.controller.monitor;

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
import com.sgcc.system.domain.SwitchOperLog;
import com.sgcc.system.service.ISwitchOperLogService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 操作日志记录Controller
 *
 */
@RestController
@RequestMapping("/system/switch_oper_log")
public class SwitchOperLogController extends BaseController
{
    @Autowired
    private ISwitchOperLogService switchOperLogService;

    /**
     * 查询操作日志记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:switch_oper_log:list')")
    @GetMapping("/list")
    public TableDataInfo list(SwitchOperLog switchOperLog)
    {
        startPage();
        List<SwitchOperLog> list = switchOperLogService.selectSwitchOperLogList(switchOperLog);
        return getDataTable(list);
    }

    /**
     * 导出操作日志记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:switch_oper_log:export')")
    @Log(title = "操作日志记录", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SwitchOperLog switchOperLog)
    {
        List<SwitchOperLog> list = switchOperLogService.selectSwitchOperLogList(switchOperLog);
        ExcelUtil<SwitchOperLog> util = new ExcelUtil<SwitchOperLog>(SwitchOperLog.class);
        return util.exportExcel(list, "操作日志记录数据");
    }

    /**
     * 获取操作日志记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:switch_oper_log:query')")
    @GetMapping(value = "/{operId}")
    public AjaxResult getInfo(@PathVariable("operId") Long operId)
    {
        return AjaxResult.success(switchOperLogService.selectSwitchOperLogByOperId(operId));
    }

    /**
     * 新增操作日志记录
     */
    @PreAuthorize("@ss.hasPermi('system:switch_oper_log:add')")
    @Log(title = "操作日志记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SwitchOperLog switchOperLog)
    {

        return toAjax(switchOperLogService.insertSwitchOperLog(switchOperLog));
    }

    /**
     * 修改操作日志记录
     */
    @PreAuthorize("@ss.hasPermi('system:switch_oper_log:edit')")
    @Log(title = "操作日志记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SwitchOperLog switchOperLog)
    {
        return toAjax(switchOperLogService.updateSwitchOperLog(switchOperLog));
    }

    /**
     * 删除操作日志记录
     */
    @PreAuthorize("@ss.hasPermi('system:switch_oper_log:remove')")
    @Log(title = "操作日志记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{operIds}")
    public AjaxResult remove(@PathVariable Long[] operIds)
    {
        return toAjax(switchOperLogService.deleteSwitchOperLogByOperIds(operIds));
    }
}
