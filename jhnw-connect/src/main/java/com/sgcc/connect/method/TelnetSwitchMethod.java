package com.sgcc.connect.method;

import com.sgcc.connect.util.TelnetSwitch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 天幕顽主
 * 通过Telnet连接Windos、linux、交换机
 * @E-mail: WeiYaNing97@163.com
 * @date 2021年09月22日 11:46
 */
@RestController
@ResponseBody
@RequestMapping("/sql/TelnetSwitchMethod")
public class TelnetSwitchMethod {
    private String hostIp;
    private String userName;
    private String userPassword;
    private TelnetSwitch telnet;

    //空格
    private static final String BLANKSPACE = new String(new byte[] { 32 });
    private String moreCmd = BLANKSPACE;

    /**
     * 连接Telnet 交换机
     * @param ip 交换机IP
     * @param name 用户名称
     * @param password 用户密码
     * @param end 结尾标识符：#,>
     * @throws Exception
     */
    @RequestMapping("requestConnect")
    public boolean requestConnect(String ip,int port,String name,String password,String end) {
        this.hostIp = ip;
        this.userName = name;
        this.userPassword = password;
        this.telnet = new TelnetSwitch(hostIp, port);
        boolean login = telnet.login(userName, userPassword, end);
        return login;
    }

    /**
     * 发送命令，返回结果
     * @param command 发送命令：参数
     * @return 返回结果
     * @throws Exception
     */
    @RequestMapping("/sendCommand")
    public String sendCommand(String command) {
        String stringCommand = telnet.sendCommand(command);
        if (stringCommand == null){
        }
        while (true){
            stringCommand = stringCommand + Judge(stringCommand);
            if (!(stringCommand.endsWith("---- More ----"))){
                return stringCommand;
            }
        }
    }


    /**
     * 发送命令，返回结果
     * @param command 发送命令：参数
     * @return 返回结果
     * @throws Exception
     */
    @RequestMapping("/sendCommandAll")
    public String sendCommandAll() {
        List<String> commandList = new ArrayList<>();
        commandList.add("display ver");
        commandList.add("display cu");
        commandList.add("display ver");
        commandList.add("display cu");
        commandList.add("display ver");
        commandList.add("display cu");

        for (String command:commandList){
            System.err.print("\r\n"+command);
            String command1 = sendCommand(command);
            System.err.print("\r\n"+command1);
        }
        return null;
    }

    /**
     * 关闭连接
     * @throws Exception
     */
    @RequestMapping("/closeConnect")
    public void closeConnect(){
        telnet.disconnect();
    }

    public String Judge(String string){
        String str = "";
        String[] split = string.split("\\n");
        boolean equals = split[split.length - 1].trim().equals("---- More ----");
        if (equals){
            str = str + telnet.sendCommand(" ");
        }
        return str;
    }
}