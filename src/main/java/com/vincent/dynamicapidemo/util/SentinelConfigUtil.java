package com.vincent.dynamicapidemo.util;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/7/24
 * @Description:
 */
public class SentinelConfigUtil {

    // 配置sentinel中的限流降级默认配置
    public static void initFlowRules(String resourceName) {
        List<FlowRule> rules = FlowRuleManager.getRules();
        FlowRule rule = new FlowRule();
        rule.setResource(resourceName);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 设置每秒的通行数为1
        rule.setCount(1);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
}
