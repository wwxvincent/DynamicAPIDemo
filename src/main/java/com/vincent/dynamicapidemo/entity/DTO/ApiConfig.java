package com.vincent.dynamicapidemo.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/4/24
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiConfig {
    private String createType;
    private String apiName;
    private String apiWorkArea;
    private String path;
    private String method;
    private String sourceType;
    private String sourceSchema;
    private String sourceTable;
    private List<String> selectList;
    private List<Param> paramsList;


}
