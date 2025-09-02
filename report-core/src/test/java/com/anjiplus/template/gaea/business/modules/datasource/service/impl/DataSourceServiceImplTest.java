package com.anjiplus.template.gaea.business.modules.datasource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.anjiplus.template.gaea.business.ReportApplication;
import com.anjiplus.template.gaea.business.modules.datasource.controller.dto.DataSourceDto;
import com.anjiplus.template.gaea.business.modules.datasource.service.DataSourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


/**
 * Created by raodeming on 2021/7/19.
 */
@SpringBootTest(classes = ReportApplication.class)
public class DataSourceServiceImplTest {

    @Autowired
    private DataSourceService dataSourceService;

    @Test
    public void testHttp(){
        DataSourceDto dto = new DataSourceDto();
        dto.setSourceType("http");
        dto.setHeader("{\"Content-Type\":\"application/json\"}");
        dto.setSourceConfig("{\"apiUrl\":\"http://10.108.26.163:9200/_xpack/sql?format=json\",\"method\":\"POST\",\"header\":\"{\\\"Content-Type\\\":\\\"application/json\\\"}\",\"body\":\"{\\\"query\\\":\\\"select 1\\\"}\"}");
        dto.setDynSentence("{\"query\": \"select HISTOGRAM(logTime,INTERVAL 1 MONTH) as h ,count(flag),flag from \\\"analysis-wifilogin\\\" where  logTime>='2021-02-22 00:28:10.000' and logTime<'2021-03-22 00:28:10.000' GROUP BY h,flag\"}");
        List<JSONObject> execute = dataSourceService.execute(dto);
        System.out.println(execute);
    }

    @Test
    public void testSQLiteConnection(){
        DataSourceDto dto = new DataSourceDto();
        dto.setSourceType("sqlite");
        // 使用内存数据库进行测试
        dto.setSourceConfig("{\"driverName\":\"org.sqlite.JDBC\",\"jdbcUrl\":\"jdbc:sqlite::memory:\"}");
        
        // 测试连接
        try {
            // 创建测试表
            dto.setDynSentence("CREATE TABLE IF NOT EXISTS test_table (id INTEGER PRIMARY KEY, name TEXT, value INTEGER)");
            dataSourceService.execute(dto);
            
            // 插入测试数据
            dto.setDynSentence("INSERT INTO test_table (name, value) VALUES ('test1', 100), ('test2', 200)");
            dataSourceService.execute(dto);
            
            // 查询数据
            dto.setDynSentence("SELECT * FROM test_table");
            List<JSONObject> result = dataSourceService.execute(dto);
            System.out.println("SQLite查询结果: " + result);
            
            assert result.size() == 2 : "应该有2条记录";
            assert "test1".equals(result.get(0).getString("name")) : "第一条记录名称应该是test1";
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("SQLite连接测试失败", e);
        }
    }

    @Test
    public void testSQLiteMetadata(){
        DataSourceDto dto = new DataSourceDto();
        dto.setSourceType("sqlite");
        dto.setSourceConfig("{\"driverName\":\"org.sqlite.JDBC\",\"jdbcUrl\":\"jdbc:sqlite::memory:\"}");
        
        try {
            // 创建测试表
            dto.setDynSentence("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL, email TEXT, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");
            dataSourceService.execute(dto);
            
            dto.setDynSentence("CREATE TABLE IF NOT EXISTS orders (id INTEGER PRIMARY KEY, user_id INTEGER, amount REAL, status TEXT)");
            dataSourceService.execute(dto);
            
            // 测试获取表列表
            List<JSONObject> tables = dataSourceService.getSQLiteTables(dto);
            System.out.println("SQLite表列表: " + tables);
            assert tables.size() >= 2 : "应该至少有2个表";
            
            // 测试获取表字段信息
            List<JSONObject> columns = dataSourceService.getSQLiteTableColumns(dto, "users");
            System.out.println("users表字段信息: " + columns);
            assert columns.size() == 4 : "users表应该有4个字段";
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("SQLite元数据测试失败", e);
        }
    }

}
