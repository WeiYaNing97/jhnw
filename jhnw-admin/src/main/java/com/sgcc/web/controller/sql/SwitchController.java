package com.sgcc.web.controller.sql;

import com.sgcc.connect.method.SshMethod;
import com.sgcc.connect.method.TelnetSwitchMethod;
import com.sgcc.sql.domain.ReturnRecord;
import com.sgcc.web.controller.webSocket.WebSocketService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2021年11月19日 15:57
 */
@RestController
@RequestMapping("/sql/switch_command")
public class SwitchController {

    public static SshMethod connectMethod;
    //连接方法
    public static String way;
    //ip地址
    private String hostIp;
    //端口号
    private int portID = 22;
    //姓名
    private String userName;
    //密码
    private String userPassword;
    //结尾标识符
    private String endIdentifier;

    public static TelnetSwitchMethod telnetSwitchMethod;

    /***
     * @method: 登录时 创建 uuid 会话开始
     * @Param: []
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("createuuid")
    public void createuuid(String mode,int port,String ip,String name, String password){
        this.way = mode;
        this.portID = port;
        Global.logIP = ip;
        Global.logUser = name;
        Global.logPassword = password;
        //会话记录 一次会话一个uuid
        Global.uuid = UUID.randomUUID().toString();
        System.out.print(Global.uuid+"\r\n");
    }
    /***
     * @method: 连接交换机
     * @Param: [mode 连接方式, ip IP地址, port, name, password, end]
     * @return: com.sgcc.jhnw.util.Result
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("requestConnect")
    public boolean requestConnect(String mode,String ip, String name, String password) {
        this.way = mode;
        this.hostIp = ip;
        this.userName = name;
        this.userPassword = password;
        boolean is_the_connection_successful =false;
        if (way.equalsIgnoreCase("ssh")){
            //创建连接方法
            this.connectMethod = new SshMethod();
            //连接telnet 或者 ssh
            is_the_connection_successful = connectMethod.requestConnect(hostIp, portID, userName, userPassword);
            return is_the_connection_successful;
        }else if (way.equalsIgnoreCase("telnet")){
            this.telnetSwitchMethod = new TelnetSwitchMethod();
            is_the_connection_successful = telnetSwitchMethod.requestConnect(hostIp, portID, userName, userPassword, endIdentifier);
            return is_the_connection_successful;
        }else {
            return is_the_connection_successful;
        }
    }

    @RequestMapping("sendMessage")
    public String sendMessage() {
        String way = this.way;
        String basicInformation = "display cu";
        //多个命令是由,号分割的，所以需要根据, 来分割。例如：display device manuinfo,display ver
        String[] commandsplit = basicInformation.split(",");
        String commandString = ""; //预设交换机返回结果
        String return_sum = ""; //当前命令字符串 返回命令总和("\r\n"分隔)
        //遍历数据表命令 分割得到的 命令数组
        for (String command : commandsplit) {
            //根据 连接方法 判断 实际连接方式
            //并发送命令 接受返回结果
            if (way.equalsIgnoreCase("ssh")) {

                WebSocketService.sendMessage("badao", command);
                commandString = connectMethod.sendCommand(command,null);
            } else if (way.equalsIgnoreCase("telnet")) {

                WebSocketService.sendMessage("badao", command);
                commandString = telnetSwitchMethod.sendCommand(command,null);
            }
            //判断命令是否错误 错误为false 正确为true
            if (!Utils.judgmentError(commandString)) {
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
            if (!current_return_log_substring_end.equals("\r\n")) {
                current_return_log = current_return_log + "\r\n";
            }
            String current_return_log_substring_start = current_return_log.substring(0, 2);
            if (!current_return_log_substring_start.equals("\r\n")) {
                current_return_log = "\r\n" + current_return_log;
            }

            WebSocketService.sendMessage("badao", current_return_log);
            //当前标识符 如：<H3C> [H3C]
            String current_identifier = commandString_split[commandString_split.length - 1].trim();
            returnRecord.setCurrentIdentifier(current_identifier);
            //当前标识符前后都没有\r\n
            String current_identifier_substring_end = current_identifier.substring(current_identifier.length() - 2, current_identifier.length());
            if (current_identifier_substring_end.equals("\r\n")) {
                current_identifier = current_identifier.substring(0, current_identifier.length() - 2);
            }
            String current_identifier_substring_start = current_identifier.substring(0, 2);
            if (current_identifier_substring_start.equals("\r\n")) {
                current_identifier = current_identifier.substring(2, current_identifier.length());
            }

            WebSocketService.sendMessage("badao", current_identifier);
            //当前命令字符串 返回命令总和("\r\n"分隔)
            return_sum += commandString + "\r\n\r\n";
        }
        return null;
    }
}