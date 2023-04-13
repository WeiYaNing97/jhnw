package com.sgcc.sql.parametric;

import com.sgcc.common.core.domain.model.LoginUser;
import com.sgcc.sql.domain.TotalQuestionTable;

import java.util.List;

public class ParameterSet {

    private List<SwitchParameters> switchParameters; //交换机信息
    private LoginUser loginUser;//登陆人信息
    private Integer threadCount;//线程数
    private List<TotalQuestionTable> TotalQuestionTablePojoList;//交换机问题

    public void setSwitchParameters(List<SwitchParameters> switchParameters) {
        this.switchParameters = switchParameters;
    }

    public void setLoginUser(LoginUser loginUser) {
        this.loginUser = loginUser;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    public void setTotalQuestionTablePojoList(List<TotalQuestionTable> totalQuestionTablePojoList) {
        TotalQuestionTablePojoList = totalQuestionTablePojoList;
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

    public List<TotalQuestionTable> getTotalQuestionTablePojoList() {
        return TotalQuestionTablePojoList;
    }

    @Override
    public String toString() {
        return "ParameterSet{" +
                "switchParameters=" + switchParameters +
                ", loginUser=" + loginUser +
                ", threadCount=" + threadCount +
                ", TotalQuestionTablePojoList=" + TotalQuestionTablePojoList +
                '}';
    }
}
