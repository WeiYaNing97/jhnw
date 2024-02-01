package com.sgcc.share.parametric;

import com.sgcc.common.core.domain.model.LoginUser;

import java.util.List;

/**
* @Description 交换机登录信息集合
* @author charles
 * @return
*/
public class ParameterSet {

    private List<SwitchParameters> switchParameters; //交换机信息
    private LoginUser loginUser;//登陆人信息
    private Integer threadCount;//线程数

    public void setSwitchParameters(List<SwitchParameters> switchParameters) {
        this.switchParameters = switchParameters;
    }
    public void setLoginUser(LoginUser loginUser) {
        this.loginUser = loginUser;
    }
    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }
    public List<SwitchParameters> getSwitchParameters() {
        return switchParameters;
    }
    public LoginUser getLoginUser() {
        return loginUser;
    }
    public Integer getThreadCount() {
        return threadCount;
    }
    @Override
    public String toString() {
        return "ParameterSet{" +
                "switchParameters=" + switchParameters +
                ", loginUser=" + loginUser +
                ", threadCount=" + threadCount +
                '}';
    }
}
