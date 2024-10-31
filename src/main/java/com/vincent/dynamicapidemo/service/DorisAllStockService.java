package com.vincent.dynamicapidemo.service;

import com.vincent.dynamicapidemo.entity.AllStockInfo;

import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/31/24
 * @Description:
 */
public interface DorisAllStockService {

    List<AllStockInfo> getStockTemp();
}
