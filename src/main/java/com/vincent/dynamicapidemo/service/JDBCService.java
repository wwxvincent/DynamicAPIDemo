package com.vincent.dynamicapidemo.service;

import com.vincent.dynamicapidemo.entity.DTO.SearchDTO;
import com.vincent.dynamicapidemo.entity.VO.ResponseVO;
import com.vincent.dynamicapidemo.entity.DTO.CreateApiDTO;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/1/24
 * @Description:
 */
public interface JDBCService {
    Object getDataFromDiffDBSource1(CreateApiDTO createApiDTO);

    ResponseVO getDataFromDiffDBSource(SearchDTO searchDTO);
}
