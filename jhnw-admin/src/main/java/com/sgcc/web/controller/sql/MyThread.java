package com.sgcc.web.controller.sql;

import com.sgcc.common.core.domain.AjaxResult;
import com.sgcc.sql.service.IBasicInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年01月05日 11:34
 */
//线程
@Component
public class MyThread extends Thread {

    static String mode = null;
    static String ip = null;
    static String name = null;
    static String password = null;
    static int port;

    @Override
    public void run() {
        SwitchInteraction switchInteraction = new SwitchInteraction();
        AjaxResult ajaxResult = switchInteraction.logInToGetBasicInformation(mode, ip, name, password, port);
        //System.err.println(MyThread.mode + MyThread.ip + MyThread.name + MyThread.password + MyThread.port);
        try {
            Thread.sleep(10);                 //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

}