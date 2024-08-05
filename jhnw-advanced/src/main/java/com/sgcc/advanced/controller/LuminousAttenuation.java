package com.sgcc.advanced.controller;
import com.sgcc.advanced.domain.InsertLightAttenuation;
import com.sgcc.advanced.domain.LightAttenuationCommand;
import com.sgcc.advanced.domain.LightAttenuationComparison;
import com.sgcc.advanced.service.ILightAttenuationCommandService;
import com.sgcc.advanced.service.ILightAttenuationComparisonService;
import com.sgcc.advanced.utils.ScreeningMethod;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.controller.SwitchScanResultController;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.switchboard.SwitchIssueEcho;
import com.sgcc.share.util.*;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.webSocket.WebSocketService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.split;

/**
 * 光衰功能
 */
@Api("光衰功能")
@RestController
@RequestMapping("/advanced/LuminousAttenuation")
@Transactional(rollbackFor = Exception.class)
public class LuminousAttenuation {

    @Autowired
    private ILightAttenuationComparisonService lightAttenuationComparisonService;
    @Autowired
    private ILightAttenuationCommandService lightAttenuationCommandService;

    /**
     * 光衰功能接口  发送命令 获取返回端口号信息数据
     * @param switchParameters
     * @return
     */
    public AjaxResult obtainLightDecay(SwitchParameters switchParameters) {

        /*1：获取配置文件关于 光衰问题的 符合交换机品牌的命令的 配置信息*/
        LightAttenuationCommand lightAttenuationCommand = new LightAttenuationCommand();
        lightAttenuationCommand.setBrand(switchParameters.getDeviceBrand());
        lightAttenuationCommand.setSwitchType(switchParameters.getDeviceModel());
        lightAttenuationCommand.setFirewareVersion(switchParameters.getFirmwareVersion());
        lightAttenuationCommand.setSubVersion(switchParameters.getSubversionNumber());

        lightAttenuationCommandService = SpringBeanUtil.getBean(ILightAttenuationCommandService.class);
        List<LightAttenuationCommand> lightAttenuationCommandList = lightAttenuationCommandService.selectLightAttenuationCommandListBySQL(lightAttenuationCommand);

        /*2：当 配置文件光衰问题的命令 为空时 进行 日志写入*/
        if (MyUtils.isCollectionEmpty(lightAttenuationCommandList)){

            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }

            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题为:光衰功能未定义获取端口号命令\r\n");

            return AjaxResult.error("未定义"+switchParameters.getDeviceBrand()+"交换机获取端口号命令");

        }

        //从 lightAttenuationCommandList 中 获取四项基本最详细的数据
        lightAttenuationCommand = ScreeningMethod.ObtainPreciseEntityClassesLightAttenuationCommand( lightAttenuationCommandList );
        /*获取up端口号命令*/
        String command = lightAttenuationCommand.getGetPortCommand();

        //配置文件光衰问题的命令 不为空时，执行交换机命令，返回交换机返回信息
        ExecuteCommand executeCommand = new ExecuteCommand();
        String returnString = executeCommand.executeScanCommandByCommand(switchParameters, command);

        returnString = "Interface Status Vlan Duplex Speed Type\n" +
                "--------------------------------------------------------------------------\n" +
                "GigabitEthernet 1/1 up 1002 Full 100M copper\n" +
                "GigabitEthernet 1/2 up 1003 Full 100M copper\n" +
                "GigabitEthernet 1/3 up 1004 Full 10M copper\n" +
                "GigabitEthernet 1/4 down 1 Unknown Unknown copper\n" +
                "GigabitEthernet 1/5 down 1001 Unknown Unknown copper\n" +
                "GigabitEthernet 1/6 up 5 Full 100M copper\n" +
                "GigabitEthernet 1/7 up 2010 Full 100M copper\n" +
                "GigabitEthernet 1/8 down 1 Unknown Unknown copper\n" +
                "GigabitEthernet 1/9 down 1 Unknown Unknown copper\n" +
                "GigabitEthernet 1/10 down 1 Unknown Unknown copper\n" +
                "GigabitEthernet 1/11 down 1 Unknown Unknown copper\n" +
                "GigabitEthernet 1/12 down 1 Unknown Unknown copper\n" +
                "GigabitEthernet 1/13 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 1/14 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 1/15 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 1/16 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 1/17 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 1/18 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 1/19 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 1/20 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 1/21 up 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 1/22 up 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 1/23 up 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 1/24 up 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/1 up routed Full 1000M fiber\n" +
                "GigabitEthernet 5/2 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/3 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/4 up 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/5 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/6 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/7 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/8 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/9 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/10 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/11 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/12 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/13 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/14 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/15 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/16 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/17 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/18 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/19 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/20 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/21 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/22 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/23 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/24 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/25 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/26 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/27 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/28 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/29 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/30 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/31 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/32 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/33 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/34 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/35 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/36 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/37 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/38 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/39 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/40 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/41 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/42 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/43 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/44 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/45 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/46 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/47 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 5/48 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 6/1 down 1 Unknown Unknown copper\n" +
                "GigabitEthernet 6/2 down 1 Unknown Unknown copper\n" +
                "GigabitEthernet 6/3 down 1 Unknown Unknown copper\n" +
                "GigabitEthernet 6/4 down 1 Unknown Unknown copper\n" +
                "GigabitEthernet 6/5 down 1 Unknown Unknown copper\n" +
                "GigabitEthernet 6/6 down 1 Unknown Unknown copper\n" +
                "GigabitEthernet 6/7 down 1 Unknown Unknown copper\n" +
                "GigabitEthernet 6/8 down 1 Unknown Unknown copper\n" +
                "GigabitEthernet 6/9 down 1 Unknown Unknown copper\n" +
                "GigabitEthernet 6/10 down 1 Unknown Unknown copper\n" +
                "GigabitEthernet 6/11 down 1 Unknown Unknown copper\n" +
                "GigabitEthernet 6/12 down 1 Unknown Unknown copper\n" +
                "GigabitEthernet 6/13 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 6/14 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 6/15 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 6/16 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 6/17 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 6/18 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 6/19 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 6/20 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 6/21 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 6/22 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 6/23 down 1 Unknown Unknown fiber\n" +
                "GigabitEthernet 6/24 down 1 Unknown Unknown fiber\n" +
                "TenGigabitEthernet 1/25 down 1 Unknown Unknown fiber\n" +
                "TenGigabitEthernet 1/26 down 1 Unknown Unknown fiber\n" +
                "TenGigabitEthernet 1/27 down 1 Unknown Unknown fiber\n" +
                "TenGigabitEthernet 1/28 down 1 Unknown Unknown fiber\n" +
                "TenGigabitEthernet 5/49 down 1 Unknown Unknown fiber\n" +
                "TenGigabitEthernet 5/50 down 1 Unknown Unknown fiber\n" +
                "TenGigabitEthernet 5/51 down 1 Unknown Unknown fiber\n" +
                "TenGigabitEthernet 5/52 down 1 Unknown Unknown fiber\n" +
                "TenGigabitEthernet 6/25 down 1 Unknown Unknown fiber\n" +
                "TenGigabitEthernet 6/26 down 1 Unknown Unknown fiber\n" +
                "TenGigabitEthernet 6/27 down 1 Unknown Unknown fiber\n" +
                "TenGigabitEthernet 6/28 down 1 Unknown Unknown fiber";

        returnString = MyUtils.trimString(returnString);

        // 4: 如果交换机返回信息为 null 则 命令错误，交换机返回错误信息
        if (returnString == null){

            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }

            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题为:光衰功能获取端口号命令错误,需要重新定义\r\n");

            return AjaxResult.error("IP地址为:"+switchParameters.getIp()+","+"问题为:光衰功能获取端口号命令错误,需要重新定义\r\n");
        }

        //5：如果交换机返回信息不为 null说明命令执行正常,
        //则继续 根据交换机返回信息获取获取 up状态 光衰端口号 铜缆除外，铜缆关键词为 COPPER
        List<String> port = ObtainUPStatusPortNumber(returnString);

        /*6：获取光衰端口号方法返回集合判断是否为空，说明没有端口号为开启状态 UP，是则进行*/
        if (MyUtils.isCollectionEmpty(port)){
            //关于没有端口号为UP状态 的错误代码库
            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }

            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题为:光衰功能无UP状态端口号\r\n");

            return AjaxResult.error("IP地址为:"+switchParameters.getIp()+","+"问题为:光衰功能无UP状态端口号\r\n");
        }

        /*7：如果交换机端口号为开启状态 UP 不为空 则需要查看是否需要转义：
        GE转译为GigabitEthernet  才能执行获取交换机端口号光衰参数命令*/
        String conversion = lightAttenuationCommand.getConversion();
        if (conversion != null){
            /*GE:GigabitEthernet*/
            String[] conversionSplit = conversion.split(";");
            for (String convers:conversionSplit){
                /* 转译 分割为 字符串数组*/
                String[] conversSplit = convers.split(":");
                for (int num=0;num<port.size();num++){
                    /* getFirstLetters 获取字符串开头字母部分
                     * 判断 是否与转译相同
                     * 如果相同 则 进行转译  */
                    if (MyUtils.getFirstLetters(port.get(num)).trim().equals(conversSplit[0])){
                        port.set(num,port.get(num).replace(conversSplit[0],conversSplit[1]));
                    }
                }
            }
        }

        //8：根据 up状态端口号 及交换机信息
        //获取光衰参数命令 ：  lightAttenuationCommand.getGetParameterCommand()
        //switchParameters ： 交换机信息类
        HashMap<String, String> getparameter = getparameter(port, switchParameters,lightAttenuationCommand.getGetParameterCommand());

        /*9：获取光衰参数为空*/
        if (MyUtils.isMapEmpty(getparameter)){
            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }

            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题为:光衰功能所有UP状态端口皆未获取到光衰参数\r\n");

            return AjaxResult.error("IP地址:"+switchParameters.getIp()+"未获取到光衰参数");
        }

        List<LightAttenuationComparison> pojoList = new ArrayList<>();

        /** 根据交换机IP获取数据表数据 */
        LightAttenuationComparison selectpojo = new LightAttenuationComparison();
        selectpojo.setSwitchIp(switchParameters.getIp());
        lightAttenuationComparisonService = SpringBeanUtil.getBean(ILightAttenuationComparisonService.class);
        List<LightAttenuationComparison> lightAttenuationComparisons = lightAttenuationComparisonService.selectLightAttenuationComparisonList(selectpojo);

        /** 数据表数据 存入MAP集合中 */
        Map<String,LightAttenuationComparison> lightAttenuationComparisonMap = new HashMap<>();
        for (LightAttenuationComparison pojo:lightAttenuationComparisons){
            lightAttenuationComparisonMap.put(pojo.getPort(),pojo);
        }
        /** 获取Map集合中KEY值数据集合*/
        List<String> keySet = lightAttenuationComparisonMap.keySet().stream().collect(Collectors.toList());
        /** 查看字符串集合A中存在，但字符串集合B中不存在的部分
         * 查看 数据表中有，但是扫描后没有的端口    数据表中独有的端口号*/
        List<String> difference = MyUtils.findDifference(keySet,port);

        /**
        * 数据表中有，扫描结果没有的 则 说明 断开了连接
         * 断开接口提示交换机端口断开连接，端口状态为DOWN*/
        for (String portstr:difference){

            /** 修改端口号状态  */
            LightAttenuationComparison lightAttenuationComparisonPojo = lightAttenuationComparisonMap.get(portstr);
            lightAttenuationComparisonPojo.setValueOne("DOWN");
            int i = lightAttenuationComparisonService.updateLightAttenuationComparison(lightAttenuationComparisonPojo);
            if (i > 0 ){

                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }

                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "光衰",
                        "系统信息:" +
                                "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:光衰功能端口号:"+portstr +"断开连接，端口状态为DOWN\r\n");

            }else {
                    String subversionNumber = switchParameters.getSubversionNumber();
                    if (subversionNumber!=null){
                        subversionNumber = "、"+subversionNumber;
                    }

                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "光衰",
                            "系统信息:" +
                                    "IP地址为:"+switchParameters.getIp()+","+
                                    "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                    "问题为:光衰功能端口号:"+portstr +"修改状态为DOWN失败\r\n"
                            );

                return AjaxResult.error();
            }
        }

        /*10:获取光衰参数不为空*/
        try {
            for (String portstr:port){


                //根据光衰参数阈值的代码库回显和日志
                String subversionNumber1 = switchParameters.getSubversionNumber();
                if (subversionNumber1!=null){
                    subversionNumber1 = "、"+subversionNumber1;
                }


                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "数据记录",
                        "系统信息:" +
                                "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion() + subversionNumber1 + ","+
                                "问题为:光衰功能端口号:"+portstr +
                                " TX:"+getparameter.get(portstr+"TX")+" RX:"+getparameter.get(portstr+"RX")+"\r\n");



                /*当光衰参数不为空时  光衰参数存入 光衰比较表*/
                if (getparameter.get(portstr+"TX") != null && getparameter.get(portstr+"RX") != null){

                    InsertLightAttenuation insertLightAttenuation = average(switchParameters, getparameter, portstr);

                    if (insertLightAttenuation.getInsertResults()>0){
                        pojoList.add(insertLightAttenuation.getLightAttenuationComparison());
                    }else {
                        /*数据库操作失败*/
                    }

                }else {
                    continue;
                }

                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("ProblemName","光衰");

                /* 如果 lightAttenuationComparisonMap 对应端口号 为空 则数据库表光衰记录表中没有相关信息
                则默认无问题*/
                if (lightAttenuationComparisonMap.get(portstr) == null){

                    hashMap.put("IfQuestion","无问题");

                }else {

                    LightAttenuationComparison lightAttenuationComparison = lightAttenuationComparisonMap.get(portstr);

                    if (lightAttenuationComparison.getRxRatedDeviation()!=null && lightAttenuationComparison.getTxRatedDeviation()!=null){
                        //判断光衰是否有问题
                        String meanJudgmentProblem = meanJudgmentProblem(lightAttenuationComparison);

                        hashMap.put("IfQuestion",meanJudgmentProblem);

                        if (meanJudgmentProblem.equals("无问题")){

                            //LightAttenuationComparison lightAttenuationComparisonPojo = lightAttenuationComparisonMap.get(portstr);

                            if (lightAttenuationComparison.getValueOne().equals("DOWN")){

                                String subversionNumber = switchParameters.getSubversionNumber();
                                if (subversionNumber!=null){
                                    subversionNumber = "、"+subversionNumber;
                                }

                                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "光衰",
                                        "系统信息:" +
                                                "IP地址为:"+switchParameters.getIp()+","+
                                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                                "问题为:光衰功能端口号:"+portstr + "恢复连接\r\n");

                            }

                        }else if (meanJudgmentProblem.equals("有问题")){

                            //LightAttenuationComparison lightAttenuationComparisonPojo = lightAttenuationComparisonMap.get(portstr);

                            if (lightAttenuationComparison.getValueOne().equals("DOWN")){

                                String subversionNumber = switchParameters.getSubversionNumber();
                                if (subversionNumber!=null){
                                    subversionNumber = "、"+subversionNumber;
                                }

                                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "光衰",
                                        "系统信息:" +
                                                "IP地址为:"+switchParameters.getIp()+","+
                                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                                "问题为:光衰功能端口号:"+portstr + "恢复连接,出现正负超限告警，提示重置基准数据\r\n");

                            }

                        }

                        //根据光衰参数阈值的代码库回显和日志
                        String subversionNumber = switchParameters.getSubversionNumber();
                        if (subversionNumber!=null){
                            subversionNumber = "、"+subversionNumber;
                        }

                        AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), null, "光衰",
                                "IP地址为:("+switchParameters.getIp()+"),"+
                                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                        lightAttenuationComparison.toJson() +"\r\n");
                    }
                }

                /*自定义分隔符*/
                String customDelimiter = (String) CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());

                hashMap.put("parameterString","端口号"+ customDelimiter +"是" + customDelimiter + portstr + customDelimiter +
                        "光衰参数"+ customDelimiter +"是"+ customDelimiter +
                        "TX:"+getparameter.get(portstr+"TX")+
                        "  RX:"+getparameter.get(portstr+"RX"));

                SwitchScanResultController switchScanResultController = new SwitchScanResultController();
                Long insertId = switchScanResultController.insertSwitchScanResult(switchParameters, hashMap);

                SwitchIssueEcho switchIssueEcho = new SwitchIssueEcho();
                switchIssueEcho.getSwitchScanResultListByData(switchParameters.getLoginUser().getUsername(),insertId);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return AjaxResult.success(pojoList);
    }


    /**
    * @Description 判断光衰是否有问题
     *  最新参数 减 起始值的绝对值 与额定偏差作比较
     *  判断是否小于额定偏差，小于则无问题，大于有问题
    * @createTime 2023/8/25 11:08
    * @desc
    * @param lightAttenuationComparison
     * @return
    */
    public String meanJudgmentProblem(LightAttenuationComparison lightAttenuationComparison) {
        //RX最新参数
        double rxLatestNumber = MyUtils.stringToDouble(lightAttenuationComparison.getRxLatestNumber());
        //TX最新参数
        double txLatestNumber = MyUtils.stringToDouble(lightAttenuationComparison.getTxLatestNumber());

        //RX起始值(基准)
        double rxStartValue = MyUtils.stringToDouble(lightAttenuationComparison.getRxStartValue());
        //TX起始值(基准)
        double txStartValue = MyUtils.stringToDouble(lightAttenuationComparison.getTxStartValue());

        //RX平均值
        double rxAverageValue = MyUtils.stringToDouble(lightAttenuationComparison.getRxAverageValue());
        //TX平均值
        double txAverageValue = MyUtils.stringToDouble(lightAttenuationComparison.getTxAverageValue());

        /*RX即时偏差*/
        //double rxImmediateDeviation = MyUtils.stringToDouble(""+CustomConfigurationUtil.getValue("光衰.rxImmediateDeviation",Constant.getProfileInformation()));
        /*TX即时偏差*/
        //double txImmediateDeviation = MyUtils.stringToDouble(""+CustomConfigurationUtil.getValue("光衰.txImmediateDeviation",Constant.getProfileInformation()));

        DecimalFormat df = new DecimalFormat("#.0000");

        //额定绝对值   |最新参数 - 起始值|
        Double rxfiberAttenuation = Math.abs(rxLatestNumber - rxStartValue);
        Double txfiberAttenuation = Math.abs(txLatestNumber - txStartValue);
        rxfiberAttenuation = MyUtils.stringToDouble(df.format(rxfiberAttenuation));
        txfiberAttenuation = MyUtils.stringToDouble(df.format(txfiberAttenuation));

        //即时绝对值   |最新参数 - 平均值|
        Double rxImmediateLightDecay  = Math.abs(rxLatestNumber - rxAverageValue);
        Double txImmediateLightDecay  = Math.abs(txLatestNumber - txAverageValue);
        rxImmediateLightDecay = MyUtils.stringToDouble(df.format(rxImmediateLightDecay));
        txImmediateLightDecay = MyUtils.stringToDouble(df.format(txImmediateLightDecay));

        /* 即时绝对值 >  即时偏差*/
        if (rxImmediateLightDecay > MyUtils.stringToDouble(lightAttenuationComparison.getTxImmediateDeviation())
        || txImmediateLightDecay > MyUtils.stringToDouble(lightAttenuationComparison.getRxImmediateDeviation())){
            return "有问题";
        }

        // 绝对值>额定偏差
        if (rxfiberAttenuation > MyUtils.stringToDouble(lightAttenuationComparison.getRxRatedDeviation())
                || txfiberAttenuation > MyUtils.stringToDouble(lightAttenuationComparison.getTxRatedDeviation())){
            return "有问题";
        }

        return "无问题";
    }

    /**
    * @Description 根据交换机返回信息获取获取 UP 状态端口号
     *
    * @desc
     * 遍历交换及返回信息，其中包含" UP "状态的、不包含"COPPER"铜缆的、
     * 不为”Eth“百兆光纤的 且不包含"."子端口号的  端口号筛选出来
     *
    * @param returnString
     * @return
    */
    public List<String> ObtainUPStatusPortNumber(String returnString) {
        /* 按行分割 交换机返回信息行信息 字符串数组*/
        String[] returnStringSplit = returnString.split("\r\n");

        /*遍历 交换机行信息字符串数组
         *  判断 交换机返回行信息是否包含 UP（状态）  且 不能为铜缆 "COPPER"
         *  是 则存放入端口待取集合*/
        List<String> strings = new ArrayList<>();
        for (String string:returnStringSplit){
            /*包含 交换机返回行信息转化为大写 UP状态  不能为COPPER铜缆的  并且该行带有“/”的 存放入端口待取集合*/
            if ((string.toUpperCase().indexOf(" UP ")!=-1)  && (string.toUpperCase().indexOf("COPPER") == -1)){
                strings.add(string.trim());
            }
        }

        /*遍历端口待取集合 执行取值方法 获取端口号*/
        List<String> port = new ArrayList<>();
        for (String information:strings){

            /*根据 UP 截取端口号 并 去除带"."的子端口*/
            String terminalSlogan = FunctionalMethods.getTerminalSlogan(information);

            /* 端口号不能为 null
            * getFirstLetters获取字符串开头字母部分  不能为 Eth  百兆网不获取 光衰信息*/
            if (terminalSlogan != null && !(MyUtils.getFirstLetters(terminalSlogan).equalsIgnoreCase("Eth"))){
                port.add(terminalSlogan);
            }

        }

        return port;
    }


    /**
     * 根据 up状态端口号 及交换机信息  发送命令并获取光衰参数信息
     * @param portNumber 端口号
     * @param switchParameters 交换机信息类
     * @return
     */
    public HashMap<String,String> getparameter(List<String> portNumber,SwitchParameters switchParameters,String command) {

        /*获取配置信息中 符合品牌的 获取基本信息的 获取光衰参数的 命令*/
        /*创建 返回对象 HashMap*/
        /*hashMap.put(port+"TX",values.get("TX")+"");
          hashMap.put(port+"RX",values.get("RX")+"");*/
        HashMap<String,String> hashMap = new HashMap<>();

        /*端口号集合 需要检测各端口号的光衰参数*/
        for (String port:portNumber){
            /*替换端口号 得到完整的 获取端口号光衰参数命令
            * 例如：端口号： GigabitEthernet1/0/0
            *       命令：   display transceiver interface 端口号 verbose
            *       替换为： display transceiver interface GigabitEthernet1/0/0 verbose*/
            String FullCommand = command.replaceAll("端口号",port);
            /**
             * 交换机执行命令 并返回结果
             */
            ExecuteCommand executeCommand = new ExecuteCommand();
            String returnResults = executeCommand.executeScanCommandByCommand(switchParameters, FullCommand);

            returnResults = "Current diagnostic parameters[AP:Average Power]:\r\n" +
                    "Temp(Celsius)   Voltage(V)      Bias(mA)            RX power(dBm)       TX power(dBm)\r\n" +
                    "37(OK)          3.36(OK)        15.91(OK)           -5.96(OK)[AP]       -6.04(OK)";

            returnResults = "Current Rx Power(dBM)                 :-11.87\r\n" +
                    "            Default Rx Power High Threshold(dBM)  :-2.00\r\n" +
                    "            Default Rx Power Low  Threshold(dBM)  :-23.98\r\n" +
                    "            Current Tx Power(dBM)                 :-2.80\r\n" +
                    "            Default Tx Power High Threshold(dBM)  :1.00\r\n" +
                    "            Default Tx Power Low  Threshold(dBM)  :-6.00";

            returnResults = "Port BW: 1G, Transceiver max BW: 1G, Transceiver Mode: SingleMode\r\n" +
                "WaveLength: 1310nm, Transmission Distance: 10km\r\n" +
                "Rx Power:  -6.0dBm, Warning range: [-16.989,  -5.999]dBm\r\n" +
                "Tx Power:  -6.20dBm, Warning range: [-9.500,  -2.999]dBm";

            returnResults = MyUtils.trimString(returnResults);

            if (returnResults == null){
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }

                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                        "异常:" +
                                "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:光衰功能"+port+"端口获取光衰参数命令错误,请重新定义\r\n");

                continue;
            }



            /**
             * 提取光衰参数
             */
            HashMap<String, Double> values = getDecayValues(returnResults,switchParameters);

            if (values.size() == 0){
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }

                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                        "异常:" +
                                "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:光衰功能"+port+"端口号未获取到光衰参数\r\n");

                continue;
            }



            hashMap.put(port+"TX",values.get("TX")+"");
            System.err.println(port+"TX" +"            "+values.get("TX")+"");
            hashMap.put(port+"RX",values.get("RX")+"");
            System.err.println(port+"RX"+"              "+values.get("RX")+"");

        }

        return hashMap;
    }


    /**
     * 提取光衰参数
     * @param string
     * @return
     */
    public HashMap<String,Double> getDecayValues(String string,SwitchParameters switchParameters) {
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

                Line_split[number] = MyUtils.caseInsensitiveReplace(Line_split[number], "RX POWER", "RXPOWER");
                Line_split[number] = MyUtils.caseInsensitiveReplace(Line_split[number], "TX POWER", "TXPOWER");

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
                /*两个都包含 则 两个参数值在一行
                * 如果不包含 ： 则将*/
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

                        AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
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
                if (MyUtils.containIgnoreCase(keyvalue,"RXPOWER")){
                    /*获取负数值 如果一个则是光衰 如果三个则是包含阈值*/
                    List<Double> doubleList = MyUtils.StringTruncationDoubleValue(keyvalue);

                    if (doubleList.size()==1){
                            /*Current Rx Power(dBM)                 :-11.87
                            Current Tx Power(dBM)                 :-2.80*/
                        rxpower = doubleList.get(0);

                    }else if (doubleList.size()==3){

                        Double rx = getParameterValueIndex("RXPOWER", doubleList, keyvalue);
                            /*  Rx Power:  -6.23dBm, Warning range: [-16.989,  -5.999]dBm
                                Tx Power:  -6.16dBm, Warning range: [-9.500,  -2.999]dBm  */
                        if (rx != null){
                            rxpower = rx;
                        }

                    }else {
                        String subversionNumber = switchParameters.getSubversionNumber();
                        if (subversionNumber!=null){
                            subversionNumber = "、"+subversionNumber;
                        }

                        AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                                "异常:" +
                                "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:光衰功能光衰参数行数值数量不正确,无法解析," +
                                "光衰参数行信息:"+keyvalue+"\r\n");

                    }
                }

                /*当 行信息包含 TX 说明是 TX数据*/
                if (MyUtils.containIgnoreCase(keyvalue,"TXPOWER")){

                    /*获取负数值 如果一个则是光衰 如果三个则是包含阈值*/
                    List<Double> doubleList = MyUtils.StringTruncationDoubleValue(keyvalue);
                    if (doubleList.size()==1){
                        txpower = doubleList.get(0);
                    }else if (doubleList.size()==3){
                        Double tx = getParameterValueIndex("TXPOWER", doubleList, keyvalue);
                        if (tx != null){
                            txpower = tx;
                        }
                    }else {
                        String subversionNumber = switchParameters.getSubversionNumber();
                        if (subversionNumber!=null){
                            subversionNumber = "、"+subversionNumber;
                        }

                        AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
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
     * 光衰参数存入 光衰比较表
     * @param switchParameters
     * @param hashMap
     * @param port
     * @return
     */
    public InsertLightAttenuation average(SwitchParameters switchParameters,HashMap<String,String> hashMap,String port) {

        LightAttenuationComparison lightAttenuationComparison = new LightAttenuationComparison();
        lightAttenuationComparison.setSwitchIp(switchParameters.getIp());
        lightAttenuationComparison.setSwitchId(FunctionalMethods.getSwitchParametersId(switchParameters));/*获取交换机四项基本信息ID*/
        lightAttenuationComparison.setPort(port);

        lightAttenuationComparisonService = SpringBeanUtil.getBean(ILightAttenuationComparisonService.class);
        List<LightAttenuationComparison> lightAttenuationComparisons = lightAttenuationComparisonService.selectLightAttenuationComparisonList(lightAttenuationComparison);

        String rx = hashMap.get(port+"RX");
        String tx = hashMap.get(port+"TX");

        if (MyUtils.isCollectionEmpty(lightAttenuationComparisons)){

            /*需要新插入信息*/
            lightAttenuationComparison = new LightAttenuationComparison();
            lightAttenuationComparison.setSwitchIp(switchParameters.getIp());
            /*获取交换机四项基本信息ID*/
            lightAttenuationComparison.setSwitchId(FunctionalMethods.getSwitchParametersId(switchParameters));
            lightAttenuationComparison.setPort(port);
            lightAttenuationComparison.setNumberParameters(1);
            lightAttenuationComparison.setRxLatestNumber(rx);
            lightAttenuationComparison.setRxAverageValue(rx);
            lightAttenuationComparison.setRxStartValue(rx);
            lightAttenuationComparison.setTxLatestNumber(tx);
            lightAttenuationComparison.setTxAverageValue(tx);
            lightAttenuationComparison.setTxStartValue(tx);
            /* 插入 默认 额定偏差*/
            lightAttenuationComparison.setRxRatedDeviation(""+CustomConfigurationUtil.getValue("光衰.rxRatedDeviation",Constant.getProfileInformation()));
            lightAttenuationComparison.setTxRatedDeviation(""+ CustomConfigurationUtil.getValue("光衰.txRatedDeviation",Constant.getProfileInformation()));
            /* 插入 默认 即时偏差*/
            lightAttenuationComparison.setRxImmediateDeviation(""+CustomConfigurationUtil.getValue("光衰.rxImmediateDeviation",Constant.getProfileInformation()));
            lightAttenuationComparison.setTxImmediateDeviation(""+ CustomConfigurationUtil.getValue("光衰.txImmediateDeviation",Constant.getProfileInformation()));


            lightAttenuationComparison.setValueOne("UP");

            InsertLightAttenuation insertLightAttenuation = new InsertLightAttenuation();
            insertLightAttenuation.setInsertResults(lightAttenuationComparisonService.insertLightAttenuationComparison(lightAttenuationComparison));
            insertLightAttenuation.setLightAttenuationComparison(lightAttenuationComparison);
            return insertLightAttenuation;
        }else {

            lightAttenuationComparison = lightAttenuationComparisons.get(0);
            lightAttenuationComparison.setRxLatestNumber(rx);
            double rxAverageValue = updateAverage(lightAttenuationComparison.getNumberParameters(), MyUtils.stringToDouble(lightAttenuationComparison.getRxAverageValue()), MyUtils.stringToDouble(rx));
            lightAttenuationComparison.setRxAverageValue(rxAverageValue+"");
            lightAttenuationComparison.setTxLatestNumber(tx);
            double txAverageValue = updateAverage(lightAttenuationComparison.getNumberParameters(), MyUtils.stringToDouble(lightAttenuationComparison.getTxAverageValue()), MyUtils.stringToDouble(tx));
            lightAttenuationComparison.setTxAverageValue("" + txAverageValue);
            lightAttenuationComparison.setNumberParameters(lightAttenuationComparison.getNumberParameters()+1);

            lightAttenuationComparison.setValueOne("UP");

            InsertLightAttenuation insertLightAttenuation = new InsertLightAttenuation();
            insertLightAttenuation.setInsertResults(lightAttenuationComparisonService.updateLightAttenuationComparison(lightAttenuationComparison));
            insertLightAttenuation.setLightAttenuationComparison(lightAttenuationComparison);
            return insertLightAttenuation;

        }
    }

    /**
     * 保留四位小数
     * @param numberParameters
     * @param avg
     * @param newParameter
     * @return
     */
    public double updateAverage(int numberParameters ,double avg, double newParameter) {
        DecimalFormat df = new DecimalFormat("#.0000");
        String result = df.format((newParameter + numberParameters * avg) / (numberParameters + 1));
        return MyUtils.stringToDouble(result);
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
        String[] split = input.split("\\s+");
        HashMap<String,Integer> position = new HashMap<>();
        for (int i = 0 ; i < split.length ; i++){
            if ( split[i].indexOf("RX")!=-1 ){/*&& split[i].indexOf("power")!=-1*/

                position.put("RX",i);
            }else if ( split[i].indexOf("TX")!=-1 ){/*&& split[i].indexOf("power")!=-1*/

                position.put("TX",i);
            }
        }
        return position;
    }

    /*获取 RX或者TX后面的第一个参数*/
    public static Double getParameterValueIndex(String keyword,List<Double> values,String input) {
        // 获取keyword在input中出现的所有位置
        List<Integer> keywordPositions = MyUtils.getSubstringPositions(input, keyword);

        if (keywordPositions.size()!=1){
            return null;
        }
        // 获取keyword在input中第一次出现的位置
        Integer keywordPosition = keywordPositions.get(0);
        // 从keyword位置后截取字符串，并提取其中的Double类型值
        List<Double> doubleList = MyUtils.StringTruncationDoubleValue(input.substring(keywordPosition+keyword.length(), input.length()));

        if (doubleList.size() == 0){
            return null;
        }else {
            return doubleList.get(0);
        }

        /*// 子字符串在父字符串中出现的所有位置
        List<Integer> keywordPositions = MyUtils.getSubstringPositions(input, keyword);

        if (keywordPositions.size()!=1){
            return null;
        }

        Integer keywordPosition = keywordPositions.get(0);

        Set<Integer> positionList = new HashSet<>();

        // 遍历values列表，获取每个值在input中的位置
        for (double value:values){
            positionList.addAll(MyUtils.getSubstringPositions(input, zero_suppression(value+"") ));
        }

        int i = 0;
        // 从keywordPosition+2开始遍历，直到input的倒数第二个字符
        for (i = keywordPosition+2 ; i < input.length()-1 ; i++){
            boolean contains = positionList.contains(i);
            if (contains){
                break;
            }
        }

        // 截取从i位置到input末尾的子字符串，并提取其中的Double类型值
        List<Double> doubleList = MyUtils.StringTruncationDoubleValue(input.substring(i, input.length()));

        if (doubleList.size() == 0){
            return null;
        }else {
            return doubleList.get(0);
        }*/
    }


    /*Double类型字符串去除结尾的0 或者结尾的.*/
    public static String zero_suppression(String value) {
        while ((value+"").endsWith("0") || (value+"").endsWith(".")){
            value = value.substring(0,value.length()-1);
        }
        return value;
    }
}
