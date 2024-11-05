package com.vincent.dynamicapidemo.entity.api;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/4/24
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("dynamic_API_database_source_config")
public class DynamicAPIDatasourceConfig {
    private String datasourceType;
    private String datasourceUrl;
    private String datasourceUsername;
    private String datasourcePassword;
    private String datasourceDriverClassName;

}
