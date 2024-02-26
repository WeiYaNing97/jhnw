package com.sgcc.advanced.test;

import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.MyUtils;
import com.sgcc.share.util.PathHelper;
import com.sgcc.share.webSocket.WebSocketService;
import java.io.IOException;
import java.util.*;

/**
 * @program: jhnw
 * @description: ProblemScanLogic 转化为 Json
 * @author:
 * @create: 2023-10-18 10:04
 **/
public class LightAttenuationTest {

    public static void main(String[] args) {

        SwitchParameters switchParameters = new SwitchParameters();

        String returnResults = "Current diagnostic parameters[AP:Average Power]:\r\n" +
                "Temp(Celsius)   Voltage(V)      Bias(mA)            RX power(dBm)       TX power(dBm)\r\n" +
                "37(OK)          3.36(OK)        15.91(OK)           -5.96(OK)[AP]       -6.04(OK)";

            /*returnResults = "Current Rx Power(dBM)                 :-11.87\r\n" +
                    "            Default Rx Power High Threshold(dBM)  :-2.00\r\n" +
                    "            Default Rx Power Low  Threshold(dBM)  :-23.98\r\n" +
                    "            Current Tx Power(dBM)                 :-2.80\r\n" +
                    "            Default Tx Power High Threshold(dBM)  :1.00\r\n" +
                    "            Default Tx Power Low  Threshold(dBM)  :-6.00";*/
        returnResults = "Port BW: 1G, Transceiver max BW: 1G, Transceiver Mode: SingleMode\r\n" +
                "WaveLength: 1310nm, Transmission Distance: 10km\r\n" +
                "Rx Power:  -6dBm, Warning range: [-16.989,  -5.999]dBm\r\n" +
                "Tx Power:  -6.20dBm, Warning range: [-9.500,  -2.999]dBm";

        HashMap<String, Double> decayValues = getDecayValues(returnResults, switchParameters);
        Set<String> keySet = decayValues.keySet();
        for (String key:keySet){
            Double aDouble = decayValues.get(key);
            System.err.println("key : "+key +"  value:"+aDouble);
        }

    }

    /**
     * 提取光衰参数
     * @param string
     * @return
     */
    public static HashMap<String,Double> getDecayValues(String string, SwitchParameters switchParameters) {
        /*根据 "\r\n" 切割为行信息*/
        String[] Line_split = string.toUpperCase().split("\r\n");

        /*
         * 自定义 光衰参数默认给个 50
         * 收发光功率可能为正，但是一般最大30左右
         * 此时默认给50 作为是否 获取到返回信息 的判断
         * */
        double txpower = 50.0;
        double rxpower = 50.0;

        /*创建字符串集合，用于存储 key：valu格式的参数
         遍历交换机返回信息，如果 tx 和 rx 不在同一行 则说明 是 key：valu格式的参数
         则 存入 集合中*/
        List<String> keyValueList = new ArrayList<>();

        /* 遍历交换机返回信息行数组 */
        for (int number = 0 ;number<Line_split.length;number++) {
            /* 字符串包含 TX POWER、RX POWER 一定是参数名
            * 但是考虑到 可能不会是 TX POWER 或者 RX POWER
            * 提供了另一种方式 当出现TX或者RX时，并且出现 dbm时，那么TX和RX的字段也会是参数名*/
            if (Line_split[number].indexOf("TX POWER") !=-1
                    || Line_split[number].indexOf("RX POWER") !=-1

                    || ((Line_split[number].indexOf("TX") !=-1 || Line_split[number].indexOf("RX") !=-1) && Line_split[number].indexOf("DBM") !=-1)){

            }else {
                continue;
            }

            /* 获取 TX POWER 和 RX POWER 的位置
             * 当其中一个值不为 -1时 则为key：value格式
             * 如果全不为 -1时 则是 两个光衰参数在同一行 的格式
             * 如果全部为 -1时，则 RX、TX 都不包含*/
            int tx = Line_split[number].indexOf("TX");
            int rx = Line_split[number].indexOf("RX");

            /* RX、TX都为-1时
             则RX、TX都不包含
             则进入下一循环*/
            if (tx ==-1 && rx ==-1){
                continue;
            }

            /*如果 RX TX 同时不为 -1
            则说明同时包含 RX TX
            则 需要判断 RX 、 TX 的先后顺序*/

            /*设 num 为 TX 与 RX的位置关系
            为1是RX、TX
            为-1时TX、RX
            为0 时 key:value */
            int num = 0 ;

            if (tx!=-1 && rx!=-1){
                /*如果全不为 -1时 则是 两个光衰参数在同一行 的格式
                   RX power(dBm)       TX power(dBm)
                   -5.96(OK)[AP]       -6.04(OK)
                *需要判断 RX和TX的先后顺序 */
                if (tx > rx){
                    /*当 tx > rx时 说明 rx在前 TX 在后
                     * 所以 num = 1时，说明 rx在前 TX 在后*/
                    num = 1;
                }else if (tx < rx){
                    /*当 tx < rx时 说明 tx在前 RX 在后
                     * 所以 num = -1时，说明 tx在前 RX 在后*/
                    num = -1;
                }

            }else {

                /*如果 TX RX 不同时为 -1 则 说明：收、发光功率不在一行
                则程序  num = 0
                为 不在一行
                为 key：value格式
                num = 0  不做修改 */

            }


            /* 因为 num = 0 所以 为 不在一行 为 key：value格式
             * 需要 存放入 集合中 */
            /* 去掉了 && (tx != -1 || rx != -1) 因为 之前 RX、TX 都不包含 已经做过判断 */
            if (num == 0){

                /* 包含 TX 或者 RX
                key:value 格式
                keyValueList集合 用于存储 key:value 格式数据*/
                keyValueList.add(Line_split[number]);

            }else {

                /*错误信息预定义 用于提取数据失败返回前端显示 */
                String nextrow = Line_split[number];
                String parameterInformation = nextrow;
                /*两个都包含 则 两个参数值在一行*/
                if (nextrow.indexOf( ":" ) == -1){
                    nextrow = Line_split[number+1];
                    parameterInformation = parameterInformation +"\r\n"+ nextrow;
                }

                String[] Line_split_split = Line_split[number].split("\\s+");

                /*字符串截取double值*/
                List<Double> values = MyUtils.StringTruncationDoubleValue(nextrow);

                HashMap<String, Integer> position = new HashMap<>();

                if (Line_split_split.length == values.size()){

                    position = getPosition(Line_split[number]);

                }else {
                    /*去除单个的空格,保留连续多个空格*/
                    String replaceAll = Line_split[number].replaceAll("(?<=\\S)\\s(?=\\S)", "");

                    Line_split_split =replaceAll.split("\\s+");
                    if (Line_split_split.length == values.size()){
                        position = getPosition(replaceAll);
                    }else {
                        String subversionNumber = switchParameters.getSubversionNumber();
                        if (subversionNumber!=null){
                            subversionNumber = "、"+subversionNumber;
                        }


                        AbnormalAlarmInformationMethod.afferent(
                                switchParameters.getIp(),
                                switchParameters.getLoginUser().getUsername(),
                                "问题日志",
                                "异常:" +
                                "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:光衰功能光衰参数取词失败," +
                                "光衰参数行信息:"+parameterInformation+"\r\n");

                    }
                }

                txpower = values.get(position.get("tx"));
                rxpower = values.get(position.get("rx"));

                break;
            }
        }


        /*key ： value 格式*/
        if (keyValueList.size()!=0){

            /*当包含 TX POWER 或 RX POWER 的数多余2条是
            要再次筛选 CURRENT 光衰参数信息 或是阈值信息*/
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
                       containIgnoreCase   判断一个字符串是否包含另一个字符串(忽略大小写)
                    * 下面两行是否包含HIGH 和 LOW 阈值区间*/

                    if (MyUtils.containIgnoreCase(keyValueList.get(num),"CURRENT")){
                        keylist.add(keyValueList.get(num));
                    }

                }
                /*筛选以后 重新赋值
                  Current Rx Power(dBM)                 :-11.87
                  Current Tx Power(dBM)                 :-2.80
                */
                if (keylist.size() > 1){
                    keyValueList = keylist;
                }

            }

            /*遍历 行信息集合*/
            for (String keyvalue:keyValueList){

                /*当 行信息包含 RX 说明是 RX数据*/
                if (MyUtils.containIgnoreCase(keyvalue,"RX")){
                    /*获取负数值 如果一个则是光衰 如果三个则是包含阈值*/
                    List<Double> doubleList = MyUtils.StringTruncationDoubleValue(keyvalue);

                    if (doubleList.size()==1){
                            /*Current Rx Power(dBM)                 :-11.87
                            Current Tx Power(dBM)                 :-2.80*/
                        rxpower = doubleList.get(0);

                    }else if (doubleList.size()==3){
                        Double tx = getParameterValueIndex("RX", doubleList, keyvalue);
                        /*  Rx Power:  -6.23dBm, Warning range: [-16.989,  -5.999]dBm
                            Tx Power:  -6.16dBm, Warning range: [-9.500,  -2.999]dBm  */
                        rxpower = doubleList.get(0);

                    }else {
                        String subversionNumber = switchParameters.getSubversionNumber();
                        if (subversionNumber!=null){
                            subversionNumber = "、"+subversionNumber;
                        }

                        AbnormalAlarmInformationMethod.afferent(
                                switchParameters.getIp(),
                                switchParameters.getLoginUser().getUsername(),
                                "问题日志",
                                "异常:" +
                                "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:光衰功能光衰参数行数值数量不正确,无法解析," +
                                "光衰参数行信息:"+keyvalue+"\r\n");
                    }
                }

                /*当 行信息包含 TX 说明是 TX数据*/
                if (MyUtils.containIgnoreCase(keyvalue,"TX")){
                    /*获取负数值 如果一个则是光衰 如果三个则是包含阈值*/
                    List<Double> doubleList = MyUtils.StringTruncationDoubleValue(keyvalue);
                    if (doubleList.size()==1){

                        txpower = doubleList.get(0);
                    }else if (doubleList.size()==3){
                        Double tx = getParameterValueIndex("TX", doubleList, keyvalue);
                        txpower = tx;
                    }else {
                        String subversionNumber = switchParameters.getSubversionNumber();
                        if (subversionNumber!=null){
                            subversionNumber = "、"+subversionNumber;
                        }


                        AbnormalAlarmInformationMethod.afferent(
                                switchParameters.getIp(),
                                switchParameters.getLoginUser().getUsername(),
                                "问题日志",
                                "异常:" +
                                "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:光衰功能光衰参数行负数数量不正确,无法解析," +
                                "光衰参数行信息:"+keyvalue+"\r\n");

                    }
                }
            }
        }

        HashMap<String,Double> hashMap = new HashMap<>();

        if (Double.valueOf(txpower).doubleValue() == 50.0 || Double.valueOf(rxpower).doubleValue() == 50.0)
            return hashMap;

        /**
         * 为什么等于1时为null？
         */
        hashMap.put("TX", txpower);/*Double.valueOf(txpower).doubleValue() == 1?null:txpower*/
        hashMap.put("RX", rxpower);/*Double.valueOf(rxpower).doubleValue() == 1?null:rxpower*/

        return hashMap;

    }

    /**
     * @Description  获取 RX TX 位置
     * @author charles
     * @createTime 2023/12/19 12:34
     * @desc
     * @param input
     * @return
     */
    public static HashMap<String,Integer> getPosition(String input) {
        String[] split = input.toLowerCase().split("\\s+");

        HashMap<String,Integer> position = new HashMap<>();

        for (int i = 0 ; i < split.length ; i++){

            if ( split[i].indexOf("rx")!=-1){/*&& split[i].indexOf("power")!=-1*/

                position.put("rx",i);

            }else if ( split[i].indexOf("tx")!=-1 ){/* &&  split[i].indexOf("power")!=-1*/

                position.put("tx",i);

            }
        }

        return position;
    }

    public static Double getParameterValueIndex(String keyword,List<Double> values,String input) {

        List<Integer> keywordPositions = MyUtils.getSubstringPositions(input, keyword);

        if (keywordPositions.size()!=1){
            return null;
        }

        Integer keywordPosition = keywordPositions.get(0);

        Set<Integer> positionList = new HashSet<>();

        for (double value:values){
            positionList.addAll(MyUtils.getSubstringPositions(input, zero_suppression(value+"") ));
        }
        int i = 0;
        for (i = keywordPosition+2 ; i < input.length()-1 ; i++){
            boolean contains = positionList.contains(i);
            if (contains){
                break;
            }
        }
        List<Double> doubleList = MyUtils.StringTruncationDoubleValue(input.substring(i, input.length()));

        if (doubleList.size() == 0){
            return null;
        }else {
            return doubleList.get(0);
        }
    }

    public static String zero_suppression(String value) {
        while ((value+"").endsWith("0") || (value+"").endsWith(".")){
            value = value.substring(0,value.length()-1);
        }
        return value;
    }
}
