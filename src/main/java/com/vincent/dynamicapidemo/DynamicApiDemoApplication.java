package com.vincent.dynamicapidemo;

import com.vincent.dynamicapidemo.controller.AdapterController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootApplication
public class DynamicApiDemoApplication {

    public static void main(String[] args) throws NoSuchMethodException{

        // 启动Spring Boot应用并获取 ApplicationContext
        ApplicationContext run = SpringApplication.run(DynamicApiDemoApplication.class, args);

        // 获取 RequestMappingHandlerMapping bean，用于动态注册 URL 映射
        RequestMappingHandlerMapping handlerMapping = run.getBean(RequestMappingHandlerMapping.class);

        // 获取 AdapterController bean，作为动态注册的目标控制器
        AdapterController adapterController = run.getBean(AdapterController.class);


        //这是一套
//        // 创建动态注册的信息，包括路径和 HTTP 方法
//        RequestMappingInfo requestMappingInfo = RequestMappingInfo
//                .paths("/dynamic-api")
//                .methods(RequestMethod.GET)
//                .build();
//        // 使用 handlerMapping 将新的 URL 映射到 AdapterController 的 myTest 方法
//        handlerMapping.registerMapping(requestMappingInfo, adapterController,
//                AdapterController.class.getDeclaredMethod("myTest"));




//        ApplicationContext run = SpringApplication.run(DynamicApiDemoApplication.class, args);
//
//
//        RequestMappingHandlerMapping bean = run.getBean(RequestMappingHandlerMapping.class);
//        AdapterController bean1 = run.getBean(AdapterController.class);
//        RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths("/test").methods(RequestMethod.GET).build();
//        bean.registerMapping(requestMappingInfo, bean1, AdapterController.class.getDeclaredMethod("myTest"));
    }

}
