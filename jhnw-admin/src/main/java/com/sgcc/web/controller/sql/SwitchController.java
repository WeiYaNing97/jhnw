package com.sgcc.web.controller.sql;

import com.sgcc.connect.method.SshMethod;
import com.sgcc.connect.method.TelnetSwitchMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}