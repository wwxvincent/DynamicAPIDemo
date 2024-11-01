package com.vincent.dynamicapidemo.service.impl;

import com.vincent.dynamicapidemo.common.ResponseDTO;
import com.vincent.dynamicapidemo.entity.DTO.CreateApiDTO;
import com.vincent.dynamicapidemo.entity.DataSource;
import com.vincent.dynamicapidemo.mapper.DataSourceMapper;
import com.vincent.dynamicapidemo.service.JDBCService;
import com.vincent.dynamicapidemo.util.JDBCUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/1/24
 * @Description:
 */
@Service
@Slf4j
public class JDBCServiceImpl implements JDBCService {

    @Autowired
    private  DataSourceMapper dataSourceMapper;

    @Override
    public ResponseDTO getDataFromDiffDBSource(CreateApiDTO createApiDTO) {
        try {
            DataSource dataSource = dataSourceMapper.getDatabaseSourceConfigById(createApiDTO.getSourceId());
            String sql = createApiDTO.getSql();
            List<Object> jdbcParamValues = Arrays.asList((Object[]) createApiDTO.getJdbcParamValues());
            //非数据库连接池连接，JDBC，每次连接，执行，然后，close
            ResponseDTO responseDTO = JDBCUtil.executeSql(dataSource, sql, jdbcParamValues);
            return responseDTO;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Object getDataFromDiffDBSource1(CreateApiDTO createApiDTO) {
        try {
            // do not use database connection pool
            // just create, execute then close
            log.debug(createApiDTO.getSql());
            DataSource dataSource = dataSourceMapper.getDatabaseSourceConfigById(createApiDTO.getSourceId());
            Connection connection = JDBCUtil.getConnection(dataSource);
            PreparedStatement preparedStatement = connection.prepareStatement(createApiDTO.getSql());

            ResultSet rs = preparedStatement.executeQuery();


            try {
                // 获取结果集的列数
                int columnCount = rs.getMetaData().getColumnCount();

                // 打印列名
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getMetaData().getColumnName(i) + "\t");
                }
                System.out.println();

                // 遍历结果集，打印每一行的数据
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        // 获取当前列的值
                        Object value = rs.getObject(i);
                        System.out.print(value + "\t");
                    }
                    System.out.println();
                }
                return rs;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // 关闭ResultSet
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                // 关闭Connection
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("no this data source");

        }
        return null;
    }


}