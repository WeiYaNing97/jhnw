package com.sgcc.share.mapper;

import com.sgcc.share.domain.DatabaseUsage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

public interface DatabaseUsageMapper {

    /**
     * 查询指定数据库的使用情况和剩余空间
     *
     * @param databaseName 数据库名
     * @return 返回指定数据库的使用情况和剩余空间的实体对象
     */
    @Select("SELECT " +
            // 查询数据库名
            "table_schema AS databaseName, " +
            // 查询已使用的空间大小（单位：MB）
            "SUM(data_length + index_length) / 1024 / 1024 AS usedSpace, " +
            // 查询剩余空间大小（单位：MB）
            "SUM(data_free) / 1024 / 1024 AS freeSpace " +
            // 从 information_schema.tables 表中选择数据
            "FROM information_schema.tables " +
            // 筛选出指定数据库名的数据
            "WHERE table_schema = #{databaseName} " +
            // 按数据库名进行分组
            "GROUP BY table_schema")
    // 返回一个表示数据库使用情况和剩余空间的实体对象
    DatabaseUsage getDatabaseUsage(String databaseName);


    @Delete(value = "delete from sys_oper_log where oper_time < #{data}")
    int deleteSysOperLogByTime(String data);
}
