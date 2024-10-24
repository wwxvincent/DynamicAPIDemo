package com.vincent.dynamicapidemo.util;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.io.IOException;

public class RequestMappingInfoDeserializer extends JsonDeserializer<RequestMappingInfo> {
    @Override
    public RequestMappingInfo deserialize(com.fasterxml.jackson.core.JsonParser p, com.fasterxml.jackson.databind.DeserializationContext ctxt) throws IOException {
        // 自定义反序列化逻辑
        // 根据 JSON 数据构造 RequestMappingInfo 实例
        return null; // 返回构造的实例
    }
}
