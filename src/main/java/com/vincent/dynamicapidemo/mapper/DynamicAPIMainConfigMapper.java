package com.vincent.dynamicapidemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vincent.dynamicapidemo.entity.api.DynamicAPIMainConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/4/24
 * @Description:
 */
@Mapper
public interface DynamicAPIMainConfigMapper extends BaseMapper<DynamicAPIMainConfig> {
}
