//package com.vincent.dynamicapidemo.mapper;
//
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.vincent.dynamicapidemo.entity.DynamicAPIMappingInfo;
//import org.apache.ibatis.annotations.Insert;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Select;
//
//import java.util.List;
//
///**
// * @Author: Vincent(Wenxuan) Wang
// * @Date: 10/23/24
// * @Description:
// */
//@Mapper
//public interface DynamicAPIMappingInfoMapper extends BaseMapper<DynamicAPIMappingInfo> {
//
//    @Select("select * from dynamic_mapping_info")
//    List<DynamicAPIMappingInfo> getAllInfo();
//
//
//    @Insert("INSERT INTO dynamic_mapping_info (`paths`, `params`, `methods`, `handler`, `targetMethodName`, `sql`, `url`) " +
//            "VALUES (#{paths}, #{params}, #{methods}, #{handler}, #{targetMethodName}, #{sql}, #{url})")
//    int saveMappingInfo(DynamicAPIMappingInfo dynamicAPIMappingInfo);
//
//}
