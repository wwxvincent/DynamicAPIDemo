package com.vincent.dynamicapidemo.mapper;

import com.vincent.dynamicapidemo.entity.AllStockInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 10/31/24
 * @Description:
 */
@Mapper
public interface DorisAllStockMapper {

    @Select("SELECT * from all_stock_temp limit 10")
    public List<AllStockInfo> getStockTemp();
}
