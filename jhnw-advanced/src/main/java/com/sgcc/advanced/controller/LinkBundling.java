package com.sgcc.advanced.controller;

import com.sgcc.advanced.aggregation.IPAddressCalculator;
import com.sgcc.advanced.aggregation.IPAddressUtils;
import com.sgcc.advanced.domain.*;
import com.sgcc.advanced.service.ILinkBindingCommandService;
import com.sgcc.advanced.utils.ObtainPreciseEntityClasses;
import com.sgcc.advanced.utils.ScreeningMethod;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.ExecuteCommand;
import com.sgcc.share.util.MyUtils;
import com.sgcc.share.util.StringBufferUtils;
import com.sgcc.share.util.WorkThreadMonitor;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 链路捆绑
 */
public class LinkBundling {

    @Autowired
    private ILinkBindingCommandService linkBindingCommandService;

    /**
     * 绑定链路接口
     *
     * @param switchParameters 包含交换机参数的SwitchParameters对象
     * @return AjaxResult对象，包含操作结果
     */
    public AjaxResult linkBindingInterface(SwitchParameters switchParameters) {
        // 检查线程中断标志  如果线程中断标志为true，则直接返回
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            return AjaxResult.success("操作成功", "线程已终止扫描");
        }

        /**
         * 1：根据四项基本信息获取链路绑定命令的对象
         */
        AjaxResult commandPojo = getLinkBindingCommandPojo(switchParameters);
        // 检查线程中断标志
        if (commandPojo == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return AjaxResult.success("操作成功", "线程已终止扫描");
        }else if (!commandPojo.get("msg").equals("操作成功")){// 如果操作消息不为"操作成功"，则直接返回commandPojo
            return commandPojo;
        }
        // 将commandPojo中的数据转换为LinkBindingCommand对象
        LinkBindingCommand linkBindingCommand = (LinkBindingCommand) commandPojo.get("data");


        /**
         * 2: 获取路由表命令,获取交换机返回信息
         */
        // 如果external信息不为空，则调用ExternalRouteAggregation类的externalRouteAggregation方法进行外部路由聚合
        List<String> external_List = getRoutingTableCommand(switchParameters,linkBindingCommand);
        // 存储目标地址和掩码的列表
        List<String> DestinationMaskList = new ArrayList<>();
        // 检查线程中断标志
        if (external_List == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return AjaxResult.success("操作成功", "线程已终止扫描");
        }else if (MyUtils.isCollectionEmpty(external_List)){
            /**
             * 3: 获取实体类的协议关键字，并根据协议关键字获取外部IP地址和掩码列表
             */
            String externalKeywords = linkBindingCommand.getKeywords();
            /* todo 虚假数据 */
            externalKeywords = "OSPF/O_INTRA/O/O_ASE/O_ASE2/C/S";
            List<ExternalIPCalculator> externalIPCalculatorList = ExternalRouteAggregation.getExternalIPCalculatorList(switchParameters,
                    external_List,
                    externalKeywords);
            // 检查线程中断标志
            if (externalIPCalculatorList == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
                // 如果线程中断标志为true，则直接返回
                return AjaxResult.success("操作成功", "线程已终止扫描");
            }
            DestinationMaskList = externalIPCalculatorList.stream().map(x -> x.getDestinationMask()).collect(Collectors.toList());
        }

        // 用于映射IP地址和链路捆绑类型的HashMap
        HashMap<String,String> ipListMap = new HashMap<>();
        List<IPCalculator> IPCalculatorList = DestinationMaskList.stream().map(ipCIDR -> IPAddressCalculator.Calculator(ipCIDR)).collect(Collectors.toList());

        //todo 与登录设备相连的用户站IP，需要前端输入的数据
        List<String> ipList = new ArrayList<>();
        for (String ip:ipList){
            for (IPCalculator ipCalculator : IPCalculatorList) {
                //判断是否为三层链路捆绑
                boolean three_layers = LinkBundlingAnalysis(ip,ipCalculator);
                if (three_layers){
                    ipListMap.put(ip,"三层链路捆绑");
                }
            }
        }

        // 返回操作成功的AjaxResult对象
        return AjaxResult.success();
    }




    /**
     * 获取路由表命令
     *
     * @param switchParameters 交换机参数对象
     * @param linkBindingCommand 链路绑定命令对象
     * @return 返回获取到的路由表命令结果
     */
    public List<String> getRoutingTableCommand(SwitchParameters switchParameters,LinkBindingCommand linkBindingCommand){

        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }

        // 获取路由聚合问题的外部命令
        String routingTableCommand = linkBindingCommand.getRoutingTableCommand();
        ExecuteCommand executeCommand = new ExecuteCommand();
        List<String> stringList = executeCommand.executeScanCommandByCommand(switchParameters, routingTableCommand);
        if (stringList == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            return null;
        }
        // todo 链路捆绑命令返回结果虚拟数据
        /*stringList = StringBufferUtils.stringBufferSplit(StringBufferUtils.arrange(new StringBuffer(routingTableCommandReturnInformation)),
                "\r\n");*/
        if (MyUtils.isCollectionEmpty(stringList)){
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
        return stringList;
    }



    /**
     * 分析给定的IP地址是否可以进行链路捆绑。
     *
     * @param ip 要分析的IP地址
     * @param ipCalculator IP计算器，用于计算IP地址的范围
     * @return 如果IP地址在CIDR范围内且可以Ping通，则返回true；否则返回false
     *
     * 此方法首先使用IPAddressUtils工具类判断给定的IP地址是否在CIDR范围内。
     * 如果在范围内，则调用itCanBePinged方法判断该IP地址是否可以Ping通，如果可以Ping通则返回true，否则返回false。
     * 如果IP地址不在CIDR范围内，则直接返回false。
     */
    public static boolean LinkBundlingAnalysis(String ip,IPCalculator ipCalculator) {
        // 判断给定的IP地址是否在CIDR范围内
        if (IPAddressUtils.isIPInRange(ip, ipCalculator.getFirstAvailable(),ipCalculator.getFinallyAvailable())) {
            // 如果在范围内，则判断是否可以Ping通
            boolean ifPing = itCanBePinged(ip, 3000);
            return ifPing;
        }
        // 如果不在范围内，则返回false
        return false;
    }


    /**
     * 判断指定的主机名是否可以被ping通
     *
     * @param hostToPing 要ping通的主机名
     * @param timeout    超时时间，单位为毫秒，如果为0则使用默认值3000毫秒
     * @return 如果主机可以被ping通，则返回true；否则返回false
     */
    public static boolean itCanBePinged(String hostToPing, int timeout) {
        if (timeout == 0) {
            // 如果超时时间为0，则将其设置为默认值3000毫秒
            timeout = 3000; // 超时时间，单位是毫秒
        }
        try {
            // 根据主机名获取InetAddress对象
            InetAddress inetAddress = InetAddress.getByName(hostToPing);
            // 判断主机是否可达，超时时间为传入的timeout
            boolean reachable = inetAddress.isReachable(timeout);
            if (reachable) {
                // 如果主机可达，则返回true
                return true;
            } else {
                // 如果主机不可达，则返回false
                return false;
            }
        } catch (UnknownHostException e) {
            // 如果主机名不存在，则返回false
            return false;
        } catch (Exception e) {
            // 如果发生其他异常，则返回false
            return false;
        }
    }

    /**
     * 获取路由聚合命令对象
     *
     * @param switchParameters 交换机参数对象
     * @return 路由聚合命令对象，若配置文件路由聚合问题的命令为空则返回null
     */
    public AjaxResult getLinkBindingCommandPojo(SwitchParameters switchParameters){
        // 检查线程中断标志
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            // 如果线程中断标志为true，则直接返回
            return null;
        }
        /**
         * 1:根据四项基本信息创建IRouteAggregationCommandService的bean实例
         *    根据Bean实例查询数据库的链路捆绑命令对象列表，当链路捆绑命令对象列表为空时，进行日志写入
         */
        LinkBindingCommand linkBindingCommand = getLinkBindingCommand(switchParameters);
        linkBindingCommandService = SpringBeanUtil.getBean(ILinkBindingCommandService.class);
        List<LinkBindingCommand> linkBindingCommandList = linkBindingCommandService.selectLinkBindingCommandListBySQL(linkBindingCommand);
        if (MyUtils.isCollectionEmpty(linkBindingCommandList)){
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
         * 2：从链路捆绑命令对象列表中获取四项基本最详细的数据
         * */
        // 此处注释掉的代码是原来的方法，后通过泛型和反射的方式进行了优化，整合了几个高级功能的筛选方法，此处注释掉的代码不再使用。
        // LinkBindingCommand linkBindingCommandPojo = ScreeningMethod.ObtainPreciseEntityClassesLinkBindingCommand(switchParameters,linkBindingCommandList);

        ObtainPreciseEntityClasses genericTest = new ObtainPreciseEntityClasses();
        LinkBindingCommand linkBindingCommandPojo = (LinkBindingCommand) genericTest.genericObtainsExactEntityClasses(switchParameters, linkBindingCommandList);


        // 检查线程中断标志 如果线程中断标志为true，则直接返回
        if (linkBindingCommandPojo ==  null &&
                WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            return null;
        }
        return AjaxResult.success(linkBindingCommandPojo);
    }


    /**
     * 获取LinkBindingCommand对象
     *
     * @param switchParameters 包含设备信息的SwitchParameters对象
     * @return 返回一个LinkBindingCommand对象
     */
    public static LinkBindingCommand getLinkBindingCommand(SwitchParameters switchParameters) {
        // 创建一个LinkBindingCommand对象
        LinkBindingCommand linkBindingCommand = new LinkBindingCommand();
        // 设置品牌
        linkBindingCommand.setBrand(switchParameters.getDeviceBrand());
        // 设置开关类型
        linkBindingCommand.setSwitchType(switchParameters.getDeviceModel());
        // 设置固件版本
        linkBindingCommand.setFirewareVersion(switchParameters.getFirmwareVersion());
        // 设置子版本号
        linkBindingCommand.setSubVersion(switchParameters.getSubversionNumber());
        // 返回LinkBindingCommand对象
        return linkBindingCommand;
    }



    static String routingTableCommandReturnInformation = "Destinations : 403      Routes : 2233\n" +
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
