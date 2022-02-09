package com.sgcc.web.controller.sql;

import com.sgcc.common.annotation.Log;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.page.TableDataInfo;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.sql.domain.CommandLogic;
import com.sgcc.sql.domain.ReturnRecord;
import com.sgcc.sql.service.ICommandLogicService;
import com.sgcc.sql.service.IReturnRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 命令逻辑Controller
 * 
 * @author 韦亚宁
 * @date 2021-12-14
 */
@RestController
@RequestMapping("/sql/command_logic")
public class CommandLogicController extends BaseController
{
    @Autowired
    private ICommandLogicService commandLogicService;
    @Autowired
    private IReturnRecordService returnRecordService;

    //public List<String> setWordList = new ArrayList<>();

    public static String switch_return_string;


    /**
    * @method: 根据命令ID集合的具体命令集合，执行
    * @Param:
    * @return:
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("/executeScanCommand")
    public void executeScanCommand()
    {
        List<Long> commandIdList = TotalQuestionTableController.longList;
        //返回信息集合
        List<String> return_strings = new ArrayList<>();
        for (Long commandId:commandIdList){
            CommandLogic commandLogic = commandLogicService.selectCommandLogicById(commandId);
            String command = commandLogic.getCommand();
            String command_string = null;
            if (SwitchController.way.equalsIgnoreCase("ssh")){
                command_string = SwitchController.connectMethod.sendCommand(command);
            }else if (SwitchController.way.equalsIgnoreCase("telnet")){
                command_string = SwitchController.telnetSwitchMethod.sendCommand(command);
            }
            command_string =Utils.trimString(command_string);
            String[] split = command_string.split("\r\n");
            ReturnRecord returnRecord = new ReturnRecord();
            returnRecord.setCurrentCommLog(command.trim());
            returnRecord.setCurrentReturnLog(command_string.substring(0,command_string.length()-split[split.length-1].length()-2).trim());
            returnRecord.setCurrentIdentifier(split[split.length-1].trim());
            int insert_Int = returnRecordService.insertReturnRecord(returnRecord);
            //判断是否简单检验
            if (commandLogic.getResultCheckId()==1l){
                //判断是否为错误命令 或是否执行成功  例如：返回信息是否包含：% Unrecognized command
                //判断命令是否错误 错误为false 正确为true
                if (!Utils.judgmentError(command_string)){
                    continue;
                }else {
                    System.err.print("简单检验，命令正确，新命令"+commandLogic.getEndIndex());
                    switch_return_string = command_string;
                    System.err.print("\r\n========\r\n"+switch_return_string);
                    Long resultCheckId = executeScanCommandByCommandId(commandLogic.getEndIndex());
                    System.out.println("命令错误"+resultCheckId);
                }
            }else {
                ProblemScanLogicController.first_problem_scanLogic_Id = Integer.valueOf(commandLogic.getProblemId().substring(3,commandLogic.getProblemId().length())).longValue();
                switch_return_string = command_string;//+"=:="+commandLogic.getProblemId();
                System.err.print("\r\n交换机返回信息：\r\n"+switch_return_string);
                //setWordList = new ArrayList();
                return_strings.add(switch_return_string);
            }
        }
    }

   /**
     * @method: 根据命令ID获取具体命令，执行
     * @Param:
     * @return:  返回的是 解决问题ID
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("/executeScanCommandByCommandId")
    public Long executeScanCommandByCommandId(Long commandId)
    {
        Long nextCommandID = 0L;
        //命令ID获取具体命令
        CommandLogic commandLogic = commandLogicService.selectCommandLogicById(commandId);
        //具体命令
        String command = commandLogic.getCommand();
        System.err.print("\r\n"+command);
        //执行命令
        String command_string = null;
        if (SwitchController.way.equalsIgnoreCase("ssh")){
            command_string = SwitchController.connectMethod.sendCommand(command);
        }else if (SwitchController.way.equalsIgnoreCase("telnet")){
            command_string = SwitchController.telnetSwitchMethod.sendCommand(command);
        }
        //修整返回信息
        command_string =Utils.trimString(command_string);
        //按行切割
        String[] split = command_string.split("\r\n");
        ReturnRecord returnRecord = new ReturnRecord();
        returnRecord.setCurrentCommLog(command.trim());
        returnRecord.setCurrentReturnLog(command_string.substring(0,command_string.length()-split[split.length-1].length()-2).trim());
        //按行切割，最后一位应该是 标识符
        returnRecord.setCurrentIdentifier(split[split.length-1].trim());
        //返回信息表，返回插入条数
        int insert_Int = returnRecordService.insertReturnRecord(returnRecord);
        //判断是否简单检验 1L为简单校验  默认0L 为分析数据表自定义校验
        if (commandLogic.getResultCheckId()==1l){
            //判断命令是否错误 错误为false 正确为true
            if (Utils.judgmentError(command_string)){
                switch_return_string = command_string;
                System.err.print("\r\n"+switch_return_string);
                System.err.print("\r\n"+"简单检验，命令正确，新命令"+commandLogic.getEndIndex());
                nextCommandID = executeScanCommandByCommandId(commandLogic.getEndIndex());
                return nextCommandID;
            }
        }else {
            //分析第一条ID
            ProblemScanLogicController.first_problem_scanLogic_Id = Integer.valueOf(commandLogic.getProblemId().substring(3,commandLogic.getProblemId().length())).longValue();
            switch_return_string = command_string;//+"=:="+commandLogic.getProblemId();
            System.err.print("\r\n"+switch_return_string);
        }
        return nextCommandID;
    }





    /**
     * 查询命令逻辑列表
     */
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
    @PreAuthorize("@ss.hasPermi('sql:command_logic:export')")
    @Log(title = "命令逻辑", businessType = BusinessType.EXPORT)
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
    @PreAuthorize("@ss.hasPermi('sql:command_logic:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(commandLogicService.selectCommandLogicById(id));
    }

    /**
     * 新增命令逻辑
     */
    @PreAuthorize("@ss.hasPermi('sql:command_logic:add')")
    @Log(title = "命令逻辑", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CommandLogic commandLogic)
    {
        return toAjax(commandLogicService.insertCommandLogic(commandLogic));
    }

    /**
     * 修改命令逻辑
     */
    @PreAuthorize("@ss.hasPermi('sql:command_logic:edit')")
    @Log(title = "命令逻辑", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CommandLogic commandLogic)
    {
        return toAjax(commandLogicService.updateCommandLogic(commandLogic));
    }

    /**
     * 删除命令逻辑
     */
    @PreAuthorize("@ss.hasPermi('sql:command_logic:remove')")
    @Log(title = "命令逻辑", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(commandLogicService.deleteCommandLogicByIds(ids));
    }
}
