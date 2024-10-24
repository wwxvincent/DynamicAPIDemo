package com.vincent.dynamicapidemo.service;

import com.vincent.dynamicapidemo.entity.RegisterMappingInfo;

import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/23/24
 * @Description:
 */
public interface RegisterMappingInfoService {

    List<RegisterMappingInfo> getExistingMappingInfo();

    int saveMappingInfo(RegisterMappingInfo registerMappingInfo);
}
