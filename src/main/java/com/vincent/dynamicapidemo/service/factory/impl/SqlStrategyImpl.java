package com.vincent.dynamicapidemo.service.factory.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vincent.dynamicapidemo.entity.DTO.ApiConfig;
import com.vincent.dynamicapidemo.entity.DTO.Param;
import com.vincent.dynamicapidemo.entity.DTO.SearchDTO;
import com.vincent.dynamicapidemo.entity.VO.ResponseVO;
import com.vincent.dynamicapidemo.entity.api.DynamicAPIDict;
import com.vincent.dynamicapidemo.entity.api.DynamicAPIMainConfig;
import com.vincent.dynamicapidemo.entity.api.DynamicAPIParamsConfig;
import com.vincent.dynamicapidemo.mapper.DynamicAPIMainConfigMapper;
import com.vincent.dynamicapidemo.mapper.DynamicAPIParamsConfigMapper;
import com.vincent.dynamicapidemo.service.factory.ApiStrategy;
import com.vincent.dynamicapidemo.mapper.DynamicAPIDictMapper;
import com.vincent.dynamicapidemo.util.JDBCUtil;
import com.vincent.dynamicapidemo.util.SqlProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/14/24
 * @Description:
 */
@Service
@Slf4j
public class SqlStrategyImpl implements ApiStrategy {

//    private final String targetMethodName = "dynamicApiMethodSql";
    private final String targetMethodName = "dynamicApiMethod";

    @Autowired
    private DynamicAPIDictMapper dynamicAPIDictMapper;

    @Autowired
    private DynamicAPIMainConfigMapper dynamicAPIMainConfigMapper;

    @Autowired
    private DynamicAPIParamsConfigMapper dynamicAPIParamsConfigMapper;

    @Override
    public String saveConfig(ApiConfig apiConfig, String handler, String url) {
        //<=== 1. 查询数据字典表，获取数据库连接配置表id ===>
        QueryWrapper<DynamicAPIDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("datasource_id") // 只选择 datasource_id 字段
                .eq("type_code",apiConfig.getSourceType())
                .eq("schema_code", apiConfig.getSourceSchema());
        List<DynamicAPIDict> resultList = dynamicAPIDictMapper.selectList(queryWrapper);
        int datasourceId = resultList.get(0).getDatasourceId();
        //<=== 2. 存入API配置主表 ===>
        DynamicAPIMainConfig dynamicAPIMainConfig = new DynamicAPIMainConfig();
        String mainUuid = UUID.randomUUID().toString();
        dynamicAPIMainConfig.setId(mainUuid);
        dynamicAPIMainConfig.setCreateType(apiConfig.getCreateType());
        dynamicAPIMainConfig.setPath(apiConfig.getPath());
        dynamicAPIMainConfig.setMethod(apiConfig.getMethod());
        dynamicAPIMainConfig.setHandler(handler);
        dynamicAPIMainConfig.setTargetMethodName(targetMethodName);
        dynamicAPIMainConfig.setUrl(url);
        dynamicAPIMainConfig.setApiName(apiConfig.getApiName());
        dynamicAPIMainConfig.setApiWorkspace(apiConfig.getApiWorkArea());
        dynamicAPIMainConfig.setDatabaseDictId(datasourceId);
        dynamicAPIMainConfig.setSqlSentence(apiConfig.getSqlSentence());
        dynamicAPIMainConfig.setStatus("1");
        String returnType = ( apiConfig.getReturnType() ).isEmpty() ? "json" : apiConfig.getReturnType();
        dynamicAPIMainConfig.setReturnType(returnType);
        dynamicAPIMainConfigMapper.insert(dynamicAPIMainConfig);
        //<=== 3.  存入参明细表 where ===>
        for (Param p : apiConfig.getParamsList()) {
            DynamicAPIParamsConfig paramsConfig = new DynamicAPIParamsConfig();
            paramsConfig.setId(UUID.randomUUID().toString());
            paramsConfig.setMainConfigId(mainUuid);
            paramsConfig.setCreateType(apiConfig.getCreateType());
            paramsConfig.setParamName(p.getParam_name());
            paramsConfig.setSort(p.getSort());
            paramsConfig.setParamValue(p.getParam_value());
            paramsConfig.setDefaultValue(p.getDefault_value());
            paramsConfig.setDescription(p.getDescription());
            dynamicAPIParamsConfigMapper.insert(paramsConfig);

        }
        return mainUuid;
    }

    @Override
    public String getTargetMethodName() {
        return this.targetMethodName;
    }

    @Override
    public ResponseVO getDataFromDiffDBSource(SearchDTO searchDTO, String connUrl, String connDriverClassName, String connUsername, String connPassword , String sql, List<Param> paramsFromRequest, List<DynamicAPIParamsConfig> paramsFromTable) {

        try {
            // 1. 获取替换占位符后的sql语句，和根据占位符顺序生成的map
            Map.Entry<String, Map<Integer, String>> result= SqlProcessUtil.parseSqlPlaceholders(sql);
            String sqlNew = result.getKey();
            Map<Integer, String> paramsIndexList = result.getValue();
            // 2. 结合request信息，生成占位符替换list
            List<Object> placerHolderList = SqlProcessUtil.getPlaceHolderList(paramsIndexList, paramsFromRequest, paramsFromTable);
            // 3. 执行JDBC连接，查询
            return JDBCUtil.executeSqlWithPlaceHolder(connUrl, connDriverClassName, connUsername, connPassword, sqlNew, placerHolderList);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
