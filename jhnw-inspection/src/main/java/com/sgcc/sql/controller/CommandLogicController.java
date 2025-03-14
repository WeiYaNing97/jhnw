package com.sgcc.sql.controller;

import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.core.page.TableDataInfo;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.util.PathHelper;
import com.sgcc.sql.domain.CommandLogic;
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.sql.service.ICommandLogicService;
import com.sgcc.sql.service.ITotalQuestionTableService;
import com.sgcc.share.webSocket.WebSocketService;
import com.sgcc.sql.util.InspectionMethods;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hpsf.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 命令逻辑Controller
 * 命令逻辑相关操作
 */
@Api(tags = "交换机命令管理")
@RestController
@RequestMapping("/sql/command_logic")
public class CommandLogicController extends BaseController
{
    @Autowired
    private  ICommandLogicService commandLogicService;
    @Autowired
    private  ITotalQuestionTableService totalQuestionTableService;


    /**
     * 查询命令逻辑列表
     */
    @ApiOperation("查询命令逻辑列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "state", value = "状态", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "cLine", value = "命令行号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "command", value = "命令", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "resultCheckId", value = "验证类型", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "problemId", value = "分析索引", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "endIndex", value = "下一命令", dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:command_logic:list')")
    @GetMapping("/list")
    public TableDataInfo list(CommandLogic commandLogic)
    {
        startPage();
        List<CommandLogic> list = commandLogicService.selectCommandLogicList(commandLogic);
        return getDataTable(list);
    }

    /**
     * 导出命令逻辑列表
     */
    @ApiOperation("导出命令逻辑列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "state", value = "状态", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "cLine", value = "命令行号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "command", value = "命令", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "resultCheckId", value = "验证类型", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "problemId", value = "分析索引", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "endIndex", value = "下一命令", dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:command_logic:export')")
    @MyLog(title = "命令逻辑", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(CommandLogic commandLogic)
    {
        List<CommandLogic> list = commandLogicService.selectCommandLogicList(commandLogic);
        ExcelUtil<CommandLogic> util = new ExcelUtil<CommandLogic>(CommandLogic.class);
        return util.exportExcel(list, "命令逻辑数据");
    }

    /**
     * 获取命令逻辑详细信息
     */
    @ApiOperation("获取命令逻辑详细信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:command_logic:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(commandLogicService.selectCommandLogicById(id));
    }

    /**
     * 新增命令逻辑
     */
    @ApiOperation("新增命令逻辑")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "state", value = "状态", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "cLine", value = "命令行号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "command", value = "命令", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "resultCheckId", value = "验证类型", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "problemId", value = "分析索引", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "endIndex", value = "下一命令", dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:command_logic:add')")
    @MyLog(title = "命令逻辑", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CommandLogic commandLogic)
    {
        return toAjax(commandLogicService.insertCommandLogic(commandLogic));
    }

    /**
     * 修改命令逻辑
     */
    @ApiOperation("修改命令逻辑")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "主键", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "state", value = "状态", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "cLine", value = "命令行号", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "command", value = "命令", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "resultCheckId", value = "验证类型", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "problemId", value = "分析索引", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "endIndex", value = "下一命令", dataTypeClass = String.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:command_logic:edit')")
    @MyLog(title = "命令逻辑", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CommandLogic commandLogic)
    {
        return toAjax(commandLogicService.updateCommandLogic(commandLogic));
    }

    /**
     * 删除命令逻辑
     */
    @ApiOperation("删除命令逻辑")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "ids", value = "主键", dataTypeClass = Array.class, required = true)
    })
    @PreAuthorize("@ss.hasPermi('sql:command_logic:remove')")
    @MyLog(title = "命令逻辑", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(commandLogicService.deleteCommandLogicByIds(ids));
    }


    /*=====================================================================================================================
    =====================================================================================================================
    =====================================================================================================================*/


    /**
     * 新增修复异常命令
     * 新增修复异常命令及问题表数据修改
     * @param totalQuestionTableId 总问题表ID
     * @param commandLogicList     命令逻辑列表
     * @return 插入结果，成功返回true，失败返回false
     */
    @ApiOperation("新增修复异常命令")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "totalQuestionTableId", value = "交换机问题ID", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "commandLogicList", value = "命令逻辑列表", dataTypeClass = List.class, required = true)
    })
    //@PreAuthorize("@ss.hasPermi('sql:command_logic:insertModifyProblemCommandSet')")
    @PostMapping("insertModifyProblemCommandSet")
    @MyLog(title = "新增修复异常命令", businessType = BusinessType.INSERT)
    public boolean insertModifyProblemCommandSet(@RequestParam String totalQuestionTableId,@RequestBody List<String> commandLogicList){
        // 获取系统登陆人信息
        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();

        // 如果修复命令集合为空 或者 交换机问题ID为0L，则返回false失败
        /*如果 修复命令集合为空  或者  交换机问题ID为0L 则 返回 false失败*/
        if (commandLogicList.size() == 0 ){/*  || totalQuestionTableId == 0L   */
            // 传输登陆人姓名及问题简述
            //传输登陆人姓名 及问题简述
            AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                    "错误:定义修复交换机问题逻辑数据为空\r\n");

            return false;
        }

        // 获取总问题表服务，并查询指定ID的总问题表信息
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(totalQuestionTableId);

        // 创建命令逻辑列表
        List<CommandLogic> commandLogics = new ArrayList<>();

        // 获取问题区域编码
        String problem_area_code = totalQuestionTableId.substring(0, 8);

        // 遍历命令逻辑列表，进行解析并添加到命令逻辑列表中
        for (int number=0;number<commandLogicList.size();number++){
            // 解析命令逻辑字符串，创建命令逻辑对象
            //CommandLogic commandLogic = analysisCommandLogicString(commandLogicList.get(number));
            CommandLogic commandLogic = InspectionMethods.analysisCommandLogic(problem_area_code,commandLogicList.get(number),"命令");
            commandLogics.add(commandLogic);
        }

        // 获取命令逻辑服务
        commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);

        // 遍历命令逻辑列表，插入命令逻辑
        for (int number=0;number<commandLogics.size();number++){
            // 插入命令逻辑，并获取插入结果
            int i = commandLogicService.insertCommandLogic(commandLogics.get(number));

            // 如果插入失败，则记录错误日志并返回false
            if (i<=0){
                // 传输登陆人姓名及问题简述
                //传输登陆人姓名 及问题简述
                AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                        "错误:修复交换机问题命令插入失败\r\n");

                return false;
            }

            // 如果是第一个命令逻辑，则设置总问题表的修复问题ID
            if (number == 0){
                totalQuestionTable.setProblemSolvingId(commandLogics.get(number).getId());
            }
        }

        // 更新总问题表信息
        int i = totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable);

        // 如果更新失败，则记录错误日志并返回false
        if (i<=0){
            // 传输登陆人姓名及问题简述
            //传输登陆人姓名 及问题简述
            AbnormalAlarmInformationMethod.afferent(null, loginUser.getUsername(), null,
                    "错误:交换机问题实体类修复问题ID修改失败\r\n");

            return false;
        }

        // 插入成功，返回true
        return true;
    }

    /**
     * @method: 修改修复异常命令List
     * @Param: [totalQuestionTableId]
     * @return: com.sgcc.common.core.domain.AjaxResult
     */
    @ApiOperation("修改修复异常命令")
    @PutMapping("updateProblemSolvingCommand")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "totalQuestionTableId", value = "交换机问题ID", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "commandLogics", value = "命令逻辑列表", dataTypeClass = List.class, required = true)
    })
    @MyLog(title = "修改修复异常命令", businessType = BusinessType.UPDATE)
    public boolean updateProblemSolvingCommand(@RequestParam String totalQuestionTableId,@RequestBody List<String> commandLogics){
        boolean deleteProblemSolvingCommand = deleteProblemSolvingCommand(totalQuestionTableId);
        if (!deleteProblemSolvingCommand){
            return false;
        }
        //重新添加修复异常命令
        boolean insertModifyProblemCommand = insertModifyProblemCommandSet(totalQuestionTableId, commandLogics);
        return insertModifyProblemCommand;
    }

    /**
     * 删除解决问题命令List
     *
     * @param totalQuestionTableId 问题表ID
     * @return AjaxResult 返回结果
     */
    @ApiOperation("删除修复异常命令")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "totalQuestionTableId", value = "交换机问题ID", dataTypeClass = String.class, required = true)
    })
    @DeleteMapping("deleteProblemSolvingCommand")
    @MyLog(title = "删除修复异常命令", businessType = BusinessType.UPDATE)
    public boolean deleteProblemSolvingCommand(@RequestBody String totalQuestionTableId){
        // 根据问题表ID查询问题数据
        //根据 问题表 问题ID 查询 问题数据
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(totalQuestionTableId);

        // 问题表解决问题ID
        //问题表 解决问题ID
        String problemSolvingId = totalQuestionTable.getProblemSolvingId();

        //解决问题命令集合
        List<CommandLogic> commandLogicList = new ArrayList<>();

        do {
            // 根据解决问题ID查询解决问题命令
            // 根据解决问题ID 查询 解决问题命令
            CommandLogic commandLogic = commandLogicService.selectCommandLogicById(problemSolvingId);

            // 加入解决问题命令集合
            // 加入 解决问题命令集合
            commandLogicList.add(commandLogic);

            // 获取下一个解决问题ID
            problemSolvingId = commandLogic.getEndIndex();

            // 当下一命令ID为0时结束循环
            //当下一命令ID为0 的时候  结束
        }while (!(problemSolvingId.equals("0")));

        //删除解决问题命令
        /*String[] ids = new String[commandLogicList.size()];
        for (int num = 0 ; num<commandLogicList.size();num++){
            ids[num] = commandLogicList.get(num).getId();
        }*/
        String[] ids = commandLogicList.stream().map(l ->l.getId()).toArray(String[]::new);
        int deleteCommandLogicByIds = commandLogicService.deleteCommandLogicByIds(ids);

        if (deleteCommandLogicByIds<=0){
            return false;
        }

        //修改解决问题命令字段
        totalQuestionTable.setProblemSolvingId(null);
        int updateTotalQuestionTable = totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable);

        if (updateTotalQuestionTable<=0){
            return false;
        }

        return true;
    }
}
