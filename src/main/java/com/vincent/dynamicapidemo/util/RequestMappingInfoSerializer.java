package com.vincent.dynamicapidemo.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.io.IOException;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/23/24
 * @Description:
 */
public class RequestMappingInfoSerializer extends JsonSerializer<RequestMappingInfo> {
    @Override
    public void serialize(RequestMappingInfo value, JsonGenerator gen, com.fasterxml.jackson.databind.SerializerProvider serializers) throws IOException, IOException {
        // 自定义序列化逻辑
        gen.writeStartObject();
        gen.writeStringField("name", value.getName());
        gen.writeFieldName("patternsCondition");
        gen.writeObject(value.getPatternsCondition());
        gen.writeEndObject();
    }
}

