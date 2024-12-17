package com.vincent.dynamicapidemo.dynamicApi.listener;


import com.vincent.dynamicapidemo.dynamicApi.entity.api.DynamicAPIMainConfig;
import com.vincent.dynamicapidemo.dynamicApi.mapper.DynamicAPIMainConfigMapper;
import com.vincent.dynamicapidemo.util.DynamicApiUtil;
import com.vincent.dynamicapidemo.util.SentinelConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.nio.charset.StandardCharsets;
import java.util.Objects;



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

    @Autowired
    private Environment env;

    @Autowired
    private DynamicAPIMainConfigMapper dynamicAPIMainConfigMapper;



    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            log.debug(new String(message.getBody(), StandardCharsets.UTF_8));
            String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
            if (messageBody.startsWith("\"") && messageBody.endsWith("\"")) {
                messageBody = messageBody.substring(1, messageBody.length() - 1);
            }            String[] parts = messageBody.split(":", 2);
            String messageId = "";
            log.debug("###### Redis info ###### ip address from Redis topic: "+parts[0]);
            log.debug("###### Redis info ###### ip addr for local machine: "+ DynamicApiUtil.getIpAddr());
            log.debug("###### Redis info ###### message of this message: "+parts[1]);
            if (!Objects.equals(DynamicApiUtil.getIpAddr(), parts[0])) {
                try {
                    messageId = parts[1];
                    log.debug("###### Redis info ###### messageId: " + messageId);
                } catch (NumberFormatException e) {
                    log.debug("###### Redis info ###### 无法将消息转换为int: " + messageBody);
                }
                DynamicAPIMainConfig dynamicAPIMainConfig = dynamicAPIMainConfigMapper.selectById(messageId);
                if(dynamicAPIMainConfig==null){
                    log.debug("Invalid dynamic api config ID from redis message");
                    throw new RuntimeException("Invalid dynamic api config ID from redis message, do not exist in database");
                }
                RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);

                // 从DB中获取配置信息，重新绑定API。
                DynamicApiUtil.create(bean, dynamicAPIMainConfig.getPath(), dynamicAPIMainConfig.getMethod(), dynamicAPIMainConfig.getHandler(), dynamicAPIMainConfig.getTargetMethodName());
                // 注册sentinel信息
                // 获取path组装资源名字，重新配置sentinel中的限流降级默认配置
                String sourceName = env.getProperty("server.servlet.context-path") + dynamicAPIMainConfig.getPath();
                SentinelConfigUtil.initFlowRules(sourceName);

                log.info("<===== load dynamic API From Redis topic : " + dynamicAPIMainConfig.toString());
            } else { // test, 成功后删掉else
                System.out.println("本机发出的topic到redis， 跳过");
                log.info("本机发出的topic到redis， 跳过");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
