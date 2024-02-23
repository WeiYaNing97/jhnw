package com.sgcc.advanced.snapshot;

import com.alibaba.fastjson.JSON;
import com.sgcc.advanced.domain.ErrorRate;
import com.sgcc.advanced.service.IErrorRateService;
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
 * @program: jhnw
 * @description: 光纤衰耗快照功能
 * @desc
 * @author:
 * @create: 2023-12-28 15:19
 **/
/* *一、开工快照：
 * 1、用户：记录开工时间，输入要监测的设备ip
 * 2、逻辑：把最近一次扫描结果，按照用户指定ip提取出数据出来，作为开工快照时间点数据。比如最近数据为当天夜里1点的数据，现在要变成开工快照时间点数据
 * 二、高频扫描（扫描周期）：
 * 1、用户指定高频时间，比如5分钟（最小间隔）
 * 2、逻辑：按照用户指定的ip设备和时间间隔开展扫描及告警，并记录（保持与主逻辑一致）。
 * 三、竣工：
 * 逻辑：1、开展一次用户指定设备的监测扫描并记录，与快照数据进行比对，发现问题及时告警。
 *          其中，以快照数据作为比对依据，本次扫描数据修正仅根据快照数据进行修正。高频扫描期间的数据不参与数据修正。修正数据包括：扫描次数、平均值2项*/
@Api("错误包快照功能")
@RestController
@RequestMapping("/advanced/ErrorPackageSnapshot")
public class ErrorPackageSnapshot {
    @Autowired
    private static IErrorRateService errorRateService;
    public static Map<String,Boolean> userMap = new HashMap<>();
    public static void main(String[] args) {
        /*前端传入交换机登录信息*/
        List<String> switchInformation = new ArrayList<>();
        switchInformation.add("{\"name\":\"admin\",\"password\":\"gFB+qFKgv/NlL92xvFOMzqLj2o6g6c0ahZhlYouwNkKJbszahQbbPvrewvItlfWESOqJZvmUKZlT5JzY9SahTJ6KNimhF1UbHi+uYjtWJxp/3Wn1EjgiLbuQiKnldMWi5PPgVcwHJU3nR3FoAI63evOVS/VW7XvEVhxgY4cBgwU=\",\"ip\":\"192.168.1.100\",\"mode\":\"ssh\",\"port\":\"22\",\"configureCiphers\":\"VnRkCznb9y5pquzRnURhQQIZMHdkdbZlXFxnxnHC5AnoJuoTgNy2YGuTeBUz3wCHRrGXQmP+aypZPUbdH+hUhl4Yevri52Zg4BGl6tnmIB1Bh8oYtHfqmWEanAVNBuaJdw8IUsCohRkTW4G0wtAQT4pDV1EsZChBTv1r8sGCxgk=\",\"row_index\":0}");
        /*startSnapshot(switchInformation , 5 );*/
    }

    /**
    * @Description 开始快照
    * @author charles
    * @createTime 2023/12/29 15:51
    * @desc
    * @param
     * @return
    */
    @RequestMapping("/startSnapshot")
    public static void startSnapshot() {/*List<String> switchInformation,Integer time*/
        List<String> switchInformation = new ArrayList<>();
        switchInformation.add("{\"name\":\"admin\",\"password\":\"gFB+qFKgv/NlL92xvFOMzqLj2o6g6c0ahZhlYouwNkKJbszahQbbPvrewvItlfWESOqJZvmUKZlT5JzY9SahTJ6KNimhF1UbHi+uYjtWJxp/3Wn1EjgiLbuQiKnldMWi5PPgVcwHJU3nR3FoAI63evOVS/VW7XvEVhxgY4cBgwU=\",\"ip\":\"192.168.1.100\",\"mode\":\"ssh\",\"port\":\"22\",\"configureCiphers\":\"VnRkCznb9y5pquzRnURhQQIZMHdkdbZlXFxnxnHC5AnoJuoTgNy2YGuTeBUz3wCHRrGXQmP+aypZPUbdH+hUhl4Yevri52Zg4BGl6tnmIB1Bh8oYtHfqmWEanAVNBuaJdw8IUsCohRkTW4G0wtAQT4pDV1EsZChBTv1r8sGCxgk=\",\"row_index\":0}");

        Integer time = 1 ;

        String simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // 预设多线程参数 Object[] 中的参数格式为： {mode,ip,name,password,port}
        List<SwitchParameters> switchParametersList = new ArrayList<>();
        List<String> ips = new ArrayList<>();

        /*将字符串格式的用户登录信息 转化为json格式的登录信息*/
        for (String information:switchInformation){
            SwitchLoginInformation switchLoginInformation = JSON.parseObject(information, SwitchLoginInformation.class);
            SwitchParameters switchParameters = new SwitchParameters();
            switchParameters.setLoginUser( SecurityUtils.getLoginUser() );/*SecurityUtils.getLoginUser()*/
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
        HashMap<String, List<ErrorRate>> errorRateMap =  getErrorRateMap(ips);

        /*3： 启动线程 */
            /*1: 启动定时任务 */
            /*3： 连接交换机 */
            /*4： 高级功能 */
        LoginUser loginUser = SecurityUtils.getLoginUser();
        ErrorPackageSnapshot.userMap.put(loginUser.getUsername(),true);

        Timer timer = new Timer();
        ErrorPackageTimed task = new ErrorPackageTimed( switchParametersList, timer, loginUser ,errorRateMap);/*交换机登录方式*/
        long delay = 0; // 延迟时间，单位为毫秒
        long period = time * 60 * 1000; // 执行间隔，单位为毫秒
        timer.schedule(task, delay, period);

    }

    public static HashMap< String , List<ErrorRate> > getErrorRateMap(List<String> ips) {
        HashMap< String , List<ErrorRate> > errorRateListMap = new HashMap<>();
        errorRateService = SpringBeanUtil.getBean(IErrorRateService.class);
        for (String ip : ips) {
            errorRateListMap.put(ip, errorRateService.selectPojoListByIP(ip));
        }
        return errorRateListMap;
    }

    /*4： 终止线程(竣工) */
    @RequestMapping("/threadInterrupt")
    public static void threadInterrupt() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        ErrorPackageSnapshot.userMap.put(loginUser.getUsername(),false);
    }
}

class ErrorPackageTimed  extends TimerTask{

    @Autowired
    private static IErrorRateService errorRateService;

    private List<SwitchParameters> switchParametersList = null;
    private  Timer timer = null;
    private LoginUser loginUser = null;
    private HashMap<String, List<ErrorRate>> errorRateMap = null;

    public  ErrorPackageTimed(List<SwitchParameters> switchParametersList, Timer timer, LoginUser loginUser,HashMap<String, List<ErrorRate>> errorRateMap) {
        this.switchParametersList = switchParametersList;
        this.timer = timer;
        this.loginUser = loginUser;
        this.errorRateMap = errorRateMap;
    }

    @Override
    public void run() {

        boolean thread = ErrorPackageSnapshot.userMap.get(this.loginUser.getUsername());

        if (thread){

            ErrorPackageMethod errorPackageMethod = new ErrorPackageMethod();
            errorPackageMethod.advancedFunction(switchParametersList,loginUser);

        }else {

            this.timer.cancel(); // 取消定时任务
            ErrorPackageSnapshot.userMap.remove(this.loginUser.getUsername());
            /*5： 快照修改 */
            /*List<ErrorRate> errorRates = new ArrayList<>();
            Collection<List<ErrorRate>> values = this.errorRateMap.values();
            for (List<ErrorRate> pojoList:values){
                errorRates.addAll(pojoList);
            }*/
            /**
             * @Description  遍历修改快照信息
             */
            /*errorRateService = SpringBeanUtil.getBean(IErrorRateService.class);
            for (ErrorRate pojo:errorRates){
                errorRateService.updateErrorRate(pojo);
            }*/
            /*6： 重新扫描并提交扫描结果 */
            ErrorPackageMethod errorPackageMethod = new ErrorPackageMethod();
            errorPackageMethod.advancedFunction(switchParametersList,loginUser);
        }
    }

}

class ErrorPackageMethod{

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
            strings.add("误码率");
            AdvancedThreadPool.switchLoginInformations(parameterSet, strings ,false);/*RSA前面方法已经解密了，如果用true 高频扫描会第二次解密出现错误*/
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}