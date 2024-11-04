package com.vincent.dynamicapidemo.util;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.druid.util.JdbcConstants;
import com.vincent.dynamicapidemo.entity.VO.ResponseVO;
import com.vincent.dynamicapidemo.entity.DataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/31/24
 * @Description:
 */
@Slf4j
public class JDBCUtil {

    public static ResultSet query(String sql, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        return preparedStatement.executeQuery();
    }

    public static Connection getConnection(DataSource dataSource) throws SQLException, ClassNotFoundException {
        String url = dataSource.getUrl();
//        switch (dataSource.getDataBaseType()) {
//            case JdbcConstants.MYSQL:
//            case "doris":
//                Class.forName(JdbcConstants.MYSQL_DRIVER_6);
//                break;
//            case JdbcConstants.HIVE:
//                Class.forName(JdbcConstants.HIVE_DRIVER);
//                break;
//            default:
//                break;
//        }
        //看className放在数据库中维护，还是在java中维护
        try {
            Class.forName(dataSource.getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection connection = DriverManager.getConnection(url, dataSource.getUserName(), dataSource.getPassWord());
        log.info("获取链接成功");
        return connection;
    }

    public static ResponseVO executeSqlPool(DataSource datasource, String sql, List<Object> jdbcParamValues) throws SQLException {
        log.debug(sql);
//        log.debug(JSON.toJSONString(jdbcParamValues));
        DruidPooledConnection connection = null;
        try {
            connection = PoolManager.getPooledConnection(datasource);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            // 参数注入
            //在 PreparedStatement 中，占位符 ? 是用来表示将要被替换的参数的位置。
            // 当你使用 PreparedStatement 的 setObject 方法时，你需要指定每个参数的位置索引，
            // 这个索引是从 1 开始的，而不是从 0 开始，这与数组或列表的索引不同。
            for (int i = 1; i < jdbcParamValues.size(); i++) {
                preparedStatement.setObject(i, jdbcParamValues.get(i-1));
            }

            boolean result = preparedStatement.execute();
            if (result) {
                ResultSet resultSet = preparedStatement.getResultSet();
                int columnCount = resultSet.getMetaData().getColumnCount();

                List<String> columns = new ArrayList<>();
                for(int i = 1; i <= columnCount; i++) {
                    columns.add(resultSet.getMetaData().getColumnName(i));
                }
                List<JSONObject> list = new ArrayList<>();
                while (resultSet.next()) {
                    JSONObject object = new JSONObject();
                    columns.stream().forEach(column -> {
                        try {
                            Object value = resultSet.getObject(column);
                            object.put(column, value);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    list.add(object);
                }
                return ResponseVO.apiSuccess(list);
            } else {
                int updateCount = preparedStatement.getUpdateCount();
                return ResponseVO.apiSuccess("sql修改数据行数："+updateCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.fail(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static ResponseVO executeSql(DataSource dataSource, String sql, List<Object> jdbcParamValues) throws SQLException, ClassNotFoundException {
        log.debug(sql);
        log.debug(JSON.toJSONString(jdbcParamValues));
        Connection connection = JDBCUtil.getConnection(dataSource);
        try {


            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            // 参数注入
            //在 PreparedStatement 中，占位符 ? 是用来表示将要被替换的参数的位置。
            // 当你使用 PreparedStatement 的 setObject 方法时，你需要指定每个参数的位置索引，
            // 这个索引是从 1 开始的，而不是从 0 开始，这与数组或列表的索引不同。
            for (int i = 0; i < jdbcParamValues.size(); i++) {
                preparedStatement.setObject(i+1, jdbcParamValues.get(i));
            }
            // 执行修改之后的sql
            boolean result = preparedStatement.execute();
            if (result) {
                ResultSet resultSet = preparedStatement.getResultSet();
                // 获取对应数据表的column数量
                int columnCount = resultSet.getMetaData().getColumnCount();

                List<String> columns = new ArrayList<>();
                for(int i = 1; i <= columnCount; i++) {
                    columns.add(resultSet.getMetaData().getColumnName(i));
                }
                List<JSONObject> list = new ArrayList<>();
                while (resultSet.next()) {
                    JSONObject object = new JSONObject();
                    columns.stream().forEach(column -> {
                        try {
                            Object value = resultSet.getObject(column);
                            object.put(column, value);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    list.add(object);
                }
                return ResponseVO.apiSuccess(list);
            } else {
                int updateCount = preparedStatement.getUpdateCount();
                return ResponseVO.apiSuccess("sql修改数据行数："+updateCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.fail(e.getMessage());
        } finally {
            // 关闭ResultSet
//            if (resultSet != null) {
//                try {
//                    rs.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
            // 关闭Connection
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main (String[] args) throws SQLException, ClassNotFoundException {
        DataSource dataSource = new DataSource();
        dataSource.setDataBaseType(JdbcConstants.MYSQL);
        dataSource.setUrl("jdbc:mysql://localhost:3306/gptDB?serverTimezone=UTC");
        dataSource.setUserName("root");
        dataSource.setPassWord("vincent2017727");
        String sql = "select * from user";



        DataSource dataSource1 = new DataSource();
        dataSource1.setDataBaseType(JdbcConstants.MYSQL);
        dataSource1.setUrl("jdbc:mysql://192.168.10.75:9030/doris_db?useSSL=false&serverTimezone=UTC");
        dataSource1.setUserName("root");
        dataSource1.setClassName("com.mysql.cj.jdbc.Driver");
//        dataSource1.setPassWord("vincent2017727");
        String sql1 = "select * from all_stock_temp limit 10";


        Connection connection = getConnection(dataSource1);
        PreparedStatement preparedStatement = connection.prepareStatement(sql1);

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

    }
}
