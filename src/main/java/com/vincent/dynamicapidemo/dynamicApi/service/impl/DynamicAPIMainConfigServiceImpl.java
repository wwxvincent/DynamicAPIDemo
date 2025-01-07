package com.vincent.dynamicapidemo.dynamicApi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vincent.dynamicapidemo.dynamicApi.entity.api.DynamicAPIMainConfig;
import com.vincent.dynamicapidemo.dynamicApi.mapper.DynamicAPIMainConfigMapper;
import com.vincent.dynamicapidemo.dynamicApi.service.DynamicAPIMainConfigService;
import com.vincent.dynamicapidemo.util.DynamicApiUtil;
import com.vincent.dynamicapidemo.util.SentinelConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/5/24
 * @Description:
 */
@Slf4j
@Service
public class DynamicAPIMainConfigServiceImpl implements DynamicAPIMainConfigService {

    @Autowired
    private DynamicAPIMainConfigMapper dynamicAPIMainConfigMapper;

    @Autowired
    private DynamicApiUtil dynamicApiUtil;



    @Override
    public List<DynamicAPIMainConfig> getExistingMappingInfo() {
        QueryWrapper<DynamicAPIMainConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status","1"); // 选出所有的状态为1的绑定信息
        return dynamicAPIMainConfigMapper.selectList(queryWrapper);
    }

    @Override
    public boolean checkExisted(String url) {
        QueryWrapper<DynamicAPIMainConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url",url);
        return !dynamicAPIMainConfigMapper.selectList(queryWrapper).isEmpty();
    }

    @Override
    public void loadExistingMappings() {

        List<DynamicAPIMainConfig> existingMappings = this.getExistingMappingInfo();
        if (!existingMappings.isEmpty()) {
            for (DynamicAPIMainConfig dynamicAPIMainConfig : existingMappings) { // 从DB中获取配置信息，重新绑定API。
                // 注册动态路由，绑定url和目标方法
                dynamicApiUtil.create( dynamicAPIMainConfig.getPath(), dynamicAPIMainConfig.getMethod(), dynamicAPIMainConfig.getHandler(), dynamicAPIMainConfig.getTargetMethodName());
                // 注册sentinel信息
                // 获取path组装资源名字，重新配置sentinel中的限流降级默认配置
//                String sourceName = env.getProperty("server.servlet.context-path") + dynamicAPIMainConfig.getPath();
//                SentinelConfigUtil.initFlowRules(sourceName);
                SentinelConfigUtil.initFlowRules(dynamicAPIMainConfig.getPath());

                log.info("<===== load dynamic API: " + dynamicAPIMainConfig.getApiName()+" : " +dynamicAPIMainConfig.getId());
            }

            log.info("Successfully loaded all existing register mappings from database.");
        } else {
            log.info("No register mappings found in the database.");
        }
    }
}
