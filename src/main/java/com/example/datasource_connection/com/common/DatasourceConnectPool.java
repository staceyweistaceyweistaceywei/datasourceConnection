package com.example.datasource_connection.com.common;

import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * created by wyx on 2019/9/25
 */
public  class DatasourceConnectPool implements DatasourceConnectPoolInterface {

    /**
     * 连接池数量
     */
    private  int maxConnectNum;
    /**
     * 当前连接数
     */
    private AtomicInteger currentConnectNum = new AtomicInteger(0);

    /**
     * 数据库驱动
     */
    private String driver;

    /**
     * 数据库地址
     */
    private String url;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户名
     */
    private String password;

    /**
     * 闲置连接
     */
    private LinkedBlockingQueue<Connection> idleConnect = new LinkedBlockingQueue<Connection>();
    /**
     * 使用的连接
     */
    private LinkedBlockingQueue<Connection> busyConnect = new LinkedBlockingQueue<Connection>();


    /**
     * 连接池
     */
    public volatile static DatasourceConnectPool pool;

    public DatasourceConnectPool(int maxConnectNum, String driver, String url, String userName, String password) {
      this.maxConnectNum = maxConnectNum;
      this.driver = driver;
      this.url = url;
      this.userName = userName;
      this.password = password;
      this.init();
    }

    public DatasourceConnectPool() {

    }


    /**
     * 获取连接池
     * @return
     */
    public static DatasourceConnectPool getDatasourceConnectPool(int maxConnectNum, String driver, String url, String userName, String password) {
        if (pool == null) {
            synchronized (DatasourceConnectPool.class) {
                if (pool == null) {
                    pool = new DatasourceConnectPool(maxConnectNum, driver, url, userName, password);
                }
            }
        }
        return pool;
    }

    /**
     * 加载驱动
     */
    private  void init() {
        try {
            Class.forName(this.driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新建连接
     */
    public Connection  createConnection(){
        Connection con = null;
        try {
            con = DriverManager.getConnection(this.url,this.userName,this.password);
        }  catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    /**
     * 获取连接
     * @return
     */
    @Override
    public Connection getConnection(String threadName){
        Connection con = null;

         //        1.第一次请求，判断是否有空闲连接，有返回
        if (null != idleConnect && idleConnect.size() > 0) {
            con = idleConnect.poll();
            if (null != con) {
                try {
                    busyConnect.put(con);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(threadName+"从idleConnect取连接,当前连接数是"+currentConnectNum.get());
                return con;
            }
        }

            //       2. 没有空闲连接，检查连接池中连接数量是否到达最大数量，没有，创建连接
            if (currentConnectNum.get() < maxConnectNum) {
                System.out.println(threadName+",当前连接数是"+currentConnectNum.get());
                if (currentConnectNum.incrementAndGet() < maxConnectNum) {
                    con = createConnection();
                    System.out.println(threadName+",--当前连接数是"+currentConnectNum.get());
                    try {
                        busyConnect.put(con);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(threadName+"创建新连接,当前连接数是"+currentConnectNum.get());
                    return con;
                }

            }
            //     3.连接池数量达到最大数，线程等待得到连接
        try {
            con = idleConnect.poll(1000, TimeUnit.MILLISECONDS);
            if (null == con) {
                throw new RuntimeException("等待超时");
            } else {
                System.out.println(threadName+"等待得到连接,当前连接数是"+currentConnectNum.get());
                return con;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return con;
    }

    /**
     * 释放连接
     */
    @Override
    public void releaseConnection (Connection con){
       busyConnect.remove(con);
       idleConnect.offer(con);
    }

    /**
     * 销毁
     */
    @Override
    public void destroy(){
        busyConnect = null;
        idleConnect = null;
    }

    //5：定时器，去检查空闲中的连接是否可用，不可用的话就换掉
    @Scheduled(fixedRate = 60*1000)//定时器，每分钟检测一次
    public void check() throws SQLException {
        Iterator it=idleConnect.iterator();
        while (it.hasNext()){
            Connection conn= (Connection) it.next();
            boolean b=conn.isValid(2000);
            if(!b){
                idleConnect.remove(conn);
                idleConnect.offer(this.createConnection());
            }
        }
    }
}
