package com.vincent.dynamicapidemo.service.impl;

import com.vincent.dynamicapidemo.entity.DynamicAPIMappingInfo;
import com.vincent.dynamicapidemo.mapper.DynamicAPIMappingInfoMapper;
import com.vincent.dynamicapidemo.service.RegisterMappingInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  @Author: Vincent(Wenxuan) Wang
 *  @Date: 10/23/24
 *  @Description:
 *
 */
@Service
public class RegisterMappingInfoServiceImpl implements RegisterMappingInfoService {

     @Autowired
     public DynamicAPIMappingInfoMapper dynamicAPIMappingInfoMapper;

     @Override
    public List<DynamicAPIMappingInfo> getExistingMappingInfo() {
        return dynamicAPIMappingInfoMapper.getAllInfo();
    }

    @Override
    public int saveMappingInfo(DynamicAPIMappingInfo dynamicAPIMappingInfo) {
        return dynamicAPIMappingInfoMapper.saveMappingInfo(dynamicAPIMappingInfo);
    }
}
