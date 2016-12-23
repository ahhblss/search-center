package com.shaw;

import com.shaw.canal.CanalClient;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by shaw on 2016/12/23 0023.
 */
@SpringBootApplication
@ImportResource({"classpath:dubbo-provider.xml"})
public class SearchApplication {
    private static final Logger logger = Logger.getLogger(SearchApplication.class);

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext ctx = SpringApplication.run(SearchApplication.class, args);
        logger.info("Search-Provider-Spring-Boot.startup!");
        CanalClient canalClient = ctx.getBean(CanalClient.class);
        canalClient.init();
        canalClient.start();
    }
}
