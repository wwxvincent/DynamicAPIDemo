package com.vincent.dynamicapidemo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.vincent.dynamicapidemo.listener.APIMessageReceiver;
import com.vincent.dynamicapidemo.listener.PrintMessageReceiver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/7/24
 * @Description:
 */
@Configuration
public class RedisConfig {

    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        //由于源码autoConfig中是<Object, Object>，开发中一般直接使用<String,Object>
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //json序列化配置
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        // String的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        //key采用String的序列化
        redisTemplate.setKeySerializer(stringRedisSerializer);
        //hash的key采用String的序列化
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        //value序列化采用jackson
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        //hash的value序列化方式采用jackson
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();

//        // 配置 Jackson2JsonRedisSerializer
//        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder().build();
//        objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        serializer.setObjectMapper(objectMapper);
//
//        // 设置 StringRedisSerializer 用于 key
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(serializer);
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashValueSerializer(serializer);
//        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    /**
    * Redis消息监听器容器
    * 这个容器加载了RedisConnectionFactory和消息监听器
    * 可以添加多个监听不同话题的redis监听器，只需要把消息监听器和相应的消息订阅处理器绑定，该消息监听器
    * 通过反射技术调用消息订阅处理器的相关方法进行一些业务处理
    *
    * @param redisConnectionFactory 连接工厂
    * @param adapter                适配器
    * @return redis消息监听容器
    *
    */
    @Bean
    @SuppressWarnings("all")
    public RedisMessageListenerContainer container(RedisConnectionFactory redisConnectionFactory, APIMessageReceiver receiver, MessageListenerAdapter adapter) {

        final String TOPIC_NAME = "api_sync_channel";
        final String TOPIC_NAME2 = "test_channel";
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        // 监听所有库的key过期事件
        container.setConnectionFactory(redisConnectionFactory);
        // 所有的订阅消息，都需要在这里进行注册绑定，new PatternTopic(TOPIC_NAME)表示发布的主题信息
        // 可以添加多个 messageListener， 配置不同的通道
        container.addMessageListener(adapter, new PatternTopic(TOPIC_NAME2));

        // 监听 "api_sync_channel" 频道
        container.addMessageListener(receiver, new PatternTopic(TOPIC_NAME));

        /**
         * 设置序列化对象
         * 特别注意：1. 发布的时候需要设置序列化；订阅方也需要设置序列化
         *         2. 设置序列化对象必须放在[加入消息监听器]这一步后面，否则会导致接收器接收不到消息
         */
        Jackson2JsonRedisSerializer seria = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        seria.setObjectMapper(objectMapper);
        container.setTopicSerializer(seria);

        return container;
    }

    /**
     * 这个地方是给messageListenerAdapter 传入一个消息接受的处理器，利用反射的方法调用“receiveMessage”
     * 也有好几个重载方法，这边默认调用处理器的方法 叫OnMessage
     *
     * @param printMessageReceiver
     * @return
     */
    @Bean
    public MessageListenerAdapter listenerAdapter(PrintMessageReceiver printMessageReceiver) {
        MessageListenerAdapter receiveMessage = new MessageListenerAdapter(printMessageReceiver, "receiveMessage");

        Jackson2JsonRedisSerializer serial = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serial.setObjectMapper(objectMapper);
        receiveMessage.setSerializer(serial);
        return receiveMessage;
    }


}
