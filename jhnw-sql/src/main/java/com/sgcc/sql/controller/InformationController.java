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
import com.sgcc.sql.domain.Information;
import com.sgcc.sql.service.IInformationService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 交换机信息Controller
 * 
 * @author ruoyi
 * @date 2023-03-07
 */
@RestController
@RequestMapping("/sql/information")
public class InformationController extends BaseController
{
    @Autowired
    private IInformationService informationService;

    /**
     * 查询交换机信息列表
     */
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
    @PreAuthorize("@ss.hasPermi('sql:information:export')")
    @Log(title = "交换机信息", businessType = BusinessType.EXPORT)
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
    @PreAuthorize("@ss.hasPermi('sql:information:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(informationService.selectInformationById(id));
    }

    /**
     * 新增交换机信息
     */
    @PreAuthorize("@ss.hasPermi('sql:information:add')")
    @Log(title = "交换机信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Information information)
    {
        return toAjax(informationService.insertInformation(information));
    }

    /**
     * 修改交换机信息
     */
    @PreAuthorize("@ss.hasPermi('sql:information:edit')")
    @Log(title = "交换机信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Information information)
    {
        return toAjax(informationService.updateInformation(information));
    }

    /**
     * 删除交换机信息
     */
    @PreAuthorize("@ss.hasPermi('sql:information:remove')")
    @Log(title = "交换机信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(informationService.deleteInformationByIds(ids));
    }


    /**
     * 查询交换机品牌信息 去重处理
     */
    @GetMapping("/informationBrand")
    public List<String> getDeviceBrand()
    {
        List<String> list = informationService.selectDeviceBrandList();
        return list;
    }

    /**
     * 查询交换机品牌信息 去重处理
     */
    @GetMapping("/informationModel")
    public List<String> getDeviceModel(String brand)
    {
        List<String> list = informationService.selectDeviceModelList(brand);
        return list;
    }
}
