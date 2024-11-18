package com.vincent.dynamicapidemo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import org.springframework.data.redis.core.RedisTemplate;


/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/8/24
 * @Description:
 */
@Slf4j
@Component
public class RedisUtils {
    //注入自定义redisTemplate
//    @Autowired
//    @Qualifier("redisTemplate")
//    private RedisTemplate redisTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 向通道发布消息
     */
    public boolean convertAndSend(String channel, Object message) {
        if (!StringUtils.hasText(channel)) {
            return false;
        }
        try {
            redisTemplate.convertAndSend(channel, message);
            log.info("发送消息成功，channel：{}，message：{}", channel, message);
            return true;
        } catch (Exception e) {
            log.info("发送消息失败，channel：{}，message：{}", channel, message);
            e.printStackTrace();
        }
        return false;
    }
}
