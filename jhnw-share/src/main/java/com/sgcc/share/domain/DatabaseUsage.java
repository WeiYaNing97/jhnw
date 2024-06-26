package com.sgcc.share.domain;

/**
 * @program: jhnw
 * @description: 访问数据库获取数据库使用情况
 * @author:
 * @create: 2024-06-20 10:51
 **/
public class DatabaseUsage {
    private String databaseName;
    private double usedSpace;
    private double freeSpace;

    // getter和setter方法

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public double getUsedSpace() {
        return usedSpace;
    }

    public void setUsedSpace(double usedSpace) {
        this.usedSpace = usedSpace;
    }

    public double getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(double freeSpace) {
        this.freeSpace = freeSpace;
    }

    public Integer getUsedSpacePercent() {
        return (int) (usedSpace / (usedSpace+freeSpace) * 100 );
    }


    @Override
    public String toString() {
        return "DatabaseUsage{" +
                "databaseName='" + databaseName + '\'' +
                ", usedSpace=" + usedSpace +
                ", freeSpace=" + freeSpace +
                '}';
    }
}
