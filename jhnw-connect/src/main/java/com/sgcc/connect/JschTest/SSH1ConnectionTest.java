package com.sgcc.connect.JschTest;

import com.jcraft.jsch.JSchException;

public class SSH1ConnectionTest {
    public static void main(String[] args) {
        SSH1Connection ssh1Connection = new SSH1Connection();
        try {
            ssh1Connection.connect("192.168.1.1", 22, "admin", "admin");
            ssh1Connection.executeCommand("ls");
        } catch (JSchException e) {
            e.printStackTrace();
        }
        ssh1Connection.disconnect();
    }
}