package com.sgcc.sql.domain;

import java.util.List;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年01月21日 17:08
 */
public class ScanResults {
    List<ScanResultsVO> scanResultsVOS;

    public List<ScanResultsVO> getScanResultsVOS() {
        return scanResultsVOS;
    }

    public void setScanResultsVOS(List<ScanResultsVO> scanResultsVOS) {
        this.scanResultsVOS = scanResultsVOS;
    }

    @Override
    public String toString() {
        return "ScanResults{" +
                "scanResultsVOS=" + scanResultsVOS +
                '}';
    }
}