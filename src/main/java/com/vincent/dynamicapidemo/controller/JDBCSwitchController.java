package com.vincent.dynamicapidemo.controller;

import com.vincent.dynamicapidemo.common.ResponseDTO;
import com.vincent.dynamicapidemo.entity.DTO.CreateApiDTO;
import com.vincent.dynamicapidemo.service.JDBCService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/1/24
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/jdbc")
public class JDBCSwitchController {

    @Autowired
    private JDBCService jdbcService;

    @PostMapping("/index")
    public ResponseDTO getDataFromDiffDBSource(@RequestBody CreateApiDTO createApiDTO) {
//        System.out.println(Arrays.deepToString(createApiDTO.getJdbcParamValues()));
        return jdbcService.getDataFromDiffDBSource(createApiDTO);
    }
}
