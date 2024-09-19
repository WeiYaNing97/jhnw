package com.sgcc.advanced.controller;

import java.util.List;

import com.sgcc.advanced.domain.RouteAggregationCommand;
import com.sgcc.advanced.service.IRouteAggregationCommandService;
import com.sgcc.advanced.utils.ScreeningMethod;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.MyUtils;
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
import com.sgcc.advanced.domain.LinkBindingCommand;
import com.sgcc.advanced.service.ILinkBindingCommandService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 链路捆绑命令Controller
 * 
 * @author ruoyi
 * @date 2024-09-09
 */
@RestController
@RequestMapping("/advanced/LinkBindingCommand")
public class LinkBindingCommandController extends BaseController
{
    @Autowired
    private ILinkBindingCommandService linkBindingCommandService;

    /**
     * 查询链路捆绑命令列表
     */
    @PreAuthorize("@ss.hasPermi('advanced:LinkBindingCommand:list')")
    @GetMapping("/list")
    public TableDataInfo list(LinkBindingCommand linkBindingCommand)
    {
        startPage();
        List<LinkBindingCommand> list = linkBindingCommandService.selectLinkBindingCommandList(linkBindingCommand);
        return getDataTable(list);
    }

    /**
     * 导出链路捆绑命令列表
     */
    @PreAuthorize("@ss.hasPermi('advanced:LinkBindingCommand:export')")
    @Log(title = "链路捆绑命令", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(LinkBindingCommand linkBindingCommand)
    {
        List<LinkBindingCommand> list = linkBindingCommandService.selectLinkBindingCommandList(linkBindingCommand);
        ExcelUtil<LinkBindingCommand> util = new ExcelUtil<LinkBindingCommand>(LinkBindingCommand.class);
        return util.exportExcel(list, "链路捆绑命令数据");
    }

    /**
     * 获取链路捆绑命令详细信息
     */
    @PreAuthorize("@ss.hasPermi('advanced:LinkBindingCommand:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(linkBindingCommandService.selectLinkBindingCommandById(id));
    }

    /**
     * 新增链路捆绑命令
     */
    @PreAuthorize("@ss.hasPermi('advanced:LinkBindingCommand:add')")
    @Log(title = "链路捆绑命令", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LinkBindingCommand linkBindingCommand)
    {
        return toAjax(linkBindingCommandService.insertLinkBindingCommand(linkBindingCommand));
    }

    /**
     * 修改链路捆绑命令
     */
    @PreAuthorize("@ss.hasPermi('advanced:LinkBindingCommand:edit')")
    @Log(title = "链路捆绑命令", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LinkBindingCommand linkBindingCommand)
    {
        return toAjax(linkBindingCommandService.updateLinkBindingCommand(linkBindingCommand));
    }

    /**
     * 删除链路捆绑命令
     */
    @PreAuthorize("@ss.hasPermi('advanced:LinkBindingCommand:remove')")
    @Log(title = "链路捆绑命令", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(linkBindingCommandService.deleteLinkBindingCommandByIds(ids));
    }

}
