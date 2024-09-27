package com.sgcc.advanced.controller;

import com.sgcc.advanced.domain.*;
import com.sgcc.advanced.service.IOspfCommandService;
import com.sgcc.advanced.utils.DataExtraction;
import com.sgcc.advanced.utils.ScreeningMethod;
import com.sgcc.advanced.utils.Utils;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.controller.SwitchScanResultController;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.switchboard.SwitchIssueEcho;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.ExecuteCommand;
import com.sgcc.share.util.MyUtils;
import com.sgcc.share.util.PathHelper;
import com.sgcc.share.webSocket.WebSocketService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * OSPF 功能
 */
@Api(tags = "OSPF功能管理")
@RestController
@RequestMapping("/advanced/OSPFFeatures")
@Transactional(rollbackFor = Exception.class)
public class OSPFFeatures {

    @Autowired
    private IOspfCommandService ospfCommandService;

    /**
     * 获取OSPF值
     *
     * @param switchParameters 交换机参数对象
     * @return AjaxResult 包含OSPF值的AjaxResult对象
     */
    public AjaxResult getOSPFValues(SwitchParameters switchParameters) {
        // 获取OSPF命令对象
        AjaxResult ospfCommandPojo = getOspfCommandPojo(switchParameters);
        if (!ospfCommandPojo.get("msg").equals("操作成功")){
            return ospfCommandPojo;
        }
        OspfCommand ospfCommand = (OspfCommand) ospfCommandPojo.get("data");

        // 获取OSPF命令执行结果
        AjaxResult ospfCommandReturnResult = getOSPFCommandReturnResult(switchParameters, ospfCommand);
        if (!ospfCommandReturnResult.get("msg").equals("操作成功")){
            return ospfCommandReturnResult;
        }
        String commandReturn = (String) ospfCommandReturnResult.get("data");

        // 获取OSPF数据列表
        AjaxResult OSPFPojoList = retrieveOSPFData(switchParameters, commandReturn);
        if (!OSPFPojoList.get("msg").equals("操作成功")){
            return OSPFPojoList;
        }
        List<OSPFPojo> pojoList = (List<OSPFPojo>) OSPFPojoList.get("data");

        // 获取OSPF异常判断结果
        AjaxResult ajaxResult = obtainOSPFExceptionJudgmentResults(switchParameters, pojoList);
        return ajaxResult;
    }


    /**
     * 获取OSPF命令对象
     *
     * @param switchParameters 交换机参数对象
     * @return AjaxResult 包含OSPF命令对象的AjaxResult对象
     */
    public AjaxResult getOspfCommandPojo(SwitchParameters switchParameters) {
        // 获取OSPF命令对象
        OspfCommand ospfCommand = getOspfCommand(switchParameters);

        // 查询OSPF 命令集合
        // 查询 符合交换机基本信息的 OSPF命令集合
        ospfCommandService = SpringBeanUtil.getBean(IOspfCommandService.class);
        List<OspfCommand> ospfCommandList = ospfCommandService.selectOspfCommandListBySQL(ospfCommand);


        /*OSPF命令集合为空  则中止OSPF高级共功能*/
        if (MyUtils.isCollectionEmpty(ospfCommandList)){
            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }
            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题为:未定义该交换机OSPF命令\r\n");
            return AjaxResult.error("IP地址为:"+switchParameters.getIp()+","+"问题为:未定义该交换机OSPF命令\r\n");
        }


        // 通过四项基本欸的精确度 筛选最精确的OSPF命令
        ospfCommand = ScreeningMethod.ObtainPreciseEntityClassesOspfCommand(ospfCommandList);
        return AjaxResult.success(ospfCommand);
    }

    /**
     * 根据交换机信息类执行OSPF命令并返回结果
     *
     * @param switchParameters 交换机参数对象
     * @return AjaxResult 包含执行结果的AjaxResult对象
     */
    public AjaxResult getOSPFCommandReturnResult(SwitchParameters switchParameters,OspfCommand ospfCommand ) {
        // 根据交换机信息类  执行交换命令
        ExecuteCommand executeCommand = new ExecuteCommand();
        String command = ospfCommand.getGetParameterCommand();
        String commandReturn = executeCommand.executeScanCommandByCommand(switchParameters,command);

        // todo OSPF虚拟数据
        commandReturn = this.commandPortReturn;
        commandReturn = MyUtils.trimString(commandReturn);

        /*执行命令返回结果为null 则是命令执行错误*/
        if (commandReturn == null){
            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }

            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题为:ospf功能命令错误,请重新定义\r\n");

            return AjaxResult.error("IP地址为:"+switchParameters.getIp()+","+"问题为:ospf功能命令错误,请重新定义\r\n");
        }

        return AjaxResult.success("操作成功",commandReturn);
    }

    /**
     * 根据交换机参数和命令返回结果获取OSPF数据
     *
     * @param switchParameters 交换机参数对象
     * @param commandReturn 命令返回结果
     * @return AjaxResult 包含OSPFPojo对象列表的AjaxResult对象
     */
    public AjaxResult retrieveOSPFData(SwitchParameters switchParameters,String commandReturn) {
        // 根据输入信息和交换机参数获取OSPFPojo对象列表
        List<OSPFPojo> pojoList =  getOSPFPojo(commandReturn,switchParameters);

        if (pojoList.size() == 0){
            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }
            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题为:ospf功能信息提取失败\r\n");
            return AjaxResult.error("IP地址为:"+switchParameters.getIp()+","+"问题为:ospf功能信息提取失败\r\n");
        }
        return AjaxResult.success(pojoList);
    }


    /**
     * 获取OSPF异常判断结果
     *
     * @param switchParameters 交换机参数对象
     * @param pojoList OSPF对象列表
     * @return AjaxResult 包含OSPF异常判断结果的AjaxResult对象
     */
    public AjaxResult obtainOSPFExceptionJudgmentResults(SwitchParameters switchParameters,List<OSPFPojo> pojoList) {
        for (OSPFPojo ospf:pojoList){
            try {
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }

                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "ospf",
                        "系统信息:" +
                                "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:ospf功能IP:"+ospf.getIp()+"端口号:"+ospf.getPort()+"状态:"+ospf.getState()+"\r\n");

                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("ProblemName","OSPF");
                if (ospf.toString().toUpperCase().indexOf("FULL")!=-1){
                    hashMap.put("IfQuestion","无问题");
                }else {
                    hashMap.put("IfQuestion","有问题");
                }

                // 获取自定义分隔符
                String customDelimiter = null;
                Object customDelimiterObject =  CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());
                if (customDelimiterObject instanceof String){
                    customDelimiter = (String) customDelimiterObject;
                }

                // 组装参数字符串
                // =:= 是自定义分割符
                hashMap.put("parameterString","功能"+customDelimiter+"是"+customDelimiter+"OSPF"+customDelimiter+"参数"+customDelimiter+"是"+customDelimiter+"地址:"+ospf.getIp()+"状态:"+ospf.getState()+"端口号:"+ospf.getPort());

                // 插入扫描结果
                SwitchScanResultController switchScanResultController = new SwitchScanResultController();
                Long insertId = switchScanResultController.insertSwitchScanResult(switchParameters, hashMap);

                // 获取扫描结果回显
                SwitchIssueEcho switchIssueEcho = new SwitchIssueEcho();
                switchIssueEcho.getSwitchScanResultListByData(switchParameters.getLoginUser().getUsername(),insertId);

            } catch (Exception e) {
                e.printStackTrace();
                return AjaxResult.error(e.getMessage());
            }
        }
        return AjaxResult.success("操作成功");
    }

    /**
     * 获取OSPF命令对象
     *
     * @param switchParameters 包含交换机参数的对象
     * @return 返回一个OSPF命令对象
     */
    public static OspfCommand getOspfCommand(SwitchParameters switchParameters) {
        // 创建一个OSPF命令对象
        OspfCommand ospfCommand = new OspfCommand();
        // 设置OSPF命令对象的品牌属性为交换机参数的品牌
        ospfCommand.setBrand(switchParameters.getDeviceBrand());
        // 设置OSPF命令对象的交换机类型属性为交换机参数的型号
        ospfCommand.setSwitchType(switchParameters.getDeviceModel());
        // 设置OSPF命令对象的固件版本属性为交换机参数的固件版本
        ospfCommand.setFirewareVersion(switchParameters.getFirmwareVersion());
        // 设置OSPF命令对象的子版本号属性为交换机参数的子版本号
        ospfCommand.setSubVersion(switchParameters.getSubversionNumber());
        // 返回创建好的OSPF命令对象
        return ospfCommand;
    }

    /**
     * 根据交换机返回信息和交换机参数获取OSPFPojo对象列表
     *
     * @param information 交换机返回信息
     * @param switchParameters 交换机参数
     * @return OSPFPojo对象列表
     */
    public static List<OSPFPojo> getOSPFPojo(String information,SwitchParameters switchParameters) {

        /**
         *根据 "obtainPortNumber.keyword" 在配置文件中 获取端口号关键词
         * Eth-Trunk Ethernet GigabitEthernet GE BAGG Eth
         * 根据空格分割为 关键词数组*/
        String deviceVersion = null;
        Object deviceVersionObject =  CustomConfigurationUtil.getValue("obtainPortNumber.keyword", Constant.getProfileInformation());
        if (deviceVersionObject instanceof String){
            deviceVersion = (String) deviceVersionObject;
        }
        String[] keywords = deviceVersion.trim().split(" ");

        /** 遍历配置文件定义的关于交换机端口号的特征 关键词
         * 如果 交换机返回结果包含 特征关键词 则 保存特征关键词 */
        List<String> keys = new ArrayList<>();
        for (String key:keywords){
            if (MyUtils.containIgnoreCase(information,key)){
                keys.add(key);
            }
        }

        /** 如果交换机返回信息中 不包含配置文件定义的 关于交换机端口号的特征 关键词
         * 则 返回空集合*/
        if (keys.size() == 0){
            return new ArrayList<>();
        }

        /**
         * 交换机返回信息 按行分割
         * 遍历交换机返回的信息行信息数组
         * 遍历特征关键词
         *  将端口号中有空格的情况 去除空格
         *  例如： GigabitEthernet 1/0/0  替换成  GigabitEthernet1/0/0*/
        List<String> string_split = Arrays.asList(information.split("\r\n"));
        for (String key:keys){
            for (int i = 0 ; i < string_split.size() ; i++){
                /* 判断一个字符串是否包含另一个字符串(忽略大小写) */
                if (MyUtils.containIgnoreCase(string_split.get(i),key+" ")){
                    string_split.set(i,string_split.get(i).replace(key+" ",key));
                }
            }
        }

        List<OSPFPojo> ospfPojos = getOSPFParameters(string_split,switchParameters.getDeviceBrand());
        return ospfPojos;
    }

    /**
     * 根据给定的字符串列表和设备品牌获取OSPF配置参数，并返回OSPFPojo列表
     *
     * @param stringSplit 包含OSPF配置参数的字符串列表
     * @param deviceBrand 设备品牌
     * @return 包含OSPF配置参数的OSPFPojo列表
     */
    private static List<OSPFPojo> getOSPFParameters(List<String> stringSplit, String deviceBrand) {
        // 获取OSPF配置参数键值对
        Map<String,String> key_value = (Map<String,String>)CustomConfigurationUtil.getValue("OSPF." + deviceBrand + ".R_table", Constant.getProfileInformation());
        // 创建OSPFPojo列表
        List<OSPFPojo> ospfPojos = new ArrayList<>();
        // 如果键值对为空，则返回空列表
        if (key_value == null){
            return ospfPojos;
        }
        // 根据键值对提取表格数据
        List<HashMap<String, Object>> hashMapList = DataExtraction.tableDataExtraction(stringSplit, key_value);

        // 遍历表格数据
        for (HashMap<String, Object> hashMap:hashMapList){
            // 创建OSPFPojo对象
            OSPFPojo ospfPojo = new OSPFPojo();
            // 设置IP地址
            ospfPojo.setIp(hashMap.get("neighborID").toString());
            // 设置端口号
            ospfPojo.setPort(hashMap.get("portNumber").toString());
            // 设置状态
            ospfPojo.setState(hashMap.get("state").toString());
            // 将OSPFPojo对象添加到列表中
            ospfPojos.add(ospfPojo);
        }

        // 返回OSPFPojo列表
        return ospfPojos;
    }

    public static String commandPortReturn = "OSPF Process 1 with Router ID 11.37.96.2\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "192.168.1.100 GigabitEthernet1/0/0 11.37.96.1 Full\n" +
            "0.0.0.0 GigabitEthernet1/0/1 11.37.96.5 Full\n" +
            "0.0.0.0 GigabitEthernet1/0/6 11.37.96.54 Full\n" +
            "0.0.0.0 GigabitEthernet1/0/8 11.37.96.73 Full\n" +
            "0.0.0.0 GigabitEthernet8/0/17 11.37.96.55 Full\n" +
            "0.0.0.0 Eth-Trunk20.2000 11.37.96.159 Full\n" +
            "0.0.0.0 GigabitEthernet1/0/17 11.37.96.41 Full\n" +
            "0.0.0.0 GigabitEthernet8/0/14 11.37.96.8 Full\n" +
            "0.0.0.2 GigabitEthernet8/0/2 11.37.96.134 Full\n" +
            "0.0.0.2 GigabitEthernet1/0/3 11.37.96.59 Full\n" +
            "0.0.0.2 GigabitEthernet1/0/4 11.37.96.60 Full\n" +
            "0.0.0.2 GigabitEthernet1/0/5 11.37.96.57 Full\n" +
            "0.0.0.2 GigabitEthernet8/0/22 11.37.96.61 Full\n" +
            "0.0.0.2 GigabitEthernet1/0/10 11.37.96.40 Full\n" +
            "0.0.0.2 GigabitEthernet1/0/15 11.37.96.12 Init\n" +
            "----------- --------------- ----------------------- ---------------------------\n" +
            "Total Peer(s): 15\n" +
            "\n" +
            "OSPF Process 11 with Router ID 30.9.98.241\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 GigabitEthernet1/0/13 30.8.1.3 Full\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1\n" +
            "\n" +
            "OSPF Process 25 with Router ID 10.122.119.49\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 GigabitEthernet8/0/19 10.122.119.18 Full\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1\n" +
            "\n" +
            "OSPF Process 310 with Router ID 28.36.127.5\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 Eth-Trunk20.2011 28.36.127.6 Init\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1\n" +
            "\n" +
            "OSPF Process 311 with Router ID 29.36.191.5\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 Eth-Trunk20.2012 29.36.191.6 Init\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1\n" +
            "\n" +
            "OSPF Process 312 with Router ID 30.9.127.5\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 0\n" +
            "\n" +
            "OSPF Process 313 with Router ID 27.36.127.5\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 0\n" +
            "\n" +
            "OSPF Process 314 with Router ID 6.40.0.45\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 Eth-Trunk20.2015 6.40.0.46 Full\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1\n" +
            "\n" +
            "OSPF Process 315 with Router ID 13.40.0.45\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 Eth-Trunk20.2016 13.40.0.46 Init\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1\n" +
            "\n" +
            "OSPF Process 316 with Router ID 172.16.193.45\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 Eth-Trunk20.2017 172.16.193.46 Full\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1\n" +
            "\n" +
            "OSPF Process 317 with Router ID 172.16.0.45\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 Eth-Trunk20.2018 172.16.0.46 Full\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1\n" +
            "\n" +
            "OSPF Process 318 with Router ID 7.36.1.45\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 Eth-Trunk20.2019 7.36.1.46 Full\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1\n" +
            "\n" +
            "OSPF Process 65534 with Router ID 128.75.212.73\n" +
            "Peer Statistic Information\n" +
            "----------------------------------------------------------------------------\n" +
            "Area Id Interface Neighbor id State\n" +
            "0.0.0.0 DCN-Serial1/0/0:0 128.79.235.137 Full\n" +
            "----------------------------------------------------------------------------\n" +
            "Total Peer(s): 1";
}
