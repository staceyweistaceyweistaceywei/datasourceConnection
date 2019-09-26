package com.example.datasource_connection.com.service;

import com.example.datasource_connection.com.common.DatabaseConnectConfigrarution;
import com.example.datasource_connection.com.common.DatasourceConnectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * created by wyx on 2019/9/25
 */
@Service
public class ConnectDataService {

    @Autowired
    DatabaseConnectConfigrarution databaseConnectConfigrarution;

    DatasourceConnectPool pool;

    public void testInsertPerson(String threadName) {
        pool = databaseConnectConfigrarution.getDataBaseConnectionPool();
        Connection con =pool.getConnection(threadName);
        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate("insert into person (name, age) VALUES('花花', 20)");
            System.out.println(threadName+"插入一条数据");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.releaseConnection(con);
        }
    }
}
