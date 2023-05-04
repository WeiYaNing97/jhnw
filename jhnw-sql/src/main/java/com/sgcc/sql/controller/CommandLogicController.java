package com.sgcc.sql.controller;

import com.sgcc.common.annotation.Log;
import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.core.page.TableDataInfo;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.connect.util.SpringBeanUtil;
import com.sgcc.sql.domain.CommandLogic;
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.sql.service.ICommandLogicService;
import com.sgcc.sql.service.ITotalQuestionTableService;
import com.sgcc.sql.util.PathHelper;
import com.sgcc.sql.webSocket.WebSocketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 命令逻辑Controller
 * 命令逻辑相关操作
 * @author 韦亚宁
 * @date 2021-12-14
 */
@Api("交换机命令管理")
@RestController
@RequestMapping("/sql/command_logic")
public class CommandLogicController extends BaseController
{
    @Autowired
    private static ICommandLogicService commandLogicService;
    @Autowired
    private static ITotalQuestionTableService totalQuestionTableService;


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
    public AjaxResult getInfo(@PathVariable("id") String id)
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
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(commandLogicService.deleteCommandLogicByIds(ids));
    }


    /*=====================================================================================================================
    =====================================================================================================================
    =====================================================================================================================*/


    /**
     * 修复问题命令插入
     * @method: 修复问题集合插入及问题表数据修改
     * @Param: [totalQuestionTableId, commandLogicList]
     * @return: boolean
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @ApiOperation("修复问题命令插入")
    //@PreAuthorize("@ss.hasPermi('sql:command_logic:insertModifyProblemCommandSet')")
    @PostMapping("insertModifyProblemCommandSet")
    @MyLog(title = "修复问题集合插入", businessType = BusinessType.INSERT)
    public boolean insertModifyProblemCommandSet(@RequestParam Long totalQuestionTableId,@RequestBody List<String> commandLogicList){
        //系统登陆人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        /*如果 修复命令集合为空  或者  交换机问题ID为0L 则 返回 false失败*/
        if (commandLogicList.size() == 0 || totalQuestionTableId == 0L){
            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(loginUser.getUsername(),"错误："+"定义修复交换机问题逻辑数据为空\r\n");
            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFile("错误："+"定义修复交换机问题逻辑数据为空\r\n"
                        +"方法com.sgcc.web.controller.sql.command_logic.insertModifyProblemCommandSet");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(totalQuestionTableId);
        List<CommandLogic> commandLogics = new ArrayList<>();
        for (int number=0;number<commandLogicList.size();number++){
            //CommandLogic commandLogic = analysisCommandLogicString(commandLogicList.get(number));
            DefinitionProblemController definitionProblemController = new DefinitionProblemController();
            CommandLogic commandLogic = definitionProblemController.analysisCommandLogic(commandLogicList.get(number));

            commandLogics.add(commandLogic);
        }

        commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
        for (int number=0;number<commandLogics.size();number++){
            int i = commandLogicService.insertCommandLogic(commandLogics.get(number));
            if (i<=0){
                //传输登陆人姓名 及问题简述
                WebSocketService.sendMessage(loginUser.getUsername(),"错误："+"修复交换机问题命令插入失败\r\n");
                try {

                    //插入问题简述及问题路径
                    PathHelper.writeDataToFile("错误："+"修复交换机问题命令插入失败\r\n"
                            +"方法com.sgcc.web.controller.sql.command_logic.insertModifyProblemCommandSet");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
            if (number == 0){
                totalQuestionTable.setProblemSolvingId(commandLogics.get(number).getId());
            }
        }

        int i = totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable);

        if (i<=0){
            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(loginUser.getUsername(),"错误："+"交换机问题实体类修复问题ID修改失败\r\n");
            try {

                //插入问题简述及问题路径
                PathHelper.writeDataToFile("错误："+"交换机问题实体类修复问题ID修改失败\r\n"
                        +"方法com.sgcc.web.controller.sql.command_logic.insertModifyProblemCommandSet");

            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        return true;

    }

    /**
     * @method: 命令表数据 有String 转化为 CommandLogic
     * @Param: [CommandLogicString]
     * @return: com.sgcc.sql.domain.CommandLogic
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public CommandLogic analysisCommandLogicString(String CommandLogicString){
        CommandLogic commandLogic = new CommandLogic();
        CommandLogicString = CommandLogicString.replace("{","");
        CommandLogicString = CommandLogicString.replace("}","");
        String[]  jsonPojo_split = CommandLogicString.split(",");

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("onlyIndex",null);
        hashMap.put("resultCheckId","0");
        hashMap.put("command",null);
        hashMap.put("nextIndex","0");
        hashMap.put("pageIndex",null);
        hashMap.put("endIndex","0");

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
                case "para":// 参数
                    hashMap.put("command",hashMap.get("command")+":"+split1);
                    break;
                case "nextIndex"://下一分析ID 也是 首分析ID
                    hashMap.put("nextIndex",split1);
                    break;
                case "pageIndex"://命令行号
                    hashMap.put("pageIndex",split1);
                    break;
            }
        }

        //如果 常规检验 的话 下一ID  应是 下一命令ID
        //下一分析ID  应是  0
        if (hashMap.get("resultCheckId").equals("1")){
            hashMap.put("endIndex",hashMap.get("nextIndex"));
            hashMap.put("nextIndex","0");
        }

        /** 主键索引 */
        commandLogic.setId(hashMap.get("onlyIndex"));
        /** 状态 */
        commandLogic.setState(null);
        /** 命令 */
        commandLogic.setCommand(hashMap.get("command"));
        /** 返回结果验证id */
        commandLogic.setResultCheckId(hashMap.get("resultCheckId"));
        /** 返回分析id */
        commandLogic.setProblemId(hashMap.get("nextIndex"));
        /** 命令结束索引 */
        commandLogic.setEndIndex(hashMap.get("endIndex"));
        /** 命令行号 */
        commandLogic.setcLine(hashMap.get("pageIndex"));

        return commandLogic;
    }


    /**
     * @method: 修改解决问题命令List
     * @Param: [totalQuestionTableId]
     * @return: com.sgcc.common.core.domain.AjaxResult
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @ApiOperation("修改修复问题命令")
    @PutMapping("updateProblemSolvingCommand")
    @MyLog(title = "修改修复问题命令", businessType = BusinessType.UPDATE)
    public boolean updateProblemSolvingCommand(@RequestParam Long totalQuestionTableId,@RequestBody List<String> commandLogics){
        boolean deleteProblemSolvingCommand = deleteProblemSolvingCommand(totalQuestionTableId);
        if (!deleteProblemSolvingCommand){
            return false;
        }
        //重新插入解决问题命令
        boolean insertModifyProblemCommand = insertModifyProblemCommandSet(totalQuestionTableId, commandLogics);
        return insertModifyProblemCommand;
    }


    /**
     * @method: 删除解决问题命令List
     * @Param: [totalQuestionTableId]
     * @return: com.sgcc.common.core.domain.AjaxResult
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @ApiOperation("删除修复问题命令")
    @DeleteMapping("deleteProblemSolvingCommand")
    @MyLog(title = "删除修复问题命令", businessType = BusinessType.UPDATE)
    public boolean deleteProblemSolvingCommand(@RequestBody Long totalQuestionTableId){
        //根据 问题表 问题ID 查询 问题数据
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(totalQuestionTableId);
        //问题表 解决问题ID
        String problemSolvingId = totalQuestionTable.getProblemSolvingId();
        //解决问题命令集合
        List<CommandLogic> commandLogicList = new ArrayList<>();
        do {
            // 根据解决问题ID 查询 解决问题命令
            CommandLogic commandLogic = commandLogicService.selectCommandLogicById(problemSolvingId);
            // 加入 解决问题命令集合
            commandLogicList.add(commandLogic);
            problemSolvingId = commandLogic.getEndIndex();
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
