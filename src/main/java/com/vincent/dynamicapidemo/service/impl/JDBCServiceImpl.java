package com.vincent.dynamicapidemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vincent.dynamicapidemo.entity.DTO.Param;
import com.vincent.dynamicapidemo.entity.DTO.SearchDTO;

import com.vincent.dynamicapidemo.entity.VO.ResponseVO;


import com.vincent.dynamicapidemo.entity.api.DynamicAPIDatasourceConfig;
import com.vincent.dynamicapidemo.entity.api.DynamicAPIDict;
import com.vincent.dynamicapidemo.entity.api.DynamicAPIMainConfig;
import com.vincent.dynamicapidemo.entity.api.DynamicAPIParamsConfig;
import com.vincent.dynamicapidemo.mapper.DynamicAPIDatasourceConfigMapper;
import com.vincent.dynamicapidemo.mapper.DynamicAPIDictMapper;
import com.vincent.dynamicapidemo.mapper.DynamicAPIMainConfigMapper;
import com.vincent.dynamicapidemo.mapper.DynamicAPIParamsConfigMapper;
import com.vincent.dynamicapidemo.service.JDBCService;
import com.vincent.dynamicapidemo.util.JDBCUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringUtils.isNumeric;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/1/24
 * @Description:
 */
@Service
@Slf4j
public class JDBCServiceImpl implements JDBCService {

    @Autowired
    private DynamicAPIDictMapper dynamicAPIDictMapper;

    @Autowired
    private DynamicAPIDatasourceConfigMapper dynamicAPIDatasourceConfigMapper;

    @Autowired
    private DynamicAPIMainConfigMapper dynamicAPIMainConfigMapper;

    @Autowired
    private DynamicAPIParamsConfigMapper dynamicAPIParamsConfigMapper;




    @Override
    public ResponseVO getDataFromDiffDBSource(SearchDTO searchDTO, String url)  {

        try {
            // 1 通过unique 的 URL 找到 datasourceDICT中的id，再通过字典表中的记录，找到对应的数据库连接信息
            QueryWrapper<DynamicAPIMainConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("url", url);
            DynamicAPIMainConfig dynamicAPIMainConfig = dynamicAPIMainConfigMapper.selectOne(queryWrapper);
            int databaseDictId = dynamicAPIMainConfig.getDatabaseDictId();
            QueryWrapper<DynamicAPIDict> dynamicAPIDictQueryWrapper = new QueryWrapper<>();
            dynamicAPIDictQueryWrapper.eq("id", databaseDictId);
            DynamicAPIDict dynamicAPIDict = dynamicAPIDictMapper.selectOne(dynamicAPIDictQueryWrapper);
            int datasourceConfigId = dynamicAPIDict.getDatasourceId();
            QueryWrapper<DynamicAPIDatasourceConfig> dynamicAPIDatasourceConfigQueryWrapper = new QueryWrapper<>();
            dynamicAPIDatasourceConfigQueryWrapper.eq("id", datasourceConfigId);
            DynamicAPIDatasourceConfig dynamicAPIDatasourceConfig = dynamicAPIDatasourceConfigMapper.selectOne(dynamicAPIDatasourceConfigQueryWrapper);

            // 2. handle the sql for execute
            /**
             *  取消select 可以随意选择功能，直接写死select部分（2/4)
             */
            // 2.1 handle with select part
//            String selectStr;
//            if (!searchDTO.getSelectList().isEmpty()) {
//                String selectParam = searchDTO.getSelectList().toString().replace("[", "").replace("]", "");
//                selectStr = selectHandler(selectParam, dynamicAPIMainConfig.getSelectList());
//            } else {
//                selectStr = dynamicAPIMainConfig.getSelectList();
//            }


            // 2.2 handle with where part
            List<Param> paramsRequest = searchDTO.getParamsList();
            QueryWrapper<DynamicAPIParamsConfig> dynamicAPIParamsConfigQueryWrapper = new QueryWrapper<>();
            dynamicAPIParamsConfigQueryWrapper.eq("main_config_id", dynamicAPIMainConfig.getId());
            dynamicAPIParamsConfigQueryWrapper.orderByAsc("sort");
            List<DynamicAPIParamsConfig> paramsFromTable = dynamicAPIParamsConfigMapper.selectList(dynamicAPIParamsConfigQueryWrapper);
            String whereStr = whereHandler(paramsRequest, paramsFromTable);
            String sqlSentence = dynamicAPIMainConfig.getSqlSentence();
            /**
             *  取消select 可以随意选择功能，直接写死select部分（3/4)
             */
//            sqlSentence = sqlSentence.replace("*", selectStr);
            // 3. 创建JDBC连接
            return JDBCUtil.executeSql(dynamicAPIDatasourceConfig.getDatasourceUrl(), dynamicAPIDatasourceConfig.getDatasourceDriverClassname(), dynamicAPIDatasourceConfig.getDatasourceUsername(), dynamicAPIDatasourceConfig.getDatasourcePassword(), sqlSentence+whereStr);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     *  取消select 可以随意选择功能，直接写死select部分（4/4)
     */
//    private String selectHandler( String selectParam, String selectFromTable) {
//        // 2.1 compare selectParam and selectFromTable
//        List<String> selectFromTableList = Arrays.asList(selectFromTable.split(","));
//        Set<String> selectParamSet = Arrays.stream(selectParam.split(","))
//                .map(String::trim)
//                .collect(Collectors.toSet());
//        // 保留 selectFromTableList 中的元素且这些元素也出现在 selectParamSet 中
//        List<String> resultList = selectFromTableList.stream()
//                .map(String::trim) // 去除空格
//                .filter(selectParamSet::contains) // 仅保留在 selectParamSet 中的元素
//                .collect(Collectors.toList());
//        return resultList.toString().replace("[", "").replace("]", "");
//    }

    private String whereHandler(List<Param> paramsRequest, List<DynamicAPIParamsConfig> paramsFromTable) {
        if (paramsRequest.isEmpty()) return "";

        Map<String, Object> paramsMap = new HashMap<>();
        for (Param param : paramsRequest) {
            paramsMap.put(param.getParam_name(), param.getParam_value());
        }
        StringBuilder sb = new StringBuilder();
        for (DynamicAPIParamsConfig item : paramsFromTable) {

            if (paramsMap.containsKey(item.getParamName())){ // 如果入参里有，加上
                sb.append(" AND ").append(item.getParamName()).append(" ").append(item.getOperator()).append(" ");
                if(!isNumeric(item.getParamValue())){
                    sb.append("'").append(paramsMap.get(item.getParamName())).append("'").append("\n");
                } else {
                    sb.append(paramsMap.get(item.getParamName())).append("\n");
                }

            } else if (item.getRequired().equals("1")) { // 如果入参里面没有，但是这个是必填项，也加上，拿默认值
                sb.append(" AND ").append(item.getParamName()).append(" ").append(item.getOperator()).append(" ");
                if(!isNumeric(item.getParamValue())){
                    sb.append("'").append(item.getDefaultValue()).append("'").append("\n");
                } else {
                    sb.append(item.getDefaultValue()).append("\n");
                }


            }
        }
        return sb.toString();
    }

}
