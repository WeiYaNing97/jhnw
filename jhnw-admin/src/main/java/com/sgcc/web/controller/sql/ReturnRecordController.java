package com.sgcc.web.controller.sql;

import com.sgcc.common.annotation.Log;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.page.TableDataInfo;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.sql.domain.ReturnRecord;
import com.sgcc.sql.service.IReturnRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
        startPage();
        List<ReturnRecord> list = returnRecordService.selectReturnRecordList(returnRecord);
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

    /**
     * 查询返回信息列表
     */
    /*@GetMapping("/getReturnRecordList")
    public AjaxResult getReturnRecordList()
    {
        String currentTime = Utils.getCurrentTime();
        String[] data_day = currentTime.split(" ");
        List<ReturnRecord> ReturnRecord_list = returnRecordService.selectReturnRecordListByDataTime(data_day[0]);

        StringBuilder stringBuilder = new StringBuilder();

        for (ReturnRecord returnRecord:ReturnRecord_list){
            stringBuilder.append(returnRecord.getCurrentCommLog());
            stringBuilder.append("\r\n");
            stringBuilder.append(returnRecord.getCurrentReturnLog());
            stringBuilder.append("\r\n");
            stringBuilder.append(returnRecord.getCurrentIdentifier());
        }

        System.err.print("\r\n"+stringBuilder+"\r\n");
        return AjaxResult.success(stringBuilder);
    }*/
}
