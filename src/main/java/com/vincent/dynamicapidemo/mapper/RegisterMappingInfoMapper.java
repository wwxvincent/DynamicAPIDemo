package com.vincent.dynamicapidemo.mapper;

import com.vincent.dynamicapidemo.entity.RegisterMappingInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/23/24
 * @Description:
 */
@Mapper
public interface RegisterMappingInfoMapper {

    @Select("select * from dynamic_mapping_info")
    List<RegisterMappingInfo> getAllInfo();


    @Insert("INSERT INTO dynamic_mapping_info (`paths`, `params`, `methods`, `handler`, `targetMethodName`, `sql`, `url`) " +
            "VALUES (#{paths}, #{params}, #{methods}, #{handler}, #{targetMethodName}, #{sql}, #{url})")
    int saveMappingInfo(RegisterMappingInfo registerMappingInfo);

}
