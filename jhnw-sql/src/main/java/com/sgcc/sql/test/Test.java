package com.sgcc.sql.test;

import com.sgcc.sql.controller.Configuration;
import com.sgcc.sql.util.MyUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Test {
    public static void main(String[] args) {
        //InetAddress address = InetAddress.getByName("itheima");
        InetAddress address = null;
        try {
            address = InetAddress.getByName("192.168.1.98");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //public String getHostName()：获取此IP地址的主机名
        String name = address.getHostName();
        //public String getHostAddress()：返回文本显示中的IP地址字符串
        String ip = address.getHostAddress();

        System.out.println("主机名：" + name);
        System.out.println("IP地址：" + ip);
    }
}
