package com.sgcc.advanced.snapshot;
import com.alibaba.fastjson.JSON;
import com.sgcc.advanced.domain.LightAttenuationComparison;
import com.sgcc.advanced.service.ILightAttenuationComparisonService;
import com.sgcc.advanced.thread.AdvancedThreadPool;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.common.utils.bean.BeanUtils;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.RSAUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @program: jhnw
 * @description: 光纤衰耗快照功能
 *
 *
 * @desc
 * 一、开工快照：
 * 1、用户：记录开工时间，输入要监测的设备ip
 * 2、逻辑：把最近一次扫描结果，按照用户指定ip提取出数据出来，作为开工快照时间点数据。比如最近数据为当天夜里1点的数据，现在要变成开工快照时间点数据
 * 二、高频扫描（扫描周期）：
 * 1、用户指定高频时间，比如5分钟（最小间隔）
 * 2、逻辑：按照用户指定的ip设备和时间间隔开展扫描及告警，并记录（保持与主逻辑一致）。
 * 三、竣工：
 * 逻辑：1、开展一次用户指定设备的监测扫描并记录，与快照数据进行比对，发现问题及时告警。其中，以快照数据作为比对依据，本次扫描数据修正仅根据快照数据进行修正。高频扫描期间的数据不参与数据修正。修正数据包括：扫描次数、平均值2项
 *
 *
 * @author:
 * @create: 2023-12-28 15:19
 **/
@Api("光衰快照功能")
@RestController
@RequestMapping("/advanced/LightAttenuationSnapshot")
public class LightAttenuationSnapshot {

    @Autowired
    private static ILightAttenuationComparisonService lightAttenuationComparisonService;
    public static Map<String,Boolean> userMap = new HashMap<>();

    /**
    * @Description 开始快照
    * @author charles
    * @createTime 2023/12/29 15:51
    * @desc
    * @param
     * @return
    */
    @RequestMapping("/startSnapshot")
    public static void startSnapshot() {

        List<String> switchInformation = new ArrayList<>();
        switchInformation.add("{\"name\":\"admin\",\"password\":\"gFB+qFKgv/NlL92xvFOMzqLj2o6g6c0ahZhlYouwNkKJbszahQbbPvrewvItlfWESOqJZvmUKZlT5JzY9SahTJ6KNimhF1UbHi+uYjtWJxp/3Wn1EjgiLbuQiKnldMWi5PPgVcwHJU3nR3FoAI63evOVS/VW7XvEVhxgY4cBgwU=\",\"ip\":\"192.168.1.100\",\"mode\":\"ssh\",\"port\":\"22\",\"configureCiphers\":\"VnRkCznb9y5pquzRnURhQQIZMHdkdbZlXFxnxnHC5AnoJuoTgNy2YGuTeBUz3wCHRrGXQmP+aypZPUbdH+hUhl4Yevri52Zg4BGl6tnmIB1Bh8oYtHfqmWEanAVNBuaJdw8IUsCohRkTW4G0wtAQT4pDV1EsZChBTv1r8sGCxgk=\",\"row_index\":0}");
        Integer time = 1;


        String simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // 预设多线程参数 Object[] 中的参数格式为： {mode,ip,name,password,port}
        List<SwitchParameters> switchParametersList = new ArrayList<>();
        List<String> ips = new ArrayList<>();

        /*将字符串格式的用户登录信息 转化为json格式的登录信息*/
        for (String information:switchInformation){
            SwitchLoginInformation switchLoginInformation = JSON.parseObject(information, SwitchLoginInformation.class);
            SwitchParameters switchParameters = new SwitchParameters();
            switchParameters.setLoginUser(SecurityUtils.getLoginUser());
            switchParameters.setScanningTime(simpleDateFormat);
            BeanUtils.copyBeanProp(switchParameters,switchLoginInformation);
            switchParameters.setPort(Integer.valueOf(switchLoginInformation.getPort()).intValue());
            //以多线程中的格式 存放数组中
            //连接方式，ip，用户名，密码，端口号
            ips.add(switchParameters.getIp());

            /*RSA解密*/
            switchParameters.setPassword(RSAUtils.decryptFrontEndCiphertext(switchParameters.getPassword()));
            switchParameters.setConfigureCiphers(RSAUtils.decryptFrontEndCiphertext(switchParameters.getConfigureCiphers()));

            switchParametersList.add(switchParameters);
        }

        /*1： 数据库查询光衰信息 存放如全局变量*/
        HashMap<String, List<LightAttenuationComparison>> LightAttenuationComparisonsMap = getLightAttenuationComparisonMap(ips);

        /*3： 启动线程 */
            /*1: 启动定时任务 */
            /*3： 连接交换机 */
            /*4： 高级功能 */
        LoginUser loginUser = SecurityUtils.getLoginUser();

        LightAttenuationSnapshot.userMap.put(loginUser.getUsername(), true);

        Timer timer = new Timer();
        LuminousAttenuationTimed task = new LuminousAttenuationTimed(switchParametersList, timer, loginUser,LightAttenuationComparisonsMap);/*交换机登录方式*/
        long delay = 0; // 延迟时间，单位为毫秒
        long period = time * 60 * 1000; // 执行间隔，单位为毫秒
        timer.schedule(task, delay, period);

    }

    public static HashMap< String , List<LightAttenuationComparison> > getLightAttenuationComparisonMap(List<String> ips) {
        HashMap< String , List<LightAttenuationComparison> > lightAttenuationComparisons = new HashMap<>();
        lightAttenuationComparisonService = SpringBeanUtil.getBean(ILightAttenuationComparisonService.class);
        for (String ip:ips) {
            lightAttenuationComparisons.put(ip, lightAttenuationComparisonService.selectPojoListByIP(ip));
        }
        return lightAttenuationComparisons;
    }

    /*4： 终止线程(竣工) */
    @RequestMapping("/threadInterrupt")
    public static void threadInterrupt() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        LightAttenuationSnapshot.userMap.put(loginUser.getUsername(),false);
    }

}

class LuminousAttenuationTimed  extends TimerTask{

    @Autowired
    private static ILightAttenuationComparisonService lightAttenuationComparisonService;

    private List<SwitchParameters> switchParametersList = null;
    private  Timer timer = null;
    private LoginUser loginUser = null;
    private HashMap<String, List<LightAttenuationComparison>> LightAttenuationComparisonsMap = null;

    public  LuminousAttenuationTimed(List<SwitchParameters> switchParametersList,
                                     Timer timer, LoginUser loginUser,
                                     HashMap<String, List<LightAttenuationComparison>> LightAttenuationComparisonsMap) {
        this.switchParametersList = switchParametersList;
        this.timer = timer;
        this.loginUser = loginUser;
        this.LightAttenuationComparisonsMap = LightAttenuationComparisonsMap;
    }

    @Override
    public void run() {

        boolean thread = LightAttenuationSnapshot.userMap.get(this.loginUser.getUsername());

        if (thread){

            System.err.println("进行定时任务");
            LuminousAttenuationMethod luminousAttenuationMethod = new LuminousAttenuationMethod();
            luminousAttenuationMethod.advancedFunction(switchParametersList,loginUser);

        }else {

            this.timer.cancel(); // 取消定时任务
            System.err.println("取消定时任务");
            LightAttenuationSnapshot.userMap.remove(this.loginUser.getUsername());

            /*5： 快照修改 */
            List<LightAttenuationComparison> lightAttenuationComparisons = new ArrayList<>();
            Collection<List<LightAttenuationComparison>> values = this.LightAttenuationComparisonsMap.values();
            for (List<LightAttenuationComparison> pojoList:values){
                lightAttenuationComparisons.addAll(pojoList);
            }

            /**
             * @Description  遍历修改快照信息*/
            for (LightAttenuationComparison pojo:lightAttenuationComparisons){
                lightAttenuationComparisonService = SpringBeanUtil.getBean(ILightAttenuationComparisonService.class);
                lightAttenuationComparisonService.updateLightAttenuationComparison(pojo);
            }

            /*6： 重新扫描并提交扫描结果 */
            LuminousAttenuationMethod luminousAttenuationMethod = new LuminousAttenuationMethod();
            luminousAttenuationMethod.advancedFunction(switchParametersList,loginUser);

        }
    }
}

class LuminousAttenuationMethod{

    public void advancedFunction(List<SwitchParameters> switchParametersList, LoginUser loginUser) {//待测
        //线程池
        ParameterSet parameterSet = new ParameterSet();
        parameterSet.setSwitchParameters(switchParametersList);
        parameterSet.setLoginUser(loginUser);
        parameterSet.setThreadCount(Integer.valueOf(5+"").intValue());
        try {

            /*高级功能线程池*/
            //boolean isRSA = true; //前端数据是否通过 RSA 加密后传入后端
            List<String> strings = new ArrayList<>();
            strings.add("光衰");
            AdvancedThreadPool.switchLoginInformations(parameterSet, strings ,false);/*RSA前面方法已经解密了，如果用true 高频扫描会第二次解密出现错误*/

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}