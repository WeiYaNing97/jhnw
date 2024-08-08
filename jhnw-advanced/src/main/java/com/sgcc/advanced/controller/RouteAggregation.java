package com.sgcc.advanced.controller;

import com.sgcc.advanced.aggregation.IPAddressCalculator;
import com.sgcc.advanced.aggregation.IPAddressUtils;
import com.sgcc.advanced.domain.*;
import com.sgcc.advanced.service.IRouteAggregationCommandService;
import com.sgcc.advanced.utils.ScreeningMethod;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.controller.SwitchScanResultController;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.switchboard.SwitchIssueEcho;
import com.sgcc.share.util.ExecuteCommand;
import com.sgcc.share.util.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class RouteAggregation {

    @Autowired
    private IRouteAggregationCommandService routeAggregationCommandService;

    /**
     * 获取聚合结果
     *
     * @param switchParameters 交换机参数
     * @return 无返回值
     */
    public  void obtainAggregationResults(SwitchParameters switchParameters) {

        String switchReturnsInformation= switchReturnsResult(switchParameters);
        /*if (switchReturnsInformation == null) {
            return AjaxResult.error("获取交换机返回信息失败");
        }*/
        /*switchReturnsInformation = H3C;*/

        // 获取IP信息列表
        List<IPInformation> ipInformationList = IPAddressUtils.getIPInformation(MyUtils.trimString(switchReturnsInformation));
        List<String> collect = ipInformationList.stream().map(ipInformation -> MyUtils.convertToCIDR(ipInformation.getIp(), ipInformation.getMask())).collect(Collectors.toList());
        // 将IP信息列表转换为IP计算器列表
        List<IPCalculator> ipCalculatorList = collect.stream().map(ipCIDR -> IPAddressCalculator.Calculator(ipCIDR)).collect(Collectors.toList());
        // 对IP计算器列表进行排序
        IPAddressUtils.sortIPCalculator(ipCalculatorList);
        List<IPAddresses> ipAddresses = IPAddressUtils.splicingAddressRange(ipCalculatorList);
        List<List<String>> returnList = new ArrayList<>();
        for (IPAddresses ipAddress : ipAddresses) {
            List<String> collect1 = ipAddress.getIpCalculatorList().stream()
                    .map(x -> x.getIp() + "/" + x.getMask() + "[" + x.getFirstAvailable() + " - " + x.getFinallyAvailable() + "]")
                    .collect(Collectors.toList());
            List<String> stringList = IPAddressUtils.getNetworkNumber(ipAddress.getIpStart(), ipAddress.getIpEnd());

            List<String> returnString = new ArrayList<>();
            returnString.add("原始网络号:");
            returnString.addAll(collect1);
            returnString.add("聚合网络号:");
            returnString.addAll(stringList);
            returnString.stream().forEach(System.err::println);
            System.err.println("==============================================================");
            returnList.add(returnString);

            HashMap<String,String> hashMap = new HashMap<>();
            if (collect1.size() == stringList.size()) {
                hashMap.put("IfQuestion","无问题");
            }else {
                hashMap.put("IfQuestion","有问题");
            }

            SwitchScanResultController switchScanResultController = new SwitchScanResultController();
            hashMap.put("ProblemName","路由聚合");
            hashMap.put("parameterString",String.join("\r\n" , returnString));
            Long insertId = switchScanResultController.insertSwitchScanResult(switchParameters, hashMap);
            SwitchIssueEcho switchIssueEcho = new SwitchIssueEcho();
            switchIssueEcho.getSwitchScanResultListByData(switchParameters.getLoginUser().getUsername(),insertId);

        }

    }


    /**
     * 根据SwitchParameters参数获取交换机返回信息
     *
     * @param switchParameters 包含交换机相关信息的参数对象
     * @return 交换机返回信息，若路由聚合命令为空则返回null
     */
    public  String switchReturnsResult(SwitchParameters switchParameters) {

        // 1：获取配置文件关于路由聚合问题的符合交换机品牌的命令的配置信息
        /* SwitchParameters switchParameters */
        RouteAggregationCommand routeAggregationCommand = new RouteAggregationCommand();
        routeAggregationCommand.setBrand(switchParameters.getDeviceBrand());
        routeAggregationCommand.setSwitchType(switchParameters.getDeviceModel());
        routeAggregationCommand.setFirewareVersion(switchParameters.getFirmwareVersion());
        routeAggregationCommand.setSubVersion(switchParameters.getSubversionNumber());

        // 获取IRouteAggregationCommandService的bean实例
        routeAggregationCommandService = SpringBeanUtil.getBean(IRouteAggregationCommandService.class);
        // 查询符合配置的路由聚合命令列表
        List<RouteAggregationCommand> routeAggregationCommandList = routeAggregationCommandService.selectRouteAggregationCommandListBySQL(routeAggregationCommand);

        // 当配置文件路由聚合问题的命令为空时，进行日志写入
        if (MyUtils.isCollectionEmpty(routeAggregationCommandList)){
            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }
            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:IP地址为:"+switchParameters.getIp()+"。"+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+"。"+
                            "问题为:路由聚合功能未定义获取网络号命令。\r\n"
            );
            return null ;
        }

        // 从routeAggregationCommandList中获取四项基本最详细的数据
        /* 从 routeAggregationCommandList 中 获取四项基本最详细的数据*/
        RouteAggregationCommand routeAggregationCommandPojo = ScreeningMethod.ObtainPreciseEntityClassesRouteAggregationCommand(routeAggregationCommandList);

        // 获取路由聚合问题的内部命令
        String portNumberCommand = routeAggregationCommandPojo.getInternalCommand();

        // 执行交换机命令，返回交换机返回信息
        /* 配置文件路由聚合问题的命令 不为空时，执行交换机命令，返回交换机返回信息*/
        ExecuteCommand executeCommand = new ExecuteCommand();
        String returnString = executeCommand.executeScanCommandByCommand(switchParameters, portNumberCommand);

        // 去除返回信息中的空白字符
        returnString = MyUtils.trimString(returnString);

        // 返回交换机返回信息
        return returnString;
    }



    public static String H3C = "  network 10.98.136.0 0.0.0.255\n" +
            "  network 10.98.137.0 0.0.0.255\n" +
            "  network 10.98.138.0 0.0.0.255\n" +
            "  network 10.98.139.0 0.0.0.255           \n" +
            "  network 10.122.114.208 0.0.0.0\n" +
            "  network 10.122.119.160 0.0.0.7\n" +
            "  network 100.1.2.0 0.0.0.255\n"+
            " network 10.122.100.0 0.0.0.255 area 0.0.0.0\n" +
            " network 10.122.114.0 0.0.0.3 area 0.0.0.0\n" +
            " network 10.122.114.36 0.0.0.3 area 0.0.0.0\n" +
            " network 10.122.114.68 0.0.0.3 area 0.0.0.0\n" +
            " network 10.122.114.80 0.0.0.3 area 0.0.0.0\n" +
            " network 10.122.114.84 0.0.0.3 area 0.0.0.0\n" +
            " network 10.122.114.108 0.0.0.3 area 0.0.0.0\n" +
            " network 10.122.114.136 0.0.0.3 area 0.0.0.0\n" +
            " network 10.122.114.225 0.0.0.0 area 0.0.0.0\n" +
            " network 10.122.119.8 0.0.0.7 area 0.0.0.0\n" +
            " network 10.122.119.24 0.0.0.7 area 0.0.0.0\n" +
            " network 10.122.119.56 0.0.0.7 area 0.0.0.0\n"+
            " network 10.122.102.0 0.0.0.255 area 0.0.0.0\n" +
            " network 10.122.106.0 0.0.0.255 area 0.0.0.0\n" +
            " network 10.122.108.0 0.0.0.255 area 0.0.0.0\n" +
            " network 10.122.114.84 0.0.0.3 area 0.0.0.0\n" +
            " network 10.122.114.88 0.0.0.3 area 0.0.0.0\n" +
            " network 10.122.114.128 0.0.0.3 area 0.0.0.0\n" +
            " network 10.123.239.0 0.0.0.255 area 0.0.0.0\n"+
            "  network 10.98.152.0 0.0.0.255\n" +
            "  network 10.98.153.0 0.0.0.255\n" +
            "  network 10.98.154.0 0.0.0.255\n" +
            "  network 10.98.155.0 0.0.0.255\n" +
            "  network 10.122.114.197 0.0.0.0";

    /*public static String H3C ="  network 10.98.136.0 0.0.0.255\n" +
            "  network 10.98.137.0 0.0.0.255\n" +
            "  network 10.98.138.0 0.0.0.255\n" +
            "  network 10.98.139.0 0.0.0.255";*/

    /*public static String H3C =
            "network 10.122.114.84 0.0.0.3 area 0.0.0.0\n" +
            "network 10.122.114.88 0.0.0.3 area 0.0.0.0\n"+
            "network 10.122.114.84 0.0.0.3 area 0.0.0.0\n" +
            "network 10.122.114.80 0.0.0.3 area 0.0.0.0";*/
}
