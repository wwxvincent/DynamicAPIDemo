package com.vincent.dynamicapidemo.sqlCheck.service.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.vincent.dynamicapidemo.dynamicApi.entity.VO.ResponseVO;
import com.vincent.dynamicapidemo.sqlCheck.service.SqlValidationRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 12/16/24
 * @Description:
 */
@Slf4j
@Component
public class TableJoinCountValidationRule implements SqlValidationRule {
    @Override
    public String getName() {
        return "TableJoinCountValidation";
    }

    @Override
    public ResponseVO validate(String sql, String type, Map<String, Object> content) {
        int maxCount = content.get("maxJoinTables") == null ? 1 : Integer.parseInt(content.get("maxJoinTables").toString());
        return getTable(sql, type, maxCount);
    }

    public ResponseVO getTable(String sql, String type, int maxCount) {
        DbType dbType;
        switch (type.toUpperCase()) {
            case "MYSQL": dbType = JdbcConstants.MYSQL; break;
            case "ORACLE": dbType = JdbcConstants.ORACLE; break;
            case "POSTGRESQL": dbType = JdbcConstants.POSTGRESQL; break;
            case "HIVE": dbType = JdbcConstants.HIVE; break;
            // add more if need
            default: log.error("Unsupported database type: {}", type); return ResponseVO.fail("Unsupported database type: " + type);
        }

        WallProvider provider = new MySqlWallProvider();
        WallCheckResult result = provider.check(sql);
        boolean valid = result.isSyntaxError();
        if (valid) {return ResponseVO.fail("Syntax Error");}

        // 解析 SQL 语句
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        StringBuilder sb = new StringBuilder();
        int tableCount = 0;
        for (SQLStatement stmt : stmtList) {
            // 创建 SchemaStatVisitor 对象
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            // 遍历 SQL 语句
            stmt.accept(visitor);
//            sb.append("Tables: " + visitor.getTables() + "\n");
//            sb.append("size: " + visitor.getTables().size() + "\n\n");

            // 获取表名
            visitor.getTables().forEach((key, value) -> {
//                System.out.print("Table Name: " + key.getName());
//                System.out.println(" ====> Table Stats: " + value);
                sb.append(" <Table: ").append(key.getName()).append("> ");
//                sb.append(" ====> Table Stats: ").append(value).append("\n");
            });
//            System.out.println("Table Number: " + visitor.getTables().size());
//            sb.append("Table Number: ").append(visitor.getTables().size());
            tableCount = visitor.getTables().size();

        }
        ResponseVO responseVO = new ResponseVO();
        responseVO.setData(tableCount);
        if(tableCount > maxCount) {
            responseVO.setMsg(this.getName() + " : over the max table limit number " + maxCount +" ! "+sb.toString());
            responseVO.setSuccess(Boolean.FALSE);
            return responseVO;
        }
        responseVO.setMsg(this.getName() + " : "+sb.toString());
        responseVO.setSuccess(true);
        return responseVO;
    }
}
