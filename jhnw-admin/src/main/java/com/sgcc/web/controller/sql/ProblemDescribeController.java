package com.sgcc.web.controller.sql;

import java.util.List;

import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.sql.service.ITotalQuestionTableService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.sgcc.common.annotation.Log;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.sql.domain.ProblemDescribe;
import com.sgcc.sql.service.IProblemDescribeService;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.common.core.page.TableDataInfo;

/**
 * 问题描述Controller
 * 
 * @author ruoyi
 * @date 2022-05-26
 */
@RestController
@RequestMapping("/sql/problem_describe")
public class ProblemDescribeController extends BaseController
{
    @Autowired
    private IProblemDescribeService problemDescribeService;

    @Autowired
    private ITotalQuestionTableService totalQuestionTableService;

    /**
     * 查询问题描述列表
     */
    @PreAuthorize("@ss.hasPermi('sql:problem_describe:list')")
    @GetMapping("/list")
    public TableDataInfo list(ProblemDescribe problemDescribe)
    {
        startPage();
        List<ProblemDescribe> list = problemDescribeService.selectProblemDescribeList(problemDescribe);
        return getDataTable(list);
    }

    /**
     * 导出问题描述列表
     */
    @PreAuthorize("@ss.hasPermi('sql:problem_describe:export')")
    @Log(title = "问题描述", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(ProblemDescribe problemDescribe)
    {
        List<ProblemDescribe> list = problemDescribeService.selectProblemDescribeList(problemDescribe);
        ExcelUtil<ProblemDescribe> util = new ExcelUtil<ProblemDescribe>(ProblemDescribe.class);
        return util.exportExcel(list, "问题描述数据");
    }

    /**
     * 获取问题描述详细信息
     */
    @PreAuthorize("@ss.hasPermi('sql:problem_describe:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(problemDescribeService.selectProblemDescribeById(id));
    }

    /**
     * 新增问题描述
     */
    @PreAuthorize("@ss.hasPermi('sql:problem_describe:add')")
    @Log(title = "问题描述", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ProblemDescribe problemDescribe)
    {
        return toAjax(problemDescribeService.insertProblemDescribe(problemDescribe));
    }

    /**
     * 修改问题描述
     */
    @PreAuthorize("@ss.hasPermi('sql:problem_describe:edit')")
    @Log(title = "问题描述", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ProblemDescribe problemDescribe)
    {
        return toAjax(problemDescribeService.updateProblemDescribe(problemDescribe));
    }

    /**
     * 删除问题描述
     */
    @PreAuthorize("@ss.hasPermi('sql:problem_describe:remove')")
    @Log(title = "问题描述", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(problemDescribeService.deleteProblemDescribeByIds(ids));
    }


    /*=====================================================================================================================
    =====================================================================================================================
    =====================================================================================================================*/

    /*可新增 可修改*/
    /**
     * 新增问题描述
     */
    @RequestMapping("/insertProblemDescribe")
    public AjaxResult insertProblemDescribe(@RequestParam Long totalQuestionTableId,@RequestBody String problemDescribe)
    {
        //根据问题ID 查询问题表问题信息
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(totalQuestionTableId);
        //创建新的 问题描述表实体类
        ProblemDescribe pojo = new ProblemDescribe();
        //问题描述放入实体类
        pojo.setProblemDescribe(problemDescribe);

        //如果问题表 问题描述ID 不为0时 说明有问题描述，修改，只修改问题描述表描述字段
        if (!(totalQuestionTable.getProblemDescribeId().equals(0l))){
            pojo.setId(totalQuestionTable.getProblemDescribeId());
            int i = problemDescribeService.updateProblemDescribe(pojo);
            if (i>0){
                return AjaxResult.success("成功！");
            }
        }
        //如果问题表 问题描述ID 为0时 则插入问题描述信息
        int i = problemDescribeService.insertProblemDescribe(pojo);
        //插入成功后 ID添加到问题表 问题描述ID字段
        if (i>0){
            totalQuestionTable.setProblemDescribeId(pojo.getId());
            i = totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable);
            if (i>0){
                return AjaxResult.success("成功！");
            }
        }
        return AjaxResult.error("失败！");
    }

    /**
     * 获取问题描述详细信息
     */
    @RequestMapping("/selectProblemDescribe")
    public ProblemDescribe selectProblemDescribe(Long totalQuestionTableId)
    {
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(totalQuestionTableId);
        return problemDescribeService.selectProblemDescribeById(totalQuestionTable.getProblemDescribeId());
    }

    /**
     *
     * 删除问题描述
     */
    @RequestMapping("/deleteProblemDescribe")
    public AjaxResult deleteProblemDescribe(Long id)
    {
        int deleteProblemDescribeById = problemDescribeService.deleteProblemDescribeById(id);
        if (deleteProblemDescribeById>0){
            //根据 问题描述表ID  查询 问题表实体类
            TotalQuestionTable totalQuestionTable =  totalQuestionTableService.selectPojoByproblemDescribeId(id);
            totalQuestionTable.setProblemDescribeId(0l);
            int updateTotalQuestionTable = totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable);
            return toAjax(updateTotalQuestionTable);
        }
        return toAjax(deleteProblemDescribeById);
    }
}