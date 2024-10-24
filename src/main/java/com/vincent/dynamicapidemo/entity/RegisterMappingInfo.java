package com.vincent.dynamicapidemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *  @Author: Vincent(Wenxuan) Wang
 *  @Date: 10/23/24
 *  @Description:
 *          RegisterMappingInfo registerMappingInfo = new RegisterMappingInfo();
 *         registerMappingInfo.setPaths("/test4");
 *         registerMappingInfo.setParams(new String[]{"fileName", "type", "isSort"});
 *         registerMappingInfo.setMethods("GET");
 *         registerMappingInfo.setHandler("adapterController");
 *         registerMappingInfo.setTargetMethodName("myTest3");
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterMappingInfo {
    private String id;
    private String paths;
    private String params;
    private RequestMethod methods;
    private String handler;
    private String targetMethodName;
}
