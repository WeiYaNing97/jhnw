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
import com.sgcc.advanced.domain.OspfCommand;
import com.sgcc.advanced.service.IOspfCommandService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * OSPF命令Controller
 * 
 * @author ruoyi
 * @date 2023-05-19
 */
@RestController
@RequestMapping("/advanced/ospf_command")
public class OspfCommandController extends BaseController
{
    @Autowired
    private IOspfCommandService ospfCommandService;

    /**
     * 查询OSPF命令列表
     */
    @PreAuthorize("@ss.hasPermi('advanced:ospf_command:list')")
    @GetMapping("/list")
    public TableDataInfo list(OspfCommand ospfCommand)
    {
        startPage();
        List<OspfCommand> list = ospfCommandService.selectOspfCommandList(ospfCommand);
        return getDataTable(list);
    }

    /**
     * 导出OSPF命令列表
     */
    @PreAuthorize("@ss.hasPermi('advanced:ospf_command:export')")
    @Log(title = "OSPF命令", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(OspfCommand ospfCommand)
    {
        List<OspfCommand> list = ospfCommandService.selectOspfCommandList(ospfCommand);
        ExcelUtil<OspfCommand> util = new ExcelUtil<OspfCommand>(OspfCommand.class);
        return util.exportExcel(list, "OSPF命令数据");
    }

    /**
     * 获取OSPF命令详细信息
     */
    @PreAuthorize("@ss.hasPermi('advanced:ospf_command:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(ospfCommandService.selectOspfCommandById(id));
    }

    /**
     * 新增OSPF命令
     */
    @PreAuthorize("@ss.hasPermi('advanced:ospf_command:add')")
    @Log(title = "OSPF命令", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OspfCommand ospfCommand)
    {
        return toAjax(ospfCommandService.insertOspfCommand(ospfCommand));
    }

    /**
     * 修改OSPF命令
     */
    @PreAuthorize("@ss.hasPermi('advanced:ospf_command:edit')")
    @Log(title = "OSPF命令", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OspfCommand ospfCommand)
    {
        return toAjax(ospfCommandService.updateOspfCommand(ospfCommand));
    }

    /**
     * 删除OSPF命令
     */
    @PreAuthorize("@ss.hasPermi('advanced:ospf_command:remove')")
    @Log(title = "OSPF命令", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(ospfCommandService.deleteOspfCommandByIds(ids));
    }
}
