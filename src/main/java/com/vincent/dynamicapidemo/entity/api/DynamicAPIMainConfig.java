package com.vincent.dynamicapidemo.entity.api;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("dynamic_API_main_config")
public class DynamicAPIMainConfig {
    @TableId(type = IdType.AUTO)
    private String id; // 假设 ID 类型是 Integer
    private String path;
    private String method;
    private String handler;
    private String targetMethodName;
    private String url;
    private String apiName;
    private String apiWorkspace;
    private int databaseDictId;
    private String selectList;
    private String sqlSentence;
    private String status;
    private String returnType;
    private String createType;

}
