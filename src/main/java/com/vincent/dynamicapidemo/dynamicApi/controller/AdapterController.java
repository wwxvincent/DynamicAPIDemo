package com.vincent.dynamicapidemo.dynamicApi.controller;

import com.alibaba.csp.sentinel.Entry;

import com.alibaba.csp.sentinel.SphU;
import com.vincent.dynamicapidemo.dynamicApi.entity.DTO.SearchDTO;
import com.vincent.dynamicapidemo.dynamicApi.entity.VO.ResponseVO;
import com.vincent.dynamicapidemo.dynamicApi.entity.DTO.ApiConfig;
import com.vincent.dynamicapidemo.dynamicApi.entity.api.DynamicAPIMainConfig;
import com.vincent.dynamicapidemo.dynamicApi.service.CreateApiService;
import com.vincent.dynamicapidemo.dynamicApi.service.DynamicAPIMainConfigService;
import com.vincent.dynamicapidemo.dynamicApi.service.UseApiService;
import com.vincent.dynamicapidemo.util.DynamicApiUtil;
import com.vincent.dynamicapidemo.util.SentinelConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;


/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/22/24
 * @Description:
 */
@Slf4j
@RestController
public class AdapterController {

    @Autowired
    private Environment env;

    private final WebApplicationContext applicationContext;

    @Autowired
    private DynamicAPIMainConfigService dynamicAPIMainConfigService;

    @Autowired
    private CreateApiService createApiService;

    @Autowired
    private UseApiService useService;

    @Autowired
    public AdapterController(WebApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        loadExistingMappings();
    }
    private void loadExistingMappings() {
        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);

        List<DynamicAPIMainConfig> existingMappings = dynamicAPIMainConfigService.getExistingMappingInfo();
        if (!existingMappings.isEmpty()) {
            for (DynamicAPIMainConfig dynamicAPIMainConfig : existingMappings) { // 从DB中获取配置信息，重新绑定API。
                // 注册动态路由，绑定url和目标方法
                DynamicApiUtil.create(bean, dynamicAPIMainConfig.getPath(), dynamicAPIMainConfig.getMethod(), dynamicAPIMainConfig.getHandler(), dynamicAPIMainConfig.getTargetMethodName());
                // 注册sentinel信息
                // 获取path组装资源名字，重新配置sentinel中的限流降级默认配置
                String sourceName = env.getProperty("server.servlet.context-path") + dynamicAPIMainConfig.getPath();
                SentinelConfigUtil.initFlowRules(sourceName);

                log.info("<===== load dynamic API: " + dynamicAPIMainConfig.getApiName()+" : " +dynamicAPIMainConfig.getId());
            }

            log.info("Successfully loaded all existing register mappings from database.");
        } else {
            log.info("No register mappings found in the database.");
        }

    }

    @PostMapping("/api/create")
    public ResponseVO create(@RequestBody ApiConfig apiConfig, HttpServletRequest request)  {
        // 获取完整的url
        String url =request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + apiConfig.getPath();
        if (dynamicAPIMainConfigService.checkExisted(url)) {
            return ResponseVO.fail("Sorry bro, this url already existed! Change one!");
        }

        // 存入到db，then 执行路由绑定和sentinel其实设置
        String apiConfigId =  createApiService.saveConfig(apiConfig,"adapterController", url);

//        return "success bro, tyr this: " + url + "\n" +"Check it in Redis <==> main config ID: " + apiConfigId;
        ResponseVO responseVO = new ResponseVO();
        responseVO.setSuccess(true);
        responseVO.setData(url);
        responseVO.setMsg("Check it in Redis <==> main config ID: " + apiConfigId);
        return responseVO;


    }

    @Async
    public CompletableFuture<ResponseVO> dynamicApiMethod(@RequestBody SearchDTO searchDTO , HttpServletRequest request) {
        // 记录开始时间
        long startTime = System.currentTimeMillis();

        try {
            Thread.sleep(searchDTO.getSleepTime()); // 参数单位为毫秒
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + request.getServletPath();
        ResponseVO responseVO = new ResponseVO();
        /**
         * to do
         */
        Entry entry = null;
        try {
            entry = SphU.entry(request.getContextPath() + request.getServletPath());
            System.out.println("11   业务逻辑被保护");
            return CompletableFuture.completedFuture(useService.getDataFromDiffDBSource(searchDTO, url));
        } catch (Exception e) {
            responseVO.setMsg(String.valueOf(e));
            return CompletableFuture.completedFuture(responseVO);
        } finally {
            if (entry != null) {
                entry.exit();
            }

            // 计算并记录执行时间
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            System.out.println("执行时间: " + executionTime + " 毫秒");
            log.info("执行时间: {} 毫秒", executionTime);
        }
    }
//    public ResponseVO dynamicApiMethod(@RequestBody SearchDTO searchDTO ,HttpServletRequest request) {
//        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + request.getServletPath();
//        ResponseVO responseVO = new ResponseVO();
//        /**
//         * to do
//         */
//        Entry entry = null;
//        try {
//            entry = SphU.entry(request.getContextPath() + request.getServletPath());
//            System.out.println("11   业务逻辑被保护");
//            return useService.getDataFromDiffDBSource(searchDTO, url);
//        } catch (Exception e) {
//            responseVO.setMsg(String.valueOf(e));
//            return responseVO;
//        } finally {
//            if (entry != null) {
//                entry.exit();
//            }
//        }
//    }

        /**
     * 模拟 往redis里发送topic
     * 1. 模拟本机发送topic，用ip addr 192.168.10.76
     * 1. 模拟集群其他服务发送topic，用ip addr 192.168.0.30
     * @param ipAddr
     * @param configId
     * @return
     */
    @GetMapping("/redis/test")
    public String testRedis(@RequestParam String ipAddr, @RequestParam String configId) {
        // 发布路由同步消息到Redis 频道
        try {
            redisTemplate.convertAndSend("api_sync_channel", ipAddr+":"+configId);
        } catch (Exception e) {
            return e.getMessage();
        }


        return "Simulating of publisher creation of An API, and then send info to redis\nsuccess. Go have a try, bro!";

    }

    //模拟call存储过程
    @GetMapping("/callFetch")
    public String callFetch() {
        String url = "jdbc:mysql://localhost:3306/gptDB";
        String user = "root";
        String password = "vincent2017727";

        List<Map<String, Object>> results = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            CallableStatement callableStatement = conn.prepareCall("{call FetchUsers()}");

            boolean hasResults = callableStatement.execute();

            while (hasResults) {
                rs = callableStatement.getResultSet();
                while (rs.next()) {
                    String userInfo = rs.getString(1);
                    System.out.println(userInfo);
                }
                // 移动到下一个结果集（如果有）
                hasResults = callableStatement.getMoreResults();
            }

//            while (rs.next()) {
//                for (int i = 1; i <= columnCount; i++) {
//                    String columnName = metaData.getColumnName(i);
//                    Object value = rs.getObject(columnName);
//                    System.out.println(columnName + ": " + value);
//                }
//            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
//        try (Connection conn = DriverManager.getConnection(url, user, password)) {
//            // 调用存储过程
//            String callProcedure = "{call FetchUsers()}";
//            try (CallableStatement callableStatement = conn.prepareCall(callProcedure);
//                 ResultSet resultSet = callableStatement.executeQuery()) {
//
//                ResultSetMetaData metaData = resultSet.getMetaData();
//                int columnCount = metaData.getColumnCount();
//
//                while (resultSet.next()) {
//                    Map<String, Object> rowData = new HashMap<>();
//                    for (int i = 1; i <= columnCount; i++) {
//                        String columnName = metaData.getColumnName(i);
//                        Object columnValue = resultSet.getObject(i);
//                        rowData.put(columnName, columnValue);
//                    }
//                    results.add(rowData);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return results.toString();
    }



}
