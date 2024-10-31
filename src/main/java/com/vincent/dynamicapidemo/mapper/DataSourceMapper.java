package com.vincent.dynamicapidemo.mapper;

import com.vincent.dynamicapidemo.entity.DataSource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/31/24
 * @Description:
 */
@Mapper
public interface DataSourceMapper {

    @Select("SELECT * FROM database_source_config")
    List<DataSource> getDatabaseSourceConfig();
}
