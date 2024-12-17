package com.vincent.dynamicapidemo.sqlCheck.entity.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 12/16/24
 * @Description:
 */
@Data
public class SqlValidationRequestVO {

    private String type;
    private String sql;
    private List<String> ruleNames;
    private Map<String, Object> context;
}
