package com.vincent.dynamicapidemo.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/24/24
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateApiDTO {

    String path;
    String method;
    String[][] params;
    String sourceType;
    String sql;
    String sourceId;
    String[] jdbcParamValues;

}
