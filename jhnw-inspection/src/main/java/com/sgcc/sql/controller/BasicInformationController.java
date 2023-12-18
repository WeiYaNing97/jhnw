package com.sgcc.sql.controller;

import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.page.TableDataInfo;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.sql.domain.BasicInformation;
import com.sgcc.sql.service.IBasicInformationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取基本信息命令Controller
 * 获取基本信息命令相关操作
 */
@Api("获取基本信息命令管理")
@RestController
@RequestMapping("/sql/basic_information")
//事务
@Transactional(rollbackFor = Exception.class)
public class BasicInformationController extends BaseController
{
    @Autowired
    private IBasicInformationService basicInformationService;

    /**
     * 查询获取基本信息命令列表
     */
    @PreAuthorize("@ss.hasPermi('sql:basic_information:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasicInformation basicInformation)
    {
        startPage();
        List<BasicInformation> list = basicInformationService.selectBasicInformationList(basicInformation);
        return getDataTable(list);
    }

    /**
     * 导出获取基本信息命令列表
     */
    @PreAuthorize("@ss.hasPermi('sql:basic_information:export')")
    @MyLog(title = "获取基本信息命令", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(BasicInformation basicInformation)
    {
        List<BasicInformation> list = basicInformationService.selectBasicInformationList(basicInformation);
        ExcelUtil<BasicInformation> util = new ExcelUtil<BasicInformation>(BasicInformation.class);
        return util.exportExcel(list, "获取基本信息命令数据");
    }

    /**
     * 获取获取基本信息命令详细信息
     */
    @PreAuthorize("@ss.hasPermi('sql:basic_information:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(basicInformationService.selectBasicInformationById(id));
    }

    /**
     * 新增获取基本信息命令
     */
    @PreAuthorize("@ss.hasPermi('sql:basic_information:add')")
    @MyLog(title = "获取基本信息命令", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BasicInformation basicInformation)
    {
        return toAjax(basicInformationService.insertBasicInformation(basicInformation));
    }

    /**
     * 修改获取基本信息命令
     */
    @PreAuthorize("@ss.hasPermi('sql:basic_information:edit')")
    @MyLog(title = "获取基本信息命令", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BasicInformation basicInformation)
    {
        return toAjax(basicInformationService.updateBasicInformation(basicInformation));
    }

    /**
     * 删除获取基本信息命令
     */
    @PreAuthorize("@ss.hasPermi('sql:basic_information:remove')")
    @MyLog(title = "获取基本信息命令", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(basicInformationService.deleteBasicInformationByIds(ids));
    }

    /**
     * 查询获取基本信息命令列表
     */
    @ApiOperation("查询获取基本信息命令列表")
    @GetMapping("/getPojolist")
    public List<BasicInformation> getPojolist()
    {
        List<BasicInformation> list = basicInformationService.selectBasicInformationList(null);
        List<BasicInformation> pojolist = new ArrayList<>();
        for (BasicInformation basicInformation:list){
            BasicInformation pojo = new BasicInformation();
            pojo.setId(basicInformation.getId());
            pojo.setCommand(basicInformation.getCommand().replace("=:=","、").replace("\\[","\\ ["));
            pojo.setProblemId(basicInformation.getProblemId());
            pojolist.add(pojo);
        }
        return pojolist;
    }
}
