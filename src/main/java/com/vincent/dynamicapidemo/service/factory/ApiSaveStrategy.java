package com.vincent.dynamicapidemo.service.factory;

import com.vincent.dynamicapidemo.entity.DTO.ApiConfig;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/14/24
 * @Description: 将动态API路由注册的配置及相关联参数 的 存入方式，改为 应用简单工厂模式 实现
 *                  Simple Factory Pattern implement
 */
public interface ApiSaveStrategy {
    String saveConfig (ApiConfig apiConfig, String handler, String url);
    String getTargetMethodName();
}