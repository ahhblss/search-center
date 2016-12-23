package com.shaw.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;

import java.net.InetSocketAddress;

public class CanalClient extends AbstractCanalClient {
    @Value("${canal.hostname}")
    private String hostname;
    @Value("${canal.port}")
    private Integer port;
    @Value("${canal.username}")
    private String username;
    @Value("${canal.password}")
    private String password;

    public CanalClient(String destination) {
        super(destination);
    }

    public void init() {
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(hostname, port), destination, username, password);
        this.setConnector(connector);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    getLogger().info("## stop the canal client");
                    destory();
                } catch (Throwable e) {
                    getLogger().warn("##something goes wrong when stopping canal:\n{}", ExceptionUtils.getFullStackTrace(e));
                } finally {
                    getLogger().info("## canal client is down.");
                }
            }
        });
    }

    public void start() {
        super.start();
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
