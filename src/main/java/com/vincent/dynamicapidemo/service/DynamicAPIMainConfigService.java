package com.vincent.dynamicapidemo.service;

import com.vincent.dynamicapidemo.entity.api.DynamicAPIMainConfig;

import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/5/24
 * @Description:
 */
public interface DynamicAPIMainConfigService {
    List<DynamicAPIMainConfig> getExistingMappingInfo();

    boolean checkExisted(String url);
}
