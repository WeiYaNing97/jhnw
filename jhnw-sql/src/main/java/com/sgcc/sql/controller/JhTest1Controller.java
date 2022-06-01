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
import com.sgcc.sql.domain.JhTest1;
import com.sgcc.sql.service.IJhTest1Service;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 前端 定义Controller
 * 
 * @author 王浩伟
 * @date 2022-06-01
 */
@RestController
@RequestMapping("/sql/jh_test1")
public class JhTest1Controller extends BaseController
{
    @Autowired
    private IJhTest1Service jhTest1Service;

    /**
     * 查询前端 定义列表
     */
    @PreAuthorize("@ss.hasPermi('sql:jh_test1:list')")
    @GetMapping("/list")
    public TableDataInfo list(JhTest1 jhTest1)
    {
        startPage();
        List<JhTest1> list = jhTest1Service.selectJhTest1List(jhTest1);
        return getDataTable(list);
    }

    /**
     * 导出前端 定义列表
     */
    @PreAuthorize("@ss.hasPermi('sql:jh_test1:export')")
    @Log(title = "前端 定义", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(JhTest1 jhTest1)
    {
        List<JhTest1> list = jhTest1Service.selectJhTest1List(jhTest1);
        ExcelUtil<JhTest1> util = new ExcelUtil<JhTest1>(JhTest1.class);
        return util.exportExcel(list, "前端 定义数据");
    }

    /**
     * 获取前端 定义详细信息
     */
    @PreAuthorize("@ss.hasPermi('sql:jh_test1:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(jhTest1Service.selectJhTest1ById(id));
    }

    /**
     * 新增前端 定义
     */
    @PreAuthorize("@ss.hasPermi('sql:jh_test1:add')")
    @Log(title = "前端 定义", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JhTest1 jhTest1)
    {
        return toAjax(jhTest1Service.insertJhTest1(jhTest1));
    }

    /**
     * 修改前端 定义
     */
    @PreAuthorize("@ss.hasPermi('sql:jh_test1:edit')")
    @Log(title = "前端 定义", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JhTest1 jhTest1)
    {
        return toAjax(jhTest1Service.updateJhTest1(jhTest1));
    }

    /**
     * 删除前端 定义
     */
    @PreAuthorize("@ss.hasPermi('sql:jh_test1:remove')")
    @Log(title = "前端 定义", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(jhTest1Service.deleteJhTest1ByIds(ids));
    }
}
