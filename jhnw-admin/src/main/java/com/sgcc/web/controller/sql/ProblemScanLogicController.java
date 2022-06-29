package com.sgcc.web.controller.sql;

import com.sgcc.common.annotation.Log;
import com.sgcc.common.core.controller.BaseController;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.page.TableDataInfo;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 问题扫描逻辑Controller
 * 
 * @author 韦亚宁
 * @date 2021-12-14
 */
@RestController
@RequestMapping("/sql/problem_scan_logic")
//事务
@Transactional(rollbackFor = Exception.class)
public class ProblemScanLogicController extends BaseController
{
    @Autowired
    private IProblemScanLogicService problemScanLogicService;
    @Autowired
    private ICommandLogicService commandLogicService;
    @Autowired
    private ITotalQuestionTableService totalQuestionTableService;


    /**
     * 查询问题扫描逻辑列表
     */
    @PreAuthorize("@ss.hasPermi('sql:problem_scan_logic:list')")
    @GetMapping("/list")
    public TableDataInfo list(ProblemScanLogic problemScanLogic)
    {
        startPage();
        List<ProblemScanLogic> list = problemScanLogicService.selectProblemScanLogicList(problemScanLogic);
        return getDataTable(list);
    }

    /**
     * 导出问题扫描逻辑列表
     */
    @PreAuthorize("@ss.hasPermi('sql:problem_scan_logic:export')")
    @Log(title = "问题扫描逻辑", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(ProblemScanLogic problemScanLogic)
    {
        List<ProblemScanLogic> list = problemScanLogicService.selectProblemScanLogicList(problemScanLogic);
        ExcelUtil<ProblemScanLogic> util = new ExcelUtil<ProblemScanLogic>(ProblemScanLogic.class);
        return util.exportExcel(list, "问题扫描逻辑数据");
    }

    /**
     * 获取问题扫描逻辑详细信息
     */
    @PreAuthorize("@ss.hasPermi('sql:problem_scan_logic:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(problemScanLogicService.selectProblemScanLogicById(id));
    }

    /**
     * 新增问题扫描逻辑
     */
    @PreAuthorize("@ss.hasPermi('sql:problem_scan_logic:add')")
    @Log(title = "问题扫描逻辑", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ProblemScanLogic problemScanLogic)
    {
        return toAjax(problemScanLogicService.insertProblemScanLogic(problemScanLogic));
    }

    /**
     * 修改问题扫描逻辑
     */
    @PreAuthorize("@ss.hasPermi('sql:problem_scan_logic:edit')")
    @Log(title = "问题扫描逻辑", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ProblemScanLogic problemScanLogic)
    {
        return toAjax(problemScanLogicService.updateProblemScanLogic(problemScanLogic));
    }

    /**
     * 删除问题扫描逻辑
     */
    @PreAuthorize("@ss.hasPermi('sql:problem_scan_logic:remove')")
    @Log(title = "问题扫描逻辑", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(problemScanLogicService.deleteProblemScanLogicByIds(ids));
    }


    /**
     * 获取交换机 基本信息命令 列表
     */
    /*@GetMapping("/getBasicInformationList")
    public String getBasicInformationList() {
        BasicInformation pojo_NULL = new BasicInformation();
        List<BasicInformation> basicInformationList = basicInformationService.selectBasicInformationList(pojo_NULL);
        pojolist = basicInformationList;
        for (BasicInformation basicInformation:basicInformationList){
            String[] split = basicInformation.getCommand().split(",");
            String commandString ="";
            String return_sum = "";
            for (String string_split:split){
                if (SwitchController.way.equalsIgnoreCase("ssh")){
                    commandString = SwitchController.connectMethod.sendCommand(string_split,null);
                }else if (SwitchController.way.equalsIgnoreCase("telnet")){
                    commandString = SwitchController.telnetSwitchMethod.sendCommand(string_split,null);
                }
                //判断命令是否错误 错误为false 正确为true
                if (!Utils.judgmentError(commandString)){
                    break;
                }
                commandString = Utils.trimString(commandString);
                String[] commandString_split = commandString.split("\r\n");
                ReturnRecord returnRecord = new ReturnRecord();
                returnRecord.setCurrentCommLog(string_split.trim());
                returnRecord.setCurrentReturnLog(commandString.substring(0,commandString.length()-commandString_split[commandString_split.length-1].length()-2).trim());
                returnRecord.setCurrentIdentifier(commandString_split[commandString_split.length-1].trim());
                int insert_Int = returnRecordService.insertReturnRecord(returnRecord);
                return_sum += commandString+"\r\n";
            }
            String command_String = Utils.trimString(return_sum);
            result_string = "";
            //分析第一条ID
            first_problem_scanLogic_Id = basicInformation.getProblemId();
            analysisReturn(command_String);

            if (this.extractInformation_string.equals("")){
                continue;
            }

            System.err.print(this.extractInformation_string);
            this.extractInformation_string = this.extractInformation_string.replace(",","");
            String[] return_result_split = this.extractInformation_string.split("=:=");
            //设备型号
            Global.deviceModel="";
            //设备品牌
            Global.deviceBrand = "";
            //内部固件版本
            Global.firmwareVersion = "";
            //子版本号
            Global.subversionNumber = "";
            for (int num = 0;num<return_result_split.length;num++){
                //设备型号
                if (return_result_split[num].equals("设备型号")){
                    num++;
                    Global.deviceModel=return_result_split[num];
                }
                //设备品牌
                if (return_result_split[num].equals("设备品牌")) {
                    num++;
                    Global.deviceBrand = return_result_split[num];
                }
                //内部固件版本
                if (return_result_split[num].equals("内部固件版本")) {
                    num++;
                    Global.firmwareVersion = return_result_split[num];
                }
                //子版本号
                if (return_result_split[num].equals("子版本号")) {
                    num++;
                    Global.subversionNumber = return_result_split[num];
                }
                if (!Global.deviceModel.equals("") && !Global.deviceBrand.equals("") && !Global.firmwareVersion.equals("") && !Global.subversionNumber.equals("")){
                    System.err.print("\r\n"+"设备型号:"+Global.deviceModel);
                    System.err.print("\r\n"+"设备品牌:"+Global.deviceBrand);
                    System.err.print("\r\n"+"内部固件版本:"+Global.firmwareVersion);
                    System.err.print("\r\n"+"子版本号:"+Global.subversionNumber);
                    return this.extractInformation_string;
                }
            }
        }
        return null;
    }*/
    /**
     *  获取交换机 基本信息命令 列表 根据分析ID获取问题扫描逻辑详细信息
     */
    /*public void analysisReturn(String resultString){
        this.line_n = 0;
        this.extractInformation_string ="";
        this.current_Round_Extraction_String="";
        //整理返回结果 去除 #
        resultString = resultString.replace("\r\n"+" # "+"\r\n","\r\n");
        //将返回结果 与 分析ID 分开
        return_information_array =resultString.split("\r\n");
        String operation = "end";
        //分析第一条ID
        String string = selectProblemScanLogicById(this.first_problem_scanLogic_Id,operation);// loop end
        System.err.print(string);
    }*/

    /**
    * @method: 执行分析
    * @Param: [resultString_ProblemScanLogicId]
    * @return: void
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    /*@RequestMapping("analysisReturnResults")
    public void analysisReturnResults(String resultString){
        resultString = CommandLogicController.switch_return_string;
        //标记 索引位置
        this.line_n = 0;
        //提取信息总和  比如获取基本信息
        this.extractInformation_string ="";
        this.current_Round_Extraction_String="";
        //整理返回结果 去除 #
        resultString = resultString.replace("\r\n"+" # "+"\r\n","\r\n");
        //将交换机返回信息 按行来切割 字符串数组
        return_information_array =resultString.split("\r\n");
        //获得第一条分析ID
        //因为前三个是 1位为操作类型（取词w、分析a、匹配m） 2,3位为品牌编码；后5位为随机生成的序号；
        //根据第一条分析ID 查询分析信息
        ProblemScanLogic problemScanLogic = problemScanLogicService.selectProblemScanLogicById(first_problem_scanLogic_Id);
        //关键字
        String matchContent = problemScanLogic.getMatchContent();
        //根据ID去分析
        String operation = "loop";
        String problemScanLogic_string = selectProblemScanLogicById(first_problem_scanLogic_Id,operation);// loop end

        System.err.print("\r\n"+this.extractInformation_string);
        System.err.print("\r\nstring:"+problemScanLogic_string);

    }*/

    /**
     * 根据分析ID获取问题扫描逻辑详细信息
     */
    /*public String selectProblemScanLogicById(String id,String operation) {
        //根据ID查询第一条分析数据
        ProblemScanLogic problemScanLogic = problemScanLogicService.selectProblemScanLogicById(id);
        //相对位置——行
        String relativePosition = problemScanLogic.getRelativePosition();
        String relativePosition_line = relativePosition.split(",")[0];
        //关键字
        String matchContent = problemScanLogic.getMatchContent();
        if (!relativePosition_line.equals("null")){
            int line_number = Integer.valueOf(relativePosition_line).intValue();
            //line_n 为上一条分析的 成功确认索引  加 下一条相对位置 就是下一个索引位置
            line_n = line_n + line_number;
        }else {
            line_n = 0 ;
        }

        //从line_n=0 开始检索集合 一直到最后一位
        for (int num = line_n;num<return_information_array.length; num++){
            //返回信息的数组元素 第num 条
            String string_line_n = return_information_array[num];
            //匹配逻辑
            String matched = problemScanLogic.getMatched();
            //取词逻辑
            String action = problemScanLogic.getAction();
            //匹配
            if (!matched.equals("null")){
                //匹配 成功 失败
                boolean matchAnalysis_true_false = Utils.matchAnalysis(matched, string_line_n, matchContent);
                //如果成功 则把 匹配成功的行数 付给变量 line_n
                if (matchAnalysis_true_false){
                    String analysis_true_string = analysis_true(problemScanLogic, num);

                    if ((analysis_true_string.indexOf("endmark") ==-1) && (analysis_true_string.indexOf("nextcmd") == -1)){
                        String ProblemScanLogic_returnstring = selectProblemScanLogicById(analysis_true_string,operation);
                        if ((analysis_true_string.indexOf("endmark") !=-1) && (analysis_true_string.indexOf("nextcmd") != -1) &&
                                operation.equalsIgnoreCase("loop")&&num<return_information_array.length){
                            String loop_string = selectProblemScanLogicById(first_problem_scanLogic_Id, operation);
                            return loop_string;
                        }
                        return ProblemScanLogic_returnstring;
                    }else {

                        //insertvalueInformationService(true,problemScanLogic,this.current_Round_Extraction_String);
                        this.current_Round_Extraction_String = "";

                        if (operation.equalsIgnoreCase("loop")&&num<return_information_array.length){
                            String loop_string = selectProblemScanLogicById(first_problem_scanLogic_Id, operation);
                            return loop_string;
                        }
                        return analysis_true_string;
                    }

                }else {

                    //insertvalueInformationService(false,problemScanLogic,this.current_Round_Extraction_String);
                    this.current_Round_Extraction_String = "";

                    //如果是最后一条信息 并且 匹配不上则
                    if ((!matchAnalysis_true_false && num == return_information_array.length-1)||(!relativePosition.equals("null") && !relativePosition.equals("0,0"))){
                        String analysis_false_string = analysis_false(problemScanLogic, num);
                        return analysis_false_string;
                    }
                }
                continue;
            }

            //取词
            if (!action.equals("null")){
                //取词数
                String wordSelection_string = Utils.wordSelection(
                        return_information_array[num], matchContent, //返回信息的一行 提取关键字
                        problemScanLogic.getrPosition(), problemScanLogic.getLength()); //位置 长度WLs

                if (wordSelection_string!=null){
                    this.extractInformation_string = this.extractInformation_string +problemScanLogic.getWordName()+"=:="+ wordSelection_string+"=:=";
                    this.current_Round_Extraction_String = this.current_Round_Extraction_String +problemScanLogic.getWordName()+"=:="+ wordSelection_string+"=:=";
                    String analysis_true_string = analysis_true(problemScanLogic, num);

                    if ((analysis_true_string.indexOf("endmark") ==-1) && (analysis_true_string.indexOf("nextcmd") == -1)){
                        String ProblemScanLogic_returnstring = selectProblemScanLogicById(analysis_true_string,operation);
                        if (operation.equalsIgnoreCase("loop")&&num<return_information_array.length){
                            String loop_string = selectProblemScanLogicById(first_problem_scanLogic_Id, operation);
                            return loop_string;
                        }
                        return ProblemScanLogic_returnstring;
                    }else {
                        insertvalueInformationService(true,problemScanLogic,this.current_Round_Extraction_String);
                        System.err.print("\r\n参数："+this.current_Round_Extraction_String);
                        this.current_Round_Extraction_String = "";
                        if (operation.equalsIgnoreCase("loop")&&num<return_information_array.length){
                            String loop_string = selectProblemScanLogicById(first_problem_scanLogic_Id, operation);
                            return loop_string;
                        }
                        return analysis_true_string;
                    }

                }else {
                    insertvalueInformationService(false,problemScanLogic,this.current_Round_Extraction_String);
                    this.current_Round_Extraction_String = "";
                    if ((wordSelection_string ==null && num == return_information_array.length-1)||(!relativePosition_line.equals("null") && !relativePosition_line.equals("0,0"))){
                        String analysis_false_string = analysis_false(problemScanLogic, num);
                        return analysis_false_string;
                    }
                }
                continue;
            }
        }
        return "";
    }*/


    /*public void insertvalueInformationService(boolean boo,ProblemScanLogic problemScanLogic,String parameterString){
        String getNextId = "";
        String problemId ="";
        String comId ="";
        if (boo){
            getNextId = problemScanLogic.gettNextId();
            problemId = problemScanLogic.getProblemId();
            comId = problemScanLogic.gettComId();
        }else {
            getNextId = problemScanLogic.getfNextId();
            problemId = problemScanLogic.getProblemId();
            comId = problemScanLogic.getfComId();
        }
        switch (getNextId){
            case "haveproblem_endmark":
                String[] parameterStringsplit = parameterString.split("=:=");
                Long outId = 0l;
                if (parameterStringsplit.length>0){
                    ValueInformation valueInformation = new ValueInformation();
                    for (int number=parameterStringsplit.length-1;number>0;number--){
                        valueInformation.setExhibit(problemScanLogic.getExhibit());//是否显示
                        valueInformation.setDynamicVname(parameterStringsplit[number]);//动态信息名称
                        valueInformation.setDisplayInformation(parameterStringsplit[number]);//动态信息(显示)
                        String information = parameterStringsplit[--number];
                        valueInformation.setDynamicInformation(information);//动态信息
                        valueInformation.setOutId(outId);
                        valueInformationService.insertValueInformation(valueInformation);
                        outId = valueInformation.getId();
                    }
                }
                SwitchProblem switchProblem = new SwitchProblem();
                switchProblem.setSwitchIp(Global.logIP);
                switchProblem.setSwitchName(Global.logUser);
                switchProblem.setSwitchPassword(Global.logPassword);
                switchProblem.setProblemId(problemId);
                switchProblem.setComId(comId);
                switchProblem.setValueId(outId);
                switchProblemService.insertSwitchProblem(switchProblem);
            case "noproblem_endmark":
            case "":
        }
    };*/

    /**
     * @method: 根据命令ID获取具体命令，执行
     * @Param:
     * @return:  返回的是 解决问题ID
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    /*@RequestMapping("/executeScanCommandByCommandId")
    public Long executeScanCommandByCommandId(String commandId)
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
            command_string = SwitchController.connectMethod.sendCommand(command,null);
        }else if (SwitchController.way.equalsIgnoreCase("telnet")){
            command_string = SwitchController.telnetSwitchMethod.sendCommand(command,null);
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
        if (commandLogic.getResultCheckId().equals("1")){
            //判断命令是否错误 错误为false 正确为true
            if (Utils.judgmentError(command_string)){
                CommandLogicController.switch_return_string = command_string;
                System.err.print("\r\n"+CommandLogicController.switch_return_string);
                System.err.print("\r\n"+"简单检验，命令正确，新命令"+commandLogic.getEndIndex());
                nextCommandID = executeScanCommandByCommandId(commandLogic.getEndIndex());
                return nextCommandID;
            }
        }else {
            //分析第一条ID
            ProblemScanLogicController.first_problem_scanLogic_Id = commandLogic.getProblemId();
            CommandLogicController.switch_return_string = command_string;//+"=:="+commandLogic.getProblemId();
            System.err.print("\r\n"+CommandLogicController.switch_return_string);
        }
        return nextCommandID;
    }*/


    /**
    * @method: 分析结果正确
    * @Param: [problemScanLogic, num]
    * @return: java.lang.String 有结果返回结果，没结果返回下一条分析ID
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    /*public String analysis_true(ProblemScanLogic problemScanLogic,int num){
        String analysis_true = null;
        //true 下一层索引
        String tNextId_string = problemScanLogic.gettNextId();
        if (!tNextId_string.equals("noproblem_endmark")
                && !tNextId_string.equals("haveproblem_endmark")
                && !tNextId_string.equals("nextcmd")){
            //匹配成功则返回 true下一条ID
            line_n = num;
            long tNextId_Integer= Integer.valueOf(problemScanLogic.gettNextId().substring(3,problemScanLogic.gettNextId().length())).longValue();
            return tNextId_Integer+"";
        }else {
            switch (tNextId_string){
                //true 存在问题——endmark：确认存在问题，是否返回看命令id
                case "haveproblem_endmark":
                    Integer tComId =Integer.valueOf(problemScanLogic.gettComId());
                    Integer tProblemIdx =Integer.valueOf(problemScanLogic.getProblemId());
                    if (tComId==0){
                        return  analysis_true = "haveproblem_endmark=:="+"问题索引=:="+tProblemIdx+"=:=tComId=:=0";
                    }else {
                        return  analysis_true = "haveproblem_endmark=:="+"问题索引=:="+tProblemIdx+"=:=tComId=:="+tComId;
                    }
                    //true 不存在问题——endmark：确认不存在问题，返回同上
                case "noproblem_endmark":
                    return analysis_true = "noproblem_endmark";
                //true nextcmd：代表需要执行命令才能进一步分析，看命令id
                case "nextcmd":
                    String commandId = problemScanLogic.gettComId();
                    executeScanCommandByCommandId(commandId);
                    return analysis_true = "nextcmd=:=tComId=:="+commandId;
            }
        }
        return analysis_true;
    }*/

    /**
    * @method: 分析结果错误
    * @Param: [problemScanLogic, num]
    * @return: java.lang.String 有结果返回结果，没结果返回下一条分析ID
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    /*public String analysis_false(ProblemScanLogic problemScanLogic,int num){
        String analysis_false=null;
        String fNextId_string = problemScanLogic.getfNextId();
        if (!fNextId_string.equals("noproblem_endmark")
                && !fNextId_string.equals("haveproblem_endmark")
                && !fNextId_string.equals("nextcmd")){
            line_n = num;
            long fNextId_Integer = Integer.valueOf(fNextId_string.substring(3, fNextId_string.length())).longValue();
            *//*analysis_false = selectProblemScanLogicById(fNextId_Integer);*//*
            return fNextId_Integer+"";
        }else {
            switch ( fNextId_string ){
                //true 存在问题——endmark：确认存在问题，是否返回看命令id
                case "haveproblem_endmark":
                    Integer fComId =Integer.valueOf(problemScanLogic.getfComId());
                    Integer fProblemIdx =Integer.valueOf(problemScanLogic.getProblemId());
                    if (fComId==0){
                        return  analysis_false = "haveproblem_endmark=:="+"问题索引=:="+fProblemIdx+"=:=fComId=:=0";
                    }else {
                        return  analysis_false = "haveproblem_endmark=:="+"问题索引=:="+fProblemIdx+"=:=fComId=:="+fComId;
                    }
                    //true 不存在问题——endmark：确认不存在问题，返回同上
                case "noproblem_endmark":
                    return analysis_false = "noproblem_endmark";
                //true nextcmd：代表需要执行命令才能进一步分析，看命令id
                case "nextcmd":
                    String commandId = problemScanLogic.getfComId();
                    executeScanCommandByCommandId(commandId);
                    return analysis_false = "nextcmd=:=fComId=:="+commandId;
            }
        }
        return analysis_false;
    }*/


    /**
     * @method: 比较版本号
     * @Param: [remove_content, compare, content] //交换机版本号  比较方法   数据库版本号
     * 如果 交换机版本号  比较方法   数据库版本号 则返回 true
     * @return: boolean
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    /*public static boolean compareVersion(String remove_content,String compare,String content){
        boolean compare_size = false;
        switch (compare){
            case ">":
                compare_size = compareVersionNumber(remove_content, content);
                return compare_size;
            case "<":
                compare_size = compareVersionNumber(content, remove_content);
                return compare_size;
            case "=":
                compare_size = remove_content.equals(content);
                return compare_size;
            case ">=":
                compare_size = compareVersionNumber(remove_content, content) || remove_content.equals(content);
                return compare_size;
            case "<=":
                compare_size = compareVersionNumber(content, remove_content) || remove_content.equals(content);
                return compare_size;
            case "!=":
                compare_size = !(remove_content.equals(content));
                return compare_size;
        }
        return compare_size;
    }*/

    /**
     * @method: 比较系统版本号大小
     * 如果 str1 > str2 返回 true
     * 如果 str1 < str2 返回 false
     * @Param: [str1, str2]
     * @return: boolean
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    /*public static boolean compareVersionNumber(String str1,String str2){
        String[] split1 = str1.split("\\.");
        String[] split2 = str2.split("\\.");
        int j;
        if (split1.length < split2.length){
            j=split1.length;
        }else if (split2.length < split1.length){
            j=split2.length;
        }else{
            j=split1.length;
        }
        for (int i=0;i<j;i++){
            int i1 = Integer.valueOf(split1[i]).intValue();
            int i2 = Integer.valueOf(split2[i]).intValue();
            if (i1>i2){
                return true;
            }else if (i1<i2){
                return false;
            }
        }

        if (split1.length < split2.length){
            return false;
        }else if (split2.length < split1.length){
            return true;
        }

        return false;
    }*/



    /*=====================================================================================================================
    =====================================================================================================================
    =====================================================================================================================*/

    /*获取参数名：用户名、密码*/
    /**
     * @method: getParameterNameCollection
     * @Param: [totalQuestionTableId]
     * @return: java.util.List<java.lang.String>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("/getParameterNameCollection")
    public List<String> getParameterNameCollection(Long totalQuestionTableId){
        //根据问题ID 获取问题表数据
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(totalQuestionTableId);
        //如果问题表数据没有定义 扫描命令的ID 则 返回null
        if (totalQuestionTable == null
                || totalQuestionTable.getCommandId() == null
                || totalQuestionTable.getCommandId().equals("")){
            return null;
        }
        //扫描命令的ID
        String commandIdString = totalQuestionTable.getCommandId();
        //hashset 获得 参数名 唯一
        HashSet<String> parameterName = new HashSet<>();
        do {
            List<CommandLogic> commandLogics = new ArrayList<>();
            String[] commandIdSplit = commandIdString.split(":");
            commandIdString = "";
            for (String id:commandIdSplit){
                CommandLogic commandLogic = commandLogicService.selectCommandLogicById(id);
                commandLogics.add(commandLogic);
            }

            for (CommandLogic commandLogic:commandLogics){
                if (!(commandLogic.getProblemId().equals("0"))){
                    List<ProblemScanLogic> problemScanLogicList = problemScanLogicList(commandLogic.getProblemId());
                    for (ProblemScanLogic problemScanLogic:problemScanLogicList){
                        //parameterName
                        if (problemScanLogic.getWordName()!=null){
                            parameterName.add(problemScanLogic.getWordName());
                        }

                        if (problemScanLogic.getfComId()!=null){
                            commandIdString = commandIdString +problemScanLogic.getfComId() +":";
                        }else if (problemScanLogic.gettComId()!=null){
                            commandIdString = commandIdString +problemScanLogic.gettComId() +":";
                        }
                    }
                }

            }
            if (commandIdString.indexOf(":")!=-1){
                commandIdString = commandIdString.substring(0,commandIdString.length()-1);
            }
        }while (!(commandIdString.equals("")));

        List<String> parameterNameList = new ArrayList<>();
        for (String name:parameterName){
            parameterNameList.add(name);
        }
        return parameterNameList;
    }

    /**
     * @method: 根据 首分析ID 获取全部分析 并拆分 成功失败合实体类
     * @Param: [problemScanLogicID]
     * @return: java.util.List<com.sgcc.sql.domain.ProblemScanLogic>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("problemScanLogicList")
    public List<ProblemScanLogic> problemScanLogicList(String problemScanLogicID){
        //String problemScanLogicID = "1649726283752";
        boolean contain = false;
        HashSet<String> problemScanLogicIDList = new HashSet<>();
        problemScanLogicIDList.add(problemScanLogicID);
        do {
            String  problemScanID = "";
            String[] problemScanLogicIDsplit = problemScanLogicID.split(":");
            for (String id:problemScanLogicIDsplit){
                ProblemScanLogic problemScanLogic = problemScanLogicService.selectProblemScanLogicById(id);
                if (problemScanLogic ==null){
                    return null;
                }
                if (problemScanLogic.gettNextId()!="" && problemScanLogic.gettNextId()!=null && !(isContainChinese(problemScanLogic.gettNextId()))){
                    problemScanID += problemScanLogic.gettNextId()+":";
                }
                if (problemScanLogic.getfNextId()!="" && problemScanLogic.getfNextId()!=null && !(isContainChinese(problemScanLogic.getfNextId()))){
                    problemScanID += problemScanLogic.getfNextId()+":";
                }
            }

            if (problemScanID.equals("")){
                break;
            }

            String[] problemScanIDsplit = problemScanID.split(":");
            problemScanID = "";
            for (String id:problemScanIDsplit){
                for (String hashSetid:problemScanLogicIDList){
                    if (!(id.equals(hashSetid))){
                        problemScanLogicIDList.add(id);
                        problemScanID += id+":";
                    }
                    break;
                }
            }

            if (!(problemScanID.equals(""))){
                contain = true;
                problemScanLogicID = problemScanID.substring(0,problemScanID.length()-1);
            }else {
                contain = false;
            }
        }while (contain);

        List<ProblemScanLogic> ProblemScanLogicList = new ArrayList<>();
        for (String id:problemScanLogicIDList){
            ProblemScanLogic problemScanLogic = problemScanLogicService.selectProblemScanLogicById(id);
            ProblemScanLogicList.add(problemScanLogic);
        }
        List<ProblemScanLogic> ProblemScanLogics = new ArrayList<>();
        for (ProblemScanLogic problemScanLogic:ProblemScanLogicList){

            if (problemScanLogic.getfLine()!=null){
                ProblemScanLogic problemScanLogicf = new ProblemScanLogic();
                problemScanLogicf.setId(problemScanLogic.getId());
                problemScanLogicf.setfLine(problemScanLogic.getfLine());
                problemScanLogicf.setfNextId(problemScanLogic.getfNextId());
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
     * 根据正则表达式判断字符是否为汉字
     */
    public static boolean isContainChinese( String str) {
        String regex = "[\u4e00-\u9fa5]";   //汉字的Unicode取值范围
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(str);
        return match.find();
    }

}
