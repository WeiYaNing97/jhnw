package com.sgcc.web.controller.sql;

import com.sgcc.common.annotation.Log;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.sql.domain.CommandLogic;
import com.sgcc.sql.domain.ProblemScanLogic;
import com.sgcc.sql.service.ICommandLogicService;
import com.sgcc.sql.service.IProblemScanLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年03月22日 11:17
 */
@RestController
@RequestMapping("/sql/DefinitionProblemController")
//事务
@Transactional(rollbackFor = Exception.class)
public class DefinitionProblemController extends BaseController {

    @Autowired
    private ICommandLogicService commandLogicService;
    @Autowired
    private IProblemScanLogicService problemScanLogicService;

    /**
     * 新增命令逻辑
     */
    public CommandLogic insertCommandLogic(CommandLogic commandLogic)
    {
        commandLogicService.insertCommandLogic(commandLogic);
        return commandLogic;
    }

    /**
     * 修改命令逻辑
     */
    public CommandLogic updateCommandLogic(CommandLogic commandLogic)
    {
        commandLogicService.updateCommandLogic(commandLogic);
        return commandLogic;
    }

    /**
     * 新增问题扫描逻辑
     */
    public ProblemScanLogic insertProblemScanLogic(ProblemScanLogic problemScanLogic)
    {
            problemScanLogicService.insertProblemScanLogic(problemScanLogic);
        return problemScanLogic;
    }

    /**
     * 修改问题扫描逻辑
     */
    public ProblemScanLogic updateProblemScanLogic( ProblemScanLogic problemScanLogic)
    {
        problemScanLogicService.updateProblemScanLogic(problemScanLogic);
        return problemScanLogic;
    }

    @RequestMapping("/definitionProblem")
    public AjaxResult DefinitionProblem(CommandLogic commandLogic,List<ProblemScanLogic> problemScanLogicList){

        commandLogic = insertCommandLogic(commandLogic);

        for (ProblemScanLogic problemScanLogic:problemScanLogicList){
            problemScanLogic = insertProblemScanLogic(problemScanLogic);
        }

        commandLogic.setProblemId("分析ID");

        commandLogic = updateCommandLogic(commandLogic);

        return null;
    }

}