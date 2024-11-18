package com.vincent.dynamicapidemo.util;

import com.vincent.dynamicapidemo.controller.AdapterController;
import com.vincent.dynamicapidemo.entity.DTO.SearchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static com.vincent.dynamicapidemo.util.SentinelConfigUtil.initFlowRules;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/10/24
 * @Description:
 */
public class DynamicApiUtil {


    /**
     * @Description: 创建动态API，注册动态路由，绑定给定的url和制定的调用方法
     * @param bean
     * @param path
     * @param method
     * @param handler
     * @param targetMethodName
     * @return
     */
    public static boolean create (RequestMappingHandlerMapping bean, String path, String method, String handler, String targetMethodName) {

        try {
            // 从DB中获取配置信息，重新绑定API。
            RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(path)
                    .methods(RequestMethod.valueOf(method))
                    .build();
            bean.registerMapping(requestMappingInfo, handler, AdapterController.class.getDeclaredMethod(targetMethodName, SearchDTO.class, HttpServletRequest.class));

            return true;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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
