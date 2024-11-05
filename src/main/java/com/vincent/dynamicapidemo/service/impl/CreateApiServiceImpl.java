package com.vincent.dynamicapidemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vincent.dynamicapidemo.entity.DTO.ApiConfig;
import com.vincent.dynamicapidemo.entity.DTO.Param;
import com.vincent.dynamicapidemo.entity.api.DynamicAPIDict;
import com.vincent.dynamicapidemo.entity.api.DynamicAPIMainConfig;
import com.vincent.dynamicapidemo.entity.api.DynamicAPIParamsConfig;
import com.vincent.dynamicapidemo.mapper.*;
import com.vincent.dynamicapidemo.service.CreateApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/2/24
 * @Description:
 */
@Service
public class CreateApiServiceImpl implements CreateApiService {


    // 确认原型后
    @Autowired
    private DynamicAPIDictMapper dynamicAPIDictMapper;

    @Autowired
    private DynamicAPIMainConfigMapper dynamicAPIMainConfigMapper;

    @Autowired
    private DynamicAPIParamsConfigMapper dynamicAPIParamsConfigMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveConfig(ApiConfig apiConfig, String handler, String targetMethodName, String url) {

        // 1. 查询数据字典表，获取数据库连接配置表id
        QueryWrapper<DynamicAPIDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("datasource_id") // 只选择 datasource_id 字段
                .eq("type_code",apiConfig.getSourceType())
                .eq("schema_code", apiConfig.getSourceSchema());
        List<DynamicAPIDict> resultList = dynamicAPIDictMapper.selectList(queryWrapper);
        int datasourceId = resultList.get(0).getDatasourceId();

        String selectStr = apiConfig.getSelectList().toString().replace("[", "").replace("]", "");
        String sb = "select " + selectStr + " from " + apiConfig.getSourceTable() + " where (1=1) ";
        // 2. 存入绑定关系表，API配置主表
        DynamicAPIMainConfig dynamicAPIMainConfig = new DynamicAPIMainConfig();
        dynamicAPIMainConfig.setPath(apiConfig.getPath());
        dynamicAPIMainConfig.setMethod(apiConfig.getMethod());
        dynamicAPIMainConfig.setHandler(handler);
        dynamicAPIMainConfig.setTargetMethodName(targetMethodName);
        dynamicAPIMainConfig.setUrl(url);
        dynamicAPIMainConfig.setAPIName(apiConfig.getAPIName());
        dynamicAPIMainConfig.setAPIWorkspace(apiConfig.getAPIWorkArea());
        dynamicAPIMainConfig.setDatabaseDictId(datasourceId);
        dynamicAPIMainConfig.setSelectList(selectStr);
        dynamicAPIMainConfig.setSqlSentence(sb);
        dynamicAPIMainConfig.setStatus("1");
        dynamicAPIMainConfigMapper.insert(dynamicAPIMainConfig);
        int mainId = dynamicAPIMainConfig.getId();

        // 3. 存入参明细表 where
        for (Param p : apiConfig.getParamsList()) {
            DynamicAPIParamsConfig paramsConfig = new DynamicAPIParamsConfig();
            paramsConfig.setMainConfigId(mainId);
            paramsConfig.setSort(p.getSort());
            paramsConfig.setParamName(p.getParam_name());
            paramsConfig.setParamValue(p.getParam_value());
            paramsConfig.setRequired(p.getRequired());
            paramsConfig.setOperator(p.getOperator());
            paramsConfig.setDefaultValue(p.getDefault_value());
            paramsConfig.setDescription(p.getDescription());
            paramsConfig.setSample(p.getSample());
            dynamicAPIParamsConfigMapper.insert(paramsConfig);
        }



        return true;
    }


//    @Override
//    public boolean create(String datasourceId, String selectList, String fixedWhereList,String optionalWhereList, String path,
//                          String targetMethodName,String method, String handler, String url) {
//
//        try {
//            // 1. update to sql table
//            DynamicAPISQLAssemble dynamicAPSQLIAssemble = new DynamicAPISQLAssemble();
//            String uid = UUID.randomUUID().toString();
//            dynamicAPSQLIAssemble.setId(uid);
//            dynamicAPSQLIAssemble.setDatasourceId(datasourceId);
//            dynamicAPSQLIAssemble.setSelectElement(selectList);
//            dynamicAPSQLIAssemble.setWhereElementFixed(fixedWhereList);
//            dynamicAPSQLIAssemble.setWhereElementOptional(optionalWhereList);
//            dynamicAPISQLAssembleMapper.insert(dynamicAPSQLIAssemble);
//            // 2. update to binding table
//            DynamicAPIMappingInfo dynamicAPIMappingInfo = new DynamicAPIMappingInfo();
//            dynamicAPIMappingInfo.setId(UUID.randomUUID().toString());
//            dynamicAPIMappingInfo.setPath(path);
//            dynamicAPIMappingInfo.setMethods(RequestMethod.valueOf(method));
//            dynamicAPIMappingInfo.setHandler(handler);
//            dynamicAPIMappingInfo.setTargetMethodName(targetMethodName);
//            dynamicAPIMappingInfo.setUrl(url);
//            dynamicAPIMappingInfo.setDatasource_id(datasourceId);// 暂时就查第一个记录
//            dynamicAPIMappingInfo.setSqlId(uid);
//            dynamicAPIMappingInfoMapper.insert(dynamicAPIMappingInfo);
//            return true;
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//            return false;
//        }
//
//    }
}
