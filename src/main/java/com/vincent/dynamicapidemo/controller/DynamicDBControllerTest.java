package com.vincent.dynamicapidemo.controller;

import com.vincent.dynamicapidemo.entity.AllStockInfo;
import com.vincent.dynamicapidemo.entity.User;
import com.vincent.dynamicapidemo.service.DBChangeService;
import com.vincent.dynamicapidemo.service.DorisAllStockService;
import com.vincent.dynamicapidemo.service.UserService;
import com.vincent.dynamicapidemo.util.DBContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/31/24
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/db")
public class DynamicDBControllerTest {

    @Autowired
    private DBChangeService dbChangeService;
    @Autowired
    UserService userService;
    @Autowired
    DorisAllStockService dorisAllStockService;

    @GetMapping("/test")
    public String test() throws Exception {

        String datasourceId = "1";
        dbChangeService.changeDB(datasourceId);
        System.out.println(DBContextHolder.getDataSource());
//        List<User> userList = userService.getAllUsers();
//        System.out.println(userList.toString());

        StringBuilder sb = new StringBuilder();
//        sb.append("Data source: local mysql： ").append("\n");
//        sb.append(userList.toString()).append("\n");

        // switch to doris
//        String datasourceId2 = "2";
//        dbChangeService.changeDB(datasourceId2);
//        List<AllStockInfo> list = dorisAllStockService.getStockTemp();
//        System.out.println(list.toString());
//        sb.append("Data source: 中南windows虚拟机 Doris： ").append("\n");
//        sb.append(list.toString());
        return "success： " +"\n"+ sb.toString();
    }
}
