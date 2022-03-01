package com.sgcc.connect.method;

import com.sgcc.connect.util.TelnetOperator;
import com.sgcc.connect.util.TelnetSwitch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年03月01日 15:28
 */
@RestController
@ResponseBody
@RequestMapping("/sql/TelnetTest")
public class TelnetTest {

    @RequestMapping("/telnetOperator")
    public String telnetOperator(){
        TelnetOperator telnet = new TelnetOperator("VT220",">");	//Windows,用VT220,否则会乱码
        telnet.login("192.168.1.1", 23, "admin", "admin");

        System.out.print("\r\n"+"1:display cu");
        String rs = telnet.sendCommandAll("display cu");
        try {
            rs = new String(rs.getBytes("ISO-8859-1"),"GBK");	//转一下编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.err.println("\r\n"+rs);


        System.out.print("\r\n"+"2:display ver");
        rs = telnet.sendCommandAll("display ver");
        try {
            rs = new String(rs.getBytes("ISO-8859-1"),"GBK");	//转一下编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.err.println("\r\n"+rs);



        System.out.print("\r\n"+"3:display cu");
        rs = telnet.sendCommandAll("display cu");
        try {
            rs = new String(rs.getBytes("ISO-8859-1"),"GBK");	//转一下编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.err.println("\r\n"+rs);





        rs = telnet.sendCommandAll("4:display ver");
        try {
            rs = new String(rs.getBytes("ISO-8859-1"),"GBK");	//转一下编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.err.println("\r\n"+rs);


        System.out.print("\r\n"+"5:display cu");
        rs = telnet.sendCommandAll("display cu");
        try {
            rs = new String(rs.getBytes("ISO-8859-1"),"GBK");	//转一下编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.err.println("\r\n"+rs);





        rs = telnet.sendCommandAll("6:display ver");
        try {
            rs = new String(rs.getBytes("ISO-8859-1"),"GBK");	//转一下编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.err.println("\r\n"+rs);


        return rs;
    }
}