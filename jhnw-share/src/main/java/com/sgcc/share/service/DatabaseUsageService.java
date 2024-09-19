package com.sgcc.share.service;

/**
 * @program: jhnw
 * @description: 直接访问整个数据库
 * @author:
 * @create: 2024-06-20 10:48
 **/
public interface DatabaseUsageService {



    double getDatabaseUsage(String databaseName);

    /**
     * 根据时间删除系统操作日志
     *
     * @param data 时间字符串，格式应符合数据库存储的日期时间格式
     * @return 成功删除的行数，如果删除失败则返回-1
     */
    int deleteSysOperLogByTime(String data);
}
