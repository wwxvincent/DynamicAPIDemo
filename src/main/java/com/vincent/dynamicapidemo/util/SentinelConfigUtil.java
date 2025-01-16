package com.vincent.dynamicapidemo.util;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.beans.factory.annotation.Value;


import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/7/24
 * @Description:
 */
public class SentinelConfigUtil {
    @Value("${server.servlet.context-path}")
    private static String contextPath;

    public SentinelConfigUtil(String contextPath) {
        SentinelConfigUtil.contextPath = contextPath;
    }

    // 配置sentinel中的限流降级默认配置
    public static void initFlowRules(String resourceName) {
        resourceName = contextPath + resourceName;
        List<FlowRule> rules = FlowRuleManager.getRules();
        FlowRule rule = new FlowRule();
        rule.setResource(resourceName);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 设置每秒的通行数为1
        rule.setCount(1);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }

    public static void removeFlowRules(String resourceName) {
        resourceName = contextPath + resourceName;
        // 获取当前所有的限流规则
        List<FlowRule> oldRules = FlowRuleManager.getRules();
        // 过滤掉不需要的规则（即资源名为resourceName的规则）
        String finalResourceName = resourceName;
        List<FlowRule> newRules = oldRules.stream()
                .filter(rule -> !rule.getResource().equals(finalResourceName))
                .collect(Collectors.toList());
        // 重新加载规则
        FlowRuleManager.loadRules(newRules);
    }
}
