package com.vincent.dynamicapidemo.util;

import com.vincent.dynamicapidemo.dynamicApi.controller.AdapterController;
import com.vincent.dynamicapidemo.dynamicApi.entity.DTO.SearchDTO;
import com.vincent.dynamicapidemo.dynamicApi.entity.api.DynamicAPIMainConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/10/24
 * @Description:
 */
@Slf4j
@Service
public class DynamicApiUtil {


    @Autowired
    private  ApplicationContext applicationContext;



    /**
     * @Description: 创建动态API，注册动态路由，绑定给定的url和制定的调用方法
     */
    public boolean create (String path, String method, String handler, String targetMethodName) {

        try {
            // 从DB中获取配置信息，重新绑定API。
            RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
            // check it, Do not duplicate register same URL&Method in Spring MVC
            Map<RequestMappingInfo, HandlerMethod> handlerMethods = bean.getHandlerMethods();
            boolean isExisted = handlerMethods.keySet().stream()
                    .anyMatch(info -> info.getPatternsCondition().getPatterns().contains(path)
                            && info.getMethodsCondition().getMethods().contains(RequestMethod.valueOf(method)));
            if(isExisted){return false;}
            RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(path)
                    .methods(RequestMethod.valueOf(method))
                    .build();
            bean.registerMapping(requestMappingInfo, handler, AdapterController.class.getDeclaredMethod(targetMethodName, SearchDTO.class, HttpServletRequest.class));

            return true;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean destroy(DynamicAPIMainConfig dynamicAPIMainConfig) {
        // 从DB中获取配置信息，重新绑定API。
        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
        // check it, Do not duplicate register same URL&Method in Spring MVC
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = bean.getHandlerMethods();
        boolean isExisted = handlerMethods.keySet().stream()
                .anyMatch(info -> info.getPatternsCondition().getPatterns().contains(dynamicAPIMainConfig.getPath())
                        && info.getMethodsCondition().getMethods().contains(RequestMethod.valueOf(dynamicAPIMainConfig.getMethod())));
        if(!isExisted){return false;}
        RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(dynamicAPIMainConfig.getPath())
                .methods(RequestMethod.valueOf(dynamicAPIMainConfig.getMethod()))
                .build();
        bean.unregisterMapping(requestMappingInfo);
        return true;
    }




    /**
     *
     * 获取本机ip地址
     */
    public static String getIpAddr() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    // 排除回环地址（127.0.0.1），获取 IPv4 地址
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().indexOf(":") == -1) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }
}
