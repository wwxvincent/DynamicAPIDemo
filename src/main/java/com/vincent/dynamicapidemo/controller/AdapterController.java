package com.vincent.dynamicapidemo.controller;

import com.alibaba.csp.sentinel.Entry;

import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.vincent.dynamicapidemo.entity.DTO.SearchDTO;
import com.vincent.dynamicapidemo.entity.VO.ResponseVO;
import com.vincent.dynamicapidemo.entity.DTO.ApiConfig;
import com.vincent.dynamicapidemo.entity.api.DynamicAPIMainConfig;
import com.vincent.dynamicapidemo.service.CreateApiService;
import com.vincent.dynamicapidemo.service.DynamicAPIMainConfigService;
import com.vincent.dynamicapidemo.service.JDBCService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
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

    @PostConstruct
    public void init() throws NoSuchMethodException {
        loadExistingMappings();
    }
    private void loadExistingMappings() throws NoSuchMethodException {
        RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);

        List<DynamicAPIMainConfig> existingMappings = dynamicAPIMainConfigService.getExistingMappingInfo();
        if (!existingMappings.isEmpty()) {
            for (DynamicAPIMainConfig dynamicAPIMainConfig : existingMappings) {
                // 从DB中获取配置信息，重新绑定API。
                RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(dynamicAPIMainConfig.getPath())
                        .methods(RequestMethod.valueOf(dynamicAPIMainConfig.getMethod()))
                        .build();
                bean.registerMapping(requestMappingInfo, dynamicAPIMainConfig.getHandler(), AdapterController.class.getDeclaredMethod(dynamicAPIMainConfig.getTargetMethodName(), SearchDTO.class, HttpServletRequest.class));
                // 获取path组装资源名字，重新配置sentinel中的限流降级默认配置
                String contextPath = env.getProperty("server.servlet.context-path");
                initFlowRules(contextPath +  dynamicAPIMainConfig.getPath());

                log.info("<===== load dynamic API: " + dynamicAPIMainConfig.toString());
            }

            log.info("Successfully loaded all existing register mappings from database.");
        } else {
            log.info("No register mappings found in the database.");
        }

    }

    // 配置sentinel中的限流降级默认配置
    private static void initFlowRules(String resourceName) {
        List<FlowRule> rules = FlowRuleManager.getRules();
        FlowRule rule = new FlowRule();
        rule.setResource(resourceName);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 设置每秒的通行数为1
        rule.setCount(1);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }

    @PostMapping("/api/create")
    public String create(@RequestBody ApiConfig apiConfig, HttpServletRequest request)  {
        String url =request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + apiConfig.getPath();
        if (dynamicAPIMainConfigService.checkExisted(url)) {
            return "Sorry bro, this url already existed! Change one!";
        }
        try {
            RequestMappingHandlerMapping bean = applicationContext.getBean(RequestMappingHandlerMapping.class);
            RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(apiConfig.getPath())
                    .methods(RequestMethod.valueOf(apiConfig.getMethod()))
                    .build();
            bean.registerMapping(requestMappingInfo, "adapterController", AdapterController.class.getDeclaredMethod("dynamicApiMethodSQL", SearchDTO.class, HttpServletRequest.class));

            initFlowRules(request.getContextPath() +  apiConfig.getPath());

            //存入到db
            createApiService.saveConfig(apiConfig,"adapterController", "dynamicApiMethodSQL",url);

            return "success bro, tyr this: " + url;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    //targetMethod for mysql now
    public ResponseVO dynamicApiMethodSQL(@RequestBody SearchDTO searchDTO ,HttpServletRequest request) {
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
