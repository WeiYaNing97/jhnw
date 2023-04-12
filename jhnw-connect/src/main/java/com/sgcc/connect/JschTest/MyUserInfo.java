package com.sgcc.connect.JschTest;

import com.jcraft.jsch.UserInfo;
public class MyUserInfo implements UserInfo {
    private String username;
    private String password;
    public MyUserInfo(String username, String password) {
        this.username = username;
        this.password = password;
    }
    @Override
    public String getPassphrase() {
        return null;
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public boolean promptPassword(String message) {
        return true;
    }
    @Override
    public boolean promptPassphrase(String message) {
        return true;
    }
    @Override
    public boolean promptYesNo(String message) {
        return true;
    }
    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}