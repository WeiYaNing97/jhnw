package com.sgcc.web.controller.thread;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.common.utils.SecurityUtils;
import com.sgcc.web.controller.sql.SwitchInteraction;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年01月05日 11:34
 */

//线程
@Component
public class ScanThread extends Thread {

    static String mode = null;
    static String ip = null;
    static String name = null;
    static String password = null;
    static int port;
    static LoginUser loginUser;
    private static Thread myThread;
    private static String time;

    @Override
    public void run() {

        Thread thread = myThread;

        SwitchInteraction switchInteraction = new SwitchInteraction();
        //扫描方法 logInToGetBasicInformation  传参 ：mode连接方式, ip 地址, name 用户名, password 密码, port 端口号
        AjaxResult ajaxResult = switchInteraction.logInToGetBasicInformation(mode, ip, name, password, port, loginUser,time,null);

        thread.interrupt();

    }

    /**
    * @method: 接收多条 交换机登录信息 连接方式。ip，用户名，密码，端口号，登录系统人员名称
    * @Param: [objects]
    * @return: void
    * @Author: 天幕顽主
    * @E-mail: WeiYaNing97@163.com
    */
    public static void switchLoginInformations(List<Object[]> objects,String ScanningTime,LoginUser login) {
        for (Object[] objects3:objects){
            mode = (String)objects3[0];
            ip = (String)objects3[1];
            name = (String)objects3[2];
            password = (String)objects3[3];
            port = (int) objects3[4];
            loginUser = login;
            time = ScanningTime;
            Thread thread = new ScanThread();
            myThread = thread;
            thread.start();
            try {
                //Thread.sleep(1000*3);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}