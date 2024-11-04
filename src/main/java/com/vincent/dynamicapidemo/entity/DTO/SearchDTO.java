package com.vincent.dynamicapidemo.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/2/24
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDTO {

    private String[][] params;


    private String bindingId;
}
