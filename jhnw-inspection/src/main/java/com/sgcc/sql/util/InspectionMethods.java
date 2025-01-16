package com.sgcc.sql.util;

import com.alibaba.fastjson.JSON;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.EncryptUtil;
import com.sgcc.share.util.MyUtils;
import com.sgcc.sql.domain.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

public class InspectionMethods {

    /*Inspection Completed*/
    /**
     * 筛选匹配度高的交换机问题
     *
     * <p>
     * 该方法通过遍历交换机问题集合，对每个问题进行筛选，确保筛选出的每个问题都是该分类和名称下最精确的一个。
     * 方法逻辑概述如下：
     * 1. 定义一个HashMap集合，键为范式分类和范式名称的组合，保证每个问题的唯一性。
     *
     * @param totalQuestionTableList 交换机问题集合
     * @return 筛选后的交换机问题列表
     */
    public static List<TotalQuestionTable> ObtainPreciseEntityClasses(List<TotalQuestionTable> totalQuestionTableList) {
        /*逻辑 定义一个 map集合 key为 范式分类和范式名称 保证问题的唯一*/
        Map<String,TotalQuestionTable> totalQuestionTableHashMap = new HashMap<>();
        /*遍历交换机问题集合*/
        for (TotalQuestionTable totalQuestionTable:totalQuestionTableList){
            // 调用comparisonConditions方法，传入HashMap和问题实体进行比较
            comparisonConditions(totalQuestionTableHashMap,totalQuestionTable);
        }
        /*定义返回格式*/
        List<TotalQuestionTable> TotalQuestionTablePojoList = new ArrayList<>();
        /*获取 map 的value值 并更存储到集合中 返回*/
        Iterator<Map.Entry< String, TotalQuestionTable >> iterator = totalQuestionTableHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry< String, TotalQuestionTable > entry = iterator.next();
            // 将HashMap中的每个值（即问题实体）添加到返回列表中
            TotalQuestionTablePojoList.add(entry.getValue());
        }
        return TotalQuestionTablePojoList;
    }


    /**
     * 根据给定的交换机问题表（TotalQuestionTable）和已存在的交换机问题表HashMap，更新HashMap中的记录。
     *
     * @param totalQuestionTableHashMap 已存在的交换机问题表HashMap，键为范式分类和范式名称的组合，值为TotalQuestionTable对象。
     * @param totalQuestionTable        新的交换机问题表对象，用于与HashMap中的对象进行比较和更新。
     *
     * 此方法首先根据给定TotalQuestionTable对象的范式分类和范式名称生成一个唯一键，
     * 然后在HashMap中查找是否存在该键对应的记录。
     *
     * 如果HashMap中不存在该键的记录，则直接将新的TotalQuestionTable对象添加到HashMap中。
     *
     * 如果HashMap中存在该键的记录，则根据以下规则进行比较和更新：
     * 1. 比较两个TotalQuestionTable对象中四项基本信息（类型、固件版本、子版本等）中"*"的数量，
     *    数量越少表示越精确。如果新对象的"*"数量少于HashMap中对象的"*"数量，则更新HashMap。
     * 2. 如果两个对象的"*"数量相同，则依次比较类型、固件版本、子版本等属性的精确度。
     *    使用filterAccurately方法比较两个字符串的精确度，该方法返回1表示第一个字符串更精确，
     *    返回2表示第二个字符串更精确，返回0表示两者精确度相同。
     * 3. 如果所有比较的属性精确度都相同，则表明两个TotalQuestionTable对象完全相同，
     *    理论上不应该出现这种情况（因为数据库中有联合唯一索引）。
     *    如果确实出现，则直接结束方法执行。
     */
    public static void comparisonConditions(Map<String,TotalQuestionTable> totalQuestionTableHashMap,
                                            TotalQuestionTable totalQuestionTable) {
        // 提取范式分类和范式名称  到 map中查询 是否返回实体类
        String key = totalQuestionTable.getTypeProblem() + totalQuestionTable.getTemProName();
        // 查询map集合中 key为 范式分类+范式名称 的问题数据
        TotalQuestionTable pojo = totalQuestionTableHashMap.get(key);

        // 如果返回为空 则可以直接存入 map集合
        if (pojo != null){

            // 如果不为空 则需要比较 两个问题那个更加精确  精确的存入Map

            // 获取 两个交换机问题数据 四项基本信息中 * 的数量
            // pojo为现存map集合中的问题数据
            int usedNumber = 0;
            if (pojo.getType().indexOf("*")!=-1){
                usedNumber = usedNumber +1;
            }
            if (pojo.getFirewareVersion().indexOf("*")!=-1){
                usedNumber = usedNumber +1;
            }
            if (pojo.getSubVersion().indexOf("*")!=-1){
                usedNumber = usedNumber +1;
            }

            // 新
            int newNumber = 0;
            if (totalQuestionTable.getType().indexOf("*")!=-1){
                newNumber = newNumber +1;
            }
            if (totalQuestionTable.getFirewareVersion().indexOf("*")!=-1){
                newNumber = newNumber +1;
            }
            if (totalQuestionTable.getSubVersion().indexOf("*")!=-1){
                newNumber = newNumber +1;
            }

            // 对比参数的数量大小
            // 因为 包含 * 的 +1 所以 数值越小的 越精确
            // 如果新遍历到的问题 数量大于 map 中的问题 则进行替代 否则 则遍历新的
            if (usedNumber > newNumber){
                // 新 比 map中的精确
                totalQuestionTableHashMap.put(key,totalQuestionTable);

            }else  if (usedNumber < newNumber) {
                // map 中的更加精确  则 进行下一层遍历
                // 如果条件不满足，直接结束方法 代码段提出  结束需要通过 return 跳出方法 不再执行后续代码
                return;
            }else if (usedNumber == newNumber){

                // 如果精确到项一样 则去比较 项的值 哪一个更加精确 例如型号：S2152 和 S*  选择 S2152
                String pojotype = pojo.getType();
                String totalQuestionTabletype = totalQuestionTable.getType();

                // 比较两个属性的精确度
                // 返回1 是第一个数属性精确 返回2 是第二个属性更精确
                // 返回0 则精确性相等 则进行下一步分析
                Integer typeinteger = filterAccurately(pojotype, totalQuestionTabletype);

                if (typeinteger == 1){
                    totalQuestionTableHashMap.put(key,pojo);

                }else if (typeinteger == 2){
                    totalQuestionTableHashMap.put(key,totalQuestionTable);

                }else if (typeinteger == 0){

                    String pojofirewareVersion = pojo.getFirewareVersion();
                    String totalQuestionTablefirewareVersion = totalQuestionTable.getFirewareVersion();

                    // 比较两个属性的精确度
                    Integer firewareVersioninteger = filterAccurately(pojofirewareVersion, totalQuestionTablefirewareVersion);

                    if (firewareVersioninteger == 1){
                        totalQuestionTableHashMap.put(key,pojo);

                    }else if (firewareVersioninteger == 2){
                        totalQuestionTableHashMap.put(key,totalQuestionTable);

                    }else if (firewareVersioninteger == 0){

                        String pojosubVersion = pojo.getSubVersion();
                        String totalQuestionTablesubVersion = totalQuestionTable.getSubVersion();

                        // 比较两个属性的精确度
                        Integer subVersioninteger = filterAccurately(pojosubVersion, totalQuestionTablesubVersion);

                        if (subVersioninteger == 1){
                            totalQuestionTableHashMap.put(key,pojo);

                        }else if (subVersioninteger == 2){
                            totalQuestionTableHashMap.put(key,totalQuestionTable);

                        }else if (subVersioninteger == 0){
                            // 如果 都相等 则 四项基本信息完全一致 此时 不应该存在
                            // 因为 sql 有联合唯一索引  四项基本信息+范式名称+范式分类
                            // 如果条件不满足，直接结束方法 代码段提出  结束需要通过 return 跳出方法 不再执行后续代码
                            return;
                        }
                    }
                }
            }
        }else {

            // map的key值为空 则 可以直接存入 map集合
            totalQuestionTableHashMap.put(key,totalQuestionTable);
        }
    }




    /*Inspection Completed*/
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
     * 命令实体类转化为字符串
     * @method: commandLogic转化为String
     * @Param: [commandLogic]
     * @return: java.lang.String
     */
    public static String commandLogicString(CommandLogic commandLogic){
        // 获取命令实体类的ID
        String onlyIndex = commandLogic.getId();
        // 初始化trueFalse为空字符串
        String trueFalse = "";
        // 获取命令实体类的命令
        String command = commandLogic.getCommand();
        // 初始化参数为空字符串
        String para = "";
        // 判断命令中是否包含":"，如果包含则进行拆分
        if (command.indexOf(":") != -1){
            String[] command_split = command.split(":");
            command = command_split[0]; // 命令
            para = command_split[1];    // 参数
        }
        // 获取命令实体类的结果检查ID
        String resultCheckId =  commandLogic.getResultCheckId();
        String nextIndex;
        // 根据结果检查ID决定下一个索引是问题ID还是结束索引
        if (resultCheckId.equals("0")){
            // resultCheckId.equals("0")  自定义
            nextIndex = commandLogic.getProblemId(); // 自定义检验，使用问题ID作为下一个索引
        }else {
            // 常规检验 执行下一命令
            nextIndex = commandLogic.getEndIndex(); // 常规检验，使用结束索引作为下一个索引
        }
        // 获取命令实体类的行号
        String pageIndex = commandLogic.getcLine();

        // 创建CommandLogicVO对象
        CommandLogicVO commandLogicVO = new CommandLogicVO();
        // 设置ID
        commandLogicVO.setOnlyIndex(onlyIndex);
        // 设置trueFalse
        commandLogicVO.setTrueFalse(trueFalse);
        // 设置命令
        commandLogicVO.setCommand(command);
        // 设置参数
        commandLogicVO.setPara(para);
        // 设置结果检查ID
        commandLogicVO.setResultCheckId(resultCheckId);
        // 设置下一个索引
        commandLogicVO.setNextIndex(nextIndex);
        // 设置行号
        commandLogicVO.setPageIndex(pageIndex);

        /* 自定义分隔符 */
        String customDelimiter = (String) CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());

        // 拼接CommandLogicVO对象为字符串
        String commandLogicVOSting = commandLogicVO.getPageIndex() + customDelimiter +"{"
                + "\"onlyIndex\"" +"="+ "\""+ commandLogicVO.getOnlyIndex() +"\","
                + "\"trueFalse\"" +"="+ "\""+ commandLogicVO.getTrueFalse() +"\","
                + "\"pageIndex\"" +"="+ "\""+ commandLogicVO.getPageIndex() +"\","
                + "\"command\"" +"="+ "\""+ commandLogicVO.getCommand() +"\","
                + "\"para\"" +"="+ "\""+ commandLogicVO.getPara() +"\","
                + "\"resultCheckId\"" +"="+ "\""+ commandLogicVO.getResultCheckId() +"\","
                + "\"nextIndex\"" +"="+ "\""+ commandLogicVO.getNextIndex() +"\"" +"}";

        // 返回拼接后的字符串
        return commandLogicVOSting;
    }


    /**
     * 字符串解析CommandLogic实体类并返回
     *
     * @param problem_area_code 问题编码
     * @param jsonPojo 提交的JSON格式分析数据
     * @param ifCommand 指示是命令还是分析操作的字符串
     * @return 解析后的CommandLogic实体类
     */
    public static CommandLogic analysisCommandLogic(String problem_area_code, String jsonPojo,String ifCommand){

        /**
         * 1： 将提交的分析数据从Json格式转化为实体类AnalyzeConvertJson
         * */
        AnalyzeConvertJson analyzeConvertJson = JSON.parseObject(jsonPojo, AnalyzeConvertJson.class);

        /**
         * 2：
         * 遍历analyzeConvertJson对象的所有字段
         * 如果某个字段的类型为String且值为空字符串（""），则将该字段的值设置为null
         */
        analyzeConvertJson = (AnalyzeConvertJson) MyUtils.setNullIfEmpty(analyzeConvertJson);

        /**
         * 3：将AnalyzeConvertJson实体类信息 赋值给 CommandLogic
         * 如果ifCommand是"命令"，则对analyzeConvertJson对象进行变形处理 下一ID为命令
         * */
        CommandLogic commandLogic = trimCommandTable(analyzeConvertJson, problem_area_code,ifCommand);
        return commandLogic;
    }


    /**
     * 将AnalyzeConvertJson对象转化为CommandLogic对象，并设置其相关属性。
     *
     * @param analyzeConvertJson 分析转换后的Json对象
     * @param problem_area_code  问题编码
     * @param ifCommand          指示是命令还是分析操作的字符串
     * @return 转化后的CommandLogic对象
     */
    public static CommandLogic trimCommandTable(AnalyzeConvertJson analyzeConvertJson, String problem_area_code,String ifCommand) {
        /**
         * 1：设置AnalyzeConvertJson对象的onlyIndex属性
         *   encodeID 判断是否为编码ID，
         *    是则直接使用（前端修改时，会有这种情况），
         *    不是则在前面拼接问题编码+onlyIndex
         * */
        analyzeConvertJson.setOnlyIndex(MyUtils.encodeID( analyzeConvertJson.getOnlyIndex() )?
                analyzeConvertJson.getOnlyIndex() : problem_area_code + analyzeConvertJson.getOnlyIndex());

        /**
         * 2：设置AnalyzeConvertJson对象的nextIndex属性
         *   encodeID 判断是否为编码ID，
         *    是则直接使用（前端修改时，会有这种情况），
         *    不是则在前面拼接问题编码+onlyIndex
         * */
        analyzeConvertJson.setNextIndex(MyUtils.encodeID( analyzeConvertJson.getNextIndex() )?
                analyzeConvertJson.getNextIndex() : problem_area_code + analyzeConvertJson.getNextIndex());

        /**
         * 3： Para 是什么？前端传过来的，暂时不清楚用处，暂时不做处理
         *    如果AnalyzeConvertJson对象的para属性不为空
         */
        if (analyzeConvertJson.getPara() != null){
            // 拼接AnalyzeConvertJson对象的command和para属性，并设置给command属性
            analyzeConvertJson.setCommand( analyzeConvertJson.getCommand() +":"+ analyzeConvertJson.getPara());
        }

        /**
         * 4： 赋值给 CommandLogic对象
         */
        // 创建CommandLogic对象
        CommandLogic commandLogic = new CommandLogic();
        // 设置CommandLogic对象的id属性
        commandLogic.setId(analyzeConvertJson.getOnlyIndex());
        // 设置CommandLogic对象的command属性
        commandLogic.setCommand(analyzeConvertJson.getCommand());
        // 此处原本设置CommandLogic对象的resultCheckId属性，但已被注释掉
        //commandLogic.setResultCheckId( analyzeConvertJson.getResultCheckId() );
        // 根据ifCommand的值决定CommandLogic对象的resultCheckId属性
        /** 该字段本想由前端传入，后发现前端传入数据始终为0
         * 记得之前实现过，后来可能前端有修改
         * 现由后端分析下一条数据是否为命令决定*/
        if (ifCommand.equals("命令")){
            // 如果ifCommand等于"命令"，则设置resultCheckId为"1"
            commandLogic.setResultCheckId("1");
        }else if (ifCommand.equals("分析")){
            // 如果ifCommand等于"分析"，则设置resultCheckId为"0"
            commandLogic.setResultCheckId("0");
        }

        // 根据CommandLogic对象的resultCheckId属性值决定设置哪个属性
        /* 属性值(0:分析逻辑表ID 1:本表ID ) 分析逻辑表：problem_scan_logic*/
        if (commandLogic.getResultCheckId().equals("1")){
            // 如果resultCheckId等于"1"，则设置CommandLogic对象的endIndex属性
            /** 命令结束索引 */
            commandLogic.setEndIndex(analyzeConvertJson.getNextIndex());
        }else if (commandLogic.getResultCheckId().equals("0")){
            // 如果resultCheckId等于"0"，则设置CommandLogic对象的problemId属性
            /** 命令结束索引 */
            commandLogic.setProblemId(analyzeConvertJson.getNextIndex());
        }
        // 设置CommandLogic对象的cLine属性
        /** 命令行号 */
        commandLogic.setcLine(analyzeConvertJson.getPageIndex());
        // 返回CommandLogic对象
        return commandLogic;
    }


    /**
     * 分析实体类转换为字符串
     * @method: problemScanLogic 实体类转化  Sting
     * @Param: [problemScanLogic]
     * @return: java.lang.String
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
            if (action.indexOf("all")!=-1){
                problemScanLogicVO.setCursorRegion("2");
                action=action.replace("all","");
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
        /*自定义分隔符*/
        String customDelimiter = (String) CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());
        String problemScanLogicVOString = problemScanLogicVO.getPageIndex()+ customDelimiter +"{"
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
        /* 定义需要返回的实体类集合 */
        List<ProblemScanLogic> ProblemScanLogics = new ArrayList<>();

        /* 遍历传入的ProblemScanLogic对象列表 */
        for (ProblemScanLogic problemScanLogic : ProblemScanLogicList) {
            /* 当错误行号不为空时，执行以下操作 */
            if (problemScanLogic.getfLine() != null) {
                /* 创建一个新的ProblemScanLogic对象用于存储错误信息 */
                ProblemScanLogic problemScanLogicf = new ProblemScanLogic();

                /* 复制原对象的ID到新对象 */
                problemScanLogicf.setId(problemScanLogic.getId());

                /* 复制与错误相关的属性到新对象 */
                problemScanLogicf.setfLine(problemScanLogic.getfLine());
                problemScanLogicf.setfNextId(problemScanLogic.getfNextId());
                problemScanLogicf.setfComId(problemScanLogic.getfComId());

                /* 清空原对象中的错误相关信息 */
                problemScanLogic.setfLine(null);
                problemScanLogic.setfNextId(null);
                problemScanLogic.setProblemId(null);
                problemScanLogic.setfComId(null);

                /* 将新对象添加到返回集合中 */
                ProblemScanLogics.add(problemScanLogicf);
            }

            /* 无论错误行号是否为空，都将原对象添加到返回集合中 */
            ProblemScanLogics.add(problemScanLogic);
        }

        /* 返回处理后的实体类集合 */
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
            if (relativePosition != null ) {/* && matchContent != null   匹配内容  */
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
