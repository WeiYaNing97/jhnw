package com.sgcc.connect.JschTest;

import com.jcraft.jsch.*;
public class SSH1Demo {
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";
    private static final String HOST = "192.168.1.1";
    private static final int PORT = 22;
    public static void main(String[] args) {

        String hostname = "192.168.1.1";
        String username = "admin";
        String password = "admin";
        int port = 22;
        try {
            JSch jsch = new JSch();
            jsch.setConfig("StrictHostKeyChecking", "no");
            jsch.setConfig("cipher.s2c", "blowfish-cbc,aes128-ctr,aes192-ctr,aes256-ctr");
            jsch.setConfig("cipher.c2s", "blowfish-cbc,aes128-ctr,aes192-ctr,aes256-ctr");
            jsch.setConfig("CheckCiphers", "blowfish-cbc");
            jsch.setConfig("CheckKexes", "diffie-hellman-group1-sha1");
            jsch.setConfig("kex", "diffie-hellman-group-exchange-sha256,diffie-hellman-group1-sha1,diffie-hellman-group-exchange-sha1");
            jsch.setConfig("server_host_key", "RSA,DSA,ssh-rsa,ssh-dss,rsa,dsa");
            jsch.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
            Session session = jsch.getSession(username, hostname, port);
            session.setUserInfo(new MyUserInfo(username,password));
            session.setConfig("cipher.s2c", "blowfish-cbc,aes128-ctr,aes192-ctr,aes256-ctr");
            session.setConfig("cipher.c2s", "blowfish-cbc,aes128-ctr,aes192-ctr,aes256-ctr");

            session.setConfig("mac.s2c", "hmac-sha2-256,hmac-sha2-512,hmac-sha1");
            session.setConfig("mac.c2s", "hmac-sha2-256,hmac-sha2-512,hmac-sha1");

            session.setConfig("CheckCiphers", "blowfish-cbc");
            session.setConfig("CheckKexes", "diffie-hellman-group1-sha1");
            session.setConfig("kex", "diffie-hellman-group-exchange-sha256,diffie-hellman-group1-sha1,diffie-hellman-group-exchange-sha1");
            session.setConfig("server_host_key", "RSA,DSA,ssh-rsa,ssh-dss,rsa,dsa");
            session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
            session.connect();
            System.err.println("连接成功");
        } catch (JSchException e) {
            e.printStackTrace();
        }

    }
}