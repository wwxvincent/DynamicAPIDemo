package com.vincent.dynamicapidemo.service.impl;

import com.vincent.dynamicapidemo.entity.DTO.ApiConfig;
import com.vincent.dynamicapidemo.factory.ApiSaveFactory;
import com.vincent.dynamicapidemo.factory.ApiSaveStrategy;
import com.vincent.dynamicapidemo.service.CreateApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/14/24
 * @Description: 将动态API路由注册的配置及相关联参数 的 存入方式，改为 应用简单工厂模式 实现
 *  *                  Simple Factory Pattern implement
 */
@Slf4j
@Service
public class CreateApiServiceImplNew implements CreateApiService {




    @Autowired
    private ApiSaveFactory apiSaveFactory;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveConfig(ApiConfig apiConfig, String handler, String targetMethodName, String url) {

        // 使用工厂方法获取对应的策略
        ApiSaveStrategy strategy = apiSaveFactory.getStrategy(apiConfig.getCreateType());
        // 调用具体策略的保存方法
        return strategy.saveConfig(apiConfig, handler, targetMethodName, url);

    }


}
