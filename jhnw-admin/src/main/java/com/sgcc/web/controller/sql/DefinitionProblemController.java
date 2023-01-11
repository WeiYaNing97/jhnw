package com.sgcc.web.controller.sql;

import cn.hutool.poi.excel.ExcelExtractorUtil;
import com.sgcc.common.annotation.Log;
import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.entity.SysUser;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.ServletUtils;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.connect.util.SpringBeanUtil;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.*;
import com.sgcc.system.service.ISysUserService;
import com.sgcc.web.controller.util.PathHelper;
import com.sgcc.web.controller.webSocket.WebSocketService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
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
    private static ICommandLogicService commandLogicService;
    @Autowired
    private static IProblemScanLogicService problemScanLogicService;
    @Autowired
    private static ITotalQuestionTableService totalQuestionTableService;
    @Autowired
    private static IBasicInformationService basicInformationService;
    @Autowired
    private static IReturnRecordService returnRecordService;
    @Autowired
    private static ISwitchErrorService switchErrorService;
    @Autowired
    private static ISwitchFailureService switchFailureService;
    @Autowired
    private static ISwitchScanResultService switchScanResultService;

    /**
     * 删除数据表所有数据
     *
     */
    @RequestMapping("deleteAllTable")
    public static void deleteAllTable() {
        basicInformationService = SpringBeanUtil.getBean(IBasicInformationService.class);
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
        problemScanLogicService = SpringBeanUtil.getBean(IProblemScanLogicService.class);
        returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);
        switchErrorService = SpringBeanUtil.getBean(ISwitchErrorService.class);
        switchFailureService = SpringBeanUtil.getBean(ISwitchFailureService.class);
        switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);

        /*删除数据表所有数据*/
        /*获取交换机基本信息命令*/
        int deleteBasicInformation =  basicInformationService.deleteBasicInformation();
        System.err.println("deleteBasicInformation:"+deleteBasicInformation);
        /*交换机问题表*/
        int deleteTotalQuestionTable =  totalQuestionTableService.deleteTotalQuestionTable();
        System.err.println("deleteTotalQuestionTable:"+deleteTotalQuestionTable);
        /*交换机扫描命令 和 修复交换机问题命令 表*/
        int deleteCommandLogic =  commandLogicService.deleteCommandLogic();
        System.err.println("deleteCommandLogic:"+deleteCommandLogic);
        /*扫描交换机问题分析表*/
        int deleteProblemScanLogic = problemScanLogicService.deleteProblemScanLogic();
        System.err.println("deleteProblemScanLogic:"+deleteProblemScanLogic);
        /*交换机返回信息表*/
        int deleteReturnRecord = returnRecordService.deleteReturnRecord();
        System.err.println("deleteReturnRecord:"+deleteReturnRecord);
        /*交换机错误表*/
        int deleteSwitchErrorByError = switchErrorService.deleteSwitchErrorByError();
        System.err.println("deleteSwitchErrorByError:"+deleteSwitchErrorByError);
        /*交换机故障表*/
        int deleteSwitchFailureByFailure = switchFailureService.deleteSwitchFailureByFailure();
        System.err.println("deleteSwitchFailureByFailure:"+deleteSwitchFailureByFailure);
        /*交换机扫描结果表*/
        int deleteSwitchScanResult = switchScanResultService.deleteSwitchScanResult();
        System.err.println("deleteSwitchScanResult:"+deleteSwitchScanResult);

    }


    /**
     * 导出问题及命令列表
     */
    public static AjaxResult totalQuestionTableExport(List<TotalQuestionTable> list)
    {
        ExcelUtil<TotalQuestionTable> util = new ExcelUtil<TotalQuestionTable>(TotalQuestionTable.class);
        AjaxResult exportExcel = util.exportExcel(list, "问题表");
        return exportExcel;
    }
    /**
     * 导出扫描命令逻辑列表
     */
    public static AjaxResult commandLogicExport(List<CommandLogic> list)
    {
        ExcelUtil<CommandLogic> util = new ExcelUtil<CommandLogic>(CommandLogic.class);
        return util.exportExcel(list, "扫描命令表");
    }
    /**
     * 导出修复命令逻辑列表
     */
    public static AjaxResult repaircommandLogicExport(List<CommandLogic> list)
    {
        ExcelUtil<CommandLogic> util = new ExcelUtil<CommandLogic>(CommandLogic.class);
        return util.exportExcel(list, "修复命令表");
    }
    /**
     * 导出问题扫描逻辑列表
     */
    public static AjaxResult problemScanLogicExport(List<ProblemScanLogic> list)
    {
        ExcelUtil<ProblemScanLogic> util = new ExcelUtil<ProblemScanLogic>(ProblemScanLogic.class);
        return util.exportExcel(list, "分析表");
    }

    /**
     * 交换机问题、分析、修复表数据导出
     * @param totalQuestionTableId
     * @return
     */
    @RequestMapping("/scanningSQL")
    public static AjaxResult scanningSQL() {
        Long totalQuestionTableId = null;

        Long[] totalQuestionTableIds = new Long[1];
        totalQuestionTableIds[0] = totalQuestionTableId;

        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //List<TotalQuestionTable> totalQuestionTableList = new ArrayList<>();
        List<String> fileName = new ArrayList<>();

        /*问题*/
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        List<TotalQuestionTable> totalQuestionTables = new ArrayList<>();
        if (totalQuestionTableId != null){
            totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableByIds(totalQuestionTableIds);
        }else {
            totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableList(null);
        }

        /* 将交换机问题表 导出成 Excel 表格， 并返回 表名 和 路径 */
        AjaxResult totalQuestionTableExport = totalQuestionTableExport(totalQuestionTables);
        fileName.add(totalQuestionTableExport.get("msg")+"");

        List<CommandLogic> commandLogicList = new ArrayList<>();
        List<CommandLogic> repaircommandLogicList = new ArrayList<>();
        List<ProblemScanLogic> problemScanLogicList = new ArrayList<>();

        for (TotalQuestionTable totalQuestionTable:totalQuestionTables){

            HashMap<String, Object> scanLogicalEntityClass = DefinitionProblemController.getScanLogicalEntityClass(totalQuestionTable, loginUser);
            if (scanLogicalEntityClass != null){
                /*命令数据*/
                List<CommandLogic> commandLogics = (List<CommandLogic>) scanLogicalEntityClass.get("CommandLogic");
                if (commandLogics != null){
                    for (CommandLogic pojo:commandLogics){
                        commandLogicList.add(pojo);
                    }
                }
            }



            /*分析数据*/
            List<ProblemScanLogic> problemScanLogics = (List<ProblemScanLogic>) scanLogicalEntityClass.get("ProblemScanLogic");
            if (problemScanLogics != null){
                for (ProblemScanLogic pojo:problemScanLogics){
                    problemScanLogicList.add(pojo);
                }
            }

            if(totalQuestionTable.getProblemSolvingId() != null){
                List<CommandLogic> commandPojoList = SolveProblemController.queryCommandSet(totalQuestionTable.getProblemSolvingId());
                if (commandPojoList != null){
                    for (CommandLogic pojo:commandPojoList){
                        repaircommandLogicList.add(pojo);
                    }
                }
            }

        }

        AjaxResult repaircommandLogicExport = repaircommandLogicExport(repaircommandLogicList);
        fileName.add(repaircommandLogicExport.get("msg")+"");

        AjaxResult commandLogicExport = commandLogicExport(commandLogicList);
        fileName.add(commandLogicExport.get("msg")+"");

        List<ProblemScanLogic> pojoList = DefinitionProblemController.definitionProblem(problemScanLogicList);
        AjaxResult problemScanLogicExport = problemScanLogicExport(pojoList);

        fileName.add(problemScanLogicExport.get("msg")+"");

        AjaxResult ajaxResult = new AjaxResult(200, "成功", fileName);
        return ajaxResult;
    }

    @PostMapping("/importData")
    @ResponseBody
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        /*上传文件名称*/
        String originalFilename = file.getOriginalFilename();
        int insert = -1;
        if (originalFilename.indexOf("问题表") != -1){
            ExcelUtil<TotalQuestionTable> totalQuestionTableutil = new ExcelUtil<TotalQuestionTable>(TotalQuestionTable.class);
            List<TotalQuestionTable> totalQuestionTableutilList = totalQuestionTableutil.importExcel(file.getInputStream());
            totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);

            for (TotalQuestionTable totalQuestionTable:totalQuestionTableutilList){
                totalQuestionTable.setId(null);
                /*插入*/
                insert= totalQuestionTableService.insertTotalQuestionTableImport(totalQuestionTable);
                System.err.println("totalQuestionTableService:"+insert);

            }

        }else if (originalFilename.indexOf("分析表") != -1){
            ExcelUtil<ProblemScanLogic> problemScanLogicutil = new ExcelUtil<ProblemScanLogic>(ProblemScanLogic.class);
            List<ProblemScanLogic> problemScanLogicutilList = problemScanLogicutil.importExcel(file.getInputStream());
            problemScanLogicService = SpringBeanUtil.getBean(IProblemScanLogicService.class);
            for (ProblemScanLogic problemScanLogic:problemScanLogicutilList){
                insert = problemScanLogicService.insertProblemScanLogicImport(problemScanLogic);
                System.err.println("problemScanLogicService:"+insert);

            }
        }else if (originalFilename.indexOf("命令表") != -1){
            ExcelUtil<CommandLogic> commandLogicutil = new ExcelUtil<CommandLogic>(CommandLogic.class);
            List<CommandLogic> commandLogicutilList = commandLogicutil.importExcel(file.getInputStream());
            commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
            for (CommandLogic commandLogic:commandLogicutilList){
                insert = commandLogicService.insertCommandLogicImport(commandLogic);
                System.err.println("commandLogicService:"+insert);

            }
        }

        if (insert>0){
            return AjaxResult.success();
        }

        return AjaxResult.error();
    }




    /**
     * a
     *
     * @method: 定义获取基本信息命令插入
     * @Param: [jsonPojoList]
     * @return: void
     * @Author: 天幕顽主=:=
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("insertInformationAnalysis/{command}/{custom}")
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
     * a
     *
     * @method: 定义获取基本信息分析数据插入
     * @Param: [jsonPojoList]
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public boolean insertInformationAnalysisMethod(LoginUser loginUser,@RequestBody List<String> jsonPojoList,Long basicInformationId){//@RequestBody List<String> jsonPojoList

        /*定义一个变量 将 分析数据集合转变为长字符串*//*
        String problemScanLogicString = "";
        for (String pojo:jsonPojoList){
            *//*以，为分割 拼接到一起*//*
            problemScanLogicString = problemScanLogicString + pojo +",";
        }
        *//*如果 长字符串 不为"" 的时候说明 分析数据集合不为空*//*
        if (!problemScanLogicString.equals("")){
            jsonPojoList = new ArrayList<>();
            *//*substring(0,problemScanLogicString.length()-1);
            * 是为了去掉最后名的 ， *//*
            problemScanLogicString = problemScanLogicString.substring(0,problemScanLogicString.length()-1);
            *//*加， 后来 换成了 \r\n  然后以 \r\n 分隔 为数据  赋值 list  基本没变化*//*
            problemScanLogicString = problemScanLogicString.replace("},","}\r\n");
            String[] commandLogic_split = problemScanLogicString.split("\r\n");
            for (int number=0; number<commandLogic_split.length; number++){
                jsonPojoList.add(commandLogic_split[number]);
            }
        }else {
            *//*如果分析数据集合为空则返回添加失败*//*
            return false;
        }*/

        if (jsonPojoList.size() == 0){
            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(loginUser.getUsername(),"错误："+"获取交换机基本信息分析数据非法为空\r\n");
            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFile("错误："+"获取交换机基本信息分析数据非法为空\r\n"
                        +"方法com.sgcc.web.controller.sql.DefinitionProblemController.insertInformationAnalysisMethod");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.err.println("\r\n"+"前端出入数据：\r\n");
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
                CommandLogic commandLogic = analysisCommandLogic(jsonPojoList.get(number));
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
        /*相同ID的分析实体类需要放到一个实体类中(因为这里是ture和false的原因，造成了一个实体类分割成了两个相同ID的实体类)*/
        List<ProblemScanLogic> problemScanLogics = definitionProblem(problemScanLogicList);
        //String commandId = null;
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
        ProblemScanLogic problemScanLogic = analysisProblemScanLogic(jsonPojoOne, "分析");

        BasicInformation basicInformation = basicInformationService.selectBasicInformationById(basicInformationId);
        basicInformation.setProblemId(problemScanLogic.getId());

        int i = basicInformationService.updateBasicInformation(basicInformation);
        if (i<=0){

            WebSocketService.sendMessage(loginUser.getUsername(),"错误："+"获取交换机基本信息命令的ProblemId字段失败\r\n");
            try {
                PathHelper.writeDataToFile("错误："+"获取交换机基本信息命令的ProblemId字段失败\r\n"
                        +"方法com.sgcc.web.controller.sql.DefinitionProblemController.insertInformationAnalysisMethod");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        return true;

    }


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
     *
     * a
     *
     *
     * @method: 插入分析问题的数据
     * @Param: [jsonPojoList]
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("definitionProblemJsonPojo")
    @MyLog(title = "定义分析问题数据插入", businessType = BusinessType.UPDATE)
    public boolean definitionProblemJson(@RequestParam Long totalQuestionTableId,@RequestBody List<String> jsonPojoList){

        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        boolean definitionProblemJsonboolean = definitionProblemJsonPojo(totalQuestionTableId,jsonPojoList,loginUser);
        return definitionProblemJsonboolean;
    }

    /**
     *
     * a
     *
     *
     * @method: 插入分析问题的数据
     * @Param: [jsonPojoList]
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    //@RequestMapping("definitionProblemJsonPojo")
    public boolean definitionProblemJsonPojo(Long totalQuestionTableId,@RequestBody List<String> jsonPojoList,LoginUser loginUser){//@RequestBody List<String> jsonPojoList

       /* String problemScanLogicString = "";
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
        }*/

       if (jsonPojoList.size() == 0){
           //传输登陆人姓名 及问题简述
           WebSocketService.sendMessage(loginUser.getUsername(),"错误："+"扫描分析数据非法为空\r\n");
           try {
               //插入问题简述及问题路径
               PathHelper.writeDataToFile("错误："+"扫描分析数据非法为空\r\n"
                       +"方法com.sgcc.web.controller.sql.DefinitionProblemController.definitionProblemJsonPojo");
           } catch (IOException e) {
               e.printStackTrace();
           }
           return false;
       }

        System.err.println("方法com.sgcc.web.controller.sql.DefinitionProblemController.definitionProblemJsonPojo\r\n");
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

        String totalQuestionTableById = totalQuestionTableId+"";;
        String commandId = null;
        for (ProblemScanLogic problemScanLogic:problemScanLogics){

            problemScanLogicService = SpringBeanUtil.getBean(IProblemScanLogicService.class);
            int i = problemScanLogicService.insertProblemScanLogic(problemScanLogic);
            if (i<=0){
                return false;
            }

        }

        for (CommandLogic commandLogic:commandLogicList){
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
        totalQuestionTable.setCommandId(commandId);
        int i = totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable);
        if (i<=0){
            return false;
        }

        return true;
    }


    /**
     * a
     *
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
     * a
     *
     * @method: 字符串解析 ProblemScanLogic 实体类 并返回
     * @Param: [jsonPojo, ifCommand : 分析、命令、问题]
     * @return: com.sgcc.sql.domain.ProblemScanLogic
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public static ProblemScanLogic analysisProblemScanLogic(@RequestBody String jsonPojo,String ifCommand){
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

                    if (split1.equals("null")){
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
        /*当匹配方式不为空 且 包含 按行 时  则为 按行匹配 则需要 拼接 relativePosition 值*/
        if (hashMap.get("matched")!=null && hashMap.get("matched").indexOf("按行")!=-1){
            /** 相对位置 */
            hashMap.put("relativePosition",hashMap.get("relative")+","+hashMap.get("position"));
        }
        /*如果下一条分析数据为命令时 则 下一条ID  赋值给 命令ID*/
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
        /*如果 trueFalse 为 失败时 则 成功行号、成功下一条分析、成功下一条命令 都复制给 失败对应 属性*/
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

            hashMap.put("problemId",hashMap.get("WTNextId"));
            //清空动作属性
            hashMap.put("action",null);
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
        //通过redisTemplate设置值
        /*List<ProblemScanLogic> resultList=(List<ProblemScanLogic>)redisTemplate.opsForList().leftPop("problemScanLogic");
        resultList.add(problemScanLogic);
        redisTemplate.opsForList().leftPush("problemScanLogic",resultList);*/
        return problemScanLogic;
    }

    /**
     *
     * 存放入 数据库里的数据 实体类  拆分  true  false
     * @param ProblemScanLogicList
     * @return
     */
    public static List<ProblemScanLogic> splitSuccessFailureLogic(List<ProblemScanLogic> ProblemScanLogicList) {
        /*遍历 分析集合
         * 当错误行号不为空时 则 错误信息取出 放入 一个新的实体类 然后放入 需要返回的实体类集合 并把原实体类错误信息清空
         * 原实体类 放入 需要返回的实体类集合
         * */
        List<ProblemScanLogic> ProblemScanLogics = new ArrayList<>();
        for (ProblemScanLogic problemScanLogic:ProblemScanLogicList){
            if (problemScanLogic.getfLine()!=null){
                ProblemScanLogic problemScanLogicf = new ProblemScanLogic();

                problemScanLogicf.setId(problemScanLogic.getId());
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
     * a
     *
     * @method: 将相同ID  时间戳 的 实体类 放到一个实体
     * @Param: [pojoList]
     * @return: java.util.List<com.sgcc.sql.domain.ProblemScanLogic>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
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
            if (relativePosition != null) {
                problemScanLogic.setRelativePosition(relativePosition);
            }
            String matchContent = pojo.getMatchContent();
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
        List<ProblemScanLogic> problemScanLogicList = new ArrayList<>();
        Iterator it = pojoMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry =(Map.Entry) it.next();
            ProblemScanLogic problemScanLogic = (ProblemScanLogic) entry.getValue();
            problemScanLogicList.add(problemScanLogic);
        }

        return problemScanLogicList;
    }

    /*定义分析问题数据回显*/
    /***
     *
     * a
     *
     *
     * @method: 根据交换机问题实体类查询问题分析逻辑数据
     * @Param: []
     * @return: java.util.List<java.lang.String>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("getAnalysisList")
    @MyLog(title = "查询定义分析问题数据", businessType = BusinessType.OTHER)
    public static AjaxResult getAnalysisListTimeouts(@RequestBody TotalQuestionTable totalQuestionTable) {

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
            List<String> result = (List<String>) future.get(Configuration.maximumTimeout, TimeUnit.MILLISECONDS);
            System.out.println(result);
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
    public static List<String> getAnalysisList(@RequestBody TotalQuestionTable totalQuestionTable,LoginUser loginUser){

        HashMap<String, Object> scanLogicalEntityClass = getScanLogicalEntityClass(totalQuestionTable, loginUser);
        if (scanLogicalEntityClass == null){
            return null;
        }

        List<CommandLogic> commandLogicList = (List<CommandLogic>) scanLogicalEntityClass.get("CommandLogic");
        List<ProblemScanLogic> problemScanLogics = (List<ProblemScanLogic>) scanLogicalEntityClass.get("ProblemScanLogic");

        HashMap<Long,String> hashMap = new HashMap<>();
        for (CommandLogic commandLogic:commandLogicList){
            String commandLogicString = commandLogicString(commandLogic);
            String[] commandLogicStringsplit = commandLogicString.split(":");
            hashMap.put(Integer.valueOf(commandLogicStringsplit[0]).longValue(),commandLogicStringsplit[1]);
        }
        for (ProblemScanLogic problemScanLogic:problemScanLogics){
            String problemScanLogicString = problemScanLogicSting(problemScanLogic,totalQuestionTable.getId()+"");
            String[] problemScanLogicStringsplit = problemScanLogicString.split(":");
            hashMap.put(Integer.valueOf(problemScanLogicStringsplit[0]).longValue(),problemScanLogicStringsplit[1]);
        }
        List<String> stringList = new ArrayList<>();
        for (Long number=0L;number<hashMap.size();number++){
            if (hashMap.get(number+1)!=null && !(hashMap.get(number+1).equals("null"))){
                System.err.println(hashMap.get(number+1));
                stringList.add(hashMap.get(number+1));
            }
        }
        return stringList;
    }

    /**
     * a
     * 根据 交换机问题实体类 获得命令集合和分析实体类集合
     *
     * @param totalQuestionTable
     * @param loginUser
     * @return
     */
    public static HashMap<String,Object> getScanLogicalEntityClass(@RequestBody TotalQuestionTable totalQuestionTable,LoginUser loginUser) {

        if (totalQuestionTable.getCommandId() == null){
            totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
            List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);

            if (null == totalQuestionTables || totalQuestionTables.size() ==0 ){
                return null;
            }
            totalQuestionTable = totalQuestionTables.get(0);
        }
        /*取出命令 或 分析 字段*/
        if (totalQuestionTable.getCommandId() == null){
            return null;
        }
        String problemScanLogicID = totalQuestionTable.getCommandId();

        /*去除 "命令" 或 "分析"  */
        String problemId = null;
        String commandID = null;
        if (problemScanLogicID.indexOf("分析") != -1){
            problemId = problemScanLogicID.replaceAll("分析","");
        }else if (problemScanLogicID.indexOf("命令") != -1){
            commandID = problemScanLogicID.replaceAll("命令","");
        }


        List<CommandLogic> commandLogicList = new ArrayList<>();
        List<ProblemScanLogic> problemScanLogics = new ArrayList<>();
        HashMap<String,Object> hashMappojo = new HashMap<>();
        do {
            /*如果分析ID 不为空 */
            if (problemId != null){
                //根据第一个分析ID 查询出所有的数据条数
                /*需要行号 所以 需要拆分 true false */
                List<ProblemScanLogic> problemScanLogicList = problemScanLogicList(problemId,loginUser);//commandLogic.getProblemId()
                problemScanLogicList =splitSuccessFailureLogic(problemScanLogicList);

                problemId = null;
                if (null == problemScanLogicList || problemScanLogicList.size() ==0 ){
                    return null;
                }
                /*遍历分析实体类集合，筛选出 命令ID  拼接命令String*/
                for (ProblemScanLogic problemScanLogic:problemScanLogicList){
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

                    problemScanLogics.add(problemScanLogic);

                    if (problemScanLogic.gettComId()!=null && problemScanLogic.gettComId()!= ""){
                        if (commandID == null){
                            commandID = problemScanLogic.gettComId()+":";
                        }else {
                            commandID += problemScanLogic.gettComId()+":";
                        }
                    }

                    if (problemScanLogic.getfComId()!=null && problemScanLogic.getfComId()!= ""){
                        if (commandID == null){
                            commandID = problemScanLogic.getfComId()+":";
                        }else {
                            commandID += problemScanLogic.getfComId()+":";
                        }
                    }

                }
                /*如果 取出的 分析数据 没有下一命令ID 则 退出 do while*/
                if (commandID == null){
                    break;
                }

            }

            /*如果 命令ID 不为空*/
            if (commandID != null){
                /*如果 命令ID 包含 ： 则需要去掉最后一个 ：*/
                if (commandID.indexOf(":")!=-1){
                    commandID = commandID.substring(0,commandID.length()-1);
                }

                String[] commandIDsplit = commandID.split(":");
                /*命令ID 清空 */
                commandID = null;

                for (String commandid:commandIDsplit){
                    if (problemId == null){
                        commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
                        CommandLogic commandLogic = commandLogicService.selectCommandLogicById(commandid);
                        if (commandLogic == null || commandLogic.getProblemId() == null){
                            return null;
                        }
                        commandLogicList.add(commandLogic);

                        if (hashMappojo.get(commandLogic.getcLine()) != null){
                            continue;
                        }else {
                            hashMappojo.put(commandLogic.getcLine(),commandLogic);
                        }

                        /*根据命令实体类 ResultCheckId 值  考虑 是否走 分析表
                         * ResultCheckId 为 0 时，则 进行分析
                         * ResultCheckId 为 1 时，则 进行拼接命令String */
                        if (commandLogic.getResultCheckId().equals("0")){
                            //根据第一个分析ID 查询出所有的数据条数
                            /*需要行号 所以 需要拆分 true false */
                            List<ProblemScanLogic> problemScanLogicList = problemScanLogicList(commandLogic.getProblemId(),loginUser);//commandLogic.getProblemId()
                            problemScanLogicList =splitSuccessFailureLogic(problemScanLogicList);

                            if (null == problemScanLogicList || problemScanLogicList.size() ==0 ){
                                return null;
                            }
                            /*遍历分析实体类集合，筛选出 命令ID  拼接命令String*/
                            for (ProblemScanLogic problemScanLogic:problemScanLogicList){

                                if (hashMappojo.get(problemScanLogic.gettLine()) != null || hashMappojo.get(problemScanLogic.getfLine()) != null){
                                    continue;
                                }else {
                                    if (problemScanLogic.gettLine() != null){
                                        hashMappojo.put(problemScanLogic.gettLine(),problemScanLogic);
                                    }
                                    if (problemScanLogic.getfLine() != null){
                                        hashMappojo.put(problemScanLogic.getfLine(),problemScanLogic);
                                    }
                                }

                                problemScanLogics.add(problemScanLogic);
                                if (problemScanLogic.gettComId()!=null && problemScanLogic.gettComId()!= ""){
                                    if (commandID == null){
                                        commandID = problemScanLogic.gettComId()+":";
                                    }else {
                                        commandID += problemScanLogic.gettComId()+":";
                                    }
                                }
                                if (problemScanLogic.getfComId()!=null && problemScanLogic.getfComId()!= ""){
                                    if (commandID == null){
                                        commandID = problemScanLogic.getfComId()+":";
                                    }else {
                                        commandID += problemScanLogic.getfComId()+":";
                                    }
                                }
                            }
                        }else {
                            commandID = commandLogic.getEndIndex()+":";
                        }

                    }
                }
            }

        }while (commandID != null);

        HashMap<String,Object> ScanLogicalEntityMap = new HashMap<>();
        ScanLogicalEntityMap.put("CommandLogic",commandLogicList);
        ScanLogicalEntityMap.put("ProblemScanLogic",problemScanLogics);

        return ScanLogicalEntityMap;
    }


    /**
     *
     * a
     *
     * 删除扫描逻辑数据
     *
     * @param id
     * @return
     */
    @RequestMapping("deleteScanningLogic")
    public static boolean deleteScanningLogic(@RequestBody Long id) {
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(id);

        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();

        /*获取命令集合和分析逻辑集合*/
        HashMap<String, Object> scanLogicalEntityClass = getScanLogicalEntityClass(totalQuestionTable, loginUser);
        List<CommandLogic> commandLogicList = (List<CommandLogic>) scanLogicalEntityClass.get("CommandLogic");
        List<ProblemScanLogic> problemScanLogics = (List<ProblemScanLogic>) scanLogicalEntityClass.get("ProblemScanLogic");
        /*根据命令集合 获取 命令ID数组，根据分析逻辑集合 获取 分析逻辑ID 数组*/
        /*List<String> commandLogicIdList = commandLogicList.stream().map(p -> p.getId()).distinct().collect(Collectors.toList());
        List<String> problemScanLogicIdList = problemScanLogics.stream().map(p -> p.getId()).distinct().collect(Collectors.toList());
        String[] commandLogicId = new String[commandLogicIdList.size()];
        String[] problemScanLogicId = new String[problemScanLogicIdList.size()];
        for (int i = 0 ; i<commandLogicIdList.size() ;i++){
            commandLogicId[i] = commandLogicIdList.get(i);
        }
        for (int i = 0 ; i<problemScanLogicIdList.size() ;i++){
            problemScanLogicId[i] = problemScanLogicIdList.get(i);
        }*/

        String[] commandLogicId = commandLogicList.stream().map(p -> p.getId()).distinct().toArray(String[]::new);
        String[] problemScanLogicId = problemScanLogics.stream().map(p -> p.getId()).distinct().toArray(String[]::new);


        commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
        int deleteCommandLogicByIds = commandLogicService.deleteCommandLogicByIds(commandLogicId);

        if (deleteCommandLogicByIds>0){
            problemScanLogicService = SpringBeanUtil.getBean(IProblemScanLogicService.class);
            int deleteProblemScanLogicByIds = problemScanLogicService.deleteProblemScanLogicByIds(problemScanLogicId);
            if (deleteProblemScanLogicByIds>0){

                totalQuestionTable.setCommandId(null);

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
            }else {
                //传输登陆人姓名 及问题简述
                WebSocketService.sendMessage(loginUser.getUsername(),"风险："+"扫描交换机问题分析逻辑删除失败\r\n");
                try {
                    //插入问题简述及问题路径
                    PathHelper.writeDataToFile("风险："+"扫描交换机问题分析逻辑删除失败\r\n"
                            +"方法com.sgcc.web.controller.sql.DefinitionProblemController.deleteScanningLogic");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else {
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
     *
     * a
     *
     * @method: 根据分析ID 获取 分析实体类集合
     * @Param: [problemScanLogicID]
     * @return: java.util.List<com.sgcc.sql.domain.ProblemScanLogic>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    //@RequestMapping("problemScanLogicList")
    public static List<ProblemScanLogic> problemScanLogicList(String problemScanLogicID,LoginUser loginUser){

        /*预设跳出循环条件 为 false*/
        boolean contain = false;
        /*预设map key为分析表ID value为 分析表实体类*/
        Map<String,ProblemScanLogic> problemScanLogicHashMap = new HashMap<>();

        do {
            String  problemScanID = "";
            /*分析ID 根据“：” 分割 为 分析ID数组*/
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
                        PathHelper.writeDataToFile("错误："+"根据ID："+id+"查询分析表数据失败,未查出对应ID数据\r\n"
                                +"方法com.sgcc.web.controller.sql.DefinitionProblemController.problemScanLogicList");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }
                problemScanLogicHashMap.put(id,problemScanLogic);
                /*当循环ID不为空时，则查询逻辑中，分支中没有下一行ID
                * 则 进行下一元素的分析iD*/
                if (problemScanLogic.getCycleStartId()!=null && !(problemScanLogic.getCycleStartId().equals("null"))){
                    continue;
                }
                /*解决 完成后 有数据 逻辑错误 不能显示问题 */
                /*if (problemScanLogic.getProblemId()!=null && (problemScanLogic.getProblemId().equals("完成"))){
                    continue;
                }*/
                /*如果 正确 下一ID 不为空 则 拼接到此轮 分析表ID中*/
                if (problemScanLogic.gettNextId()!=null && !(problemScanLogic.gettNextId().equals("null"))
                        && problemScanLogic.gettNextId()!="" &&  !(isContainChinese(problemScanLogic.gettNextId()))){
                    problemScanID += problemScanLogic.gettNextId()+":";
                }
                /*如果 错误 下一ID 不为空 则 拼接到此轮 分析表ID中*/
                if (problemScanLogic.getfNextId()!=null && !(problemScanLogic.getfNextId().equals("null"))
                        && problemScanLogic.getfNextId()!="" &&  !(isContainChinese(problemScanLogic.getfNextId()))){
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
                for (String hashSetid: problemScanLogicHashMap.keySet()){
                    if (!(id.equals(hashSetid))){
                        problemScanID += id+":";
                    }
                    break;
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
        List<ProblemScanLogic> ProblemScanLogicList = new ArrayList<>();
        Iterator<Map.Entry<String, ProblemScanLogic>> it = problemScanLogicHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ProblemScanLogic> entry = it.next();
            ProblemScanLogicList.add(entry.getValue());
        }

        /*此时查询出了  数据库存储的 信息*/
        return ProblemScanLogicList;
    }


    /**
     * 根据正则表达式判断字符是否为汉字
     */
    public static boolean isContainChinese( String str) {
        String regex = "[\u4e00-\u9fa5]"; //汉字的Unicode取值范围
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(str);
        return match.find();
    }

    /**
     *
     * a
     *
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

        String commandLogicVOSting =commandLogicVO.getPageIndex()+":"+"{"
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
     *
     * a
     *
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
        String relative = null;
        //按列匹配
        String position = null;
        problemScanLogicVO.setRelative(relative);
        problemScanLogicVO.setPosition(position);

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
                problemScanLogicVO.setRelative(relative);
                problemScanLogicVO.setPosition(position);
            }
        }
        problemScanLogicVO.setMatched(matched);



        if (problemScanLogic.getMatchContent()!=null){
            String matchContent = problemScanLogic.getMatchContent();
            problemScanLogicVO.setMatchContent(matchContent);
        }
        if (problemScanLogic.getAction()!=null){
            String action = problemScanLogic.getAction();
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

        String problemScanLogicVOString = problemScanLogicVO.getPageIndex()+":"+"{"
                +"\"onlyIndex\"" +"="+ "\""+ problemScanLogicVO.getOnlyIndex() +"\","
                +"\"trueFalse\"" +"="+ "\""+ problemScanLogicVO.getTrueFalse() +"\","
                +"\"matched\"" +"="+ "\""+ problemScanLogicVO.getMatched() +"\","
                +"\"relative\"" +"="+ "\""+ problemScanLogicVO.getRelative() +"\","
                +"\"position\"" +"="+ "\""+ problemScanLogicVO.getPosition() +"\","
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

    /*定义分析问题数据修改*/
    @RequestMapping("updateAnalysis")
    //@MyLog(title = "修改分析问题数据", businessType = BusinessType.UPDATE)
    public boolean updateAnalysis(@RequestParam Long totalQuestionTableId,@RequestBody List<String> pojoList){

        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();

        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(totalQuestionTableId);
        AjaxResult analysisListTimeouts = getAnalysisListTimeouts(totalQuestionTable);
        List<String> analysisList = (List<String>) analysisListTimeouts.get("data");

        /*如果 analysisList 为空则未定义 可直接插入
        * 如果 analysisList 不为空则未定义 则需要先删除*/
        if (analysisList != null){
            List<String> jsonPojoList = new ArrayList<>();
            for (String analysis:analysisList){
                jsonPojoList.add(analysis.replaceAll("\"=\"","\":\""));
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
            HashSet<String> problemScanLogicSet = new HashSet<>();
            for (ProblemScanLogic problemScanLogic:problemScanLogics){
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
    @RequestMapping("updatebasicAnalysis")
    @MyLog(title = "修改获取交换机基本信息逻辑", businessType = BusinessType.UPDATE)
    public boolean updatebasicAnalysis(@RequestParam Long basicInformationId,@RequestBody List<String> pojoList){
        basicInformationService = SpringBeanUtil.getBean(IBasicInformationService.class);
        BasicInformation basicInformation = basicInformationService.selectBasicInformationById(basicInformationId);

        /*根据分析ID 获取 分析实体类集合*/
        /*因为是要删除 需要ID唯一 所以不需要拆分 */
        List<ProblemScanLogic> problemScanLogicList = problemScanLogicList(basicInformation.getProblemId(),SecurityUtils.getLoginUser());//commandLogic.getProblemId()

        /*String[] ids = new String[problemScanLogicList.size()];
        for (int i=0;i<problemScanLogicList.size();i++){
            ids[i] = problemScanLogicList.get(i).getId();
        }*/

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
     * a
     *
     * 回显获取交换机基本信息逻辑数据超时方法
     * @param problemId
     * @return
     */
    @RequestMapping("getBasicInformationProblemScanLogic")
    @MyLog(title = "回显获取交换机基本信息逻辑数据", businessType = BusinessType.OTHER)
    public AjaxResult getBasicInformationProblemScanLogicTimeouts(@RequestBody String problemId) {
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
            System.out.println(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {

            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(loginUser.getUsername(),"风险："+"回显获取交换机基本信息逻辑数据超时\r\n");
            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFile("风险："+"回显获取交换机基本信息逻辑数据超时\r\n"
                        +"方法com.sgcc.web.controller.sql.DefinitionProblemController.getBasicInformationProblemScanLogicTimeouts");
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
     *
     * a
     *
     * 回显获取交换机基本信息逻辑数据
     * @param problemId 首分析DI
     * @param loginUser
     * @return
     */
    public  List<String>  getBasicInformationProblemScanLogic(String problemId,LoginUser loginUser) {
        //loginUser 登陆人信息
        //problemId 分析ID
        /*根据分析ID 获取 分析实体类集合*/
        /* 因为要返回前端信息 成功和失败分为两行 所以 拆分 true false*/
        List<ProblemScanLogic> problemScanLogicList = problemScanLogicList(problemId,loginUser);//commandLogic.getProblemId()
        problemScanLogicList = splitSuccessFailureLogic(problemScanLogicList);

        if (null == problemScanLogicList || problemScanLogicList.size() ==0 ){
            return null;
        }

        HashMap<Long,String> hashMap = new HashMap<>();
        for (ProblemScanLogic problemScanLogic:problemScanLogicList){
            /*因为获取交换机基本信息，故没有 问题ID 为null*/
            String problemScanLogicString = problemScanLogicSting(problemScanLogic,null);
            String[] problemScanLogicStringsplit = problemScanLogicString.split(":");
            /*problemScanLogicStringsplit[0] 行号*/
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
     *
     * a
     *
     * 删除获取交换机基本信息逻辑数据
     *
     * @param id
     * @return
     */
    @RequestMapping("deleteBasicInformationProblemScanLogic")
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
                PathHelper.writeDataToFile("风险："+"获取交换机基本信息分析逻辑未定义\r\n"
                        +"方法com.sgcc.web.controller.sql.DefinitionProblemController.deleteBasicInformationProblemScanLogic");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }else {
            problemId = basicInformation.getProblemId();
        }

        //loginUser 登陆人信息
        //problemId 分析ID
        /*根据分析ID 获取 分析实体类集合 不用拆分 true false*/
        List<ProblemScanLogic> problemScanLogicList = DefinitionProblemController.problemScanLogicList(problemId,loginUser);//commandLogic.getProblemId()

        if (null == problemScanLogicList || problemScanLogicList.size() ==0 ){
            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(loginUser.getUsername(),"风险："+"获取交换机基本信息分析逻辑为空\r\n");
            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFile("风险："+"获取交换机基本信息分析逻辑为空\r\n"
                        +"方法com.sgcc.web.controller.sql.DefinitionProblemController.deleteBasicInformationProblemScanLogic");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        /*String[] arr = new String[problemScanLogicList.size()];
        for (int i = 0;i<problemScanLogicList.size();i++){
            arr[i] = problemScanLogicList.get(i).getId();
        }*/

        String[] arr = problemScanLogicList.stream().map(p -> p.getId()).toArray(String[]::new);

        problemScanLogicService = SpringBeanUtil.getBean(IProblemScanLogicService.class);
        int i = problemScanLogicService.deleteProblemScanLogicByIds(arr);
        if (i<=0){
            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(loginUser.getUsername(),"风险："+"获取交换机基本信息分析逻辑删除失败\r\n");
            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFile("风险："+"获取交换机基本信息分析逻辑删除失败\r\n"
                        +"方法com.sgcc.web.controller.sql.DefinitionProblemController.deleteBasicInformationProblemScanLogic");
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
                    PathHelper.writeDataToFile("风险："+"获取交换机基本信息命令删除失败\r\n"
                            +"方法com.sgcc.web.controller.sql.DefinitionProblemController.deleteBasicInformationProblemScanLogic");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }
            return true;
        }
    }
}