package com.vincent.dynamicapidemo.util;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.druid.util.JdbcConstants;
import com.vincent.dynamicapidemo.entity.VO.ResponseVO;
import com.vincent.dynamicapidemo.entity.DataSource;
import com.vincent.dynamicapidemo.entity.api.DynamicAPIDatasourceConfig;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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

    public static Connection getConnection(String url, String className, String userName, String passWord) throws SQLException, ClassNotFoundException {
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
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection connection = DriverManager.getConnection(url, userName, passWord);
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

    public static ResponseVO executeSqlWithPlaceHolder(String url, String className, String userName, String passWord, String sql, List<Object> paramsIndexList) throws SQLException, ClassNotFoundException {
        //1、获取连接
        log.debug(sql);
        Connection connection = JDBCUtil.getConnection(url, className, userName, passWord);
        try {
            //2、创建预编译对象
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            //3、替换替换符内容
            for (int i = 0; i < paramsIndexList.size(); i++) {
                preparedStatement.setObject(i+1, paramsIndexList.get(i));
            }
            //4、执行SQL得到结果集
            ResultSet result = preparedStatement.executeQuery();
            //5、得到是否登录成功，成功就是为true,失败为False
            int columnCount = result.getMetaData().getColumnCount();

            List<String> columns = new ArrayList<>();
            for(int i = 1; i <= columnCount; i++) {
                columns.add(result.getMetaData().getColumnName(i));
            }
            List<JSONObject> list = new ArrayList<>();
            while (result.next()) {
                JSONObject object = new JSONObject();
                columns.stream().forEach(column -> {
                    try {
                        Object value = result.getObject(column);
                        object.put(column, value);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                list.add(object);
            }
            return ResponseVO.apiSuccess(list);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseVO.fail(e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //6、释放资源

    }


    public static ResponseVO executeSql(String url, String className, String userName, String passWord, String sql) throws SQLException, ClassNotFoundException {
        log.debug(sql);
        Connection connection = JDBCUtil.getConnection(url, className, userName, passWord);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

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

//    public static ResponseVO executeSql(String url, String className, String userName, String passWord, String sql, List<Object> jdbcParamValues) throws SQLException, ClassNotFoundException {
//        log.debug(sql);
//        log.debug(JSON.toJSONString(jdbcParamValues));
//        Connection connection = JDBCUtil.getConnection(url, className, userName, passWord);
//        try {
//
//
//            PreparedStatement preparedStatement = connection.prepareStatement(sql);
//            // 参数注入
//            //在 PreparedStatement 中，占位符 ? 是用来表示将要被替换的参数的位置。
//            // 当你使用 PreparedStatement 的 setObject 方法时，你需要指定每个参数的位置索引，
//            // 这个索引是从 1 开始的，而不是从 0 开始，这与数组或列表的索引不同。
//            for (int i = 0; i < jdbcParamValues.size(); i++) {
//                preparedStatement.setObject(i+1, jdbcParamValues.get(i));
//            }
//            // 执行修改之后的sql
//            boolean result = preparedStatement.execute();
//            if (result) {
//                ResultSet resultSet = preparedStatement.getResultSet();
//                // 获取对应数据表的column数量
//                int columnCount = resultSet.getMetaData().getColumnCount();
//
//                List<String> columns = new ArrayList<>();
//                for(int i = 1; i <= columnCount; i++) {
//                    columns.add(resultSet.getMetaData().getColumnName(i));
//                }
//                List<JSONObject> list = new ArrayList<>();
//                while (resultSet.next()) {
//                    JSONObject object = new JSONObject();
//                    columns.stream().forEach(column -> {
//                        try {
//                            Object value = resultSet.getObject(column);
//                            object.put(column, value);
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//                    });
//                    list.add(object);
//                }
//                return ResponseVO.apiSuccess(list);
//            } else {
//                int updateCount = preparedStatement.getUpdateCount();
//                return ResponseVO.apiSuccess("sql修改数据行数："+updateCount);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseVO.fail(e.getMessage());
//        } finally {
//            // 关闭ResultSet
////            if (resultSet != null) {
////                try {
////                    rs.close();
////                } catch (SQLException e) {
////                    e.printStackTrace();
////                }
////            }
//            // 关闭Connection
//            try {
//                connection.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    public static void main (String[] args) throws SQLException, ClassNotFoundException {
//
//    }
}
