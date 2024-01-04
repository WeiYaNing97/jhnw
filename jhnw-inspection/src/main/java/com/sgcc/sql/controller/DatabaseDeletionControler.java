package com.sgcc.sql.controller;

import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.service.IReturnRecordService;
import com.sgcc.share.service.ISwitchErrorService;
import com.sgcc.share.service.ISwitchFailureService;
import com.sgcc.share.service.ISwitchScanResultService;
import com.sgcc.sql.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @date 2022年03月22日 11:17
 * 数据库数据删除
 */
@Api("数据库数据删除管理")
@RestController
@RequestMapping("/sql/DatabaseDeletionControler")
@Transactional(rollbackFor = Exception.class)
public class DatabaseDeletionControler {

    @Autowired
    private ICommandLogicService commandLogicService;
    @Autowired
    private IProblemScanLogicService problemScanLogicService;
    @Autowired
    private ITotalQuestionTableService totalQuestionTableService;
    @Autowired
    private IBasicInformationService basicInformationService;
    @Autowired
    private IReturnRecordService returnRecordService;
    @Autowired
    private ISwitchErrorService switchErrorService;
    @Autowired
    private ISwitchFailureService switchFailureService;
    @Autowired
    private ISwitchScanResultService switchScanResultService;
    /**
     * 删除数据表所有数据
     *
     */
    @ApiOperation("删除数据表所有数据")
    @MyLog(title = "删除数据表所有数据", businessType = BusinessType.DELETE)
    @DeleteMapping("deleteAllTable")
    public void deleteAllTable() {
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
        /*交换机问题表*/
        int deleteTotalQuestionTable =  totalQuestionTableService.deleteTotalQuestionTable();
        /*交换机扫描命令 和 修复交换机问题命令 表*/
        int deleteCommandLogic =  commandLogicService.deleteCommandLogic();
        /*扫描交换机问题分析表*/
        int deleteProblemScanLogic = problemScanLogicService.deleteProblemScanLogic();
        /*交换机返回信息表*/
        int deleteReturnRecord = returnRecordService.deleteReturnRecord();
        /*交换机错误表*/
        int deleteSwitchErrorByError = switchErrorService.deleteSwitchErrorByError();
        /*交换机故障表*/
        int deleteSwitchFailureByFailure = switchFailureService.deleteSwitchFailureByFailure();
        /*交换机扫描结果表*/
        int deleteSwitchScanResult = switchScanResultService.deleteSwitchScanResult();
    }
}
