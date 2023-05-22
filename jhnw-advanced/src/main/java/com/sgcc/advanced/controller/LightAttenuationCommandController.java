package com.sgcc.advanced.controller;

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
import com.sgcc.advanced.domain.LightAttenuationCommand;
import com.sgcc.advanced.service.ILightAttenuationCommandService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 光衰命令Controller
 * 
 * @author ruoyi
 * @date 2023-05-19
 */
@RestController
@RequestMapping("/advanced/light_attenuation_command")
public class LightAttenuationCommandController extends BaseController
{
    @Autowired
    private ILightAttenuationCommandService lightAttenuationCommandService;
    /**
     * 查询光衰命令列表
     */
    @PreAuthorize("@ss.hasPermi('advanced:light_attenuation_command:list')")
    @GetMapping("/list")
    public TableDataInfo list(LightAttenuationCommand lightAttenuationCommand)
    {
        startPage();
        List<LightAttenuationCommand> list = lightAttenuationCommandService.selectLightAttenuationCommandList(lightAttenuationCommand);
        return getDataTable(list);
    }
    /**
     * 导出光衰命令列表
     */
    @PreAuthorize("@ss.hasPermi('advanced:light_attenuation_command:export')")
    @Log(title = "光衰命令", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(LightAttenuationCommand lightAttenuationCommand)
    {
        List<LightAttenuationCommand> list = lightAttenuationCommandService.selectLightAttenuationCommandList(lightAttenuationCommand);
        ExcelUtil<LightAttenuationCommand> util = new ExcelUtil<LightAttenuationCommand>(LightAttenuationCommand.class);
        return util.exportExcel(list, "光衰命令数据");
    }

    /**
     * 获取光衰命令详细信息
     */
    @PreAuthorize("@ss.hasPermi('advanced:light_attenuation_command:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(lightAttenuationCommandService.selectLightAttenuationCommandById(id));
    }

    /**
     * 新增光衰命令
     */
    @PreAuthorize("@ss.hasPermi('advanced:light_attenuation_command:add')")
    @Log(title = "光衰命令", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LightAttenuationCommand lightAttenuationCommand)
    {
        return toAjax(lightAttenuationCommandService.insertLightAttenuationCommand(lightAttenuationCommand));
    }

    /**
     * 修改光衰命令
     */
    @PreAuthorize("@ss.hasPermi('advanced:light_attenuation_command:edit')")
    @Log(title = "光衰命令", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LightAttenuationCommand lightAttenuationCommand)
    {
        return toAjax(lightAttenuationCommandService.updateLightAttenuationCommand(lightAttenuationCommand));
    }

    /**
     * 删除光衰命令
     */
    @PreAuthorize("@ss.hasPermi('advanced:light_attenuation_command:remove')")
    @Log(title = "光衰命令", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(lightAttenuationCommandService.deleteLightAttenuationCommandByIds(ids));
    }
}
