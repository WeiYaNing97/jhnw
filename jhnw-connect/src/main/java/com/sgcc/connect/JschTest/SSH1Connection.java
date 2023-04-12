package com.sgcc.connect.JschTest;

import com.jcraft.jsch.*;
public class SSH1Connection {
    private JSch jsch;
    private Session session;
    public void connect(String host, int port, String username, String password) throws JSchException {
        jsch = new JSch();
        session = jsch.getSession(username, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setConfig("cipher.s2c", "3des-cbc");
        session.setConfig("cipher.c2s", "3des-cbc");
        session.setConfig("mac.s2c", "hmac-md5");
        session.setConfig("mac.c2s", "hmac-md5");
        session.setConfig("compression.s2c", "zlib");
        session.setConfig("compression.c2s", "zlib");
        session.setConfig("kex", "diffie-hellman-group1-sha1");
        session.connect();
    }
    public void disconnect() {
        if (session != null) {
            session.disconnect();
        }
    }
    public void executeCommand(String command) throws JSchException {
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);
        channel.connect();
        channel.disconnect();
    }
}