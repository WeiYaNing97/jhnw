package com.sgcc.advanced.controller;

import com.sgcc.advanced.domain.Ospf;
import com.sgcc.advanced.domain.OspfCommand;
import com.sgcc.advanced.domain.OspfEnum;
import com.sgcc.advanced.service.IOspfCommandService;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.controller.SwitchScanResultController;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.switchboard.SwitchIssueEcho;
import com.sgcc.share.util.FunctionalMethods;
import com.sgcc.share.util.MyUtils;
import com.sgcc.share.util.PathHelper;
import com.sgcc.share.webSocket.WebSocketService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * OSPF 功能
 */
@Api("OSPF功能相关")
@RestController
@RequestMapping("/advanced/OSPFFeatures")
@Transactional(rollbackFor = Exception.class)
public class OSPFFeatures {

    @Autowired
    private IOspfCommandService ospfCommandService;

    /**
     * ospf 功能接口
     * @param switchParameters
     */
    public void getOSPFValues(SwitchParameters switchParameters) {
        /*查询OSPF 命令集合*/
        OspfCommand ospfCommand = new OspfCommand();
        ospfCommand.setBrand(switchParameters.getDeviceBrand());
        ospfCommand.setSwitchType(switchParameters.getDeviceModel());
        ospfCommand.setFirewareVersion(switchParameters.getFirmwareVersion());
        ospfCommand.setSubVersion(switchParameters.getSubversionNumber());
        /*查询 符合交换机基本信息的 OSPF命令集合*/
        ospfCommandService = SpringBeanUtil.getBean(IOspfCommandService.class);
        List<OspfCommand> ospfCommandList = ospfCommandService.selectOspfCommandList(ospfCommand);
        /*OSPF命令集合为空  则中止OSPF高级共功能*/
        if (MyUtils.isCollectionEmpty(ospfCommandList)){
            // todo 中止OSPF高级共功能  显示前端写入日志
            return;
        }

        /*通过四项基本欸的精确度 筛选最精确的OSPF命令*/
        ospfCommand = getpojo(ospfCommandList);
        String command = ospfCommand.getGetParameterCommand();

        /**
         * 根据交换机信息类  执行交换命令
         */
        String commandReturn = FunctionalMethods.executeScanCommandByCommand(switchParameters,command);
        /*commandReturn = "OSPF process 100, 3 Neighbors, 3 is Full:\n" +
                "Neighbor ID     Pri   State                BFD State  Dead Time   Address         Interface\n" +
                "10.122.114.89     1   Full/BDR             -          00:00:39    10.122.114.89   GigabitEthernet 9/2\n" +
                "10.122.119.18     1   Full/DR              -          00:00:34    10.122.114.94   GigabitEthernet 9/1\n" +
                "10.122.114.226    1   Full/DR              -          00:00:34    10.122.114.134  GigabitEthernet 9/14";
        commandReturn = MyUtils.trimString(commandReturn);*/




        /*执行命令返回结果为null 则是命令执行错误*/
        if (commandReturn == null){
            // todo ospf:命令错误,请重新定义  错误代码
            WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"系统信息:"+switchParameters.getIp()+":"+"ospf:命令错误,请重新定义" +"\r\n");
            try {
                PathHelper.writeDataToFileByName(switchParameters.getIp()+":ospf:命令错误,请重新定义\r\n","ospf");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        /*根据交换机返回信息提取OSPF数据*/
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
                        /*continue;*/
                    }else {
                        hashMap.put("IfQuestion","有问题");
                    }
                    // =:= 是自定义分割符
                    hashMap.put("parameterString","功能=:=是=:=OSPF=:=参数=:=是=:=地址:"+ospf.getNeighborID()+"状态:"+ospf.getState()+"端口号:"+ospf.getPortNumber());
                    Long insertId = switchScanResultController.insertSwitchScanResult(switchParameters, hashMap);

                    SwitchIssueEcho switchIssueEcho = new SwitchIssueEcho();
                    switchIssueEcho.getSwitchScanResultListByData(switchParameters.getLoginUser().getUsername(),insertId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据交换机返回信息 提取 OSPF数据
     * @param returnString
     * @return
     */
    public static AjaxResult getOspfListByString(String returnString) {
        /*行数据*/
        String[] returnStringSplit = returnString.split("\r\n");
        /*标题行信息*/
        String NameLine = null;
        /*OSPF数组*/
        List<String> valueList = new ArrayList<>();
        for (int number = 0 ;number < returnStringSplit.length; number++){
            if (NameLine != null){
                /*将标题行下方数据 存入 OSPF数组*/
                valueList.add(returnStringSplit[number].trim());
            }
            if (NameLine == null){
                /*判断一个字符串是否包含另一个字符串(忽略大小写)*/
                if (MyUtils.containIgnoreCase(returnStringSplit[number],"State")){
                    /*标题行信息 赋值*/
                    NameLine = returnStringSplit[number];
                }
            }
        }

        Ospf propertyValueSubscripts = null;
        /*标题的 标题名数*/
        int number = 0 ;
        List<Ospf> ospfList = null;
        if (NameLine == null){
            // todo 获取标题行失败
            return AjaxResult.error("获取标题行失败");
        }else {
            /*获取属性值下标*/
            List<Object> property = getPropertyValueSubscripts(NameLine);
            if (property == null){
                // todo 获取标题行下标失败
                return AjaxResult.error("获取标题行下标失败");
            }
            propertyValueSubscripts = (Ospf) property.get(0);
            number = (Integer) property.get(1);
        }

        /*标题下第一行 为参数行*/
        String value = valueList.get(0);
        /*获取参数行的列*/
        String[] value_split = value.split(" ");
        /*如果参数行的列数量 与 标题行的标题名数一致则 可以直接根据 下标取值*/
        int len = value_split.length;
        if (len != number){
            /*如果不一致 则需要去去除空格*/
            valueList = removOspfSpaceCharacter(number,valueList);
        }

        if (MyUtils.isCollectionEmpty(valueList)){
            return AjaxResult.error("获取参数内容失败,未获取参数行");
        }else {
            /*根据参数下表提取对应参数*/
            ospfList= getPojoList(propertyValueSubscripts,valueList,number);
        }
        return AjaxResult.success(ospfList);
    }

    /**
     * 根据参数下表提取对应参数
     * @param pojo
     * @param stringList
     * @param number
     * @return
     */
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


    /**
     * 去除属性值中间空格  通过代码逻辑
     * @param strings
     * @return
     */
    public static List<String> removOspfSpaceCharacter(int number,List<String> strings) {
        /*创建返回对象*/
        List<String> returnStringList = new ArrayList<>();
        /*遍历行信息*/
        for (String lineInformation:strings){
            /*针对以太网的处理*/
            String[] line_split = lineInformation.split(" ");
            for (int num = line_split.length-1 ;num >= 0; num--){
                /* 查看是否存在包含/的且只有数字的项
                * 有则比较是否和标题行数量一致
                * 一致则放入返回对象
                * 否则跳出循环 返回结果*/
                if (line_split[num].indexOf("/")!=-1 && !(MyUtils.judgeContainsStr(line_split[num].replaceAll("/","")))){
                    lineInformation = lineInformation.replaceAll(" "+line_split[num],line_split[num]);
                    /*一致则放入返回对象*/
                    if (lineInformation.split(" ").length == number){
                        returnStringList.add(lineInformation);
                        break;
                    }else {
                        /*否则跳出循环 返回结果*/
                        return returnStringList;
                    }
                }
            }
        }
        return returnStringList;
    }


    /**
     * 获取属性值下标
     * @param information
     * @return
     */
    public static List<Object> getPropertyValueSubscripts(String information) {
        /*单词数组*/
        String[] string_split = information.trim().split(" ");
        String word = "";
        Ospf ospf = new Ospf();
        int number = 0;
        /*遍历单词数组 从前往后拼接匹配配置文件中配置的 标题名称*/
        for (int num = 0 ; num < string_split.length ; num++ ){
            word = word + " "+string_split[num];
            /*拼接匹配配置文件中配置的 标题名称
            * 返回配置文件中的key
            * 得到 标题名称 在数组中的下标*/
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
            /* ospf 中存储的不是属性值 是下标值*/
            objects.add(ospf);
            objects.add(number);
            return objects;
        }
        return null;
    }


    /**
     * 获取 四项基本信息更加匹配的命令实体类
     * @param pojoList
     * @return
     */
    public static OspfCommand getpojo(List<OspfCommand> pojoList) {
        OspfCommand ospfCommand = new OspfCommand();
        int sum = 0;
        for (OspfCommand pojo:pojoList){
            int num = 0 ;
            if (!(pojo.getBrand().equals("*"))){
                ++num;
            }
            if (!(pojo.getSwitchType().equals("*"))){
                ++num;
            }
            if (!(pojo.getFirewareVersion().equals("*"))){
                ++num;
            }
            if (!(pojo.getSubVersion().equals("*"))){
                ++num;
            }
            if (sum<num){
                sum = num;
                ospfCommand = pojo;
            }else if (sum == num && (pojo.getSwitchType().equals("*")) && (pojo.getSubVersion().equals("*"))){
                ospfCommand = pojo;
            }
        }
        return ospfCommand;
    }

}
