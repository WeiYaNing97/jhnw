package com.sgcc.sql.test;

import cn.hutool.extra.ssh.JschUtil;
import com.jcraft.jsch.Session;

public class JschUtilTest {

    public static void main(String[] args) {
        Session session = JschUtil.getSession("192.168.1.100", 22, "admin", "admin");
        JschUtil.bindPort(session, "192.168.0.102", 8181, 8181);
    }
}
