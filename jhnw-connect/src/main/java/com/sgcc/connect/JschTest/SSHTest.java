package com.sgcc.connect.JschTest;

import com.jcraft.jsch.*;
import java.util.Properties;
public class SSHTest {

    public static void main(String[] args) {
        String host = "192.168.1.1";
        String user = "admin";
        String password = "admin";
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            config.put("cipher.s2c", "aes128-cbc,aes192-cbc,aes256-cbc");
            config.put("cipher.c2s", "aes128-cbc,aes192-cbc,aes256-cbc");
            config.put("mac.s2c", "hmac-sha2-256,hmac-sha2-512,hmac-sha1");
            config.put("mac.c2s", "hmac-sha2-256,hmac-sha2-512,hmac-sha1");
            config.put("kex", "diffie-hellman-group-exchange-sha256,diffie-hellman-group-exchange-sha1");
            session.setConfig(config);


            session.setPassword(password);
            session.setTimeout(30000); // 连接超时时间为30秒

            Channel channel = session.openChannel("shell");
            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);
            channel.connect();


            System.err.println("连接成功");


            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

}
