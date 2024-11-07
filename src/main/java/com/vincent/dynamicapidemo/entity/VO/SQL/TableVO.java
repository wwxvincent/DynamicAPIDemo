package com.vincent.dynamicapidemo.entity.VO.SQL;

import lombok.Data;

import java.util.List;

@Data
public class TableVO {

    private String tableName ;
    private String tableAlias ;
    private String type;  //表类型 ： 2.实体表 0物理表 1 逻辑临时表
    private String dbName ;   //数据库名
    private String path ;    //表路径
    public List<ItemVO> itemList  ;   //字段列表

    private boolean isSingle = false ;  //是否单表
}
