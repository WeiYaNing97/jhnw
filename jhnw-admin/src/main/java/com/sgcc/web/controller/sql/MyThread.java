package com.sgcc.web.controller.sql;

import com.sgcc.common.core.domain.AjaxResult;
import org.springframework.stereotype.Component;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年01月05日 11:34
 */
//线程
@Component
public class MyThread extends Thread {

    public String[] informationArray;

    public MyThread(String[] informationArray){
        this.informationArray = informationArray;
    }

    @Override
    public void run() {
        String mode =informationArray[0];
        String ip = informationArray[1];
        String name =informationArray[2];
        String password =informationArray[3];
        int port =  Integer.valueOf(informationArray[4]).intValue();
        SwitchInteraction switchInteraction = new SwitchInteraction();
        AjaxResult ajaxResult = switchInteraction.logInToGetBasicInformation(mode, ip, name, password, port);
        try {
            Thread.sleep(10);                 //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}