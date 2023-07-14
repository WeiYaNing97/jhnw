package com.sgcc.share.util;

import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.domain.ReturnRecord;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.service.IReturnRecordService;
import com.sgcc.share.webSocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.concurrent.*;

public class ExecuteCommand {

    @Autowired
    private static IReturnRecordService returnRecordService;

    //命令返回信息
    private static String command_string = null;
    /**
     * 根据交换机信息类 与 具体命令，执行并返回交换机返回信息
     * @param switchParameters
     * @param command
     * @return
     */
    public String executeScanCommandByCommand(SwitchParameters switchParameters, String command) {
        //交换机返回信息 插入 数据库
        ReturnRecord returnRecord = new ReturnRecord();
        /*程序登录用户*/
        returnRecord.setUserName(switchParameters.getLoginUser().getUsername());
        /*交换机IP、品牌、型号、版本、子版本*/
        returnRecord.setSwitchIp(switchParameters.getIp());
        returnRecord.setBrand(switchParameters.getDeviceBrand());
        returnRecord.setType(switchParameters.getDeviceModel());
        returnRecord.setFirewareVersion(switchParameters.getFirmwareVersion());
        returnRecord.setSubVersion(switchParameters.getSubversionNumber());
        /*交换机执行的命令*/
        returnRecord.setCurrentCommLog(command);

        /*交换机返回信息 插入数据库状态 为-1时错误 否则为交换机返回信息 在数据库中的ID*/
        int insert_id = 0;
        /*交换机返回信息 是否存在故障的标志 默认为 true*/
        boolean deviceBrand = true;
        do {
            deviceBrand = true;
            if (switchParameters.getMode().equalsIgnoreCase("ssh")) {
                /*当交换机连接协议为 SSH时*/
                // todo 交换机发送命令的前端回显
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(), switchParameters.getIp()+"发送:" + command+"\r\n");
                try {
                    PathHelper.writeDataToFile(switchParameters.getIp()+"发送:" + command+"\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }


                /**
                 * 交换机返回信息 及 超时
                 */
                ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                // 提交要执行的方法，并设置超时时间为2秒
                ScheduledFuture<?> future = executor.schedule(() -> {
                    // 执行的方法逻辑
                    command_string = switchParameters.getConnectMethod().sendCommand(switchParameters.getIp(), switchParameters.getSshConnect(), command, null);

                }, 1, TimeUnit.SECONDS);
                try {
                    // 等待任务执行结果，同时设置超时时间为20秒
                    future.get(20, TimeUnit.SECONDS);
                } catch (TimeoutException e) {

                    try {
                        String subversionNumber = switchParameters.getSubversionNumber();
                        if (subversionNumber!=null){
                            subversionNumber = "、"+subversionNumber;
                        }
                        WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"异常:" +
                                "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:命令："+ command+"未获取到交换机返回信息\r\n");
                        PathHelper.writeDataToFileByName(
                                "IP地址为:"+switchParameters.getIp()+","+
                                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                        "问题为:命令："+ command+"未获取到交换机返回信息\r\n"
                                , "问题日志");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    command_string = switchParameters.getConnectMethod().sendCommand(switchParameters.getIp(), switchParameters.getSshConnect(), " ", null);

                    return null;

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    /**
                     * 在Java的try-catch-finally语句中，如果try或catch块中存在return语句，会先执行finally块中的代码，然后再返回到调用的地方。
                     * 无论try或catch块中是否存在return语句，finally块中的代码都会被执行。
                     * finally块通常用于清理资源、关闭文件或释放锁等操作，以确保在程序执行过程中不论是否发生异常都能正确地执行这些操作。
                     * 需要注意的是，如果finally块中也存在return语句，它将会覆盖try或catch块中的return语句，即最终返回的结果是finally块中的return语句的返回值。
                     */
                    executor.shutdown();
                }






            } else if (switchParameters.getMode().equalsIgnoreCase("telnet")) {
                // todo 交换机发送命令的前端回显
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(), switchParameters.getIp()+"发送:" + command+"\r\n");
                try {
                    PathHelper.writeDataToFile(switchParameters.getIp()+"发送:" + command+"\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                command_string = switchParameters.getTelnetSwitchMethod().sendCommand(switchParameters.getIp(), switchParameters.getTelnetComponent(), command, null);
            }
            returnRecord.setCurrentReturnLog(command_string);
            //修整返回信息
            command_string = MyUtils.trimString(command_string);
            //粗略查看是否存在 故障 存在故障返回 false 不存在故障返回 true
            // 存在故障返回 false
            if (!FunctionalMethods.switchfailure(switchParameters, command_string)) {
                /*字段按行分割为 行信息数组 LineInformation*/
                String[] commandStringSplit = command_string.split("\r\n");
                /* 遍历交换机行信息*/
                for (String LineInformation : commandStringSplit) {
                    /*当行信息包含 故障时 进入错误代码库*/
                    deviceBrand = FunctionalMethods.switchfailure(switchParameters, LineInformation);
                    if (!deviceBrand) {

                        try {
                            String subversionNumber = switchParameters.getSubversionNumber();
                            if (subversionNumber!=null){
                                subversionNumber = "、"+subversionNumber;
                            }
                            WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"异常:" +
                                    "IP地址为:"+switchParameters.getIp()+","+
                                    "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                    "问题为:返回结果异常\r\n"+
                                    "命令:"+command+
                                    "异常信息:"+LineInformation+"\r\n");
                            PathHelper.writeDataToFileByName(
                                    "IP地址为:"+switchParameters.getIp()+","+
                                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                            "问题为:返回结果异常\r\n"+
                                            "命令:"+command+
                                            "异常信息:"+LineInformation+"\r\n"
                                    , "问题日志");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        returnRecord.setCurrentIdentifier(switchParameters.getIp() + "出现故障:"+LineInformation+"\r\n");
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
            /*如果有有故障时 deviceBrand为 false 的重新执行命令*/
        }while (!deviceBrand);
        //返回信息表，返回插入条数
        returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);
        returnRecord = returnRecordService.selectReturnRecordById(Integer.valueOf(insert_id).longValue());

        //去除其他 交换机登录信息
        command_string = FunctionalMethods.removeLoginInformation(command_string);

        //按行切割: 字段按行分割为 行信息数组 LineInformation
        String[] LineInformation = command_string.split("\r\n");
        /*当返回信息不止为 标识符时*/
        if (LineInformation.length != 1){
            /*获取返回日志 去除 标识符
             * 标识符为 最后一个元素  注意要删除 \r\n  所以-2*/
            String current_return_log = command_string.substring(0,command_string.length()-LineInformation[LineInformation.length-1].length()-2);

            // TODO 去掉^之前的 \r\n
            if (current_return_log.indexOf("^")!=-1){
                current_return_log = current_return_log.substring(2 + LineInformation[LineInformation.length-1].trim().length(),current_return_log.length());
            }

            returnRecord.setCurrentReturnLog(current_return_log);

            // todo  交换机返回日志的前端回显
            WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),switchParameters.getIp()+"接收:"+current_return_log+"\r\n");
            try {
                PathHelper.writeDataToFile(switchParameters.getIp()+"接收:"+current_return_log+"\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //按行切割最后一位应该是 标识符
        String current_identifier = LineInformation[LineInformation.length-1].trim();
        returnRecord.setCurrentIdentifier(current_identifier);

        // todo  交换机返回标识符的前端回显
        WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),switchParameters.getIp()+"接收:"+current_identifier+"\r\n");
        try {
            PathHelper.writeDataToFile(switchParameters.getIp()+"接收:"+current_identifier+"\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //返回信息表，返回插入条数
        returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);
        returnRecordService.updateReturnRecord(returnRecord);

        //粗略判断命令是否错误 错误为false 正确为true
        if (!(FunctionalMethods.judgmentError( switchParameters,command_string))){
            /*字段按行分割为 行信息数组 LineInformation*/
            for (String string_split:LineInformation){
                /* 按行 去查找具体错误信息*/
                if (!FunctionalMethods.judgmentError( switchParameters,string_split)){
                    try {
                        String subversionNumber = switchParameters.getSubversionNumber();
                        if (subversionNumber!=null){
                            subversionNumber = "、"+subversionNumber;
                        }
                        WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"异常:" +
                                "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:返回结果异常\r\n"+
                                "命令:"+command+
                                "异常信息:"+command_string+"\r\n");
                        PathHelper.writeDataToFileByName(
                                "IP地址为:"+switchParameters.getIp()+","+
                                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                        "问题为:返回结果异常\r\n"+
                                        "命令:"+command+
                                        "异常信息:"+command_string+"\r\n"
                                , "问题日志");
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
