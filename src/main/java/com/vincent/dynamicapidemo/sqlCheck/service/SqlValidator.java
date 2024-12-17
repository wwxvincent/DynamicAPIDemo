package com.vincent.dynamicapidemo.sqlCheck.service;

import com.vincent.dynamicapidemo.dynamicApi.entity.VO.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 12/16/24
 * @Description:
 */
@Component
@Slf4j
public class SqlValidator {

    private final Map<String, SqlValidationRule> ruleMap = new HashMap<>();

    // Spring 自动注入所有实现了 SqlValidationRule 的组件
    public SqlValidator(List<SqlValidationRule> rules) {
        for (SqlValidationRule rule : rules) {
            ruleMap.put(rule.getName(), rule);
        }
    }

    /**
     * 执行动态校验
     */
    public List<ResponseVO> validate(String sql, String type, List<String> ruleNames, Map<String, Object> context) {
        List<ResponseVO> results = new ArrayList<>();
        for (String ruleName : ruleNames) {
            SqlValidationRule rule = ruleMap.get(ruleName);
            if (rule != null) {
                results.add(rule.validate(sql,type, context));
            } else {
                results.add(ResponseVO.fail("未找到规则: " + ruleName));
            }
        }
        return results;
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("Rule map initialized: " + ruleMap);
    }
}
