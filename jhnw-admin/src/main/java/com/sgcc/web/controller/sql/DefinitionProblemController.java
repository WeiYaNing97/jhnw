package com.sgcc.web.controller.sql;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.connect.translate.Stack;
import com.sgcc.sql.domain.CommandLogic;
import com.sgcc.sql.domain.ProblemScanLogic;
import com.sgcc.sql.service.ICommandLogicService;
import com.sgcc.sql.service.IProblemScanLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
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
        String oneId = null;
        //当前插入条ID
        String twoId = null;
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
                if (oneId!=null){
                    ProblemScanLogic problemScanLogic1 = problemScanLogicService.selectProblemScanLogicById(oneId);
                    problemScanLogic1.settNextId(twoId.toString());
                    int i1 = problemScanLogicService.updateProblemScanLogic(problemScanLogic1);
                }
                SuccessOrFailure = "成功"+pojo.getId();
            }else if (pojo.gettNextId().equals("失败")){
                oneId = (String) stack.pop();
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
                    String LongId = substring;
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
                    if (oneId!=null){
                        ProblemScanLogic problemScanLogic1 = problemScanLogicService.selectProblemScanLogicById(oneId);
                        problemScanLogic1.settNextId(twoId.toString());
                        int i1 = problemScanLogicService.updateProblemScanLogic(problemScanLogic1);
                    }
                }

                SuccessOrFailure = "null";
            }
        }

    }

    @RequestMapping("definitionProblemJsonPojo")
    public void definitionProblemJsonPojo(){

    }

    @RequestMapping("analysisProblemScanLogic")
    private ProblemScanLogic analysisProblemScanLogic(String jsonPojo){
        ProblemScanLogic problemScanLogic = new ProblemScanLogic();
        /** 主键索引 */
        String id = null;
        /** 匹配 */
        String matched= null;
        /** 相对位置 */
        String relativePosition= null;
        /** 匹配内容 */
        String matchContent= null;
        /** 动作 */
        String action= null;
        /** 位置 */
        Integer rPosition= null;
        /** 长度 */
        String length = null;
        /** 是否显示 */
        String exhibit = null;
        /** 取词名称 */
        String wordName= null;
        /** 比较 */
        String compare= null;
        /** 内容 */
        String content= null;
        /** true下一条分析索引 */
        String tNextId= null;
        /** true下一条命令索引 */
        String tComId= null;
        /** true问题索引 */
        String tProblemId= null;
        /** false行号 */
        String fLine= null;
        /** true行号 */
        String tLine= null;
        /** false下一条分析索引 */
        String fNextId= null;
        /** false下一条命令索引 */
        String fComId= null;
        /** false问题索引 */
        String fProblemId= null;
        /** 返回命令 */
        Long returnCmdId= null;
        /** 循环起始ID */
        Long cycleStartId= null;

        /** 主键索引 */
        problemScanLogic.setId(id);
        /** 匹配 */
        problemScanLogic.setMatched(matched);
        /** 相对位置 */
        problemScanLogic.setRelativePosition(relativePosition);
        /** 匹配内容 */
        problemScanLogic.setMatchContent(matchContent);
        /** 动作 */
        problemScanLogic.setAction(action);
        /** 位置 */
        problemScanLogic.setrPosition(rPosition);
        /** 长度 */
        problemScanLogic.setLength(length);
        /** 是否显示 */
        problemScanLogic.setExhibit(exhibit);
        /** 取词名称 */
        problemScanLogic.setWordName(wordName);
        /** 比较 */
        problemScanLogic.setCompare(compare);
        /** 内容 */
        problemScanLogic.setContent(content);
        /** true下一条分析索引 */
        problemScanLogic.settNextId(tNextId);
        /** true下一条命令索引 */
        problemScanLogic.settComId(tComId);
        /** true问题索引 */
        problemScanLogic.settProblemId(tProblemId);
        /** false行号 */
        problemScanLogic.setfLine(fLine);
        /** true行号 */
        problemScanLogic.settLine(tLine);
        /** false下一条分析索引 */
        problemScanLogic.setfNextId(fNextId);
        /** false下一条命令索引 */
        problemScanLogic.setfComId(fComId);
        /** false问题索引 */
        problemScanLogic.setfProblemId(fProblemId);
        /** 返回命令 */
        problemScanLogic.setReturnCmdId(returnCmdId);
        /** 循环起始ID */
        problemScanLogic.setCycleStartId(cycleStartId);
        return null;
    }

    @RequestMapping("analysisCommandLogic")
    private CommandLogic analysisCommandLogic(String jsonPojo){
        CommandLogic commandLogic = new CommandLogic();
        jsonPojo = jsonPojo.replace("{","");
        jsonPojo = jsonPojo.replace("}","");
        String[]  jsonPojo_split = jsonPojo.split(",");

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("onlyIndex",null);
        hashMap.put("resultCheckId","0");
        hashMap.put("command",null);
        hashMap.put("nextIndex","0");
        hashMap.put("pageIndex",null);
        hashMap.put("endIndex","0");

        for (String pojo:jsonPojo_split){
            String[] split = pojo.split(":");
            String split0 = split[0].substring(1, split[0].length() - 1);
            String split1 = split[1].substring(1, split[1].length() - 1);
            switch (split0){
                case "onlyIndex"://本层ID 主键ID
                    hashMap.put("onlyIndex",split1);
                case "resultCheckId":// 常规校验0 自定义校验0
                    hashMap.put("resultCheckId",split1);
                case "command":// 命令
                    hashMap.put("command",split1);
                case "nextIndex"://下一分析ID 也是 首分析ID
                    hashMap.put("nextIndex",split1);
                case "pageIndex"://本层ID 主键ID
                    hashMap.put("pageIndex",split1);
            }
        }

        /** 主键索引 */
        //commandLogic.setId(hashMap.get("onlyIndex"));
        /** 状态 */
        commandLogic.setState(null);
        /** 命令 */
        commandLogic.setCommand(hashMap.get("command"));
        /** 返回结果验证id */
        //commandLogic.setResultCheckId(hashMap.get("resultCheckId"));
        /** 返回分析id */
        commandLogic.setProblemId(hashMap.get("nextIndex"));
        /** 命令结束索引 */
        //commandLogic.setEndIndex(hashMap.get("endIndex"));

        return commandLogic;
    }
}