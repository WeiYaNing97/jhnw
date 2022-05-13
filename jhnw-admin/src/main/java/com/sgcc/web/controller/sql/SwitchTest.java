package com.sgcc.web.controller.sql;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年01月07日 9:43
 */
@RestController
@RequestMapping("/sql/SwitchTest")
public class SwitchTest {

    @RequestMapping("/testTelnet")
    public void testTelnet(){
        String string = "\r\n%Apr  4 03:00:49:885 2000 H3C SHELL 5 LOGIN:- 1 - admin(192.168.1.98) in unit1 login\n" +
                "\r\n%Apr  4 03:04:03:302 2000 H3C SHELL 5 LOGOUT:- 1 - admin(192.168.1.98) in unit1 logout \n" +
                "\r\n%Apr  4 03:06:17:304 2000 H3C SHELL 5 LOGOUT:- 1 - admin(192.168.1.99) in unit1 logout \n" +
                "\r\n%Apr  4 03:07:30:301 2000 H3C SHELL 5 LOGOUT:- 1 - root(192.168.1.99) in unit1 logout \n" +
                "\r\n%Apr  4 02:57:29:902 2000 H3C SHELL 5 LOGIN:- 1 - root(192.168.1.99) in unit1 login\n" +
                "\r\n%Apr  4 03:06:17:306 2000 H3C SHELL 5 LOGOUT:interface Ethernet1/0/2";
        //String removeLoginInformation =TranSlate.tranSlate(string);
        String removeLoginInformation = Utils.removeLoginInformation(string);
        System.out.print(removeLoginInformation);
    }


    @RequestMapping("/compareVersionText")
    public void compareVersionText(){
        String compare = "10.37.65<ver<=20.36.59";
        boolean compareVersionBoolean = Utils.compareVersion(null,compare);
    }

}