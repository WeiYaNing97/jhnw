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
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: 错误包快照功能
 * @program: jhnw
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
    public static void startSnapshot(@RequestBody List<String> switchInformation) {/*List<String> switchInformation,Integer time*/


        // 预设多线程参数 Object[] 中的参数格式为： {mode,ip,name,password,port}
        List<SwitchParameters> switchParametersList = new ArrayList<>();
        List<String> ips = new ArrayList<>();

        /*将字符串格式的用户登录信息 转化为json格式的登录信息*/
        for (String information:switchInformation){

            SwitchLoginInformation switchLoginInformation = JSON.parseObject(information, SwitchLoginInformation.class);
            SwitchParameters switchParameters = new SwitchParameters();
            switchParameters.setLoginUser( SecurityUtils.getLoginUser() );/*SecurityUtils.getLoginUser()*/

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
            /*a: 启动定时任务 */
            /*b： 连接交换机 */
            /*c： 运行分析 */
        LoginUser loginUser = SecurityUtils.getLoginUser();
        ErrorPackageSnapshot.userMap.put(loginUser.getUsername(),true);

        SnapshotTaskTime snapshotTaskTime = new SnapshotTaskTime();
        snapshotTaskTime.setSwitchParametersList(switchParametersList);
        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        snapshotTaskTime.setStartTime(startTime);
        ErrorPackageSnapshot.snapshotTaskTimeMap.put(loginUser.getUsername(),snapshotTaskTime);

        Timer timer = new Timer();
        ErrorPackageTimed task = new ErrorPackageTimed(startTime, switchParametersList, timer, loginUser ,errorRateMap);/*交换机登录方式*/

        Integer time = 1 ;
        /*long delay = 0; // 延迟时间，单位为毫秒
        long period = time * 60 * 1000; // 执行间隔，单位为毫秒*/
        timer.schedule(task, 0, time * 60 * 1000);

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
    @PostMapping("/threadInterrupt/{isDelete}")
    public static void threadInterrupt(@PathVariable boolean isDelete) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        ErrorPackageSnapshot.userMap.put(loginUser.getUsername(),false);

        SnapshotTaskTime snapshotTaskTime = ErrorPackageSnapshot.snapshotTaskTimeMap.get(loginUser.getUsername());
        ErrorPackageSnapshot.snapshotTaskTimeMap.remove(loginUser.getUsername());
        if (isDelete){
            String startTime = snapshotTaskTime.getStartTime();
            String endTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            ISwitchScanResultService switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);
            String temProName = "错误包";
            for (SwitchParameters pojo:snapshotTaskTime.getSwitchParametersList()){
                /* 根据IP 范式名称 和 开始时间、结束时间 删除数据 */
                int number = switchScanResultService.deleteSwitchScanResultByIPAndTime(pojo.getIp(),temProName,startTime,endTime);
                System.err.println(number);
            }
        }
    }


}

class ErrorPackageTimed  extends TimerTask{

    @Autowired
    private static IErrorRateService errorRateService;

    private String startTime = null;
    private List<SwitchParameters> switchParametersList = null;
    private  Timer timer = null;
    private LoginUser loginUser = null;
    private HashMap<String, List<ErrorRate>> errorRateMap = null;

    public  ErrorPackageTimed(String startTime,List<SwitchParameters> switchParametersList, Timer timer, LoginUser loginUser,HashMap<String, List<ErrorRate>> errorRateMap) {
        this.startTime = startTime;
        this.switchParametersList = switchParametersList;
        this.timer = timer;
        this.loginUser = loginUser;
        this.errorRateMap = errorRateMap;
    }

    @Override
    public void run() {

        boolean thread = false;
        if (ErrorPackageSnapshot.userMap.containsKey(this.loginUser.getUsername()) && ErrorPackageSnapshot.userMap.get(this.loginUser.getUsername()) != null) {
            thread = (boolean) ErrorPackageSnapshot.userMap.get(this.loginUser.getUsername());
        }

        if (thread){

            ErrorPackageMethod errorPackageMethod = new ErrorPackageMethod();
            errorPackageMethod.advancedFunction(switchParametersList,loginUser);

        }else {

            this.timer.cancel(); // 取消定时任务
            ErrorPackageSnapshot.userMap.remove(this.loginUser.getUsername());

            String startTime = this.startTime;
            String endTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            System.err.println("开始时间 "+startTime);
            System.err.println("结束时间 "+endTime);

            /** 5： 快照修改
             * 获取快照前数据 及 Id数据 */
            List<ErrorRate> errorRateList = new ArrayList<>();
            Collection<List<ErrorRate>> values = this.errorRateMap.values();
            List<Long> errorRateIds = new ArrayList<>();
            for (List<ErrorRate> errorRates:values){
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
            HashMap<String, List<ErrorRate>> newMap =  ErrorPackageSnapshot.getErrorRateMap(ipList);
            Collection<List<ErrorRate>> newValues = newMap.values();
            List<Long> pojoIds = new ArrayList<>();
            for (List<ErrorRate> pojoList:newValues){
                for (ErrorRate pojo:pojoList){
                    pojoIds.add(pojo.getId());
                }
            }

            /**7 获取 竣工后新增的数据 */
            pojoIds.removeAll(errorRateIds);
            if (pojoIds.size()!=0){
                System.err.println("/**7 获取 竣工后新增的数据 */ : "+pojoIds);
                errorRateService = SpringBeanUtil.getBean(IErrorRateService.class);
                errorRateService.deleteErrorRateByIds(pojoIds.toArray(new Long[pojoIds.size()]));
            }


            /*6： 重新扫描并提交扫描结果 */
            ErrorPackageMethod errorPackageMethod = new ErrorPackageMethod();
            errorPackageMethod.advancedFunction(switchParametersList,loginUser);

        }
    }

}

class ErrorPackageMethod{

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
            strings.add("错误包");
            AdvancedThreadPool.switchLoginInformations(parameterSet, strings ,false);/*RSA前面方法已经解密了，如果用true 高频扫描会第二次解密出现错误*/
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}