package com.shaw;

import com.shaw.canal.CanalClient;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import java.util.concurrent.CountDownLatch;

/**
 * Created by shaw on 2016/12/23 0023.
 */
@SpringBootApplication
@ImportResource({"classpath:dubbo-provider.xml"})
public class SearchApplication {
    private static final Logger logger = Logger.getLogger(SearchApplication.class);

    @Bean
    public CountDownLatch closeLatch() {
        return new CountDownLatch(1);
    }


    public static void main(String[] args) throws InterruptedException {
        ApplicationContext ctx = SpringApplication.run(SearchApplication.class, args);
        logger.info("Search-Provider-Spring-Boot.startup!");
        CanalClient canalClient = ctx.getBean(CanalClient.class);
        canalClient.init();
        canalClient.start();
//        CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
    }
}
