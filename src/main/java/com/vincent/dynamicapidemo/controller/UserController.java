package com.vincent.dynamicapidemo.controller;

import com.vincent.dynamicapidemo.entity.User;
import com.vincent.dynamicapidemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/23/24
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public String test() {
        return "test success";
    }


    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
