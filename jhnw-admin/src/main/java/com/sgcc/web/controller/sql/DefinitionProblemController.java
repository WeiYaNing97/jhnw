package com.sgcc.web.controller.sql;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.connect.translate.Stack;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.ICommandLogicService;
import com.sgcc.sql.service.IProblemScanLogicService;
import com.sgcc.sql.service.ITotalQuestionTableService;
import com.sun.org.apache.xerces.internal.xs.StringList;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private ITotalQuestionTableService totalQuestionTableService;
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
                         problemScanLogic.setMatched(pojo.getMatched()!=null?pojo.getMatched():"null");
                    }else {
                        problemScanLogic.setfLine(pojo.getfLine());
                        problemScanLogic.setfNextId(pojo.getfNextId());
                        problemScanLogic.setfComId(pojo.getfComId());
                        problemScanLogic.setfProblemId(pojo.getfProblemId());
                    }
                }
            }
        }


        ProblemScanLogic[] problemScanLogics = new ProblemScanLogic[problemScanLogicList.size()];
        for (int number=0;number<problemScanLogicList.size();number++){
            problemScanLogics[number] = problemScanLogicList.get(number);
        }
        ProblemScanLogic problemScan = null;
        for (int i =0;i<problemScanLogics.length-1;i++){
            for (int j =0;j<problemScanLogics.length - 1 - i;j++){
                if (Integer.valueOf(problemScanLogics[j].gettLine()).intValue() > Integer.valueOf(problemScanLogics[j+1].gettLine()).intValue()){
                    problemScan = problemScanLogics[j];
                    problemScanLogics[j] = problemScanLogics[j+1];
                    problemScanLogics[j+1] = problemScan;
                }

            }
        }

        problemScanLogicList = new ArrayList<>();
        for (int i =0;i<problemScanLogics.length;i++){
            problemScanLogicList.add(problemScanLogics[i]);
        }

        return problemScanLogicList;
    }

    /**
    * @method: 插入 问题数据
    * @Param: [jsonPojoList]
    * @return: void
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("definitionProblemJsonPojo")
    public void definitionProblemJsonPojo(){//@RequestBody List<String> jsonPojoList
        List<String> jsonPojoList = new ArrayList<>();
        String s0="{\"targetType\":\"command\",\"onlyIndex\":1650009428953,\"trueFalse\":\"\",\"command\":\"display cu\",\"resultCheckId\":\"0\",\"nextIndex\":1650009464386,\"pageIndex\":1}";
        String s1="{\"targetType\":\"match\",\"onlyIndex\":1650009464386,\"trueFalse\":\"成功\",\"matched\":\"全文精确匹配\",\"matchContent\":\"local-user\",\"nextIndex\":1650009472121,\"pageIndex\":2}";
        String s2="{\"targetType\":\"takeword\",\"onlyIndex\":1650009472121,\"trueFalse\":\"\",\"action\":\"取词\",\"rPosition\":\"1\",\"length\":\"1w\",\"exhibit\":\"显示\",\"wordName\":\"用户名\",\"nextIndex\":1650009481194,\"pageIndex\":3,\"matchContent\":\"local-user\"}";
        String s3="{\"targetType\":\"lipre\",\"onlyIndex\":1650009481194,\"trueFalse\":\"成功\",\"matched\":\"按行精确匹配\",\"position\":0,\"relative\":\"1\",\"matchContent\":\"password simple\",\"nextIndex\":1650009493738,\"pageIndex\":4}";
        String s4="{\"targetType\":\"takeword\",\"onlyIndex\":1650009493738,\"trueFalse\":\"\",\"action\":\"取词\",\"rPosition\":\"1\",\"length\":\"1w\",\"exhibit\":\"不显示\",\"wordName\":\"密码\",\"nextIndex\":1650009515433,\"pageIndex\":5,\"matchContent\":\"password simple\"}";
        String s5="{\"targetType\":\"prodes\",\"onlyIndex\":1650009515433,\"trueFalse\":\"\",\"action\":\"问题\",\"tProblemId\":15,\"tNextId\":\"有问题\",\"nextIndex\":1650009539915,\"pageIndex\":6}";
        String s6="{\"targetType\":\"wloop\",\"onlyIndex\":1650009539915,\"trueFalse\":\"\",\"action\":\"循环\",\"cycleStartId\":1650009464386,\"nextIndex\":1650009481194,\"pageIndex\":7}";
        String s7="{\"targetType\":\"liprefal\",\"onlyIndex\":1650009481194,\"trueFalse\":\"失败\",\"nextIndex\":1650009464386,\"pageIndex\":8}";
        String s8="{\"targetType\":\"matchfal\",\"onlyIndex\":1650009464386,\"trueFalse\":\"失败\",\"pageIndex\":9}";
        jsonPojoList.add(s0);
        jsonPojoList.add(s1);
        jsonPojoList.add(s2);
        jsonPojoList.add(s3);
        jsonPojoList.add(s4);
        jsonPojoList.add(s5);
        jsonPojoList.add(s6);
        jsonPojoList.add(s7);
        jsonPojoList.add(s8);

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
                case "tNextId"://下一分析ID 也是 首分析ID
                    /** true下一条分析索引 */
                    hashMap.put("tNextId",split1);
                    if (split1.indexOf("问题")!=-1){
                        hashMap.put("action",split1);
                    }
                    break;
                case "nextIndex"://下一分析ID 也是 首分析ID
                    /** true下一条分析索引 */
                    hashMap.put("tNextId",split1);
                    break;
                case "cycleStartId":
                    hashMap.put("cycleStartId",split1);
                    break;
                case "pageIndex"://行号
                    /** true行号 */
                    hashMap.put("tLine",split1);
                    break;
                case "trueFalse"://成功 失败
                    /** true行号 */
                    hashMap.put("trueFalse",split1);
                    break;
                case "tProblemId":
                    /** true问题索引 */
                    hashMap.put("tProblemId",split1);
                    break;
                case "fProblemId":
                    /** false问题索引 */
                    hashMap.put("fProblemId",split1);
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
            problemScanLogic.setCycleStartId(hashMap.get("cycleStartId"));
        }

        //通过redisTemplate设置值
        List<ProblemScanLogic> resultList=(List<ProblemScanLogic>)redisTemplate.opsForList().leftPop("problemScanLogic");

        if (problemScanLogic.getAction().equals("循环")){
            problemScanLogic.setfNextId(null);
            problemScanLogic.settNextId(null);
        }

        resultList.add(problemScanLogic);
        redisTemplate.opsForList().leftPush("problemScanLogic",resultList);

        //int i = problemScanLogicService.insertProblemScanLogic(problemScanLogic);

        return problemScanLogic;
    }

    /**
    * @method: problemScanLogic   转化  Sting
    * @Param: [problemScanLogic]
    * @return: java.lang.String
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    public String problemScanLogicSting(ProblemScanLogic problemScanLogic){
        ProblemScanLogicVO problemScanLogicVO = new ProblemScanLogicVO();
        problemScanLogicVO.setTrueFalse("");
        String onlyIndex = problemScanLogic.getId();
        problemScanLogicVO.setOnlyIndex(onlyIndex);
        String matched = problemScanLogic.getMatched();
        String relative = null;
        String position = null;
        if (problemScanLogic.getMatched()!=null){
            if (problemScanLogic.getMatched().equals("匹配") && problemScanLogic.getRelativePosition().equals("null")){
                matched = "全文"+problemScanLogic.getMatched();
            }else if (problemScanLogic.getMatched().equals("匹配")){
                matched = "按行"+problemScanLogic.getMatched();
                String relativePosition = problemScanLogic.getRelativePosition();
                String[] relativePositionSplit = relativePosition.split(",");
                relative = relativePositionSplit[0];
                position = relativePositionSplit[1];
            }
        }
        problemScanLogicVO.setMatched(matched);
        problemScanLogicVO.setRelative(relative);
        problemScanLogicVO.setPosition(position);
        if (problemScanLogic.getMatchContent()!=null){
            String matchContent = problemScanLogic.getMatchContent();
            problemScanLogicVO.setMatchContent(matchContent);
        }
        if (problemScanLogic.getAction()!=null){
            String action = problemScanLogic.getAction();
            problemScanLogicVO.setAction(action);
        }
        if (problemScanLogic.getrPosition()!=null){
            Integer rPosition = problemScanLogic.getrPosition();
            problemScanLogicVO.setrPosition(rPosition);
        }
        if (problemScanLogic.getLength()!=null){
            String length = problemScanLogic.getLength();
            problemScanLogicVO.setLength(length);
        }
        if (problemScanLogic.getExhibit()!=null){
            String exhibit = problemScanLogic.getExhibit().equals("是")?"显示":"不显示";
            problemScanLogicVO.setExhibit(exhibit);
        }
        if (problemScanLogic.getWordName()!=null){
            String wordName = problemScanLogic.getWordName();
            problemScanLogicVO.setWordName(wordName);
        }
        if (problemScanLogic.getCompare()!=null){
            String compare = problemScanLogic.getCompare();
            problemScanLogicVO.setCompare(compare);
        }
        if (problemScanLogic.getContent()!=null){
            String content = problemScanLogic.getContent();
            problemScanLogicVO.setContent(content);
        }
        String pageIndex = null;
        if (problemScanLogic.gettLine()!=null){
            pageIndex = problemScanLogic.gettLine();
        }
        if (problemScanLogic.getfLine()!=null){
            problemScanLogicVO.setTrueFalse("失败");
            pageIndex = problemScanLogic.getfLine();
        }
        problemScanLogicVO.setPageIndex(pageIndex);
        String nextIndex = null;
        if (problemScanLogic.gettNextId()!=null){
            nextIndex = problemScanLogic.gettNextId();
        }
        if (problemScanLogic.getfNextId()!=null){
            nextIndex = problemScanLogic.getfNextId();
        }
        if (problemScanLogic.gettComId()!=null){
            nextIndex = problemScanLogic.gettComId();
        }
        if (problemScanLogic.getfComId()!=null){
            nextIndex = problemScanLogic.getfComId();
        }
        problemScanLogicVO.setNextIndex(nextIndex);
        String problemId = null;
        if (problemScanLogic.gettProblemId()!=null){
            problemId = problemScanLogic.gettProblemId();
        }
        if (problemScanLogic.getfProblemId()!=null){
            problemId = problemScanLogic.getfProblemId();
        }
        problemScanLogicVO.setProblemId(problemId);
        String cycleStartId = null;
        if (problemScanLogic.getCycleStartId()!=null){
            cycleStartId = problemScanLogic.getCycleStartId();
        }
        problemScanLogicVO.setCycleStartId(cycleStartId);

        if (matched!=null && !(matched.equals("null")) && problemScanLogic.gettLine()!=null){
            problemScanLogicVO.setTrueFalse("成功");
        }



        String problemScanLogicVOString = problemScanLogicVO.toString();
        problemScanLogicVOString = problemScanLogicVOString.replace("'","\"");
        problemScanLogicVOString = problemScanLogicVOString.replace(",",",\"");
        problemScanLogicVOString = problemScanLogicVOString.replace("=","\"=");
        problemScanLogicVOString = problemScanLogicVOString.replace("{","{\"");
        problemScanLogicVOString = problemScanLogicVOString.replace("\" ","\"");
        problemScanLogicVOString = problemScanLogicVOString.replace(" \"","\"");
        problemScanLogicVOString = problemScanLogicVOString.replace("ProblemScanLogicVO",pageIndex+":");
        return problemScanLogicVOString;
    }

    /***
    * @method: 字符串解析 CommandLogic 实体类 并返回
    * @Param: [jsonPojo]
    * @return: com.sgcc.sql.domain.CommandLogic
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
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
                case "pageIndex"://命令行号
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
        /** 命令行号 */
        commandLogic.setcLine(hashMap.get("pageIndex"));

        //int i = commandLogicService.insertCommandLogic(commandLogic);

        return commandLogic;
    }

    /**
    * @method: commandLogic  转化为   String
    * @Param: [commandLogic]
    * @return: java.lang.String
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    public String commandLogicString(CommandLogic commandLogic){
        String onlyIndex = commandLogic.getId();
        String trueFalse = "";
        String command = commandLogic.getCommand();
        String resultCheckId =  commandLogic.getResultCheckId();
        String nextIndex;
        if (resultCheckId.equals("0")){
            //resultCheckId.equals("0")  自定义
            nextIndex = commandLogic.getProblemId();
        }else {
            //常规检验 执行下一命令
            nextIndex = commandLogic.getProblemId();
        }

        String pageIndex = commandLogic.getcLine();

        CommandLogicVO commandLogicVO = new CommandLogicVO();
        commandLogicVO.setOnlyIndex(onlyIndex);
        commandLogicVO.setTrueFalse(trueFalse);
        commandLogicVO.setCommand(command);
        commandLogicVO.setResultCheckId(resultCheckId);
        commandLogicVO.setNextIndex(nextIndex);
        commandLogicVO.setPageIndex(pageIndex);

        String commandLogicVOSting = commandLogicVO.toString();
        commandLogicVOSting = commandLogicVOSting.replace("'","\"");
        commandLogicVOSting = commandLogicVOSting.replace(",",",\"");
        commandLogicVOSting = commandLogicVOSting.replace("=","\"=");
        commandLogicVOSting = commandLogicVOSting.replace("{","{\"");
        commandLogicVOSting = commandLogicVOSting.replace("\" ","\"");
        commandLogicVOSting = commandLogicVOSting.replace(" \"","\"");
        commandLogicVOSting = commandLogicVOSting.replace("CommandLogicVO",pageIndex+":");
        return commandLogicVOSting;
    }


    /***
    * @method: 根据 交换机属性 问题 回显问题数据
    * @Param: []
    * @return: java.util.List<java.lang.String>
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("getAnalysisList")
    public List<String> getAnalysisList( String brand, String type,
                                         String firewareVersion, String subVersion,
                                        String problemName){
        TotalQuestionTable totalQuestionTable = new TotalQuestionTable();
        totalQuestionTable.setBrand(brand);
        totalQuestionTable.setType(type);
        totalQuestionTable.setFirewareVersion(firewareVersion);
        totalQuestionTable.setSubVersion(subVersion);
        totalQuestionTable.setProblemName(problemName);
        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);

        if (null == totalQuestionTables || totalQuestionTables.size() ==0 ){
            return null;
        }

        String problemScanLogicID = totalQuestionTables.get(0).getCommandId();
        List<CommandLogic> commandLogicList = new ArrayList<>();
        List<ProblemScanLogic> problemScanLogics = new ArrayList<>();
        do {
            String[] problemScanLogicIDsplit = problemScanLogicID.split(":");
            for (String problemID:problemScanLogicIDsplit){
                CommandLogic commandLogic = commandLogicService.selectCommandLogicById(problemID);
                if (commandLogic == null || commandLogic.getProblemId() == null){
                    return null;
                }
                commandLogicList.add(commandLogic);
                List<ProblemScanLogic> problemScanLogicList = problemScanLogicList(commandLogic.getProblemId());//commandLogic.getProblemId()
                if (null == problemScanLogicList || problemScanLogicList.size() ==0 ){
                    return null;
                }
                problemScanLogicID = "";
                for (ProblemScanLogic problemScanLogic:problemScanLogicList){
                    problemScanLogics.add(problemScanLogic);
                    if (problemScanLogic.gettComId()!=null && problemScanLogic.gettComId()!= ""){
                        problemScanLogicID += problemScanLogic.gettComId()+":";
                    }
                    if (problemScanLogic.getfComId()!=null && problemScanLogic.getfComId()!= ""){
                        problemScanLogicID += problemScanLogic.getfComId()+":";
                    }
                }
                if (problemScanLogicID!=""){
                    break;
                }
            }
        }while (problemScanLogicID.indexOf(":")!=-1);

        HashMap<Long,String> hashMap = new HashMap<>();

        for (CommandLogic commandLogic:commandLogicList){
            String commandLogicString = commandLogicString(commandLogic);
            String[] commandLogicStringsplit = commandLogicString.split(":");
            hashMap.put(Integer.valueOf(commandLogicStringsplit[0]).longValue(),commandLogicStringsplit[1]);
        }
        for (ProblemScanLogic problemScanLogic:problemScanLogics){
            String problemScanLogicString = problemScanLogicSting(problemScanLogic);
            String[] problemScanLogicStringsplit = problemScanLogicString.split(":");
            hashMap.put(Integer.valueOf(problemScanLogicStringsplit[0]).longValue(),problemScanLogicStringsplit[1]);
        }

        List<String> stringList = new ArrayList<>();
        for (Long number=0L;number<hashMap.size();number++){
            stringList.add(hashMap.get(number+1));
        }

        return stringList;
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
                problemScanLogicf.setfProblemId(problemScanLogic.getfProblemId());
                problemScanLogicf.setfComId(problemScanLogic.getfComId());
                problemScanLogic.setfLine(null);
                problemScanLogic.setfNextId(null);
                problemScanLogic.setfProblemId(null);
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