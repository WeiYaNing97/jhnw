package com.sgcc.sql.util;

import com.sgcc.advanced.thread.AdvancedThreadPool;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.utils.bean.BeanUtils;
import com.sgcc.share.connectutil.SpringBeanUtil;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.MyUtils;
import com.sgcc.share.webSocket.WebSocketService;
import com.sgcc.sql.domain.TimedTaskVO;
import com.sgcc.sql.domain.TotalQuestionTable;
import com.sgcc.sql.service.ITotalQuestionTableService;
import com.sgcc.sql.thread.DirectionalScanThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: jhnw
 * @description: 模板定时任务
 * @author:
 * @create: 2024-04-09 09:54
 **/
public class TemplateScheduledTasks extends TimerTask {
    private List<SwitchLoginInformation> switchLoginInformations = null;
    private Timer timer = null;
    private TimedTaskVO timedTaskVO = null;
    private LoginUser loginUser = null;
    public TemplateScheduledTasks(List<SwitchLoginInformation> switchLoginInformations, Timer timer, TimedTaskVO timedTaskVO,LoginUser loginUser){
        this.switchLoginInformations = switchLoginInformations;
        this.timer = timer;
        this.timedTaskVO = timedTaskVO;
        this.loginUser = loginUser;
    }
    @Override
    public void run() {
        Tasks.method(this.switchLoginInformations,this.timedTaskVO.getSelectFunctions(),this.loginUser);
    }
}

class Tasks{

    @Autowired
    private static ITotalQuestionTableService totalQuestionTableService;

    public static void method(List<SwitchLoginInformation> switchLoginInformations,List<String> selectFunctions,LoginUser loginUser) {

        String simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // 预设多线程参数 Object[] 中的参数格式为： {mode,ip,name,password,port}
        List<SwitchParameters> switchParametersList = new ArrayList<>();
        /*将字符串格式的用户登录信息 转化为json格式的登录信息*/
        for (SwitchLoginInformation switchLoginInformation:switchLoginInformations){

            SwitchParameters switchParameters = new SwitchParameters();
            switchParameters.setLoginUser(loginUser);
            switchParameters.setScanningTime(simpleDateFormat);
            BeanUtils.copyBeanProp(switchParameters,switchLoginInformation);
            switchParameters.setPort(Integer.valueOf(switchLoginInformation.getPort()).intValue());
            //以多线程中的格式 存放数组中
            //连接方式，ip，用户名，密码，端口号
            switchParametersList.add(switchParameters);
        }

        //线程池
        ParameterSet parameterSet = new ParameterSet();
        parameterSet.setSwitchParameters(switchParametersList);
        parameterSet.setLoginUser(loginUser);
        parameterSet.setThreadCount( 5 );

        /* 高级功能集合 */
        List<String> advancedFeatureName = new ArrayList<>();
        /* 自定义问题 集合 */
        List<Long> customQuestionCollection = new ArrayList<>();

        for (String function:selectFunctions){
            if (MyUtils.determineWhetherAStringIsAPureNumber(function)){
                customQuestionCollection.add(Long.valueOf(function).longValue());
            }else {
                advancedFeatureName.add(function);
            }
        }

        List<TotalQuestionTable> totalQuestionTables = new ArrayList<>();
        if (customQuestionCollection.size() != 0 ){

            Long[] customQuestionArray = customQuestionCollection.toArray(new Long[customQuestionCollection.size()]);
            totalQuestionTableService = SpringBeanUtil.getBean(ITotalQuestionTableService.class);//解决 多线程 service 为null问题
            totalQuestionTables = totalQuestionTableService.selectTotalQuestionTableByIds(customQuestionArray);

        }else if (advancedFeatureName.size() == 0 && customQuestionCollection.size() == 0){
            WebSocketService webSocketService = new WebSocketService();
            webSocketService.sendMessage(parameterSet.getLoginUser().getUsername(),"接收:"+"扫描结束\r\n");

        }

        try {
            //boolean isRSA = true; 密码是否 RSA加密
            DirectionalScanThreadPool directionalScanThreadPool = new DirectionalScanThreadPool();
            directionalScanThreadPool.switchLoginInformations(parameterSet, totalQuestionTables, advancedFeatureName,false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.err.println("扫描结束");

    }
}