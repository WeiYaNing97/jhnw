package com.sgcc.web.controller.sql;
import com.sgcc.common.annotation.Excel;
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
    public ProblemScanLogic analysisProblemScanLogic(){//@RequestBody String jsonPojo


        ProblemScanLogic problemScanLogic = new ProblemScanLogic();
        HashMap<String,String> hashMap = new HashMap<>();

        String jsonPojo = "{\"targetType\":\"match\",\"onlyIndex\":1649225359539,\"matched\":\"全文精确匹配\",\"matchContent\":\"local-user\",\"nextIndex\":1649225363210,\"pageIndex\":2}";
        jsonPojo = jsonPojo.replace("{","");
        jsonPojo = jsonPojo.replace("}","");
        String[]  jsonPojo_split = jsonPojo.split(",");

        /** 主键索引 */
        hashMap.put("id",null);
        /** 匹配 */
        hashMap.put("matched",null);
        /** 相对位置 */
        hashMap.put("relativePosition",null);
        /** 相对位置 行*/
        hashMap.put("relative",null);
        /** 相对位置 列*/
        hashMap.put("position",null);
        /** 匹配内容 */
        hashMap.put("matchContent",null);
        /** 动作 */
        hashMap.put("action",null);
        /** 位置 */
        hashMap.put("rPosition",null);
        /** 长度 */
        hashMap.put("length",null);
        /** 是否显示 */
        hashMap.put("exhibit",null);
        /** 取词名称 */
        hashMap.put("wordName",null);
        /** 比较 */
        hashMap.put("compare",null);
        /** 内容 */
        hashMap.put("content",null);
        /** true下一条分析索引 */
        hashMap.put("tNextId",null);
        /** true下一条命令索引 */
        hashMap.put("tComId",null);
        /** true问题索引 */
        hashMap.put("tProblemId",null);
        /** false行号 */
        hashMap.put("fLine",null);
        /** true行号 */
        hashMap.put("tLine",null);
        /** false下一条分析索引 */
        hashMap.put("fNextId",null);
        /** false下一条命令索引 */
        hashMap.put("fComId",null);
        /** false问题索引 */
        hashMap.put("fProblemId",null);
        /** 返回命令 */
        hashMap.put("returnCmdId",null);
        /** 循环起始ID */
        hashMap.put("cycleStartId",null);

        for (String pojo:jsonPojo_split){
            String[] split = pojo.split(":");
            String split0 = split[0].replace("\"","");
            String split1 = split[1].replace("\"","");
            switch (split0){
                case "onlyIndex"://本层ID 主键ID
                    hashMap.put("id",split1);
                    break;
                case "matched":// 匹配
                    if (split1.equals("全文精确匹配")){
                        /** 匹配 */
                        hashMap.put("matched","精确匹配");
                        /** 相对位置 */
                        hashMap.put("relativePosition","null");
                    }else if (split1.equals("全文模糊匹配")){
                        /** 匹配 */
                        hashMap.put("matched","模糊匹配");
                        /** 相对位置 */
                        hashMap.put("relativePosition","null");
                    }else if (split1.equals("按行精确匹配")){
                        /** 匹配 */
                        hashMap.put("matched","精确匹配");
                    }else if (split1.equals("按行模糊匹配")){
                        /** 匹配 */
                        hashMap.put("matched","模糊匹配");
                    }
                        break;
                case "relative":
                    /** 相对位置 行*/
                    hashMap.put("relative",split1);
                    break;
                case "position":
                    /** 相对位置 列*/
                    hashMap.put("position",split1);
                    break;
                case "matchContent":
                    /** 匹配内容 */
                    hashMap.put("matchContent",split1);
                    break;
                case "action":
                    /** 动作 */
                    hashMap.put("action",split1);
                    break;
                case "rPosition":
                    /** 位置 */
                    hashMap.put("rPosition",split1);
                    break;
                case "length":
                    /** 长度 */
                    hashMap.put("length",split1);
                    break;
                case "exhibit":
                    /** 是否显示 */
                    hashMap.put("exhibit",split1);
                    break;
                case "compare":
                    /** 比较 */
                    hashMap.put("compare",split1);
                case "content":
                    /** 内容 */
                    hashMap.put("content",split1);
                case "nextIndex"://下一分析ID 也是 首分析ID
                    /** true下一条分析索引 */
                    hashMap.put("tNextId",split1);
                    break;
                case "pageIndex"://本层ID 主键ID
                    /** true行号 */
                    hashMap.put("tLine",split1);
                    break;
            }
        }

        if (hashMap.get("matched").indexOf("按行")!=-1){
            /** 相对位置 */
            hashMap.put("relativePosition",hashMap.get("relative")+","+hashMap.get("position"));
        }

        if ("命令&问题".equals("命令")){
            /** true下一条命令索引 */
            hashMap.put("tComId",hashMap.get("tNextId"));
        }else if ("命令&问题".equals("问题")){
            /** /** true问题索引 */
            hashMap.put("tProblemId",hashMap.get("tNextId"));
        }

        if ("失败".equals("失败")){
            /** false行号 */
            hashMap.put("fLine",hashMap.get("tLine"));
            /** false下一条分析索引 */
            hashMap.put("fNextId",hashMap.get("tNextId"));
            /** false下一条命令索引 */
            hashMap.put("fComId",hashMap.get("tComId"));
            /** false问题索引 */
            hashMap.put("fProblemId",hashMap.get("tProblemId"));

            /** true下一条分析索引 */
            hashMap.put("tNextId",null);
            /** true下一条命令索引 */
            hashMap.put("tComId",null);
            /** true问题索引 */
            hashMap.put("tProblemId",null);
            /** true行号 */
            hashMap.put("tLine",null);
        }


        /** 主键索引 */
        problemScanLogic.setId(hashMap.get("id"));
        /** 匹配 */
        problemScanLogic.setMatched(hashMap.get("matched"));
        /** 相对位置 */
        problemScanLogic.setRelativePosition(hashMap.get("relativePosition"));
        /** 匹配内容 */
        problemScanLogic.setMatchContent(hashMap.get("matchContent"));
        /** 动作 */
        problemScanLogic.setAction(hashMap.get("action"));
        if (hashMap.get("rPosition")!=null){
            /** 位置 */
            problemScanLogic.setrPosition(Integer.valueOf(hashMap.get("rPosition")).intValue());
        }
        /** 长度 */
        problemScanLogic.setLength(hashMap.get("length"));
        /** 是否显示 */
        problemScanLogic.setExhibit(hashMap.get("exhibit"));
        /** 取词名称 */
        problemScanLogic.setWordName(hashMap.get("wordName"));
        /** 比较 */
        problemScanLogic.setCompare(hashMap.get("compare"));
        /** 内容 */
        problemScanLogic.setContent(hashMap.get("content"));
        /** true下一条分析索引 */
        problemScanLogic.settNextId(hashMap.get("tNextId"));
        /** true下一条命令索引 */
        problemScanLogic.settComId(hashMap.get("tComId"));
        /** true问题索引 */
        problemScanLogic.settProblemId(hashMap.get("tProblemId"));
        /** false行号 */
        problemScanLogic.setfLine(hashMap.get("fLine"));
        /** true行号 */
        problemScanLogic.settLine(hashMap.get("tLine"));
        /** false下一条分析索引 */
        problemScanLogic.setfNextId(hashMap.get("fNextId"));
        /** false下一条命令索引 */
        problemScanLogic.setfComId(hashMap.get("fComId"));
        /** false问题索引 */
        problemScanLogic.setfProblemId(hashMap.get("fProblemId"));
        if (hashMap.get("returnCmdId")!=null){
            /** 返回命令 */
            problemScanLogic.setReturnCmdId(Integer.valueOf(hashMap.get("returnCmdId")).longValue());
        }
        if (hashMap.get("cycleStartId")!=null){
            /** 循环起始ID */
            problemScanLogic.setCycleStartId(Integer.valueOf(hashMap.get("cycleStartId")).longValue());
        }

        int i = problemScanLogicService.insertProblemScanLogic(problemScanLogic);

        return null;
    }

    @RequestMapping("analysisCommandLogic")
    public CommandLogic analysisCommandLogic(@RequestBody String jsonPojo){
        //String jsonPojo = "{\"targetType\":\"command\",\"onlyIndex\":164922535744,\"resultCheckId\":\"1\",\"command\":\"disply cu\",\"nextIndex\":1649225359539,\"pageIndex\":1}";
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
            String split0 = split[0].replace("\"","");
            String split1 = split[1].replace("\"","");
            switch (split0){
                case "onlyIndex"://本层ID 主键ID
                    hashMap.put("onlyIndex",split1);
                    break;
                case "resultCheckId":// 常规校验1 自定义校验0
                    hashMap.put("resultCheckId",split1);
                    break;
                case "command":// 命令
                    hashMap.put("command",split1);
                    break;
                case "nextIndex"://下一分析ID 也是 首分析ID
                    hashMap.put("nextIndex",split1);
                    break;
                case "pageIndex"://本层ID 主键ID
                    hashMap.put("pageIndex",split1);
                    break;
            }
        }

        //如果 常规检验 的话 下一ID  应是 下一命令ID
        //下一分析ID  应是  0
        if (hashMap.get("resultCheckId").equals("1")){
            hashMap.put("endIndex",hashMap.get("nextIndex"));
            hashMap.put("nextIndex","0");
        }

        /** 主键索引 */
        commandLogic.setId(hashMap.get("onlyIndex"));
        /** 状态 */
        commandLogic.setState(null);
        /** 命令 */
        commandLogic.setCommand(hashMap.get("command"));
        /** 返回结果验证id */
        commandLogic.setResultCheckId(hashMap.get("resultCheckId"));
        /** 返回分析id */
        commandLogic.setProblemId(hashMap.get("nextIndex"));
        /** 命令结束索引 */
        commandLogic.setEndIndex(hashMap.get("endIndex"));

        int i = commandLogicService.insertCommandLogic(commandLogic);

        return commandLogic;
    }
}