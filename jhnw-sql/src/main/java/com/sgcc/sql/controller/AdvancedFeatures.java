package com.sgcc.sql.controller;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.connect.method.SshMethod;
import com.sgcc.connect.method.TelnetSwitchMethod;
import com.sgcc.connect.util.SpringBeanUtil;
import com.sgcc.connect.util.SshConnect;
import com.sgcc.connect.util.TelnetComponent;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.ICommandLogicService;
import com.sgcc.sql.service.IReturnRecordService;
import com.sgcc.sql.service.ITotalQuestionTableService;
import com.sgcc.sql.util.MyUtils;
import com.sgcc.sql.util.PathHelper;
import com.sgcc.sql.webSocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdvancedFeatures {

    @Autowired
    private static IReturnRecordService returnRecordService;
    @Autowired
    private static ITotalQuestionTableService totalQuestionTableService;
    @Autowired
    private static ICommandLogicService commandLogicService;

    public static void analyseOspf(Map<String,String> user_String, Map<String,Object> user_Object) {

        String commandReturn = executeScanCommandByCommand(user_String, user_Object);

        AjaxResult ospfListByString = getOspfListByString(commandReturn);

        if(ospfListByString.get("msg").equals("操作成功")){
            List<Ospf> ospfList = (List<Ospf>) ospfListByString.get("data");
            for (Ospf ospf:ospfList){
                try {
                    PathHelper.writeDataToFileByName(user_String.get("ip")+":" + ospf.toString()+"\r\n","ospf");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    public static AjaxResult getOspfListByString(String returnString) {
        returnString = MyUtils.trimString(returnString);
        String[] returnStringSplit = returnString.split("\r\n");

        String NameLine = null;
        List<String> valueList = new ArrayList<>();

        for (int number = 0 ;number < returnStringSplit.length; number++){
            if (NameLine != null){
                valueList.add(returnStringSplit[number].trim());
            }

            if (NameLine == null){
                if (returnStringSplit[number].indexOf("State") != -1 || returnStringSplit[number].indexOf("state") != -1){
                    NameLine = returnStringSplit[number];
                }
            }
        }

        Ospf propertyValueSubscripts = null;
        int number = 0 ;
        List<Ospf> ospfList = null;
        if (NameLine == null){
            return AjaxResult.error("获取标题行失败");
        }else {
            List<Object> property = getPropertyValueSubscripts(NameLine);
            propertyValueSubscripts = (Ospf) property.get(0);
            number = (Integer) property.get(1);
        }
        if (propertyValueSubscripts == null){
            return AjaxResult.error("获取标题内容失败");
        }else {

            String value = valueList.get(0);
            String[] value_split = value.split(" ");
            int len = value_split.length;

            if (len != number){
                valueList = removOspfSpaceCharacter(valueList);
            }

            value = valueList.get(0);
            value_split = value.split(" ");
            len = value_split.length;

            if (len != number){
                return AjaxResult.error("获取参数内容失败");
            }else {
                ospfList= getPojoList(propertyValueSubscripts,valueList,number);

            }

        }
        return AjaxResult.success(ospfList);
    }

    public static List<Ospf> getPojoList(Ospf pojo,List<String> stringList,int number) {
        List<Ospf> ospfList = new ArrayList<>();
        for (String pojoString:stringList){
            Ospf ospf = new Ospf();
            String[] pojoStringSplit = pojoString.split(" ");
            if (number != pojoStringSplit.length){
                break;
            }

            if(pojo.getNeighborID() != null){
                ospf.setNeighborID(pojoStringSplit[Integer.valueOf(pojo.getNeighborID()).intValue()]);
            }
            if(pojo.getPri() != null){
                ospf.setPri(pojoStringSplit[Integer.valueOf(pojo.getPri()).intValue()]);
            }
            if(pojo.getState() != null){
                ospf.setState(pojoStringSplit[Integer.valueOf(pojo.getState()).intValue()]);
            }
            if(pojo.getDeadTime() != null){
                ospf.setDeadTime(pojoStringSplit[Integer.valueOf(pojo.getDeadTime()).intValue()]);
            }
            if(pojo.getAddress() != null){
                ospf.setAddress(pojoStringSplit[Integer.valueOf(pojo.getAddress()).intValue()]);
            }
            if(pojo.getPortNumber() != null){
                ospf.setPortNumber(pojoStringSplit[Integer.valueOf(pojo.getPortNumber()).intValue()]);
            }
            if(pojo.getBFDState() != null){
                ospf.setBFDState(pojoStringSplit[Integer.valueOf(pojo.getBFDState()).intValue()]);
            }

            ospfList.add(ospf);
        }
        return ospfList;
    }

    /*去除属性值中间空格*/
    public static List<String> removOspfSpaceCharacter(List<String> strings) {

        String  ospfSpaceCharacter = Configuration.ospfSpaceCharacter;
        String[] ospfSpaceCharacterSplit = ospfSpaceCharacter.split(";");
        for (String SpaceCharacter:ospfSpaceCharacterSplit){
            for (int num = 0 ; num <strings.size();num++){
                strings.set(num,strings.get(num).replaceAll(SpaceCharacter+" ",SpaceCharacter));
            }
        }
        return strings;
    }



    /*获取属性值下标*/
    public static List<Object> getPropertyValueSubscripts(String information) {
        OspfEnum.assignment();
        information = MyUtils.trimString(information);

        String[] string_split = information.trim().split(" ");
        String word = "";
        Ospf ospf = new Ospf();
        int number = 0;
        for (int num = 0 ; num < string_split.length ; num++ ){

            word = word + " "+string_split[num];

            String enumeratorValues = OspfEnum.enumeratorValues(word.trim());
            if (enumeratorValues != null){

                switch (enumeratorValues){
                    case "neighborID":
                        ospf.setNeighborID(number+"");
                        break;
                    case "pri":
                        ospf.setPri(number+"");
                        break;
                    case "state":
                        ospf.setState(number+"");
                        break;
                    case "deadTime":
                        ospf.setDeadTime(number+"");
                        break;
                    case "address":
                        ospf.setAddress(number+"");
                        break;
                    case "portNumber":
                        ospf.setPortNumber(number+"");
                        break;
                    case "BFDState":
                        ospf.setBFDState(number+"");
                        break;
                }
                number++;
                word = "";
            }

        }
        if (word.equals("")){
            List<Object> objects = new ArrayList<>();
            objects.add(ospf);
            objects.add(number);
            return objects;
        }
        return null;
    }



    /**
     * @method: 根据命令ID获取具体命令，执行并返回交换机返回信息
     * @Param:
     * @return:  返回的是 解决问题ID
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     * 分析ID 连接方式 ssh和telnet连接
     */
    public static String executeScanCommandByCommand(Map<String,String> user_String,
                                                           Map<String,Object> user_Object) {


        TotalQuestionTable totalQuestionTable = new TotalQuestionTable();
        totalQuestionTable.setBrand(user_String.get("deviceBrand"));
        totalQuestionTable.setType(user_String.get("deviceModel"));
        totalQuestionTable.setFirewareVersion(user_String.get("firmwareVersion"));
        totalQuestionTable.setSubVersion(user_String.get("subversionNumber"));
        totalQuestionTable.setTemProName("OSPF");
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.queryAdvancedFeaturesList(totalQuestionTable);

        TotalQuestionTable totalQuestionTablePojo = new TotalQuestionTable();
        if (totalQuestionTables.size()==0){
            return null;
        }else if (totalQuestionTables.size()==1){
            totalQuestionTablePojo = totalQuestionTables.get(0);
        }else {
            totalQuestionTables = MyUtils.ObtainPreciseEntityClasses(totalQuestionTables);
            totalQuestionTablePojo = totalQuestionTables.get(0);
        }

        String commandId = totalQuestionTablePojo.getCommandId();
        commandId = commandId.substring(2,commandId.length());
        commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
        CommandLogic commandLogic = commandLogicService.selectCommandLogicById(commandId);
        String command = commandLogic.getCommand();
        SshConnect sshConnect = (SshConnect) user_Object.get("sshConnect");
        SshMethod connectMethod = (SshMethod) user_Object.get("connectMethod");
        TelnetComponent telnetComponent = (TelnetComponent) user_Object.get("telnetComponent");
        TelnetSwitchMethod telnetSwitchMethod = (TelnetSwitchMethod) user_Object.get("telnetSwitchMethod");
        LoginUser loginUser = (LoginUser)user_Object.get("loginUser");
        String userName = loginUser.getUsername();

        //具体命令
        command = command.trim();
        System.err.println("OSPF : "+command);
        //执行命令
        //命令返回信息
        String command_string = null;
        //交换机返回信息 插入 数据库
        ReturnRecord returnRecord = new ReturnRecord();

        int insert_id = 0;
        returnRecord.setUserName(userName);
        returnRecord.setSwitchIp(user_String.get("ip"));
        returnRecord.setBrand(user_String.get("deviceBrand"));
        returnRecord.setType(user_String.get("deviceModel"));
        returnRecord.setFirewareVersion(user_String.get("firmwareVersion"));
        returnRecord.setSubVersion(user_String.get("subversionNumber"));
        returnRecord.setCurrentCommLog(command.trim());
        boolean deviceBrand = true;

        String way = user_String.get("mode");//登录方式

        do {
            deviceBrand = true;

            if (way.equalsIgnoreCase("ssh")) {
                WebSocketService.sendMessage(userName, user_String.get("ip")+"发送:" + command+"\r\n");

                try {
                    PathHelper.writeDataToFile(user_String.get("ip")+"发送:" + command+"\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                command_string = connectMethod.sendCommand(user_String.get("ip"), sshConnect, command, null);
                //command_string = Utils.removeLoginInformation(command_string);
            } else if (way.equalsIgnoreCase("telnet")) {
                WebSocketService.sendMessage(userName, user_String.get("ip")+"发送:" + command+"\r\n");

                try {
                    PathHelper.writeDataToFile(user_String.get("ip")+"发送:" + command+"\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                command_string = telnetSwitchMethod.sendCommand(user_String.get("ip"), telnetComponent, command, null);
                //command_string = Utils.removeLoginInformation(command_string);
            }

            returnRecord.setCurrentReturnLog(command_string);

            //粗略查看是否存在 故障 存在故障返回 false 不存在故障返回 true
            boolean switchfailure = MyUtils.switchfailure(user_String, command_string);

            // 存在故障返回 false
            if (!switchfailure) {
                String[] commandStringSplit = command_string.split("\r\n");
                for (String returnString : commandStringSplit) {
                    deviceBrand = MyUtils.switchfailure(user_String, returnString);
                    if (!deviceBrand) {
                        System.err.println("\r\n"+user_String.get("ip") + "故障:"+returnString+"\r\n");
                        WebSocketService.sendMessage(userName,"故障:"+user_String.get("ip")+":"+returnString+"\r\n");

                        try {
                            PathHelper.writeDataToFile("故障:"+user_String.get("ip")+":"+returnString+"\r\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        returnRecord.setCurrentIdentifier(user_String.get("ip") + "出现故障:"+returnString+"\r\n");
                        if (way.equalsIgnoreCase("ssh")){
                            connectMethod.sendCommand(user_String.get("ip"),sshConnect,"\r ",user_String.get("notFinished"));
                        }else if (way.equalsIgnoreCase("telnet")){
                            telnetSwitchMethod.sendCommand(user_String.get("ip"),telnetComponent,"\n ",user_String.get("notFinished"));
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

        WebSocketService.sendMessage(userName,user_String.get("ip")+"接收:"+current_return_log+"\r\n");

        try {
            PathHelper.writeDataToFile(user_String.get("ip")+"接收:"+current_return_log+"\r\n");
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

        WebSocketService.sendMessage(userName,user_String.get("ip")+"接收:"+current_identifier+"\r\n");
        try {
            PathHelper.writeDataToFile(user_String.get("ip")+"接收:"+current_identifier+"\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }


        //返回信息表，返回插入条数
        returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);
        int update = returnRecordService.updateReturnRecord(returnRecord);

        //判断命令是否错误 错误为false 正确为true
        if (!(MyUtils.judgmentError( user_String,command_string))){
            //  简单检验，命令正确，新命令  commandLogic.getEndIndex()

            String[] returnString_split = command_string.split("\r\n");

            for (String string_split:returnString_split){
                if (!MyUtils.judgmentError( user_String,string_split)){
                    System.err.println("\r\n"+user_String.get("ip") +":" +command+ "错误:"+command_string+"\r\n");
                    WebSocketService.sendMessage(userName,"风险:"+user_String.get("ip")  +"命令:" +command +":"+command_string+"\r\n");

                    try {
                        PathHelper.writeDataToFile("风险:"+user_String.get("ip") + ":" +command +":"+command_string+"\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return command_string;
                }
            }

        }

        return command_string;
    }
}
