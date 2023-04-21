package com.sgcc.sql.senior;

import com.sgcc.common.core.domain.AjaxResult;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 光衰功能
 */
@Api("光衰功能")
@RestController
@RequestMapping("/sql/LuminousAttenuation")
@Transactional(rollbackFor = Exception.class)
public class LuminousAttenuation {

    @Autowired
    private static IReturnRecordService returnRecordService;


    public static AjaxResult obtainLightDecay(SwitchParameters switchParameters) {
        CustomConfigurationUtil customConfigurationUtil = new CustomConfigurationUtil();

        String command = customConfigurationUtil.obtainConfigurationFileParameterValues("光衰." + switchParameters.getDeviceBrand()+".获取端口号命令");

        if (command == null){
            try {
                PathHelper.writeDataToFileByName("IP地址:"+switchParameters.getIp()+"未定义"+switchParameters.getDeviceBrand()+"交换机获取端口号命令","光衰");
                return AjaxResult.error("未定义"+switchParameters.getDeviceBrand()+"交换机获取端口号命令");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String returnString = OSPFFeatures.executeScanCommandByCommand(switchParameters, command);

        if (returnString == null){
            WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"系统信息:"+switchParameters.getIp()+":获取端口号命令错误,请重新定义\r\n");
            try {
                PathHelper.writeDataToFileByName(switchParameters.getIp()+":获取端口号命令错误,请重新定义\r\n","ospf");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<String> port = luminousAttenuationgetPort(returnString);

        Object escape = customConfigurationUtil.obtainConfigurationFileParameter("光衰." + switchParameters.getDeviceBrand() + ".转译");
        if (escape != null){
            Map<String,String> escapeMap = (Map<String,String>) escape;
            Set<String> mapKey = escapeMap.keySet();
            for (String key:mapKey){
                port = port.stream().map(m -> m.replace(key , escapeMap.get(key))).collect(Collectors.toList());
            }
        }

        if (port.size() == 0){
            /*没有端口号为开启状态*/
            try {
                PathHelper.writeDataToFileByName("IP地址:"+switchParameters.getIp()+"无UP状态端口号","光衰");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("IP地址:"+switchParameters.getIp()+"未获取到UP状态端口号");
        }

        HashMap<String, String> getparameter = getparameter(port, switchParameters);
        if (getparameter == null){
            /*未获取到光衰参数*/
            try {
                PathHelper.writeDataToFileByName("IP地址:"+switchParameters.getIp()+"未获取到光衰参数","光衰");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("IP地址:"+switchParameters.getIp()+"未获取到光衰参数");
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (String str:port){
            stringBuffer.append("IP地址:"+switchParameters.getIp()+"端口号:"+str+"TX:"+getparameter.get(str+"TX")+"RX:"+getparameter.get(str+"RX")+"\r\n");
        }

        try {
            WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"系统信息:"+switchParameters.getIp()+":"+"光衰:\r\n"+ stringBuffer.toString()+"\r\n");
            PathHelper.writeDataToFileByName(stringBuffer.toString(),"光衰");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return AjaxResult.success();
    }

    /*获取端口号*/
    public static List<String> luminousAttenuationgetPort(String returnString) {
        returnString = MyUtils.trimString(returnString);
        String[] returnStringSplit = returnString.split("\r\n");
        List<String> strings = new ArrayList<>();
        for (String string:returnStringSplit){
            if ((string.toUpperCase().indexOf(" UP ")!=-1) && (string.toUpperCase().indexOf("COPPER") == -1) && string.indexOf("/")!=-1){
                strings.add(string.trim());
            }
        }
        List<String> port = new ArrayList<>();
        for (String string:strings){
            String terminalSlogan = getTerminalSlogan(string);
            if (terminalSlogan != null){
                port.add(terminalSlogan);
            }
        }
        return port;
    }

    /* 根据 UP 截取端口号   */
    public static String getTerminalSlogan(String information){
        String[] informationSplit = information.toUpperCase().split(" UP ");
        information = null;
        for (String string:informationSplit){
            if (string.indexOf("/")!=-1){
                String[] string_split = string.split(" ");
                for (int num = 0;num < string_split.length;num++){
                    if (string_split[num].indexOf("/")!=-1){
                        if (MyUtils.judgeContainsStr(string_split[num])){
                            return string_split[num];
                        }else {
                            /*例如：  GigabitEthernet 2/1 */
                            information =string_split[num];
                            return string_split[--num] +" "+ information ;
                        }
                    }
                }
            }
        }
        return information.replaceAll("GE","GigabitEthernet");
    }

    /* 根据端口号 和 数据库中部定义的 获取光衰信息命令
    * */
    public static HashMap<String,String> getparameter(List<String> portNumber,SwitchParameters switchParameters) {
        CustomConfigurationUtil customConfigurationUtil = new CustomConfigurationUtil();
        String command = customConfigurationUtil.obtainConfigurationFileParameterValues("光衰." + switchParameters.getDeviceBrand()+".获取光衰参数命令");

        HashMap<String,String> hashMap = new HashMap<>();
        for (String port:portNumber){
            String com = command.replaceAll("端口号",port);
            System.err.println("获取光衰参数命令:"+com);
            String returnResults = OSPFFeatures.executeScanCommandByCommand(switchParameters, com);

            if (returnResults == null){
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"系统信息:"+switchParameters.getIp()+":获取光衰参数命令错误,请重新定义\r\n");
                try {
                    PathHelper.writeDataToFileByName(switchParameters.getIp()+":获取光衰参数命令错误,请重新定义\r\n","ospf");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            HashMap<String, String> values = getDecayValues(returnResults);
            hashMap.put(port+"TX",values.get("TX"));
            hashMap.put(port+"RX",values.get("RX"));
        }
        return hashMap;
    }


    /*get 光衰 参数*/
    public static HashMap<String,String> getDecayValues(String string) {

        string = MyUtils.trimString(string);
        String[] Line_split = string.split("\r\n");

        String txpower = null;
        String rxpower = null;

        List<String> keyValueList = new ArrayList<>();
        for (int number = 0 ;number<Line_split.length;number++) {
            int tx = Line_split[number].toUpperCase().indexOf("TX POWER");
            int rx = Line_split[number].toUpperCase().indexOf("RX POWER");

            int num = 0 ;
            if (tx!=-1 && rx!=-1){
                /*都包含*/
                if (tx > rx){
                    num = 1;
                }else if (tx < rx){
                    num = -1;
                }
                keyValueList.add(Line_split[number]);
            }else if (tx ==-1 && rx ==-1){
                /* 都不包含*/
                continue;
            }

            if (num == 0 && (tx != -1 || rx != -1)){
                /* 包含 TX 或者 RX */
                /*key : value*/
                keyValueList.add(Line_split[number]);

            }else {
                /*两个都包含 则 两个参数值在一行*/
                String nextrow = Line_split[number+1];
                String[] values = nextrow.split(" ");
                int j = 1;
                for (int i = 0 ;i < values.length;i++){
                    if (num == 1){
                        /*RX   TX*/
                        if (values[i].indexOf("-")!=-1 && j==1){
                            rxpower = values[i];
                            j=2;
                        }else if (values[i].indexOf("-")!=-1 && j==2){
                            txpower = values[i];
                        }
                    }else if (num == -1){
                        /*TX  RX*/
                        if (values[i].indexOf("-")!=-1 && j==1){
                            txpower = values[i];
                            j=2;
                        }else if (values[i].indexOf("-")!=-1 && j==2){
                            rxpower = values[i];
                        }
                    }
                    if (txpower != null && rxpower != null){
                        break;
                    }
                }
                break;
            }
        }

        /*key ： value 格式*/
        if (keyValueList.size()!=0){

            if (keyValueList.size() > 2){
                List<String> keylist = new ArrayList<>();
                for (String key:keyValueList){//Current
                    if (key.toUpperCase().indexOf("CURRENT")!=-1){
                        keylist.add(key);
                    }
                }

                if (keylist.size() > 1){
                    keyValueList = keylist;
                }
            }


            /*只有两行 则 直接取值*/
            if (keyValueList.size() >1){
                for (String keyvalue:keyValueList){
                    if (keyvalue.toUpperCase().indexOf("RX POWER") != -1){
                        keyvalue = keyvalue.replaceAll(":"," : ");
                        keyvalue = MyUtils.repaceWhiteSapce(keyvalue);
                        String[] split = keyvalue.split(":");
                        if (split.length >1){
                            rxpower = split[1].indexOf("-") !=-1 ?split[1]:null;
                        }
                    }
                    if (keyvalue.toUpperCase().indexOf("TX POWER") != -1){
                        keyvalue = keyvalue.replaceAll(":"," : ");
                        keyvalue = MyUtils.repaceWhiteSapce(keyvalue);
                        String[] split = keyvalue.split(":");
                        if (split.length >1){
                            txpower = split[1].indexOf("-") !=-1 ?split[1]:null;
                        }
                    }
                    if (txpower != null && rxpower != null){
                        String[] txpowersplit = txpower.split(", Warning");
                        String[] rxpowersplit = rxpower.split(", Warning");
                        txpower = txpowersplit[0];
                        rxpower = rxpowersplit[0];
                        break;
                    }
                }
            }
        }
        HashMap<String,String> hashMap = new HashMap<>();
        if (txpower != null && rxpower != null){
            hashMap.put("TX",txpower);
            hashMap.put("RX",rxpower);
        }
        return hashMap;
    }
}
