package com.sgcc.sql.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.sgcc.common.annotation.MyLog;
import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.enums.BusinessType;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.connect.method.SshMethod;
import com.sgcc.connect.method.TelnetSwitchMethod;
import com.sgcc.connect.util.SpringBeanUtil;
import com.sgcc.connect.util.SshConnect;
import com.sgcc.connect.util.TelnetComponent;
import com.sgcc.sql.domain.*;
import com.sgcc.sql.service.*;
import com.sgcc.sql.thread.DirectionalScanThreadPool;
import com.sgcc.sql.thread.ScanFixedThreadPool;
import com.sgcc.sql.util.EncryptUtil;
import com.sgcc.sql.util.MyUtils;
import com.sgcc.sql.util.PathHelper;
import com.sgcc.sql.util.RSAUtils;
import com.sgcc.sql.webSocket.WebSocketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 与交换机交互方法类
 * @date 2022年01月05日 14:
 *
 *
 */
@Api("扫描相关")
@RestController
@RequestMapping("/sql/SwitchInteraction")
@Transactional(rollbackFor = Exception.class)
public class SwitchInteraction {

    @Autowired
    private static ICommandLogicService commandLogicService;

    @Autowired
    private static IReturnRecordService returnRecordService;

    @Autowired
    private static IProblemScanLogicService problemScanLogicService;

    @Autowired
    private static IValueInformationService valueInformationService;

    @Autowired
    private static ISwitchProblemService switchProblemService;

    @Autowired
    private static ITotalQuestionTableService totalQuestionTableService;

    @Autowired
    private static IBasicInformationService basicInformationService;

    @Autowired
    private static ISwitchScanResultService switchScanResultService;

    /*==================================================================================================================
    ====================================================================================================================
    ====================================================================================================================
    ==================================================================================================================*/

    /**
     *
     * a
     * 测试获取交换机基本信息逻辑执行结果
     *
     * @param command
     * @param pojoList
     * @return
     */
    @ApiOperation("预执行获取交换机基本信息")
    @GetMapping("testToObtainBasicInformation/{ip}/{name}/{password}/{port}/{mode}/{configureCiphers}/{command}")
    @MyLog(title = "测试获取交换机基本信息逻辑执行结果", businessType = BusinessType.OTHER)
    public String testToObtainBasicInformation(@PathVariable String ip,@PathVariable String name,@PathVariable String password,@PathVariable String port,@PathVariable String mode,@PathVariable String configureCiphers,
                                               @PathVariable String[] command,@RequestBody List<String> pojoList) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String scanningTime = simpleDateFormat.format(new Date());

        //用户信息  及   交换机信息
        Map<String,String> user_String = new HashMap<>();
        user_String.put("mode",mode);//登录方式
        user_String.put("ip",ip);//ip地址
        user_String.put("name",name);//用户名
        user_String.put("password",password);//用户密码
        user_String.put("configureCiphers",configureCiphers);//配置密码
        user_String.put("port",port+"");//登录端口号
        //扫描时间 获取当前时间  时间格式为 "yyyy-MM-dd HH:mm:ss"  字符串
        user_String.put("ScanningTime",scanningTime);
        //交换机信息  预设为空
        //设备型号
        user_String.put("deviceModel",null);
        //设备品牌
        user_String.put("deviceBrand",null);
        //内部固件版本
        user_String.put("firmwareVersion",null);
        //子版本号
        user_String.put("subversionNumber",null);


        Map<String,Object> user_Object = new HashMap<>();
        //ssh连接方法
        SshMethod connectMethod = null;
        user_Object.put("connectMethod",connectMethod);
        //telnet连接方法
        TelnetSwitchMethod telnetSwitchMethod = null;
        user_Object.put("telnetSwitchMethod",telnetSwitchMethod);
        //系统登录人信息
        user_Object.put("loginUser",loginUser);

        AjaxResult requestConnect_ajaxResult = requestConnect(user_String);

        Map<String,Object> objectMap = (Map<String,Object>) requestConnect_ajaxResult.get("data");

        if (objectMap == null){
            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(loginUser.getUsername(),"错误："+ip+"交换机连接失败\r\n");
            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFile("错误："+ip+"交换机连接失败\r\n"
                        +"方法com.sgcc.web.controller.sql.SwitchInteraction.testToObtainBasicInformation");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //是否连接成功 返回信息集合的 第一项 为 是否连接成功
        boolean requestConnect_boolean = (boolean) objectMap.get("TrueAndFalse");

        //如果连接成功
        if(requestConnect_boolean){
            //密码 MD5 加密
            String passwordDensificationAndSalt = EncryptUtil.densificationAndSalt(user_String.get("password"));
            user_String.put("password",passwordDensificationAndSalt);//用户密码
            //密码 MD5 加密
            String configureCiphersDensificationAndSalt = EncryptUtil.densificationAndSalt(user_String.get("configureCiphers"));
            user_String.put("configureCiphers",configureCiphersDensificationAndSalt);//用户密码

            //返回信息集合的 第二项 为 连接方式：ssh 或 telnet
            String requestConnect_way = (String) objectMap.get("way");
            //SSH 连接工具
            SshConnect sshConnect = null;
            //SSH 连接工具
            TelnetComponent telnetComponent = null;

            user_Object.put("connectMethod",connectMethod);
            user_Object.put("telnetSwitchMethod",telnetSwitchMethod);
            user_Object.put("sshConnect",sshConnect);
            user_Object.put("telnetComponent",telnetComponent);

            //如果连接方式为ssh则 连接方法返回集合参数为 connectMethod参数
            //如果连接方式为telnet则 连接方法返回集合参数为 telnetSwitchMethod参数
            if (requestConnect_way.equalsIgnoreCase("ssh")){
                connectMethod = (SshMethod)objectMap.get("connectMethod");
                sshConnect = (SshConnect)objectMap.get("sshConnect");

                user_Object.put("connectMethod",connectMethod);
                user_Object.put("sshConnect",sshConnect);
            }else if (requestConnect_way.equalsIgnoreCase("telnet")){
                telnetSwitchMethod = (TelnetSwitchMethod)objectMap.get("telnetSwitchMethod");
                telnetComponent = (TelnetComponent)objectMap.get("telnetComponent");

                user_Object.put("telnetSwitchMethod",telnetSwitchMethod);
                user_Object.put("telnetComponent",telnetComponent);
            }

            List<ProblemScanLogic> problemScanLogicList = new ArrayList<>();

            /*遍历分析逻辑字符串集合：List<String> pojoList
            通过 调用 analysisProblemScanLogic 方法 将字符串 转化为 分析逻辑实体类，
            并放入 分析逻辑实体类集合 List<ProblemScanLogic> problemScanLogicList。*/
            for (String pojo:pojoList){
                //本条是分析 下一条是 分析
                ProblemScanLogic problemScanLogic = DefinitionProblemController.analysisProblemScanLogic(pojo, "分析");
                problemScanLogicList.add(problemScanLogic);
            }

            //将相同ID  时间戳 的 实体类 放到一个实体
            List<ProblemScanLogic> problemScanLogics = DefinitionProblemController.definitionProblem(problemScanLogicList);

            //获取交换机基本信息
            //getBasicInformationList 通过 特定方式 获取 基本信息
            //getBasicInformationList 通过扫描方式 获取 基本信息
            AjaxResult basicInformationList_ajaxResult = getBasicInformationTest(user_String,user_Object,command,problemScanLogics);   //getBasicInformationList
            HashMap<String,String> data = (HashMap<String,String>) basicInformationList_ajaxResult.get("data");
            System.err.println("测试获取基本信息");

            System.err.println("pinpai："+data.get("pinpai"));
            System.err.println("xinghao："+data.get("xinghao"));
            System.err.println("banben："+data.get("banben"));
            System.err.println("zibanben："+data.get("zibanben"));


            /*关闭连接交换机*/
            if (requestConnect_way.equalsIgnoreCase("ssh")){
                connectMethod.closeConnect(sshConnect);
            }else if (requestConnect_way.equalsIgnoreCase("telnet")){
                telnetSwitchMethod.closeSession(telnetComponent);
            }


            return "设备品牌："+data.get("pinpai")+" 设备型号："+data.get("xinghao")+" 内部固件版本："+data.get("banben")+" 子版本号："+data.get("zibanben");
        }

        return  "交换机连接失败";
    }


    /***
     * @method: 多线程扫描 获取到的是字符串格式的参数 {"ip":"192.168.1.100","name":"admin","password":"admin","mode":"ssh","port":"22"}
     * @Param: [switchInformation]
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @ApiOperation("专项扫描问题")
    @GetMapping("directionalScann/{totalQuestionTableId}/{scanNum}")///{totalQuestionTableId}/{scanNum}
    @MyLog(title = "专项扫描问题", businessType = BusinessType.OTHER)
    public String directionalScann(@RequestBody List<String> switchInformation,@PathVariable  List<Long> totalQuestionTableId,@PathVariable  Long scanNum) {//@RequestBody List<String> switchInformation,@PathVariable  List<Long> totalQuestionTableId,@PathVariable  Long scanNum

        // 预设多线程参数 Object[] 中的参数格式为： {mode,ip,name,password,port}
        List<Object[]> objectsList = new ArrayList<>();
        for (String information:switchInformation){
            // information  : {"ip":"192.168.1.100","name":"admin","password":"admin","mode":"ssh","port":"22"}
            // 去除花括号得到： "ip":"192.168.1.100","name":"admin","password":"admin","mode":"ssh","port":"22"
            information = information.replace("{","");
            information = information.replace("}","");
            /*以逗号分割 获取到 集合 集合为：
                information_split.get(0)  "ip":"192.168.1.100"
                information_split.get(1)  "name":"admin"
                information_split.get(2)  "password":"admin"
                information_split.get(3)  "mode":"ssh"
                information_split.get(4)  "port":"22"
            */
            String[] information_split = information.split(",");
            // 四个参数 设默认值
            String ip = "";
            String name = "";
            String password = "";
            String mode = "";
            String configureCiphers = "";
            int port = 0;

            for (String string:information_split){
                // string  参数为  ip:192.168.1.100  或  name:admin 或 password:admin 或 mode:ssh 或 port:22
                string = string.replace("\"","");
                // 以 ： 分割 得到
                /*
                string_split[0] ip           string_split[1] 192.168.1.100
                string_split[0] name         string_split[1] admin
                string_split[0] password     string_split[1] admin
                string_split[0] mode         string_split[1] ssh
                string_split[0] port         string_split[1] 22
                */
                String[] string_split = string.split(":");
                switch (string_split[0]){
                    case "ip" :  ip=string_split[1];
                        break;
                    case "name" :  name=string_split[1];
                        break;
                    case "password" :
                        password=string_split[1];
                        password = RSAUtils.decryptFrontEndCiphertext(password);
                        break;
                    case "configureCiphers" :
                        configureCiphers=string_split[1];
                        configureCiphers = RSAUtils.decryptFrontEndCiphertext(configureCiphers);
                        break;
                    case "mode" :  mode=string_split[1];
                        break;
                    case "port" :  port= Integer.valueOf(string_split[1]).intValue() ;
                        break;
                }
            }
            //以多线程中的格式 存放数组中
            //连接方式，ip，用户名，密码，端口号
            Object[] objects = {mode,ip,name,password,configureCiphers,port};
            objectsList.add(objects);
        }
        List<TotalQuestionTable> totalQuestionTables = new ArrayList<>();
        Long[] ids = totalQuestionTableId.toArray(new Long[totalQuestionTableId.size()]);
        if (ids.length != 0){
            totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);//解决 多线程 service 为null问题
            totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableByIds(ids);
        }else {
            return "扫描结束";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String scanningTime = simpleDateFormat.format(new Date());
        LoginUser login = SecurityUtils.getLoginUser();

        try {
            DirectionalScanThreadPool.switchLoginInformations(objectsList,totalQuestionTables,scanningTime,login,Integer.valueOf(scanNum+"").intValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebSocketService.sendMessage(login.getUsername(),"系统信息："+"扫描结束\r\n");
        try {
            PathHelper.writeDataToFile("系统信息："+"扫描结束\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "扫描结束";
    }


    /**
    * @method: 多线程扫描 获取到的是字符串格式的参数 {"ip":"192.168.1.100","name":"admin","password":"admin","mode":"ssh","port":"22"}
    * @Param: [switchInformation]
    * @return: void
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */

    @ApiOperation("扫描全部问题")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "switchInformation", value = "交换机登录信息集合", dataType = "List<String>", dataTypeClass = Object.class),
            @ApiImplicitParam(name = "scanNum", value = "线程数", dataType = "Long", dataTypeClass = Long.class),
    })
    @GetMapping("multipleScans/{scanNum}")
    @MyLog(title = "扫描全部问题", businessType = BusinessType.OTHER)
    public String multipleScans(@RequestBody List<String> switchInformation,@PathVariable  Long scanNum) {//待测
        // 预设多线程参数 Object[] 中的参数格式为： {mode,ip,name,password,port}
        List<Object[]> objectsList = new ArrayList<>();
        for (String information:switchInformation){
            // information  : {"ip":"192.168.1.100","name":"admin","password":"admin","mode":"ssh","port":"22"}
            // 去除花括号得到： "ip":"192.168.1.100","name":"admin","password":"admin","mode":"ssh","port":"22"
            information = information.replace("{","");
            information = information.replace("}","");
            /*以逗号分割 获取到 集合 集合为：
                information_split.get(0)  "ip":"192.168.1.100"
                information_split.get(1)  "name":"admin"
                information_split.get(2)  "password":"admin"
                information_split.get(3)  "mode":"ssh"
                information_split.get(4)  "port":"22"
            */
            String[] information_split = information.split(",");
            // 四个参数 设默认值
            String ip = "";
            String name = "";
            String password = "";
            String configureCiphers = "";//配置密码
            String mode = "";
            int port = 0;

            for (String string:information_split){
                // string  参数为  ip:192.168.1.100  或  name:admin 或 password:admin 或 mode:ssh 或 port:22
                string = string.replace("\"","");
                // 以 ： 分割 得到
                /*
                string_split[0] ip           string_split[1] 192.168.1.100
                string_split[0] name         string_split[1] admin
                string_split[0] password     string_split[1] admin
                string_split[0] mode         string_split[1] ssh
                string_split[0] port         string_split[1] 22
                */
                String[] string_split = string.split(":");
                switch (string_split[0]){
                    case "ip" :  ip=string_split[1];
                        break;
                    case "name" :  name=string_split[1];
                        break;
                    case "password" :
                        String ciphertext = string_split[1];
                        String ciphertextString = RSAUtils.decryptFrontEndCiphertext(ciphertext);
                        password = ciphertextString;
                        break;
                    case "configureCiphers" :
                        String configureCiphersciphertext = string_split[1];
                        String configureCiphersciphertextString = RSAUtils.decryptFrontEndCiphertext(configureCiphersciphertext);
                        configureCiphers = configureCiphersciphertextString;
                        break;

                    case "mode" :  mode=string_split[1];
                        break;
                    case "port" :  port= Integer.valueOf(string_split[1]).intValue() ;
                        break;
                }
            }
            //以多线程中的格式 存放数组中
            //连接方式，ip，用户名，密码，端口号
            Object[] objects = {mode,ip,name,password,configureCiphers,port};
            objectsList.add(objects);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ScanningTime = simpleDateFormat.format(new Date());
        LoginUser login = SecurityUtils.getLoginUser();

        //ScanThread.switchLoginInformations(objectsList,ScanningTime,login);
        //线程池
        try {
            ScanFixedThreadPool.switchLoginInformations(objectsList,ScanningTime,login,Integer.valueOf(scanNum+"").intValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebSocketService.sendMessage(login.getUsername(),"发送和接收："+"扫描结束\r\n");
        try {
            PathHelper.writeDataToFile("系统信息："+"扫描结束\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "扫描结束";
    }



    /**
     *
     * a
     *
     *
     *
    * @method: 扫描方法 logInToGetBasicInformation
    * @Param: [mode, ip, name, password, port] 传参 ：mode连接方式, ip 地址, name 用户名, password 密码, port 端口号，
     *          loginUser 登录人信息，time 扫描时间
     *          List<TotalQuestionTable> totalQuestionTables  用于 专项扫描
    * @return: com.sgcc.common.core.domain.AjaxResult
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @GetMapping("logInToGetBasicInformation")
    public AjaxResult logInToGetBasicInformation(String mode, String ip, String name, String password, String configureCiphers , int port,
                                                 LoginUser loginUser,String ScanningTime,List<TotalQuestionTable> totalQuestionTables) {

        //交换机信息
        Map<String,String> user_String = new HashMap<>();

        //交换机用户信息
        // 登录方式 mode
        // IP地址 ip
        // 用户名 name
        // 用户密码 password
        // 配置密码 configureCiphers
        // 端口号 port
        // 扫描时间 ScanningTime
        user_String.put("mode",mode);//登录方式
        user_String.put("ip",ip);//ip地址
        user_String.put("name",name);//用户名
        user_String.put("password",password);//用户密码
        user_String.put("configureCiphers",configureCiphers);//配置密码
        user_String.put("port",port+"");//登录端口号
        //扫描时间 获取当前时间  时间格式为 "yyyy-MM-dd HH:mm:ss"  字符串
        user_String.put("ScanningTime",ScanningTime);

        //交换机信息  预设为空
        //设备型号
        user_String.put("deviceModel",null);
        //设备品牌
        user_String.put("deviceBrand",null);
        //内部固件版本
        user_String.put("firmwareVersion",null);
        //子版本号
        user_String.put("subversionNumber",null);

        Map<String,Object> user_Object = new HashMap<>();
        //ssh连接方法
        SshMethod connectMethod = null;
        user_Object.put("connectMethod",connectMethod);
        //telnet连接方法
        TelnetSwitchMethod telnetSwitchMethod = null;
        user_Object.put("telnetSwitchMethod",telnetSwitchMethod);
        //系统登录人信息
        user_Object.put("loginUser",loginUser);

        //连接交换机  requestConnect：
        //传入参数：[mode 连接方式, ip IP地址, name 用户名, password 密码, port 端口号,
        //                          connectMethod ssh连接方法, telnetSwitchMethod telnet连接方法]
        //返回信息为：[是否连接成功,mode 连接方式, ip IP地址, name 用户名, password 密码, port 端口号,
        //                         connectMethod ssh连接方法 或者 telnetSwitchMethod telnet连接方法（其中一个，为空者不存在）
        //                         SshConnect ssh连接工具 或者 TelnetComponent telnet连接工具（其中一个，为空者不存在）]
        AjaxResult requestConnect_ajaxResult = requestConnect(user_String);

        //如果返回为 交换机连接失败 则连接交换机失败
        if(requestConnect_ajaxResult.get("msg").equals("交换机连接失败")){
            return AjaxResult.error("交换机连接失败");
        }

        //解析返回参数 data
        /*
        返回值 list集合
        元素0 ：是否连接成功
        元素1 ：连接方法
        元素2 ：交换机IP
        元素3 ：交换机登录用户
        元素4 ：交换机登录用户密码
        元素5 ：交换机连接端口号
        元素6 ：ssh连接对象：如果连接方法为telnet则connectMethod为空，插入connectMethod失败
        元素7 ：telnet连接对象：如果连接方法为ssh则telnetSwitchMethod为空，插入telnetSwitchMethod失败
        元素8 ：ssh连接工具对象
        元素9 ：telnet连接工具对象
        */

        Map<String,Object> objectMap = (Map<String,Object>) requestConnect_ajaxResult.get("data");

        //是否连接成功 返回信息集合的 第一项 为 是否连接成功
        boolean requestConnect_boolean = (boolean) objectMap.get("TrueAndFalse");

        //如果连接成功
        if(requestConnect_boolean){
            //密码 MD5 加密
            String passwordDensificationAndSalt = EncryptUtil.densificationAndSalt(user_String.get("password"));
            user_String.put("password",passwordDensificationAndSalt);//用户密码
            //密码 MD5 加密
            String configureCiphersDensificationAndSalt = EncryptUtil.densificationAndSalt(user_String.get("configureCiphers"));
            user_String.put("configureCiphers",configureCiphersDensificationAndSalt);//用户密码

            //返回信息集合的 第二项 为 连接方式：ssh 或 telnet
            String requestConnect_way = (String) objectMap.get("way");
            //SSH 连接工具
            SshConnect sshConnect = null;
            //SSH 连接工具
            TelnetComponent telnetComponent = null;

            user_Object.put("connectMethod",connectMethod);
            user_Object.put("telnetSwitchMethod",telnetSwitchMethod);
            user_Object.put("sshConnect",sshConnect);
            user_Object.put("telnetComponent",telnetComponent);

            //如果连接方式为ssh则 连接方法返回集合参数为 connectMethod参数
            //如果连接方式为telnet则 连接方法返回集合参数为 telnetSwitchMethod参数
            if (requestConnect_way.equalsIgnoreCase("ssh")){
                connectMethod = (SshMethod)objectMap.get("connectMethod");
                sshConnect = (SshConnect)objectMap.get("sshConnect");

                user_Object.put("connectMethod",connectMethod);
                user_Object.put("sshConnect",sshConnect);
            }else if (requestConnect_way.equalsIgnoreCase("telnet")){
                telnetSwitchMethod = (TelnetSwitchMethod)objectMap.get("telnetSwitchMethod");
                telnetComponent = (TelnetComponent)objectMap.get("telnetComponent");

                user_Object.put("telnetSwitchMethod",telnetSwitchMethod);
                user_Object.put("telnetComponent",telnetComponent);
            }

            //获取交换机基本信息
            //getBasicInformationList 通过 特定方式 获取 基本信息
            //getBasicInformationList 通过扫描方式 获取 基本信息
            AjaxResult basicInformationList_ajaxResult = getBasicInformationList(user_String,user_Object);   //getBasicInformationList
            //AjaxResult basicInformationList_ajaxResult = getBasicInformationCurrency(user_String, user_Object); //basicInformationCurrency

            if (!(basicInformationList_ajaxResult.get("msg").equals("未定义该交换机获取基本信息命令及分析"))){

                //5.获取交换机可扫描的问题并执行分析操作
                AjaxResult ajaxResult = scanProblem(
                        user_String, //登录交换机的 用户信息 登录方式、ip、name、password
                        user_Object,
                         totalQuestionTables);
                if (requestConnect_way.equalsIgnoreCase("ssh")){
                    connectMethod.closeConnect(sshConnect);
                }else if (requestConnect_way.equalsIgnoreCase("telnet")){
                    telnetSwitchMethod.closeSession(telnetComponent);
                }

                if (ajaxResult !=null && ajaxResult.get("msg").equals("未定义交换机问题")){
                    LoginUser user = (LoginUser) user_Object.get("loginUser");
                    WebSocketService.sendMessage(user.getUsername(),"风险:"+"ip:"+user_String.get("ip") + "未定问题"+"\r\n");
                    try {
                        PathHelper.writeDataToFile("风险:"+"ip:"+user_String.get("ip") + "未定问题"+"\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return basicInformationList_ajaxResult;
            }
            return AjaxResult.error("未定义该交换机获取基本信息命令及分析");
        }

        return AjaxResult.error("交换机连接失败");
    }


    /**
     * 连接交换机方法
     * @method: 连接交换机
     * @Param: [mode 连接方式, ip IP地址, name 用户名, password 密码, port 端口号,]
     * @Param: [connectMethod ssh连接方法, telnetSwitchMethod telnet连接方法]
     * @E-mail: WeiYaNing97@163.com
     *
     * 通过参数参数 [mode 连接方式, ip IP地址, name 用户名, password 密码, port 端口号,] 连接交换机，
     * 如果是ssh连接 则connectMethod 属性值存在，用于通过ssh连接 ，telnetSwitchMethod为空
     * 如果是telnet连接 则telnetSwitchMethod 属性值存在，用于通过ssh连接 ，connectMethod为空
     *返回信息为 是否连接成功 + 全部传入参数 ，此时 connectMethod(telnetSwitchMethod) 已经连接交换机
     */
    @GetMapping("requestConnect")
    public static AjaxResult requestConnect(Map<String,String> user_String) {
        //交换机用户信息
        // 登录方式 mode
        // IP地址 ip
        // 用户名 name
        // 用户密码 password
        // 端口号 port

        String way = user_String.get("mode");//连接方法
        String hostIp = user_String.get("ip") ;//ip地址
        int portID = Integer.valueOf(user_String.get("port")).intValue() ;//端口号
        String userName = user_String.get("name") ;//姓名
        String userPassword = user_String.get("password") ;//密码
        String configureCiphers = user_String.get("configureCiphers");//配置密码

        //设定连接结果 预设连接失败为 false
        boolean is_the_connection_successful =false;
        //ssh 和 telnet 连接方法 预设为null
        SshConnect sshConnect = null;
        TelnetComponent telnetComponent = null;
        //连接方法
        SshMethod connectMethod = null;
        TelnetSwitchMethod telnetSwitchMethod = null;

        if (way.equalsIgnoreCase("ssh")){
            //创建ssh连接方法
            connectMethod = new SshMethod();
            //连接ssh 成功为 true  失败为  false
            sshConnect = connectMethod.requestConnect(hostIp, portID, userName, userPassword);

            if (sshConnect!=null){
                is_the_connection_successful = true;
            }
        }else if (way.equalsIgnoreCase("telnet")){
            //创建telnet连接方法
            telnetSwitchMethod = new TelnetSwitchMethod();
            //连接telnet 成功为 true  失败为  false
            telnetComponent = telnetSwitchMethod.requestConnect(hostIp, portID, userName, userPassword, null);

            if (telnetComponent!=null){
                is_the_connection_successful = true;
            }

        }

        /*List<Object> objectList = new ArrayList<>();  //设定返回值 list集合
        objectList.add(is_the_connection_successful); //元素0 ：是否连接成功
        objectList.add(way);                          //元素1 ：连接方法
        objectList.add(hostIp);                       //元素2 ：交换机ID
        objectList.add(userName);                     //元素3 ：交换机登录用户
        objectList.add(userPassword);                 //元素4 ：交换机登录用户密码
        objectList.add(portID);                       //元素5 ：交换机连接端口号
        objectList.add(connectMethod);                //元素6 ：ssh连接对象：如果连接方法为telnet则connectMethod为空，插入connectMethod失败
        objectList.add(telnetSwitchMethod);           //元素7 ：telnet连接对象：如果连接方法为ssh则telnetSwitchMethod为空，插入telnetSwitchMethod失败
        objectList.add(sshConnect);                   //元素8 ：ssh连接工具对象
        objectList.add(telnetComponent);              //元素9 ：telnet连接工具对象
        objectList.add(configureCiphers);             //元素10 ： 配置密码configureCiphers*/

        Map<String,Object> objectMap = new HashMap<>();  //设定返回值 list集合
        objectMap.put("TrueAndFalse",is_the_connection_successful); //元素0 ：是否连接成功
        objectMap.put("way",way);                          //元素1 ：连接方法
        objectMap.put("hostIp",hostIp);                       //元素2 ：交换机ID
        objectMap.put("userName",userName);                     //元素3 ：交换机登录用户
        objectMap.put("userPassword",userPassword);                 //元素4 ：交换机登录用户密码
        objectMap.put("portID",portID);                       //元素5 ：交换机连接端口号
        objectMap.put("connectMethod",connectMethod);                //元素6 ：ssh连接对象：如果连接方法为telnet则connectMethod为空，插入connectMethod失败
        objectMap.put("telnetSwitchMethod",telnetSwitchMethod);           //元素7 ：telnet连接对象：如果连接方法为ssh则telnetSwitchMethod为空，插入telnetSwitchMethod失败
        objectMap.put("sshConnect",sshConnect);                   //元素8 ：ssh连接工具对象
        objectMap.put("telnetComponent",telnetComponent);              //元素9 ：telnet连接工具对象
        objectMap.put("configureCiphers",configureCiphers);             //元素10 ： 配置密码configureCiphers



        /* 返回信息 ： [是否连接成功,mode 连接方式, ip IP地址, name 用户名, password 密码, port 端口号,
                connectMethod ssh连接方法 或者 telnetSwitchMethod telnet连接方法（其中一个，为空者不存在）]*/
        if(is_the_connection_successful){
            //enable
            String enable = enable(objectMap);
            if (enable.equals("交换机连接成功")){
                return AjaxResult.success(objectMap);
            }else {
                return AjaxResult.error("交换机连接失败");
            }
        }else {
            return AjaxResult.error("交换机连接失败");
        }

    }

    /**
     * 配置密码  enable 方法
     * @param objectMap
     * @return
     */
    public static String enable(Map<String,Object> objectMap) {
        String way = (String) objectMap.get("way");
        String hostIp = (String) objectMap.get("hostIp");

        String returnString = null;
        if (way.equalsIgnoreCase("ssh")){
            SshMethod connectMethod = (SshMethod) objectMap.get("connectMethod");
            SshConnect sshConnect = (SshConnect) objectMap.get("sshConnect");
            returnString = connectMethod.sendCommand(hostIp,sshConnect,"\r",null);
        }else if (way.equalsIgnoreCase("telnet")){
            TelnetSwitchMethod telnetSwitchMethod = (TelnetSwitchMethod) objectMap.get("telnetSwitchMethod");
            TelnetComponent telnetComponent = (TelnetComponent) objectMap.get("telnetComponent");
            returnString = telnetSwitchMethod.sendCommand(hostIp, telnetComponent, "\r", null);
        }
        if (returnString==null){
            return "交换机连接失败";
        }
        String trim = returnString.trim();
        if (trim.substring(trim.length()-1,trim.length()).equals(">")){
            if (way.equalsIgnoreCase("ssh")){
                SshMethod connectMethod = (SshMethod) objectMap.get("connectMethod");
                SshConnect sshConnect = (SshConnect) objectMap.get("sshConnect");
                returnString = connectMethod.sendCommand(hostIp,sshConnect,"enable",null);
            }else if (way.equalsIgnoreCase("telnet")){
                TelnetSwitchMethod telnetSwitchMethod = (TelnetSwitchMethod) objectMap.get("telnetSwitchMethod");
                TelnetComponent telnetComponent = (TelnetComponent) objectMap.get("telnetComponent");
                returnString = telnetSwitchMethod.sendCommand(hostIp, telnetComponent, "enable", null);
            }

            if (returnString == null){
                return "交换机连接失败";
            }else {
                returnString = returnString.trim();
                String substring = returnString.substring(returnString.length() - 1, returnString.length());
                if (returnString.indexOf("command")!=-1 && returnString.indexOf("%")!=-1 ){
                    return "交换机连接成功";
                }else if (substring.equalsIgnoreCase("#")){
                    return "交换机连接成功";
                }else if (returnString.indexOf(":")!=-1){
                    if (way.equalsIgnoreCase("ssh")){
                        SshMethod connectMethod = (SshMethod) objectMap.get("connectMethod");
                        SshConnect sshConnect = (SshConnect) objectMap.get("sshConnect");
                        returnString = connectMethod.sendCommand(hostIp,sshConnect,(String) objectMap.get("configureCiphers"),null);
                    }else if (way.equalsIgnoreCase("telnet")){
                        TelnetSwitchMethod telnetSwitchMethod = (TelnetSwitchMethod) objectMap.get("telnetSwitchMethod");
                        TelnetComponent telnetComponent = (TelnetComponent) objectMap.get("telnetComponent");
                        returnString = telnetSwitchMethod.sendCommand(hostIp, telnetComponent, (String) objectMap.get("configureCiphers"), null);
                    }
                    return "交换机连接成功";
                }
            }
        }

        return "交换机连接失败";
    }


    /**
     * @method: 通用获取交换机基本信息  多个命令依次执行 按，分割
     * @Param: [user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法,
     * user_Object ：
     * SshConnect ssh连接工具，connectMethod ssh连接,
     * TelnetComponent Telnet连接工具，telnetSwitchMethod telnet连接]
     * @E-mail: WeiYaNing97@163.com
     * 通过null 查询初所有会的交换机基本信息的命令字符串集合
     * 遍历交换机基本信息的命令字符串集合 通过 扫描分析方法 获得所有提取信息
     * 通过返回提取的信息，给基本属性赋值
     * 成功则返回基本信息 否则 遍历下一条 交换机基本信息的命令字符串集合信息
     */
    public static AjaxResult getBasicInformationCurrency(Map<String,String> user_String,Map<String,Object> user_Object) {
        //四个参数 赋值
        SshConnect sshConnect = (SshConnect) user_Object.get("sshConnect");
        SshMethod connectMethod = (SshMethod) user_Object.get("connectMethod");
        TelnetComponent telnetComponent = (TelnetComponent) user_Object.get("telnetComponent");
        TelnetSwitchMethod telnetSwitchMethod = (TelnetSwitchMethod) user_Object.get("telnetSwitchMethod");

        //获取登录系统用户信息
        LoginUser loginUser = (LoginUser)user_Object.get("loginUser");
        String userName = loginUser.getUsername();

        //basicInformation : display device manuinfo,display ver
        //连接方式 ssh telnet
        String way = user_String.get("mode");
        //目前获取基本信息命令是多个命令是由,号分割的，
        // 所以需要根据, 来分割。例如：display device manuinfo,display ver

        String[] commandsplit = {"dis ver"};

        String commandString =""; //预设交换机返回结果
        String return_sum = ""; //当前命令字符串总和 返回命令总和("\r\n"分隔)

        //遍历数据表命令 分割得到的 命令数组
        for (String command:commandsplit){

            //创建 存储交换机返回数据 实体类
            ReturnRecord returnRecord = new ReturnRecord();
            int insert_Int = 0; //交换机返回结果插入数据库ID
            returnRecord.setUserName(userName);
            returnRecord.setSwitchIp(user_String.get("ip"));
            returnRecord.setBrand(user_String.get("deviceBrand"));
            returnRecord.setType(user_String.get("deviceModel"));
            returnRecord.setFirewareVersion(user_String.get("firmwareVersion"));
            returnRecord.setSubVersion(user_String.get("subversionNumber"));
            // 执行命令赋值
            String commandtrim = command.trim();
            returnRecord.setCurrentCommLog(commandtrim);
            //根据 连接方法 判断 实际连接方式
            //并发送命令 接受返回结果
            boolean deviceBrand = true;
            do {
                deviceBrand = true;
                if (way.equalsIgnoreCase("ssh")){
                    //  WebSocket 传输 命令
                    WebSocketService.sendMessage(userName,"发送和接收:"+command+"\r\n");
                    try {
                        PathHelper.writeDataToFile("发送:"+command+"\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    commandString = connectMethod.sendCommand(user_String.get("ip"),sshConnect,command,user_String.get("notFinished"));
                }else if (way.equalsIgnoreCase("telnet")){
                    //  WebSocket 传输 命令
                    WebSocketService.sendMessage(userName,"发送和接收:"+command);
                    try {
                        PathHelper.writeDataToFile("发送:"+command+"\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    commandString = telnetSwitchMethod.sendCommand(user_String.get("ip"),telnetComponent,command,user_String.get("notFinished"));
                }
                //  WebSocket 传输 交换机返回结果
                returnRecord.setCurrentReturnLog(commandString);
                //粗略查看是否存在 故障
                // 存在故障返回 false 不存在故障返回 true
                boolean switchfailure = MyUtils.switchfailure(user_String, commandString);

                // 存在故障返回 false
                if (!switchfailure){
                    // 交换机返回结果 按行 分割成 交换机返回信息数组
                    String[] commandStringSplit = commandString.split("\r\n");
                    // 遍历交换机返回信息数组
                    for (String returnString:commandStringSplit){
                        // 查看是否存在 故障
                        // 存在故障返回 false 不存在故障返回 true
                        deviceBrand = MyUtils.switchfailure(user_String, returnString);
                        // 存在故障返回 false
                        if (!deviceBrand){

                            System.err.println("\r\n"+user_String.get("ip") + "\r\n故障:"+returnString+"\r\n");

                            WebSocketService.sendMessage(userName,"故障:"+user_String.get("ip") + ":"+returnString+"\r\n");

                            try {
                                PathHelper.writeDataToFile("故障:"+user_String.get("ip") + ":"+returnString+"\r\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            returnRecord.setCurrentIdentifier(user_String.get("ip") + "出现故障:"+returnString);

                            if (way.equalsIgnoreCase("ssh")){
                                connectMethod.sendCommand(user_String.get("ip"),sshConnect," ",user_String.get("notFinished"));
                            }else if (way.equalsIgnoreCase("telnet")){
                                telnetSwitchMethod.sendCommand(user_String.get("ip"),telnetComponent," ",user_String.get("notFinished"));
                            }
                        }
                    }
                }

                returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
                insert_Int = returnRecordService.insertReturnRecord(returnRecord);

                if (insert_Int <= 0){
                    //传输登陆人姓名 及问题简述
                    WebSocketService.sendMessage(loginUser.getUsername(),"错误："+"交换机返回信息插入失败\r\n");
                    try {
                        //插入问题简述及问题路径
                        PathHelper.writeDataToFile("错误："+"交换机返回信息插入失败\r\n"
                                +"方法com.sgcc.web.controller.sql.SwitchInteraction.getBasicInformationList");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }while (!deviceBrand);

            returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
            returnRecord = returnRecordService.selectReturnRecordById(Integer.valueOf(insert_Int).longValue());

            //去除其他 交换机登录信息
            commandString = MyUtils.removeLoginInformation(commandString);

            //交换机返回信息 修整字符串  去除多余 "\r\n" 连续空格 为插入数据美观
            commandString = MyUtils.trimString(commandString);

            //交换机返回信息 按行分割为 字符串数组
            String[] commandString_split = commandString.split("\r\n");

            // 返回日志内容
            String current_return_log = "";
            if (commandString_split.length !=1 ){

                current_return_log = commandString.substring(0, commandString.length() - commandString_split[commandString_split.length - 1].length() - 2).trim();
                returnRecord.setCurrentReturnLog(current_return_log);

                //返回日志前后都有\r\n 可以改为 current_return_log.endsWith("\r\n");

                String current_return_log_substring_end = current_return_log.substring(current_return_log.length() - 2, current_return_log.length());
                if (!current_return_log_substring_end.equals("\r\n")){
                    current_return_log = current_return_log+"\r\n";
                }

                String current_return_log_substring_start = current_return_log.substring(0, 2);
                if (!current_return_log_substring_start.equals("\r\n")){
                    current_return_log = "\r\n"+current_return_log;
                }

            }

            WebSocketService.sendMessage(userName,"发送和接收:"+current_return_log+"\r\n");

            try {
                PathHelper.writeDataToFile("接收:"+current_return_log+"\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            //当前标识符 如：<H3C> [H3C]
            String current_identifier = commandString_split[commandString_split.length - 1].trim();
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

            WebSocketService.sendMessage(userName,"发送和接收:"+current_identifier+"\r\n");
            try {
                PathHelper.writeDataToFile("接收:"+current_identifier+"\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            //存储交换机返回数据 插入数据库
            returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
            int update = returnRecordService.updateReturnRecord(returnRecord);

            //判断命令是否错误 错误为false 正确为true
            if (!MyUtils.judgmentError( user_String,commandString)){
                //如果返回信息错误 则结束当前命令，执行 遍历数据库下一条命令字符串(,)

                String[] returnString_split = commandString.split("\r\n");
                for (String string_split:returnString_split){
                    if (!MyUtils.judgmentError( user_String,string_split)){

                        System.err.println("\r\n"+user_String.get("ip")+ ":" +command+ "错误:"+string_split+"\r\n");
                        WebSocketService.sendMessage(userName,"风险:"+user_String.get("ip")+ ":" +command+ ":"+string_split+"\r\n");
                        try {
                            PathHelper.writeDataToFile("风险:"+user_String.get("ip")+ ":" +command+ ":"+string_split+"\r\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                }

                break;
            }

            //当前命令字符串 返回命令总和("\r\n"分隔)
            return_sum +=  commandtrim +"\r\n"+ commandString+"\r\n";
        }


        HashMap<String, String> hashMap = analyzeStringToGetBasicInformation(return_sum);

        if (hashMap.get("pinpai")!=null
                && hashMap.get("xinghao")!=null
                && hashMap.get("banben")!=null
                && hashMap.get("zibanben")!=null){

            hashMap.put("pinpai",removeSpecialSymbols(hashMap.get("pinpai")));
            hashMap.put("xinghao",removeSpecialSymbols(hashMap.get("xinghao")));
            hashMap.put("banben",removeSpecialSymbols(hashMap.get("banben")));
            hashMap.put("zibanben",removeSpecialSymbols(hashMap.get("zibanben")));


            //设备型号
            user_String.put("deviceModel",hashMap.get("xinghao"));
            //设备品牌
            user_String.put("deviceBrand",hashMap.get("pinpai"));
            //内部固件版本
            user_String.put("firmwareVersion",hashMap.get("banben"));
            //子版本号
            user_String.put("subversionNumber",hashMap.get("zibanben"));


            return AjaxResult.success(hashMap);
        }

        return AjaxResult.error("未定义该交换机获取基本信息命令及分析");

    }


    /**
     * 是否是以 固定字符 开头和结尾 是 则去除  （获取交换机基本信息用到）
     * @param args
     * @return
     */
    public static String removeSpecialSymbols(String args) {
        String[] specialSymbols = {".",","};
        for (String symbol:specialSymbols){
            boolean startWith = StrUtil.startWith(args, symbol);
            boolean endWith = StrUtil.endWith(args, symbol);
            if (startWith){
                args = args.substring(1,args.length());
            }
            if (endWith){
                args = args.substring(0,args.length()-1);
            }
        }
        return args;
    }


    /**
     * 通过 配置文件 获取取交换机基本信息规则
     * @param returns
     * @return  返回  交换机基本信息
     */
    public static HashMap<String,String> analyzeStringToGetBasicInformation(String returns) {

        returns = returns.replace("\n","\r\n");
        String returns_String = MyUtils.trimString(returns);


        String equipmentBrand = Configuration.equipmentBrand;
        String equipmentModel = Configuration.equipmentModel;

        String[] equipmentBrandsplit = equipmentBrand.split(";");
        String[] equipmentModelsplit = equipmentModel.split(";");

        String brand = "";
        String model = "";
        String firmwareVersion = "";
        String subversionNo = "";
        HashMap<String,String> map = new HashMap<>();
        map.put("pinpai",null);
        map.put("xinghao",null);
        map.put("banben",null);
        map.put("zibanben",null);


        for (String brandString:equipmentBrandsplit){
            if (returns.indexOf(" "+ brandString +" ") != -1){
                brand = brandString;
            }
        }

        String[] return_word = returns_String.trim().split(" ");
        if (!(brand.equals(""))){
            for (int number = 0 ; number < return_word.length; number++){
                if (return_word[number].equals(brand)){

                    number = number +1;
                    String brand_after = return_word[number];
                    System.err.println(brand+"后面是："+brand_after);

                    for (String modelString:equipmentModelsplit){
                        /*不以 * 开头*/
                        if (!(modelString.substring(0,1).equals("*"))){
                            modelString = modelString.replace("*", "");
                            boolean b = StrUtil.startWith(brand_after, modelString);
                            if (b){
                                model = brand_after;
                                break;
                            }
                        }
                        /*不以 * 结束*/
                        if (!(modelString.substring(modelString.length()-1,modelString.length()).equals("*"))){

                            modelString = modelString.replace("*", "");
                            boolean b = StrUtil.endWith(brand_after, modelString);
                            if (b){
                                model = brand_after;
                                break;
                            }
                        }
                        /*前后都有 *  */
                        if (!(modelString.substring(0,1).equals("*"))
                            &&
                                !(modelString.substring(modelString.length()-1,modelString.length()).equals("*"))){

                            modelString = modelString.replace("*", "");
                            boolean b = brand_after.indexOf(modelString)!=-1;
                            if (b){
                                model = brand_after;
                                break;
                            }
                        }
                    }

                    if (!(model.equals(""))){
                        break;
                    }

                }
            }
        }
        for (int number = 0 ; number < return_word.length; number++){
            if (return_word[number].equals("Version")){
                firmwareVersion = return_word[number+1];
            }
            if (return_word[number].equals("Release")){
                subversionNo = return_word[number+1];
            }
            if (!(firmwareVersion.equals("")) && !(subversionNo.equals(""))){
                break;
            }
        }
        System.err.println("品牌"+brand+"型号"+model+"版本"+firmwareVersion+"子版本"+subversionNo);

        map.put("pinpai",!(brand.equals(""))?brand:null);
        map.put("xinghao",!(model.equals(""))?model:null);
        map.put("banben",!(firmwareVersion.equals(""))?firmwareVersion:null);
        map.put("zibanben",!(subversionNo.equals(""))?subversionNo:null);

        return map;
    }


    /**
     * @method: 测试获取交换机基本信息  多个命令依次执行 按，分割
     * @Param: [user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法,
     * user_Object ：
     * SshConnect ssh连接工具，connectMethod ssh连接,
     * TelnetComponent Telnet连接工具，telnetSwitchMethod telnet连接]
     * @E-mail: WeiYaNing97@163.com
     * 通过null 查询初所有会的交换机基本信息的命令字符串集合
     * 遍历交换机基本信息的命令字符串集合 通过 扫描分析方法 获得所有提取信息
     * 通过返回提取的信息，给基本属性赋值
     * 成功则返回基本信息 否则 遍历下一条 交换机基本信息的命令字符串集合信息
     */
    public static AjaxResult getBasicInformationTest(Map<String,String> user_String,Map<String,Object> user_Object,String[] commands ,List<ProblemScanLogic> problemScanLogicList) {
        //四个参数 赋值
        SshConnect sshConnect = (SshConnect) user_Object.get("sshConnect");
        SshMethod connectMethod = (SshMethod) user_Object.get("connectMethod");
        TelnetComponent telnetComponent = (TelnetComponent) user_Object.get("telnetComponent");
        TelnetSwitchMethod telnetSwitchMethod = (TelnetSwitchMethod) user_Object.get("telnetSwitchMethod");

        //获取登录系统用户信息
        LoginUser loginUser = (LoginUser)user_Object.get("loginUser");
        String userName = loginUser.getUsername();

        if (commands.length == 0){
            return AjaxResult.error("未定义该交换机获取基本信息命令及分析");
        }

        //basicInformation : display device manuinfo,display ver
        //连接方式 ssh telnet
        String way = user_String.get("mode");
        //目前获取基本信息命令是多个命令是由,号分割的，
        // 所以需要根据, 来分割。例如：display device manuinfo,display ver

        String[] commandsplit = commands;

        String commandString =""; //预设交换机返回结果
        String return_sum = ""; //当前命令字符串总和 返回命令总和("\r\n"分隔)

        //遍历数据表命令 分割得到的 命令数组
        for (String command:commandsplit){

            //创建 存储交换机返回数据 实体类
            ReturnRecord returnRecord = new ReturnRecord();
            int insert_Int = 0; //交换机返回结果插入数据库ID
            returnRecord.setUserName(userName);
            returnRecord.setSwitchIp(user_String.get("ip"));
            returnRecord.setBrand(user_String.get("deviceBrand"));
            returnRecord.setType(user_String.get("deviceModel"));
            returnRecord.setFirewareVersion(user_String.get("firmwareVersion"));
            returnRecord.setSubVersion(user_String.get("subversionNumber"));
            // 执行命令赋值
            String commandtrim = command.trim();
            returnRecord.setCurrentCommLog(commandtrim);
            //根据 连接方法 判断 实际连接方式
            //并发送命令 接受返回结果
            boolean deviceBrand = true;
            do {
                deviceBrand = true;
                if (way.equalsIgnoreCase("ssh")){
                    //  WebSocket 传输 命令
                    WebSocketService.sendMessage(userName,"发送:"+command+"\r\n");
                    try {
                        PathHelper.writeDataToFile("发送:"+command+"\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    commandString = connectMethod.sendCommand(user_String.get("ip"),sshConnect,command,user_String.get("notFinished"));
                }else if (way.equalsIgnoreCase("telnet")){
                    //  WebSocket 传输 命令
                    WebSocketService.sendMessage(userName,"发送:"+command);
                    try {
                        PathHelper.writeDataToFile("发送:"+command+"\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    commandString = telnetSwitchMethod.sendCommand(user_String.get("ip"),telnetComponent,command,user_String.get("notFinished"));
                }
                //  WebSocket 传输 交换机返回结果
                returnRecord.setCurrentReturnLog(commandString);
                //粗略查看是否存在 故障
                // 存在故障返回 false 不存在故障返回 true
                boolean switchfailure = MyUtils.switchfailure(user_String, commandString);

                // 存在故障返回 false
                if (!switchfailure){
                    // 交换机返回结果 按行 分割成 交换机返回信息数组
                    String[] commandStringSplit = commandString.split("\r\n");
                    // 遍历交换机返回信息数组
                    for (String returnString:commandStringSplit){
                        // 查看是否存在 故障
                        // 存在故障返回 false 不存在故障返回 true
                        deviceBrand = MyUtils.switchfailure(user_String, returnString);
                        // 存在故障返回 false
                        if (!deviceBrand){

                            System.err.println("\r\n"+user_String.get("ip") + "\r\n故障:"+returnString+"\r\n");

                            WebSocketService.sendMessage(userName,"故障:"+user_String.get("ip") + ":"+returnString+"\r\n");

                            try {
                                PathHelper.writeDataToFile("故障:"+user_String.get("ip") + ":"+returnString+"\r\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            returnRecord.setCurrentIdentifier(user_String.get("ip") + "出现故障:"+returnString);

                            if (way.equalsIgnoreCase("ssh")){
                                connectMethod.sendCommand(user_String.get("ip"),sshConnect," ",user_String.get("notFinished"));
                            }else if (way.equalsIgnoreCase("telnet")){
                                telnetSwitchMethod.sendCommand(user_String.get("ip"),telnetComponent," ",user_String.get("notFinished"));
                            }
                        }
                    }
                }

                returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
                insert_Int = returnRecordService.insertReturnRecord(returnRecord);

                if (insert_Int <= 0){
                    //传输登陆人姓名 及问题简述
                    WebSocketService.sendMessage(loginUser.getUsername(),"错误："+"交换机返回信息插入失败\r\n");
                    try {
                        //插入问题简述及问题路径
                        PathHelper.writeDataToFile("错误："+"交换机返回信息插入失败\r\n"
                                +"方法com.sgcc.web.controller.sql.SwitchInteraction.getBasicInformationList");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }while (!deviceBrand);

            returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
            returnRecord = returnRecordService.selectReturnRecordById(Integer.valueOf(insert_Int).longValue());

            //去除其他 交换机登录信息
            commandString = MyUtils.removeLoginInformation(commandString);

            //交换机返回信息 修整字符串  去除多余 "\r\n" 连续空格 为插入数据美观
            commandString = MyUtils.trimString(commandString);

            //交换机返回信息 按行分割为 字符串数组
            String[] commandString_split = commandString.split("\r\n");

            // 返回日志内容
            String current_return_log = "";
            if (commandString_split.length !=1 ){

                current_return_log = commandString.substring(0, commandString.length() - commandString_split[commandString_split.length - 1].length() - 2).trim();
                returnRecord.setCurrentReturnLog(current_return_log);

                //返回日志前后都有\r\n 可以改为 current_return_log.endsWith("\r\n");

                String current_return_log_substring_end = current_return_log.substring(current_return_log.length() - 2, current_return_log.length());
                if (!current_return_log_substring_end.equals("\r\n")){
                    current_return_log = current_return_log+"\r\n";
                }

                String current_return_log_substring_start = current_return_log.substring(0, 2);
                if (!current_return_log_substring_start.equals("\r\n")){
                    current_return_log = "\r\n"+current_return_log;
                }

            }

            WebSocketService.sendMessage(userName,"接收:"+current_return_log+"\r\n");

            try {
                PathHelper.writeDataToFile("接收:"+current_return_log+"\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            //当前标识符 如：<H3C> [H3C]
            String current_identifier = commandString_split[commandString_split.length - 1].trim();
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

            WebSocketService.sendMessage(userName,"接收:"+current_identifier+"\r\n");
            try {
                PathHelper.writeDataToFile("接收:"+current_identifier+"\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            //存储交换机返回数据 插入数据库
            returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
            int update = returnRecordService.updateReturnRecord(returnRecord);

            //判断命令是否错误 错误为false 正确为true
            if (!MyUtils.judgmentError( user_String,commandString)){
                //如果返回信息错误 则结束当前命令，执行 遍历数据库下一条命令字符串(,)

                String[] returnString_split = commandString.split("\r\n");
                for (String string_split:returnString_split){
                    if (!MyUtils.judgmentError( user_String,string_split)){

                        System.err.println("\r\n"+user_String.get("ip")+ ":" +command+ "错误:"+string_split+"\r\n");
                        WebSocketService.sendMessage(userName,"风险:"+user_String.get("ip")+ ":" +command+ ":"+string_split+"\r\n");
                        try {
                            PathHelper.writeDataToFile("风险:"+user_String.get("ip")+ ":" +command+ ":"+string_split+"\r\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                }

                break;
            }

            //当前命令字符串 返回命令总和("\r\n"分隔)
            if(way.equalsIgnoreCase("ssh")){
                return_sum +=  commandtrim +"\r\n"+ commandString+"\r\n";
            }else {
                // telnet 自带
                return_sum +=  commandString+"\r\n";
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
        String extractInformation_string1 = analysisReturn(user_String, user_Object ,null,
                return_sum, first_problem_scanLogic_Id,problemScanLogicList);

        //设备型号
        String deviceModel= "";
        //设备品牌
        String deviceBrand = "";
        //内部固件版本
        String firmwareVersion = "";
        //子版本号
        String subversionNumber = "";
        //extractInformation_string1 = extractInformation_string1.replace(",","");
        String[] return_result_split = extractInformation_string1.split("=:=");
        for (int num = 0;num<return_result_split.length;num++){
            //设备型号
            if (return_result_split[num].equals("设备型号")){
                num = num+2;
                deviceModel=return_result_split[num];
            }
            //设备品牌
            if (return_result_split[num].equals("设备品牌")) {
                num = num+2;
                deviceBrand = return_result_split[num];
            }
            //内部固件版本
            if (return_result_split[num].equals("内部固件版本")) {
                num = num+2;
                firmwareVersion = return_result_split[num];
            }
            //子版本号
            if (return_result_split[num].equals("子版本号")) {
                num = num+2;
                subversionNumber = return_result_split[num];
            }

                // 根据交换机信息查询 获取 扫描问题的 命令ID
                    /*List<String> stringList = new ArrayList<>();
                    stringList.add(deviceBrand);
                    stringList.add(deviceModel);
                    stringList.add(firmwareVersion);
                    stringList.add(subversionNumber);
                    WebSocketService.sendMessage("basicinformation"+userName,stringList);*/
                HashMap<String,String> map = new HashMap<>();
                map.put("pinpai",deviceBrand);
                map.put("xinghao",deviceModel);
                map.put("banben",firmwareVersion);
                map.put("zibanben",subversionNumber);
                //设备型号
                user_String.put("deviceModel",deviceModel);
                //设备品牌
                user_String.put("deviceBrand",deviceBrand);
                //内部固件版本
                user_String.put("firmwareVersion",firmwareVersion);
                //子版本号
                user_String.put("subversionNumber",subversionNumber);

                WebSocketService.sendMessage(userName,"系统信息:"+user_String.get("ip") +"基本信息："+ "设备品牌："+deviceBrand+ "设备型号："+deviceModel+ "内部固件版本："+firmwareVersion+ "子版本号："+subversionNumber+"\r\n");

                try {
                    PathHelper.writeDataToFile("系统信息:"+user_String.get("ip") +"基本信息："+ "设备品牌："+deviceBrand+ "设备型号："+deviceModel+ "内部固件版本："+firmwareVersion+ "子版本号："+subversionNumber+"\r\n");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return AjaxResult.success(map);
        }
        WebSocketService.sendMessage(userName,"系统信息:"+user_String.get("ip") +"基本信息："+ "设备品牌："+deviceBrand+ "设备型号："+deviceModel+ "内部固件版本："+firmwareVersion+ "子版本号："+subversionNumber+"\r\n");

        try {
            PathHelper.writeDataToFile("系统信息:"+user_String.get("ip") +"基本信息："+ "设备品牌："+deviceBrand+ "设备型号："+deviceModel+ "内部固件版本："+firmwareVersion+ "子版本号："+subversionNumber+"\r\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return AjaxResult.error("未定义该交换机获取基本信息命令及分析");

    }



    /**
     * @method: 获取交换机基本信息  多个命令依次执行 按，分割
     * @Param: [user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法,
     * user_Object ：
     * SshConnect ssh连接工具，connectMethod ssh连接,
     * TelnetComponent Telnet连接工具，telnetSwitchMethod telnet连接]
     * @E-mail: WeiYaNing97@163.com
     * 通过null 查询初所有会的交换机基本信息的命令字符串集合
     * 遍历交换机基本信息的命令字符串集合 通过 扫描分析方法 获得所有提取信息
     * 通过返回提取的信息，给基本属性赋值
     * 成功则返回基本信息 否则 遍历下一条 交换机基本信息的命令字符串集合信息
     */
    @GetMapping("/getBasicInformationList")
   // @MyLog(title = "获取交换机基本信息", businessType = BusinessType.OTHER)
    public static AjaxResult getBasicInformationList(Map<String,String> user_String,Map<String,Object> user_Object) {
        //四个参数 赋值
        SshConnect sshConnect = (SshConnect) user_Object.get("sshConnect");
        SshMethod connectMethod = (SshMethod) user_Object.get("connectMethod");
        TelnetComponent telnetComponent = (TelnetComponent) user_Object.get("telnetComponent");
        TelnetSwitchMethod telnetSwitchMethod = (TelnetSwitchMethod) user_Object.get("telnetSwitchMethod");
        //获取登录系统用户信息
        LoginUser loginUser = (LoginUser)user_Object.get("loginUser");
        String userName = loginUser.getUsername();

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
            String way = user_String.get("mode");
            //目前获取基本信息命令是多个命令是由,号分割的，
            // 所以需要根据, 来分割。例如：display device manuinfo,display ver
            String[] removecustom = basicInformation.getCommand().split("\\[");
            String[] commandsplit = removecustom[0].split("=:=");

            String commandString =""; //预设交换机返回结果
            String return_sum = ""; //当前命令字符串总和 返回命令总和("\r\n"分隔)

            boolean loop = false;

            //遍历数据表命令 分割得到的 命令数组
            for (String command:commandsplit){

                //创建 存储交换机返回数据 实体类
                ReturnRecord returnRecord = new ReturnRecord();
                int insert_Int = 0;
                // 系统登录人名字
                // ip
                // 品牌
                // 型号
                // 固件版本
                // 子版本
                returnRecord.setUserName(userName);
                returnRecord.setSwitchIp(user_String.get("ip"));
                returnRecord.setBrand(user_String.get("deviceBrand"));
                returnRecord.setType(user_String.get("deviceModel"));
                returnRecord.setFirewareVersion(user_String.get("firmwareVersion"));
                returnRecord.setSubVersion(user_String.get("subversionNumber"));

                // 执行命令赋值
                String commandtrim = command.trim();
                returnRecord.setCurrentCommLog(commandtrim);

                //根据 连接方法 判断 实际连接方式
                //并发送命令 接受返回结果
                boolean deviceBrand = true;
                do {
                    deviceBrand = true;
                    if (way.equalsIgnoreCase("ssh")){
                        //  WebSocket 传输 命令
                        WebSocketService.sendMessage(userName,"发送:"+command+"\r\n");
                        try {
                            PathHelper.writeDataToFile("发送:"+command+"\r\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        commandString = connectMethod.sendCommand(user_String.get("ip"),sshConnect,command,user_String.get("notFinished"));
                    }else if (way.equalsIgnoreCase("telnet")){
                        //  WebSocket 传输 命令
                        WebSocketService.sendMessage(userName,"发送:"+command);
                        try {
                            PathHelper.writeDataToFile("发送:"+command+"\r\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        commandString = telnetSwitchMethod.sendCommand(user_String.get("ip"),telnetComponent,command,user_String.get("notFinished"));
                    }
                    //  WebSocket 传输 交换机返回结果
                    returnRecord.setCurrentReturnLog(commandString);

                    //粗略查看是否存在 故障
                    // 存在故障返回 false 不存在故障返回 true
                    boolean switchfailure = MyUtils.switchfailure(user_String, commandString);
                    // 存在故障返回 false
                    if (!switchfailure){
                        // 交换机返回结果 按行 分割成 交换机返回信息数组
                        String[] commandStringSplit = commandString.split("\r\n");
                        // 遍历交换机返回信息数组
                        for (String returnString:commandStringSplit){
                            // 查看是否存在 故障
                            // 存在故障返回 false 不存在故障返回 true
                            deviceBrand = MyUtils.switchfailure(user_String, returnString);
                            // 存在故障返回 false
                            if (!deviceBrand){
                                loop = true;
                                System.err.println("\r\n"+user_String.get("ip") + "\r\n故障:"+returnString+"\r\n");

                                WebSocketService.sendMessage(userName,"故障:"+user_String.get("ip") + ":"+returnString+"\r\n");

                                try {
                                    PathHelper.writeDataToFile("故障:"+user_String.get("ip") + ":"+returnString+"\r\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                returnRecord.setCurrentIdentifier(user_String.get("ip") + "出现故障:"+returnString);

                                if (way.equalsIgnoreCase("ssh")){
                                    connectMethod.sendCommand(user_String.get("ip"),sshConnect," ",user_String.get("notFinished"));
                                }else if (way.equalsIgnoreCase("telnet")){
                                    telnetSwitchMethod.sendCommand(user_String.get("ip"),telnetComponent," ",user_String.get("notFinished"));
                                }
                            }
                        }
                    }

                    returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
                    insert_Int = returnRecordService.insertReturnRecord(returnRecord);

                    if (insert_Int <= 0){
                        //传输登陆人姓名 及问题简述
                        WebSocketService.sendMessage(loginUser.getUsername(),"错误："+"交换机返回信息插入失败\r\n");
                        try {
                            //插入问题简述及问题路径
                            PathHelper.writeDataToFile("错误："+"交换机返回信息插入失败\r\n"
                                    +"方法com.sgcc.web.controller.sql.SwitchInteraction.getBasicInformationList");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }while (!deviceBrand);


                returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
                returnRecord = returnRecordService.selectReturnRecordById(Integer.valueOf(insert_Int).longValue());

                //去除其他 交换机登录信息
                commandString = MyUtils.removeLoginInformation(commandString);

                //交换机返回信息 修整字符串  去除多余 "\r\n" 连续空格 为插入数据美观
                commandString = MyUtils.trimString(commandString);

                //交换机返回信息 按行分割为 字符串数组
                String[] commandString_split = commandString.split("\r\n");

                // 返回日志内容
                String current_return_log = "";
                if (commandString_split.length !=1 ){
                    /*通过截取 去除 标识符 和 标识符与返回日志中间的 \r\n*/
                    current_return_log = commandString.substring(0, commandString.length() - commandString_split[commandString_split.length - 1].length() - 2).trim();
                    returnRecord.setCurrentReturnLog(current_return_log);

                    //返回日志前后都有\r\n 可以改为 current_return_log.endsWith("\r\n");
                    // todo 测试一下endsWith() startsWith()方法
                    //返回日志前后都有\r\n 可以改为 current_return_log.endsWith("\r\n");
                    //endsWith() 方法用于测试字符串是否以指定的后缀结束。
                    //startsWith() 前缀

                    String current_return_log_substring_end = current_return_log.substring(current_return_log.length() - 2, current_return_log.length());
                    if (!current_return_log_substring_end.equals("\r\n")){
                        current_return_log = current_return_log+"\r\n";
                    }
                    String current_return_log_substring_start = current_return_log.substring(0, 2);
                    if (!current_return_log_substring_start.equals("\r\n")){
                        current_return_log = "\r\n"+current_return_log;
                    }

                }

                WebSocketService.sendMessage(userName,"接收:"+current_return_log+"\r\n");

                try {
                    PathHelper.writeDataToFile("接收:"+current_return_log+"\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //当前标识符 如：<H3C> [H3C]
                String current_identifier = commandString_split[commandString_split.length - 1].trim();
                returnRecord.setCurrentIdentifier(current_identifier);
                //当前标识符前后都没有\r\n

                // todo 测试一下endsWith() startsWith()方法
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

                WebSocketService.sendMessage(userName,"接收:"+current_identifier+"\r\n");
                try {
                    PathHelper.writeDataToFile("接收:"+current_identifier+"\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //存储交换机返回数据 插入数据库
                returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
                int update = returnRecordService.updateReturnRecord(returnRecord);

                //判断命令是否错误 错误为false 正确为true
                if (!MyUtils.judgmentError( user_String,commandString)){
                    //如果返回信息错误 则结束当前命令，执行 遍历数据库下一条命令字符串(,)

                    String[] returnString_split = commandString.split("\r\n");
                    for (String string_split:returnString_split){
                        if (!MyUtils.judgmentError( user_String,string_split)){
                            loop = true;
                            System.err.println("\r\n"+user_String.get("ip")+ ":" +command+ "错误:"+string_split+"\r\n");
                            WebSocketService.sendMessage(userName,"风险:"+user_String.get("ip")+ ":" +command+ ":"+string_split+"\r\n");
                            try {
                                PathHelper.writeDataToFile("风险:"+user_String.get("ip")+ ":" +command+ ":"+string_split+"\r\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            break;
                        }
                    }

                    break;
                }

                //当前命令字符串 返回命令总和("\r\n"分隔)
                return_sum += commandtrim + "\r\n" +commandString+"\r\n\r\n";
            }
            //修整 当前命令字符串 返回信息  去除多余 "\r\n" 连续空格
            //应该可以去除 因为 上面 每个单独命令已经执行过
            // 注释掉 可能会在两条交换机返回信息中 存在 "\r\n\r\n" 情况 按"\r\n"分割可能会出现空白元素
            //String command_String = Utils.trimString(return_sum);

            if (loop){
                continue;
            }

            //分析第一条ID basicInformation.getProblemId() (为 问题扫描逻辑表  ID)
            String first_problem_scanLogic_Id = basicInformation.getProblemId();

            // 根据交换机返回信息、分析ID 分析逻辑集合获取交换机基本信息
            //进行分析 返回总提取信息
            String extractInformation_string1 = analysisReturn(user_String, user_Object ,null,
                    return_sum, first_problem_scanLogic_Id,null);

            if (extractInformation_string1.indexOf("错误") !=-1){
                continue;
            }

            if (extractInformation_string1.equals("") || extractInformation_string1 == null){
                continue;
            }


            //设备型号
            String deviceModel= "";
            //设备品牌
            String deviceBrand = "";
            //内部固件版本
            String firmwareVersion = "";
            //子版本号
            String subversionNumber = "";
            //extractInformation_string1 = extractInformation_string1.replace(",","");
            String[] return_result_split = extractInformation_string1.split("=:=");
            for (int num = 0;num<return_result_split.length;num++){
                //设备型号
                if (return_result_split[num].equals("设备型号")){
                    num = num+2;
                    deviceModel=return_result_split[num];
                }
                //设备品牌
                if (return_result_split[num].equals("设备品牌")) {
                    num = num+2;
                    deviceBrand = return_result_split[num];
                }
                //内部固件版本
                if (return_result_split[num].equals("内部固件版本")) {
                    num = num+2;
                    firmwareVersion = return_result_split[num];
                }
                //子版本号
                if (return_result_split[num].equals("子版本号")) {
                    num = num+2;
                    subversionNumber = return_result_split[num];
                }

                if (!deviceModel.equals("") && !deviceBrand.equals("") && !firmwareVersion.equals("") && !subversionNumber.equals("")){
                    // 根据交换机信息查询 获取 扫描问题的 命令ID
                    /*List<String> stringList = new ArrayList<>();
                    stringList.add(deviceBrand);
                    stringList.add(deviceModel);
                    stringList.add(firmwareVersion);
                    stringList.add(subversionNumber);
                    WebSocketService.sendMessage("basicinformation"+userName,stringList);*/

                    HashMap<String,String> map = new HashMap<>();
                    map.put("pinpai",deviceBrand);
                    map.put("xinghao",deviceModel);
                    map.put("banben",firmwareVersion);
                    map.put("zibanben",subversionNumber);

                    //设备型号
                    user_String.put("deviceModel",deviceModel);
                    //设备品牌
                    user_String.put("deviceBrand",deviceBrand);
                    //内部固件版本
                    user_String.put("firmwareVersion",firmwareVersion);
                    //子版本号
                    user_String.put("subversionNumber",subversionNumber);

                    WebSocketService.sendMessage(userName,"系统信息:"+user_String.get("ip") +"基本信息："+ "设备品牌："+deviceBrand+ "设备型号："+deviceModel+ "内部固件版本："+firmwareVersion+ "子版本号："+subversionNumber+"\r\n");

                    try {
                        PathHelper.writeDataToFile("系统信息:"+user_String.get("ip") +"基本信息："+ "设备品牌："+deviceBrand+ "设备型号："+deviceModel+ "内部固件版本："+firmwareVersion+ "子版本号："+subversionNumber+"\r\n");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return AjaxResult.success(map);
                }
            }
            WebSocketService.sendMessage(userName,"系统信息:"+user_String.get("ip") +"基本信息："+ "设备品牌："+deviceBrand+ "设备型号："+deviceModel+ "内部固件版本："+firmwareVersion+ "子版本号："+subversionNumber+"\r\n");

            try {
                PathHelper.writeDataToFile("系统信息:"+user_String.get("ip") +"基本信息："+ "设备品牌："+deviceBrand+ "设备型号："+deviceModel+ "内部固件版本："+firmwareVersion+ "子版本号："+subversionNumber+"\r\n");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return AjaxResult.error("未定义该交换机获取基本信息命令及分析");

    }

    /**
     * @method: 根据交换机返回信息、分析ID 分析逻辑集合获取交换机基本信息
     * @Param: [user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法, connectMethod ssh连接, telnetSwitchMethod telnet连接]
     * @Param: [resultString 交换机返回信息,first_problem_scanLogic_Id 第一条分析ID,firmwareVersion 内部固件版本号)
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public static String analysisReturn(Map<String,String> user_String,Map<String,Object> user_Object,TotalQuestionTable totalQuestionTable,
                                 String resultString,String first_problem_scanLogic_Id,List<ProblemScanLogic> problemScanLogicList){

        //整理返回结果 去除 #
        //测试后无用 暂注释掉   注释掉可能会出现的情况 按行分割后 出现某数组元素只有 # 的情况
        //resultString = resultString.replace("\r\n"+" # "+"\r\n","\r\n");

        //交换机返回结果 按行分割 交换机返回信息字符串
        String[] return_information_array =resultString.split("\r\n");
        //是否循环判断 loop循环 end 单次
        /* 传入参数[user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法, connectMethod ssh连接, telnetSwitchMethod telnet连接,
                交换机返回信息字符串, 单次分析提取数据，循环分析提取数据
                交换机返回信息字符串分析索引位置(光标)，第一条分析ID， 当前分析ID ，是否循环 ，内部固件版本号]
         */
        //设备型号=:=S3600-28P-EI=:=设备品牌=:=H3C=:=内部固件版本=:=3.10,=:=子版本号=:=1510P09=:=
        Integer numberOfCycles = Configuration.numberOfCycles.intValue();
        String strings = selectProblemScanLogicById(user_String, user_Object, totalQuestionTable,
                return_information_array, "", "",
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
        System.err.print("\r\n基本信息："+strings+"\r\n");
        return strings;
    }

    /**
     * 根据配置文件 去除标识符  （TM） ®
     * @param strings
     * @return
     */
    public static String removeIdentifier(String strings) {
        String[] identifiersplit = Configuration.identifier.split(";");
        for (String identifier:identifiersplit){
            strings =strings.replace(identifier,"");
        }
        return strings;
    }

    /**
     * @method: * 分析第一条ID 和 是否循环判断 返回是否有错
     * 根据分析ID获取问题扫描逻辑详细信息
     * @Param: [user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法, connectMethod ssh连接, telnetSwitchMethod telnet连接,
     *                 交换机返回信息字符串, 单次分析提取数据，循环分析提取数据
     *                 line_n:交换机返回信息字符串分析索引位置(光标)，firstID:第一条分析ID， currentID:当前分析ID ，
     *                 insertsInteger：插入问题数据次数、为了“完成”]
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     * 用户信息 连接方式 ssh、telnet
     * 返回信息按行分字符串集合  单次提取数据  循环总提取数据
     * 集合行数  第一分析ID 当前循环ID  是否循环 内部固件版本号
     */

    // 是否用首ID ifFirstID 分析首ID firstID   现行ID currentID 是否循环
    public static String selectProblemScanLogicById(Map<String,String> user_String,
                                             Map<String,Object> user_Object,
                                             TotalQuestionTable totalQuestionTable,
                                             String[] return_information_array, String current_Round_Extraction_String, String extractInformation_string,
                                             int line_n, String firstID ,List<ProblemScanLogic> problemScanLogicList, String currentID,
                                             Integer insertsInteger,
                                                    Integer loop,Integer numberOfCycles) {

        LoginUser loginUser = (LoginUser) user_Object.get("loginUser");

        /*判断当前分析ID(currentID)是否为空。如果为空则用第一条分析ID(firstID).
        如果当前分析ID(currentID)不为空，说明是第二次调用本方法，则使用当前分析ID(currentID)，
        都赋值给ID。*/
        //第一条分析ID
        String id = firstID; //用于第一次分析 和  循环分析
        //如果当前分析ID  currentID  不为空，则用当前分析ID
        if (currentID != null){
            id = currentID;
        }

        /*判断输入参数problemScanLogicList分析逻辑实体类集合是否为空，
        不为空则，分析逻辑数据通过problemScanLogicList来存储。
        如果为空，则需要查询数据库。都是通过ID来获取具体分析逻辑数据。*/
        //控制台输出 分析表 分析ID
        System.err.print("\r\n执行分析ID:\r\n"+id+"\r\n");
        ProblemScanLogic problemScanLogic = null;
        if (problemScanLogicList == null){
            //根据ID查询分析数据
            //根据第一条分析ID 查询分析信息
            problemScanLogicService = SpringBeanUtil.getBean(IProblemScanLogicService.class);
            problemScanLogic = problemScanLogicService.selectProblemScanLogicById(id);
        }else {
            for (ProblemScanLogic pojo:problemScanLogicList){
                if (pojo.getId().equals(id) || pojo.getId() == id){
                    problemScanLogic = pojo;
                }
            }
        }


        //如果循环ID不为空的话 说明 分析数据为循环分析 则 需要调出循环ID 当做 当前分析ID 继续执行
        //循环分析数据 不需要分析 功能指向循环位置
        if (problemScanLogic.getCycleStartId()!=null && !(problemScanLogic.getCycleStartId().equals("null"))){
            //比较循环次数和最大循环测试
            loop = loop +1;
            if (loop > numberOfCycles){
                WebSocketService.sendMessage(loginUser.getUsername(),"错误:"+user_String.get("ip")+ ":"+ "问题类型"+totalQuestionTable.getTypeProblem()+ "问题名称"+totalQuestionTable.getTemProName()+ "错误:"+"循环超时"+"\r\n");
                try {
                    PathHelper.writeDataToFile("错误:"+user_String.get("ip")+ ":"+ "问题类型"+totalQuestionTable.getTypeProblem()+ "问题名称"+totalQuestionTable.getTemProName()+ "错误:"+"循环超时"+"\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            /*需要调出循环ID 当做 当前分析ID 继续执行*/
            /* firstID = problemScanLogic.getCycleStartId();  更改：改为赋值当前分析ID 可以保留 首分析ID */
            currentID = problemScanLogic.getCycleStartId();
            String loop_string = selectProblemScanLogicById(user_String,user_Object,totalQuestionTable,
                    return_information_array,"",extractInformation_string,
                    line_n,firstID,problemScanLogicList,currentID,insertsInteger, loop, numberOfCycles);
            return loop_string;

        }

        // todo 打算修改的 ProblemId  问题
        //如果 问题索引字段 不为空 null 则 说明  分析数据 是 分析出问题或者可以结束了
        // problemScanLogic.getProblemId() 可以为 有问题(前端显示:异常) 无问题(前端显示:安全) 完成
        if (problemScanLogic.getProblemId()!=null){
            //有问题 无问题
            /*查看问题ID(ProblemId)字段。如果该字段不为空，则分析出问题了。
            1：如果ProblemId字段包含"问题" 或者 ProblemId字段不包含"问题"并且ProblemId字段不包含"完成"(自定义的问题名称)。*/
            if (problemScanLogic.getProblemId().indexOf("问题")!=-1
                    || (problemScanLogic.getProblemId().indexOf("问题") ==-1  && problemScanLogic.getProblemId().indexOf("完成") ==-1)){

                /*如果是自定义的问题名称，则像前端报告，且写入日志。*/
                if (problemScanLogic.getProblemId().indexOf("问题") ==-1  && problemScanLogic.getProblemId().indexOf("完成") ==-1){
                    //  自定义   问题
                    WebSocketService.sendMessage(loginUser.getUsername(),"风险:"+user_String.get("ip")+ ":"+ "问题类型"+totalQuestionTable.getTypeProblem()+ "问题名称"+totalQuestionTable.getTemProName()+ "风险:"+problemScanLogic.getProblemId()+"\r\n");
                    try {
                        PathHelper.writeDataToFile("风险:"+user_String.get("ip")+ ":"+ "问题类型"+totalQuestionTable.getTypeProblem()+ "问题名称"+totalQuestionTable.getTemProName()+ "风险:"+problemScanLogic.getProblemId()+"\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                /*try {
                    Thread.sleep(1000*3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                //问题数据 插入问题表 如果有参数 及插入
                //insertvalueInformationService(user_String,user_Object, totalQuestionTable,problemScanLogic,current_Round_Extraction_String);
                insertSwitchScanResult(user_String,user_Object, totalQuestionTable,problemScanLogic,current_Round_Extraction_String);

                //插入问题数据次数 加一
                insertsInteger++;
                current_Round_Extraction_String = "";
                //获取扫描出问题数据列表  集合 并放入 websocket
                //根据 用户信息 和 扫描时间

                //getUnresolvedProblemInformationByData(user_String,user_Object);
                getSwitchScanResultListByData(user_String,user_Object);

                /*如果tNextId下一分析ID(此时tNextId默认为下一分析ID)不为空时，则tNextId赋值给当前分析ID 调用本方法，继续分析流程。*/
                /*完成、有问题、无问题时，走下一t ID*/
                if (problemScanLogic.gettNextId() != null){
                    currentID = problemScanLogic.gettNextId();
                    /*如果使用 第一条分析ID firstID  则 当前分析ID currentID 要为 null*/
                    String loop_string = selectProblemScanLogicById(user_String,user_Object,totalQuestionTable,
                            return_information_array,"",extractInformation_string,
                            line_n,firstID,problemScanLogicList,currentID,insertsInteger, loop, numberOfCycles);

                    return loop_string;

                }

                /*如果tComId不为空时，则调用方法executeScanCommandByCommandId发送新命令，通过analysisReturnResults进行新的分析。*/
                if (problemScanLogic.gettComId() != null){
                    List<Object> executeScanCommandByCommandId_object = executeScanCommandByCommandId(user_String,totalQuestionTable,problemScanLogic.gettComId(),user_String.get("notFinished"),
                            user_String.get("mode"), user_Object);

                    if (executeScanCommandByCommandId_object.size() == 1){
                        AjaxResult ajaxResult = (AjaxResult) executeScanCommandByCommandId_object.get(0);
                        if ((ajaxResult.get("msg")+"").indexOf("错误") !=-1){

                            return ajaxResult.get("msg")+"";

                        }
                    }

                    String analysisReturnResults_String = analysisReturnResults(user_String, user_Object,totalQuestionTable,
                            executeScanCommandByCommandId_object,current_Round_Extraction_String, extractInformation_string);
                    return analysisReturnResults_String;
                }

            }

            //TODO ProblemId 包含完成 且 插入条数insertsInteger 为0

            // 分析执行 完成
            /*完成有程序分析结束  也可能是 获取基本信息 结束*/
            if (problemScanLogic.getProblemId().indexOf("完成")!=-1){
                return extractInformation_string;
            }

        }

        String condition = problemScanLogic.getRelativePosition().equals("null")?"全文":"按行";

        //相对位置——行,列(1,0)
        String relativePosition = problemScanLogic.getRelativePosition();
        //相对位置行
        String relativePosition_line ="";
        //相对位置列
        String relativePosition_row ="";
        /*如果 relativePosition 不为 null 并且 不为 “null”
        * 则说明是按行匹配  需要获取 行数   或者列数（未做）*/
        if (relativePosition!=null && !(relativePosition.equals("null"))){
            String[] relativePosition_split = relativePosition.split(",");
            //相对位置行
            relativePosition_line = relativePosition_split[0];
            //相对位置列
            relativePosition_row = relativePosition_split[1];
        }
        //分析数据 的 关键字
        String matchContent = "";
        if (problemScanLogic.getMatchContent()!=null){
            matchContent = problemScanLogic.getMatchContent();
            matchContent = matchContent.trim();
        }

        //标定从第line_n开始扫描
        //相对位置行 relativePosition_line
        //分析数据 相对位置为空 或者 line_n !=0 不为0
        //relativePosition_line = "null" 则从头开始匹配
        //如果 !relativePosition_line.equals("null")  则 根据 relativePosition_line 行来分析
        if (!relativePosition_line.equals("") || line_n !=0){
            int line_number = 0;
            if (!relativePosition_line.equals("")){
                line_number = Integer.valueOf(relativePosition_line).intValue();
            }
            //line_n 为上一条分析的 成功确认索引  加 下一条相对位置 就是下一个索引位置
            line_n = line_n + line_number;
        }else {
            line_n = 0 ;
        }

        //匹配逻辑
        String matched = null;
        if (problemScanLogic.getMatched()!=null){
            matched = problemScanLogic.getMatched();
        }

        //取词逻辑
        String action = null;
        if (problemScanLogic.getAction() != null){
            action = problemScanLogic.getAction();
        }

        //比较分析
        String compare = null;
        if (problemScanLogic.getCompare() != null){
            compare = problemScanLogic.getCompare();
        }


        //从line_n=0 开始检索集合 一直到最后一位
        for (int num = line_n; num<return_information_array.length; num++){

            //光标位置
            line_n = num;
            //返回信息的数组元素 第num 条
            String information_line_n = return_information_array[num];

            //匹配逻辑 有成功失败之分
            if (matched != null){

                //根据匹配方法 得到是否匹配（成功:true 失败:false）
                //matched : 精确匹配  information_line_n：交换机返回信息行  matchContent：数据库 关键词
                boolean matchAnalysis_true_false = MyUtils.matchAnalysis(matched, information_line_n, matchContent);

                if (matchAnalysis_true_false){
                    //  自定义   问题
                    WebSocketService.sendMessage(loginUser.getUsername(),"TrueAndFalse:"+user_String.get("ip") +  (totalQuestionTable==null?"：获取交换机基本信息":("：问题类型"+totalQuestionTable.getTypeProblem()+ "问题名称"+totalQuestionTable.getTemProName()))+
                            ":"+condition+matched+problemScanLogic.getMatchContent()+"成功\r\n");
                    try {
                        PathHelper.writeDataToFile("TrueAndFalse:"+user_String.get("ip")+  (totalQuestionTable==null?"：获取交换机基本信息":("：问题类型"+totalQuestionTable.getTypeProblem()+ "问题名称"+totalQuestionTable.getTemProName()))+
                                ":"+condition+matched+problemScanLogic.getMatchContent()+"成功\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                //如果最终逻辑成功 则把 匹配成功的行数 付给变量 line_n
                if (matchAnalysis_true_false){

                    /*if (problemScanLogic.gettComId()!=null && problemScanLogic.gettComId()!=""){
                        List<Object> executeScanCommandByCommandId_object = executeScanCommandByCommandId(user_String,totalQuestionTable,problemScanLogic.gettComId(),user_String.get("notFinished"),
                                user_String.get("mode"), user_Object);

                        if (executeScanCommandByCommandId_object.size() == 1){
                            AjaxResult ajaxResult = (AjaxResult) executeScanCommandByCommandId_object.get(0);
                            if ((ajaxResult.get("msg")+"").indexOf("错误") !=-1){
                                return ajaxResult.get("msg")+"";
                            }
                        }

                        String analysisReturnResults_String = analysisReturnResults(user_String, user_Object,totalQuestionTable,
                                executeScanCommandByCommandId_object,current_Round_Extraction_String, extractInformation_string);

                        return analysisReturnResults_String;
                    }

                    //下一条true分析ID
                    if (problemScanLogic.gettNextId()!=null && problemScanLogic.gettNextId()!=""){
                        String tNextId = problemScanLogic.gettNextId();
                        String ProblemScanLogic_returnstring = selectProblemScanLogicById(user_String,user_Object,totalQuestionTable,
                                return_information_array,current_Round_Extraction_String,extractInformation_string,
                                line_n,firstID,problemScanLogicList,tNextId,insertsInteger, loop, numberOfCycles);
                        //如果返回信息为null
                        if (ProblemScanLogic_returnstring!=null){
                            //内分析传到上一层
                            //extractInformation_string 是 分析的 总提取信息记录 所以要把内层的记录 传给 外层
                            extractInformation_string = ProblemScanLogic_returnstring;
                            return ProblemScanLogic_returnstring;
                        }
                        return ProblemScanLogic_returnstring;
                    }*/

                    String trueLogic = trueLogic(user_String, user_Object, totalQuestionTable,
                            return_information_array, current_Round_Extraction_String, extractInformation_string,
                            line_n, firstID, problemScanLogicList, currentID,
                            insertsInteger, loop, numberOfCycles, problemScanLogic);

                    return trueLogic;

                    //匹配失败
                }else {

                    //relativePosition.equals("null") 全文检索
                    // 如果不是最后一条信息 并且 全文检索的话  则返回到循环 返回信息数组 的下一条
                    if (relativePosition.equals("null") && num<return_information_array.length-1){
                        continue;
                    }

                    //  自定义   问题
                    WebSocketService.sendMessage(loginUser.getUsername(),"TrueAndFalse:"+user_String.get("ip")+  (totalQuestionTable==null?"：获取交换机基本信息":("：问题类型"+totalQuestionTable.getTypeProblem()+ "问题名称"+totalQuestionTable.getTemProName()))+
                            ":"+condition+matched+problemScanLogic.getMatchContent()+"失败\r\n");
                    try {
                        PathHelper.writeDataToFile("TrueAndFalse:"+user_String.get("ip")+  (totalQuestionTable==null?"：获取交换机基本信息":("：问题类型"+totalQuestionTable.getTypeProblem()+ "问题名称"+totalQuestionTable.getTemProName()))+
                                ":"+condition+matched+problemScanLogic.getMatchContent()+"失败\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    /*if (problemScanLogic.getfComId()!=null && problemScanLogic.getfComId()!=""){
                        List<Object> executeScanCommandByCommandId_object = executeScanCommandByCommandId(user_String,totalQuestionTable,problemScanLogic.getfComId(),user_String.get("notFinished"), user_String.get("mode"), user_Object);

                        if (executeScanCommandByCommandId_object.size() == 1){
                            AjaxResult ajaxResult = (AjaxResult) executeScanCommandByCommandId_object.get(0);
                            if ((ajaxResult.get("msg")+"").indexOf("错误") !=-1){
                                return ajaxResult.get("msg")+"";
                            }
                        }

                        String analysisReturnResults_String = analysisReturnResults(user_String, user_Object,totalQuestionTable,
                                executeScanCommandByCommandId_object,  current_Round_Extraction_String,  extractInformation_string);
                        return analysisReturnResults_String;
                    }

                    if (problemScanLogic.getfNextId()!=null && problemScanLogic.getfNextId()!=null){
                        //下一条frue分析ID
                        String fNextId = problemScanLogic.getfNextId();
                        String ProblemScanLogic_returnstring = selectProblemScanLogicById(user_String,user_Object,totalQuestionTable,
                                return_information_array,current_Round_Extraction_String,extractInformation_string,
                                line_n,firstID,problemScanLogicList,fNextId,insertsInteger, loop, numberOfCycles);
                        //如果返回信息为null
                        if (ProblemScanLogic_returnstring!=null){
                            //内分析传到上一层
                            //extractInformation_string 是 分析的 总提取信息记录 所以要把内层的记录 传给 外层
                            extractInformation_string = ProblemScanLogic_returnstring;
                            return ProblemScanLogic_returnstring;
                        }
                        return ProblemScanLogic_returnstring;
                    }*/

                    String falseLogic = falseLogic(user_String, user_Object, totalQuestionTable,
                            return_information_array, current_Round_Extraction_String, extractInformation_string,
                            line_n, firstID, problemScanLogicList, currentID,
                            insertsInteger, loop, numberOfCycles, problemScanLogic);

                    return falseLogic;

                }
            }


            //取词
            if (action!=null && !action.equals("null")){
                //取词数
                String wordSelection_string = null;

                if (action.equals("品牌")){
                    wordSelection_string = user_String.get("deviceBrand");
                }else if (action.equals("型号")){
                    wordSelection_string = user_String.get("deviceModel");
                }else if (action.equals("内部固件版本")){
                    wordSelection_string = user_String.get("firmwareVersion");
                }else if (action.equals("子版本号")){
                    wordSelection_string = user_String.get("subversionNumber");
                }else {
                    //取词操作
                    wordSelection_string = MyUtils.wordSelection(
                            return_information_array[num],matchContent, //返回信息的一行 提取关键字
                            relativePosition_line,problemScanLogic.getrPosition(), problemScanLogic.getLength()); //位置 长度WLs
                }

                //取词逻辑只有成功，但是如果取出为空 则为 取词失败
                if (wordSelection_string == null){

                    //  自定义   问题
                    WebSocketService.sendMessage(loginUser.getUsername(),"TrueAndFalse:"+user_String.get("ip")+  (totalQuestionTable==null ? "：获取交换机基本信息" : ("：问题类型"+totalQuestionTable.getTypeProblem()+ "问题名称"+totalQuestionTable.getTemProName()))+
                            ":取词"+problemScanLogic.getWordName()+"失败\r\n");
                    try {
                        PathHelper.writeDataToFile("TrueAndFalse:"+user_String.get("ip")+  (totalQuestionTable==null ? "：获取交换机基本信息" : ("：问题类型"+totalQuestionTable.getTypeProblem()+ "问题名称"+totalQuestionTable.getTemProName()))+
                                ":取词"+problemScanLogic.getWordName()+"失败\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return "取词失败！";
                }

                //  自定义   问题
                WebSocketService.sendMessage(loginUser.getUsername(),"TrueAndFalse:"+user_String.get("ip") +  (totalQuestionTable==null ? "：获取交换机基本信息" : ("：问题类型"+totalQuestionTable.getTypeProblem()+ "问题名称"+totalQuestionTable.getTemProName()))+
                        ":取词"+problemScanLogic.getWordName()+"成功\r\n");
                try {
                    PathHelper.writeDataToFile("TrueAndFalse:"+user_String.get("ip") +  (totalQuestionTable==null ? "：获取交换机基本信息" : ("：问题类型"+totalQuestionTable.getTypeProblem()+ "问题名称"+totalQuestionTable.getTemProName()))+
                            ":取词"+problemScanLogic.getWordName()+"成功\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*判断 字符串 最后一位 是否为 . 或者 ,  去掉*/
                wordSelection_string = MyUtils.judgeResultWordSelection(wordSelection_string);


                //problemScanLogic.getWordName() 取词名称
                //problemScanLogic.getExhibit() 是否可以显示
                //wordSelection_string 取词内容
                extractInformation_string = extractInformation_string +problemScanLogic.getWordName()+"=:="+problemScanLogic.getExhibit()+"=:="+ wordSelection_string+"=:=";
                current_Round_Extraction_String = current_Round_Extraction_String +problemScanLogic.getWordName()+"=:="+problemScanLogic.getExhibit()+"=:="+ wordSelection_string+"=:=";

                /*if (problemScanLogic.gettComId()!=null && problemScanLogic.gettComId()!=""){
                    List<Object> executeScanCommandByCommandId_object = executeScanCommandByCommandId(user_String,totalQuestionTable,problemScanLogic.gettComId(),user_String.get("notFinished"), user_String.get("mode"),user_Object);

                    if (executeScanCommandByCommandId_object.size() == 1){
                        AjaxResult ajaxResult = (AjaxResult) executeScanCommandByCommandId_object.get(0);
                        if ((ajaxResult.get("msg")+"").indexOf("错误") !=-1){
                            return ajaxResult.get("msg")+"";
                        }
                    }

                    String analysisReturnResults_String = analysisReturnResults(user_String,user_Object,totalQuestionTable,
                            executeScanCommandByCommandId_object,  current_Round_Extraction_String,  extractInformation_string);

                    return analysisReturnResults_String;
                }

                if (problemScanLogic.gettNextId()!=null && problemScanLogic.gettNextId()!=""){
                    //下一ID
                    String tNextId = problemScanLogic.gettNextId();
                    String ProblemScanLogic_returnstring = selectProblemScanLogicById(user_String,user_Object,totalQuestionTable,
                            return_information_array,current_Round_Extraction_String,extractInformation_string,
                            line_n,firstID,problemScanLogicList,tNextId,insertsInteger, loop, numberOfCycles);
                    if (ProblemScanLogic_returnstring!=null){
                        extractInformation_string = ProblemScanLogic_returnstring;
                        return ProblemScanLogic_returnstring;
                    }
                    return ProblemScanLogic_returnstring;
                }*/

                String trueLogic = trueLogic(user_String, user_Object, totalQuestionTable,
                        return_information_array, current_Round_Extraction_String, extractInformation_string,
                        line_n, firstID, problemScanLogicList, currentID,
                        insertsInteger, loop, numberOfCycles, problemScanLogic);

                return trueLogic;

            }

            //比较
            if (compare!=null){

                //比较
                boolean compare_boolean = MyUtils.compareVersion(user_String,compare,current_Round_Extraction_String);


                if (compare_boolean){
                    //  自定义   问题
                    WebSocketService.sendMessage(loginUser.getUsername(),"TrueAndFalse:"+user_String.get("ip")+ (totalQuestionTable!=null?"：获取交换机基本信息":("：问题类型"+totalQuestionTable.getTypeProblem()+ "问题名称"+totalQuestionTable.getTemProName()))+
                            ":比较"+problemScanLogic.getCompare()+"成功\r\n");
                    try {
                        PathHelper.writeDataToFile("TrueAndFalse:"+user_String.get("ip")+ (totalQuestionTable!=null?"：获取交换机基本信息":("：问题类型"+totalQuestionTable.getTypeProblem()+ "问题名称"+totalQuestionTable.getTemProName()))+
                                ":比较"+problemScanLogic.getCompare()+"成功\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    //  自定义   问题
                    WebSocketService.sendMessage(loginUser.getUsername(),"TrueAndFalse:"+user_String.get("ip")+ (totalQuestionTable!=null?"：获取交换机基本信息":("：问题类型"+totalQuestionTable.getTypeProblem()+ "问题名称"+totalQuestionTable.getTemProName()))+
                            ":比较"+problemScanLogic.getCompare()+"失败\r\n");
                    try {
                        PathHelper.writeDataToFile("TrueAndFalse:"+user_String.get("ip")+ (totalQuestionTable!=null?"：获取交换机基本信息":("：问题类型"+totalQuestionTable.getTypeProblem()+ "问题名称"+totalQuestionTable.getTemProName()))+
                                ":比较"+problemScanLogic.getCompare()+"失败\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }



                if (compare_boolean){

                    /*if (problemScanLogic.gettComId()!=null && problemScanLogic.gettComId()!=""){
                        List<Object> executeScanCommandByCommandId_object = executeScanCommandByCommandId(user_String,totalQuestionTable,problemScanLogic.gettComId(),user_String.get("notFinished"), user_String.get("mode"),user_Object);

                        if (executeScanCommandByCommandId_object.size() == 1){
                            AjaxResult ajaxResult = (AjaxResult) executeScanCommandByCommandId_object.get(0);
                            if ((ajaxResult.get("msg")+"").indexOf("错误") !=-1){
                                return ajaxResult.get("msg")+"";
                            }
                        }

                        String analysisReturnResults_String = analysisReturnResults(user_String,user_Object,totalQuestionTable,
                                executeScanCommandByCommandId_object,  current_Round_Extraction_String,  extractInformation_string);
                        return analysisReturnResults_String;
                    }

                    if (problemScanLogic.gettNextId()!=null && problemScanLogic.gettNextId()!=""){
                        String tNextId = problemScanLogic.gettNextId();
                        String ProblemScanLogic_returnstring = selectProblemScanLogicById(user_String,user_Object,totalQuestionTable,
                                return_information_array,current_Round_Extraction_String,extractInformation_string,
                                line_n,firstID,problemScanLogicList,tNextId,insertsInteger, loop, numberOfCycles);

                        if (ProblemScanLogic_returnstring!=null){
                            extractInformation_string = ProblemScanLogic_returnstring;
                            return ProblemScanLogic_returnstring;
                        }

                        return ProblemScanLogic_returnstring;
                    }*/

                    String trueLogic = trueLogic(user_String, user_Object, totalQuestionTable,
                            return_information_array, current_Round_Extraction_String, extractInformation_string,
                            line_n, firstID, problemScanLogicList, currentID,
                            insertsInteger, loop, numberOfCycles, problemScanLogic);

                    return trueLogic;

                }else {

                    /*if (problemScanLogic.getfComId()!=null && problemScanLogic.getfComId()!=""){
                        List<Object> executeScanCommandByCommandId_object = executeScanCommandByCommandId(user_String,totalQuestionTable,problemScanLogic.getfComId(),user_String.get("notFinished"), user_String.get("mode"),user_Object);

                        if (executeScanCommandByCommandId_object.size() == 1){
                            AjaxResult ajaxResult = (AjaxResult) executeScanCommandByCommandId_object.get(0);
                            if ((ajaxResult.get("msg")+"").indexOf("错误") !=-1){
                                return ajaxResult.get("msg")+"";
                            }
                        }

                        String analysisReturnResults_String = analysisReturnResults(user_String,user_Object,totalQuestionTable,
                                executeScanCommandByCommandId_object,  current_Round_Extraction_String,  extractInformation_string);

                        return analysisReturnResults_String;

                    }

                    if (problemScanLogic.getfNextId()!=null && problemScanLogic.getfNextId()!=""){
                        String fNextId = problemScanLogic.getfNextId();
                        String ProblemScanLogic_returnstring = selectProblemScanLogicById(user_String,user_Object,totalQuestionTable,
                                return_information_array,current_Round_Extraction_String,extractInformation_string,
                                line_n,firstID,problemScanLogicList,fNextId,insertsInteger, loop, numberOfCycles);

                        if (ProblemScanLogic_returnstring!=null){
                            extractInformation_string = ProblemScanLogic_returnstring;
                            return ProblemScanLogic_returnstring;
                        }

                        return ProblemScanLogic_returnstring;
                    }*/

                    String falseLogic = falseLogic(user_String, user_Object, totalQuestionTable,
                            return_information_array, current_Round_Extraction_String, extractInformation_string,
                            line_n, firstID, problemScanLogicList, currentID,
                            insertsInteger, loop, numberOfCycles, problemScanLogic);

                    return falseLogic;

                }
            }
        }
        return null;
    }


    /*true逻辑*/
    public static String trueLogic(Map<String, String> user_String, Map<String, Object> user_Object, TotalQuestionTable totalQuestionTable,
                                   String[] return_information_array, String current_Round_Extraction_String, String extractInformation_string,
                                   int line_n, String firstID, List<ProblemScanLogic> problemScanLogicList, String currentID,
                                   Integer insertsInteger, Integer loop, Integer numberOfCycles, ProblemScanLogic problemScanLogic) {

        if (problemScanLogic.gettComId()!=null && problemScanLogic.gettComId()!=""){
            List<Object> executeScanCommandByCommandId_object = executeScanCommandByCommandId(user_String,totalQuestionTable,problemScanLogic.gettComId(),user_String.get("notFinished"),
                    user_String.get("mode"), user_Object);

            if (executeScanCommandByCommandId_object.size() == 1){
                AjaxResult ajaxResult = (AjaxResult) executeScanCommandByCommandId_object.get(0);
                if ((ajaxResult.get("msg")+"").indexOf("错误") !=-1){
                    return ajaxResult.get("msg")+"";
                }
            }

            String analysisReturnResults_String = analysisReturnResults(user_String, user_Object,totalQuestionTable,
                    executeScanCommandByCommandId_object,current_Round_Extraction_String, extractInformation_string);

            return analysisReturnResults_String;
        }

        //下一条true分析ID
        if (problemScanLogic.gettNextId()!=null && problemScanLogic.gettNextId()!=""){
            String tNextId = problemScanLogic.gettNextId();
            String ProblemScanLogic_returnstring = selectProblemScanLogicById(user_String,user_Object,totalQuestionTable,
                    return_information_array,current_Round_Extraction_String,extractInformation_string,
                    line_n,firstID,problemScanLogicList,tNextId,insertsInteger, loop, numberOfCycles);
            //如果返回信息为null
            if (ProblemScanLogic_returnstring!=null){
                //内分析传到上一层
                //extractInformation_string 是 分析的 总提取信息记录 所以要把内层的记录 传给 外层
                extractInformation_string = ProblemScanLogic_returnstring;
                return ProblemScanLogic_returnstring;
            }
            return ProblemScanLogic_returnstring;
        }
        return null;
    }


    /*false 逻辑*/
    public static String falseLogic(Map<String, String> user_String, Map<String, Object> user_Object, TotalQuestionTable totalQuestionTable,
                                    String[] return_information_array, String current_Round_Extraction_String, String extractInformation_string,
                                    int line_n, String firstID, List<ProblemScanLogic> problemScanLogicList, String currentID,
                                    Integer insertsInteger, Integer loop, Integer numberOfCycles, ProblemScanLogic problemScanLogic) {

        if (problemScanLogic.getfComId()!=null && problemScanLogic.getfComId()!=""){
            List<Object> executeScanCommandByCommandId_object = executeScanCommandByCommandId(user_String,totalQuestionTable,problemScanLogic.getfComId(),user_String.get("notFinished"), user_String.get("mode"), user_Object);

            if (executeScanCommandByCommandId_object.size() == 1){
                AjaxResult ajaxResult = (AjaxResult) executeScanCommandByCommandId_object.get(0);
                if ((ajaxResult.get("msg")+"").indexOf("错误") !=-1){
                    return ajaxResult.get("msg")+"";
                }
            }

            String analysisReturnResults_String = analysisReturnResults(user_String, user_Object,totalQuestionTable,
                    executeScanCommandByCommandId_object,  current_Round_Extraction_String,  extractInformation_string);
            return analysisReturnResults_String;
        }

        if (problemScanLogic.getfNextId()!=null && problemScanLogic.getfNextId()!=null){
            //下一条frue分析ID
            String fNextId = problemScanLogic.getfNextId();
            String ProblemScanLogic_returnstring = selectProblemScanLogicById(user_String,user_Object,totalQuestionTable,
                    return_information_array,current_Round_Extraction_String,extractInformation_string,
                    line_n,firstID,problemScanLogicList,fNextId,insertsInteger, loop, numberOfCycles);
            //如果返回信息为null
            if (ProblemScanLogic_returnstring!=null){
                //内分析传到上一层
                //extractInformation_string 是 分析的 总提取信息记录 所以要把内层的记录 传给 外层
                extractInformation_string = ProblemScanLogic_returnstring;
                return ProblemScanLogic_returnstring;
            }
            return ProblemScanLogic_returnstring;
        }
        return null;
    }

    /**
     * @method: 插人动态信息数据
     * @Param: [Map<String,String> user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法, connectMethod ssh连接, telnetSwitchMethod telnet连接
     *                 boolean boo  分析成功 还是分析失败 true && false
     *                 ProblemScanLogic problemScanLogic 分析信息
     *                 String parameterString 单词提取信息
     *                 ]
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public static void insertSwitchScanResult (Map<String,String> user_String,Map<String,Object> user_Object,
                                                     TotalQuestionTable totalQuestionTable,
                                                     ProblemScanLogic problemScanLogic,
                                                     String parameterString){

        //系统登录人 用户名
        LoginUser loginUser = (LoginUser)user_Object.get("loginUser");
        String userName = loginUser.getUsername();
        //系统登录人 手机号
        String phonenumber = loginUser.getUser().getPhonenumber();

        //截取 有问题 还是 无问题
        String substring = problemScanLogic.getProblemId();
        //截取 问题代码
        String problemId = totalQuestionTable.getId()+"";

        SwitchScanResult switchScanResult = new SwitchScanResult();

        //插入问题数据
        switchScanResult.setSwitchIp(user_String.get("ip")); // ip

        switchScanResult.setBrand(totalQuestionTable.getBrand());
        switchScanResult.setSwitchType(totalQuestionTable.getType());
        switchScanResult.setFirewareVersion(totalQuestionTable.getFirewareVersion());
        switchScanResult.setSubVersion(totalQuestionTable.getSubVersion());

        switchScanResult.setSwitchName(user_String.get("name")); //name
        switchScanResult.setSwitchPassword(user_String.get("password")); //password
        switchScanResult.setConfigureCiphers(user_String.get("configureCiphers"));

        switchScanResult.setLoginMethod(user_String.get("mode"));
        switchScanResult.setPortNumber(Integer.valueOf(user_String.get("port")).intValue());

        switchScanResult.setProblemId(problemId); // 问题索引
        switchScanResult.setTypeProblem(totalQuestionTable.getTypeProblem());
        switchScanResult.setTemProName(totalQuestionTable.getTemProName());
        switchScanResult.setProblemName(totalQuestionTable.getProblemName());

        switchScanResult.setRemarks(totalQuestionTable.getRemarks());
        switchScanResult.setProblemDescribeId(totalQuestionTable.getProblemDescribeId());

        switchScanResult.setDynamicInformation(parameterString);
        switchScanResult.setIfQuestion(substring); //是否有问题

        if (totalQuestionTable.getProblemSolvingId() != null){
            switchScanResult.setComId(totalQuestionTable.getProblemSolvingId());//命令索引
        }else {
            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(loginUser.getUsername(),"风险："+totalQuestionTable.getId()
                    +totalQuestionTable.getTypeProblem()+totalQuestionTable.getTemProName()+totalQuestionTable.getProblemName()
                    +"未定义解决问题\r\n");
            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFile("风险："+totalQuestionTable.getId()
                        +totalQuestionTable.getTypeProblem()+totalQuestionTable.getTemProName()+totalQuestionTable.getProblemName()
                        +"未定义解决问题\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        switchScanResult.setUserName(userName);//登录名称
        switchScanResult.setPhonenumber(phonenumber); //登录手机号
        //插入 扫描时间
        String loginTime = user_String.get("ScanningTime");
        DateTime dateTime = new DateTime(loginTime, "yyyy-MM-dd HH:mm:ss");
        switchScanResult.setCreateTime(dateTime);

        //插入问题
        switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);
        switchScanResultService.insertSwitchScanResult(switchScanResult);
    };


    /**
     * @method: 查询扫描出的问题表 放入 websocket
     * @Param: []
     * @return: java.util.List<com.sgcc.sql.domain.SwitchProblem>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @GetMapping("getUnresolvedProblemInformationByData")
    @ApiOperation("查询当前扫描出的问题表放入websocket")
    public static List<ScanResultsVO> getUnresolvedProblemInformationByData(Map<String,String> user_String,Map<String,Object> user_Object){

        //用户名
        LoginUser loginUser = (LoginUser)user_Object.get("loginUser");
        String loginName = loginUser.getUsername();
        //扫描时间
        String loginTime = user_String.get("ScanningTime");
        switchProblemService = SpringBeanUtil.getBean(ISwitchProblemService.class);
        List<SwitchProblemVO> switchProblemList = switchProblemService.selectUnresolvedProblemInformationByDataAndUserName(loginTime,loginName);
        if (switchProblemList.size() == 0){
            return null;
        }
        for (SwitchProblemVO switchProblemVO:switchProblemList){
            Date date1 = new Date();
            switchProblemVO.hproblemId =  Long.valueOf(MyUtils.getTimestamp(date1)+""+ (int)(Math.random()*10000+1)).longValue();
            List<SwitchProblemCO> switchProblemCOList = switchProblemVO.getSwitchProblemCOList();
            for (SwitchProblemCO switchProblemCO:switchProblemCOList){
                valueInformationService = SpringBeanUtil.getBean(IValueInformationService.class);//解决 多线程 service 为null问题
                List<ValueInformationVO> valueInformationVOList = valueInformationService.selectValueInformationVOListByID(switchProblemCO.getValueId());
                for (ValueInformationVO valueInformationVO:valueInformationVOList){
                    Date date2 = new Date();
                    valueInformationVO.hproblemId = Long.valueOf(MyUtils.getTimestamp(date2)+""+ (int)(Math.random()*10000+1)).longValue();
                }

                Date date3 = new Date();
                switchProblemCO.hproblemId = Long.valueOf(MyUtils.getTimestamp(date3)+""+ (int)(Math.random()*10000+1)).longValue();
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
            scanResultsVO.hproblemId = Long.valueOf(MyUtils.getTimestamp(date4)+""+ (int)(Math.random()*10000+1)).longValue();
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

        WebSocketService.sendMessage("loophole"+loginName,scanResultsVOList);

        return scanResultsVOList;
    }

    /**
     *
     * a
     *
     * @method: 查询扫描出的问题表 放入 websocket
     * @Param: []
     * @return: java.util.List<com.sgcc.sql.domain.SwitchProblem>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @GetMapping("getSwitchScanResultListByData")
    @ApiOperation("查询当前扫描出的问题表放入websocket")
    public static List<ScanResultsVO> getSwitchScanResultListByData(Map<String,String> user_String,Map<String,Object> user_Object){

        //用户名
        LoginUser loginUser = (LoginUser)user_Object.get("loginUser");
        String loginName = loginUser.getUsername();

        //扫描时间 带有结构
        String loginTime = user_String.get("ScanningTime");
        switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);
        List<SwitchProblemVO> switchProblemList = switchScanResultService.selectSwitchScanResultListByDataAndUserName(loginTime,loginName);
        if (switchProblemList.size() == 0){
            return null;
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
                switchProblemCO.setHproblemId(Long.valueOf(MyUtils.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
                /*定义 参数集合 */
                List<ValueInformationVO> valueInformationVOList = new ArrayList<>();
                /*根据 结构数据中的 交换机扫描结果ID 在交换机扫描结果数据 hashmap中 取出 *//*
                SwitchScanResult switchScanResult = hashMap.get(switchProblemCO.getQuestionId());*/
                //提取信息 如果不为空 则有参数
                if (switchProblemCO.getDynamicInformation()!=null && !switchProblemCO.getDynamicInformation().equals("")){//switchScanResult.getDynamicInformation()!=null && !switchScanResult.getDynamicInformation().equals("")
                    //String dynamicInformation = switchScanResult.getDynamicInformation();
                    String dynamicInformation = switchProblemCO.getDynamicInformation();
                    //几个参数中间的 参数是 以  "=:=" 来分割的
                    //设备型号=:=是=:=S3600-28P-EI=:=设备品牌=:=是=:=H3C=:=内部固件版本=:=是=:=3.10,=:=子版本号=:=是=:=1510P09=:=
                    String[] dynamicInformationsplit = dynamicInformation.split("=:=");
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
                            valueInformationVO.setHproblemId(Long.valueOf(MyUtils.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
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
            Date date4 = new Date();
            scanResultsVO.hproblemId = Long.valueOf(MyUtils.getTimestamp(date4)+""+ (int)(Math.random()*10000+1)).longValue();
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
            scanResultsVO.setHproblemId(Long.valueOf(MyUtils.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
            List<SwitchProblemVO> switchProblemVOList = scanResultsVO.getSwitchProblemVOList();
            for (SwitchProblemVO switchProblemVO:switchProblemVOList){
                switchProblemVO.setHproblemId(Long.valueOf(MyUtils.getTimestamp(new Date())+""+ (int)(Math.random()*10000+1)).longValue());
                switchProblemVO.setSwitchIp(null);
            }
        }

        WebSocketService.sendMessage("loophole"+loginName,scanResultsVOList);

        return scanResultsVOList;
    }


    /**
     * @method: 根据命令ID获取具体命令，执行并返回交换机返回信息
     * @Param:
     * @return:  返回的是 解决问题ID
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     * 分析ID 连接方式 ssh和telnet连接
     */
    @GetMapping("/executeScanCommandByCommandId")
    public static List<Object> executeScanCommandByCommandId(Map<String,String> user_String,
                                                             TotalQuestionTable totalQuestionTable,
                                                             String commandId,String notFinished,
                                                             String way,
                                                             Map<String,Object> user_Object) {
        //四参数赋值

        SshConnect sshConnect = (SshConnect) user_Object.get("sshConnect");
        SshMethod connectMethod = (SshMethod) user_Object.get("connectMethod");
        TelnetComponent telnetComponent = (TelnetComponent) user_Object.get("telnetComponent");
        TelnetSwitchMethod telnetSwitchMethod = (TelnetSwitchMethod) user_Object.get("telnetSwitchMethod");
        LoginUser loginUser = (LoginUser)user_Object.get("loginUser");
        String userName = loginUser.getUsername();

        //命令ID获取具体命令
        commandLogicService = SpringBeanUtil.getBean(ICommandLogicService.class);
        CommandLogic commandLogic = commandLogicService.selectCommandLogicById(commandId);
        if (commandLogic == null){

            //传输登陆人姓名 及问题简述
            WebSocketService.sendMessage(loginUser.getUsername(),"风险："+totalQuestionTable.getId()+ ":"
                    +totalQuestionTable.getTypeProblem()+":"
                    +totalQuestionTable.getProblemName()+":"
                    +totalQuestionTable.getTemProName()+":"+"问题定义错误扫描命令不存在\r\n");
            try {
                //插入问题简述及问题路径
                PathHelper.writeDataToFile("风险："+totalQuestionTable.getId()+ ":"
                        +totalQuestionTable.getTypeProblem()+":"
                        +totalQuestionTable.getProblemName()+":"
                        +totalQuestionTable.getTemProName()+":"+"问题定义错误扫描命令不存在\r\n"
                        +"方法com.sgcc.web.controller.sql.SwitchInteraction.executeScanCommandByCommandId");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        //具体命令
        String command = commandLogic.getCommand();

        //执行命令
        //命令返回信息
        String command_string = null;

        //交换机返回信息 插入 数据库
        ReturnRecord returnRecord = new ReturnRecord();

        int insert_id = 0;
        returnRecord.setUserName(userName);
        returnRecord.setSwitchIp(user_String.get("ip"));
        returnRecord.setBrand(user_String.get("deviceBrand"));
        returnRecord.setType(user_String.get("deviceModel"));
        returnRecord.setFirewareVersion(user_String.get("firmwareVersion"));
        returnRecord.setSubVersion(user_String.get("subversionNumber"));
        returnRecord.setCurrentCommLog(command.trim());
        boolean deviceBrand = true;

        do {
            deviceBrand = true;

            if (way.equalsIgnoreCase("ssh")) {
                WebSocketService.sendMessage(userName, "发送:" + command+"\r\n");

                try {
                    PathHelper.writeDataToFile("发送:" + command+"\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                command_string = connectMethod.sendCommand(user_String.get("ip"), sshConnect, command, notFinished);
                //command_string = Utils.removeLoginInformation(command_string);
            } else if (way.equalsIgnoreCase("telnet")) {
                WebSocketService.sendMessage(userName, "发送:" + command+"\r\n");

                try {
                    PathHelper.writeDataToFile("发送:" + command+"\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                command_string = telnetSwitchMethod.sendCommand(user_String.get("ip"), telnetComponent, command, notFinished);
                //command_string = Utils.removeLoginInformation(command_string);
            }

            returnRecord.setCurrentReturnLog(command_string);

            //粗略查看是否存在 故障 存在故障返回 false 不存在故障返回 true
            boolean switchfailure = MyUtils.switchfailure(user_String, command_string);

            // 存在故障返回 false
            if (!switchfailure) {
                String[] commandStringSplit = command_string.split("\r\n");
                for (String returnString : commandStringSplit) {
                    deviceBrand = MyUtils.switchfailure(user_String, returnString);
                    if (!deviceBrand) {
                        System.err.println("\r\n"+user_String.get("ip") + "故障:"+returnString+"\r\n");
                        WebSocketService.sendMessage(userName,"故障:"+"IP:"+user_String.get("ip")+":"+returnString+"\r\n");

                        try {
                            PathHelper.writeDataToFile("故障:"+"IP:"+user_String.get("ip")+":"+returnString+"\r\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        returnRecord.setCurrentIdentifier(user_String.get("ip") + "出现故障:"+returnString+"\r\n");
                        if (way.equalsIgnoreCase("ssh")){
                            connectMethod.sendCommand(user_String.get("ip"),sshConnect,"\r ",user_String.get("notFinished"));
                        }else if (way.equalsIgnoreCase("telnet")){
                            telnetSwitchMethod.sendCommand(user_String.get("ip"),telnetComponent,"\n ",user_String.get("notFinished"));
                        }

                        break;

                    }
                }

            }

            //返回信息表，返回插入条数
            returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);
            insert_id = returnRecordService.insertReturnRecord(returnRecord);

        }while (!deviceBrand);

        //返回信息表，返回插入条数
        returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);
        returnRecord = returnRecordService.selectReturnRecordById(Integer.valueOf(insert_id).longValue());

        //去除其他 交换机登录信息
        command_string = MyUtils.removeLoginInformation(command_string);
        //修整返回信息
        command_string = MyUtils.trimString(command_string);
        //去除 ---- More ----
        command_string = command_string.replaceAll(user_String.get("notFinished"),"");

        //按行切割
        String[] split = command_string.split("\r\n");


        String current_return_log = "";
        if (split.length != 1){
            current_return_log = command_string.substring(0,command_string.length()-split[split.length-1].length()-2).trim();
            returnRecord.setCurrentReturnLog(current_return_log);

            //返回日志前后都有\r\n
            String current_return_log_substring_end = current_return_log.substring(current_return_log.length() - 2, current_return_log.length());
            if (!current_return_log_substring_end.equals("\r\n")){
                current_return_log = current_return_log+"\r\n";
            }
            String current_return_log_substring_start = current_return_log.substring(0, 2);
            if (!current_return_log_substring_start.equals("\r\n")){
                current_return_log = "\r\n"+current_return_log;
            }

        }

        WebSocketService.sendMessage(userName,"接收:"+current_return_log+"\r\n");

        try {
            PathHelper.writeDataToFile("接收:"+current_return_log+"\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //按行切割，最后一位应该是 标识符
        String current_identifier = split[split.length-1].trim();
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

        WebSocketService.sendMessage(userName,"接收:"+current_identifier+"\r\n");
        try {
            PathHelper.writeDataToFile("接收:"+current_identifier+"\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }


        //返回信息表，返回插入条数
        returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);
        int update = returnRecordService.updateReturnRecord(returnRecord);

        //判断命令是否错误 错误为false 正确为true
        if (!(MyUtils.judgmentError( user_String,command_string))){
            //  简单检验，命令正确，新命令  commandLogic.getEndIndex()

            String[] returnString_split = command_string.split("\r\n");

            for (String string_split:returnString_split){
                if (!MyUtils.judgmentError( user_String,string_split)){
                    System.err.println("\r\n"+user_String.get("ip")+": 问题 ："+totalQuestionTable.getProblemName() +":" +command+ "错误:"+command_string+"\r\n");
                    WebSocketService.sendMessage(userName,"风险:"+"IP:"+user_String.get("ip") + "问题:"+totalQuestionTable.getProblemName() +"命令:" +command +":"+command_string+"\r\n");

                    try {
                        PathHelper.writeDataToFile("风险:"+"IP:"+user_String.get("ip") + "问题:"+totalQuestionTable.getProblemName() +"命令:" +command +":"+command_string+"\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    List<Object> objectList = new ArrayList<>();
                    objectList.add(AjaxResult.error(user_String.get("ip")+": 问题 ："+totalQuestionTable.getProblemName() +":" +command+ "错误:"+command_string));
                    return objectList;
                }
            }

            if (commandLogic.getEndIndex().equals("0")){
                return null;
            }

            List<Object> objectList = executeScanCommandByCommandId(user_String,totalQuestionTable,commandLogic.getEndIndex(),notFinished, way,user_Object);
            return objectList;
        }

        //判断是否简单检验 1L为简单校验  默认0L 为分析数据表自定义校验
        String first_problem_scanLogic_Id = "";
        if (!(commandLogic.getResultCheckId().equals("1"))){
            //分析第一条ID
            first_problem_scanLogic_Id = commandLogic.getProblemId();
        }else {
            List<Object> objectList = executeScanCommandByCommandId(user_String,totalQuestionTable,commandLogic.getEndIndex(),notFinished, way,user_Object);
            return objectList;
        }

        List<Object> objectList = new ArrayList<>();
        objectList.add(way.equalsIgnoreCase("ssh") ? command.trim()+"\r\n"+command_string : command_string);//交换机返回信息
        System.err.println("\r\n交换机返回信息:"+command.trim()+"\r\n"+command_string+"\r\n");
        objectList.add(first_problem_scanLogic_Id);//分析第一条ID
        return objectList;
    }

    /**
     * @method: 执行分析
     * @Param: [resultString_ProblemScanLogicId]
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     * 用户信息 连接方式 ssh、telnet
     * 交换机返回信息 分析ID
     */
    @GetMapping("analysisReturnResults")
    public static String analysisReturnResults(Map<String,String> user_String,
                                        Map<String,Object> user_Object,
                                        TotalQuestionTable totalQuestionTable,
                                        List<Object> executeScanCommandByCommandId_object,String current_Round_Extraction_String,String extractInformation_string){

        String resultString = executeScanCommandByCommandId_object.get(0).toString();//交换机返回信息
        String first_problem_scanLogic_Id = executeScanCommandByCommandId_object.get(1)+"";//分析第一条ID

        //将交换机返回信息 按行来切割 字符串数组
        String[] return_information_array =resultString.split("\r\n");

        Integer numberOfCycles = Configuration.numberOfCycles.intValue();

        //根据ID去分析
        String problemScanLogic_string = selectProblemScanLogicById( user_String,user_Object, totalQuestionTable,
                return_information_array,current_Round_Extraction_String,extractInformation_string,
                0,first_problem_scanLogic_Id,null,null,0,0,numberOfCycles);// loop end

        if (problemScanLogic_string!=null){
            return problemScanLogic_string;
        }else {
            return null;
        }
    }


    /**
     * @method: 5.获取交换机可扫描的问题并执行分析操作
     * @Param: [user_String, connectMethod, telnetSwitchMethod]
     * @return: com.sgcc.common.core.domain.AjaxResult
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @GetMapping("scanProblem")
    public AjaxResult scanProblem(Map<String,String> user_String, //登录交换机的 用户信息 登录方式、ip、name、password
                                  Map<String,Object> user_Object,
                                  List<TotalQuestionTable> totalQuestionTables){
        //交换机基本信息
        String deviceModel = user_String.get("deviceModel");//设备型号
        String deviceBrand = user_String.get("deviceBrand");//设备品牌
        String firmwareVersion = user_String.get("firmwareVersion");//内部固件版本
        String subversionNumber = user_String.get("subversionNumber");//子版本号

        /*存储 可扫描交换机问题*/
        List<TotalQuestionTable> totalQuestionTableList = new ArrayList<>();

        //totalQuestionTables == null 的时候 是扫描全部问题
        if (totalQuestionTables == null){
            //根据交换机基本信息 查询 可执行命令的 命令信息
            AjaxResult commandIdByInformation_ajaxResult = commandIdByInformation(deviceModel, deviceBrand, firmwareVersion, subversionNumber);

            if (commandIdByInformation_ajaxResult == null){
                return  AjaxResult.success("未定义交换机问题");
            }
            totalQuestionTableList = (List<TotalQuestionTable>) commandIdByInformation_ajaxResult.get("data");

        }else {
            //totalQuestionTables != null 是 专项扫描问题
            // 匹配符合问题
            for (TotalQuestionTable totalQuestionTable:totalQuestionTables){
                String brand = totalQuestionTable.getBrand();
                String type = totalQuestionTable.getType();
                String version = totalQuestionTable.getFirewareVersion();
                String subVersion = totalQuestionTable.getSubVersion();
                if (brand.equals(deviceBrand)
                        && (type.equals(deviceModel) || type.equals("*"))
                        && (version.equals(firmwareVersion) || version.equals("*"))
                        && (subVersion.equals(subversionNumber) || subVersion.equals("*"))){
                    totalQuestionTableList.add(totalQuestionTable);
                }else {

                }
            }
        }

        List<TotalQuestionTable> TotalQuestionTablePojoList = new ArrayList<>();
        Map<String,TotalQuestionTable> totalQuestionTableHashMap = new HashMap<>();

        for (TotalQuestionTable totalQuestionTable:totalQuestionTableList){
            String key =totalQuestionTable.getTypeProblem() + totalQuestionTable.getTemProName();
            TotalQuestionTable pojo = totalQuestionTableHashMap.get(key);
            if (pojo != null){
                int usedNumber = 0;
                if (!(pojo.getType().equals("*"))){
                    usedNumber = usedNumber +1;
                }
                if (!(pojo.getFirewareVersion().equals("*"))){
                    usedNumber = usedNumber +1;
                }
                if (!(pojo.getSubVersion().equals("*"))){
                    usedNumber = usedNumber +1;
                }
                int newNumber = 0;
                if (!(totalQuestionTable.getType().equals("*"))){
                    newNumber = newNumber +1;
                }
                if (!(totalQuestionTable.getFirewareVersion().equals("*"))){
                    newNumber = newNumber +1;
                }
                if (!(totalQuestionTable.getSubVersion().equals("*"))){
                    newNumber = newNumber +1;
                }
                if (usedNumber < newNumber){
                    totalQuestionTableHashMap.put(key,totalQuestionTable);
                }else if (usedNumber == newNumber){
                    String pojotype = pojo.getType();
                    String totalQuestionTabletype = totalQuestionTable.getType();
                    Integer typeinteger = MyUtils.filterAccurately(pojotype, totalQuestionTabletype);
                    if (typeinteger == 1){
                        totalQuestionTableHashMap.put(key,pojo);
                    }else if (typeinteger == 2){
                        totalQuestionTableHashMap.put(key,totalQuestionTable);
                    }else if (typeinteger == 0){
                        String pojofirewareVersion = pojo.getFirewareVersion();
                        String totalQuestionTablefirewareVersion = totalQuestionTable.getFirewareVersion();
                        Integer firewareVersioninteger = MyUtils.filterAccurately(pojofirewareVersion, totalQuestionTablefirewareVersion);
                        if (firewareVersioninteger == 1){
                            totalQuestionTableHashMap.put(key,pojo);
                        }else if (firewareVersioninteger == 2){
                            totalQuestionTableHashMap.put(key,totalQuestionTable);
                        }else if (firewareVersioninteger == 0){
                            String pojosubVersion = pojo.getSubVersion();
                            String totalQuestionTablesubVersion = totalQuestionTable.getSubVersion();
                            Integer subVersioninteger = MyUtils.filterAccurately(pojosubVersion, totalQuestionTablesubVersion);
                            if (subVersioninteger == 1){
                                totalQuestionTableHashMap.put(key,pojo);
                            }else if (subVersioninteger == 2){
                                totalQuestionTableHashMap.put(key,totalQuestionTable);
                            }else if (subVersioninteger == 0){
                                totalQuestionTableHashMap.put(key,totalQuestionTable);
                            }
                        }
                    }
                }else  if (usedNumber > newNumber) {
                    totalQuestionTableHashMap.put(key,pojo);
                }
            }else {
                totalQuestionTableHashMap.put(key,totalQuestionTable);
            }
        }
        Iterator <Map.Entry< String, TotalQuestionTable >> iterator = totalQuestionTableHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry< String, TotalQuestionTable > entry = iterator.next();
            TotalQuestionTablePojoList.add(entry.getValue());
        }


        // todo 连续几个命令然后再执行 分析 可以做吗？
        for (TotalQuestionTable totalQuestionTable:TotalQuestionTablePojoList){
            if (totalQuestionTable.getCommandId().indexOf("命令") != -1){
                // ---- More ----
                user_String.put("notFinished",totalQuestionTable.getNotFinished());
                //根据命令ID获取具体命令，执行
                //返回  交换机返回信息 和  第一条分析ID
                List<Object> executeScanCommandByCommandId_object = executeScanCommandByCommandId(user_String, totalQuestionTable,totalQuestionTable.getCommandId().replace("命令",""),user_String.get("notFinished"),
                        user_String.get("mode"), user_Object);

                if (executeScanCommandByCommandId_object.size() == 1){
                    AjaxResult ajaxResult = (AjaxResult) executeScanCommandByCommandId_object.get(0);
                    if ((ajaxResult.get("msg")+"").indexOf("错误") !=-1){
                        /*进行下一分析*/
                        /*此处不需要错误日志 executeScanCommandByCommandId 方法写全*/
                        continue;
                    }
                }

                //分析
                String analysisReturnResults_String = analysisReturnResults(user_String, user_Object , totalQuestionTable,
                        executeScanCommandByCommandId_object,  "",  "");

                //return AjaxResult.success(analysisReturnResults_String);
            }else if (totalQuestionTable.getCommandId().indexOf("分析") != -1){
                List<Object> executeScanCommandByCommandId_object = new ArrayList<>();
                executeScanCommandByCommandId_object.add("");
                executeScanCommandByCommandId_object.add(totalQuestionTable.getCommandId().replaceAll("分析",""));
                //分析
                String analysisReturnResults_String = analysisReturnResults(user_String, user_Object , totalQuestionTable,
                        executeScanCommandByCommandId_object,  "",  "");

                //return AjaxResult.success(analysisReturnResults_String);
            }
        }
        return null;
    }


    /**
     * @method: 根据交换机信息查询 获取 扫描问题的
     * @Param: []
     * @return: java.util.List<java.lang.Long>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @GetMapping(value = "/commandIdByInformation")
    public AjaxResult commandIdByInformation(String deviceModel,String deviceBrand,String firmwareVersion,String subversionNumber)
    {
        TotalQuestionTable totalQuestionTable = new TotalQuestionTable();
        totalQuestionTable.setBrand(deviceBrand);
        totalQuestionTable.setType(deviceModel);
        totalQuestionTable.setFirewareVersion(firmwareVersion);
        totalQuestionTable.setSubVersion(subversionNumber);

        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);

        //查询可扫描问题
        //List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.queryScannableQuestionsList(totalQuestionTable);
        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.queryVagueScannableQuestionsList(totalQuestionTable);
        if (totalQuestionTables.size() != 0){

            List<TotalQuestionTable> pojoList = new ArrayList<>();

            for (TotalQuestionTable pojo:totalQuestionTables){
                if (pojo.getCommandId()!=null && pojo.getCommandId()!=""){
                    pojoList.add(pojo);
                }
            }

            return AjaxResult.success(pojoList);

        }else {
            return null;
        }
    }

}