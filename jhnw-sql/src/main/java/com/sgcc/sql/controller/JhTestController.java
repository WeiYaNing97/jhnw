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
import com.sgcc.sql.domain.JhTest;
import com.sgcc.sql.service.IJhTestService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 前端 扫描页Controller
 * 
 * @author 王浩伟
 * @date 2022-06-01
 */
@RestController
@RequestMapping("/sql/jh_test")
public class JhTestController extends BaseController
{
    @Autowired
    private IJhTestService jhTestService;

    /**
     * 查询前端 扫描页列表
     */
    @PreAuthorize("@ss.hasPermi('sql:jh_test:list')")
    @GetMapping("/list")
    public TableDataInfo list(JhTest jhTest)
    {
        startPage();
        List<JhTest> list = jhTestService.selectJhTestList(jhTest);
        return getDataTable(list);
    }

    /**
     * 导出前端 扫描页列表
     */
    @PreAuthorize("@ss.hasPermi('sql:jh_test:export')")
    @Log(title = "前端 扫描页", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(JhTest jhTest)
    {
        List<JhTest> list = jhTestService.selectJhTestList(jhTest);
        ExcelUtil<JhTest> util = new ExcelUtil<JhTest>(JhTest.class);
        return util.exportExcel(list, "前端 扫描页数据");
    }

    /**
     * 获取前端 扫描页详细信息
     */
    @PreAuthorize("@ss.hasPermi('sql:jh_test:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(jhTestService.selectJhTestById(id));
    }

    /**
     * 新增前端 扫描页
     */
    @PreAuthorize("@ss.hasPermi('sql:jh_test:add')")
    @Log(title = "前端 扫描页", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JhTest jhTest)
    {
        return toAjax(jhTestService.insertJhTest(jhTest));
    }

    /**
     * 修改前端 扫描页
     */
    @PreAuthorize("@ss.hasPermi('sql:jh_test:edit')")
    @Log(title = "前端 扫描页", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JhTest jhTest)
    {
        return toAjax(jhTestService.updateJhTest(jhTest));
    }

    /**
     * 删除前端 扫描页
     */
    @PreAuthorize("@ss.hasPermi('sql:jh_test:remove')")
    @Log(title = "前端 扫描页", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(jhTestService.deleteJhTestByIds(ids));
    }
}
