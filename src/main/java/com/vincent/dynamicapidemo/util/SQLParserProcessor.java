package com.vincent.dynamicapidemo.util;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLStatementParser;
//import com.sql.sqlflow.SqlFlowApplication;
//import com.sql.sqlflow.druid.parser.prehandle.PreHandleUtil;
//import com.sql.sqlflow.druid.parser.prehandle.Regex;
//import com.sql.sqlflow.druid.parser.vo.*;
import com.vincent.dynamicapidemo.entity.VO.SQL.BloodRelationVO;
import com.vincent.dynamicapidemo.entity.VO.SQL.BloodTableRelationVO;
import com.vincent.dynamicapidemo.entity.VO.SQL.ItemVO;
import com.vincent.dynamicapidemo.entity.VO.SQL.TableVO;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Vincent(Wenxuan) Wang
 * @Date: 11/7/24
 */
@Component
public class SQLParserProcessor {

    public List<BloodTableRelationVO> process(String sql, String dbType, List<BloodTableRelationVO> bloodTableRelationList) {
        //0.sql 预处理
        sql = sql.replace("{", "").replace("}", "").replace("LIKE ANY", "IN");

        //1.判断SQL是否为Union
        SQLStatementParser ast = new HiveStatementParser(sql);
        SQLStatement statement = null;

        try {
            statement = ast.parseStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //2.只处理insert和Create句型 ， 不包含DDL
        if (statement instanceof SQLInsertStatement) {
            System.out.println("我是Insert语句");
            SQLInsertStatement insertStatement = (SQLInsertStatement) statement;

            if (insertStatement.getValuesList().size() == 0) {   //不处理单纯的insert...values的句型
                //处理with******************************************************************
                bloodTableRelationList = this.processWith(insertStatement.getWith(), bloodTableRelationList);

                //1)处理insert (目标对象）********************************
                //获取表名
                SQLExprTableSource tableSource = insertStatement.getTableSource();
                String tableName = tableSource.getTableName();
                String tableAlias = tableSource.getAlias();
                if (tableAlias == null || tableAlias.equals("")) {
                    tableAlias = tableName;
                }

                String owner = ((SQLPropertyExpr) tableSource.getExpr()).getOwnernName();
                System.out.println("目标表名：" + tableName + " 目标库名：" + owner);

                //获取字段列表
                List<SQLExpr> columns = insertStatement.getColumns();

                //表名路径
                String path = owner + "/" + tableName;

                //组装目标表对象
                TableVO targetTableVO = new TableVO();
                targetTableVO.setDbName(owner);
                targetTableVO.setPath(path);
                targetTableVO.setType("实体表");
                targetTableVO.setTableAlias(tableAlias);
                targetTableVO.setTableName(tableName);
                targetTableVO.setItemList(this.getInsertAndCreateItemList(columns, owner, tableName));

                //解析语法树
                bloodTableRelationList = this.processSelect(insertStatement.getQuery(), targetTableVO, bloodTableRelationList);
            }
        }
        else if(statement instanceof HiveCreateTableStatement){
            System.out.println("我是Create语句");
            HiveCreateTableStatement createStatement = (HiveCreateTableStatement) statement ;
            if(createStatement.getSelect() !=null ){   //不处理DDL语句
                //1) 处理Create（目标对象）
                SQLExprTableSource tableSource = createStatement.getTableSource() ;
                String tableName = tableSource.getTableName();
                String tableAlias = tableSource.getAlias() ;
                if(tableAlias ==null || tableAlias.equals("")){
                    tableAlias = tableName ;
                }
                String owner = ((SQLPropertyExpr) tableSource.getExpr()).getOwnernName() ;
                System.out.println("目标表名：" + tableName + " 目标库名：" + owner);

                //表名路径
                String path = owner + "/" + tableName;

                //组装目标对象
                TableVO targetTableVO = new TableVO();
                targetTableVO.setDbName(owner);
                targetTableVO.setPath(path);
                targetTableVO.setType("实体表");
                targetTableVO.setTableAlias(tableAlias);
                targetTableVO.setTableName(tableName);
                targetTableVO.setItemList(null);

                if(createStatement.getSelect() == null && createStatement.getLike() != null){
                    System.out.println("不处理create table ... like");
                }else if(createStatement.getLike() ==null && createStatement.getSelect()==null){
                    System.out.println("不处理DDL");
                }else {
                    //解析语法树
                    bloodTableRelationList = this.processSelect(createStatement.getSelect(), targetTableVO , bloodTableRelationList);
                }
            }
        }


        return bloodTableRelationList;
    }

    //处理with
    public List<BloodTableRelationVO> processWith(SQLWithSubqueryClause withSubqueryClause , List<BloodTableRelationVO> bloodTableRelationList){
        //处理with(先处理查询，再处理with)
        if(withSubqueryClause !=null){
            //处理with(表、表别名、字段、别名)
            List<SQLWithSubqueryClause.Entry> list = withSubqueryClause.getEntries() ;
            for(SQLWithSubqueryClause.Entry entry : list){
                //1.目标表处理
                String uuid = entry.getAlias();
                String targetTableName = uuid ;
                String targetTableAlias = targetTableName ;
                String targetTableType= "虚拟表";
                String targetTableDbName = "";
                String targetTablePath = targetTableDbName + "/" + targetTableName ;

                TableVO targetTableVO = new TableVO();
                targetTableVO.setDbName("/");
                targetTableVO.setTableName(targetTableName);
                targetTableVO.setPath(targetTablePath);
                targetTableVO.setTableAlias(targetTableAlias);
                targetTableVO.setItemList(null);

                bloodTableRelationList = processSelect(entry.getSubQuery() , targetTableVO , bloodTableRelationList);
            }
        }
        return bloodTableRelationList ;
    }

    public List<BloodTableRelationVO> processSelect (SQLSelect sqlSelect , TableVO targetTableVO , List<BloodTableRelationVO> bloodTableRelationList){
        System.out.println("processSelect");
        //处理Union
        if(sqlSelect.getQuery() instanceof SQLUnionQuery){
            System.out.println("union块");
            //处理union
            //处理Union(表、表别名、字段、字段别名)
            SQLUnionQuery unionQuery = (SQLUnionQuery) sqlSelect.getQuery();
            bloodTableRelationList = processUnion(unionQuery , targetTableVO , bloodTableRelationList);
        }

        //处理一般查询
        else if(sqlSelect.getQuery() instanceof SQLSelectQueryBlock){
            SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock) sqlSelect.getQuery();

            //处理表
            SQLTableSource from = selectQueryBlock.getFrom();

            if(from instanceof SQLExprTableSource){
                processFrom(from , targetTableVO , bloodTableRelationList , true);
            }else if(from instanceof SQLSubqueryTableSource){
                processFrom(from , targetTableVO , bloodTableRelationList , true) ;
            }else{
                processFrom(from , targetTableVO , bloodTableRelationList , true) ;
            }
        }
        return bloodTableRelationList ;
    }

    //处理union
    public List<BloodTableRelationVO> processUnion(SQLUnionQuery unionQuery , TableVO targetTableVO , List<BloodTableRelationVO> bloodTableRelationList){
        //获得右侧的SQL
        SQLSelectQuery rightQuery = unionQuery.getRight() ;
        if(rightQuery instanceof SQLSelectQueryBlock){
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) rightQuery;
            SQLTableSource from = queryBlock.getFrom() ;
            if(from instanceof SQLExprTableSource){
                this.processFrom(from , targetTableVO , bloodTableRelationList , true);
            }else{
                this.processFrom(from , targetTableVO , bloodTableRelationList , true);
            }
        }

        //获得Union中剩下的SQL块（从下往上解析）
        SQLSelectQuery leftQuery = unionQuery.getLeft() ;
        processQuery(leftQuery , targetTableVO , bloodTableRelationList) ;

        return bloodTableRelationList ;
    }

    //处理from(1.一般表 2.子查询 3.left join
    public TableVO processFrom(SQLTableSource from , TableVO targetTableVO , List<BloodTableRelationVO> bloodTableRelationList , boolean isSingle){
        TableVO sourceTableVO = new TableVO();

        //一般的表
        if(from instanceof SQLExprTableSource){
            SQLExprTableSource tableSource = (SQLExprTableSource) from ;
            SQLExpr expr = tableSource.getExpr() ;
            sourceTableVO.setSingle(isSingle);

            if(expr instanceof SQLIdentifierExpr){
                SQLIdentifierExpr _table = (SQLIdentifierExpr) expr ;
                String tableName = _table.getName() ;
                String tableAlias = tableSource.getAlias() ;
                String tableOwner = "" ;

                if(tableAlias ==null || tableAlias.equals("")){
                    tableAlias  = tableName ;
                }
                String tablePath = tableOwner + "/" + tableName ;
                String tableType = "实体表";

                sourceTableVO.setTableName(tableName);
                sourceTableVO.setTableAlias(tableAlias);
                sourceTableVO.setPath(tablePath);
                sourceTableVO.setType(tableType);

                //处理字段
                SQLSelectQueryBlock _block = null ;
                _block = (SQLSelectQueryBlock) getSQLQueryBlock(from);

                List<SQLSelectItem> selectItems = _block.getSelectList() ;
                List<ItemVO> itemList = new ArrayList<>();
                for(SQLSelectItem selectItem : selectItems){
                    SQLExpr itemExpr = selectItem.getExpr();
                    String itemAlias = "";
                    if(itemAlias == null || itemAlias.equals("")){
                        itemAlias = selectItem.getAlias() ;
                    }
                    itemList = this.processItem(itemExpr  , itemList , tableName , itemAlias) ;
                }
                sourceTableVO.setItemList(itemList);

                //加入血缘
                BloodTableRelationVO bloodTableRelationVO = new BloodTableRelationVO();
                bloodTableRelationVO.setSourceTableVO(sourceTableVO);
                bloodTableRelationVO.setTargetTableVO(targetTableVO);
                bloodTableRelationList.add(bloodTableRelationVO);
            } else if (expr instanceof SQLPropertyExpr){
                SQLPropertyExpr _table = (SQLPropertyExpr) expr ;
                String tableName = _table.getName();
                String tableAlias = tableSource.getAlias() ;
                String tableOwner = _table.getOwnernName() ;
                if(tableAlias == null || tableAlias.equals("")){
                    tableAlias = tableName ;
                }
                String tableType = "实体表";
                String tablePath = tableOwner + "/" + tableName ;

                //组装源对象
                sourceTableVO.setTableName(tableName);
                sourceTableVO.setDbName(tableOwner);
                sourceTableVO.setTableAlias(tableAlias);
                sourceTableVO.setPath(tablePath);
                sourceTableVO.setType(tableType);

                //处理字段
                SQLSelectQueryBlock _block = null ;
                _block = (SQLSelectQueryBlock) getSQLQueryBlock(from);

                List<SQLSelectItem> selectItems = _block.getSelectList() ;
                List<ItemVO> itemList = new ArrayList<>();

                for(SQLSelectItem selectItem : selectItems){
                    SQLExpr itemExpr = selectItem.getExpr();
                    String itemAlias = "";
                    if(itemAlias == null || itemAlias.equals("")){
                        itemAlias = selectItem.getAlias() ;
                    }
                    itemList = this.processItem(itemExpr  , itemList , tableName , itemAlias) ;
                }
                sourceTableVO.setItemList(itemList);

                //加入血缘
                BloodTableRelationVO bloodTableRelationVO = new BloodTableRelationVO();
                bloodTableRelationVO.setSourceTableVO(sourceTableVO);
                bloodTableRelationVO.setTargetTableVO(targetTableVO);
                bloodTableRelationList.add(bloodTableRelationVO);
            }
        }
        //左连接
        else if(from instanceof SQLJoinTableSource){
            SQLJoinTableSource joinTable = (SQLJoinTableSource) from ;
            //左边的表（有可能也是join
            SQLTableSource right = joinTable.getRight();

            if(right instanceof SQLJoinTableSource){
                System.out.println("left 也是左连接 ， 处理血缘关系");
                String tableAlias = right.getAlias();
                String uuid = tableAlias + "-" + UUID.randomUUID().toString();
                String tableName = uuid ;
                String tablePath = tableAlias ;
                String tableType = "虚拟表";

                //组装源对象
                sourceTableVO.setTableName(tableName);
                sourceTableVO.setTableAlias(tableAlias);
                sourceTableVO.setPath(tablePath);
                sourceTableVO.setType(tableType);

                SQLSelectQueryBlock _block = null ;
                if(right.getParent() instanceof SQLSelectQueryBlock){
                    _block = (SQLSelectQueryBlock) right.getParent() ;
                }else if(right.getParent().getParent() instanceof SQLSelectQueryBlock){
                    _block = (SQLSelectQueryBlock) right.getParent().getParent() ;
                }else if(right.getParent().getParent().getParent() instanceof SQLSelectQueryBlock){
                    _block = (SQLSelectQueryBlock) right.getParent().getParent().getParent() ;
                }else{
                    System.out.println(_block.toString());
                }

                if(_block !=null){
                    List<ItemVO> itemList = new ArrayList<>();
                    List<SQLSelectItem> selectItems = _block.getSelectList() ;
                    itemList = new ArrayList<>();
                    for(SQLSelectItem selectItem : selectItems){
                        SQLExpr expr = selectItem.getExpr();
                        SQLExpr itemExpr = selectItem.getExpr();
                        String itemAlias = "";
                        if(itemAlias == null || itemAlias.equals("")){
                            itemAlias = selectItem.getAlias() ;
                        }
                        System.out.println("字段别名："  + selectItem.getAlias());
                        itemList = this.processItem(itemExpr , itemList , tableName , itemAlias);
                    }
                    sourceTableVO.setItemList(itemList);
                }

                //加入血缘
                BloodTableRelationVO bloodTableRelationVO = new BloodTableRelationVO();
                bloodTableRelationVO.setSourceTableVO(sourceTableVO);
                bloodTableRelationVO.setTargetTableVO(targetTableVO);
                bloodTableRelationList.add(bloodTableRelationVO);
            }else{
                processFrom (right , targetTableVO , bloodTableRelationList , isSingle);
            }

            //左边的表（有可能也是join
            SQLTableSource left = joinTable.getLeft();
            processFrom(left , targetTableVO , bloodTableRelationList , isSingle) ;
        }
        //子查询
        else if(from instanceof SQLSubqueryTableSource){
            String tableAlias = from.getAlias() ;
            String tableName = tableAlias + "-" + UUID.randomUUID().toString();
            String dbName = "";
            String tablePath = dbName + "/" + tableName ;
            String tableType = "虚拟表";

            List<ItemVO> itemList = new ArrayList<>();
            SQLSelectQueryBlock _block = null ;

            _block = (SQLSelectQueryBlock)getSQLQueryBlock(from);
            if(_block !=null){
                itemList = new ArrayList<>();
                List<SQLSelectItem> selectItems = _block.getSelectList() ;
                itemList = new ArrayList<>();
                for(SQLSelectItem selectItem : selectItems){
                    SQLExpr expr = selectItem.getExpr();
                    SQLExpr itemExpr = selectItem.getExpr();
                    String itemAlias = "";
                    if(itemAlias == null || itemAlias.equals("")){
                        itemAlias = selectItem.getAlias() ;
                    }
                    System.out.println("字段别名："  + selectItem.getAlias());
                    itemList = this.processItem(itemExpr , itemList , tableName , itemAlias);
                }
                sourceTableVO.setItemList(itemList);
            }

            //组装源对象
            sourceTableVO.setTableName(tableName);
            sourceTableVO.setTableAlias(tableAlias);
            sourceTableVO.setPath(tablePath);
            sourceTableVO.setType(tableType);
            sourceTableVO.setItemList(itemList);
            sourceTableVO.setSingle(isSingle);

            //加入血缘
            BloodTableRelationVO bloodTableRelationVO = new BloodTableRelationVO();
            bloodTableRelationVO.setSourceTableVO(sourceTableVO);
            bloodTableRelationVO.setTargetTableVO(targetTableVO);
            bloodTableRelationList.add(bloodTableRelationVO);

            //将源对想作为目标对象传入
            processSelect(((SQLSubqueryTableSource)from).getSelect() , sourceTableVO , bloodTableRelationList) ;
        }
        else if (from instanceof SQLUnionQueryTableSource){
            SQLUnionQueryTableSource unionFrom = (SQLUnionQueryTableSource) from ;
            String tableAlias = unionFrom.getAlias();
            String uuid = tableAlias + "-" + UUID.randomUUID().toString() ;
            String tableName = uuid ;
            String tablePath = tableAlias ;
            String tableType = "虚拟表";

            //组装源对象
            sourceTableVO.setTableName(tableName);
            sourceTableVO.setTableAlias(tableAlias);
            sourceTableVO.setPath(tablePath);
            sourceTableVO.setType(tableType);

            SQLSelectQueryBlock _block = null ;

            _block = (SQLSelectQueryBlock)getSQLQueryBlock(from);
            if(_block !=null){
                List<ItemVO> itemList = new ArrayList<>();
                List<SQLSelectItem> selectItems = _block.getSelectList() ;
                itemList = new ArrayList<>();
                for(SQLSelectItem selectItem : selectItems){
                    SQLExpr expr = selectItem.getExpr();
                    SQLExpr itemExpr = selectItem.getExpr();
                    String itemAlias = "";
                    if(itemAlias == null || itemAlias.equals("")){
                        itemAlias = selectItem.getAlias() ;
                    }
                    System.out.println("字段别名："  + selectItem.getAlias());
                    itemList = this.processItem(itemExpr , itemList , tableName , itemAlias);
                }
                sourceTableVO.setItemList(itemList);
            }

            //加入血缘
            BloodTableRelationVO bloodTableRelationVO = new BloodTableRelationVO();
            bloodTableRelationVO.setSourceTableVO(sourceTableVO);
            bloodTableRelationVO.setTargetTableVO(targetTableVO);
            bloodTableRelationList.add(bloodTableRelationVO);

            //处理Union中的歌SQL
            List<SQLSelectQuery> queryList = unionFrom.getUnion().getRelations();
            for(SQLSelectQuery query : queryList){
                bloodTableRelationList = processQuery(query , sourceTableVO , bloodTableRelationList) ;
            }
        }else if (from instanceof SQLLateralViewTableSource){
            SQLTableSource tableSource = this.processLateralView((SQLLateralViewTableSource)from);
            if(tableSource instanceof SQLExprTableSource){
                processFrom(tableSource , targetTableVO , bloodTableRelationList, true ) ;
            }else{
                processFrom(tableSource , targetTableVO , bloodTableRelationList , false);
            }
        }
        return sourceTableVO ;
    }

    //处理lateralView
    public SQLTableSource processLateralView(SQLLateralViewTableSource sqlLateralViewTableSource){
        SQLTableSource tableSource = sqlLateralViewTableSource.getTableSource() ;
        while(tableSource instanceof SQLLateralViewTableSource){
            tableSource = ((SQLLateralViewTableSource) tableSource).getTableSource()  ;
        }
        return tableSource;
    }

    //处理from 1.一般表 2.子查询 3.left join
    public List<BloodTableRelationVO> processQuery(SQLSelectQuery query , TableVO targetTableVO , List<BloodTableRelationVO> bloodTableRelationList){
        //一般的表
        if(query instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query ;
            SQLTableSource from = queryBlock.getFrom() ;
            if(from instanceof SQLExprTableSource){
                System.out.println("这是单表");
                processFrom(from , targetTableVO , bloodTableRelationList , true);
            }else {
                processFrom(from , targetTableVO , bloodTableRelationList , false);
            }
        }else if(query instanceof SQLUnionQuery){
            bloodTableRelationList = processUnion((SQLUnionQuery) query , targetTableVO , bloodTableRelationList);
        }else{
            System.out.println("处理其他UnionSQL块");
        }
        return bloodTableRelationList;
    }

    //处理字段
    public List<ItemVO> processItem(SQLExpr expr , List<ItemVO> itemList , String tableName , String itemAlias){
        ItemVO item = new ItemVO();
        if(expr instanceof SQLAllColumnExpr){
            System.out.println("字段名：" + expr.toString());   //输出字段名
        }else if(expr instanceof SQLIdentifierExpr){
            SQLIdentifierExpr iExpr = (SQLIdentifierExpr)expr ;
            String itemName = iExpr.getName();
            String itemFormula = iExpr.toString() ;
            if(itemAlias == null){
                itemAlias = itemName ;
            }
            String itemOwner = tableName ;
            item.setItemName(itemName);
            item.setItemAlias(itemAlias);
            item.setItemOwner(itemOwner);
            item.setItemFormula(itemFormula);

            itemList.add(item);
        }else if(expr instanceof SQLPropertyExpr){
            SQLPropertyExpr pExpr = (SQLPropertyExpr) expr ;
            String itemName = pExpr.getName();
            String itemFormula = pExpr.toString();
            if(itemAlias ==null || itemAlias.equals("")){
                itemAlias = itemName;
            }
            String itemOwner = pExpr.getOwnernName() ;
            item.setItemName(itemName);
            item.setItemAlias(itemAlias);
            item.setItemOwner(itemOwner);
            item.setItemFormula(itemFormula);

            itemList.add(item);
        }else if(expr instanceof SQLCharExpr){
            SQLCharExpr cExpr = (SQLCharExpr) expr ;
            String itemName = cExpr.getText();
            String itemFormula = cExpr.toString();
            String itemOwner = "";

            item.setItemName(itemName);
            item.setItemAlias(itemAlias);
            item.setItemOwner(itemOwner);
            item.setItemFormula(itemFormula);
            item.setItemNotIsConstant(false);

            itemList.add(item);
        }else if(expr instanceof SQLCastExpr){
            SQLCastExpr castExpr = (SQLCastExpr) expr ;
            String itemName = "";
            String itemFormula = "";
            String itemOwner = "";

            //cast中未使用函数
            if(castExpr.getExpr() instanceof SQLIdentifierExpr){
                itemName = ((SQLIdentifierExpr) castExpr.getExpr()).getName();
                itemFormula = castExpr.toString() ;

                item.setItemName(itemName);
                item.setItemAlias(itemAlias);
                item.setItemOwner(itemOwner);
                item.setItemFormula(itemFormula);

                itemList.add(item);
            }
            //cast中使用了嵌套
            else if(castExpr.getExpr() instanceof SQLMethodInvokeExpr){
                //处理函数方法
                itemFormula = castExpr.toString();
                itemAlias = ((SQLSelectItem) castExpr.getParent()).getAlias();

                item.setItemAlias(itemAlias);
                item.setItemFormula(itemFormula);

                itemList = this.processMethod((SQLMethodInvokeExpr)castExpr.getExpr() , item , itemList);
            }
            else if(castExpr.getExpr() instanceof SQLPropertyExpr){
                //处理函数方法
                SQLPropertyExpr pExpr = (SQLPropertyExpr) castExpr.getExpr();
                itemName = pExpr.getName() ;
                itemAlias = ((SQLSelectItem)castExpr.getParent()).getAlias() ;
                if(itemAlias ==null){
                    itemAlias = itemName;
                }
                itemFormula = castExpr.toString() ;
                itemOwner = pExpr.getOwnernName() ;

                item.setItemName(itemName);
                item.setItemAlias(itemAlias);
                item.setItemOwner(itemOwner);
                item.setItemFormula(itemFormula);

                itemList.add(item);
            }
        }
        //处理方法
        else if(expr instanceof SQLMethodInvokeExpr){
            String itemFormula = expr.toString() ;
            item.setItemAlias(itemAlias);
            item.setItemFormula(itemFormula);

            itemList = processMethod((SQLMethodInvokeExpr)expr, item, itemList) ;
        }

        //处理 case when
        else if(expr instanceof SQLCaseExpr){
            String itemFormula = expr.toString() ;
            item.setItemAlias(itemAlias);
            item.setItemFormula(itemFormula);

            SQLCaseExpr caseExpr = (SQLCaseExpr) expr ;
            List<SQLCaseExpr.Item> caseList = caseExpr.getItems() ;
            for(SQLCaseExpr.Item caseItem : caseList){
                itemList = processCaseWhen2(caseItem , item , itemList , itemAlias);
            }
            //处理else
            itemList = processSQLExpr(caseExpr.getElseExpr() , item , itemList , itemAlias );
        }

        //处理聚合函数
        else if(expr instanceof SQLAggregateExpr){
            String itemFormula = expr.toString() ;
            item.setItemAlias(itemAlias);
            item.setItemFormula(itemFormula);
            item.setItemNotIsConstant(false) ;    //如果常量，则设置为false

            itemList.add(item);
        }

        //处理常量
        else if(expr instanceof SQLIntegerExpr){
            SQLIntegerExpr intExpr = (SQLIntegerExpr) expr ;
            String itemName = intExpr.getNumber().toString();
            String itemFormula = intExpr.toString() ;
            String itemOwner = "";

            item.setItemName(itemName);
            item.setItemAlias(itemAlias);
            item.setItemOwner(itemOwner);
            item.setItemFormula(itemFormula);
            item.setItemNotIsConstant(false) ;    //如果常量，则设置为false

            itemList.add(item);
        }

        else if(expr instanceof SQLBinaryExpr){
            itemList = processSQLExpr(expr , item , itemList , itemAlias);
        }
        else {
            System.out.println(expr.toString());
        }
        return itemList  ;
    }

    //转化为目标的字段（insert)
    public List<ItemVO> getInsertAndCreateItemList(List<SQLExpr> columns , String dbName , String tableName ){
        List<ItemVO> list = new ArrayList<>();
        if(columns.size()>0){
            //有字段的场景
            for(SQLExpr column : columns){
                //变量初始化
                ItemVO item = null;
                String itemName = null ;

                //组装字段对象
                if(column instanceof SQLAllColumnExpr){
                    item = new ItemVO();
                    itemName= column.toString() ;

                    item.setItemName((itemName));
                }else if(column instanceof SQLIdentifierExpr){
                    item = new ItemVO ();
                    itemName = ((SQLIdentifierExpr) column).getName();

                    item.setItemName(itemName);
                }else if(column instanceof SQLPropertyExpr){
                    item = new ItemVO();
                    itemName = ((SQLPropertyExpr) column).getName() ;

                    item.setItemName(itemName);
                }else{
                    item = new ItemVO();
                    itemName = column.toString();
                }

                list.add(item);
            }
        }
        else{
            //目标表没有自断
            //List= getTargetItemList(dbName , tableName ) ;
        }
        return list;
    }

    public List<ItemVO> getItemList(List<SQLSelectItem> selectItems , String tableName){
        List<ItemVO> itemList = new ArrayList<ItemVO>();
        for(SQLSelectItem selectItem : selectItems){
            SQLExpr expr = selectItem.getExpr() ;
            SQLExpr itemExpr = selectItem.getExpr() ;
            String itemAlias = "";

            if(itemAlias == null || itemAlias.equals("")){
                itemAlias = selectItem.getAlias() ;
            }
            System.out.println("字段别名："  + selectItem.getAlias());
            itemList = this.processItem(itemExpr , itemList , tableName , itemAlias);
        }
        return itemList ;
    }

    //处理字段血缘
    public List<BloodRelationVO> processAllBloodRelation(List<BloodTableRelationVO> bloodTableRelationList){
        TableVO sourceTableVO ;
        String sourceTableName ;
        String sourceTablePath ;
        String sourceTableType ;
        String sourceTableAlias ;

        String sourceItemName ;
        String sourceItemAlias ;
        String sourceItemOwner ;
        String sourceItemFormula ;
        boolean sourceIsSingle ;

        boolean sourceItemNotIsConstant ;
        List<ItemVO> sourceItemList;

        TableVO targetTableVO ;
        String targetTableName ;
        String targetTablePath ;
        String targetTableType ;
        String targetTableAlias ;

        String targetItemName ;
        String targetItemOwner ;
        String targetItemFormula ;
        BloodRelationVO bloodRelationVO = new BloodRelationVO();

        List<ItemVO> targetItemList;

        List<BloodRelationVO> allBloodRelationList = new ArrayList<>();
        int tableSize = getTableCount(bloodTableRelationList);
        int i = 0 ;
        TableVO oneTargetTableVO = new TableVO();

        for(BloodTableRelationVO relationVO : bloodTableRelationList){
            targetTableVO = relationVO.getTargetTableVO() ;
            if(i==0){
                oneTargetTableVO = targetTableVO ;
            }
            targetTableName = targetTableVO.getTableName();
            targetTableAlias = targetTableVO.getTableAlias()  ;
            targetTablePath = targetTableVO.getPath() ;
            targetTableType = targetTableVO.getType() ;
            targetItemList = targetTableVO.getItemList() ;

            sourceTableVO = relationVO.getSourceTableVO();
            sourceTableName = sourceTableVO.getTableName();
            sourceTableAlias = sourceTableVO.getTableAlias();
            sourceTablePath = sourceTableVO.getPath() ;
            sourceTableType = sourceTableVO.getType() ;
            sourceItemList = sourceTableVO.getItemList();
            sourceIsSingle = sourceTableVO.isSingle() ;

            //表级血缘映射
            bloodRelationVO = new BloodRelationVO();
            bloodRelationVO.setSourceName(relationVO.getSourceTableVO().getTableName());
            bloodRelationVO.setSourcePath(relationVO.getSourceTableVO().getPath());
            bloodRelationVO.setTargetName(relationVO.getTargetTableVO().getTableName());
            bloodRelationVO.setTargetPath(relationVO.getTargetTableVO().getPath());
            bloodRelationVO.setObjectType("1");
            allBloodRelationList.add(bloodRelationVO);

            if(sourceItemList!=null){
                //目标表没有自断或者字段为*
                if(targetItemList ==null || targetItemList.size()==0 || (targetItemList.size()==1 && (targetItemList.get(0).equals("*")))){
                    for(ItemVO sourceItemVO : sourceItemList){
                        sourceItemName = sourceItemVO.getItemName();
                        sourceItemOwner = sourceItemVO.getItemOwner() ;
                        //没有owner的情况 ：1）单表 2)常量
                        if((sourceItemOwner ==null || sourceItemOwner.equals("*")) && sourceIsSingle ){
                            sourceItemOwner = sourceTableAlias ;
                        }
                        sourceItemAlias = sourceItemVO.getItemFormula() ;
                        sourceItemNotIsConstant = sourceItemVO.isItemNotIsConstant();
                        //别名为空：不处理常量的情况
                        if((sourceItemAlias ==null || sourceItemAlias.equals("")) && sourceIsSingle){
                            sourceItemAlias = sourceItemName ;
                        }
                        sourceItemFormula = sourceItemVO.getItemFormula();
                        sourceItemNotIsConstant = sourceItemVO.isItemNotIsConstant() ;

                        //判断字段所有者与表别名是否一致（如果一致则建立血缘关系
                        if(sourceItemOwner !=null && (sourceItemOwner.equals(sourceTableName) || sourceItemOwner.equals(sourceTableAlias))){
                            targetItemName = sourceItemAlias ;

                            bloodRelationVO = new BloodRelationVO();
                            bloodRelationVO.setSourceName(sourceItemName);
                            bloodRelationVO.setSourcePath(sourceTablePath + "/" + sourceItemName);
                            bloodRelationVO.setTargetName(targetItemName);
                            bloodRelationVO.setTargetPath(targetTablePath + "/" + targetItemName);
                            bloodRelationVO.setTargetItemFormula(sourceItemFormula);
                            bloodRelationVO.setObjectType("2");
                            allBloodRelationList.add(bloodRelationVO);
                        }
                    }
                }

                //源字段为*
                else if(sourceItemList.size() ==1 && (sourceItemList.get(0)).equals("*")){
                    for(ItemVO targetItemVO :targetItemList){
                        targetItemName = targetItemVO.getItemName() ;
                        sourceItemName = targetItemName ;

                        bloodRelationVO = new BloodRelationVO() ;
                        bloodRelationVO.setSourceName(sourceItemName);
                        bloodRelationVO.setSourcePath(sourceTablePath + "/" + sourceItemName);
                        bloodRelationVO.setTargetName(targetItemName);
                        bloodRelationVO.setTargetPath(targetTablePath + "/" + targetItemName);
                        bloodRelationVO.setObjectType("2");

                        allBloodRelationList.add(bloodRelationVO);
                    }
                }
                //源与目标字段都有的场景
                else{
                    for(ItemVO targetItemVO : targetItemList){
                        targetItemName = targetItemVO.getItemName();
                        targetItemOwner = targetItemVO.getItemOwner() ;
                        for(ItemVO sourceItemVO : sourceItemList){
                            sourceItemName = sourceItemVO.getItemName();
                            sourceItemAlias = sourceItemVO.getItemAlias();
                            if(sourceItemAlias ==null){
                                sourceItemAlias = sourceItemName;
                            }
                            sourceItemOwner = sourceItemVO.getItemOwner() ;
                            sourceItemFormula = sourceItemVO.getItemFormula() ;
                            //需要判断字段别名和目标表的别名相同
                            if(sourceItemAlias.equals(targetItemName) && sourceItemOwner.equals(sourceTableAlias)){
                                bloodRelationVO = new BloodRelationVO() ;
                                bloodRelationVO.setSourceName(sourceItemName);
                                bloodRelationVO.setSourcePath(sourceTablePath + "/" + sourceItemName);
                                bloodRelationVO.setTargetName(targetItemName);
                                bloodRelationVO.setTargetPath(targetTablePath + "/" + targetItemName);
                                bloodRelationVO.setTargetItemFormula(sourceItemFormula);
                                bloodRelationVO.setObjectType("2");
                                allBloodRelationList.add(bloodRelationVO);
                            }
                        }
                    }
                }
            }
        }
        return allBloodRelationList;
    }

    public List<ItemVO> processMethod(SQLMethodInvokeExpr methodInvokeExpr , ItemVO item , List<ItemVO> itemList) {
        String itemName = "";
        String itemOwner = "";
        System.out.println(methodInvokeExpr.toString());
        if (methodInvokeExpr.getMethodName().equals("ROW_NUMBER")) {
            item.setItemName(item.getItemAlias());
            itemList.add(item);
        } else {
            //处理方法
            List<SQLExpr> sqlExprList = methodInvokeExpr.getArguments();
            for (SQLExpr expr : sqlExprList) {
                processSQLExpr(expr, item, itemList , item.getItemAlias());
            }
        }
        return itemList;
    }

    public List<ItemVO> processCaseWhen(SQLCaseExpr.Item caseItem, ItemVO item  ,List<ItemVO> itemList , String itemAlias) {
        System.out.println(caseItem.toString());
        SQLExpr thenExpr = caseItem.getValueExpr();
        String itemName = "";
        String itemOwner = "";
        String itemFormula = caseItem.toString();

        ItemVO newItem = new ItemVO();
        BeanUtils.copyProperties(item, newItem);
        if (thenExpr instanceof SQLCharExpr) {
        /*
            逻辑处理:如果then是常量，则取when的条件
        */
            SQLExpr whenExpr = caseItem.getConditionExpr();
            if (whenExpr instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr _whenExpr = (SQLBinaryOpExpr) whenExpr;
                itemList = processCondition(_whenExpr.getLeft(), item, itemList , itemAlias);
                itemList = processCondition(_whenExpr.getRight(), item, itemList , itemAlias);
            } else if (whenExpr instanceof SQLBetweenExpr) {
                //TODO Between暂时不支持
                itemList = processSQLExpr(whenExpr, item, itemList , itemAlias);
            }
        } else if (thenExpr instanceof SQLPropertyExpr) {
            SQLPropertyExpr pExpr = (SQLPropertyExpr) thenExpr;
            itemName = pExpr.getName();
            itemOwner = pExpr.getOwnernName();
            newItem.setItemName(itemName);
            newItem.setItemOwner(itemOwner);
            newItem.setItemFormula(itemFormula);
            itemList.add(newItem);
        } else if (thenExpr instanceof SQLCaseExpr) {
            //TODO 这里有问题(case when 嵌套)
            SQLCaseExpr caseExpr = (SQLCaseExpr) thenExpr;
            for (SQLCaseExpr.Item __item : caseExpr.getItems()) {
                processCaseWhenPlus(__item, item, itemList , itemAlias);
            }
            //处理else
            if (caseExpr.getElseExpr() != null) {
                processSQLExpr(caseExpr.getElseExpr(), item, itemList ,itemAlias);
            }
            System.out.println(caseExpr.toString());
        } else if (thenExpr instanceof SQLBinaryOpExpr) {//TODO 表达式计算
            processSQLExpr(thenExpr, item, itemList ,itemAlias);
        } else if (thenExpr instanceof SQLMethodInvokeExpr) {
            processSQLExpr(thenExpr, item, itemList ,itemAlias);
        } else {
            System.out.println("case when 例外处理");
        }
        return itemList;
    }

    public List<ItemVO> processCaseWhen2(SQLCaseExpr.Item caseItem, ItemVO item  ,List<ItemVO> itemList , String itemAlias) {
        System.out.println(caseItem.toString());

        //处理条件
        SQLExpr condExpr = caseItem.getConditionExpr();
        itemList = processSQLExpr(condExpr , item, itemList , itemAlias) ;

        //处理then
        SQLExpr thenExpr = caseItem.getValueExpr();
        itemList = processSQLExpr( thenExpr , item , itemList , itemAlias);

        return itemList ;
    }

    public List<ItemVO> processCaseWhenPlus(SQLCaseExpr.Item caseItem, ItemVO item , List<ItemVO> itemList , String itemAlias) {
        System.out.println(caseItem.toString());
        SQLExpr thenExpr = caseItem.getValueExpr();
        String itemName = "";
        String itemOwner = "";
        String itemFormula = caseItem.toString();
        if (thenExpr instanceof SQLCharExpr) {
            /*
                逻辑处理:如果then是常里，则取When的条件
             */
            SQLExpr whenExpr = caseItem.getConditionExpr();
            if (whenExpr instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr _whenExpr = (SQLBinaryOpExpr) whenExpr;
                itemList = processCondition(_whenExpr.getLeft(), item, itemList , itemAlias);
                itemList = processCondition(_whenExpr.getRight(), item, itemList , itemAlias);
            } else if (whenExpr instanceof SQLBetweenExpr) {
                //TOD0 Betiveen智时不支持
            } else if (thenExpr instanceof SQLPropertyExpr) {
                SQLPropertyExpr pExpr = (SQLPropertyExpr) thenExpr;
                itemName = pExpr.getName();
                itemOwner = pExpr.getOwnernName();
                item.setItemName(itemName);
                item.setItemOwner(itemOwner);
                item.setItemFormula(itemFormula);
                itemList.add(item);
            } else if (thenExpr instanceof SQLCaseExpr) {
                //todo 这里有问题 case when 嵌套
                SQLCaseExpr caseExpr = (SQLCaseExpr) thenExpr;
                for(SQLCaseExpr.Item __item : caseExpr.getItems()){
                    processCaseWhen(__item , item , itemList , itemAlias);
                }

                //处理else
                if(caseExpr.getElseExpr()!=null){
                    processSQLExpr(caseExpr.getElseExpr() , item , itemList , itemAlias);
                }

                System.out.println(caseExpr.toString());
            }
        }
        return itemList ;
    }


    //处理条件(左侧和右侧)
    public List<ItemVO>processCondition(SQLExpr sqlExpr,ItemVO item ,List<ItemVO> itemList , String itemAlias) {
        String itemName = "";
        String itemOwner ="";
        //表达式处理
        if (sqlExpr instanceof SQLPropertyExpr) {
            SQLPropertyExpr pExpr = (SQLPropertyExpr) sqlExpr;
            itemOwner = pExpr.getOwnernName();
            itemName = pExpr.getName();
            item.setItemOwner(itemOwner);
            item.setItemName(itemName);
            itemList.add(item);
        }
        //字符串不处理
        else {
            processSQLExpr(sqlExpr, item, itemList , itemAlias);
        }
        return itemList;
    }

    //处理没有owner的问题
    public int getTableCount(List<BloodTableRelationVO> bloodTableRelationList) {
        Map<String, TableVO> tableMap = new HashMap();
        for (BloodTableRelationVO bloodTableRelationVO : bloodTableRelationList) {
            String tableType = "";
            String tablePath = "";
            TableVO sourceTableVO = bloodTableRelationVO.getSourceTableVO();
            tableType = sourceTableVO.getType();
            tablePath = sourceTableVO.getPath();
            if (!tableType.equals("虚拟表")) {
                tableMap.put(tablePath, sourceTableVO);
            }
            TableVO targetTableVO = bloodTableRelationVO.getTargetTableVO();
            tableType = targetTableVO.getType();
            tablePath = targetTableVO.getPath();
            if (!tableType.equals("虚拟表")) {
                tableMap.put(tablePath, targetTableVO);
            }
        }

        return tableMap.size();
    }

    //处理表达式
    public List<ItemVO> processSQLExpr(SQLExpr expr , ItemVO itemVO , List<ItemVO> itemList , String itemAlias) {
        String itemName = "";
        String itemOwner = "";

        if (expr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binExpr = (SQLBinaryOpExpr) expr;
            //先处理右边
            SQLExpr right = binExpr.getRight();
            itemList = processSQLExpr(right, itemVO, itemList , itemAlias);

            //再处理左边
            SQLExpr left = binExpr.getLeft();
            itemList = processSQLExpr(left, itemVO, itemList, itemAlias);
        } else if (expr instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr iExpr = (SQLIdentifierExpr) expr;
            itemName = iExpr.getName();
            ItemVO newItemVO = new ItemVO();
            BeanUtils.copyProperties(itemVO, newItemVO);
            newItemVO.setItemName(itemName);
            newItemVO.setItemOwner(itemOwner);
            if (newItemVO.getItemAlias() == null) {
                newItemVO.setItemAlias(itemName);
            }

            itemList.add(newItemVO);
        } else if (expr instanceof SQLPropertyExpr) {
            SQLPropertyExpr pExpr = (SQLPropertyExpr) expr;
            itemName = pExpr.getName();
            itemOwner = pExpr.getOwnernName();
            ItemVO newItemVO = new ItemVO();
            BeanUtils.copyProperties(itemVO, newItemVO);
            newItemVO.setItemName(itemName);
            newItemVO.setItemOwner(itemOwner);
            if (newItemVO.getItemAlias() == null) {
                newItemVO.setItemAlias(itemName);
            }
            itemList.add(newItemVO);
        } else if (expr instanceof SQLBetweenExpr) {
            SQLBetweenExpr btExpr = (SQLBetweenExpr) expr;
            SQLExpr testExpr = btExpr.getTestExpr();
            itemList = processSQLExpr(testExpr, itemVO, itemList , itemAlias);
            SQLExpr begExpr = btExpr.getBeginExpr();
            itemList = processSQLExpr(begExpr, itemVO, itemList, itemAlias);
            SQLExpr engExpr = btExpr.getEndExpr();
            itemList = processSQLExpr(engExpr, itemVO, itemList, itemAlias);
        } else if (expr instanceof SQLCastExpr) {
            SQLCastExpr castExpr = (SQLCastExpr) expr;
            itemList = processSQLExpr(castExpr.getExpr(), itemVO, itemList, itemAlias);
        } else if (expr instanceof SQLMethodInvokeExpr) {
            System.out.println("CAST待处理");
            SQLMethodInvokeExpr methodExpr = (SQLMethodInvokeExpr) expr;
            List<SQLExpr> sqlExprList = methodExpr.getArguments();
            for (SQLExpr sqlExpr : sqlExprList) {
                itemList = processSQLExpr(sqlExpr, itemVO, itemList , itemAlias);
            }
        } else if (expr instanceof SQLCharExpr) {
            System.out.println("常量字符不处理");
        } else if (expr instanceof SQLCaseExpr) {
            SQLCaseExpr caseExpr = (SQLCaseExpr) expr;
            List<SQLCaseExpr.Item> caseList = caseExpr.getItems();
            for (SQLCaseExpr.Item caseItem : caseList) {
                //判断then
                if (!(caseItem.getValueExpr() instanceof SQLCharExpr)) {
                    itemList = processSQLExpr(caseItem.getConditionExpr(), itemVO, itemList, itemAlias);
                } else {
                    itemList = processSQLExpr(caseItem.getValueExpr(), itemVO, itemList, itemAlias);
                }
            }
            //处理e1se
            itemList = processSQLExpr(caseExpr.getElseExpr(), itemVO, itemList, itemAlias);
        } else if (expr instanceof SQLAllColumnExpr) {
            itemName = itemVO.getItemFormula();
            ItemVO newItemVO = new ItemVO();
            BeanUtils.copyProperties(itemVO, newItemVO);
            newItemVO.setItemName(itemName);
            newItemVO.setItemOwner(itemOwner);

            itemList.add(newItemVO);
        }else if(expr instanceof SQLInListExpr) {
            itemList = processSQLExpr(((SQLInListExpr) expr).getExpr(), itemVO, itemList, itemAlias);
        }else{
            System.out.println("无法识别的SQLExpr");
        }

        return itemList;
    }




    public SQLObject getSQLQueryBlock(SQLTableSource from) {
        SQLObject sqlObject = from.getParent();
        while (!(sqlObject instanceof SQLSelectQueryBlock)) {
            sqlObject = sqlObject.getParent();
        }
        return sqlObject;
    }

    public List<ItemVO> getTargetItemList(String dbName, String tableName) {
        List<ItemVO> list = new ArrayList();
        ItemVO itemVO;
        itemVO = new ItemVO();
        itemVO.setItemName("Al");
        list.add(itemVO);
        itemVO = new ItemVO();
        itemVO.setItemName("A2");

        return list;
    }

    public String getTableType(String tableName) {
        String tableType = "";
        if (tableName.substring(0, tableName.indexOf("_")).equals("TMP")) {
            tableType = "物理临时表";
        } else if (tableName.substring(0, 1).equals("/")) {
            tableType = "逻辑临时表";
        } else {
            tableType = "实体表";
        }
        return tableType;
    }
}
