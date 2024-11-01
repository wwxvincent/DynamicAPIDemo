package com.vincent.dynamicapidemo.service;

import com.vincent.dynamicapidemo.common.ResponseDTO;
import com.vincent.dynamicapidemo.entity.DTO.CreateApiDTO;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/1/24
 * @Description:
 */
public interface JDBCService {
    Object getDataFromDiffDBSource1(CreateApiDTO createApiDTO);

    ResponseDTO getDataFromDiffDBSource(CreateApiDTO createApiDTO);
}
