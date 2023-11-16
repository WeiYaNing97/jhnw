package com.sgcc.sql.domain;

/**
 * @program: jhnw
 * @description: 交换机执行命令返回结果
 * @author:
 * @create: 2023-11-10 16:12
 **/
public class CommandReturn {
    /*命令是否执行成功*/
    private boolean successOrNot;
    /*执行命令返回结果*/
    private String returnResults;
    /*分析ID*/
    private String analysisID;
    public boolean isSuccessOrNot() {
        return successOrNot;
    }
    public void setSuccessOrNot(boolean successOrNot) {
        this.successOrNot = successOrNot;
    }
    public String getReturnResults() {
        return returnResults;
    }
    public void setReturnResults(String returnResults) {
        this.returnResults = returnResults;
    }
    public String getAnalysisID() {
        return analysisID;
    }
    public void setAnalysisID(String analysisID) {
        this.analysisID = analysisID;
    }
}
