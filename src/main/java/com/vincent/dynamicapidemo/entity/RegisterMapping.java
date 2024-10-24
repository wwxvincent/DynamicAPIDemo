package com.vincent.dynamicapidemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/23/24
 * @Description: RequestMappingInfo mapping, Object handler, Method method
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterMapping {
    private RequestMappingInfo requestMappingInfo;
    private Object handler;
    private Method method;
}
