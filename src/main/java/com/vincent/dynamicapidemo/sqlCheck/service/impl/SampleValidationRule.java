package com.vincent.dynamicapidemo.sqlCheck.service.impl;

import com.vincent.dynamicapidemo.dynamicApi.entity.VO.ResponseVO;
import com.vincent.dynamicapidemo.sqlCheck.service.SqlValidationRule;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 12/17/24
 * @Description:
 */
@Component
public class SampleValidationRule implements SqlValidationRule {
    @Override
    public String getName() {
        return "SampleValidation";
    }

    @Override
    public ResponseVO validate(String sql, String type, Map<String, Object> content) {
        ResponseVO responseVO = new ResponseVO();
        responseVO.setMsg(this.getName()+" : just for sample validation, add rule to use");
        return responseVO;
    }
}
