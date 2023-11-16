package com.sgcc.sql.util;

import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.EncryptUtil;
import com.sgcc.sql.domain.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

public class InspectionMethods {
    /**
     * 筛选匹配度高的交换机问题
     * 逻辑 定义一个 map集合 key为 范式分类和范式名称 保证问题的唯一
     * 遍历交换机问题集合 提取范式分类和范式名称  到 map中查询 是否返回实体类
     * 如果返回为空 则可以直接存入 map集合
     * 如果不为空 则需要比较 两个问题那个更加精确  精确的存入Map
     * 如果精确到项一样 则去比较 项的值 哪一个更加精确 例如型号：S2152 和 S*  选择 S2152
     * @param totalQuestionTableList
     * @return
     */
    public static List<TotalQuestionTable> ObtainPreciseEntityClasses(List<TotalQuestionTable> totalQuestionTableList) {
        /*定义返回内容*/
        List<TotalQuestionTable> TotalQuestionTablePojoList = new ArrayList<>();
        /*逻辑 定义一个 map集合 key为 范式分类和范式名称 保证问题的唯一*/
        Map<String,TotalQuestionTable> totalQuestionTableHashMap = new HashMap<>();
        /*遍历交换机问题集合*/
        for (TotalQuestionTable totalQuestionTable:totalQuestionTableList){
            /*提取范式分类和范式名称  到 map中查询 是否返回实体类*/
            String key =totalQuestionTable.getTypeProblem() + totalQuestionTable.getTemProName();
            TotalQuestionTable pojo = totalQuestionTableHashMap.get(key);
            /*如果返回为空 则可以直接存入 map集合*/
            if (pojo != null){
                /*如果不为空 则需要比较 两个问题那个更加精确  精确的存入Map */
                /* 获取 两个交换机问题的 参数数量的精确度 */
                /*map*/
                int usedNumber = 0;
                if (!(pojo.getType().equals("*"))){
                    usedNumber = usedNumber +1;
                }
                if (!(pojo.getFirewareVersion().equals("*"))){
                    usedNumber = usedNumber +1;
                }
                if (!(pojo.getSubVersion().equals("*"))){
                    usedNumber = usedNumber +1;
                }
                /*新*/
                int newNumber = 0;
                if (!(totalQuestionTable.getType().equals("*"))){
                    newNumber = newNumber +1;
                }
                if (!(totalQuestionTable.getFirewareVersion().equals("*"))){
                    newNumber = newNumber +1;
                }
                if (!(totalQuestionTable.getSubVersion().equals("*"))){
                    newNumber = newNumber +1;
                }
                /*对比参数的数量大小
                 * 如果新遍历到的问题 数量大于 map 中的问题 则进行替代 否则 则遍历新的*/
                if (usedNumber < newNumber){
                    /* 新 比 map中的精确*/
                    totalQuestionTableHashMap.put(key,totalQuestionTable);
                }else if (usedNumber == newNumber){
                    /*如果精确到项一样 则去比较 项的值 哪一个更加精确 例如型号：S2152 和 S*  选择 S2152*/

                    String pojotype = pojo.getType();
                    String totalQuestionTabletype = totalQuestionTable.getType();
                    /*比较两个属性的精确度
                     * 返回1 是第一个数属性精确 返回2 是第二个属性更精确
                     * 返回0 则精确性相等 则进行下一步分析*/
                    Integer typeinteger = filterAccurately(pojotype, totalQuestionTabletype);
                    if (typeinteger == 1){
                        totalQuestionTableHashMap.put(key,pojo);
                    }else if (typeinteger == 2){
                        totalQuestionTableHashMap.put(key,totalQuestionTable);
                    }else if (typeinteger == 0){
                        String pojofirewareVersion = pojo.getFirewareVersion();
                        String totalQuestionTablefirewareVersion = totalQuestionTable.getFirewareVersion();
                        /*比较两个属性的精确度*/
                        Integer firewareVersioninteger = filterAccurately(pojofirewareVersion, totalQuestionTablefirewareVersion);
                        if (firewareVersioninteger == 1){
                            totalQuestionTableHashMap.put(key,pojo);
                        }else if (firewareVersioninteger == 2){
                            totalQuestionTableHashMap.put(key,totalQuestionTable);
                        }else if (firewareVersioninteger == 0){
                            String pojosubVersion = pojo.getSubVersion();
                            String totalQuestionTablesubVersion = totalQuestionTable.getSubVersion();
                            /*比较两个属性的精确度*/
                            Integer subVersioninteger = filterAccurately(pojosubVersion, totalQuestionTablesubVersion);
                            if (subVersioninteger == 1){
                                totalQuestionTableHashMap.put(key,pojo);
                            }else if (subVersioninteger == 2){
                                totalQuestionTableHashMap.put(key,totalQuestionTable);
                            }else if (subVersioninteger == 0){
                                /* 如果 都相等 则 四项基本信息完全一致 此时 不应该存在
                                 * 因为 sql 有联合唯一索引  四项基本信息+范式名称+范式分类
                                 * */
                                continue;
                                //totalQuestionTableHashMap.put(key,totalQuestionTable);
                            }
                        }
                    }
                }else  if (usedNumber > newNumber) {
                    /* map 中的更加精确  则 进行下一层遍历*/
                    continue;
                    //totalQuestionTableHashMap.put(key,pojo);
                }
            }else {
                totalQuestionTableHashMap.put(key,totalQuestionTable);
            }
        }

        /*获取 map 的value值 并更存储到集合中 返回*/
        Iterator<Map.Entry< String, TotalQuestionTable >> iterator = totalQuestionTableHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry< String, TotalQuestionTable > entry = iterator.next();
            TotalQuestionTablePojoList.add(entry.getValue());
        }
        return TotalQuestionTablePojoList;

    }

    /**
     * 比较两个属性的精确度
     * @param value1
     * @param value2
     * @return
     */
    public static Integer filterAccurately(String value1,String value2) {
        /*查看是否包含 * */
        boolean value1Boolean = value1.indexOf("*")!=-1;
        boolean value2Boolean = value2.indexOf("*")!=-1;
        /*两项的长度*/
        int value1Length = value1.length();
        int value2Length = value2.length();
        if (value1Boolean && value2Boolean){
            /*如果两个都含有 * 取最长的*/
            if (value1Length<value2Length){
                return 2;
            }else if (value1Length>value2Length){
                return 1;
            }else if (value1Length == value2Length){
                return 0;
            }
        }else {
            /*两个 至少有一个没含有 * */
            if (value1Boolean || value2Boolean){
                /*有一个含有 * 返回 没有*的*/
                if (value1Boolean){
                    return 2;
                }
                if (value2Boolean){
                    return 1;
                }
            }
        }
        return 0;
    }


    /**
     * @method: commandLogic转化为String
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
        String commandLogicVOSting =commandLogicVO.getPageIndex()+"=:="+"{"
                +"\"onlyIndex\"" +"="+ "\""+ commandLogicVO.getOnlyIndex() +"\","
                +"\"trueFalse\"" +"="+ "\""+ commandLogicVO.getTrueFalse() +"\","
                +"\"pageIndex\"" +"="+ "\""+ commandLogicVO.getPageIndex() +"\","
                +"\"command\"" +"="+ "\""+ commandLogicVO.getCommand() +"\","
                +"\"para\"" +"="+ "\""+ commandLogicVO.getPara() +"\","
                +"\"resultCheckId\"" +"="+ "\""+ commandLogicVO.getResultCheckId() +"\","
                +"\"nextIndex\"" +"="+ "\""+ commandLogicVO.getNextIndex() +"\"" +"}";
        return commandLogicVOSting;
    }

    /**
     * @method: 字符串解析 CommandLogic 实体类 并返回
     * @Param: [jsonPojo]
     * @return: com.sgcc.sql.domain.CommandLogic
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public static CommandLogic analysisCommandLogic(@RequestBody String jsonPojo){
        /*第一步：去掉“{”“}”，然后以“，”分割（扫描逻辑中命令是否有带“，”的，会有影响）*/
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
        hashMap.put("para",null);
        hashMap.put("objective",null);
            /*遍历属性数组，以“：”分割为[“属性名”，“属性值”]的数组
                ，使用 属性名 匹配 hashmap中的key值，给key值赋值*/
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
                case "objective":// 命令目的
                    hashMap.put("objective",split1);
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
                case "para"://参数
                    hashMap.put("para",split1);
                    break;
            }
        }

        //如果 常规检验 的话 下一ID  应是 下一命令ID
        //下一分析ID  应是  0
        /*如果为常规校验的话，resultCheckId = 1；则分析数据的下一条ID为下一命令ID。则nextIndex属性值 应赋值给 实体类endIndex字段。*/
        if (hashMap.get("resultCheckId").equals("1")){
            hashMap.put("endIndex",hashMap.get("nextIndex"));
            hashMap.put("nextIndex","0");
        }
        /** 主键索引 */
        commandLogic.setId(hashMap.get("onlyIndex"));
        /** 状态 */
        commandLogic.setState(null);

        /** 命令 */
        if (hashMap.get("para") != null && !(hashMap.get("para").equals(""))){
            hashMap.put("command",hashMap.get("command")+":"+hashMap.get("para"));
        }
        commandLogic.setCommand(hashMap.get("command"));
        /** 返回结果验证id */
        commandLogic.setResultCheckId(hashMap.get("resultCheckId"));
        /** 返回分析id */
        commandLogic.setProblemId(hashMap.get("nextIndex"));
        /** 命令结束索引 */
        commandLogic.setEndIndex(hashMap.get("endIndex"));
        /** 命令行号 */
        commandLogic.setcLine(hashMap.get("pageIndex"));

        /*插入数据库*/
        //int i = commandLogicService.insertCommandLogic(commandLogic);
        return commandLogic;
    }

    /**
     * @method: problemScanLogic   转化  Sting
     * @Param: [problemScanLogic]
     * @return: java.lang.String
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public static String problemScanLogicSting(ProblemScanLogic problemScanLogic,String id){
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
        String relative = "0";
        //按列匹配
        String position = "0";
        problemScanLogicVO.setRelative(relative);
        problemScanLogicVO.setPosition(position);
        problemScanLogicVO.setCursorRegion("0");
        if (problemScanLogic.getMatched()!=null && !(problemScanLogic.getMatched().equals("null"))){

            if (problemScanLogic.getMatched().indexOf("present")!=-1 || problemScanLogic.getMatched().indexOf("full")!=-1){
                matched = problemScanLogic.getMatched().substring(0,4);
            }

        }
        problemScanLogicVO.setMatched(matched);
        /* 如果 Ln Cn 不为 null  和  "null"  则  为 0,0 格式 则 需要分割*/
        if (problemScanLogic.getRelativePosition()!=null && !(problemScanLogic.getRelativePosition().equals("null"))){
            String relativePosition = problemScanLogic.getRelativePosition();
            String[] relativePositionSplit = relativePosition.split(",");
            relative = relativePositionSplit[0];
            // todo
            if (problemScanLogic.getMatched()!=null && problemScanLogic.getMatched().indexOf("present")!=-1){
                relative = relative +"&present";
            }
            if (problemScanLogic.getMatched()!=null && problemScanLogic.getMatched().indexOf("full")!=-1){
                relative = relative +"&full";
            }
            position = relativePositionSplit[1];
        }
        problemScanLogicVO.setRelative(relative);
        problemScanLogicVO.setPosition(position);
        if (problemScanLogic.getMatchContent()!=null){
            String matchContent = problemScanLogic.getMatchContent();
            problemScanLogicVO.setMatchContent(matchContent);
        }
        if (problemScanLogic.getAction()!=null){
            String action = problemScanLogic.getAction();
            if (action.indexOf("full")!=-1){
                problemScanLogicVO.setCursorRegion("1");
                action=action.replace("full","");
                problemScanLogic.setAction(action);
            }
            problemScanLogicVO.setAction(action);
            if (!(problemScanLogic.getRelativePosition().equals("null"))){
                String relativePosition = problemScanLogic.getRelativePosition();
                String[] relativePositionSplit = relativePosition.split(",");
                relative = relativePositionSplit[0];
                position = relativePositionSplit[1];
                problemScanLogicVO.setRelative(relative);
                problemScanLogicVO.setPosition(position);
            }

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
        String problem = null;
        if (problemScanLogic.getProblemId()!=null){
            /* 有问题 无问题*/
            problem = problemScanLogic.getProblemId();
            if (problem.indexOf("问题")!=-1){
                problemScanLogicVO.setAction("问题");
                if(problem.equals("有问题")){
                    problemScanLogicVO.settNextId("异常");
                }else if(problem.equals("无问题")){
                    problemScanLogicVO.settNextId("安全");
                }
            }else if (problem.equals("完成")){
                problemScanLogicVO.setAction("问题");
                problemScanLogicVO.settNextId(problem);
            }else {
                if (id == null){
                    problemScanLogicVO.setAction("问题");
                    problemScanLogicVO.settNextId(problem);
                }else {
                    problemScanLogicVO.setAction("问题");
                    problemScanLogicVO.settNextId(problemScanLogic.getProblemId());
                }
            }
        }
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
        String problemScanLogicVOString = problemScanLogicVO.getPageIndex()+"=:="+"{"
                +"\"onlyIndex\"" +"="+ "\""+ problemScanLogicVO.getOnlyIndex() +"\","
                +"\"trueFalse\"" +"="+ "\""+ problemScanLogicVO.getTrueFalse() +"\","
                +"\"matched\"" +"="+ "\""+ problemScanLogicVO.getMatched() +"\","
                +"\"relative\"" +"="+ "\""+ problemScanLogicVO.getRelative() +"\","
                +"\"position\"" +"="+ "\""+ problemScanLogicVO.getPosition() +"\","
                +"\"cursorRegion\"" +"="+ "\""+ problemScanLogicVO.getCursorRegion() +"\","
                +"\"matchContent\"" +"="+ "\""+ problemScanLogicVO.getMatchContent() +"\","
                +"\"action\"" +"="+ "\""+ problemScanLogicVO.getAction() +"\","
                +"\"tNextId\"" +"="+ "\""+ problemScanLogicVO.gettNextId() +"\","
                +"\"rPosition\"" +"="+ "\""+ problemScanLogicVO.getrPosition() +"\","
                +"\"length\"" +"="+ "\""+ problemScanLogicVO.getLength() +"\","
                +"\"exhibit\"" +"="+ "\""+ problemScanLogicVO.getExhibit() +"\","
                +"\"wordName\"" +"="+ "\""+ problemScanLogicVO.getWordName() +"\","
                +"\"compare\"" +"="+ "\""+ problemScanLogicVO.getCompare() +"\","
                +"\"content\"" +"="+ "\""+ problemScanLogicVO.getContent() +"\","
                +"\"nextIndex\"" +"="+ "\""+ problemScanLogicVO.getNextIndex() +"\","
                +"\"problemId\"" +"="+ "\""+ problemScanLogicVO.getProblemId() +"\","
                +"\"cycleStartId\"" +"="+ "\""+ problemScanLogicVO.getCycleStartId() +"\","
                +"\"pageIndex\"" +"="+ "\""+ problemScanLogicVO.getPageIndex() +"\""+"}";
        return problemScanLogicVOString;
    }

    /**
     * @method: 字符串解析 ProblemScanLogic 实体类 并返回
     * @Param: [jsonPojo, ifCommand : 分析、命令、问题]
     * @return: com.sgcc.sql.domain.ProblemScanLogic
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public static ProblemScanLogic analysisProblemScanLogic(@RequestBody String jsonPojo, String ifCommand){
        ProblemScanLogic problemScanLogic = new ProblemScanLogic();
        HashMap<String,String> hashMap = new HashMap<>();
        /*去掉“{”“}”*/
        jsonPojo = jsonPojo.replace("{","");
        jsonPojo = jsonPojo.replace("}","");
        /*单引号 替换为 双引号。*/
        jsonPojo = jsonPojo.replace("'","\"");
        /*通过替换，使属性名和属性值的结构都变化为---  “key”：“value”。*/
        jsonPojo = jsonPojo.replace("\":","\":\"");//jsonPojo = jsonPojo.replace(":\"","\":\"");
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
        hashMap.put("relative","0");
        /** 相对位置 列*/
        hashMap.put("position","0");
        /** 返回 0 行*/
        hashMap.put("cursorRegion",null); // 0 从当前行往下  1  全文
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
            String[] split = pojo.split("\":");
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
                    } else if (split1.equals("精确匹配")){
                        /** 匹配 */
                        hashMap.put("matched","精确匹配");
                    }else if (split1.equals("模糊匹配")){
                        /** 匹配 */
                        hashMap.put("matched","模糊匹配");
                    }
                    break;
                case "relative":
                    /** 相对位置 行*/
                    //有两种情况  数字  和 词汇
                    hashMap.put("relative",split1);
                    break;
                case "position":
                    /** 相对位置 列*/
                    hashMap.put("position",split1);
                    break;
                case "cursorRegion":
                    /** 相对位置 列*/
                    hashMap.put("cursorRegion",split1);
                    break;
                case "matchContent":
                    /** 匹配内容 */
                    if (split1 != null && split1.equals("null")){
                        hashMap.put("matchContent",null);
                    }else {
                        hashMap.put("matchContent",split1);
                    }
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
            }
        }
        /*精确、模糊 匹配*/
        if (hashMap.get("matched")!=null){
            /** 相对位置 */
            if (hashMap.get("relative").indexOf("&")!=-1){
                /* 位置 : 按行和全文 */
                String[] relatives = hashMap.get("relative").split("&");
                hashMap.put("relativePosition", relatives[0] +","+hashMap.get("position"));
            }else {
                hashMap.put("relativePosition",hashMap.get("relative")+","+hashMap.get("position"));
            }
        }
        /*当 ifCommand 属性值为命令时 则 下一条分析数据为命令
         *如果下一条分析数据为命令时 则 下一条IDtNextId  要赋值给 命令ID
         * 然后下一条ID tNextId 置空 null  */
        if (ifCommand.equals("命令")){
            /** true下一条命令索引 */
            hashMap.put("tComId",hashMap.get("tNextId"));
            hashMap.put("tNextId",null);
        }
        /*
         * 默认情况下 行号、下一条分析ID、下一条命令ID 是 成功对应的属性
         *
         * 如果 trueFalse 为 失败时
         * 则 成功行号、成功下一条分析、成功下一条命令 都复制给 失败对应 属性
         */
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
            //hashMap.get("WTNextId") 存放有无问题
            hashMap.put("problemId",hashMap.get("WTNextId"));
            //清空动作属性
            hashMap.put("action",null);
        }
        /** 主键索引 */
        problemScanLogic.setId(hashMap.get("id"));
        /** 匹配 */
        if (hashMap.get("matched")!=null){
            /*精确匹配*/
            /*如果 relative  */
            if (hashMap.get("relative").indexOf("&")!=-1){
                /* 位置 ：按行和全文*/
                String[] relatives = hashMap.get("relative").split("&");
                problemScanLogic.setMatched(hashMap.get("matched") + relatives[1]);
            }else {
                problemScanLogic.setMatched(hashMap.get("matched"));
            }
            // 如果 匹配为 “null” 则  置空设为null
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
            if (hashMap.get("action").equals("取词") && hashMap.get("cursorRegion")!=null && hashMap.get("matched") ==null){
                problemScanLogic.setAction(hashMap.get("action") + (hashMap.get("cursorRegion").equals("1")?"full":""));
            }
            if (problemScanLogic.getAction().equals("null")){
                problemScanLogic.setAction(null);
            }
        }
        /*如果取词 添加 按行直接取词 跳动光标*/
        if (problemScanLogic.getAction() != null){
            /** 相对位置 */
            hashMap.put("relativePosition",hashMap.get("relative")+","+hashMap.get("position"));
            problemScanLogic.setRelativePosition(hashMap.get("relativePosition"));
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
        /*问题*/
        if (hashMap.get("problemId")!=null){
            problemScanLogic.setProblemId(hashMap.get("problemId"));
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
        return problemScanLogic;
    }

    /**
     * ProblemScanLogic实体类  拆分  true  false
     * @Description  当错误行号不为空时 则 错误信息取出 放入 一个新的实体类 然后放入 需要返回的实体类集合 并把原实体类错误信息清空
     * @author charles
     * @createTime 2023/11/3 13:13
     * @desc
     * @param ProblemScanLogicList
     * @return
     */
    public static  List<ProblemScanLogic> splitSuccessFailureLogic(List<ProblemScanLogic> ProblemScanLogicList) {
        /* 定义需要返回的实体类*/
        List<ProblemScanLogic> ProblemScanLogics = new ArrayList<>();
        /*当错误行号不为空时 则 错误信息取出 放入 一个新的实体类 然后放入 需要返回的实体类集合 并把原实体类错误信息清空*/
        for (ProblemScanLogic problemScanLogic:ProblemScanLogicList){
            if (problemScanLogic.getfLine()!=null){
                ProblemScanLogic problemScanLogicf = new ProblemScanLogic();
                problemScanLogicf.setId(problemScanLogic.getId());
                problemScanLogicf.setMatched(problemScanLogic.getMatched());
                problemScanLogicf.setRelativePosition(problemScanLogic.getRelativePosition());
                problemScanLogicf.setfLine(problemScanLogic.getfLine());
                problemScanLogicf.setfNextId(problemScanLogic.getfNextId());
                problemScanLogicf.setLength(problemScanLogic.getLength());
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
     * @Description 将相同ID  时间戳 的 实体类 放到一个实体
     * @author charles
     * @createTime 2023/10/12 14:30
     * @desc
     * @param pojoList
     * @return
     */
    public static List<ProblemScanLogic> definitionProblem(List<ProblemScanLogic> pojoList){
        //根据 set 特性 获取分析ID、不重复
        HashSet<String> hashSet = new HashSet<>();
        for (ProblemScanLogic problemScanLogic:pojoList){
            hashSet.add(problemScanLogic.getId());
        }
        //创建ProblemScanLogic 集合 放入获取分析ID 作为返回的 实体类集合
        Map<String,ProblemScanLogic> pojoMap = new HashMap<>();
        for (String problemScanLogicId:hashSet){
            ProblemScanLogic problem_scanLogic = new ProblemScanLogic();
            problem_scanLogic.setId(problemScanLogicId);
            pojoMap.put(problemScanLogicId,problem_scanLogic);
        }
        for (ProblemScanLogic pojo:pojoList){
            String pojoId = pojo.getId();
            ProblemScanLogic problemScanLogic = pojoMap.get(pojoId);
            //当 两个实体类的 分析ID 相等时 由前端返回的集合 赋值给 返回实体类
            String id = pojo.getId();
            if (id != null) {
                problemScanLogic.setId(id);
            }
            String matched = pojo.getMatched();
            if (matched != null){
                problemScanLogic.setMatched(matched);
            }
            String relativePosition = pojo.getRelativePosition();
            String matchContent = pojo.getMatchContent();
            /* 2023.10.12 前端数据传入 出现问题 false数据与 true数据不一致*/
            if (relativePosition != null && matchContent != null) {
                problemScanLogic.setRelativePosition(relativePosition);
            }
            if (matchContent != null) {
                problemScanLogic.setMatchContent(matchContent);
            }
            String action = pojo.getAction();
            if (action != null) {
                problemScanLogic.setAction(action);
            }
            Integer rPosition = pojo.getrPosition();
            if (rPosition != null) {
                problemScanLogic.setrPosition(rPosition);
            }
            String length = pojo.getLength();
            if (length != null) {
                problemScanLogic.setLength(length);
            }
            String exhibit = pojo.getExhibit();
            if (exhibit != null) {
                problemScanLogic.setExhibit(exhibit);
            }
            String wordName = pojo.getWordName();
            if (wordName != null) {
                problemScanLogic.setWordName(wordName);
            }
            String compare = pojo.getCompare();
            if (compare != null) {
                problemScanLogic.setCompare(compare);
            }
            String tNextId = pojo.gettNextId();
            if (tNextId != null) {
                problemScanLogic.settNextId(tNextId);
            }
            String tComId = pojo.gettComId();
            if (tComId != null) {
                problemScanLogic.settComId(tComId);
            }
            String problemId = pojo.getProblemId();
            if (problemId != null) {
                problemScanLogic.setProblemId(problemId);
            }
            String fLine = pojo.getfLine();
            if (fLine != null) {
                problemScanLogic.setfLine(fLine);
            }
            String tLine = pojo.gettLine();
            if (tLine != null) {
                problemScanLogic.settLine(tLine);
            }
            String fNextId = pojo.getfNextId();
            if (fNextId != null) {
                problemScanLogic.setfNextId(fNextId);
            }
            String fComId = pojo.getfComId();
            if (fComId != null) {
                problemScanLogic.setfComId(fComId);
            }
            Long returnCmdId = pojo.getReturnCmdId();
            if (returnCmdId != null) {
                problemScanLogic.setReturnCmdId(returnCmdId);
            }
            String cycleStartId = pojo.getCycleStartId();
            if (cycleStartId != null) {
                problemScanLogic.setCycleStartId(cycleStartId);
            }
            pojoMap.put(pojoId,problemScanLogic);

        }
        List<ProblemScanLogic> problemScanLogicList = new ArrayList<>();
        Iterator it = pojoMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry =(Map.Entry) it.next();
            ProblemScanLogic problemScanLogic = (ProblemScanLogic) entry.getValue();
            problemScanLogicList.add(problemScanLogic);
        }
        return problemScanLogicList;
    }

    /**
     * 交换机登录信息提取
     */
    public static SwitchParameters getUserMap(String userinformation) {
        /*不用 JSON.parseObject 方法 是因为 port 字段类型不匹配。前端传入 String，实体类 Integer */
        //用户信息
        String userInformationString = userinformation;
        userInformationString = userInformationString.replace("{","");
        userInformationString = userInformationString.replace("}","");
        userInformationString = userInformationString.replace("\"","");
        String[] userinformationSplit = userInformationString.split(",");
        SwitchParameters switchParameters = new SwitchParameters();
        for (String userString:userinformationSplit){
            String[] userStringsplit = userString.split(":");
            String key = userStringsplit[0];
            String value = userStringsplit[1];
            switch (key.trim()){
                case "mode":
                    //登录方式
                    switchParameters.setMode(value);
                    break;
                case "ip":
                    //ip地址
                    switchParameters.setIp(value);
                    break;
                case "name":
                    //用户名
                    switchParameters.setName(value);
                    break;
                case "password":
                    //密码
                    switchParameters.setPassword(EncryptUtil.desaltingAndDecryption(value));
                    break;
                case "configureCiphers":
                    //密码
                    switchParameters.setConfigureCiphers(EncryptUtil.desaltingAndDecryption(value));
                    break;
                case "port":
                    //端口号
                    switchParameters.setPort(Integer.valueOf(value).intValue());
                    break;
            }
        }
        return switchParameters;
    }
}
