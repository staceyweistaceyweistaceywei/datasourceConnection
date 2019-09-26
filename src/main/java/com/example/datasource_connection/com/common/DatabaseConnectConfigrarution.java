package com.example.datasource_connection.com.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * created by wyx on 2019/9/25
 */
@Configuration
public class DatabaseConnectConfigrarution {

    @Bean
    public DatasourceConnectPool getDataBaseConnectionPool() {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://127.0.0.1:3306/study_data?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true&serverTimezone=UTC";
        String userName = "root";
        String password = "123";
        DatasourceConnectPool pool = DatasourceConnectPool.getDatasourceConnectPool(6, driver, url, userName, password);
//        DatasourceConnectPool pool = new DatasourceConnectPool(6, "com.mysql.cj.jdbc.Driver", "jdbc:mysql://127.0.0.1:3306/study_data", "root", "123");
        return pool;
    }
}
