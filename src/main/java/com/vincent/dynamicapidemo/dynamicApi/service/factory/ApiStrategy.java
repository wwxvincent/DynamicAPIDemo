package com.vincent.dynamicapidemo.dynamicApi.service.factory;

import com.vincent.dynamicapidemo.dynamicApi.entity.DTO.ApiConfig;
import com.vincent.dynamicapidemo.dynamicApi.entity.DTO.Param;
import com.vincent.dynamicapidemo.dynamicApi.entity.DTO.SearchDTO;
import com.vincent.dynamicapidemo.dynamicApi.entity.VO.ResponseVO;
import com.vincent.dynamicapidemo.dynamicApi.entity.api.DynamicAPIParamsConfig;

import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/14/24
 * @Description: 将动态API路由注册的配置及相关联参数 的 存入方式，改为 应用简单工厂模式 实现
 *                  Simple Factory Pattern implement
 */
public interface ApiStrategy {
    String saveConfig (ApiConfig apiConfig, String handler, String url);
    String getTargetMethodName();
    ResponseVO getDataFromDiffDBSource (SearchDTO searchDTO, String connUrl, String connDriverClassName, String connUsername, String connPassword, String sql, List<Param> paramsFromRequest, List<DynamicAPIParamsConfig> paramsFromTable);
}