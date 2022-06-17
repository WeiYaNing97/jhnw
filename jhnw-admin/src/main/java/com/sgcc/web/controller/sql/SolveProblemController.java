package com.sgcc.web.controller.sql;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.connect.method.SshMethod;
import com.sgcc.connect.method.TelnetSwitchMethod;
import com.sgcc.connect.util.SshConnect;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.*;
import com.sgcc.web.controller.webSocket.WebSocketService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Autowired
    private ITotalQuestionTableService totalQuestionTableService;

    @Autowired
    private ISwitchProblemService switchProblemService;


    /*=====================================================================================================================
    =====================================================================================================================
    =====================================================================================================================*/


    /*解决问题命令回显*/
    /**
     * @method: 根据问题ID 查询 解决问题ID命令 返回List<String>
     * @Param: [totalQuestionTableId]
     * @return: com.sgcc.common.core.domain.AjaxResult
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("queryCommandListBytotalQuestionTableId")
    public List<String> queryCommandListBytotalQuestionTableId(Long totalQuestionTableId){

        TotalQuestionTable totalQuestionTable = totalQuestionTableService.selectTotalQuestionTableById(totalQuestionTableId);
        if (totalQuestionTable.getProblemSolvingId() == null || totalQuestionTable.getProblemSolvingId().equals("null")){
            return null;
        }
        String problemSolvingId = totalQuestionTable.getProblemSolvingId();

        List<CommandLogic> commandLogicList = new ArrayList<>();
        do {
            CommandLogic commandLogic = commandLogicService.selectCommandLogicById(problemSolvingId);
            commandLogicList.add(commandLogic);
            problemSolvingId = commandLogic.getEndIndex();
        }while (!(problemSolvingId.equals("0")));

        List<String> commandLogicStringList = new ArrayList<>();
        for (CommandLogic commandLogic:commandLogicList){
            String string = DefinitionProblemController.commandLogicString(commandLogic);
            String[] split = string.split(":");
            System.err.println("\r\n"+split[1]+"\r\n");
            commandLogicStringList.add(split[1]);
        }

        return commandLogicStringList;
    }





    /***
     * @method: 批量修复问题
     * @Param: []
     * @return: com.sgcc.common.core.domain.AjaxResult
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("/batchSolution/{userinformation}/{commandValueList}")
    public AjaxResult batchSolution(@PathVariable List<String> userinformation,@PathVariable List<String> commandValueList){
        //String mode,String ip,String name,String password,String port
        //用户信息
        String userInformationString = userinformation.toString();
        userInformationString = userInformationString.replace("[{","");
        userInformationString = userInformationString.replace("}]","");
        userInformationString = userInformationString.replace("\"","");
        String[] userinformationSplit = userInformationString.split(",");

        Map<String,String> user_String = new HashMap<>();
        for (String userString:userinformationSplit){
            String[] userStringsplit = userString.split(":");
            String key = userStringsplit[0];
            String value = userStringsplit[1];
            switch (key.trim()){
                case "mode":
                    //登录方式
                    user_String.put("mode",value);
                    break;
                case "ip":
                    //ip地址
                    user_String.put("ip",value);
                    break;
                case "name":
                    //用户名
                    user_String.put("name",value);
                    break;
                case "password":
                    //密码
                    user_String.put("password",value);
                    break;
                case "port":
                    //端口号
                    user_String.put("port",value);
                    break;
            }
        }

        //连接交换机
        //ssh连接
        SshMethod connectMethod = null;
        //telnet连接
        TelnetSwitchMethod telnetSwitchMethod = null;
        /* requestConnect方法：
        传入参数：[mode 连接方式, ip IP地址, name 用户名, password 密码, port 端口号,
            connectMethod ssh连接方法, telnetSwitchMethod telnet连接方法]
        返回信息为：[是否连接成功,mode 连接方式, ip IP地址, name 用户名, password 密码, port 端口号,
            connectMethod ssh连接方法 或者 telnetSwitchMethod telnet连接方法（其中一个，为空者不存在）] */
        AjaxResult requestConnect_ajaxResult = SwitchInteraction.requestConnect( user_String , connectMethod , telnetSwitchMethod );
        //解析返回参数
        List<Object> informationList = (List<Object>) requestConnect_ajaxResult.get("data");

        //是否连接成功
        boolean requestConnect_boolean = informationList.get(0).toString().equals("true");
        if (requestConnect_boolean){
            //commandValueList  命令ID 参数ID
            String commandValueListString = commandValueList.toString();
            commandValueListString = commandValueListString.replace("[{","");
            commandValueListString = commandValueListString.replace("}]","");
            //去掉 “
            commandValueListString = commandValueListString.replace("\"","");
            //将 },  改为 \r\n
            commandValueListString = commandValueListString.replace("},","\r\n");
            //去掉 {
            commandValueListString = commandValueListString.replace("{","");
            // 得到：comId:1,valueId:2
            String[] commandValueSplit = commandValueListString.split("\r\n");

            for (String commandValue:commandValueSplit){
                String command_Value = null;
                String[] command_value = commandValue.split(",");
                String commandvalueSting = null;
                String comId = null;
                String valueId = null;

                for (String command_valueSting:command_value){
                    String[] command_value_split = command_valueSting.split(":");
                    switch (command_value_split[0].trim()){
                        case "comId":
                            comId = command_value_split[1].trim();
                            break;
                        case "valueId":
                            valueId = command_value_split[1].trim();
                            break;
                    }
                }

                //当命令不等于空时
                if (comId!=null){
                    //命令赋值
                    commandvalueSting = comId;
                    //修复问题 会出现 不需要参数的
                    //如果参数不为空
                    if (valueId!=null){
                        //参数赋值
                        commandvalueSting = commandvalueSting +":"+ valueId;
                    }else if (valueId == null){
                        //参数赋值
                        commandvalueSting = commandvalueSting +":"+ 0;
                    }
                    command_Value = commandvalueSting;
                    commandvalueSting = null;
                }

                //将 命令ID 和 参数ID 分离开来
                String[] commandValuesplit = command_Value.split(":");
                //传参 命令ID 和 参数ID
                //返回 命令集合 和 参数集合
                AjaxResult ajaxResult = queryParameterSet(commandValuesplit[0], Long.valueOf(commandValuesplit[1]).longValue());
                Object[] commandvalue =  (Object[])ajaxResult.get("data");

                //命令集合
                List<String> commandList = (List<String>) commandvalue[0];
                //参数集合
                List<ValueInformationVO> valueInformationVOList = (List<ValueInformationVO>)commandvalue[1];
                //解决问题
                String solveProblem = solveProblem(informationList, commandList, valueInformationVOList);

                if (solveProblem.equals("成功")){
                    SwitchProblem switchProblem = switchProblemService.selectSwitchProblemByValueId(Integer.valueOf(valueId).longValue());
                    switchProblem.setResolved("是");
                    int i = switchProblemService.updateSwitchProblem(switchProblem);
                    if (i<=0){
                        return AjaxResult.error("修复失败");
                    }
                }

                //命令清空 方便下一轮命令判空
                comId = null;

                //参数清空  方便下一轮参数判空
                valueId = null;

                if (informationList.get(1).toString().equalsIgnoreCase("ssh")){
                    connectMethod.closeConnect((SshConnect)informationList.get(8));
                }else if (informationList.get(1).toString().equalsIgnoreCase("telnet")){
                    telnetSwitchMethod.closeSession();
                }

            }
            return AjaxResult.success("修复成功");
        }
        return AjaxResult.error("连接交换机失败");
    }

    /***
     * 返回 命令集合 和 参数集合
     * @method: queryParameterSet
     * @Param: []
     * @return: com.sgcc.common.core.domain.AjaxResult
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     * 传参 命令ID 和 参数ID
     */
    @RequestMapping("queryParameterSet")
    public AjaxResult queryParameterSet(String commandID,Long valueID){
        //根据 第一个命令 ID
        //根据 命令ID commandID 查询命令集合
        AjaxResult ajaxResult = queryCommandSet(commandID);
        List<CommandLogic> commandLogicList = (List<CommandLogic>)ajaxResult.get("data");
        //定义命令集合 commandList
        List<String> commandList = new ArrayList<>();
        //将命令 放入 命令集合
        for (CommandLogic commandLogic:commandLogicList){
            commandList.add(commandLogic.getCommand());
        }
        //查询 参数信息集合
        List<ValueInformationVO> valueInformationVOList = new ArrayList<>();
        //如果 参数ID 不为0 则 有参数 需要获取相关参数
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

    /**
     * 查询命令集合
     * @method: 根据 命令ID commandId 查询命令集合 用于解决问题
     * @Param: [commandId]
     * @return: com.sgcc.common.core.domain.AjaxResult
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("queryCommandSet")
    public AjaxResult queryCommandSet(String commandId_Long){

        List<CommandLogic> commandLogicList = new ArrayList<>();
        do {
            CommandLogic commandLogic = commandLogicService.selectCommandLogicById(commandId_Long);
            commandLogicList.add(commandLogic);
            commandId_Long = commandLogic.getEndIndex();
        }while (!(commandId_Long.equals("0")));
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
    public String solveProblem(List<Object> informationList,
                               List<String> commandList,
                               List<ValueInformationVO> valueInformationVOList){
        //遍历命令集合    根据参数名称 获取真实命令
        //local-user:用户名
        //password cipher:密码
        for (int num = 0;num<commandList.size();num++){
            String[] command_split = commandList.get(num).split(":");
            // command_split.length>1  说明有参数名称
            if (command_split.length>1){
                //获取参数名称
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
        SshConnect sshConnect = null;
        if (requestConnect_way.equalsIgnoreCase("ssh")){
            connectMethod = (SshMethod)informationList.get(6);
            sshConnect = (SshConnect)informationList.get(8);
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
                commandString = connectMethod.sendCommand(sshConnect,command,null);
            }else if (requestConnect_way.equalsIgnoreCase("telnet")){

                WebSocketService.sendMessage("badao",command);
                commandString = telnetSwitchMethod.sendCommand(command,null);
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
            if (insert_Int <= 0){
                return "失败";
            }
        }

        return "成功";
    }
}