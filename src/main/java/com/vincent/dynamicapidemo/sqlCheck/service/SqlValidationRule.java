package com.vincent.dynamicapidemo.sqlCheck.service;

import com.vincent.dynamicapidemo.dynamicApi.entity.VO.ResponseVO;

import java.util.Map;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 12/16/24
 * @Description:
 */
public interface SqlValidationRule {

    String getName();

    ResponseVO validate(String sql, String type, Map<String, Object> content);
}
