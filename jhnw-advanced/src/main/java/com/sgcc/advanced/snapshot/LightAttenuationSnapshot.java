package com.sgcc.advanced.snapshot;
import com.alibaba.fastjson.JSON;
import com.sgcc.advanced.domain.LightAttenuationComparison;
import com.sgcc.advanced.domain.SnapshotTaskTime;
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
import com.sgcc.share.util.MyUtils;
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
 * @description: 光纤衰耗快照功能
 * @desc
 * @author:
 * @create: 2023-12-28 15:19
 **/

/* 一、开工快照：
 * 1、用户：记录开工时间，输入要监测的设备ip
 * 2、逻辑：把最近一次扫描结果，按照用户指定ip提取出数据出来，作为开工快照时间点数据。比如最近数据为当天夜里1点的数据，现在要变成开工快照时间点数据
 * 二、高频扫描（扫描周期）：
 * 1、用户指定高频时间，比如5分钟（最小间隔）
 * 2、逻辑：按照用户指定的ip设备和时间间隔开展扫描及告警，并记录（保持与主逻辑一致）。
 * 三、竣工：
 * 逻辑：1、开展一次用户指定设备的监测扫描并记录，与快照数据进行比对，发现问题及时告警。其中，以快照数据作为比对依据，本次扫描数据修正仅根据快照数据进行修正。高频扫描期间的数据不参与数据修正。修正数据包括：扫描次数、平均值2项
 */

@Api("光衰快照功能")
@RestController
@RequestMapping("/advanced/LightAttenuationSnapshot")
public class LightAttenuationSnapshot {

    @Autowired
    private static ILightAttenuationComparisonService lightAttenuationComparisonService;
    public static Map<String,Boolean> userMap = new HashMap<>();
    public static Map<String, SnapshotTaskTime> snapshotTaskTimeMap = new HashMap<>();

    /**
     * 开始快照
     *
     * @param switchInformation 交换机信息列表，格式为json字符串
     * @return 无返回值
     * @throws Exception 可能会抛出异常
     * @description 将字符串格式的交换机登录信息转化为json格式的登录信息，并启动定时任务进行光衰信息快照
     * @author charles
     * @createTime 2023/12/29 15:51
     */
    @RequestMapping("/startSnapshot")
    public static void startSnapshot(@RequestBody List<String> switchInformation) {
        // 初始化交换机参数列表和IP列表
        // 预设多线程参数 Object[] 中的参数格式为： {mode,ip,name,password,port}
        List<SwitchParameters> switchParametersList = new ArrayList<>();
        List<String> ips = new ArrayList<>();

        // 将字符串格式的交换机登录信息转化为json格式的登录信息
        for (String information:switchInformation){
            SwitchLoginInformation switchLoginInformation = JSON.parseObject(information, SwitchLoginInformation.class);
            SwitchParameters switchParameters = new SwitchParameters();
            switchParameters.setLoginUser(SecurityUtils.getLoginUser());

            // 将交换机的属性信息复制到新的交换机参数对象中
            BeanUtils.copyBeanProp(switchParameters,switchLoginInformation);
            // 设置交换机的端口号
            switchParameters.setPort(Integer.valueOf(switchLoginInformation.getPort()).intValue());

            // 将IP添加到IP列表中
            //连接方式，ip，用户名，密码，端口号
            ips.add(switchParameters.getIp());

            // RSA解密密码 和 配置密文
            String password = RSAUtils.decryptFrontEndCiphertext(switchParameters.getPassword());
            switchParameters.setPassword(password);
            String ConfigureCiphers = RSAUtils.decryptFrontEndCiphertext(switchParameters.getConfigureCiphers());
            switchParameters.setConfigureCiphers(ConfigureCiphers);

            // 将交换机参数添加到交换机参数列表中
            switchParametersList.add(switchParameters);
        }

        // 从数据库中查询光衰信息，并存储到全局变量中
        HashMap<String, List<LightAttenuationComparison>> LightAttenuationComparisonsMap = getLightAttenuationComparisonMap(ips);

        // 启动线程池
        // 获取当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();

        // 标记当前用户已经开始快照
        LightAttenuationSnapshot.userMap.put(loginUser.getUsername(), true);

        // 初始化快照任务时间对象，并设置交换机参数列表和开始时间
        SnapshotTaskTime snapshotTaskTime = new SnapshotTaskTime();
        snapshotTaskTime.setSwitchParametersList(switchParametersList);
        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        snapshotTaskTime.setStartTime(startTime);

        // 将快照任务时间对象存储到全局变量中
        LightAttenuationSnapshot.snapshotTaskTimeMap.put(loginUser.getUsername(),snapshotTaskTime);

        // 打印当前已经开始快照的用户
        Set<String> keySet = LightAttenuationSnapshot.userMap.keySet();
        for (String key:keySet){
            System.err.println("LightAttenuationSnapshot.userMap == "+key);
        }

        // 初始化定时器
        Timer timer = new Timer();

        // 初始化定时任务对象，并设置开始时间、交换机参数列表、定时器、当前登录用户和光衰信息比较映射
        LuminousAttenuationTimed task = new LuminousAttenuationTimed(startTime,switchParametersList, timer, loginUser,LightAttenuationComparisonsMap);/*交换机登录方式*/

        // 设置执行间隔时间为1分钟
        Integer time = 1;

        // 启动定时任务，初始延迟为0毫秒，执行间隔为1分钟
        /*long delay = 0; // 延迟时间，单位为毫秒
        long period = time * 60 * 1000; // 执行间隔，单位为毫秒*/
        timer.schedule(task, 0, time * 60 * 1000);
    }

    /**
     * 根据IP地址列表获取光衰信息对比映射。
     *
     * @param ips IP地址列表
     * @return 光衰信息对比映射
     */
    public static HashMap< String , List<LightAttenuationComparison> > getLightAttenuationComparisonMap(List<String> ips) {
        // 创建一个用于存储光衰信息对比映射的HashMap对象
        HashMap< String , List<LightAttenuationComparison> > lightAttenuationComparisons = new HashMap<>();

        // 获取ILightAttenuationComparisonService接口的实例对象
        lightAttenuationComparisonService = SpringBeanUtil.getBean(ILightAttenuationComparisonService.class);

        // 遍历IP地址列表
        for (String ip:ips) {
            // 调用ILightAttenuationComparisonService接口的selectPojoListByIP方法，传入IP地址作为参数，获取对应的光衰信息列表
            // 并将IP地址和光衰信息列表作为键值对存入HashMap中
            lightAttenuationComparisons.put(ip, lightAttenuationComparisonService.selectPojoListByIP(ip));
        }

        // 返回存储了光衰信息对比映射的HashMap对象
        return lightAttenuationComparisons;
    }

    /**
     * 终止线程（竣工）
     *
     * @param isDelete 是否删除数据，true表示删除，false表示不删除
     * @return 无返回值
     */
    @RequestMapping("/threadInterrupt/{isDelete}")
    public static void threadInterrupt(@PathVariable boolean isDelete) {
        // 获取当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 将用户标记为已终止快照
        LightAttenuationSnapshot.userMap.put(loginUser.getUsername(),false);
        // 获取当前用户的快照任务时间对象
        SnapshotTaskTime snapshotTaskTime = LightAttenuationSnapshot.snapshotTaskTimeMap.get(loginUser.getUsername());
        // 如果快照任务时间对象为空，则直接返回
        if (snapshotTaskTime == null){
            return;
        }
        // 从快照任务时间映射中移除当前用户的快照任务时间对象
        LightAttenuationSnapshot.snapshotTaskTimeMap.remove(loginUser.getUsername());
        if (isDelete){
            // 获取快照任务的开始时间
            String startTime = snapshotTaskTime.getStartTime();
            // 获取当前时间作为结束时间
            String endTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            // 获取交换机扫描结果服务
            ISwitchScanResultService switchScanResultService = SpringBeanUtil.getBean(ISwitchScanResultService.class);
            // 设定范式名称为"光衰"
            String temProName = "光衰";
            // 遍历快照任务中的交换机参数列表
            for (SwitchParameters pojo:snapshotTaskTime.getSwitchParametersList()){
                // 调用交换机扫描结果服务，根据IP地址、范式名称、开始时间和结束时间删除数据
                // 并返回删除的数据条数
                int number = switchScanResultService.deleteSwitchScanResultByIPAndTime(pojo.getIp(),temProName,startTime,endTime);
                // 打印删除的数据条数
                System.err.println(number);
            }
        }
    }

}

class LuminousAttenuationTimed  extends TimerTask{

    @Autowired
    private static ILightAttenuationComparisonService lightAttenuationComparisonService;

    private String startTime = null;
    private List<SwitchParameters> switchParametersList = null;
    private  Timer timer = null;
    private LoginUser loginUser = null;
    /**  开启快照前数据 */
    private HashMap<String, List<LightAttenuationComparison>> LightAttenuationComparisonsMap = null;

    /**
     * 构造函数，创建一个LuminousAttenuationTimed对象
     *
     * @param startTime 定时任务开始时间
     * @param switchParametersList 交换机参数列表
     * @param timer 定时器
     * @param loginUser 登录用户
     * @param LightAttenuationComparisonsMap 光衰信息对比映射
     */
    public  LuminousAttenuationTimed(String startTime,List<SwitchParameters> switchParametersList,
                                     Timer timer, LoginUser loginUser,
                                     HashMap<String, List<LightAttenuationComparison>> LightAttenuationComparisonsMap) {
        // 设置定时任务开始时间
        this.startTime = startTime;
        // 设置交换机参数列表
        this.switchParametersList = switchParametersList;
        // 设置定时器
        this.timer = timer;
        // 设置登录用户
        this.loginUser = loginUser;
        // 设置光衰信息对比映射
        this.LightAttenuationComparisonsMap = LightAttenuationComparisonsMap;
    }

    /**
     * 执行定时任务，根据用户是否开启光衰功能决定执行光衰方法或取消定时任务。
     * 若用户已开启光衰功能，则执行光衰方法；若未开启，则取消定时任务，并删除用户的光衰快照信息，
     * 遍历修改快照信息，重新扫描并提交扫描结果。
     */
    @Override
    public void run() {

        boolean thread = false;
        if (LightAttenuationSnapshot.userMap.containsKey(this.loginUser.getUsername()) && LightAttenuationSnapshot.userMap.get(this.loginUser.getUsername()) != null) {
            thread = (boolean) LightAttenuationSnapshot.userMap.get(this.loginUser.getUsername());
        }

        if (thread){
            System.err.println("光衰功能进行定时任务");
            LuminousAttenuationMethod luminousAttenuationMethod = new LuminousAttenuationMethod();
            luminousAttenuationMethod.advancedFunction(switchParametersList,loginUser);

        }else {

            this.timer.cancel(); // 取消定时任务

            System.err.println("光衰功能取消定时任务");
            LightAttenuationSnapshot.userMap.remove(this.loginUser.getUsername());

            // 快照修改 - 获取快照前数据 及 Id数据
            List<LightAttenuationComparison> lightAttenuationComparisons = new ArrayList<>();
            List<Long> lightAttenuationComparisonIds = new ArrayList<>();
            Collection<List<LightAttenuationComparison>> values = this.LightAttenuationComparisonsMap.values();
            for (List<LightAttenuationComparison> pojoList:values){
                lightAttenuationComparisons.addAll(pojoList);
                for (LightAttenuationComparison pojo:pojoList){
                    lightAttenuationComparisonIds.add(pojo.getId());
                }
            }

            // 获取竣工后的数据 及 ID集合
            List<String> ipList = new ArrayList<>();
            for (SwitchParameters pojo:switchParametersList){
                ipList.add( pojo.getIp() );
            }
            HashMap<String, List<LightAttenuationComparison>> newMap = LightAttenuationSnapshot.getLightAttenuationComparisonMap( ipList );
            Collection<List<LightAttenuationComparison>> newValues = newMap.values();
            List<Long> pojoIds = new ArrayList<>();
            for (List<LightAttenuationComparison> pojoList:newValues){
                for (LightAttenuationComparison pojo:pojoList){
                    pojoIds.add(pojo.getId());
                }
            }

            // 获取 竣工后新增的数据
            pojoIds.removeAll(lightAttenuationComparisonIds);
            if (pojoIds.size()!=0){
                System.err.println("/* 获取 竣工后新增的数据 */ : "+pojoIds);
                lightAttenuationComparisonService = SpringBeanUtil.getBean(ILightAttenuationComparisonService.class);
                lightAttenuationComparisonService.deleteLightAttenuationComparisonByIds(pojoIds.toArray(new Long[pojoIds.size()]));
            }

            // 遍历修改快照信息
            for (LightAttenuationComparison pojo:lightAttenuationComparisons){
                lightAttenuationComparisonService = SpringBeanUtil.getBean(ILightAttenuationComparisonService.class);
                lightAttenuationComparisonService.updateLightAttenuationComparison(pojo);
            }

            // 重新扫描并提交扫描结果
            LuminousAttenuationMethod luminousAttenuationMethod = new LuminousAttenuationMethod();
            luminousAttenuationMethod.advancedFunction(switchParametersList,loginUser);

        }
    }
}

class LuminousAttenuationMethod{

    /**
     * 高级功能方法，用于执行光衰扫描任务
     *
     * @param switchParametersList 交换机参数列表
     * @param loginUser 登录用户
     * @throws InterruptedException 如果线程被中断，则抛出 InterruptedException 异常
     */
    public void advancedFunction(List<SwitchParameters> switchParametersList, LoginUser loginUser) {//待测
        // 格式化当前日期时间
        String simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // 遍历交换机参数列表，设置扫描时间为当前时间
        for (SwitchParameters pojo:switchParametersList){
            pojo.setScanningTime(simpleDateFormat);
        }

        // 创建线程池参数对象
        ParameterSet parameterSet = new ParameterSet();
        // 设置交换机参数列表
        parameterSet.setSwitchParameters(switchParametersList);
        // 设置登录用户
        parameterSet.setLoginUser(loginUser);
        // 设置线程数量
        parameterSet.setThreadCount(Integer.valueOf(5+"").intValue());

        try {
            // 运行分析线程池
            // 创建一个字符串列表并添加"光衰"
            //boolean isRSA = true; //前端数据是否通过 RSA 加密后传入后端
            List<String> strings = new ArrayList<>();
            strings.add("光衰");

            // 调用线程池执行光衰扫描任务
            // 注意：RSA解密已在前面方法完成，如果使用true则高频扫描时会出现第二次解密错误
            AdvancedThreadPool.switchLoginInformations(parameterSet, strings ,false);/*RSA前面方法已经解密了，如果用true 高频扫描会第二次解密出现错误*/

        } catch (InterruptedException e) {
            // 如果线程被中断，则打印异常堆栈信息
            e.printStackTrace();
        }

    }

}