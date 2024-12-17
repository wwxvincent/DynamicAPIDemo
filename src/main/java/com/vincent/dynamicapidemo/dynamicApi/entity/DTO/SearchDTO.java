package com.vincent.dynamicapidemo.dynamicApi.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
