package com.sgcc.web.controller.sql;

import com.sgcc.common.annotation.Excel;
import com.sgcc.common.annotation.Excel.ColumnType;
import com.sgcc.common.annotation.Excel.Type;
import com.sgcc.common.annotation.Excels;
import com.sgcc.common.core.domain.BaseEntity;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年06月27日 10:36
 */
public class SwitchEntry extends BaseEntity {

    /** 交换机ip */
    @Excel(name = "交换机ip", cellType = ColumnType.NUMERIC, prompt = "交换机ip")
    private String switchIp;

    /** 交换机姓名 */
    @Excel(name = "交换机姓名")
    private String switchName;

    /** 交换机密码 */
    @Excel(name = "交换机密码")
    private String switchPassword;

    /** 交换机连接方式 */
    @Excel(name = "交换机连接方式")
    private String connectionMode;

    /** 交换机连接端口号 */
    @Excel(name = "交换机连接端口号")
    private String portNumber;

    public String getSwitchIp() {
        return switchIp;
    }

    public void setSwitchIp(String switchIp) {
        this.switchIp = switchIp;
    }

    public String getSwitchName() {
        return switchName;
    }

    public void setSwitchName(String switchName) {
        this.switchName = switchName;
    }

    public String getSwitchPassword() {
        return switchPassword;
    }

    public void setSwitchPassword(String switchPassword) {
        this.switchPassword = switchPassword;
    }

    public String getConnectionMode() {
        return connectionMode;
    }

    public void setConnectionMode(String connectionMode) {
        this.connectionMode = connectionMode;
    }

    public String getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

    @Override
    public String toString() {
        return "SwitchEntry{" +
                "switchIp='" + switchIp + '\'' +
                ", switchName='" + switchName + '\'' +
                ", switchPassword='" + switchPassword + '\'' +
                ", connectionMode='" + connectionMode + '\'' +
                ", portNumber='" + portNumber + '\'' +
                '}';
    }

}