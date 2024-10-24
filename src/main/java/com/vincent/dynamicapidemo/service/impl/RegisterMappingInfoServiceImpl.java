package com.vincent.dynamicapidemo.service.impl;

import com.vincent.dynamicapidemo.entity.RegisterMappingInfo;
import com.vincent.dynamicapidemo.mapper.RegisterMappingInfoMapper;
import com.vincent.dynamicapidemo.service.RegisterMappingInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
     public RegisterMappingInfoMapper registerMappingInfoMapper;

     @Override
    public List<RegisterMappingInfo> getExistingMappingInfo() {
        return registerMappingInfoMapper.getAllInfo();
    }

    @Override
    public int saveMappingInfo(RegisterMappingInfo registerMappingInfo) {
        return registerMappingInfoMapper.saveMappingInfo(registerMappingInfo);
    }
}
