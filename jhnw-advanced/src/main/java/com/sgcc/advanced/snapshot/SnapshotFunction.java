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
     * @Description 开始快照
     * @author charles
     * @createTime 2023/12/29 15:51
     * @desc
     * @param
     * @return
     */
    @RequestMapping("/startSnapshot")
    public static void startSnapshot(@RequestBody List<String> switchInformation) {


        // 预设多线程参数 Object[] 中的参数格式为： {mode,ip,name,password,port}
        List<SwitchParameters> switchParametersList = new ArrayList<>();
        List<String> ips = new ArrayList<>();

        /*将字符串格式的用户登录信息 转化为json格式的登录信息*/
        for (String information:switchInformation){
            SwitchLoginInformation switchLoginInformation = JSON.parseObject(information, SwitchLoginInformation.class);
            SwitchParameters switchParameters = new SwitchParameters();
            switchParameters.setLoginUser(SecurityUtils.getLoginUser());

            BeanUtils.copyBeanProp(switchParameters,switchLoginInformation);
            switchParameters.setPort(Integer.valueOf(switchLoginInformation.getPort()).intValue());
            //以多线程中的格式 存放数组中
            //连接方式，ip，用户名，密码，端口号
            ips.add(switchParameters.getIp());

            /*RSA解密*/
            String password = RSAUtils.decryptFrontEndCiphertext(switchParameters.getPassword());
            switchParameters.setPassword(password);
            String ConfigureCiphers = RSAUtils.decryptFrontEndCiphertext(switchParameters.getConfigureCiphers());
            switchParameters.setConfigureCiphers(ConfigureCiphers);

            switchParametersList.add(switchParameters);
        }

        /*1： 数据库查询光衰信息 存放如全局变量*/
        HashMap<String, List<LightAttenuationComparison>> LightAttenuationComparisonsMap = getLightAttenuationComparisonMap(ips);
        HashMap<String, List<ErrorRate>> errorRateMap =  getErrorRateMap(ips);

        /*3： 启动线程 */
        /*1: 启动定时任务 */
        /*3： 连接交换机 */
        /*4： 运行分析 */
        LoginUser loginUser = SecurityUtils.getLoginUser();

        SnapshotFunction.userMap.put(loginUser.getUsername(), true);

        SnapshotTaskTime snapshotTaskTime = new SnapshotTaskTime();
        snapshotTaskTime.setSwitchParametersList(switchParametersList);
        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        snapshotTaskTime.setStartTime(startTime);
        SnapshotFunction.snapshotTaskTimeMap.put(loginUser.getUsername(),snapshotTaskTime);

        Set<String> keySet = SnapshotFunction.userMap.keySet();
        for (String key:keySet){
            System.err.println("SnapshotFunction.userMap == "+key);
        }

        Timer timer = new Timer();
        SnapshotTimed task = new SnapshotTimed(snapshotTaskTime,switchParametersList, timer, loginUser,LightAttenuationComparisonsMap,errorRateMap);/*交换机登录方式*/

        Integer time = 1;

        /*long delay = 0; // 延迟时间，单位为毫秒
        long period = time * 60 * 1000; // 执行间隔，单位为毫秒*/
        timer.schedule(task, 0, time * 60 * 1000);

    }
    /*4： 终止线程(竣工) */
    @RequestMapping("/threadInterrupt/{isDelete}")
    public static void threadInterrupt(@PathVariable boolean isDelete) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SnapshotFunction.userMap.put(loginUser.getUsername(),false);
        SnapshotTaskTime snapshotTaskTime = SnapshotFunction.snapshotTaskTimeMap.get(loginUser.getUsername());
        if (snapshotTaskTime == null){
            return;
        }
        SnapshotFunction.snapshotTaskTimeMap.remove(loginUser.getUsername());
        if (isDelete){
            SnapshotFunction.deleteOrSave.put(loginUser.getUsername(),true);
        }else {
            SnapshotFunction.deleteOrSave.put(loginUser.getUsername(),false);
        }

    }

    public static HashMap< String , List<LightAttenuationComparison> > getLightAttenuationComparisonMap(List<String> ips) {
        HashMap< String , List<LightAttenuationComparison> > lightAttenuationComparisons = new HashMap<>();
        lightAttenuationComparisonService = SpringBeanUtil.getBean(ILightAttenuationComparisonService.class);
        for (String ip:ips) {
            lightAttenuationComparisons.put(ip, lightAttenuationComparisonService.selectPojoListByIP(ip));
        }
        return lightAttenuationComparisons;
    }

    public static HashMap< String , List<ErrorRate> > getErrorRateMap(List<String> ips) {
        HashMap< String , List<ErrorRate> > errorRateListMap = new HashMap<>();
        errorRateService = SpringBeanUtil.getBean(IErrorRateService.class);
        for (String ip : ips) {
            errorRateListMap.put(ip, errorRateService.selectPojoListByIP(ip));
        }
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

    public  SnapshotTimed(SnapshotTaskTime snapshotTaskTime,
                          List<SwitchParameters> switchParametersList,
                         Timer timer, LoginUser loginUser,
                         HashMap<String, List<LightAttenuationComparison>> LightAttenuationComparisonsMap,
                         HashMap<String, List<ErrorRate>> errorRateMap) {
        this.snapshotTaskTime = snapshotTaskTime;
        this.switchParametersList = switchParametersList;
        this.timer = timer;
        this.loginUser = loginUser;
        this.LightAttenuationComparisonsMap = LightAttenuationComparisonsMap;
        this.errorRateMap = errorRateMap;

    }

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

    public void advancedFunction(List<SwitchParameters> switchParametersList, LoginUser loginUser) {//待测

        String simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        for (SwitchParameters pojo:switchParametersList){
            pojo.setScanningTime(simpleDateFormat);
        }

        //线程池
        ParameterSet parameterSet = new ParameterSet();
        parameterSet.setSwitchParameters(switchParametersList);
        parameterSet.setLoginUser(loginUser);
        parameterSet.setThreadCount(Integer.valueOf(5+"").intValue());

        try {
            /*运行分析线程池*/
            //boolean isRSA = true; //前端数据是否通过 RSA 加密后传入后端
            List<String> strings = new ArrayList<>();
            strings.add("光衰");
            strings.add("错误包");
            AdvancedThreadPool.switchLoginInformations(parameterSet, strings ,false);/*RSA前面方法已经解密了，如果用true 高频扫描会第二次解密出现错误*/
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}