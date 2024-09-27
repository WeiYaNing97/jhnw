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
import com.sgcc.share.domain.ErrorCodeTable;
import com.sgcc.share.service.IErrorCodeTableService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 错误代码Controller
 * 
 * @author ruoyi
 * @date 2024-09-27
 */
@RestController
@RequestMapping("/share/ErrorCodeTable")
public class ErrorCodeTableController extends BaseController
{
    @Autowired
    private IErrorCodeTableService errorCodeTableService;

    /**
     * 查询错误代码列表
     */
    @PreAuthorize("@ss.hasPermi('share:ErrorCodeTable:list')")
    @GetMapping("/list")
    public TableDataInfo list(ErrorCodeTable errorCodeTable)
    {
        startPage();
        List<ErrorCodeTable> list = errorCodeTableService.selectErrorCodeTableList(errorCodeTable);
        return getDataTable(list);
    }

    /**
     * 导出错误代码列表
     */
    @PreAuthorize("@ss.hasPermi('share:ErrorCodeTable:export')")
    @Log(title = "错误代码", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(ErrorCodeTable errorCodeTable)
    {
        List<ErrorCodeTable> list = errorCodeTableService.selectErrorCodeTableList(errorCodeTable);
        ExcelUtil<ErrorCodeTable> util = new ExcelUtil<ErrorCodeTable>(ErrorCodeTable.class);
        return util.exportExcel(list, "错误代码数据");
    }

    /**
     * 获取错误代码详细信息
     */
    @PreAuthorize("@ss.hasPermi('share:ErrorCodeTable:query')")
    @GetMapping(value = "/{errorCodeNumber}")
    public AjaxResult getInfo(@PathVariable("errorCodeNumber") String errorCodeNumber)
    {
        ErrorCodeTable errorCodeTable = errorCodeTableService.selectErrorCodeTableByErrorCodeNumber(errorCodeNumber);
        if (errorCodeTable == null){
            return AjaxResult.error("未知错误,错误代码不存在!");
        }
        return AjaxResult.success(errorCodeTable);
    }

    /**
     * 新增错误代码
     */
    @PreAuthorize("@ss.hasPermi('share:ErrorCodeTable:add')")
    @Log(title = "错误代码", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ErrorCodeTable errorCodeTable)
    {
        return toAjax(errorCodeTableService.insertErrorCodeTable(errorCodeTable));
    }

    /**
     * 修改错误代码
     */
    @PreAuthorize("@ss.hasPermi('share:ErrorCodeTable:edit')")
    @Log(title = "错误代码", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ErrorCodeTable errorCodeTable)
    {
        return toAjax(errorCodeTableService.updateErrorCodeTable(errorCodeTable));
    }

    /**
     * 删除错误代码
     */
    @PreAuthorize("@ss.hasPermi('share:ErrorCodeTable:remove')")
    @Log(title = "错误代码", businessType = BusinessType.DELETE)
	@DeleteMapping("/{errorCodeNumbers}")
    public AjaxResult remove(@PathVariable String[] errorCodeNumbers)
    {
        return toAjax(errorCodeTableService.deleteErrorCodeTableByErrorCodeNumbers(errorCodeNumbers));
    }
}
