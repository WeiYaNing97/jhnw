package com.sgcc.web.controller.sql;

import com.sgcc.common.annotation.Log;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.page.TableDataInfo;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 问题扫描逻辑Controller
 * 
 * @author 韦亚宁
 * @date 2021-12-14
 */
@RestController
@RequestMapping("/sql/problem_scan_logic")
//事务
@Transactional(rollbackFor = Exception.class)
public class ProblemScanLogicController extends BaseController
{
    @Autowired
    private IProblemScanLogicService problemScanLogicService;
    @Autowired
    private ICommandLogicService commandLogicService;
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


    /*=====================================================================================================================
    =====================================================================================================================
    =====================================================================================================================*/

    /*获取参数名：用户名、密码*/
    /**
     * @method: getParameterNameCollection
     * @Param: [totalQuestionTableId]
     * @return: java.util.List<java.lang.String>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("/getParameterNameCollection")
    public List<String> getParameterNameCollection(Long totalQuestionTableId){
        //根据问题ID 获取问题表数据
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(totalQuestionTableId);
        //如果问题表数据没有定义 扫描命令的ID 则 返回null
        if (totalQuestionTable == null
                || totalQuestionTable.getCommandId() == null
                || totalQuestionTable.getCommandId().equals("")){
            return null;
        }
        //扫描命令的ID
        String commandIdString = totalQuestionTable.getCommandId();
        //hashset 获得 参数名 唯一
        HashSet<String> parameterName = new HashSet<>();
        do {
            List<CommandLogic> commandLogics = new ArrayList<>();
            String[] commandIdSplit = commandIdString.split(":");
            commandIdString = "";
            for (String id:commandIdSplit){
                CommandLogic commandLogic = commandLogicService.selectCommandLogicById(id);
                commandLogics.add(commandLogic);
            }

            for (CommandLogic commandLogic:commandLogics){
                if (!(commandLogic.getProblemId().equals("0"))){
                    List<ProblemScanLogic> problemScanLogicList = problemScanLogicList(commandLogic.getProblemId());
                    for (ProblemScanLogic problemScanLogic:problemScanLogicList){
                        //parameterName
                        if (problemScanLogic.getWordName()!=null){
                            parameterName.add(problemScanLogic.getWordName());
                        }

                        if (problemScanLogic.getfComId()!=null){
                            commandIdString = commandIdString +problemScanLogic.getfComId() +":";
                        }else if (problemScanLogic.gettComId()!=null){
                            commandIdString = commandIdString +problemScanLogic.gettComId() +":";
                        }
                    }
                }

            }
            if (commandIdString.indexOf(":")!=-1){
                commandIdString = commandIdString.substring(0,commandIdString.length()-1);
            }
        }while (!(commandIdString.equals("")));

        List<String> parameterNameList = new ArrayList<>();
        for (String name:parameterName){
            parameterNameList.add(name);
        }
        return parameterNameList;
    }

    /**
     * @method: 根据 首分析ID 获取全部分析 并拆分 成功失败合实体类
     * @Param: [problemScanLogicID]
     * @return: java.util.List<com.sgcc.sql.domain.ProblemScanLogic>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("problemScanLogicList")
    public List<ProblemScanLogic> problemScanLogicList(String problemScanLogicID){
        //String problemScanLogicID = "1649726283752";
        boolean contain = false;
        HashSet<String> problemScanLogicIDList = new HashSet<>();
        problemScanLogicIDList.add(problemScanLogicID);
        do {
            String  problemScanID = "";
            String[] problemScanLogicIDsplit = problemScanLogicID.split(":");
            for (String id:problemScanLogicIDsplit){
                ProblemScanLogic problemScanLogic = problemScanLogicService.selectProblemScanLogicById(id);
                if (problemScanLogic ==null){
                    return null;
                }
                if (problemScanLogic.gettNextId()!="" && problemScanLogic.gettNextId()!=null && !(isContainChinese(problemScanLogic.gettNextId()))){
                    problemScanID += problemScanLogic.gettNextId()+":";
                }
                if (problemScanLogic.getfNextId()!="" && problemScanLogic.getfNextId()!=null && !(isContainChinese(problemScanLogic.getfNextId()))){
                    problemScanID += problemScanLogic.getfNextId()+":";
                }
            }

            if (problemScanID.equals("")){
                break;
            }

            String[] problemScanIDsplit = problemScanID.split(":");
            problemScanID = "";
            for (String id:problemScanIDsplit){
                for (String hashSetid:problemScanLogicIDList){
                    if (!(id.equals(hashSetid))){
                        problemScanLogicIDList.add(id);
                        problemScanID += id+":";
                    }
                    break;
                }
            }

            if (!(problemScanID.equals(""))){
                contain = true;
                problemScanLogicID = problemScanID.substring(0,problemScanID.length()-1);
            }else {
                contain = false;
            }
        }while (contain);

        List<ProblemScanLogic> ProblemScanLogicList = new ArrayList<>();
        for (String id:problemScanLogicIDList){
            ProblemScanLogic problemScanLogic = problemScanLogicService.selectProblemScanLogicById(id);
            ProblemScanLogicList.add(problemScanLogic);
        }
        List<ProblemScanLogic> ProblemScanLogics = new ArrayList<>();
        for (ProblemScanLogic problemScanLogic:ProblemScanLogicList){

            if (problemScanLogic.getfLine()!=null){
                ProblemScanLogic problemScanLogicf = new ProblemScanLogic();
                problemScanLogicf.setId(problemScanLogic.getId());
                problemScanLogicf.setfLine(problemScanLogic.getfLine());
                problemScanLogicf.setfNextId(problemScanLogic.getfNextId());
                problemScanLogicf.setProblemId(problemScanLogic.getProblemId());
                problemScanLogicf.setfComId(problemScanLogic.getfComId());
                problemScanLogic.setfLine(null);
                problemScanLogic.setfNextId(null);
                problemScanLogic.setProblemId(null);
                problemScanLogic.setfComId(null);
                ProblemScanLogics.add(problemScanLogicf);
            }
            ProblemScanLogics.add(problemScanLogic);
        }

        return ProblemScanLogics;
    }

    /**
     * 根据正则表达式判断字符是否为汉字
     */
    public static boolean isContainChinese( String str) {
        String regex = "[\u4e00-\u9fa5]";   //汉字的Unicode取值范围
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(str);
        return match.find();
    }

}
