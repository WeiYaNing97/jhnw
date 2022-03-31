package com.sgcc.web.controller.sql;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.connect.translate.Stack;
import com.sgcc.sql.domain.CommandLogic;
import com.sgcc.sql.domain.ProblemScanLogic;
import com.sgcc.sql.service.ICommandLogicService;
import com.sgcc.sql.service.IProblemScanLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年03月22日 11:17
 */
@RestController
@RequestMapping("/sql/DefinitionProblemController")
//事务
//@Transactional(rollbackFor = Exception.class)
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


    @RequestMapping("definitionProblem")
    public void definitionProblem(){//@RequestBody List<ProblemScanLogic> problemScanLogicList

        List<ProblemScanLogic> pojoList = new ArrayList<>();
        ProblemScanLogic problemScanLogic = new ProblemScanLogic();
        //匹配成功
        problemScanLogic.setMatched("精确匹配");
        problemScanLogic.setRelativePosition("null");
        problemScanLogic.setMatchContent("local-user");
        problemScanLogic.settLine("1");
        problemScanLogic.settNextId("成功");
        pojoList.add(problemScanLogic);
        problemScanLogic = new ProblemScanLogic();
        //取词
        problemScanLogic.setAction("取词");
        problemScanLogic.setRelativePosition("0,0");
        problemScanLogic.setMatchContent("local-user");
        problemScanLogic.setMatched("null");
        problemScanLogic.settLine("2");
        problemScanLogic.settNextId("null");
        problemScanLogic.setrPosition(1);
        problemScanLogic.setLength("1W");
        pojoList.add(problemScanLogic);
        problemScanLogic = new ProblemScanLogic();
        //匹配成功
        problemScanLogic.setMatched("精确匹配");
        problemScanLogic.setRelativePosition("1,0");
        problemScanLogic.setMatchContent("password simple");
        problemScanLogic.settLine("3");
        problemScanLogic.settNextId("成功");
        pojoList.add(problemScanLogic);
        problemScanLogic = new ProblemScanLogic();
        //取词
        problemScanLogic.setAction("取词");
        problemScanLogic.setRelativePosition("0,0");
        problemScanLogic.setMatchContent("password simple");
        problemScanLogic.setMatched("null");
        problemScanLogic.settLine("4");
        problemScanLogic.settNextId("null");
        problemScanLogic.setrPosition(1);
        problemScanLogic.setLength("1W");
        pojoList.add(problemScanLogic);
        problemScanLogic = new ProblemScanLogic();
        //匹配失败
        problemScanLogic.settLine("5");
        problemScanLogic.settNextId("失败");
        pojoList.add(problemScanLogic);
        problemScanLogic = new ProblemScanLogic();
        //取词
        problemScanLogic.setAction("取词");
        problemScanLogic.setRelativePosition("0,0");
        problemScanLogic.setMatchContent("password simple1234");
        problemScanLogic.setMatched("null");
        problemScanLogic.settLine("6");
        problemScanLogic.settNextId("null");
        problemScanLogic.setrPosition(1);
        problemScanLogic.setLength("1W");
        pojoList.add(problemScanLogic);
        problemScanLogic = new ProblemScanLogic();
        //取词
        problemScanLogic.setAction("取词");
        problemScanLogic.setRelativePosition("0,0");
        problemScanLogic.setMatchContent("password simple5678");
        problemScanLogic.setMatched("null");
        problemScanLogic.settLine("7");
        problemScanLogic.settNextId("null");
        problemScanLogic.setrPosition(1);
        problemScanLogic.setLength("1W");
        pojoList.add(problemScanLogic);
        problemScanLogic = new ProblemScanLogic();
        //匹配失败
        problemScanLogic.settLine("8");
        problemScanLogic.settNextId("失败");
        pojoList.add(problemScanLogic);
        problemScanLogic = new ProblemScanLogic();
        //比较
        problemScanLogic.setAction("取词");
        problemScanLogic.setRelativePosition("0,0");
        problemScanLogic.setMatchContent("比较");
        problemScanLogic.setMatched("null");
        problemScanLogic.settLine("9");
        problemScanLogic.settNextId("null");
        problemScanLogic.setrPosition(1);
        problemScanLogic.setLength("1W");
        pojoList.add(problemScanLogic);

        //上一条ID
        Long oneId = 0l;
        //当前插入条ID
        Long twoId = 0l;
        //是否失败
        String SuccessOrFailure = "null";

        int number = 0;
        Stack stack = new Stack();
        for (ProblemScanLogic pojo:pojoList){
            oneId = twoId;
            if (pojo.gettNextId().equals("成功")){
                int i = problemScanLogicService.insertProblemScanLogic(pojo);
                twoId = pojo.getId();
                stack.push(twoId);
                if (oneId!=0){
                    ProblemScanLogic problemScanLogic1 = problemScanLogicService.selectProblemScanLogicById(oneId);
                    problemScanLogic1.settNextId(twoId.toString());
                    int i1 = problemScanLogicService.updateProblemScanLogic(problemScanLogic1);
                }
                SuccessOrFailure = "成功"+pojo.getId();
            }else if (pojo.gettNextId().equals("失败")){
                oneId = (Long)stack.pop();
                ProblemScanLogic problemScanLogic1 = problemScanLogicService.selectProblemScanLogicById(oneId);
                problemScanLogic1.setfNextId(pojo.gettNextId());
                problemScanLogic1.setfLine(pojo.gettLine());
                int i1 = problemScanLogicService.updateProblemScanLogic(problemScanLogic1);
                SuccessOrFailure = "失败"+problemScanLogic1.getId();
            } else if (pojo.gettNextId().equals("null")){
                //如果上一级为成功或者失败
                if (SuccessOrFailure.indexOf("成功")!=-1 || SuccessOrFailure.indexOf("失败")!=-1){
                    int i = problemScanLogicService.insertProblemScanLogic(pojo);
                    twoId = pojo.getId();
                    String substring = SuccessOrFailure.substring(2, SuccessOrFailure.length());
                    Long LongId = Long.valueOf(substring).longValue();
                    ProblemScanLogic problemScanLogic1 = problemScanLogicService.selectProblemScanLogicById(LongId);
                    if (SuccessOrFailure.indexOf("成功")!=-1){
                        problemScanLogic1.settNextId(twoId.toString());
                    }else if (SuccessOrFailure.indexOf("失败")!=-1){
                        problemScanLogic1.setfNextId(twoId.toString());
                    }
                    int i1 = problemScanLogicService.updateProblemScanLogic(problemScanLogic1);
                }else if (SuccessOrFailure.indexOf("null")!=-1){
                    int i = problemScanLogicService.insertProblemScanLogic(pojo);
                    twoId = pojo.getId();
                    if (oneId!=0){
                        ProblemScanLogic problemScanLogic1 = problemScanLogicService.selectProblemScanLogicById(oneId);
                        problemScanLogic1.settNextId(twoId.toString());
                        int i1 = problemScanLogicService.updateProblemScanLogic(problemScanLogic1);
                    }
                }

                SuccessOrFailure = "null";
            }
        }

    }

}