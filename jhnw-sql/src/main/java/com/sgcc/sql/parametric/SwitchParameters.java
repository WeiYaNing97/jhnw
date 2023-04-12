package com.sgcc.sql.parametric;

import com.sgcc.connect.method.SshMethod;
import com.sgcc.connect.method.TelnetSwitchMethod;
import com.sgcc.connect.util.SshConnect;
import com.sgcc.connect.util.TelnetComponent;

import java.util.HashMap;
import java.util.Map;

public class SwitchParameters {
    /**
     * 交换机登录信息
     */
    private String ip; //IP地址
    private String name; //用户名
    private String password; //密码
    private String configureCiphers;//配置密码
    private String mode;//连接方式

    /**
     * 交换机属性信息
     */
    private String deviceModel;//设备型号
    private String deviceBrand;//设备品牌
    private String firmwareVersion;//内部固件版本
    private String subversionNumber;//子版本号

    /**
     * 交换机连接方法
     */
    private SshMethod connectMethod;//ssh连接方法
    private TelnetSwitchMethod telnetSwitchMethod;//telnet连接方法
    private SshConnect sshConnect;//ssh连接工具
    private TelnetComponent telnetComponent;//telnet连接工具


    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfigureCiphers(String configureCiphers) {
        this.configureCiphers = configureCiphers;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public void setSubversionNumber(String subversionNumber) {
        this.subversionNumber = subversionNumber;
    }

    public void setConnectMethod(SshMethod connectMethod) {
        this.connectMethod = connectMethod;
    }

    public void setTelnetSwitchMethod(TelnetSwitchMethod telnetSwitchMethod) {
        this.telnetSwitchMethod = telnetSwitchMethod;
    }

    public void setSshConnect(SshConnect sshConnect) {
        this.sshConnect = sshConnect;
    }

    public void setTelnetComponent(TelnetComponent telnetComponent) {
        this.telnetComponent = telnetComponent;
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getConfigureCiphers() {
        return configureCiphers;
    }

    public String getMode() {
        return mode;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public String getSubversionNumber() {
        return subversionNumber;
    }

    public SshMethod getConnectMethod() {
        return connectMethod;
    }

    public TelnetSwitchMethod getTelnetSwitchMethod() {
        return telnetSwitchMethod;
    }

    public SshConnect getSshConnect() {
        return sshConnect;
    }

    public TelnetComponent getTelnetComponent() {
        return telnetComponent;
    }

    @Override
    public String toString() {
        return "SwitchParameters{" +
                "ip='" + ip + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", configureCiphers='" + configureCiphers + '\'' +
                ", mode='" + mode + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", deviceBrand='" + deviceBrand + '\'' +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", subversionNumber='" + subversionNumber + '\'' +
                ", connectMethod=" + connectMethod +
                ", telnetSwitchMethod=" + telnetSwitchMethod +
                ", sshConnect=" + sshConnect +
                ", telnetComponent=" + telnetComponent +
                '}';
    }
}
