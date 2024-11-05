package com.vincent.dynamicapidemo.controller;

//import com.fasterxml.jackson.databind.util.JSONPObject;
import com.vincent.dynamicapidemo.entity.DTO.SearchDTO;
import com.vincent.dynamicapidemo.entity.VO.ResponseVO;
import com.vincent.dynamicapidemo.entity.DTO.CreateApiDTO;
//import com.vincent.dynamicapidemo.entity.DynamicAPIMappingInfo;
import com.vincent.dynamicapidemo.entity.DTO.ApiConfig;
import com.vincent.dynamicapidemo.service.CreateApiService;
import com.vincent.dynamicapidemo.service.JDBCService;
//import com.vincent.dynamicapidemo.service.RegisterMappingInfoService;
import com.vincent.dynamicapidemo.service.UserService;
//import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/22/24
 * @Description:
 */
@RestController
public class AdapterController {

    private final WebApplicationContext applicationContext;

//    private final List<DynamicAPIMappingInfo> dynamicAPIMappingInfoList = new ArrayList<>();

    @Autowired
    private UserService userService;

//    @Autowired
//    private RegisterMappingInfoService registerMappingInfoService;

    @Autowired
    private JDBCService jdbcService;

    @Autowired
    private CreateApiService createApiService;

    @Autowired
    public AdapterController(WebApplicationContext applicationContext) throws NoSuchMethodException {
        this.applicationContext = applicationContext;

    }

    @PostConstruct
    public void init() throws NoSuchMethodException {
//        loadExistingMappings();
    }
//    private void loadExistingMappings() throws NoSuchMethodException {
//        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
//
//        List<DynamicAPIMappingInfo> existingMappings = registerMappingInfoService.getExistingMappingInfo();
//        if (!existingMappings.isEmpty()) {
//            dynamicAPIMappingInfoList.addAll(existingMappings);
//            // 遍历所有的映射关系并进行处理
//            for (DynamicAPIMappingInfo dynamicAPIMappingInfo : dynamicAPIMappingInfoList) {
//
//                RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(dynamicAPIMappingInfo.getPath())
//                        .methods(dynamicAPIMappingInfo.getMethods())
//                        .build();
//                bean.registerMapping(requestMappingInfo, "adapterController", AdapterController.class.getDeclaredMethod("dynamicApiMethodSQL", SearchDTO.class));
//
//
//            }
//            System.out.println("Successfully loaded register mappings from database.");
//        } else {
//            System.out.println("No register mappings found in the database.");
//        }
//
//    }



    @PostMapping("/api/createJDBC2")
    public String create(@RequestBody ApiConfig apiConfig, HttpServletRequest request)  {
        try {
            RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
            RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(apiConfig.getPath())
                    .methods(RequestMethod.valueOf(apiConfig.getMethod()))
                    .build();
            bean.registerMapping(requestMappingInfo, "adapterController", AdapterController.class.getDeclaredMethod("dynamicApiMethodSQL", SearchDTO.class, HttpServletRequest.class));

            String url =request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + apiConfig.getPath();

            //存入到db
            createApiService.saveConfig(apiConfig,"adapterController", "dynamicApiMethodSQL",url);

            return "success bro, tyr this: " + url;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    public ResponseVO dynamicApiMethodSQL(@RequestBody SearchDTO searchDTO ,HttpServletRequest request) {
        String url =request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + request.getServletPath();


        return jdbcService.getDataFromDiffDBSource(searchDTO, url);
    }

////    @PostMapping("/api/createJDBC1")
////    public String createJDBC1(@RequestBody CreateApiDTO createApiDTO, HttpServletRequest request)  {
////        try {
////            RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
////            RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(createApiDTO.getPath())
////                .methods(RequestMethod.valueOf(createApiDTO.getMethod()))
////                .build();
////            bean.registerMapping(requestMappingInfo, "adapterController", AdapterController.class.getDeclaredMethod("dynamicApiMethodSQL", SearchDTO.class));
////
////            String url =request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + createApiDTO.getPath();
////
////            //存入到db
////            createApiService.create( "1", createApiDTO.getSelectList().toString(), createApiDTO.getFixedWhereList().toString(), createApiDTO.getOptionalWhereList().toString(),
////                    createApiDTO.getPath(), "dynamicApiMethodSQL",createApiDTO.getMethod(), "adapterController",  url);
////        return "yes";
////    } catch (NoSuchMethodException e) {
////        e.printStackTrace();
////        return "Error: " + e.getMessage();
////    }
//    }
//
////    @PostMapping("/api/createJDBC")
////    public String createJDBC(@RequestBody CreateApiDTO createApiDTO, HttpServletRequest request) throws NoSuchMethodException {
////
////        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
////        // 无参get方法
////        RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths("/test")
////                .methods(RequestMethod.GET).build();
////        bean.registerMapping(requestMappingInfo, "adapterController", AdapterController.class.getDeclaredMethod("dynamicApiMethodSQL", SearchDTO.class));
////        String url =request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + createApiDTO.getPath();
////        return "success to create and reload createRestApi() "+ url;
////
////    }
//    private boolean bindingApi(String path,String handler,String method, String targetMethodName,String selectList, String fixedWhereList,
//                               String optionalWhereList,String url, boolean load) throws NoSuchMethodException {
//
//            //        // 创建动态注册的信息，包括路径和 HTTP 方法
////        RequestMappingInfo requestMappingInfo = RequestMappingInfo
////                .paths("/api/users/index")
////                .methods(RequestMethod.GET)
////                .build();
////        // 使用 handlerMapping 将新的 URL 映射到 AdapterController 的 myTest 方法
////        bean.registerMapping(requestMappingInfo, "adapterController",
////                AdapterController.class.getDeclaredMethod("getAllUsers"));
////
//            RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
//            RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(path)
//                    .methods(RequestMethod.valueOf(method))
//                    .build();
//            bean.registerMapping(requestMappingInfo, handler, AdapterController.class.getDeclaredMethod(targetMethodName, SearchDTO.class));
//            //update to database
//            if (!load) createApiService.create( "1", selectList, fixedWhereList, optionalWhereList, path,
//                     targetMethodName,method, handler,  url);
//
//
//        return true;
//    }







//    @PostMapping("/api/create")
//    public String create(@RequestBody CreateApiDTO createApiDTO) throws NoSuchMethodException {
//        String type = createApiDTO.getSourceType();
//        String path = createApiDTO.getPath();
//        String method = createApiDTO.getMethod();
////        String[][] params = createApiDTO.getParams();
//        String sqlStr = createApiDTO.getSql();
//        System.out.println(createApiDTO.toString());
//
//        String targetMethodName;
//        if ("sql".equals(type)) {
//            targetMethodName = "dynamicApiMethodSQL";
//        } else if ("table".equals(type)) {
//            targetMethodName = "dynamicApiMethodTable";
//        } else if ("jar".equals(type)) {
//            targetMethodName = "dynamicApiMethodJar";
//        } else {
//            targetMethodName = "";
//            return "Illegal source type";
//        }
//        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
//        // 无参get方法
//
//        if (createApiDTO.getParams() != null) {
//            String[][] paramsCollection = createApiDTO.getParams();
//            int size = paramsCollection[0].length;
//            // 动态创建参数类型数组
//            Class<?>[] paramTypes = new Class<?>[size];
//            // 根据 params 数组中每个参数的类型来设置 paramTypes
//            // 您可以根据具体的需求来调整
//            String[] params = new String[size];
//            for(int i = 0; i < size; i++) {
//                params[i] = paramsCollection[i][0];
//            }
////            Arrays.fill(params, "params");
//            RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(path)
//                    .methods(RequestMethod.valueOf(method))
//                    .params(params)
//                    .build();
//
//            bean.registerMapping(requestMappingInfo, "adapterController", AdapterController.class.getDeclaredMethod(targetMethodName, Object[].class));
//        } else {
//            // method without parameters
//            RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(path)
//                    .methods(RequestMethod.valueOf(method))
//                    .build();
//
//            bean.registerMapping(requestMappingInfo, "adapterController", AdapterController.class.getDeclaredMethod(targetMethodName));
//
//        }
//
//        return "success";
//    }
//    Object dynamicApiMethodSQL(@RequestParam Object... params){
//        System.out.println(Arrays.toString(params));
//        return null;
//    }
//
//    @GetMapping("/create1")
//    public String create1(HttpServletRequest request) throws NoSuchMethodException {
//        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
//        // 无参get方法
//        RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths("/test1").methods(RequestMethod.GET).build();
//        bean.registerMapping(requestMappingInfo, "adapterController", AdapterController.class.getDeclaredMethod("myTest"));
//        // 创建一个RegisterMapping对象，然后存入到List里去
//        // create a registermappinginfo obj to store this mapping relationship
//        RegisterMappingInfo registerMappingInfo = new RegisterMappingInfo();
//        registerMappingInfo.setPaths("/test1");
//        registerMappingInfo.setMethods(RequestMethod.GET);
//        registerMappingInfo.setHandler("adapterController");
//        registerMappingInfo.setTargetMethodName("myTest");
//        registerMappingInfo.setSql("select * from user");
//        registerMappingInfo.setUrl( request.getRequestURL().toString());
//        // update it to database
//        registerMappingInfoService.saveMappingInfo(registerMappingInfo);
//        return "success to create and reload createRestApi()  " + request.getRequestURL().toString() ;
//    }
    @GetMapping("/create2")
    public String create2() throws NoSuchMethodException {
        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
        // 无参get方法
        RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths("/test2")
                .params("fileName")
                .methods(RequestMethod.GET).build();
        bean.registerMapping(requestMappingInfo, "adapterController", AdapterController.class.getDeclaredMethod("myTest2", String.class));
        String url= requestMappingInfo.getDirectPaths().toString();
        return "success to create and reload createRestApi() "+ url;
    }
    Object myTest2(@RequestParam("fileName") String value) {
        return "this is my param : " + value;
    }
//    @GetMapping("/create3")
//    public String create3() throws NoSuchMethodException {
//        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
//                // 创建动态注册的信息，包括路径和 HTTP 方法
//        RequestMappingInfo requestMappingInfo = RequestMappingInfo
//                .paths("/test3")
//                .methods(RequestMethod.GET)
//                .build();
//        // 使用 handlerMapping 将新的 URL 映射到 AdapterController 的 myTest 方法
//        bean.registerMapping(requestMappingInfo, "adapterController",
//                AdapterController.class.getDeclaredMethod("myTest"));
//
//        return "success to create and reload createRestApi()";
//    }
//
//    @GetMapping("/create4")
//    public String create4() throws NoSuchMethodException {
//        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
//        // 创建动态注册的信息，包括路径和 HTTP 方法
//        RequestMappingInfo requestMappingInfo = RequestMappingInfo
//                .paths("/test4")
//                .params(new String[]{"fileName", "type", "isSort"})
//                .methods(RequestMethod.GET)
//                .build();
//        // 使用 handlerMapping 将新的 URL 映射到 AdapterController 的 myTest 方法
//        bean.registerMapping(requestMappingInfo, "adapterController",
//                AdapterController.class.getDeclaredMethod("myTest3", String.class, String.class, String.class));
//
//        // create a registermappinginfo obj to store this mapping relationship
//        // add it to mapping list for init load when app restart
//        RegisterMappingInfo registerMappingInfo = new RegisterMappingInfo();
//        registerMappingInfo.setPaths("/test4");
//        registerMappingInfo.setParams("fileName,type,isSort");
//        registerMappingInfo.setMethods(RequestMethod.GET);
//        registerMappingInfo.setHandler("adapterController");
//        registerMappingInfo.setTargetMethodName("myTest3");
////        registerMappingInfoList.add(registerMappingInfo);
//        registerMappingInfoService.saveMappingInfo(registerMappingInfo);
//
//        return  "http://localhost:8092/blog/test4?fileName=hhh&isSort=YYY&type=KKK";
//    }
//    @GetMapping("/create5")
//    public String create5() throws NoSuchMethodException {
//        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
//        // 创建动态注册的信息，包括路径和 HTTP 方法
//        RequestMappingInfo requestMappingInfo = RequestMappingInfo
//                .paths("/api/users/index")
//                .methods(RequestMethod.GET)
//                .build();
//        // 使用 handlerMapping 将新的 URL 映射到 AdapterController 的 myTest 方法
//        bean.registerMapping(requestMappingInfo, "adapterController",
//                AdapterController.class.getDeclaredMethod("getAllUsers"));
//
//        return  "http://localhost:8092/blog/api/users/index";
//    }
//    public List<User> getAllUsers() {
//        return userService.getAllUsers();
//    }
//    Object myTest3(@RequestParam("fileName") String fileName,
//                   @RequestParam("type") String type,
//                   @RequestParam("isSort") String isSort) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("fileName", fileName);
//        jsonObject.put("type", type);
//        jsonObject.put("isSort", isSort);
//        return "this is test request from test3- values: " +jsonObject.toString();
//    }
//
//    Object myTest() {
//        return "this is test request from myTest1";
//    }
//

//
//    @GetMapping("/index")
//    public String index(@RequestBody CreateApiDTO createApiDTO, HttpServletRequest request) {
//        // 获取完整的请求 URL
//        String fullUrl = request.getRequestURL().toString();
//        System.out.println("Full URL: " + fullUrl);
//
//        return "常规API测试" ;
//    }
//    @GetMapping("/ttt")
//    Object objTest(@RequestParam Object[] params){
//        System.out.println(Arrays.toString(params));
//        return Arrays.toString(params);
//    }
//    @GetMapping("/tttt")
//    Object objTest2(@RequestParam Object... params){
//        System.out.println(Arrays.toString(params));
//        return Arrays.toString(params);
//    }
//
//    @GetMapping("/index2")
//    public List<RegisterMappingInfo> index2() {
//        return registerMappingInfoService.getExistingMappingInfo();
//    }

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
