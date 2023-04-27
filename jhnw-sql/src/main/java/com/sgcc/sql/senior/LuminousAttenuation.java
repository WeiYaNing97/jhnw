package com.sgcc.sql.senior;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.sql.controller.SwitchInteraction;
import com.sgcc.sql.controller.SwitchScanResultController;
import com.sgcc.sql.domain.Constant;
import com.sgcc.sql.parametric.SwitchParameters;
import com.sgcc.sql.util.CustomConfigurationUtil;
import com.sgcc.sql.util.FunctionalMethods;
import com.sgcc.sql.util.MyUtils;
import com.sgcc.sql.util.PathHelper;
import com.sgcc.sql.webSocket.WebSocketService;
import io.swagger.annotations.Api;
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

    /**
     * 光衰功能接口
     * @param switchParameters
     * @return
     */
    public AjaxResult obtainLightDecay(SwitchParameters switchParameters) {
        /*1：获取配置文件关于 光衰问题的 符合交换机品牌的命令的 配置信息*/
        String command = (String) CustomConfigurationUtil.getValue("光衰." + switchParameters.getDeviceBrand()+".获取端口号命令", Constant.getProfileInformation());
        /*2：当 配置文件光衰问题的命令 为空时 进行 日志写入*/
        if (command == null){
            // todo 关于交换机获取端口号命令 的错误代码库  缺少传输给前端的信息
            try {
                PathHelper.writeDataToFileByName("IP地址:"+switchParameters.getIp()+"未定义"+switchParameters.getDeviceBrand()+"交换机获取端口号命令","光衰");
                return AjaxResult.error("未定义"+switchParameters.getDeviceBrand()+"交换机获取端口号命令");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*3：配置文件光衰问题的命令 不为空时，执行交换机命令，返回交换机返回信息*/
        String returnString = FunctionalMethods.executeScanCommandByCommand(switchParameters, command);

        /*4: 如果交换机返回信息为 null 则 命令错误，交换机返回错误信息*/
        if (returnString == null){
            // todo 关于交换机返回错误信息 的错误代码库
            WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"系统信息:"+switchParameters.getIp()+":获取端口号命令错误,请重新定义\r\n");
            try {
                PathHelper.writeDataToFileByName(switchParameters.getIp()+":获取端口号命令错误,请重新定义\r\n","ospf");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*5：如果交换机返回信息不为 null说明命令执行正常, 则继续 根据交换机返回信息获取获取光衰端口号*/
        List<String> port = luminousAttenuationgetPort(returnString);
        /*6：获取光衰端口号方法返回集合判断是否为空，说明没有端口号为开启状态 UP，是则进行*/
        if (MyUtils.isCollectionEmpty(port)){
            // todo 关于没有端口号为UP状态 的错误代码库
            try {
                PathHelper.writeDataToFileByName("IP地址:"+switchParameters.getIp()+"无UP状态端口号","光衰");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("IP地址:"+switchParameters.getIp()+"未获取到UP状态端口号");
        }
        /*7：如果交换机端口号为开启状态 UP 不为空 则需要查看是否需要转义：
        GE转译为GigabitEthernet  才能执行获取交换机端口号光衰参数命令*/
        Object escape = CustomConfigurationUtil.getValue("光衰." + switchParameters.getDeviceBrand() + ".转译",Constant.getProfileInformation());
        if (escape != null){
            Map<String,String> escapeMap = (Map<String,String>) escape;
            Set<String> mapKey = escapeMap.keySet();
            for (String key:mapKey){
                port = port.stream().map(m -> m.replace(key , escapeMap.get(key))).collect(Collectors.toList());
            }
        }

        /*8：根据 up状态端口号 及交换机信息 获取光衰参数 */
        HashMap<String, Double> getparameter = getparameter(port, switchParameters);
        /*9：获取光衰参数为空*/
        if (MyUtils.isMapEmpty(getparameter)){
            // todo 关于未获取到光衰参数 的错误代码库
            try {
                PathHelper.writeDataToFileByName("IP地址:"+switchParameters.getIp()+"未获取到光衰参数\r\n","光衰");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("IP地址:"+switchParameters.getIp()+"未获取到光衰参数");
        }

        /*10:获取光衰参数不为空*/
        try {
            for (String str:port){
                // todo  根据光衰参数阈值  的代码库 回显和日志
                String lightAttenuationInformation = "IP地址:"+switchParameters.getIp()+
                        "端口号:"+str+"TX:"+getparameter.get(str+"TX")+"阈值["+getparameter.get(str+"TXLOW")+","+getparameter.get(str+"TXHIGH")+"]"+
                                      "RX:"+getparameter.get(str+"RX")+"阈值["+getparameter.get(str+"RXLOW")+","+getparameter.get(str+"RXHIGH")+"]";
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),
                        "系统信息:"+switchParameters.getIp()+":"+"光衰:"+ lightAttenuationInformation+"\r\n");
                PathHelper.writeDataToFileByName(lightAttenuationInformation+"\r\n","光衰");
                SwitchScanResultController switchScanResultController = new SwitchScanResultController();
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("ProblemName","光衰");
                if (MyUtils.isInRange(getparameter.get(str+"RX"),getparameter.get(str+"RXLOW"),getparameter.get(str+"RXHIGH"))){
                    hashMap.put("IfQuestion","无问题");
                }else {
                    hashMap.put("IfQuestion","有问题");
                }

                hashMap.put("parameterString","端口号=:=是=:="+str+"=:=光衰参数=:=是=:=" +
                        "TX:"+getparameter.get(str+"TX")+"阈值["+getparameter.get(str+"TXLOW")+","+getparameter.get(str+"TXHIGH")+"]"+
                        "RX:"+getparameter.get(str+"RX")+"阈值["+getparameter.get(str+"RXLOW")+","+getparameter.get(str+"RXHIGH")+"]");
                switchScanResultController.insertSwitchScanResult(switchParameters,hashMap);
                SwitchInteraction.getSwitchScanResultListByData(switchParameters);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return AjaxResult.success();
    }

    /**
     * 根据交换机返回信息获取获取光衰端口号
     * @param returnString
     * @return
     */
    public static List<String> luminousAttenuationgetPort(String returnString) {
        String[] returnStringSplit = returnString.split("\r\n");
        List<String> strings = new ArrayList<>();
        for (String string:returnStringSplit){
            /*包含 交换机返回行信息转化为大写 UP状态  不能为COPPER铜缆的  并且该行带有“/”的 存放入端口待取集合*/
            if ((string.toUpperCase().indexOf(" UP ")!=-1) && string.indexOf("/")!=-1 && (string.toUpperCase().indexOf("COPPER") == -1)){
                strings.add(string.trim());
            }
        }
        /*判断端口待取集合是否为空*/
        if (MyUtils.isCollectionEmpty(strings)){
            return null;
        }
        List<String> port = new ArrayList<>();
        /*遍历端口待取集合 执行取值方法 获取端口号*/
        for (String information:strings){
            /*根据 UP 截取端口号*/
            String terminalSlogan = getTerminalSlogan(information);
            if (terminalSlogan != null){
                port.add(terminalSlogan);
            }
        }
        return port;
    }


    /**
     * 根据 UP 截取端口号
     * @param information
     * @return
     */
    public static String getTerminalSlogan(String information){
        /*GigabitEthernet 9/1 up routed Full 1000M fiber*/
        /*根据UP分割字符串*/
        /*交换机信息 根据 up(忽略大小写) 分割*/
        String[] informationSplit = MyUtils.splitIgnoreCase(information," UP ");
        /*遍历数组包含/的为端口号 但不能确定端口号是否完全
        * 此时需要判断提取到的端口号是否包含字母
        * 包含则为完全端口号 否则为不完全端口号，需要加前面的GigabitEthernet*/
        for (String string:informationSplit){
            if (string.indexOf("/")!=-1){
                String[] string_split = string.split(" ");
                for (int num = 0;num < string_split.length;num++){
                    if (string_split[num].indexOf("/")!=-1){
                        /*判断提取到的端口号是否包含字母*/
                        if (MyUtils.judgeContainsStr(string_split[num])){
                            /*包含则为完全端口号 否则为不完全端口号*/
                            return string_split[num];
                        }else {
                            /*例如：  GigabitEthernet 2/1 */
                            /*否则为不完全端口号，需要加前面的GigabitEthernet*/
                            information =string_split[num];
                            return string_split[--num] +" "+ information ;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据 up状态端口号 及交换机信息 获取光衰参数
     * @param portNumber 端口号
     * @param switchParameters 交换机信息类
     * @return
     */
    public static HashMap<String,Double> getparameter(List<String> portNumber,SwitchParameters switchParameters) {
        /*获取配置信息中 符合品牌的 获取基本信息的 获取光衰参数的 命令*/
        String command = (String) CustomConfigurationUtil.getValue("光衰." + switchParameters.getDeviceBrand()+".获取光衰参数命令",Constant.getProfileInformation());
        /*创建 返回对象 HashMap*/
        HashMap<String,Double> hashMap = new HashMap<>();
        /*端口号集合 需要检测各端口号的光衰参数*/
        for (String port:portNumber){
            /*替换端口号 得到完整的 获取端口号光衰参数命令 */
            String FullCommand = command.replaceAll("端口号",port);
            /*交换机执行命令 并返回结果*/
            String returnResults = FunctionalMethods.executeScanCommandByCommand(switchParameters, FullCommand);

            if (returnResults == null){
                // todo 获取光衰参数命令错误代码库
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"系统信息:"+switchParameters.getIp()+":获取光衰参数命令错误,请重新定义\r\n");
                try {
                    PathHelper.writeDataToFileByName(switchParameters.getIp()+":获取光衰参数命令错误,请重新定义\r\n","ospf");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            /*提取光衰参数*/
            HashMap<String, Double> values = getDecayValues(returnResults,switchParameters);
            if (values == null)
                return null;
            hashMap.put(port+"TX",values.get("TX"));
            hashMap.put(port+"RX",values.get("RX"));
            hashMap.put(port+"TXHIGH",values.get("TXHIGH"));
            hashMap.put(port+"RXHIGH",values.get("RXHIGH"));
            hashMap.put(port+"TXLOW",values.get("TXLOW"));
            hashMap.put(port+"RXLOW",values.get("RXLOW"));
        }
        return hashMap;
    }


    /**
     * 提取光衰参数
     * @param string
     * @return
     */
    public static HashMap<String,Double> getDecayValues(String string,SwitchParameters switchParameters) {
        /*切割成行信息*/
        String[] Line_split = string.split("\r\n");
        List<Integer> threshold = (List<Integer>) CustomConfigurationUtil.getValue("光衰." + switchParameters.getDeviceBrand()+".阈值", Constant.getProfileInformation());
        /*自定义 光衰参数默认给个 100*/
        double txpower = 1;
        double rxpower = 1;
        double txpowerhigh = Double.valueOf(threshold.get(1)+"").doubleValue();
        double txpowerlow = Double.valueOf(threshold.get(0)+"").doubleValue();
        double rxpowerhigh = Double.valueOf(threshold.get(1)+"").doubleValue();
        double rxpowerlow = Double.valueOf(threshold.get(0)+"").doubleValue();

        List<String> keyValueList = new ArrayList<>();
        for (int number = 0 ;number<Line_split.length;number++) {
            /* 获取 TX POWER 和 RX POWER 的位置
            * 当其中一个值不为 -1时 则为key：value格式
            * 如果全不为 -1时 则是 两个光衰参数在同一行 的格式*/
            int tx = Line_split[number].toUpperCase().indexOf("TX POWER");
            int rx = Line_split[number].toUpperCase().indexOf("RX POWER");
            /* 设置 光衰参数的格式 预设为0key：value格式   为1是RX、TX  为-1时TX、RX*/
            int num = 0 ;
            if (tx!=-1 && rx!=-1){
                /*如果全不为 -1时 则是 两个光衰参数在同一行 的格式*/
                if (tx > rx){
                    num = 1;
                }else if (tx < rx){
                    num = -1;
                }
            }else if (tx ==-1 && rx ==-1){
                /* RX、TX 都不包含 则进入下一循环*/
                continue;
            }

            if (num == 0 && (tx != -1 || rx != -1)){
                /* 包含 TX 或者 RX */
                /*key : value*/
                keyValueList.add(Line_split[number]);
            }else {
                /*两个都包含 则 两个参数值在一行*/
                String nextrow = Line_split[number+1];
                /*字符串截取double值*/
                List<Double> values = MyUtils.StringTruncationDoubleValue(nextrow);
                if (values.size()!=2){
                    /*光衰参数行有多余2个负数 无法去除*/
                    // todo 光衰参数取值失败 光衰参数行有多余2个负数 错误代码
                    return null;
                }
                if (num == 1){
                    /*RX   TX*/
                    rxpower = values.get(0);
                    txpower = values.get(1);
                }else if (num == -1){
                    /*TX  RX*/
                    txpower = values.get(0);
                    rxpower = values.get(1);
                }
                break;
            }
        }

        /*key ： value 格式*/
        if (keyValueList.size()!=0){
            /*当包含 TX POWER 或 RX POWER 的数多余2条是 要再次筛选  光衰参数信息 或是阈值信息*/
            if (keyValueList.size() > 2){
                /*存储再次筛选后的 行信息*/
                List<String> keylist = new ArrayList<>();
                for (int num = 0 ; num<keyValueList.size();num++){

                    /*Current Rx Power(dBM)                 :-11.87
                      Default Rx Power High Threshold(dBM)  :-2.00
                      Default Rx Power Low  Threshold(dBM)  :-23.98
                      Current Tx Power(dBM)                 :-2.80
                      Default Tx Power High Threshold(dBM)  :1.00
                      Default Tx Power Low  Threshold(dBM)  :-6.00*/
                    /*遇到包含 CURRENT 是则存储行信息集合
                    * 下面两行是否包含HIGH 和 LOW */
                    if (MyUtils.containIgnoreCase(keyValueList.get(num),"CURRENT")){
                        keylist.add(keyValueList.get(num));
                        if (MyUtils.containIgnoreCase(keyValueList.get(num+1),"HIGH") || MyUtils.containIgnoreCase(keyValueList.get(num+1),"LOW")){
                            keylist.add(keyValueList.get(num+1));
                        }
                        if (MyUtils.containIgnoreCase(keyValueList.get(num+2),"HIGH") || MyUtils.containIgnoreCase(keyValueList.get(num+2),"LOW")){
                            keylist.add(keyValueList.get(num+2));
                        }
                    }
                }
                if (keylist.size() > 1){
                    keyValueList = keylist;
                }
            }


            /*遍历 行信息集合*/
            for (String keyvalue:keyValueList){
                /*当 行信息包含 RX 说明是 RX数据*/
                if (MyUtils.containIgnoreCase(keyvalue,"RX")){
                    if (MyUtils.containIgnoreCase(keyvalue,"RX POWER")){
                        /*获取负数值 如果一个则是光衰 如果三个则是包含阈值*/
                        List<Double> doubleList = MyUtils.StringTruncationDoubleValue(keyvalue);
                        if (doubleList.size()==1){
                            rxpower = doubleList.get(0);
                        }else if (doubleList.size()==3){
                            rxpower = doubleList.get(0);
                            rxpowerlow = doubleList.get(1);
                            rxpowerhigh = doubleList.get(2);
                        }else {
                            // todo 光衰参数取值失败 光衰参数行负数数量不正确 错误代码
                            return null;
                        }
                    }else if (MyUtils.containIgnoreCase(keyvalue,"HIGH")){
                        List<Double> doubleList = MyUtils.StringTruncationDoubleValue(keyvalue);
                        if (doubleList.size()==1){
                            rxpower = doubleList.get(0);
                        }
                    }else if (MyUtils.containIgnoreCase(keyvalue,"LOW")){
                        List<Double> doubleList = MyUtils.StringTruncationDoubleValue(keyvalue);
                        if (doubleList.size()==1){
                            rxpower = doubleList.get(0);
                        }
                    }
                }
                /*当 行信息包含 TX 说明是 TX数据*/
                if (MyUtils.containIgnoreCase(keyvalue,"TX")){
                    if (MyUtils.containIgnoreCase(keyvalue,"TX POWER")){
                        /*获取负数值 如果一个则是光衰 如果三个则是包含阈值*/
                        List<Double> doubleList = MyUtils.StringTruncationDoubleValue(keyvalue);
                        if (doubleList.size()==1){
                            txpower = doubleList.get(0);
                        }else if (doubleList.size()==3){
                            txpower = doubleList.get(0);
                            txpowerlow = doubleList.get(1);
                            txpowerhigh = doubleList.get(2);
                        }else {
                            // todo 光衰参数取值失败 光衰参数行负数数量不正确 错误代码
                            return null;
                        }
                    }else if (MyUtils.containIgnoreCase(keyvalue,"HIGH")){
                        List<Double> doubleList = MyUtils.StringTruncationDoubleValue(keyvalue);
                        if (doubleList.size()==1){
                            txpower = doubleList.get(0);
                        }
                    }else if (MyUtils.containIgnoreCase(keyvalue,"LOW")){
                        List<Double> doubleList = MyUtils.StringTruncationDoubleValue(keyvalue);
                        if (doubleList.size()==1){
                            txpower = doubleList.get(0);
                        }
                    }
                }
            }

        }
        if (Double.valueOf(txpower).doubleValue() == 1 || Double.valueOf(rxpower).doubleValue() == 1)
            return null;
        HashMap<String,Double> hashMap = new HashMap<>();
        hashMap.put("TX",Double.valueOf(txpower).doubleValue() == 1?null:txpower);
        hashMap.put("RX",Double.valueOf(rxpower).doubleValue() == 1?null:rxpower);
        hashMap.put("TXHIGH",txpowerhigh);
        hashMap.put("RXHIGH",rxpowerhigh);
        hashMap.put("TXLOW",txpowerlow);
        hashMap.put("RXLOW",rxpowerlow);
        return hashMap;
    }
}
