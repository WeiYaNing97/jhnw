package com.sgcc.share.parametric;

import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.share.connectutil.SshConnect;
import com.sgcc.share.connectutil.TelnetComponent;
import com.sgcc.share.method.SshMethod;
import com.sgcc.share.method.TelnetSwitchMethod;

/**
* @Description 贯穿整个扫描过程的集合
*/

public class SwitchParameters {

    private String scanMark; //扫描标记

    private LoginUser loginUser;//登陆人信息
    private String threadName;//线程名
    private String scanningTime;//扫描时间
    private String notFinished;
    /**
     * 交换机登录信息
     */
    private String ip; //IP地址
    private String name; //用户名
    private String password; //密码
    private String configureCiphers;//配置密码
    private String mode;//连接方式
    private Integer port;//连接端口号
    /**
     * 交换机属性信息
     */
    private String routerFlag;
    private String deviceModel;//设备型号
    private String deviceBrand;//设备品牌
    private String firmwareVersion;//内部固件版本
    private String subversionNumber;//子版本号
    /**
     * 交换机连接方法
     */
    private SshMethod connectMethod;//ssh连接方法
    private TelnetSwitchMethod telnetSwitchMethod;//telnet连接方法
    private SshConnect sshConnect;//(JSCH 使用方法类)
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
    public Integer getPort() {
        return port;
    }
    public void setPort(Integer port) {
        this.port = port;
    }
    public LoginUser getLoginUser() {
        return loginUser;
    }
    public void setLoginUser(LoginUser loginUser) {
        this.loginUser = loginUser;
    }
    public String getThreadName() {
        return threadName;
    }
    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
    public String getRouterFlag() {
        return routerFlag;
    }
    public void setRouterFlag(String routerFlag) {
        this.routerFlag = routerFlag;
    }
    public String getScanningTime() {
        return scanningTime;
    }
    public void setScanningTime(String scanningTime) {
        this.scanningTime = scanningTime;
    }
    public String getNotFinished() {
        return notFinished;
    }
    public void setNotFinished(String notFinished) {
        this.notFinished = notFinished;
    }


    public String getScanMark() {
        return scanMark;
    }

    public void setScanMark(String scanMark) {
        this.scanMark = scanMark;
    }
}
