package com.sgcc.sql.controller;
import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.page.TableDataInfo;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * 问题扫描逻辑Controller
 */
@Api(tags = "问题扫描逻辑管理")
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
    @ApiOperation("查询问题扫描逻辑列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataType = "String"),
            @ApiImplicitParam(name = "matched", value = "匹配", dataType = "String"),
            @ApiImplicitParam(name = "relativePosition", value = "相对位置", dataType = "String"),
            @ApiImplicitParam(name = "matchContent", value = "匹配内容", dataType = "String"),
            @ApiImplicitParam(name = "action", value = "动作", dataType = "String"),
            @ApiImplicitParam(name = "rPosition", value = "位置", dataType = "Integer"),
            @ApiImplicitParam(name = "length", value = "长度", dataType = "String"),
            @ApiImplicitParam(name = "exhibit", value = "是否显示", dataType = "String"),
            @ApiImplicitParam(name = "wordName", value = "取词名称", dataType = "String"),
            @ApiImplicitParam(name = "compare", value = "比较", dataType = "String"),
            @ApiImplicitParam(name = "tNextId", value = "true下一条分析索引", dataType = "String"),
            @ApiImplicitParam(name = "tComId", value = "true下一条命令索引", dataType = "String"),
            @ApiImplicitParam(name = "problemId", value = "问题索引", dataType = "String"),
            @ApiImplicitParam(name = "fLine", value = "false行号", dataType = "String"),
            @ApiImplicitParam(name = "tLine", value = "true行号", dataType = "String"),
            @ApiImplicitParam(name = "fNextId", value = "false下一条分析索引", dataType = "String"),
            @ApiImplicitParam(name = "fComId", value = "false下一条命令索引", dataType = "String"),
            @ApiImplicitParam(name = "returnCmdId", value = "返回命令", dataType = "Long"),
            @ApiImplicitParam(name = "cycleStartId", value = "循环起始索引", dataType = "String")
    })
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
    @ApiOperation("导出问题扫描逻辑列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataType = "String"),
            @ApiImplicitParam(name = "matched", value = "匹配", dataType = "String"),
            @ApiImplicitParam(name = "relativePosition", value = "相对位置", dataType = "String"),
            @ApiImplicitParam(name = "matchContent", value = "匹配内容", dataType = "String"),
            @ApiImplicitParam(name = "action", value = "动作", dataType = "String"),
            @ApiImplicitParam(name = "rPosition", value = "位置", dataType = "Integer"),
            @ApiImplicitParam(name = "length", value = "长度", dataType = "String"),
            @ApiImplicitParam(name = "exhibit", value = "是否显示", dataType = "String"),
            @ApiImplicitParam(name = "wordName", value = "取词名称", dataType = "String"),
            @ApiImplicitParam(name = "compare", value = "比较", dataType = "String"),
            @ApiImplicitParam(name = "tNextId", value = "true下一条分析索引", dataType = "String"),
            @ApiImplicitParam(name = "tComId", value = "true下一条命令索引", dataType = "String"),
            @ApiImplicitParam(name = "problemId", value = "问题索引", dataType = "String"),
            @ApiImplicitParam(name = "fLine", value = "false行号", dataType = "String"),
            @ApiImplicitParam(name = "tLine", value = "true行号", dataType = "String"),
            @ApiImplicitParam(name = "fNextId", value = "false下一条分析索引", dataType = "String"),
            @ApiImplicitParam(name = "fComId", value = "false下一条命令索引", dataType = "String"),
            @ApiImplicitParam(name = "returnCmdId", value = "返回命令", dataType = "Long"),
            @ApiImplicitParam(name = "cycleStartId", value = "循环起始索引", dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('sql:problem_scan_logic:export')")
    @MyLog(title = "问题扫描逻辑", businessType = BusinessType.EXPORT)
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
    @ApiOperation("获取问题扫描逻辑详细信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('sql:problem_scan_logic:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(problemScanLogicService.selectProblemScanLogicById(id));
    }

    /**
     * 新增问题扫描逻辑
     */
    @ApiOperation("新增问题扫描逻辑")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataType = "String"),
            @ApiImplicitParam(name = "matched", value = "匹配", dataType = "String"),
            @ApiImplicitParam(name = "relativePosition", value = "相对位置", dataType = "String"),
            @ApiImplicitParam(name = "matchContent", value = "匹配内容", dataType = "String"),
            @ApiImplicitParam(name = "action", value = "动作", dataType = "String"),
            @ApiImplicitParam(name = "rPosition", value = "位置", dataType = "Integer"),
            @ApiImplicitParam(name = "length", value = "长度", dataType = "String"),
            @ApiImplicitParam(name = "exhibit", value = "是否显示", dataType = "String"),
            @ApiImplicitParam(name = "wordName", value = "取词名称", dataType = "String"),
            @ApiImplicitParam(name = "compare", value = "比较", dataType = "String"),
            @ApiImplicitParam(name = "tNextId", value = "true下一条分析索引", dataType = "String"),
            @ApiImplicitParam(name = "tComId", value = "true下一条命令索引", dataType = "String"),
            @ApiImplicitParam(name = "problemId", value = "问题索引", dataType = "String"),
            @ApiImplicitParam(name = "fLine", value = "false行号", dataType = "String"),
            @ApiImplicitParam(name = "tLine", value = "true行号", dataType = "String"),
            @ApiImplicitParam(name = "fNextId", value = "false下一条分析索引", dataType = "String"),
            @ApiImplicitParam(name = "fComId", value = "false下一条命令索引", dataType = "String"),
            @ApiImplicitParam(name = "returnCmdId", value = "返回命令", dataType = "Long"),
            @ApiImplicitParam(name = "cycleStartId", value = "循环起始索引", dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('sql:problem_scan_logic:add')")
    @MyLog(title = "问题扫描逻辑", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ProblemScanLogic problemScanLogic)
    {
        return toAjax(problemScanLogicService.insertProblemScanLogic(problemScanLogic));
    }

    /**
     * 修改问题扫描逻辑
     */
    @ApiOperation("修改问题扫描逻辑")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataType = "String"),
            @ApiImplicitParam(name = "matched", value = "匹配", dataType = "String"),
            @ApiImplicitParam(name = "relativePosition", value = "相对位置", dataType = "String"),
            @ApiImplicitParam(name = "matchContent", value = "匹配内容", dataType = "String"),
            @ApiImplicitParam(name = "action", value = "动作", dataType = "String"),
            @ApiImplicitParam(name = "rPosition", value = "位置", dataType = "Integer"),
            @ApiImplicitParam(name = "length", value = "长度", dataType = "String"),
            @ApiImplicitParam(name = "exhibit", value = "是否显示", dataType = "String"),
            @ApiImplicitParam(name = "wordName", value = "取词名称", dataType = "String"),
            @ApiImplicitParam(name = "compare", value = "比较", dataType = "String"),
            @ApiImplicitParam(name = "tNextId", value = "true下一条分析索引", dataType = "String"),
            @ApiImplicitParam(name = "tComId", value = "true下一条命令索引", dataType = "String"),
            @ApiImplicitParam(name = "problemId", value = "问题索引", dataType = "String"),
            @ApiImplicitParam(name = "fLine", value = "false行号", dataType = "String"),
            @ApiImplicitParam(name = "tLine", value = "true行号", dataType = "String"),
            @ApiImplicitParam(name = "fNextId", value = "false下一条分析索引", dataType = "String"),
            @ApiImplicitParam(name = "fComId", value = "false下一条命令索引", dataType = "String"),
            @ApiImplicitParam(name = "returnCmdId", value = "返回命令", dataType = "Long"),
            @ApiImplicitParam(name = "cycleStartId", value = "循环起始索引", dataType = "String")
    })
    @PreAuthorize("@ss.hasPermi('sql:problem_scan_logic:edit')")
    @MyLog(title = "问题扫描逻辑", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ProblemScanLogic problemScanLogic)
    {
        return toAjax(problemScanLogicService.updateProblemScanLogic(problemScanLogic));
    }

    /**
     * 删除问题扫描逻辑
     */
    @ApiOperation("删除问题扫描逻辑")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "ids", value = "主键", dataType = "String[]")
    })
    @PreAuthorize("@ss.hasPermi('sql:problem_scan_logic:remove')")
    @MyLog(title = "问题扫描逻辑", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(problemScanLogicService.deleteProblemScanLogicByIds(ids));
    }


    /**
     * 定义修复问题命令时 获取定义的参数名的方法  由getParameterNameCollectionNo方法优化得到
     * @method: getParameterNameCollection    命令ID 没有带ID之前的 方法
     * @Param: [totalQuestionTableId]
     * @return: java.util.List<java.lang.String>
     */
    @ApiOperation("获取定义的参数名")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "totalQuestionTableId", value = "交换机问题ID", dataType = "String")
    })
    @GetMapping("/getParameterNameCollection/{totalQuestionTableId}")
    public List<String> getParameterNameCollection(@PathVariable String totalQuestionTableId){
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
