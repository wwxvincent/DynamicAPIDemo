package com.vincent.dynamicapidemo.entity.VO.SQL;

import lombok.Data;


/**
 * @author Lu.yk
 * @date 2022/6/14 - 14:22
 */
@Data
public class BloodTableRelationVO implements Cloneable {

    private TableVO sourceTableVO ;   //源表对象
    private TableVO targetTableVO ;   //目标表对象
}
