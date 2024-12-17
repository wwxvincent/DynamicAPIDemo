package com.vincent.dynamicapidemo.dynamicApi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vincent.dynamicapidemo.dynamicApi.entity.api.DynamicAPIParamsConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/4/24
 * @Description:
 */
@Mapper
public interface DynamicAPIParamsConfigMapper extends BaseMapper<DynamicAPIParamsConfig> {
}