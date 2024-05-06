package com.sgcc.sql.util;

import com.sgcc.advanced.thread.AdvancedThreadPool;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.utils.bean.BeanUtils;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.PathHelper;
import com.sgcc.share.webSocket.WebSocketService;
import com.sgcc.sql.domain.TimedTaskVO;
import org.apache.poi.ss.formula.functions.T;

import java.io.IOException;
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
        System.err.println(timedTaskVO);
    }
    @Override
    public void run() {
        Tasks.method(this.switchLoginInformations,this.timedTaskVO.getFunctionName(),this.loginUser);
    }
}

class Tasks{
    public static void method(List<SwitchLoginInformation> switchLoginInformations,List<String> functionName,LoginUser loginUser) {

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

        try {
            /*高级功能线程池*/
            //boolean isRSA = true; //前端数据是否通过 RSA 加密后传入后端
            AdvancedThreadPool.switchLoginInformations(parameterSet, functionName,false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.err.println("扫描结束");

    }
}