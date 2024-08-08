package com.sgcc.sql.controller;

import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.page.TableDataInfo;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.sql.domain.BasicInformation;
import com.sgcc.sql.service.IBasicInformationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取基本信息命令Controller
 * 获取基本信息命令相关操作
 */
@Api(tags = "获取基本信息命令管理")
@RestController
@RequestMapping("/sql/basic_information")
//事务
@Transactional(rollbackFor = Exception.class)
public class BasicInformationController extends BaseController
{
    @Autowired
    private IBasicInformationService basicInformationService;

    /**
     * 查询获取基本信息命令列表
     */
    @ApiOperation("获取基本信息命令列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键",dataType = "Long"),
            @ApiImplicitParam(name = "command", value = "命令",dataType = "String"),
            @ApiImplicitParam(name = "problemId", value = "分析索引",dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('sql:basic_information:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasicInformation basicInformation)
    {
        startPage();
        List<BasicInformation> list = basicInformationService.selectBasicInformationList(basicInformation);
        return getDataTable(list);
    }

    /**
     * 导出获取基本信息命令列表
     */
    @ApiOperation("导出获取基本信息命令列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键",dataType = "Long"),
            @ApiImplicitParam(name = "command", value = "命令",dataType = "String"),
            @ApiImplicitParam(name = "problemId", value = "分析索引",dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('sql:basic_information:export')")
    @MyLog(title = "获取基本信息命令", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(BasicInformation basicInformation)
    {
        List<BasicInformation> list = basicInformationService.selectBasicInformationList(basicInformation);
        ExcelUtil<BasicInformation> util = new ExcelUtil<BasicInformation>(BasicInformation.class);
        return util.exportExcel(list, "获取基本信息命令数据");
    }

    /**
     * 获取获取基本信息命令详细信息
     */
    @ApiOperation("获取获取基本信息命令详细信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键",dataType = "Long")
    })
    @PreAuthorize("@ss.hasPermi('sql:basic_information:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(basicInformationService.selectBasicInformationById(id));
    }

    /**
     * 新增获取基本信息命令
     */
    @ApiOperation("新增获取基本信息命令")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键",dataType = "Long"),
            @ApiImplicitParam(name = "command", value = "命令",dataType = "String"),
            @ApiImplicitParam(name = "problemId", value = "分析索引",dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('sql:basic_information:add')")
    @MyLog(title = "获取基本信息命令", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BasicInformation basicInformation)
    {
        return toAjax(basicInformationService.insertBasicInformation(basicInformation));
    }

    /**
     * 修改获取基本信息命令
     */
    @ApiOperation("修改获取基本信息命令")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键",dataType = "Long"),
            @ApiImplicitParam(name = "command", value = "命令",dataType = "String"),
            @ApiImplicitParam(name = "problemId", value = "分析索引",dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('sql:basic_information:edit')")
    @MyLog(title = "获取基本信息命令", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BasicInformation basicInformation)
    {
        return toAjax(basicInformationService.updateBasicInformation(basicInformation));
    }

    /**
     * 删除获取基本信息命令
     */
    @ApiOperation("删除获取基本信息命令")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "ids", value = "主键",dataType = "Long[]")
    })
    @PreAuthorize("@ss.hasPermi('sql:basic_information:remove')")
    @MyLog(title = "获取基本信息命令", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(basicInformationService.deleteBasicInformationByIds(ids));
    }

    /**
     * 查询获取基本信息命令列表
     *
     * @return 返回一个包含转换后的基本信息命令列表的List对象
     */
    @ApiOperation("查询获取基本信息命令列表")
    @GetMapping("/getPojolist")
    public List<BasicInformation> getPojolist()
    {
        // 查询基本信息列表
        List<BasicInformation> list = basicInformationService.selectBasicInformationList(null);
        // 创建一个新的列表，用于存放转换后的基本信息对象
        List<BasicInformation> pojolist = new ArrayList<>();

        // 获取自定义分隔符
        /*自定义分隔符*/
        String customDelimiter = (String) CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());

        // 遍历查询结果列表
        for (BasicInformation basicInformation:list){
            // 创建一个新的基本信息对象
            BasicInformation pojo = new BasicInformation();
            // 设置ID
            pojo.setId(basicInformation.getId());
            // 替换命令中的自定义分隔符和左方括号
            pojo.setCommand(basicInformation.getCommand().replace(customDelimiter,"、").replace("\\[","\\ ["));
            // 设置问题ID
            pojo.setProblemId(basicInformation.getProblemId());
            // 将转换后的基本信息对象添加到新列表中
            pojolist.add(pojo);
        }

        // 返回转换后的基本信息列表
        return pojolist;
    }
}
