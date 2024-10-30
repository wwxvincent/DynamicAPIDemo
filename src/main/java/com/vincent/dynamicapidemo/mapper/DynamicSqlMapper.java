package com.vincent.dynamicapidemo.mapper;

import com.vincent.dynamicapidemo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/29/24
 * @Description:
 */
@Mapper
public interface DynamicSqlMapper {

    @Select("${sql}")
    List<Map<String, Object>> executeDynamicSql(@Param("sql") String sql);
}
