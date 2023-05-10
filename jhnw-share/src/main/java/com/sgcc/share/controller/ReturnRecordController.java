package com.sgcc.share.controller;

import com.sgcc.common.annotation.Log;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.core.page.TableDataInfo;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.share.domain.ReturnRecord;
import com.sgcc.share.service.IReturnRecordService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 返回信息Controller
 * 
 * @author 韦亚宁
 * @date 2021-12-22
 */
@RestController
@RequestMapping("/sql/return_record")
//事务
@Transactional(rollbackFor = Exception.class)
@Api("返回信息")
public class ReturnRecordController extends BaseController
{
    @Autowired
    private IReturnRecordService returnRecordService;

    /**
     * 查询返回信息列表
     */
    @PreAuthorize("@ss.hasPermi('sql:return_record:list')")
    @GetMapping("/list")
    public TableDataInfo list(ReturnRecord returnRecord)
    {
        LoginUser login = SecurityUtils.getLoginUser();
        returnRecord.setUserName(login.getUsername());
        startPage();
        List<ReturnRecord> list = returnRecordService.selectReturnRecordList(returnRecord);
        for (ReturnRecord pojo:list){
            pojo.setCurrentReturnLog(null);
        }
        return getDataTable(list);
    }

    /**
     * 导出返回信息列表
     */
    @PreAuthorize("@ss.hasPermi('sql:return_record:export')")
    @Log(title = "返回信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(ReturnRecord returnRecord)
    {
        List<ReturnRecord> list = returnRecordService.selectReturnRecordList(returnRecord);
        ExcelUtil<ReturnRecord> util = new ExcelUtil<ReturnRecord>(ReturnRecord.class);
        return util.exportExcel(list, "返回信息数据");
    }

    /**
     * 获取返回信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('sql:return_record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(returnRecordService.selectReturnRecordById(id));
    }

    /**
     * 新增返回信息
     */
    @PreAuthorize("@ss.hasPermi('sql:return_record:add')")
    @Log(title = "返回信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ReturnRecord returnRecord)
    {
        return toAjax(returnRecordService.insertReturnRecord(returnRecord));
    }

    /**
     * 修改返回信息
     */
    @PreAuthorize("@ss.hasPermi('sql:return_record:edit')")
    @Log(title = "返回信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ReturnRecord returnRecord)
    {
        return toAjax(returnRecordService.updateReturnRecord(returnRecord));
    }

    /**
     * 删除返回信息
     */
    @PreAuthorize("@ss.hasPermi('sql:return_record:remove')")
    @Log(title = "返回信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(returnRecordService.deleteReturnRecordByIds(ids));
    }

}
