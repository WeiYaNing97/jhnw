package com.sgcc.web.controller.sql;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
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
import com.sgcc.web.controller.thread.DirectionalScanThreadPool;
import com.sgcc.web.controller.thread.ScanFixedThreadPool;
import com.sgcc.web.controller.util.EncryptUtil;
import com.sgcc.web.controller.util.RSAUtils;
import com.sgcc.web.controller.webSocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 与交换机交互方法类
 * @date 2022年01月05日 14:18
 */

@RestController
@RequestMapping("/sql/SwitchInteraction")
public class SwitchInteraction {

    @Autowired
    private ICommandLogicService commandLogicService;

    @Autowired
    private IReturnRecordService returnRecordService;

    @Autowired
    private IProblemScanLogicService problemScanLogicService;

    @Autowired
    private IValueInformationService valueInformationService;

    @Autowired
    private ISwitchProblemService switchProblemService;

    @Autowired
    private ITotalQuestionTableService totalQuestionTableService;

    @Autowired
    private IBasicInformationService basicInformationService;


    /***
     * @method: 多线程扫描 获取到的是字符串格式的参数 {"ip":"192.168.1.100","name":"admin","password":"admin","mode":"ssh","port":"22"}
     * @Param: [switchInformation]
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("directionalScann")///{totalQuestionTableId}/{scanNum}
    @MyLog(title = "专项全部问题", businessType = BusinessType.OTHER)
    public String directionalScann() {//@RequestBody List<String> switchInformation,@PathVariable  List<Long> totalQuestionTableId,@PathVariable  Long scanNum
        List<String> switchInformation = new ArrayList<>();
        List<Long> totalQuestionTableId = new ArrayList<>();
        Long scanNum = 1l;
        String str = "{\"ip\":\"192.168.1.100\",\"name\":\"admin\",\"password\":\"admin\",\"mode\":\"ssh\",\"port\":22}";
        Long l1 = 42l;
        Long l2 = 43l;
        switchInformation.add(str);
        totalQuestionTableId.add(l1);
        totalQuestionTableId.add(l2);

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
                    case "password" :  password=string_split[1];
                        break;
                    case "mode" :  mode=string_split[1];
                        break;
                    case "port" :  port= Integer.valueOf(string_split[1]).intValue() ;
                        break;
                }
            }
            //以多线程中的格式 存放数组中
            //连接方式，ip，用户名，密码，端口号
            Object[] objects = {mode,ip,name,password,port};
            objectsList.add(objects);
        }
        List<TotalQuestionTable> totalQuestionTables = new ArrayList<>();
        Long[] ids = totalQuestionTableId.toArray(new Long[totalQuestionTableId.size()]);
        if (ids.length != 0){
            totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);//解决 多线程 service 为null问题
            totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableByIds(ids);
        }else {
            return "扫描完成";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String scanningTime = simpleDateFormat.format(new Date());
        LoginUser login = SecurityUtils.getLoginUser();

        try {
            DirectionalScanThreadPool.switchLoginInformations(objectsList,totalQuestionTables,scanningTime,login,Integer.valueOf(scanNum+"").intValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebSocketService.sendMessage("badao"+login.getUsername(),"\r\n扫描结束\r\n");

        return "扫描完成";
    }


    /*==================================================================================================================
    ====================================================================================================================
    ====================================================================================================================
    ==================================================================================================================*/

    /***
    * @method: 多线程扫描 获取到的是字符串格式的参数 {"ip":"192.168.1.100","name":"admin","password":"admin","mode":"ssh","port":"22"}
    * @Param: [switchInformation]
    * @return: void
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("multipleScans/{scanNum}")
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
                    case "mode" :  mode=string_split[1];
                        break;
                    case "port" :  port= Integer.valueOf(string_split[1]).intValue() ;
                        break;
                }
            }
            //以多线程中的格式 存放数组中
            //连接方式，ip，用户名，密码，端口号
            Object[] objects = {mode,ip,name,password,port};
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

        WebSocketService.sendMessage("badao"+login.getUsername(),"\r\n扫描结束\r\n");

        return "扫描完成";
    }



    /**
    * @method: 扫描方法 logInToGetBasicInformation
    * @Param: [mode, ip, name, password, port] 传参 ：mode连接方式, ip 地址, name 用户名, password 密码, port 端口号
    * @return: com.sgcc.common.core.domain.AjaxResult
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    @RequestMapping("logInToGetBasicInformation")
    public AjaxResult logInToGetBasicInformation(String mode, String ip, String name, String password, int port,LoginUser loginUser,String ScanningTime,List<TotalQuestionTable> totalQuestionTables) {

        //用户信息  及   交换机信息
        Map<String,String> user_String = new HashMap<>();

        //用户信息
        user_String.put("mode",mode);//登录方式
        user_String.put("ip",ip);//ip地址
        user_String.put("name",name);//用户名
        user_String.put("password",password);//用户密码
        user_String.put("port",port+"");//登录端口号
        user_String.put("ScanningTime",ScanningTime);//扫描时间 获取当前时间  时间格式为 "yyyy-MM-dd HH:mm:ss"  字符串


        //交换机信息
        //设备型号
        user_String.put("deviceModel",null);
        //设备品牌
        user_String.put("deviceBrand",null);
        //内部固件版本
        user_String.put("firmwareVersion",null);
        //子版本号
        user_String.put("subversionNumber",null);

        Map<String,Object> user_Object = new HashMap<>();
        //ssh连接
        SshMethod connectMethod = null;
        user_Object.put("connectMethod",connectMethod);
        //telnet连接
        TelnetSwitchMethod telnetSwitchMethod = null;
        user_Object.put("telnetSwitchMethod",telnetSwitchMethod);
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
        元素9 ：telnet连接工具对象*/

        List<Object> objectList = (List<Object>) requestConnect_ajaxResult.get("data");

        //是否连接成功 返回信息集合的 第一项 为 是否连接成功
        boolean requestConnect_boolean = objectList.get(0).toString().equals("true");

        //如果连接成功
        if(requestConnect_boolean){
            String passwordDensificationAndSalt = EncryptUtil.densificationAndSalt(user_String.get("password"));
            user_String.put("password",passwordDensificationAndSalt);//用户密码

            //返回信息集合的 第二项 为 连接方式：ssh 或 telnet
            String requestConnect_way = objectList.get(1).toString();
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
                connectMethod = (SshMethod)objectList.get(6);
                sshConnect = (SshConnect)objectList.get(8);
                user_Object.put("connectMethod",connectMethod);
                user_Object.put("sshConnect",sshConnect);
            }else if (requestConnect_way.equalsIgnoreCase("telnet")){
                telnetSwitchMethod = (TelnetSwitchMethod)objectList.get(7);
                telnetComponent = (TelnetComponent)objectList.get(9);
                user_Object.put("telnetSwitchMethod",telnetSwitchMethod);
                user_Object.put("telnetComponent",telnetComponent);
            }

            //获取交换机基本信息
            //getBasicInformationList 通过 特定方式 获取 基本信息
            //getBasicInformationList 通过扫描方式 获取 基本信息
            AjaxResult basicInformationList_ajaxResult = getBasicInformationList(user_String,user_Object);   //getBasicInformationList

            if (!(basicInformationList_ajaxResult.get("msg").equals("未定义该交换机获取基本信息命令及分析"))){

                //获取 匹配的 交换机可执行的 命令ID  并 循环执行分析操作
                AjaxResult ajaxResult = scanProblem(
                        user_String, //登录交换机的 用户信息 登录方式、ip、name、password
                        user_Object,
                         totalQuestionTables);

                if (requestConnect_way.equalsIgnoreCase("ssh")){
                    connectMethod.closeConnect(sshConnect);
                }else if (requestConnect_way.equalsIgnoreCase("telnet")){
                    telnetSwitchMethod.closeSession(telnetComponent);
                }
                return basicInformationList_ajaxResult;

            }
            return AjaxResult.error("未定义该交换机获取基本信息命令及分析！");
        }
        return AjaxResult.error("连接交换机失败！");
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
    @RequestMapping("requestConnect")
    //@MyLog(title = "连接交换机", businessType = BusinessType.OTHER)
    public static AjaxResult requestConnect(Map<String,String> user_String) {
        //用户信息
        String way = user_String.get("mode");//连接方法
        String hostIp = user_String.get("ip") ;//ip地址
        int portID = Integer.valueOf(user_String.get("port")).intValue() ;//端口号
        String userName = user_String.get("name") ;//姓名
        String userPassword = user_String.get("password") ;//密码
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

        List<Object> objectList = new ArrayList<>();  //设定返回值 list集合
        objectList.add(is_the_connection_successful); //元素0 ：是否连接成功
        objectList.add(way);                          //元素1 ：连接方法
        objectList.add(hostIp);                       //元素2 ：交换机ID
        objectList.add(userName);                     //元素3 ：交换机登录用户
        objectList.add(userPassword);                 //元素4 ：交换机登录用户密码
        objectList.add(portID);                       //元素5 ：交换机连接端口号
        objectList.add(connectMethod);                //元素6 ：ssh连接对象：如果连接方法为telnet则connectMethod为空，插入connectMethod失败
        objectList.add(telnetSwitchMethod);           //元素7 ：telnet连接对象：如果连接方法为ssh则telnetSwitchMethod为空，插入telnetSwitchMethod失败
        objectList.add(sshConnect);                   //元素8 ：ssh连接工具对象
        objectList.add(telnetComponent);                 //元素9 ：telnet连接工具对象
        /* 返回信息 ： [是否连接成功,mode 连接方式, ip IP地址, name 用户名, password 密码, port 端口号,
                connectMethod ssh连接方法 或者 telnetSwitchMethod telnet连接方法（其中一个，为空者不存在）]*/
        if(is_the_connection_successful){
            return AjaxResult.success(objectList);
        }else {

            return AjaxResult.error("交换机连接失败");

        }
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
    public AjaxResult getBasicInformationList(Map<String,String> user_String,Map<String,Object> user_Object) {
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

        //遍历命令表命令 执行命令
        for (BasicInformation basicInformation:basicInformationList){
            //basicInformation : display device manuinfo,display ver
            //连接方式 ssh telnet
            String way = user_String.get("mode");
            //目前获取基本信息命令是多个命令是由,号分割的，
            // 所以需要根据, 来分割。例如：display device manuinfo,display ver
            String[] commandsplit = basicInformation.getCommand().split(",");

            String commandString =""; //预设交换机返回结果
            String return_sum = ""; //当前命令字符串总和 返回命令总和("\r\n"分隔)

            //遍历数据表命令 分割得到的 命令数组
            for (String command:commandsplit){

                //创建 存储交换机返回数据 实体类
                ReturnRecord returnRecord = new ReturnRecord();
                int insert_Int = 0;
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
                        WebSocketService.sendMessage("badao"+userName,command);
                        commandString = connectMethod.sendCommand(user_String.get("ip"),sshConnect,command,user_String.get("notFinished"));
                    }else if (way.equalsIgnoreCase("telnet")){
                        WebSocketService.sendMessage("badao"+userName,command);
                        commandString = telnetSwitchMethod.sendCommand(user_String.get("ip"),telnetComponent,command,user_String.get("notFinished"));
                    }

                    returnRecord.setCurrentReturnLog(commandString);

                    //粗略查看是否存在 故障 存在故障返回 false 不存在故障返回 true
                    boolean switchfailure = Utils.switchfailure(user_String.get("deviceBrand"), commandString);
                    // 存在故障返回 false
                    if (!switchfailure){

                        String[] commandStringSplit = commandString.split("\r\n");

                        for (String returnString:commandStringSplit){
                            deviceBrand = Utils.switchfailure(user_String.get("deviceBrand"), returnString);
                            if (!deviceBrand){
                                System.err.println("\r\n"+user_String.get("ip") + "\r\n故障:"+returnString+"\r\n");
                                WebSocketService.sendMessage("error"+userName,"\r\n"+user_String.get("ip") + "\r\n故障:"+returnString+"\r\n");
                                returnRecord.setCurrentIdentifier(user_String.get("ip") + "出现故障:"+returnString+"\r\n");

                                if (way.equalsIgnoreCase("ssh")){
                                    connectMethod.sendCommand(user_String.get("ip"),sshConnect," ",user_String.get("notFinished"));
                                }else if (way.equalsIgnoreCase("telnet")){
                                    telnetSwitchMethod.sendCommand(user_String.get("ip"),telnetComponent," ",user_String.get("notFinished"));
                                }

                                break;
                            }
                        }

                    }

                    returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
                    insert_Int = returnRecordService.insertReturnRecord(returnRecord);

                }while (!deviceBrand);

                returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
                returnRecord = returnRecordService.selectReturnRecordById(Integer.valueOf(insert_Int).longValue());

                //去除其他 交换机登录信息
                commandString = Utils.removeLoginInformation(commandString);

                //交换机返回信息 修整字符串  去除多余 "\r\n" 连续空格 为插入数据美观
                commandString = Utils.trimString(commandString);

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

                WebSocketService.sendMessage("badao"+userName,current_return_log);
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

                WebSocketService.sendMessage("badao"+userName,current_identifier);

                //存储交换机返回数据 插入数据库
                returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);//解决 多线程 service 为null问题
                int update = returnRecordService.updateReturnRecord(returnRecord);

                //判断命令是否错误 错误为false 正确为true
                if (!Utils.judgmentError(commandString)){
                    //如果返回信息错误 则结束当前命令，执行 遍历数据库下一条命令字符串(,)

                    String[] returnString_split = commandString.split("\r\n");
                    for (String string_split:returnString_split){
                        if (!Utils.judgmentError(string_split)){
                            System.err.println("\r\n"+user_String.get("ip")+ ":" +command+ "错误:"+string_split+"\r\n");
                            WebSocketService.sendMessage("error"+userName,"\r\n"+user_String.get("ip")+ ":" +command+ "错误:"+string_split+"\r\n");
                            break;
                        }
                    }

                    break;
                }

                //当前命令字符串 返回命令总和("\r\n"分隔)
                return_sum += commandString+"\r\n\r\n";
            }
            //修整 当前命令字符串 返回信息  去除多余 "\r\n" 连续空格
            //应该可以去除 因为 上面 每个单独命令已经执行过
            // 注释掉 可能会在两条交换机返回信息中 存在 "\r\n\r\n" 情况 按"\r\n"分割可能会出现空白元素
            //String command_String = Utils.trimString(return_sum);

            //分析第一条ID basicInformation.getProblemId() (为 问题扫描逻辑表  ID)
            String first_problem_scanLogic_Id = basicInformation.getProblemId();
            // 获取交换机 基本信息命令 列表 根据分析ID获取问题扫描逻辑详细信息
            //进行分析 返回总提取信息
            String extractInformation_string1 = analysisReturn(user_String, user_Object ,null,
                    return_sum, first_problem_scanLogic_Id);

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
            extractInformation_string1 = extractInformation_string1.replace(",","");
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
                    List<String> stringList = new ArrayList<>();
                    stringList.add(deviceBrand);
                    stringList.add(deviceModel);
                    stringList.add(firmwareVersion);
                    stringList.add(subversionNumber);
                    /*return AjaxResult.success(stringList);*/

                    WebSocketService.sendMessage("basicinformation"+userName,stringList);

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

                    return AjaxResult.success(map);
                }
            }
        }

        return AjaxResult.error("未定义该交换机获取基本信息命令及分析");

    }


    /**
     * @method: 根据命令表 获取交换机基本信息  多个命令非依次执行
     * @Param: [user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法,
     * SshConnect ssh连接工具，connectMethod ssh连接,
     * TelnetComponent Telnet连接工具，telnetSwitchMethod telnet连接]
     * @E-mail: WeiYaNing97@163.com
     * 通过null 查询初所有会的交换机基本信息的命令字符串集合
     * 遍历交换机基本信息的命令字符串集合 通过 扫描分析方法 获得所有提取信息
     * 通过返回提取的信息，给基本属性赋值
     * 成功则返回基本信息 否则 遍历下一条 交换机基本信息的命令字符串集合信息
     */
    //@MyLog(title = "获取交换机基本信息", businessType = BusinessType.OTHER)
    public AjaxResult getBasicInformation(Map<String,String> user_String,Map<String,Object> user_Object) {

        SshConnect sshConnect = (SshConnect) user_Object.get("sshConnect");
        SshMethod connectMethod = (SshMethod) user_Object.get("connectMethod");
        TelnetComponent telnetComponent = (TelnetComponent) user_Object.get("telnetComponent");
        TelnetSwitchMethod telnetSwitchMethod = (TelnetSwitchMethod) user_Object.get("telnetSwitchMethod");

        LoginUser loginUser = (LoginUser)user_Object.get("loginUser");
        String userName = loginUser.getUsername();

        //查询 获取基本信息命令表  中的全部命令
        //BasicInformation pojo_NULL = new BasicInformation();
        //null
        //根据 null 查询 得到表中所有数据
        totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);//解决 多线程 service 为null问题
        String problemName = "获取基本信息";
        TotalQuestionTable totalQuestionTable = new TotalQuestionTable();
        totalQuestionTable.setProblemName(problemName);
        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableList(totalQuestionTable);

        String command_return_information = null;
        Long analysis_id = null;
        for (TotalQuestionTable pojo:totalQuestionTables){
            user_String.put("notFinished",pojo.getNotFinished());

            List<Object> executeScanCommandByCommandId_object = executeScanCommandByCommandId(user_String,pojo.getCommandId(),user_String.get("notFinished"),
                    user_String.get("mode"), user_Object);

            String analysisReturnResults_String = analysisReturnResults(user_String, user_Object ,pojo,
                    executeScanCommandByCommandId_object,"", "");
            System.err.print("\r\nanalysisReturnResults_String:\r\n"+analysisReturnResults_String);


            if (analysisReturnResults_String.equals("") || analysisReturnResults_String == null){
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
            analysisReturnResults_String = analysisReturnResults_String.replace(",","");
            String[] return_result_split = analysisReturnResults_String.split("=:=");
            for (int num = 0;num<return_result_split.length;num++){
                //设备型号
                if (return_result_split[num].equals("设备型号")){
                    num = num + 2;
                    deviceModel=return_result_split[num];
                }
                //设备品牌
                if (return_result_split[num].equals("设备品牌")) {
                    num = num + 2;
                    deviceBrand = return_result_split[num];
                }
                //内部固件版本
                if (return_result_split[num].equals("内部固件版本")) {
                    num = num + 2;
                    firmwareVersion = return_result_split[num];
                }
                //子版本号
                if (return_result_split[num].equals("子版本号")) {
                    num = num + 2;
                    subversionNumber = return_result_split[num];
                }
                if (!deviceModel.equals("") && !deviceBrand.equals("") && !firmwareVersion.equals("") && !subversionNumber.equals("")){
                    // 根据交换机信息查询 获取 扫描问题的 命令ID
                    List<String> stringList = new ArrayList<>();
                    stringList.add(deviceBrand);
                    stringList.add(deviceModel);
                    stringList.add(firmwareVersion);
                    stringList.add(subversionNumber);
                    /*return AjaxResult.success(stringList);*/

                    WebSocketService.sendMessage("basicinformation"+userName,stringList);

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

                    return AjaxResult.success(map);
                }
            }
        }

        return AjaxResult.error("未定义该交换机获取基本信息命令及分析");
    }



    /**
     * @method: 获取交换机 基本信息命令 列表 根据分析ID获取问题扫描逻辑详细信息
     * @Param: [user_String 用户信息【连接方式、ip地址、用户名、密码】, way 连接方法, connectMethod ssh连接, telnetSwitchMethod telnet连接]
     * @Param: [resultString 交换机返回信息,first_problem_scanLogic_Id 第一条分析ID,firmwareVersion 内部固件版本号)
     * @return: void
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    public String analysisReturn(Map<String,String> user_String,Map<String,Object> user_Object,TotalQuestionTable totalQuestionTable,
                                 String resultString,String first_problem_scanLogic_Id){

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
        String strings = selectProblemScanLogicById(user_String, user_Object, totalQuestionTable,
                return_information_array, "", "",
                0, first_problem_scanLogic_Id, null,0);// loop end
        //控制台 输出  交换机 基本信息
        System.err.print("\r\n基本信息："+strings+"\r\n");
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
    public String selectProblemScanLogicById(Map<String,String> user_String,
                                             Map<String,Object> user_Object,
                                             TotalQuestionTable totalQuestionTable,
                                             String[] return_information_array, String current_Round_Extraction_String, String extractInformation_string,
                                             int line_n, String firstID, String currentID,
                                             Integer insertsInteger) {

        //第一条分析ID
        String id = firstID; //用于第一次分析 和  循环分析
        //如果当前分析ID  currentID  不为空，则用当前分析ID
        if (currentID != null){
            id = currentID;
        }

        //控制台输出 分析表 分析ID
        System.err.print("\r\n执行分析ID:\r\n"+id+"\r\n");

        //根据ID查询分析数据
        //根据第一条分析ID 查询分析信息
        problemScanLogicService = SpringBeanUtil.getBean(IProblemScanLogicService.class);
        ProblemScanLogic problemScanLogic = problemScanLogicService.selectProblemScanLogicById(id);

        //如果循环ID不为空的话 说明 分析数据为循环分析 则 需要调出循环ID 当做第一分析ID 当前分析ID 为空 继续执行
        //循环分析数据 不需要分析 功能指向循环位置
        if (problemScanLogic.getCycleStartId()!=null && !(problemScanLogic.getCycleStartId().equals("null"))){
            //调出循环ID 当做第一分析ID
            firstID = problemScanLogic.getCycleStartId();
            String loop_string = selectProblemScanLogicById(user_String,user_Object,totalQuestionTable,
                    return_information_array,"",extractInformation_string,
                    line_n,firstID,null,insertsInteger);
            return loop_string;
        }

        //如果 问题索引字段 不为空 null 则 说明  分析数据 是 分析出问题或者可以结束了
        // problemScanLogic.getProblemId() 可以为 有问题(前端显示:异常) 无问题(前端显示:安全) 完成
        if (problemScanLogic.getProblemId()!=null){
            //有问题 无问题
            if (problemScanLogic.getProblemId().indexOf("问题")!=-1){

                try {
                    Thread.sleep(1000*3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //问题数据 插入问题表 如果有参数 及插入
                insertvalueInformationService(user_String,user_Object, totalQuestionTable,problemScanLogic,current_Round_Extraction_String);
                //插入问题数据次数 加一
                insertsInteger++;
                current_Round_Extraction_String = "";
                //获取扫描出问题数据列表  集合 并放入 websocket
                //根据 用户信息 和 扫描时间

                getUnresolvedProblemInformationByData(user_String,user_Object);

                firstID = problemScanLogic.gettNextId();

                String loop_string = selectProblemScanLogicById(user_String,user_Object,totalQuestionTable,
                        return_information_array,"",extractInformation_string,
                        line_n,firstID,null,insertsInteger);

                return loop_string;

            }

            // 分析执行 完成
            if (problemScanLogic.getProblemId().indexOf("完成")!=-1){
                return extractInformation_string;
            }

        }

        //相对位置——行,列(1,0)
        String relativePosition = problemScanLogic.getRelativePosition();
        //相对位置行
        String relativePosition_line ="";
        //相对位置列
        String relativePosition_row ="";
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
        }

        //标定从第line_n开始扫描
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

        //从line_n=0 开始检索集合 一直到最后一位
        for (int num = line_n;num<return_information_array.length; num++){

            //匹配逻辑
            String matched = null;
            if (problemScanLogic.getMatched()!=null){
                matched = problemScanLogic.getMatched();
            }
            //取词逻辑
            String action = problemScanLogic.getAction();
            //比较分析
            String compare = problemScanLogic.getCompare();

            //光标位置
            line_n = num;
            //返回信息的数组元素 第num 条
            String information_line_n = return_information_array[num];

            //匹配逻辑 有成功失败之分
            if (matched != null){
                //根据匹配方法 得到是否匹配（成功:true 失败:false）
                //matched : 精确匹配  information_line_n：交换机返回信息行  matchContent：数据库 关键词
                boolean matchAnalysis_true_false = Utils.matchAnalysis(matched, information_line_n, matchContent);

                //如果最终逻辑成功 则把 匹配成功的行数 付给变量 line_n
                if (matchAnalysis_true_false){

                    if (problemScanLogic.gettComId()!=null && problemScanLogic.gettComId()!=""){
                        List<Object> executeScanCommandByCommandId_object = executeScanCommandByCommandId(user_String,problemScanLogic.gettComId(),user_String.get("notFinished"),
                                user_String.get("mode"), user_Object);
                        String analysisReturnResults_String = analysisReturnResults(user_String, user_Object,totalQuestionTable,
                                executeScanCommandByCommandId_object,current_Round_Extraction_String, extractInformation_string);
                        return analysisReturnResults_String;
                    }

                    //下一条true分析ID
                    if (problemScanLogic.gettNextId()!=null && problemScanLogic.gettNextId()!=""){
                        String tNextId = problemScanLogic.gettNextId();
                        String ProblemScanLogic_returnstring = selectProblemScanLogicById(user_String,user_Object,totalQuestionTable,
                                return_information_array,current_Round_Extraction_String,extractInformation_string,
                                line_n,firstID,tNextId,insertsInteger);
                        //如果返回信息为null
                        if (ProblemScanLogic_returnstring!=null){
                            //内分析传到上一层
                            //extractInformation_string 是 分析的 总提取信息记录 所以要把内层的记录 传给 外层
                            extractInformation_string = ProblemScanLogic_returnstring;
                            return ProblemScanLogic_returnstring;
                        }
                        return ProblemScanLogic_returnstring;
                    }

                    //匹配失败
                }else {

                    //relativePosition.equals("null") 全文检索
                    // 如果不是最后一条信息 并且 全文检索的话  则返回到循环 返回信息数组 的下一条
                    if (relativePosition.equals("null") && num<return_information_array.length-1){
                        continue;
                    }

                    if (problemScanLogic.getfComId()!=null && problemScanLogic.getfComId()!=""){
                        List<Object> executeScanCommandByCommandId_object = executeScanCommandByCommandId(user_String,problemScanLogic.getfComId(),user_String.get("notFinished"), user_String.get("mode"), user_Object);
                        String analysisReturnResults_String = analysisReturnResults(user_String, user_Object,totalQuestionTable,
                                executeScanCommandByCommandId_object,  current_Round_Extraction_String,  extractInformation_string);
                        return analysisReturnResults_String;
                    }

                    if (problemScanLogic.getfNextId()!=null && problemScanLogic.getfNextId()!=null){
                        //下一条frue分析ID
                        String fNextId = problemScanLogic.getfNextId();
                        String ProblemScanLogic_returnstring = selectProblemScanLogicById(user_String,user_Object,totalQuestionTable,
                                return_information_array,current_Round_Extraction_String,extractInformation_string,
                                line_n,firstID,fNextId,insertsInteger);
                        //如果返回信息为null
                        if (ProblemScanLogic_returnstring!=null){
                            //内分析传到上一层
                            //extractInformation_string 是 分析的 总提取信息记录 所以要把内层的记录 传给 外层
                            extractInformation_string = ProblemScanLogic_returnstring;
                            return ProblemScanLogic_returnstring;
                        }
                        return ProblemScanLogic_returnstring;
                    }
                }
            }

            //取词
            if (action!=null && !action.equals("null")){

                //取词数
                String wordSelection_string = Utils.wordSelection(
                        return_information_array[num], matchContent, //返回信息的一行 提取关键字
                        problemScanLogic.getrPosition(), problemScanLogic.getLength()); //位置 长度WLs
                //取词逻辑只有成功，但是如果取出为空 则为 取词失败
                if (wordSelection_string == null){
                    return "取词失败！";
                }

                extractInformation_string = extractInformation_string +problemScanLogic.getWordName()+"=:="+problemScanLogic.getExhibit()+"=:="+ wordSelection_string+"=:=";
                current_Round_Extraction_String = current_Round_Extraction_String +problemScanLogic.getWordName()+"=:="+problemScanLogic.getExhibit()+"=:="+ wordSelection_string+"=:=";

                if (problemScanLogic.gettComId()!=null && problemScanLogic.gettComId()!=""){
                    List<Object> executeScanCommandByCommandId_object = executeScanCommandByCommandId(user_String,problemScanLogic.gettComId(),user_String.get("notFinished"), user_String.get("mode"),user_Object);
                    String analysisReturnResults_String = analysisReturnResults(user_String,user_Object,totalQuestionTable,
                            executeScanCommandByCommandId_object,  current_Round_Extraction_String,  extractInformation_string);
                    return analysisReturnResults_String;
                }

                if (problemScanLogic.gettNextId()!=null && problemScanLogic.gettNextId()!=""){
                    //下一ID
                    String tNextId = problemScanLogic.gettNextId();
                    String ProblemScanLogic_returnstring = selectProblemScanLogicById(user_String,user_Object,totalQuestionTable,
                            return_information_array,current_Round_Extraction_String,extractInformation_string,
                            line_n,firstID,tNextId,insertsInteger);
                    if (ProblemScanLogic_returnstring!=null){
                        extractInformation_string = ProblemScanLogic_returnstring;
                        return ProblemScanLogic_returnstring;
                    }
                    return ProblemScanLogic_returnstring;
                }
            }


            //比较
            if (compare!=null){

                //比较
                boolean compare_boolean = Utils.compareVersion(user_String,compare);

                if (compare_boolean){

                    if (problemScanLogic.gettComId()!=null && problemScanLogic.gettComId()!=""){
                        List<Object> executeScanCommandByCommandId_object = executeScanCommandByCommandId(user_String,problemScanLogic.gettComId(),user_String.get("notFinished"), user_String.get("mode"),user_Object);
                        String analysisReturnResults_String = analysisReturnResults(user_String,user_Object,totalQuestionTable,
                                executeScanCommandByCommandId_object,  current_Round_Extraction_String,  extractInformation_string);
                        return analysisReturnResults_String;
                    }

                    if (problemScanLogic.gettNextId()!=null && problemScanLogic.gettNextId()!=""){
                        String tNextId = problemScanLogic.gettNextId();
                        String ProblemScanLogic_returnstring = selectProblemScanLogicById(user_String,user_Object,totalQuestionTable,
                                return_information_array,current_Round_Extraction_String,extractInformation_string,
                                line_n,firstID,tNextId,insertsInteger);

                        if (ProblemScanLogic_returnstring!=null){
                            extractInformation_string = ProblemScanLogic_returnstring;
                            return ProblemScanLogic_returnstring;
                        }

                        return ProblemScanLogic_returnstring;
                    }

                }else {

                    if (problemScanLogic.getfComId()!=null && problemScanLogic.getfComId()!=""){
                        List<Object> executeScanCommandByCommandId_object = executeScanCommandByCommandId(user_String,problemScanLogic.getfComId(),user_String.get("notFinished"), user_String.get("mode"),user_Object);
                        String analysisReturnResults_String = analysisReturnResults(user_String,user_Object,totalQuestionTable,
                                executeScanCommandByCommandId_object,  current_Round_Extraction_String,  extractInformation_string);

                        return analysisReturnResults_String;

                    }

                    if (problemScanLogic.getfNextId()!=null && problemScanLogic.getfNextId()!=""){
                        String fNextId = problemScanLogic.getfNextId();
                        String ProblemScanLogic_returnstring = selectProblemScanLogicById(user_String,user_Object,totalQuestionTable,
                                return_information_array,current_Round_Extraction_String,extractInformation_string,
                                line_n,firstID,fNextId,insertsInteger);

                        if (ProblemScanLogic_returnstring!=null){
                            extractInformation_string = ProblemScanLogic_returnstring;
                            return ProblemScanLogic_returnstring;
                        }

                        return ProblemScanLogic_returnstring;
                    }

                }
            }
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
    public void insertvalueInformationService(Map<String,String> user_String,Map<String,Object> user_Object,
                                              TotalQuestionTable totalQuestionTable,
                                              ProblemScanLogic problemScanLogic,
                                              String parameterString){

        //系统登录人 用户名
        LoginUser loginUser = (LoginUser)user_Object.get("loginUser");
        String userName = loginUser.getUsername();
        //系统登录人 手机号
        String phonenumber = loginUser.getUser().getPhonenumber();
        //截取 有问题 还是 无问题
        String substring = problemScanLogic.getProblemId().substring(0, 3);
        //截取 问题代码
        String problemId = problemScanLogic.getProblemId().substring(3, problemScanLogic.getProblemId().length());

        //参数组中的 第一个参数ID  默认为 0
        Long outId = 0l;

        //提取信息 如果不为空 则有参数
        if (parameterString!=null && !parameterString.equals("")){

            //几个参数中间的 参数是 以  "=:=" 来分割的
            //设备型号=:=是=:=S3600-28P-EI=:=设备品牌=:=是=:=H3C=:=内部固件版本=:=是=:=3.10,=:=子版本号=:=是=:=1510P09=:=
            String[] parameterStringsplit = parameterString.split("=:=");

            //判断提取参数 是否为空
            if (parameterStringsplit.length>0){
                //创建 参数 实体类
                ValueInformation valueInformation = new ValueInformation();
                //考虑到 需要获取 参数 的ID 所以要从参数组中获取第一个参数的 ID
                //所以 参数组 要倒序插入
                for (int number=parameterStringsplit.length-1;number>0;number--){
                    //插入参数
                    //用户名=:=是=:=admin=:=密码=:=否=:=$c$3$ucuLP5tRIUiNMSGST3PKZPvR0Z0bw2/g=:=
                    String setDynamicInformation=parameterStringsplit[number];
                    valueInformation.setDisplayInformation(setDynamicInformation);//动态信息(显示
                    valueInformation.setDynamicInformation(setDynamicInformation);//动态信息
                    --number;
                    String setExhibit=parameterStringsplit[number];
                    valueInformation.setExhibit(setExhibit);//是否显示

                    if (setExhibit.equals("否")){
                        String setDynamicInformationMD5 = EncryptUtil.densificationAndSalt(setDynamicInformation);
                        valueInformation.setDynamicInformation(setDynamicInformationMD5);//动态信息
                    }

                    --number;
                    valueInformation.setDynamicVname(parameterStringsplit[number]);//动态信息名称
                    valueInformation.setOutId(outId);

                    //参数插入
                    valueInformationService = SpringBeanUtil.getBean(IValueInformationService.class);
                    valueInformationService.insertValueInformation(valueInformation);
                    outId = valueInformation.getId();
                }
            }
        }
        //插入问题数据
        SwitchProblem switchProblem = new SwitchProblem();
        switchProblem.setSwitchIp(user_String.get("ip")); // ip
        switchProblem.setSwitchName(user_String.get("name")); //name
        switchProblem.setSwitchPassword(user_String.get("password")); //password

        switchProblem.setLoginMethod(user_String.get("mode"));
        switchProblem.setPortNumber(user_String.get("port"));

        switchProblem.setProblemId(problemId); // 问题索引
        switchProblem.setComId(problemScanLogic.gettComId());//命令索引
        switchProblem.setValueId(outId);//参数索引
        switchProblem.setIfQuestion(substring); //是否有问题
        switchProblem.setComId(totalQuestionTable.getProblemSolvingId());
        switchProblem.setUserName(userName);//参数索引
        switchProblem.setPhonenumber(phonenumber); //是否有问题
        //插入 扫描时间
        String loginTime = user_String.get("ScanningTime");
        DateTime dateTime = new DateTime(loginTime, "yyyy-MM-dd HH:mm:ss");
        switchProblem.setCreateTime(dateTime);

        //插入问题
        switchProblemService = SpringBeanUtil.getBean(ISwitchProblemService.class);
        switchProblemService.insertSwitchProblem(switchProblem);
    };


    /**
     * @method: 查询扫描出的问题表 放入 websocket
     * @Param: []
     * @return: java.util.List<com.sgcc.sql.domain.SwitchProblem>
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @RequestMapping("getUnresolvedProblemInformationByData")
    public List<ScanResultsVO> getUnresolvedProblemInformationByData(Map<String,String> user_String,Map<String,Object> user_Object){

        //用户名
        LoginUser loginUser = (LoginUser)user_Object.get("loginUser");
        String loginName = loginUser.getUsername();
        //扫描时间
        String loginTime = user_String.get("ScanningTime");
        switchProblemService = SpringBeanUtil.getBean(ISwitchProblemService.class);
        List<SwitchProblemVO> switchProblemList = switchProblemService.selectUnresolvedProblemInformationByDataAndUserName(loginTime,loginName);

        for (SwitchProblemVO switchProblemVO:switchProblemList){
            Date date1 = new Date();
            switchProblemVO.hproblemId =  Long.valueOf(Utils.getTimestamp(date1)+""+ (int)(Math.random()*10000+1)).longValue();
            List<SwitchProblemCO> switchProblemCOList = switchProblemVO.getSwitchProblemCOList();
            for (SwitchProblemCO switchProblemCO:switchProblemCOList){
                valueInformationService = SpringBeanUtil.getBean(IValueInformationService.class);//解决 多线程 service 为null问题
                List<ValueInformationVO> valueInformationVOList = valueInformationService.selectValueInformationVOListByID(switchProblemCO.getValueId());
                for (ValueInformationVO valueInformationVO:valueInformationVOList){
                    Date date2 = new Date();
                    valueInformationVO.hproblemId = Long.valueOf(Utils.getTimestamp(date2)+""+ (int)(Math.random()*10000+1)).longValue();
                }

                Date date3 = new Date();
                switchProblemCO.hproblemId = Long.valueOf(Utils.getTimestamp(date3)+""+ (int)(Math.random()*10000+1)).longValue();
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
            scanResultsVO.hproblemId = Long.valueOf(Utils.getTimestamp(date4)+""+ (int)(Math.random()*10000+1)).longValue();
            scanResultsVOList.add(scanResultsVO);
        }

        for (ScanResultsVO scanResultsVO:scanResultsVOList){
            List<SwitchProblemVO> switchProblemVOList = new ArrayList<>();
            for (SwitchProblemVO switchProblemVO:switchProblemList){
                if (switchProblemVO.getSwitchIp() == scanResultsVO.getSwitchIp()){
                    switchProblemVO.setSwitchIp(null);
                    switchProblemVOList.add(switchProblemVO);
                }
            }
            scanResultsVO.setSwitchProblemVOList(switchProblemVOList);
        }

        WebSocketService.sendMessage("loophole"+loginName,scanResultsVOList);

        return scanResultsVOList;
    }



    /**
     * @method: 根据命令ID获取具体命令，执行
     * @Param:
     * @return:  返回的是 解决问题ID
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     * 分析ID 连接方式 ssh和telnet连接
     */
    @RequestMapping("/executeScanCommandByCommandId")
    public List<Object> executeScanCommandByCommandId(Map<String,String> user_String,String commandId,String notFinished,String way,Map<String,Object> user_Object) {
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

        //具体命令
        String command = commandLogic.getCommand();
        System.err.print("\r\n执行命令：\r\n"+command+"\r\n");
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
                WebSocketService.sendMessage("badao" + userName, command);
                command_string = connectMethod.sendCommand(user_String.get("ip"), sshConnect, command, notFinished);
                //command_string = Utils.removeLoginInformation(command_string);
            } else if (way.equalsIgnoreCase("telnet")) {
                WebSocketService.sendMessage("badao" + userName, command);
                command_string = telnetSwitchMethod.sendCommand(user_String.get("ip"), telnetComponent, command, notFinished);
                //command_string = Utils.removeLoginInformation(command_string);
            }

            returnRecord.setCurrentReturnLog(command_string);

            //粗略查看是否存在 故障 存在故障返回 false 不存在故障返回 true
            boolean switchfailure = Utils.switchfailure(user_String.get("deviceBrand"), command_string);
            // 存在故障返回 false
            if (!switchfailure) {

                String[] commandStringSplit = command_string.split("\r\n");

                for (String returnString : commandStringSplit) {
                    deviceBrand = Utils.switchfailure(user_String.get("deviceBrand"), returnString);
                    if (!deviceBrand) {
                        System.err.println("\r\n"+user_String.get("ip") + "故障:"+returnString+"\r\n");
                        WebSocketService.sendMessage("error"+userName,"\r\n"+user_String.get("ip") + "故障:"+returnString+"\r\n");
                        returnRecord.setCurrentIdentifier(user_String.get("ip") + "出现故障:"+returnString+"\r\n");

                        if (way.equalsIgnoreCase("ssh")){
                            connectMethod.sendCommand(user_String.get("ip"),sshConnect," ",user_String.get("notFinished"));
                        }else if (way.equalsIgnoreCase("telnet")){
                            telnetSwitchMethod.sendCommand(user_String.get("ip"),telnetComponent," ",user_String.get("notFinished"));
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
        command_string = Utils.removeLoginInformation(command_string);
        //修整返回信息
        command_string =Utils.trimString(command_string);

        command_string = command_string.replaceAll(user_String.get("notFinished"),"");

        //按行切割
        String[] split = command_string.split("\r\n");


        String current_return_log = "";
        if (split.length != 1){
            current_return_log =command_string.substring(0,command_string.length()-split[split.length-1].length()-2).trim();
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

        WebSocketService.sendMessage("badao"+userName,current_return_log);

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

        WebSocketService.sendMessage("badao"+userName,current_identifier);

        //返回信息表，返回插入条数
        returnRecordService = SpringBeanUtil.getBean(IReturnRecordService.class);
        int update = returnRecordService.updateReturnRecord(returnRecord);
        //判断是否简单检验 1L为简单校验  默认0L 为分析数据表自定义校验
        String first_problem_scanLogic_Id = "";
        if (commandLogic.getResultCheckId().equals("1")){
            //判断命令是否错误 错误为false 正确为true
            if (Utils.judgmentError(command_string)){
                //  简单检验，命令正确，新命令  commandLogic.getEndIndex()

                String[] returnString_split = command_string.split("\r\n");
                for (String string_split:returnString_split){
                    if (!Utils.judgmentError(string_split)){
                        System.err.println("\r\n"+user_String.get("ip")+ ":" +command+ "错误:"+string_split+"\r\n");
                        WebSocketService.sendMessage("error"+userName,"\r\n"+user_String.get("ip")+ ":" +command+ "错误:"+string_split+"\r\n");
                        List<Object> objectList = new ArrayList<>();
                        objectList.add(user_String.get("ip")+ ":" +command+ "错误:"+string_split);
                        return objectList;
                    }
                }

                List<Object> objectList = executeScanCommandByCommandId(user_String,commandLogic.getEndIndex(),notFinished, way,user_Object);
                return objectList;
            }
        }else {
            //分析第一条ID
            first_problem_scanLogic_Id = commandLogic.getProblemId();
        }

        List<Object> objectList = new ArrayList<>();
        objectList.add(command_string);//交换机返回信息
        System.err.println("交换机返回信息:\r\n"+command_string);
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
    @RequestMapping("analysisReturnResults")
    public String analysisReturnResults(Map<String,String> user_String,
                                        Map<String,Object> user_Object,
                                        TotalQuestionTable totalQuestionTable,
                                        List<Object> executeScanCommandByCommandId_object,String current_Round_Extraction_String,String extractInformation_string){

        String resultString = executeScanCommandByCommandId_object.get(0).toString();//交换机返回信息
        String first_problem_scanLogic_Id = executeScanCommandByCommandId_object.get(1)+"";//分析第一条ID

        //整理返回结果 去除 #
        resultString = resultString.replace("\r\n"+" # "+"\r\n","\r\n");

        //将交换机返回信息 按行来切割 字符串数组
        String[] return_information_array =resultString.split("\r\n");

        //根据ID去分析
        String problemScanLogic_string = selectProblemScanLogicById( user_String,user_Object, totalQuestionTable,
                return_information_array,current_Round_Extraction_String,extractInformation_string,
                0,first_problem_scanLogic_Id,null,0);// loop end

        if (problemScanLogic_string!=null){

            return problemScanLogic_string;

        }else {

            return null;

        }
    }


    /**
     * @method: 获取 匹配的 交换机可执行的 命令ID  并 循环执行分析操作
     * @Param: [user_String, connectMethod, telnetSwitchMethod]
     * @return: com.sgcc.common.core.domain.AjaxResult
     * @Author: 天幕顽主
     * @E-mail: WeiYaNing97@163.com
     */
    @PostMapping("scanProblem")
    public AjaxResult scanProblem(Map<String,String> user_String, //登录交换机的 用户信息 登录方式、ip、name、password
                                  Map<String,Object> user_Object,
                                  List<TotalQuestionTable> totalQuestionTables){
        //交换机基本信息
        String deviceModel = user_String.get("deviceModel");//设备型号
        String deviceBrand = user_String.get("deviceBrand");//设备品牌
        String firmwareVersion = user_String.get("firmwareVersion");//内部固件版本
        String subversionNumber = user_String.get("subversionNumber");//子版本号

        List<TotalQuestionTable> commandIdByInformation_comandID_Long = new ArrayList<>();

        if (totalQuestionTables == null){
            //根据交换机基本信息 查询 可执行命令的 命令信息
            AjaxResult commandIdByInformation_ajaxResult = commandIdByInformation(deviceModel, deviceBrand, firmwareVersion, subversionNumber);
            commandIdByInformation_comandID_Long = (List<TotalQuestionTable>) commandIdByInformation_ajaxResult.get("data");
        }else {
            for (TotalQuestionTable totalQuestionTable:totalQuestionTables){
                String brand = totalQuestionTable.getBrand();
                String type = totalQuestionTable.getType();
                String version = totalQuestionTable.getFirewareVersion();
                String subVersion = totalQuestionTable.getSubVersion();
                if (brand.equals(deviceBrand) && type.equals(deviceModel) && version.equals(firmwareVersion) && subVersion.equals(subversionNumber)){
                    commandIdByInformation_comandID_Long.add(totalQuestionTable);
                }
            }
        }

        for (TotalQuestionTable totalQuestionTable:commandIdByInformation_comandID_Long){
            // ---- More ----
            user_String.put("notFinished",totalQuestionTable.getNotFinished());

            //根据命令ID获取具体命令，执行
            //返回  交换机返回信息 和  第一条分析ID
            List<Object> executeScanCommandByCommandId_object = executeScanCommandByCommandId(user_String,totalQuestionTable.getCommandId(),user_String.get("notFinished"),
                    user_String.get("mode"), user_Object);
            if (executeScanCommandByCommandId_object.size() == 1){
                String string = (String) executeScanCommandByCommandId_object.get(0);
                if (string.equals("错误")){
                    continue;
                }

            }
            //分析
            String analysisReturnResults_String = analysisReturnResults(user_String, user_Object , totalQuestionTable,
                    executeScanCommandByCommandId_object,  "",  "");
            System.err.print("\r\nanalysisReturnResults_String:\r\n"+analysisReturnResults_String);
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
        List<TotalQuestionTable> totalQuestionTables = totalQuestionTableService.queryScannableQuestionsList(totalQuestionTable);
        if (totalQuestionTables!=null){
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