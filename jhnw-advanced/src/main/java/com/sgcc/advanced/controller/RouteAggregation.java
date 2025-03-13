package com.sgcc.advanced.controller;

import com.sgcc.advanced.aggregation.IPAddressCalculator;
import com.sgcc.advanced.aggregation.IPAddressUtils;
import com.sgcc.advanced.domain.*;
import com.sgcc.advanced.service.IRouteAggregationCommandService;
import com.sgcc.advanced.utils.ObtainPreciseEntityClasses;
import com.sgcc.advanced.utils.ScreeningMethod;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.controller.SwitchScanResultController;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.switchboard.SwitchIssueEcho;
import com.sgcc.share.util.ExecuteCommand;
import com.sgcc.share.util.MyUtils;
import com.sgcc.share.util.StringBufferUtils;
import com.sgcc.share.util.WorkThreadMonitor;
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
     * @param switchParameters 交换机参数对象
     * @return 无返回值
     */
    public AjaxResult obtainAggregationResults(SwitchParameters switchParameters) {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return AjaxResult.success("操作成功", "线程已终止扫描");
        }

        /**
         * 1:获取路由聚合命令对象
         */
        AjaxResult commandPojo = getRouteAggregationCommandPojo(switchParameters);
        // 检查线程中断标志
        if (commandPojo==null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return AjaxResult.success("操作成功", "线程已终止扫描");
        } else if (!commandPojo.get("msg").equals("操作成功")){
            return commandPojo;
        }
        RouteAggregationCommand routeAggregationCommandPojo = (RouteAggregationCommand) commandPojo.get("data");



        /**
         * 2:内部路由聚合
         * 如果internal信息不为空，则调用InternalRouteAggregation类的internalRouteAggregation方法进行内部路由聚合
         */
        List<String> internal_List = executeInternalCommand(switchParameters, routeAggregationCommandPojo);
        // 检查线程中断标志
        if (internal_List == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return AjaxResult.success("操作成功", "线程已终止扫描");
        } else if (!MyUtils.isCollectionEmpty(internal_List)){// 如果internal信息不为空
            /**
             * 调用internalRouteAggregation方法进行内部路由聚合
             */
            internalRouteAggregation(switchParameters,internal_List);
        }


        /**
         * 3：外部路由聚合
         * 如果external信息不为空，则调用ExternalRouteAggregation类的externalRouteAggregation方法进行外部路由聚合
         */
        List<String> external_List = executeExternalCommand(switchParameters, routeAggregationCommandPojo);
        // 检查线程中断标志
        if (external_List == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return AjaxResult.success("操作成功", "线程已终止扫描");
        } else if (!MyUtils.isCollectionEmpty(external_List)){// 如果external信息不为空
            // 从switchReturnsMap中获取externalKeywords信息
            String externalKeywords = routeAggregationCommandPojo.getExternalKeywords();
            // todo 外部路由聚合 协议关键词 虚假数据
            externalKeywords = "OSPF/O_INTRA/O/O_ASE/O_ASE2/C/S";

            /**
             * 调用ExternalRouteAggregation类的externalRouteAggregation方法进行外部路由聚合
             * */
            ExternalRouteAggregation.externalRouteAggregation(switchParameters,external_List,externalKeywords);
        }
        return AjaxResult.success();
    }



    /**
     * 执行内部命令以获取路由聚合问题的信息
     *
     * @param switchParameters 交换机参数对象
     * @param routeAggregationCommandPojo 路由聚合命令对象
     * @return 交换机返回的信息
     */
    public List<String> executeInternalCommand(SwitchParameters switchParameters,RouteAggregationCommand routeAggregationCommandPojo) {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }

        /**
         * 1：获取路由聚合问题的内部命令，执行交换机命令返回交换机返回信息
         *      检查线程中断标志，并判空
         * */
        String internalCommand = routeAggregationCommandPojo.getInternalCommand();
        ExecuteCommand executeCommand = new ExecuteCommand();
        List<String> internal_List = executeCommand.executeScanCommandByCommand(switchParameters, internalCommand);
        if (internal_List == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            return null;
        }

        // todo 内部路由聚合命令交换机返回信息-虚拟数据
        //internal_List = StringBufferUtils.stringBufferSplit(StringBufferUtils.arrange(new StringBuffer(H3C)),"\r\n");
        if (MyUtils.isCollectionEmpty(internal_List)){
            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }
            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题为:路由聚合问题的内部命令错误,请重新定义\r\n");
        }
        return internal_List;
    }

    /**
     * 执行外部命令以获取路由聚合问题的信息
     *
     * @param switchParameters 交换机参数对象
     * @param routeAggregationCommandPojo 路由聚合命令对象
     * @return 交换机返回的外部信息
     */
    public List<String> executeExternalCommand(SwitchParameters switchParameters,RouteAggregationCommand routeAggregationCommandPojo){
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }

        /**
         * 1：获取路由聚合问题的外部命令，执行交换机命令返回交换机返回信息
         * */
        String externalCommand = routeAggregationCommandPojo.getExternalCommand();
        ExecuteCommand executeCommand = new ExecuteCommand();
        List<String> external_List = executeCommand.executeScanCommandByCommand(switchParameters, externalCommand);
        if (external_List == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            return null;
        }
        // todo 外部路由聚合命令交换机返回信息-虚拟数据
        /*external_List = StringBufferUtils.stringBufferSplit(StringBufferUtils.arrange(new StringBuffer(externalreturnInformation)),
                "\r\n");*/
        if (MyUtils.isCollectionEmpty(external_List)){
            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }
            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题为:路由聚合问题的外部命令错误,请重新定义\r\n");
        }
        return external_List;
    }





    /**
     * 根据四项基本信息，填写路由聚合命令对象，并返回该对象。
     *
     * @param switchParameters 交换机参数对象
     * @return 路由聚合命令对象
     */
    public RouteAggregationCommand getRouteAggregationCommand(SwitchParameters switchParameters) {
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }

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
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }

        /**
         * 1：根据四项基本信息，填写路由聚合命令对象,
         *       后根据带有四项基本信息命令的路由聚合命令对象 查询符合配置的路由聚合命令列表
         *       判断路由聚合命令列表是否为空，为空则返回未定义信息
         */
        RouteAggregationCommand routeAggregationCommand = getRouteAggregationCommand(switchParameters);
        // 检查线程中断标志 如果线程中断标志为true，则直接返回
        if (routeAggregationCommand == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            return null;
        }
        routeAggregationCommandService = SpringBeanUtil.getBean(IRouteAggregationCommandService.class);
        List<RouteAggregationCommand> routeAggregationCommandList = routeAggregationCommandService.selectRouteAggregationCommandListBySQL(routeAggregationCommand);
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

        /**
         * 2：路由聚合命令列表不为空，则筛选出四项基本最详细的数据
         */
        // 此处注释掉的代码是原来的方法，后通过泛型和反射的方式进行了优化，整合了几个高级功能的筛选方法，此处注释掉的代码不再使用。
        // RouteAggregationCommand routeAggregationCommandPojo = ScreeningMethod.ObtainPreciseEntityClassesRouteAggregationCommand(switchParameters,routeAggregationCommandList);

        ObtainPreciseEntityClasses genericTest = new ObtainPreciseEntityClasses();
        RouteAggregationCommand routeAggregationCommandPojo = (RouteAggregationCommand) genericTest.genericObtainsExactEntityClasses(switchParameters, routeAggregationCommandList);

        // 检查线程中断标志 如果线程中断标志为true，则直接返回
        if (routeAggregationCommandPojo==null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            return null;
        }
        return AjaxResult.success(routeAggregationCommandPojo);
    }


    /**
     * 内部路由聚合方法
     *
     * @param switchParameters 交换机参数对象
     * @param switchReturnsinternalInformation_List 交换机返回的内部信息集合
     * @return 无返回值
     */
    public static void internalRouteAggregation(SwitchParameters switchParameters,List<String> switchReturnsinternalInformation_List) {
        // 检查线程中断标志 如果线程中断标志为true，则直接返回
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            return;
        }

        /**
         * 1：从给定的交换机返回信息中提取IP信息，并返回IP信息列表
         */
        List<IPInformation> ipInformationList = IPAddressUtils.getIPInformation(switchParameters,switchReturnsinternalInformation_List);
        // 检查线程中断标志 如果线程中断标志为true，则直接返回
        if (ipInformationList == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            return;
        }
        // 将IP地址和子网掩码转换为CIDR表示法的字符串。 10.98.136.0/24
        List<String> collect = ipInformationList.stream().map(ipInformation -> MyUtils.convertToCIDR(ipInformation.getIp(), ipInformation.getMask())).collect(Collectors.toList());
        // 将IP信息列表转换为 IP地址段范围 列表 ： 获取CIDR表示法的 子网掩码和地址段范围
        List<IPCalculator> ipCalculatorList = collect.stream().map(ipCIDR -> IPAddressCalculator.Calculator(ipCIDR)).collect(Collectors.toList());
        // 对 IP地址段范围 列表进行排序
        IPAddressUtils.sortIPCalculator(ipCalculatorList);



        /**
         * 2：将传入的 IP地址段范围 列表进行IP地址段的拼接，返回拼接后的IP地址段列表。拼接IP地址段包含起始IP地址和结束IP地址及对应的IP地址段范围
         * */
        List<IPAddresses> ipAddresses = IPAddressUtils.splicingAddressRange(switchParameters,ipCalculatorList);
        // 检查线程中断标志 如果线程中断标志为true，则直接返回
        if (ipAddresses == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            return;
        }

        /**
         * 3:遍历拼接后的IP地址段列表，对每个IP地址段的地址段范围拆分成新的更长的地址段。返回聚合后的IP地址段列表。
         */
        List<List<String>> returnList = new ArrayList<>();
        for (IPAddresses ipAddress : ipAddresses) {
            // 检查线程中断标志
            if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
                // 如果线程中断标志为true，则直接返回
                return;
            }
            // 获取IP地址段的预聚合结果列表
            List<String> preAggregationRouteList = ipAddress.getIpCalculatorList().stream()
                    .map(x -> x.getIp() + "/" + x.getMask() + "[" + x.getFirstAvailable() + " - " + x.getFinallyAvailable() + "]")
                    .collect(Collectors.toList());

            // 获取IP地址段的聚合结果列表
            List<String> aggregatedRouteList = IPAddressUtils.getNetworkNumber(ipAddress.getIpStart(), ipAddress.getIpEnd());
            List<String> returnString = new ArrayList<>();
            // 添加原始网络号标签
            returnString.add("原始网络号:");
            // 添加预聚合结果列表
            returnString.addAll(preAggregationRouteList);
            // 添加聚合网络号标签
            returnString.add("聚合网络号:");
            // 添加聚合结果列表
            returnString.addAll(aggregatedRouteList);
            // 将结果添加到返回列表中
            returnList.add(returnString);
            // 输出控制台
            returnString.stream().forEach(System.err::println);
            System.err.println("==============================================================");
            HashMap<String,String> hashMap = new HashMap<>();
            // 判断聚合结果是否有问题
            if (MyUtils.areAllElementsEqualAtSameIndex(
                    IPAddressUtils.ipSort(preAggregationRouteList),
                    IPAddressUtils.ipSort(aggregatedRouteList))) {//preAggregationRouteList.size() == aggregatedRouteList.size()
                hashMap.put("IfQuestion","无问题");
            }else {
                hashMap.put("IfQuestion","有问题");
            }
            // 创建SwitchScanResultController对象用于插入扫描结果
            SwitchScanResultController switchScanResultController = new SwitchScanResultController();
            // 设置问题名称为"内部路由聚合"
            hashMap.put("ProblemName","内部路由聚合");
            // 将结果拼接成字符串存入parameterString
            hashMap.put("parameterString",String.join("\r\n" , returnString));
            // 插入扫描结果并获取插入ID
            Long insertId = switchScanResultController.insertSwitchScanResult(switchParameters, hashMap);
            // 创建SwitchIssueEcho对象用于获取扫描结果回显
            SwitchIssueEcho switchIssueEcho = new SwitchIssueEcho();
            // 获取扫描结果回显
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
