package com.vincent.dynamicapidemo.entity.api;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/4/24
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("dynamic_API_params_config")
public class DynamicAPIParamsConfig {
    private String id;
    private String mainConfigId;
    private int sort;
    private String paramName;
    private String paramValue;
    private String required;
    private String operator;
    private String defaultValue;
    private String description;
    private String sample;
}
