package com.sgcc.share.service.impl;

import com.sgcc.share.domain.DatabaseUsage;
import com.sgcc.share.mapper.DatabaseUsageMapper;
import com.sgcc.share.service.DatabaseUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: jhnw
 * @description: 直接访问数据库实现类
 * @author:
 * @create: 2024-06-20 10:49
 **/
@Service
public class DatabaseUsageServiceImpl implements DatabaseUsageService {
    @Autowired
    private DatabaseUsageMapper databaseUsageMapper;

    /**
     * 根据数据库名称获取数据库使用情况。
     *
     * @param databaseName 数据库名称
     * @return 包含数据库使用情况的 DatabaseUsage 对象
     * @throws Exception 如果获取数据库使用情况过程中出现异常
     */
    public DatabaseUsage getDatabaseUsage(String databaseName) {
        // 调用 databaseUsageMapper 的 getDatabaseUsage 方法，根据数据库名称获取数据库使用情况
        DatabaseUsage databaseUsage = databaseUsageMapper.getDatabaseUsage(databaseName);

        // 将获取到的数据库使用情况打印到错误输出流中（通常用于调试）
        /*System.err.println(databaseUsage.toString());*/

        // 返回获取到的数据库使用情况对象
        return databaseUsage;
    }


    /**
     * 根据时间删除系统操作日志
     *
     * @param data 时间字符串，表示要删除的系统操作日志的时间范围
     * @return 成功删除的系统操作日志数量，如果删除失败则返回负数或0
     */
    @Override
    public int deleteSysOperLogByTime(String data) {
        // 调用 databaseUsageMapper 的 deleteSysOperLogByTime 方法，传入时间字符串 data 作为参数
        // 返回成功删除的系统操作日志数量
        return databaseUsageMapper.deleteSysOperLogByTime(data);
    }
}
