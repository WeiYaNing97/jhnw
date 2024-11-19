package com.sgcc.sql.controller;
import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSON;
import com.sgcc.advanced.controller.*;
import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.bean.BeanUtils;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.domain.*;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.service.IInformationService;
import com.sgcc.share.service.IReturnRecordService;
import com.sgcc.share.service.ISwitchScanResultService;
import com.sgcc.share.switchboard.ConnectToObtainInformation;
import com.sgcc.share.switchboard.SwitchIssueEcho;
import com.sgcc.share.util.*;
import com.sgcc.share.webSocket.WebSocketService;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.*;
import com.sgcc.sql.thread.DirectionalScanThreadPool;
import com.sgcc.sql.thread.ScanFixedThreadPool;
import com.sgcc.sql.util.InspectionMethods;
import com.sgcc.sql.util.ScanLogicMethods;
import io.swagger.annotations.*;
import org.apache.poi.hpsf.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 与交换机交互方法类
 * @date 2022年01月05日 14:
 *
 *
 */
@Api(tags ="扫描相关")
@RestController
@RequestMapping("/sql/SwitchInteraction")
@Transactional(rollbackFor = Exception.class)

public class SwitchInteraction {

    @Autowired
    private  ICommandLogicService commandLogicService;
    @Autowired
    private  IReturnRecordService returnRecordService;
    @Autowired
    private  IProblemScanLogicService problemScanLogicService;
    @Autowired
    private  IValueInformationService valueInformationService;
    @Autowired
    private  ISwitchProblemService switchProblemService;
    @Autowired
    private  ITotalQuestionTableService totalQuestionTableService;
    @Autowired
    private  IBasicInformationService basicInformationService;
    @Autowired
    private  ISwitchScanResultService switchScanResultService;
    @Autowired
    private  IInformationService informationService;
    @Autowired
    private  IFormworkService formworkService;

    /*全面扫描线程池*/
    HashMap<String,ScanFixedThreadPool> scanFixedThreadPoolHashMap = new HashMap<>();
    /* 专项扫描线程池*/
    HashMap<String,DirectionalScanThreadPool> directionalScanThreadPoolHashMap = new HashMap<>();

    /**
    * @Description预执行获取交换机基本信息
    * @desc
    * @param ip
     * @param name
     * @param password
     * @param port
     * @param mode 连接方式
     * @param configureCiphers	 配置密码
     * @param command	 命令数组
     * @param pojoList	分析数据集合
     * @return
    */
    @ApiOperation("预执行获取交换机基本信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ip", value = "交换机IP", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "name", value = "交换机用户名", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "password", value = "<PASSWORD>", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "port", value = "交换机端口", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "mode", value = "连接方式", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "configureCiphers", value = "配置密码", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "command", value = "命令数组", dataTypeClass = Array.class, required = true),
            @ApiImplicitParam(name = "pojoList", value = "分析数据集合", dataTypeClass = List.class, required = true)
    })
    @PostMapping("testToObtainBasicInformation/{ip}/{name}/{password}/{port}/{mode}/{configureCiphers}/{command}")
    @MyLog(title = "测试获取交换机基本信息逻辑执行结果", businessType = BusinessType.OTHER)
    public String testToObtainBasicInformation(@PathVariable String ip,@PathVariable String name,@PathVariable String password,
                                               @PathVariable String port,@PathVariable String mode,
                                               @PathVariable String configureCiphers, @PathVariable String[] command,@RequestBody List<String> pojoList) {

        /*交换机信息类  为了减少方法调用间传的参数*/
        SwitchParameters switchParameters = new SwitchParameters();
        switchParameters.setMode(mode);
        switchParameters.setIp(ip);
        switchParameters.setName(name);
        switchParameters.setPassword(password);
        switchParameters.setConfigureCiphers(configureCiphers);
        switchParameters.setPort(Integer.valueOf(port).intValue());
        switchParameters.setScanningTime(MyUtils.getDate("yyyy-MM-dd HH:mm:ss"));/*用户扫描时间*/
        switchParameters.setLoginUser(SecurityUtils.getLoginUser());/*登录用户信息  loginUser*/

        /*连接交换机   返回交换机信息 */
        AjaxResult requestConnect_ajaxResult = null;
        for (int number = 0; number <1 ; number++){
            /* 连接交换机返回 交换机信息类 */
            ConnectToObtainInformation connectToObtainInformation = new  ConnectToObtainInformation();
            requestConnect_ajaxResult = connectToObtainInformation.requestConnect(switchParameters);
            if (!(requestConnect_ajaxResult.get("msg").equals("交换机连接失败"))){
                break;
            }
        }

        //如果返回为 交换机连接失败 则连接交换机失败
        /*交换机连接失败 前端显示连接失败原因 写入日志*/
        if(requestConnect_ajaxResult.get("msg").equals("交换机连接失败")){

            List<String> loginError = (List<String>) requestConnect_ajaxResult.get("loginError");

            if (loginError != null || loginError.size() != 0){
                for (int number = 1;number<loginError.size();number++){

                    String loginErrorString = loginError.get(number);
                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "交换机连接",
                            "风险:"+switchParameters.getIp()+loginErrorString+"\r\n");

                }
            }

            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), null, "交换机连接",
                    "风险:"+ switchParameters.getIp() + "交换机连接失败\r\n");

            return "交换机连接失败";
        }

        //解析返回参数 data
        switchParameters = (SwitchParameters) requestConnect_ajaxResult.get("data");

        //是否连接成功 返回信息集合的 第一项 为 是否连接成功
        //如果连接成功
        if(requestConnect_ajaxResult.get("msg").equals("操作成功")){

            //密码 MD5 加密
            String passwordDensificationAndSalt = EncryptUtil.densificationAndSalt(switchParameters.getPassword());
            switchParameters.setPassword(passwordDensificationAndSalt);//用户密码
            //配置密码 MD5 加密
            String configureCiphersDensificationAndSalt = EncryptUtil.densificationAndSalt(switchParameters.getConfigureCiphers());
            switchParameters.setConfigureCiphers(configureCiphersDensificationAndSalt);//用户密码

            /* 因为没有走数据库 所以要 处理前端传入数据 组成集合 */
            List<ProblemScanLogic> problemScanLogicList = new ArrayList<>();
            /*遍历分析逻辑字符串集合：List<String> pojoList
            通过 调用 analysisProblemScanLogic 方法 将字符串 转化为 分析逻辑实体类，
            并放入 分析逻辑实体类集合 List<ProblemScanLogic> problemScanLogicList。*/
            DefinitionProblemController definitionProblemController = new DefinitionProblemController();
            for (String pojo:pojoList){
                //本条是分析 下一条是 分析
                /*ProblemScanLogic problemScanLogic = InspectionMethods.analysisProblemScanLogic(pojo, "分析");
                problemScanLogicList.add(problemScanLogic);*/

                /* 预执行获取交换机基本信息 不往数据库中插数据 所以问题编码 和 地区编码 不添加*/
                AnalyzeConvertJson analyzeConvertJson = definitionProblemController.getAnalyzeConvertJson( "" , pojo, "分析");
                ProblemScanLogic problemScanLogic = new ProblemScanLogic();
                problemScanLogic = (ProblemScanLogic) definitionProblemController.copyProperties( analyzeConvertJson , problemScanLogic);
                problemScanLogicList.add(problemScanLogic);

            }

            //将相同ID  时间戳 的 实体类 放到一个实体
            List<ProblemScanLogic> problemScanLogics = InspectionMethods.definitionProblem(problemScanLogicList);

            //获取交换机基本信息
            //getBasicInformationList 通过 特定方式 获取 基本信息
            //getBasicInformationList 通过扫描方式 获取 基本信息
            AjaxResult basicInformationList_ajaxResult = getBasicInformationTest(switchParameters,command,problemScanLogics);   //getBasicInformationList
            switchParameters = (SwitchParameters) basicInformationList_ajaxResult.get("data");

            /*关闭连接交换机*/
            if (switchParameters.getMode().equalsIgnoreCase("ssh")){
                switchParameters.getConnectMethod().closeConnect(switchParameters.getSshConnect());
            }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
                switchParameters.getTelnetSwitchMethod().closeSession(switchParameters.getTelnetComponent());
            }

            return "设备品牌:"+switchParameters.getDeviceBrand()+"设备型号:"+switchParameters.getDeviceModel()
                    +"内部固件版本:"+switchParameters.getFirmwareVersion()+"子版本号:"+switchParameters.getSubversionNumber();

        }else {

            List<String> loginError = (List<String>) requestConnect_ajaxResult.get("loginError");

            if (loginError != null || loginError.size() != 0){
                for (int number = 1;number<loginError.size();number++){
                    String loginErrorString = loginError.get(number);
                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "交换机连接",
                            "风险:"+switchParameters.getIp()+loginErrorString+"\r\n");
                }
            }

            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "交换机连接",
                    "风险:"+switchParameters.getIp() + "交换机连接失败\r\n");
        }

        return  "交换机连接失败";
    }

    @ApiOperation("模板扫描接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "switchInformation", value = "交换机登录信息集合", dataTypeClass = List.class, required = true),
            @ApiImplicitParam(name = "formworkId", value = "模板ID", dataTypeClass = Long.class, required = true),
            @ApiImplicitParam(name = "scanNum", value = "线程数", dataTypeClass = Long.class, required = true)
    })
    @PostMapping("/formworkScann/{formworkId}/{scanNum}")///{totalQuestionTableId}/{scanNum}
    @MyLog(title = "模板扫描", businessType = BusinessType.OTHER)
    public String formworkScann(@ApiParam(name = "switchInformation", value = "交换机登录信息集合") @RequestBody List<String> switchInformation,@PathVariable  Long formworkId,@PathVariable  Long scanNum) {

        // 获取FormworkService实例
        formworkService = SpringBeanUtil.getBean(IFormworkService.class);

        // 查询问题模板信息
        Formwork formwork = formworkService.selectFormworkById(formworkId);

        // 将模板索引按逗号分割成数组
        String[] formworkSplit = formwork.getFormworkIndex().split(",");

        // 将数组转换为List
        List<String> totalQuestionTableId = Arrays.stream(formworkSplit).collect(Collectors.toList());

        // 调用directionalScann方法进行模板扫描
        String formworkScann = directionalScann(switchInformation, totalQuestionTableId, scanNum);

        // 返回扫描结果
        return formworkScann;
    }



    /***
     * @method: 多线程 专项扫描问题 获取到的是字符串格式的参数 {"ip":"192.168.1.100","name":"admin","password":"admin","mode":"ssh","port":"22"}
     * @Param: [switchInformation]
     * @return: void
     */
    @ApiOperation("专项扫描接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "switchInformation", value = "交换机登录信息集合", dataTypeClass = List.class, required = true),
            @ApiImplicitParam(name = "totalQuestionTableId", value = "问题表ID集合", dataTypeClass = List.class, required = true),
            @ApiImplicitParam(name = "scanNum", value = "线程数", dataTypeClass = Long.class, required = true)
    })
    @PostMapping("/directionalScann/{totalQuestionTableId}/{scanNum}")///{totalQuestionTableId}/{scanNum}
    @MyLog(title = "专项扫描", businessType = BusinessType.OTHER)
    public  String directionalScann(@RequestBody List<String> switchInformation,@PathVariable  List<String> totalQuestionTableId,@PathVariable  Long scanNum) {//@RequestBody List<String> switchInformation,@PathVariable  List<Long> totalQuestionTableId,@PathVariable  Long scanNum

        String simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        /*交换机信息集合*/
        List<SwitchParameters> switchParametersList = new ArrayList<>();
        for (String information:switchInformation){
            SwitchLoginInformation switchLoginInformation = JSON.parseObject(information, SwitchLoginInformation.class);

            SwitchParameters switchParameters = new SwitchParameters();
            switchParameters.setLoginUser(SecurityUtils.getLoginUser());
            switchParameters.setScanningTime(simpleDateFormat);

            BeanUtils.copyBeanProp(switchParameters,switchLoginInformation);
            switchParameters.setPort(Integer.valueOf(switchLoginInformation.getPort()).intValue());
            switchParametersList.add(switchParameters);

        }

        /*交换机问题表集合*/
        List<Long> idScan = new ArrayList<>();

        /*运行分析集合*/
        List<String> advancedName = new ArrayList<>();
        /* 遍历专项扫描问题集合*/
        for (String id:totalQuestionTableId){
            /*判断字符串是否为全数字*/
            if (MyUtils.allIsNumeric(id)){
                /*交换机问题表*/
                idScan.add(Long.valueOf(id).longValue());
            }else {
                /*运行分析*/
                advancedName.add(id);
            }
        }

        /*根据交换机问题表ID集合 查询问题表数据*/
        ParameterSet parameterSet = new ParameterSet();
        parameterSet.setLoginUser(SecurityUtils.getLoginUser());
        Long[] ids = idScan.toArray(new Long[idScan.size()]);
        List<TotalQuestionTable> totalQuestionTables = new ArrayList<>();

        WebSocketService webSocketService = new WebSocketService();

        if (ids.length != 0 ){
            totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);//解决 多线程 service 为null问题
            totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableByIds(ids);
        }else if (ids.length == 0 && advancedName.size() == 0){
            webSocketService.sendMessage(parameterSet.getLoginUser().getUsername(),"接收:"+"扫描结束\r\n");
            return "扫描结束";
        }

        parameterSet.setThreadCount(Integer.valueOf(scanNum+"").intValue());/*线程*/
        parameterSet.setSwitchParameters(switchParametersList);/*交换机信息表*/

        try {
            //boolean isRSA = true; 密码是否 RSA加密
            DirectionalScanThreadPool directionalScanThreadPool = new DirectionalScanThreadPool();
            directionalScanThreadPoolHashMap.put(parameterSet.getLoginUser().getUsername(),directionalScanThreadPool);
            directionalScanThreadPool.switchLoginInformations(parameterSet, totalQuestionTables, advancedName,true);
            directionalScanThreadPoolHashMap.remove(parameterSet.getLoginUser().getUsername());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //传输登陆人姓名 及问题简述
        webSocketService.sendMessage(parameterSet.getLoginUser().getUsername(),"接收:"+"扫描结束\r\n");
        try {
            //插入问题简述及问题路径
            PathHelper.writeDataToFile("接收:"+"扫描结束\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "扫描结束";
    }

    /* 模版、专项扫描终止 */
    @PostMapping("/directionalTerminationScann")
    public void directionalTerminationScann() {
        String username = SecurityUtils.getLoginUser().getUsername();
        DirectionalScanThreadPool directionalScanThreadPool = directionalScanThreadPoolHashMap.get(username);
        directionalScanThreadPool.terminationScanThread();
        directionalScanThreadPoolHashMap.remove(username);
    }

    /*Inspection Completed*/
    /**
    * @Description 多线程扫描全部问题 获取到的是字符串格式的参数 {"ip":"192.168.1.100","name":"admin","password":"admin","mode":"ssh","port":"22"}
    * @createTime 2024/1/29 16:04
    * @param switchInformation
     * @param scanNum
     * @return
    */
    @ApiOperation("全部扫描接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "switchInformation", value = "交换机登录信息集合", dataTypeClass = List.class, required = true),
            @ApiImplicitParam(name = "scanNum", value = "线程数", dataTypeClass = Long.class, required = true)
    })
    @PostMapping("/multipleScans/{scanNum}")
    @MyLog(title = "全部扫描", businessType = BusinessType.OTHER)
    public String multipleScans(@RequestBody List<String> switchInformation,
                                @PathVariable  Long scanNum) {

        /* 交换机登录信息集合 及 系统登录人信息 线程数 */
        ParameterSet parameterSet = new ParameterSet();
        parameterSet.setLoginUser(SecurityUtils.getLoginUser());/*程序登陆人*/
        parameterSet.setThreadCount(Integer.valueOf(scanNum+"").intValue());/*线程数*/

        /* 交换机登录信息集合 */
        List<SwitchParameters> switchParametersList = new ArrayList<>();
        for (String information:switchInformation){

            /* 交换机登录信息 转化为 实体类 */
            SwitchLoginInformation switchLoginInformation = JSON.parseObject(information, SwitchLoginInformation.class);

            // 四个参数 设默认值
            SwitchParameters switchParameters = new SwitchParameters();
            switchParameters.setLoginUser(SecurityUtils.getLoginUser());
            switchParameters.setScanningTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));/*扫描时间*/
            BeanUtils.copyBeanProp(switchParameters,switchLoginInformation);
            switchParameters.setPort(Integer.valueOf(switchLoginInformation.getPort()).intValue());

            //以多线程中的格式 存放数组中
            //连接方式，ip，用户名，密码，端口号
            switchParametersList.add(switchParameters);

        }

        /* 交换机登录信息集合 */
        parameterSet.setSwitchParameters(switchParametersList);

        /*线程池*/
        try {
            /*扫描全部问题线程池*/
            //boolean isRSA = true; 密码是否经过RSA加密
            ScanFixedThreadPool scanFixedThreadPool = new ScanFixedThreadPool();
            scanFixedThreadPoolHashMap.put(parameterSet.getLoginUser().getUsername(),scanFixedThreadPool);
            scanFixedThreadPool.switchLoginInformations(parameterSet, true);
            scanFixedThreadPoolHashMap.remove(parameterSet.getLoginUser().getUsername());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //  扫描结束 提示前端信息
        //传输登陆人姓名 及问题简述
        WebSocketService webSocketService = new WebSocketService();
        webSocketService.sendMessage(parameterSet.getLoginUser().getUsername(),"接收:扫描结束\r\n");

        try {
            //插入问题简述及问题路径
            PathHelper.writeDataToFile("接收:扫描结束\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "扫描结束";
    }

    /* 全面扫描终止 */
    @PostMapping("/multipleTerminationScann")
    public void multipleTerminationScann() {
        String username = SecurityUtils.getLoginUser().getUsername();
        ScanFixedThreadPool scanFixedThreadPool = scanFixedThreadPoolHashMap.get(username);
        scanFixedThreadPool.terminationScanThread();
        scanFixedThreadPoolHashMap.remove(username);
    }


    /* 为了方便 全部扫描 和 专项扫描 已经将方法提取出来 加入到了线程的run方法中  */
    /**
     * @method: 扫描方法 logInToGetBasicInformation
     * @Param: [threadName, mode, ip, name, password, port] 传参:mode连接方式, ip 地址, name 用户名, password 密码, port 端口号，
     *          loginUser 登录人信息，time 扫描时间
     *          List<TotalQuestionTable> totalQuestionTables  用于 专项扫描
     * @return: com.sgcc.common.core.domain.AjaxResult
     */
    /*@GetMapping("logInToGetBasicInformation")*/
    public AjaxResult logInToGetBasicInformation(SwitchParameters switchParameters,
                                                 List<TotalQuestionTable> totalQuestionTables,
                                                 List<String> advancedName,boolean isRSA) {

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

            return basicInformationList_ajaxResult;
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
        AjaxResult ajaxResult = scanProblem(switchParameters,totalQuestionTables);

        /* 关闭交换机连接 */
        if (switchParameters.getMode().equalsIgnoreCase("ssh")){
            switchParameters.getConnectMethod().closeConnect(switchParameters.getSshConnect());
        }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
            switchParameters.getTelnetSwitchMethod().closeSession(switchParameters.getTelnetComponent());
        }

        return basicInformationList_ajaxResult;
    }


    /**
     * @method: 预执行 获取交换机基本信息  多个命令依次执行 按，分割
     * @Param: [user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法,
     * user_Object ：
     * SshConnect ssh连接工具，connectMethod ssh连接,
     * TelnetComponent Telnet连接工具，telnetSwitchMethod telnet连接]
     * 通过null 查询初所有会的交换机基本信息的命令字符串集合
     * 遍历交换机基本信息的命令字符串集合 通过 扫描分析方法 获得所有提取信息
     * 通过返回提取的信息，给基本属性赋值
     * 成功则返回基本信息 否则 遍历下一条 交换机基本信息的命令字符串集合信息
     */
    public AjaxResult getBasicInformationTest(SwitchParameters switchParameters,String[] commands ,List<ProblemScanLogic> problemScanLogicList) {

        if (commands.length == 0){
            return AjaxResult.error("未定义该交换机获取基本信息命令及分析");
        }

        //basicInformation : display device manuinfo,display ver
        //连接方式 ssh telnet
        //目前获取基本信息命令是多个命令是由,号分割的，
        // 所以需要根据, 来分割。例如：display device manuinfo,display ver
        String[] commandsplit = commands;
        List<String> commandString_List = new ArrayList<>(); //预设交换机返回结果
        List<String> return_sum_List = new ArrayList<>(); //当前命令字符串总和 返回命令总和("\r\n"分隔)
        //遍历数据表命令 分割得到的 命令数组
        for (String command:commandsplit){
            //创建 存储交换机返回数据 实体类
            ReturnRecord returnRecord = new ReturnRecord();
            int insert_Int = 0; //交换机返回结果插入数据库ID
            returnRecord.setUserName(switchParameters.getLoginUser().getUsername());
            returnRecord.setSwitchIp(switchParameters.getIp());
            returnRecord.setBrand(switchParameters.getDeviceBrand());
            returnRecord.setType(switchParameters.getDeviceModel());
            returnRecord.setFirewareVersion(switchParameters.getFirmwareVersion());
            returnRecord.setSubVersion(switchParameters.getSubversionNumber());
            // 执行命令赋值
            String commandtrim = command.trim();
            returnRecord.setCurrentCommLog(commandtrim);
            //根据 连接方法 判断 实际连接方式
            //并发送命令 接受返回结果
            boolean deviceBrand = true;
            do {
                deviceBrand = true;
                if (switchParameters.getMode().equalsIgnoreCase("ssh")){
                    //  WebSocket 传输 命令
                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                            switchParameters.getIp()+"发送:"+command+"\r\n");

                    //command
                    /*根据交换机信息类 与 具体命令，执行并返回交换机返回信息
                     * 返回结果
                     * 如果交换机返回信息错误，则返回信息为 null*/
                    ExecuteCommand executeCommand = new ExecuteCommand();
                    commandString_List = executeCommand.executeScanCommandByCommand(switchParameters, command);
                    if (MyUtils.isCollectionEmpty(commandString_List)){
                        //交换机返回信息错误 导致 方法返回值为null
                        //所谓 修复失败 则返回错误
                        return AjaxResult.error("未定义该交换机获取基本信息命令及分析");
                    }
                    //commandString = switchParameters.getConnectMethod().sendCommand(switchParameters.getIp(),switchParameters.getSshConnect(),command,switchParameters.getNotFinished());
                }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
                    //  WebSocket 传输 命令
                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                            switchParameters.getIp()+"发送:"+command);

                    commandString_List = switchParameters.getTelnetSwitchMethod().sendCommand(switchParameters.getIp(),switchParameters.getTelnetComponent(),command,switchParameters.getNotFinished());
                }

                StringBuffer stringBuffer = new StringBuffer();
                for (String returnString : commandString_List){
                    stringBuffer.append(returnString).append("\r\n");
                }
                stringBuffer = StringBufferUtils.substring(stringBuffer,0,stringBuffer.length()-2);

                //  WebSocket 传输 交换机返回结果
                returnRecord.setCurrentReturnLog(stringBuffer);
                //粗略查看是否存在 故障
                // 存在故障返回 false 不存在故障返回 true
                boolean switchfailure = FunctionalMethods.switchfailure(switchParameters, stringBuffer);
                // 存在故障返回 false
                if (!switchfailure){

                    // 遍历交换机返回信息数组
                    for (String returnString:commandString_List){
                        // 查看是否存在 故障
                        // 存在故障返回 false 不存在故障返回 true
                        deviceBrand = FunctionalMethods.switchfailure(switchParameters, new StringBuffer(returnString));
                        // 存在故障返回 false
                        if (!deviceBrand){

                            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                                    "故障:"+switchParameters.getIp() + ":"+returnString+"\r\n");

                            returnRecord.setCurrentIdentifier(switchParameters.getIp() + "出现故障:"+returnString);
                            if (switchParameters.getMode().equalsIgnoreCase("ssh")){
                                //command
                                /*根据交换机信息类 与 具体命令，执行并返回交换机返回信息
                                 * 返回结果
                                 * 如果交换机返回信息错误，则返回信息为 null*/
                                ExecuteCommand executeCommand = new ExecuteCommand();
                                commandString_List = executeCommand.executeScanCommandByCommand(switchParameters, " ");
                                if ( MyUtils.isCollectionEmpty(commandString_List)){
                                    //交换机返回信息错误 导致 方法返回值为null
                                    //所谓 修复失败 则返回错误
                                    return AjaxResult.error("未定义该交换机获取基本信息命令及分析");
                                }
                                //switchParameters.getConnectMethod().sendCommand(switchParameters.getIp(),switchParameters.getSshConnect()," ",switchParameters.getNotFinished());
                            }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
                                switchParameters.getTelnetSwitchMethod().sendCommand(switchParameters.getIp(),switchParameters.getTelnetComponent()," ",switchParameters.getNotFinished());
                            }
                        }
                    }
                }
                returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
                insert_Int = returnRecordService.insertReturnRecord(returnRecord);
                if (insert_Int <= 0){

                    //传输登陆人姓名 及问题简述
                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                            "错误:交换机执行"+commandtrim+"命令返回信息插入失败\r\n");

                }

            }while (!deviceBrand);
            returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
            returnRecord = returnRecordService.selectReturnRecordById(Integer.valueOf(insert_Int).longValue());

            StringBuffer stringBuffer = new StringBuffer();
            for (String returnString : commandString_List){
                stringBuffer.append(returnString).append("\r\n");
            }
            stringBuffer = StringBufferUtils.substring(stringBuffer,0,stringBuffer.length()-2);

            //去除其他 交换机登录信息
            stringBuffer = FunctionalMethods.removeLoginInformation(stringBuffer);


            // 返回日志内容
            StringBuffer current_return_log = new StringBuffer();
            if (commandString_List.size() !=1 ){

                current_return_log = StringBufferUtils.substring(stringBuffer, 0, stringBuffer.length() - commandString_List.get(commandString_List.size() - 1).length() - 2);
                returnRecord.setCurrentReturnLog(current_return_log);

                //返回日志前后都有\r\n 可以改为 current_return_log.endsWith("\r\n");
                StringBuffer current_return_log_substring_end = StringBufferUtils.substring(current_return_log,current_return_log.length() - 2, current_return_log.length());
                if (!current_return_log_substring_end.toString().equals("\r\n")){
                    current_return_log.append("\r\n");
                }
                String current_return_log_substring_start = current_return_log.substring(0, 2);
                if (!current_return_log_substring_start.equals("\r\n")){
                    current_return_log.insert(0, "\r\n");
                }
            }

            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                    switchParameters.getIp()+"接收:"+current_return_log+"\r\n");

            //当前标识符 如：<H3C> [H3C]
            String current_identifier = commandString_List.get(commandString_List.size() - 1).trim();
            returnRecord.setCurrentIdentifier(current_identifier);
            //当前标识符前后都没有\r\n
            String current_identifier_substring_end = current_identifier.substring(current_identifier.length() - 2, current_identifier.length());
            if (current_identifier_substring_end.equals("\r\n")){
                current_identifier = current_identifier.substring(0,current_identifier.length()-2);
            }
            String current_identifier_substring_start = current_identifier.substring(0, 2);
            if (current_identifier_substring_start.equals("\r\n")){
                current_identifier = current_identifier.substring(2,current_identifier.length());
            }


            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                    switchParameters.getIp()+"接收:"+current_identifier+"\r\n");


            //存储交换机返回数据 插入数据库
            returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
            int update = returnRecordService.updateReturnRecord(returnRecord);
            //判断命令是否错误 错误为false 正确为true
            if (!FunctionalMethods.judgmentError( switchParameters,stringBuffer)){
                //如果返回信息错误 则结束当前命令，执行 遍历数据库下一条命令字符串(,)
                for (String string_split:commandString_List){
                    if (!FunctionalMethods.judgmentError( switchParameters,new StringBuffer(string_split))){

                        AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                                "风险:"+switchParameters.getIp()+ ":" +command+ ":"+string_split+"\r\n");

                        break;
                    }
                }
                break;
            }
            //当前命令字符串 返回命令总和("\r\n"分隔)
            if(switchParameters.getMode().equalsIgnoreCase("ssh")){
                return_sum_List.add(commandtrim);
                return_sum_List.addAll(commandString_List);
            }else {
                // telnet 自带
                return_sum_List.addAll(commandString_List);
            }
        }


        //修整 当前命令字符串 返回信息  去除多余 "\r\n" 连续空格
        //应该可以去除 因为 上面 每个单独命令已经执行过
        // 注释掉 可能会在两条交换机返回信息中 存在 "\r\n\r\n" 情况 按"\r\n"分割可能会出现空白元素
        //String command_String = Utils.trimString(return_sum);
        //分析第一条ID basicInformation.getProblemId() (为 问题扫描逻辑表  ID)
        String first_problem_scanLogic_Id = null;
        for (ProblemScanLogic pojo:problemScanLogicList){
            if (pojo.gettLine().equals("1")){
                first_problem_scanLogic_Id = pojo.getId();
            }
        }


        // 获取交换机 基本信息命令 列表 根据分析ID获取问题扫描逻辑详细信息
        //进行分析 返回总提取信息
        String extractInformation_string1 = analysisReturn(switchParameters,null,
                return_sum_List, first_problem_scanLogic_Id,problemScanLogicList);
        //extractInformation_string1 = extractInformation_string1.replace(",","");

        /*自定义分隔符*/
        String customDelimiter = null;
        Object customDelimiterObject = CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());
        if (customDelimiterObject instanceof  String){
            customDelimiter = (String) customDelimiterObject;
        }


        String[] return_result_split = extractInformation_string1.split(customDelimiter);
        for (int num = 0;num<return_result_split.length;num++){
            //设备型号
            if (return_result_split[num].equals("设备型号")){
                num = num+2;
                switchParameters.setDeviceModel(return_result_split[num]);
            }
            //设备品牌
            if (return_result_split[num].equals("设备品牌")) {
                num = num+2;
                switchParameters.setDeviceBrand(return_result_split[num]);
            }
            //内部固件版本
            if (return_result_split[num].equals("内部固件版本")) {
                num = num+2;
                switchParameters.setFirmwareVersion(return_result_split[num]);
            }
            //子版本号
            if (return_result_split[num].equals("子版本号")) {
                num = num+2;
                switchParameters.setSubversionNumber(return_result_split[num]);
            }
            // 根据交换机信息查询 获取 扫描问题的 命令ID
            /*List<String> stringList = new ArrayList<>();
                    stringList.add(deviceBrand);
                    stringList.add(deviceModel);
                    stringList.add(firmwareVersion);
                    stringList.add(subversionNumber);
                    WebSocketService.sendMessage("basicinformation"+userName,stringList);*/

            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                    "系统信息:"+switchParameters.getIp()+
                            "基本信息:设备品牌:"+switchParameters.getDeviceBrand()+
                            "设备型号:"+switchParameters.getDeviceModel()+
                            "内部固件版本:"+switchParameters.getFirmwareVersion()+
                            "子版本号:"+switchParameters.getSubversionNumber()+"\r\n");

            return AjaxResult.success(switchParameters);
        }



        AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                "系统信息:"+switchParameters.getIp() +
                        "基本信息:设备品牌:"+switchParameters.getDeviceBrand()+
                        "设备型号:"+switchParameters.getDeviceModel()+
                        "内部固件版本:"+switchParameters.getFirmwareVersion()+
                        "子版本号:"+switchParameters.getSubversionNumber()+"\r\n");



        return AjaxResult.error("未定义该交换机获取基本信息命令及分析");
    }

    /**
     * @method: 获取交换机基本信息  多个命令依次执行 按，分割
     * @Param: [user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法,
     * user_Object ：
     * SshConnect ssh连接工具，connectMethod ssh连接,
     * TelnetComponent Telnet连接工具，telnetSwitchMethod telnet连接]
     * 通过null 查询初所有会的交换机基本信息的命令字符串集合
     * 遍历交换机基本信息的命令字符串集合 通过 扫描分析方法 获得所有提取信息
     * 通过返回提取的信息，给基本属性赋值
     * 成功则返回基本信息 否则 遍历下一条 交换机基本信息的命令字符串集合信息
     */

    public  AjaxResult getBasicInformationList(SwitchParameters switchParameters) {
        //查询 获取基本信息命令表  中的全部命令
        //BasicInformation pojo_NULL = new BasicInformation(); //null
        //根据 null 查询 得到表中所有数据
        basicInformationService = SpringBeanUtil.getBean(IBasicInformationService.class);//解决 多线程 service 为null问题
        List<BasicInformation> basicInformationList = basicInformationService.selectBasicInformationList(null);
        if (basicInformationList.size() == 0){
            return AjaxResult.error("未定义该交换机获取基本信息命令及分析");
        }

        //遍历命令表命令 执行命令
        for (BasicInformation basicInformation:basicInformationList){
            //basicInformation : display device manuinfo,display ver
            //连接方式 ssh telnet
            //目前获取基本信息命令是多个命令是由,号分割的，
            // 所以需要根据, 来分割。例如：display device manuinfo,display ver
            String[] removecustom = basicInformation.getCommand().split("\\[");

            /*自定义分隔符*/
            String customDelimiter = null;
            Object customDelimiterObject = CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());
            if (customDelimiterObject instanceof  String){
                customDelimiter = (String) customDelimiterObject;
            }

            String[] commandsplit = removecustom[0].split(customDelimiter);

            List<String> commandString = new ArrayList<>(); //预设交换机返回结果
            List<String>  return_sum = new ArrayList<>(); //当前命令字符串总和 返回命令总和("\r\n"分隔)

            boolean loop = false;
            //遍历数据表命令 分割得到的 命令数组
            for (String command:commandsplit){
                //创建 存储交换机返回数据 实体类
                ReturnRecord returnRecord = new ReturnRecord();
                int insert_Int = 0;
                // 系统登录人名字 ip 品牌 型号 固件版本 子版本
                returnRecord.setUserName(switchParameters.getLoginUser().getUsername());
                returnRecord.setSwitchIp(switchParameters.getIp());
                returnRecord.setBrand(switchParameters.getDeviceBrand());
                returnRecord.setType(switchParameters.getDeviceModel());
                returnRecord.setFirewareVersion(switchParameters.getFirmwareVersion());
                returnRecord.setSubVersion(switchParameters.getSubversionNumber());

                // 执行命令赋值
                String commandtrim = command.trim();
                returnRecord.setCurrentCommLog(commandtrim);

                //根据 连接方法 判断 实际连接方式
                //并发送命令 接受返回结果
                boolean deviceBrand = true;
                do {
                    deviceBrand = true;
                    if (switchParameters.getMode().equalsIgnoreCase("ssh")){
                        //  WebSocket 传输 命令
                        AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                                switchParameters.getIp()+"发送:"+command+"\r\n");

                        //command
                        /*根据交换机信息类 与 具体命令，执行并返回交换机返回信息
                         * 返回结果
                         * 如果交换机返回信息错误，则返回信息为 null*/
                        ExecuteCommand executeCommand = new ExecuteCommand();
                        commandString = executeCommand.executeScanCommandByCommand(switchParameters, command);

                        if (commandString.size() == 0){
                            //交换机返回信息错误 导致 方法返回值为null
                            //所谓 修复失败 则返回错误
                            break;
                        }

                        //commandString = switchParameters.getConnectMethod().sendCommand(switchParameters.getIp(),switchParameters.getSshConnect(),command,switchParameters.getNotFinished());
                    }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
                        //  WebSocket 传输 命令
                        AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                                switchParameters.getIp()+"发送:"+command);

                        commandString = switchParameters.getTelnetSwitchMethod().sendCommand(switchParameters.getIp(),switchParameters.getTelnetComponent(),command,switchParameters.getNotFinished());
                    }

                    StringBuffer stringBuffer = new StringBuffer();
                    for (String string : commandString) {
                        stringBuffer.append(string).append("\r\n");
                    }
                    stringBuffer = StringBufferUtils.substring(stringBuffer,0,stringBuffer.length()-2);
                    //  WebSocket 传输 交换机返回结果
                    returnRecord.setCurrentReturnLog(stringBuffer);

                    //粗略查看是否存在 故障
                    // 存在故障返回 false 不存在故障返回 true
                    boolean switchfailure = FunctionalMethods.switchfailure(switchParameters, stringBuffer);
                    // 存在故障返回 false
                    if (!switchfailure){
                        List<String> commandStringSplit_List = StringBufferUtils.stringBufferSplit(stringBuffer,"\r\n");
                        // 交换机返回结果 按行 分割成 交换机返回信息数组
                        // 遍历交换机返回信息数组
                        for (String returnString:commandStringSplit_List){
                            // 查看是否存在 故障
                            // 存在故障返回 false 不存在故障返回 true
                            deviceBrand = FunctionalMethods.switchfailure(switchParameters,new StringBuffer(returnString));
                            // 存在故障返回 false
                            if (!deviceBrand){
                                loop = true;

                                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                                        "故障:"+switchParameters.getIp() + ":"+returnString+"\r\n");

                                returnRecord.setCurrentIdentifier(switchParameters.getIp() + "出现故障:"+returnString);
                                if (switchParameters.getMode().equalsIgnoreCase("ssh")){
                                    //command
                                    /*根据交换机信息类 与 具体命令，执行并返回交换机返回信息
                                     * 返回结果
                                     * 如果交换机返回信息错误，则返回信息为 null*/
                                    ExecuteCommand executeCommand = new ExecuteCommand();

                                    commandString = executeCommand.executeScanCommandByCommand(switchParameters, " ");
                                    if (commandString.size() == 0){
                                        //交换机返回信息错误 导致 方法返回值为null
                                        //所谓 修复失败 则返回错误
                                        break;
                                    }


                                    //switchParameters.getConnectMethod().sendCommand(switchParameters.getIp(),switchParameters.getSshConnect()," ",switchParameters.getNotFinished());
                                }else if (switchParameters.getMode().equalsIgnoreCase("telnet")){
                                    switchParameters.getTelnetSwitchMethod().sendCommand(switchParameters.getIp(),switchParameters.getTelnetComponent()," ",switchParameters.getNotFinished());
                                }
                            }
                        }
                    }
                    returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
                    insert_Int = returnRecordService.insertReturnRecord(returnRecord);
                    if (insert_Int <= 0){
                        //传输登陆人姓名 及问题简述
                        AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                                "错误:交换机返回信息插入失败\r\n");

                    }
                }while (!deviceBrand);

                if (commandString == null){
                    //交换机返回信息错误 导致 方法返回值为null
                    //所谓 修复失败 则返回错误
                    break;
                }

                returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
                returnRecord = returnRecordService.selectReturnRecordById(Integer.valueOf(insert_Int).longValue());

                StringBuffer stringBuffer = new StringBuffer();
                for (String string : commandString) {
                    stringBuffer.append(string).append("\r\n");
                }
                stringBuffer = StringBufferUtils.substring(stringBuffer,0,stringBuffer.length()-2);

                // 返回日志内容
                StringBuffer current_return_log = new StringBuffer();
                if (commandString.size() !=1 ){
                    /*通过截取 去除 标识符 和 标识符与返回日志中间的 \r\n*/

                    current_return_log = StringBufferUtils.substring(stringBuffer,0, stringBuffer.length() - commandString.get(commandString.size() - 1).length() - 2);

                    returnRecord.setCurrentReturnLog(current_return_log);
                    //返回日志前后都有\r\n 可以改为 current_return_log.endsWith("\r\n");
                    //测试一下endsWith() startsWith()方法
                    //返回日志前后都有\r\n 可以改为 current_return_log.endsWith("\r\n");
                    //endsWith() 方法用于测试字符串是否以指定的后缀结束。
                    //startsWith() 前缀
                    StringBuffer current_return_log_substring_end = StringBufferUtils.substring(current_return_log,current_return_log.length() - 2, current_return_log.length());

                    if (!current_return_log_substring_end.equals("\r\n")){
                        current_return_log.append("\r\n");
                    }
                    String current_return_log_substring_start = current_return_log.substring(0, 2);
                    if (!current_return_log_substring_start.equals("\r\n")){
                        current_return_log.insert(0,"\r\n");
                    }
                }

                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                        switchParameters.getIp()+"接收:"+current_return_log+"\r\n");

                //当前标识符 如：<H3C> [H3C]
                String current_identifier = commandString.get(commandString.size() - 1);
                returnRecord.setCurrentIdentifier(current_identifier);

                //当前标识符前后都没有\r\n
                //测试一下endsWith() startsWith()方法
                //返回日志前后都有\r\n 可以改为 current_return_log.endsWith("\r\n");
                //endsWith() 方法用于测试字符串是否以指定的后缀结束。
                //startsWith() 前缀
                String current_identifier_substring_end = current_identifier.substring(current_identifier.length() - 2, current_identifier.length());
                if (current_identifier_substring_end.equals("\r\n")){
                    current_identifier = current_identifier.substring(0,current_identifier.length()-2);
                }
                String current_identifier_substring_start = current_identifier.substring(0, 2);
                if (current_identifier_substring_start.equals("\r\n")){
                    current_identifier = current_identifier.substring(2,current_identifier.length());
                }

                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                        switchParameters.getIp()+"接收:"+current_identifier+"\r\n");

                //存储交换机返回数据 插入数据库
                returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
                int update = returnRecordService.updateReturnRecord(returnRecord);

                StringBuffer returnStringBuffer = new StringBuffer();
                for (String commandReturnString : commandString) {
                    returnStringBuffer.append(commandReturnString).append("\r\n");
                }
                returnStringBuffer = StringBufferUtils.substring(returnStringBuffer,0,
                        returnStringBuffer.length()-2);

                //判断命令是否错误 错误为false 正确为true
                if (!FunctionalMethods.judgmentError( switchParameters,returnStringBuffer)){
                    //如果返回信息错误 则结束当前命令，执行 遍历数据库下一条命令字符串(,)

                    for (String string_split:commandString){
                        if (!FunctionalMethods.judgmentError( switchParameters,new StringBuffer(string_split))){
                            loop = true;

                            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                                    "风险:"+switchParameters.getIp()+ ":" +command+ ":"+string_split+"\r\n");

                            continue;
                        }
                    }
                    continue;
                }

                //当前命令字符串 返回命令总和("\r\n"分隔)
                return_sum.add(commandtrim);
                return_sum.addAll(commandString);
            }
            //修整 当前命令字符串 返回信息  去除多余 "\r\n" 连续空格
            //应该可以去除 因为 上面 每个单独命令已经执行过
            // 注释掉 可能会在两条交换机返回信息中 存在 "\r\n\r\n" 情况 按"\r\n"分割可能会出现空白元素
            //String command_String = Utils.trimString(return_sum);
            if (loop){
                continue;
            }

            if (basicInformation.getProblemId() == null){
                continue;
            }

            //分析第一条ID basicInformation.getProblemId() (为 问题扫描逻辑表  ID)
            String first_problem_scanLogic_Id = basicInformation.getProblemId();
            // 根据交换机返回信息、分析ID 分析逻辑集合获取交换机基本信息
            //进行分析 返回总提取信息
            String extractInformation_string1 = analysisReturn(switchParameters,null,
                    return_sum, first_problem_scanLogic_Id,null);
            if (extractInformation_string1.indexOf("错误") !=-1){
                continue;
            }

            if (extractInformation_string1.equals("") || extractInformation_string1 == null){
                continue;
            }

            //extractInformation_string1 = extractInformation_string1.replace(",","");

            /*自定义分隔符*/
            String[] return_result_split = extractInformation_string1.split(customDelimiter);

            for (int num = 0;num<return_result_split.length;num++){
                //设备型号
                if (return_result_split[num].equals("设备型号")){
                    num = num+2;
                    switchParameters.setDeviceModel(return_result_split[num]);
                }
                //设备品牌
                if (return_result_split[num].equals("设备品牌")) {
                    num = num+2;
                    switchParameters.setDeviceBrand(return_result_split[num]);
                }
                //内部固件版本
                if (return_result_split[num].equals("内部固件版本")) {
                    num = num+2;
                    switchParameters.setFirmwareVersion(return_result_split[num]);
                }
                //子版本号
                if (return_result_split[num].equals("子版本号")) {
                    num = num+2;
                    switchParameters.setSubversionNumber(return_result_split[num]);
                }
                if (!switchParameters.getDeviceBrand().equals("") && !switchParameters.getDeviceModel().equals("")
                        && !switchParameters.getFirmwareVersion().equals("") && !switchParameters.getSubversionNumber().equals("")){

                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                            "系统信息:"+switchParameters.getIp() +
                                    "基本信息:设备品牌:"+switchParameters.getDeviceBrand()+
                                    "设备型号:"+switchParameters.getDeviceModel()+
                                    "内部固件版本:"+switchParameters.getFirmwareVersion()+
                                    "子版本号:"+switchParameters.getSubversionNumber()+"\r\n");

                    List<String> stringList = new ArrayList<>();
                    stringList.add(switchParameters.getDeviceModel());
                    stringList.add(switchParameters.getFirmwareVersion());
                    stringList.add(switchParameters.getSubversionNumber());
                    boolean brand = false;
                    informationService = SpringBeanUtil.getBean(IInformationService.class);
                    List<String> brandList = informationService.selectDeviceBrandList();
                    for (String string:brandList){
                        if (switchParameters.getDeviceBrand().equalsIgnoreCase(string)){
                            brand = true;
                        }
                    }
                    if (!brand || !(MyUtils.thereAreNumbersInTheSet(stringList))){
                        break;
                    }
                    return AjaxResult.success(switchParameters);
                }
            }

            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                    "系统信息:"+switchParameters.getIp() +
                            "基本信息:设备品牌:"+switchParameters.getDeviceBrand()+
                            "设备型号:"+switchParameters.getDeviceModel()+
                            "内部固件版本:"+switchParameters.getFirmwareVersion()+
                            "子版本号:"+switchParameters.getSubversionNumber()+"\r\n");


        }

        return AjaxResult.error("未定义该交换机获取基本信息命令及分析");
    }

    /**
     * @method: 根据交换机返回信息、分析ID 分析逻辑集合获取交换机基本信息
     * @Param: [user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法, connectMethod ssh连接, telnetSwitchMethod telnet连接]
     * @Param: [resultString 交换机返回信息,first_problem_scanLogic_Id 第一条分析ID,firmwareVersion 内部固件版本号)
     * @return: void
     */
    public String analysisReturn(SwitchParameters switchParameters,TotalQuestionTable totalQuestionTable,
                                 List<String> return_sum_List,String first_problem_scanLogic_Id,List<ProblemScanLogic> problemScanLogicList){
        //整理返回结果 去除 #
        //测试后无用 暂注释掉   注释掉可能会出现的情况 按行分割后 出现某数组元素只有 # 的情况
        //resultString = resultString.replace("\r\n"+" # "+"\r\n","\r\n");
        //是否循环判断 loop循环 end 单次
        /* 传入参数[user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法, connectMethod ssh连接, telnetSwitchMethod telnet连接,
                交换机返回信息字符串, 单次分析提取数据，循环分析提取数据
                交换机返回信息字符串分析索引位置(光标)，第一条分析ID， 当前分析ID ，是否循环 ，内部固件版本号] */
        // 设备型号=:=S3600-28P-EI=:=设备品牌=:=H3C=:=内部固件版本=:=3.10,=:=子版本号=:=1510P09=:=
        Integer numberOfCycles = 10;
        Object numberOfCyclesObject = CustomConfigurationUtil.getValue("configuration.numberOfCycles", Constant.getProfileInformation());/*最大循环次数*/
        if (numberOfCyclesObject !=null && numberOfCyclesObject instanceof Integer){
            numberOfCycles = Integer.valueOf( (String) numberOfCyclesObject ).intValue();
        }

        String strings = selectProblemScanLogicById(switchParameters, totalQuestionTable,
                return_sum_List, //交换机返回结果 按行分割 交换机返回信息字符串
                "", "",
                0, first_problem_scanLogic_Id ,problemScanLogicList, null,0, 0, numberOfCycles);// loop end

        if (strings == null){
            return "获取基本信息错误";
        }

        if (strings.indexOf("错误") !=-1){
            return strings;
        }

        /*去除标识符 （TM）*/
        strings = removeIdentifier(strings);

        //控制台 输出  交换机 基本信息
        //System.err.print("\r\n基本信息："+strings+"\r\n");

        return strings;
    }

    /**
     * 根据配置文件 去除标识符  （TM） ®
     * @param strings
     * @return
     */
    public String removeIdentifier(String strings) {
        /*# 标识符  两个标识符之间以;分阁*/
        String identifierString = null;
        Object identifierStringObject = CustomConfigurationUtil.getValue("configuration.identifier", Constant.getProfileInformation());
        if (identifierStringObject instanceof String){
            identifierString = (String) identifierStringObject;
        }

        String[] identifiersplit = identifierString.split(";");
        for (String identifier:identifiersplit){
            strings =strings.replace(identifier,"");
        }
        return strings;
    }


    /*Inspection Completed*/
    /**
     * 分析第一条ID 和 是否循环判断 返回是否有错
     * 根据分析ID获取问题扫描逻辑详细信息
     * @param switchParameters    交换机信息
     * @param totalQuestionTable   交换机在扫描的问题
     * @param return_information_List  交换机返回结果按行分割 交换机返回信息的行字符串
     * @param current_Round_Extraction_String  单词扫描提取信息
     * @param extractInformation_string   多次提取信息总和
     * @param line_n  光标
     * @param firstID  第一条分析ID
     * @param problemScanLogicList  分析逻辑
     * @param currentID  当前分析ID
     * @param insertsInteger 插入数据次数
     * @param loop   记录循环次数，经过了几次循环次数
     * @param numberOfCycles 最大循环次数

     * @return
     */
    public String selectProblemScanLogicById(SwitchParameters switchParameters,
                                             TotalQuestionTable totalQuestionTable,
                                             List<String> return_information_List,
                                             String current_Round_Extraction_String, String extractInformation_string,
                                             int line_n, String firstID ,List<ProblemScanLogic> problemScanLogicList, String currentID,
                                             Integer insertsInteger,
                                             Integer loop,Integer numberOfCycles) {

        /** 创建对象： 将要执行的ID */
        /*默认使用当前分析ID : currentID
        判断当前分析ID(currentID)是否为空。
        如果为空则用第一条分析ID(firstID).
        如果当前分析ID(currentID)不为空，说明是第二次调用本方法，则使用当前分析ID(currentID) 赋值给ID
        */
        String id = currentID;
        if (currentID == null){
            id = firstID;
        }



        /**判断逻辑 是通过数据表获取 还是前端传入数据*/
        // 分析逻辑是否为空，以及是查询数据库还是使用预加载数据
        /* 判断输入参数：problemScanLogicList 分析逻辑实体类集合  是否 为空，
        *
        * 不为空则，则为预取交换机基本信息功能。 分析逻辑数据通过problemScanLogicList来存储。
        * 如果为空，则需要查询数据库。都是通过ID来获取具体分析逻辑数据。
        *
        * 如果是空，则通过ID 在数据库中查询出要执行的分析数据
        * 如果不为空 则通过ID 在集合列表中查询出要执行的分析数据
        */
        //分析逻辑
        ProblemScanLogic problemScanLogic = null;
        if (problemScanLogicList == null){

            /** 可以优化一下，通过分析ID先进行查询逻辑集合，然后根据ID到逻辑集合中获取，可以减少访问数据库次数，又可以与预取交换机基本信息功能一致 */
            //根据ID查询分析数据
            //根据第一条分析ID 查询将要进行的分析逻辑信息
            problemScanLogicService = SpringBeanUtil.getBean(IProblemScanLogicService.class);
            problemScanLogic = problemScanLogicService.selectProblemScanLogicById(id);

        }else {

            //遍历逻辑集合
            //根据将要进行的分析逻辑ID，取出将要进行的分析逻辑信息
            for (ProblemScanLogic pojo:problemScanLogicList){
                if (pojo.getId().equals(id) || pojo.getId() == id){
                    problemScanLogic = pojo;
                }
            }

        }




        /**  判断是否是循环逻辑 并处理循环逻辑 */
        // 如果循环ID不为空的话 说明分析数据为 循环分析 则需要 调出循环ID 当做 当前分析ID继续执行
        // 循环分析数据 不需要分析 功能指向循环位置
        if (problemScanLogic.getCycleStartId()!=null){ /*&& !(problemScanLogic.getCycleStartId().equals("null"))   已经不存在等于“null”的情况了*/

            //比较循环次数和最大循环测试
            loop = loop + 1;

            /* 告警、异常信息写入 */
            if (loop > numberOfCycles){
                /*循环超过了最大循环次数*/
                String subversionNumber = switchParameters.getSubversionNumber();
                if (subversionNumber!=null){
                    subversionNumber = "、"+subversionNumber;
                }

                AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                        "异常:IP地址为:"+switchParameters.getIp()+"。"+
                        "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+
                        "。问题类型:"+totalQuestionTable.getTypeProblem()+ "。问题名称:"+totalQuestionTable.getTemProName()+
                        "。错误:循环超最大次数。\r\n");
                return null;
            }


            /*需要调出循环ID 当做 当前分析ID 继续执行分析*/
            /*currentID = problemScanLogic.getCycleStartId();*/
            String loop_string = selectProblemScanLogicById(
                    switchParameters,/*交换机信息类*/
                    totalQuestionTable,/*交换机问题类*/
                    return_information_List,/*交换机返回信息 行数组*/
                    "", /*单次提取数据 因为是要进行下一循环 所以 传输为""*/
                    extractInformation_string, /*该问题全部提取数据*/
                    line_n, /*光标位置*/
                    firstID, /*第一分析ID*/
                    problemScanLogicList, /*交换机分析数据集合 用于专项扫描*/
                    problemScanLogic.getCycleStartId(), /*当前分析ID*/
                    insertsInteger, /*问题插入数据库次数*/
                    loop, /*循环次数*/
                    numberOfCycles);/*最大循环次数*/

            return loop_string;

        }



        /** 判断 是否有问题 */
        //如果 问题索引字段 不为空 null 则 说明  分析数据 是 分析出问题或者可以结束了
        // problemScanLogic.getProblemId() 可以为 有问题(前端显示:异常) 无问题(前端显示:安全) 完成
        if (problemScanLogic.getProblemId()!=null){

            //有问题 无问题  完成
            /*
            查看问题ID(ProblemId)字段。
            如果该字段不为空，则分析出问题了或者分析完成了。

            1：如果 ProblemId 字段包含"问题"
            2：ProblemId字段不包含  "问题"  并且ProblemId字段  不包含"完成"  (自定义的问题名称)。
            */
            if (problemScanLogic.getProblemId().indexOf("问题")!=-1
                    || (problemScanLogic.getProblemId().indexOf("问题") ==-1  && !problemScanLogic.getProblemId().equals("完成") )){

                /**自定义的问题名称，*/
                /*告警、异常信息写入 */
                /* ProblemId字段不包含  "问题"  并且ProblemId字段  不包含"完成"  (自定义的问题名称)。 */
                if (problemScanLogic.getProblemId().indexOf("问题") ==-1  && !problemScanLogic.getProblemId().equals("完成") ){
                    String subversionNumber = switchParameters.getSubversionNumber();
                    if (subversionNumber!=null){
                        subversionNumber = "、"+subversionNumber;
                    }

                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                            "异常:IP地址为:"+switchParameters.getIp()+"。"+
                                    "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+"。"+
                                    "问题类型:"+totalQuestionTable.getTypeProblem()+ "。问题名称:"+totalQuestionTable.getTemProName()+"。\r\n");
                }


                //问题数据 插入问题表
                Long insertId = insertSwitchScanResult(switchParameters, totalQuestionTable, problemScanLogic, current_Round_Extraction_String);

                if (insertId < 0){

                    String subversionNumber = switchParameters.getSubversionNumber();
                    if (subversionNumber!=null){
                        subversionNumber = "、"+subversionNumber;
                    }

                    AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null ,
                            "TrueAndFalse:IP地址为:"+switchParameters.getIp()+","+
                            "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+","+
                            "问题类型:"+(totalQuestionTable==null?"获取交换机基本信息":(totalQuestionTable.getTypeProblem()+ "问题名称:"+totalQuestionTable.getTemProName()))+
                            "问题数据插入失败\r\n");

                    return "问题数据插入失败";
                }

                //插入问题数据次数 加一
                insertsInteger++;

                /*单词提取数据清空*/
                current_Round_Extraction_String = "";

                //根据 用户信息 和 扫描时间 获取扫描出问题数据列表  集合 并放入 websocket
                new SwitchIssueEcho().getSwitchScanResultListByData(switchParameters.getLoginUser().getUsername(),insertId);

                //getSwitchScanResultListBySwitchParameters(switchParameters);
                /*
                如果tNextId下一分析ID(此时tNextId默认为下一分析ID)不为空时，(此时逻辑上还有下一部 例如 进行循环)
                则tNextId赋值给当前分析ID 调用本方法，继续分析流程。
                */
                /** 有问题、无问题时，逻辑没有完成 走下一 ID*/
                if (problemScanLogic.gettNextId() != null){

                    /*如果使用 第一条分析ID firstID  则 当前分析ID currentID 要为 null*/
                    String loop_string = selectProblemScanLogicById(switchParameters,totalQuestionTable,
                            return_information_List,"",extractInformation_string,
                            line_n,firstID,problemScanLogicList,
                            problemScanLogic.gettNextId(), /*currentID = problemScanLogic.gettNextId();*/
                            insertsInteger, loop, numberOfCycles);

                    return loop_string;

                }

                /*如果tComId不为空时，则调用方法executeScanCommandByCommandId发送新命令，
                通过analysisReturnResults进行新的分析。*/
                if (problemScanLogic.gettComId() != null){

                    CommandReturn commandReturn = executeScanCommandByCommandId(switchParameters,totalQuestionTable,problemScanLogic.gettComId());

                    if (!commandReturn.isSuccessOrNot()){
                        /*交换机返回错误信息处理*/
                        return null;
                    }

                    String analysisReturnResults_String = analysisReturnResults(switchParameters,totalQuestionTable,
                            commandReturn,current_Round_Extraction_String, extractInformation_string);
                    return analysisReturnResults_String;
                }


            }


            //insertsInteger 关于扫描结果插入条数逻辑分析
            // ProblemId 包含完成 且 插入条数insertsInteger 为0
            // 分析执行 完成
            /*完成有程序分析结束  也可能是 获取基本信息 结束*/
            if (problemScanLogic.getProblemId().equals("完成") && insertsInteger == 0){

                //问题数据 插入问题表  完成
                //Long insertId = insertSwitchScanResult(switchParameters, totalQuestionTable, problemScanLogic, current_Round_Extraction_String);
                return extractInformation_string;

            }else if (problemScanLogic.getProblemId().equals("完成")){
                return extractInformation_string;
            }
        }


        /*匹配方式*/
        String matching_logic = "";
        //相对位置行
        String relativePosition_line ="";
        /*//相对位置列
        String relativePosition_row ="";*/
        if (problemScanLogic.getRelativePosition()!=null && !(problemScanLogic.getRelativePosition().equals("null"))){

            /* present,0
            * 1,0  */
            /*present 当前行*/
            String[] relativePosition_split = problemScanLogic.getRelativePosition().split(",");

            //相对位置行
            relativePosition_line = relativePosition_split[0];
            //相对位置列
            /* relativePosition_row = relativePosition_split[1]; */

            //匹配方式
            matching_logic = relativePosition_line;
        }


        //匹配逻辑
        String matched = null;
        if (problemScanLogic.getMatched()!=null){

            /*精确匹配full
            精确匹配present*/
            matched = problemScanLogic.getMatched();
            /* full&present    从当前行进行全文匹配   */
            matching_logic = matched.substring(4,matched.length()) + "&" +matching_logic;

            /*精确匹配  模糊匹配*/
            matched = matched.substring(0,4);
        }


        //取词逻辑
        String action = problemScanLogic.getAction();
        //比较分析
        String compare = problemScanLogic.getCompare();


        /* 定义记录之前的光标位置的参数 */
        //int frontMarker = 0;

        /** 取词 */
        if (action != null && action.indexOf("full") != -1){
            /*full为 相对于全文取词    ：   取词full
            relativePosition_line 是相对于第几行*/
            //frontMarker = line_n;
            line_n = Integer.valueOf(relativePosition_line).intValue();

        }else if (action != null && action.equalsIgnoreCase("取词")){
            //  && action.indexOf("present") != -1  present 改为了空
            /*present为按行扫描  此时不需要记录之前光标
            relativePosition_line 相对于第几行
            当relativePosition_line = 0 是 扫描光标行*/

            line_n = line_n + Integer.valueOf(relativePosition_line).intValue();

        }


        /*匹配逻辑 */
        if (matched != null && matching_logic!=""){

            /*full全文按行  &   presentrelative
            * full&full       记录位置 回到第一行 开始全文扫描
            * full&present    不记录位置 从当前行 开始全文扫描
            * present&present 不记录位置 只在当前行
            * present&full    不记录位置 只在全文进行当前行匹配  无意义
            * full&N          记录位置 到第N行 开始全文扫描  N 是全文
            * present&N       不记录位置 只在当前行进行匹配  N 是相对前光标

            注意  缺少 从之前光标相对位置进行全文匹配 */

            /*光标位置*/
            switch(matching_logic){
                case "full&full" : //从第一行 全文扫描
                    //frontMarker = line_n;
                    line_n = 1;
                    break;

                case "full&present":     //从当前行 全文扫描
                case "present&present":  //只匹配当前行
                    break;

                case "present&full":     //全文匹配 当前行 无意义
                    break;

                default:

                    String[] matching_logic_split = matching_logic.split("&");
                    if (matching_logic_split.length == 2){

                        /* N 转化为 int类型 */
                        int line_num = Integer.valueOf(matching_logic_split[1]).intValue();
                        if (matching_logic.indexOf("full&") == -1) {

                            if (matching_logic.indexOf("present&")!=-1){//不记录位置 只在当前行进行匹配
                                //frontMarker = line_n;
                                line_n = line_n + line_num;
                            }

                        } else {// 从 第N行全文匹配

                            //frontMarker = line_n;
                            line_n = line_num;

                        }

                    }

                    if (problemScanLogic.getRelativePosition().equals("null")){

                        //frontMarker = line_n;
                        line_n = line_n;
                    }

            }
        }

        //分析数据 的 关键字
        String matchContent = "";
        if (problemScanLogic.getMatchContent()!=null){
            matchContent = problemScanLogic.getMatchContent().trim();
        }

        ScanLogicMethods scanLogicMethods = new ScanLogicMethods();

        //从line_n=0 开始检索集合 一直到最后一位
        for ( int num = line_n ; num< return_information_List.size() ; num++ ) {

            //光标位置
            line_n = num;

            //返回信息的数组元素 第 num 条
            String information_line_n = return_information_List.get(num);

            //匹配逻辑 有成功失败之分
            if (matched != null){

                String matchedReturnString = scanLogicMethods.MatchingLogicMethod(switchParameters,
                        matched, information_line_n , matchContent,
                        totalQuestionTable,
                        return_information_List, current_Round_Extraction_String, extractInformation_string,
                        line_n, firstID, problemScanLogicList, currentID,
                        insertsInteger, loop, numberOfCycles, problemScanLogic,
                        matching_logic, num);

                if ( matchedReturnString.equals("continue") && num + 1 < return_information_List.size() ){
                    continue;

                }else {
                    return matchedReturnString;

                }

            }

            //取词
            if (action!=null && !action.equals("null")){

                String actionReturnString = scanLogicMethods.LogicalMethodofWordExtraction(switchParameters,
                        action, information_line_n, matchContent,
                        totalQuestionTable,
                        return_information_List, current_Round_Extraction_String, extractInformation_string,
                        line_n, firstID, problemScanLogicList, currentID,
                        insertsInteger, loop, numberOfCycles, problemScanLogic,
                        relativePosition_line);

                /* return_information_array[num] */
                if (actionReturnString.equals("continue") && num + 1 < return_information_List.size() ){
                    continue;
                }

                return actionReturnString;

            }

            //比较
            if (compare!=null){

                String compareReturnString = scanLogicMethods.ComparativeLogicMethod(switchParameters,
                        compare,
                        totalQuestionTable,
                        return_information_List, current_Round_Extraction_String, extractInformation_string,
                        line_n, firstID, problemScanLogicList, currentID,
                        insertsInteger, loop, numberOfCycles, problemScanLogic);

                return compareReturnString;

            }

        }

        return null;
    }

    /*Inspection Completed*/
    /**
    * @Description
    * @desc
    * @param switchParameters	交换机登录信息
     * @param totalQuestionTable	问题表
     * @param problemScanLogic	 分析信息
     * @param parameterString	 单词提取信息
     * @return
    */
    public Long insertSwitchScanResult (SwitchParameters switchParameters,
                                        TotalQuestionTable totalQuestionTable,
                                        ProblemScanLogic problemScanLogic,
                                        String parameterString){

        /*扫描结果表实体类*/
        SwitchScanResult switchScanResult = new SwitchScanResult();

        //交换机ID 和 扫描线程名
        /** 添加线程名原因，测试过程中同一台交换机扫描了两遍，
         * 因为 交换机IP 与 交换机四项基本信息 一致，两遍扫描结果拼接到了一起，此时要求两遍扫描结果区分开，
         * 所以加上了线程名，根据不同的线程区分开来*/
        switchScanResult.setSwitchIp(switchParameters.getIp()+":"+switchParameters.getThreadName()); // ip
        /*获取交换机四项基本信息ID*/
        switchScanResult.setSwitchId(FunctionalMethods.getSwitchParametersId(switchParameters));
        /*名字 密码 配置密码 连接方式 端口号*/
        switchScanResult.setSwitchName(switchParameters.getName()); //name
        /** EncryptUtil.densificationAndSalt  组合加密 */
        switchScanResult.setSwitchPassword( EncryptUtil.densificationAndSalt( switchParameters.getPassword() )); //password
        switchScanResult.setConfigureCiphers( switchParameters.getConfigureCiphers() == null ? null : EncryptUtil.densificationAndSalt(switchParameters.getConfigureCiphers()));

        /*登录方式 及 端口号*/
        switchScanResult.setLoginMethod(switchParameters.getMode());
        switchScanResult.setPortNumber(switchParameters.getPort());

        /*问题索引*/
        //问题ID
        switchScanResult.setProblemId(totalQuestionTable.getId()+""); // 问题索引

        /*范式分类 范式名称 自定义名称*/
        switchScanResult.setTypeProblem(totalQuestionTable.getTypeProblem());
        switchScanResult.setTemProName(totalQuestionTable.getTemProName());
        switchScanResult.setProblemName(totalQuestionTable.getProblemName());

        /*问题备注 问题详细说明和指导索引*/
        switchScanResult.setRemarks(totalQuestionTable.getRemarks());
        switchScanResult.setProblemDescribeId(totalQuestionTable.getProblemDescribeId());

        /* 取值参数 */
        switchScanResult.setDynamicInformation(new StringBuffer(parameterString));

        /*是否有问题*/
        //有问题、无问题
        switchScanResult.setIfQuestion(problemScanLogic.getProblemId());

        /* 判断是否定义解决问题逻辑*/
        if (totalQuestionTable.getProblemSolvingId() != null){
            switchScanResult.setComId(totalQuestionTable.getProblemSolvingId());//命令索引

        }else {

            /* 告警、异常信息写入 */
            //传输登陆人姓名 及问题简述
            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), null,
                    "风险:"+totalQuestionTable.getId()
                    +totalQuestionTable.getTypeProblem()
                            +totalQuestionTable.getTemProName()
                            +totalQuestionTable.getProblemName()
                    +"未定义解决问题\r\n");

        }

        switchScanResult.setUserName(switchParameters.getLoginUser().getUsername());//登录名称
        switchScanResult.setPhonenumber(switchParameters.getLoginUser().getUser().getPhonenumber()); //登录手机号
        //插入 扫描时间
        switchScanResult.setCreateTime(new DateTime(switchParameters.getScanningTime(), "yyyy-MM-dd HH:mm:ss"));

        //插入问题
        switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);
        int insertId = switchScanResultService.insertSwitchScanResult(switchScanResult);

        return Long.valueOf(insertId).longValue();
    };

    /**
     * @method: 查询扫描出的问题表 放入 websocket
     * @Param: []
     * @return: java.util.List<com.sgcc.sql.domain.SwitchProblem>
     */
    public List<ScanResultsVO> getUnresolvedProblemInformationByData(Map<String,String> user_String,Map<String,Object> user_Object){
        //用户名
        LoginUser loginUser = (LoginUser)user_Object.get("loginUser");
        String loginName = loginUser.getUsername();

        //扫描时间
        String loginTime = user_String.get("ScanningTime");
        switchProblemService = SpringBeanUtil.getBean(ISwitchProblemService.class);
        List<SwitchProblemVO> switchProblemList = switchProblemService.selectUnresolvedProblemInformationByDataAndUserName(loginTime,loginName);
        if (switchProblemList.size() == 0){
            return new ArrayList<>();
        }

        for (SwitchProblemVO switchProblemVO:switchProblemList){
            Date date1 = new Date();
            switchProblemVO.hproblemId =  Long.valueOf(FunctionalMethods.getTimestamp(date1)+""+ (int)(Math.random()*10000+1)).longValue();
            List<SwitchProblemCO> switchProblemCOList = switchProblemVO.getSwitchProblemCOList();
            for (SwitchProblemCO switchProblemCO:switchProblemCOList){
                valueInformationService = SpringBeanUtil.getBean(IValueInformationService.class);//解决 多线程 service 为null问题
                List<ValueInformationVO> valueInformationVOList = valueInformationService.selectValueInformationVOListByID(switchProblemCO.getValueId());
                for (ValueInformationVO valueInformationVO:valueInformationVOList){
                    Date date2 = new Date();
                    valueInformationVO.hproblemId = Long.valueOf(FunctionalMethods.getTimestamp(date2)+""+ (int)(Math.random()*10000+1)).longValue();
                }
                Date date3 = new Date();
                switchProblemCO.hproblemId = Long.valueOf(FunctionalMethods.getTimestamp(date3)+""+ (int)(Math.random()*10000+1)).longValue();
                switchProblemCO.setValueInformationVOList(valueInformationVOList);
            }
        }

        //将IP地址去重放入set集合中
        HashSet<String> ip_hashSet = new HashSet<>();
        for (SwitchProblemVO switchProblemVO:switchProblemList){
            ip_hashSet.add(switchProblemVO.getSwitchIp());
        }

        //将ip存入回显实体类
        List<ScanResultsVO> scanResultsVOList = new ArrayList<>();
        for (String ip_string:ip_hashSet){
            ScanResultsVO scanResultsVO = new ScanResultsVO();
            scanResultsVO.setSwitchIp(ip_string);
            Date date4 = new Date();
            scanResultsVO.hproblemId = Long.valueOf(FunctionalMethods.getTimestamp(date4)+""+ (int)(Math.random()*10000+1)).longValue();
            scanResultsVOList.add(scanResultsVO);
        }

        for (ScanResultsVO scanResultsVO:scanResultsVOList){
            List<SwitchProblemVO> switchProblemVOList = new ArrayList<>();
            String pinpai = "*";
            String xinghao = "*";
            String banben = "*";
            String zibanben = "*";
            for (SwitchProblemVO switchProblemVO:switchProblemList){
                if (switchProblemVO.getSwitchIp() .equals(scanResultsVO.getSwitchIp()) ){
                    String brand = switchProblemVO.getBrand();
                    if (!(brand .equals("*"))){
                        pinpai = brand;
                    }
                    String switchType = switchProblemVO.getSwitchType();
                    if (!(switchType .equals("*"))){
                        xinghao = switchType;
                    }
                    String firewareVersion = switchProblemVO.getFirewareVersion();
                    if (!(firewareVersion .equals("*"))){
                        banben = firewareVersion;
                    }
                    String subVersion = switchProblemVO.getSubVersion();
                    if (!(subVersion .equals("*"))){
                        zibanben = subVersion;
                    }
                    switchProblemVOList.add(switchProblemVO);
                }
            }
            scanResultsVO.setSwitchIp(scanResultsVO.getSwitchIp());
            scanResultsVO.setShowBasicInfo("("+pinpai+" "+xinghao+" "+banben+" "+zibanben+")");
            scanResultsVO.setSwitchProblemVOList(switchProblemVOList);
        }

        for (ScanResultsVO scanResultsVO:scanResultsVOList){
            List<SwitchProblemVO> switchProblemVOList = scanResultsVO.getSwitchProblemVOList();
            for (SwitchProblemVO switchProblemVO:switchProblemVOList){
                switchProblemVO.setSwitchIp(null);
            }
        }
        WebSocketService webSocketService = new WebSocketService();
        webSocketService.sendMessage("loophole"+loginName,scanResultsVOList);
        return scanResultsVOList;
    }

    /**
     * @method: 查询扫描出的问题表 放入 websocket
     * @Param: [ ]
     * @return: java.util.List < com.sgcc.sql.domain.SwitchProblem >
     */
    public List<ScanResultsVO> getSwitchScanResultListBySwitchParameters(SwitchParameters switchParameters){
        switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);
        List<SwitchProblemVO> switchProblemList = switchScanResultService.selectSwitchScanResultListByDataAndUserName(switchParameters.getScanningTime(),switchParameters.getLoginUser().getUsername());
        if (switchProblemList.size() == 0){
            return new ArrayList<>();
        }

        /*根据用户名 和 扫描时间 得到 交换机扫描结果数据*//*
        List<SwitchScanResult> list = switchScanResultService.selectSwitchScanResultByDataAndUserName(loginTime,loginName);
        HashMap<Long,SwitchScanResult> hashMap = new HashMap<>();
        for (SwitchScanResult switchScanResult:list){
            hashMap.put(switchScanResult.getId(),switchScanResult);
        }*/
        /*遍历带有结构的 扫描结果表*/
        for (SwitchProblemVO switchProblemVO:switchProblemList){
            List<SwitchProblemCO> switchProblemCOList = switchProblemVO.getSwitchProblemCOList();
            for (SwitchProblemCO switchProblemCO:switchProblemCOList){
                /*赋值随机数 前端需要*/
                switchProblemCO.setHproblemId(Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
                /*定义 参数集合 */
                List<ValueInformationVO> valueInformationVOList = new ArrayList<>();
                /*根据 结构数据中的 交换机扫描结果ID 在交换机扫描结果数据 hashmap中 取出 *//*
                SwitchScanResult switchScanResult = hashMap.get(switchProblemCO.getQuestionId());*/
                //提取信息 如果不为空 则有参数
                if (switchProblemCO.getDynamicInformation()!=null && !switchProblemCO.getDynamicInformation().equals("")){//switchScanResult.getDynamicInformation()!=null && !switchScanResult.getDynamicInformation().equals("")
                    //String dynamicInformation = switchScanResult.getDynamicInformation();
                    String dynamicInformation = switchProblemCO.getDynamicInformation();

                    /*自定义分隔符*/
                    String customDelimiter = null;
                    Object customDelimiterObject = CustomConfigurationUtil.getValue("configuration.customDelimiter", Constant.getProfileInformation());
                    if (customDelimiterObject instanceof String){
                        customDelimiter = (String) customDelimiterObject;
                    }

                    //几个参数中间的 参数默认是 以  "=:=" 来分割的
                    //设备型号=:=是=:=S3600-28P-EI=:=设备品牌=:=是=:=H3C=:=内部固件版本=:=是=:=3.10,=:=子版本号=:=是=:=1510P09=:=

                    String[] dynamicInformationsplit = dynamicInformation.split(customDelimiter);
                    //判断提取参数 是否为空
                    if (dynamicInformationsplit.length>0){
                        //考虑到 需要获取 参数 的ID 所以要从参数组中获取第一个参数的 ID
                        //所以 参数组 要倒序插入
                        for (int number=dynamicInformationsplit.length-1;number>0;number--){
                            //创建 参数 实体类
                            ValueInformationVO valueInformationVO = new ValueInformationVO();
                            //插入参数
                            //用户名=:=是=:=admin=:=密码=:=否=:=$c$3$ucuLP5tRIUiNMSGST3PKZPvR0Z0bw2/g=:=
                            String setDynamicInformation=dynamicInformationsplit[number];
                            valueInformationVO.setDynamicInformation(setDynamicInformation);
                            --number;
                            String setExhibit=dynamicInformationsplit[number];
                            valueInformationVO.setExhibit(setExhibit);//是否显示
                            if (setExhibit.equals("否")){
                                String setDynamicInformationMD5 = EncryptUtil.densificationAndSalt(setDynamicInformation);
                                valueInformationVO.setDynamicInformation(setDynamicInformationMD5);//动态信息
                            }
                            --number;
                            valueInformationVO.setDynamicVname(dynamicInformationsplit[number]);//动态信息名称
                            valueInformationVO.setHproblemId(Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
                            valueInformationVOList.add(valueInformationVO);
                        }
                    }
                }
                switchProblemCO.setValueInformationVOList(valueInformationVOList);
            }
        }

        //将IP地址去重放入set集合中
        HashSet<String> ip_hashSet = new HashSet<>();
        for (SwitchProblemVO switchProblemVO:switchProblemList){
            ip_hashSet.add(switchProblemVO.getSwitchIp());
        }

        //将ip存入回显实体类
        List<ScanResultsVO> scanResultsVOList = new ArrayList<>();
        for (String ip_string:ip_hashSet){
            ScanResultsVO scanResultsVO = new ScanResultsVO();
            scanResultsVO.setSwitchIp(ip_string);
            scanResultsVO.hproblemId = Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue();
            scanResultsVOList.add(scanResultsVO);
        }

        for (ScanResultsVO scanResultsVO:scanResultsVOList){
            List<SwitchProblemVO> switchProblemVOList = new ArrayList<>();
            String pinpai = "*";
            String xinghao = "*";
            String banben = "*";
            String zibanben = "*";
            for (SwitchProblemVO switchProblemVO:switchProblemList){
                if (switchProblemVO.getSwitchIp() .equals(scanResultsVO.getSwitchIp()) ){
                    String brand = switchProblemVO.getBrand();
                    if (!(brand .equals("*"))){
                        pinpai = brand;
                    }
                    String switchType = switchProblemVO.getSwitchType();
                    if (!(switchType .equals("*"))){
                        xinghao = switchType;
                    }
                    String firewareVersion = switchProblemVO.getFirewareVersion();
                    if (!(firewareVersion .equals("*"))){
                        banben = firewareVersion;
                    }
                    String subVersion = switchProblemVO.getSubVersion();
                    if (subVersion != null && !(subVersion .equals("*"))){
                        zibanben = subVersion;
                    }
                    switchProblemVOList.add(switchProblemVO);
                }
            }
            scanResultsVO.setSwitchIp(scanResultsVO.getSwitchIp());
            scanResultsVO.setShowBasicInfo("("+pinpai+" "+xinghao+" "+banben+" "+zibanben+")");
            scanResultsVO.setSwitchProblemVOList(switchProblemVOList);
        }

        for (ScanResultsVO scanResultsVO:scanResultsVOList){
            scanResultsVO.setHproblemId(Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
            String switchIp = scanResultsVO.getSwitchIp();
            String[] split = switchIp.split(":");
            scanResultsVO.setSwitchIp(split[0]);
            List<SwitchProblemVO> switchProblemVOList = scanResultsVO.getSwitchProblemVOList();
            for (SwitchProblemVO switchProblemVO:switchProblemVOList){
                switchProblemVO.setHproblemId(Long.valueOf(FunctionalMethods.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
                switchProblemVO.setSwitchIp(null);
            }
        }

        WebSocketService webSocketService = new WebSocketService();
        webSocketService.sendMessage("loophole"+switchParameters.getLoginUser().getUsername(),scanResultsVOList);
        return scanResultsVOList;

    }

    /*Inspection Completed*/
    /**
     * @method: 根据命令ID获取具体命令，执行并返回交换机返回信息
     * @Param:
     * @return:  返回的是 解决问题ID
     * 分析ID 连接方式 ssh和telnet连接
     */
    public CommandReturn executeScanCommandByCommandId(SwitchParameters switchParameters,
                                                      TotalQuestionTable totalQuestionTable,
                                                      String commandId) {
        //命令ID获取具体命令
        commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
        CommandLogic commandLogic = commandLogicService.selectCommandLogicById(commandId);

        /*告警、异常信息写入*/
        if (commandLogic == null){
            //传输登陆人姓名 及问题简述
            String subversionNumber = switchParameters.getSubversionNumber();

            if (subversionNumber!=null){
                subversionNumber = "、"+subversionNumber;
            }

            AbnormalAlarmInformationMethod.afferent(switchParameters.getIp(), switchParameters.getLoginUser().getUsername(), "问题日志",
                    "异常:" +
                    "IP地址为:"+switchParameters.getIp()+"。"+
                    "基本信息为:"+switchParameters.getDeviceBrand()+"、"+switchParameters.getDeviceModel()+"、"+switchParameters.getFirmwareVersion()+subversionNumber+"。"+
                    "问题ID为"+totalQuestionTable.getId()+ "问题分类:" +totalQuestionTable.getTypeProblem()+"问题名称:" +totalQuestionTable.getProblemName()+":" +totalQuestionTable.getTemProName()+"，定义问题扫描命令不存在。\r\n");

            CommandReturn commandReturn = new CommandReturn();
            commandReturn.setSuccessOrNot(false);

            return commandReturn;

        }

        //具体命令
        String command = commandLogic.getCommand().trim();
        //执行命令
        ExecuteCommand executeCommand = new ExecuteCommand();
        List<String> command_string_List = executeCommand.executeScanCommandByCommand(switchParameters, command);

        //判断是否简单检验 1L为简单校验  默认0L 为分析数据表自定义校验
        String first_problem_scanLogic_Id = "";
        if (!(commandLogic.getResultCheckId().equals("1"))){
            //分析第一条ID
            first_problem_scanLogic_Id = commandLogic.getProblemId();
        }else {

            /*接下来还是命令 不进行分析*/
            CommandReturn commandReturn = executeScanCommandByCommandId(switchParameters,totalQuestionTable,commandLogic.getEndIndex());
            return commandReturn;

        }

        CommandReturn commandReturn = new CommandReturn();
        if (MyUtils.isCollectionEmpty(command_string_List)){
            /* 是否执行成功 */
            commandReturn.setSuccessOrNot(false);

        }else {
            /* 是否执行成功 */
            commandReturn.setSuccessOrNot(true);
            /* 命令返回信息*/
            commandReturn.setReturnResults(command_string_List);
            /* 分析ID */
            commandReturn.setAnalysisID(first_problem_scanLogic_Id);
        }

        return commandReturn;
    }

    /*Inspection Completed*/
    /**
    * @Description 执行分析
    * @desc
    * @param switchParameters	 交换机登录信息
     * @param totalQuestionTable	交换机问题表数据
     * @param commandReturn	     交换机执行命令返回结果 即  分析ID
     * @param current_Round_Extraction_String  当前提取数据字符串
     * @param extractInformation_string    所有提取数据字符串
     * @return
    */
    public String analysisReturnResults(SwitchParameters switchParameters,
                                        TotalQuestionTable totalQuestionTable,
                                        CommandReturn commandReturn,String current_Round_Extraction_String,String extractInformation_string){

        //配置文件中 获取 最大循环次数  循环 为定义问题的循环 例如 获取多用户
        Integer numberOfCycles = 10;

        Object numberOfCyclesObject = CustomConfigurationUtil.getValue("configuration.numberOfCycles", Constant.getProfileInformation());
        if (numberOfCyclesObject instanceof Integer){
            numberOfCycles = (Integer) numberOfCyclesObject;
        }

        /*根据分析ID获取问题扫描逻辑详细信息*/
        String problemScanLogic_string = selectProblemScanLogicById(switchParameters, totalQuestionTable,
                commandReturn.getReturnResults() //交换机返回信息  行信息数组
                ,current_Round_Extraction_String,extractInformation_string, 0,
                commandReturn.getAnalysisID()  //分析第一条ID
                ,null,null,0,0,numberOfCycles);// loop end


        return problemScanLogic_string;
    }


    /*Inspection Completed*/
    /**
     * @method: 5.获取交换机可扫描的问题并执行分析操作
     * 当 totalQuestionTables 不为空时，为专项扫描
     * @Param: [user_String, connectMethod, telnetSwitchMethod]
     * @return: com.sgcc.common.core.domain.AjaxResult
     */
    public AjaxResult scanProblem (SwitchParameters switchParameters,
                                  List<TotalQuestionTable> totalQuestionTables) {
        /* 存储 满足思想基本信息的、将要扫描的 交换机问题 */
        List<TotalQuestionTable> totalQuestionTableList = new ArrayList<>();

        /* totalQuestionTables == null 的时候 是扫描全部问题
         * totalQuestionTables != null 的时候 是专项扫描
         * 获取满足思想基本信息的、将要扫描的 交换机问题*/
        if (totalQuestionTables == null){

            /* 根据交换机基本信息 查询 可扫描的交换机问题 */
            AjaxResult commandIdByInformation_ajaxResult = commandIdByInformation(switchParameters);
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


                return  AjaxResult.success("未定义交换机问题");
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
                CommandReturn commandReturn = executeScanCommandByCommandId(switchParameters,totalQuestionTable,totalQuestionTable.getLogicalID().replace("命令",""));
                if (!commandReturn.isSuccessOrNot()){
                    /*交换机返回错误信息处理
                     * 遍历下一个问题*/
                    continue;
                }

                //分析
                String analysisReturnResults_String = analysisReturnResults(switchParameters, totalQuestionTable,
                        commandReturn ,  "",  "");

            }else if (totalQuestionTable.getLogicalID().indexOf("分析") != -1){

                CommandReturn commandReturn = new CommandReturn();
                commandReturn.setReturnResults(new ArrayList<>());
                commandReturn.setAnalysisID(totalQuestionTable.getLogicalID().replaceAll("分析",""));

                //分析
                String analysisReturnResults_String = analysisReturnResults(switchParameters, totalQuestionTable,
                        commandReturn,  "",  "");

            }
        }

        return new AjaxResult();
    }

    /*Inspection Completed*/
    /**
     * @method: 根据交换机信息查询 获取 扫描问题的
     *  根据交换机基本信息 查询 可执行命令的 命令信息
     * @Param: []
     * @return: java.util.List<java.lang.Long>
     */
    public AjaxResult commandIdByInformation(SwitchParameters switchParameters) {

        /* 自定义交换问题表 根据四项基本信息 查询问题数据*/
        TotalQuestionTable totalQuestionTable = new TotalQuestionTable();

        totalQuestionTable.setBrand(switchParameters.getDeviceBrand());
        totalQuestionTable.setType(switchParameters.getDeviceModel());
        totalQuestionTable.setFirewareVersion(switchParameters.getFirmwareVersion());
        totalQuestionTable.setSubVersion(switchParameters.getSubversionNumber());

        //查询可扫描问题
        //List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.queryScannableQuestionsList(totalQuestionTable);
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);
        /*拼接 SQL*/
        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.queryVagueScannableQuestionsList(totalQuestionTable);


        if (MyUtils.isCollectionEmpty(totalQuestionTables)){

            return new AjaxResult();
        }else {

            List<TotalQuestionTable> pojoList = new ArrayList<>();

            for (TotalQuestionTable pojo:totalQuestionTables){
                /*判断是否定义分析逻辑*/
                if (pojo.getLogicalID()!=null && pojo.getLogicalID()!=""){
                    pojoList.add(pojo);
                }
            }

            return AjaxResult.success(pojoList);
        }
    }
}