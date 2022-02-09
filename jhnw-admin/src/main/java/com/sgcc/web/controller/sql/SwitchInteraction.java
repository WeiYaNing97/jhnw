package com.sgcc.web.controller.sql;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.entity.GlobalVariable;
import com.sgcc.common.core.domain.entity.SysUser;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.connect.method.SshMethod;
import com.sgcc.connect.method.TelnetSwitchMethod;
import com.sgcc.framework.web.service.TokenService;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.*;
import com.sgcc.web.controller.webSocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * 与交换机交互方法类
 * @date 2022年01月05日 14:18
 */
@RestController
@RequestMapping("/sql/SwitchInteraction")
public class SwitchInteraction {
    @Autowired
    private ICommandLogicService commandLogicService;
    @Autowired
    private IReturnRecordService returnRecordService;
    @Autowired
    private IProblemScanLogicService problemScanLogicService;
    @Autowired
    private IValueInformationService valueInformationService;
    @Autowired
    private ISwitchProblemService switchProblemService;
    @Autowired
    private ITotalQuestionTableService totalQuestionTableService;
    @Autowired
    private IBasicInformationService basicInformationService;


    //总方法
    @PostMapping("logInToGetBasicInformation")
    public AjaxResult logInToGetBasicInformation(String mode, String ip, String name, String password, int port) {
        List<String> user_String = new ArrayList<>();
        user_String.add(mode);
        user_String.add(ip);
        user_String.add(name);
        user_String.add(password);

        //ssh连接
        SshMethod connectMethod = null;
        //telnet连接
        TelnetSwitchMethod telnetSwitchMethod = null;
        /* requestConnect方法：
        传入参数：[mode 连接方式, ip IP地址, name 用户名, password 密码, port 端口号,
            connectMethod ssh连接方法, telnetSwitchMethod telnet连接方法]
        返回信息为：[是否连接成功,mode 连接方式, ip IP地址, name 用户名, password 密码, port 端口号,
            connectMethod ssh连接方法 或者 telnetSwitchMethod telnet连接方法（其中一个，为空者不存在）] */
        AjaxResult requestConnect_ajaxResult = requestConnect(mode, ip, name, password, port, connectMethod, telnetSwitchMethod);
        //解析返回参数
        List<Object> objectList = (List<Object>) requestConnect_ajaxResult.get("data");
        //是否连接成功
        boolean requestConnect_boolean = objectList.get(0).toString().equals("true");
        //如果连接成功
        if(requestConnect_boolean){
            //连接方式：ssh 或 telnet
            String requestConnect_way = objectList.get(1).toString();
            //如果连接方式为ssh则 连接方法返回集合最后一个参数为 connectMethod参数
            //如果连接方式为telnet则 连接方法返回集合最后一个参数为 telnetSwitchMethod参数
            if (requestConnect_way.equalsIgnoreCase("ssh")){
                connectMethod = (SshMethod)objectList.get(6);
            }else if (requestConnect_way.equalsIgnoreCase("telnet")){
                telnetSwitchMethod = (TelnetSwitchMethod)objectList.get(6);
            }

            //获取基本信息
            AjaxResult basicInformationList_ajaxResult = getBasicInformationList(user_String,requestConnect_way, connectMethod, telnetSwitchMethod);
            AjaxResult ajaxResult = scanProblem(basicInformationList_ajaxResult, //AjaxResult 交换机的基本信息 //设备型号 设备品牌 内部固件版本 子版本号
                    user_String, //登录交换机的 用户信息 登录方式、ip、name、password
                    requestConnect_way, connectMethod, telnetSwitchMethod);
            return basicInformationList_ajaxResult;
        }
        return AjaxResult.error("连接交换机失败！");
    }

    @PostMapping("scanProblem")
    public AjaxResult scanProblem(AjaxResult basicInformationList_ajaxResult, //AjaxResult 交换机的基本信息 //设备型号 设备品牌 内部固件版本 子版本号
                                  List<String> user_String, //登录交换机的 用户信息 登录方式、ip、name、password
                                  String requestConnect_way,SshMethod connectMethod,TelnetSwitchMethod telnetSwitchMethod){
        /*List<String> basicInformation_List = (List<String>) basicInformationList_ajaxResult.get("data");
        String deviceModel = basicInformation_List.get(0);//设备型号
        String deviceBrand = basicInformation_List.get(1);//设备品牌
        String firmwareVersion = basicInformation_List.get(2);//内部固件版本
        String subversionNumber = basicInformation_List.get(3);//子版本号*/

        HashMap<String,String> basicInformation_List = (HashMap<String,String>) basicInformationList_ajaxResult.get("data");
        String deviceModel = basicInformation_List.get("xinghao");//设备型号
        String deviceBrand = basicInformation_List.get("pinpai");//设备品牌
        String firmwareVersion = basicInformation_List.get("banben");//内部固件版本
        String subversionNumber = basicInformation_List.get("zibanben");//子版本号

        //获取可执行命令ID
        AjaxResult commandIdByInformation_ajaxResult = commandIdByInformation(deviceModel, deviceBrand, firmwareVersion, subversionNumber);
        List<TotalQuestionTable> commandIdByInformation_comandID_Long = (List<TotalQuestionTable>) commandIdByInformation_ajaxResult.get("data");
        String command_return_information = null;
        Long analysis_id = null;
        for (TotalQuestionTable totalQuestionTable:commandIdByInformation_comandID_Long){
            List<Object> executeScanCommandByCommandId_object = executeScanCommandByCommandId(totalQuestionTable.getCommandId(), requestConnect_way, connectMethod, telnetSwitchMethod);
            command_return_information = executeScanCommandByCommandId_object.get(0).toString();
            analysis_id = (Long) executeScanCommandByCommandId_object.get(1);
            String analysisReturnResults_String = analysisReturnResults(user_String,
                    requestConnect_way, connectMethod, telnetSwitchMethod,
                    command_return_information, analysis_id,firmwareVersion,totalQuestionTable.getIfCycle());
            System.err.print("\r\nanalysisReturnResults_String:\r\n"+analysisReturnResults_String);
        }
        return null;
    }


    /**
     * 连接交换机方法
     * @method: 连接交换机
     * @Param: [mode 连接方式, ip IP地址, name 用户名, password 密码, port 端口号,]
     * @Param: [connectMethod ssh连接方法, telnetSwitchMethod telnet连接方法]
     * @E-mail: WeiYaNing97@163.com
     *
     * 通过参数参数 [mode 连接方式, ip IP地址, name 用户名, password 密码, port 端口号,] 连接交换机，
     * 如果是ssh连接 则connectMethod 属性值存在，用于通过ssh连接 ，telnetSwitchMethod为空
     * 如果是telnet连接 则telnetSwitchMethod 属性值存在，用于通过ssh连接 ，connectMethod为空
     *返回信息为 是否连接成功 + 全部传入参数 ，此时 connectMethod(telnetSwitchMethod) 已经连接交换机
     */
    @RequestMapping("requestConnect")
    public AjaxResult requestConnect(String mode, String ip, String name, String password, int port,
                                     SshMethod connectMethod, TelnetSwitchMethod telnetSwitchMethod) {
        String way = mode;//连接方法
        String hostIp = ip;//ip地址
        int portID = port;//端口号
        String userName = name;//姓名
        String userPassword = password;//密码
        //设定连接结果 预设连接失败为 false
        boolean is_the_connection_successful =false;
        if (way.equalsIgnoreCase("ssh")){
            //创建ssh连接方法
            connectMethod = new SshMethod();
            //连接ssh
            is_the_connection_successful = connectMethod.requestConnect(hostIp, portID, userName, userPassword);
        }else if (way.equalsIgnoreCase("telnet")){
            //创建telnet连接方法
            telnetSwitchMethod = new TelnetSwitchMethod();
            //连接telnet
            is_the_connection_successful = telnetSwitchMethod.requestConnect(hostIp, portID, userName, userPassword, null);
        }

        List<Object> objectList = new ArrayList<>();  //设定返回值 list集合
        objectList.add(is_the_connection_successful); //元素0 ：是否连接成功
        objectList.add(way);                          //元素1 ：连接方法
        objectList.add(hostIp);                       //元素2 ：交换机ID
        objectList.add(userName);                     //元素3 ：交换机登录用户
        objectList.add(userPassword);                 //元素4 ：交换机登录用户密码
        objectList.add(portID);                       //元素5 ：交换机连接端口号
        objectList.add(connectMethod);                //元素6 ：ssh连接对象：如果连接方法为telnet则connectMethod为空，插入connectMethod失败
        objectList.add(telnetSwitchMethod);           //元素6 ：telnet连接对象：如果连接方法为ssh则telnetSwitchMethod为空，插入telnetSwitchMethod失败

        /* 返回信息 ： [是否连接成功,mode 连接方式, ip IP地址, name 用户名, password 密码, port 端口号,
                connectMethod ssh连接方法 或者 telnetSwitchMethod telnet连接方法（其中一个，为空者不存在）]*/
        return AjaxResult.success(objectList);
    }

    /**
     * @method: 获取交换机基本信息
     * @Param: [user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法, connectMethod ssh连接, telnetSwitchMethod telnet连接]
     * @E-mail: WeiYaNing97@163.com
     *
     * 通过null 查询初所有会的交换机基本信息的命令字符串集合
     * 遍历交换机基本信息的命令字符串集合 通过 扫描分析方法 获得所有提取信息
     * 通过返回提取的信息，给基本属性赋值
     * 成功则返回基本信息 否则 遍历下一条 交换机基本信息的命令字符串集合信息
     */
    @GetMapping("/getBasicInformationList")
    public AjaxResult getBasicInformationList(List<String> user_String,String way,SshMethod connectMethod,TelnetSwitchMethod telnetSwitchMethod) {
        //查询 获取基本信息命令表  中的全部命令
        //BasicInformation pojo_NULL = new BasicInformation(); //null
        //根据 null 查询 得到表中所有数据
        List<BasicInformation> basicInformationList = basicInformationService.selectBasicInformationList(null);
        //遍历命令表命令 执行命令
        for (BasicInformation basicInformation:basicInformationList){
            //多个命令是由,号分割的，所以需要根据, 来分割。例如：display device manuinfo,display ver
            String[] commandsplit = basicInformation.getCommand().split(",");
            String commandString =""; //预设交换机返回结果
            String return_sum = ""; //当前命令字符串 返回命令总和("\r\n"分隔)
            //遍历数据表命令 分割得到的 命令数组
            for (String command:commandsplit){
                //根据 连接方法 判断 实际连接方式
                //并发送命令 接受返回结果
                if (way.equalsIgnoreCase("ssh")){

                    WebSocketService.sendMessage("badao",command);
                    commandString = connectMethod.sendCommand(command);
                }else if (way.equalsIgnoreCase("telnet")){

                    WebSocketService.sendMessage("badao",command);
                    commandString = telnetSwitchMethod.sendCommand(command);
                }
                //判断命令是否错误 错误为false 正确为true
                if (!Utils.judgmentError(commandString)){
                    //如果返回信息错误 则结束当前命令，执行 遍历数据库下一条命令字符串(,)
                    break;
                }
                //交换机返回信息 修整字符串  去除多余 "\r\n" 连续空格 为插入数据美观
                commandString = Utils.trimString(commandString);

                //交换机返回信息 按行分割为 字符串数组
                String[] commandString_split = commandString.split("\r\n");
                //创建 存储交换机返回数据 实体类
                ReturnRecord returnRecord = new ReturnRecord();
                // 执行命令赋值
                String commandtrim = command.trim();
                returnRecord.setCurrentCommLog(commandtrim);
                // 返回日志内容
                String current_return_log = commandString.substring(0, commandString.length() - commandString_split[commandString_split.length - 1].length() - 2).trim();
                returnRecord.setCurrentReturnLog(current_return_log);
                //返回日志前后都有\r\n
                String current_return_log_substring_end = current_return_log.substring(current_return_log.length() - 2, current_return_log.length());
                if (!current_return_log_substring_end.equals("\r\n")){
                    current_return_log = current_return_log+"\r\n";
                }
                String current_return_log_substring_start = current_return_log.substring(0, 2);
                if (!current_return_log_substring_start.equals("\r\n")){
                    current_return_log = "\r\n"+current_return_log;
                }

                WebSocketService.sendMessage("badao",current_return_log);
                //当前标识符 如：<H3C> [H3C]
                String current_identifier = commandString_split[commandString_split.length - 1].trim();
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

                WebSocketService.sendMessage("badao",current_identifier);
                //存储交换机返回数据 插入数据库
                int insert_Int = returnRecordService.insertReturnRecord(returnRecord);
                //当前命令字符串 返回命令总和("\r\n"分隔)
                return_sum += commandString+"\r\n\r\n";
            }
            //修整 当前命令字符串 返回信息  去除多余 "\r\n" 连续空格
            //应该可以去除 因为 上面 每个单独命令已经执行过
            // 注释掉 可能会在两条交换机返回信息中 存在 "\r\n\r\n" 情况 按"\r\n"分割可能会出现空白元素
            //String command_String = Utils.trimString(return_sum);

            //分析第一条ID basicInformation.getProblemId()  :  mh300013  mh3(前三个为操作和品牌的缩写) + 00013(为 问题扫描逻辑表  ID)
            Long first_problem_scanLogic_Id = Integer.valueOf(basicInformation.getProblemId().substring(3,basicInformation.getProblemId().length())).longValue();
            //返回总提取信息
            String extractInformation_string1 = analysisReturn(user_String,way, connectMethod, telnetSwitchMethod,
                    return_sum, first_problem_scanLogic_Id,null);

            if (extractInformation_string1.equals("") || extractInformation_string1 == null){
                continue;
            }
            //设备型号
            String deviceModel= "";
            //设备品牌
            String deviceBrand = "";
            //内部固件版本
            String firmwareVersion = "";
            //子版本号
            String subversionNumber = "";
            extractInformation_string1 = extractInformation_string1.replace(",","");
            String[] return_result_split = extractInformation_string1.split("=:=");
            for (int num = 0;num<return_result_split.length;num++){
                //设备型号
                if (return_result_split[num].equals("设备型号")){
                    num++;
                    deviceModel=return_result_split[num];
                }
                //设备品牌
                if (return_result_split[num].equals("设备品牌")) {
                    num++;
                    deviceBrand = return_result_split[num];
                }
                //内部固件版本
                if (return_result_split[num].equals("内部固件版本")) {
                    num++;
                    firmwareVersion = return_result_split[num];
                }
                //子版本号
                if (return_result_split[num].equals("子版本号")) {
                    num++;
                    subversionNumber = return_result_split[num];
                }
                if (!deviceModel.equals("") && !deviceBrand.equals("") && !firmwareVersion.equals("") && !subversionNumber.equals("")){
                    // 根据交换机信息查询 获取 扫描问题的 命令ID
                    List<String> stringList = new ArrayList<>();
                    stringList.add(deviceBrand);
                    stringList.add(deviceModel);
                    stringList.add(firmwareVersion);
                    stringList.add(subversionNumber);
                    /*return AjaxResult.success(stringList);*/

                    WebSocketService.sendMessage("basicinformation",stringList);

                    HashMap<String,String> map = new HashMap<>();
                    map.put("pinpai",deviceBrand);
                    map.put("xinghao",deviceModel);
                    map.put("banben",firmwareVersion);
                    map.put("zibanben",subversionNumber);
                    /*WebSocketService.sendMessage("pinpai",deviceBrand);
                    WebSocketService.sendMessage("xinghao",deviceModel);
                    WebSocketService.sendMessage("banben",firmwareVersion);
                    WebSocketService.sendMessage("zibanben",subversionNumber);*/
                    return AjaxResult.success(map);
                }
            }
        }
        return null;
    }

    /**
     * @method: 获取交换机 基本信息命令 列表 根据分析ID获取问题扫描逻辑详细信息
     * @Param: [user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法, connectMethod ssh连接, telnetSwitchMethod telnet连接]
     * @Param: [resultString 交换机返回信息,first_problem_scanLogic_Id 第一条分析ID,firmwareVersion 内部固件版本号)
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public String analysisReturn(List<String> user_String,String way,SshMethod connectMethod,TelnetSwitchMethod telnetSwitchMethod,
                                 String resultString,Long first_problem_scanLogic_Id,String firmwareVersion){

        //整理返回结果 去除 #
        //测试后无用 暂注释掉   注释掉可能会出现的情况 按行分割后 出现某数组元素只有 # 的情况
        //resultString = resultString.replace("\r\n"+" # "+"\r\n","\r\n");

        //交换机返回结果 按行分割 交换机返回信息字符串
        String[] return_information_array =resultString.split("\r\n");
        //是否循环判断 loop循环 end 单次
        String operation = "end";
        /* 传入参数[user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法, connectMethod ssh连接, telnetSwitchMethod telnet连接,
                交换机返回信息字符串, 单次分析提取数据，循环分析提取数据
                交换机返回信息字符串分析索引位置(光标)，第一条分析ID， 当前分析ID ，是否循环 ，内部固件版本号]
         */
        //设备型号=:=S3600-28P-EI=:=设备品牌=:=H3C=:=内部固件版本=:=3.10,=:=子版本号=:=1510P09=:=
        List<String> strings = selectProblemScanLogicById(user_String,way, connectMethod, telnetSwitchMethod,
                return_information_array, "", "",
                0, first_problem_scanLogic_Id, null, operation,firmwareVersion,true);// loop end

        return strings.get(0);
    }


    /**
     * @method: 根据交换机信息查询 获取 扫描问题的
     * @Param: []
     * @return: java.util.List<java.lang.Long>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @GetMapping(value = "/commandIdByInformation")
    public AjaxResult commandIdByInformation(String deviceModel,String deviceBrand,String firmwareVersion,String subversionNumber)
    {
        TotalQuestionTable totalQuestionTable = new TotalQuestionTable();
        totalQuestionTable.setBrand(deviceBrand);
        totalQuestionTable.setType(deviceModel);
        totalQuestionTable.setFirewareVersion(firmwareVersion);
        totalQuestionTable.setSubVersion(subversionNumber);
        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);
        if (totalQuestionTables!=null){
            return AjaxResult.success(totalQuestionTables);
        }else {
            return null;
        }
    }


    /**
     * @method: 根据命令ID集合的具体命令集合，执行
     * 根据 命令ID 和 连接方式 ssh和telnet连接 获得
     * @Param:
     * @return:
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("/executeScanCommand")
    public void executeScanCommand(List<String> user_String,List<Long> commandIdList,
                                   String way,SshMethod connectMethod,TelnetSwitchMethod telnetSwitchMethod,
                                   String firmwareVersion,String operation) {
        for (Long commandId:commandIdList){
            CommandLogic commandLogic = commandLogicService.selectCommandLogicById(commandId);
            String command = commandLogic.getCommand();
            String command_string = null;
            if (way.equalsIgnoreCase("ssh")){

                WebSocketService.sendMessage("badao",command);
                command_string = connectMethod.sendCommand(command);
            }else if (way.equalsIgnoreCase("telnet")){

                WebSocketService.sendMessage("badao",command);
                command_string = telnetSwitchMethod.sendCommand(command);
            }

            command_string =Utils.trimString(command_string);


            String[] split = command_string.split("\r\n");
            ReturnRecord returnRecord = new ReturnRecord();
            returnRecord.setCurrentCommLog(command.trim());
            //返回日志
            String current_return_log = command_string.substring(0,command_string.length()-split[split.length-1].length()-2).trim();
            returnRecord.setCurrentReturnLog(current_return_log);
            //返回日志前后都有\r\n
            String current_return_log_substring_end = current_return_log.substring(current_return_log.length() - 2, current_return_log.length());
            if (!current_return_log_substring_end.equals("\r\n")){
                current_return_log = current_return_log+"\r\n";
            }
            String current_return_log_substring_start = current_return_log.substring(0, 2);
            if (!current_return_log_substring_start.equals("\r\n")){
                current_return_log = "\r\n"+current_return_log;
            }

            WebSocketService.sendMessage("badao",current_return_log);
            //当前标识符
            String current_identifier = split[split.length-1].trim();
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

            WebSocketService.sendMessage("badao",current_identifier);

            int insert_Int = returnRecordService.insertReturnRecord(returnRecord);
            //判断是否简单检验
            if (commandLogic.getResultCheckId()==1l){
                //判断命令是否错误 错误为false 正确为true
                if (!Utils.judgmentError(command_string)){
                    continue;
                }else {
                    System.err.print("简单检验，命令正确，新命令"+commandLogic.getEndIndex());
                    List<Object> objectList = executeScanCommandByCommandId(commandLogic.getEndIndex(), way, connectMethod, telnetSwitchMethod);
                    System.out.println("命令错误"+objectList.get(1));
                }
            }else {
                Long first_problem_scanLogic_Id = Integer.valueOf(commandLogic.getProblemId().substring(3,commandLogic.getProblemId().length())).longValue();
                System.err.print("\r\n交换机返回信息：\r\n"+command_string);
                // 分析
                String analysisReturnResults_String = analysisReturnResults(user_String,way, connectMethod, telnetSwitchMethod, command_string, first_problem_scanLogic_Id,firmwareVersion,operation);
            }
        }
    }

    /**
     * @method: 根据命令ID获取具体命令，执行
     * @Param:
     * @return:  返回的是 解决问题ID
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     * 分析ID 连接方式 ssh和telnet连接
     */
    @RequestMapping("/executeScanCommandByCommandId")
    public List<Object> executeScanCommandByCommandId(Long commandId,String way,SshMethod connectMethod,TelnetSwitchMethod telnetSwitchMethod) {

        //命令ID获取具体命令
        CommandLogic commandLogic = commandLogicService.selectCommandLogicById(commandId);
        //具体命令
        String command = commandLogic.getCommand();
        //执行命令
        String command_string = null;
        if (way.equalsIgnoreCase("ssh")){

            WebSocketService.sendMessage("badao",command);
            command_string = connectMethod.sendCommand(command);
        }else if (way.equalsIgnoreCase("telnet")){

            WebSocketService.sendMessage("badao",command);
            command_string = telnetSwitchMethod.sendCommand(command);
        }
        //修整返回信息
        command_string =Utils.trimString(command_string);

        //按行切割
        String[] split = command_string.split("\r\n");
        ReturnRecord returnRecord = new ReturnRecord();
        returnRecord.setCurrentCommLog(command.trim());
        String current_return_log =command_string.substring(0,command_string.length()-split[split.length-1].length()-2).trim();
        returnRecord.setCurrentReturnLog(current_return_log);
        //返回日志前后都有\r\n
        String current_return_log_substring_end = current_return_log.substring(current_return_log.length() - 2, current_return_log.length());
        if (!current_return_log_substring_end.equals("\r\n")){
            current_return_log = current_return_log+"\r\n";
        }
        String current_return_log_substring_start = current_return_log.substring(0, 2);
        if (!current_return_log_substring_start.equals("\r\n")){
            current_return_log = "\r\n"+current_return_log;
        }

        WebSocketService.sendMessage("badao",current_return_log);

        //按行切割，最后一位应该是 标识符
        String current_identifier = split[split.length-1].trim();
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

        WebSocketService.sendMessage("badao",current_identifier);
        //返回信息表，返回插入条数
        int insert_Int = returnRecordService.insertReturnRecord(returnRecord);
        //判断是否简单检验 1L为简单校验  默认0L 为分析数据表自定义校验
        Long first_problem_scanLogic_Id = 0l;
        if (commandLogic.getResultCheckId()==1l){
            //判断命令是否错误 错误为false 正确为true
            if (Utils.judgmentError(command_string)){
                System.err.print("\r\n"+"简单检验，命令正确，新命令"+commandLogic.getEndIndex());
                List<Object> objectList = executeScanCommandByCommandId(commandLogic.getEndIndex(), way, connectMethod, telnetSwitchMethod);
                return objectList;
            }
        }else {
            //分析第一条ID
            first_problem_scanLogic_Id = Integer.valueOf(commandLogic.getProblemId().substring(3,commandLogic.getProblemId().length())).longValue();
        }
        List<Object> objectList = new ArrayList<>();
        objectList.add(command_string);
        objectList.add(first_problem_scanLogic_Id);
        return objectList;
    }


    /**
     * @method: 执行分析
     * @Param: [resultString_ProblemScanLogicId]
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     * 用户信息 连接方式 ssh、telnet
     * 交换机返回信息 分析ID
     */
    @RequestMapping("analysisReturnResults")
    public String analysisReturnResults(List<String> user_String,String way,SshMethod connectMethod,TelnetSwitchMethod telnetSwitchMethod,
                                        String resultString,Long first_problem_scanLogic_Id,String firmwareVersion,String operation){

        //整理返回结果 去除 #
        resultString = resultString.replace("\r\n"+" # "+"\r\n","\r\n");
        //将交换机返回信息 按行来切割 字符串数组
        String[] return_information_array =resultString.split("\r\n");
        //获得第一条分析ID
        //因为前三个是 1位为操作类型（取词w、分析a、匹配m） 2,3位为品牌编码；后5位为随机生成的序号；
        //根据第一条分析ID 查询分析信息
        ProblemScanLogic problemScanLogic = problemScanLogicService.selectProblemScanLogicById(first_problem_scanLogic_Id);
        //根据ID去分析
        List<String> problemScanLogic_stringList = selectProblemScanLogicById( user_String,way,connectMethod, telnetSwitchMethod,
                return_information_array,"","",
                0,first_problem_scanLogic_Id,null,operation,firmwareVersion,true);// loop end
        if (problemScanLogic_stringList!=null){
            return problemScanLogic_stringList.get(0);
        }else {
            return null;
        }
    }

    /**
     * @method: * 分析第一条ID 和 是否循环判断 返回是否有错
     * 根据分析ID获取问题扫描逻辑详细信息
     * @Param: [user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法, connectMethod ssh连接, telnetSwitchMethod telnet连接,
     *                 交换机返回信息字符串, 单次分析提取数据，循环分析提取数据
     *                 交换机返回信息字符串分析索引位置(光标)，第一条分析ID， 当前分析ID ，是否循环 ，内部固件版本号]
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     * 用户信息 连接方式 ssh、telnet
     * 返回信息按行分字符串集合  单次提取数据  循环总提取数据
     * 集合行数  第一分析ID 当前循环ID  是否循环 内部固件版本号
     */
    // 是否用首ID ifFirstID 分析首ID firstID   现行ID currentID 是否循环
    public List<String> selectProblemScanLogicById(List<String> user_String,String way,SshMethod connectMethod,TelnetSwitchMethod telnetSwitchMethod,
                                                   String[] return_information_array,String current_Round_Extraction_String,String extractInformation_string,
                                                   int line_n, Long firstID,Long currentID,String operation,String firmwareVersion,
                                                   boolean logic_true_false) {
        //第一条分析ID
        Long id = firstID;
        //如果当前分析ID不为空，则用当前分析ID
        if (currentID != null){
            id = currentID;
        }
        //根据ID查询  分析数据
        ProblemScanLogic problemScanLogic = problemScanLogicService.selectProblemScanLogicById(id);
        //相对位置——行(1,0)
        String relativePosition = problemScanLogic.getRelativePosition();
        String relativePosition_line = relativePosition.split(",")[0];
        //分析数据 的 关键字
        String matchContent = problemScanLogic.getMatchContent();

        //标定从第line_n开始扫描
        //分析数据 相对位置为空 或者 line_n !=0 不为0
        //relativePosition_line = "null" 则从头开始匹配
        //如果 !relativePosition_line.equals("null")  则 根据 relativePosition_line 行来分析

        if (!relativePosition_line.equals("null") || line_n !=0){
            int line_number = 0;
            if (!relativePosition_line.equals("null")){
                line_number = Integer.valueOf(relativePosition_line).intValue();
            }
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
            //比较分析
            String compare = problemScanLogic.getCompare();
            //与或 逻辑
            String logic = problemScanLogic.getLogic();
            // logic_true_false  上一分析的逻辑

            //匹配
            if (!matched.equals("null")){
                //匹配 成功 失败
                boolean matchAnalysis_true_false = Utils.matchAnalysis(matched, string_line_n, matchContent);

                //与或逻辑
                boolean matched_true_false = false;
                if (logic.equalsIgnoreCase("and")){
                    matched_true_false = logic_true_false && matchAnalysis_true_false;
                }else if (logic.equalsIgnoreCase("or")){
                    matched_true_false = logic_true_false || matchAnalysis_true_false;
                }else {
                    matched_true_false = matchAnalysis_true_false;
                }


                //如果成功 则把 匹配成功的行数 付给变量 line_n
                if (matched_true_false){
                    //执行成功逻辑
                    //下一条分析ID+"=:="+当前行数
                    //有问题-问题索引-命令索引
                    //无问题-无问题
                    //待定-下一命令ID
                    String analysis_true_string = analysis_true(way,connectMethod, telnetSwitchMethod,problemScanLogic, num);

                    //不包含：有问题无问题  和  待确定 时 说明 是具体ID 则进行下一步：  加上 不能确定
                    if ((analysis_true_string.indexOf("问题") ==-1) && (analysis_true_string.indexOf("继续") == -1)){
                        //下一条分析ID+"=:="+当前行数
                        String[] analysis_true_split = analysis_true_string.split("=:=");
                        //下一条分析ID
                        analysis_true_string =analysis_true_split[0];
                        //当前行数
                        line_n=Integer.valueOf(analysis_true_split[1]).intValue();
                        //根据下一条分析ID 和 当前行 来进行 下条分析
                        //返回 {总截取信息，true/false流程返回信息}
                        List<String> ProblemScanLogic_returnstring = selectProblemScanLogicById(user_String,way,connectMethod, telnetSwitchMethod,
                                return_information_array,current_Round_Extraction_String,extractInformation_string,
                                line_n,firstID,Integer.valueOf(analysis_true_string).longValue(),operation,firmwareVersion,true);
                        //如果返回信息为null
                        if (ProblemScanLogic_returnstring!=null){
                            //内分析传到上一层
                            extractInformation_string = ProblemScanLogic_returnstring.get(0);
                            return ProblemScanLogic_returnstring;
                        }

                        if (operation.equalsIgnoreCase("loop")&&num<return_information_array.length){

                            if (ProblemScanLogic_returnstring.get(1).indexOf("没问题")!=-1 && line_n != return_information_array.length){
                                line_n = Integer.valueOf(ProblemScanLogic_returnstring.get(1).split("=:=")[1]).intValue();
                            }

                            List<String> loop_string = selectProblemScanLogicById(user_String,way,connectMethod, telnetSwitchMethod,
                                    return_information_array,"",extractInformation_string,
                                    line_n,firstID,null, operation,firmwareVersion,true);
                            if (loop_string!=null){
                                extractInformation_string = loop_string.get(0);
                                return loop_string;
                            }
                            return loop_string;
                        }


                        return ProblemScanLogic_returnstring;
                    }else {
                        if ((analysis_true_string.indexOf("问题") !=-1) && (analysis_true_string.indexOf("继续") != -1)){
                            insertvalueInformationService(user_String,true,problemScanLogic,current_Round_Extraction_String);
                            current_Round_Extraction_String = "";
                            getUnresolvedProblemInformationByData();
                        }

                        if (operation.equalsIgnoreCase("loop")&&num<return_information_array.length){
                            List<String> loop_string = selectProblemScanLogicById(user_String,way,connectMethod, telnetSwitchMethod,
                                    return_information_array,"",extractInformation_string,
                                    line_n,firstID,null, operation,firmwareVersion,true);
                            if (loop_string!=null){
                                extractInformation_string = loop_string.get(0);
                                return loop_string;
                            }
                            return loop_string;
                        }
                        List<String> stringList = new ArrayList<>();
                        stringList.add(extractInformation_string);
                        stringList.add(analysis_true_string);
                        return stringList;
                    }
                //匹配失败
                }else {

                    //insertvalueInformationService(user_String,false,problemScanLogic,current_Round_Extraction_String);
                    //current_Round_Extraction_String = "";
                    String analysis_false_string = analysis_false(way,connectMethod,telnetSwitchMethod,problemScanLogic, num);

                    //如果是最后一条信息 并且 匹配不上则
                    if ((!matchAnalysis_true_false && num == return_information_array.length-1) ){
                        List<String> stringList = new ArrayList<>();
                        stringList.add(extractInformation_string);
                        stringList.add(analysis_false_string+"=:="+line_n);
                        return stringList;
                    }

                    if (relativePosition_line.equals("null")){
                        continue;
                    }

                    //如果包含问题 说明存在问题或者 不存在问题 有结果
                    if ((analysis_false_string.indexOf("问题") !=-1) || (analysis_false_string.indexOf("继续") !=-1)){
                        insertvalueInformationService(user_String,false,problemScanLogic,current_Round_Extraction_String);
                        current_Round_Extraction_String = "";
                        getUnresolvedProblemInformationByData();

                        //如果 可以是循环的，且 不是最后一行 则 尽行下一步分析
                        if (operation.equalsIgnoreCase("loop")&&num<return_information_array.length){

                            List<String> loop_string = selectProblemScanLogicById(user_String,way,connectMethod, telnetSwitchMethod,
                                    return_information_array,"",extractInformation_string,
                                    line_n,firstID,null, operation,firmwareVersion,false);
                            if (loop_string!=null){
                                extractInformation_string = loop_string.get(0);
                                return loop_string;
                            }
                            return loop_string;
                        }

                    }else if ((analysis_false_string.indexOf("问题") ==-1) && (analysis_false_string.indexOf("继续") ==-1) && (analysis_false_string.indexOf("完成") ==-1)){
                        //如果 不包含问题  继续  完成 说明 没有结果 是具体ID
                        //下一条分析ID+"=:="+当前行数
                        String[] analysis_true_split = analysis_false_string.split("=:=");
                        //下一条分析ID
                        analysis_false_string =analysis_true_split[0];
                        //当前行数
                        line_n=Integer.valueOf(analysis_true_split[1]).intValue();
                        //根据下一条分析ID 和 当前行 来进行 下条分析
                        //返回 {总截取信息，true/false流程返回信息}
                        List<String> ProblemScanLogic_returnstring = selectProblemScanLogicById(user_String,way,connectMethod, telnetSwitchMethod,
                                return_information_array,current_Round_Extraction_String,extractInformation_string,
                                line_n,firstID,Integer.valueOf(analysis_false_string).longValue(),operation,firmwareVersion,false);
                        //如果返回信息为null
                        if (ProblemScanLogic_returnstring!=null){
                            //内分析传到上一层
                            extractInformation_string = ProblemScanLogic_returnstring.get(0);
                            return ProblemScanLogic_returnstring;
                        }

                        if (operation.equalsIgnoreCase("loop")&&num<return_information_array.length){

                            if (ProblemScanLogic_returnstring.get(1).indexOf("没问题")!=-1 && line_n != return_information_array.length){
                                line_n = Integer.valueOf(ProblemScanLogic_returnstring.get(1).split("=:=")[1]).intValue();
                            }

                            List<String> loop_string = selectProblemScanLogicById(user_String,way,connectMethod, telnetSwitchMethod,
                                    return_information_array,"",extractInformation_string,
                                    line_n,firstID,null, operation,firmwareVersion,false);
                            if (loop_string!=null){
                                extractInformation_string = loop_string.get(0);
                                return loop_string;
                            }
                            return loop_string;
                        }


                        return ProblemScanLogic_returnstring;
                    }
                }
                continue;
            }

            //取词
            if (!action.equals("null") && !action.equals("取版本")){
                //取词数
                String wordSelection_string = Utils.wordSelection(action, //提取方法 ：取词 取版本
                        return_information_array[num], matchContent, //返回信息的一行 提取关键字
                        problemScanLogic.getrPosition(), problemScanLogic.getLength()); //位置 长度WLs

                //与或逻辑
                boolean action_true_false = false;
                if (logic.equalsIgnoreCase("and")){
                    action_true_false = logic_true_false && wordSelection_string!=null;
                }else if (logic.equalsIgnoreCase("or")){
                    action_true_false = logic_true_false || wordSelection_string!=null;
                }else {
                    action_true_false = wordSelection_string!=null;
                }


                //取词结果 不为空 为正确
                if (action_true_false){
                    extractInformation_string = extractInformation_string +problemScanLogic.getWordName()+"=:="+ wordSelection_string+"=:=";
                    current_Round_Extraction_String = current_Round_Extraction_String +problemScanLogic.getWordName()+"=:="+problemScanLogic.getExhibit()+"=:="+ wordSelection_string+"=:=";
                    //分析结果为 true 有结果返回结果，没结果返回下一条分析ID
                    String analysis_true_string = analysis_true(way,connectMethod,telnetSwitchMethod,problemScanLogic, num);

                    //不包含：有问题无问题和待确定 时 说明 是具体ID 则进行下一步：  加上 不能确定
                    if ((analysis_true_string.indexOf("问题") ==-1) && (analysis_true_string.indexOf("继续") == -1)){

                        String[] analysis_true_split = analysis_true_string.split("=:=");
                        analysis_true_string =analysis_true_split[0];
                        line_n=Integer.valueOf(analysis_true_split[1]).intValue();


                        List<String> ProblemScanLogic_returnstring = selectProblemScanLogicById(user_String,way,connectMethod, telnetSwitchMethod,
                                return_information_array,current_Round_Extraction_String,extractInformation_string,
                                line_n,firstID,Integer.valueOf(analysis_true_string).longValue(),operation,firmwareVersion,true);
                        if (ProblemScanLogic_returnstring!=null){
                            extractInformation_string = ProblemScanLogic_returnstring.get(0);
                            return ProblemScanLogic_returnstring;
                        }
                        if (operation.equalsIgnoreCase("loop")&&num<return_information_array.length){

                            if (ProblemScanLogic_returnstring.get(1).indexOf("没问题")!=-1 && line_n != return_information_array.length){
                                line_n = Integer.valueOf(ProblemScanLogic_returnstring.get(1).split("=:=")[1]).intValue();
                            }

                            List<String> loop_string = selectProblemScanLogicById(user_String,way,connectMethod, telnetSwitchMethod,
                                    return_information_array,"",extractInformation_string,
                                    line_n,firstID,null, operation,firmwareVersion,true);
                            if (ProblemScanLogic_returnstring!=null){
                                extractInformation_string = ProblemScanLogic_returnstring.get(0);
                                return ProblemScanLogic_returnstring;
                            }
                            return loop_string;
                        }
                        return ProblemScanLogic_returnstring;
                    }else {
                        insertvalueInformationService(user_String,true,problemScanLogic,current_Round_Extraction_String);
                        current_Round_Extraction_String = "";
                        getUnresolvedProblemInformationByData();

                        if (operation.equalsIgnoreCase("loop")&&num<return_information_array.length){
                            List<String> loop_string = selectProblemScanLogicById(user_String,way,connectMethod, telnetSwitchMethod,
                                    return_information_array,"",extractInformation_string,
                                    line_n,firstID,null, operation,firmwareVersion,true);
                            if (loop_string!=null){
                                extractInformation_string = loop_string.get(0);
                                return loop_string;
                            }
                            return loop_string;
                        }
                        List<String> stringList = new ArrayList<>();
                        stringList.add(extractInformation_string);
                        stringList.add(analysis_true_string);
                        return stringList;
                    }

                }else {
                    //走 false 逻辑
                    String analysis_false_string = analysis_false(way,connectMethod,telnetSwitchMethod,problemScanLogic, num);

                    //insertvalueInformationService(user_String,false,problemScanLogic,current_Round_Extraction_String);
                    current_Round_Extraction_String = "";

                    if ((wordSelection_string ==null && num == return_information_array.length-1)){
                        List<String> stringList = new ArrayList<>();
                        stringList.add(extractInformation_string);
                        stringList.add(analysis_false_string+"=:="+line_n);
                        return stringList;
                    }

                    if (relativePosition_line.equals("null")){
                        continue;
                    }

                    //如果包含问题 说明存在问题或者 不存在问题 有结果
                    if ((analysis_false_string.indexOf("问题") !=-1) || (analysis_false_string.indexOf("继续") !=-1)){
                        insertvalueInformationService(user_String,false,problemScanLogic,current_Round_Extraction_String);
                        current_Round_Extraction_String = "";
                        getUnresolvedProblemInformationByData();

                        //如果 可以是循环的，且 不是最后一行 则 尽行下一步分析
                        if (operation.equalsIgnoreCase("loop")&&num<return_information_array.length){

                            List<String> loop_string = selectProblemScanLogicById(user_String,way,connectMethod, telnetSwitchMethod,
                                    return_information_array,"",extractInformation_string,
                                    line_n,firstID,null, operation,firmwareVersion,false);
                            if (loop_string!=null){
                                extractInformation_string = loop_string.get(0);
                                return loop_string;
                            }
                            return loop_string;
                        }

                    }else if ((analysis_false_string.indexOf("问题") ==-1) && (analysis_false_string.indexOf("继续") ==-1) && (analysis_false_string.indexOf("完成") ==-1)){

                        String[] analysis_true_split = analysis_false_string.split("=:=");
                        analysis_false_string =analysis_true_split[0];
                        line_n=Integer.valueOf(analysis_true_split[1]).intValue();


                        List<String> ProblemScanLogic_returnstring = selectProblemScanLogicById(user_String,way,connectMethod, telnetSwitchMethod,
                                return_information_array,current_Round_Extraction_String,extractInformation_string,
                                line_n,firstID,Integer.valueOf(analysis_false_string).longValue(),operation,firmwareVersion,false);
                        if (ProblemScanLogic_returnstring!=null){
                            extractInformation_string = ProblemScanLogic_returnstring.get(0);
                            return ProblemScanLogic_returnstring;
                        }
                        if (operation.equalsIgnoreCase("loop")&&num<return_information_array.length){

                            if (ProblemScanLogic_returnstring.get(1).indexOf("没问题")!=-1 && line_n != return_information_array.length){
                                line_n = Integer.valueOf(ProblemScanLogic_returnstring.get(1).split("=:=")[1]).intValue();
                            }

                            List<String> loop_string = selectProblemScanLogicById(user_String,way,connectMethod, telnetSwitchMethod,
                                    return_information_array,"",extractInformation_string,
                                    line_n,firstID,null, operation,firmwareVersion,false);
                            if (ProblemScanLogic_returnstring!=null){
                                extractInformation_string = ProblemScanLogic_returnstring.get(0);
                                return ProblemScanLogic_returnstring;
                            }
                            return loop_string;
                        }
                        return ProblemScanLogic_returnstring;

                    }


                }
                continue;
            }

            //比较
            if (compare!=null && !compare.equals("")){
                String remove_content = "";
                switch (action){
                    case "取版本":
                        remove_content = firmwareVersion;
                        break;
                    case "":
                        //取词数
                        remove_content = Utils.wordSelection(action, //提取方法 ：取词 取版本
                                return_information_array[num], matchContent, //返回信息的一行 提取关键字
                                problemScanLogic.getrPosition(), problemScanLogic.getLength()); //位置 长度WLs
                        break;
                }
                boolean compare_boolean = Utils.compareVersion(remove_content, compare, problemScanLogic.getContent());

                //与或逻辑
                boolean compare_true_false = false;
                if (logic.equalsIgnoreCase("and")){
                    compare_true_false = logic_true_false && compare_boolean;
                }else if (logic.equalsIgnoreCase("or")){
                    compare_true_false = logic_true_false || compare_boolean;
                }else {
                    compare_true_false = compare_boolean;
                }

                if (compare_true_false){
                    String analysis_true_string = analysis_true(way,connectMethod,telnetSwitchMethod,problemScanLogic, num);

                    //不包含：有问题无问题和待确定 时 说明 是具体ID 则进行下一步：  加上 不能确定
                    if ((analysis_true_string.indexOf("问题") ==-1) && (analysis_true_string.indexOf("继续") == -1)){

                        String[] analysis_true_split = analysis_true_string.split("=:=");
                        analysis_true_string =analysis_true_split[0];
                        line_n=Integer.valueOf(analysis_true_split[1]).intValue();


                        List<String> ProblemScanLogic_returnstring = selectProblemScanLogicById(user_String,way,connectMethod, telnetSwitchMethod,
                                return_information_array,current_Round_Extraction_String,extractInformation_string,
                                line_n,firstID,Integer.valueOf(analysis_true_string).longValue(),operation,firmwareVersion,true);
                        if (ProblemScanLogic_returnstring!=null){
                            extractInformation_string = ProblemScanLogic_returnstring.get(0);
                            return ProblemScanLogic_returnstring;
                        }

                        if (operation.equalsIgnoreCase("loop")&&num<return_information_array.length){

                            if (ProblemScanLogic_returnstring.get(1).indexOf("没问题")!=-1 && line_n != return_information_array.length){
                                line_n = Integer.valueOf(ProblemScanLogic_returnstring.get(1).split("=:=")[1]).intValue();
                            }

                            List<String> loop_string = selectProblemScanLogicById(user_String,way,connectMethod, telnetSwitchMethod,
                                    return_information_array,"",extractInformation_string,
                                    line_n,firstID,null, operation,firmwareVersion,true);
                            if (loop_string!=null){
                                extractInformation_string = loop_string.get(0);
                                return loop_string;
                            }
                            return loop_string;
                        }
                        return ProblemScanLogic_returnstring;
                    }else {
                        insertvalueInformationService(user_String,true,problemScanLogic,current_Round_Extraction_String);
                        current_Round_Extraction_String = "";
                        getUnresolvedProblemInformationByData();

                        if (operation.equalsIgnoreCase("loop")&&num<return_information_array.length){
                            List<String> loop_string = selectProblemScanLogicById(user_String,way,connectMethod, telnetSwitchMethod,
                                    return_information_array,"",extractInformation_string,
                                    line_n,firstID,null, operation,firmwareVersion,true);
                            if (loop_string!=null){
                                extractInformation_string = loop_string.get(0);
                                return loop_string;
                            }
                            return loop_string;
                        }
                        List<String> stringList = new ArrayList<>();
                        stringList.add(extractInformation_string);
                        stringList.add(analysis_true_string);
                        return stringList;
                    }

                }else {

                    if (relativePosition_line.equals("null")){
                        continue;
                    }

                    String analysis_false_string = analysis_false(way,connectMethod,telnetSwitchMethod,problemScanLogic, num);

                    if ((analysis_false_string.indexOf("问题") ==-1) && (analysis_false_string.indexOf("继续") == -1)){

                        String[] analysis_true_split = analysis_false_string.split("=:=");
                        analysis_false_string =analysis_true_split[0];
                        line_n=Integer.valueOf(analysis_true_split[1]).intValue();


                        List<String> ProblemScanLogic_returnstring = selectProblemScanLogicById(user_String,way,connectMethod, telnetSwitchMethod,
                                return_information_array,current_Round_Extraction_String,extractInformation_string,
                                line_n,firstID,Integer.valueOf(analysis_false_string).longValue(),operation,firmwareVersion,false);
                        if (ProblemScanLogic_returnstring!=null){
                            extractInformation_string = ProblemScanLogic_returnstring.get(0);
                            return ProblemScanLogic_returnstring;
                        }

                        if (operation.equalsIgnoreCase("loop")&&num<return_information_array.length){

                            if (ProblemScanLogic_returnstring.get(1).indexOf("没问题")!=-1 && line_n != return_information_array.length){
                                line_n = Integer.valueOf(ProblemScanLogic_returnstring.get(1).split("=:=")[1]).intValue();
                            }

                            List<String> loop_string = selectProblemScanLogicById(user_String,way,connectMethod, telnetSwitchMethod,
                                    return_information_array,"",extractInformation_string,
                                    line_n,firstID,null, operation,firmwareVersion,false);
                            if (loop_string!=null){
                                extractInformation_string = loop_string.get(0);
                                return loop_string;
                            }
                            return loop_string;
                        }
                        return ProblemScanLogic_returnstring;
                    }else {
                        insertvalueInformationService(user_String,compare_boolean,problemScanLogic,current_Round_Extraction_String);
                        current_Round_Extraction_String = "";
                        getUnresolvedProblemInformationByData();

                        if (operation.equalsIgnoreCase("loop")&&num<return_information_array.length){
                            List<String> loop_string = selectProblemScanLogicById(user_String,way,connectMethod, telnetSwitchMethod,
                                    return_information_array,"",extractInformation_string,
                                    line_n,firstID,null, operation,firmwareVersion,false);
                            if (loop_string!=null){
                                extractInformation_string = loop_string.get(0);
                                return loop_string;
                            }
                            return loop_string;
                        }
                        List<String> stringList = new ArrayList<>();
                        stringList.add(extractInformation_string);
                        stringList.add(analysis_false_string);
                        return stringList;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @method: 插曲动态信息数据
     * @Param: [user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法, connectMethod ssh连接, telnetSwitchMethod telnet连接,
     *                 交换机返回信息字符串, 单次分析提取数据，循环分析提取数据
     *                 交换机返回信息字符串分析索引位置(光标)，第一条分析ID， 当前分析ID ，是否循环 ，内部固件版本号]
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public void insertvalueInformationService(List<String> user_String,boolean boo,ProblemScanLogic problemScanLogic,String parameterString){

        String userName = GlobalVariable.userName;
        String nickName = GlobalVariable.nickName;
        String phonenumber = GlobalVariable.phonenumber;



        //第一条分析ID : 有问题  没问题
        String getNextId = "";
        //问题索引
        String problemId ="";
        //解决问题命令ID
        String comId ="";
        //分析结果 true  or  false
        if (boo){
            getNextId = problemScanLogic.gettNextId();
            problemId = problemScanLogic.gettProblemId();
            comId = problemScanLogic.gettComId();
        }else {
            getNextId = problemScanLogic.getfNextId();
            problemId = problemScanLogic.getfProblemId();
            comId = problemScanLogic.getfComId();
        }

        switch (getNextId){
            case "有问题":
            case "没问题":
                //参数的 第一个ID
                Long outId = 0l;
                if (parameterString!=null && !parameterString.equals("")){
                    String[] parameterStringsplit = parameterString.split("=:=");
                    if (parameterStringsplit.length>0){
                        ValueInformation valueInformation = new ValueInformation();
                        for (int number=parameterStringsplit.length-1;number>0;number--){
                            //插入参数

                            //用户名=:=是=:=admin=:=密码=:=否=:=$c$3$ucuLP5tRIUiNMSGST3PKZPvR0Z0bw2/g=:=
                            String setDynamicInformation=parameterStringsplit[number];
                            valueInformation.setDisplayInformation(setDynamicInformation);//动态信息(显示
                            valueInformation.setDynamicInformation(setDynamicInformation);//动态信息
                            --number;
                            String setExhibit=parameterStringsplit[number];
                            valueInformation.setExhibit(setExhibit);//是否显示
                            --number;
                            valueInformation.setDynamicVname(parameterStringsplit[number]);//动态信息名称
                            valueInformation.setOutId(outId);
                            valueInformationService.insertValueInformation(valueInformation);
                            outId = valueInformation.getId();
                        }
                    }
                }
                //插入问题数据
                SwitchProblem switchProblem = new SwitchProblem();
                switchProblem.setSwitchIp(user_String.get(1)); // ip
                switchProblem.setSwitchName(user_String.get(2)); //name
                switchProblem.setSwitchPassword(user_String.get(3)); //password
                switchProblem.setProblemId(problemId); // 问题索引
                switchProblem.setComId(comId);//命令索引
                switchProblem.setValueId(outId);//参数索引
                switchProblem.setIfQuestion(getNextId); //是否有问题

                switchProblem.setUserName(userName);//参数索引
                switchProblem.setPhonenumber(phonenumber); //是否有问题
                switchProblemService.insertSwitchProblem(switchProblem);
            case "":
        }
    };

    /**
     * @method: 分析结果正确
     * @Param: [way 连接方法, connectMethod ssh连接, telnetSwitchMethod telnet连接,
     *                problemScanLogic 扫描逻辑表数据信息, num 交换机返回信息索引位置(光标)]
     * @return: java.lang.String 有结果返回结果，没结果返回下一条分析ID
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public String analysis_true(String way,SshMethod connectMethod,TelnetSwitchMethod telnetSwitchMethod,ProblemScanLogic problemScanLogic,int num){
        // 设置返回信息 初始化为空
        String analysis_true = null;
        //扫描逻辑表数据信息 true 结果 下一条分析ID
        String tNextId_string = problemScanLogic.gettNextId();
        //如果 下一条分析ID 不包含  没问题 有问题 继续
        //否则 为 具体ID

        if (!tNextId_string.equals("没问题")
                && !tNextId_string.equals("有问题")
                && !tNextId_string.equals("继续")
                && !tNextId_string.equals("完成")
                && !tNextId_string.equals("")){
            //匹配成功则返回 true下一条ID
            int line_n = num;
            long tNextId_Integer= Integer.valueOf(problemScanLogic.gettNextId().substring(3,problemScanLogic.gettNextId().length())).longValue();
            return tNextId_Integer+"=:="+line_n;
        }else {
            //如果 下一条分析ID 包含  没问题 有问题 继续
            switch (tNextId_string){
                //true 存在问题——问题：确认存在问题，是否返回看命令id
                case "有问题":
                    //返回 下一条分析ID  和  交换机返回信息索引位置(光标)
                    Integer tComId =Integer.valueOf(problemScanLogic.gettComId());
                    Integer tProblemIdx =Integer.valueOf(problemScanLogic.gettProblemId());
                    if (tComId==0){
                        return  analysis_true = "有问题=:="+"问题索引=:="+tProblemIdx+"=:=tComId=:=0";
                    }else {
                        return  analysis_true = "有问题=:="+"问题索引=:="+tProblemIdx+"=:=tComId=:="+tComId;
                    }
                    //true 不存在问题——问题：确认不存在问题，返回同上
                case "没问题":
                    return analysis_true = "没问题";
                //true 继续：代表需要执行命令才能进一步分析，看命令id
                case "继续":
                    //返回待分析继续 命令id
                    Long commandId =Integer.valueOf(problemScanLogic.gettComId()).longValue();
                    executeScanCommandByCommandId(commandId,way,connectMethod,telnetSwitchMethod);
                    return analysis_true = "继续=:=tComId=:="+commandId;
                case "完成":
                    return analysis_true = "完成";
            }
        }
        return analysis_true;
    }

    /**
     * @method: 分析结果错误
     * @Param: [way 连接方法, connectMethod ssh连接, telnetSwitchMethod telnet连接,
     *                 problemScanLogic 扫描逻辑表数据信息, num 交换机返回信息索引位置(光标)]
     * @return: java.lang.String 有结果返回结果，没结果返回下一条分析ID
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public String analysis_false(String way,SshMethod connectMethod,TelnetSwitchMethod telnetSwitchMethod,ProblemScanLogic problemScanLogic,int num){
        // 设置返回信息 初始化为空
        String analysis_false=null;
        //扫描逻辑表数据信息 false 结果 下一条分析ID
        String fNextId_string = problemScanLogic.getfNextId();
        //如果 下一条分析ID 不包含  没问题 有问题 继续
        //否则 为 具体ID
        if (!fNextId_string.equals("没问题")
                && !fNextId_string.equals("有问题")
                && !fNextId_string.equals("继续")
                && !fNextId_string.equals("完成")
                && !fNextId_string.equals("")){
            //返回 下一条分析ID  和  交换机返回信息索引位置(光标)
            int line_n = num;
            long fNextId_Integer = Integer.valueOf(fNextId_string.substring(3, fNextId_string.length())).longValue();
            return fNextId_Integer+"=:="+line_n;
        }else {
            //如果 下一条分析ID 包含  没问题 有问题 继续
            switch ( fNextId_string ){
                //false 存在问题——问题：确认存在问题，是否返回看命令id
                case "有问题":
                    Integer fComId =Integer.valueOf(problemScanLogic.getfComId());
                    Integer fProblemIdx =Integer.valueOf(problemScanLogic.getfProblemId());
                    if (fComId==0){
                        return  analysis_false = "有问题=:="+"问题索引=:="+fProblemIdx+"=:=fComId=:=0";
                    }else {
                        return  analysis_false = "有问题=:="+"问题索引=:="+fProblemIdx+"=:=fComId=:="+fComId;
                    }
                    //false 不存在问题——问题：确认不存在问题，返回同上
                case "没问题":
                    return analysis_false = "没问题";
                //false 继续：代表需要执行命令才能进一步分析，看命令id
                case "继续":
                    //返回待分析继续 命令id
                    long commandId = Integer.valueOf(problemScanLogic.getfComId()).longValue();
                    executeScanCommandByCommandId(commandId,way,connectMethod,telnetSwitchMethod);
                    return analysis_false = "继续=:=fComId=:="+commandId;
                case "完成":
                    return analysis_false = "完成";
                case "":
            }
        }
        return analysis_false;
    }

    /**
     * @method: getUnresolvedProblemInformationByData
     * @Param: []
     * @return: java.util.List<com.sgcc.sql.domain.SwitchProblem>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("getUnresolvedProblemInformationByData")
    public List<ScanResultsVO> getUnresolvedProblemInformationByData(){

        ScanResults scanResults = new ScanResults();

        String userName = GlobalVariable.userName;
        Long loginTime = GlobalVariable.loginTime;
        if(loginTime == null){
            loginTime = System.currentTimeMillis();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateFormat = simpleDateFormat.format(loginTime);
        dateFormat = dateFormat.split(" ")[0];
        List<SwitchProblemVO> switchProblemList = switchProblemService.selectUnresolvedProblemInformationByData(dateFormat,userName);
        for (SwitchProblemVO switchProblemVO:switchProblemList){
            List<SwitchProblemCO> switchProblemCOList = switchProblemVO.getSwitchProblemCOList();
            for (SwitchProblemCO switchProblemCO:switchProblemCOList){
                List<ValueInformationVO> valueInformationVOList = valueInformationService.selectValueInformationVOListByID(switchProblemCO.getValueId());
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
            scanResultsVOList.add(scanResultsVO);
        }


        for (ScanResultsVO scanResultsVO:scanResultsVOList){
            List<SwitchProblemVO> switchProblemVOList = new ArrayList<>();
            for (SwitchProblemVO switchProblemVO:switchProblemList){
                if (switchProblemVO.getSwitchIp().equals(scanResultsVO.getSwitchIp())){
                    switchProblemVOList.add(switchProblemVO);
                }
            }
            scanResultsVO.setSwitchProblemVOList(switchProblemVOList);
        }
        scanResults.setScanResultsVOS(scanResultsVOList);
        WebSocketService.sendMessage("loophole",scanResultsVOList);
        return scanResultsVOList;
    }
}