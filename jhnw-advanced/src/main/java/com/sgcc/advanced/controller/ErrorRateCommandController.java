package com.sgcc.advanced.controller;
import java.sql.Array;
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
import com.sgcc.advanced.domain.ErrorRateCommand;
import com.sgcc.advanced.service.IErrorRateCommandService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 错误包命令Controller
 * 
 * @author ruoyi
 * @date 2023-05-19
 */
@Api(tags = "错误包命令管理")
@RestController
@RequestMapping("/advanced/error_rate_command")
public class ErrorRateCommandController extends BaseController
{
    @Autowired
    private IErrorRateCommandService errorRateCommandService;
    /**
     * 查询错误包命令列表
     */
    @ApiOperation("查询错误包命令列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键ID",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "brand", value = "品牌",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "switchType", value = "型号",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "subVersion", value = "子版本号",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "getPortCommand", value = "获取up端口号命令",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "getParameterCommand", value = "获取错误包参数命令",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "conversion", value = "转译",dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('advanced:error_rate_command:list')")
    @GetMapping("/list")
    public TableDataInfo list(ErrorRateCommand errorRateCommand)
    {

        startPage();
        List<ErrorRateCommand> list = errorRateCommandService.selectErrorRateCommandList(errorRateCommand);
        return getDataTable(list);
    }

    /**
     * 导出错误包命令列表
     */
    @ApiOperation("导出错误包命令列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键ID",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "brand", value = "品牌",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "switchType", value = "型号",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "subVersion", value = "子版本号",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "getPortCommand", value = "获取up端口号命令",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "getParameterCommand", value = "获取错误包参数命令",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "conversion", value = "转译",dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('advanced:error_rate_command:export')")
    @MyLog(title = "错误包命令", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(ErrorRateCommand errorRateCommand)
    {
        List<ErrorRateCommand> list = errorRateCommandService.selectErrorRateCommandList(errorRateCommand);
        ExcelUtil<ErrorRateCommand> util = new ExcelUtil<ErrorRateCommand>(ErrorRateCommand.class);
        return util.exportExcel(list, "错误包命令数据");
    }

    /**
     * 获取错误包命令详细信息
     */
    @ApiOperation("获取错误包命令详细信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键ID",dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('advanced:error_rate_command:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(errorRateCommandService.selectErrorRateCommandById(id));
    }

    /**
     * 新增错误包命令
     */
    @ApiOperation("新增错误包命令")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键ID",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "brand", value = "品牌",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "switchType", value = "型号",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "subVersion", value = "子版本号",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "getPortCommand", value = "获取up端口号命令",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "getParameterCommand", value = "获取错误包参数命令",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "conversion", value = "转译",dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('advanced:error_rate_command:add')")
    @MyLog(title = "错误包命令", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ErrorRateCommand errorRateCommand)
    {
        if (errorRateCommand.getConversion()==null){
            errorRateCommand.setConversion("GE:GigabitEthernet");
        }
        return toAjax(errorRateCommandService.insertErrorRateCommand(errorRateCommand));
    }

    /**
     * 修改错误包命令
     */
    @ApiOperation("修改错误包命令")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键ID",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "brand", value = "品牌",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "switchType", value = "型号",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "subVersion", value = "子版本号",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "getPortCommand", value = "获取up端口号命令",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "getParameterCommand", value = "获取错误包参数命令",dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "conversion", value = "转译",dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('advanced:error_rate_command:edit')")
    @MyLog(title = "错误包命令", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ErrorRateCommand errorRateCommand)
    {
        return toAjax(errorRateCommandService.updateErrorRateCommand(errorRateCommand));
    }

    /**
     * 删除错误包命令
     */
    @ApiOperation("删除错误包命令")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "ids", value = "主键ID",dataTypeClass = Array.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('advanced:error_rate_command:remove')")
    @MyLog(title = "错误包命令", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(errorRateCommandService.deleteErrorRateCommandByIds(ids));
    }
}
