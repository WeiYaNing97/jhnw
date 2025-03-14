package com.sgcc.sql.controller;
import com.alibaba.fastjson.JSON;
import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.bean.BeanUtils;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.domain.*;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.service.IReturnRecordService;
import com.sgcc.share.service.ISwitchInformationService;
import com.sgcc.share.service.ISwitchScanResultService;
import com.sgcc.share.switchboard.ConnectToObtainInformation;
import com.sgcc.share.util.*;
import com.sgcc.sql.domain.*;
import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.sql.service.*;
import com.sgcc.sql.thread.RepairFixedThreadPool;
import com.sgcc.share.webSocket.WebSocketService;
import com.sgcc.sql.util.InspectionMethods;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 与交换机交互方法类
 * @date 2022年01月05日 14:18
 */
@Api(tags = "修复问题管理")
@RestController
@RequestMapping("/sql/SolveProblemController")
@Transactional(rollbackFor = Exception.class)
public class SolveProblemController {

    @Autowired
    private  ICommandLogicService commandLogicService;
    @Autowired
    private  IValueInformationService valueInformationService;
    @Autowired
    private  IReturnRecordService returnRecordService;
    @Autowired
    private  ITotalQuestionTableService totalQuestionTableService;
    @Autowired
    private  ISwitchProblemService switchProblemService;
    @Autowired
    private  ISwitchScanResultService switchScanResultService;
    @Autowired
    private ISwitchInformationService switchInformationService;

    /**
     * @method: 根据问题ID 查询 解决问题ID命令 返回List<String>
     * @Param: [totalQuestionTableId]
     * @return: com.sgcc.common.core.domain.AjaxResult
     *
     */
    @GetMapping("/queryCommandListBytotalQuestionTableId/{totalQuestionTableId}")
    @ApiOperation("根据交换机问题ID查询修复异常命令")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "totalQuestionTableId",value = "交换机问题ID", dataTypeClass = String.class, required = true)
    })
    public List<String> queryCommandListBytotalQuestionTableId(@PathVariable String totalQuestionTableId){

        /*根据ID 查询问题表数据*/
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(totalQuestionTableId);

        if (totalQuestionTable.getProblemSolvingId() == null){

            //传输登陆人姓名 及问题简述

            AbnormalAlarmInformationMethod.afferent(null, SecurityUtils.getLoginUser().getUsername(), null,
                    "错误:交换机问题表修复命令ID为空\r\n");
            return new ArrayList<>();
        }

        /*修复命令ID*/
        String problemSolvingId = totalQuestionTable.getProblemSolvingId();
        /*定义命令集合*/
        List<CommandLogic> commandLogicList = new ArrayList<>();
        do {
            commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
            CommandLogic commandLogic = commandLogicService.selectCommandLogicById(problemSolvingId);
            commandLogicList.add(commandLogic);
            /*修复逻辑只有命令,所以下一ID只能是命令ID ： EndIndex*/
            problemSolvingId = commandLogic.getEndIndex();
        }while (!(problemSolvingId.equals("0")));

        /*自定义分隔符*/
        String customDelimiter = (String) CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());

        List<String> commandLogicStringList = new ArrayList<>();
        for (CommandLogic commandLogic:commandLogicList){
            String string = InspectionMethods.commandLogicString(commandLogic);
            String[] split = string.split(customDelimiter);
            commandLogicStringList.add(split[1]);
        }
        return commandLogicStringList;
    }


    /**
    * @Description 根据提交信息，修复交换机问题  前后端交互接口
    * @author charles
    * @createTime 2024/5/8 14:23
    * @desc
    * @param userinformation  交换机登录信息
     * @param problemIdList  交换机扫描结果ID
     * @param scanNum  线程数
     * @param allProIdList  交换机所有扫描结果ID
     * @return
    */
    @ApiOperation("修复异常接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "userinformation",value = "交换机登录信息", dataTypeClass = List.class, required = true),
            @ApiImplicitParam(name = "problemIdList",value = "交换机扫描结果ID", dataTypeClass = List.class, required = true),
            @ApiImplicitParam(name = "scanNum",value = "线程数", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "allProIdList",value = "交换机所有扫描结果ID", dataTypeClass = List.class, required = false)
    })
    @PostMapping(value = {"batchSolutionMultithreading/{problemIdList}/{scanNum}/{allProIdList}","batchSolutionMultithreading/{problemIdList}/{scanNum}"})
    @MyLog(title = "修复异常", businessType = BusinessType.OTHER)
    public String batchSolutionMultithreading(@RequestBody List<Object> userinformation,
                                            @PathVariable  List<String> problemIdList,
                                            @PathVariable  String scanNum,
                                            @PathVariable(value = "allProIdList",required = false)  List<String> allProIdList) {

        /**交换机用户信息String 格式
            set集合去重 */
        Set<String> userHashSet = userinformation.stream().map(Object::toString).collect(Collectors.toSet());

        /** 交换机登录信息 装换成 Json格式 并存储实体类集合*/
        List<SwitchParameters> switchParametersList = new ArrayList<>();
        /** 创建一个 交换机IP和交换机问题一一对应的map集合*/
        HashMap<String,List<SwitchScanResult>> switchScanResultMap = new HashMap<>();
        // 迭代器遍历交换机用户信息HashSet
        Iterator<String> iterator = userHashSet.iterator();
        String simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());/*扫描时间*/
        while(iterator.hasNext()){
            /* 交换机登录信息 转化为 交换机登录信息实体类 */
            SwitchLoginInformation switchLoginInformation = JSON.parseObject(iterator.next(), SwitchLoginInformation.class);
            // 四个参数 设默认值
            SwitchParameters switchParameters = new SwitchParameters();
            switchParameters.setLoginUser(SecurityUtils.getLoginUser());/*系统登录用户信息*/
            switchParameters.setScanningTime(simpleDateFormat);/*扫描时间*/

            BeanUtils.copyBeanProp(switchParameters,switchLoginInformation);
            switchParameters.setPort(Integer.valueOf(switchLoginInformation.getPort()).intValue());//连接端口号

            /*交换机密码 配置密码*/
            switchParameters.setPassword(EncryptUtil.desaltingAndDecryption(switchParameters.getPassword()));
            switchParameters.setConfigureCiphers(EncryptUtil.desaltingAndDecryption(switchParameters.getConfigureCiphers()));

            //以多线程中的格式 存放数组中
            //连接方式，ip，用户名，密码，端口号
            switchParametersList.add(switchParameters);

            /** 将交换机IP 存入 map集合*/
            switchScanResultMap.put(switchParameters.getIp(),new ArrayList<>());
        }


        /** 需要修复的问题ID数组
          目前可以修复的只有自定义问题，即安全配置*/
        /* 交换机扫描结果ID 转变为 Long类型*/
        Long[] ids = problemIdList.stream().map(m ->Integer.valueOf(m).longValue()).toArray(Long[]::new);

        /*
         * 根据 需要修复的问题ID数组  查询 扫描出的问题
         * 根据ID集合 查询所有 扫描结果SwitchProblem 数据*/
        switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);
        List<SwitchScanResult> switchScanResultList = switchScanResultService.selectSwitchScanResultByIds(ids);

        /* 交换机问题 */
        /* 遍历 交换机基本信息 */
        for (SwitchScanResult switchScanResult:switchScanResultList){
            List<SwitchScanResult> switchScanResults = switchScanResultMap.get(switchScanResult.getSwitchIp().split(":")[0]);
            switchScanResults.add(switchScanResult);
            switchScanResultMap.put(switchScanResult.getSwitchIp().split(":")[0] , switchScanResults );
        }

        ParameterSet parameterSet = new ParameterSet();
        parameterSet.setSwitchParameters(switchParametersList);/*修复交换机集合*/
        parameterSet.setThreadCount(Integer.valueOf(scanNum+"").intValue());
        parameterSet.setLoginUser(SecurityUtils.getLoginUser());

        try {
            /*交换机所有扫描结果ID*/
            if (allProIdList != null || allProIdList.size() != 0){
                problemIdList = allProIdList;
            }
            RepairFixedThreadPool.Solution(parameterSet, switchScanResultMap, problemIdList);//scanNum
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        AbnormalAlarmInformationMethod.afferent(null, parameterSet.getLoginUser().getUsername(), null,
                "接收:修复结束\r\n");

        return "修复结束" ;
    }



    /**
    * @Description
    * @author charles
    * @createTime 2024/5/8 15:52
    * @desc
     * @param switchParameters	交换机登录信息
     * @param switchScanResultList	 交换机问题集合
     * @param problemIds	所有问题ID
     * @return
    */
    public AjaxResult batchSolution(SwitchParameters switchParameters, List<SwitchScanResult> switchScanResultList , List<String> problemIds){

        /* 连接交换机 requestConnect 方法：
        传入参数：[mode 连接方式, ip IP地址, name 用户名, password 密码, port 端口号,
            connectMethod ssh连接方法, telnetSwitchMethod telnet连接方法]
        返回信息为：[是否连接成功,mode 连接方式, ip IP地址, name 用户名, password 密码, port 端口号,
            connectMethod ssh连接方法 或者 telnetSwitchMethod telnet连接方法（其中一个，为空者不存在）] */
        ConnectToObtainInformation connectToObtainInformation = new ConnectToObtainInformation();
        AjaxResult requestConnect_ajaxResult = connectToObtainInformation.requestConnect( switchParameters );

        //如果返回为 交换机连接失败 则连接交换机失败
        if(requestConnect_ajaxResult.get("msg").equals("交换机连接失败")){
            List<String> loginError = (List<String>) requestConnect_ajaxResult.get("loginError");
            if (loginError != null || loginError.size() != 0){
                for (int number = 1;number<loginError.size();number++){
                    String loginErrorString = loginError.get(number);
                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "交换机连接",
                            "风险:"+switchParameters.getIp()+loginErrorString+"\r\n");
                }
            }
            AbnormalAlarmInformationMethod.afferent(null, null, "交换机连接",
                    "风险:"+switchParameters.getIp() + "交换机连接失败\r\n");
            return AjaxResult.error("交换机连接失败");
        }



        switchParameters = (SwitchParameters) requestConnect_ajaxResult.get("data");
        //是否连接成功
        //boolean requestConnect_boolean = requestConnect_ajaxResult.get("msg").equals("操作成功");//(boolean) objectMap.get("TrueAndFalse");
        if (requestConnect_ajaxResult.get("msg").equals("操作成功")){
            //获取交换机基本信息
            //getBasicInformationList 通过 特定方式 获取 基本信息
            //getBasicInformationList 通过扫描方式 获取 基本信息
            AjaxResult basicInformationList_ajaxResult = connectToObtainInformation.getBasicInformationCurrency(switchParameters);
            if (basicInformationList_ajaxResult == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
                //关闭连接
                if (switchParameters.getMode().equalsIgnoreCase("ssh")){
                    switchParameters.getConnectMethod().closeConnect(switchParameters.getSshConnect());
                }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
                    switchParameters.getTelnetSwitchMethod().closeSession(switchParameters.getTelnetComponent());
                }
                return null;
            }
            if (basicInformationList_ajaxResult.get("msg").equals("未定义该交换机获取基本信息命令及分析")){
                //关闭连接
                if (switchParameters.getMode().equalsIgnoreCase("ssh")){
                    switchParameters.getConnectMethod().closeConnect(switchParameters.getSshConnect());
                }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
                    switchParameters.getTelnetSwitchMethod().closeSession(switchParameters.getTelnetComponent());
                }
                return AjaxResult.error("未定义该交换机获取基本信息命令及分析");
            }

            /*要修复问题的集合*/
            List<SwitchScanResult> switchScanResultLists = new ArrayList<>();
            for (SwitchScanResult switchScanResult:switchScanResultList){
                //交换机基本信息
                switchInformationService = SpringBeanUtil.getBean(ISwitchInformationService.class);
                SwitchInformation switchInformation = switchInformationService.selectSwitchInformationById(switchScanResult.getSwitchId());
                if (switchScanResult.getSwitchIp().startsWith(switchParameters.getIp())
                        && switchParameters.getDeviceBrand().equals(switchInformation.getBrand())
                        && switchParameters.getDeviceModel().equals(switchInformation.getSwitchType())
                        && switchParameters.getFirmwareVersion().equals(switchInformation.getFirewareVersion())
                        && switchParameters.getSubversionNumber().equals(switchInformation.getSubVersion())){
                    switchScanResultLists.add(switchScanResult);
                }else {

                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                            "错误:"+"问题名称:" +switchScanResult.getTypeProblem()+"-"+switchScanResult.getTemProName()+"-"+switchScanResult.getProblemName()+"交换机基本信息不一致"+"\r\n");

                }
            }

            if (switchScanResultLists.size() == 0){
                //getUnresolvedProblemInformationByIds(loginUser,problemIds);
                if (problemIds != null){
                    getSwitchScanResultListByIds(switchParameters.getLoginUser(),problemIds);
                }


                if (switchParameters.getMode().equalsIgnoreCase("ssh")){
                    switchParameters.getConnectMethod().closeConnect(switchParameters.getSshConnect());
                }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
                    switchParameters.getTelnetSwitchMethod().closeSession(switchParameters.getTelnetComponent());
                }
                return AjaxResult.success("修复结束");
            }

            /*遍历 需要修复的 交换机问题*/
            for (SwitchScanResult switchScanResult:switchScanResultLists){
                /*如果交换机扫描结果中 未定义 修复问题 则 去交换机问题表中查询 并赋值*/
                if (switchScanResult.getComId() == null || switchScanResult.getComId().equals("null")){
                    totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
                    TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(switchScanResult.getProblemId());
                    if (totalQuestionTable.getProblemSolvingId() != null){
                        switchScanResult.setComId(totalQuestionTable.getProblemSolvingId());
                    }else {

                        AbnormalAlarmInformationMethod.afferent(null, switchParameters.getLoginUser().getUsername(), null,
                                "风险:问题名称:" +switchScanResult.getTypeProblem()+
                                        "-"+switchScanResult.getTemProName()+
                                        "-"+switchScanResult.getProblemName()+
                                        "未定义解决问题命令\r\n");

                        continue;
                    }
                }

                //传参 修复问题命令ID
                //返回 命令集合 和 参数集合
                List<CommandLogic> commandLogics = queryCommandSet(switchScanResult.getComId() + "");
                //JDK8新特性测试
                List<String> commandList = commandLogics.stream().map(m -> m.getCommand()).collect(Collectors.toList());
                if (commandList.size() == 0){

                    AbnormalAlarmInformationMethod.afferent(null, switchParameters.getLoginUser().getUsername(), null,
                            "错误:问题名称:"+switchScanResult.getTypeProblem()+
                                    "-"+switchScanResult.getTemProName()+
                                    "-"+switchScanResult.getProblemName()+
                                    "未定义解决问题命令\r\n");

                    continue;
                }

                /*返回  map  参数名 ： 参数值*/
                HashMap<String, String> valueHashMap = separationParameters(switchScanResult.getDynamicInformation());
                //执行解决问题
                String solveProblem = solveProblem(switchParameters,switchScanResult, commandList,valueHashMap);//userName

                if (solveProblem.equals("成功")){
                    switchScanResult.setIfQuestion("已解决");
                    // 根据 问题ID  查询 扫描出的问题
                    switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);
                    /*根据ID集合 查询所有  SwitchProblem 数据*/
                    int i = switchScanResultService.updateSwitchScanResult(switchScanResult);
                    if (i<=0){

                        AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                                "错误:问题名称:"+switchScanResult.getTypeProblem()+
                                        "-"+switchScanResult.getTemProName()+
                                        "-"+switchScanResult.getProblemName()+"修复失败\r\n");

                    }
                }

                if(solveProblem.indexOf("错误") != -1){

                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                            "错误:问题名称:" +switchScanResult.getTypeProblem()+
                                    "-"+switchScanResult.getTemProName()+
                                    "-"+switchScanResult.getProblemName()+"修复失败\r\n");

                }

                //getUnresolvedProblemInformationByIds(loginUser,problemIds);
                if (problemIds != null){
                    getSwitchScanResultListByIds(switchParameters.getLoginUser(),problemIds);
                }
            }

            //getUnresolvedProblemInformationByIds(loginUser,problemIds);
            if (problemIds != null){
                getSwitchScanResultListByIds(switchParameters.getLoginUser(),problemIds);
            }

            if (switchParameters.getMode().equalsIgnoreCase("ssh")){
                switchParameters.getConnectMethod().closeConnect(switchParameters.getSshConnect());
            }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
                switchParameters.getTelnetSwitchMethod().closeSession(switchParameters.getTelnetComponent());
            }

            return AjaxResult.success("修复结束");
        }

        return AjaxResult.error("交换机连接失败");
    }


    /**
     * 获取  key为参数名 value为参数值 的 map集合
     * @param dynamicInformation
     * @return  key : value   ->  用户名 : admin
     */
    public HashMap<String,String> separationParameters(StringBuffer dynamicInformation) {
        //几个参数中间的 参数是 以  "=:=" 来分割的
        //设备型号=:=是=:=S3600-28P-EI=:=设备品牌=:=是=:=H3C=:=内部固件版本=:=是=:=3.10,=:=子版本号=:=是=:=1510P09=:=
        /*自定义分隔符*/
        String customDelimiter = (String) CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());

        List<String> parameterStringsplit_List =StringBufferUtils.stringBufferSplit(dynamicInformation,"\r\n");

        HashMap<String,String> valueHashMap = new HashMap<>();
        //判断提取参数 是否为空
        if (parameterStringsplit_List.size()>0){
            //考虑到 需要获取 参数 的ID 所以要从参数组中获取第一个参数的 ID
            //所以 参数组 要倒序插入
            for (int number=parameterStringsplit_List.size()-1;number>0;number--){
                //插入参数
                //用户名=:=是=:=admin=:=密码=:=否=:=$c$3$ucuLP5tRIUiNMSGST3PKZPvR0Z0bw2/g=:=
                String setDynamicInformation=parameterStringsplit_List.get(number);
                String information = setDynamicInformation;//动态信息
                number = number - 2;
                String name = parameterStringsplit_List.get(number);//动态信息名称
                valueHashMap.put(name,information);
            }
        }
        return valueHashMap;
    }


    /**
     * 查询修复命令集合
     * @method: 根据 命令ID commandId 查询命令集合 用于解决问题
     * @Param: [commandId]
     * @return: com.sgcc.common.core.domain.AjaxResult
     */
    @ApiOperation(value = "查询修复异常命令集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commandId", value = "修复命令ID", dataTypeClass = String.class, required = true)
    })
    @GetMapping("queryCommandSet")
    public List<CommandLogic> queryCommandSet(String commandId){
        List<CommandLogic> commandLogicList = new ArrayList<>();
        do {
            commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
            CommandLogic commandLogic = commandLogicService.selectCommandLogicById(commandId);
            commandLogicList.add(commandLogic);
            commandId = commandLogic.getEndIndex();
        }while (!(commandId.equals("0")));
        return commandLogicList;
    }

    /**
     * @method: 执行解决问题
     * @Param: [parameterID]
     * @return: com.sgcc.common.core.domain.AjaxResult
     */
    //@RequestMapping("solveProblem")
    public String solveProblem(SwitchParameters switchParameters,
                               SwitchScanResult switchScanResult,
                               List<String> commandList,
                               HashMap<String,String> valueHashMap){
        //遍历命令集合    根据参数名称 获取真实命令
        //local-user:用户名
        //password cipher:密码
        for (int num = 0;num<commandList.size();num++){
            String[] command_split = commandList.get(num).split(":");
            // command_split.length>1  说明有参数名称
            if (command_split.length>1){
                //获取参数名称
                String value_string= command_split[command_split.length-1];
                String command_sum = command_split[0] + " "+ valueHashMap.get(value_string);
                commandList.set(num,command_sum);
            }else {
                commandList.set(num,command_split[0]);
            }
        }

        StringBuffer commandString = new StringBuffer(); //预设交换机返回结果

        //user_String, connectMethod, telnetSwitchMethod
        for (String command:commandList){
            //根据 连接方法 判断 实际连接方式
            //并发送命令 接受返回结果
            //创建 存储交换机返回数据 实体类
            ReturnRecord returnRecord = new ReturnRecord();
            returnRecord.setSwitchIp(switchParameters.getIp());
            returnRecord.setUserName(switchParameters.getLoginUser().getUsername());
            returnRecord.setBrand(switchParameters.getDeviceBrand());
            returnRecord.setType(switchParameters.getDeviceModel());
            returnRecord.setFirewareVersion(switchParameters.getFirmwareVersion());
            returnRecord.setSubVersion(switchParameters.getSubversionNumber());
            returnRecord.setCurrentCommLog(command.trim());

            int insert_id = 0;
            boolean deviceBrand = true;
            do {
                deviceBrand = true;
                if (switchParameters.getMode().equalsIgnoreCase("ssh")){


                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                            switchParameters.getIp()+"发送:"+command+"\r\n");

                    //command
                    /*根据交换机信息类 与 具体命令，执行并返回交换机返回信息
                     * 返回结果
                     * 如果交换机返回信息错误，则返回信息为 null*/
                    ExecuteCommand executeCommand = new ExecuteCommand();
                    List<String> stringList = executeCommand.executeScanCommandByCommand(switchParameters, command);
                    if (MyUtils.isCollectionEmpty(stringList)){
                        //交换机返回信息错误 导致 方法返回值为null
                        //所谓 修复失败 则返回错误
                        return switchParameters.getIp()+": 问题 ："+switchScanResult.getProblemName() +":" +command+ "错误:交换机返回错误信息";
                    }
                    StringBuffer stringBuffer = new StringBuffer();
                    for (String string:stringList){
                        stringBuffer.append(string).append("\r\n");
                    }
                    commandString = StringBufferUtils.substring(stringBuffer,0,stringBuffer.length()-2);
                    //commandString = switchParameters.getConnectMethod().sendCommand(switchParameters.getIp(),switchParameters.getSshConnect(),command,null);
                    //commandString = Utils.removeLoginInformation(commandString);
                }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){

                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                            switchParameters.getIp()+"发送:"+command+"\r\n");

                    List<String> stringList = switchParameters.getTelnetSwitchMethod().sendCommand(switchParameters.getIp(), switchParameters.getTelnetComponent(), command, null);
                    StringBuffer stringBuffer = new StringBuffer();
                    for (String string : stringList) {
                        stringBuffer.append(string).append("\r\n");
                    }
                    commandString = StringBufferUtils.substring(stringBuffer,0,stringBuffer.length()-2);
                    //commandString = Utils.removeLoginInformation(commandString);
                }

                System.err.println("\r\n"+"命令："+command+"\r\n交换机返回信息:\r\n"+commandString+"\r\n");
                returnRecord.setCurrentReturnLog(commandString);
                //粗略查看是否存在 故障 存在故障返回 false 不存在故障返回 true
                boolean switchfailure = FunctionalMethods.switchfailure(switchParameters, commandString);
                // 存在故障返回 false
                if (!switchfailure) {

                    List<String> commandStringSplit = StringBufferUtils.stringBufferSplit(commandString,"\r\n");

                    for (String returnString : commandStringSplit) {
                        deviceBrand = FunctionalMethods.switchfailure(switchParameters,new StringBuffer(returnString));
                        if (!deviceBrand) {
                            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                                    "故障:"+switchParameters.getIp()+":"+returnString+"\r\n");

                            returnRecord.setCurrentIdentifier(switchParameters.getIp()+ "出现故障:"+returnString+"\r\n");
                            if (switchParameters.getMode().equalsIgnoreCase("ssh")){
                                //command
                                /*根据交换机信息类 与 具体命令，执行并返回交换机返回信息
                                 * 返回结果
                                 * 如果交换机返回信息错误，则返回信息为 null*/
                                ExecuteCommand executeCommand = new ExecuteCommand();
                                List<String> stringList = executeCommand.executeScanCommandByCommand(switchParameters, " ");

                                if (MyUtils.isCollectionEmpty(stringList)){
                                    //交换机返回信息错误 导致 方法返回值为null
                                    //所谓 修复失败 则返回错误
                                    return switchParameters.getIp()+": 问题 ："+switchScanResult.getProblemName() +":" +command+ "错误:交换机返回错误信息";
                                }

                                StringBuffer stringBuffer = new StringBuffer();
                                for (String string:stringList){
                                    stringBuffer.append(string).append("\r\n");
                                }

                                commandString = StringBufferUtils.substring(stringBuffer,0,stringBuffer.length()-2);


                                //switchParameters.getConnectMethod().sendCommand(switchParameters.getIp(),switchParameters.getSshConnect()," ",switchParameters.getNotFinished());
                            }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
                                switchParameters.getTelnetSwitchMethod().sendCommand(switchParameters.getIp(),switchParameters.getTelnetComponent()," ",switchParameters.getNotFinished());
                            }
                            break;
                        }
                    }
                }
                //返回信息表，返回插入条数
                returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);
                insert_id = returnRecordService.insertReturnRecord(returnRecord);
            }while (!deviceBrand);

            //返回信息表，返回插入条数
            returnRecord = returnRecordService.selectReturnRecordById(Integer.valueOf(insert_id).longValue());
            //去除其他 交换机登录信息
            commandString = FunctionalMethods.removeLoginInformation(commandString);

            //交换机返回信息 修整字符串  去除多余 "\r\n" 连续空格 为插入数据美观
            commandString = StringBufferUtils.arrange(commandString);

            //交换机返回信息 按行分割为 字符串数组
            List<String> commandString_split_List = StringBufferUtils.stringBufferSplit(commandString, "\r\n");

            // 返回日志内容
            if (commandString_split_List.size() > 1){
                StringBuffer current_return_log = StringBufferUtils.substring(commandString,0, commandString.length() - commandString_split_List.get(commandString_split_List.size() - 1).length() - 2);

                returnRecord.setCurrentReturnLog(current_return_log);

                //返回日志前后都有\r\n
                StringBuffer current_return_log_substring_end = StringBufferUtils.substring(current_return_log,current_return_log.length() - 2, current_return_log.length());
                if (!current_return_log_substring_end.equals("\r\n")){
                    current_return_log.append("\r\n");
                }
                String current_return_log_substring_start = current_return_log.substring(0, 2);
                if (!current_return_log_substring_start.equals("\r\n")){
                    current_return_log.insert(0,"\r\n");
                }

                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                        switchParameters.getIp()+"接收:"+current_return_log+"\r\n");

                //当前标识符 如：<H3C> [H3C]
                String current_identifier = commandString_split_List.get(commandString_split_List.size() - 1);
                returnRecord.setCurrentIdentifier(current_identifier);
                //当前标识符前后都没有\r\n
                String current_identifier_substring_end = current_identifier.substring(current_identifier.length() - 2, current_identifier.length());
                if (current_identifier_substring_end.equals("\r\n")){
                    current_identifier = current_identifier.substring(0,current_identifier.length()-2);
                }
                String current_identifier_substring_start = current_identifier.substring(0, 2);
                if (current_identifier_substring_start.equals("\r\n")){
                    current_identifier = current_identifier.substring(2,current_identifier.length());
                }

                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                        switchParameters.getIp()+"接收:"+current_identifier+"\r\n");


            }else if (commandString_split_List.size() == 1){
                returnRecord.setCurrentIdentifier("\r\n"+commandString_split_List.get(0)+"\r\n");

                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                        switchParameters.getIp()+"接收:"+commandString_split_List.get(0)+"\r\n");

            }

            //返回信息表，返回插入条数
            int update = returnRecordService.updateReturnRecord(returnRecord);
            //判断命令是否错误 错误为false 正确为true
            if (!(FunctionalMethods.judgmentError( switchParameters,commandString))){

                //  简单检验，命令正确，新命令  commandLogic.getEndIndex()
                List<String> returnString_split_List = StringBufferUtils.stringBufferSplit(commandString, "\r\n");

                for (String string_split:returnString_split_List){
                    if (!FunctionalMethods.judgmentError( switchParameters,new StringBuffer(string_split))){

                        AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                                "风险:"+switchParameters.getIp() +":问题:"+switchScanResult.getProblemName() +"命令:" +command +"错误:"+string_split+"\r\n");

                        List<Object> objectList = new ArrayList<>();
                        objectList.add(AjaxResult.error(switchParameters.getIp()+":问题:"+switchScanResult.getProblemName() +":" +command+ "错误:"+string_split));

                        return switchParameters.getIp()+":问题:"+switchScanResult.getProblemName() +":" +command+ "错误:"+string_split;
                    }
                }
            }
        }

        return "成功";
    }



    /**
     * 根据问题ID列表获取未解决问题的信息
     *
     * @param loginUser 登录用户信息
     * @param problemIds 问题ID列表
     * @return 返回一个包含转换后的扫描结果实体类的列表
     */
    @ApiOperation("根据问题ID列表获取未解决问题的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginUser", value = "登录用户信息", dataTypeClass = LoginUser.class, required = true),
            @ApiImplicitParam(name = "problemIds", value = "问题ID列表", dataTypeClass = List.class, required = true)
    })
    @GetMapping("getUnresolvedProblemInformationByIds")
    public List<ScanResultsVO> getUnresolvedProblemInformationByIds(LoginUser loginUser,List<String> problemIds){//待测
        Long[] id = problemIds.stream().map(p -> Long.parseLong(p)).toArray(Long[]::new);
        switchProblemService = SpringBeanUtil.getBean(ISwitchProblemService.class);
        List<SwitchProblemVO> switchProblemList = switchProblemService.selectUnresolvedProblemInformationByIds(id);
        if (switchProblemList.size() == 0){
            return new ArrayList<>();
        }
        for (SwitchProblemVO switchProblemVO:switchProblemList){
            Date date1 = new Date();
            switchProblemVO.hproblemId =  Long.valueOf(FunctionalMethods.getTimestamp(date1)+""+ (int)(Math.random()*10000+1)).longValue();
            List<SwitchProblemCO> switchProblemCOList = switchProblemVO.getSwitchProblemCOList();
            for (SwitchProblemCO switchProblemCO:switchProblemCOList){
                valueInformationService = SpringBeanUtil.getBean(IValueInformationService.class);//解决 多线程 service 为null问题
                List<ValueInformationVO> valueInformationVOList = valueInformationService.selectValueInformationVOListByID(switchProblemCO.getValueId());
                for (ValueInformationVO valueInformationVO:valueInformationVOList){
                    Date date2 = new Date();
                    valueInformationVO.hproblemId = Long.valueOf(FunctionalMethods.getTimestamp(date2)+""+ (int)(Math.random()*10000+1)).longValue();
                }
                Date date3 = new Date();
                switchProblemCO.hproblemId = Long.valueOf(FunctionalMethods.getTimestamp(date3)+""+ (int)(Math.random()*10000+1)).longValue();
                switchProblemCO.setValueInformationVOList(valueInformationVOList);
            }
        }
        //将IP地址去重放入set集合中
        HashSet<String> ip_hashSet = new HashSet<>();
        for (SwitchProblemVO switchProblemVO:switchProblemList){
            ip_hashSet.add(switchProblemVO.getSwitchIp());
        }
        //将ip存入回显实体类
        List<ScanResultsVO> scanResultsVOList = new ArrayList<>();
        for (String ip_string:ip_hashSet){
            ScanResultsVO scanResultsVO = new ScanResultsVO();
            scanResultsVO.setSwitchIp(ip_string);
            Date date4 = new Date();
            scanResultsVO.hproblemId = Long.valueOf(FunctionalMethods.getTimestamp(date4)+""+ (int)(Math.random()*10000+1)).longValue();
            scanResultsVOList.add(scanResultsVO);
        }
        for (ScanResultsVO scanResultsVO:scanResultsVOList){
            List<SwitchProblemVO> switchProblemVOList = new ArrayList<>();
            String pinpai = "*";
            String xinghao = "*";
            String banben = "*";
            String zibanben = "*";
            for (SwitchProblemVO switchProblemVO:switchProblemList){
                if (switchProblemVO.getSwitchIp().equals(scanResultsVO.getSwitchIp())){

                    String brand = switchProblemVO.getBrand();
                    if (!(brand .equals("*"))){
                        pinpai = brand;
                    }
                    String switchType = switchProblemVO.getSwitchType();
                    if (!(switchType .equals("*"))){
                        xinghao = switchType;
                    }
                    String firewareVersion = switchProblemVO.getFirewareVersion();
                    if (!(firewareVersion .equals("*"))){
                        banben = firewareVersion;
                    }
                    String subVersion = switchProblemVO.getSubVersion();
                    if (!(subVersion .equals("*"))){
                        zibanben = subVersion;
                    }
                    switchProblemVOList.add(switchProblemVO);
                }
            }
            scanResultsVO.setSwitchIp(scanResultsVO.getSwitchIp());
            scanResultsVO.setShowBasicInfo("("+pinpai+" "+xinghao+" "+banben+" "+zibanben+")");
            scanResultsVO.setSwitchProblemVOList(switchProblemVOList);
        }
        for (ScanResultsVO scanResultsVO:scanResultsVOList){
            List<SwitchProblemVO> switchProblemVOList = scanResultsVO.getSwitchProblemVOList();
            for (SwitchProblemVO switchProblemVO:switchProblemVOList){
                switchProblemVO.setSwitchIp(null);
            }
        }
        WebSocketService webSocketService = new WebSocketService();
        webSocketService.sendMessage("loophole:"+loginUser.getUsername(),scanResultsVOList);
        return scanResultsVOList;
    }



    /**
     * 根据问题ID列表获取交换机扫描结果列表
     *
     * @param loginUser 登录用户信息
     * @param problemIds 问题ID列表
     * @return 包含转换后的扫描结果实体类的列表
     */
    @ApiOperation( "根据问题ID列表获取交换机扫描结果列表" )
    @ApiImplicitParams( {
            @ApiImplicitParam( name = "loginUser", value = "登录用户信息", dataTypeClass = LoginUser.class, required = true ),
            @ApiImplicitParam( name = "problemIds", value = "问题ID列表", dataTypeClass = List.class, required = true)
    })
    @GetMapping("getSwitchScanResultListByIds")
    public List<ScanResultsVO> getSwitchScanResultListByIds(LoginUser loginUser,List<String> problemIds){//待测
        Long[] id = problemIds.stream().map(p -> Long.parseLong(p)).toArray(Long[]::new);

        switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);

        List<SwitchScanResult> list = switchScanResultService.selectSwitchScanResultByIds(id);
        HashMap<Long,SwitchScanResult> hashMap = new HashMap<>();
        for (SwitchScanResult switchScanResult:list){
            hashMap.put(switchScanResult.getId(),switchScanResult);
        }

        List<SwitchProblemVO> switchProblemList = switchScanResultService.selectSwitchScanResultListByIds(id);
        for (SwitchProblemVO switchProblemVO:switchProblemList){
            List<SwitchProblemCO> switchProblemCOList = switchProblemVO.getSwitchProblemCOList();
            for (SwitchProblemCO switchProblemCO:switchProblemCOList){
                switchProblemCO.setHproblemId(Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
                List<ValueInformationVO> valueInformationVOList = new ArrayList<>();
                SwitchScanResult switchScanResult = hashMap.get(switchProblemCO.getQuestionId());
                //提取信息 如果不为空 则有参数
                if (switchScanResult.getDynamicInformation()!=null && !switchScanResult.getDynamicInformation().equals("")){
                    StringBuffer dynamicInformation = switchScanResult.getDynamicInformation();

                    //几个参数中间的 参数是 以  "=:=" 来分割的
                    //设备型号=:=是=:=S3600-28P-EI=:=设备品牌=:=是=:=H3C=:=内部固件版本=:=是=:=3.10,=:=子版本号=:=是=:=1510P09=:=
                    /*自定义分隔符*/
                    String customDelimiter = (String) CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());
                    List<String> dynamicInformationsplit_List = StringBufferUtils.stringBufferSplit(dynamicInformation,customDelimiter);

                    //判断提取参数 是否为空
                    if (dynamicInformationsplit_List.size()>0){
                        //考虑到 需要获取 参数 的ID 所以要从参数组中获取第一个参数的 ID
                        //所以 参数组 要倒序插入
                        for (int number=dynamicInformationsplit_List.size()-1;number>0;number--){
                            //创建 参数 实体类
                            ValueInformationVO valueInformationVO = new ValueInformationVO();
                            //插入参数
                            //用户名=:=是=:=admin=:=密码=:=否=:=$c$3$ucuLP5tRIUiNMSGST3PKZPvR0Z0bw2/g=:=
                            String setDynamicInformation=dynamicInformationsplit_List.get(number);
                            valueInformationVO.setDynamicInformation(setDynamicInformation);
                            --number;
                            String setExhibit=dynamicInformationsplit_List.get(number);
                            valueInformationVO.setExhibit(setExhibit);//是否显示
                            if (setExhibit.equals("否")){
                                String setDynamicInformationMD5 = EncryptUtil.densificationAndSalt(setDynamicInformation);
                                valueInformationVO.setDynamicInformation(setDynamicInformationMD5);//动态信息
                            }
                            --number;
                            valueInformationVO.setDynamicVname(dynamicInformationsplit_List.get(number));//动态信息名称
                            valueInformationVO.setHproblemId(Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
                            valueInformationVOList.add(valueInformationVO);
                        }
                    }
                }
                switchProblemCO.setValueInformationVOList(valueInformationVOList);
            }
        }

        //将IP地址去重放入set集合中
        HashSet<String> ip_hashSet = new HashSet<>();
        for (SwitchProblemVO switchProblemVO:switchProblemList){
            ip_hashSet.add(switchProblemVO.getSwitchIp());
        }

        //将ip存入回显实体类
        List<ScanResultsVO> scanResultsVOList = new ArrayList<>();
        for (String ip_string:ip_hashSet){
            ScanResultsVO scanResultsVO = new ScanResultsVO();
            scanResultsVO.setSwitchIp(ip_string);
            scanResultsVO.setHproblemId(Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
            scanResultsVOList.add(scanResultsVO);
        }

        for (ScanResultsVO scanResultsVO:scanResultsVOList){
            List<SwitchProblemVO> switchProblemVOList = new ArrayList<>();
            String pinpai = "*";
            String xinghao = "*";
            String banben = "*";
            String zibanben = "*";
            for (SwitchProblemVO switchProblemVO:switchProblemList){
                if (switchProblemVO.getSwitchIp().equals(scanResultsVO.getSwitchIp())){
                    String brand = switchProblemVO.getBrand();
                    if (!(brand .equals("*"))){
                        pinpai = brand;
                    }
                    String switchType = switchProblemVO.getSwitchType();
                    if (!(switchType .equals("*"))){
                        xinghao = switchType;
                    }
                    String firewareVersion = switchProblemVO.getFirewareVersion();
                    if (!(firewareVersion .equals("*"))){
                        banben = firewareVersion;
                    }
                    String subVersion = switchProblemVO.getSubVersion();
                    if (!(subVersion .equals("*"))){
                        zibanben = subVersion;
                    }
                    switchProblemVOList.add(switchProblemVO);
                }
            }
            scanResultsVO.setSwitchIp(scanResultsVO.getSwitchIp());
            scanResultsVO.setShowBasicInfo("("+pinpai+" "+xinghao+" "+banben+" "+zibanben+")");
            scanResultsVO.setSwitchProblemVOList(switchProblemVOList);
        }

        for (ScanResultsVO scanResultsVO:scanResultsVOList){
            scanResultsVO.setHproblemId(Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
            List<SwitchProblemVO> switchProblemVOList = scanResultsVO.getSwitchProblemVOList();
            for (SwitchProblemVO switchProblemVO:switchProblemVOList){
                switchProblemVO.setSwitchIp(null);
                switchProblemVO.setHproblemId(Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
            }
        }
        WebSocketService webSocketService = new WebSocketService();
        webSocketService.sendMessage("loophole"+loginUser.getUsername(),scanResultsVOList);
        return scanResultsVOList;
    }
}