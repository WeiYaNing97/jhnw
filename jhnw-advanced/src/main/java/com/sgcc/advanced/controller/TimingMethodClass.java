package com.sgcc.advanced.controller;
import com.sgcc.advanced.thread.AdvancedThreadPool;
import com.sgcc.advanced.thread.TimedTaskRetrievalFile;
import com.sgcc.common.core.domain.entity.SysUser;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.utils.bean.BeanUtils;
import com.sgcc.share.domain.SwitchLoginInformation;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import com.sgcc.share.parametric.ParameterSet;
import com.sgcc.share.parametric.SwitchParameters;
import com.sgcc.share.util.MyUtils;
import com.sgcc.share.util.PathHelper;
import com.sgcc.share.webSocket.WebSocketService;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: jhnw
 * @description: 定时方法类
 * @author:
 * @create: 2024-01-17 15:07
 **/
@Component("timingMethodClass")
public class TimingMethodClass {

    /**
     * @Description  定时任务
     * @author charles
     * @createTime 2023/11/8 13:46
     * @desc
     * @param
     * @return
     */
    public String advancedScheduledTasks(String nameList) {

        String simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // 预设多线程参数 Object[] 中的参数格式为： {mode,ip,name,password,port}
        List<SwitchParameters> switchParametersList = new ArrayList<>();
        List<String> functionName =new ArrayList<>();
        String[] namesplit = nameList.split(";");
        for (String name:namesplit){
            functionName.add( name );
        }

        LoginUser loginUser = new LoginUser();
        SysUser user = new SysUser();
        user.setUserName("quartz");
        loginUser.setUser(user);


        /*获取定时任务 获取交换机登录信息 集合*/
        String excelName = "交换机信息模板";

        List<SwitchLoginInformation> switchLoginInformations = TimedTaskRetrievalFile.readCiphertextExcel(MyUtils.getProjectPath()+"\\jobExcel\\"+ excelName +".txt");


        if (MyUtils.isCollectionEmpty(switchLoginInformations)){
            return "扫描结束";
        }
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
        parameterSet.setThreadCount(Integer.valueOf(5+"").intValue());

        try {
            /*高级功能线程池*/
            // boolean isRSA = false;  //前端数据是否通过 RSA 加密后传入后端  读取表格信息 密码为明文
            AdvancedThreadPool.switchLoginInformations(parameterSet, functionName,false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.err.println("扫描结束");

        AbnormalAlarmInformationMethod.afferent(parameterSet.getLoginUser().getUsername(),null,"接收："+"扫描结束\r\n");

        /*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime_10 = dateFormat.format(new Date(new Date().getTime() + 600000));
        while (true){
            if (WebSocketService.userMap.get(parameterSet.getLoginUser().getUsername()) != null){
                WebSocketService.userMap.remove(parameterSet.getLoginUser().getUsername());
                return "扫描结束";
            }
            if (dateFormat.format(new Date(new Date().getTime())).compareTo(nowTime_10) >=0 ){
                return "扫描结束";
            }
        }*/
        return "扫描结束";

    }

}
