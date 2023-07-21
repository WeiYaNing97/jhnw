package com.sgcc.sql.controller;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.util.MyUtils;
import com.sgcc.sql.domain.CommandLogic;
import com.sgcc.sql.domain.ProblemScanLogic;
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.sql.service.ICommandLogicService;
import com.sgcc.sql.service.IProblemScanLogicService;
import com.sgcc.sql.service.ITotalQuestionTableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年03月22日 11:17
 *
 * 数据库数据导入导出 相关功能
 *
 */
@Api("数据库导入导出管理")
@RestController
@RequestMapping("/sql/ImportExportController")
@Transactional(rollbackFor = Exception.class)
public class ImportExportController {

    @Autowired
    private  ICommandLogicService commandLogicService;
    @Autowired
    private  IProblemScanLogicService problemScanLogicService;
    @Autowired
    private  ITotalQuestionTableService totalQuestionTableService;

    /**
     * 交换机问题、分析、修复表数据导出
     * @param
     * @return
     */
    @ApiOperation("数据库导出")
    @GetMapping("/scanningSQL")
    public AjaxResult scanningSQL() {
        Long totalQuestionTableId = null;
        Long[] totalQuestionTableIds = new Long[1];
        totalQuestionTableIds[0] = totalQuestionTableId;
        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //List<TotalQuestionTable> totalQuestionTableList = new ArrayList<>();
        List<String> fileName = new ArrayList<>();

        /*问题*/
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
            totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableList(null);
        }

        DefinitionProblemController definitionProblemController = new DefinitionProblemController();
        for (TotalQuestionTable totalQuestionTable:totalQuestionTables){
            HashMap<String, Object> scanLogicalEntityClass = definitionProblemController.getScanLogicalEntityClass(totalQuestionTable, loginUser);
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
                SolveProblemController solveProblemController = new SolveProblemController();
                List<CommandLogic> commandPojoList = solveProblemController.queryCommandSet(totalQuestionTable.getProblemSolvingId());
                if (commandPojoList != null){
                    for (CommandLogic pojo:commandPojoList){
                        repaircommandLogicList.add(pojo);
                    }
                }
            }
        }
        List<ProblemScanLogic> pojoList = definitionProblemController.definitionProblem(problemScanLogicList);

        List<String> exportText = new ArrayList<>();
        exportText.add("交换机问题表");
        for (TotalQuestionTable pojo:totalQuestionTables){
            String entityClassString = MyUtils.getEntityClassString(pojo);
            exportText.add(entityClassString);
        }

        exportText.add("交换机分析表");
        for (ProblemScanLogic pojo:pojoList){
            String entityClassString = MyUtils.getEntityClassString(pojo);
            exportText.add(entityClassString);
        }

        exportText.add("交换机命令表");
        for (CommandLogic pojo:commandLogicList){
            String entityClassString = MyUtils.getEntityClassString(pojo);
            exportText.add(entityClassString);
        }
        for (CommandLogic pojo:repaircommandLogicList){
            String entityClassString = MyUtils.getEntityClassString(pojo);
            exportText.add(entityClassString);
        }
        fileName.add(MyUtils.fileWrite(exportText, MyUtils.getDate("yyyyMMddhhmmss")));

        AjaxResult ajaxResult = new AjaxResult(200, "成功", fileName);
        return ajaxResult;

    }

    @ApiOperation("数据库导入")
    @PostMapping("/importData")
    @ResponseBody
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {

        InputStream inputStream = file.getInputStream();

        /* 读入TXT文件 */
        InputStreamReader reader = new InputStreamReader(inputStream); // 建立一个输入流对象reader
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
        String line = "";
        List<String> line_list = new ArrayList<>();
        while ((line = br.readLine()) != null) {// 一次读入一行数据
            line_list.add(line);
        }
        List<String> totalQuestionTableList = new ArrayList<>(); //1  交换机问题表
        List<String> commandLogicList = new ArrayList<>(); //2   交换机命令表
        List<String> ProblemScanLogicList = new ArrayList<>(); //3  交换机分析表
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
                    ProblemScanLogicList.add(line_String);
                    break;
            }
        }

        List<TotalQuestionTable> totalQuestionTables = new ArrayList<>();
        for (int i = 1 ; i <totalQuestionTableList.size();i++){
            totalQuestionTables.add(importConversionTotalQuestionTable(totalQuestionTableList.get(i)));
        }
        List<CommandLogic> commandLogics = new ArrayList<>();
        for (int i = 1 ; i <commandLogicList.size();i++){
            commandLogics.add(importConversionCommandLogic(commandLogicList.get(i)));
        }
        List<ProblemScanLogic> problemScanLogics = new ArrayList<>();
        for (int i = 1 ; i <ProblemScanLogicList.size();i++){
            problemScanLogics.add(importConversionProblemScanLogic(ProblemScanLogicList.get(i)));
        }


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
     * 字符串 转化为  交换机问题 TotalQuestionTable
     * @param totalQuestionTable
     * @return
     */
    public TotalQuestionTable importConversionTotalQuestionTable(String totalQuestionTable) {
        totalQuestionTable = totalQuestionTable.substring(0,totalQuestionTable.length()-1);
        String[] totalQuestionTableSplit = totalQuestionTable.split("\",");

        TotalQuestionTable pojo = new TotalQuestionTable();

        for (String key_value:totalQuestionTableSplit){
            String[] key_value_split = key_value.split(":\"");
            String key = key_value_split[0];
            String value = key_value_split[1];

            switch (key){
                case "id":
                    pojo.setId(value.equals("Is_Empty")?null:Integer.valueOf(value).longValue());
                    break;
                case "brand":
                    pojo.setBrand(value.equals("Is_Empty")?null:value);
                    break;
                case "type":
                    pojo.setType(value.equals("Is_Empty")?null:value);
                    break;
                case "firewareVersion":
                    pojo.setFirewareVersion(value.equals("Is_Empty")?null:value);
                    break;
                case "subVersion":
                    pojo.setSubVersion(value.equals("Is_Empty")?null:value);
                    break;
                case "notFinished":
                    pojo.setNotFinished(value.equals("Is_Empty")?null:value);
                    break;
                case "commandId":
                    pojo.setCommandId(value.equals("Is_Empty")?null:value);
                    break;
                case "typeProblem":
                    pojo.setTypeProblem(value.equals("Is_Empty")?null:value);
                    break;
                case "temProName":
                    pojo.setTemProName(value.equals("Is_Empty")?null:value);
                    break;
                case "problemName":
                    pojo.setProblemName(value.equals("Is_Empty")?null:value);
                    break;
                case "problemDescribeId":
                    pojo.setProblemDescribeId(value.equals("Is_Empty")?null:Integer.valueOf(value).longValue());
                    break;
                case "problemSolvingId":
                    pojo.setProblemSolvingId(value.equals("Is_Empty")?null:value);
                    break;
                case "remarks":
                    pojo.setRemarks(value.equals("Is_Empty")?null:value);
                    break;
                case "requiredItems":
                    pojo.setRequiredItems(value.equals("Is_Empty")?null:Integer.valueOf(value).longValue());
                    break;
            }
        }
        return pojo;
    }

    /**
     * 字符串 转化为  交换机问题 CommandLogic
     * @param commandLogic
     * @return
     */
    public CommandLogic importConversionCommandLogic(String commandLogic) {
        commandLogic = commandLogic.substring(0,commandLogic.length()-1);
        String[] commandLogicSplit = commandLogic.split("\",");

        CommandLogic pojo = new CommandLogic();

        for (String key_value:commandLogicSplit){
            String[] key_value_split = key_value.split(":\"");
            String key = key_value_split[0];
            String value = key_value_split[1];

            switch (key){
                case "id":
                    pojo.setId(value.equals("Is_Empty")?null:value);
                    break;
                case "state":
                    pojo.setState(value.equals("Is_Empty")?null:value);
                    break;
                case "cLine":
                    pojo.setcLine(value.equals("Is_Empty")?null:value);
                    break;
                case "command":
                    pojo.setCommand(value.equals("Is_Empty")?null:value);
                    break;
                case "resultCheckId":
                    pojo.setResultCheckId(value.equals("Is_Empty")?null:value);
                    break;
                case "problemId":
                    pojo.setProblemId(value.equals("Is_Empty")?null:value);
                    break;
                case "endIndex":
                    pojo.setEndIndex(value.equals("Is_Empty")?null:value);
                    break;
            }
        }

        return pojo;
    }

    /**
     * 字符串 转化为  交换机分析类 problemScanLogic
     * @param problemScanLogic
     * @return
     */
    public ProblemScanLogic importConversionProblemScanLogic(String problemScanLogic) {
        problemScanLogic = problemScanLogic.substring(0, problemScanLogic.length() - 1);
        String[] problemScanLogicSplit = problemScanLogic.split("\",");

        ProblemScanLogic pojo = new ProblemScanLogic();

        for (String key_value:problemScanLogicSplit){

            String[] key_value_split = key_value.split(":\"");
            String key = key_value_split[0];
            String value = key_value_split[1];

            switch (key){
                case "id":
                    pojo.setId(value.equals("Is_Empty")?null:value);
                    break;
                case "matched":
                    pojo.setMatched(value.equals("Is_Empty")?null:value);
                    break;
                case "relativePosition":
                    pojo.setRelativePosition(value.equals("Is_Empty")?null:value);
                    break;
                case "matchContent":
                    pojo.setMatchContent(value.equals("Is_Empty")?null:value);
                    break;
                case "action":
                    pojo.setAction(value.equals("Is_Empty")?null:value);
                    break;
                case "rPosition":
                    pojo.setrPosition(value.equals("Is_Empty")?null:Integer.valueOf(value).intValue());
                    break;
                case "length":
                    pojo.setLength(value.equals("Is_Empty")?null:value);
                    break;
                case "exhibit":
                    pojo.setExhibit(value.equals("Is_Empty")?null:value);
                    break;
                case "wordName":
                    pojo.setWordName(value.equals("Is_Empty")?null:value);
                    break;
                case "compare":
                    pojo.setCompare(value.equals("Is_Empty")?null:value);
                    break;
                case "tNextId":
                    pojo.settNextId(value.equals("Is_Empty")?null:value);
                    break;
                case "tComId":
                    pojo.settComId(value.equals("Is_Empty")?null:value);
                    break;
                case "problemId":
                    pojo.setProblemId(value.equals("Is_Empty")?null:value);
                    break;
                case "fLine":
                    pojo.setFLine(value.equals("Is_Empty")?null:value);
                    break;
                case "tLine":
                    pojo.settLine(value.equals("Is_Empty")?null:value);
                    break;
                case "fNextId":
                    pojo.setfNextId(value.equals("Is_Empty")?null:value);
                    break;
                case "fComId":
                    pojo.setfComId(value.equals("Is_Empty")?null:value);
                    break;
                case "returnCmdId":
                    pojo.setReturnCmdId(value.equals("Is_Empty")?null:Integer.valueOf(value).longValue());
                    break;
                case "cycleStartId":
                    pojo.setCycleStartId(value.equals("Is_Empty")?null:value);
                    break;
            }
        }

        return pojo;

    }


}
