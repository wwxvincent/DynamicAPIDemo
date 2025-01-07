package com.vincent.dynamicapidemo.dynamicApi.service.impl;

import com.vincent.dynamicapidemo.dynamicApi.entity.DTO.ApiConfig;
import com.vincent.dynamicapidemo.dynamicApi.entity.DTO.SearchDTO;
import com.vincent.dynamicapidemo.dynamicApi.entity.VO.ResponseVO;
import com.vincent.dynamicapidemo.dynamicApi.entity.api.DynamicAPIMainConfig;
import com.vincent.dynamicapidemo.dynamicApi.service.CreateApiService;
import com.vincent.dynamicapidemo.dynamicApi.service.DynamicAPIMainConfigService;
import com.vincent.dynamicapidemo.dynamicApi.service.DynamicApiService;
import com.vincent.dynamicapidemo.dynamicApi.service.UseApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 1/6/25
 * @Description:
 */
@Service
public class DynamicApiServiceImpl implements DynamicApiService {
    @Autowired
    private DynamicAPIMainConfigService dynamicAPIMainConfigService;

    @Autowired
    private CreateApiService createApiService;

    @Autowired
    private UseApiService useApiService;


    @Override
    public void loadExistingMappings() {
        dynamicAPIMainConfigService.loadExistingMappings();
    }

    @Override
    public List<DynamicAPIMainConfig> getExistingMappingInfo() {
        return dynamicAPIMainConfigService.getExistingMappingInfo();
    }

    @Override
    public boolean checkExisted(String url) {
        return dynamicAPIMainConfigService.checkExisted(url);
    }

    @Override
    public String saveConfig(ApiConfig apiConfig, String handler, String url) {
        return createApiService.saveConfig(apiConfig, handler, url);
    }

    @Override
    public ResponseVO getDataFromDiffDBSource(SearchDTO searchDTO, String url) {
        return useApiService.getDataFromDiffDBSource(searchDTO, url);
    }
}
