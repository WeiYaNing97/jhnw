package com.sgcc.share.controller;

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
import com.sgcc.share.domain.NonRelationalDataTable;
import com.sgcc.share.service.INonRelationalDataTableService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 非关系型数据 存储长数据字段Controller
 * 
 * @author ruoyi
 * @date 2024-05-22
 */
@RestController
@RequestMapping("/share/non_relational_data_table")
public class NonRelationalDataTableController extends BaseController
{
    @Autowired
    private INonRelationalDataTableService nonRelationalDataTableService;

    /**
     * 查询非关系型数据 存储长数据字段列表
     */
    @PreAuthorize("@ss.hasPermi('share:non_relational_data_table:list')")
    @GetMapping("/list")
    public TableDataInfo list(NonRelationalDataTable nonRelationalDataTable)
    {
        startPage();
        List<NonRelationalDataTable> list = nonRelationalDataTableService.selectNonRelationalDataTableList(nonRelationalDataTable);
        return getDataTable(list);
    }

    /**
     * 导出非关系型数据 存储长数据字段列表
     */
    @PreAuthorize("@ss.hasPermi('share:non_relational_data_table:export')")
    @Log(title = "非关系型数据 存储长数据字段", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(NonRelationalDataTable nonRelationalDataTable)
    {
        List<NonRelationalDataTable> list = nonRelationalDataTableService.selectNonRelationalDataTableList(nonRelationalDataTable);
        ExcelUtil<NonRelationalDataTable> util = new ExcelUtil<NonRelationalDataTable>(NonRelationalDataTable.class);
        return util.exportExcel(list, "非关系型数据 存储长数据字段数据");
    }

    /**
     * 获取非关系型数据 存储长数据字段详细信息
     */
    @PreAuthorize("@ss.hasPermi('share:non_relational_data_table:query')")
    @GetMapping(value = "/{nonRelationalId}")
    public AjaxResult getInfo(@PathVariable("nonRelationalId") String nonRelationalId)
    {
        return AjaxResult.success(nonRelationalDataTableService.selectNonRelationalDataTableByNonRelationalId(nonRelationalId));
    }

    /**
     * 新增非关系型数据 存储长数据字段
     */
    @PreAuthorize("@ss.hasPermi('share:non_relational_data_table:add')")
    @Log(title = "非关系型数据 存储长数据字段", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody NonRelationalDataTable nonRelationalDataTable)
    {
        return toAjax(nonRelationalDataTableService.insertNonRelationalDataTable(nonRelationalDataTable));
    }

    /**
     * 修改非关系型数据 存储长数据字段
     */
    @PreAuthorize("@ss.hasPermi('share:non_relational_data_table:edit')")
    @Log(title = "非关系型数据 存储长数据字段", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody NonRelationalDataTable nonRelationalDataTable)
    {
        return toAjax(nonRelationalDataTableService.updateNonRelationalDataTable(nonRelationalDataTable));
    }

    /**
     * 删除非关系型数据 存储长数据字段
     */
    @PreAuthorize("@ss.hasPermi('share:non_relational_data_table:remove')")
    @Log(title = "非关系型数据 存储长数据字段", businessType = BusinessType.DELETE)
	@DeleteMapping("/{nonRelationalIds}")
    public AjaxResult remove(@PathVariable String[] nonRelationalIds)
    {
        return toAjax(nonRelationalDataTableService.deleteNonRelationalDataTableByNonRelationalIds(nonRelationalIds));
    }
}
