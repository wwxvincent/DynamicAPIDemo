package com.vincent.dynamicapidemo.dynamicApi.entity.VO.SQL;

import lombok.Data;


/**
 * @author Lu.yk
 * @date 2022/6/14 - 14:22
 */
@Data
public class BloodRelationVO implements Cloneable {

    private String sourceName ;
    private String sourcePath ;
    private String sourceType ;

    private String targetName ;
    private String targetPath ;
    private String targetType ;

    public String targetItemFormula ;   //目标字段口径
    public String objectType ;   //对象类型 ： Table ， Item
}
