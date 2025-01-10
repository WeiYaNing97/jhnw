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
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.MyUtils;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.*;
import com.sgcc.sql.util.InspectionMethods;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


/**
 * @date 2022年03月22日 11:17
 */
@Api(tags = "定义交换机信息分析接口")
@RestController
@RequestMapping("/sql/DefinitionProblemController")
@Transactional(rollbackFor = Exception.class)
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
     * 删除扫描逻辑数据
     *
     * @param id 扫描逻辑数据的ID
     * @return 删除是否成功，成功返回true，失败返回false
     */
    @DeleteMapping("deleteScanningLogic")
    @ApiOperation("删除交换机问题分析逻辑数据")
    @ApiImplicitParam(name = "id", value = "交换机问题的ID", dataTypeClass = String.class, required = true)
    @MyLog(title = "删除交换机问题分析逻辑数据", businessType = BusinessType.DELETE)
    public boolean deleteScanningLogic(@RequestBody String id) {
        // 获取总问题表服务
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        // 根据ID查询总问题表
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(id);
        // 获取登录用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 获取扫描逻辑实体类
        HashMap<String, Object> scanLogicalEntityClass = getScanLogicalEntityClass(totalQuestionTable, loginUser);
        // 获取命令逻辑列表
        List<CommandLogic> commandLogicList = (List<CommandLogic>) scanLogicalEntityClass.get("CommandLogic");
        // 获取问题扫描逻辑列表
        List<ProblemScanLogic> problemScanLogics = (List<ProblemScanLogic>) scanLogicalEntityClass.get("ProblemScanLogic");
        // 提取命令逻辑ID列表
        String[] commandLogicId = commandLogicList.stream().map(p -> p.getId()).distinct().toArray(String[]::new);
        // 提取问题扫描逻辑ID列表
        String[] problemScanLogicId = problemScanLogics.stream().map(p -> p.getId()).distinct().toArray(String[]::new);
        int deleteCommandLogicByIds = 1;
        // 如果存在命令逻辑
        if (commandLogicId.length>0){
            // 获取命令逻辑服务
            commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
            // 删除命令逻辑
            deleteCommandLogicByIds = commandLogicService.deleteCommandLogicByIds(commandLogicId);
        }
        // 如果命令逻辑删除成功且存在问题扫描逻辑
        if (deleteCommandLogicByIds >0 && problemScanLogicId.length >0){
            // 获取问题扫描逻辑服务
            problemScanLogicService = SpringBeanUtil.getBean(IProblemScanLogicService.class);
            // 删除问题扫描逻辑
            int deleteProblemScanLogicByIds = problemScanLogicService.deleteProblemScanLogicByIds(problemScanLogicId);
            // 如果问题扫描逻辑删除成功
            if (deleteProblemScanLogicByIds>0){
                // 将总问题表的逻辑ID置为空
                totalQuestionTable.setLogicalID(null);
                // 更新总问题表
                int updateQuestionTableById = totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable);
                // 如果更新成功，则返回true
                if (updateQuestionTableById>0){
                    return true;
                }else {
                    // 发送风险提示：扫描交换机问题表数据删除失败
                    AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                            "风险:扫描交换机问题表数据删除失败\r\n");
                }
            }else {
                // 发送风险提示：扫描交换机问题分析逻辑删除失败
                AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                        "风险:扫描交换机问题分析逻辑删除失败\r\n");
            }
            // 只有存在命令逻辑没有分析逻辑
        } else if (deleteCommandLogicByIds >0  && problemScanLogicId.length == 0){
            // 将总问题表的逻辑ID置为空
            totalQuestionTable.setLogicalID(null);
            // 更新总问题表
            int updateQuestionTableById = totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable);
            // 如果更新成功，则返回true
            if (updateQuestionTableById>0){
                return true;
            }else {
                // 发送风险提示：扫描交换机问题表数据删除失败
                AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                        "风险:扫描交换机问题表数据删除失败\r\n");
            }
            // 命令逻辑删除失败
        } else {
            // 发送风险提示：扫描交换机问题命令删除失败
            AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                    "风险:扫描交换机问题命令删除失败\r\n");
        }
        // 默认返回false
        return false;
    }
    /*定义分析问题数据修改
     * 实现逻辑是，在查询功能中提取查询方法，加上删除与添加功能 实现*/
    @ApiOperation("修改交换机问题分析逻辑数据")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "totalQuestionTableId", value = "交换机问题的ID", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "pojoList", value = "交换机问题分析逻辑数据json列表", dataTypeClass = List.class, required = true)
    })
    @PutMapping("updateAnalysis")
    @MyLog(title = "修改交换机问题分析逻辑数据", businessType = BusinessType.UPDATE)
    public boolean updateAnalysis(@RequestParam String totalQuestionTableId,@RequestBody List<String> pojoList){
        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();

        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(totalQuestionTableId);

        /** 根据 交换机问题实体类
         获得命令集合和分析实体类集合*/
        HashMap<String, Object> scanLogicalEntityClass = getScanLogicalEntityClass(totalQuestionTable, loginUser);
        if (scanLogicalEntityClass.size() == 0){
            return false;
        }

        /* 获取两个实体类集合*/
        List<ProblemScanLogic> problemScanLogics = (List<ProblemScanLogic>) scanLogicalEntityClass.get("ProblemScanLogic");

        /* 获取两个实体类ID集合 */
        Set<String> problemScanLogicSet = problemScanLogics.stream().map(pojo -> pojo.getId()).collect(Collectors.toSet());
        for (String id:problemScanLogicSet){
            int j = problemScanLogicService.deleteProblemScanLogicById(id);
            if (j<=0){
                return false;
            }
        }

        List<CommandLogic> commandLogicList = (List<CommandLogic>) scanLogicalEntityClass.get("CommandLogic");
        Set<String> commandLogicSet = commandLogicList.stream().map(pojo -> pojo.getId()).collect(Collectors.toSet());
        for (String id:commandLogicSet){
            int i = commandLogicService.deleteCommandLogicById(id);
            if (i<=0){
                return false;
            }
        }

        for (int i=0 ;i<pojoList.size();i++){
            if (pojoList.get(i).indexOf("undefined") != -1){
                pojoList.set(i,pojoList.get(i).replace("undefined","null"));
            }
        }

        boolean definitionProblemJsonPojo = definitionProblemJsonPojo(totalQuestionTableId,pojoList,loginUser);//jsonPojoList
        return definitionProblemJsonPojo;
    }

    /**
     * 定义获取交换机基本信息命令插入的方法
     *
     * @param jsonPojoList 交换机基本信息json列表
     * @param command      交换机基本信息命令数组
     * @param custom       自定义参数
     * @return 插入成功返回true，否则返回false
     */
    @ApiOperation("定义获取交换机基本信息命令")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "jsonPojoList", value = "获取交换机基本信息逻辑json列表", dataTypeClass = List.class, required = true),
            @ApiImplicitParam(name = "command", value = "交换机基本信息命令数组", dataTypeClass = Array.class, required = true),
            @ApiImplicitParam(name = "custom", value = "自定义参数", dataTypeClass = String.class, required = true)
    })
    @PostMapping("insertInformationAnalysis/{command}/{custom}")
    @MyLog(title = "定义获取基本信息分析数据插入", businessType = BusinessType.INSERT)
    public boolean insertInformationAnalysis(@RequestBody List<String> jsonPojoList,@PathVariable String[] command,@PathVariable String custom){
        // 将自定义参数转为字符串数组
        custom = "["+custom+"]";
        String comands = "";

        // 获取自定义分隔符
        String customDelimiter = null;
        Object customDelimiterObject = CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());
        if (customDelimiterObject != null && customDelimiterObject instanceof String){
            customDelimiter = (String) customDelimiterObject;
        }

        // 拼接命令字符串
        for (int num = 0 ;num < command.length; num++){
            comands = comands + command[num] + customDelimiter ;
        }

        // 去除最后一个分隔符，并添加自定义参数
        comands = comands.substring(0,  comands.length() - customDelimiter.length()  ) + custom;

        // 获取系统登录人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();

        // 创建BasicInformation实体类
        BasicInformation basicInformation = new BasicInformation();

        // 设置交换机基本信息命令
        basicInformation.setCommand(comands);

        // 获取BasicInformationService的Bean实例
        basicInformationService = SpringBeanUtil.getBean(IBasicInformationService.class);

        // 调用插入交换机基本信息的方法，返回结果保存在变量i中
        int i = basicInformationService.insertBasicInformation(basicInformation);

        // 如果插入失败（i<=0) 则返回false
        if (i<=0){
            // 发送告警信息，包含登录人姓名和问题简述
            //传输登陆人姓名 及问题简述
            AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                    "错误:获取交换机基本信息命令插入失败\r\n");
            return false;
        }

        // 获取插入的交换机基本信息的ID
        Long id = basicInformation.getId();

        // 调用插入交换机信息分析的方法，返回结果保存在变量insertInformationAnalysisMethod中
        boolean insertInformationAnalysisMethod = insertInformationAnalysisMethod(loginUser,jsonPojoList,id);

        // 返回插入交换机信息分析的结果
        return insertInformationAnalysisMethod;
    }

    /**
     * @param basicInformationId
     * @param pojoList
     * @return
     */
    @ApiOperation("修改获取交换机基本信息逻辑")
    @PutMapping("updatebasicAnalysis")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "basicInformationId", value = "交换机基本信息ID", dataTypeClass = Long.class, required = true),
            @ApiImplicitParam(name = "pojoList", value = "获取交换机基本信息逻辑json列表", dataTypeClass = List.class, required = true)
    })
    @MyLog(title = "修改获取交换机基本信息逻辑", businessType = BusinessType.UPDATE)
    public boolean updatebasicAnalysis(@RequestParam Long basicInformationId,@RequestBody List<String> pojoList){
        basicInformationService = SpringBeanUtil.getBean(IBasicInformationService.class);
        BasicInformation basicInformation = basicInformationService.selectBasicInformationById(basicInformationId);

        /*根据分析ID 获取 分析实体类集合*/
        /*因为是要删除 需要ID唯一 所以不需要拆分 */
        List<ProblemScanLogic> problemScanLogicList = problemScanLogicList(basicInformation.getProblemId(),SecurityUtils.getLoginUser());//commandLogic.getProblemId()

        /* 因为ID唯一 所以不用去重 */
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
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "problemId", value = "分析逻辑ID", dataTypeClass = String.class, required = true)
    })
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
            AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                    "风险:回显获取交换机基本信息逻辑数据超时\r\n");

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
     * 删除获取交换机基本信息逻辑数据
     * @param id
     * @return
     */
    @ApiOperation("删除获取交换机基本信息逻辑数据")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键ID", dataTypeClass = Long.class, required = true)
    })
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
            AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                    "风险:获取交换机基本信息分析逻辑未定义\r\n");

            return false;
        }else {
            problemId = basicInformation.getProblemId();
        }

        //loginUser 登陆人信息  problemId 分析ID
        /*根据分析ID 获取 分析实体类集合 不用拆分 true false*/
        DefinitionProblemController definitionProblemController = new DefinitionProblemController();
        List<ProblemScanLogic> problemScanLogicList = definitionProblemController.problemScanLogicList(problemId,loginUser);//commandLogic.getProblemId()
        if (problemScanLogicList.size() == 0 ){
            //传输登陆人姓名 及问题简述
            AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                    "风险:获取交换机基本信息分析逻辑为空\r\n");
            return false;
        }

        /*转化为数组 并 删除*/
        String[] arr = problemScanLogicList.stream().map(p -> p.getId()).toArray(String[]::new);
        problemScanLogicService = SpringBeanUtil.getBean(IProblemScanLogicService.class);
        int i = problemScanLogicService.deleteProblemScanLogicByIds(arr);
        if (i<=0){
            //传输登陆人姓名 及问题简述

            AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                    "风险:获取交换机基本信息分析逻辑删除失败\r\n");

            return false;
        }else {
            int j = basicInformationService.deleteBasicInformationById(id);
            if (j<=0){
                //传输登陆人姓名 及问题简述
                AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                        "风险:获取交换机基本信息命令删除失败\r\n");

                return false;
            }
            return true;
        }

    }

    /**
     * 插入分析问题的数据
     *
     * @param totalQuestionTableId 交换机问题实体类的ID
     * @param jsonPojoList         分析问题的JSON对象列表
     * @return 插入是否成功，成功返回true，否则返回false
     */
    @PostMapping("definitionProblemJsonPojo")
    @MyLog(title = "新增交换机问题分析逻辑数据", businessType = BusinessType.UPDATE)
    @ApiOperation("新增交换机问题分析逻辑数据")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "totalQuestionTableId", value = "交换机问题ID", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "jsonPojoList", value = "问题分析逻辑的JSON对象列表", dataTypeClass = List.class, required = true)
    })
    public boolean definitionProblemJson(@RequestParam String totalQuestionTableId,@RequestBody List<String> jsonPojoList){
        /**
         * 1：获取系统登录人信息
         * */
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 调用definitionProblemJsonPojo方法，传入交换机问题实体类的ID、分析问题的JSON对象列表和登录人信息
        // 并将返回的结果赋值给definitionProblemJsonboolean变量
        boolean definitionProblemJsonboolean = definitionProblemJsonPojo(totalQuestionTableId,jsonPojoList,loginUser);
        // 返回definitionProblemJsonboolean变量的值
        return definitionProblemJsonboolean;
    }

    /**
     * 根据交换机问题实体类查询问题分析逻辑数据
     *
     * @param totalQuestionTable 交换机问题实体类
     * @return java.util.List<java.lang.String> 返回一个字符串列表，表示问题分析逻辑数据
     */
    @ApiOperation("查询交换机问题分析逻辑数据")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "brand", value = "品牌", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "type", value = "型号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "firewareVersion", value = "内部固件版本", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "subVersion", value = "子版本号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "notFinished", value = "未完成标志", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "logicalID", value = "扫描索引", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "typeProblem", value = "范式分类", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "temProName", value = "范式名称", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "problemName", value = "自定义名称", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "problemDescribeId", value = "问题详细说明和指导索引", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "problemSolvingId", value = "解决问题命令索引", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "remarks", value = "问题备注", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "requiredItems", value = "是否必扫", dataTypeClass = Long.class, required = true)

    })
    @GetMapping("getAnalysisList")
    @MyLog(title = "查询交换机问题分析逻辑数据", businessType = BusinessType.OTHER)
    public AjaxResult getAnalysisListTimeouts(TotalQuestionTable totalQuestionTable) {
        /**
         * 1：获取系统登录人信息
         * */
        LoginUser loginUser = SecurityUtils.getLoginUser();

        /**
         * 2：创建一个单线程执行器，用于超时控制。
         *      这里使用了单线程执行器，因为不需要并行处理多个任务，只需要等待一个任务的完成即可
         *      只想任务 方法：getAnalysisList
         */
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // 创建一个数组，用于存储返回的字符串列表
        final List<String>[] analysisList = new List[]{new ArrayList<>()};

        // 创建一个异步任务，用于执行获取问题分析逻辑数据的操作
        FutureTask future = new FutureTask(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                // 获取分析列表 : 调用获取问题分析逻辑数据的方法，并将结果存储在数组中
                analysisList[0] = getAnalysisList(totalQuestionTable,loginUser);
                return analysisList[0];
            }
        });

        // 执行异步任务
        executor.execute(future);

        try {
            /**
             * 3：设置超时时间，默认为1000毫秒（即1秒）。
             *    这里使用了自定义配置工具类CustomConfigurationUtil来获取超时时间，如果配置文件中没有设置，则使用默认值1000毫秒。
             */
            Integer maximumTimeoutString = 1000;
            Object  maximumTimeoutObject = CustomConfigurationUtil.getValue("configuration.maximumTimeout", Constant.getProfileInformation());
            if (maximumTimeoutObject !=null && maximumTimeoutObject instanceof Integer){
                maximumTimeoutString = (Integer) maximumTimeoutObject;
            }

            // 获取异步任务的执行结果，并设置超时时间
            List<String> result = (List<String>) future.get(Long.valueOf(maximumTimeoutString).longValue(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            // 如果发生超时异常，返回查询超时的错误结果
            return AjaxResult.error("查询超时");
        } finally {
            // 取消异步任务
            future.cancel(true);

            // 关闭执行器，释放资源
            /* 关闭连接 */
            executor.shutdown();
        }

        // 返回成功的结果，并带上获取到的问题分析逻辑数据
        return AjaxResult.success(analysisList[0]);
    }


    /**
    * @Description  定义获取基本信息分析数据插入
    * @author charles
    * @createTime 2024/5/30 16:03
    * @desc
    * @param loginUser	 程序登录信息
     * @param jsonPojoList	命令及分析逻辑 字符串集合
     * @param basicInformationId	问题表数据ID
     * @return
    */
    public boolean insertInformationAnalysisMethod(LoginUser loginUser,@RequestBody List<String> jsonPojoList,Long basicInformationId){//@RequestBody List<String> jsonPojoList

        if (jsonPojoList.size() == 0){
            //传输登陆人姓名 及问题简述
            AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                    "错误:获取交换机基本信息分析数据非法为空\r\n");
        }

        System.err.println("定义获取基本信息分析数据插入:\r\n");
        for (String jsonPojo:jsonPojoList){
            System.err.println(jsonPojo);
        }

        // 命令集合 和 分析逻辑集合
        List<CommandLogic> commandLogicList = new ArrayList<>();
        List<ProblemScanLogic> problemScanLogicList = new ArrayList<>();
        // 问题表数据ID  截取前8位 四位问题编码和四位地区编码
        String problem_area_code = (basicInformationId + "").substring(0, 8);

        /*遍历分析数据
        * 如果分析数据中含有command 则 是命令 则 进入 String 转 命令实体类方法
        * 如果分析数据中不含有command 则 是分析 则 进入 String 转 分析实体类方法*/
        for (int number = 0; number < jsonPojoList.size(); number++){

            boolean isCommand = false;
            /** 判断下一条是否是命令  因为 如果下一条是命令 则要 将 下一条分析ID 放入 命令ID
             * 获取下一ID 判断下一ID对应的数据 是否包含 command
             * 如果此方法不能实现，则获取下一元素 判断是否包含 command  但是此方法有风险。因为下一条要执行的数据 不一定是集合的下一个元素。*/
            String[] split = jsonPojoList.get(number).split("\"nextIndex\":");
            if (split.length == 2){
                String firstNumberFromString = MyUtils.getEncodingID(split[1]);
                for (String jsonPojo:jsonPojoList){
                    if ((jsonPojo.indexOf("\"onlyIndex\":" + firstNumberFromString)!=-1 || jsonPojo.indexOf("\"onlyIndex\":\"" + firstNumberFromString)!=-1 )
                            &&jsonPojo.indexOf("command") !=-1){
                        isCommand = true;
                        /*System.err.println(" id符合，且包含 command");*/
                    }
                }
            }else if (number+1<jsonPojoList.size() && jsonPojoList.get(number+1).indexOf("command") !=-1){
                /*System.err.println(" 集合下一元素 包含 command ");*/
                isCommand = true;
            }
            // 如果 前端传输字符串  存在 command  说明 是命令数据
            if (jsonPojoList.get(number).indexOf("command")!=-1){

                if ( isCommand  ){
                    // 命令数据解析才成为命令实体 并添加到命令集合中
                    CommandLogic commandLogic = InspectionMethods.analysisCommandLogic(problem_area_code, jsonPojoList.get(number),"命令");
                    commandLogicList.add(commandLogic);
                    continue;
                }else {
                    // 命令数据解析才成为命令实体 并添加到命令集合中
                    CommandLogic commandLogic = InspectionMethods.analysisCommandLogic(problem_area_code, jsonPojoList.get(number),"分析");
                    commandLogicList.add(commandLogic);
                    continue;
                }
            }else if (!(jsonPojoList.get(number).indexOf("command") !=-1)){
                /*如果分析数据中不含有command 则 是分析 则 进入 String 转 分析实体类方法
                * 如果当前集合元素是分析 则 需要考虑下一集合元素是 命令 还是分析
                * 如果是命令 则 在 Sting 转 分析实体类中 传入 命令属性值
                * 如果是分析 则 在 Sting 转 分析实体类中 传入 分析属性值
                * 这会影响到 前端数据传入的 下一ID 的 赋值问题*/
                if (number+1<jsonPojoList.size()){
                    // 判断下一条是否是命令  因为 如果下一条是命令 则要 将 下一条分析ID 放入 命令ID
                    if ( isCommand  ){
                        //本条是分析 下一条是 命令
                        /*ProblemScanLogic problemScanLogic = InspectionMethods.analysisProblemScanLogic(jsonPojoList.get(number), "命令");
                        problemScanLogicList.add(problemScanLogic);*/
                        AnalyzeConvertJson analyzeConvertJson = getAnalyzeConvertJson(problem_area_code, jsonPojoList.get(number), "命令");
                        ProblemScanLogic problemScanLogic = new ProblemScanLogic();
                        problemScanLogic = (ProblemScanLogic) copyProperties( analyzeConvertJson , problemScanLogic);
                        problemScanLogicList.add(problemScanLogic);
                        continue;
                    }else {
                        //本条是分析 下一条是 分析
                        /*ProblemScanLogic problemScanLogic = InspectionMethods.analysisProblemScanLogic(jsonPojoList.get(number), "分析");
                        problemScanLogicList.add(problemScanLogic);*/
                        AnalyzeConvertJson analyzeConvertJson = getAnalyzeConvertJson(problem_area_code, jsonPojoList.get(number), "分析");
                        ProblemScanLogic problemScanLogic = new ProblemScanLogic();
                        problemScanLogic = (ProblemScanLogic) copyProperties( analyzeConvertJson , problemScanLogic);
                        problemScanLogicList.add(problemScanLogic);
                        continue;
                    }
                }else {
                    //本条是分析 下一条是 问题
                    /*ProblemScanLogic problemScanLogic = InspectionMethods.analysisProblemScanLogic(jsonPojoList.get(number), "分析");
                    problemScanLogicList.add(problemScanLogic);*/
                    AnalyzeConvertJson analyzeConvertJson = getAnalyzeConvertJson(problem_area_code, jsonPojoList.get(number), "分析");
                    ProblemScanLogic problemScanLogic = new ProblemScanLogic();
                    problemScanLogic = (ProblemScanLogic) copyProperties( analyzeConvertJson , problemScanLogic);
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

                AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                        "错误:获取交换机基本信息分析数据插入失败\r\n");

                return false;
            }
        }

        /*获取交换机基本信息第一条数据为ID 需要传送给 获取交换机基本信息命令的分析ID*/
        String jsonPojoOne = jsonPojoList.get(0);

        /*ProblemScanLogic problemScanLogic = InspectionMethods.analysisProblemScanLogic(jsonPojoOne, "分析");*/
        AnalyzeConvertJson analyzeConvertJson = getAnalyzeConvertJson( problem_area_code , jsonPojoOne, "分析");
        ProblemScanLogic problemScanLogic = new ProblemScanLogic();
        problemScanLogic = (ProblemScanLogic) copyProperties( analyzeConvertJson , problemScanLogic);
        BasicInformation basicInformation = basicInformationService.selectBasicInformationById(basicInformationId);
        basicInformation.setProblemId(problemScanLogic.getId());
        int i = basicInformationService.updateBasicInformation(basicInformation);
        if (i<=0){
            AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                    "错误:获取交换机基本信息命令的ProblemId字段失败\r\n");
            return false;
        }
        return true;
    }

    /**
    * @Description 插入分析问题的数据
    * @desc
    * @param totalQuestionTableId
     * @param jsonPojoList
     * @param loginUser
     * @return
    */
    public boolean definitionProblemJsonPojo(String totalQuestionTableId,@RequestBody List<String> jsonPojoList,LoginUser loginUser){
        // /*判断问题分析逻辑是否为空 传输登陆人姓名 及问题简述*/
       if (jsonPojoList.size() == 0){
           AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                   "错误:扫描分析数据非法为空\r\n");
           return false;
       }

        System.err.println("\r\n"+"前端出入数据：\r\n");
        for (String jsonPojo:jsonPojoList){
            System.err.println(jsonPojo);
        }

        List<CommandLogic> commandLogicList = new ArrayList<>();
        List<ProblemScanLogic> problemScanLogicList = new ArrayList<>();
        String problem_area_code = totalQuestionTableId.substring(0, 8);

        /*遍历数据 属于命令还是分析数据*/
        for (int number=0;number<jsonPojoList.size();number++){
            boolean isCommand = false;
            /** 判断下一条是否是命令  因为 如果下一条是命令 则要 将 下一条分析ID 放入 命令ID
             * 获取下一ID 判断下一ID对应的数据 是否包含 command
             * 如果此方法不能实现，则获取下一元素 判断是否包含 command  但是此方法有风险。因为下一条要执行的数据 不一定是集合的下一个元素。
             *
             * todo :也可以通过判断是否   "targetType":"command"
             * */
            String[] split = jsonPojoList.get(number).split("\"nextIndex\":");
            if (split.length == 2){
                // 使用正则表达式,在Java中获取字符串第一个数值数字部分。
                String firstNumberFromString = MyUtils.getEncodingID(split[1]);
                for (String jsonPojo:jsonPojoList){
                    if ((jsonPojo.indexOf("\"onlyIndex\":" + firstNumberFromString)!=-1
                      || jsonPojo.indexOf("\"onlyIndex\":\"" + firstNumberFromString)!=-1 )
                      && jsonPojo.indexOf("\"targetType\":\"command\"") !=-1){
                        isCommand = true;
                        System.err.println(" id符合，且包含 command ");
                    }
                }
            }else if ( number+1<jsonPojoList.size() && jsonPojoList.get(number+1).indexOf("\"targetType\":\"command\"") !=-1){
                System.err.println(" 集合下一元素 包含 command ");
                isCommand = true;
            }


            // 如果 前端传输字符串  存在 command  说明 是命令
            if (jsonPojoList.get(number).indexOf("\"targetType\":\"command\"")!=-1){
                /**
                 * 转变为命令实体类
                 */
                if (isCommand){
                    CommandLogic commandLogic = InspectionMethods.analysisCommandLogic(problem_area_code,jsonPojoList.get(number),"命令");
                    commandLogicList.add(commandLogic);
                    continue;
                }else {
                    CommandLogic commandLogic = InspectionMethods.analysisCommandLogic(problem_area_code,jsonPojoList.get(number),"分析");
                    commandLogicList.add(commandLogic);
                    continue;
                }
            }else if (!(jsonPojoList.get(number).indexOf("\"targetType\":\"command\"") !=-1)
                    && !(jsonPojoList.get(number).indexOf("method") !=-1)){//!(jsonPojoList.get(number).indexOf("method") !=-1)    什么原因？？？？？
                /**
                 * 转变为分析实体类
                 */
                /*当数据不为集合的最后一个元素时
                * 需要判断 下一条数据是否为 命令 如果是命令 则 分析数据应该标明下一条为命令表数据*/
                if (number+1<jsonPojoList.size()){
                    if (isCommand){
                        //本条是分析 下一条是 命令
                        AnalyzeConvertJson analyzeConvertJson = getAnalyzeConvertJson(problem_area_code,jsonPojoList.get(number), "命令");
                        ProblemScanLogic pojo = new ProblemScanLogic();
                        pojo = (ProblemScanLogic) copyProperties( analyzeConvertJson , pojo);
                        problemScanLogicList.add(pojo);
                        continue;
                    }else {
                        //本条是分析 下一条是 分析
                        AnalyzeConvertJson analyzeConvertJson = getAnalyzeConvertJson(problem_area_code,jsonPojoList.get(number), "分析");
                        ProblemScanLogic pojo = new ProblemScanLogic();
                        pojo = (ProblemScanLogic) copyProperties( analyzeConvertJson , pojo);
                        problemScanLogicList.add(pojo);
                        continue;
                    }
                }else {
                    //本条是分析 下一条是 问题
                    AnalyzeConvertJson analyzeConvertJson = getAnalyzeConvertJson(problem_area_code,jsonPojoList.get(number), "分析");
                    ProblemScanLogic pojo = new ProblemScanLogic();
                    pojo = (ProblemScanLogic) copyProperties( analyzeConvertJson , pojo);
                    problemScanLogicList.add(pojo);
                    continue;
                }
            }
        }

        //将相同ID  时间戳 的 实体类 放到一个实体
        List<ProblemScanLogic> problemScanLogics = InspectionMethods.definitionProblem(problemScanLogicList);
        String commandId = null;
        for (ProblemScanLogic problemScanLogic:problemScanLogics){
            problemScanLogicService = SpringBeanUtil.getBean(IProblemScanLogicService.class);
            int i = problemScanLogicService.insertProblemScanLogic(problemScanLogic);
            if (i<=0){
                return false;
            }
        }

        /**
         * 获取问题分析中的第一条数据将对应ID 赋值给问题表逻辑ID字段
         */
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
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(totalQuestionTableId);
        /*赋值问题表数据的 逻辑ID  此时为命令ID*/
        totalQuestionTable.setLogicalID(commandId);
        int i = totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable);
        if (i<=0){
            return false;
        }
        return true;
    }

    /**
     * 复制对象的属性值到另一个对象中。
     *
     * @param source 源对象，包含要复制的属性值。
     * @param target 目标对象，要接收源对象的属性值。
     * @return 返回目标对象，该对象已包含源对象的属性值。
     * @throws IllegalAccessException 如果源对象或目标对象的属性不可访问。
     */
    public static Object copyProperties(Object source, Object target) {
        // 获取源对象的类
        Class<?> sourceClass = source.getClass();
        // 获取目标对象的类
        Class<?> targetClass = target.getClass();
        // 获取源对象的所有属性
        Field[] sourceFields = sourceClass.getDeclaredFields();
        // 获取目标对象的所有属性
        Field[] targetFields = targetClass.getDeclaredFields();
        // 遍历源对象的属性
        for (Field sourceField : sourceFields) {
            // 设置源对象的属性为可访问
            sourceField.setAccessible(true);
            // 遍历目标对象的属性
            for (Field targetField : targetFields) {
                // 设置目标对象的属性为可访问
                targetField.setAccessible(true);
                // 如果源对象的属性名与目标对象的属性名相同，并且属性类型也相同
                if (sourceField.getName().equals(targetField.getName()) && sourceField.getType().equals(targetField.getType())) {
                    try {
                        // 将源对象的属性值复制到目标对象的属性中
                        targetField.set(target, sourceField.get(source));
                    } catch (IllegalAccessException e) {
                        // 如果属性不可访问，则打印异常堆栈
                        e.printStackTrace();
                    }
                }
            }
        }
        // 返回目标对象
        return target;
    }

    /**
     * 获取分析列表
     * @param totalQuestionTable 交换机问题实体类
     * @param loginUser 登录用户
     * @return 包含分析结果的字符串列表
     */
    public  List<String> getAnalysisList(@RequestBody TotalQuestionTable totalQuestionTable, LoginUser loginUser){
        /**
         * 1: 根据交换机问题实体类获取命令集合和分析实体类集合
         */
        HashMap<String, Object> scanLogicalEntityClass = getScanLogicalEntityClass(totalQuestionTable, loginUser);
        // 如果获取的实体类集合为空，则直接返回空列表
        if (scanLogicalEntityClass.size() == 0){
            return new ArrayList<>();
        }

        // 用于存储命令和分析实体类集合的哈希表，key为行号，value为实体类字符串
        HashMap<Long, String> hashMap = new HashMap<>();

        // 获取配置文件中的自定义分隔符
        String customDelimiter = null;
        Object customDelimiterObject = CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());
        // 判断获取到的分隔符是否为字符串类型
        if (customDelimiterObject instanceof String){
            customDelimiter = (String) customDelimiterObject;
        }

        /**
         * 2: 获取命令实体类集合
         *    将实体类转变为字符串并存入哈希表
         */
        List<CommandLogic> commandLogicList = (List<CommandLogic>) scanLogicalEntityClass.get("CommandLogic");
        // 遍历命令逻辑实体类集合，将每个实体类转换为字符串，并存入哈希表
        for (CommandLogic commandLogic : commandLogicList){
            // 将命令逻辑实体类转换为字符串
            String commandLogicString = InspectionMethods.commandLogicString(commandLogic);
            // 使用自定义分隔符对字符串进行分割
            String[] commandLogicStringsplit = commandLogicString.split(customDelimiter);
            // 将行号和对应的字符串存入哈希表
            hashMap.put(Integer.valueOf(commandLogicStringsplit[0]).longValue(), commandLogicStringsplit[1]);
        }

        /**
         * 3: 获取分析实体类集合
         *    将实体类转变为字符串并存入哈希表
         */
        List<ProblemScanLogic> problemScanLogics = (List<ProblemScanLogic>) scanLogicalEntityClass.get("ProblemScanLogic");
        // 遍历问题扫描逻辑实体类集合，将每个实体类转换为字符串，并存入哈希表
        for (ProblemScanLogic problemScanLogic : problemScanLogics){
            // 将问题扫描逻辑实体类转换为字符串
            String problemScanLogicString = InspectionMethods.problemScanLogicSting(problemScanLogic, totalQuestionTable.getId());
            // 使用自定义分隔符对字符串进行分割
            String[] problemScanLogicStringsplit = problemScanLogicString.split(customDelimiter);
            // 将行号和对应的字符串存入哈希表
            hashMap.put(Integer.valueOf(problemScanLogicStringsplit[0]).longValue(), problemScanLogicStringsplit[1]);
        }

        // 将哈希表中的value集合转换为列表
        Collection<String> values = hashMap.values();
        List<String> stringList = new ArrayList<>(values);
        // 打印转换后的字符串列表
        for (String str : stringList){
            System.err.println(str);
        }
        // 返回转换后的字符串列表
        return stringList;
    }


    /**
     * 根据交换机问题实体类获取命令集合和分析实体类集合
     *
     * @param totalQuestionTable 交换机问题实体类
     * @param loginUser 登录用户
     * @return 包含命令集合和分析实体类集合的HashMap
     */
    public  HashMap<String,Object> getScanLogicalEntityClass(@RequestBody TotalQuestionTable totalQuestionTable,LoginUser loginUser) {
        /** 1: 判断逻辑ID 是否为空
         *     如果为空 则 返回 null
         *     取出命令 或 分析 字段
         */
        if (totalQuestionTable.getLogicalID() == null){
            return new HashMap<>();
        }
        String problemScanLogicID = totalQuestionTable.getLogicalID();

        /**
         * 2: 判断逻辑ID 是命令ID还是分析ID
         *    去除 "命令" 或 "分析"
         *    得到 命令表或分析表ID（存入分析集合）
         */
        List<String> problemIds = new ArrayList<>();//存储 分析ID集合
        List<String> commandIDs = new ArrayList<>();//存储 命令ID集合
        if (problemScanLogicID.indexOf("分析") != -1){
            problemIds.add(problemScanLogicID.replaceAll("分析",""));
        }else if (problemScanLogicID.indexOf("命令") != -1){
            commandIDs.add(problemScanLogicID.replaceAll("命令",""));
        }

        /**
         * 3: 根据 分析ID集合 和 命令ID集合 获取命令集合和分析实体类集合
         */
        HashMap<String, Object> problemIdsAndCommandIDs = getProblemIdsAndCommandIDs(problemIds, commandIDs, loginUser);
        return problemIdsAndCommandIDs;
    }

    /**
     * 根据提供的问题ID和命令ID，获取对应的问题实体类和命令实体类，并返回一个包含这些实体类的HashMap。
     *
     * @param problemIds 包含问题ID的列表
     * @param commandIDs 包含命令ID的列表
     * @param loginUser 当前登录用户
     * @return 包含命令实体类和问题实体类的HashMap，键为行号，值为对应的实体类对象
     */
    public HashMap<String,Object> getProblemIdsAndCommandIDs (List<String> problemIds,
                                            List<String> commandIDs,
                                            LoginUser loginUser) {
        List<CommandLogic> commandLogicList = new ArrayList<>();//命令实体类集合
        List<ProblemScanLogic> problemScanLogics = new ArrayList<>();//分析实体类集合
        HashMap<String,Object> hashMappojo = new HashMap<>();//存储方法返回数据
        do {
            if (problemIds.size() != 0){//如果分析ID集合不为空，则可以根据分析ID查询出，分析实体类集合
                List<ProblemScanLogic> problemScanLogicList = new ArrayList<>();//根据分析ID 查询出所有的数据条数
                for (String problemId:problemIds){
                    problemScanLogicList.addAll(problemScanLogicList(problemId,loginUser));//commandLogic.getProblemId()
                }
                /*实体类拆分true和false*/
                problemScanLogicList = InspectionMethods.splitSuccessFailureLogic(problemScanLogicList);


                /* 查询完数据 将分析ID置空 */
                problemIds = new ArrayList<>();
                if (problemScanLogicList.size() ==0 && commandIDs.size() == 0){
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
                    if (commandLogic == null){
                        continue;
                    }
                    if (commandLogic.getProblemId() == null &&
                        commandLogic.getEndIndex() == null &&
                        problemIds.size() == 0){
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
     * 根据分析ID获取分析实体类集合
     *
     * @param problemScanLogicID 分析ID
     * @param loginUser 登录用户信息
     * @return 返回分析实体类集合
     */
    //@RequestMapping("problemScanLogicList")
    public List<ProblemScanLogic> problemScanLogicList(String problemScanLogicID,LoginUser loginUser){
        /*预设跳出循环条件 为 false*/
        boolean contain = false;
        /*预设map key为分析表ID value为 分析表实体类*/
        Map<String,ProblemScanLogic> problemScanLogicHashMap = new HashMap<>();
        do {
            String  problemScanID = "";
            // 分析ID 根据“：” 分割 为 分析ID数组，
            // 因为分析逻辑 可能出现 true 和 false 多个ID情况
            String[] problemScanLogicIDsplit = problemScanLogicID.split(":");
            // 遍历分析ID数组
            for (String id:problemScanLogicIDsplit){
                // 根据分析ID 在 map中查询 分析实体类
                ProblemScanLogic pojo = problemScanLogicHashMap.get(id);
                if (pojo != null){
                    // 如果查询不为null
                    // 则 说明 map中存在 分析实体类，
                    // 需要进行 分析ID数组的 下一项 查询
                    //problemScanLogicHashMap.put(id,pojo);
                    continue;
                }
                // 如果查询为null
                // 则 说明 map中不存在 分析实体类，
                // 需要到数据库中查询，并放入 map中 存储
                // 如果查询结果为 null 则返回 查询结果为null
                problemScanLogicService = SpringBeanUtil.getBean(IProblemScanLogicService.class);
                ProblemScanLogic problemScanLogic = problemScanLogicService.selectProblemScanLogicById(id);
                if (problemScanLogic ==null){
                    // 传输登陆人姓名 及问题简述
                    AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                            "错误:根据ID："+id+"查询分析表数据失败,未查出对应ID数据\r\n");
                    // 返回空集合
                    return new ArrayList<>();
                }
                problemScanLogicHashMap.put(id,problemScanLogic);
                // 当循环ID不为空时，
                // 查询逻辑中分支中没有下一行ID，
                // 则进行下一元素的分析iD
                if (problemScanLogic.getCycleStartId()!=null && !(problemScanLogic.getCycleStartId().equals("null"))){
                    continue;
                }
                // 如果 正确 下一ID 不为空
                // 则 拼接到 分析表ID中
                if (problemScanLogic.gettNextId()!=null && !(problemScanLogic.gettNextId().equals("null"))
                        && problemScanLogic.gettNextId()!="" &&  !(MyUtils.isContainChinese(problemScanLogic.gettNextId()))){
                    problemScanID += problemScanLogic.gettNextId()+":";
                }
                // 如果 错误 下一ID 不为空
                // 则 拼接到 分析表ID中
                if (problemScanLogic.getfNextId()!=null && !(problemScanLogic.getfNextId().equals("null"))
                        && problemScanLogic.getfNextId()!="" &&  !(MyUtils.isContainChinese(problemScanLogic.getfNextId()))){
                    problemScanID += problemScanLogic.getfNextId()+":";
                }
            }
            // 如果没有ID 则 视为没有下一层 分析数据，
            // 则 退出 do while
            if (problemScanID.equals("")){
                break;
            }
            // 分割 分析ID 为数组，遍历数组 及 map，
            // 去除 map中存在的 分析ID
            String[] problemScanIDsplit = problemScanID.split(":");
            problemScanID = "";
            for (String id:problemScanIDsplit){
                if (problemScanLogicHashMap.get(id) == null){
                    problemScanID += id+":";
                }
            }
            // 如果problemScanID 不为 "" 则 contain = true，
            // 并去掉 problemScanID 最后一个 ：
            if (!(problemScanID.equals(""))){
                contain = true;
                problemScanLogicID = problemScanID.substring(0,problemScanID.length()-1);
            }else {
                contain = false;
            }
        }while (contain);
        // map中数据 存放到list集合中
        Collection<ProblemScanLogic> values = problemScanLogicHashMap.values();
        // 将Collection转换为List
        List<ProblemScanLogic> ProblemScanLogicList = new ArrayList<>(values);
        /*此时查询出了  数据库存储的 信息*/
        return ProblemScanLogicList;
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

        /*自定义分隔符*/
        String customDelimiter = null;
        Object customDelimiterObject = (Object) CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());
        if (customDelimiterObject != null && customDelimiterObject instanceof String){
            customDelimiter = (String) customDelimiterObject;
        }

        /*行号:实体类*/
        HashMap<Long,String> hashMap = new HashMap<>();
        for (ProblemScanLogic problemScanLogic:problemScanLogicList){
            /*因为获取交换机基本信息，故没有 问题ID 为null*/
            String problemScanLogicString = InspectionMethods.problemScanLogicSting(problemScanLogic,null);
            String[] problemScanLogicStringsplit = problemScanLogicString.split(customDelimiter);
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
     * 将给定的字符串转化为分析实体类。
     *
     * @param problem_area_code 问题编码与区域编码的字符串
     * @param information       分析数据的字符串，应为JSON格式
     * @param ifCommand         指示是命令还是分析操作的字符串，取值应为"命令"或"分析"
     * @return 转化后的分析实体类对象
     *
     * @author charles
     * @createTime 2024/5/30 15:56
     * @note 注意，该方法在处理analysis数据时，会删除所有的"null"字符串，并将空字符串("")的字段值设为null。
     *       此外，如果ifCommand为"命令"，则会对转化后的对象进行变形处理。
     */
    public static AnalyzeConvertJson getAnalyzeConvertJson(String problem_area_code,String information,String ifCommand) {
        /**
         *TODO 修改分析逻辑，前端提交的数据有问题  "length":"nullnull"
         * {"targetType":"takeword","onlyIndex":1716950612245,"trueFalse":"","checked":false,"action":"取词","position":0,"cursorRegion":"2","exhibit":"显示","wordName":"1","nextIndex":"SCRT00011716946238058","length":"nullnull","pageIndex":2}
         * {"targetType":"takeword","onlyIndex":1716950619965,"trueFalse":"","checked":false,"action":"取词","position":0,"cursorRegion":"2","exhibit":"显示","wordName":"2","nextIndex":"SCRT00011716946250825","length":"nullnull","pageIndex":4}
        */
        // 删除information中所有的"null"字符串
        information = information.replace("null","");

        /**
         * 1： 将提交的分析数据从Json格式转化为实体类对象
         * */
        AnalyzeConvertJson analyzeConvertJson = JSON.parseObject(information, AnalyzeConvertJson.class);

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
        analyzeConvertJson = deformation(problem_area_code,analyzeConvertJson, ifCommand);

        // 返回转化后的分析实体类对象
        return analyzeConvertJson;
    }

    /**
     * AnalyzeConvertJson对象进行变形处理。
     *
     * @param problem_area_code 问题编码与区域编码的字符串
     * @param analyzeConvertJson 分析数据对应的AnalyzeConvertJson对象
     * @param ifCommand 指示是命令还是分析操作的字符串
     * @return 变形处理后的AnalyzeConvertJson对象
     */
    public static AnalyzeConvertJson deformation(String problem_area_code,AnalyzeConvertJson analyzeConvertJson,String ifCommand) {

        // 行,列偏移   RelativePosition
        // 列偏移 目前无用 默认为0
        if (analyzeConvertJson.getPosition() == null){
            analyzeConvertJson.setPosition("0");
        }

        /* 行偏移
        * 当前行
        * 全文起始
        * 自定义行  相对于上一行来说*/
        if (analyzeConvertJson.getRelative() == null){
            analyzeConvertJson.setRelative("0");
        }

        /** 取词逻辑*/
        /* 取值逻辑中 取出的值是否可以显示 */
        if (analyzeConvertJson.getExhibit()!=null
                && analyzeConvertJson.getExhibit().equals("显示")){
            analyzeConvertJson.setExhibit("是");
        }else {
            analyzeConvertJson.setExhibit("否");
        }

        /** 匹配 、 取词 逻辑*/
        if (analyzeConvertJson.getMatched() != null
                || (analyzeConvertJson.getAction() != null && analyzeConvertJson.getAction().indexOf("取词")!=-1)){
            /** 行,列偏移 相对位置 */
            if (analyzeConvertJson.getRelative().indexOf("&")!=-1){
                /** present&full
                 * 位置 : 按行和全文
                 * 第一个参数为 从当前行匹配
                 * 第二个参数为 进行全文匹配*/
                String[] relatives = analyzeConvertJson.getRelative().split("&");
                analyzeConvertJson.setRelativePosition(relatives[0] +"," + analyzeConvertJson.getPosition());
            }else {

                /* 行偏移 */
                analyzeConvertJson.setRelativePosition(analyzeConvertJson.getRelative() +"," + analyzeConvertJson.getPosition());

            }
        }


        /*当 analyzeConvertJson.getCommand() 属性值为 null 时 则 下一条分析数据为命令
         *如果下一条分析数据为命令时 则 下一条IDtNextId  要赋值给 命令ID
         * 然后下一条ID tNextId 置空 null  */
        if (analyzeConvertJson.getTrueFalse() !=null && analyzeConvertJson.getTrueFalse() .equals("失败") && ifCommand.equals("命令")){

            /** true下一条命令索引 */
            analyzeConvertJson.setfComId(MyUtils.encodeID( analyzeConvertJson.getNextIndex())?analyzeConvertJson.getNextIndex() : problem_area_code + analyzeConvertJson.getNextIndex() );
            analyzeConvertJson.setfLine(analyzeConvertJson.getPageIndex());

        }else if (ifCommand.equals("命令")){
            /** true下一条命令索引 */
            analyzeConvertJson.settComId(MyUtils.encodeID( analyzeConvertJson.getNextIndex() )?analyzeConvertJson.getNextIndex() :  problem_area_code + analyzeConvertJson.getNextIndex() );
            analyzeConvertJson.settLine(analyzeConvertJson.getPageIndex());
        }

        //如果动作属性不为空  且动作属性参数为 有无问题时  需要清空动作属性
        if (analyzeConvertJson.getAction() != null && analyzeConvertJson.getAction().indexOf("问题")!=-1){
            //problemId字段 存放 有无问题 加 问题表数据ID
            //hashMap.get("WTNextId") 存放有无问题
            analyzeConvertJson.setProblemId( analyzeConvertJson.gettNextId());
            //清空动作属性
            analyzeConvertJson.setAction(null);
        }

        /*
         * 默认情况下 行号、下一条分析ID、下一条命令ID 是 成功对应的属性
         * 如果 trueFalse 为 失败时
         * 则 成功行号、成功下一条分析、成功下一条命令 都复制给 失败对应 属性
         */
        if (analyzeConvertJson.getTrueFalse() !=null && analyzeConvertJson.getTrueFalse() .equals("失败") && ifCommand.equals("分析")){

            //如果实体类是 失败 则 把默认成功数据 赋值给 失败数据
            analyzeConvertJson.setfLine(analyzeConvertJson.getPageIndex());
            analyzeConvertJson.setfNextId(MyUtils.encodeID( analyzeConvertJson.getNextIndex()) ? analyzeConvertJson.getNextIndex(): problem_area_code + analyzeConvertJson.getNextIndex());

            //把 默认成功数据 清除
            analyzeConvertJson.settComId(null);
            analyzeConvertJson.settLine(null);

        }else if (ifCommand.equals("分析")){
            analyzeConvertJson.settNextId(analyzeConvertJson.getNextIndex() == null? null : (MyUtils.encodeID( analyzeConvertJson.getNextIndex() )? analyzeConvertJson.getNextIndex() :  problem_area_code + analyzeConvertJson.getNextIndex()));
            analyzeConvertJson.settLine(analyzeConvertJson.getPageIndex());

            //把 默认成功数据 清除
            analyzeConvertJson.setfComId(null);
            analyzeConvertJson.setfLine(null);

        }


        //如果动作属性不为空  且动作属性参数为 循环时  需要清空动作属性
        if (analyzeConvertJson.getAction()!=null && analyzeConvertJson.getAction().equals("循环")){
            //需要清空动作属性
            analyzeConvertJson.setAction(null);
            analyzeConvertJson.settNextId(null);
        }
        //如果动作属性不为空  且动作属性参数为 比较时  需要清空动作属性
        if (analyzeConvertJson.getAction() !=null && analyzeConvertJson.getAction().equals("比较")){
            //清空动作属性
            analyzeConvertJson.setAction(null);
        }

        /** 动作 */
        if (analyzeConvertJson.getAction()!=null
                && analyzeConvertJson.getAction().equals("取词")
                && analyzeConvertJson.getCursorRegion()!=null
                && analyzeConvertJson.getMatched() ==null){
            switch (analyzeConvertJson.getCursorRegion()){
                case "1":
                    analyzeConvertJson.setAction(analyzeConvertJson.getAction() + "full");
                    break;
                case "0":
                    analyzeConvertJson.setAction(analyzeConvertJson.getAction());
                    break;
                case "2":
                    analyzeConvertJson.setAction(analyzeConvertJson.getAction()+"all");
            }
        }


        /** 主键索引 */
        analyzeConvertJson.setId( MyUtils.encodeID(analyzeConvertJson.getOnlyIndex() )? analyzeConvertJson.getOnlyIndex() : problem_area_code + analyzeConvertJson.getOnlyIndex());

        /** 匹配 */
        if (analyzeConvertJson.getMatched()!=null){
            /*精确匹配*/
            /*如果 relative  */
            if (analyzeConvertJson.getRelative().indexOf("&")!=-1){
                /* 位置 ：按行和全文*/
                String[] relatives = analyzeConvertJson.getRelative().split("&");
                analyzeConvertJson.setMatched(analyzeConvertJson.getMatched() + relatives[1]);
            }
        }

        if (analyzeConvertJson.getCycleStartId()!=null){
            analyzeConvertJson.setCycleStartId(MyUtils.encodeID( analyzeConvertJson.getCycleStartId() )? analyzeConvertJson.getCycleStartId() : problem_area_code + analyzeConvertJson.getCycleStartId());
        }
        return analyzeConvertJson;
    }
}