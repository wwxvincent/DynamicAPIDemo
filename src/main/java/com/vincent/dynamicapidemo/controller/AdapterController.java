package com.vincent.dynamicapidemo.controller;

import com.alibaba.csp.sentinel.Entry;

import com.alibaba.csp.sentinel.SphU;
import com.vincent.dynamicapidemo.entity.DTO.SearchDTO;
import com.vincent.dynamicapidemo.entity.VO.ResponseVO;
import com.vincent.dynamicapidemo.entity.DTO.ApiConfig;
import com.vincent.dynamicapidemo.entity.api.DynamicAPIMainConfig;
import com.vincent.dynamicapidemo.service.CreateApiService;
import com.vincent.dynamicapidemo.service.DynamicAPIMainConfigService;
import com.vincent.dynamicapidemo.service.JDBCService;
import com.vincent.dynamicapidemo.util.DynamicApiUtil;
import com.vincent.dynamicapidemo.util.SentinelConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;


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
    private JDBCService jdbcService;

    @Autowired
    private CreateApiService createApiService;

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
    public String create(@RequestBody ApiConfig apiConfig, HttpServletRequest request)  {
        // 获取完整的url
        String url =request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + apiConfig.getPath();
        if (dynamicAPIMainConfigService.checkExisted(url)) {
            return "Sorry bro, this url already existed! Change one!";
        }

        // 存入到db，then 执行路由绑定和sentinel其实设置
        String apiConfigId =  createApiService.saveConfig(apiConfig,"adapterController", url);

        return "success bro, tyr this: " + url + "\n" +"main config ID: " + apiConfigId;

    }

    //targetMethod for create api by table
    public ResponseVO dynamicApiMethodTable(@RequestBody SearchDTO searchDTO ,HttpServletRequest request) {
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + request.getServletPath();
        ResponseVO responseVO = new ResponseVO();
        Entry entry = null;

        try {
            entry = SphU.entry(request.getContextPath() + request.getServletPath());
            System.out.println("11   业务逻辑被保护");
            return jdbcService.getDataFromDiffDBSource(searchDTO, url);
        } catch (Exception e) {
            responseVO.setMsg(String.valueOf(e));
            return responseVO;
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
    }

    //targetMethod for create api by table
    public ResponseVO dynamicApiMethodSql(@RequestBody SearchDTO searchDTO ,HttpServletRequest request) {
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + request.getServletPath();
        ResponseVO responseVO = new ResponseVO();
        /**
         * to do
         */

        return null;
    }


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

//    @GetMapping("/create2")
//    public String create2() throws NoSuchMethodException {
//        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
//        // 无参get方法
//        RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths("/test2")
//                .params("fileName")
//                .methods(RequestMethod.GET).build();
//        bean.registerMapping(requestMappingInfo, "adapterController", AdapterController.class.getDeclaredMethod("myTest2", String.class));
//        String url= requestMappingInfo.getDirectPaths().toString();
//        return "success to create and reload createRestApi() "+ url;
//    }
//    Object myTest2(@RequestParam("fileName") String value) {
//        return "this is my param : " + value;
//    }

}
