package com.sgcc.sql;

import com.sgcc.advanced.controller.*;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.switchboard.ConnectToObtainInformation;
import com.sgcc.sql.controller.SwitchInteraction;
import com.sgcc.sql.domain.CommandReturn;
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.sql.util.InspectionMethods;

import java.util.ArrayList;
import java.util.List;

public class TerminationTest {

    public void logInToGetBasicInformation_scanProblem(SwitchParameters switchParameters,
                                                 List<TotalQuestionTable> totalQuestionTables,
                                                 List<String> advancedName, boolean isRSA) {

        /*连接交换机 获取交换机基本信息*/
        ConnectToObtainInformation connectToObtainInformation = new ConnectToObtainInformation();
        AjaxResult basicInformationList_ajaxResult = connectToObtainInformation.connectSwitchObtainBasicInformation(switchParameters,isRSA);

        /* 告警、异常信息写入*/
        if (basicInformationList_ajaxResult.get("msg").equals("交换机连接失败")
                || basicInformationList_ajaxResult.get("msg").equals("未定义该交换机获取基本信息命令及分析")
                || basicInformationList_ajaxResult.get("msg").equals("交换机登录信息获取失败")){

            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }

            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:" +
                            "IP地址为:"+switchParameters.getIp()+"。"+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+"。"+
                            "问题为:"+basicInformationList_ajaxResult.get("msg")+"。\r\n");

            return;
        }

        /*交换机登录信息 通过获取交换机基本信息 该实体类已经包含交换机基本信息*/
        switchParameters = (SwitchParameters) basicInformationList_ajaxResult.get("data");


        /** 普通扫描是否含有运行分析 ， 如果含有运行分析在接下来先扫描运行分析 */
        if (advancedName != null && advancedName.size() != 0){

            for (String function:advancedName){
                switch (function){
                    case "OSPF":
                        OSPFFeatures ospfFeatures = new OSPFFeatures();
                        ospfFeatures.getOSPFValues(switchParameters);
                        break;
                    case "光衰":
                        LuminousAttenuation luminousAttenuation = new LuminousAttenuation();
                        luminousAttenuation.obtainLightDecay(switchParameters);
                        break;
                    case "错误包":
                        ErrorPackage errorPackage = new ErrorPackage();
                        errorPackage.getErrorPackage(switchParameters);
                        break;


                    case "路由聚合":
                        // 创建RouteAggregation对象并执行obtainAggregationResults方法
                        RouteAggregation routeAggregation = new RouteAggregation();
                        routeAggregation.obtainAggregationResults(switchParameters);
                        break;

                    case "链路捆绑":
                        // 创建LinkBundling对象并执行linkBindingInterface方法
                        LinkBundling linkBundling = new LinkBundling();
                        linkBundling.linkBindingInterface(switchParameters);
                        break;
                }
            }

        }


        //5.获取交换机可扫描的问题并执行分析操作
        /*当 totalQuestionTables 不为空时，为专项扫描*/
        /* 存储 满足思想基本信息的、将要扫描的 交换机问题 */
        List<TotalQuestionTable> totalQuestionTableList = new ArrayList<>();

        SwitchInteraction switchInteraction = new SwitchInteraction();

        /* totalQuestionTables == null 的时候 是扫描全部问题
         * totalQuestionTables != null 的时候 是专项扫描
         * 获取满足思想基本信息的、将要扫描的 交换机问题*/
        if (totalQuestionTables == null){

            /* 根据交换机基本信息 查询 可扫描的交换机问题 */
            AjaxResult commandIdByInformation_ajaxResult = switchInteraction.commandIdByInformation(switchParameters);
            /*告警、异常信息写入*/
            /* 返回AjaxResult长度为0 则未定义交换机问题*/
            if (commandIdByInformation_ajaxResult.size() == 0){

                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                        "异常:IP地址为:"+switchParameters.getIp()+"。"+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+"。"+
                                "问题为:未定义交换机问题。\r\n");


                return;
            }
            /* 返回AjaxResult长度不为0 则赋值可扫描交换机问题集合 */
            totalQuestionTableList = (List<TotalQuestionTable>) commandIdByInformation_ajaxResult.get("data");

        }else {

            //totalQuestionTables != null 是 专项扫描问题
            for (TotalQuestionTable totalQuestionTable:totalQuestionTables){
                // 匹配符合当前交换机四项基本信息的问题   要么问题和交换机的基本信息相同  要么问题的四项基本信息为*
                if (  totalQuestionTable.getBrand().equals(switchParameters.getDeviceBrand())
                        && (  totalQuestionTable.getType().equals(switchParameters.getDeviceModel()) || totalQuestionTable.getType().equals("*")  )
                        && (  totalQuestionTable.getFirewareVersion().equals(switchParameters.getFirmwareVersion()) || totalQuestionTable.getFirewareVersion().equals("*")  )
                        && (  totalQuestionTable.getSubVersion().equals(switchParameters.getSubversionNumber()) || totalQuestionTable.getSubVersion().equals("*")  )){

                    totalQuestionTableList.add(totalQuestionTable);
                }else {
                    //不匹配则跳过 继续下一个问题
                    continue;
                }
            }
        }


        /*筛选匹配度高的交换机问题*/
        List<TotalQuestionTable> TotalQuestionTablePojoList = InspectionMethods.ObtainPreciseEntityClasses(totalQuestionTableList);

        for (TotalQuestionTable totalQuestionTable:TotalQuestionTablePojoList){
            /*告警、异常信息写入*/
            if (totalQuestionTable.getProblemSolvingId() == null || totalQuestionTable.getProblemSolvingId().equals("null")){
                //传输登陆人姓名 及问题简述
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                        "异常:IP地址为:"+switchParameters.getIp()+"。"+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+"。"+
                                "问题ID为:"+totalQuestionTable.getId() +
                                "问题分类:"+totalQuestionTable.getTypeProblem()+
                                "问题名称为:"+totalQuestionTable.getTemProName()+":"+totalQuestionTable.getProblemName()
                                +",未定义解决问题。\r\n"
                );
            }

            if (totalQuestionTable.getLogicalID().indexOf("命令") != -1){

                /* 交换机返回结果未结束标志符：   ---- More ----    */
                switchParameters.setNotFinished(totalQuestionTable.getNotFinished());

                //根据命令ID获取具体命令，执行
                //返回  交换机返回信息 和  第一条分析ID
                CommandReturn commandReturn = switchInteraction.executeScanCommandByCommandId(switchParameters,totalQuestionTable,totalQuestionTable.getLogicalID().replace("命令",""));
                if (!commandReturn.isSuccessOrNot()){
                    /*交换机返回错误信息处理
                     * 遍历下一个问题*/
                    continue;
                }

                //分析
                String analysisReturnResults_String = switchInteraction.analysisReturnResults(switchParameters, totalQuestionTable,
                        commandReturn ,  "",  "");

            }else if (totalQuestionTable.getLogicalID().indexOf("分析") != -1){

                CommandReturn commandReturn = new CommandReturn();
                commandReturn.setReturnResults(new ArrayList<>());
                commandReturn.setAnalysisID(totalQuestionTable.getLogicalID().replaceAll("分析",""));

                //分析
                String analysisReturnResults_String = switchInteraction.analysisReturnResults(switchParameters, totalQuestionTable,
                        commandReturn,  "",  "");

            }
        }

        /* 关闭交换机连接 */
        if (switchParameters.getMode().equalsIgnoreCase("ssh")){
            switchParameters.getConnectMethod().closeConnect(switchParameters.getSshConnect());
        }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
            switchParameters.getTelnetSwitchMethod().closeSession(switchParameters.getTelnetComponent());
        }

    }
}
