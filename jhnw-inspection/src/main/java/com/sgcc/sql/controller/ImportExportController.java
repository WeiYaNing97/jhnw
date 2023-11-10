package com.sgcc.sql.controller;

import com.alibaba.fastjson.JSON;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.share.util.MyUtils;
import com.sgcc.sql.domain.CommandLogic;
import com.sgcc.sql.domain.ProblemScanLogic;
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.sql.service.ICommandLogicService;
import com.sgcc.sql.service.IProblemScanLogicService;
import com.sgcc.sql.service.ITotalQuestionTableService;
import com.sgcc.sql.util.InspectionMethods;
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
    @PostMapping("/scanningSQL")
    public AjaxResult scanningSQL() {
        Long totalQuestionTableId = null;
        Long[] totalQuestionTableIds = new Long[1];
        totalQuestionTableIds[0] = totalQuestionTableId;
        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
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

}
