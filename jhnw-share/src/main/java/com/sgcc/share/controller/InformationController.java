package com.sgcc.share.controller;

import java.util.List;

import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.share.domain.Information;
import com.sgcc.share.service.IInformationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 交换机信息Controller
 * 
 * @author ruoyi
 * @date 2023-03-07
 */
@Api(tags = "交换机品牌、型号管理")
@RestController
@RequestMapping("/share/information")
public class InformationController extends BaseController
{
    @Autowired
    private IInformationService informationService;

    /**
     * 查询交换机信息列表
     */
    @ApiOperation("查询交换机品牌、型号列表")
    @PreAuthorize("@ss.hasPermi('sql:information:list')")
    @GetMapping("/list")
    public TableDataInfo list(Information information)
    {
        startPage();
        List<Information> list = informationService.selectInformationList(information);
        return getDataTable(list);
    }

    /**
     * 导出交换机信息列表
     */
    @ApiOperation("导出交换机品牌、型号列表")
    @PreAuthorize("@ss.hasPermi('sql:information:export')")
    @MyLog(title = "交换机信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(Information information)
    {
        List<Information> list = informationService.selectInformationList(information);
        ExcelUtil<Information> util = new ExcelUtil<Information>(Information.class);
        return util.exportExcel(list, "交换机信息数据");
    }

    /**
     * 获取交换机信息详细信息
     */
    @ApiOperation("获取交换机品牌、型号详细信息")
    @PreAuthorize("@ss.hasPermi('sql:information:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(informationService.selectInformationById(id));
    }

    /**
     * 新增交换机信息
     */
    @ApiOperation("新增交换机品牌、型号")
    @PreAuthorize("@ss.hasPermi('sql:information:add')")
    @MyLog(title = "交换机信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Information information)
    {
        return toAjax(informationService.insertInformation(information));
    }

    /**
     * 修改交换机信息
     */
    @ApiOperation("修改交换机品牌、型号")
    @PreAuthorize("@ss.hasPermi('sql:information:edit')")
    @MyLog(title = "交换机信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Information information)
    {
        return toAjax(informationService.updateInformation(information));
    }

    /**
     * 删除交换机信息
     */
    @ApiOperation("删除交换机品牌、型号")
    @PreAuthorize("@ss.hasPermi('sql:information:remove')")
    @MyLog(title = "交换机信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(informationService.deleteInformationByIds(ids));
    }


    /**
     * 查询交换机品牌信息 去重处理
     */
    public List<String> getDeviceBrand()
    {
        return informationService.selectDeviceBrandList();
    }

    /**
     * 查询交换机品牌信息 去重处理
     */
    public List<String> getDeviceModel(String brand)
    {
        return informationService.selectDeviceModelList(brand);
    }
}
