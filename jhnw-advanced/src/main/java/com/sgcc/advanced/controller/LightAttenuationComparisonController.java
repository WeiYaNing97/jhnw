package com.sgcc.advanced.controller;

import com.sgcc.advanced.domain.LightAttenuationComparison;
import com.sgcc.advanced.service.ILightAttenuationComparisonService;
import com.sgcc.advanced.utils.Utils;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.share.connectutil.SpringBeanUtil;
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

import java.util.List;

/**
 * 光衰平均值比较Controller
 * 
 * @author ruoyi
 * @date 2023-05-06
 */
@RestController
@RequestMapping("/advanced/comparison")
public class LightAttenuationComparisonController extends BaseController
{
    @Autowired
    private ILightAttenuationComparisonService lightAttenuationComparisonService;

    /**
     * 查询 光衰平均值 比较列表
     */
    @PreAuthorize("@ss.hasPermi('advanced:comparison:list')")
    @GetMapping("/list")
    public TableDataInfo list(LightAttenuationComparison lightAttenuationComparison)
    {
        startPage();
        List<LightAttenuationComparison> list = lightAttenuationComparisonService.selectLightAttenuationComparisonList(lightAttenuationComparison);
        return getDataTable(list);
    }

    /**
     * 导出光衰平均值比较列表
     */
    @PreAuthorize("@ss.hasPermi('advanced:comparison:export')")
    @Log(title = "光衰平均值比较", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(LightAttenuationComparison lightAttenuationComparison)
    {
        List<LightAttenuationComparison> list = lightAttenuationComparisonService.selectLightAttenuationComparisonList(lightAttenuationComparison);
        ExcelUtil<LightAttenuationComparison> util = new ExcelUtil<LightAttenuationComparison>(LightAttenuationComparison.class);
        return util.exportExcel(list, "光衰平均值比较数据");
    }

    /**
     * 获取光衰平均值比较详细信息
     */
    @PreAuthorize("@ss.hasPermi('advanced:comparison:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(lightAttenuationComparisonService.selectLightAttenuationComparisonById(id));
    }

    /**
     * 新增光衰平均值比较
     */
    @PreAuthorize("@ss.hasPermi('advanced:comparison:add')")
    @Log(title = "光衰平均值比较", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LightAttenuationComparison lightAttenuationComparison)
    {
        return toAjax(lightAttenuationComparisonService.insertLightAttenuationComparison(lightAttenuationComparison));
    }

    /**
     * 修改光衰平均值比较
     */
    @PreAuthorize("@ss.hasPermi('advanced:comparison:edit')")
    @Log(title = "光衰平均值比较", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LightAttenuationComparison lightAttenuationComparison)
    {
        return toAjax(lightAttenuationComparisonService.updateLightAttenuationComparison(lightAttenuationComparison));
    }

    /**
     * 删除光衰平均值比较
     */
    @PreAuthorize("@ss.hasPermi('advanced:comparison:remove')")
    @Log(title = "光衰平均值比较", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(lightAttenuationComparisonService.deleteLightAttenuationComparisonByIds(ids));
    }

    /**
     * 修改光衰平均值比较
     */
    @PreAuthorize("@ss.hasPermi('advanced:comparison:edit')")
    @Log(title = "光衰平均值比较", businessType = BusinessType.UPDATE)
    @PutMapping("/reset")
    public AjaxResult reset(@RequestBody LightAttenuationComparison lightAttenuationComparison)
    {
        /*根据实体类ID 查询数据库中的实体类*/
        lightAttenuationComparisonService = SpringBeanUtil.getBean(ILightAttenuationComparisonService.class);
        LightAttenuationComparison pojo = lightAttenuationComparisonService.selectLightAttenuationComparisonById(lightAttenuationComparison.getId());
        /*查看前端传入数据是否有修改
        * 如果没有修改，则默认 当前参数设置为 基准和平均值*/
        if (Utils.LightAttenuationComparisonEquals(lightAttenuationComparison,pojo)){
            /* 数量设置为 1 */
            lightAttenuationComparison.setNumberParameters(1);
            /* 基准 */
            lightAttenuationComparison.setTxStartValue(lightAttenuationComparison.getTxLatestNumber());
            lightAttenuationComparison.setRxStartValue(lightAttenuationComparison.getRxLatestNumber());
            /* 平均值 */
            lightAttenuationComparison.setTxAverageValue(lightAttenuationComparison.getTxLatestNumber());
            lightAttenuationComparison.setRxAverageValue(lightAttenuationComparison.getRxLatestNumber());
            return toAjax(lightAttenuationComparisonService.updateLightAttenuationComparison(lightAttenuationComparison));
        }
        /* 数量设置为 1 */
        lightAttenuationComparison.setNumberParameters(1);
        return toAjax(lightAttenuationComparisonService.updateLightAttenuationComparison(lightAttenuationComparison));
    }
}
