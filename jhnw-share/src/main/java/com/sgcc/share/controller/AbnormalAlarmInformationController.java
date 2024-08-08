package com.sgcc.share.controller;

import java.util.List;

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
import com.sgcc.common.annotation.Log;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.share.domain.AbnormalAlarmInformation;
import com.sgcc.share.service.IAbnormalAlarmInformationService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 异常告警信息Controller
 * 
 * @author ruoyi
 * @date 2024-02-26
 */
@Api(tags = "异常告警信息管理")
@RestController
@RequestMapping("/share/abnormal_alarm_information")
public class AbnormalAlarmInformationController extends BaseController
{
    @Autowired
    private IAbnormalAlarmInformationService abnormalAlarmInformationService;

    /**
     * 查询异常告警信息列表
     */
    @ApiOperation("查询异常告警信息列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "switchIp", value = "交换机ip", dataType = "String"),
            @ApiImplicitParam(name = "questionType", value = "问题类型", dataType = "String"),
            @ApiImplicitParam(name = "questionInformation", value = "问题信息", dataType = "String"),
            @ApiImplicitParam(name = "userName", value = "登录名称", dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('share:abnormal_alarm_information:list')")
    @GetMapping("/list")
    public TableDataInfo list(AbnormalAlarmInformation abnormalAlarmInformation)
    {
        startPage();
        List<AbnormalAlarmInformation> list = abnormalAlarmInformationService.selectAbnormalAlarmInformationList(abnormalAlarmInformation);
        return getDataTable(list);
    }

    /**
     * 导出异常告警信息列表
     */
    @ApiOperation("导出异常告警信息列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "switchIp", value = "交换机ip", dataType = "String"),
            @ApiImplicitParam(name = "questionType", value = "问题类型", dataType = "String"),
            @ApiImplicitParam(name = "questionInformation", value = "问题信息", dataType = "String"),
            @ApiImplicitParam(name = "userName", value = "登录名称", dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('share:abnormal_alarm_information:export')")
    @Log(title = "异常告警信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(AbnormalAlarmInformation abnormalAlarmInformation)
    {
        List<AbnormalAlarmInformation> list = abnormalAlarmInformationService.selectAbnormalAlarmInformationList(abnormalAlarmInformation);
        ExcelUtil<AbnormalAlarmInformation> util = new ExcelUtil<AbnormalAlarmInformation>(AbnormalAlarmInformation.class);
        return util.exportExcel(list, "异常告警信息数据");
    }

    /**
     * 获取异常告警信息详细信息
     */
    @ApiOperation("获取异常告警信息详细信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "switchIp", value = "交换机ip", dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('share:abnormal_alarm_information:query')")
    @GetMapping(value = "/{switchIp}")
    public AjaxResult getInfo(@PathVariable("switchIp") String switchIp)
    {
        return AjaxResult.success(abnormalAlarmInformationService.selectAbnormalAlarmInformationBySwitchIp(switchIp));
    }

    /**
     * 新增异常告警信息
     */
    @ApiOperation("新增异常告警信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "switchIp", value = "交换机ip", dataType = "String"),
            @ApiImplicitParam(name = "questionType", value = "问题类型", dataType = "String"),
            @ApiImplicitParam(name = "questionInformation", value = "问题信息", dataType = "String"),
            @ApiImplicitParam(name = "userName", value = "登录名称", dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('share:abnormal_alarm_information:add')")
    @Log(title = "异常告警信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AbnormalAlarmInformation abnormalAlarmInformation)
    {
        return toAjax(abnormalAlarmInformationService.insertAbnormalAlarmInformation(abnormalAlarmInformation));
    }

    /**
     * 修改异常告警信息
     */
    @ApiOperation("修改异常告警信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "switchIp", value = "交换机ip", dataType = "String"),
            @ApiImplicitParam(name = "questionType", value = "问题类型", dataType = "String"),
            @ApiImplicitParam(name = "questionInformation", value = "问题信息", dataType = "String"),
            @ApiImplicitParam(name = "userName", value = "登录名称", dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('share:abnormal_alarm_information:edit')")
    @Log(title = "异常告警信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AbnormalAlarmInformation abnormalAlarmInformation)
    {
        return toAjax(abnormalAlarmInformationService.updateAbnormalAlarmInformation(abnormalAlarmInformation));
    }

    /**
     * 删除异常告警信息
     */
    @ApiOperation("删除异常告警信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "switchIps", value = "交换机ip", dataType = "String[]")
    })
    @PreAuthorize("@ss.hasPermi('share:abnormal_alarm_information:remove')")
    @Log(title = "异常告警信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{switchIps}")
    public AjaxResult remove(@PathVariable String[] switchIps)
    {
        return toAjax(abnormalAlarmInformationService.deleteAbnormalAlarmInformationBySwitchIps(switchIps));
    }
}
