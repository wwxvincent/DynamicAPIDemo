package com.vincent.dynamicapidemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/8/24
 * @Description:
 */
@SpringBootTest
public class RedisConnectionTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testRedisConnection() {
        try {
            // 测试 Redis 写入
            stringRedisTemplate.opsForValue().set("testKey", "testValue");
            // 测试 Redis 读取
            String value = stringRedisTemplate.opsForValue().get("testKey");

            assertEquals("testValue", value);
            System.out.println("Redis 连接成功，值为：" + value);
        } catch (Exception e) {
            fail("无法连接到 Redis，错误信息: " + e.getMessage());
        }
    }
}
