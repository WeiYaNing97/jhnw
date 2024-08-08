package com.sgcc.sql.controller;
import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.page.TableDataInfo;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.DateUtils;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.ISwitchProblemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 交换机问题Controller
 */
@Api(tags = "交换机问题管理(未用)")
@RestController
@RequestMapping("/sql/switch_problem")
public class SwitchProblemController extends BaseController
{
    @Autowired
    private ISwitchProblemService switchProblemService;

    /**
     * 查询交换机问题列表
     */
    @ApiOperation(value = "查询交换机问题列表")
    @PreAuthorize("@ss.hasPermi('sql:switch_problem:list')")
    @GetMapping("/list")
    public TableDataInfo list(SwitchProblem switchProblem)
    {
        startPage();
        List<SwitchProblem> list = switchProblemService.selectSwitchProblemList(switchProblem);
        return getDataTable(list);
    }

    /**
     * 导出交换机问题列表
     */
    @ApiOperation(value = "导出交换机问题列表")
    @PreAuthorize("@ss.hasPermi('sql:switch_problem:export')")
    @MyLog(title = "交换机问题", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SwitchProblem switchProblem)
    {
        List<SwitchProblem> list = switchProblemService.selectSwitchProblemList(switchProblem);
        ExcelUtil<SwitchProblem> util = new ExcelUtil<SwitchProblem>(SwitchProblem.class);
        return util.exportExcel(list, "交换机问题数据");
    }

    /**
     * 获取交换机问题详细信息
     */
    @ApiOperation(value = "获取交换机问题详细信息")
    @PreAuthorize("@ss.hasPermi('sql:switch_problem:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(switchProblemService.selectSwitchProblemById(id));
    }

    /**
     * 新增交换机问题
     */
    @ApiOperation(value = "新增交换机问题")
    @PreAuthorize("@ss.hasPermi('sql:switch_problem:add')")
    @MyLog(title = "交换机问题", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SwitchProblem switchProblem)
    {
        switchProblem.setCreateTime(DateUtils.getNowDate());
        return toAjax(switchProblemService.insertSwitchProblem(switchProblem));
    }

    /**
     * 修改交换机问题
     */
    @ApiOperation(value = "修改交换机问题")
    @PreAuthorize("@ss.hasPermi('sql:switch_problem:edit')")
    @MyLog(title = "交换机问题", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SwitchProblem switchProblem)
    {
        return toAjax(switchProblemService.updateSwitchProblem(switchProblem));
    }

    /**
     * 删除交换机问题
     */
    @ApiOperation(value = "删除交换机问题")
    @PreAuthorize("@ss.hasPermi('sql:switch_problem:remove')")
    @MyLog(title = "交换机问题", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(switchProblemService.deleteSwitchProblemByIds(ids));
    }

}
