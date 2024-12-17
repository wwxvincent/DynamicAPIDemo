package com.vincent.dynamicapidemo.sqlCheck.controller;

import com.vincent.dynamicapidemo.dynamicApi.entity.VO.ResponseVO;
import com.vincent.dynamicapidemo.sqlCheck.entity.VO.SqlValidationRequestVO;
import com.vincent.dynamicapidemo.sqlCheck.service.SqlValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 12/16/24
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/sql")
public class sqlCheckController {

    private final SqlValidator sqlValidator;

    public sqlCheckController(SqlValidator sqlValidator) {
        this.sqlValidator = sqlValidator;
    }

    @PostMapping("/validate")
    public ResponseEntity<List<ResponseVO>> validateSql(@RequestBody SqlValidationRequestVO request) {
        List<ResponseVO> results = sqlValidator.validate(
                request.getSql(),
                request.getType(),
                request.getRuleNames(),
                request.getContext()
        );
        return ResponseEntity.ok(results);
    }

}
