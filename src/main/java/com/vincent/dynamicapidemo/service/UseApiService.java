package com.vincent.dynamicapidemo.service;

import com.vincent.dynamicapidemo.entity.DTO.SearchDTO;
import com.vincent.dynamicapidemo.entity.VO.ResponseVO;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/14/24
 * @Description:
 */
public interface UseApiService {

    ResponseVO getDataFromDiffDBSource(SearchDTO searchDTO, String url);

}
