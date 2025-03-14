package com.sgcc.sql.controller;
import java.util.List;
import java.util.stream.Collectors;
import com.sgcc.common.annotation.MyLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hpsf.Array;
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
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.sql.domain.Formwork;
import com.sgcc.sql.service.IFormworkService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 问题模板Controller
 * @author ruoyi
 * @date 2023-03-13
 */
@Api(tags = "问题模板管理")
@RestController
@RequestMapping("/sql/formwork")
public class FormworkController extends BaseController
{
    @Autowired
    private IFormworkService formworkService;

    /**
     * 查询问题模板列表
     */
    @ApiOperation(value = "查询问题模板列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataTypeClass = Long.class, required = true),
            @ApiImplicitParam(name = "formworkName", value = "模板名称", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "formworkIndex", value = "模板索引", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "reserve", value = "预留1", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "reservetwo", value = "预留2", dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:formwork:list')")
    @GetMapping("/list")
    public TableDataInfo list(Formwork formwork)
    {
        startPage();
        List<Formwork> list = formworkService.selectFormworkList(formwork);
        return getDataTable(list);
    }

    /**
     * 查询问题模板名字列表
     */
    @ApiOperation(value = "查询问题模板名字列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataTypeClass = Long.class, required = true),
            @ApiImplicitParam(name = "formworkName", value = "模板名称", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "formworkIndex", value = "模板索引", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "reserve", value = "预留1", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "reservetwo", value = "预留2", dataTypeClass = String.class, required = true)
    })
    @GetMapping("/getNameList")
    public List<String> getNameList(Formwork formwork)
    {
        List<Formwork> list = formworkService.selectFormworkList(formwork);
        List<String> collect = list.stream().map(t -> t.getFormworkName()).collect(Collectors.toList());
        return collect;
    }

    /**
     * 查询问题模板列表
     */
    @ApiOperation(value = "查询问题模板列表")
    @ApiImplicitParams(value = {@ApiImplicitParam(name = "formworkName", value = "模板名称", dataTypeClass = String.class, required = true)
    })
    @GetMapping("/pojoByformworkName")
    public Formwork pojoByformworkName(String formworkName)
    {
        Formwork formwork = new Formwork();
        formwork.setFormworkName(formworkName);
        List<Formwork> list = formworkService.selectFormworkList(formwork);
        return list.get(0);
    }

    /**
     * 导出问题模板列表
     */
    @ApiOperation(value = "导出问题模板列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataTypeClass = Long.class, required = true),
            @ApiImplicitParam(name = "formworkName", value = "模板名称", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "formworkIndex", value = "模板索引", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "reserve", value = "预留1", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "reservetwo", value = "预留2", dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:formwork:export')")
    @MyLog(title = "问题模板", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(Formwork formwork)
    {
        List<Formwork> list = formworkService.selectFormworkList(formwork);
        ExcelUtil<Formwork> util = new ExcelUtil<Formwork>(Formwork.class);
        return util.exportExcel(list, "问题模板数据");
    }

    /**
     * 获取问题模板详细信息
     */
    @ApiOperation(value = "获取问题模板详细信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataTypeClass = Long.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:formwork:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(formworkService.selectFormworkById(id));
    }

    /**
     * 获取问题模板详细信息
     */
    @ApiOperation(value = "获取问题模板详细信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "ids", value = "主键", dataTypeClass = Array.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:formwork:query')")
    @GetMapping(value = "/getInfoByIds/{ids}")
    public AjaxResult getInfoByIds(@PathVariable("ids") Long[] ids)
    {
        return AjaxResult.success(formworkService.selectFormworkByIds(ids));
    }

    /**
     * 新增问题模板
     */
    @ApiOperation(value = "新增问题模板")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataTypeClass = Long.class, required = true),
            @ApiImplicitParam(name = "formworkName", value = "模板名称", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "formworkIndex", value = "模板索引", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "reserve", value = "预留1", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "reservetwo", value = "预留2", dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:formwork:add')")
    @MyLog(title = "问题模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Formwork formwork)
    {
        return toAjax(formworkService.insertFormwork(formwork));
    }

    /**
     * 修改问题模板
     */
    @ApiOperation(value = "修改问题模板")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataTypeClass = Long.class, required = true),
            @ApiImplicitParam(name = "formworkName", value = "模板名称", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "formworkIndex", value = "模板索引", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "reserve", value = "预留1", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "reservetwo", value = "预留2", dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:formwork:edit')")
    @MyLog(title = "问题模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Formwork formwork)
    {
        return toAjax(formworkService.updateFormwork(formwork));
    }

    /**
     * 删除问题模板
     */
    @ApiOperation(value = "删除问题模板")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "ids", value = "主键", dataTypeClass = Array.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:formwork:remove')")
    @MyLog(title = "问题模板", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(formworkService.deleteFormworkByIds(ids));
    }
}
