package com.sgcc.sql.controller;

import com.sgcc.common.annotation.Log;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.page.TableDataInfo;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.sql.domain.ValueInformation;
import com.sgcc.sql.domain.ValueInformationVO;
import com.sgcc.sql.service.IValueInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 取值信息存储Controller
 * 
 * @author ruoyi
 * @date 2021-12-28
 */
@RestController
@RequestMapping("/sql/value_information")
public class ValueInformationController extends BaseController
{
    @Autowired
    private IValueInformationService valueInformationService;

    /**
     * 查询取值信息存储列表
     */
    @PreAuthorize("@ss.hasPermi('sql:value_information:list')")
    @GetMapping("/list")
    public TableDataInfo list(ValueInformation valueInformation)
    {
        startPage();
        List<ValueInformation> list = valueInformationService.selectValueInformationList(valueInformation);
        return getDataTable(list);
    }

    /**
     * 导出取值信息存储列表
     */
    @PreAuthorize("@ss.hasPermi('sql:value_information:export')")
    @Log(title = "取值信息存储", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(ValueInformation valueInformation)
    {
        List<ValueInformation> list = valueInformationService.selectValueInformationList(valueInformation);
        ExcelUtil<ValueInformation> util = new ExcelUtil<ValueInformation>(ValueInformation.class);
        return util.exportExcel(list, "取值信息存储数据");
    }

    /**
     * 获取取值信息存储详细信息
     */
    @PreAuthorize("@ss.hasPermi('sql:value_information:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(valueInformationService.selectValueInformationById(id));
    }

    /**
     * 新增取值信息存储
     */
    @PreAuthorize("@ss.hasPermi('sql:value_information:add')")
    @Log(title = "取值信息存储", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ValueInformation valueInformation)
    {
        return toAjax(valueInformationService.insertValueInformation(valueInformation));
    }

    /**
     * 修改取值信息存储
     */
    @PreAuthorize("@ss.hasPermi('sql:value_information:edit')")
    @Log(title = "取值信息存储", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ValueInformation valueInformation)
    {
        return toAjax(valueInformationService.updateValueInformation(valueInformation));
    }

    /**
     * 删除取值信息存储
     */
    @PreAuthorize("@ss.hasPermi('sql:value_information:remove')")
    @Log(title = "取值信息存储", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(valueInformationService.deleteValueInformationByIds(ids));
    }

    /**
    * @method: 根据第一个参数id 查询参数列表
    * @Param: [id]
    * @return: java.util.List<com.sgcc.sql.domain.ValueInformationVO>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("getValueInformationVOByID")
    public List<ValueInformationVO> getValueInformationVOByID(Long id){
        List<ValueInformationVO> valueInformationVOList = valueInformationService.selectValueInformationVOListByID(id);
        return valueInformationVOList;
    }
}
