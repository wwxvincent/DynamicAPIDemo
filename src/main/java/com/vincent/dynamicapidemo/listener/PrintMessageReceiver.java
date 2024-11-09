package com.vincent.dynamicapidemo.listener;

import com.vincent.dynamicapidemo.entity.DTO.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/8/24
 * @Description:
 */
@Slf4j
@Component
public class PrintMessageReceiver {


    public void receiveMessage(MessageDTO messageDTO) {

        // 接收的topic

        log.info("message:" + messageDTO.getContent());
    }
}