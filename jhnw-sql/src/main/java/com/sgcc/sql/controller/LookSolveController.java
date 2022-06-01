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
import com.sgcc.sql.domain.LookSolve;
import com.sgcc.sql.service.ILookSolveService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 前端 回显解决问题命令页面Controller
 * 
 * @author 王浩伟
 * @date 2022-06-01
 */
@RestController
@RequestMapping("/sql/look_solve")
public class LookSolveController extends BaseController
{
    @Autowired
    private ILookSolveService lookSolveService;

    /**
     * 查询前端 回显解决问题命令页面列表
     */
    @PreAuthorize("@ss.hasPermi('sql:look_solve:list')")
    @GetMapping("/list")
    public TableDataInfo list(LookSolve lookSolve)
    {
        startPage();
        List<LookSolve> list = lookSolveService.selectLookSolveList(lookSolve);
        return getDataTable(list);
    }

    /**
     * 导出前端 回显解决问题命令页面列表
     */
    @PreAuthorize("@ss.hasPermi('sql:look_solve:export')")
    @Log(title = "前端 回显解决问题命令页面", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(LookSolve lookSolve)
    {
        List<LookSolve> list = lookSolveService.selectLookSolveList(lookSolve);
        ExcelUtil<LookSolve> util = new ExcelUtil<LookSolve>(LookSolve.class);
        return util.exportExcel(list, "前端 回显解决问题命令页面数据");
    }

    /**
     * 获取前端 回显解决问题命令页面详细信息
     */
    @PreAuthorize("@ss.hasPermi('sql:look_solve:query')")
    @GetMapping(value = "/{command}")
    public AjaxResult getInfo(@PathVariable("command") String command)
    {
        return AjaxResult.success(lookSolveService.selectLookSolveByCommand(command));
    }

    /**
     * 新增前端 回显解决问题命令页面
     */
    @PreAuthorize("@ss.hasPermi('sql:look_solve:add')")
    @Log(title = "前端 回显解决问题命令页面", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LookSolve lookSolve)
    {
        return toAjax(lookSolveService.insertLookSolve(lookSolve));
    }

    /**
     * 修改前端 回显解决问题命令页面
     */
    @PreAuthorize("@ss.hasPermi('sql:look_solve:edit')")
    @Log(title = "前端 回显解决问题命令页面", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LookSolve lookSolve)
    {
        return toAjax(lookSolveService.updateLookSolve(lookSolve));
    }

    /**
     * 删除前端 回显解决问题命令页面
     */
    @PreAuthorize("@ss.hasPermi('sql:look_solve:remove')")
    @Log(title = "前端 回显解决问题命令页面", businessType = BusinessType.DELETE)
	@DeleteMapping("/{commands}")
    public AjaxResult remove(@PathVariable String[] commands)
    {
        return toAjax(lookSolveService.deleteLookSolveByCommands(commands));
    }
}
