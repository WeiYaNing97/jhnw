package com.sgcc.sql.util;

import com.sgcc.share.domain.Constant;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.CustomConfigurationUtil;
import com.sgcc.share.util.FunctionalMethods;
import com.sgcc.share.util.PathHelper;
import com.sgcc.share.webSocket.WebSocketService;
import com.sgcc.sql.controller.SwitchInteraction;
import com.sgcc.sql.domain.CommandReturn;
import com.sgcc.sql.domain.ProblemScanLogic;
import com.sgcc.sql.domain.TotalQuestionTable;

import java.io.IOException;
import java.util.List;

/**
 * @program: jhnw
 * @description: 扫描相关逻辑方法
 * @author:
 * @create: 2023-11-20 09:26
 **/
public class ScanLogicMethods {

    /*Inspection Completed*/
    /**
    * @Description 匹配逻辑方法
    * @author charles
    * @createTime 2023/11/20 10:30
    * @desc
    * @param switchParameters 交换机信息
     * @param matched 精确匹配 模糊匹配 不存在
     * @param information_line_n 交换机返回信息行
     * @param matchContent 匹配关键词
     * @param totalQuestionTable 问题表数据
     * @param return_information_array 发送命令，交换机返回信息集合
     * @param current_Round_Extraction_String
     * @param extractInformation_string
     * @param line_n 光标
     * @param firstID 第一条分析ID
     * @param problemScanLogicList 分析逻辑
     * @param currentID 当前分析ID
     * @param insertsInteger 插入数据次数
     * @param loop 记录循环次数，经过了几次循环次数
     * @param numberOfCycles 最大循环次数
     * @param problemScanLogic
     * @param matching_logic
     * @param num
     * @return
    */
    public String MatchingLogicMethod(SwitchParameters switchParameters,
                                             String matched, String information_line_n, String matchContent,
                                             TotalQuestionTable totalQuestionTable,
                                             String[] return_information_array, String current_Round_Extraction_String, String extractInformation_string,
                                             int line_n, String firstID , List<ProblemScanLogic> problemScanLogicList, String currentID,
                                             Integer insertsInteger, Integer loop,Integer numberOfCycles,ProblemScanLogic problemScanLogic,
                                             String matching_logic, int num) {

        /** 匹配方法 */
        // 根据匹配方法 得到是否匹配（成功:true 失败:false）
        // matched : 精确匹配 模糊匹配 不存在
        // information_line_n：交换机返回信息行  matchContent：匹配关键词

        boolean matchAnalysis_true_false = matchAnalysis(matched, information_line_n, matchContent.trim());

        //如果最终逻辑成功 则把 匹配成功的行数 付给变量 line_n
        if (matchAnalysis_true_false){

            /**匹配成功*/
            /* 告警、异常信息写入 */
            try {
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"TrueAndFalse:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        "问题类型:"+(totalQuestionTable==null?"获取交换机基本信息":(totalQuestionTable.getTypeProblem()+ "问题名称:"+totalQuestionTable.getTemProName()))+"、"+matched+matchContent+"成功\r\n");
                PathHelper.writeDataToFile(
                        "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题类型:"+(totalQuestionTable==null?"获取交换机基本信息":(totalQuestionTable.getTypeProblem()+ "问题名称:"+totalQuestionTable.getTemProName()))+"、"+matched+matchContent+"成功\r\n"
                );
            } catch (IOException e) {
                e.printStackTrace();
            }


            /**扫描分析成功逻辑*/
            ScanLogicMethods scanLogicMethods = new ScanLogicMethods();
            String trueLogic = scanLogicMethods.trueLogic(switchParameters, totalQuestionTable,
                    return_information_array, current_Round_Extraction_String, extractInformation_string,
                    line_n, firstID, problemScanLogicList, currentID,
                    insertsInteger, loop, numberOfCycles, problemScanLogic);

            return trueLogic;

        }else {

            /**匹配失败*/
            /* 取词方法包含 "full&"  则说明是全文匹配 则需要配置当前及下文数据
             * 并且 当前行不为 倒数第二行时（倒数第一行 为 标识符 如：<H3C-S2152-1> ） 则 continue 继续遍历 */
                    /*|| problemScanLogic.getRelativePosition().indexOf("null") != -1
                    注释原因 逻辑修改后一定不包含null
                      参数值一般为 ： present,0   full,0   1,0   0,1*/
            if ((matching_logic.indexOf("full&")!=-1)
                    && num < return_information_array.length-1){
                return "continue";
            }

            /* 告警、异常信息写入 */
            try {
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"TrueAndFalse:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        "问题类型:"+(totalQuestionTable==null?"获取交换机基本信息":(totalQuestionTable.getTypeProblem()+ "问题名称:"+totalQuestionTable.getTemProName()))+
                        "、"+matched+matchContent+"失败\r\n");
                PathHelper.writeDataToFile(
                        "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题类型:"+(totalQuestionTable==null?"获取交换机基本信息":(totalQuestionTable.getTypeProblem()+ "问题名称:"+totalQuestionTable.getTemProName()))+
                                "、"+matched+matchContent+"失败\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*失败逻辑*/
            ScanLogicMethods scanLogicMethods = new ScanLogicMethods();
            String falseLogic = scanLogicMethods.falseLogic(switchParameters, totalQuestionTable,
                    return_information_array, current_Round_Extraction_String, extractInformation_string,
                    line_n, firstID, problemScanLogicList, currentID,
                    insertsInteger, loop, numberOfCycles, problemScanLogic);

            return falseLogic;

        }
    }


    /*Inspection Completed*/
    /**
     * @method: 匹配方法  精确匹配 模糊匹配 不存在
     *
     * @Param:
     *
     * matched : 精确匹配    取词方式
     * information_line_n：交换机返回信息行
     * matchContent：数据库 关键词
     * @return: boolean
     */
    public static boolean matchAnalysis(String matchType,String returnString,String matchString){

        switch(matchType){
            case "精确匹配" :

                if ((" "+returnString+" ").indexOf(" "+matchString+" ") != -1){
                    return true;
                }else {
                    return false;
                }

                /*//先模糊匹配 看是否存在
                int indexPosition = returnString.indexOf(matchString);
                if (indexPosition!=-1){//模糊匹配 存在
                    if ((" "+returnString+" ").indexOf(" "+matchString+" ") != -1){
                        return true;
                    }else {
                        return false;
                    }
                }else {
                    return false;
                }*/

            case "模糊匹配" :

                if (returnString.indexOf(matchString)!=-1){
                    return true;
                }else {
                    return false;
                }

            case "不存在" :

                if (returnString.indexOf(matchString)!=-1){
                    return false;
                }else {
                    return true;
                }

            default :
                return false;
        }
    }


    /*Inspection Completed*/
    /**
    * @Description 取词逻辑方法
    * @author charles
    * @createTime 2023/11/20 10:31
    * @desc
    * @param switchParameters 交换机信息
     * @param action  取词：取词、取词full、品牌、型号、内部固件版本、子版本号
     * @param information_line_n 交换机返回信息行
     * @param matchContent  匹配关键词
     * @param totalQuestionTable 问题表数据
     * @param return_information_array 发送命令，交换机返回信息集合
     * @param current_Round_Extraction_String
     * @param extractInformation_string
     * @param line_n 光标
     * @param firstID 第一条分析ID
     * @param problemScanLogicList 分析逻辑
     * @param currentID 当前分析ID
     * @param insertsInteger 插入数据次数
     * @param loop 记录循环次数，经过了几次循环次数
     * @param numberOfCycles 最大循环次数
     * @param problemScanLogic
     * @param relativePosition_line
     * @return
    */
    public String LogicalMethodofWordExtraction(
            SwitchParameters switchParameters,
            String action, String information_line_n, String matchContent,
            TotalQuestionTable totalQuestionTable,
            String[] return_information_array, String current_Round_Extraction_String, String extractInformation_string,
            int line_n, String firstID , List<ProblemScanLogic> problemScanLogicList, String currentID,
            Integer insertsInteger, Integer loop,Integer numberOfCycles,ProblemScanLogic problemScanLogic,
            String relativePosition_line) {

        //取词数  取词的时候没有了通过四项基本信息名称获取数据
        /*String wordSelection_string = null;
        if (action.equals("品牌")){
            wordSelection_string = switchParameters.getDeviceBrand();
        }else if (action.equals("型号")){
            wordSelection_string = switchParameters.getDeviceModel();
            // todo 内部固件版本
        }else if (action.equals("内部固件版本")){
            wordSelection_string = switchParameters.getFirmwareVersion();
        }else if (action.equals("子版本号")){
            wordSelection_string = switchParameters.getSubversionNumber();
        }else {
            //取词操作
            wordSelection_string = FunctionalMethods.wordSelection(
                    information_line_n,matchContent, //返回信息的一行     matchContent.trim() 提取关键字
                    relativePosition_line,problemScanLogic.getrPosition(), problemScanLogic.getLength()); //位置 长度WLs
        }*/

        //取词操作
        String wordSelection_string = FunctionalMethods.wordSelection(
                information_line_n,matchContent, //返回信息的一行     matchContent.trim() 提取关键字
                relativePosition_line,problemScanLogic.getrPosition(), problemScanLogic.getLength()); //位置 长度WLs

        /** 取词逻辑只有成功，但是如果取出为空 则为 取词失败 */
        if (wordSelection_string == null){

            /* action.indexOf("full") != -1 取词包含 full  则 说明是全文取词 */
            /* action 可能为 ： 取词、取词full*/
            if (action.indexOf("full") != -1){
                return "continue";
            }

            /* 告警、异常信息写入 */
            try {
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"TrueAndFalse:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        "问题类型:"+(totalQuestionTable==null?"获取交换机基本信息":(totalQuestionTable.getTypeProblem()+ "问题名称:"+totalQuestionTable.getTemProName()))+
                        "、取词"+problemScanLogic.getWordName()+"失败\r\n");
                PathHelper.writeDataToFile(
                        "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题类型:"+(totalQuestionTable==null?"获取交换机基本信息":(totalQuestionTable.getTypeProblem()+ "问题名称:"+totalQuestionTable.getTemProName()))+
                                "、取词"+problemScanLogic.getWordName()+"失败\r\n"
                );
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "取词失败!";

        }



        try {

            /* 告警、异常信息写入 */
            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }
            WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"TrueAndFalse:" +
                    "IP地址为:"+switchParameters.getIp()+","+
                    "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                    "问题类型:"+(totalQuestionTable==null?"获取交换机基本信息":(totalQuestionTable.getTypeProblem()+ "问题名称:"+totalQuestionTable.getTemProName()))+
                    "、取词"+problemScanLogic.getWordName()+"成功\r\n");
            PathHelper.writeDataToFile(
                    "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题类型:"+(totalQuestionTable==null?"获取交换机基本信息":(totalQuestionTable.getTypeProblem()+ "问题名称:"+totalQuestionTable.getTemProName()))+
                            "、取词"+problemScanLogic.getWordName()+"成功\r\n"
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*判断 字符串 最后一位 是否为 . 或者 ,  去掉*/
        wordSelection_string = FunctionalMethods.judgeResultWordSelection(wordSelection_string);

        //problemScanLogic.getWordName() 取词名称
        //problemScanLogic.getExhibit() 是否可以显示
        //wordSelection_string 取词内容
        /*自定义分隔符*/
        String customDelimiter = (String) CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());

        extractInformation_string = extractInformation_string +problemScanLogic.getWordName()+customDelimiter+problemScanLogic.getExhibit()+customDelimiter+ wordSelection_string+customDelimiter;
        current_Round_Extraction_String = current_Round_Extraction_String +problemScanLogic.getWordName()+customDelimiter+problemScanLogic.getExhibit()+customDelimiter+ wordSelection_string+customDelimiter;

        /*成功逻辑*/
        ScanLogicMethods scanLogicMethods = new ScanLogicMethods();
        String trueLogic = scanLogicMethods.trueLogic(switchParameters, totalQuestionTable,
                return_information_array, current_Round_Extraction_String, extractInformation_string,
                line_n, firstID, problemScanLogicList, currentID,
                insertsInteger, loop, numberOfCycles, problemScanLogic);

        return trueLogic;
    }

    /*Inspection Completed*/
    /**
    * @Description 比较逻辑方法
    * @author charles
    * @createTime 2023/11/20 10:31
    * @desc
    * @param switchParameters
     * @param compare
     *
     * @param totalQuestionTable
     * @param return_information_array
     * @param current_Round_Extraction_String
     * @param extractInformation_string
     *
     * @param line_n
     * @param firstID
     * @param problemScanLogicList
     * @param currentID
     * @param insertsInteger
     * @param loop
     * @param numberOfCycles
     *
     * @param problemScanLogic
     * @return
    */
    public String ComparativeLogicMethod(
            SwitchParameters switchParameters,
            String compare,
            TotalQuestionTable totalQuestionTable,
            String[] return_information_array, String current_Round_Extraction_String, String extractInformation_string,
            int line_n, String firstID , List<ProblemScanLogic> problemScanLogicList, String currentID,
            Integer insertsInteger, Integer loop,Integer numberOfCycles,ProblemScanLogic problemScanLogic) {

        /** 比较 */
        boolean compare_boolean = FunctionalMethods.compareVersion(switchParameters,compare,current_Round_Extraction_String);

        if (compare_boolean){

            /* 告警、异常信息写入 */
            try {
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"TrueAndFalse:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        "问题类型:"+(totalQuestionTable==null?"获取交换机基本信息":(totalQuestionTable.getTypeProblem()+ "问题名称:"+totalQuestionTable.getTemProName()))+
                        "、比较"+problemScanLogic.getCompare()+"成功\r\n");
                PathHelper.writeDataToFile(
                        "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题类型:"+(totalQuestionTable==null?"获取交换机基本信息":(totalQuestionTable.getTypeProblem()+ "问题名称:"+totalQuestionTable.getTemProName()))+
                                "、比较"+problemScanLogic.getCompare()+"成功\r\n"
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {

            /* 告警、异常信息写入 */
            try {
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                WebSocketService.sendMessage(switchParameters.getLoginUser().getUsername(),"TrueAndFalse:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        "问题类型:"+(totalQuestionTable==null?"获取交换机基本信息":(totalQuestionTable.getTypeProblem()+ "问题名称:"+totalQuestionTable.getTemProName()))+
                        "、比较"+problemScanLogic.getCompare()+"失败\r\n");
                PathHelper.writeDataToFile(
                        "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题类型:"+(totalQuestionTable==null?"获取交换机基本信息":(totalQuestionTable.getTypeProblem()+ "问题名称:"+totalQuestionTable.getTemProName()))+
                                "、比较"+problemScanLogic.getCompare()+"失败\r\n"
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        ScanLogicMethods scanLogicMethods = new ScanLogicMethods();
        if (compare_boolean){
            /*成功逻辑*/
            String trueLogic = scanLogicMethods.trueLogic(switchParameters, totalQuestionTable,
                    return_information_array, current_Round_Extraction_String, extractInformation_string,
                    line_n, firstID, problemScanLogicList, currentID,
                    insertsInteger, loop, numberOfCycles, problemScanLogic);
            return trueLogic;
        }else {
            /*失败逻辑*/
            String falseLogic = scanLogicMethods.falseLogic(switchParameters, totalQuestionTable,
                    return_information_array, current_Round_Extraction_String, extractInformation_string,
                    line_n, firstID, problemScanLogicList, currentID,
                    insertsInteger, loop, numberOfCycles, problemScanLogic);
            return falseLogic;
        }


    }


    /*Inspection Completed*/
    /**
     * @Description  扫描分析成功逻辑
     * @desc
     * @param switchParameters 交换机登录信息
     * @param totalQuestionTable  问题表
     * @param return_information_array  交换机返回信息
     * @param current_Round_Extraction_String 单词提取信息
     * @param extractInformation_string 完整取词信息
     * @param line_n 扫描光标行数
     * @param firstID 分析逻辑开始ID
     * @param problemScanLogicList  分析逻辑数据集合
     * @param currentID 当前分析ID
     * @param insertsInteger 插入数据次数
     * @param loop 循环次数
     * @param numberOfCycles  最大循环次数
     * @param problemScanLogic  分析逻辑
     *
     * @return 返回提取信息 或者 null
     */
    public  String trueLogic(SwitchParameters switchParameters,TotalQuestionTable totalQuestionTable,
                            String[] return_information_array, String current_Round_Extraction_String, String extractInformation_string,
                            int line_n, String firstID, List<ProblemScanLogic> problemScanLogicList, String currentID,
                            Integer insertsInteger, Integer loop, Integer numberOfCycles, ProblemScanLogic problemScanLogic) {

        SwitchInteraction switchInteraction = new SwitchInteraction();

        /*判断 命令字段是否为空 不为空 则 进行 发送命令进行分析*/
        if (problemScanLogic.gettComId()!=null && problemScanLogic.gettComId()!=""){

            /**发送命令 返回结果*/
            CommandReturn commandReturn = switchInteraction.executeScanCommandByCommandId(switchParameters,totalQuestionTable,problemScanLogic.gettComId());

            if (!commandReturn.isSuccessOrNot()){
                /*交换机返回错误信息处理*/
                return null;
            }

            /** 分析 */
            String analysisReturnResults_String = switchInteraction.analysisReturnResults(switchParameters,totalQuestionTable,
                    commandReturn,current_Round_Extraction_String, extractInformation_string);

            return analysisReturnResults_String;
        }

        /** 判断 下一条分析ID 是否为空
         * 不为空 则继续进行分析*/
        if (problemScanLogic.gettNextId()!=null && problemScanLogic.gettNextId()!=""){

            /*继续进行分析*/
            String ProblemScanLogic_returnstring = switchInteraction.selectProblemScanLogicById(switchParameters,totalQuestionTable,
                    return_information_array,current_Round_Extraction_String,extractInformation_string,
                    line_n,firstID,problemScanLogicList,problemScanLogic.gettNextId(),insertsInteger, loop, numberOfCycles);

            //如果返回信息为null
            if ( ProblemScanLogic_returnstring != null){

                //内分析传到上一层
                //extractInformation_string 是 分析的 总提取信息记录 所以要把内层的记录 传给 外层
                //extractInformation_string = ProblemScanLogic_returnstring;

                return ProblemScanLogic_returnstring;

            }

            return ProblemScanLogic_returnstring;

        }
        return null;
    }


    /*Inspection Completed*/
    /**
    * @Description  扫描分析失败逻辑
    * @author charles
    * @createTime 2023/11/20 10:48
    * @desc
    * @param switchParameters
     * @param totalQuestionTable
     * @param return_information_array
     * @param current_Round_Extraction_String
     * @param extractInformation_string
     * @param line_n
     * @param firstID
     * @param problemScanLogicList
     * @param currentID
     * @param insertsInteger
     * @param loop
     * @param numberOfCycles
     * @param problemScanLogic
     * @return
    */
    public  String falseLogic(SwitchParameters switchParameters, TotalQuestionTable totalQuestionTable,
                             String[] return_information_array, String current_Round_Extraction_String, String extractInformation_string,
                             int line_n, String firstID, List<ProblemScanLogic> problemScanLogicList, String currentID,
                             Integer insertsInteger, Integer loop, Integer numberOfCycles, ProblemScanLogic problemScanLogic) {

        SwitchInteraction switchInteraction = new SwitchInteraction();

        /*判断 命令字段是否为空 不为空 则 进行 发送命令进行分析*/
        if (problemScanLogic.getfComId()!=null && problemScanLogic.getfComId()!=""){

            /**发送命令 返回结果*/
            CommandReturn commandReturn  = switchInteraction.executeScanCommandByCommandId(switchParameters,totalQuestionTable,problemScanLogic.getfComId());

            if (!commandReturn.isSuccessOrNot()){
                /*交换机返回错误信息处理*/
                return null;
            }

            /** 分析 */
            String analysisReturnResults_String = switchInteraction.analysisReturnResults(switchParameters,totalQuestionTable,
                    commandReturn ,  current_Round_Extraction_String,  extractInformation_string);
            return analysisReturnResults_String;
        }

        /* 判断 下一条分析ID 是否为空
        不为空 则继续进行分析*/
        if (problemScanLogic.getfNextId()!=null && problemScanLogic.getfNextId()!=null){

            String ProblemScanLogic_returnstring = switchInteraction.selectProblemScanLogicById(switchParameters,totalQuestionTable,
                    return_information_array,current_Round_Extraction_String,extractInformation_string,
                    line_n,firstID,problemScanLogicList,
                    problemScanLogic.getfNextId(), // problemScanLogic.getfNextId(); 下一条frue分析ID
                    insertsInteger, loop, numberOfCycles);

            //如果返回信息为null
            if (ProblemScanLogic_returnstring!=null){
                //内分析传到上一层
                //extractInformation_string 是 分析的 总提取信息记录 所以要把内层的记录 传给 外层
                // extractInformation_string = ProblemScanLogic_returnstring;
                return ProblemScanLogic_returnstring;
            }

            return ProblemScanLogic_returnstring;
        }

        return null;
    }


}
