package com.shaw.canal;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by tuanzi on 2016/9/19 0019.
 */
public class StartCanalClientListener implements ApplicationListener<ContextRefreshedEvent> {


    //容器启动完毕，运行cannalClient线程
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //spring 容器初始化后运行canal client  springmvc 容器初始化无视
        if (event.getApplicationContext().getDisplayName().equals("Root WebApplicationContext")) {
            final CanalClient canalClient = (CanalClient) event.getApplicationContext().getBean("canalClient");
            canalClient.init();
            canalClient.start();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        canalClient.getLogger().info("## stop the canal client");
                        canalClient.destory();
                    } catch (Throwable e) {
                        canalClient.getLogger().warn("##something goes wrong when stopping canal:\n{}", ExceptionUtils.getFullStackTrace(e));
                    } finally {
                        canalClient.getLogger().info("## canal client is down.");
                    }
                }
            });
        }

    }
}
