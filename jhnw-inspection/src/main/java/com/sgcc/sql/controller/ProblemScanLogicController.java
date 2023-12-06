package com.sgcc.sql.controller;

import com.sgcc.common.annotation.Log;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.core.page.TableDataInfo;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.util.MyUtils;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * 问题扫描逻辑Controller
 */
@RestController
@RequestMapping("/sql/problem_scan_logic")
//事务
@Transactional(rollbackFor = Exception.class)
public class ProblemScanLogicController extends BaseController {
    @Autowired
    private IProblemScanLogicService problemScanLogicService;
    @Autowired
    private ITotalQuestionTableService totalQuestionTableService;
    /**
     * 查询问题扫描逻辑列表
     */
    @PreAuthorize("@ss.hasPermi('sql:problem_scan_logic:list')")
    @GetMapping("/list")
    public TableDataInfo list(ProblemScanLogic problemScanLogic)
    {
        startPage();
        List<ProblemScanLogic> list = problemScanLogicService.selectProblemScanLogicList(problemScanLogic);
        return getDataTable(list);
    }

    /**
     * 导出问题扫描逻辑列表
     */
    @PreAuthorize("@ss.hasPermi('sql:problem_scan_logic:export')")
    @Log(title = "问题扫描逻辑", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(ProblemScanLogic problemScanLogic)
    {
        List<ProblemScanLogic> list = problemScanLogicService.selectProblemScanLogicList(problemScanLogic);
        ExcelUtil<ProblemScanLogic> util = new ExcelUtil<ProblemScanLogic>(ProblemScanLogic.class);
        return util.exportExcel(list, "问题扫描逻辑数据");
    }

    /**
     * 获取问题扫描逻辑详细信息
     */
    @PreAuthorize("@ss.hasPermi('sql:problem_scan_logic:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(problemScanLogicService.selectProblemScanLogicById(id));
    }

    /**
     * 新增问题扫描逻辑
     */
    @PreAuthorize("@ss.hasPermi('sql:problem_scan_logic:add')")
    @Log(title = "问题扫描逻辑", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ProblemScanLogic problemScanLogic)
    {
        return toAjax(problemScanLogicService.insertProblemScanLogic(problemScanLogic));
    }

    /**
     * 修改问题扫描逻辑
     */
    @PreAuthorize("@ss.hasPermi('sql:problem_scan_logic:edit')")
    @Log(title = "问题扫描逻辑", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ProblemScanLogic problemScanLogic)
    {
        return toAjax(problemScanLogicService.updateProblemScanLogic(problemScanLogic));
    }

    /**
     * 删除问题扫描逻辑
     */
    @PreAuthorize("@ss.hasPermi('sql:problem_scan_logic:remove')")
    @Log(title = "问题扫描逻辑", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(problemScanLogicService.deleteProblemScanLogicByIds(ids));
    }


    /**
     *  todo 定义修复问题命令时 获取定义的参数名的方法  由getParameterNameCollectionNo方法优化得到
     * @method: getParameterNameCollection    命令ID 没有带ID之前的 方法
     * @Param: [totalQuestionTableId]
     * @return: java.util.List<java.lang.String>
     */
    @ApiOperation("获取定义的参数名")
    @GetMapping("/getParameterNameCollection/{totalQuestionTableId}")
    public List<String> getParameterNameCollection(@PathVariable Long totalQuestionTableId){
        //根据问题ID 获取问题表数据
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(totalQuestionTableId);

        /*根据 交换机问题实体类 获得命令集合和分析实体类集合*/
        DefinitionProblemController definitionProblemController = new DefinitionProblemController();
        HashMap<String, Object> scanLogicalEntityClass = definitionProblemController.getScanLogicalEntityClass(totalQuestionTable, SecurityUtils.getLoginUser());
        if (scanLogicalEntityClass.size() == 0){
            return new ArrayList<>();
        }

        /* 获取分析实体类集合*/
        List<ProblemScanLogic> problemScanLogics = (List<ProblemScanLogic>) scanLogicalEntityClass.get("ProblemScanLogic");

        List<String> wordNameList = new ArrayList<>();
        for (ProblemScanLogic pojo:problemScanLogics){
            if (pojo.getWordName() != null){
                wordNameList.add(pojo.getWordName());
            }
        }

        return wordNameList;
    }
}
