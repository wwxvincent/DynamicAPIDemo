package com.vincent.dynamicapidemo.dynamicApi.service;

import com.vincent.dynamicapidemo.dynamicApi.entity.DTO.ApiConfig;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/2/24
 * @Description:
 */
public interface CreateApiService {

    String saveConfig(ApiConfig apiConfig, String handler, String url);
}
