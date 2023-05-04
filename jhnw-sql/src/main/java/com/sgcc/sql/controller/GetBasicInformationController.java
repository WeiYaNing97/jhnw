package com.sgcc.sql.controller;

import cn.hutool.core.util.StrUtil;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.connect.util.SpringBeanUtil;
import com.sgcc.sql.domain.Constant;
import com.sgcc.sql.domain.ReturnRecord;
import com.sgcc.sql.parametric.SwitchParameters;
import com.sgcc.sql.service.*;
import com.sgcc.sql.util.CustomConfigurationUtil;
import com.sgcc.sql.util.FunctionalMethods;
import com.sgcc.sql.util.MyUtils;
import com.sgcc.sql.util.PathHelper;
import com.sgcc.sql.webSocket.WebSocketService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetBasicInformationController {

    @Autowired
    private static IInformationService informationService;

    /**
     * @method: 通用获取交换机基本信息  多个命令依次执行 按，分割
     * @Param: [user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法,
     * user_Object ：
     * SshConnect ssh连接工具，connectMethod ssh连接,
     * TelnetComponent Telnet连接工具，telnetSwitchMethod telnet连接]
     * @E-mail: WeiYaNing97@163.com
     * 通过null 查询初所有会的交换机基本信息的命令字符串集合
     * 遍历交换机基本信息的命令字符串集合 通过 扫描分析方法 获得所有提取信息
     * 通过返回提取的信息，给基本属性赋值
     *
     * 成功则返回基本信息 否则 遍历下一条 交换机基本信息的命令字符串集合信息
     *
     */
    @ApiOperation("通用获取交换机基本信息")
    public AjaxResult getBasicInformationCurrency(SwitchParameters switchParameters) {

        //目前获取基本信息命令是多个命令是由,号分割的，
        // 所以需要根据, 来分割。例如：display device manuinfo,display ver
        /*H3C*/
        String[] commandsplit = ( (String) CustomConfigurationUtil.getValue("BasicInformation.getBrandCommand", Constant.getProfileInformation())).split(";");
        String commandString =""; //预设交换机返回结果
        //遍历数据表命令 分割得到的 命令数组
        for (String command:commandsplit){
            /*根据交换机信息类 与 具体命令，执行并返回交换机返回信息
            * 返回结果*/
            commandString = FunctionalMethods.executeScanCommandByCommand(switchParameters, command);
            if (commandString == null){
                continue;
            }
            /*根据交换机返回结果 获取 交换机基本信息*/
            HashMap<String, String> hashMap = analyzeStringToGetBasicInformation(commandString);

            if (hashMap.get("pinpai")!=null
                    && hashMap.get("xinghao")!=null
                    && hashMap.get("banben")!=null
                    && hashMap.get("routerFlag")!=null){

                hashMap.put("pinpai",hashMap.get("pinpai"));
                hashMap.put("xinghao",hashMap.get("xinghao"));
                hashMap.put("banben",removeSpecialSymbols(hashMap.get("banben")));
                hashMap.put("zibanben",removeSpecialSymbols(hashMap.get("zibanben")));
                hashMap.put("routerFlag",hashMap.get("routerFlag"));
                System.err.println("品牌:"+hashMap.get("pinpai")+"型号:"+hashMap.get("xinghao")+
                        "版本:"+hashMap.get("banben")+"子版本:"+hashMap.get("zibanben")+
                        "设备:"+hashMap.get("routerFlag"));

                try {
                    WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"系统信息:"+switchParameters.getIp() +"基本信息："+
                            "设备品牌："+hashMap.get("pinpai")+
                            "设备型号："+hashMap.get("xinghao")+
                            "内部固件版本："+hashMap.get("banben")+
                            "子版本号："+hashMap.get("zibanben")+
                            "设备："+hashMap.get("routerFlag")+"\r\n");
                    PathHelper.writeDataToFileByName("系统信息:"+switchParameters.getIp()+"成功基本信息："+
                            "设备品牌："+hashMap.get("pinpai")+
                            "设备型号："+hashMap.get("xinghao")+
                            "内部固件版本："+hashMap.get("banben")+
                            "子版本号："+hashMap.get("zibanben")+
                            "设备："+hashMap.get("routerFlag")+"\r\n","基本信息");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                switchParameters.setDeviceBrand(hashMap.get("pinpai"));
                switchParameters.setDeviceModel(hashMap.get("xinghao"));
                switchParameters.setFirmwareVersion(hashMap.get("banben"));
                switchParameters.setSubversionNumber(hashMap.get("zibanben"));
                switchParameters.setRouterFlag(hashMap.get("routerFlag"));
                return AjaxResult.success(switchParameters);
            }else {
                try {
                    PathHelper.writeDataToFileByName("系统信息:"+ switchParameters.getIp() +"失败基本信息："+
                            "设备品牌："+hashMap.get("pinpai")+
                            "设备型号："+hashMap.get("xinghao")+
                            "内部固件版本："+hashMap.get("banben")+
                            "子版本号："+hashMap.get("zibanben")+
                            "设备："+hashMap.get("routerFlag")+"\r\n","基本信息");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return AjaxResult.error("未定义该交换机获取基本信息命令及分析");
    }


    /**
     * 是否是以 固定字符 开头和结尾 是 则去除  （获取交换机基本信息用到）
     * @param args
     * @return
     */
    public static String removeSpecialSymbols(String args) {
        String[] specialSymbols = {".",","};
        for (String symbol:specialSymbols){
            boolean startWith = StrUtil.startWith(args, symbol);
            boolean endWith = StrUtil.endWith(args, symbol);
            if (startWith){
                args = args.substring(1,args.length());
            }
            if (endWith){
                args = args.substring(0,args.length()-1);
            }
        }
        return args;
    }


    /**
     * 通过 配置文件 获取取交换机基本信息规则
     * 根据交换机返回结果 获取 交换机基本信息
     * @return  返回  交换机基本信息
     */
    public HashMap<String,String> analyzeStringToGetBasicInformation(String returns_String) {
        returns_String = returns_String.replaceAll("\r\n"," ");
        informationService = SpringBeanUtil.getBean(IInformationService.class);
        List<String> brandList = informationService.selectDeviceBrandList();
        String brand = null;
        String model = null;
        String firmwareVersion = null;
        String subversionNo = null;
        String router = "交换机";
        /* 创建返回对象 */
        HashMap<String,String> map = new HashMap<>();
        String[] return_word = returns_String.split(" ");
        /*遍历匹配 品牌  H3C*/
        for (String brandString:brandList){
            /*for (int number = 0 ; number < return_word.length; number++){
                if (brandString.equalsIgnoreCase(return_word[number])){
                    brand = brandString;
                    break;
                }
            }*/
            /*判断是否包含品牌*/
            if (MyUtils.containIgnoreCase(returns_String," "+brandString+" ")){
                brand = brandString;
                break;
            }
        }
        if (brand == null){
            return map;
        }
        /*匹配 型号 */
        List<String> modelList = informationService.selectDeviceModelList(brand);
        for (String modelString:modelList){
            /*原方法*/
            /*for (int number = 0 ; number < return_word.length; number++){
                if (modelString.equalsIgnoreCase(return_word[number])){
                    model = modelString;
                    break;
                }
            }*/
            /*判断是否包含型号*/
            if (MyUtils.containIgnoreCase(returns_String," "+modelString+" ")){
                model = modelString;
                break;
            }
        }
        /* 获取 设备是 路由器的标志*/
        String routerFlag = (String) CustomConfigurationUtil.getValue("BasicInformation.routerFlag",Constant.getProfileInformation());
        String[] flagSplit = routerFlag.toUpperCase().split(";");
        for (int number = 0 ; number < return_word.length; number++){
            for (String flag:flagSplit){
                /* 以 配置文件 属性值并且包含数字*/
                if (return_word[number].startsWith(flag)){
                    if (MyUtils.isNumeric(return_word[number])){
                        router = return_word[number];
                        break;
                    }
                }
            }
        }

        /** 设备版本 */
        /*yml 配置文件中 多个值之间用;隔开*/
        String deviceVersion = (String) CustomConfigurationUtil.getValue("BasicInformation.deviceVersion",Constant.getProfileInformation());
        String[] deviceVersionSplit =deviceVersion.split(";");
        /*遍历配置文件中的 属性值*/
        for (String version:deviceVersionSplit){
            /*判断是否包含配置文件中的 属性值
            * 如果不包含 则 下一循环*/
            if (!MyUtils.containIgnoreCase(returns_String," "+version+" ")){
                continue;
            }
            /*属性值可能是多个单词*/
            String[] versionSplit = version.split(" ");
            int versionNumber = versionSplit.length;
            for (int number = 0 ; number < return_word.length; number++){
                /*遍历 交换机返回信息的 单词数组
                * 如果匹配到 则判断配置文件中 信息的单词数
                * 如果是一个单词 则直接取下一个
                * 如果是多个单词 则比较多个单词 是否匹配  匹配 则直接取下一个*/
                if (return_word[number].equalsIgnoreCase(versionSplit[0])){
                    if (versionSplit.length == 1){
                        if (MyUtils.containDigit(return_word[number+1])){
                            firmwareVersion = return_word[number+1];
                            break;
                        }
                    }else {
                        String device = "";
                        for (int num = 0 ; num < versionNumber ; num++){
                            device = device + return_word[number + num] +" ";
                        }
                        device = device.trim();
                        if (version.equalsIgnoreCase(device)){
                            if (MyUtils.containDigit(return_word[number + (versionNumber-1) + 1])){
                                firmwareVersion =  return_word[number + (versionNumber-1) + 1];
                                break;
                            }
                        }
                    }
                }
            }

        }


        /** 设备子版本 */
        String deviceSubversion = (String) CustomConfigurationUtil.getValue("BasicInformation.deviceSubversion",Constant.getProfileInformation());
        String[] deviceSubversionSplit =deviceSubversion.split(";");
        for (String version:deviceSubversionSplit){
            /*判断是否包含配置文件中的 属性值
             * 如果不包含 则 下一循环*/
            if (!MyUtils.containIgnoreCase(returns_String," "+version+" ")){
                continue;
            }
            String[] versionSplit = version.split(" ");
            int versionNumber = versionSplit.length;
            for (int number = 0 ; number < return_word.length; number++){
                if (return_word[number].equalsIgnoreCase(versionSplit[0])){
                    if (versionSplit.length == 1){
                        if (MyUtils.containDigit(return_word[number+1])){
                            subversionNo = return_word[number+1];
                            break;
                        }
                    }else {
                        String device = "";
                        for (int num = 0 ; num < versionNumber ; num++){
                            device = device + return_word[number + num] +" ";
                        }
                        device = device.trim();
                        if (version.equalsIgnoreCase(device)){
                            if (MyUtils.containDigit(return_word[number + (versionNumber-1) + 1])){
                                subversionNo =  return_word[number + (versionNumber-1) + 1];
                                break;
                            }
                        }
                    }
                }
            }
        }
        map.put("pinpai",brand);
        map.put("xinghao",model);
        map.put("banben",firmwareVersion);
        map.put("zibanben",subversionNo);
        map.put("routerFlag",router);
        if (model == null){

        }
        return map;
    }


}
