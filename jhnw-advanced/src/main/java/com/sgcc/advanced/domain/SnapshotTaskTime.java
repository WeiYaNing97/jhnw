package com.sgcc.advanced.domain;

import com.sgcc.share.parametric.SwitchParameters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: jhnw
 * @description: 快照任务时间实体类
 * @author:
 * @create: 2024-05-15 11:55
 **/
public class SnapshotTaskTime {

    private List<SwitchParameters> switchParametersList = null;
    private String startTime = null;
    private String endTime =  null;

    public List<SwitchParameters> getSwitchParametersList() {
        return switchParametersList;
    }

    public void setSwitchParametersList(List<SwitchParameters> switchParametersList) {
        this.switchParametersList = switchParametersList;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
