package com.vincent.dynamicapidemo.dynamicApi.service;

import com.vincent.dynamicapidemo.dynamicApi.entity.DTO.ApiConfig;
import com.vincent.dynamicapidemo.dynamicApi.entity.DTO.SearchDTO;
import com.vincent.dynamicapidemo.dynamicApi.entity.VO.ResponseVO;
import com.vincent.dynamicapidemo.dynamicApi.entity.api.DynamicAPIMainConfig;

import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 1/6/25
 * @Description:
 */
public interface DynamicApiService {
    void loadExistingMappings();

//    List<DynamicAPIMainConfig> getExistingMappingInfo();

    boolean checkExisted(String url);

    String saveConfig(ApiConfig apiConfig, String handler, String url);

    ResponseVO getDataFromDiffDBSource(SearchDTO searchDTO, String url);
}
