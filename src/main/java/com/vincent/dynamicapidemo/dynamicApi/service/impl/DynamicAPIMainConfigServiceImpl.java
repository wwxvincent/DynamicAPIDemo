package com.vincent.dynamicapidemo.dynamicApi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vincent.dynamicapidemo.dynamicApi.entity.api.DynamicAPIMainConfig;
import com.vincent.dynamicapidemo.dynamicApi.mapper.DynamicAPIMainConfigMapper;
import com.vincent.dynamicapidemo.dynamicApi.service.DynamicAPIMainConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/5/24
 * @Description:
 */
@Service
public class DynamicAPIMainConfigServiceImpl implements DynamicAPIMainConfigService {

    @Autowired
    private DynamicAPIMainConfigMapper dynamicAPIMainConfigMapper;

    @Override
    public List<DynamicAPIMainConfig> getExistingMappingInfo() {
        QueryWrapper<DynamicAPIMainConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status","1"); // 选出所有的状态为1的绑定信息
        return dynamicAPIMainConfigMapper.selectList(queryWrapper);
    }

    @Override
    public boolean checkExisted(String url) {
        QueryWrapper<DynamicAPIMainConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url",url);
        return !dynamicAPIMainConfigMapper.selectList(queryWrapper).isEmpty();
    }
}
