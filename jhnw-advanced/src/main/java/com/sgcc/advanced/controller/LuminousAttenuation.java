package com.sgcc.advanced.controller;
import com.sgcc.advanced.domain.InsertLightAttenuation;
import com.sgcc.advanced.domain.LightAttenuationCommand;
import com.sgcc.advanced.domain.LightAttenuationComparison;
import com.sgcc.advanced.service.ILightAttenuationCommandService;
import com.sgcc.advanced.service.ILightAttenuationComparisonService;
import com.sgcc.advanced.utils.DataExtraction;
import com.sgcc.advanced.utils.ScreeningMethod;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.controller.SwitchScanResultController;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.switchboard.SwitchIssueEcho;
import com.sgcc.share.util.*;
import com.sgcc.share.domain.Constant;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.split;

/**
 * 光衰功能
 */
@Api(tags = "光衰功能管理")
@RestController
@RequestMapping("/advanced/LuminousAttenuation")
@Transactional(rollbackFor = Exception.class)
public class LuminousAttenuation {

    @Autowired
    private ILightAttenuationComparisonService lightAttenuationComparisonService;
    @Autowired
    private ILightAttenuationCommandService lightAttenuationCommandService;


    /**
     * 获取扫描结果
     *
     * @param switchParameters 交换机参数对象
     * @param ports 端口列表
     * @param getparameter 获取的参数集合
     * @return AjaxResult对象，包含光衰比较列表
     */
    public AjaxResult obtainScanningResults(SwitchParameters switchParameters,List<String> ports,HashMap<String, String> getparameter) {
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
        List<String> difference = MyUtils.findDifference(keySet,ports);

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

        /*自定义分隔符*/
        String customDelimiter =null;
        Object customDelimiterObject =  CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());
        if (customDelimiterObject instanceof String){
            customDelimiter = (String) customDelimiterObject;
        }

        List<LightAttenuationComparison> pojoList = new ArrayList<>();

        /*10:获取光衰参数不为空*/
        try {
            for (String portstr:ports){

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
                        // todo 数据库操作失败
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
                        if (meanJudgmentProblem.equals("无问题") && lightAttenuationComparison.getValueOne().equals("DOWN")){
                            //LightAttenuationComparison lightAttenuationComparisonPojo = lightAttenuationComparisonMap.get(portstr);

                            handleProblem(switchParameters,"问题为:光衰功能端口号:"+portstr + "恢复连接\r\n");

                        }else if (meanJudgmentProblem.equals("有问题") && lightAttenuationComparison.getValueOne().equals("DOWN")){
                            //LightAttenuationComparison lightAttenuationComparisonPojo = lightAttenuationComparisonMap.get(portstr);

                            handleProblem(switchParameters,"问题为:光衰功能端口号:"+portstr + "恢复连接,出现正负超限告警，提示重置基准数据\r\n");

                        }

                        //根据光衰参数阈值的代码库回显和日志
                        logAbnormalAlarmInformation(switchParameters, lightAttenuationComparison);

                    }
                }

                /* customDelimiter 自定义分隔符 */
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
     * 记录异常告警信息
     * 该方法用于组装并记录光衰异常告警信息，包括设备基本信息和光衰检测结果
     *
     * @param switchParameters 用于获取设备的IP、品牌、型号及软件版本等基本信息
     * @param lightAttenuationComparison 用于获取光衰检测结果的详细信息
     */
    private void logAbnormalAlarmInformation(SwitchParameters switchParameters, LightAttenuationComparison lightAttenuationComparison) {
        // 附加子版本号，用于精确描述设备的软件版本
        String subversionNumber = appendSubversionNumber(switchParameters);

        // 组装并发送异常告警信息
        AbnormalAlarmInformationMethod.afferent(
                switchParameters.getIp(), // 设备IP地址
                null, // 此处传递null，表示无特定异常类型
                "光衰", // 异常类型描述
                "IP地址为:(" + switchParameters.getIp() + "),"
                        + "基本信息为:" + switchParameters.getDeviceBrand() + "、" + switchParameters.getDeviceModel() + "、"
                        + switchParameters.getFirmwareVersion() + subversionNumber + ","
                        + lightAttenuationComparison.toJson() + "\r\n" // 将光衰检测结果转换为JSON格式并附加到信息中
        );
    }

    /**
     * 处理问题方法
     * 该方法用于处理网络设备出现的特定问题，如光衰等
     * 它会将问题相关信息和设备信息一起记录，以便后续分析和追踪
     *
     * @param switchParameters 网络设备参数，包含设备的IP、品牌、型号、固件版本等信息
     * @param questionInformation 问题描述信息，详细描述当前设备遇到的问题
     */
    private void handleProblem(SwitchParameters switchParameters, String questionInformation) {

        // 获取并拼接子版本号
        String subversionNumber = appendSubversionNumber(switchParameters);

        // 记录异常报警信息
        // 该信息包括设备IP、用户、问题类型以及详细的问题和设备信息
        AbnormalAlarmInformationMethod.afferent(
                switchParameters.getIp(),
                switchParameters.getLoginUser().getUsername(),
                "光衰",
                "系统信息:" +
                        "IP地址为:" + switchParameters.getIp() + "," +
                        "基本信息为:" + switchParameters.getDeviceBrand() + "、" + switchParameters.getDeviceModel() + "、" + switchParameters.getFirmwareVersion() + subversionNumber + "," +
                        questionInformation
        );
    }

    /**
     * 将子版本号追加到字符串
     * 如果子版本号存在，则返回以"、"开头的子版本号字符串；如果不存在，则返回空字符串
     *
     * @param switchParameters 切换参数对象，包含子版本号等信息
     * @return 追加了子版本号的字符串，如果子版本号不存在，则返回空字符串
     */
    private String appendSubversionNumber(SwitchParameters switchParameters) {
        String subversionNumber = switchParameters.getSubversionNumber();
        return subversionNumber != null ? "、" + subversionNumber : "";
    }

    /**
     * 光衰功能接口  发送命令 获取返回端口号信息数据
     *
     * @param switchParameters 交换机参数对象
     * @return AjaxResult对象，包含操作结果
     */
    public AjaxResult obtainLightDecay(SwitchParameters switchParameters) {
        // 获取光衰命令对象
        AjaxResult lightAttenuationCommandPojo = getLightAttenuationCommandPojo(switchParameters);
        // 如果命令操作不成功，则返回错误结果
        if (!lightAttenuationCommandPojo.get("msg").equals("操作成功")){
            return lightAttenuationCommandPojo;
        }

        // 将返回结果转换为光衰命令对象
        LightAttenuationCommand lightAttenuationCommand = (LightAttenuationCommand)lightAttenuationCommandPojo.get("data");

        // 获取端口号信息数据
        AjaxResult AjaxResultport = getPort(switchParameters, lightAttenuationCommand);
        // 如果获取端口号操作不成功，则返回错误结果
        if(!AjaxResultport.get("msg").equals("操作成功")){
            return AjaxResultport;
        }
        // 将返回结果转换为端口号列表
        List<String> ports = (List<String>) AjaxResultport.get("data");

        // 获取光衰参数
        AjaxResult lightAttenuationParameters = getLightAttenuationParameters(ports, switchParameters, lightAttenuationCommand);
        // 如果获取光衰参数操作不成功，则返回错误结果
        if (!lightAttenuationParameters.get("msg").equals("操作成功")){
            return lightAttenuationParameters;
        }
        // 将返回结果转换为光衰参数集合
        HashMap<String, String> getparameter = (HashMap<String, String>) lightAttenuationParameters.get("data");

        // 获取扫描结果
        AjaxResult ajaxResult = obtainScanningResults(switchParameters, ports, getparameter);
        // 返回扫描结果
        return ajaxResult;
    }


    /**
     * 根据交换机参数获取光衰端口号
     *
     * @param switchParameters 交换机参数对象
     * @return AjaxResult对象，包含获取到的光衰端口号列表
     */
    public AjaxResult getPort(SwitchParameters switchParameters,LightAttenuationCommand lightAttenuationCommand) {

        /*获取up端口号命令*/
        String command = lightAttenuationCommand.getGetPortCommand();
        //配置文件光衰问题的命令 不为空时，执行交换机命令，返回交换机返回信息
        ExecuteCommand executeCommand = new ExecuteCommand();
        String returnString = executeCommand.executeScanCommandByCommand(switchParameters, command);
        // todo 光衰虚拟数据
        returnString = this.returnPortString;
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

        return AjaxResult.success(port);
    }

    /**
     * 获取光衰参数
     *
     * @param port 光衰端口列表
     * @param switchParameters 交换机参数对象
     * @param lightAttenuationCommand 光衰命令对象
     * @return 包含获取到的光衰参数的 AjaxResult 对象
     */
    public AjaxResult getLightAttenuationParameters(List<String> port, SwitchParameters switchParameters, LightAttenuationCommand lightAttenuationCommand) {
        // 调用 getparameter 方法获取参数，并将结果保存在 getparameter 变量中
        HashMap<String, String> getparameter = getparameter(port, switchParameters, lightAttenuationCommand.getGetParameterCommand());

        // 如果 getparameter 变量为空
        if (MyUtils.isMapEmpty(getparameter)) {
            // 获取交换机的子版本号
            String subversionNumber = switchParameters.getSubversionNumber();

            // 如果子版本号不为空，则在其前面加上"、"
            if (subversionNumber != null) {
                subversionNumber = "、" + subversionNumber;
            }

            // 调用 AbnormalAlarmInformationMethod 类的 afferent 方法记录问题日志
            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:" +
                            "IP地址为:" + switchParameters.getIp() + "," +
                            "基本信息为:" + switchParameters.getDeviceBrand() + "、" + switchParameters.getDeviceModel() + "、" + switchParameters.getFirmwareVersion() + subversionNumber + "," +
                            "问题为:光衰功能所有UP状态端口皆未获取到光衰参数\r\n");

            // 返回错误结果的 AjaxResult 对象
            return AjaxResult.error("IP地址:" + switchParameters.getIp() + "未获取到光衰参数");
        }

        // 返回成功结果的 AjaxResult 对象，并包含获取到的参数 getparameter
        return AjaxResult.success(getparameter);
    }

    /**
     * 根据交换机参数获取光衰减命令对象并返回AjaxResult
     *
     * @param switchParameters 交换机参数对象
     * @return AjaxResult 光衰减命令对象
     */
    public  AjaxResult getLightAttenuationCommandPojo(SwitchParameters switchParameters) {
        /*1：获取配置文件关于 光衰问题的 符合交换机品牌的命令的 配置信息*/
        // 根据交换机参数获取光衰减命令对象
        LightAttenuationCommand lightAttenuationCommand = getLightAttenuationCommand(switchParameters);
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
        return AjaxResult.success(lightAttenuationCommand);
    }

    /**
    * 判断光衰是否有问题
    * 通过比较最新参数与起始值的绝对差值是否小于额定偏差来判断光衰情况
    * 小于额定偏差表示无问题，大于则表示有问题
    *
    * @param lightAttenuationComparison 光衰比较数据，包含RX和TX的最新参数、起始值、平均值和额定偏差等
    * @return 返回判断结果，"无问题"或"有问题"
    */
    public String meanJudgmentProblem(LightAttenuationComparison lightAttenuationComparison) {

        // 将字符串参数转换为double类型
        //RX最新参数
        double rxLatestNumber = MyUtils.stringToDouble(lightAttenuationComparison.getRxLatestNumber());
        //TX最新参数
        double txLatestNumber = MyUtils.stringToDouble(lightAttenuationComparison.getTxLatestNumber());
        //RX起始值(基准)
        double rxStartValue = MyUtils.stringToDouble(lightAttenuationComparison.getRxStartValue());
        //TX起始值(基准)
        double txStartValue = MyUtils.stringToDouble(lightAttenuationComparison.getTxStartValue());

        // 获取平均值
        //RX平均值
        double rxAverageValue = MyUtils.stringToDouble(lightAttenuationComparison.getRxAverageValue());
        //TX平均值
        double txAverageValue = MyUtils.stringToDouble(lightAttenuationComparison.getTxAverageValue());

        // 用于格式化输出
        DecimalFormat df = new DecimalFormat("#.0000");

        // 计算额定绝对值：最新参数减起始值的绝对值
        Double rxfiberAttenuation = Math.abs(rxLatestNumber - rxStartValue);
        Double txfiberAttenuation = Math.abs(txLatestNumber - txStartValue);
        rxfiberAttenuation = MyUtils.stringToDouble(df.format(rxfiberAttenuation));
        txfiberAttenuation = MyUtils.stringToDouble(df.format(txfiberAttenuation));

        // 计算即时绝对值：最新参数减平均值的绝对值
        Double rxImmediateLightDecay = Math.abs(rxLatestNumber - rxAverageValue);
        Double txImmediateLightDecay = Math.abs(txLatestNumber - txAverageValue);
        rxImmediateLightDecay = MyUtils.stringToDouble(df.format(rxImmediateLightDecay));
        txImmediateLightDecay = MyUtils.stringToDouble(df.format(txImmediateLightDecay));

        // 判断即时绝对值是否大于即时偏差
        if (rxImmediateLightDecay > MyUtils.stringToDouble(lightAttenuationComparison.getTxImmediateDeviation())
                || txImmediateLightDecay > MyUtils.stringToDouble(lightAttenuationComparison.getRxImmediateDeviation())) {
            return "有问题";
        }

        // 判断额定绝对值是否大于额定偏差
        if (rxfiberAttenuation > MyUtils.stringToDouble(lightAttenuationComparison.getRxRatedDeviation())
                || txfiberAttenuation > MyUtils.stringToDouble(lightAttenuationComparison.getTxRatedDeviation())) {
            return "有问题";
        }

        // 获取光衰百分比配置
        String percentage = null;
        Object percentageObject = CustomConfigurationUtil.getValue("光衰.percentage", Constant.getProfileInformation());
        if (percentageObject instanceof String) {
            percentage = (String) percentageObject;
        }

        // 处理光衰百分比
        List<Double> doubles = MyUtils.StringTruncationDoubleValue(percentage);
        if (doubles.size() == 1) {
            double percentageDouble = doubles.get(0) * 0.01;
            //RX绝对值
            double rxAbs = Math.abs(rxLatestNumber - rxStartValue);
            //TX绝对值
            double txAbs = Math.abs(txLatestNumber - txStartValue);
            if (rxAbs < Math.abs(rxStartValue) * percentageDouble
                    && txAbs < Math.abs(txStartValue) * percentageDouble) {
                return "无问题";
            } else {
                return "有问题";
            }
        }

        // 默认返回无问题
        return "无问题";
    }


    /**
    * @Description 根据交换机返回信息获取获取 UP 状态端口号 非"COPPER"铜缆的 数据
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
            // todo 光衰虚拟数据
            returnResults = this.returnValueResults;
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
            /*HashMap<String, Double> values = getDecayValues(returnResults,switchParameters);*/
            HashMap<String, Double> values =obtainTheParameterOfLightAttenuation(returnResults,switchParameters);

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

            if(values.get("TX") != null){
                hashMap.put(port+"TX",values.get("TX")+"");
            }else if (LuminousAttenuationL_listEnum.TX_Power().toString() != null){
                hashMap.put(port+"TX",values.get(LuminousAttenuationL_listEnum.TX_Power().toString())+"");
            }
            /*System.err.println(port+"TX" +"            "+hashMap.get(port+"TX"));*/

            if(values.get("RX") != null){
                hashMap.put(port+"RX",values.get("RX")+"");
            }else if (values.get(LuminousAttenuationL_listEnum.RX_Power().toString()) != null){
                hashMap.put(port+"RX",values.get(LuminousAttenuationL_listEnum.RX_Power().toString())+"");
            }
            /*System.err.println(port+"RX"+"              "+hashMap.get(port+"RX"));*/
        }
        return hashMap;
    }

    /**
     * 根据返回结果和交换机参数获取光衰参数值
     *
     * @param returnResults 返回结果字符串
     * @param switchParameters 交换机参数
     * @return 包含光衰参数值的HashMap，其中key为参数名称，value为对应的参数值
     */
    private HashMap<String, Double> obtainTheParameterOfLightAttenuation(String returnResults, SwitchParameters switchParameters) {
        HashMap<String, Double> values = new HashMap<>();

        // 获取关键字配置
        Map<String,Object> keywordS = (Map<String,Object>) CustomConfigurationUtil.getValue("光衰." + switchParameters.getDeviceBrand(), Constant.getProfileInformation());

        if (keywordS == null){
            return values;
        }

        Map<String,String> keywordMap = new HashMap<>();
        List<String> keywordList = keywordS.keySet().stream().collect(Collectors.toList());
        List<String> returnResults_split_List = Arrays.stream(returnResults.split("\r\n")).collect(Collectors.toList());

        // 处理R_table的情况
        if (keywordList.indexOf("R_table")!=-1){
            // 获取R_table关键字配置
            keywordMap = (Map<String,String>) CustomConfigurationUtil.getValue("光衰." + switchParameters.getDeviceBrand()+".R_table", Constant.getProfileInformation());
            if (keywordMap == null){
                return values;
            }
            // 解析表格数据
            List<HashMap<String, Object>> stringObjectHashMapList = DataExtraction.tableDataExtraction(returnResults_split_List,
                    keywordMap,switchParameters);
            if (stringObjectHashMapList.size() != 1){
                return values;
            }
            Set<String> strings = stringObjectHashMapList.get(0).keySet();
            for (String keywordName:strings){
                // 提取字符串中的数字
                List<Double> doubles = MyUtils.StringTruncationDoubleValue( ( String ) stringObjectHashMapList.get(0).get(keywordName));
                if (doubles.size() == 1){
                    values.put(keywordName,doubles.get(0));
                }
            }

            // 处理L_list的情况
        }else if (keywordList.indexOf("L_list")!=-1){
            // 获取L_list关键字配置
            keywordMap = (Map<String,String>) CustomConfigurationUtil.getValue("光衰." + switchParameters.getDeviceBrand()+".L_list", Constant.getProfileInformation());
            if (keywordMap == null){
                return values;
            }
            Set<String> strings = keywordMap.keySet();
            for (String keywordName:strings){
                for (String keyword:returnResults_split_List) {
                    Object mapvalue = keywordMap.get(keywordName);
                    if (mapvalue instanceof String){
                        // 获取占位符的含义
                        Map<String,String> theMeaningOfPlaceholders = DataExtraction.getTheMeaningOfPlaceholders(keyword, (String) mapvalue,switchParameters);
                        if (theMeaningOfPlaceholders.size() == 1){
                            String words = keywordMap.get(keywordName);
                            // 提取占位符
                            List<String> placeholders = DataExtraction.getPlaceholders(words);
                            if (placeholders.size() == 1){
                                // 提取占位符对应的字符串中的数字
                                List<Double> doubles = MyUtils.StringTruncationDoubleValue(theMeaningOfPlaceholders.get(placeholders.get(0)));
                                if (doubles.size() == 1){
                                    values.put(keywordName,doubles.get(0));
                                }
                            }
                        }
                    }else if (mapvalue instanceof Map){
                        Map<String,String> map = (Map<String,String>) mapvalue;
                        // 获取占位符的含义
                        Map<String,String> theMeaningOfPlaceholders = DataExtraction.getTheMeaningOfPlaceholders(keyword,map.get("keyword"),switchParameters);
                        if (theMeaningOfPlaceholders.size() == 1){
                            String words = keywordMap.get(keywordName);
                            // 提取占位符
                            List<String> placeholders = DataExtraction.getPlaceholders(words);
                            if (placeholders.size() == 1){
                                // 提取占位符对应的字符串中的数字
                                List<Double> doubles = MyUtils.StringTruncationDoubleValue(theMeaningOfPlaceholders.get(placeholders.get(0)));
                                if (doubles.size() == 1){
                                    values.put(keywordName,doubles.get(0));
                                }
                            }
                        }else {
                            /* todo 获取到多个占位符 */
                            for (String getkey_ymlvalue:theMeaningOfPlaceholders.keySet()){
                                for (String ymlkey:map.keySet()){
                                    if (map.get(ymlkey).equals(getkey_ymlvalue)){
                                        // 提取占位符对应的字符串中的数字
                                        List<Double> doubles = MyUtils.StringTruncationDoubleValue(theMeaningOfPlaceholders.get(getkey_ymlvalue));
                                        if (doubles.size() == 1){
                                            values.put(ymlkey,doubles.get(0));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return values;
    }


    /**
     * 根据交换机参数获取光衰减命令对象
     *
     * @param switchParameters 交换机参数对象
     * @return 返回创建好的光衰减命令对象
     */
    public static LightAttenuationCommand getLightAttenuationCommand(SwitchParameters switchParameters) {
        // 创建一个 LightAttenuationCommand 对象
        LightAttenuationCommand lightAttenuationCommand = new LightAttenuationCommand();
        // 设置 LightAttenuationCommand 对象的品牌属性为交换机参数的品牌
        lightAttenuationCommand.setBrand(switchParameters.getDeviceBrand());
        // 设置 LightAttenuationCommand 对象的交换机类型属性为交换机参数的型号
        lightAttenuationCommand.setSwitchType(switchParameters.getDeviceModel());
        // 设置 LightAttenuationCommand 对象的固件版本属性为交换机参数的固件版本
        lightAttenuationCommand.setFirewareVersion(switchParameters.getFirmwareVersion());
        // 设置 LightAttenuationCommand 对象的子版本号属性为交换机参数的子版本号
        lightAttenuationCommand.setSubVersion(switchParameters.getSubversionNumber());
        // 返回创建好的 LightAttenuationCommand 对象
        return lightAttenuationCommand;
    }

    /**
     * 光衰参数存入 光衰比较表
     * @param switchParameters 交换机参数
     * @param hashMap 光衰参数哈希表
     * @param port 端口号
     * @return 插入结果
     */
    public InsertLightAttenuation average(SwitchParameters switchParameters,HashMap<String,String> hashMap,String port) {
        // 创建光衰比较表对象
        LightAttenuationComparison lightAttenuationComparison = new LightAttenuationComparison();
        // 设置交换机的IP地址
        lightAttenuationComparison.setSwitchIp(switchParameters.getIp());
        // 获取交换机四项基本信息ID并设置到光衰比较表对象中
        lightAttenuationComparison.setSwitchId(FunctionalMethods.getSwitchParametersId(switchParameters));/*获取交换机四项基本信息ID*/
        // 设置端口号
        lightAttenuationComparison.setPort(port);

        // 获取光衰比较表服务
        lightAttenuationComparisonService = SpringBeanUtil.getBean(ILightAttenuationComparisonService.class);
        // 查询光衰比较表列表
        List<LightAttenuationComparison> lightAttenuationComparisons = lightAttenuationComparisonService.selectLightAttenuationComparisonList(lightAttenuationComparison);

        // 获取接收端光衰参数
        String rx = hashMap.get(port+"RX");
        // 获取发送端光衰参数
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
            Object rxRatedDeviation = CustomConfigurationUtil.getValue("光衰.rxRatedDeviation",Constant.getProfileInformation());
            if (rxRatedDeviation!=null ){
                lightAttenuationComparison.setRxRatedDeviation(rxRatedDeviation+"");
            }
            Object txRatedDeviation = CustomConfigurationUtil.getValue("光衰.txRatedDeviation",Constant.getProfileInformation());
            if (txRatedDeviation!=null ){
                lightAttenuationComparison.setTxRatedDeviation(txRatedDeviation+"");
            }

            /* 插入 默认 即时偏差*/
            Object rxImmediateDeviation = CustomConfigurationUtil.getValue("光衰.rxImmediateDeviation",Constant.getProfileInformation());
            if (rxImmediateDeviation!=null ){
                lightAttenuationComparison.setRxImmediateDeviation(rxImmediateDeviation+"");
            }
            Object txImmediateDeviation = CustomConfigurationUtil.getValue("光衰.txImmediateDeviation",Constant.getProfileInformation());
            if (txImmediateDeviation!=null ){
                lightAttenuationComparison.setTxImmediateDeviation(txImmediateDeviation+"");
            }

            lightAttenuationComparison.setValueOne("UP");

            InsertLightAttenuation insertLightAttenuation = new InsertLightAttenuation();
            insertLightAttenuation.setInsertResults(lightAttenuationComparisonService.insertLightAttenuationComparison(lightAttenuationComparison));
            insertLightAttenuation.setLightAttenuationComparison(lightAttenuationComparison);
            return insertLightAttenuation;
        }else {
            // 否则，获取光衰比较表列表的第一个对象
            lightAttenuationComparison = lightAttenuationComparisons.get(0);
            // 设置最新的接收端光衰参数
            lightAttenuationComparison.setRxLatestNumber(rx);
            // 更新接收端光衰平均值
            double rxAverageValue = updateAverage(lightAttenuationComparison.getNumberParameters(), MyUtils.stringToDouble(lightAttenuationComparison.getRxAverageValue()), MyUtils.stringToDouble(rx));
            // 设置接收端光衰平均值
            lightAttenuationComparison.setRxAverageValue(rxAverageValue+"");
            // 设置最新的发送端光衰参数
            lightAttenuationComparison.setTxLatestNumber(tx);
            // 更新发送端光衰平均值
            double txAverageValue = updateAverage(lightAttenuationComparison.getNumberParameters(), MyUtils.stringToDouble(lightAttenuationComparison.getTxAverageValue()), MyUtils.stringToDouble(tx));
            // 设置发送端光衰平均值
            lightAttenuationComparison.setTxAverageValue("" + txAverageValue);
            // 更新光衰参数数量
            lightAttenuationComparison.setNumberParameters(lightAttenuationComparison.getNumberParameters()+1);

            // 设置状态为UP
            lightAttenuationComparison.setValueOne("UP");

            // 创建插入光衰结果对象
            InsertLightAttenuation insertLightAttenuation = new InsertLightAttenuation();
            // 设置插入结果
            insertLightAttenuation.setInsertResults(lightAttenuationComparisonService.updateLightAttenuationComparison(lightAttenuationComparison));
            // 设置光衰比较表对象
            insertLightAttenuation.setLightAttenuationComparison(lightAttenuationComparison);
            // 返回插入结果
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


    private static String returnPortString = "Interface Status Vlan Duplex Speed Type\n" +
            "--------------------------------------------------------------------------\n" +
            "GigabitEthernet 1/1 up 1002 Full 100M copper\n" +
            "GigabitEthernet 1/2 up 1003 Full 100M copper\n" +
            "GigabitEthernet 1/3 up 1004 Full 10M copper\n" +
            "GigabitEthernet 1/4 down 1 Unknown Unknown copper\n" +
            "GigabitEthernet 1/5 down 1001 Unknown Unknown copper\n" +
            "GigabitEthernet 1/6 up 5 Full 100M copper\n" +
            "GigabitEthernet 1/7 down 2010 Full 100M fiber\n" +
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
    private static String returnValueResults = "Current diagnostic parameters[AP:Average Power]:\r\n" +
            "Temp(Celsius)   Voltage(V)      Bias(mA)            RX power(dBm)       TX power(dBm)\r\n" +
            "37(OK)          3.36(OK)        15.91(OK)           -5.96(OK)[AP]       -6.04(OK)";
    /*private static String returnValueResults = "Current Rx Power(dBM)                 :-11.87\r\n" +
            "            Default Rx Power High Threshold(dBM)  :-2.00\r\n" +
            "            Default Rx Power Low  Threshold(dBM)  :-23.98\r\n" +
            "            Current Tx Power(dBM)                 :-2.80\r\n" +
            "            Default Tx Power High Threshold(dBM)  :1.00\r\n" +
            "            Default Tx Power Low  Threshold(dBM)  :-6.00";*/
    /*private static String returnValueResults = "Port BW: 1G, Transceiver max BW: 1G, Transceiver Mode: SingleMode\r\n" +
            "WaveLength: 1310nm, Transmission Distance: 10km\r\n" +
            "Rx Power:  -6.0dBm, Warning range: [-16.989,  -5.999]dBm\r\n" +
            "Tx Power:  -6.20dBm, Warning range: [-9.500,  -2.999]dBm";*/
}
enum LuminousAttenuationL_listEnum{

    keyword, /* 占位符关键词*/
    RX_Power, /* 单占位符获取 RX_Power 总包*/
    TX_Power; /* 单占位符获取 TX_Power 总包*/

    static LuminousAttenuationL_listEnum keyword(){
        return keyword;
    }

    static LuminousAttenuationL_listEnum RX_Power(){
        return RX_Power;
    }

    static LuminousAttenuationL_listEnum TX_Power(){
        return TX_Power;
    }
}