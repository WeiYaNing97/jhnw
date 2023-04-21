package com.sgcc.sql.senior;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.connect.util.SpringBeanUtil;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.parametric.SwitchParameters;
import com.sgcc.sql.service.IReturnRecordService;
import com.sgcc.sql.util.CustomConfigurationUtil;
import com.sgcc.sql.util.MyUtils;
import com.sgcc.sql.util.PathHelper;
import com.sgcc.sql.webSocket.WebSocketService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OSPF 功能
 */
@Api("OSPF功能相关")
@RestController
@RequestMapping("/sql/OSPFFeatures")
@Transactional(rollbackFor = Exception.class)
public class OSPFFeatures {

    @Autowired
    private static IReturnRecordService returnRecordService;

    public static void getOSPFValues(SwitchParameters switchParameters) {
        CustomConfigurationUtil customConfigurationUtil = new CustomConfigurationUtil();
        Object objectMap  = customConfigurationUtil.obtainConfigurationFileParameter("OSPF.command");
        if (objectMap == null){
            return;
        }
        String command = null;
        if (objectMap instanceof Map){
            Map<String,Object> commandMap = (Map<String,Object>)objectMap;
            List<String> attributeList = new ArrayList<>();
            String attribute = null;
            if (switchParameters.getSubversionNumber() != null){
                attribute = (switchParameters.getDeviceBrand()+">"+switchParameters.getDeviceModel()+">"+
                        switchParameters.getFirmwareVersion() )+ ">"+switchParameters.getSubversionNumber();
                attributeList.add(attribute);
            }
            attribute = switchParameters.getDeviceBrand()+">"+switchParameters.getDeviceModel()+">"+
                    switchParameters.getFirmwareVersion();
            attributeList.add(attribute);
            attribute = switchParameters.getDeviceBrand()+">"+switchParameters.getDeviceModel();
            attributeList.add(attribute);
            attribute = switchParameters.getDeviceBrand();
            attributeList.add(attribute);
            attribute = switchParameters.getDeviceBrand()+">*>"+switchParameters.getFirmwareVersion();
            attributeList.add(attribute);
            for (int i = 0;i < attributeList.size();i++){
                command = (String) commandMap.get(attributeList.get(i));
                if (command!=null && i == attributeList.size()-2){
                    String mohu = (String) commandMap.get(attributeList.get(++i));
                    if (mohu !=null){
                        command = mohu;
                        break;
                    }
                }
            }
        }

        String commandReturn = executeScanCommandByCommand(switchParameters,command);

        if (commandReturn == null){
            WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"系统信息:"+switchParameters.getIp()+":"+"ospf:命令错误,请重新定义" +"\r\n");
            try {
                PathHelper.writeDataToFileByName(switchParameters.getIp()+":ospf:命令错误,请重新定义\r\n","ospf");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        AjaxResult ospfListByString = getOspfListByString(commandReturn);
        if(ospfListByString.get("msg").equals("操作成功")){
            List<Ospf> ospfList = (List<Ospf>) ospfListByString.get("data");
            for (Ospf ospf:ospfList){
                try {
                    WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"系统信息:"+switchParameters.getIp()+":"+"ospf:"+ ospf.toString()+"\r\n");
                    PathHelper.writeDataToFileByName(switchParameters.getIp()+":" + ospf.toString()+"\r\n","ospf");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据交换机返回结果 获取 OSPF 参数
     * @param returnString
     * @return
     */
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
        CustomConfigurationUtil customConfigurationUtil = new CustomConfigurationUtil();
        String command = customConfigurationUtil.obtainConfigurationFileParameterValues("OSPF.ospfSpaceCharacter");
        String[] ospfSpaceCharacterSplit = command.split(";");
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
    public static String executeScanCommandByCommand(SwitchParameters switchParameters,String command) {

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
