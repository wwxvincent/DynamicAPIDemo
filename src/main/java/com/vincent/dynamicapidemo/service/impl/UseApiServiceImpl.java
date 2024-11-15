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
import com.vincent.dynamicapidemo.service.UseApiService;
import com.vincent.dynamicapidemo.service.factory.ApiFactory;
import com.vincent.dynamicapidemo.service.factory.ApiStrategy;
import com.vincent.dynamicapidemo.util.JDBCUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/14/24
 * @Description:
 */
@Slf4j
@Service
public class UseApiServiceImpl implements UseApiService {

    @Autowired
    private DynamicAPIDictMapper dynamicAPIDictMapper;

    @Autowired
    private DynamicAPIDatasourceConfigMapper dynamicAPIDatasourceConfigMapper;

    @Autowired
    private DynamicAPIMainConfigMapper dynamicAPIMainConfigMapper;

    @Autowired
    private DynamicAPIParamsConfigMapper dynamicAPIParamsConfigMapper;

    @Autowired
    private ApiFactory apiFactory;

    @Override
    public ResponseVO getDataFromDiffDBSource(SearchDTO searchDTO, String url) {
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
            // 1.1 拿到执行JDBC 的 四要素
            String connUrl = dynamicAPIDatasourceConfig.getDatasourceUrl();
            String connDriverClassName = dynamicAPIDatasourceConfig.getDatasourceDriverClassname();
            String connUsername = dynamicAPIDatasourceConfig.getDatasourceUsername();
            String connPassword = dynamicAPIDatasourceConfig.getDatasourcePassword();
            // 1.2 拿到db中的param list 和 发送请求的 param list, 并把他们转化成 name value 的 map
            List<Param> paramsFromRequest = searchDTO.getParamsList();
            QueryWrapper<DynamicAPIParamsConfig> dynamicAPIParamsConfigQueryWrapper = new QueryWrapper<>();
            dynamicAPIParamsConfigQueryWrapper.eq("main_config_id", dynamicAPIMainConfig.getId());
            List<DynamicAPIParamsConfig> paramsFromTable = dynamicAPIParamsConfigMapper.selectList(dynamicAPIParamsConfigQueryWrapper);
            // 1.3 拿到未处理的 sql
            String sql = dynamicAPIMainConfig.getSqlSentence();
            // 2. sql的处理 和其他的逻辑，以及处理好后执行JDBC访问数据库的代码。都放到各自工厂实现类里去
            ApiStrategy strategy = apiFactory.getStrategy(dynamicAPIMainConfig.getCreateType());
            return  strategy.getDataFromDiffDBSource(searchDTO, connUrl, connDriverClassName, connUsername, connPassword,sql, paramsFromRequest, paramsFromTable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
