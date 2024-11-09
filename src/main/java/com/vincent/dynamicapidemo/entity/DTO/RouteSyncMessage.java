package com.vincent.dynamicapidemo.entity.DTO;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/8/24
 * @Description:
 */
@Data
public class RouteSyncMessage implements Serializable {

    private String path;
    private String method;
    private String handler;
    private String targetMethodName;
}
