package com.vincent.dynamicapidemo.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/4/24
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Param {
    private int sort;
    private String param_name;
    private String param_value;
    private String operator;
    private String required;
    private String default_value;
    private String description;
    private String sample;
}
