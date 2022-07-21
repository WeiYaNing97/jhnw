package com.sgcc.web.controller.sql;

import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.connect.method.SshMethod;
import com.sgcc.connect.method.TelnetSwitchMethod;
import com.sgcc.connect.util.SpringBeanUtil;
import com.sgcc.connect.util.SshConnect;
import com.sgcc.connect.util.TelnetComponent;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.*;
import com.sgcc.web.controller.webSocket.WebSocketService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;

import java.util.*;

/**
 * 与交换机交互方法类
 * * @E-mail: WeiYaNing97@163.com
 * @date 2022年01月05日 14:18
 */
@RestController
@RequestMapping("/sql/SolveProblemController")
public class SolveProblemController extends Thread {

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

    private static List<String> userinformationList;
    private static Long problemId;

    /*=====================================================================================================================
    =====================================================================================================================
    =====================================================================================================================*/



    @Override
    public void run() {
        AjaxResult ajaxResult = batchSolution(userinformationList,problemId);
    }

    @RequestMapping("batchSolutionMultithreading")
    @MyLog(title = "批量修复问题", businessType = BusinessType.OTHER)
    public static void batchSolutionMultithreading(List<Object> userinformation,List<String> problemIdList) {
        int number = userinformation.size();
        for (int i = 0 ; i<number ; i++){
            userinformationList = (List<String>) userinformation.get(i);
            problemId = Integer.valueOf(problemIdList.get(i)).longValue();
            Thread thread = new SolveProblemController();
            thread.start();
            try {
                //Thread.sleep(1000*3);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*解决问题命令回显*/
    /**
     * @method: 根据问题ID 查询 解决问题ID命令 返回List<String>
     * @Param: [totalQuestionTableId]
     * @return: com.sgcc.common.core.domain.AjaxResult
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("queryCommandListBytotalQuestionTableId")
    @MyLog(title = "查询解决问题命令", businessType = BusinessType.OTHER)
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
    @RequestMapping("/batchSolution/{userinformation}/{problemId}")
    //@MyLog(title = "修复问题", businessType = BusinessType.OTHER)
    public AjaxResult batchSolution(@PathVariable List<String> userinformation,Long problemId){
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
        AjaxResult requestConnect_ajaxResult = SwitchInteraction.requestConnect( user_String );
        //解析返回参数
        List<Object> informationList = (List<Object>) requestConnect_ajaxResult.get("data");

        //是否连接成功
        boolean requestConnect_boolean = informationList.get(0).toString().equals("true");

        if (requestConnect_boolean){


            SwitchProblem pojo = switchProblemService.selectSwitchProblemById(problemId);
            //传参 命令ID 和 参数ID
                //返回 命令集合 和 参数集合
                AjaxResult ajaxResult = queryParameterSet(pojo.getComId(), pojo.getValueId());
                Object[] commandvalue =  (Object[])ajaxResult.get("data");

                //命令集合
                List<String> commandList = (List<String>) commandvalue[0];
                //参数集合
                List<ValueInformationVO> valueInformationVOList = (List<ValueInformationVO>)commandvalue[1];
                //解决问题
                String solveProblem = solveProblem(informationList, commandList, valueInformationVOList);//userName

                if (solveProblem.equals("成功")){
                    pojo.setResolved("是");
                    pojo.setIfQuestion("无问题");
                    int i = switchProblemService.updateSwitchProblem(pojo);
                    if (i<=0){
                        return AjaxResult.error("修复失败");
                    }
                }

                if (informationList.get(1).toString().equalsIgnoreCase("ssh")){
                    connectMethod.closeConnect((SshConnect)informationList.get(8));
                }else if (informationList.get(1).toString().equalsIgnoreCase("telnet")){
                    telnetSwitchMethod.closeSession((TelnetComponent)informationList.get(9));
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
        TelnetComponent telnetComponent = null;

        if (requestConnect_way.equalsIgnoreCase("ssh")){
            connectMethod = (SshMethod)informationList.get(6);
            sshConnect = (SshConnect)informationList.get(8);
        }else if (requestConnect_way.equalsIgnoreCase("telnet")){
            telnetSwitchMethod = (TelnetSwitchMethod)informationList.get(6);
            telnetComponent = (TelnetComponent)informationList.get(9);
        }

        String commandString =""; //预设交换机返回结果

        LoginUser loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getUsername();

        //user_String, connectMethod, telnetSwitchMethod
        for (String command:commandList){
            //根据 连接方法 判断 实际连接方式
            //并发送命令 接受返回结果
            System.err.print("\r\n"+"命令："+command+"\r\n");
            if (requestConnect_way.equalsIgnoreCase("ssh")){

                WebSocketService.sendMessage("badao"+userName,command);
                commandString = connectMethod.sendCommand((String) informationList.get(2),sshConnect,command,null);
                //commandString = Utils.removeLoginInformation(commandString);
            }else if (requestConnect_way.equalsIgnoreCase("telnet")){

                WebSocketService.sendMessage("badao"+userName,command);
                commandString = telnetSwitchMethod.sendCommand((String) informationList.get(2),telnetComponent,command,null);
                //commandString = Utils.removeLoginInformation(commandString);
            }
            //判断命令是否错误 错误为false 正确为true
            if (!Utils.judgmentError(commandString)){
                //如果返回信息错误 则结束当前命令，执行 遍历数据库下一条命令字符串(,)
                break;
            }

            //去除其他 交换机登录信息
            commandString = Utils.removeLoginInformation(commandString);

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
                WebSocketService.sendMessage("badao"+userName,current_return_log);
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

                WebSocketService.sendMessage("badao"+userName,current_identifier);
            }else if (commandString_split.length == 1){
                returnRecord.setCurrentIdentifier("\r\n"+commandString_split[0]+"\r\n");
                WebSocketService.sendMessage("badao"+userName,"\r\n"+commandString_split[0]+"\r\n");
            }
            //存储交换机返回数据 插入数据库
            int insert_Int = returnRecordService.insertReturnRecord(returnRecord);
            if (insert_Int <= 0){
                return "失败";
            }
        }

        return "成功";
    }



    /**
     * @method: 查询扫描出的问题表 放入 websocket
     * @Param: []
     * @return: java.util.List<com.sgcc.sql.domain.SwitchProblem>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("getUnresolvedProblemInformationByData")
    @MyLog(title = "查询扫描出的问题列表", businessType = BusinessType.OTHER)
    public List<ScanResultsVO> getUnresolvedProblemInformationByData(){

        LoginUser loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getUsername();

        List<SwitchProblemVO> switchProblemList = switchProblemService.selectUnresolvedProblemInformationByDataAndUserName(null,userName);
        for (SwitchProblemVO switchProblemVO:switchProblemList){
            Date date1 = new Date();
            switchProblemVO.hproblemId =  Long.valueOf(Utils.getTimestamp(date1)+""+ (int)(Math.random()*10000+1)).longValue();
            List<SwitchProblemCO> switchProblemCOList = switchProblemVO.getSwitchProblemCOList();
            for (SwitchProblemCO switchProblemCO:switchProblemCOList){
                valueInformationService = SpringBeanUtil.getBean(IValueInformationService.class);//解决 多线程 service 为null问题
                List<ValueInformationVO> valueInformationVOList = valueInformationService.selectValueInformationVOListByID(switchProblemCO.getValueId());
                for (ValueInformationVO valueInformationVO:valueInformationVOList){
                    Date date2 = new Date();
                    valueInformationVO.hproblemId = Long.valueOf(Utils.getTimestamp(date2)+""+ (int)(Math.random()*10000+1)).longValue();
                }
                Date date3 = new Date();
                switchProblemCO.hproblemId = Long.valueOf(Utils.getTimestamp(date3)+""+ (int)(Math.random()*10000+1)).longValue();
                switchProblemCO.setValueInformationVOList(valueInformationVOList);
            }
        }
        //将IP地址去重放入set集合中
        HashSet<String> time_hashSet = new HashSet<>();
        for (SwitchProblemVO switchProblemVO:switchProblemList){
            Date createTime = switchProblemVO.getCreateTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String time = simpleDateFormat.format(createTime);
            time_hashSet.add(time);
        }
        List<ScanResultsVO> scanResultsVOList = new ArrayList<>();
        for (String time:time_hashSet){
            ScanResultsVO scanResultsVO = new ScanResultsVO();
            scanResultsVO.setCreateTime(time);
            scanResultsVOList.add(scanResultsVO);
        }
        for (ScanResultsVO scanResultsVO:scanResultsVOList){
            String createTime = scanResultsVO.getCreateTime();
            for (SwitchProblemVO switchProblemVO:switchProblemList){
                Date time = switchProblemVO.getCreateTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String format_time = simpleDateFormat.format(time);
                if (format_time.equals(createTime)){
                    List<SwitchProblemVO> switchProblemVOList = scanResultsVO.getSwitchProblemVOList();
                    if (switchProblemVOList == null){
                        List<SwitchProblemVO> switchProblemVOS = new ArrayList<>();
                        switchProblemVOS.add(switchProblemVO);
                        scanResultsVO.setSwitchProblemVOList(switchProblemVOS);
                    }else {
                        switchProblemVOList.add(switchProblemVO);
                        scanResultsVO.setSwitchProblemVOList(switchProblemVOList);
                    }
                }
            }
        }
        return scanResultsVOList;
    }
}