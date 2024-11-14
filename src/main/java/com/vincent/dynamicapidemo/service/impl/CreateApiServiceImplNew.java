package com.vincent.dynamicapidemo.service.impl;

import com.vincent.dynamicapidemo.entity.DTO.ApiConfig;
import com.vincent.dynamicapidemo.service.factory.ApiSaveFactory;
import com.vincent.dynamicapidemo.service.factory.ApiSaveStrategy;
import com.vincent.dynamicapidemo.service.CreateApiService;
import com.vincent.dynamicapidemo.util.DynamicApiUtil;
import com.vincent.dynamicapidemo.util.SentinelConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

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
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment env;

    @Autowired
    private ApiSaveFactory apiSaveFactory;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveConfig(ApiConfig apiConfig, String handler, String url) {

        // 使用工厂方法获取对应的策略
        ApiSaveStrategy strategy = apiSaveFactory.getStrategy(apiConfig.getCreateType());
        // 调用具体策略的保存方法
        String apiConfigId = strategy.saveConfig(apiConfig, handler, url);
        String targetMethodName = strategy.getTargetMethodName();
        /**
         * 存储完 数据库，注册动态路由 和
         */
        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
        // 注册动态路由，绑定url和目标方法
        DynamicApiUtil.create(bean, apiConfig.getPath(), apiConfig.getMethod(), "adapterController",targetMethodName);
        // 注册sentinel信息
        // 获取path组装资源名字，重新配置sentinel中的限流降级默认配置
        String sourceName = env.getProperty("server.servlet.context-path") + apiConfig.getPath();
        SentinelConfigUtil.initFlowRules(sourceName);
        // 组装传到redis中的topic
        String message = DynamicApiUtil.getIpAddr() + ":";
        message = message + apiConfigId;
        redisTemplate.convertAndSend("api_sync_channel", message);

        return apiConfigId;

    }


}
