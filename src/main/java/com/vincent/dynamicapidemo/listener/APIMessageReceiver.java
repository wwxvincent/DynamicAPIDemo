package com.vincent.dynamicapidemo.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent.dynamicapidemo.controller.AdapterController;
import com.vincent.dynamicapidemo.entity.DTO.MessageDTO;
import com.vincent.dynamicapidemo.entity.DTO.RouteSyncMessage;
import com.vincent.dynamicapidemo.entity.DTO.SearchDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/8/24
 * @Description:
 */
@Slf4j
@Component
public class APIMessageReceiver implements MessageListener {

    @Autowired
    private ApplicationContext applicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper();



    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // 反序列化消息
            RouteSyncMessage routeSyncMessage = objectMapper.readValue(message.getBody(), RouteSyncMessage.class);
            if (routeSyncMessage != null) {
                registerDynamicApi(routeSyncMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // PUBLISH api_sync_channel "{\"path\":\"/test\", \"method\":\"POST\", \"handler\":\"adapterController\", \"targetMethodName\":\"dynamicApiMethodSQL\"}"
    //用这个 在 redis里 发布
    private void registerDynamicApi(RouteSyncMessage routeSyncMessage) {
        try {
            RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);

            // 从DB中获取配置信息，重新绑定API。
            RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(routeSyncMessage.getPath())
                    .methods(RequestMethod.valueOf(routeSyncMessage.getMethod()))
                    .build();
            bean.registerMapping(requestMappingInfo, routeSyncMessage.getHandler(), AdapterController.class.getDeclaredMethod(routeSyncMessage.getTargetMethodName(), SearchDTO.class, HttpServletRequest.class));
            // 获取path组装资源名字，重新配置sentinel中的限流降级默认配置
//            String contextPath = env.getProperty("server.servlet.context-path");
//            initFlowRules(contextPath +  dynamicAPIMainConfig.getPath());

            System.out.println(routeSyncMessage.getPath());
            System.out.println(routeSyncMessage.getMethod());
            System.out.println(routeSyncMessage.getTargetMethodName());
            System.out.println(routeSyncMessage.getHandler());
//            log.info("<===== load dynamic API: " + dynamicAPIMainConfig.toString());
            System.out.println("API 注册成功：" + routeSyncMessage.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
