package com.vincent.dynamicapidemo.util;

import com.vincent.dynamicapidemo.dynamicApi.entity.DTO.Param;
import com.vincent.dynamicapidemo.dynamicApi.entity.api.DynamicAPIParamsConfig;

import java.util.*;

import static org.apache.commons.lang.StringUtils.isNumeric;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/15/24
 * @Description:
 */
public class SqlProcessUtil {

    /**
     * 解析 SQL 语句中的占位符 `#{paramName}`，替换为 `?` 并返回参数顺序映射。
     * @param sql 待解析的 SQL 语句
     * @return Map.Entry，其中 key 为解析后的 SQL 字符串，value 为参数映射 Map
     */
    public static Map.Entry<String, Map<Integer, String>> parseSqlPlaceholders(String sql) {
        StringBuilder sb = new StringBuilder();
        Map<Integer, String> paramMap = new HashMap<>();
        int sort = 0;
        for (int i = 0; i < sql.length(); i++) {
            char currentChar = sql.charAt(i);
            // 检查是否是占位符的开始 `#{`
            if (currentChar == '#' && i + 1 < sql.length() && sql.charAt(i + 1) == '{') {
                int start = i + 2;
                int end = start;
                // 查找 `}` 的位置
                while (end < sql.length() && sql.charAt(end) != '}') {
                    end++;
                }
                if (end < sql.length()) {
                    // 提取参数名
                    String paramName = sql.substring(start, end);
                    // 替换为 `?`
                    sb.append('?');
                    // 更新参数顺序映射 map，key 为顺序号，value 为参数名
                    paramMap.put(sort++, paramName);
                    // 更新 i 跳过 `#{paramName}`
                    i = end;
                } else {
                    // 如果没有找到 `}`，则保留原字符
                    sb.append(currentChar);
                }
            } else {
                // 非占位符字符，直接添加到结果 SQL
                sb.append(currentChar);
            }
        }
        // 使用 Map.Entry 返回结果
        return new AbstractMap.SimpleEntry<>(sb.toString(), paramMap);
    }

    public static List<Object> getPlaceHolderList(Map<Integer, String> paramsIndexList,List<Param> paramsFromRequest, List<DynamicAPIParamsConfig> paramsFromTable) {
        List<Object> placeHolderList = new ArrayList<>();
        for (int i = 0; i < paramsIndexList.size(); i++) {
            String paramName = paramsIndexList.get(i);
            String value = null;

            // 1. 先从 paramsFromRequest 中查找对应的 Param
            for (Param requestParam : paramsFromRequest) {
                if (paramName.equals(requestParam.getParam_name())) {
                    value = requestParam.getParam_value();
//                    placeHolderList.add(requestParam.getParam_value());
//                    placeHolderList.set(i, String.valueOf(requestParam.getParam_value()));
                    break;
                }
            }
            // 2. 如果请求参数中没有对应的值，从 paramsFromTable 中查找默认值
            if (value== null) {
                for (DynamicAPIParamsConfig tableParam : paramsFromTable) {
                    if (paramName.equals(tableParam.getParamName())) {
                        value =  tableParam.getDefaultValue();
                        break;
                    }
                }
            }
            // 3. 如果都找不到，抛出异常
            if (value == null) {
                throw new IllegalArgumentException("参数 `" + paramName + "` 无效，未找到对应的值");
            }
            placeHolderList.add(value);
        }



        return placeHolderList;
    }

    public static String whereHandler(List<Param> paramsFromRequest, List<DynamicAPIParamsConfig> paramsFromTable) {
        if (paramsFromRequest.isEmpty()) return "";

        Map<String, Object> paramsMap = new HashMap<>();
        for (Param param : paramsFromRequest) {
            paramsMap.put(param.getParam_name(), param.getParam_value());
        }
        StringBuilder sb = new StringBuilder();
        for (DynamicAPIParamsConfig item : paramsFromTable) {

            if (paramsMap.containsKey(item.getParamName())){ // 如果入参里有，加上
                sb.append(" AND ").append(item.getParamName()).append(" ").append(item.getOperator()).append(" ");
                if(!isNumeric(item.getParamValue())){
                    sb.append("'").append(paramsMap.get(item.getParamName())).append("'").append("\n");
                } else {
                    sb.append(paramsMap.get(item.getParamName())).append("\n");
                }

            } else if (item.getRequired().equals("1")) { // 如果入参里面没有，但是这个是必填项，也加上，拿默认值
                sb.append(" AND ").append(item.getParamName()).append(" ").append(item.getOperator()).append(" ");
                if(!isNumeric(item.getParamValue())){
                    sb.append("'").append(item.getDefaultValue()).append("'").append("\n");
                } else {
                    sb.append(item.getDefaultValue()).append("\n");
                }


            }
        }
        return sb.toString();
    }

}
