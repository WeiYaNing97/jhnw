package com.sgcc.share.domain;

import com.sgcc.common.annotation.Excel;

import java.util.List;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年08月12日 11:33
 */
public class ScanResultsCO {

    public Long hproblemId;

    public Long id;

    /** 扫描时间 */
    @Excel(name = "扫描时间")
    private String createTime;

    public List<ScanResultsVO> scanResultsVOList;

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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<ScanResultsVO> getScanResultsVOList() {
        return scanResultsVOList;
    }

    public void setScanResultsVOList(List<ScanResultsVO> scanResultsVOList) {
        this.scanResultsVOList = scanResultsVOList;
    }

    @Override
    public String toString() {
        return "ScanResultsCO{" +
                "hproblemId=" + hproblemId +
                ", id=" + id +
                ", createTime='" + createTime + '\'' +
                ", scanResultsVOList=" + scanResultsVOList +
                '}';
    }
}