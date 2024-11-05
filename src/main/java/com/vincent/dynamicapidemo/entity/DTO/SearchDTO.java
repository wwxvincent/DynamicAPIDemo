package com.vincent.dynamicapidemo.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/2/24
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDTO {

    private List<Param> paramsList;


    private List<String>selectList;
}
