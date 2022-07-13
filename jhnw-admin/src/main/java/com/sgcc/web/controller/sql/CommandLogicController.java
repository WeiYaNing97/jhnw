package com.sgcc.web.controller.sql;

import com.sgcc.common.annotation.Log;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.page.TableDataInfo;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.sql.domain.CommandLogic;
import com.sgcc.sql.domain.ReturnRecord;
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.sql.service.ICommandLogicService;
import com.sgcc.sql.service.IReturnRecordService;
import com.sgcc.sql.service.ITotalQuestionTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
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
    private ITotalQuestionTableService totalQuestionTableService;


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
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(commandLogicService.deleteCommandLogicByIds(ids));
    }


    /*=====================================================================================================================
    =====================================================================================================================
    =====================================================================================================================*/


    /**
     * @method: 解决问题集合插入 及 问题表数据修改
     * @Param: [totalQuestionTableId, commandLogicList]
     * @return: boolean
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @PreAuthorize("@ss.hasPermi('sql:command_logic:insertModifyProblemCommandSet')")
    //@Log(title = "修复问题命令插入", businessType = BusinessType.OTHER)
    @RequestMapping("insertModifyProblemCommandSet")
    //@Log(title = "解决问题集合插入", businessType = BusinessType.INSERT)
    public boolean insertModifyProblemCommandSet(@RequestParam Long totalQuestionTableId,@RequestBody List<String> commandLogicList){

        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(totalQuestionTableId);
        List<CommandLogic> commandLogics = new ArrayList<>();
        for (int number=0;number<commandLogicList.size();number++){
            CommandLogic commandLogic = analysisCommandLogicString(commandLogicList.get(number));
            commandLogics.add(commandLogic);
        }
        for (int number=0;number<commandLogics.size();number++){
            int i = commandLogicService.insertCommandLogic(commandLogics.get(number));
            if (i<=0){
                return false;
            }
            if (number == 0){
                totalQuestionTable.setProblemSolvingId(commandLogics.get(number).getId());
            }
        }
        int i = totalQuestionTableService.updateTotalQuestionTable(totalQuestionTable);
        if (i<=0){
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
    @RequestMapping("updateProblemSolvingCommand")
    //@Log(title = "修改修复问题命令", businessType = BusinessType.OTHER)
    public boolean updateProblemSolvingCommand(@RequestParam Long totalQuestionTableId,@RequestBody List<String> commandLogics){
        //根据 问题表 问题ID 查询 问题数据
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
        for (CommandLogic commandLogic:commandLogicList){
            int i = commandLogicService.deleteCommandLogicById(commandLogic.getId());
            if (i<=0){
                return false;
            }
        }
        //重新插入解决问题命令
        boolean insertModifyProblemCommand = insertModifyProblemCommandSet(totalQuestionTableId, commandLogics);
        return insertModifyProblemCommand;
    }
}
