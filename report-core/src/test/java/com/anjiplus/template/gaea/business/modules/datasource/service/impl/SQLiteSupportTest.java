package com.anjiplus.template.gaea.business.modules.datasource.service.impl;

import com.anjiplus.template.gaea.business.modules.datasource.controller.dto.DataSourceDto;
import com.anjiplus.template.gaea.business.util.JdbcConstants;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.Assert.assertNotNull;

public class SQLiteSupportTest {

    @Test
    public void testSQLiteMemoryConnection() throws Exception {
        // Simple sanity check for sqlite driver presence
        Class.forName(JdbcConstants.SQLITE_DRIVER);
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        assertNotNull(conn);
        conn.close();
    }
}

