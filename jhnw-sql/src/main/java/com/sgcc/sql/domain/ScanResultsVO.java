package com.sgcc.sql.domain;

import com.sgcc.common.annotation.Excel;

import java.util.List;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年01月12日 11:44
 */
public class ScanResultsVO {

    public Long hproblemId;

    public Long id;

    /** 交换机ip */
    @Excel(name = "交换机ip")
    private String switchIp;

    public List<SwitchProblemVO> switchProblemVOList;

    public String getSwitchIp() {
        return switchIp;
    }

    public void setSwitchIp(String switchIp) {
        this.switchIp = switchIp;
    }

    public List<SwitchProblemVO> getSwitchProblemVOList() {
        return switchProblemVOList;
    }

    public void setSwitchProblemVOList(List<SwitchProblemVO> switchProblemVOList) {
        this.switchProblemVOList = switchProblemVOList;
    }

    public Long getHproblemId() {
        return hproblemId;
    }

    public void setHproblemId(Long hproblemId) {
        this.hproblemId = hproblemId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ScanResultsVO{" +
                "hproblemId=" + hproblemId +
                ", id=" + id +
                ", switchIp='" + switchIp + '\'' +
                ", switchProblemVOList=" + switchProblemVOList +
                '}';
    }
}