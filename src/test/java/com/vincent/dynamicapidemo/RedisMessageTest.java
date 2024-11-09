package com.vincent.dynamicapidemo;

import com.vincent.dynamicapidemo.entity.DTO.MessageDTO;
import com.vincent.dynamicapidemo.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/8/24
 * @Description:
 */
@Slf4j
@SpringBootTest
public class RedisMessageTest {

    @Autowired
    private RedisUtils redisUtils;

    @Test
    public void test() {
        final String TOPIC_NAME1 = "TEST_TOPIC1";
        final String TOPIC_NAME2 = "TEST_TOPIC2";

        // 发布消息
        MessageDTO dto = new MessageDTO();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        dto.setData(timeFormatter.format(now));
        dto.setTitle("日常消息");
        dto.setContent("hello world");

        redisUtils.convertAndSend(TOPIC_NAME1, dto);
    }
}
