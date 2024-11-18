package com.vincent.dynamicapidemo.listener;

import com.vincent.dynamicapidemo.entity.DTO.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/8/24
 * @Description:
 */
@Slf4j
@Component
public class PrintMessageReceiver {
    @Autowired
    private RedisTemplate redisTemplate;

    public void receiveMessage(MessageDTO messageDto , String channel) {

        // 接收的topic
        log.info("channel:" + channel);

        log.info("message:" + messageDto.getTitle());
    }
}
