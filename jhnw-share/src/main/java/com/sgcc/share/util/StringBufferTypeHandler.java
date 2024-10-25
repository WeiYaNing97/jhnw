package com.sgcc.share.util;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringBufferTypeHandler extends BaseTypeHandler<StringBuffer> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, StringBuffer parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toString());
    }

    @Override
    public StringBuffer getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String str = rs.getString(columnName);
        return str == null ? null : new StringBuffer(str);
    }

    @Override
    public StringBuffer getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String str = rs.getString(columnIndex);
        return str == null ? null : new StringBuffer(str);
    }

    @Override
    public StringBuffer getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String str = cs.getString(columnIndex);
        return str == null ? null : new StringBuffer(str);
    }
}