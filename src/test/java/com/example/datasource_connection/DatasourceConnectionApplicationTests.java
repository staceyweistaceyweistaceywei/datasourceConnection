package com.example.datasource_connection;

import com.example.datasource_connection.com.service.ConnectDataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

import static org.springframework.test.context.transaction.TestTransaction.start;
import static sun.misc.PostVMInitHook.run;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DatasourceConnectionApplicationTests {

	@Autowired
	ConnectDataService connectDataService;

	@Test
	public void contextLoads() {

		CountDownLatch latch = new CountDownLatch(8);
        System.out.println("主线程开始了");
		for (int i = 0; i < 8 ; i++) {
        Thread t = new Thread(){
                public void run() {
                    try {
                        latch.countDown();
                        System.out.println(Thread.currentThread().getName()+"准备就绪");
                        latch.await();
                        connectDataService.testInsertPerson(Thread.currentThread().getName());
                        System.out.println(Thread.currentThread().getName()+"执行完毕");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
		}

        try {
            System.out.println("主线程睡眠8秒");
            Thread.sleep(8000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("主线程："+Thread.currentThread().getName()+", 是否活着："+Thread.currentThread().isAlive());
	}

}
