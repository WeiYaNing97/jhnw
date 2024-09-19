package com.sgcc.advanced.controller;

import com.sgcc.advanced.aggregation.IPAddressCalculator;
import com.sgcc.advanced.aggregation.IPAddressUtils;
import com.sgcc.advanced.domain.*;
import com.sgcc.advanced.service.IRouteAggregationCommandService;
import com.sgcc.advanced.utils.ScreeningMethod;
import com.sgcc.common.core.domain.AjaxResult;
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
import java.util.Map;
import java.util.stream.Collectors;

public class RouteAggregation {

    @Autowired
    private IRouteAggregationCommandService routeAggregationCommandService;

    /**
     * 执行内部命令以获取路由聚合问题的信息
     *
     * @param switchParameters 交换机参数对象
     * @param routeAggregationCommandPojo 路由聚合命令对象
     * @return 交换机返回的信息
     */
    public String executeInternalCommand(SwitchParameters switchParameters,RouteAggregationCommand routeAggregationCommandPojo) {
        // 获取路由聚合问题的内部命令
        String internalCommand = routeAggregationCommandPojo.getInternalCommand();
        // 执行交换机命令，返回交换机返回信息
        /* 配置文件路由聚合问题的命令 不为空时，执行交换机命令，返回交换机返回信息*/
        ExecuteCommand executeCommand = new ExecuteCommand();
        String internal = executeCommand.executeScanCommandByCommand(switchParameters, internalCommand);
        // 去除返回信息中的空白字符
        internal = H3C;
        internal = MyUtils.trimString(internal);
        return internal;
    }

    /**
     * 执行外部命令以获取路由聚合问题的信息
     *
     * @param switchParameters 交换机参数对象
     * @param routeAggregationCommandPojo 路由聚合命令对象
     * @return 交换机返回的外部信息
     */
    public String executeExternalCommand(SwitchParameters switchParameters,RouteAggregationCommand routeAggregationCommandPojo){
        // 获取路由聚合问题的外部命令
        String externalCommand = routeAggregationCommandPojo.getExternalCommand();
        ExecuteCommand executeCommand = new ExecuteCommand();
        String external = executeCommand.executeScanCommandByCommand(switchParameters, externalCommand);
        // 去除返回信息中的空白字符
        external = externalreturnInformation;
        external = MyUtils.trimString(external);
        return external;
    }


    /**
     * 获取聚合结果
     *
     * @param switchParameters 交换机参数对象
     * @return 无返回值
     */
    public AjaxResult obtainAggregationResults(SwitchParameters switchParameters) {

        AjaxResult commandPojo = getRouteAggregationCommandPojo(switchParameters);
        if (!commandPojo.get("msg").equals("操作成功")){
            return commandPojo;
        }
        RouteAggregationCommand routeAggregationCommandPojo = (RouteAggregationCommand) commandPojo.get("data");

        /**
         * 执行内部路由命令
         * 如果internal信息不为空，则调用InternalRouteAggregation类的internalRouteAggregation方法进行内部路由聚合
         */
        String internal = executeInternalCommand(switchParameters, routeAggregationCommandPojo);
        // 如果internal信息不为空
        if (internal != null){
            // 调用internalRouteAggregation方法进行内部路由聚合
            internalRouteAggregation(switchParameters,internal);
        }

        /**
         * 执行外部路由命令
         * 如果external信息不为空，则调用ExternalRouteAggregation类的externalRouteAggregation方法进行外部路由聚合
         */
        String external = executeExternalCommand(switchParameters, routeAggregationCommandPojo);
        // 如果external信息不为空
        if (external!=null){
            // 从switchReturnsMap中获取externalKeywords信息
            String externalKeywords = routeAggregationCommandPojo.getExternalKeywords();
            externalKeywords = "OSPF/O_INTRA/O/O_ASE/O_ASE2/C/S";

            // 调用ExternalRouteAggregation类的externalRouteAggregation方法进行外部路由聚合
            ExternalRouteAggregation.externalRouteAggregation(switchParameters,external,externalKeywords);
        }

        return AjaxResult.success();
    }


    /**
     * 获取路由聚合命令
     *
     * @param switchParameters 交换机参数对象
     * @return 路由聚合命令对象
     */
    public RouteAggregationCommand getRouteAggregationCommand(SwitchParameters switchParameters) {
        // 创建一个路由聚合命令对象
        RouteAggregationCommand routeAggregationCommand = new RouteAggregationCommand();
        // 设置路由聚合命令对象的品牌为交换机参数对象的设备品牌
        routeAggregationCommand.setBrand(switchParameters.getDeviceBrand());
        // 设置路由聚合命令对象的交换机类型为交换机参数对象的设备型号
        routeAggregationCommand.setSwitchType(switchParameters.getDeviceModel());
        // 设置路由聚合命令对象的固件版本为交换机参数对象的固件版本
        routeAggregationCommand.setFirewareVersion(switchParameters.getFirmwareVersion());
        // 设置路由聚合命令对象的子版本号为交换机参数对象的子版本号
        routeAggregationCommand.setSubVersion(switchParameters.getSubversionNumber());
        // 返回路由聚合命令对象
        return routeAggregationCommand;
    }

    /**
     * 获取路由聚合命令对象
     *
     * @param switchParameters 交换机参数对象
     * @return 路由聚合命令对象，若配置文件路由聚合问题的命令为空则返回null
     */
    public AjaxResult getRouteAggregationCommandPojo(SwitchParameters switchParameters){
        // 1：获取配置文件关于路由聚合问题的符合交换机品牌的命令的配置信息
        /* SwitchParameters switchParameters */
        RouteAggregationCommand routeAggregationCommand = getRouteAggregationCommand(switchParameters);

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
            return AjaxResult.error("IP地址为:"+switchParameters.getIp()+","+"问题为:路由聚合功能未定义获取网络号命令\r\n");
        }

        // 从routeAggregationCommandList中获取四项基本最详细的数据
        /* 从 routeAggregationCommandList 中 获取四项基本最详细的数据*/
        RouteAggregationCommand routeAggregationCommandPojo = ScreeningMethod.ObtainPreciseEntityClassesRouteAggregationCommand(routeAggregationCommandList);
        return AjaxResult.success(routeAggregationCommandPojo);
    }


    /**
     * 内部路由聚合方法
     *
     * @param switchParameters 交换机参数对象
     * @param switchReturnsinternalInformation 交换机返回的内部信息
     * @return 无返回值
     */
    public static void internalRouteAggregation(SwitchParameters switchParameters,String switchReturnsinternalInformation) {
        // 获取IP信息列表
        List<IPInformation> ipInformationList = IPAddressUtils.getIPInformation(MyUtils.trimString(switchReturnsinternalInformation));
        List<String> collect = ipInformationList.stream().map(ipInformation -> MyUtils.convertToCIDR(ipInformation.getIp(), ipInformation.getMask())).collect(Collectors.toList());

        // 将IP信息列表转换为IP计算器列表
        List<IPCalculator> ipCalculatorList = collect.stream().map(ipCIDR -> IPAddressCalculator.Calculator(ipCIDR)).collect(Collectors.toList());

        // 对IP计算器列表进行排序
        IPAddressUtils.sortIPCalculator(ipCalculatorList);

        List<IPAddresses> ipAddresses = IPAddressUtils.splicingAddressRange(ipCalculatorList);
        List<List<String>> returnList = new ArrayList<>();
        for (IPAddresses ipAddress : ipAddresses) {
            List<String> preAggregationRouteList = ipAddress.getIpCalculatorList().stream()
                    .map(x -> x.getIp() + "/" + x.getMask() + "[" + x.getFirstAvailable() + " - " + x.getFinallyAvailable() + "]")
                    .collect(Collectors.toList());

            List<String> aggregatedRouteList = IPAddressUtils.getNetworkNumber(ipAddress.getIpStart(), ipAddress.getIpEnd());

            List<String> returnString = new ArrayList<>();
            returnString.add("原始网络号:");
            returnString.addAll(preAggregationRouteList);
            returnString.add("聚合网络号:");
            returnString.addAll(aggregatedRouteList);
            returnList.add(returnString);


            //输出控制台
            returnString.stream().forEach(System.err::println);
            System.err.println("==============================================================");


            HashMap<String,String> hashMap = new HashMap<>();
            if (preAggregationRouteList.size() == aggregatedRouteList.size()) {
                hashMap.put("IfQuestion","无问题");
            }else {
                hashMap.put("IfQuestion","有问题");
            }


            SwitchScanResultController switchScanResultController = new SwitchScanResultController();
            hashMap.put("ProblemName","内部路由聚合");
            hashMap.put("parameterString",String.join("\r\n" , returnString));
            Long insertId = switchScanResultController.insertSwitchScanResult(switchParameters, hashMap);
            SwitchIssueEcho switchIssueEcho = new SwitchIssueEcho();
            switchIssueEcho.getSwitchScanResultListByData(switchParameters.getLoginUser().getUsername(),insertId);

        }
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


    static String externalreturnInformation = "Destinations : 403      Routes : 2233\n" +
            "\n" +
            "Destination/Mask   Proto   Pre Cost        NextHop         Interface\n" +
            "0.0.0.0/0          O_ASE2  150 1           10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "0.0.0.0/32         Direct  0   0           127.0.0.1       InLoop0\n" +
            "10.0.0.0/8         O_ASE2  150 1           10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "10.10.0.0/16       O_ASE2  150 20          10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "10.13.0.0/16       O_ASE2  150 20          10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "10.19.0.0/16       O_ASE2  150 20          10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "10.98.128.0/19     O_ASE2  150 500         10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "10.98.128.0/24     O_INTRA 10  104         10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "10.98.128.1/32     O_INTRA 10  104         10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "10.98.129.0/24     O_INTRA 10  104         10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "10.98.129.1/32     O_INTRA 10  104         10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n" +
            "                                           10.98.138.2     Vlan6\n" +
            "                                           10.98.138.147   Vlan3\n" +
            "                                           10.98.138.195   Vlan2000\n" +
            "                                           10.98.139.239   Vlan4\n" +
            "10.98.130.0/24     O_INTRA 10  104         10.98.136.13    Vlan7\n" +
            "                                           10.98.137.71    Vlan200\n"+
            "O     10.122.114.92/30 [110/2] via 10.122.114.90, 336d,22:11:17, GigabitEthernet 9/2\n" +
            "O     10.122.114.108/30 [110/2] via 10.122.114.86, 55d,15:58:41, GigabitEthernet 9/1\n" +
            "O E2  10.122.114.112/30 [110/20] via 10.122.114.86, 259d,14:47:59, GigabitEthernet 9/1\n" +
            "O     10.122.114.116/30 [110/3] via 10.122.114.86, 46d,15:14:52, GigabitEthernet 9/1\n" +
            "C     10.122.114.128/30 is directly connected, GigabitEthernet 9/14\n" +
            "C     10.122.114.129/32 is local host. \n" +
            "O     10.122.114.132/30 [110/2] via 10.122.114.90, 27d,17:27:30, GigabitEthernet 9/2\n" +
            "                        [110/2] via 10.122.114.130, 27d,17:27:30, GigabitEthernet 9/14\n" +
            "O     10.122.114.136/30 [110/2] via 10.122.114.86, 336d,22:11:17, GigabitEthernet 9/1\n" +
            "O E2  10.122.114.144/30 [110/1] via 10.122.114.86, 98d,11:28:16, GigabitEthernet 9/1\n" +
            "O E2  10.122.114.148/30 [110/1] via 10.122.114.86, 98d,11:28:16, GigabitEthernet 9/1\n" +
            "                        [110/1] via 10.122.114.90, 98d,11:28:16, GigabitEthernet 9/2";
}
