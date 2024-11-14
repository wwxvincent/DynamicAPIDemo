package com.vincent.dynamicapidemo.service;

import com.vincent.dynamicapidemo.entity.DTO.ApiConfig;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/2/24
 * @Description:
 */
public interface CreateApiService {

    int saveConfig(ApiConfig apiConfig, String handler, String url);
}
