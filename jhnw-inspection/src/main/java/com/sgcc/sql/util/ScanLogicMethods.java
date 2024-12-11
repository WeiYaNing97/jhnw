package com.sgcc.sql.util;

import com.sgcc.share.domain.Constant;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.*;
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
     * @param return_information_List 发送命令，交换机返回信息集合
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
                                             List<String> return_information_List, String current_Round_Extraction_String, String extractInformation_string,
                                             int line_n, String firstID , List<ProblemScanLogic> problemScanLogicList, String currentID,
                                             Integer insertsInteger, Integer loop,Integer numberOfCycles,ProblemScanLogic problemScanLogic,
                                             String matching_logic, int num) {
        // 中断检测判断线程是否关闭
        if ( WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            return null;
        }

        /**
         * 1：根据匹配方式（精确匹配、模糊匹配、不存在） 得到匹配匹配结果（成功:true 失败:false）
         *
         * matched: 精确匹配 模糊匹配 不存在
         * information_line_n：交换机返回信息行 matchContent：匹配关键词
         * */
        boolean matchAnalysis_true_false = matchAnalysis(matched, information_line_n, matchContent.trim());

        /**
         * 2: 根据匹配结果（成功:true 失败:false）执行对应的逻辑
         *    2.1：匹配成功后 进行成功逻辑
         *    2.2：匹配失败后
         *      2.2.1:判断是否是全文匹配，如果是，则返回"continue"，继续遍历交换机返回结果下一行信息。
         *      2.2.2:如果不是，则进行失败逻辑
         */
        //如果最终逻辑成功 则把 匹配成功的行数 付给变量 line_n
        if (matchAnalysis_true_false){
            // 应张主任要求，每一条成功或者失败都要记录
            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }
            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                    "TrueAndFalse:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题类型:"+(totalQuestionTable==null?"获取交换机基本信息":(totalQuestionTable.getTypeProblem()+ "问题名称:"+totalQuestionTable.getTemProName()))+"、"+matched+matchContent+"成功\r\n");

            //匹配成功后，进行成功逻辑
            ScanLogicMethods scanLogicMethods = new ScanLogicMethods();
            String trueLogic = scanLogicMethods.trueLogic(switchParameters, totalQuestionTable,
                    return_information_List, current_Round_Extraction_String, extractInformation_string,
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
                    && num < return_information_List.size()-1){
                return "continue";
            }
            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }
            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                    "TrueAndFalse:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题类型:"+(totalQuestionTable==null?"获取交换机基本信息":(totalQuestionTable.getTypeProblem()+ "问题名称:"+totalQuestionTable.getTemProName()))+
                            "、"+matched+matchContent+"失败\r\n");
            //进行失败逻辑
            ScanLogicMethods scanLogicMethods = new ScanLogicMethods();
            String falseLogic = scanLogicMethods.falseLogic(switchParameters, totalQuestionTable,
                    return_information_List, current_Round_Extraction_String, extractInformation_string,
                    line_n, firstID, problemScanLogicList, currentID,
                    insertsInteger, loop, numberOfCycles, problemScanLogic);
            return falseLogic;
        }
    }



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
     * @param return_information_List 发送命令，交换机返回信息集合
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
            List<String> return_information_List, String current_Round_Extraction_String, String extractInformation_string,
            int line_n, String firstID , List<ProblemScanLogic> problemScanLogicList, String currentID,
            Integer insertsInteger, Integer loop,Integer numberOfCycles,ProblemScanLogic problemScanLogic,
            String relativePosition_line) {

        // 中断检查，判断线程是否关闭
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            return null;
        }

        /**
         * 1.取词操作
         *   1.1 判断action是否包含all，包含则说明是取全文，循环遍历所有返回信息并拼接为字符串。
         *   1.2 如果不包含all，则按取词条件取词。
         *   取词成功或失败，都要记录取词结果
         *   取词包含full，则说明是全文取词，循环遍历所有返回信息，按照取词条件取词。
         *   1.3 如果取词成功，则进行下一步逻辑
         */
        //取词操作
        StringBuffer wordSelection_string = new StringBuffer();
        if (action.indexOf("all") != -1){
            for (int i = 0; i < return_information_List.size(); i++) {
                wordSelection_string.append(return_information_List.get(i)).append("\r\n");
            }
            wordSelection_string = StringBufferUtils.substring(wordSelection_string,0,wordSelection_string.length()-2);
        }else {
            String s = FunctionalMethods.wordSelection(
                    information_line_n, matchContent, //返回信息的一行     matchContent.trim() 提取关键字
                    relativePosition_line, problemScanLogic.getrPosition(), problemScanLogic.getLength());//位置 长度WLs
            //取词操作
            wordSelection_string = new StringBuffer(s);
        }

        /** 取词逻辑只有成功，但是如果取出为空 则为 取词失败 */
        if (wordSelection_string == null || wordSelection_string.length() == 0){

            /* action.indexOf("full") != -1 取词包含 full  则 说明是全文取词 */
            /* action 可能为 ： 取词、取词full、取词all*/
            if (action.indexOf("full") != -1){
                return "continue";
            }else {
                //进行失败处理
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }
                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                        "TrueAndFalse:" +
                                "IP地址为:"+switchParameters.getIp()+","+
                                "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                                "问题类型:"+(totalQuestionTable==null?"获取交换机基本信息":(totalQuestionTable.getTypeProblem()+ "问题名称:"+totalQuestionTable.getTemProName()))+
                                "、取词"+problemScanLogic.getWordName()+"失败\r\n");
                return "取词失败!";
            }
        }
        /*取词成功记录*/
        String subversionNumber = switchParameters.getSubversionNumber();
        if (subversionNumber!=null){
            subversionNumber = "、"+subversionNumber;
        }
        AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                "TrueAndFalse:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        "问题类型:"+(totalQuestionTable==null?"获取交换机基本信息":(totalQuestionTable.getTypeProblem()+ "问题名称:"+totalQuestionTable.getTemProName()))+
                        "、取词"+problemScanLogic.getWordName()+"成功\r\n");


        /**
         * 2.取词结果进行整理
         *   2.1：去到结尾的.或者,
         *   2.2：提取配置文件中的自定义分隔符，然后拼接字符串，拼接出本轮扫描提取的词、和全部循环扫描提取到的词。
         */
        /*判断字符串最后一位是否为.或者,去掉*/
        wordSelection_string = FunctionalMethods.judgeResultWordSelection(wordSelection_string);
        //problemScanLogic.getWordName() 取词名称
        //problemScanLogic.getExhibit() 是否可以显示
        //wordSelection_string 取词内容
        /*自定义分隔符*/
        String customDelimiter = (String) CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());
        extractInformation_string = extractInformation_string +problemScanLogic.getWordName()+customDelimiter+problemScanLogic.getExhibit()+customDelimiter+ wordSelection_string+customDelimiter;
        current_Round_Extraction_String = current_Round_Extraction_String +problemScanLogic.getWordName()+customDelimiter+problemScanLogic.getExhibit()+customDelimiter+ wordSelection_string+customDelimiter;

        /**
         * 3.取词成功后，进行成功逻辑
         * */
        ScanLogicMethods scanLogicMethods = new ScanLogicMethods();
        String trueLogic = scanLogicMethods.trueLogic(switchParameters, totalQuestionTable,
                return_information_List, current_Round_Extraction_String, extractInformation_string,
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
     * @param return_information_List
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
            List<String> return_information_List, String current_Round_Extraction_String, String extractInformation_string,
            int line_n, String firstID , List<ProblemScanLogic> problemScanLogicList, String currentID,
            Integer insertsInteger, Integer loop,Integer numberOfCycles,ProblemScanLogic problemScanLogic) {
        // 中断检查，判断线程是否关闭
        if ( WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            return null;
        }

        /**
         * 1：比较逻辑，如果比较成功，则进行下一步逻辑
         *    如果比较失败，则进行失败逻辑
         *    如果比较成功，则进行成功逻辑
         * */
        boolean compare_boolean = FunctionalMethods.compareVersion(switchParameters,compare,current_Round_Extraction_String);

        /**
         * 2: 比较成功和失败，都要记录比较结果
         */
        String truefalse = compare_boolean ? "成功" : "失败";
        String subversionNumber = switchParameters.getSubversionNumber();
        if (subversionNumber!=null){
            subversionNumber = "、"+subversionNumber;
        }
        AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                "TrueAndFalse:" +
                        "IP地址为:"+switchParameters.getIp()+","+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                        "问题类型:"+(totalQuestionTable==null?"获取交换机基本信息":(totalQuestionTable.getTypeProblem()+ "问题名称:"+totalQuestionTable.getTemProName()))+
                        "、比较" + problemScanLogic.getCompare() + truefalse + "\r\n");

        /**
         * 3: 比较成功 则进行成功逻辑 否则 进入失败逻辑
         */
        ScanLogicMethods scanLogicMethods = new ScanLogicMethods();
        if (compare_boolean){
            /*成功逻辑*/
            String trueLogic = scanLogicMethods.trueLogic(switchParameters, totalQuestionTable,
                    return_information_List, current_Round_Extraction_String, extractInformation_string,
                    line_n, firstID, problemScanLogicList, currentID,
                    insertsInteger, loop, numberOfCycles, problemScanLogic);
            return trueLogic;
        }else {
            /*失败逻辑*/
            String falseLogic = scanLogicMethods.falseLogic(switchParameters, totalQuestionTable,
                    return_information_List, current_Round_Extraction_String, extractInformation_string,
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
     * @param return_information_List  交换机返回信息
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
                            List<String> return_information_List, String current_Round_Extraction_String, String extractInformation_string,
                            int line_n, String firstID, List<ProblemScanLogic> problemScanLogicList, String currentID,
                            Integer insertsInteger, Integer loop, Integer numberOfCycles, ProblemScanLogic problemScanLogic) {
        // 中断检测,判断线程是否关闭
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            return null;
        }


        /**
         * 1：进入成功逻辑，只需要关注当前分析数据中的 成功下一命令字段和 成功下一条分析ID
         * 首先判断成功逻辑中 下一命令字段是否为空 不为空则 发送命令进行分析
         * 否则 判断成功下一条分析ID是否为空 不为空 则继续进行分析
         *  否则 告警写入日志 并返回null
         */
        SwitchInteraction switchInteraction = new SwitchInteraction();
        /*判断 命令字段是否为空 不为空 则 进行 发送命令进行分析*/
        if (problemScanLogic.gettComId()!=null && problemScanLogic.gettComId()!=""){
            /**
             * 发送命令进行分析 返回结果
             * */
            CommandReturn commandReturn = switchInteraction.executeScanCommandByCommandId(switchParameters,totalQuestionTable,problemScanLogic.gettComId());
            if (commandReturn == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
                return null;
            }else if (commandReturn == null || !commandReturn.isSuccessOrNot()){
                /*交换机返回错误信息处理*/
                return null;
            }
            /** 分析 */
            String analysisReturnResults_String = switchInteraction.analysisReturnResults(switchParameters,totalQuestionTable,
                    commandReturn,current_Round_Extraction_String, extractInformation_string);
            return analysisReturnResults_String;


        }else if (problemScanLogic.gettNextId()!=null && problemScanLogic.gettNextId()!=""){
            /** 判断 下一条分析ID 是否为空
             * 不为空 则继续进行分析*/
            /*继续进行分析*/
            String ProblemScanLogic_returnstring = switchInteraction.selectProblemScanLogicById(switchParameters,totalQuestionTable,
                    return_information_List,current_Round_Extraction_String,extractInformation_string,
                    line_n,firstID,problemScanLogicList,problemScanLogic.gettNextId(),insertsInteger, loop, numberOfCycles);
            return ProblemScanLogic_returnstring;


        }else {
            String subversionNumber = switchParameters.getSubversionNumber();
            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }
            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                    "TrueAndFalse:" +
                            "IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题类型:成功逻辑和失败逻辑,皆为空,分析逻辑错误");
            return null;
        }
    }


    /*Inspection Completed*/
    /**
    * @Description  扫描分析失败逻辑
    * @author charles
    * @createTime 2023/11/20 10:48
    * @desc
    * @param switchParameters
     * @param totalQuestionTable
     * @param return_information_List
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
                             List<String> return_information_List, String current_Round_Extraction_String, String extractInformation_string,
                             int line_n, String firstID, List<ProblemScanLogic> problemScanLogicList, String currentID,
                             Integer insertsInteger, Integer loop, Integer numberOfCycles, ProblemScanLogic problemScanLogic) {
        // 中断检查判断线程是否关闭
        if (WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
            return null;
        }

        /**
         * 1：进入失败逻辑，只需要关注当前分析数据中的 失败下一命令字段和 失败下一条分析ID
         * 首先判断失败逻辑中 下一命令字段是否为空 不为空则 发送命令进行分析
         * 否则 判断失败下一条分析ID是否为空 不为空 则继续进行分析
         *  否则 告警写入日志 并返回null
         */
        SwitchInteraction switchInteraction = new SwitchInteraction();

        /**下一命令字段是否为空 不为空则 发送命令进行分析 返回结果*/
        if (problemScanLogic.getfComId()!=null && problemScanLogic.getfComId()!=""){
            CommandReturn commandReturn  = switchInteraction.executeScanCommandByCommandId(switchParameters,totalQuestionTable,problemScanLogic.getfComId());
            // 判断线程是否关闭
            if (commandReturn == null && WorkThreadMonitor.getShutdown_Flag(switchParameters.getScanMark())){
                return null;
            }else if (commandReturn == null || !commandReturn.isSuccessOrNot()){
                /*交换机返回错误信息处理*/
                return null;
            }
            String analysisReturnResults_String = switchInteraction.analysisReturnResults(switchParameters,totalQuestionTable,
                    commandReturn ,  current_Round_Extraction_String,  extractInformation_string);
            return analysisReturnResults_String;
        }


        /**判断下一条分析ID是否为空不为空则继续进行分析*/
        if (problemScanLogic.getfNextId()!=null && problemScanLogic.getfNextId()!=null){
            String ProblemScanLogic_returnstring = switchInteraction.selectProblemScanLogicById(switchParameters,totalQuestionTable,
                    return_information_List,current_Round_Extraction_String,extractInformation_string,
                    line_n,firstID,problemScanLogicList,
                    problemScanLogic.getfNextId(), // problemScanLogic.getfNextId(); 下一条frue分析ID
                    insertsInteger, loop, numberOfCycles);
            return ProblemScanLogic_returnstring;
        }
        return null;
    }
}
