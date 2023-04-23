package com.sgcc.sql.senior;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.connect.util.SpringBeanUtil;
import com.sgcc.sql.controller.SwitchScanResultController;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.parametric.SwitchParameters;
import com.sgcc.sql.service.IReturnRecordService;
import com.sgcc.sql.util.CustomConfigurationUtil;
import com.sgcc.sql.util.FunctionalMethods;
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
import java.util.HashMap;
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

    /**
     * ospf 功能接口
     * @param switchParameters
     */
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

        String commandReturn = FunctionalMethods.executeScanCommandByCommand(switchParameters,command);


        commandReturn = "<AnPingJu_H3C_7503E>display ospf peer \n" +
                "\n" +
                "                  OSPF Process 100 with Router ID 10.122.114.208\n" +
                "                        Neighbor Brief Information\n" +
                "\n" +
                " Area: 0.0.0.0\n" +
                " Router ID       Address         Pri Dead-Time Interface       State\n" +
                " 10.122.114.196  10.98.138.149   1   37        Vlan3           Full/BDR\n" +
                " 10.122.114.196  10.98.139.246   1   38        Vlan4           Full/BDR\n" +
                " 10.122.114.196  10.98.138.3     1   39        Vlan6           Full/BDR\n" +
                " 10.122.114.196  10.98.136.14    1   35        Vlan7           Full/BDR\n" +
                " 10.122.114.196  10.98.137.72    1   35        Vlan200         Full/BDR\n" +
                " 10.122.114.196  10.98.138.196   1   35        Vlan2000        Full/BDR\n" +
                " 10.122.114.220  10.122.119.166  1   35        Vlan2001        Full/BDR\n" +
                " 10.122.114.196  100.1.2.253     1   35        Vlan50          Full/BDR";

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
                    WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"系统信息:"+switchParameters.getIp()+":"+"ospf:地址:"+ospf.getNeighborID()+"状态:"+ospf.getState()+"端口号:"+ospf.getPortNumber()+"\r\n");
                    PathHelper.writeDataToFileByName(switchParameters.getIp()+":地址:"+ospf.getNeighborID()+"状态:"+ospf.getState()+"端口号:"+ospf.getPortNumber()+"\r\n","ospf");
                    SwitchScanResultController switchScanResultController = new SwitchScanResultController();
                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("ProblemName","OSPF");
                    if (ospf.toString().toUpperCase().indexOf("FULL")!=-1){
                        hashMap.put("IfQuestion","无问题");
                    }else {
                        hashMap.put("IfQuestion","有问题");
                    }
                    // =:= 是自定义分割符
                    hashMap.put("parameterString","功能=:=是=:=OSPF=:=参数=:=是=:=地址:"+ospf.getNeighborID()+"状态:"+ospf.getState()+"端口号:"+ospf.getPortNumber());
                    switchScanResultController.insertSwitchScanResult(switchParameters,hashMap);

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
}
