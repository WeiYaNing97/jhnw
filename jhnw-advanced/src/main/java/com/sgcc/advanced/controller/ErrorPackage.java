package com.sgcc.advanced.controller;
import com.sgcc.advanced.domain.ErrorRate;
import com.sgcc.advanced.domain.ErrorRateCommand;
import com.sgcc.advanced.service.IErrorRateCommandService;
import com.sgcc.advanced.service.IErrorRateService;
import com.sgcc.advanced.utils.DataExtraction;
import com.sgcc.advanced.utils.ObtainPreciseEntityClasses;
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

import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "错误包功能管理")
@RestController
@RequestMapping("/advanced/ErrorPackage")
@Transactional(rollbackFor = Exception.class)
public class ErrorPackage {
    @Autowired
    private IErrorRateService errorRateService;
    @Autowired
    private IErrorRateCommandService errorRateCommandService;


    /**
     * 获取错误包信息
     *
     * @param switchParameters 交换机参数对象
     * @return AjaxResult 包含错误包信息的AjaxResult对象
     */
    public AjaxResult getErrorPackage(SwitchParameters switchParameters) {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return AjaxResult.success("操作成功", "线程已终止扫描");
        }


        /**
         * 1：获取四项基本最详细的数据
         */
        AjaxResult errorRateCommandPojo = getErrorRateCommandPojo(switchParameters);
        // 检查线程中断标志
        if (errorRateCommandPojo == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return AjaxResult.success("操作成功", "线程已终止扫描");
        } else if (!errorRateCommandPojo.get("msg").equals("操作成功")){
            return errorRateCommandPojo;
        }
        ErrorRateCommand errorRateCommand = (ErrorRateCommand) errorRateCommandPojo.get("data");


        /**
         * 2：获取端口列表
         */
        AjaxResult AjaxResultPort = getPort(switchParameters, errorRateCommand);
        // 检查线程中断标志
        if (AjaxResultPort == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return AjaxResult.success("操作成功", "线程已终止扫描");
        } else if (!AjaxResultPort.get("msg").equals("操作成功")){
            return AjaxResultPort;
        }
        List<String> portList = (List<String>) AjaxResultPort.get("data");


        /**
         * 3：获取错误包参数集合
         */
        AjaxResult AjaxResultErrorPackageParameter = getErrorPackageParameters(switchParameters, errorRateCommand, portList);
        // 检查线程中断标志
        if (AjaxResultErrorPackageParameter == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return AjaxResult.success("操作成功", "线程已终止扫描");
        } else if (!AjaxResultErrorPackageParameter.get("msg").equals("操作成功")){
            return AjaxResultErrorPackageParameter;
        }
        HashMap<String, Object> errorPackageParameters = (HashMap<String, Object>) AjaxResultErrorPackageParameter.get("data");


        /**
         * 4：获取扫描结果
         */
        AjaxResult ajaxResult = obtainScanningResults(switchParameters, portList, errorPackageParameters);
        // 检查线程中断标志
        if (ajaxResult == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return AjaxResult.success("操作成功", "线程已终止扫描");
        }
        return ajaxResult;
    }



    /**
     * 获取判断结果
     *
     * @param switchParameters 交换机参数对象
     * @param errorRateMap     数据库错误率映射表 key：端口哈  value：错误率对象
     * @param errorPackageParameters 错误包参数映射表
     * @param port             交换机端口号
     * @param switchID         交换机ID
     * @return 封装了判断结果的HashMap
     */
    public HashMap<String,String> obtainJudgmentResult(SwitchParameters switchParameters,
                                                       Map<String,ErrorRate> errorRateMap,
                                                       HashMap<String, Object> errorPackageParameters,
                                                       String port,
                                                       Long switchID) {

        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getLoginUser().getUsername())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }


        /** 当前扫描数据*/
        ErrorRate errorRate = new ErrorRate();
        errorRate.setSwitchIp(switchParameters.getIp()); /*IP*/
        errorRate.setSwitchId(switchID); /*交换机基本信息ID*/
        errorRate.setPort(port); /*交换机端口号*/
        /* 获取该端口号的 错误包参数 */
        Map<String,String> errorPackageValue = (Map<String,String>) errorPackageParameters.get(port);
        Set<String> strings = errorPackageValue.keySet();
        for (String key:strings){
            if (key.toLowerCase().equals("Input".toLowerCase()) ){
                //MyUtils.StringTruncationMatcherValue(error).equals("")?"0":MyUtils.StringTruncationMatcherValue(error)
                /*去除原因 前面方法已经将非纯数字、因素去除了*/
                errorRate.setInputErrors(MyUtils.StringTruncationMatcherValue(errorPackageValue.get(key)));
            }
            if (key.toLowerCase().equals("Output".toLowerCase())){
                errorRate.setOutputErrors(MyUtils.StringTruncationMatcherValue(errorPackageValue.get(key)));
            }
            if (key.toLowerCase().equals("CRC".toLowerCase())){
                errorRate.setCrc(MyUtils.StringTruncationMatcherValue(errorPackageValue.get(key)));
            }
            if (key.toLowerCase().equals("Description".toLowerCase())){
                errorRate.setDescription(errorPackageValue.get(key));
            }
        }
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("ProblemName","错误包");
        String subversionNumber1 = switchParameters.getSubversionNumber();
        if (subversionNumber1!=null){
            subversionNumber1 = "、"+subversionNumber1;
        }
        AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "错误包",
                "系统信息:" +
                        "IP地址为:("+switchParameters.getIp()+"),"+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber1+","+
                        errorRate.toJson()+"\r\n");
        /** 数据库数据*/
        ErrorRate primaryErrorRate = errorRateMap.get(port);
        if (primaryErrorRate == null){
            /* 数据库中没有历史数据 可以直接插入 */
            errorRate.setLink("UP");
            errorRateService.insertErrorRate(errorRate);
            hashMap.put("IfQuestion","无问题");
            //continue;
        }else {
            /*num 判断 两次扫描数值是否一致*/
            int num = 0;
            if ((primaryErrorRate.getInputErrors() !=null && errorRate.getInputErrors() !=null) &&
                    (!primaryErrorRate.getInputErrors().equals(errorRate.getInputErrors()))){
                num++;
            }else if ((primaryErrorRate.getInputErrors() !=null && errorRate.getInputErrors() ==null)
                    || (primaryErrorRate.getInputErrors() ==null && errorRate.getInputErrors() !=null)){
                /*原数据 或者 新数据 一项为空 另一项不为空*/
                num++;
            }
            if ((primaryErrorRate.getOutputErrors() !=null && errorRate.getOutputErrors() !=null) &&
                    (!primaryErrorRate.getOutputErrors().equals(errorRate.getOutputErrors()))){
                num++;
            }else if((primaryErrorRate.getOutputErrors() ==null && errorRate.getOutputErrors() !=null)
                    || (primaryErrorRate.getOutputErrors() !=null && errorRate.getOutputErrors() ==null)){
                num++;
            }
            if ((primaryErrorRate.getCrc() !=null && errorRate.getCrc() !=null) &&
                    (!primaryErrorRate.getCrc().equals(errorRate.getCrc()))){
                num++;
            }else if((primaryErrorRate.getCrc() ==null && errorRate.getCrc() !=null)
                    || (primaryErrorRate.getCrc() !=null && errorRate.getCrc() ==null)){
                num++;
            }
            /*如果 num 不为0 则前后数据不一致 则有问题*/
            if (num>0){
                errorRate.setId(primaryErrorRate.getId());
                /* 判断数据库数据 是 否为 断开状态*/
                if ( primaryErrorRate.getLink().equals("DOWN")){
                    String subversionNumber = switchParameters.getSubversionNumber();
                    if (subversionNumber!=null){
                        subversionNumber = "、"+subversionNumber;
                    }
                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "错误包",
                            "系统信息:" +
                                    "IP地址为:"+switchParameters.getIp()+","+
                                    "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                    "问题为:错误包功能端口号:"+ port + "恢复连接,出现正负超限告警，提示重置基准数据\r\n");
                    errorRate.setLink("UP");
                }
                int i = errorRateService.updateErrorRate(errorRate);
                hashMap.put("IfQuestion","有问题");
            }else if (num == 0){
                errorRate.setId(primaryErrorRate.getId());
                /*判断数据库数据是否为 断开状态*/
                if ( primaryErrorRate.getLink().equals("DOWN")){
                    String subversionNumber = switchParameters.getSubversionNumber();
                    if (subversionNumber!=null){
                        subversionNumber = "、"+subversionNumber;
                    }
                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "错误包",
                            "系统信息:" +
                                    "IP地址为:"+switchParameters.getIp()+","+
                                    "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                    "问题为:错误包功能端口号:"+ port + "恢复连接\r\n");
                    errorRate.setLink("UP");
                }
                int i = errorRateService.updateErrorRate(errorRate);
                hashMap.put("IfQuestion","无问题");
                //continue;
            }

                /*
                判断误码率百分比是否满足要求
                误码率百分比 检验错误*/
            boolean Percentage = judgingPercentage(errorPackageValue);
            if (!Percentage){
                hashMap.put("IfQuestion","有问题");
            }

        }
        /*自定义分隔符*/
        String customDelimiter = null;
        Object customDelimiterObject = CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());
        if (customDelimiterObject instanceof String){
            customDelimiter = (String) customDelimiterObject;
        }

        // =:= 是自定义分割符
        String portNumber = "端口号"+customDelimiter+"是"+customDelimiter+port+" "+ errorRate.getDescription() + customDelimiter;
        String InputErrors = "";
        String OutputErrors = "";
        String Crc = "";

        /** 如果数据库相关数据为空 则可以直接赋值插入*/
        if (primaryErrorRate != null){
            InputErrors = primaryErrorRate.getInputErrors()!=null?"input"+customDelimiter+"是"+customDelimiter+"input原:"+primaryErrorRate.getInputErrors()+",input现:"+errorRate.getInputErrors()+customDelimiter
                    :"input"+ customDelimiter +"是"+ customDelimiter + errorRate.getInputErrors() + customDelimiter;
            OutputErrors = primaryErrorRate.getOutputErrors()!=null?"output"+customDelimiter+"是"+customDelimiter+"output原:"+primaryErrorRate.getOutputErrors()+",output现:"+errorRate.getOutputErrors()+customDelimiter
                    :"output"+ customDelimiter +"是"+ customDelimiter + errorRate.getOutputErrors() + customDelimiter;
            Crc = primaryErrorRate.getCrc()!=null?"crc"+customDelimiter+"是"+customDelimiter+"crc原:"+primaryErrorRate.getCrc()+",crc现:"+errorRate.getCrc()
                    :"crc"+customDelimiter+"是" + customDelimiter +errorRate.getCrc();
        }else {
            InputErrors = "input"+ customDelimiter +"是"+ customDelimiter + errorRate.getInputErrors() + customDelimiter;
            OutputErrors = "output"+ customDelimiter +"是"+ customDelimiter + errorRate.getOutputErrors() + customDelimiter;
            Crc = "crc"+customDelimiter+"是" + customDelimiter +errorRate.getCrc();
        }

        String parameterString = portNumber +" "+ InputErrors+" "+OutputErrors+" "+Crc;
        if (parameterString.endsWith( customDelimiter )){
            parameterString = parameterString.substring(0,parameterString.length()-3);
        }
        hashMap.put("parameterString",parameterString);

        return hashMap;
    }


    /**
     * 获取扫描结果
     *
     * @param switchParameters 交换机参数对象
     * @param portList 端口列表
     * @param errorPackageParameters 错误包参数集合
     * @return AjaxResult对象，包含操作结果
     */
    public AjaxResult obtainScanningResults(SwitchParameters switchParameters , List<String> portList,HashMap<String, Object> errorPackageParameters) {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }


        ErrorRate selectpojo = new ErrorRate();
        selectpojo.setSwitchIp(switchParameters.getIp());
        //解决多线程 service 为null问题
        //查询数据库错误包列表
        errorRateService = SpringBeanUtil.getBean(IErrorRateService.class);
        List<ErrorRate> list = errorRateService.selectErrorRateList(selectpojo);
        Map<String,ErrorRate> errorRateMap = new HashMap<>();
        for (ErrorRate pojo:list){
            errorRateMap.put( pojo.getPort() , pojo );
        }
        List<String> keySet = errorRateMap.keySet().stream().collect(Collectors.toList());

        /**
         * 查看字符串集合A中存在，但字符串集合B中不存在的部分
         * 查看数据表中有，但是扫描后没有的端口（数据表中独有的端口号）
         */
        List<String> difference = MyUtils.findDifference(keySet,portList);

        /*获取交换机四项基本信息ID*/
        Long switchID = FunctionalMethods.getSwitchParametersId(switchParameters);
        // 检查线程中断标志
        if (switchID == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }


        for (String port:difference){
            // 修改端口号状态
            ErrorRate errorRate = errorRateMap.get(port);
            errorRate.setLink("DOWN");
            int i = errorRateService.updateErrorRate(errorRate);

            if (i > 0 ){
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "错误包",
                        "系统信息:" +
                                "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:错误包功能端口号:"+ port +"断开连接，端口状态为DOWN\r\n");
            }else {
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "错误包",
                        "系统信息:" +
                                "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:错误包功能端口号:"+ port +"修改状态为DOWN失败\r\n");
                return AjaxResult.error();
            }
        }

        // 获取错误包参数集合中的Key
        for (String port:portList){
            //获取判断结果
            HashMap<String, String> hashMap = obtainJudgmentResult(switchParameters,
                    errorRateMap,
                    errorPackageParameters,
                    port,
                    switchID);
            // 检查线程中断标志
            if (hashMap == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
                // 如果线程中断标志为true，则直接返回
                return null;
            }

            SwitchScanResultController switchScanResultController = new SwitchScanResultController();
            Long insertId = switchScanResultController.insertSwitchScanResult(switchParameters, hashMap);
            SwitchIssueEcho switchIssueEcho = new SwitchIssueEcho();
            switchIssueEcho.getSwitchScanResultListByData(switchParameters.getLoginUser().getUsername(),insertId);
        }

        return AjaxResult.success();
    }



    /**
     * 根据交换机参数、错误率命令和端口列表获取错误包参数
     *
     * @param switchParameters 交换机参数对象
     * @param errorRateCommand 错误率命令对象
     * @param portList 端口列表
     * @return AjaxResult 封装了错误包参数的AjaxResult对象
     */
    public AjaxResult getErrorPackageParameters(SwitchParameters switchParameters,ErrorRateCommand errorRateCommand,List<String> portList) {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }

        /**
         * 1：获取错误包参数命令*/
        String errorPackageCommand = errorRateCommand.getGetParameterCommand();
        /*获取到错误包参数 map集合*/
        HashMap<String, Object> errorPackageParameters = getErrorPackageParameters(switchParameters, portList, errorPackageCommand);
        // 检查线程中断标志
        if (errorPackageParameters == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }
        if ( errorPackageParameters.size() == 0 ){
            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }
            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题为:错误包功能所有UP状态端口皆未获取到错误包参数\r\n");
            return AjaxResult.error("所有端口都没有获取到错误包参数");
        }
        return AjaxResult.success(errorPackageParameters);
    }

    /**
     * 根据交换机参数和错误率命令获取端口号
     *
     * @param switchParameters 交换机参数对象
     * @param errorRateCommand 错误率命令对象
     * @return AjaxResult 封装了端口号列表的AjaxResult对象
     */
    public AjaxResult getPort(SwitchParameters switchParameters,ErrorRateCommand errorRateCommand) {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }

        /**
         * 1:获取up端口号命令，执行交换机命令，返回交换机返回信息
         *  如果交换机返回信息为空 则 命令错误，交换机返回错误信息
         * */
        String portNumberCommand = errorRateCommand.getGetPortCommand();
        /*配置文件错误包问题的命令 不为空时，执行交换机命令，返回交换机返回信息*/
        ExecuteCommand executeCommand = new ExecuteCommand();
        List<String> return_information_List = executeCommand.executeScanCommandByCommand(switchParameters, portNumberCommand);
        // 检查线程中断标志
        if (return_information_List == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }
        // todo 错误包功能获取端口号，虚拟数据
        return_information_List = StringBufferUtils.stringBufferSplit(
                StringBufferUtils.arrange(new StringBuffer(this.switchPortReturnsResult)),
                "\r\n");
        if (MyUtils.isCollectionEmpty(return_information_List)) {
            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }
            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:" +
                            "IP地址为:"+switchParameters.getIp()+"。"+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+"。"+
                            "问题为:错误包功能获取端口号命令错误,需要重新定义。\r\n");
            return AjaxResult.error("IP地址为:"+switchParameters.getIp()+","+"问题为:错误包功能获取端口号命令错误,需要重新定义\r\n");
        }




        /**2：根据交换机返回信息获取错误包端口号*/
        List<String> portList = ObtainUPStatusPortNumber(switchParameters,return_information_List);
        // 检查线程中断标志
        if (portList == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }
        /*获取光衰端口号方法返回集合判断是否为空，说明没有端口号为开启状态 UP*/
        if (MyUtils.isCollectionEmpty(portList)){
            //关于没有端口号为UP状态 的错误代码库
            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }
            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题为:错误包功能无UP状态端口号,是否需要CRT检查异常\r\n"
            );
            return AjaxResult.error("IP地址为:"+switchParameters.getIp()+","+"问题为:错误包功能无UP状态端口号,是否需要CRT检查异常\r\n");

        }




        /**
         * 3：如果交换机端口号为开启状态 UP 不为空 则需要查看是否需要转义：
         * GE转译为GigabitEthernet  才能执行获取交换机端口号光衰参数命令*/
        String conversion = errorRateCommand.getConversion();
        if (conversion != null){
            String[] conversionSplit = conversion.split(";");
            for (String convers:conversionSplit){
                /* 转译 分割为 字符串数组*/
                String[] conversSplit = convers.split(":");
                for (int num=0;num<portList.size();num++){
                    /* getFirstLetters 获取字符串开头字母部分
                     * 判断 是否与转译相同
                     * 如果相同 则 进行转译  */
                    if (MyUtils.getFirstLetters(portList.get(num)).trim().equals(conversSplit[0])){
                        portList.set(num,portList.get(num).replace(conversSplit[0],conversSplit[1]));
                    }
                }
            }
        }

        return AjaxResult.success(portList);
    }


    /**
     * 根据交换机参数获取错误率命令对象
     *
     * @param switchParameters 交换机参数对象
     * @return ErrorRateCommand 错误率命令对象
     */
    public static ErrorRateCommand getErrorRateCommand(SwitchParameters switchParameters) {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }
        // 创建一个ErrorRateCommand对象
        ErrorRateCommand errorRateCommand = new ErrorRateCommand();
        // 设置设备品牌
        errorRateCommand.setBrand(switchParameters.getDeviceBrand());
        // 设置设备型号
        errorRateCommand.setSwitchType(switchParameters.getDeviceModel());
        // 设置固件版本
        errorRateCommand.setFirewareVersion(switchParameters.getFirmwareVersion());
        // 设置子版本号
        errorRateCommand.setSubVersion(switchParameters.getSubversionNumber());
        // 返回ErrorRateCommand对象
        return errorRateCommand;
    }

    /**
     * 根据交换机参数获取错误率命令对象
     *
     * @param switchParameters 交换机参数对象
     * @return AjaxResult 封装了错误率命令对象的AjaxResult对象
     */
    public  AjaxResult getErrorRateCommandPojo(SwitchParameters switchParameters) {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }


        /**
         * 1:获取错误率命令对象
          */
        ErrorRateCommand errorRateCommand = getErrorRateCommand(switchParameters);
        // 检查线程中断标志
        if (errorRateCommand ==null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }

        /**
         * 2:获取错误率命令服务对象
         *     根据错误率命令对象查询错误率命令列表，
         *     如果为空则写入日志并返回错误结果，
         *     否则筛选错误率命令对象
          */
        errorRateCommandService = SpringBeanUtil.getBean(IErrorRateCommandService.class);
        // 根据错误率命令对象查询错误率命令列表
        List<ErrorRateCommand> errorRateCommandList = errorRateCommandService.selectErrorRateCommandListBySQL(errorRateCommand);
        //当配置文件错误包问题的命令为空时进行日志写入
        if (MyUtils.isCollectionEmpty(errorRateCommandList)){
            // 获取子版本号
            String subversionNumber = switchParameters.getSubversionNumber();
            // 如果子版本号不为空，则在前面添加顿号
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }
            // 写入异常日志
            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:IP地址为:"+switchParameters.getIp()+"。"+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+"。"+
                            "问题为:错误包功能未定义获取端口号的命令。\r\n"
            );
            // 返回错误结果
            return AjaxResult.error("IP地址为:"+switchParameters.getIp()+","+"问题为:错误包功能未定义获取端口号的命令\r\n");
        }

        /**
         * 3:筛选错误率命令对象
         *  从errorRateCommandList中获取四项基本最详细的数据
         */
        // 此处注释掉的代码是原来的方法，后通过泛型和反射的方式进行了优化，整合了几个高级功能的筛选方法，此处注释掉的代码不再使用。
        // ErrorRateCommand errorRateCommandPojo = ScreeningMethod.ObtainPreciseEntityClassesErrorRateCommand(switchParameters,errorRateCommandList);

        ObtainPreciseEntityClasses genericTest = new ObtainPreciseEntityClasses();
        ErrorRateCommand errorRateCommandPojo = (ErrorRateCommand) genericTest.genericObtainsExactEntityClasses(switchParameters, errorRateCommandList);

        // 检查线程中断标志
        if (errorRateCommandPojo ==null){
            // 如果线程中断标志为true，则直接返回
            return null;
        }
        // 返回成功结果，并包含筛选后的错误率命令对象
        return AjaxResult.success(errorRateCommandPojo);
    }



    /**
     * 判断光衰百分比是否满足要求
     *
     * @param errorPackageValue 包含数据包值的Map
     * @return 如果光衰百分比满足要求则返回true，否则返回false
     */
    public static boolean judgingPercentage(Map<String,String> errorPackageValue) {
        String InputPKT = null;
        String OutputPKT = null;

        if (errorPackageValue.get(ErrorPackageL_listEnum.singlePlaceholderInput().toString())!=null
        || errorPackageValue.get(ErrorPackageL_listEnum.singlePlaceholderOutput().toString())!=null
        || errorPackageValue.get(ErrorPackageL_listEnum.multiplePlaceholdersInput().toString())!=null
        || errorPackageValue.get(ErrorPackageL_listEnum.multiplePlaceholdersOutput().toString())!=null){

            if (errorPackageValue.get(ErrorPackageL_listEnum.singlePlaceholderInput().toString())!=null){
                InputPKT = errorPackageValue.get(ErrorPackageL_listEnum.singlePlaceholderInput().toString());

            }
            if (errorPackageValue.get(ErrorPackageL_listEnum.multiplePlaceholdersInput().toString())!=null){
                InputPKT = errorPackageValue.get(ErrorPackageL_listEnum.multiplePlaceholdersInput().toString());

            }
            if (errorPackageValue.get(ErrorPackageL_listEnum.singlePlaceholderOutput().toString())!=null){
                OutputPKT = errorPackageValue.get(ErrorPackageL_listEnum.singlePlaceholderOutput().toString());

            }
            if (errorPackageValue.get(ErrorPackageL_listEnum.multiplePlaceholdersOutput().toString())!=null){
                OutputPKT = errorPackageValue.get(ErrorPackageL_listEnum.multiplePlaceholdersOutput().toString());
            }

        }

        /* 获取光衰百分数 如果没有相关设定 默认无问题 返回 true*/
        String percentageString = null;
        Object percentageObject = CustomConfigurationUtil.getValue("错误包.percentage", Constant.getProfileInformation());
        if (percentageObject instanceof String){
            percentageString = (String) percentageObject;
        }
        if (percentageString == null){
            return true;
        }

        /* 字符串截取double值 */
        List<Double> doubles = MyUtils.StringTruncationDoubleValue(percentageString);
        if (doubles.size() == 1){
            double percentagedouble = doubles.get(0) * 0.01;
            if (InputPKT!=null){
                if (MyUtils.stringToDouble(errorPackageValue.get("Input"))/MyUtils.stringToDouble(InputPKT) < percentagedouble){

                }else {
                    return false;
                }
            }
            if (OutputPKT!=null){
                if (MyUtils.stringToDouble(errorPackageValue.get("Input"))/MyUtils.stringToDouble(OutputPKT) < percentagedouble){

                }else {
                    return false;
                }
            }
        }

        return true;
    }

    /*发送命令 错误包数量信息*/
    /**
     * 获取错误包参数
     *
     * @param switchParameters 包含交换机相关参数的SwitchParameters对象
     * @param portNumber       包含交换机端口号的列表
     * @param errorPackageCommand 获取错误包参数的命令
     * @return 包含错误包参数的HashMap对象，其中key为端口号，value为包含错误包参数的列表
     * @throws Exception 执行命令或处理结果时发生的异常
     *
     * @author charles
     * @createTime 2023/12/19 21:41
     */
    public HashMap<String,Object> getErrorPackageParameters(SwitchParameters switchParameters,List<String> portNumber,String errorPackageCommand) {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }

        // 创建返回对象 HashMap
        HashMap<String,Object> hashMap = new HashMap<>();
        ExecuteCommand executeCommand = new ExecuteCommand();
        /**
         * 1: 遍历端口号集合，检测各端口号的错误包参数
         *    替换端口号，得到完整的获取端口号错误包参数命令
         *    执行交换机命令，并返回结果
         */
        for (String port:portNumber){
            //替换端口号，得到完整的获取错误包参数命令;
            String FullCommand = errorPackageCommand.replaceAll("端口号",port);
            List<String> returnResults_List = executeCommand.executeScanCommandByCommand(switchParameters, FullCommand);
            // 检查线程中断标志
            if (returnResults_List == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
                // 如果线程中断标志为true，则直接返回
                return null;
            }
            //todo 错误包虚拟数据
            returnResults_List = StringBufferUtils.stringBufferSplit(
                    StringBufferUtils.arrange(new StringBuffer(this.switchPortValueReturnsResult)),
                    "\r\n");
            if (MyUtils.isCollectionEmpty(returnResults_List)) {
                // 如果结果为空，则进行异常处理
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                // 记录异常日志
                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                        "异常:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        "问题为:错误包功能"+port+"端口获取错误包参数命令错误,请重新定义\r\n");
                // 继续处理下一个端口号
                continue;
            }



            /**
             * 2:查看交换机错误包数量
             */
            Map<String, String> parameters = getParameters(switchParameters,returnResults_List, port);
            // 检查线程中断标志
            if (parameters == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
                // 如果线程中断标志为true，则直接返回
                return null;
            }
            if (parameters.size() == 0){
                // 如果获取到的错误包数量为空，则进行异常处理
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                // 记录异常日志
                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                        "异常:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        "问题为:错误包功能"+port+"端口未获取到错误包\r\n");
                // 继续处理下一个端口号
                continue;
            }else {
                // 获取描述信息 描述：Description:*/
                String description = getDescription(returnResults_List);
                if (description!=null){
                    parameters.put("Description" , description);
                }
                // 将获取到的错误包参数添加到结果HashMap中
                hashMap.put(port,parameters);
            }

        }
        // 返回结果HashMap
        return hashMap;
    }

    /**
     * 根据交换机返回信息获取错误包端口号
     *
     * @param return_information_List 交换机返回的信息字符串
     * @return 包含错误包端口号的字符串列表
     * @author charles
     * @createTime 2023/12/19 21:36
     */
    public List<String> ObtainUPStatusPortNumber(SwitchParameters switchParameters,
                                                 List<String> return_information_List) {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }

        /**
         * 1:遍历交换机行信息字符串数组，筛选UP状态的字符串
         */
        List<String> strings = new ArrayList<>();
        for (String string:return_information_List){
            // 判断是否包含 " UP "
            if ((string.toUpperCase().indexOf(" UP ")!=-1)){
                // 去除字符串两边的空格，并存入端口待取集合
                strings.add(string.trim());
            }
        }
        /**
         * 2：遍历端口待取集合，执行取值方法获取端口号
          */
        List<String> port = new ArrayList<>();
        for (String information:strings){
            // 根据 UP 截取端口号，并去除带"."的子端口
            String terminalSlogan = FunctionalMethods.getTerminalSlogan(information);
            if (terminalSlogan != null){
                // 将端口号添加到结果列表中
                port.add(terminalSlogan);
            }
        }
        return port;
    }

    /**
     * 查看交换机错误包数量
     *
     * @param switchParameters 包含交换机相关参数的SwitchParameters对象
     * @param returnResults     交换机返回的结果信息
     * @param port              需要查询的端口号
     * @return 交换机错误包数量参数的列表，列表中的元素格式为"参数名:参数值"
     */
    public Map<String,String> getParameters(SwitchParameters switchParameters,List<String> returnResults_List,String port) {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }

        /**
         * 1: 根据四项基本信息 查询获取光衰参数的关键词
         */
        Map<String, Object> deviceVersion = FunctionalMethods.getKeywords(switchParameters,"错误包");
        // 检查线程中断标志
        if (deviceVersion == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        } else if (deviceVersion.size() == 0){
            return new HashMap<>();
        }

        /**
         * 2: 根据关键词获取交换机错误包数量参数的Key名列表
         */
        Set<String> strings = deviceVersion.keySet();
        /*key ：value
        *   Input: $ input errors
            Output: $ output errors
            CRC: $ CRC,
        */
        /* 获取三个参数的关键词 并存储再 hashMap 集合中*/
        HashMap<String,String> hashMap =new HashMap<>();
        HashMap<String,Object> pktMap =new HashMap<>();
        for (String key:strings){
            switch (key.toLowerCase()){
                case "input":
                    String input = (String) deviceVersion.get(key);
                    hashMap.put("Input",input);
                    break;
                case "output":
                    String output = (String) deviceVersion.get(key);
                    hashMap.put("Output",output);
                    break;
                case "crc":
                    String crc = (String) deviceVersion.get(key);
                    hashMap.put("CRC",crc);
                    break;
                case "inputpkt":
                    pktMap.put(ErrorPackageL_listEnum.singlePlaceholderInput().toString(),deviceVersion.get(key));
                    break;
                case "outputpkt":
                    pktMap.put(ErrorPackageL_listEnum.singlePlaceholderOutput().toString(),deviceVersion.get(key));
                    break;
            }
        }

        /* 获取 hashMap 的 key值 */
        Set<String> keySet = hashMap.keySet();

        /* 遍历 hashMap 的 key值
        * 获取关键词是否包含 Total Error
        * 如果存在则通过 Total Error 先获取到 ： Input 和 Output*/
        HashMap<String, String> valueTotalError = new HashMap<>();

        for (String key:keySet){
            String mapvalue = hashMap.get(key);
            int num = mapvalue.indexOf("\\n");
            if ( num != -1){
                List<String> value = getValueWrap(returnResults_List,mapvalue, num);
                if (value.size() == 1){
                    valueTotalError.put(key,value.get(0));
                }else {
                    /* todo 根据占位符 获取到了多条数据 获取参数失败 */
                    AbnormalAlarmInformationMethod.afferent(null,
                            switchParameters.getLoginUser().getUsername(),
                            "错误代码",
                            "OPSA0005");
                }
            }
        }


        /*总包数*/
        if (pktMap.keySet().size() != 0){
            Object inputpkt = pktMap.get(ErrorPackageL_listEnum.singlePlaceholderInput().toString());
            Object outputpkt = pktMap.get(ErrorPackageL_listEnum.singlePlaceholderOutput().toString());
            if(inputpkt != null){
                if (inputpkt instanceof Map){
                    Map<String,Object> InputPKT =(Map<String,Object>) inputpkt;
                    String keyword = (String) InputPKT.get(ErrorPackageL_listEnum.key().toString());
                    hashMap.put(ErrorPackageL_listEnum.singlePlaceholderInput().toString(),keyword);
                    String KPT = (String) InputPKT.get(ErrorPackageL_listEnum.multiplePlaceholdersInput().toString());
                    hashMap.put(ErrorPackageL_listEnum.multiplePlaceholdersInput().toString(),KPT);
                }else if (inputpkt instanceof String){
                    String InputPKT =(String) inputpkt;
                    hashMap.put(ErrorPackageL_listEnum.singlePlaceholderInput().toString(),InputPKT);
                }
            }

            if(outputpkt != null){
                if (outputpkt instanceof Map){
                    Map<String,Object> OutputPKT =(Map<String,Object>) outputpkt;
                    String keyword = (String) OutputPKT.get(ErrorPackageL_listEnum.key().toString());
                    hashMap.put(ErrorPackageL_listEnum.singlePlaceholderOutput().toString(),keyword);
                    String KPT = (String) OutputPKT.get(ErrorPackageL_listEnum.multiplePlaceholdersOutput().toString());
                    hashMap.put(ErrorPackageL_listEnum.multiplePlaceholdersOutput().toString(),KPT);
                }else if (outputpkt instanceof String){
                    String OutputPKT =(String) outputpkt;
                    hashMap.put(ErrorPackageL_listEnum.singlePlaceholderOutput().toString(),OutputPKT);
                }
            }
        }



        /*遍历 hashMap 的 key值  获取对应的参数值*/
        for (String key:keySet){

            int position = hashMap.get(key).indexOf("\\n");
            /*key值 的 value值是否包含 "Total Error" */
            if (position != -1){

                String substring = hashMap.get(key).substring(position+2).trim();
                String lineString = substring.split(" ")[0];
                hashMap.put(key,substring.substring(lineString.length()));

                /*如果根据 "Total Error" 获取行信息为 空 则取下一参数*/
                if (valueTotalError.size()==0){
                    continue;
                }

                /*根据 key 获取关键词 */
                String value = valueTotalError.get(key);
                if (value == null){
                    continue;
                }

                /*根据配置文件的取值信息 取参数值*/
                Map<String,String> placeholdersContainingList = DataExtraction.getTheMeaningOfPlaceholders(value, hashMap.get(key),switchParameters);
                // 检查线程中断标志
                if (placeholdersContainingList == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
                    // 如果线程中断标志为true，则直接返回
                    return null;
                }else if (placeholdersContainingList.size() == 1){/* 如果获取到的是一个参数 则可以直接赋值 */
                    String words = hashMap.get(key);
                    List<String> placeholders = DataExtraction.getPlaceholders(words);
                    if (placeholders.size() == 1){
                        List<Integer> integers = MyUtils.extractInts(placeholdersContainingList.get(placeholders.get(0)));
                        if (integers.size() == 1){
                            hashMap.put(key,integers.get(0)+"");
                        }
                    }
                }else {
                    /* 多个占位符 获取到了多个参数
                    * 遍历占位符 */
                    for (String getkey_ymlvalue:placeholdersContainingList.keySet()){
                        /* 获取配置文件 占位符与含义的map数据 */
                        Map<String, String> stringStringMap = (Map<String, String>) deviceVersion.get(key);
                        /* 遍历配置文件 占位符与含义的map数据
                        * 与占位符做对比 获取对应占位符含义
                        * 将占位符含义 与占位符参数组合 */
                        for (String ymlkey:stringStringMap.keySet()){
                            if (stringStringMap.get(ymlkey).equals(getkey_ymlvalue)){
                                hashMap.put(ymlkey,placeholdersContainingList.get(getkey_ymlvalue));
                            }
                        }
                    }
                }

            }else {

                /* 按行分割 交换机返回信息每行信息 为字符串数组*/
                /*遍历 交换机返回信息行信息 字符串数组*/
                for (String str:returnResults_List){
                    /*根据配置文件的取值信息 取参数值*/
                    Map<String,String> placeholdersContainingList = DataExtraction.getTheMeaningOfPlaceholders(str, hashMap.get(key),switchParameters);
                    // 检查线程中断标志
                    if (placeholdersContainingList == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
                        // 如果线程中断标志为true，则直接返回
                        return null;
                    }else if (placeholdersContainingList.size() == 1){
                        String words = hashMap.get(key);
                        List<String> placeholders = DataExtraction.getPlaceholders(words);
                        if (placeholders.size() == 1){
                            List<Integer> integers = MyUtils.extractInts(placeholdersContainingList.get(placeholders.get(0)));
                            if (integers.size() == 1){
                                hashMap.put(key,integers.get(0)+"");
                                break;
                            }
                        }
                    }else {
                        /* 多个占位符 获取到了多个参数
                         * 遍历占位符 */
                        for (String getkey_ymlvalue:placeholdersContainingList.keySet()){
                            /* 获取配置文件 占位符与含义的map数据 */
                            Map<String, String> stringStringMap = (Map<String, String>) deviceVersion.get(key);
                            /* 遍历配置文件 占位符与含义的map数据
                             * 与占位符做对比 获取对应占位符含义
                             * 将占位符含义 与占位符参数组合 */
                            for (String ymlkey:stringStringMap.keySet()){
                                if (stringStringMap.get(ymlkey).equals(getkey_ymlvalue)){
                                    hashMap.put(ymlkey,placeholdersContainingList.get(getkey_ymlvalue));
                                }
                            }
                        }
                    }

                }
            }
        }

        Map<String,String> stringMap = new HashMap<>();

        for (String key:keySet){
            if (MyUtils.determineWhetherAStringIsAPureNumber(hashMap.get(key))){
                stringMap.put(key , hashMap.get(key));
            }else {
                /*  端口未获取到错误包 */
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }

                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                        "异常:" +
                                "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题为:错误包功能"+port+"端口,"+key+"参数取参失败\r\n");
            }
        }

        return stringMap;
    }


    /**
     * 从信息字符串中按照指定占位符和行数提取包含关键词的行信息
     *
     * @param information 包含信息的字符串
     * @param placeholder 占位符字符串，格式为{行数 占位符内容}，例如{2 KEYWORD}
     * @param num 占位符中行数之前的字符数
     * @return 包含关键词的行信息列表
     */
    public List<String> getValueWrap(List<String> returnResults_List,String placeholder,Integer num) {

        String startString = placeholder.substring(0, num).trim();
        String endString = placeholder.substring(num + 2).trim();
        String lineString = endString.split(" ")[0];
        endString = endString.substring(lineString.length());
        Integer line = Integer.valueOf(lineString).intValue();

        String mark = "";
        String keyword = "";
        int position = 0;
        boolean $_position = startString.contains("$");
        if ($_position) {
            position = -1;
            mark = endString;
            keyword = startString;
        }else {
            $_position = endString.contains("$");
            if ($_position){
                position = 1;
                mark = startString;
                keyword = endString;
            }
        }

        /* 获取配置文件中的关键词 */
        List<String> keywordList = Arrays.stream(keyword.split("\\$")).map(x -> x.trim()).collect(Collectors.toList());


        // 遍历交换机返回信息 行信息
        List<String> valueList = new ArrayList<>();
        for (int number = 0; number<returnResults_List.size(); number++){
            /*//判断一个字符串是否包含另一个字符串(忽略大小写)
            if (MyUtils.containIgnoreCase(informationSplit[number],mark)){
                valueList.add(mark);
            }else {
                continue;
            }
            int i = number + (line * position);
            // 判断主字符串是否包含集合中的所有元素(忽略大小写)
            if (MyUtils.containsAllElements(informationSplit[ i ],keywordList)){
                valueList.add(informationSplit[ i ]);
            }*/

            int i = number + (line * position);
            if (MyUtils.containIgnoreCase(returnResults_List.get(number),mark)
                    && i < returnResults_List.size()
                    && MyUtils.containsAllElements(returnResults_List.get(i),keywordList)){
                valueList.add(returnResults_List.get(i));
            }
        }

        return valueList;

        /*List<String> returnvalue = new ArrayList<>();
        //在给定的字符串列表中查找指定元素的所有位置，并返回位置列表。
        List<Integer> elementPositions = MyUtils.findElementPositions(valueList, mark);
        for (Integer element_position:elementPositions){
            if (element_position+position < valueList.size() && element_position+position >= 0 && !(valueList.get(element_position+position).equals(mark))){
                returnvalue.add(valueList.get(element_position+position));
            }
        }
        return returnvalue;*/
    }

    /**
     * 根据配置文件的取值信息 取参数值  根据分割数组 利用下标取词
     * 逻辑：因为取词为数字 占位符为：$ 。
     * 1：首先将交换机返回信息数字替换为"",将配置文件中的占位符$替换为""
     * 2：如果str1Str 不包含 str2Str 则返回null
     * 3：将配置文件的关键词按空格转换为字符串数组，获取$ 的下标、
     * 4：如果关键词的$位置在开头和结尾，则可以根据去掉$的关键词分割交换机返回信息。然后（$开头：第一元素的最后一位。$结尾：第二元素的第一位。）获取参数
     * 5：关键词根据 $ 分割为数组，按照元素分割交换机返回信息。
     * @param str1
     * @param str2
     * @return
     */
    public String getPlaceholdersContaining(String str1 , String str2) {

        str1 = adjustColon(str1.trim());
        str2 = adjustColon(str2.trim());

        /*1 首先将交换机返回信息数字替换为"",
        将配置文件中的占位符$替换为""*/
        String str1Str = str1.replaceAll("\\d", "");
        String str2Str = str2.trim().replace("$", "");

        /*2 如果 行信息 不包含 关键词 则返回null*/
        if (str1Str.indexOf(str2Str) ==-1){
            return null;
        }

        /*3  占位符位置 将配置文件的关键词按空格转换为字符串数组，获取$ 的下标、*/
        Integer num = null;
        String[] $str2 = str2.split(" ");
        for (int number = 0; number<$str2.length; number++){
            if ($str2[number].equalsIgnoreCase("$")){
                num = number;
                break;
            }
        }

        /*4 如果关键词的$位置在开头和结尾，则可以根据去掉$的关键词分割交换机返回信息。
        然后（$开头：第一元素的最后一位。$结尾：第二元素的第一位。）获取参数
         */
        if (num == 0){
            // $在关键词的开头
            String[] str1Split = str1.split(str2Str.trim());
            // 开头等于二，关键词在中间 $代表的参数会在第一个数组元素中 ["元素一 $","元素二"]
            // 开头等于 一，关键词在结尾 $代表的参数在唯一一个数组元素中 ["元素一 $"]
            /*关键词在中间 或者在结尾*/
            if (str1Split.length==2 || str1Split.length==1){

                String[] parameterArray = str1Split[0].trim().split(" ");
                /*获取数组的最后一个元素*/
                String value = parameterArray[parameterArray.length-1].trim();
                /*去掉结尾的 . 或 ，*/
                value = FunctionalMethods.judgeResultWordSelection(new StringBuffer(value)).toString();
                /*检查元素是否为纯数字*/
                if (MyUtils.determineWhetherAStringIsAPureNumber(value)){
                    if (str1.indexOf(str2.replace("$", value))!=-1){
                        return value;
                    }
                }

            }else {
                /*如果有多个 关键词，则需要遍历获取，直到获取到数字为止 ["元素一 $","元素二","元素三","元素四"]*/
                for (String returnstr1:str1Split){

                    String[] parameterArray = returnstr1.trim().split(" ");
                    /*获取数组的最后一个元素*/
                    String value = parameterArray[parameterArray.length-1].trim();
                    /*去掉结尾的 . 或 ，*/
                    value = FunctionalMethods.judgeResultWordSelection(new StringBuffer(value)).toString();
                    /*检查元素是否为纯数字*/
                    if (MyUtils.determineWhetherAStringIsAPureNumber(value)){
                        if (str1.indexOf(str2.replace("$", value))!=-1){
                            return value;
                        }
                    }

                }
                /*如果未取出，则返回null*/
                return null;
            }
        }else if (num == $str2.length-1){
            // $在关键词的结尾
            String[] str1Split = str1.split(str2Str.trim());
            //结尾等于二，关键词在中间 $代表的参数会在第二个数组元素中   ["元素一","$ 元素二"]
            // 结尾等于 一，关键词在开头 $代表的参数在唯一一个数组元素中  ["$ 元素一"]
            if (str1Split.length==2){
                String[] parameterArray = str1Split[1].trim().split(" ");
                /*获取数组的第一个元素*/
                String value = parameterArray[0].trim();
                value = FunctionalMethods.judgeResultWordSelection(new StringBuffer(value)).toString();
                if (MyUtils.determineWhetherAStringIsAPureNumber(value)){
                    if (str1.indexOf(str2.replace("$", value))!=-1){
                        return value;
                    }
                }
            }else if (str1Split.length==1){
                // ["$ 元素一","$ 元素二","$ 元素三","$ 元素四","$ 元素五"]
                String[] parameterArray = str1Split[0].trim().split(" ");
                /*获取数组的第一个元素*/
                String value = parameterArray[0].trim();
                value = FunctionalMethods.judgeResultWordSelection(new StringBuffer(value)).toString();
                if (MyUtils.determineWhetherAStringIsAPureNumber(value)){
                    if (str1.indexOf(str2.replace("$", value))!=-1){
                        return value;
                    }
                }
            }else {
                /*如果有多个 关键词，则需要遍历获取，直到获取到数字为止*/
                for (String returnstr1:str1Split){
                    String[] parameterArray = returnstr1.trim().split(" ");
                    /*获取数组的第一个元素*/
                    String value = parameterArray[0].trim();
                    /*去掉结尾的 . 或 ，*/
                    value = FunctionalMethods.judgeResultWordSelection(new StringBuffer(value)).toString();
                    /*检查元素是否为纯数字*/
                    if (MyUtils.determineWhetherAStringIsAPureNumber(value)){
                        if (str1.indexOf(str2.replace("$", value))!=-1){
                            return value;
                        }
                    }
                }
                /*如果未取出，则返回null*/
                return null;
            }
        }else {
            /*'Input: $ input errors'*/
            /* 5  关键词根据 $ 分割为数组，按照元素分割交换机返回信息。*/
            String[] str2Split = str2.trim().split(" \\$ ");

            // str2Split 的长度为 2  则证明$在关键词的 中间
            if (str2Split.length==2){

                String[] split = str1.split(str2Split[0]);
                /*第一关键词在中间位置 长度为2*/
                /*第一关键词在开头位置 长度为1*/
                /*此时只有 一个 匹配到第一关键词的可以直接截取第二关键词
                * split.length == 2 是因为
                *
                * 这是因为在Java中，字符串分割方法split()默认会保留分割符。
                * 当分割关键词在第一位时，分割符会被保留在第一个元素中，导致数组的第一个元素为空字符串。*/
                if (split.length == 2 || split.length == 1){
                    /*根据第二关键词分割数组*/
                    /*因为要获取的是数字，则此时第一个元素为要获取的参数*/
                    String[] split2 = split[split.length - 1].split(str2Split[1]);
                    String value = split2[0].trim();
                    value = FunctionalMethods.judgeResultWordSelection(new StringBuffer(value)).toString();
                    if (MyUtils.determineWhetherAStringIsAPureNumber(value)){
                        if (str1.indexOf(str2.replace("$", value))!=-1){
                            return value;
                        }
                    }
                }else {
                    /*有多个第一关键词*/
                    /*遍历所有第一关键词 数组元素*/
                    for (String string:str2Split){
                        String[] split2 = string.split(str2Split[1]);
                        String value = split2[0].trim();
                        value = FunctionalMethods.judgeResultWordSelection(new StringBuffer(value)).toString();
                        if (MyUtils.determineWhetherAStringIsAPureNumber(value)){
                            if (str1.indexOf(str2.replace("$", value))!=-1){
                                return value;
                            }
                        }
                    }
                }

            }
        }

        return null;
    }

    /**
     * @Description  根据关键字 取错误包数量  匹配关键词，截取数字 （被DataExtraction.getTheMeaningOfPlaceholders整合了）
     * @author charles
     * @createTime 2023/12/22 9:06
     * @desc
     *  字符串根据关键字取词，取错误包数量的时候,冒号"："不受影响，可以直接改为" : ",来避免工程师的输入引发问题
     *  如果占位符$ 在首尾位置，则根据 第一次出现 和 最后一次出现 检查关键字在字符串中的位置，取关键字前 或者 取关键字后的数据
     *  如果占位符$ 在关键词的中间位置，则按$ 分割，然后然后按照得到的两个数组元素匹配字符串 截取中间位置
     *  为了 消除有字母影响，使用方法  StringTruncationDoubleValue  来截取 $ 位置上的 数字
     *
     * @param keyword
     * @param input
     * @return
     */
    public String getTheMeaningOfPlaceholders(String input,String keyword) {

        keyword = keyword.replace(":"," : ").replaceAll("\\s+"," ").trim();
        input = input.replace(":"," : ").replaceAll("\\s+"," ").trim();

        /*1 首先将交换机返回信息数字替换为"",
        将配置文件中的占位符$替换为""*/
        String str1Str = input.replaceAll("\\d", "");
        String str2Str = keyword.trim().replace("$", "");

        /*2 如果 行信息 不包含 关键词 则返回null*/
        if (str1Str.indexOf(str2Str) ==-1){
            return null;
        }

        if (keyword.startsWith("$")){

            /*替换掉$ 方便匹配*/
            keyword = keyword.replace("$","").trim();

            /*第一次出现的位置*/
            int index = input.indexOf(keyword);
            String substring = input.substring(0, index);
            List<Integer> integerList = MyUtils.extractInts(substring);

            return MyUtils.removeNonAlphanumeric(integerList.get(integerList.size()-1)+"");

        }else if (keyword.endsWith("$")){

            keyword = keyword.replace("$","").trim();

            /*最后一次出现位置*/
            int lastIndex = input.lastIndexOf(keyword);
            String substring = input.substring(lastIndex + keyword.length(), input.length());
            List<Integer> integerList = MyUtils.extractInts(substring);

            return MyUtils.removeNonAlphanumeric(integerList.get(0)+"");

        }else {
            /*$ 出现在中间*/
            String[] $split = keyword.split("\\$");
            /*第一次出现的位置*/
            int start = input.indexOf($split[0]);
            int end = input.indexOf($split[1]);
            if ( start+$split[0].length() < end ){
                String substring = input.substring(start + $split[0].length(), end).trim();;
                List<Integer> integerList = MyUtils.extractInts(substring);
                if (integerList.size() == 1){
                    return MyUtils.removeNonAlphanumeric(integerList.get(0)+"");
                }
            }

        }
        return null;
    }

    /**
    * @Description 字符串调整方法，当字符串中存在":"，且":"的前一字符不为" ",下一字符为" "时，则将":"替换成" :"。
    * @author charles
    * @createTime 2023/12/20 15:55
    * @desc
    * @param input
     * @return
    */
    public static String adjustColon(String input) {
        /*Pattern pattern = Pattern.compile("(?<! ):");
        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll(" :");*/
        return input.replaceAll("(?<=\\S):(?=\\s)|(?<=\\s):(?=\\S)", " : ").replaceAll("\\s+"," ");
    }

    /**
     * 获取 描述：Description:  电力局数据库表还没有改(添加 Description 字段)
     * 忽略大小写判断返回信息是否包含 Description:  如果不包含返回 null
     * 如果包含，则按行分割，然后逐行判断是否包含 Description:
     * 如果包含 则返回 Description 属性值
     * @param information
     * @return
     */
    public static String getDescription(List<String> returnResults_List) {
        /* 配置文件中 获取 Description 关键词
        * 关键词 根据 ； 转化为 关键词 字符串 数组*/
        String descriptionValue = null;
        Object descriptionValueObject = (Object) CustomConfigurationUtil.getValue("错误包.描述", Constant.getProfileInformation());
        if (descriptionValueObject instanceof String){
            descriptionValue = (String) descriptionValueObject;
        }


        if (descriptionValue == null){
            return null;
        }

        StringBuffer stringBuffer = new StringBuffer();
        for (String information:returnResults_List){
            stringBuffer.append(information).append(" ");
        }

        String[] descriptionSplit = descriptionValue.split(";");
        for (String description:descriptionSplit){
            /* 判断交换机返回信息 是否包含 Description关键词
            * 如果不包含 则 跳出当前循环 进行下一个 循环*/

            if (!StringBufferUtils.stringBufferContainString(stringBuffer,description)){
                continue;
            }

            /*遍历 交换机返回信息数组
            * 判断 Description关键词 在那一行 */
            for (String string:returnResults_List){
                /*交换机返回信息行信息中 判断是否包含 Description关键词
                * 如果包含 则 != -1
                * 如果不包含 则 = -1*/
                /* 如果行信息中 以"Description:" 开头 则 i=0 */
                int i = string.toLowerCase().indexOf(description.toLowerCase());
                if (i!=-1){
                    int i1 = string.toLowerCase().indexOf(description.toLowerCase());
                    if (i1 !=-1){
                        /* 获取 Description 关键词 所在行信息中 Description: 后面的内容*/
                        String substring = string.substring(i+description.length()).trim();
                        if (substring.startsWith(":")){
                            substring = substring.substring(1).trim();
                        }
                        return substring;
                    }
                }
            }
        }
        return null;
    }


    private static String switchPortReturnsResult = "The brief information of interface(s) under route mode:\n" +
            "Link: ADM - administratively down; Stby - standby\n" +
            "Protocol: (s) - spoofing\n" +
            "Interface            Link Protocol Main IP         Description\n" +
            "Loop114              UP   UP(s)    10.122.114.208\n" +
            "M-E0/0/0             DOWN DOWN     --\n" +
            "NULL0                UP   UP(s)    --\n" +
            "Vlan3                UP   UP       10.98.138.147\n" +
            "Vlan4                UP   UP       10.98.139.239\n" +
            "Vlan6                UP   UP       10.98.138.2\n" +
            "Vlan7                UP   UP       10.98.136.13\n" +
            "Vlan50               UP   UP       100.1.2.252\n" +
            "Vlan200              UP   UP       10.98.137.71\n" +
            "Vlan2000             UP   UP       10.98.138.195   to-shiju\n" +
            "Vlan2001             UP   UP       10.122.119.161\n" +
            "\n" +
            "The brief information of interface(s) under bridge mode:\n" +
            "Link: ADM - administratively down; Stby - standby\n" +
            "Speed or Duplex: (a)/A - auto; H - half; F - full\n" +
            "Type: A - access; T - trunk; H - hybrid\n" +
            "Interface            Link Speed   Duplex Type PVID Description\n" +
            "BAGG1                UP   2G(a)   F(a)   T    1    To_HX_S7506E\n" +
            "GE0/0/1              UP   1G(a)   F(a)   T    1\n" +
            "Ethernet0/0/0        UP  auto    A      T    1\n" +
            "Eth-Trunk20.2000     UP   1G(a)   F(a)   T    1\n" +
            "GE0/0/4              ADM  auto    A      T    1\n" +
            "GE0/0/5              UP   1G(a)   F(a)   T    1\n" +
            "GE0/0/6              ADM  auto    A      T    1\n" +
            "GE0/0/7              UP   1G(a)   F(a)   T    1    To_AnBeiSuo_S5720_G0/0/49\n" +
            "GE0/0/8              ADM  auto    A      A    1\n" +
            "GE0/0/9              ADM  auto    A      A    1\n" +
            "GE0/0/10             ADM  auto    A      A    1\n" +
            "GE0/0/11             DOWN 1G      F      T    1    To_ZhuLouJiFang2_XG0/0/3\n" +
            "GE0/0/12             UP   1G(a)   F(a)   T    1    To_HX_S7506E\n" +
            "GE0/0/13             ADM  auto    A      A    1\n" +
            "GE0/0/14             ADM  auto    A      A    1\n" +
            "GE0/0/15             ADM  auto    A      A    1\n" +
            "GE0/0/16             DOWN 1G      F      T    1    to_fajianbu_S3448\n" +
            "GE0/0/17             ADM  auto    A      A    1\n" +
            "GE0/0/18             ADM  auto    A      A    1\n" +
            "GE0/0/19             ADM  auto    A      A    1\n" +
            "GE0/0/20             ADM  auto    A      A    1\n" +
            "GE0/0/21             ADM  auto    A      A    1\n" +
            "GE0/0/22             ADM  auto    A      A    1\n" +
            "GE0/0/23             ADM  auto    A      A    1\n" +
            "GE0/0/24             ADM  auto    A      T    1    to_fajianbu_S3448\n" +
            "GE0/0/25             DOWN auto    A      T    1\n" +
            "GE0/0/26             DOWN auto    A      T    1\n" +
            "GE0/0/27             UP auto    A      T    1\n" +
            "GE0/0/28             DOWN auto    A      T    1\n" +
            "GE0/0/29             DOWN auto    A      T    1\n" +
            "GE0/0/30             DOWN   1G(a)   F(a)   T    1    To_HX_S7506E\n" +
            "GE0/0/31             UP   1G(a)   F(a)   A    2001 To_ShiJu\n" +
            "GE0/0/32             DOWN  auto    A      A    200  To_HX_S7506E\n" +
            "GE0/0/33             UP   1G(a)   F(a)   A    2001 To_ShiJu\n";

    /*private static String switchPortValueReturnsResult = "GigabitEthernet1/0/25 current state: UP\n" +
            " IP Packet Frame Type: PKTFMT_ETHNT_2, Hardware Address: 0cda-41de-4e33\n" +
            " Description :To_ShuJuWangHuLian_G1/0/18\n" +
            " Loopback is not set\n" +
            " Media type is twisted pair\n" +
            " Port hardware type is  1000_BASE_T\n" +
            " 1000Mbps-speed mode, full-duplex mode\n" +
            " Link speed type is autonegotiation, link duplex type is autonegotiation\n" +
            " Flow-control is not enabled\n" +
            " The Maximum Frame Length is 10000\n" +
            " Broadcast MAX-ratio: 100%\n" +
            " Unicast MAX-ratio: 100%\n" +
            " Multicast MAX-ratio: 100%\n" +
            " Allow jumbo frame to pass\n" +
            " PVID: 1\n" +
            " Mdi type: auto\n" +
            " Port link-type: trunk\n" +
            "  VLAN passing  : 118, 602\n" +
            "  VLAN permitted: 118, 602\n" +
            "  Trunk port encapsulation: IEEE 802.1q\n" +
            " Port priority: 0\n" +
            " Last clearing of counters:  Never\n" +
            " Peak value of input: 207721 bytes/sec, at 2022-11-08 06:26:00\n" +
            " Peak value of output: 33198 bytes/sec, at 2023-03-27 10:50:33\n" +
            " Last 300 seconds input:  2 packets/sec 282 bytes/sec 0%\n" +
            " Last 300 seconds output:  2 packets/sec 290 bytes/sec 0%\n" +
            " Input (total):  56148368 packets, 6611001881 bytes\n" +
            "         56111416 unicasts, 36952 broadcasts, 0 multicasts, 0 pauses\n" +
            " Input (normal):  56148368 packets, - bytes\n" +
            "         56111416 unicasts, 36952 broadcasts, 0 multicasts, 0 pauses\n" +
            " Input:  10 input errors, 0 runts, 0 giants, 0 throttles\n" +
            "         20 CRC, 0 frame, - overruns, 0 aborts\n" +
            "         - ignored, - parity errors\n" +
            " Output (total): 46229751 packets, 4553563599 bytes\n" +
            "         43884692 unicasts, 911492 broadcasts, 1433567 multicasts, 0 pauses\n" +
            " Output (normal): 46229751 packets, - bytes\n" +
            "         43884692 unicasts, 911492 broadcasts, 1433567 multicasts, 0 pauses\n" +
            " Output: 30 output errors, - underruns, - buffer failures\n" +
            "         0 aborts, 0 deferred, 0 collisions, 0 late collisions\n" +
            "         0 lost carrier, - no carrier";*/

     private static String switchPortValueReturnsResult =
                    " Description :To_AnBeiSuo_S5720_G0/0/49\n" +
                    "    Input: 22789602665 bytes, 122755407 packets\n" +
                    "    Output: 86753318501 bytes, 163119446 packets\n" +
                    "Input: 982431567 packets, 1214464892426 bytes\n" +
                    "      Unicast: 981439518, Multicast: 404\n" +
                    "      Broadcast: 991644, Jumbo: 0\n" +
                    "      Discard: 0, Pause: 0\n" +
                    "      Frames: 0\n" +
                    "      Total Error: 60\n" +
                    "      CRC: 1, Giants: 0\n" +
                    "      Jabbers: 0, Fragments: 0\n" +
                    "      Runts: 0, DropEvents: 0\n" +
                    "      Alignments: 0, Symbols: 1\n" +
                    "      Ignoreds: 0\n" +
                    "Output: 508606045 packets, 45046419657 bytes\n" +
                    "      Unicast: 502482495, Multicast: 6122779\n" +
                    "      Broadcast: 771, Jumbo: 0\n" +
                    "      Discard: 0, Pause: 0\n" +
                    "      Total Error: 100\n" +
                    "      Collisions: 0, ExcessiveCollisions: 0\n" +
                    "      Late Collisions: 0, Deferreds: 0\n" +
                    "      Buffers Purged: 0";

}

enum ErrorPackageL_listEnum{
    keyword, /* 占位符关键词*/

    InputPKT, /* 单占位符获取Input总包*/
    OutputPKT, /* 单占位符获取Output总包*/
    InputPKT_PKT, /* 多占位符获取Input总包*/
    OutputPKT_PKT; /* 多占位符获取Input总包*/

    static ErrorPackageL_listEnum key(){
        return keyword;
    }
    static ErrorPackageL_listEnum singlePlaceholderInput(){
        return InputPKT;
    }
    static ErrorPackageL_listEnum singlePlaceholderOutput(){
        return OutputPKT;
    }
    static ErrorPackageL_listEnum multiplePlaceholdersInput(){
        return InputPKT_PKT;
    }
    static ErrorPackageL_listEnum multiplePlaceholdersOutput(){
        return OutputPKT_PKT;
    }
}