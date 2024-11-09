package com.vincent.dynamicapidemo.listener;

import com.vincent.dynamicapidemo.entity.DTO.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/8/24
 * @Description:
 */
@Component
@Slf4j
public class RedisMessageListener implements MessageListener {

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void onMessage(Message message, byte[] pattern) {

        // 接收的topic
        log.info("channel:" + new String(pattern));

        //序列化对象（特别注意：发布的时候需要设置序列化；订阅方也需要设置序列化）
        MessageDTO messageDto = (MessageDTO) redisTemplate.getValueSerializer().deserialize(message.getBody());
        log.info(messageDto.getData()+","+messageDto.getContent());
    }
}
