package com.sgcc.sql.controller;

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
import com.sgcc.sql.domain.SolveQuestion;
import com.sgcc.sql.service.ISolveQuestionService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 前端 解决问题Controller
 * 
 * @author 王浩伟
 * @date 2022-06-01
 */
@RestController
@RequestMapping("/sql/solve_question")
public class SolveQuestionController extends BaseController
{
    @Autowired
    private ISolveQuestionService solveQuestionService;

    /**
     * 查询前端 解决问题列表
     */
    @PreAuthorize("@ss.hasPermi('sql:solve_question:list')")
    @GetMapping("/list")
    public TableDataInfo list(SolveQuestion solveQuestion)
    {
        startPage();
        List<SolveQuestion> list = solveQuestionService.selectSolveQuestionList(solveQuestion);
        return getDataTable(list);
    }

    /**
     * 导出前端 解决问题列表
     */
    @PreAuthorize("@ss.hasPermi('sql:solve_question:export')")
    @Log(title = "前端 解决问题", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SolveQuestion solveQuestion)
    {
        List<SolveQuestion> list = solveQuestionService.selectSolveQuestionList(solveQuestion);
        ExcelUtil<SolveQuestion> util = new ExcelUtil<SolveQuestion>(SolveQuestion.class);
        return util.exportExcel(list, "前端 解决问题数据");
    }

    /**
     * 获取前端 解决问题详细信息
     */
    @PreAuthorize("@ss.hasPermi('sql:solve_question:query')")
    @GetMapping(value = "/{commond}")
    public AjaxResult getInfo(@PathVariable("commond") String commond)
    {
        return AjaxResult.success(solveQuestionService.selectSolveQuestionByCommond(commond));
    }

    /**
     * 新增前端 解决问题
     */
    @PreAuthorize("@ss.hasPermi('sql:solve_question:add')")
    @Log(title = "前端 解决问题", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SolveQuestion solveQuestion)
    {
        return toAjax(solveQuestionService.insertSolveQuestion(solveQuestion));
    }

    /**
     * 修改前端 解决问题
     */
    @PreAuthorize("@ss.hasPermi('sql:solve_question:edit')")
    @Log(title = "前端 解决问题", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SolveQuestion solveQuestion)
    {
        return toAjax(solveQuestionService.updateSolveQuestion(solveQuestion));
    }

    /**
     * 删除前端 解决问题
     */
    @PreAuthorize("@ss.hasPermi('sql:solve_question:remove')")
    @Log(title = "前端 解决问题", businessType = BusinessType.DELETE)
	@DeleteMapping("/{commonds}")
    public AjaxResult remove(@PathVariable String[] commonds)
    {
        return toAjax(solveQuestionService.deleteSolveQuestionByCommonds(commonds));
    }
}
