package com.anjiplus.template.gaea.business.modules.datasource.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLite独立测试类，不依赖Spring上下文
 */
public class SQLiteStandaloneTest {

    @Test
    public void testSQLiteJDBCConnection() throws SQLException, ClassNotFoundException {
        // 加载SQLite驱动
        Class.forName("org.sqlite.JDBC");
        
        // 创建内存数据库连接
        String url = "jdbc:sqlite::memory:";
        Connection connection = DriverManager.getConnection(url);
        
        Assertions.assertNotNull(connection, "SQLite连接不应为空");
        Assertions.assertTrue(connection.isValid(2), "SQLite连接应该有效");
        
        // 创建测试表
        String createTableSQL = "CREATE TABLE IF NOT EXISTS test_table (id INTEGER PRIMARY KEY, name TEXT, value INTEGER)";
        PreparedStatement createStmt = connection.prepareStatement(createTableSQL);
        createStmt.execute();
        createStmt.close();
        
        // 插入测试数据
        String insertSQL = "INSERT INTO test_table (name, value) VALUES (?, ?)";
        PreparedStatement insertStmt = connection.prepareStatement(insertSQL);
        insertStmt.setString(1, "test1");
        insertStmt.setInt(2, 100);
        insertStmt.executeUpdate();
        
        insertStmt.setString(1, "test2");
        insertStmt.setInt(2, 200);
        insertStmt.executeUpdate();
        insertStmt.close();
        
        // 查询数据
        String selectSQL = "SELECT * FROM test_table";
        PreparedStatement selectStmt = connection.prepareStatement(selectSQL);
        ResultSet rs = selectStmt.executeQuery();
        
        List<JSONObject> results = new ArrayList<>();
        while (rs.next()) {
            JSONObject row = new JSONObject();
            row.put("id", rs.getInt("id"));
            row.put("name", rs.getString("name"));
            row.put("value", rs.getInt("value"));
            results.add(row);
        }
        
        rs.close();
        selectStmt.close();
        
        Assertions.assertEquals(2, results.size(), "应该有2条记录");
        Assertions.assertEquals("test1", results.get(0).getString("name"), "第一条记录名称应该是test1");
        Assertions.assertEquals(100, results.get(0).getIntValue("value"), "第一条记录值应该是100");
        
        connection.close();
        
        System.out.println("SQLite JDBC连接测试成功！");
        System.out.println("查询结果: " + results);
    }

    @Test
    public void testSQLiteMetadataQueries() throws SQLException, ClassNotFoundException {
        // 加载SQLite驱动
        Class.forName("org.sqlite.JDBC");
        
        // 创建内存数据库连接
        String url = "jdbc:sqlite::memory:";
        Connection connection = DriverManager.getConnection(url);
        
        // 创建测试表
        String createUsersSQL = "CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL, email TEXT, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)";
        PreparedStatement createUsersStmt = connection.prepareStatement(createUsersSQL);
        createUsersStmt.execute();
        createUsersStmt.close();
        
        String createOrdersSQL = "CREATE TABLE orders (id INTEGER PRIMARY KEY, user_id INTEGER, amount REAL, status TEXT)";
        PreparedStatement createOrdersStmt = connection.prepareStatement(createOrdersSQL);
        createOrdersStmt.execute();
        createOrdersStmt.close();
        
        // 测试获取表列表
        String getTablesSQL = "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'";
        PreparedStatement getTablesStmt = connection.prepareStatement(getTablesSQL);
        ResultSet tablesRs = getTablesStmt.executeQuery();
        
        List<String> tables = new ArrayList<>();
        while (tablesRs.next()) {
            tables.add(tablesRs.getString("name"));
        }
        tablesRs.close();
        getTablesStmt.close();
        
        Assertions.assertEquals(2, tables.size(), "应该有2个表");
        Assertions.assertTrue(tables.contains("users"), "应该包含users表");
        Assertions.assertTrue(tables.contains("orders"), "应该包含orders表");
        
        // 测试获取表字段信息
        String getColumnsSQL = "PRAGMA table_info(users)";
        PreparedStatement getColumnsStmt = connection.prepareStatement(getColumnsSQL);
        ResultSet columnsRs = getColumnsStmt.executeQuery();
        
        List<JSONObject> columns = new ArrayList<>();
        while (columnsRs.next()) {
            JSONObject column = new JSONObject();
            column.put("columnName", columnsRs.getString("name"));
            column.put("dataType", columnsRs.getString("type"));
            column.put("nullable", columnsRs.getInt("notnull") == 0 ? "YES" : "NO");
            column.put("isPrimaryKey", columnsRs.getInt("pk") > 0 ? "YES" : "NO");
            columns.add(column);
        }
        columnsRs.close();
        getColumnsStmt.close();
        
        Assertions.assertEquals(4, columns.size(), "users表应该有4个字段");
        
        // 验证字段信息
        JSONObject idColumn = columns.stream()
            .filter(col -> "id".equals(col.getString("columnName")))
            .findFirst()
            .orElse(null);
        Assertions.assertNotNull(idColumn, "应该有id字段");
        Assertions.assertEquals("YES", idColumn.getString("isPrimaryKey"), "id字段应该是主键");
        
        connection.close();
        
        System.out.println("SQLite元数据查询测试成功！");
        System.out.println("表列表: " + tables);
        System.out.println("users表字段: " + columns);
    }
}