package com.vincent.dynamicapidemo.controller;

//import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent.dynamicapidemo.entity.RegisterMapping;
import com.vincent.dynamicapidemo.entity.RegisterMappingInfo;
import com.vincent.dynamicapidemo.entity.User;
import com.vincent.dynamicapidemo.service.RegisterMappingInfoService;
import com.vincent.dynamicapidemo.service.UserService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
//import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/22/24
 * @Description:
 */
@RestController
public class AdapterController {

    private final WebApplicationContext applicationContext;

    private final List<RegisterMappingInfo> registerMappingInfoList = new ArrayList<>();

    @Autowired
    private UserService userService;

    @Autowired
    private RegisterMappingInfoService registerMappingInfoService;

    @Autowired
    public AdapterController(WebApplicationContext applicationContext) throws NoSuchMethodException {
        this.applicationContext = applicationContext;

    }

    @PostConstruct
    public void init() throws NoSuchMethodException {
        loadExistingMappings();
    }
    private void loadExistingMappings() throws NoSuchMethodException {
        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);

        List<RegisterMappingInfo> existingMappings = registerMappingInfoService.getExistingMappingInfo();
        if (existingMappings != null) {
            registerMappingInfoList.addAll(existingMappings);
            System.out.println("Successfully loaded register mappings from database.");
        } else {
            System.err.println("No register mappings found in the database.");
        }

        // 遍历所有的映射关系并进行处理
        for (RegisterMappingInfo registerMappingInfo : registerMappingInfoList) {

            // method with parameters
            if (registerMappingInfo.getParams() != null) {
                String paramString = registerMappingInfo.getParams();
                String[] params = paramString.split(",");
                int size = params.length;
                // 动态创建参数类型数组
                Class<?>[] paramTypes = new Class<?>[size];
                // 根据 params 数组中每个参数的类型来设置 paramTypes
                // 您可以根据具体的需求来调整
                Arrays.fill(paramTypes, String.class);
                RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(registerMappingInfo.getPaths())
                        .methods(registerMappingInfo.getMethods())
                        .params(params)
                        .build();

                bean.registerMapping(requestMappingInfo, registerMappingInfo.getHandler(), AdapterController.class.getDeclaredMethod(registerMappingInfo.getTargetMethodName(), paramTypes));
            } else {
                // method without parameters
                RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(registerMappingInfo.getPaths()).methods(registerMappingInfo.getMethods()).build();

                bean.registerMapping(requestMappingInfo, registerMappingInfo.getHandler(), AdapterController.class.getDeclaredMethod(registerMappingInfo.getTargetMethodName()));

            }
        }
    }

    @GetMapping("/index")
    public String index() {
        return "常规API测试";
    }

    @GetMapping("/index2")
    public List<RegisterMappingInfo> index2() {
        return registerMappingInfoService.getExistingMappingInfo();
    }

    @GetMapping("/create1")
    public String create1() throws NoSuchMethodException {
        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
        // 无参get方法
        RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths("/test1").methods(RequestMethod.GET).build();
        bean.registerMapping(requestMappingInfo, "adapterController", AdapterController.class.getDeclaredMethod("myTest"));
        // 创建一个RegisterMapping对象，然后存入到List里去
        // create a registermappinginfo obj to store this mapping relationship
        RegisterMappingInfo registerMappingInfo = new RegisterMappingInfo();
        registerMappingInfo.setPaths("/test1");
        registerMappingInfo.setMethods(RequestMethod.GET);
        registerMappingInfo.setHandler("adapterController");
        registerMappingInfo.setTargetMethodName("myTest");
        // update it to database
        registerMappingInfoService.saveMappingInfo(registerMappingInfo);

        return "success to create and reload createRestApi()";
    }
    @GetMapping("/create2")
    public String create2() throws NoSuchMethodException {
        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
        // 无参get方法
        RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths("/test2")
                .params("fileName")
                .methods(RequestMethod.GET).build();
        bean.registerMapping(requestMappingInfo, "adapterController", AdapterController.class.getDeclaredMethod("myTest2", String.class));

        return "success to create and reload createRestApi()";
    }
    @GetMapping("/create3")
    public String create3() throws NoSuchMethodException {
        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
                // 创建动态注册的信息，包括路径和 HTTP 方法
        RequestMappingInfo requestMappingInfo = RequestMappingInfo
                .paths("/test3")
                .methods(RequestMethod.GET)
                .build();
        // 使用 handlerMapping 将新的 URL 映射到 AdapterController 的 myTest 方法
        bean.registerMapping(requestMappingInfo, "adapterController",
                AdapterController.class.getDeclaredMethod("myTest"));

        return "success to create and reload createRestApi()";
    }

    @GetMapping("/create4")
    public String create4() throws NoSuchMethodException {
        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
        // 创建动态注册的信息，包括路径和 HTTP 方法
        RequestMappingInfo requestMappingInfo = RequestMappingInfo
                .paths("/test4")
                .params(new String[]{"fileName", "type", "isSort"})
                .methods(RequestMethod.GET)
                .build();
        // 使用 handlerMapping 将新的 URL 映射到 AdapterController 的 myTest 方法
        bean.registerMapping(requestMappingInfo, "adapterController",
                AdapterController.class.getDeclaredMethod("myTest3", String.class, String.class, String.class));

        // create a registermappinginfo obj to store this mapping relationship
        // add it to mapping list for init load when app restart
        RegisterMappingInfo registerMappingInfo = new RegisterMappingInfo();
        registerMappingInfo.setPaths("/test4");
        registerMappingInfo.setParams("fileName,type,isSort");
        registerMappingInfo.setMethods(RequestMethod.GET);
        registerMappingInfo.setHandler("adapterController");
        registerMappingInfo.setTargetMethodName("myTest3");
//        registerMappingInfoList.add(registerMappingInfo);
        registerMappingInfoService.saveMappingInfo(registerMappingInfo);

        return  "http://localhost:8092/blog/test4?fileName=hhh&isSort=YYY&type=KKK";
    }
    @GetMapping("/create5")
    public String create5() throws NoSuchMethodException {
        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
        // 创建动态注册的信息，包括路径和 HTTP 方法
        RequestMappingInfo requestMappingInfo = RequestMappingInfo
                .paths("/api/users/index")
                .methods(RequestMethod.GET)
                .build();
        // 使用 handlerMapping 将新的 URL 映射到 AdapterController 的 myTest 方法
        bean.registerMapping(requestMappingInfo, "adapterController",
                AdapterController.class.getDeclaredMethod("getAllUsers"));

        return  "http://localhost:8092/blog/api/users/index";
    }
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    Object myTest3(@RequestParam("fileName") String fileName,
                   @RequestParam("type") String type,
                   @RequestParam("isSort") String isSort) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fileName", fileName);
        jsonObject.put("type", type);
        jsonObject.put("isSort", isSort);
        return "this is test request from test3- values: " +jsonObject.toString();
    }

    Object myTest() {
        return "this is test request from myTest1";
    }

    Object myTest2(@RequestParam("fileName") String value) {
        return "this is my param : " + value;
    }

//    public static String serializeAndSave(RegisterMapping registerMapping) {
//        try {
//            // 将 RegisterMapping 对象转换为 JSON 字符串
//            String jsonString = objectMapper.writeValueAsString(registerMapping);
//            System.out.println("Serialized JSON: " + jsonString);
//
//            // 假设有一个数据库方法 saveToDatabase(String json) 可以保存数据
////            System.out.println("Serialized JSON String: " + jsonString);
//
//            return jsonString;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//    private static void deserialized(String jsonString) {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        try {
//            // 从数据库读取的 JSON 字符串
////            String jsonString = getFromDatabase();
//
//
//            // 反序列化 JSON 字符串为 RegisterMapping 对象
//            RegisterMapping registerMapping = objectMapper.readValue(jsonString, RegisterMapping.class);
//
//            System.out.println("Deserialized RegisterMapping:");
//            System.out.println("RequestMappingInfo: " + registerMapping.getRequestMappingInfo());
//            System.out.println("Handler: " + registerMapping.getHandler());
//            System.out.println("Method: " + registerMapping.getMethod());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
