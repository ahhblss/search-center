package com.shaw;

import com.shaw.canal.CanalClient;
import com.shaw.handler.BaseCanalDataHandler;
import com.shaw.handler.BlogDataHandler;
import com.shaw.lucene.BlogIndex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/23 0023.
 */
@Configuration
public class BeanConfig {
    @Value("${canal.destination}")
    private String canalDestination;
    private BlogIndex blogIndex;
    private BlogDataHandler blogDataHandler;

    @Bean
    public BlogIndex blogIndex() throws Exception {
        this.blogIndex = new BlogIndex();
        return blogIndex;
    }

    @Bean
    public BlogDataHandler blogDataHandler() throws Exception {
        blogDataHandler = new BlogDataHandler();
        return blogDataHandler;
    }

    @Bean
    public CanalClient canalClient() throws Exception {
        //创建canal客户端
        CanalClient canalClient = new CanalClient(canalDestination);
        //设置binlog数据处理handlers
        Map<String, BaseCanalDataHandler> handlerMap = new HashMap<>();
        handlerMap.put(BlogDataHandler.HANDLER_KEY, blogDataHandler);
        canalClient.setDataHandlerMap(handlerMap);
        return canalClient;
    }

}
