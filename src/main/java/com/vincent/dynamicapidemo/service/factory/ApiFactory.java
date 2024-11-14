package com.vincent.dynamicapidemo.service.factory;

import com.vincent.dynamicapidemo.service.factory.impl.SqlStrategyImpl;
import com.vincent.dynamicapidemo.service.factory.impl.TableStrategyImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/14/24
 * @Description:
 */
@Component
public class ApiFactory {


    @Autowired
    private TableStrategyImpl tableStrategyImpl;

    @Autowired
    private SqlStrategyImpl sqlStrategyImpl;

    public ApiStrategy getStrategy(String type) {
        switch (type.toUpperCase()) {
            case "TABLE":
                return tableStrategyImpl;
            case "SQL":
                return sqlStrategyImpl;
            case "JAR":
                // 可以添加JAR策略类
                throw new UnsupportedOperationException("JAR save strategy not implemented yet");
            default:
                throw new IllegalArgumentException("Unknown create type: " + type);
        }
    }
}
