package com.vincent.dynamicapidemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vincent.dynamicapidemo.entity.api.DynamicAPIDatasourceConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/5/24
 * @Description:
 */
@Mapper
public interface DynamicAPIDatasourceConfigMapper extends BaseMapper<DynamicAPIDatasourceConfig> {
}
