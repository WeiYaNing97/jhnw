package com.sgcc.sql.util;

import com.sgcc.connect.util.SpringBeanUtil;
import com.sgcc.sql.domain.ReturnRecord;
import com.sgcc.sql.parametric.SwitchParameters;
import com.sgcc.sql.service.IReturnRecordService;
import com.sgcc.sql.webSocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * 功能方法类
 */
public class FunctionalMethods {

    @Autowired
    private static IReturnRecordService returnRecordService;

    /**
     * 根据交换机信息类 与 具体命令，执行并返回交换机返回信息
     * @param switchParameters
     * @param command
     * @return
     */
    public static String executeScanCommandByCommand(SwitchParameters switchParameters, String command) {

        //执行命令
        //命令返回信息
        String command_string = null;
        //交换机返回信息 插入 数据库
        ReturnRecord returnRecord = new ReturnRecord();

        int insert_id = 0;
        returnRecord.setUserName(switchParameters.getLoginUser().getUsername());
        returnRecord.setSwitchIp(switchParameters.getIp());
        returnRecord.setBrand(switchParameters.getDeviceBrand());
        returnRecord.setType(switchParameters.getDeviceModel());
        returnRecord.setFirewareVersion(switchParameters.getFirmwareVersion());
        returnRecord.setSubVersion(switchParameters.getSubversionNumber());
        returnRecord.setCurrentCommLog(command);
        boolean deviceBrand = true;

        do {
            deviceBrand = true;

            if (switchParameters.getMode().equalsIgnoreCase("ssh")) {
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(), switchParameters.getIp()+"发送:" + command+"\r\n");

                try {
                    PathHelper.writeDataToFile(switchParameters.getIp()+"发送:" + command+"\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                command_string = switchParameters.getConnectMethod().sendCommand(switchParameters.getIp(), switchParameters.getSshConnect(), command, null);
                //command_string = Utils.removeLoginInformation(command_string);
            } else if (switchParameters.getMode().equalsIgnoreCase("telnet")) {
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(), switchParameters.getIp()+"发送:" + command+"\r\n");

                try {
                    PathHelper.writeDataToFile(switchParameters.getIp()+"发送:" + command+"\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                command_string = switchParameters.getTelnetSwitchMethod().sendCommand(switchParameters.getIp(), switchParameters.getTelnetComponent(), command, null);
                //command_string = Utils.removeLoginInformation(command_string);
            }

            returnRecord.setCurrentReturnLog(command_string);

            //粗略查看是否存在 故障 存在故障返回 false 不存在故障返回 true
            boolean switchfailure = MyUtils.switchfailure(switchParameters, command_string);

            // 存在故障返回 false
            if (!switchfailure) {
                String[] commandStringSplit = command_string.split("\r\n");
                for (String returnString : commandStringSplit) {
                    deviceBrand = MyUtils.switchfailure(switchParameters, returnString);
                    if (!deviceBrand) {
                        System.err.println("\r\n"+switchParameters.getIp() + "故障:"+returnString+"\r\n");
                        WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"故障:"+switchParameters.getIp()+":"+returnString+"\r\n");

                        try {
                            PathHelper.writeDataToFile("故障:"+switchParameters.getIp()+":"+returnString+"\r\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        returnRecord.setCurrentIdentifier(switchParameters.getIp() + "出现故障:"+returnString+"\r\n");
                        if (switchParameters.getMode().equalsIgnoreCase("ssh")){
                            switchParameters.getConnectMethod().sendCommand(switchParameters.getIp(),switchParameters.getSshConnect(),"\r ",switchParameters.getNotFinished());
                        }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
                            switchParameters.getTelnetSwitchMethod().sendCommand(switchParameters.getIp(),switchParameters.getTelnetComponent(),"\n ",switchParameters.getNotFinished());
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
        returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);
        returnRecord = returnRecordService.selectReturnRecordById(Integer.valueOf(insert_id).longValue());

        //去除其他 交换机登录信息
        command_string = MyUtils.removeLoginInformation(command_string);
        //修整返回信息
        command_string = MyUtils.trimString(command_string);

        //按行切割
        String[] split = command_string.split("\r\n");


        String current_return_log = "";
        if (split.length != 1){
            current_return_log = command_string.substring(0,command_string.length()-split[split.length-1].length()-2).trim();
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

        }

        WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),switchParameters.getIp()+"接收:"+current_return_log+"\r\n");

        try {
            PathHelper.writeDataToFile(switchParameters.getIp()+"接收:"+current_return_log+"\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),switchParameters.getIp()+"接收:"+current_identifier+"\r\n");
        try {
            PathHelper.writeDataToFile(switchParameters.getIp()+"接收:"+current_identifier+"\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }


        //返回信息表，返回插入条数
        returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);
        int update = returnRecordService.updateReturnRecord(returnRecord);

        //判断命令是否错误 错误为false 正确为true
        if (!(MyUtils.judgmentError( switchParameters,command_string))){
            //  简单检验，命令正确，新命令  commandLogic.getEndIndex()

            String[] returnString_split = command_string.split("\r\n");

            for (String string_split:returnString_split){
                if (!MyUtils.judgmentError( switchParameters,string_split)){
                    System.err.println("\r\n"+switchParameters.getIp() +":" +command+ "错误:"+command_string+"\r\n");
                    WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"风险:"+switchParameters.getIp()  +"命令:" +command +":"+command_string+"\r\n");
                    try {
                        PathHelper.writeDataToFile("风险:"+switchParameters.getIp() + ":" +command +":"+command_string+"\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            }

        }

        return command_string;
    }
}
