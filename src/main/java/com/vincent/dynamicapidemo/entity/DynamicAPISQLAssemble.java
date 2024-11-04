package com.vincent.dynamicapidemo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/2/24
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("dynamic_API_sql_assemble")
public class DynamicAPISQLAssemble {

    private String id;
    private String datasourceId;
    private String selectElement;
    private String whereElementFixed;
    private String whereElementFixedOperator;
    private String whereElementOptional;
    private String whereElementOptionalOperator;
    private String whereElementOptionalStatus;
}
