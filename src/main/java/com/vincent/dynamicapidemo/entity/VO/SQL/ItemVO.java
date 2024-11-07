package com.vincent.dynamicapidemo.entity.VO.SQL;

import lombok.Data;

@Data
public class ItemVO {

    private String itemName ;
    private String itemAlias ;
    private String itemFormula ;  //计算公式
    private String itemOwner ;   //所有者

    public boolean itemNotIsConstant  = true ;  //不为常量

    
}
