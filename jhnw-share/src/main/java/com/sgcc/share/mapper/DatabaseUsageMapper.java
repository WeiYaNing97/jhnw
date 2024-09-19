package com.sgcc.share.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

public interface DatabaseUsageMapper {


    /**
     * 查询指定数据库的使用情况，并返回使用空间大小（单位：MB），保留两位小数
     *
     * @param databaseName 数据库名称
     * @return 返回一个double类型的值，表示数据库使用空间大小（单位：MB），保留两位小数
     */
    @Select("SELECT " +
            "ROUND( SUM( data_length + index_length ) / 1024 / 1024, 2 ) " +
            "FROM information_schema.TABLES " +
            "WHERE table_schema = #{databaseName}")
    double getDatabaseUsage(String databaseName);


    @Delete(value = "delete from sys_oper_log where oper_time < #{data}")
    int deleteSysOperLogByTime(String data);
}
