package com.vincent.dynamicapidemo.factory;

import com.vincent.dynamicapidemo.factory.impl.TableSaveStrategyImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/14/24
 * @Description:
 */
@Component
public class ApiSaveFactory {


    @Autowired
    private TableSaveStrategyImpl tableSaveStrategyImpl;

    public ApiSaveStrategy getStrategy(String type) {
        switch (type.toUpperCase()) {
            case "TABLE":
                return tableSaveStrategyImpl;
            case "SQL":
                // 可以添加SQL策略类
                throw new UnsupportedOperationException("SQL save strategy not implemented yet");
            case "JAR":
                // 可以添加JAR策略类
                throw new UnsupportedOperationException("JAR save strategy not implemented yet");
            default:
                throw new IllegalArgumentException("Unknown create type: " + type);
        }
    }
}
