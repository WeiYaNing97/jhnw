package com.sgcc.share.controller;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.share.domain.SwitchInformation;
import com.sgcc.share.service.ISwitchInformationService;
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
 * 交换机四项基本信息Controller
 */
@RestController
@RequestMapping("/share/switchinformation")
public class SwitchInformationController extends BaseController
{
    @Autowired
    private ISwitchInformationService switchInformationService;

    /**
     * 查询交换机四项基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('sql:switchInformation:list')")
    @GetMapping("/list")
    public TableDataInfo list(SwitchInformation switchInformation)
    {
        startPage();
        if (switchInformation.getSubVersion()==null){
            switchInformation.setSubVersion("null");
        }
        List<SwitchInformation> list = switchInformationService.selectSwitchInformationList(switchInformation);
        return getDataTable(list);
    }

    /**
     * 导出交换机四项基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('sql:switchInformation:export')")
    @Log(title = "交换机四项基本信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SwitchInformation switchInformation)
    {
        if (switchInformation.getSubVersion()==null){
            switchInformation.setSubVersion("null");
        }
        List<SwitchInformation> list = switchInformationService.selectSwitchInformationList(switchInformation);
        ExcelUtil<SwitchInformation> util = new ExcelUtil<SwitchInformation>(SwitchInformation.class);
        return util.exportExcel(list, "交换机四项基本信息数据");
    }

    /**
     * 获取交换机四项基本信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('sql:switchInformation:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(switchInformationService.selectSwitchInformationById(id));
    }

    /**
     * 新增交换机四项基本信息
     */
    @PreAuthorize("@ss.hasPermi('sql:switchInformation:add')")
    @Log(title = "交换机四项基本信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SwitchInformation switchInformation)
    {
        return toAjax(switchInformationService.insertSwitchInformation(switchInformation));
    }

    /**
     * 修改交换机四项基本信息
     */
    @PreAuthorize("@ss.hasPermi('sql:switchInformation:edit')")
    @Log(title = "交换机四项基本信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SwitchInformation switchInformation)
    {
        return toAjax(switchInformationService.updateSwitchInformation(switchInformation));
    }

    /**
     * 删除交换机四项基本信息
     */
    @PreAuthorize("@ss.hasPermi('sql:switchInformation:remove')")
    @Log(title = "交换机四项基本信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(switchInformationService.deleteSwitchInformationByIds(ids));
    }
}
