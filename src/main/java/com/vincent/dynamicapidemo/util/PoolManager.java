package com.vincent.dynamicapidemo.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.vincent.dynamicapidemo.entity.DataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/31/24
 * @Description:
 */
@Slf4j
public class PoolManager {

    private static Lock lock = new ReentrantLock();

    private static Lock deleteLock = new ReentrantLock();

    // all datasource link pool will store in this map
    static Map<String, DruidDataSource> map = new HashMap<>();

    public static DruidDataSource getJDBCConnectionPool(DataSource dataSource) {
        if (map.containsKey(dataSource.getUrl())) {
            return map.get(dataSource.getDatasourceId());
        } else {
            lock.lock();
            try {
                log.info(Thread.currentThread().getName() + "获取锁");
                if (!map.containsKey(dataSource.getDatasourceId())) {
                    DruidDataSource druidDataSource = new DruidDataSource();
                    druidDataSource.setName(dataSource.getDatasourceName());
                    druidDataSource.setUrl(dataSource.getUrl());
                    druidDataSource.setName(dataSource.getUserName());
                    druidDataSource.setPassword(dataSource.getPassWord());
                    druidDataSource.setDriverClassName(dataSource.getClassName());
                    //失败后重连次数
                    druidDataSource.setConnectionErrorRetryAttempts(3);
                    druidDataSource.setBreakAfterAcquireFailure(Boolean.TRUE);

                    map.put(dataSource.getDatasourceId(), druidDataSource);
                    log.info("创建Druid连接池成功：{}", druidDataSource);
                }
                return map.get(dataSource.getDatasourceId());
            } catch (Exception e) {
//                throw new RuntimeException(e);
                return null;
            } finally {
                lock.unlock();
            }
        }
    }

    // delete from database link pool
    public static void removeJDBCConnectionPool(String id) {
        deleteLock.lock();
        try {
            DruidDataSource druidDataSource = map.get(id);
            if (druidDataSource != null) {
                druidDataSource.close();
                map.remove(id);
            }
        } catch (Exception e) {
            log.error(e.toString());
        } finally {
            deleteLock.unlock();
        }
    }

    public static DruidPooledConnection getPooledConnection(DataSource dataSource) throws SQLException {
        DruidDataSource pool = PoolManager.getJDBCConnectionPool(dataSource);
        DruidPooledConnection connection = pool.getConnection();
        // log.info("获取链接成功");
        return connection;
    }
}
