//package com.vincent.dynamicapidemo.service.impl;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.vincent.dynamicapidemo.entity.DTO.ApiConfig;
//import com.vincent.dynamicapidemo.entity.DTO.Param;
//import com.vincent.dynamicapidemo.entity.api.DynamicAPIDict;
//import com.vincent.dynamicapidemo.entity.api.DynamicAPIMainConfig;
//import com.vincent.dynamicapidemo.entity.api.DynamicAPIParamsConfig;
//import com.vincent.dynamicapidemo.mapper.*;
//import com.vincent.dynamicapidemo.service.CreateApiService;
//import com.vincent.dynamicapidemo.service.DynamicAPIMainConfigService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
///**
// * @Author: Vincent(Wenxuan) Wang
// * @Date: 11/2/24
// * @Description:
// */
//@Slf4j
//@Service
//public class CreateApiServiceImpl implements CreateApiService {
//
//
//    // 确认原型后
//    @Autowired
//    private DynamicAPIDictMapper dynamicAPIDictMapper;
//
//    @Autowired
//    private DynamicAPIMainConfigMapper dynamicAPIMainConfigMapper;
//
//    @Autowired
//    private DynamicAPIMainConfigService dynamicAPIMainConfigService;
//
//    @Autowired
//    private DynamicAPIParamsConfigMapper dynamicAPIParamsConfigMapper;
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public int saveConfig(ApiConfig apiConfig, String handler, String targetMethodName, String url) {
//
//        String type = apiConfig.getCreateType();
//        switch (type) {
//            case "TABLE" :
//                return tableSave(apiConfig, handler, targetMethodName, url);
//            case "SQL" :
//                // to-do
//                return -1;
//            case "JAR" :
//                // to-do
//                return -1;
//            default:
//                return -1;
//        }
//    }
//
//    private int tableSave(ApiConfig apiConfig, String handler, String targetMethodName, String url) {
//        if(dynamicAPIMainConfigService.checkExisted(url)) {
//            log.info("<===== Invalid url !!!! This url already existed");
//            return -1;
//        }
//
//        // 1. 查询数据字典表，获取数据库连接配置表id
//        QueryWrapper<DynamicAPIDict> queryWrapper = new QueryWrapper<>();
//        queryWrapper.select("datasource_id") // 只选择 datasource_id 字段
//                .eq("type_code",apiConfig.getSourceType())
//                .eq("schema_code", apiConfig.getSourceSchema());
//        List<DynamicAPIDict> resultList = dynamicAPIDictMapper.selectList(queryWrapper);
//        int datasourceId = resultList.get(0).getDatasourceId();
//
//        String selectStr = apiConfig.getSelectList().toString().replace("[", "").replace("]", "");
////        String sb = "select * from " + apiConfig.getSourceTable() + " where (1=1) "; // 用的时候，用select来替换掉*
//        //取消select 可以随意选择功能，直接写死select部分（1/3)
//        String sb = "select "+ selectStr + " from " + apiConfig.getSourceTable() + " where (1=1) "; // 用的时候，用select来替换掉*
//        // 2. 存入绑定关系表，API配置主表
//        DynamicAPIMainConfig dynamicAPIMainConfig = new DynamicAPIMainConfig();
//        dynamicAPIMainConfig.setPath(apiConfig.getPath());
//        dynamicAPIMainConfig.setMethod(apiConfig.getMethod());
//        dynamicAPIMainConfig.setHandler(handler);
//        dynamicAPIMainConfig.setTargetMethodName(targetMethodName);
//        dynamicAPIMainConfig.setUrl(url);
//        dynamicAPIMainConfig.setApiName(apiConfig.getApiName());
//        dynamicAPIMainConfig.setApiWorkspace(apiConfig.getApiWorkArea());
//        dynamicAPIMainConfig.setDatabaseDictId(datasourceId);
//        dynamicAPIMainConfig.setSelectList(selectStr);
//        dynamicAPIMainConfig.setSqlSentence(sb);
//        dynamicAPIMainConfig.setStatus("1");
//        dynamicAPIMainConfigMapper.insert(dynamicAPIMainConfig);
//        int mainId = dynamicAPIMainConfig.getId();
//
//        // 3. 存入参明细表 where
//        for (Param p : apiConfig.getParamsList()) {
//            DynamicAPIParamsConfig paramsConfig = new DynamicAPIParamsConfig();
//            paramsConfig.setMainConfigId(mainId);
//            paramsConfig.setSort(p.getSort());
//            paramsConfig.setParamName(p.getParam_name());
//            paramsConfig.setParamValue(p.getParam_value());
//            paramsConfig.setRequired(p.getRequired());
//            paramsConfig.setOperator(p.getOperator());
//            paramsConfig.setDefaultValue(p.getDefault_value());
//            paramsConfig.setDescription(p.getDescription());
//            paramsConfig.setSample(p.getSample());
//            dynamicAPIParamsConfigMapper.insert(paramsConfig);
//        }
//        return mainId;
//    }
//
//}
