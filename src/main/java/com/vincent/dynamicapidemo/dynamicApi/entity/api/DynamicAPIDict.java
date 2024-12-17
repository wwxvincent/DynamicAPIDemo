package com.vincent.dynamicapidemo.dynamicApi.entity.api;

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
@TableName("dynamic_API_dict")
public class DynamicAPIDict {
    private String typeCode;
    private String typeName;
    private String schemaCode;
    private String schemaName;
    private int datasourceId;
}
