package com.example.datasource_connection.com.common;

import java.sql.Connection;

/**
 * created by wyx on 2019/9/25
 */
public interface DatasourceConnectPoolInterface {

     /**
      * 参数threadName是为了方便检查是哪个线程获取连接
      * @param threadName
      * @return
      */
     Connection getConnection(String threadName);

     void releaseConnection(Connection con);

     void destroy();

}
