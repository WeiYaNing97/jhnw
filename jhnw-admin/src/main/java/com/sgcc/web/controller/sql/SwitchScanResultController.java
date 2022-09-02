package com.sgcc.web.controller.sql;

import java.util.*;

import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.connect.util.SpringBeanUtil;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.IValueInformationService;
import com.sgcc.web.controller.util.EncryptUtil;
import com.sgcc.web.controller.webSocket.WebSocketService;
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
import com.sgcc.sql.service.ISwitchScanResultService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 交换机扫描结果Controller
 * 
 * @author ruoyi
 * @date 2022-08-26
 */
@RestController
@RequestMapping("/sql/switch_scan_result")
public class SwitchScanResultController extends BaseController
{
    @Autowired
    private ISwitchScanResultService switchScanResultService;

    /**
     * 查询交换机扫描结果列表
     */
    @PreAuthorize("@ss.hasPermi('sql:switch_scan_result:list')")
    @GetMapping("/list")
    public TableDataInfo list(SwitchScanResult switchScanResult)
    {
        LoginUser login = SecurityUtils.getLoginUser();
        switchScanResult.setUserName(login.getUsername());
        startPage();
        List<SwitchScanResult> list = switchScanResultService.selectSwitchScanResultList(switchScanResult);
        for (SwitchScanResult pojo:list){
            pojo.setDynamicInformation(null);
        }
        return getDataTable(list);
    }

    /**
     * 导出交换机扫描结果列表
     */
    @PreAuthorize("@ss.hasPermi('sql:switch_scan_result:export')")
    @Log(title = "交换机扫描结果", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SwitchScanResult switchScanResult)
    {
        List<SwitchScanResult> list = switchScanResultService.selectSwitchScanResultList(switchScanResult);
        ExcelUtil<SwitchScanResult> util = new ExcelUtil<SwitchScanResult>(SwitchScanResult.class);
        return util.exportExcel(list, "交换机扫描结果数据");
    }

    /**
     * 获取交换机扫描结果详细信息
     */
    @PreAuthorize("@ss.hasPermi('sql:switch_scan_result:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(switchScanResultService.selectSwitchScanResultById(id));
    }

    /**
     * 新增交换机扫描结果
     */
    @PreAuthorize("@ss.hasPermi('sql:switch_scan_result:add')")
    @Log(title = "交换机扫描结果", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SwitchScanResult switchScanResult)
    {
        return toAjax(switchScanResultService.insertSwitchScanResult(switchScanResult));
    }

    /**
     * 修改交换机扫描结果
     */
    @PreAuthorize("@ss.hasPermi('sql:switch_scan_result:edit')")
    @Log(title = "交换机扫描结果", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SwitchScanResult switchScanResult)
    {
        return toAjax(switchScanResultService.updateSwitchScanResult(switchScanResult));
    }

    /**
     * 删除交换机扫描结果
     */
    @PreAuthorize("@ss.hasPermi('sql:switch_scan_result:remove')")
    @Log(title = "交换机扫描结果", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(switchScanResultService.deleteSwitchScanResultByIds(ids));
    }

}
