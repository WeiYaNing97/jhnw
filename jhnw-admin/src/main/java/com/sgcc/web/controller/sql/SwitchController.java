package com.sgcc.web.controller.sql;

import com.sgcc.common.annotation.Excel;
import com.sgcc.common.annotation.Log;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.entity.SysUser;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.poi.ExcelUtil;
import com.sgcc.connect.method.SshMethod;
import com.sgcc.connect.method.TelnetSwitchMethod;
import com.sgcc.connect.util.SshInformation;
import com.sgcc.sql.domain.ReturnRecord;
import com.sgcc.web.controller.webSocket.WebSocketService;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2021年11月19日 15:57
 */
@RestController
@RequestMapping("/sql/switch_command")
public class SwitchController  {

    @RequestMapping("/SwitchEntry")

    public List<SwitchEntry> SwitchEntry(String file) {

        file = "C:\\Users\\Administrator\\Desktop\\123.xls";

        List<SwitchEntry> switchEntries = new ArrayList<>();

        try {
            List<Object> excel = excel(file);
            for (Object object:excel){
                List<String> stringList = (List<String>) object;
                if (stringList.get(0) == null || stringList.get(0)==""
                    ||stringList.get(1) == null || stringList.get(1)==""
                        ||stringList.get(2) == null || stringList.get(2)==""){
                    return switchEntries;
                }
                SwitchEntry switchEntry = new SwitchEntry();

                switchEntry.setSwitchIp(stringList.get(0));
                switchEntry.setSwitchName(stringList.get(1));
                switchEntry.setSwitchPassword(stringList.get(2));
                switchEntry.setConnectionMode(stringList.get(3));
                switchEntry.setPortNumber(stringList.get(4));

                switchEntries.add(switchEntry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return switchEntries;
    }


    public static List<Object> excel(String file) throws Exception {
        //用流的方式先读取到你想要的excel的文件
        FileInputStream fis = new FileInputStream(new File(file));
        //解析excel
        POIFSFileSystem pSystem = new POIFSFileSystem(fis);
        //获取整个excel
        HSSFWorkbook hb = new HSSFWorkbook(pSystem);
        //获取第一个表单sheet
        HSSFSheet sheet = hb.getSheetAt(0);
        //获取第一行
        int firstrow = sheet.getFirstRowNum();
        //获取最后一行
        int lastrow = sheet.getLastRowNum();
        List<Object> objectList = new ArrayList<>();
        //循环行数依次获取列数
        for (int i = firstrow; i < lastrow+2; i++) {
            //获取哪一行i
            Row row = sheet.getRow(i);
            if (row != null) {
                //获取这一行的第一列
                int firstcell = row.getFirstCellNum();
                //获取这一行的最后一列
                int lastcell = row.getLastCellNum();
                //创建一个集合，用处将每一行的每一列数据都存入集合中
                List<String> list = new ArrayList<>();
                for (int j = firstcell; j <lastcell; j++) {
                    //获取第j列
                    Cell cell = row.getCell(j);

                    if (cell != null) {
                        list.add(cell.toString());
                    }else {
                        list.add("");
                    }

                }
                objectList.add(list);
            }
        }
        fis.close();
        return objectList;
    }

    @RequestMapping("/export")
    public AjaxResult export()
    {
        List<SwitchEntry> list = new ArrayList<>();

        ExcelUtil<SwitchEntry> util = new ExcelUtil<SwitchEntry>(SwitchEntry.class);

        return util.exportExcel(list, "交换机登录信息","交换机登录信息");
    }
















    /*public static SshMethod connectMethod;
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

    public static TelnetSwitchMethod telnetSwitchMethod;*/

    /***
     * @method: 登录时 创建 uuid 会话开始
     * @Param: []
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    /*@RequestMapping("createuuid")
    public void createuuid(String mode,int port,String ip,String name, String password){
        this.way = mode;
        this.portID = port;
        Global.logIP = ip;
        Global.logUser = name;
        Global.logPassword = password;
        //会话记录 一次会话一个uuid
        Global.uuid = UUID.randomUUID().toString();
        System.out.print(Global.uuid+"\r\n");
    }*/


    /***
     * @method: 连接交换机
     * @Param: [mode 连接方式, ip IP地址, port, name, password, end]
     * @return: com.sgcc.jhnw.util.Result
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    /*@RequestMapping("requestConnect")
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
    }*/

    /**
    * @method: 发送命令
    * @Param: []
    * @return: java.lang.String
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    /*@RequestMapping("sendMessage")
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
    }*/
}