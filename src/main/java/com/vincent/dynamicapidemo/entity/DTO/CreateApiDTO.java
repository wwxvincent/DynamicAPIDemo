package com.vincent.dynamicapidemo.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/24/24
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateApiDTO {

    String APIName;
    String APIWorkArea;
    String path;
    String method;
    String sourceType;
    String sourceDatabase;
    String sourceTable;
    List<Object> selectList;
    List<Object> fixedWhereList;
    List<Object> optionalWhereList;

}
