package com.sgcc.advanced.controller;

import java.util.List;

import com.sgcc.common.annotation.MyLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
@Api(tags = "光衰命令管理")
@RestController
@RequestMapping("/advanced/light_attenuation_command")
public class LightAttenuationCommandController extends BaseController
{
    @Autowired
    private ILightAttenuationCommandService lightAttenuationCommandService;
    /**
     * 查询光衰命令列表
     */
    @ApiOperation("查询光衰命令列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataType = "String"),
            @ApiImplicitParam(name = "brand", value = "品牌", dataType = "String"),
            @ApiImplicitParam(name = "switchType", value = "型号", dataType = "String"),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataType = "String"),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataType = "String"),
            @ApiImplicitParam(name = "getPortCommand", value = "获取up端口号命令", dataType = "String"),
            @ApiImplicitParam(name = "getParameterCommand", value = "获取光衰参数命令", dataType = "String"),
            @ApiImplicitParam(name = "conversion", value = "转译", dataType = "String")
    })
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
    @ApiOperation("导出光衰命令列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataType = "String"),
            @ApiImplicitParam(name = "brand", value = "品牌", dataType = "String"),
            @ApiImplicitParam(name = "switchType", value = "型号", dataType = "String"),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataType = "String"),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataType = "String"),
            @ApiImplicitParam(name = "getPortCommand", value = "获取up端口号命令", dataType = "String"),
            @ApiImplicitParam(name = "getParameterCommand", value = "获取光衰参数命令", dataType = "String"),
            @ApiImplicitParam(name = "conversion", value = "转译", dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('advanced:light_attenuation_command:export')")
    @MyLog(title = "光衰命令", businessType = BusinessType.EXPORT)
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
    @ApiOperation("获取光衰命令详细信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('advanced:light_attenuation_command:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(lightAttenuationCommandService.selectLightAttenuationCommandById(id));
    }

    /**
     * 新增光衰命令
     */
    @ApiOperation("新增光衰命令")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataType = "String"),
            @ApiImplicitParam(name = "brand", value = "品牌", dataType = "String"),
            @ApiImplicitParam(name = "switchType", value = "型号", dataType = "String"),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataType = "String"),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataType = "String"),
            @ApiImplicitParam(name = "getPortCommand", value = "获取up端口号命令", dataType = "String"),
            @ApiImplicitParam(name = "getParameterCommand", value = "获取光衰参数命令", dataType = "String"),
            @ApiImplicitParam(name = "conversion", value = "转译", dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('advanced:light_attenuation_command:add')")
    @MyLog(title = "光衰命令", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LightAttenuationCommand lightAttenuationCommand)
    {
        if (lightAttenuationCommand.getConversion()==null){
            lightAttenuationCommand.setConversion("GE:GigabitEthernet");
        }
        return toAjax(lightAttenuationCommandService.insertLightAttenuationCommand(lightAttenuationCommand));
    }

    /**
     * 修改光衰命令
     */
    @ApiOperation("修改光衰命令")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataType = "String"),
            @ApiImplicitParam(name = "brand", value = "品牌", dataType = "String"),
            @ApiImplicitParam(name = "switchType", value = "型号", dataType = "String"),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataType = "String"),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataType = "String"),
            @ApiImplicitParam(name = "getPortCommand", value = "获取up端口号命令", dataType = "String"),
            @ApiImplicitParam(name = "getParameterCommand", value = "获取光衰参数命令", dataType = "String"),
            @ApiImplicitParam(name = "conversion", value = "转译", dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('advanced:light_attenuation_command:edit')")
    @MyLog(title = "光衰命令", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LightAttenuationCommand lightAttenuationCommand)
    {
        return toAjax(lightAttenuationCommandService.updateLightAttenuationCommand(lightAttenuationCommand));
    }

    /**
     * 删除光衰命令
     */
    @ApiOperation("删除光衰命令")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "ids", value = "主键", dataType = "String[]")
    })
    @PreAuthorize("@ss.hasPermi('advanced:light_attenuation_command:remove')")
    @MyLog(title = "光衰命令", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(lightAttenuationCommandService.deleteLightAttenuationCommandByIds(ids));
    }
}
