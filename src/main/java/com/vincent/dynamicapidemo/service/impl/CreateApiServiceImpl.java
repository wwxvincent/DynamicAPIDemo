package com.vincent.dynamicapidemo.service.impl;

import com.vincent.dynamicapidemo.entity.DTO.CreateApiDTO;
import com.vincent.dynamicapidemo.entity.DynamicAPIMappingInfo;
import com.vincent.dynamicapidemo.entity.DynamicAPISQLAssemble;
import com.vincent.dynamicapidemo.mapper.DynamicAPIMappingInfoMapper;
import com.vincent.dynamicapidemo.mapper.DynamicAPISQLAssembleMapper;
import com.vincent.dynamicapidemo.service.CreateApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/2/24
 * @Description:
 */
@Service
public class CreateApiServiceImpl implements CreateApiService {

    @Autowired
    private DynamicAPISQLAssembleMapper dynamicAPISQLAssembleMapper;

    @Autowired
    private DynamicAPIMappingInfoMapper dynamicAPIMappingInfoMapper;


    @Override
    public boolean create(String datasourceId, String selectList, String fixedWhereList,String optionalWhereList, String path,
                          String targetMethodName,String method, String handler, String url) {

        try {
            // 1. update to sql table
            DynamicAPISQLAssemble dynamicAPSQLIAssemble = new DynamicAPISQLAssemble();
            String uid = UUID.randomUUID().toString();
            dynamicAPSQLIAssemble.setId(uid);
            dynamicAPSQLIAssemble.setDatasourceId(datasourceId);
            dynamicAPSQLIAssemble.setSelectElement(selectList);
            dynamicAPSQLIAssemble.setWhereElementFixed(fixedWhereList);
            dynamicAPSQLIAssemble.setWhereElementOptional(optionalWhereList);
            dynamicAPISQLAssembleMapper.insert(dynamicAPSQLIAssemble);
            // 2. update to binding table
            DynamicAPIMappingInfo dynamicAPIMappingInfo = new DynamicAPIMappingInfo();
            dynamicAPIMappingInfo.setId(UUID.randomUUID().toString());
            dynamicAPIMappingInfo.setPath(path);
            dynamicAPIMappingInfo.setMethods(RequestMethod.valueOf(method));
            dynamicAPIMappingInfo.setHandler(handler);
            dynamicAPIMappingInfo.setTargetMethodName(targetMethodName);
            dynamicAPIMappingInfo.setUrl(url);
            dynamicAPIMappingInfo.setDatasource_id(datasourceId);// 暂时就查第一个记录
            dynamicAPIMappingInfo.setSqlId(uid);
            dynamicAPIMappingInfoMapper.insert(dynamicAPIMappingInfo);
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }

    }
}
