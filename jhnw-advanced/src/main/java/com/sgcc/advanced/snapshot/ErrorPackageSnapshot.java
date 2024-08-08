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
     * 开始快照
     *
     * @param switchInformation 交换机信息列表，格式为字符串
     * @return 无返回值
     * @author charles
     * @createTime 2023/12/29 15:51
     * @description 将字符串格式的交换机信息转化为SwitchParameters对象列表，并启动定时任务进行快照分析
     */
    @PostMapping("/startSnapshot")
    public static void startSnapshot(@RequestBody List<String> switchInformation) {/*List<String> switchInformation,Integer time*/

        // 初始化SwitchParameters对象列表和IP列表
        // 预设多线程参数 Object[] 中的参数格式为： {mode,ip,name,password,port}
        List<SwitchParameters> switchParametersList = new ArrayList<>();
        List<String> ips = new ArrayList<>();

        // 将字符串格式的交换机信息转换为JSON格式的SwitchLoginInformation对象列表
        for (String information:switchInformation){
            SwitchLoginInformation switchLoginInformation = JSON.parseObject(information, SwitchLoginInformation.class);

            // 创建SwitchParameters对象，并设置登录用户
            SwitchParameters switchParameters = new SwitchParameters();
            switchParameters.setLoginUser( SecurityUtils.getLoginUser() );/*SecurityUtils.getLoginUser()*/

            // 将SwitchLoginInformation对象的属性复制到SwitchParameters对象中
            BeanUtils.copyBeanProp(switchParameters,switchLoginInformation);

            // 设置端口号
            switchParameters.setPort(Integer.valueOf(switchLoginInformation.getPort()).intValue());

            // 将IP地址添加到IP列表中
            //连接方式，ip，用户名，密码，端口号
            ips.add(switchParameters.getIp());

            // 对密码和配置密码进行RSA解密
            switchParameters.setPassword(RSAUtils.decryptFrontEndCiphertext(switchParameters.getPassword()));
            switchParameters.setConfigureCiphers(RSAUtils.decryptFrontEndCiphertext(switchParameters.getConfigureCiphers()));

            // 将SwitchParameters对象添加到列表中
            switchParametersList.add(switchParameters);
        }

        // 查询光衰信息并存放到全局变量中
        HashMap<String, List<ErrorRate>> errorRateMap =  getErrorRateMap(ips);

        // 启动线程
        LoginUser loginUser = SecurityUtils.getLoginUser();
        ErrorPackageSnapshot.userMap.put(loginUser.getUsername(),true);

        // 创建SnapshotTaskTime对象，并设置相关属性
        SnapshotTaskTime snapshotTaskTime = new SnapshotTaskTime();
        snapshotTaskTime.setSwitchParametersList(switchParametersList);
        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        snapshotTaskTime.setStartTime(startTime);
        ErrorPackageSnapshot.snapshotTaskTimeMap.put(loginUser.getUsername(),snapshotTaskTime);

        // 创建Timer对象
        Timer timer = new Timer();

        // 创建ErrorPackageTimed任务对象
        ErrorPackageTimed task = new ErrorPackageTimed(startTime, switchParametersList, timer, loginUser ,errorRateMap);/*交换机登录方式*/

        // 设置定时任务的执行时间间隔
        Integer time = 1 ;
        // timer.schedule(task, delay, period);
        /*long delay = 0; // 延迟时间，单位为毫秒
        long period = time * 60 * 1000; // 执行间隔，单位为毫秒*/
        timer.schedule(task, 0, time * 60 * 1000);

    }

    /**
     * 根据IP地址列表获取错误率列表的映射关系
     *
     * @param ips IP地址列表
     * @return 错误率列表的映射关系，键为IP地址，值为错误率列表
     */
    public static HashMap< String , List<ErrorRate> > getErrorRateMap(List<String> ips) {
        // 创建一个错误率列表的映射关系
        HashMap< String , List<ErrorRate> > errorRateListMap = new HashMap<>();
        // 获取错误率服务类的实例
        errorRateService = SpringBeanUtil.getBean(IErrorRateService.class);
        // 遍历IP地址列表
        for (String ip : ips) {
            // 根据IP地址获取错误率列表，并放入映射关系中
            errorRateListMap.put(ip, errorRateService.selectPojoListByIP(ip));
        }
        // 返回错误率列表的映射关系
        return errorRateListMap;
    }

    /**
     * 终止线程
     *
     * @param isDelete 是否删除数据，true表示删除，false表示不删除
     * @return 无返回值
     * @PostMapping("/threadInterrupt/{isDelete}") 接收一个路径变量isDelete，用于指示是否删除数据
     * @PathVariable boolean isDelete 路径变量，表示是否删除数据
     */
    @PostMapping("/threadInterrupt/{isDelete}")
    public static void threadInterrupt(@PathVariable boolean isDelete) {
        // 获取当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 将用户名与false值放入userMap中，表示线程中断
        ErrorPackageSnapshot.userMap.put(loginUser.getUsername(),false);

        // 根据用户名获取线程任务时间对象
        SnapshotTaskTime snapshotTaskTime = ErrorPackageSnapshot.snapshotTaskTimeMap.get(loginUser.getUsername());
        // 从线程任务时间映射中移除该用户的线程任务时间对象
        ErrorPackageSnapshot.snapshotTaskTimeMap.remove(loginUser.getUsername());

        if (isDelete){
            // 获取线程任务开始时间
            String startTime = snapshotTaskTime.getStartTime();
            // 获取当前时间，并格式化为字符串
            String endTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            // 获取ISwitchScanResultService接口的实现类对象
            ISwitchScanResultService switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);
            // 设定范式名称为"错误包"
            String temProName = "错误包";

            // 遍历线程任务时间对象中的SwitchParameters列表
            for (SwitchParameters pojo:snapshotTaskTime.getSwitchParametersList()){
                // 根据IP、范式名称、开始时间和结束时间删除数据
                int number = switchScanResultService.deleteSwitchScanResultByIPAndTime(pojo.getIp(),temProName,startTime,endTime);
                // 输出删除的数据条数
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

    /**
     * 构造一个ErrorPackageTimed对象
     *
     * @param startTime 开始时间
     * @param switchParametersList 交换机参数列表
     * @param timer 定时器
     * @param loginUser 登录用户
     * @param errorRateMap 光衰信息映射表
     */
    public  ErrorPackageTimed(String startTime,List<SwitchParameters> switchParametersList, Timer timer, LoginUser loginUser,HashMap<String, List<ErrorRate>> errorRateMap) {
        this.startTime = startTime;
        this.switchParametersList = switchParametersList;
        this.timer = timer;
        this.loginUser = loginUser;
        this.errorRateMap = errorRateMap;
    }

    /**
     * 重写Thread类的run方法，用于执行线程任务。
     * 根据线程是否启动的状态，执行不同的操作。
     * 如果线程已启动，则执行高级功能函数；
     * 如果线程未启动，则取消定时任务，移除用户信息，并获取竣工前后数据对比结果，执行重新扫描并提交扫描结果操作。
     */
    @Override
    public void run() {
        // 线程是否启动的标志位
        boolean thread = false;
        // 判断用户是否存在于userMap中，并且对应的值不为null
        if (ErrorPackageSnapshot.userMap.containsKey(this.loginUser.getUsername()) && ErrorPackageSnapshot.userMap.get(this.loginUser.getUsername()) != null) {
            // 获取用户对应的线程状态，并赋值给thread
            thread = (boolean) ErrorPackageSnapshot.userMap.get(this.loginUser.getUsername());
        }

        // 如果线程已启动
        if (thread){
            // 创建ErrorPackageMethod对象
            ErrorPackageMethod errorPackageMethod = new ErrorPackageMethod();
            // 调用高级功能函数
            errorPackageMethod.advancedFunction(switchParametersList,loginUser);
        // 如果线程未启动
        } else {
            // 取消定时任务
            this.timer.cancel();
            // 从userMap中移除用户信息
            ErrorPackageSnapshot.userMap.remove(this.loginUser.getUsername());

            // 获取开始时间和结束时间
            /*String startTime = this.startTime;
            String endTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            System.err.println("开始时间 " + startTime);
            System.err.println("结束时间 " + endTime);*/

            // 快照修改：获取快照前数据 及 Id数据
            List<ErrorRate> errorRateList = new ArrayList<>();
            Collection<List<ErrorRate>> values = this.errorRateMap.values();
            List<Long> errorRateIds = new ArrayList<>();
            for (List<ErrorRate> errorRates:values){
                errorRateList.addAll(errorRates);
                for (ErrorRate errorRate:errorRates){
                    errorRateIds.add(errorRate.getId());
                }
            }

            // 获取竣工后的数据 及 ID集合
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

            // 获取 竣工后新增的数据
            pojoIds.removeAll(errorRateIds);
            if (pojoIds.size()!=0){
                System.err.println("/* 获取 竣工后新增的数据 */ : " + pojoIds);
                // 获取IErrorRateService接口的实例
                errorRateService = SpringBeanUtil.getBean(IErrorRateService.class);
                // 删除新增的数据
                errorRateService.deleteErrorRateByIds(pojoIds.toArray(new Long[pojoIds.size()]));
            }

            // 重新扫描并提交扫描结果
            ErrorPackageMethod errorPackageMethod = new ErrorPackageMethod();
            errorPackageMethod.advancedFunction(switchParametersList,loginUser);
        }
    }

}

class ErrorPackageMethod{

    /**
     * 执行高级功能函数
     *
     * @param switchParametersList SwitchParameters列表
     * @param loginUser 登录用户信息
     * @throws InterruptedException 线程中断异常
     */
    public void advancedFunction(List<SwitchParameters> switchParametersList, LoginUser loginUser) {
        // 设置当前日期时间格式并获取当前时间字符串
        String simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // 遍历SwitchParameters列表，设置每个对象的扫描时间为当前时间
        for (SwitchParameters pojo:switchParametersList){
            pojo.setScanningTime(simpleDateFormat);
        }

        // 创建一个ParameterSet对象
        ParameterSet parameterSet = new ParameterSet();

        // 设置ParameterSet对象的SwitchParameters列表
        parameterSet.setSwitchParameters(switchParametersList);

        // 设置ParameterSet对象的登录用户信息
        parameterSet.setLoginUser(loginUser);

        // 设置ParameterSet对象的线程数量为5
        parameterSet.setThreadCount(Integer.valueOf(5+"").intValue());

        try {
            // 创建一个字符串列表并添加"错误包"
            List<String> strings = new ArrayList<>();
            strings.add("错误包");

            // 调用AdvancedThreadPool的switchLoginInformations方法，传入参数并执行（注意：RSA前面方法已经解密了，如果用true 高频扫描会第二次解密出现错误）
            AdvancedThreadPool.switchLoginInformations(parameterSet, strings ,false);/*RSA前面方法已经解密了，如果用true 高频扫描会第二次解密出现错误*/
        } catch (InterruptedException e) {
            // 如果发生线程中断异常，则打印异常堆栈信息
            e.printStackTrace();
        }
    }
}