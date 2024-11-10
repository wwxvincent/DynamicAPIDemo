package com.vincent.dynamicapidemo.util;

import com.vincent.dynamicapidemo.controller.AdapterController;
import com.vincent.dynamicapidemo.entity.DTO.SearchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;

import static com.vincent.dynamicapidemo.util.SentinelConfigUtil.initFlowRules;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/10/24
 * @Description:
 */
public class DynamicApiUtil {

    @Autowired
    private ApplicationContext applicationContext;

    public boolean create (String path, String method, String handler, String targetMethodName, String resourceName) {
        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);

        try {
            // 从DB中获取配置信息，重新绑定API。
            RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(path)
                    .methods(RequestMethod.valueOf(method))
                    .build();
            bean.registerMapping(requestMappingInfo, handler, AdapterController.class.getDeclaredMethod(targetMethodName, SearchDTO.class, HttpServletRequest.class));
            // 获取path组装资源名字，重新配置sentinel中的限流降级默认配置
            SentinelConfigUtil.initFlowRules(resourceName);

            return true;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
