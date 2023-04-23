package com.sgcc.sql.controller;

import java.util.*;

import cn.hutool.core.date.DateTime;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.connect.util.SpringBeanUtil;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.parametric.SwitchParameters;
import com.sgcc.sql.service.IReturnRecordService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
//事务
@Transactional(rollbackFor = Exception.class)
public class SwitchScanResultController extends BaseController
{
    @Autowired
    private ISwitchScanResultService switchScanResultService;

    /**
     * 查询交换机扫描结果列表
     */
    @ApiOperation("查询交换机扫描结果列表")
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
    @ApiOperation("导出交换机扫描结果列表")
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
    @ApiOperation("获取交换机扫描结果详细信息")
    @PreAuthorize("@ss.hasPermi('sql:switch_scan_result:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(switchScanResultService.selectSwitchScanResultById(id));
    }

    /**
     * 新增交换机扫描结果
     */
    @ApiOperation("新增交换机扫描结果")
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
    @ApiOperation("修改交换机扫描结果")
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

    /**
     * @method: 高级功能扫描结果插入数据库
     * @Param:
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public void insertSwitchScanResult (SwitchParameters switchParameters, HashMap<String,String> hashMap){

        SwitchScanResult switchScanResult = new SwitchScanResult();

        //插入问题数据
        switchScanResult.setSwitchIp(switchParameters.getIp()+":"+switchParameters.getThreadName()); // ip

        switchScanResult.setBrand(switchParameters.getDeviceBrand());
        switchScanResult.setSwitchType(switchParameters.getDeviceModel());
        switchScanResult.setFirewareVersion(switchParameters.getFirmwareVersion());
        switchScanResult.setSubVersion(switchParameters.getSubversionNumber());

        switchScanResult.setSwitchName(switchParameters.getName()); //name
        switchScanResult.setSwitchPassword(switchParameters.getPassword()); //password
        switchScanResult.setConfigureCiphers(switchParameters.getConfigureCiphers());

        switchScanResult.setLoginMethod(switchParameters.getMode());
        switchScanResult.setPortNumber(switchParameters.getPort());

        switchScanResult.setTypeProblem("高级功能");
        switchScanResult.setTemProName(hashMap.get("ProblemName"));
        switchScanResult.setProblemName(hashMap.get("ProblemName"));
        switchScanResult.setDynamicInformation(hashMap.get("parameterString"));
        switchScanResult.setIfQuestion(hashMap.get("IfQuestion")); //是否有问题


        switchScanResult.setUserName(switchParameters.getLoginUser().getUsername());//登录名称
        switchScanResult.setPhonenumber(switchParameters.getLoginUser().getUser().getPhonenumber()); //登录手机号
        //插入 扫描时间
        DateTime dateTime = new DateTime(switchParameters.getScanningTime(), "yyyy-MM-dd HH:mm:ss");
        switchScanResult.setCreateTime(dateTime);

        //插入问题
        switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);
        switchScanResultService.insertSwitchScanResult(switchScanResult);
    };

}
