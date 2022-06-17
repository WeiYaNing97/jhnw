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

    //public static List<String> longList;

    /**
     * @method: 根据交换机信息查询 扫描问题的 命令ID
     * @Param: []
     * @return: java.util.List<java.lang.Long>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @GetMapping(value = "/commandIdByInformation")
    public List<String> commandIdByInformation(String brand,
                                               String type,
                                               String firewareVersion,
                                               String subversionNumber)
    {
        TotalQuestionTable totalQuestionTable = new TotalQuestionTable();

        totalQuestionTable.setBrand(brand);
        totalQuestionTable.setType(type);
        totalQuestionTable.setFirewareVersion(firewareVersion);
        totalQuestionTable.setSubVersion(subversionNumber);

        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        if (totalQuestionTables!=null){
            List<String> longList = new ArrayList<>();
            for (TotalQuestionTable pojo:totalQuestionTables){
                longList.add(pojo.getCommandId());
            }
            //this.longList=longList;
            return longList;
        }else {
            return null;
        }
    }


    /**
     * 查询问题及命令列表
     */
    @RequestMapping("/select")
    public Long select(@RequestBody TotalQuestionTable totalQuestionTable)
    {

        totalQuestionTable.setCommandId(null);
        List<TotalQuestionTable> list = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        return list.get(0).getId();
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

    /*=====================================================================================================================
    =====================================================================================================================
    =====================================================================================================================*/

    /**
     * 新增问题及命令
     */
    @RequestMapping("add")
    public String add(@RequestBody TotalQuestionTable totalQuestionTable)
    {
        totalQuestionTable.setNotFinished(totalQuestionTable.getNotFinished());
        int i = totalQuestionTableService.insertTotalQuestionTable(totalQuestionTable);
        return totalQuestionTable.getId()+"";
    }

    /**
     * @method: 查询所有品牌
     * @Param: [totalQuestionTable]
     * @return: java.util.List<java.lang.String>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("/brandlist")
    public List<String> brandlist()
    {
        TotalQuestionTable totalQuestionTable = new TotalQuestionTable();
        totalQuestionTable.setCommandId(null);
        List<TotalQuestionTable> typeProblemlist = totalQuestionTableService.selectTotalQuestionTablebrandList(totalQuestionTable);
        List<String> stringList = new ArrayList<>();
        for (TotalQuestionTable pojo:typeProblemlist){
            stringList.add(pojo.getBrand());
        }
        return stringList;
    }

    /**
     * @method: 根据所选品牌 查询所有型号
     * @Param: [totalQuestionTable]
     * @return: java.util.List<java.lang.String>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("/typelist")
    public List<String> typelist(@RequestBody TotalQuestionTable totalQuestionTable)
    {
        totalQuestionTable.setCommandId(null);
        List<TotalQuestionTable> typeProblemlist = totalQuestionTableService.selectTotalQuestionTabletypelist(totalQuestionTable);
        List<String> stringList = new ArrayList<>();
        for (TotalQuestionTable pojo:typeProblemlist){
            stringList.add(pojo.getType());
        }
        return stringList;
    }

    /**
     * @method: 根据所选品牌、型号   查询所有内部固件版本
     * @Param: [totalQuestionTable]
     * @return: java.util.List<java.lang.String>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("/firewareVersionlist")
    public List<String> firewareVersionlist(@RequestBody TotalQuestionTable totalQuestionTable)
    {
        totalQuestionTable.setCommandId(null);
        List<TotalQuestionTable> typeProblemlist = totalQuestionTableService.selectTotalQuestionTablefirewareVersionlist(totalQuestionTable);

        List<String> stringList = new ArrayList<>();
        for (TotalQuestionTable pojo:typeProblemlist){
            stringList.add(pojo.getFirewareVersion());
        }
        return stringList;
    }

    /**
     * @method: 根据所选品牌、型号、内部固件版本   查询所有子版本号
     * @Param: [totalQuestionTable]
     * @return: java.util.List<java.lang.String>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("/subVersionlist")
    public List<String> subVersionlist(@RequestBody TotalQuestionTable totalQuestionTable)
    {
        totalQuestionTable.setCommandId(null);
        List<TotalQuestionTable> typeProblemlist = totalQuestionTableService.selectTotalQuestionTablesubVersionlist(totalQuestionTable);
        List<String> stringList = new ArrayList<>();
        for (TotalQuestionTable pojo:typeProblemlist){
            stringList.add(pojo.getSubVersion());
        }
        return stringList;
    }

    /**
    * @method: 根据问题实体类查询问题种类
    * @Param: [totalQuestionTable]
    * @return: java.util.List<java.lang.String>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("/typeProblemlist")
    public List<String> typeProblemlist(@RequestBody TotalQuestionTable totalQuestionTable)
    {
        totalQuestionTable.setCommandId(null);
        List<TotalQuestionTable> typeProblemlist = totalQuestionTableService.selectTotalQuestionTabletypeProblemList(totalQuestionTable);
        List<String> stringList = new ArrayList<>();
        for (TotalQuestionTable pojo:typeProblemlist){
            stringList.add(pojo.getTypeProblem());
        }
        return stringList;
    }

    /**
    * @method: 根据问题实体类查询问题名称
    * @Param: [totalQuestionTable]
    * @return: java.util.List<java.lang.String>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("/problemNameList")
    public List<String> problemNameList(@RequestBody TotalQuestionTable totalQuestionTable)
    {
        String selectCommandId = totalQuestionTable.getCommandId();
        totalQuestionTable.setCommandId(null);
        startPage();
        List<TotalQuestionTable> list = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        List<String> totalQuestionTables = new ArrayList<>();
        if (selectCommandId.equals("0")){
            //未定义解决问题命令
            for (TotalQuestionTable pojo:list){
                if (pojo.getCommandId() == null || pojo.getCommandId().equals("")){
                    totalQuestionTables.add(pojo.getProblemName());
                }
            }
            return totalQuestionTables;
        }
        for (TotalQuestionTable pojo:list){
            totalQuestionTables.add(pojo.getProblemName());
        }
        return totalQuestionTables;
    }

    /**
     * @method: 获取 解决问题 命令ID
     * @Param: []
     * @return: java.util.List<java.lang.Long>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping(value = "/totalQuestionTableId")
    public Long totalQuestionTableId( String brand, String type, String firewareVersion, String subversionNumber, String problemName, String typeProblem)
    {
        TotalQuestionTable totalQuestionTable = new TotalQuestionTable();

        totalQuestionTable.setBrand(brand);
        totalQuestionTable.setType(type);
        totalQuestionTable.setFirewareVersion(firewareVersion);
        totalQuestionTable.setSubVersion(subversionNumber);
        totalQuestionTable.setProblemName(problemName);
        totalQuestionTable.setTypeProblem(typeProblem);
        totalQuestionTable.setCommandId(null);

        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        if (totalQuestionTables!=null){
            return totalQuestionTables.get(0).getId();
        }
        return null;
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
        if (selectCommandId.equals("0")){//未定义解决问题命令
            for (TotalQuestionTable pojo:list){
                if (pojo.getCommandId() == null || pojo.getCommandId().equals("")){
                    totalQuestionTables.add(pojo);
                }
            }
            return getDataTable(totalQuestionTables);
        }
        return getDataTable(list);
    }

}
