package com.vincent.dynamicapidemo.service;

import com.vincent.dynamicapidemo.entity.DTO.ApiConfig;
import com.vincent.dynamicapidemo.entity.DTO.CreateApiDTO;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/2/24
 * @Description:
 */
public interface CreateApiService {

//    boolean create(String datasourceId, String selectList, String fixedWhereList,String optionalWhereList, String path,
//                   String targetMethodName,String method, String handler, String url);

    boolean saveConfig(ApiConfig apiConfig, String handler, String targetMethodName, String url);
}
