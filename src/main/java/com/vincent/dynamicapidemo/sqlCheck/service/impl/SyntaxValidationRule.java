package com.vincent.dynamicapidemo.sqlCheck.service.impl;

import com.alibaba.druid.wall.Violation;
import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.vincent.dynamicapidemo.dynamicApi.entity.VO.ResponseVO;
import com.vincent.dynamicapidemo.sqlCheck.service.SqlValidationRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 12/16/24
 * @Description:
 */
@Component
@Slf4j
public class SyntaxValidationRule implements SqlValidationRule {
    @Override
    public String getName() {
        return "SyntaxValidation";
    }

    @Override
    public ResponseVO validate(String sql, String type, Map<String, Object> content) {
        try {
            // 假设语法校验逻辑在此处
            WallProvider provider = new MySqlWallProvider();
            WallCheckResult result = provider.check(sql);

            boolean valid = result.isSyntaxError();
            StringBuilder errorStr = new StringBuilder();
            if (valid) {
                List<Violation> errorList = result.getViolations();
                for (Violation error : errorList) {
                    errorStr.append(error.getMessage()).append("\n");
                }
            }
            return !valid ? ResponseVO.successWithMsg(this.getName() + " : No Error") : ResponseVO.fail(this.getName() + " : " +errorStr.toString());
        } catch (Exception e) {
            return ResponseVO.fail(this.getName() + " : 语法解析异常: " + e.getMessage());
        }
    }
}
