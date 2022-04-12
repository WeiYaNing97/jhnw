package com.sgcc.web.controller.sql;
import com.sgcc.common.annotation.Excel;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.connect.translate.Stack;
import com.sgcc.sql.domain.CommandLogic;
import com.sgcc.sql.domain.ProblemScanLogic;
import com.sgcc.sql.service.ICommandLogicService;
import com.sgcc.sql.service.IProblemScanLogicService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    @Autowired
    private RedisTemplate redisTemplate;

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


    public void definitionProblem1(List<ProblemScanLogic> pojoList){
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
    
    /**
    * @method: 将相同ID  时间戳 的 实体类 放到一个实体
    * @Param: [pojoList]
    * @return: java.util.List<com.sgcc.sql.domain.ProblemScanLogic>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    public List<ProblemScanLogic> definitionProblem(List<ProblemScanLogic> pojoList){
        HashSet<String> hashSet = new HashSet<>();
        for (ProblemScanLogic problemScanLogic:pojoList){
            hashSet.add(problemScanLogic.getId());
        }
        List<ProblemScanLogic> problemScanLogicList = new ArrayList<>();
        for (String problemScanLogicId:hashSet){
            ProblemScanLogic problemScanLogic = new ProblemScanLogic();
            problemScanLogic.setId(problemScanLogicId);
            problemScanLogicList.add(problemScanLogic);
        }
        for (ProblemScanLogic pojo:pojoList){
            for (ProblemScanLogic problemScanLogic:problemScanLogicList){
                if (pojo.getId().equals(problemScanLogic.getId())){
                    if (pojo.getfLine()==null
                            && pojo.getfNextId()==null
                            && pojo.getfComId()==null
                            && pojo.getfProblemId()==null){
                         BeanUtils.copyProperties(pojo,problemScanLogic);
                    }else {
                        problemScanLogic.setfLine(pojo.getfLine());
                        problemScanLogic.setfNextId(pojo.getfNextId());
                        problemScanLogic.setfComId(pojo.getfComId());
                        problemScanLogic.setfProblemId(pojo.getfProblemId());
                    }
                }
            }
        }
        return problemScanLogicList;
    }

    @RequestMapping("definitionProblemJsonPojo")
    public void definitionProblemJsonPojo(@RequestBody List<String> jsonPojoList){//@RequestBody List<String> jsonPojoList
        //List<String> jsonPojoList = new ArrayList<>();
        //String s0="{\"targetType\":\"command\",\"onlyIndex\":1649662321060,\"trueFalse\":\"\",\"command\":\"sys\",\"nextIndex\":1649662324652,\"pageIndex\":1}";
        //String s1="{\"targetType\":\"match\",\"onlyIndex\":1649662324652,\"trueFalse\":\"成功\",\"matched\":\"全文精确匹配\",\"matchContent\":\"local\",\"nextIndex\":1649662331436,\"pageIndex\":2}";
        //String s2="{\"targetType\":\"takeword\",\"onlyIndex\":1649662331436,\"trueFalse\":\"\",\"action\":\"取词\",\"rPosition\":\"1\",\"length\":\"1\",\"exhibit\":\"显示\",\"wordName\":\"用户名\",\"nextIndex\":1649662339524,\"pageIndex\":3,\"matchContent\":\"local\"}";
        //String s3="{\"targetType\":\"lipre\",\"onlyIndex\":1649662339524,\"trueFalse\":\"成功\",\"matched\":\"按行精确匹配\",\"position\":0,\"relative\":\"1\",\"matchContent\":\"pass\",\"nextIndex\":1649662346276,\"pageIndex\":4}";
        //String s4="{\"targetType\":\"takeword\",\"onlyIndex\":1649662346276,\"trueFalse\":\"\",\"action\":\"取词\",\"rPosition\":\"1\",\"length\":\"1\",\"exhibit\":\"不显示\",\"wordName\":\"密码\",\"nextIndex\":1649662339524,\"pageIndex\":5,\"matchContent\":\"pass\"}";
        //String s5="{\"targetType\":\"liprefal\",\"onlyIndex\":1649662339524,\"trueFalse\":\"失败\",\"nextIndex\":1649662324652,\"pageIndex\":6}";
        //String s6="{\"targetType\":\"matchfal\",\"onlyIndex\":1649662324652,\"trueFalse\":\"失败\",\"pageIndex\":7}";
        //jsonPojoList.add(s0);
        //jsonPojoList.add(s1);
        //jsonPojoList.add(s2);
        //jsonPojoList.add(s3);
        //jsonPojoList.add(s4);
        //jsonPojoList.add(s5);
        //jsonPojoList.add(s6);

        List<CommandLogic> commandLogicList = new ArrayList<>();
        List<ProblemScanLogic> problemScanLogicList = new ArrayList<>();
        for (int number=0;number<jsonPojoList.size();number++){
            if (jsonPojoList.get(number).indexOf("command")!=-1){
                CommandLogic commandLogic = analysisCommandLogic(jsonPojoList.get(number));
                commandLogicList.add(commandLogic);
                continue;
            }else if (jsonPojoList.get(number).indexOf("command") ==-1){
                if (number+1<jsonPojoList.size()){
                    if (jsonPojoList.get(number+1).indexOf("command") !=-1){
                        //本条是分析 下一条是 问题
                        ProblemScanLogic problemScanLogic = analysisProblemScanLogic(jsonPojoList.get(number), "命令");
                        problemScanLogicList.add(problemScanLogic);
                        continue;
                    }else if (jsonPojoList.get(number+1).indexOf("问题") !=-1){
                        //本条是分析 下一条是 问题
                        ProblemScanLogic problemScanLogic = analysisProblemScanLogic(jsonPojoList.get(number), "问题");
                        problemScanLogicList.add(problemScanLogic);
                        continue;
                    }else {
                        //本条是分析 下一条是 问题
                        ProblemScanLogic problemScanLogic = analysisProblemScanLogic(jsonPojoList.get(number), "分析");
                        problemScanLogicList.add(problemScanLogic);
                        continue;
                    }
                }else {
                    //本条是分析 下一条是 问题
                    ProblemScanLogic problemScanLogic = analysisProblemScanLogic(jsonPojoList.get(number), "分析");
                    problemScanLogicList.add(problemScanLogic);
                    continue;
                }
            }
        }

        List<ProblemScanLogic> problemScanLogics = definitionProblem(problemScanLogicList);

        for (ProblemScanLogic problemScanLogic:problemScanLogics){
            int i = problemScanLogicService.insertProblemScanLogic(problemScanLogic);
        }
        for (CommandLogic commandLogic:commandLogicList){
            int i = commandLogicService.insertCommandLogic(commandLogic);
        }

    }

    /**
    * @method: 字符串解析 ProblemScanLogic 实体类 并返回
    * @Param: [jsonPojo, ifCommand : 分析、命令、问题]
    * @return: com.sgcc.sql.domain.ProblemScanLogic
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("analysisProblemScanLogic")
    public ProblemScanLogic analysisProblemScanLogic(@RequestBody String jsonPojo,String ifCommand){
        ProblemScanLogic problemScanLogic = new ProblemScanLogic();
        HashMap<String,String> hashMap = new HashMap<>();
        jsonPojo = jsonPojo.replace("{","");
        jsonPojo = jsonPojo.replace("}","");
        String[]  jsonPojo_split = jsonPojo.split(",");

        /** 主键索引 */
        hashMap.put("id",null);
        /** 匹配 */
        hashMap.put("matched","null");
        /** 相对位置 */
        hashMap.put("relativePosition","null");
        /** 相对位置 行*/
        hashMap.put("relative",null);
        /** 相对位置 列*/
        hashMap.put("position",null);
        /** 匹配内容 */
        hashMap.put("matchContent",null);
        /** 动作 */
        hashMap.put("action","null");
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
        //成功失败
        hashMap.put("trueFalse",null);

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
                        hashMap.put("matched","全文精确匹配");
                        /** 相对位置 */
                        hashMap.put("relativePosition","null");
                    }else if (split1.equals("全文模糊匹配")){
                        /** 匹配 */
                        hashMap.put("matched","全文模糊匹配");
                        /** 相对位置 */
                        hashMap.put("relativePosition","null");
                    }else if (split1.equals("按行精确匹配")){
                        /** 匹配 */
                        hashMap.put("matched","按行精确匹配");
                    }else if (split1.equals("按行模糊匹配")){
                        /** 匹配 */
                        hashMap.put("matched","按行模糊匹配");
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
                    hashMap.put("exhibit",split1.equals("显示")?"是":"否");
                    break;
                case "wordName":
                    /** 是否显示 */
                    hashMap.put("wordName",split1);
                    break;
                case "compare":
                    /** 比较 */
                    hashMap.put("compare",split1);
                    break;
                case "content":
                    /** 内容 */
                    hashMap.put("content",split1);
                    break;
                case "nextIndex"://下一分析ID 也是 首分析ID
                    /** true下一条分析索引 */
                    hashMap.put("tNextId",split1);
                    break;
                case "pageIndex"://本层ID 主键ID
                    /** true行号 */
                    hashMap.put("tLine",split1);
                    break;
                case "trueFalse"://本层ID 主键ID
                    /** true行号 */
                    hashMap.put("trueFalse",split1);
                    break;
            }
        }

        if (hashMap.get("matched")!=null && hashMap.get("matched").indexOf("按行")!=-1){
            /** 相对位置 */
            hashMap.put("relativePosition",hashMap.get("relative")+","+hashMap.get("position"));
        }

        if (ifCommand.equals("命令")){
            /** true下一条命令索引 */
            hashMap.put("tComId",hashMap.get("tNextId"));
            hashMap.put("tNextId",null);
        }else if (ifCommand.equals("问题")){
            /** /** true问题索引 */
            hashMap.put("tProblemId",hashMap.get("tNextId"));
            hashMap.put("tNextId",null);
        }

        if (hashMap.get("action")!=null && hashMap.get("action").equals("取词")){
            List<ProblemScanLogic> resultList=(List<ProblemScanLogic>)redisTemplate.opsForList().leftPop("problemScanLogic");
            redisTemplate.opsForList().leftPush("problemScanLogic",resultList);
            for (ProblemScanLogic pojo:resultList){
                if ((pojo.gettNextId()!=null && pojo.gettNextId().equals(hashMap.get("id")))
                        ||(pojo.getfNextId()!=null && pojo.getfNextId().equals(hashMap.get("id")))){
                    hashMap.put("matchContent",pojo.getMatchContent());
                    break;
                }
            }
        }

        if (hashMap.get("trueFalse")!=null && hashMap.get("trueFalse").equals("失败")){
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
        if (hashMap.get("matched")!="null"){
            problemScanLogic.setMatched(hashMap.get("matched").substring(2,hashMap.get("matched").length()));
        }
        /** 相对位置 */
        if (hashMap.get("relativePosition")!=null){
            problemScanLogic.setRelativePosition(hashMap.get("relativePosition"));
        }
        /** 匹配内容 */
        if (hashMap.get("matchContent")!=null){
            problemScanLogic.setMatchContent(hashMap.get("matchContent"));
        }
        /** 动作 */
        if (hashMap.get("action")!=null){
            problemScanLogic.setAction(hashMap.get("action"));
        }
        if (hashMap.get("rPosition")!=null){
            /** 位置 */
            problemScanLogic.setrPosition(Integer.valueOf(hashMap.get("rPosition")).intValue());
        }
        /** 长度 */
        if (hashMap.get("length")!=null){
            problemScanLogic.setLength(hashMap.get("length"));
        }
        /** 是否显示 */
        if (hashMap.get("exhibit")!=null){
            problemScanLogic.setExhibit(hashMap.get("exhibit"));
        }
        /** 取词名称 */
        if (hashMap.get("wordName")!=null){
            problemScanLogic.setWordName(hashMap.get("wordName"));
        }
        /** 比较 */
        if (hashMap.get("compare")!=null){
            problemScanLogic.setCompare(hashMap.get("compare"));
        }
        /** 内容 */
        if (hashMap.get("content")!=null){
            problemScanLogic.setContent(hashMap.get("content"));
        }
        /** true下一条分析索引 */
        if (hashMap.get("tNextId")!=null){
            problemScanLogic.settNextId(hashMap.get("tNextId"));
        }
        /** true下一条命令索引 */
        if (hashMap.get("tComId")!=null){
            problemScanLogic.settComId(hashMap.get("tComId"));
        }
        /** true问题索引 */
        if (hashMap.get("tProblemId")!=null){
            problemScanLogic.settProblemId(hashMap.get("tProblemId"));
        }
        /** false行号 */
        if (hashMap.get("fLine")!=null){
            problemScanLogic.setfLine(hashMap.get("fLine"));
        }
        /** true行号 */
        if (hashMap.get("tLine")!=null){
            problemScanLogic.settLine(hashMap.get("tLine"));
        }
        /** false下一条分析索引 */
        if (hashMap.get("fNextId")!=null){
            problemScanLogic.setfNextId(hashMap.get("fNextId"));
        }
        /** false下一条命令索引 */
        if (hashMap.get("fComId")!=null){
            problemScanLogic.setfComId(hashMap.get("fComId"));
        }
        /** false问题索引 */
        if (hashMap.get("fProblemId")!=null){
            problemScanLogic.setfProblemId(hashMap.get("fProblemId"));
        }
        if (hashMap.get("returnCmdId")!=null){
            /** 返回命令 */
            problemScanLogic.setReturnCmdId(Integer.valueOf(hashMap.get("returnCmdId")).longValue());
        }
        if (hashMap.get("cycleStartId")!=null){
            /** 循环起始ID */
            problemScanLogic.setCycleStartId(Integer.valueOf(hashMap.get("cycleStartId")).longValue());
        }

        //通过redisTemplate设置值
        List<ProblemScanLogic> resultList=(List<ProblemScanLogic>)redisTemplate.opsForList().leftPop("problemScanLogic");
        resultList.add(problemScanLogic);
        redisTemplate.opsForList().leftPush("problemScanLogic",resultList);

        //int i = problemScanLogicService.insertProblemScanLogic(problemScanLogic);

        return problemScanLogic;
    }

    /***
    * @method: 字符串解析 CommandLogic 实体类 并返回
    * @Param: [jsonPojo]
    * @return: com.sgcc.sql.domain.CommandLogic
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("analysisCommandLogic")
    public CommandLogic analysisCommandLogic(@RequestBody String jsonPojo){
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

        //int i = commandLogicService.insertCommandLogic(commandLogic);

        return commandLogic;
    }
}