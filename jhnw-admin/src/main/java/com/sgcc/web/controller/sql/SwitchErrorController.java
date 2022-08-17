package com.sgcc.web.controller.sql;
import java.util.List;

import com.sgcc.connect.util.SpringBeanUtil;
import com.sgcc.sql.service.ISwitchFailureService;
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
import com.sgcc.sql.domain.SwitchError;
import com.sgcc.sql.service.ISwitchErrorService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 交换机错误Controller
 * 
 * @author ruoyi
 * @date 2022-08-16
 */
@RestController
@RequestMapping("/sql/switch_error")
public class SwitchErrorController extends BaseController
{
    @Autowired
    private ISwitchErrorService switchErrorService;

    /**
     * 查询交换机错误列表
     */
    @GetMapping("/switchErrorList")
    public List<SwitchError> selectSwitchErrorListByPojo(SwitchError switchError)
    {
        switchErrorService = SpringBeanUtil.getBean(ISwitchErrorService.class);
        List<SwitchError> list = switchErrorService.selectSwitchErrorListByPojo(switchError);
        return list;
    }

    /**
     * 查询交换机错误列表
     */
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
    @PreAuthorize("@ss.hasPermi('sql:switch_error:export')")
    @Log(title = "交换机错误", businessType = BusinessType.EXPORT)
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
    @PreAuthorize("@ss.hasPermi('sql:switch_error:query')")
    @GetMapping(value = "/{errorId}")
    public AjaxResult getInfo(@PathVariable("errorId") Long errorId)
    {
        return AjaxResult.success(switchErrorService.selectSwitchErrorByErrorId(errorId));
    }

    /**
     * 新增交换机错误
     */
    @PreAuthorize("@ss.hasPermi('sql:switch_error:add')")
    @Log(title = "交换机错误", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SwitchError switchError)
    {
        return toAjax(switchErrorService.insertSwitchError(switchError));
    }

    /**
     * 修改交换机错误
     */
    @PreAuthorize("@ss.hasPermi('sql:switch_error:edit')")
    @Log(title = "交换机错误", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SwitchError switchError)
    {
        return toAjax(switchErrorService.updateSwitchError(switchError));
    }

    /**
     * 删除交换机错误
     */
    @PreAuthorize("@ss.hasPermi('sql:switch_error:remove')")
    @Log(title = "交换机错误", businessType = BusinessType.DELETE)
	@DeleteMapping("/{errorIds}")
    public AjaxResult remove(@PathVariable Long[] errorIds)
    {
        return toAjax(switchErrorService.deleteSwitchErrorByErrorIds(errorIds));
    }
}
