package com.sgcc.web.controller.sql;

import com.sgcc.common.annotation.Log;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.ICommandLogicService;
import com.sgcc.sql.service.IProblemScanLogicService;
import com.sgcc.sql.service.ITotalQuestionTableService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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


    /*=====================================================================================================================
    =====================================================================================================================
    =====================================================================================================================*/

    /**
     * @method: 插入分析问题的数据
     * @Param: [jsonPojoList]
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("definitionProblemJsonPojo")
    //@Log(title = "定义分析", businessType = BusinessType.OTHER)
    public boolean definitionProblemJsonPojo(@RequestBody List<String> jsonPojoList){//@RequestBody List<String> jsonPojoList

        String problemScanLogicString = "";
        for (String pojo:jsonPojoList){
            problemScanLogicString = problemScanLogicString + pojo +",";
        }
        if (!problemScanLogicString.equals("")){
            jsonPojoList = new ArrayList<>();
            problemScanLogicString = problemScanLogicString.substring(0,problemScanLogicString.length()-1);
            problemScanLogicString = problemScanLogicString.replace("},","}\r\n");
            String[] commandLogic_split = problemScanLogicString.split("\r\n");
            for (int number=0; number<commandLogic_split.length; number++){
                jsonPojoList.add(commandLogic_split[number]);
            }
        }else {
            return false;
        }

        System.err.println("\r\n"+"前端出入数据：\r\n");
        for (String jsonPojo:jsonPojoList){
            System.err.println(jsonPojo);
        }

        List<CommandLogic> commandLogicList = new ArrayList<>();
        List<ProblemScanLogic> problemScanLogicList = new ArrayList<>();
        for (int number=0;number<jsonPojoList.size();number++){
            // 如果 前端传输字符串  存在 command  说明 是命令
            if (jsonPojoList.get(number).indexOf("command")!=-1){
                CommandLogic commandLogic = analysisCommandLogic(jsonPojoList.get(number));
                commandLogicList.add(commandLogic);
                continue;
            }else if (!(jsonPojoList.get(number).indexOf("command") !=-1)){

                if (number+1<jsonPojoList.size()){
                    // 判断下一条是否是命令  因为 如果下一条是命令 则要 将 下一条分析ID 放入 命令ID
                    if (jsonPojoList.get(number+1).indexOf("command") !=-1){
                        //本条是分析 下一条是 命令
                        ProblemScanLogic problemScanLogic = analysisProblemScanLogic(jsonPojoList.get(number), "命令");
                        problemScanLogicList.add(problemScanLogic);
                        continue;
                    }else {
                        //本条是分析 下一条是 分析
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
        //将相同ID  时间戳 的 实体类 放到一个实体
        List<ProblemScanLogic> problemScanLogics = definitionProblem(problemScanLogicList);
        String totalQuestionTableById = null;
        String commandId = null;
        for (ProblemScanLogic problemScanLogic:problemScanLogics){
            //提取 问题ID
            if (problemScanLogic.getProblemId()!=null &&problemScanLogic.getProblemId().indexOf("问题")!=-1){
                totalQuestionTableById = problemScanLogic.getProblemId().substring(3,problemScanLogic.getProblemId().length());
            }
            int i = problemScanLogicService.insertProblemScanLogic(problemScanLogic);
            if (i<=0){
                return false;
            }
        }
        for (CommandLogic commandLogic:commandLogicList){
            if (commandLogic.getcLine().equals("1")){
                commandId = commandLogic.getId();
            }
            int i = commandLogicService.insertCommandLogic(commandLogic);
            if (i<=0){
                return false;
            }
        }
        if(totalQuestionTableById!=null){
            TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(Integer.valueOf(totalQuestionTableById).longValue());
            totalQuestionTable.setCommandId(commandId);
            int i = totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable);
            if (i<=0){
                return false;
            }
        }
        return true;
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
        jsonPojo = jsonPojo.replace("'","\"");
        jsonPojo = jsonPojo.replace("\":","\":\"");
        jsonPojo = jsonPojo.replace(":\"","\":\"");
        jsonPojo = jsonPojo.replace("\",","\",\"");
        jsonPojo = jsonPojo.replace(",\"","\",\"");
        jsonPojo = jsonPojo.replace("\"\"","\"");
        jsonPojo = jsonPojo.replace("\"\"","\"");
        String[]  jsonPojo_split = jsonPojo.split("\""+","+"\"");

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
        /** 问题索引 */
        hashMap.put("problemId",null);
        /** false行号 */
        hashMap.put("fLine",null);
        /** true行号 */
        hashMap.put("tLine",null);
        /** false下一条分析索引 */
        hashMap.put("fNextId",null);
        /** false下一条命令索引 */
        hashMap.put("fComId",null);
        /** 返回命令 */
        hashMap.put("returnCmdId",null);
        /** 循环起始ID */
        hashMap.put("cycleStartId",null);
        /** 成功失败 */
        hashMap.put("trueFalse",null);
        /** 存放有无问题 */
        hashMap.put("WTNextId",null);

        for (String pojo:jsonPojo_split){
            String[] split = pojo.split(":");
            String split0 = split[0].replace("\"","");
            String split1 = null;
            if (split.length>1){
                split1 = split[1].replace("\"","");
            }
            switch (split0){
                case "onlyIndex"://本层ID 主键ID
                    hashMap.put("id",split1);
                    break;
                case "matched":// 匹配
                    if (split1.equals("null")){
                        /** 匹配 */
                        hashMap.put("matched",null);
                    } else if (split1.equals("全文精确匹配")){
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
                    /** 比较1 */
                    hashMap.put("compare",split1);
                    break;
                case "content":
                    /** 内容 */
                    hashMap.put("content",split1);
                    break;
                case "tNextId":
                    //存放有无问题
                    hashMap.put("WTNextId",split1);
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
                case "problemId":
                    /** 问题索引 */
                    hashMap.put("problemId",split1);
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
        /*if (hashMap.get("action")!=null && hashMap.get("action").equals("取词")){
            List<ProblemScanLogic> resultList=(List<ProblemScanLogic>)redisTemplate.opsForList().leftPop("problemScanLogic");
            redisTemplate.opsForList().leftPush("problemScanLogic",resultList);
            for (ProblemScanLogic pojo:resultList){
                if ((pojo.gettNextId()!=null && pojo.gettNextId().equals(hashMap.get("id")))
                        ||(pojo.getfNextId()!=null && pojo.getfNextId().equals(hashMap.get("id")))){
                    hashMap.put("matchContent",pojo.getMatchContent());
                    break;
                }
            }
        }*/
        if (hashMap.get("trueFalse")!=null && hashMap.get("trueFalse").equals("失败")){
            //如果实体类是 失败 则 把默认成功数据 赋值给 失败数据
            /** false行号 */hashMap.put("fLine",hashMap.get("tLine"));
            /** false下一条分析索引 */hashMap.put("fNextId",hashMap.get("tNextId"));
            /** false下一条命令索引 */hashMap.put("fComId",hashMap.get("tComId"));
            //把 默认成功数据 清除
            /** true下一条分析索引 */hashMap.put("tNextId",null);
            /** true下一条命令索引 */hashMap.put("tComId",null);
            /** true行号 */hashMap.put("tLine",null);
        }
        //如果动作属性不为空  且动作属性参数为 循环时  需要清空动作属性
        if (hashMap.get("action")!=null && hashMap.get("action").equals("循环")){
            //需要清空动作属性
            hashMap.put("action",null);
            hashMap.put("tNextId",null);
        }
        //如果动作属性不为空  且动作属性参数为 比较时  需要清空动作属性
        if (hashMap.get("action")!=null && hashMap.get("action").equals("比较")){
            //清空动作属性
            hashMap.put("action",null);
        }
        //如果动作属性不为空  且动作属性参数为 有无问题时  需要清空动作属性
        if (hashMap.get("action")!=null && hashMap.get("action").indexOf("问题")!=-1){
            //problemId字段 存放 有无问题 加 问题表数据ID
            hashMap.put("problemId",hashMap.get("WTNextId")+hashMap.get("problemId"));
            //清空动作属性
            hashMap.put("action",null);
            //当 有无问题为完成时  则  problemId ==  完成
            if(hashMap.get("WTNextId").equals("完成")){
                hashMap.put("problemId",hashMap.get("WTNextId"));
            }
        }

        /** 主键索引 */
        problemScanLogic.setId(hashMap.get("id"));
        /** 匹配 */
        if (hashMap.get("matched")!=null){
            problemScanLogic.setMatched(hashMap.get("matched").substring(2,hashMap.get("matched").length()));
            // 如果 匹配为 “null” 则  设为null
            if (problemScanLogic.getMatched().equals("null")){
                problemScanLogic.setMatched(null);
            }
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
            if (problemScanLogic.getAction().equals("null")){
                problemScanLogic.setAction(null);
            }
        }
        if (hashMap.get("rPosition")!=null && !(hashMap.get("rPosition").equals("null"))){
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
            if (problemScanLogic.getWordName().equals("null")){
                problemScanLogic.setWordName(null);
            }
        }
        /** 比较 */
        if (hashMap.get("compare")!=null){
            if(hashMap.get("compare").equals("null")){
                problemScanLogic.setCompare(null);
            }else {
                problemScanLogic.setCompare(hashMap.get("compare"));
            }
        }
        /** true下一条分析索引 */
        if (hashMap.get("tNextId")!=null){
            problemScanLogic.settNextId(hashMap.get("tNextId"));
            if (problemScanLogic.gettNextId().equals("null")){
                problemScanLogic.settNextId(null);
            }
        }
        /** true下一条命令索引 */
        if (hashMap.get("tComId")!=null){
            problemScanLogic.settComId(hashMap.get("tComId"));
        }
        /** true问题索引 */
        if (hashMap.get("problemId")!=null){
            problemScanLogic.setProblemId(hashMap.get("problemId"));
            if (problemScanLogic.getProblemId().equals("null")){
                problemScanLogic.setProblemId(null);
            }
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
        if (hashMap.get("returnCmdId")!=null && !(hashMap.get("returnCmdId").equals("null"))){
            /** 返回命令 */
            problemScanLogic.setReturnCmdId(Integer.valueOf(hashMap.get("returnCmdId")).longValue());
        }
        if (hashMap.get("cycleStartId")!=null){
            /** 循环起始ID */
            problemScanLogic.setCycleStartId(hashMap.get("cycleStartId"));
            if (problemScanLogic.getCycleStartId().equals("null")){
                problemScanLogic.setCycleStartId(null);
            }
        }
        //通过redisTemplate设置值
        /*List<ProblemScanLogic> resultList=(List<ProblemScanLogic>)redisTemplate.opsForList().leftPop("problemScanLogic");
        resultList.add(problemScanLogic);
        redisTemplate.opsForList().leftPush("problemScanLogic",resultList);*/
        return problemScanLogic;
    }

    /**
     * @method: 将相同ID  时间戳 的 实体类 放到一个实体
     * @Param: [pojoList]
     * @return: java.util.List<com.sgcc.sql.domain.ProblemScanLogic>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public List<ProblemScanLogic> definitionProblem(List<ProblemScanLogic> pojoList){
        //根据 set 特性 获取分析ID、不重复
        HashSet<String> hashSet = new HashSet<>();
        for (ProblemScanLogic problemScanLogic:pojoList){
            hashSet.add(problemScanLogic.getId());
        }
        //创建ProblemScanLogic 集合 放入获取分析ID 作为返回的 实体类集合
        List<ProblemScanLogic> problemScanLogicList = new ArrayList<>();
        for (String problemScanLogicId:hashSet){
            ProblemScanLogic problemScanLogic = new ProblemScanLogic();
            problemScanLogic.setId(problemScanLogicId);
            problemScanLogicList.add(problemScanLogic);
        }
        //遍历前端返回的集合
        for (ProblemScanLogic pojo:pojoList){
            //遍历 分析ID 不重复的 集合
            for (ProblemScanLogic problemScanLogic:problemScanLogicList){
                //当 两个实体类的 分析ID 相等时 由前端返回的集合 赋值给 返回实体类
                if (pojo.getId().equals(problemScanLogic.getId())){
                    //pojo.getfLine()==null  时  前端返回的实体类 是成功或是不分成功失败的数据
                    if (pojo.getfLine()==null){
                        BeanUtils.copyProperties(pojo,problemScanLogic);
                        problemScanLogic.setMatched(pojo.getMatched()!=null?pojo.getMatched():null);
                    }else {
                        problemScanLogic.setfLine(pojo.getfLine());
                        problemScanLogic.setfNextId(pojo.getfNextId());
                        problemScanLogic.setfComId(pojo.getfComId());
                        problemScanLogic.setProblemId(pojo.getProblemId());
                    }
                }
            }
        }

        //根据 gettLine  排序
        /*ProblemScanLogic[] problemScanLogics = new ProblemScanLogic[problemScanLogicList.size()];
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
        }*/

        return problemScanLogicList;
    }

    /*定义分析问题数据回显*/
    /***
     * @method: 根据 交换机属性 问题 回显问题数据
     * @Param: []
     * @return: java.util.List<java.lang.String>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("getAnalysisList")
    public List<String> getAnalysisList(@RequestBody TotalQuestionTable totalQuestionTable){

        //给 问题表数据 赋值
        TotalQuestionTable pojo = new TotalQuestionTable();
        pojo.setBrand(totalQuestionTable.getBrand());
        pojo.setType(totalQuestionTable.getType());
        pojo.setFirewareVersion(totalQuestionTable.getFirewareVersion());
        pojo.setSubVersion(totalQuestionTable.getSubVersion());
        pojo.setProblemName(totalQuestionTable.getProblemName());

        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableList(pojo);

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
                //根据第一个分析ID 查询出所有的数据条数
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
            System.err.println(hashMap.get(number+1));
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
                if (problemScanLogic.getCycleStartId()!=null && !(problemScanLogic.getCycleStartId().equals("null"))){
                    continue;
                }
                if (problemScanLogic.getProblemId()!=null && (problemScanLogic.getProblemId().equals("完成"))){
                    continue;
                }
                if (problemScanLogic.gettNextId()!=null && !(problemScanLogic.gettNextId().equals("null"))
                        && problemScanLogic.gettNextId()!="" &&  !(isContainChinese(problemScanLogic.gettNextId()))){
                    problemScanID += problemScanLogic.gettNextId()+":";
                }
                if (problemScanLogic.getfNextId()!=null && !(problemScanLogic.getfNextId().equals("null"))
                        && problemScanLogic.getfNextId()!="" &&  !(isContainChinese(problemScanLogic.getfNextId()))){
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

    /**
     * @method: commandLogic  转化为   String
     * @Param: [commandLogic]
     * @return: java.lang.String
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public static String commandLogicString(CommandLogic commandLogic){
        String onlyIndex = commandLogic.getId();
        String trueFalse = "";
        String command = commandLogic.getCommand();
        String para = "";
        if (command.indexOf(":")!=-1){
            String[] command_split = command.split(":");
            command = command_split[0];
            para = command_split[1];
        }
        String resultCheckId =  commandLogic.getResultCheckId();
        String nextIndex;
        if (resultCheckId.equals("0")){
            //resultCheckId.equals("0")  自定义
            nextIndex = commandLogic.getProblemId();
        }else {
            //常规检验 执行下一命令
            nextIndex = commandLogic.getEndIndex();
        }

        String pageIndex = commandLogic.getcLine();

        CommandLogicVO commandLogicVO = new CommandLogicVO();
        commandLogicVO.setOnlyIndex(onlyIndex);
        commandLogicVO.setTrueFalse(trueFalse);
        commandLogicVO.setCommand(command);
        commandLogicVO.setPara(para);
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

    /**
     * @method: problemScanLogic   转化  Sting
     * @Param: [problemScanLogic]
     * @return: java.lang.String
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public String problemScanLogicSting(ProblemScanLogic problemScanLogic){
        //定义一个 符合 前端字符串的 实体类
        ProblemScanLogicVO problemScanLogicVO = new ProblemScanLogicVO();
        //成功 && 失败
        problemScanLogicVO.setTrueFalse("");
        //本行ID
        String onlyIndex = problemScanLogic.getId();
        problemScanLogicVO.setOnlyIndex(onlyIndex);
        //匹配
        String matched = problemScanLogic.getMatched();
        //按行匹配
        String relative = null;
        //按列匹配
        String position = null;
        if (problemScanLogic.getMatched()!=null && !(problemScanLogic.getMatched().equals("null"))){
            //匹配 不为 null 且 不为“null” 则
            if (problemScanLogic.getMatched().indexOf("匹配")!=-1 && problemScanLogic.getRelativePosition().equals("null")){
                matched = "全文"+problemScanLogic.getMatched();
            }else if (problemScanLogic.getMatched().indexOf("匹配")!=-1){
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
            problemScanLogicVO.setAction("比较");
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
        if (problemScanLogic.getProblemId()!=null){
            problemId = problemScanLogic.getProblemId();
            if (problemId.indexOf("问题")!=-1){
                problemScanLogicVO.setAction("问题");
                if(problemId.substring(0,3).equals("有问题")){
                    problemScanLogicVO.settNextId("异常");
                }else if(problemId.substring(0,3).equals("无问题")){
                    problemScanLogicVO.settNextId("安全");
                }
                problemId = problemId.substring(3,problemId.length());
            }
            if (problemId.equals("完成")){
                problemScanLogicVO.setAction("问题");
                problemScanLogicVO.settNextId(problemId);
                problemId = null;
            }
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
        // 当循环ID属性的参数不为空的时候  分析数据为 循环数据 则  动作属性的参数 赋值为  “循环”
        if (problemScanLogicVO.getCycleStartId()!=null && !(problemScanLogicVO.getCycleStartId().equals("null"))){
            //动作属性的参数 赋值为  “循环”
            problemScanLogicVO.setAction("循环");
        }

        String problemScanLogicVOString = problemScanLogicVO.toString();
        problemScanLogicVOString = problemScanLogicVOString.replace("'","\"");
        problemScanLogicVOString = problemScanLogicVOString.replace(",",",\"");
        //  将 "="改为"\"=" 会对 比较中的  <=  >=  ==  !=  有影响
        problemScanLogicVOString = problemScanLogicVOString.replace("=","\"=");
        //消除上一步的影响
        problemScanLogicVOString = problemScanLogicVOString.replace("<\"=","<=");
        problemScanLogicVOString = problemScanLogicVOString.replace(">\"=",">=");
        problemScanLogicVOString = problemScanLogicVOString.replace("!\"=","!=");
        problemScanLogicVOString = problemScanLogicVOString.replace("\"=\"=","==");

        problemScanLogicVOString = problemScanLogicVOString.replace("{","{\"");
        problemScanLogicVOString = problemScanLogicVOString.replace("\" ","\"");
        problemScanLogicVOString = problemScanLogicVOString.replace(" \"","\"");
        problemScanLogicVOString = problemScanLogicVOString.replace("ProblemScanLogicVO",pageIndex+":");

        return problemScanLogicVOString;
    }

    /*定义分析问题数据修改*/
    @RequestMapping("updateAnalysis")
    public boolean updateAnalysis(@RequestParam Long totalQuestionTableId,@RequestBody List<String> jsonPojoList){

        String problemScanLogicString = "";
        for (String pojo:jsonPojoList){
            problemScanLogicString = problemScanLogicString + pojo +",";
        }
        if (!problemScanLogicString.equals("")){
            jsonPojoList = new ArrayList<>();
            problemScanLogicString = problemScanLogicString.substring(0,problemScanLogicString.length()-1);
            problemScanLogicString = problemScanLogicString.replace("},","}\r\n");
            String[] commandLogic_split = problemScanLogicString.split("\r\n");
            for (int number=0; number<commandLogic_split.length; number++){
                jsonPojoList.add(commandLogic_split[number]);
            }
        }else {
            return false;
        }

        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(totalQuestionTableId);
        String commandId = totalQuestionTable.getCommandId();

        CommandLogic commandLogic = commandLogicService.selectCommandLogicById(commandId);
        String problemId = commandLogic.getProblemId();
        if (problemId==null || problemId.equals("")){
            int i = commandLogicService.deleteCommandLogicById(commandId);
            if (i<=0){
                return false;
            }
        }else {
            int i = commandLogicService.deleteCommandLogicById(commandId);
            if (i<=0){
                return false;
            }
            boolean b = deleteProblemScanLogicList(problemId);
            if (!b){
                return false;
            }
        }
        boolean b = definitionProblemJsonPojo(jsonPojoList);//jsonPojoList
        if (!b){
            return false;
        }
        return true;
    }


    public boolean deleteProblemScanLogicList(String problemScanId){
        List<ProblemScanLogic> problemScanLogicList = problemScanLogicList(problemScanId);
        HashSet<String> problemScanLogicIdList = new HashSet<>();
        for (ProblemScanLogic problemScanLogic:problemScanLogicList){
            problemScanLogicIdList.add(problemScanLogic.getId());
        }
        String[] problemScanLogicIdArray = new String[problemScanLogicIdList.size()];
        int i = 0 ;
        for (String problemScanLogicId:problemScanLogicIdList){
            problemScanLogicIdArray[i] = problemScanLogicId;
            i++;
        }
        int j = problemScanLogicService.deleteProblemScanLogicByIds(problemScanLogicIdArray);
        if (j>0){
            return true;
        }else {
            return false;
        }
    }


}