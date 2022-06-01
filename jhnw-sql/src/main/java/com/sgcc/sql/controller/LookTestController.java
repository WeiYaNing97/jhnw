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
import com.sgcc.sql.domain.LookTest;
import com.sgcc.sql.service.ILookTestService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 前端 回显定义问题页面Controller
 * 
 * @author 王浩伟
 * @date 2022-06-01
 */
@RestController
@RequestMapping("/sql/look_test")
public class LookTestController extends BaseController
{
    @Autowired
    private ILookTestService lookTestService;

    /**
     * 查询前端 回显定义问题页面列表
     */
    @PreAuthorize("@ss.hasPermi('sql:look_test:list')")
    @GetMapping("/list")
    public TableDataInfo list(LookTest lookTest)
    {
        startPage();
        List<LookTest> list = lookTestService.selectLookTestList(lookTest);
        return getDataTable(list);
    }

    /**
     * 导出前端 回显定义问题页面列表
     */
    @PreAuthorize("@ss.hasPermi('sql:look_test:export')")
    @Log(title = "前端 回显定义问题页面", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(LookTest lookTest)
    {
        List<LookTest> list = lookTestService.selectLookTestList(lookTest);
        ExcelUtil<LookTest> util = new ExcelUtil<LookTest>(LookTest.class);
        return util.exportExcel(list, "前端 回显定义问题页面数据");
    }

    /**
     * 获取前端 回显定义问题页面详细信息
     */
    @PreAuthorize("@ss.hasPermi('sql:look_test:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(lookTestService.selectLookTestById(id));
    }

    /**
     * 新增前端 回显定义问题页面
     */
    @PreAuthorize("@ss.hasPermi('sql:look_test:add')")
    @Log(title = "前端 回显定义问题页面", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LookTest lookTest)
    {
        return toAjax(lookTestService.insertLookTest(lookTest));
    }

    /**
     * 修改前端 回显定义问题页面
     */
    @PreAuthorize("@ss.hasPermi('sql:look_test:edit')")
    @Log(title = "前端 回显定义问题页面", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LookTest lookTest)
    {
        return toAjax(lookTestService.updateLookTest(lookTest));
    }

    /**
     * 删除前端 回显定义问题页面
     */
    @PreAuthorize("@ss.hasPermi('sql:look_test:remove')")
    @Log(title = "前端 回显定义问题页面", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(lookTestService.deleteLookTestByIds(ids));
    }
}
