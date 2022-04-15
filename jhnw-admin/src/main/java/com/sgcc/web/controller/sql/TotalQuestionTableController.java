package com.sgcc.web.controller.sql;

import com.sgcc.common.annotation.Log;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.page.TableDataInfo;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.sql.service.ITotalQuestionTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 问题及命令Controller
 * 
 * @author 韦亚宁
 * @date 2021-12-14
 */
@RestController
@RequestMapping("/sql/total_question_table")
public class TotalQuestionTableController extends BaseController
{
    @Autowired
    private ITotalQuestionTableService totalQuestionTableService;

    public static List<String> longList;
    /**
     * @method: 根据交换机信息查询 扫描问题的 命令ID
     * @Param: []
     * @return: java.util.List<java.lang.Long>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @GetMapping(value = "/commandIdByInformation")
    public List<String> commandIdByInformation()//String brand,String type,String firewareVersion
    {
        TotalQuestionTable totalQuestionTable = new TotalQuestionTable();
        /*totalQuestionTable.setBrand(brand);
        totalQuestionTable.setType(type);
        totalQuestionTable.setFirewareVersion(firewareVersion);*/
        totalQuestionTable.setBrand(Global.deviceBrand);
        totalQuestionTable.setType(Global.deviceModel);
        totalQuestionTable.setFirewareVersion(Global.firmwareVersion);
        totalQuestionTable.setSubVersion(Global.subversionNumber);
        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        if (totalQuestionTables!=null){
            List<String> longList = new ArrayList<>();
            for (TotalQuestionTable pojo:totalQuestionTables){
                longList.add(pojo.getCommandId());
            }
            this.longList=longList;
            return longList;
        }else {
            return null;
        }
    }

    /**
     * 查询问题及命令列表
     */
    //@PreAuthorize("@ss.hasPermi('sql:total_question_table:list')")
    @RequestMapping("/list")
    public TableDataInfo list(@RequestBody TotalQuestionTable totalQuestionTable)
    {
        String selectCommandId = totalQuestionTable.getCommandId();
        totalQuestionTable.setCommandId(null);
        startPage();
        List<TotalQuestionTable> list = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        List<TotalQuestionTable> totalQuestionTables = new ArrayList<>();
        if (selectCommandId.equals("0")){
            for (TotalQuestionTable pojo:list){
                if (pojo.getCommandId() == null || pojo.getCommandId().equals("")){
                    totalQuestionTables.add(pojo);
                }
            }
            return getDataTable(totalQuestionTables);
        }
        return getDataTable(list);
    }

    /**
     * 查询问题及命令列表
     */
    //@PreAuthorize("@ss.hasPermi('sql:total_question_table:list')")
    @RequestMapping("/typeProblemlist")
    public List<String> typeProblemlist()//@RequestBody TotalQuestionTable totalQuestionTable
    {
        TotalQuestionTable totalQuestionTable = new TotalQuestionTable();
        totalQuestionTable.setSubVersion("1510P09");
        totalQuestionTable.setCommandId(null);
        List<TotalQuestionTable> typeProblemlist = totalQuestionTableService.selectTotalQuestionTabletypeProblemList(totalQuestionTable);
        List<String> stringList = new ArrayList<>();
        for (TotalQuestionTable pojo:typeProblemlist){
            stringList.add(pojo.getTypeProblem());
        }
        return stringList;
    }

    /**
     * 导出问题及命令列表
     */
    @PreAuthorize("@ss.hasPermi('sql:total_question_table:export')")
    @Log(title = "问题及命令", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(TotalQuestionTable totalQuestionTable)
    {
        List<TotalQuestionTable> list = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        ExcelUtil<TotalQuestionTable> util = new ExcelUtil<TotalQuestionTable>(TotalQuestionTable.class);
        return util.exportExcel(list, "问题及命令数据");
    }

    /**
     * 获取问题及命令详细信息
     */
    @PreAuthorize("@ss.hasPermi('sql:total_question_table:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(totalQuestionTableService.selectTotalQuestionTableById(id));
    }

    /**
     * 新增问题及命令
     */
    //@PreAuthorize("@ss.hasPermi('sql:total_question_table:add')")
    //@Log(title = "问题及命令", businessType = BusinessType.INSERT)
    @RequestMapping("add")
    public String add(@RequestBody TotalQuestionTable totalQuestionTable)
    {
        int i = totalQuestionTableService.insertTotalQuestionTable(totalQuestionTable);
        return totalQuestionTable.getId()+"";
    }

    /**
     * 修改问题及命令
     */
    @PreAuthorize("@ss.hasPermi('sql:total_question_table:edit')")
    @Log(title = "问题及命令", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TotalQuestionTable totalQuestionTable)
    {
        return toAjax(totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable));
    }

    /**
     * 修改问题及命令 的 循环
     */
    public AjaxResult updateTotalQuestionTable(Long id)
    {
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(id);
        return toAjax(totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable));
    }

    /**
     * 删除问题及命令
     */
    @PreAuthorize("@ss.hasPermi('sql:total_question_table:remove')")
    @Log(title = "问题及命令", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(totalQuestionTableService.deleteTotalQuestionTableByIds(ids));
    }
}
