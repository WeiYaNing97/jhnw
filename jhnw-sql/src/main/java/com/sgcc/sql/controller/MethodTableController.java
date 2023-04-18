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
import com.sgcc.sql.domain.MethodTable;
import com.sgcc.sql.service.IMethodTableService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 方法Controller
 * 
 * @author ruoyi
 * @date 2023-04-13
 */
@RestController
@RequestMapping("/sql/MethodTable")
public class MethodTableController extends BaseController
{
    @Autowired
    private IMethodTableService methodTableService;

    /**
     * 查询方法列表
     */
    @PreAuthorize("@ss.hasPermi('sql:MethodTable:list')")
    @GetMapping("/list")
    public TableDataInfo list(MethodTable methodTable)
    {
        startPage();
        List<MethodTable> list = methodTableService.selectMethodTableList(methodTable);
        return getDataTable(list);
    }

    /**
     * 导出方法列表
     */
    @PreAuthorize("@ss.hasPermi('sql:MethodTable:export')")
    @Log(title = "方法", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(MethodTable methodTable)
    {
        List<MethodTable> list = methodTableService.selectMethodTableList(methodTable);
        ExcelUtil<MethodTable> util = new ExcelUtil<MethodTable>(MethodTable.class);
        return util.exportExcel(list, "方法数据");
    }

    /**
     * 获取方法详细信息
     */
    @PreAuthorize("@ss.hasPermi('sql:MethodTable:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(methodTableService.selectMethodTableById(id));
    }

    /**
     * 新增方法
     */
    @PreAuthorize("@ss.hasPermi('sql:MethodTable:add')")
    @Log(title = "方法", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MethodTable methodTable)
    {
        return toAjax(methodTableService.insertMethodTable(methodTable));
    }

    /**
     * 修改方法
     */
    @PreAuthorize("@ss.hasPermi('sql:MethodTable:edit')")
    @Log(title = "方法", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MethodTable methodTable)
    {
        return toAjax(methodTableService.updateMethodTable(methodTable));
    }

    /**
     * 删除方法
     */
    @PreAuthorize("@ss.hasPermi('sql:MethodTable:remove')")
    @Log(title = "方法", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(methodTableService.deleteMethodTableByIds(ids));
    }
}
