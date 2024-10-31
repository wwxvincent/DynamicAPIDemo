package com.vincent.dynamicapidemo.service.impl;

import com.vincent.dynamicapidemo.entity.DataSource;
import com.vincent.dynamicapidemo.mapper.DataSourceMapper;
import com.vincent.dynamicapidemo.service.DBChangeService;
import com.vincent.dynamicapidemo.util.DBContextHolder;
import com.vincent.dynamicapidemo.util.DynamicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/31/24
 * @Description:
 */
@Service
public class DBChangeServiceImpl implements DBChangeService {

    @Autowired
    DataSourceMapper dataSourceMapper;
    @Autowired
    private DynamicDataSource dynamicDataSource;

    @Override
    public List<DataSource> getDatabaseSourceConfig() {
        return dataSourceMapper.getDatabaseSourceConfig();
    }

    @Override
    public boolean changeDB(String dataSourceId) throws Exception {
        // 默认切换到主数据源，进行整体资源的查找、
        DBContextHolder.clearDataSource();

        List<DataSource> dataSources = dataSourceMapper.getDatabaseSourceConfig();

        for (DataSource dataSource : dataSources) {
            if (dataSource.getDatasourceId().equals(dataSourceId)) {
                System.out.println("需要使用的数据源已经找到，datasourceId 是：" + dataSource.getDatasourceId());
                // 创建数据源连接 & 检查，若存在则不需要重新创建
                dynamicDataSource.createDataSourceWithCheck(dataSource);
                // switch to this datasource
                DBContextHolder.setDataSource(dataSource.getDatasourceId());
                return true;
            }
        }
        return false;
    }
}
