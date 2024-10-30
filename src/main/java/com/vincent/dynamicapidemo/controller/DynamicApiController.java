package com.vincent.dynamicapidemo.controller;

import com.vincent.dynamicapidemo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/23/24
 * @Description: 这个方法局限性太强，暂不使用
 */
@Slf4j
@RestController
@RequestMapping("/mapTest")
public class DynamicApiController {

    private final Map<String, String> apiResponses = new HashMap<>();

    @PostMapping("/create-api")
    public String createApi(@RequestBody Map<String, String> requestBody) {
        String endpoint = requestBody.get("endpoint");
        String responseData = requestBody.get("responseData");

        // 添加动态路由
        apiResponses.put(endpoint, responseData);

        return "http://localhost:8092/blog/mapTest/api/" + endpoint;
    }
    @GetMapping("/test")
    public String test() {
        return "test success";
    }

    @GetMapping("/api/{endpoint}")
    public String getApiResponse(@PathVariable String endpoint) {
        return apiResponses.getOrDefault(endpoint, "Not Found");
    }

    public static void handleDifferentTypes(Object... args) {
        for (Object arg : args) {
            if (arg instanceof String) {
                System.out.println("String: " + arg);
            } else if (arg instanceof Integer) {
                System.out.println("Integer: " + arg);
            } else if (arg instanceof Double) {
                System.out.println("Double: " + arg);
            } else {
                System.out.println("Unknown type: " + arg);
            }
        }
    }

    public static void main(String[] args) {
        User user = new User();
        handleDifferentTypes("123", "A", 221, user);
    }
}
