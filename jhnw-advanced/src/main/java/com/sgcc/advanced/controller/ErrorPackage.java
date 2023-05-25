package com.sgcc.advanced.controller;
import com.sgcc.advanced.domain.ErrorRate;
import com.sgcc.advanced.domain.ErrorRateCommand;
import com.sgcc.advanced.service.IErrorRateCommandService;
import com.sgcc.advanced.service.IErrorRateService;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.controller.SwitchScanResultController;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.switchboard.SwitchIssueEcho;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.FunctionalMethods;
import com.sgcc.share.util.MyUtils;
import com.sgcc.share.util.PathHelper;
import com.sgcc.share.domain.Constant;
import com.sgcc.share.webSocket.WebSocketService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Api("误码率功能")
@RestController
@RequestMapping("/advanced/ErrorPackage")
@Transactional(rollbackFor = Exception.class)
public class ErrorPackage {
    @Autowired
    private IErrorRateService errorRateService;
    @Autowired
    private IErrorRateCommandService errorRateCommandService;

    public AjaxResult getErrorPackage(SwitchParameters switchParameters) {
        /*1：获取配置文件关于 误码率问题的 符合交换机品牌的命令的 配置信息*/
        ErrorRateCommand errorRateCommand = new ErrorRateCommand();
        errorRateCommand.setBrand(switchParameters.getDeviceBrand());
        errorRateCommand.setSwitchType(switchParameters.getDeviceModel());
        errorRateCommand.setFirewareVersion(switchParameters.getFirmwareVersion());
        errorRateCommand.setSubVersion(switchParameters.getSubversionNumber());
        errorRateCommandService = SpringBeanUtil.getBean(IErrorRateCommandService.class);
        List<ErrorRateCommand> errorRateCommandList = errorRateCommandService.selectErrorRateCommandList(errorRateCommand);

        /*2：当 配置文件误码率问题的命令 为空时 进行 日志写入*/
        if (MyUtils.isCollectionEmpty(errorRateCommandList)){
            // todo 关于交换机获取端口号命令 的错误代码库  缺少传输给前端的信息
            try {
                PathHelper.writeDataToFileByName("IP地址:"+switchParameters.getIp()+"未定义"+switchParameters.getDeviceBrand()+"交换机获取端口号命令\r\n","误码率");
                return AjaxResult.error("未定义"+switchParameters.getDeviceBrand()+"交换机获取端口号命令");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        errorRateCommand =getpojo(errorRateCommandList);
        String portNumberCommand = errorRateCommand.getGetPortCommand();

        /*3：配置文件误码率问题的命令 不为空时，执行交换机命令，返回交换机返回信息*/
        String returnString = FunctionalMethods.executeScanCommandByCommand(switchParameters, portNumberCommand);
        /*4: 如果交换机返回信息为 null 则 命令错误，交换机返回错误信息*/
        if (returnString == null){
            // todo 关于交换机返回错误信息 的错误代码库
            WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"系统信息:"+switchParameters.getIp()+":获取端口号命令错误,请重新定义\r\n");
            try {
                PathHelper.writeDataToFileByName(switchParameters.getIp()+":获取端口号命令错误,请重新定义\r\n","误码率");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("IP地址:"+switchParameters.getIp()+"获取端口号命令错误,请重新定义");
        }
        /*5：如果交换机返回信息不为 null说明命令执行正常, 则继续 根据交换机返回信息获取误码率端口号*/
        List<String> portList = ObtainUPStatusPortNumber(returnString);
        /*6：获取光衰端口号方法返回集合判断是否为空，说明没有端口号为开启状态 UP，是则进行*/
        if (MyUtils.isCollectionEmpty(portList)){
            // todo 关于没有端口号为UP状态 的错误代码库
            try {
                PathHelper.writeDataToFileByName("IP地址:"+switchParameters.getIp()+"无UP状态端口号\r\n","误码率");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AjaxResult.error("IP地址:"+switchParameters.getIp()+"未获取到UP状态端口号");
        }



        /*7：如果交换机端口号为开启状态 UP 不为空 则需要查看是否需要转义：
        GE转译为GigabitEthernet  才能执行获取交换机端口号光衰参数命令*/
        String conversion = errorRateCommand.getConversion();
        String[] conversionSplit = conversion.split(";");
        Map<String,String> escapeMap = new HashMap<>();
        for (String convers:conversionSplit){
            escapeMap.put(convers.split(":")[0],convers.split(":")[1]);
        }
        Set<String> mapKey = escapeMap.keySet();
        for (String key:mapKey){
            portList = portList.stream().map(m -> m.replace(key , escapeMap.get(key))).collect(Collectors.toList());
        }



        /*获取配置文件关于 误码率问题的 符合交换机品牌的命令的 配置信息*/
        String errorPackageCommand = errorRateCommand.getGetParameterCommand();

        HashMap<String, Object> errorPackageParameters = getErrorPackageParameters(switchParameters, portList, errorPackageCommand);
        if (errorPackageParameters == null){
            return AjaxResult.error("未获取到误码率参数");
        }

        /*获取交换机四项基本信息ID*/
        Long switchID = 0l;
        switchID = FunctionalMethods.getSwitchParametersId(switchParameters);

        errorRateService = SpringBeanUtil.getBean(IErrorRateService.class);//解决 多线程 service 为null问题
        Set<String> strings = errorPackageParameters.keySet();
        for (String port:strings){

            ErrorRate errorRate = new ErrorRate();
            errorRate.setSwitchIp(switchParameters.getIp());
            errorRate.setSwitchId(switchID);
            errorRate.setPort(port);

            List<ErrorRate> list = errorRateService.selectErrorRateList(errorRate);
            ErrorRate primaryErrorRate = new ErrorRate();
            if (!(MyUtils.isCollectionEmpty(list))){
                primaryErrorRate = list.get(0);
            }

            List<String> errorPackageValue = (List<String>) errorPackageParameters.get(port);
            for (String error:errorPackageValue){
                if (MyUtils.containIgnoreCase(error,"input") || MyUtils.containIgnoreCase(error,"Rx") ){
                    errorRate.setInputErrors(MyUtils.StringTruncationMatcherValue(error));
                }
                if (MyUtils.containIgnoreCase(error,"output") || MyUtils.containIgnoreCase(error,"Tx") ){
                    errorRate.setOutputErrors(MyUtils.StringTruncationMatcherValue(error));
                }
                if (MyUtils.containIgnoreCase(error,"crc")){
                    errorRate.setCrc(MyUtils.StringTruncationMatcherValue(error));
                }
            }

            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("ProblemName","误码率");

            if (primaryErrorRate.getId() == null){
                int i = errorRateService.insertErrorRate(errorRate);
                hashMap.put("IfQuestion","无问题");
            }else {
                int num = 0;
                if ((primaryErrorRate.getInputErrors() !=null && errorRate.getInputErrors() !=null) &&
                        (!primaryErrorRate.getInputErrors().equals(errorRate.getInputErrors()))){
                    num++;
                }
                if ((primaryErrorRate.getOutputErrors() !=null && errorRate.getOutputErrors() !=null) &&
                        (!primaryErrorRate.getOutputErrors().equals(errorRate.getOutputErrors()))){
                    num++;
                }
                if ((primaryErrorRate.getCrc() !=null && errorRate.getCrc() !=null) &&
                        (!primaryErrorRate.getCrc().equals(errorRate.getCrc()))){
                    num++;
                }
                if (num>0){
                    errorRate.setId(primaryErrorRate.getId());
                    int i = errorRateService.updateErrorRate(errorRate);
                    hashMap.put("IfQuestion","有问题");
                }else if (num == 0){
                    errorRate.setId(primaryErrorRate.getId());
                    int i = errorRateService.updateErrorRate(errorRate);
                    hashMap.put("IfQuestion","无问题");
                    /*continue;*/
                }
            }

            // =:= 是自定义分割符
            String parameterString = (errorRate.getInputErrors()!=null?"input=:=是=:=input原:"+primaryErrorRate.getInputErrors()+",input现:"+errorRate.getInputErrors()+"=:=":"")
                    +(errorRate.getOutputErrors()!=null?"output=:=是=:=output原:"+primaryErrorRate.getOutputErrors()+",output现:"+errorRate.getOutputErrors()+"=:=":"")
                    +(errorRate.getCrc()!=null?"crc=:=是=:=crc原:"+primaryErrorRate.getCrc()+",crc现:"+errorRate.getCrc():"");
            if (parameterString.endsWith("=:=")){
                parameterString = parameterString.substring(0,parameterString.length()-3);
            }
            hashMap.put("parameterString",parameterString);

            SwitchScanResultController switchScanResultController = new SwitchScanResultController();
            Long insertId = switchScanResultController.insertSwitchScanResult(switchParameters, hashMap);
            SwitchIssueEcho switchIssueEcho = new SwitchIssueEcho();
            switchIssueEcho.getSwitchScanResultListByData(switchParameters.getLoginUser().getUsername(),insertId);
        }
        return null;
    }

    public HashMap<String,Object> getErrorPackageParameters(SwitchParameters switchParameters,List<String> portNumber,String errorPackageCommand) {
        /*创建 返回对象 List<String>*/
        HashMap<String,Object> hashMap = new HashMap<>();
        /*端口号集合 需要检测各端口号的误码率参数*/
        for (String port:portNumber){
            List<String> valueList = new ArrayList<>();
            /*替换端口号 得到完整的 获取端口号误码率参数命令 */
            String FullCommand = errorPackageCommand.replaceAll("端口号",port);
            /*交换机执行命令 并返回结果*/
            String returnResults = FunctionalMethods.executeScanCommandByCommand(switchParameters, FullCommand);

            if (returnResults == null){
                // todo 获取光衰参数命令错误代码库
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"系统信息:"+switchParameters.getIp()+":获取误码率参数命令错误,请重新定义\r\n");
                try {
                    PathHelper.writeDataToFileByName(switchParameters.getIp()+":获取光衰参数命令错误,请重新定义\r\n","误码率");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }

            String[] returnResultssplit = returnResults.split("\r\n");
            for (String information:returnResultssplit){
                /*提取误码率参数*/
                List<String> value = getParameters(information);
                if (MyUtils.isCollectionEmpty(value)){
                    // todo 为提取到误码率参数
                    continue;
                }else {
                    valueList.addAll(value);
                }
            }

            if (MyUtils.isCollectionEmpty(valueList)){
                continue;
            }else {
                hashMap.put(port,valueList);
            }
        }
        return hashMap;
    }


    /**
     * 根据交换机返回信息获取获取UP状态端口号
     * @param returnString
     * @return
     */
    public static List<String> ObtainUPStatusPortNumber(String returnString) {
        String[] returnStringSplit = returnString.split("\r\n");
        List<String> strings = new ArrayList<>();
        for (String string:returnStringSplit){
            /*包含 交换机返回行信息转化为大写 UP状态  并且该行带有“/”的 存放入端口待取集合*/
            if ((string.toUpperCase().indexOf(" UP ")!=-1) && string.indexOf("/")!=-1){
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
            String terminalSlogan = LuminousAttenuation.getTerminalSlogan(information);
            if (terminalSlogan != null){
                port.add(terminalSlogan);
            }
        }
        return port;
    }

    /**
     * 查看交换机误码率数量
     * @param information
     * @return
     */
    public List<String> getParameters(String information) {
        String[] keyword = {"input errors","output errors","CRC,","CRC:","RxErrorPkts","TxErrorPkts"};
        List<String> keyList = new ArrayList<>();
        for (String key:keyword){
            if (information.toUpperCase().indexOf(key.toUpperCase())!=-1){
                keyList.add(key);
            }
        }
        List<String> returnList = new ArrayList<>();
        for (String key:keyList){
            switch (key){
                case "input errors":
                case "output errors":
                case "CRC,":
                    String[] inputoutputCRC = MyUtils.splitIgnoreCase(information, key);
                    String[] inputoutputCRCsplit = inputoutputCRC[0].split(",");
                    returnList.add(inputoutputCRCsplit[inputoutputCRCsplit.length-1]+key);
                    break;
                case "CRC:":
                    String[] CRC = MyUtils.splitIgnoreCase(information, key);
                    String[] CRCplit = CRC[1].split(",");
                    returnList.add(key + CRCplit[0]);
                    break;
                case "RxErrorPkts":
                case "TxErrorPkts":
                    String[] rxtx = information.split(":");
                    returnList.add(rxtx[0] +" : "+  rxtx[1]);
                    break;
            }
        }
        return returnList;
    }

    public static ErrorRateCommand getpojo(List<ErrorRateCommand> pojoList) {
        ErrorRateCommand errorRateCommand = new ErrorRateCommand();
        int sum = 0;
        for (ErrorRateCommand pojo:pojoList){
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
                errorRateCommand = pojo;
            }else if (sum == num && (pojo.getSwitchType().equals("*")) && (pojo.getSubVersion().equals("*"))){
                errorRateCommand = pojo;
            }
        }
        return errorRateCommand;
    }

}
