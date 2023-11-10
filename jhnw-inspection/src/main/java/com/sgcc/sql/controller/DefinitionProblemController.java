package com.sgcc.sql.controller;
import com.alibaba.fastjson.JSON;
import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.MyUtils;
import com.sgcc.share.util.PathHelper;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.*;
import com.sgcc.share.webSocket.WebSocketService;
import com.sgcc.sql.util.InspectionMethods;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年03月22日 11:17
 */
@RestController
@RequestMapping("/sql/DefinitionProblemController")
@Transactional(rollbackFor = Exception.class)
@Api("问题相关")
public class DefinitionProblemController extends BaseController {

    @Autowired
    private  ICommandLogicService commandLogicService;
    @Autowired
    private  IProblemScanLogicService problemScanLogicService;
    @Autowired
    private  ITotalQuestionTableService totalQuestionTableService;
    @Autowired
    private  IBasicInformationService basicInformationService;
    /**
     * 交换机问题、分析、修复表数据导出
     * @param
     * @return
     */
    @ApiOperation("数据库导出")
    @PostMapping("/scanningSQL")
    public AjaxResult scanningSQL() {
        Long totalQuestionTableId = null;
        Long[] totalQuestionTableIds = new Long[1];
        totalQuestionTableIds[0] = totalQuestionTableId;
        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //List<TotalQuestionTable> totalQuestionTableList = new ArrayList<>();
        List<String> fileName = new ArrayList<>();
        /*问题表*/
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        /* todo 交换机问题表*/
        List<TotalQuestionTable> totalQuestionTables = new ArrayList<>();
        /* todo 命令表 */
        List<CommandLogic> commandLogicList = new ArrayList<>();
        /* todo 命令表 */
        List<CommandLogic> repaircommandLogicList = new ArrayList<>();
        /* todo 分析表 */
        List<ProblemScanLogic> problemScanLogicList = new ArrayList<>();
        if (totalQuestionTableId != null){
            totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableByIds(totalQuestionTableIds);
        }else {
            /*导出功能 查询所有*/
            totalQuestionTables = totalQuestionTableService.scanningSQLselectTotalQuestionTableList();
        }
        DefinitionProblemController definitionProblemController = new DefinitionProblemController();
        for (TotalQuestionTable totalQuestionTable:totalQuestionTables){
            HashMap<String, Object> scanLogicalEntityClass = definitionProblemController.getScanLogicalEntityClass(totalQuestionTable, loginUser);
            if (scanLogicalEntityClass.size() != 0){
                /*命令数据*/
                List<CommandLogic> commandLogics = (List<CommandLogic>) scanLogicalEntityClass.get("CommandLogic");
                if (commandLogics.size() != 0){
                    for (CommandLogic pojo:commandLogics){
                        commandLogicList.add(pojo);
                    }
                }
            }
            /*分析数据*/
            List<ProblemScanLogic> problemScanLogics = (List<ProblemScanLogic>) scanLogicalEntityClass.get("ProblemScanLogic");
            if (problemScanLogics.size() != 0){
                for (ProblemScanLogic pojo:problemScanLogics){
                    problemScanLogicList.add(pojo);
                }
            }
            if(totalQuestionTable.getProblemSolvingId() != null){
                SolveProblemController solveProblemController = new SolveProblemController();
                List<CommandLogic> commandPojoList = solveProblemController.queryCommandSet(totalQuestionTable.getProblemSolvingId());
                if (commandPojoList.size() != 0){
                    for (CommandLogic pojo:commandPojoList){
                        repaircommandLogicList.add(pojo);
                    }
                }
            }
        }
        List<ProblemScanLogic> pojoList = InspectionMethods.definitionProblem(problemScanLogicList);
        List<String> exportText = new ArrayList<>();
        exportText.add("交换机问题表");
        for (TotalQuestionTable pojo:totalQuestionTables){
            exportText.add(pojo.toJson());
        }
        exportText.add("交换机分析表");
        for (ProblemScanLogic pojo:pojoList){
            exportText.add(pojo.toJson());
        }
        exportText.add("交换机命令表");
        for (CommandLogic pojo:commandLogicList){
            exportText.add(pojo.toJson());
        }
        for (CommandLogic pojo:repaircommandLogicList){
            exportText.add(pojo.toJson());
        }
        fileName.add(MyUtils.fileWrite(exportText, MyUtils.getDate("yyyyMMddhhmmss")));
        AjaxResult ajaxResult = new AjaxResult(200, "成功", fileName);
        return ajaxResult;
    }

    @ApiOperation("数据库导入")
    @PostMapping("/importData")
    @ResponseBody
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {

        /* 读入TXT文件
         * 将读取到的信息 按行放入list集合中*/
        InputStream inputStream = file.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream); // 建立一个输入流对象reader
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
        List<String> line_list = new ArrayList<>();
        String line = "";
        while ((line = br.readLine()) != null) {// 一次读入一行数据
            line_list.add(line);
        }
        /*遍历信息集合 根据关键词判断放入 三张表 的集合中*/
        List<String> totalQuestionTableList = new ArrayList<>(); //1  交换机问题表
        List<String> commandLogicList = new ArrayList<>(); //2   交换机命令表
        List<String> problemScanLogicList = new ArrayList<>(); //3  交换机分析表
        int number = 0;
        for (int num = 0;num<line_list.size();num++){
            String line_String = line_list.get(num);
            if (line_String.equals("交换机问题表")){
                number = 1;
            }else if (line_String.equals("交换机命令表")){
                number = 2;
            }else if (line_String.equals("交换机分析表")){
                number = 3;
            }
            switch (number){
                case 1:
                    totalQuestionTableList.add(line_String);
                    break;
                case 2:
                    commandLogicList.add(line_String);
                    break;
                case 3:
                    problemScanLogicList.add(line_String);
                    break;
            }
        }
        /*遍历三张表的集合
         * 将 Json字符串 转变换为 对应的实体类*/
        List<CommandLogic> commandLogics = new ArrayList<>();
        for (int i = 1 ; i <commandLogicList.size();i++){
            commandLogics.add( JSON.parseObject( commandLogicList.get(i), CommandLogic.class) );
        }
        List<ProblemScanLogic> problemScanLogics = new ArrayList<>();
        for (int i = 1 ; i <problemScanLogicList.size();i++){
            problemScanLogics.add( JSON.parseObject( problemScanLogicList.get(i), ProblemScanLogic.class) );
        }
        List<TotalQuestionTable> totalQuestionTables = new ArrayList<>();
        for (int i = 1 ; i <totalQuestionTableList.size();i++){
            totalQuestionTables.add( JSON.parseObject( totalQuestionTableList.get(i), TotalQuestionTable.class) );
        }
        /*将实体类对象插入数据库*/
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
        problemScanLogicService = SpringBeanUtil.getBean(IProblemScanLogicService.class);
        for (CommandLogic pojo:commandLogics){
            int i = commandLogicService.insertCommandLogicImport(pojo);
            if (i<0){
                return AjaxResult.error();
            }
        }
        for (ProblemScanLogic pojo:problemScanLogics){
            int i = problemScanLogicService.insertProblemScanLogicImport(pojo);
            if (i<0){
                return AjaxResult.error();
            }
        }
        for (TotalQuestionTable pojo:totalQuestionTables){
            int i = totalQuestionTableService.insertTotalQuestionTableImport(pojo);
            if (i<0){
                return AjaxResult.error();
            }
        }
        return AjaxResult.success();
    }


    /**
     * @method: 定义获取基本信息命令插入
     * @Param: [jsonPojoList]
     * @return: void
     * @Author: 天幕顽主=:=
     * @E-mail: WeiYaNing97@163.com
     */
    @ApiOperation("定义获取交换机基本信息命令")
    @PostMapping("insertInformationAnalysis/{command}/{custom}")
    @MyLog(title = "定义获取基本信息分析数据插入", businessType = BusinessType.UPDATE)
    public boolean insertInformationAnalysis(@RequestBody List<String> jsonPojoList,@PathVariable String[] command,@PathVariable String custom){
        custom = "["+custom+"]";
        String comands = "";
        for (int num = 0 ;num < command.length; num++){
            comands = comands + command[num] + "=:=";
        }
        comands = comands.substring(0,comands.length()-"=:=".length()) + custom;
        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //创建实体类
        BasicInformation basicInformation = new BasicInformation();
        //放入获取交换机基本信息命令
        basicInformation.setCommand(comands);
        basicInformationService = SpringBeanUtil.getBean(IBasicInformationService.class);
        int i = basicInformationService.insertBasicInformation(basicInformation);
        //当i<=0时插入失败
        if (i<=0){
            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(loginUser.getUsername(),"错误："+"获取交换机基本信息命令插入失败\r\n");
            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFile("错误："+"获取交换机基本信息命令插入失败\r\n"
                        +"方法com.sgcc.web.controller.sql.DefinitionProblemController.insertInformationAnalysis");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        Long id = basicInformation.getId();
        boolean insertInformationAnalysisMethod = insertInformationAnalysisMethod(loginUser,jsonPojoList,id);
        return insertInformationAnalysisMethod;
    }

    /**
     * @method: 定义获取基本信息分析数据插入
     * @Param: [jsonPojoList]
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public boolean insertInformationAnalysisMethod(LoginUser loginUser,@RequestBody List<String> jsonPojoList,Long basicInformationId){//@RequestBody List<String> jsonPojoList
        if (jsonPojoList.size() == 0){
            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(loginUser.getUsername(),"错误："+"获取交换机基本信息分析数据非法为空\r\n");
            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFile("错误："+"获取交换机基本信息分析数据非法为空\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.err.println("定义获取基本信息分析数据插入\r\n");
        for (String jsonPojo:jsonPojoList){
            System.err.println(jsonPojo);
        }
        List<CommandLogic> commandLogicList = new ArrayList<>();
        List<ProblemScanLogic> problemScanLogicList = new ArrayList<>();
        /*遍历分析数据
        * 如果分析数据中含有command 则 是命令 则 进入 String 转 命令实体类方法
        * 如果分析数据中不含有command 则 是分析 则 进入 String 转 分析实体类方法*/
        for (int number=0;number<jsonPojoList.size();number++){
            // 如果 前端传输字符串  存在 command  说明 是命令
            if (jsonPojoList.get(number).indexOf("command")!=-1){
                CommandLogic commandLogic = InspectionMethods.analysisCommandLogic(jsonPojoList.get(number));
                commandLogicList.add(commandLogic);
                continue;
            }else if (!(jsonPojoList.get(number).indexOf("command") !=-1)){
                /*如果分析数据中不含有command 则 是分析 则 进入 String 转 分析实体类方法
                * 如果当前集合元素是分析 则 需要考虑下一集合元素是 命令 还是分析
                * 如果是命令 则 在 Sting 转 分析实体类中 传入 命令属性值
                * 如果是分析 则 在 Sting 转 分析实体类中 传入 分析属性值
                * 这会影响到 前端数据传入的 下一ID 的 赋值问题*/
                if (number+1<jsonPojoList.size()){
                    // 判断下一条是否是命令  因为 如果下一条是命令 则要 将 下一条分析ID 放入 命令ID
                    if (jsonPojoList.get(number+1).indexOf("command") !=-1){
                        //本条是分析 下一条是 命令
                        ProblemScanLogic problemScanLogic = InspectionMethods.analysisProblemScanLogic(jsonPojoList.get(number), "命令");
                        problemScanLogicList.add(problemScanLogic);
                        continue;
                    }else {
                        //本条是分析 下一条是 分析
                        ProblemScanLogic problemScanLogic = InspectionMethods.analysisProblemScanLogic(jsonPojoList.get(number), "分析");
                        problemScanLogicList.add(problemScanLogic);
                        continue;
                    }
                }else {
                    //本条是分析 下一条是 问题
                    ProblemScanLogic problemScanLogic = InspectionMethods.analysisProblemScanLogic(jsonPojoList.get(number), "分析");
                    problemScanLogicList.add(problemScanLogic);
                    continue;
                }
            }
        }
        //将相同ID  时间戳 的 实体类 放到一个实体
        /*相同ID的分析实体类需要放到一个实体类中(因为这里是ture和false的原因，造成了一个实体类分割成了两个相同ID的实体类)*/
        List<ProblemScanLogic> problemScanLogics = InspectionMethods.definitionProblem(problemScanLogicList);
        for (ProblemScanLogic problemScanLogic:problemScanLogics){
            problemScanLogicService = SpringBeanUtil.getBean(IProblemScanLogicService.class);
            int i = problemScanLogicService.insertProblemScanLogic(problemScanLogic);
            if (i<=0){
                WebSocketService.sendMessage(loginUser.getUsername(),"错误："+"获取交换机基本信息分析数据插入失败\r\n");
                try {
                    PathHelper.writeDataToFile("错误："+"获取交换机基本信息分析数据插入失败\r\n"
                            +"方法com.sgcc.web.controller.sql.DefinitionProblemController.insertInformationAnalysisMethod");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }

        /*获取交换机基本信息第一条数据为ID 需要传送给 获取交换机基本信息命令的分析ID*/
        String jsonPojoOne = jsonPojoList.get(0);
        ProblemScanLogic problemScanLogic = InspectionMethods.analysisProblemScanLogic(jsonPojoOne, "分析");
        BasicInformation basicInformation = basicInformationService.selectBasicInformationById(basicInformationId);
        basicInformation.setProblemId(problemScanLogic.getId());
        int i = basicInformationService.updateBasicInformation(basicInformation);
        if (i<=0){
            WebSocketService.sendMessage(loginUser.getUsername(),"错误："+"获取交换机基本信息命令的ProblemId字段失败\r\n");
            try {
                PathHelper.writeDataToFile("错误："+"获取交换机基本信息命令的ProblemId字段失败\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }


    /**
     * @method: 插入分析问题的数据
     * @Param: [jsonPojoList]
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @PostMapping("definitionProblemJsonPojo")
    @MyLog(title = "定义分析问题数据插入", businessType = BusinessType.UPDATE)
    @ApiOperation("定义分析问题数据")
    public boolean definitionProblemJson(@RequestParam Long totalQuestionTableId,@RequestBody List<String> jsonPojoList){

        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        boolean definitionProblemJsonboolean = definitionProblemJsonPojo(totalQuestionTableId,jsonPojoList,loginUser);
        return definitionProblemJsonboolean;
    }

    /**
     * @method: 插入分析问题的数据
     * @Param: [jsonPojoList]
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public boolean definitionProblemJsonPojo(Long totalQuestionTableId,@RequestBody List<String> jsonPojoList,LoginUser loginUser){
        //@RequestBody List<String> jsonPojoList
        // /*判断问题分析逻辑是否为空*/
       if (jsonPojoList.size() == 0){
           //传输登陆人姓名 及问题简述
           WebSocketService.sendMessage(loginUser.getUsername(),"错误："+"扫描分析数据非法为空\r\n");
           try {
               //插入问题简述及问题路径
               PathHelper.writeDataToFile("错误："+"扫描分析数据非法为空\r\n");
           } catch (IOException e) {
               e.printStackTrace();
           }
           return false;
       }

        System.err.println("\r\n"+"前端出入数据：\r\n");
        for (String jsonPojo:jsonPojoList){
            System.err.println(jsonPojo);
        }

        List<CommandLogic> commandLogicList = new ArrayList<>();
        List<ProblemScanLogic> problemScanLogicList = new ArrayList<>();
        /*遍历数据 属于命令还是分析数据*/
        for (int number=0;number<jsonPojoList.size();number++){
            // 如果 前端传输字符串  存在 command  说明 是命令
            if (jsonPojoList.get(number).indexOf("command")!=-1){

                CommandLogic commandLogic = InspectionMethods.analysisCommandLogic(jsonPojoList.get(number));
                commandLogicList.add(commandLogic);
                continue;

            }else if (!(jsonPojoList.get(number).indexOf("command") !=-1) && !(jsonPojoList.get(number).indexOf("method") !=-1)){
                /*当数据不为集合的最后一个元素时
                * 需要判断 下一条数据是否为 命令 如果是命令 则 分析数据应该标明下一条为命令表数据*/
                if (number+1<jsonPojoList.size()){

                    // 判断下一条是否是命令  因为 如果下一条是命令 则要 将 下一条分析ID 放入 命令ID
                    if (jsonPojoList.get(number+1).indexOf("command") !=-1){
                        //本条是分析 下一条是 命令
                        ProblemScanLogic problemScanLogic = InspectionMethods.analysisProblemScanLogic(jsonPojoList.get(number), "命令");
                        problemScanLogicList.add(problemScanLogic);
                        continue;
                    }else {
                        //本条是分析 下一条是 分析
                        ProblemScanLogic problemScanLogic = InspectionMethods.analysisProblemScanLogic(jsonPojoList.get(number), "分析");
                        problemScanLogicList.add(problemScanLogic);
                        continue;
                    }

                }else {

                    //本条是分析 下一条是 问题
                    ProblemScanLogic problemScanLogic = InspectionMethods.analysisProblemScanLogic(jsonPojoList.get(number), "分析");
                    problemScanLogicList.add(problemScanLogic);
                    continue;

                }

            }
        }

        //将相同ID  时间戳 的 实体类 放到一个实体
        List<ProblemScanLogic> problemScanLogics = InspectionMethods.definitionProblem(problemScanLogicList);
        String totalQuestionTableById = totalQuestionTableId+"";;
        String commandId = null;
        for (ProblemScanLogic problemScanLogic:problemScanLogics){
            problemScanLogicService = SpringBeanUtil.getBean(IProblemScanLogicService.class);
            int i = problemScanLogicService.insertProblemScanLogic(problemScanLogic);
            if (i<=0){
                return false;
            }

        }

        /* 如果命令 为 第一个参数 则 问题数据的下一条ID 添加命令*/
        for (CommandLogic commandLogic:commandLogicList){
            /*判断行号是否为 1 */
            if (commandLogic.getcLine().equals("1")){
                commandId = "命令"+commandLogic.getId();
            }
            commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
            int i = commandLogicService.insertCommandLogic(commandLogic);
            if (i<=0){
                return false;
            }
        }
        if(commandId == null){
            for (ProblemScanLogic problemScanLogic:problemScanLogics){
                if (problemScanLogic.gettLine().equals("1")){
                    commandId = "分析"+problemScanLogic.getId();
                }
            }
        }
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(Integer.valueOf(totalQuestionTableById).longValue());
        /*赋值问题表数据的 逻辑ID  此时为命令ID*/
        totalQuestionTable.setLogicalID(commandId);



        int i = totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable);
        if (i<=0){
            return false;
        }
        return true;
    }




    /***
     * @method: 根据交换机问题实体类查询问题分析逻辑数据
     * @Param: []
     * @return: java.util.List<java.lang.String>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @ApiOperation("查询定义分析问题数据")
    @GetMapping("getAnalysisList")
    @MyLog(title = "查询定义分析问题数据", businessType = BusinessType.OTHER)
    public AjaxResult getAnalysisListTimeouts(TotalQuestionTable totalQuestionTable) {
        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        final List<String>[] analysisList = new List[]{new ArrayList<>()};
        FutureTask future = new FutureTask(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                analysisList[0] = getAnalysisList(totalQuestionTable,loginUser);
                return analysisList[0];
            }
        });
        executor.execute(future);
        try {
            Integer maximumTimeoutString = (Integer) CustomConfigurationUtil.getValue("configuration.maximumTimeout", Constant.getProfileInformation());
            List<String> result = (List<String>) future.get(Long.valueOf(maximumTimeoutString).longValue(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            return AjaxResult.error("查询超时");
        }finally{
            future.cancel(true);
            executor.shutdown();
        }
        return AjaxResult.success(analysisList[0]);
    }


    //@RequestMapping("getAnalysisList")
    public  List<String> getAnalysisList(@RequestBody TotalQuestionTable totalQuestionTable,LoginUser loginUser){
        /*根据 交换机问题实体类 获得命令集合和分析实体类集合*/
        HashMap<String, Object> scanLogicalEntityClass = getScanLogicalEntityClass(totalQuestionTable, loginUser);

        if (scanLogicalEntityClass.size() == 0){
            return new ArrayList<>();
        }
        /* 获取两个实体类集合*/
        List<CommandLogic> commandLogicList = (List<CommandLogic>) scanLogicalEntityClass.get("CommandLogic");
        List<ProblemScanLogic> problemScanLogics = (List<ProblemScanLogic>) scanLogicalEntityClass.get("ProblemScanLogic");

        HashMap<Long,String> hashMap = new HashMap<>();
        for (CommandLogic commandLogic:commandLogicList){
            /* 1=:={"onlyIndex"="1697080798279","trueFalse"="","pageIndex"="1","command"="dis cu","para"="","resultCheckId"="0","nextIndex"="1697080824879"} */
            String commandLogicString = InspectionMethods.commandLogicString(commandLogic);
            String[] commandLogicStringsplit = commandLogicString.split("=:=");
            hashMap.put(Integer.valueOf(commandLogicStringsplit[0]).longValue(),commandLogicStringsplit[1]);
        }
        for (ProblemScanLogic problemScanLogic:problemScanLogics){
            String problemScanLogicString = InspectionMethods.problemScanLogicSting(problemScanLogic,totalQuestionTable.getId()+"");
            String[] problemScanLogicStringsplit = problemScanLogicString.split("=:=");
            hashMap.put(Integer.valueOf(problemScanLogicStringsplit[0]).longValue(),problemScanLogicStringsplit[1]);
        }

        /*List<String> stringList = new ArrayList<>();
        for (Long number=0L;number<hashMap.size();number++){
            if (hashMap.get(number+1)!=null && !(hashMap.get(number+1).equals("null"))){
                System.err.println(hashMap.get(number+1));
                stringList.add(hashMap.get(number+1));
            }
        }*/
        Collection<String> values = hashMap.values();
        List<String> stringList = new ArrayList<>(values);
        return stringList;
    }

    /**
    * @Description  根据 交换机问题实体类 获得命令集合和分析实体类集合
    * @author charles
    * @createTime 2023/10/12 15:04
    * @desc
    * @param totalQuestionTable
     * @param loginUser
     * @return
    */
    public  HashMap<String,Object> getScanLogicalEntityClass(@RequestBody TotalQuestionTable totalQuestionTable,LoginUser loginUser) {
        /* 判断分析ID 是否为空
         * 如果为空 则 返回 null
         * 取出命令 或 分析 字段 */
        if (totalQuestionTable.getLogicalID() == null){
            return new HashMap<>();
        }
        String problemScanLogicID = totalQuestionTable.getLogicalID();
        /*去除 "命令" 或 "分析"
        * 得到 命令表 或者    分析表ID（存入分析集合）
        * */
        List<String> problemIds = new ArrayList<>();
        List<String> commandIDs = new ArrayList<>();
        if (problemScanLogicID.indexOf("分析") != -1){
            problemIds.add(problemScanLogicID.replaceAll("分析",""));
        }else if (problemScanLogicID.indexOf("命令") != -1){
            commandIDs.add(problemScanLogicID.replaceAll("命令",""));
        }
        /* 命令表集合
        * 返回分析数据集合 */
        List<CommandLogic> commandLogicList = new ArrayList<>();
        List<ProblemScanLogic> problemScanLogics = new ArrayList<>();
        HashMap<String,Object> hashMappojo = new HashMap<>();
        do {
            /* 如果分析ID 不为空
             * 则当前的ID为 分析数据ID */
            if (problemIds.size() != 0){
                //根据分析ID 查询出所有的数据条数
                /*需要行号 所以 需要拆分 true false */
                List<ProblemScanLogic> problemScanLogicList = new ArrayList<>();
                for (String problemId:problemIds){
                    problemScanLogicList.addAll(problemScanLogicList(problemId,loginUser));//commandLogic.getProblemId()
                }
                /*存放入数据库里的数据实体类拆分true和false*/
                problemScanLogicList = InspectionMethods.splitSuccessFailureLogic(problemScanLogicList);
                /* 查询完数据 将分析ID置空 */
                problemIds = new ArrayList<>();
                if (problemScanLogicList.size() ==0 ){
                    HashMap<String,Object> ScanLogicalEntityMap = new HashMap<>();
                    ScanLogicalEntityMap.put("CommandLogic",commandLogicList);
                    ScanLogicalEntityMap.put("ProblemScanLogic",problemScanLogics);
                    return ScanLogicalEntityMap;

                }
                /*遍历分析实体类集合，筛选出 命令ID  拼接命令String*/
                for (ProblemScanLogic problemScanLogic:problemScanLogicList){
                    /* 根据行号 查询 map集合中 是否存在 分析实体类
                    * 存在则结束本次循环
                    * 不存在 则插入map集合 并插入 返回分析数据集合 */
                    if (hashMappojo.get(problemScanLogic.gettLine()) != null
                            || hashMappojo.get(problemScanLogic.getfLine()) != null){
                        continue;
                    }else {
                        if (problemScanLogic.gettLine() != null){
                            hashMappojo.put(problemScanLogic.gettLine(),problemScanLogic);
                        }
                        if (problemScanLogic.getfLine() != null){
                            hashMappojo.put(problemScanLogic.getfLine(),problemScanLogic);
                        }
                    }
                    /*插入 返回分析数据集合*/
                    problemScanLogics.add(problemScanLogic);
                    /*判断 下一命令ID是否为空 不为空则 插入命令ID集合*/
                    if (problemScanLogic.gettComId()!=null && problemScanLogic.gettComId()!= ""){
                        commandIDs.add(problemScanLogic.gettComId());
                    }
                    if (problemScanLogic.getfComId()!=null && problemScanLogic.getfComId()!= ""){
                        commandIDs.add(problemScanLogic.getfComId());
                    }
                }
                /*如果 取出的 分析数据 没有下一命令ID 则 退出 do while*/
                if (commandIDs.size() == 0){
                    break;
                }
            }
            /*如果 命令ID集合 不为空*/
            if (commandIDs.size() != 0){
                List<String>  commandIDList = commandIDs;
                /*命令ID 清空 */
                commandIDs = new ArrayList<>();
                for (String commandid:commandIDList){
                    commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
                    CommandLogic commandLogic = commandLogicService.selectCommandLogicById(commandid);
                    if (commandLogic == null || commandLogic.getProblemId() == null){
                        HashMap<String,Object> ScanLogicalEntityMap = new HashMap<>();
                        ScanLogicalEntityMap.put("CommandLogic",commandLogicList);
                        ScanLogicalEntityMap.put("ProblemScanLogic",problemScanLogics);
                        return ScanLogicalEntityMap;
                    }
                    commandLogicList.add(commandLogic);
                    if (hashMappojo.get(commandLogic.getcLine()) != null){
                        continue;
                    }else {
                        hashMappojo.put(commandLogic.getcLine(),commandLogic);
                    }
                    /*根据命令实体类 ResultCheckId 值  考虑 是否走 分析表
                     * ResultCheckId 为 0 时，则 进行分析
                     * ResultCheckId 为 1 时，则 进行命令 */
                    if (commandLogic.getResultCheckId().equals("0")){
                        problemIds.add(commandLogic.getProblemId());
                    }else {
                        commandIDs.add(commandLogic.getEndIndex());
                    }
                }
            }
        }while (commandIDs.size() != 0 || problemIds.size() != 0);
        HashMap<String,Object> ScanLogicalEntityMap = new HashMap<>();
        ScanLogicalEntityMap.put("CommandLogic",commandLogicList);
        ScanLogicalEntityMap.put("ProblemScanLogic",problemScanLogics);
        return ScanLogicalEntityMap;
    }


    /**
     * @param id
     * @return
     */
    @DeleteMapping("deleteScanningLogic")
    @ApiOperation("删除扫描逻辑数据")
    public boolean deleteScanningLogic(@RequestBody Long id) {
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(id);
        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        /* 获取命令集合和分析逻辑集合 */
        HashMap<String, Object> scanLogicalEntityClass = getScanLogicalEntityClass(totalQuestionTable, loginUser);
        /*命令集合  分析逻辑集合  */
        List<CommandLogic> commandLogicList = (List<CommandLogic>) scanLogicalEntityClass.get("CommandLogic");
        List<ProblemScanLogic> problemScanLogics = (List<ProblemScanLogic>) scanLogicalEntityClass.get("ProblemScanLogic");
        /*命令集合、分析逻辑集合 筛选ID集合*/
        String[] commandLogicId = commandLogicList.stream().map(p -> p.getId()).distinct().toArray(String[]::new);
        String[] problemScanLogicId = problemScanLogics.stream().map(p -> p.getId()).distinct().toArray(String[]::new);

        int deleteCommandLogicByIds = 1;
        /*命令集合不为空 删除命令集合*/
        if (commandLogicId.length>0){
            commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
            deleteCommandLogicByIds = commandLogicService.deleteCommandLogicByIds(commandLogicId);
        }

        /*当命令删除成功 且 存在分析时 删除分析数据*/
        if (deleteCommandLogicByIds >0 && problemScanLogicId.length >0){
            problemScanLogicService = SpringBeanUtil.getBean(IProblemScanLogicService.class);
            int deleteProblemScanLogicByIds = problemScanLogicService.deleteProblemScanLogicByIds(problemScanLogicId);
            if (deleteProblemScanLogicByIds>0){
                totalQuestionTable.setLogicalID(null);
                int updateQuestionTableById = totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable);
                if (updateQuestionTableById>0){
                    /*删除成功*/
                    return true;
                }else {
                    //传输登陆人姓名 及问题简述
                    WebSocketService.sendMessage(loginUser.getUsername(),"风险："+"扫描交换机问题表数据删除失败\r\n");
                    try {
                        //插入问题简述及问题路径
                        PathHelper.writeDataToFile("风险："+"扫描交换机问题表数据删除失败\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else {
                //传输登陆人姓名 及问题简述
                WebSocketService.sendMessage(loginUser.getUsername(),"风险："+"扫描交换机问题分析逻辑删除失败\r\n");
                try {
                    //插入问题简述及问题路径
                    PathHelper.writeDataToFile("风险："+"扫描交换机问题分析逻辑删除失败\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (deleteCommandLogicByIds >0  && problemScanLogicId.length == 0){
            /*只有 存在命令 没有分析*/
            totalQuestionTable.setLogicalID(null);
            int updateQuestionTableById = totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable);
            if (updateQuestionTableById>0){
                /*删除成功*/
                return true;
            }else {
                //传输登陆人姓名 及问题简述
                WebSocketService.sendMessage(loginUser.getUsername(),"风险："+"扫描交换机问题表数据删除失败\r\n");
                try {
                    //插入问题简述及问题路径
                    PathHelper.writeDataToFile("风险："+"扫描交换机问题表数据删除失败\r\n"
                            +"方法com.sgcc.web.controller.sql.DefinitionProblemController.deleteScanningLogic");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            //deleteCommandLogicByIds < 0
            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(loginUser.getUsername(),"风险："+"扫描交换机问题命令删除失败\r\n");
            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFile("风险："+"扫描交换机问题命令删除失败\r\n"
                        +"方法com.sgcc.web.controller.sql.DefinitionProblemController.deleteScanningLogic");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 根据分析ID 获取 分析实体类集合
    * @Description
    * @author charles
    * @createTime 2023/11/3 13:24
    * @desc
    * @param problemScanLogicID
     * @param loginUser
     * @return  返回 集合
    */
    //@RequestMapping("problemScanLogicList")
    public List<ProblemScanLogic> problemScanLogicList(String problemScanLogicID,LoginUser loginUser){
        /*预设跳出循环条件 为 false*/
        boolean contain = false;
        /*预设map key为分析表ID value为 分析表实体类*/
        Map<String,ProblemScanLogic> problemScanLogicHashMap = new HashMap<>();
        do {
            String  problemScanID = "";
            /*分析ID 根据“：” 分割 为 分析ID数组
            * 因为分析逻辑 可能出现 true 和 false 多个ID情况*/
            String[] problemScanLogicIDsplit = problemScanLogicID.split(":");
            /*遍历分析ID数组*/
            for (String id:problemScanLogicIDsplit){
                /*根据分析ID 在 map中查询 分析实体类
                * 如果查询不为null 则 说明 map中存在 分析实体类
                * 需要进行 分析ID数组的 下一项 查询  。
                * 结束本次循环 进行下一循环 用 continue */
                ProblemScanLogic pojo = problemScanLogicHashMap.get(id);
                if (pojo!=null){
                    problemScanLogicHashMap.put(id,pojo);
                    continue;
                }
                /*如果查询为null 则 说明 map中不存在 分析实体类
                * 需要到数据库中查询
                * 并放入 map中 存储
                * 如果查询结果为 null 则返回 查询结果为null*/
                problemScanLogicService = SpringBeanUtil.getBean(IProblemScanLogicService.class);
                ProblemScanLogic problemScanLogic = problemScanLogicService.selectProblemScanLogicById(id);
                if (problemScanLogic ==null){
                    //传输登陆人姓名 及问题简述
                    WebSocketService.sendMessage(loginUser.getUsername(),"错误："+"根据ID："+id+"查询分析表数据失败,未查出对应ID数据\r\n");
                    try {
                        //插入问题简述及问题路径
                        PathHelper.writeDataToFile("错误："+"根据ID："+id+"查询分析表数据失败,未查出对应ID数据\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    /* 返回空集合 */
                    return new ArrayList<>();
                }
                problemScanLogicHashMap.put(id,problemScanLogic);
                /*当循环ID不为空时，则查询逻辑中，分支中没有下一行ID
                * 则 进行下一元素的分析iD*/
                if (problemScanLogic.getCycleStartId()!=null && !(problemScanLogic.getCycleStartId().equals("null"))){
                    continue;
                }
                /*如果 正确 下一ID 不为空 则 拼接到 分析表ID中*/
                if (problemScanLogic.gettNextId()!=null && !(problemScanLogic.gettNextId().equals("null"))
                        && problemScanLogic.gettNextId()!="" &&  !(MyUtils.isContainChinese(problemScanLogic.gettNextId()))){
                    problemScanID += problemScanLogic.gettNextId()+":";
                }
                /*如果 错误 下一ID 不为空 则 拼接到 分析表ID中*/
                if (problemScanLogic.getfNextId()!=null && !(problemScanLogic.getfNextId().equals("null"))
                        && problemScanLogic.getfNextId()!="" &&  !(MyUtils.isContainChinese(problemScanLogic.getfNextId()))){
                    problemScanID += problemScanLogic.getfNextId()+":";
                }
            }
            /*如果没有ID 则 视为没有下一层 分析数据 则 退出 do while*/
            if (problemScanID.equals("")){
                break;
            }
            /*分割 分析ID 为数组
            * 遍历数组 及 map
            * 去除 map中存在的 分析ID*/
            String[] problemScanIDsplit = problemScanID.split(":");
            problemScanID = "";
            for (String id:problemScanIDsplit){
                if (problemScanLogicHashMap.get(id) == null){
                    problemScanID += id+":";
                }
            }

            /*如果problemScanID 不为 "" 则 contain = true
            * problemScanID 去掉 最后一个 ： */
            if (!(problemScanID.equals(""))){
                contain = true;
                problemScanLogicID = problemScanID.substring(0,problemScanID.length()-1);
            }else {
                contain = false;
            }

        }while (contain);

        /*map中数据 存放到list集合中*/
        /*List<ProblemScanLogic> ProblemScanLogicList = new ArrayList<>();
        Iterator<Map.Entry<String, ProblemScanLogic>> it = problemScanLogicHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ProblemScanLogic> entry = it.next();
            ProblemScanLogicList.add(entry.getValue());
        }*/
        Collection<ProblemScanLogic> values = problemScanLogicHashMap.values();
        // 将Collection转换为List
        List<ProblemScanLogic> ProblemScanLogicList = new ArrayList<>(values);
        /*此时查询出了  数据库存储的 信息*/
        return ProblemScanLogicList;
    }

    /*定义分析问题数据修改  由下方updateAnalysisPrimary方法优化得到*/
    @ApiOperation("分析问题数据修改")
    @PutMapping("updateAnalysis")
    //@MyLog(title = "修改分析问题数据", businessType = BusinessType.UPDATE)
    public boolean updateAnalysis(@RequestParam Long totalQuestionTableId,@RequestBody List<String> pojoList){
        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(totalQuestionTableId);
        /*根据 交换机问题实体类 获得命令集合和分析实体类集合*/
        HashMap<String, Object> scanLogicalEntityClass = getScanLogicalEntityClass(totalQuestionTable, loginUser);
        if (scanLogicalEntityClass.size() == 0){
            return false;
        }
        /* 获取两个实体类集合*/
        List<CommandLogic> commandLogicList = (List<CommandLogic>) scanLogicalEntityClass.get("CommandLogic");
        List<ProblemScanLogic> problemScanLogics = (List<ProblemScanLogic>) scanLogicalEntityClass.get("ProblemScanLogic");
        /* 获取两个实体类ID集合 */
        Set<String> commandLogicSet = commandLogicList.stream().map(pojo -> pojo.getId()).collect(Collectors.toSet());
        Set<String> problemScanLogicSet = problemScanLogics.stream().map(pojo -> pojo.getId()).collect(Collectors.toSet());

        for (String id:problemScanLogicSet){
            int j = problemScanLogicService.deleteProblemScanLogicById(id);
            if (j<=0){
                return false;
            }
        }
        for (String id:commandLogicSet){
            int i = commandLogicService.deleteCommandLogicById(id);
            if (i<=0){
                return false;
            }
        }

        boolean definitionProblemJsonPojo = definitionProblemJsonPojo(totalQuestionTableId,pojoList,loginUser);//jsonPojoList
        return definitionProblemJsonPojo;
    }



    /*定义分析问题数据修改 优化后得到上方 updateAnalysis方法 */
    //@MyLog(title = "修改分析问题数据", businessType = BusinessType.UPDATE)
    public boolean updateAnalysisPrimary (@RequestParam Long totalQuestionTableId,@RequestBody List<String> pojoList){
        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(totalQuestionTableId);
        AjaxResult analysisListTimeouts = getAnalysisListTimeouts(totalQuestionTable);
        if (!(analysisListTimeouts.get("msg").equals("操作成功"))){
            return false;
        }
        List<String> analysisList = (List<String>) analysisListTimeouts.get("data");
        /*如果 analysisList 为空则未定义 可直接插入
        * 如果 analysisList 不为空则未定义 则需要先删除*/
        if (analysisList != null || analysisList.size() != 0){
            List<String> jsonPojoList = new ArrayList<>();
            for (String analysis:analysisList){
                jsonPojoList.add(analysis.replaceAll("\"=\"","\":\""));
            }
            List<CommandLogic> commandLogicList = new ArrayList<>();
            List<ProblemScanLogic> problemScanLogicList = new ArrayList<>();
            for (int number=0;number<jsonPojoList.size();number++){
                // 如果 前端传输字符串  存在 command  说明 是命令
                if (jsonPojoList.get(number).indexOf("command")!=-1){
                    CommandLogic commandLogic = InspectionMethods.analysisCommandLogic(jsonPojoList.get(number));
                    commandLogicList.add(commandLogic);
                    continue;
                }else if (!(jsonPojoList.get(number).indexOf("command") !=-1)){
                    if (number+1<jsonPojoList.size()){
                        // 判断下一条是否是命令  因为 如果下一条是命令 则要 将 下一条分析ID 放入 命令ID
                        if (jsonPojoList.get(number+1).indexOf("command") !=-1){
                            //本条是分析 下一条是 命令
                            ProblemScanLogic problemScanLogic = InspectionMethods.analysisProblemScanLogic(jsonPojoList.get(number), "命令");
                            problemScanLogicList.add(problemScanLogic);
                            continue;
                        }else {
                            //本条是分析 下一条是 分析
                            ProblemScanLogic problemScanLogic = InspectionMethods.analysisProblemScanLogic(jsonPojoList.get(number), "分析");
                            problemScanLogicList.add(problemScanLogic);
                            continue;
                        }
                    }else {
                        //本条是分析 下一条是 问题
                        ProblemScanLogic problemScanLogic = InspectionMethods.analysisProblemScanLogic(jsonPojoList.get(number), "分析");
                        problemScanLogicList.add(problemScanLogic);
                        continue;
                    }
                }
            }

            //将相同ID  时间戳 的 实体类 放到一个实体
            //注释原因该方法是为了获取不重复的分析ID(将相同ID时间戳的实体类放到一个实体)，下方使用了HashSet也可以使ID不重复
            //List<ProblemScanLogic> problemScanLogics = definitionProblem(problemScanLogicList);
            HashSet<String> problemScanLogicSet = new HashSet<>();
            for (ProblemScanLogic problemScanLogic:problemScanLogicList){
                problemScanLogicSet.add(problemScanLogic.getId());
            }
            HashSet<String> commandLogicSet = new HashSet<>();
            for (CommandLogic commandLogic:commandLogicList){
                commandLogicSet.add(commandLogic.getId());
            }
            for (String id:problemScanLogicSet){
                int j = problemScanLogicService.deleteProblemScanLogicById(id);
                if (j<=0){
                    return false;
                }
            }
            for (String id:commandLogicSet){
                int i = commandLogicService.deleteCommandLogicById(id);
                if (i<=0){
                    return false;
                }
            }
        }
        boolean definitionProblemJsonPojo = definitionProblemJsonPojo(totalQuestionTableId,pojoList,loginUser);//jsonPojoList
        return definitionProblemJsonPojo;
    }


    /**
     *
     * @param basicInformationId
     * @param pojoList
     * @return
     */
    @ApiOperation("修改获取交换机基本信息逻辑")
    @PutMapping("updatebasicAnalysis")
    @MyLog(title = "修改获取交换机基本信息逻辑", businessType = BusinessType.UPDATE)
    public boolean updatebasicAnalysis(@RequestParam Long basicInformationId,@RequestBody List<String> pojoList){
        basicInformationService = SpringBeanUtil.getBean(IBasicInformationService.class);
        BasicInformation basicInformation = basicInformationService.selectBasicInformationById(basicInformationId);
        /*根据分析ID 获取 分析实体类集合*/
        /*因为是要删除 需要ID唯一 所以不需要拆分 */
        List<ProblemScanLogic> problemScanLogicList = problemScanLogicList(basicInformation.getProblemId(),SecurityUtils.getLoginUser());//commandLogic.getProblemId()
        String[] ids = problemScanLogicList.stream().map(p -> p.getId()).toArray(String[]::new);
        int j = problemScanLogicService.deleteProblemScanLogicByIds(ids);
        if (j<=0){
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        /*调用insertInformationAnalysisMethod方法，插入新的分析数据*/
        boolean insertInformationAnalysisMethod = insertInformationAnalysisMethod(loginUser,pojoList,basicInformationId);
        if (!insertInformationAnalysisMethod){
            return false;
        }
        return true;
    }


    /**
     * 回显获取交换机基本信息逻辑数据超时方法
     * @param problemId
     * @return
     */
    @ApiOperation("回显获取交换机基本信息逻辑数据")
    @GetMapping("getBasicInformationProblemScanLogic/{problemId}")
    @MyLog(title = "回显获取交换机基本信息逻辑数据", businessType = BusinessType.OTHER)
    public AjaxResult getBasicInformationProblemScanLogicTimeouts(@PathVariable String problemId) {
        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        final List<String>[] analysisList = new List[]{new ArrayList<>()};
        FutureTask future = new FutureTask(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                analysisList[0] = getBasicInformationProblemScanLogic(problemId,loginUser);
                return analysisList[0];
            }
        });
        executor.execute(future);
        try {
            List<String> result = (List<String>) future.get(60000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(loginUser.getUsername(),"风险："+"回显获取交换机基本信息逻辑数据超时\r\n");
            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFile("风险："+"回显获取交换机基本信息逻辑数据超时\r\n");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return AjaxResult.error("查询超时");
        }finally{
            future.cancel(true);
            executor.shutdown();
        }
        List<String> List = analysisList[0];
        List<String> strings = new ArrayList<>();
        for (String stringList:List){
            if (stringList != null && !(stringList.equals("null"))){
                strings.add(stringList);
            }
        }
        return AjaxResult.success(strings);
    }

    /**
     * 回显获取交换机基本信息逻辑数据
     * @param problemId 首分析DI
     * @param loginUser
     * @return
     */
    public  List<String>  getBasicInformationProblemScanLogic(String problemId,LoginUser loginUser) {
        //loginUser 登陆人信息
        //problemId 分析ID
        /*根据分析ID 获取 分析实体类集合*/
        /* 因为要返回前端信息 成功和失败分为两行 所以 拆分 true false */
        List<ProblemScanLogic> problemScanLogicList = problemScanLogicList(problemId,loginUser);//commandLogic.getProblemId()
        if (problemScanLogicList.size() ==0 ){
            return new ArrayList<>();
        }
        problemScanLogicList = InspectionMethods.splitSuccessFailureLogic(problemScanLogicList);
        /*行号:实体类*/
        HashMap<Long,String> hashMap = new HashMap<>();
        for (ProblemScanLogic problemScanLogic:problemScanLogicList){
            /*因为获取交换机基本信息，故没有 问题ID 为null*/
            String problemScanLogicString = InspectionMethods.problemScanLogicSting(problemScanLogic,null);
            String[] problemScanLogicStringsplit = problemScanLogicString.split("=:=");
            /*problemScanLogicStringsplit[0] 行号*/
            hashMap.put(Integer.valueOf(problemScanLogicStringsplit[0]).longValue(),problemScanLogicStringsplit[1]);
        }
        /*根据行号排序，创建成新的集合 并返回*/
        List<String> stringList = new ArrayList<>();
        for (Long number=0L;number<hashMap.size();number++){
            stringList.add(hashMap.get(number+1));
        }
        return stringList;
    }


    /**
     * 删除获取交换机基本信息逻辑数据
     * @param id
     * @return
     */
    @ApiOperation("删除获取交换机基本信息逻辑数据")
    @DeleteMapping("deleteBasicInformationProblemScanLogic")
    @MyLog(title = "删除获取交换机基本信息逻辑数据", businessType = BusinessType.DELETE)
    public boolean deleteBasicInformationProblemScanLogic(@RequestBody Long id) {
        basicInformationService = SpringBeanUtil.getBean(IBasicInformationService.class);
        BasicInformation basicInformation = basicInformationService.selectBasicInformationById(id);
        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String problemId = null ;
        if (basicInformation.getProblemId() == null){
            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(loginUser.getUsername(),"风险："+"获取交换机基本信息分析逻辑未定义\r\n");
            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFile("风险："+"获取交换机基本信息分析逻辑未定义\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }else {
            problemId = basicInformation.getProblemId();
        }

        //loginUser 登陆人信息  problemId 分析ID
        /*根据分析ID 获取 分析实体类集合 不用拆分 true false*/
        DefinitionProblemController definitionProblemController = new DefinitionProblemController();
        List<ProblemScanLogic> problemScanLogicList = definitionProblemController.problemScanLogicList(problemId,loginUser);//commandLogic.getProblemId()
        if (problemScanLogicList.size() ==0 ){
            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(loginUser.getUsername(),"风险："+"获取交换机基本信息分析逻辑为空\r\n");
            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFile("风险："+"获取交换机基本信息分析逻辑为空\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        /*转化为数组 并 删除*/
        String[] arr = problemScanLogicList.stream().map(p -> p.getId()).toArray(String[]::new);
        problemScanLogicService = SpringBeanUtil.getBean(IProblemScanLogicService.class);
        int i = problemScanLogicService.deleteProblemScanLogicByIds(arr);
        if (i<=0){
            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(loginUser.getUsername(),"风险："+"获取交换机基本信息分析逻辑删除失败\r\n");
            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFile("风险："+"获取交换机基本信息分析逻辑删除失败\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }else {
            int j = basicInformationService.deleteBasicInformationById(id);
            if (j<=0){
                //传输登陆人姓名 及问题简述
                WebSocketService.sendMessage(loginUser.getUsername(),"风险："+"获取交换机基本信息命令删除失败\r\n");
                try {
                    //插入问题简述及问题路径
                    PathHelper.writeDataToFile("风险："+"获取交换机基本信息命令删除失败\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
            return true;
        }
    }
}