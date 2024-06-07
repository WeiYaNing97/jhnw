package com.sgcc.advanced.snapshot;

import com.alibaba.fastjson.JSON;
import com.sgcc.advanced.domain.ErrorRate;
import com.sgcc.advanced.domain.LightAttenuationComparison;
import com.sgcc.advanced.domain.SnapshotTaskTime;
import com.sgcc.advanced.service.IErrorRateService;
import com.sgcc.advanced.service.ILightAttenuationComparisonService;
import com.sgcc.advanced.thread.AdvancedThreadPool;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.bean.BeanUtils;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.service.ISwitchScanResultService;
import com.sgcc.share.util.RSAUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: jhnw
 * @description: 快照功能启动
 * @author:
 * @create: 2024-05-17 13:57
 **/
@Api("快照功能")
@RestController
@RequestMapping("/advanced/SnapshotFunction")
public class SnapshotFunction {

    @Autowired
    private static IErrorRateService errorRateService;
    @Autowired
    private static ILightAttenuationComparisonService lightAttenuationComparisonService;

    public static Map<String,Boolean> userMap = new HashMap<>();
    public static Map<String,Boolean> deleteOrSave = new HashMap<>();
    public static Map<String, SnapshotTaskTime> snapshotTaskTimeMap = new HashMap<>();

    /**
     * 开始快照
     *
     * @param switchInformation 交换机登录信息列表
     * @return 无返回值
     * @throws Exception 抛出异常
     *
     * @Description 此方法用于开始执行交换机快照任务，包括多线程参数的转换、RSA解密、数据库查询光衰信息和错误率信息，
     *              以及启动定时任务连接交换机并运行分析。
     *
     * @author charles
     * @createTime 2023/12/29 15:51
     */
    @RequestMapping("/startSnapshot")
    public static void startSnapshot(@RequestBody List<String> switchInformation) {
        // 初始化用于存放多线程参数的列表
        // 预设多线程参数 Object[] 中的参数格式为： {mode,ip,name,password,port}
        List<SwitchParameters> switchParametersList = new ArrayList<>();
        // 初始化用于存放IP地址的列表
        List<String> ips = new ArrayList<>();

        // 遍历交换机登录信息列表
        /*将字符串格式的用户登录信息 转化为json格式的登录信息*/
        for (String information:switchInformation){
            // 将字符串格式的登录信息转换为JSON格式的SwitchLoginInformation对象
            SwitchLoginInformation switchLoginInformation = JSON.parseObject(information, SwitchLoginInformation.class);
            // 创建SwitchParameters对象
            SwitchParameters switchParameters = new SwitchParameters();
            // 设置登录用户
            switchParameters.setLoginUser(SecurityUtils.getLoginUser());

            // 将SwitchLoginInformation对象的属性复制到SwitchParameters对象中
            BeanUtils.copyBeanProp(switchParameters,switchLoginInformation);
            // 设置端口号
            switchParameters.setPort(Integer.valueOf(switchLoginInformation.getPort()).intValue());
            // 将IP地址添加到ips列表中
            //连接方式，ip，用户名，密码，端口号
            ips.add(switchParameters.getIp());

            // 对密码和配置密文进行RSA解密
            String password = RSAUtils.decryptFrontEndCiphertext(switchParameters.getPassword());
            switchParameters.setPassword(password);
            String ConfigureCiphers = RSAUtils.decryptFrontEndCiphertext(switchParameters.getConfigureCiphers());
            switchParameters.setConfigureCiphers(ConfigureCiphers);

            // 将解密后的SwitchParameters对象添加到switchParametersList列表中
            switchParametersList.add(switchParameters);
        }

        // 查询数据库中的光衰信息，并存储到全局变量中
        HashMap<String, List<LightAttenuationComparison>> LightAttenuationComparisonsMap = getLightAttenuationComparisonMap(ips);
        HashMap<String, List<ErrorRate>> errorRateMap =  getErrorRateMap(ips);

        // 初始化登录用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();

        // 将登录用户信息添加到SnapshotFunction.userMap中
        SnapshotFunction.userMap.put(loginUser.getUsername(), true);

        // 创建SnapshotTaskTime对象，并设置相关属性
        SnapshotTaskTime snapshotTaskTime = new SnapshotTaskTime();
        snapshotTaskTime.setSwitchParametersList(switchParametersList);
        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        snapshotTaskTime.setStartTime(startTime);
        // 将SnapshotTaskTime对象添加到SnapshotFunction.snapshotTaskTimeMap中
        SnapshotFunction.snapshotTaskTimeMap.put(loginUser.getUsername(),snapshotTaskTime);

        // 遍历SnapshotFunction.userMap的键集
        Set<String> keySet = SnapshotFunction.userMap.keySet();
        for (String key:keySet){
            // 打印SnapshotFunction.userMap的键
            System.err.println("SnapshotFunction.userMap == "+key);
        }

        // 创建Timer对象
        Timer timer = new Timer();

        // 创建SnapshotTimed任务对象，并设置相关属性
        SnapshotTimed task = new SnapshotTimed(snapshotTaskTime,switchParametersList, timer, loginUser,LightAttenuationComparisonsMap,errorRateMap);/*交换机登录方式*/

        // 定义执行任务的时间间隔（这里为1分钟）
        Integer time = 1;

        // 使用Timer对象的schedule方法定时执行任务
        /*long delay = 0; // 延迟时间，单位为毫秒
        long period = time * 60 * 1000; // 执行间隔，单位为毫秒*/
        timer.schedule(task, 0, time * 60 * 1000);

    }

    /**
     * 终止线程（竣工）
     *
     * @param isDelete 是否删除，true为删除，false为不删除
     * @return 无返回值
     * @RequestMapping("/threadInterrupt/{isDelete}") 用于指定该方法对应的请求URL，其中{isDelete}为路径变量，对应isDelete参数
     * @PathVariable boolean isDelete 用于将请求路径中的变量值绑定到方法参数上
     */
    /*4： 终止线程(竣工) */
    @RequestMapping("/threadInterrupt/{isDelete}")
    public static void threadInterrupt(@PathVariable boolean isDelete) {
        // 获取当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 将用户名对应的值设为false，表示线程已终止
        SnapshotFunction.userMap.put(loginUser.getUsername(),false);
        // 根据用户名获取对应的快照任务时间对象
        SnapshotTaskTime snapshotTaskTime = SnapshotFunction.snapshotTaskTimeMap.get(loginUser.getUsername());
        // 如果快照任务时间对象为空，则直接返回
        if (snapshotTaskTime == null){
            return;
        }
        // 从快照任务时间映射中移除当前用户的快照任务时间对象
        SnapshotFunction.snapshotTaskTimeMap.remove(loginUser.getUsername());
        // 根据isDelete的值，决定是否在删除或保存映射中添加当前用户，并设置对应的值为true或false
        if (isDelete){
            // 如果isDelete为true，则在删除或保存映射中添加当前用户，并设置对应的值为true
            SnapshotFunction.deleteOrSave.put(loginUser.getUsername(),true);
        }else {
            // 如果isDelete为false，则在删除或保存映射中添加当前用户，并设置对应的值为false
            SnapshotFunction.deleteOrSave.put(loginUser.getUsername(),false);
        }
    }

    /**
     * 获取光衰比较信息映射表
     *
     * @param ips IP地址列表
     * @return 返回一个HashMap，键为IP地址，值为对应IP地址的光衰比较信息列表
     */
    public static HashMap< String , List<LightAttenuationComparison> > getLightAttenuationComparisonMap(List<String> ips) {
        // 创建一个HashMap，用于存储IP地址与光衰比较信息的映射关系
        HashMap< String , List<LightAttenuationComparison> > lightAttenuationComparisons = new HashMap<>();

        // 获取ILightAttenuationComparisonService类型的bean实例
        lightAttenuationComparisonService = SpringBeanUtil.getBean(ILightAttenuationComparisonService.class);

        // 遍历IP地址列表
        for (String ip:ips) {
            // 调用服务层方法，根据IP地址获取对应的光衰比较信息列表
            // 并将IP地址和光衰比较信息列表存入HashMap中
            lightAttenuationComparisons.put(ip, lightAttenuationComparisonService.selectPojoListByIP(ip));
        }

        // 返回存储了IP地址与光衰比较信息映射关系的HashMap
        return lightAttenuationComparisons;
    }

    /**
     * 获取错误率映射表
     *
     * @param ips IP地址列表
     * @return 返回一个HashMap，键为IP地址，值为对应IP地址的错误率列表
     */
    public static HashMap< String , List<ErrorRate> > getErrorRateMap(List<String> ips) {
        // 创建一个HashMap，用于存储IP地址与错误率列表的映射关系
        HashMap< String , List<ErrorRate> > errorRateListMap = new HashMap<>();
        // 获取IErrorRateService类型的bean实例
        errorRateService = SpringBeanUtil.getBean(IErrorRateService.class);
        // 遍历IP地址列表
        for (String ip : ips) {
            // 调用服务层方法，根据IP地址获取对应的错误率列表
            // 并将IP地址和错误率列表存入HashMap中
            errorRateListMap.put(ip, errorRateService.selectPojoListByIP(ip));
        }
        // 返回存储了IP地址与错误率映射关系的HashMap
        return errorRateListMap;
    }
}

class SnapshotTimed  extends TimerTask{

    @Autowired
    private static IErrorRateService errorRateService;
    @Autowired
    private static ILightAttenuationComparisonService lightAttenuationComparisonService;

    private SnapshotTaskTime snapshotTaskTime = null;
    private List<SwitchParameters> switchParametersList = null;
    private  Timer timer = null;
    private LoginUser loginUser = null;
    /**  开启快照前数据 */
    private HashMap<String, List<LightAttenuationComparison>> LightAttenuationComparisonsMap = null;
    HashMap<String, List<ErrorRate>> errorRateMap = null;

    /**
     * SnapshotTimed类的构造函数
     *
     * @param snapshotTaskTime 交换机快照任务时间对象
     * @param switchParametersList 交换机参数列表
     * @param timer 定时器对象
     * @param loginUser 登录用户对象
     * @param LightAttenuationComparisonsMap 光衰比较信息映射表
     * @param errorRateMap 错误率映射表
     */
    public  SnapshotTimed(SnapshotTaskTime snapshotTaskTime,
                          List<SwitchParameters> switchParametersList,
                         Timer timer, LoginUser loginUser,
                         HashMap<String, List<LightAttenuationComparison>> LightAttenuationComparisonsMap,
                         HashMap<String, List<ErrorRate>> errorRateMap) {
        // 设置交换机快照任务时间对象
        this.snapshotTaskTime = snapshotTaskTime;
        // 设置交换机参数列表
        this.switchParametersList = switchParametersList;
        // 设置定时器对象
        this.timer = timer;
        // 设置登录用户对象
        this.loginUser = loginUser;
        // 设置光衰比较信息映射表
        this.LightAttenuationComparisonsMap = LightAttenuationComparisonsMap;
        // 设置错误率映射表
        this.errorRateMap = errorRateMap;

    }

    /**
     * 线程运行方法，执行定时任务。
     *
     * @Override 重写父类方法
     */
    @Override
    public void run() {

        boolean thread = false;
        if (SnapshotFunction.userMap.containsKey(this.loginUser.getUsername()) && SnapshotFunction.userMap.get(this.loginUser.getUsername()) != null) {
            thread = (boolean) SnapshotFunction.userMap.get(this.loginUser.getUsername());
        }

        if (thread){

            System.err.println("光衰功能进行定时任务");
            SnapshotMethod snapshotMethod = new SnapshotMethod();
            snapshotMethod.advancedFunction(switchParametersList,loginUser);

        }else {

            this.timer.cancel(); // 取消定时任务

            System.err.println("光衰功能取消定时任务");
            SnapshotFunction.userMap.remove(this.loginUser.getUsername());

            /** 5： 快照修改
             * 获取快照前数据 及 Id数据 */
            List<LightAttenuationComparison> lightAttenuationComparisons = new ArrayList<>();
            List<Long> lightAttenuationIds = new ArrayList<>();
            Collection<List<LightAttenuationComparison>> lightAttenuationvalues = this.LightAttenuationComparisonsMap.values();
            for (List<LightAttenuationComparison> pojoList:lightAttenuationvalues){
                lightAttenuationComparisons.addAll(pojoList);
                for (LightAttenuationComparison pojo:pojoList){
                    lightAttenuationIds.add(pojo.getId());
                }
            }

            List<ErrorRate> errorRateList = new ArrayList<>();
            Collection<List<ErrorRate>> errorRateValues = this.errorRateMap.values();
            List<Long> errorRateIds = new ArrayList<>();
            for (List<ErrorRate> errorRates:errorRateValues){
                errorRateList.addAll(errorRates);
                for (ErrorRate errorRate:errorRates){
                    errorRateIds.add(errorRate.getId());
                }
            }


            /**6 获取竣工后的数据 及 ID集合*/
            List<String> ipList = new ArrayList<>();
            for (SwitchParameters pojo:switchParametersList){
                ipList.add( pojo.getIp() );
            }
            HashMap<String, List<LightAttenuationComparison>> newLightAttenuationMap = SnapshotFunction.getLightAttenuationComparisonMap( ipList );
            Collection<List<LightAttenuationComparison>> newLightAttenuationValues = newLightAttenuationMap.values();
            List<Long> newLightAttenuationIds = new ArrayList<>();
            for (List<LightAttenuationComparison> pojoList:newLightAttenuationValues){
                for (LightAttenuationComparison pojo:pojoList){
                    newLightAttenuationIds.add(pojo.getId());
                }
            }


            HashMap<String, List<ErrorRate>> newErrorRateMap =  ErrorPackageSnapshot.getErrorRateMap(ipList);
            Collection<List<ErrorRate>> newErrorRateValues = newErrorRateMap.values();
            List<Long> newErrorRateIds = new ArrayList<>();
            for (List<ErrorRate> pojoList:newErrorRateValues){
                for (ErrorRate pojo:pojoList){
                    newErrorRateIds.add(pojo.getId());
                }
            }


            /**7 获取 竣工后新增的数据 */
            lightAttenuationComparisonService = SpringBeanUtil.getBean(ILightAttenuationComparisonService.class);
            newLightAttenuationIds.removeAll(lightAttenuationIds);
            if (newLightAttenuationIds.size()!=0){
                System.err.println("/**7 获取 竣工后新增的数据 */ : "+newLightAttenuationIds);
                lightAttenuationComparisonService.deleteLightAttenuationComparisonByIds(newLightAttenuationIds.toArray(new Long[newLightAttenuationIds.size()]));
            }

            errorRateService = SpringBeanUtil.getBean(IErrorRateService.class);
            newErrorRateIds.removeAll(errorRateIds);
            if (newErrorRateIds.size()!=0){
                System.err.println("/**7 获取 竣工后新增的数据 */ : "+ newErrorRateIds );
                errorRateService.deleteErrorRateByIds(newErrorRateIds.toArray(new Long[newErrorRateIds.size()]));
            }

            /**
             * @Description  遍历修改快照信息*/
            if(lightAttenuationComparisons.size()!=0){
                for (LightAttenuationComparison pojo:lightAttenuationComparisons){
                    lightAttenuationComparisonService.updateLightAttenuationComparison(pojo);
                }
            }
            if (errorRateList.size()!=0){
                for (ErrorRate pojo:errorRateList){
                    errorRateService.updateErrorRate(pojo);
                }
            }

            /** 删除 */
            if ( SnapshotFunction.deleteOrSave.get(loginUser.getUsername()) ){
                String startTime = snapshotTaskTime.getStartTime();
                String endTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                System.err.println("开始时间："+startTime + "结束时间："+ endTime);
                ISwitchScanResultService switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);

                String temProName = "光衰";
                for (SwitchParameters pojo:snapshotTaskTime.getSwitchParametersList()){
                    /* 根据IP 范式名称 和 开始时间、结束时间 删除数据 */
                    int number = switchScanResultService.deleteSwitchScanResultByIPAndTime(pojo.getIp(),temProName,startTime,endTime);
                    System.err.println(number);
                }

                temProName = "错误包";
                for (SwitchParameters pojo:snapshotTaskTime.getSwitchParametersList()){
                    /* 根据IP 范式名称 和 开始时间、结束时间 删除数据 */
                    int number = switchScanResultService.deleteSwitchScanResultByIPAndTime(pojo.getIp(),temProName,startTime,endTime);
                    System.err.println(number);
                }
            }

            SnapshotFunction.deleteOrSave.remove(loginUser.getUsername());

            /*6： 重新扫描并提交扫描结果 */
            SnapshotMethod snapshotMethod = new SnapshotMethod();
            snapshotMethod.advancedFunction(switchParametersList,loginUser);

        }

    }
}

class SnapshotMethod{

    /**
     * 高级功能方法，用于执行一系列针对交换机参数列表的操作。
     *
     * @param switchParametersList 交换机参数列表
     * @param loginUser 登录用户
     * @throws InterruptedException 如果线程被中断则抛出此异常
     */
    public void advancedFunction(List<SwitchParameters> switchParametersList, LoginUser loginUser) {//待测

        // 设置当前时间格式为 "yyyy-MM-dd HH:mm:ss"
        String simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        // 遍历交换机参数列表
        for (SwitchParameters pojo:switchParametersList){
            // 设置扫描时间为当前时间
            pojo.setScanningTime(simpleDateFormat);
        }

        // 创建参数集对象
        ParameterSet parameterSet = new ParameterSet();
        // 设置交换机参数列表
        parameterSet.setSwitchParameters(switchParametersList);
        // 设置登录用户
        parameterSet.setLoginUser(loginUser);
        // 设置线程池数量为5
        parameterSet.setThreadCount(Integer.valueOf(5+"").intValue());

        try {
            // 运行分析线程池
            // 创建一个字符串列表
            //boolean isRSA = true; //前端数据是否通过 RSA 加密后传入后端
            List<String> strings = new ArrayList<>();
            // 添加光衰到字符串列表
            strings.add("光衰");
            // 添加错误包到字符串列表
            strings.add("错误包");
            // 调用 AdvancedThreadPool 的 switchLoginInformations 方法，传入参数集、字符串列表和 false（表示不进行 RSA 解密）
            // 注意：这里注释中提到 RSA 前面方法已经解密了，所以用 false，高频扫描会第二次解密出现错误
            AdvancedThreadPool.switchLoginInformations(parameterSet, strings ,false);/*RSA前面方法已经解密了，如果用true 高频扫描会第二次解密出现错误*/
        } catch (InterruptedException e) {
            // 捕获 InterruptedException 异常并打印堆栈信息
            e.printStackTrace();
        }
    }
}