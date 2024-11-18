package com.vincent.dynamicapidemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/30/24
 * @Description:
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DataSource {
    String datasourceId;
    String datasourceName;
    String url;
    String userName;
    String passWord;
    String className;
    String code;
    String dataBaseType;
}
