package com.sgcc.web.controller.sql;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.connect.method.SshMethod;
import com.sgcc.connect.method.TelnetSwitchMethod;
import com.sgcc.sql.domain.CommandLogic;
import com.sgcc.sql.domain.ReturnRecord;
import com.sgcc.sql.domain.ValueInformation;
import com.sgcc.sql.domain.ValueInformationVO;
import com.sgcc.sql.service.ICommandLogicService;
import com.sgcc.sql.service.IReturnRecordService;
import com.sgcc.sql.service.IValueInformationService;
import com.sgcc.web.controller.webSocket.WebSocketService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 与交换机交互方法类
 * * @E-mail: WeiYaNing97@163.com
 * @date 2022年01月05日 14:18
 */
@RestController
@RequestMapping("/sql/SolveProblemController")
public class SolveProblemController {

    @Autowired
    private ICommandLogicService commandLogicService;
    @Autowired
    private IValueInformationService valueInformationService;
    @Autowired
    private IReturnRecordService returnRecordService;

    /***
    * @method: 批量解决问题
    * @Param: []
    * @return: com.sgcc.common.core.domain.AjaxResult
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("batchSolution")
    public AjaxResult batchSolution(){
        Map<String,String> user_String = new HashMap<>();
        user_String.put("mode","ssh");
        user_String.put("ip","192.168.1.1");
        user_String.put("name","admin");
        user_String.put("password","admin");
        user_String.put("port","22");

        List<String> command_value_String = new ArrayList<>();
        command_value_String.add("4:6");

        //ssh连接
        SshMethod connectMethod = null;
        //telnet连接
        TelnetSwitchMethod telnetSwitchMethod = null;
        /* requestConnect方法：
        传入参数：[mode 连接方式, ip IP地址, name 用户名, password 密码, port 端口号,
            connectMethod ssh连接方法, telnetSwitchMethod telnet连接方法]
        返回信息为：[是否连接成功,mode 连接方式, ip IP地址, name 用户名, password 密码, port 端口号,
            connectMethod ssh连接方法 或者 telnetSwitchMethod telnet连接方法（其中一个，为空者不存在）] */
        AjaxResult requestConnect_ajaxResult = SwitchInteraction.requestConnect(user_String,connectMethod, telnetSwitchMethod);

        //解析返回参数
        List<Object> informationList = (List<Object>) requestConnect_ajaxResult.get("data");
        //是否连接成功
        boolean requestConnect_boolean = informationList.get(0).toString().equals("true");

        if (requestConnect_boolean){
            for (String commandValue:command_value_String){
                String[] commandValueSplit = commandValue.split(":");
                Long.valueOf(commandValueSplit[0]).longValue();
                AjaxResult ajaxResult = queryParameterSet(Long.valueOf(commandValueSplit[0]).longValue(), Long.valueOf(commandValueSplit[1]).longValue());
                Object[] command_value =  (Object[])ajaxResult.get("data");
                List<String> commandList = (List<String>) command_value[0];
                List<ValueInformationVO> valueInformationVOList = (List<ValueInformationVO>)command_value[1];
                AjaxResult solveProblemAjaxResult = solveProblem(informationList, commandList, valueInformationVOList);
            }
        }
        return null;
    }


    /***
    * @method: queryParameterSet
    * @Param: []
    * @return: com.sgcc.common.core.domain.AjaxResult
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("queryParameterSet")
    public AjaxResult queryParameterSet(Long commandID,Long valueID){

        AjaxResult ajaxResult = queryCommandSet(commandID);
        List<CommandLogic> commandLogicList = (List<CommandLogic>)ajaxResult.get("data");
        List<String> commandList = new ArrayList<>();

        for (CommandLogic commandLogic:commandLogicList){
            commandList.add(commandLogic.getCommand());
        }

        List<ValueInformationVO> valueInformationVOList = new ArrayList<>();
        while (valueID != 0){
            ValueInformation valueInformation = valueInformationService.selectValueInformationById(valueID);
            ValueInformationVO valueInformationVO = new ValueInformationVO();
            BeanUtils.copyProperties(valueInformation,valueInformationVO);
            valueInformationVOList.add(valueInformationVO);
            valueID = valueInformation.getOutId();
        }

        Object[] command_value = new Object[2];
        command_value[0] = commandList;
        command_value[1] = valueInformationVOList;
        return AjaxResult.success(command_value);
    }

    /***
     * @method: 根据 命令ID commandId 查询命令集合 用于解决问题
     * @Param: [commandId]
     * @return: com.sgcc.common.core.domain.AjaxResult
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("queryCommandSet")
    public AjaxResult queryCommandSet(Long commandId_Long){

        List<CommandLogic> commandLogicList = new ArrayList<>();
        do {
            CommandLogic commandLogic = commandLogicService.selectCommandLogicById(commandId_Long);
            commandLogicList.add(commandLogic);
            commandId_Long = commandLogic.getEndIndex();
        }while (commandId_Long != 0);
        return AjaxResult.success(commandLogicList);
    }

    /**
    * @method: 解决问题
    * @Param: [parameterID]
    * @return: com.sgcc.common.core.domain.AjaxResult
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("solveProblem")
    public AjaxResult solveProblem(List<Object> informationList,
            List<String> commandList,List<ValueInformationVO> valueInformationVOList){

        for (int num = 0;num<commandList.size();num++){
            String[] command_split = commandList.get(num).split(":");
            if (command_split.length>1){
                String value_string= command_split[command_split.length-1];
                for (ValueInformationVO valueInformationVO:valueInformationVOList){
                    if (valueInformationVO.getDynamicVname().equals(value_string)){
                        String command_sum = command_split[0] + " "+ valueInformationVO.getDynamicInformation();
                        commandList.set(num,command_sum);
                    }
                }
            }else {
                commandList.set(num,command_split[0]);
            }
        }

        //ssh连接
        SshMethod connectMethod = null;
        //telnet连接
        TelnetSwitchMethod telnetSwitchMethod = null;

        //连接方式：ssh 或 telnet
        String requestConnect_way = informationList.get(1).toString();
        //如果连接方式为ssh则 连接方法返回集合最后一个参数为 connectMethod参数
        //如果连接方式为telnet则 连接方法返回集合最后一个参数为 telnetSwitchMethod参数
        if (requestConnect_way.equalsIgnoreCase("ssh")){
            connectMethod = (SshMethod)informationList.get(6);
        }else if (requestConnect_way.equalsIgnoreCase("telnet")){
            telnetSwitchMethod = (TelnetSwitchMethod)informationList.get(6);
        }

        String commandString =""; //预设交换机返回结果

        //user_String, connectMethod, telnetSwitchMethod
        for (String command:commandList){
            //根据 连接方法 判断 实际连接方式
            //并发送命令 接受返回结果
            System.err.print("\r\n"+"命令："+command+"\r\n");
            if (requestConnect_way.equalsIgnoreCase("ssh")){

                WebSocketService.sendMessage("badao",command);
                commandString = connectMethod.sendCommand(command);
            }else if (requestConnect_way.equalsIgnoreCase("telnet")){

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
            System.err.print("\r\n"+"交换机返回信息："+commandString+"\r\n");
            //交换机返回信息 按行分割为 字符串数组
            String[] commandString_split = commandString.split("\r\n");
            //创建 存储交换机返回数据 实体类
            ReturnRecord returnRecord = new ReturnRecord();
            // 执行命令赋值
            String commandtrim = command.trim();
            returnRecord.setCurrentCommLog(commandtrim);
            // 返回日志内容
            if (commandString_split.length > 1){
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
            }else if (commandString_split.length == 1){
                returnRecord.setCurrentIdentifier("\r\n"+commandString_split[0]+"\r\n");
                WebSocketService.sendMessage("badao","\r\n"+commandString_split[0]+"\r\n");
            }
            //存储交换机返回数据 插入数据库
            int insert_Int = returnRecordService.insertReturnRecord(returnRecord);
        }

        return AjaxResult.error("执行成功！");
    }

}