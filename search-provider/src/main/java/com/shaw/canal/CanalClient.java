package com.shaw.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import org.springframework.beans.factory.annotation.Value;

import java.net.InetSocketAddress;

/**
 * Created by shaw on 2016/9/14 0014.
 */
public class CanalClient extends AbstractCanalClient {
    @Value("#{config['canal.hostname']}")
    private String hostname;
    @Value("#{config['canal.port']}")
    private Integer port;
    @Value("#{config['canal.username']}")
    private String username;
    @Value("#{config['canal.password']}")
    private String password;

    public CanalClient(String destination) {
        super(destination);
    }

    public void init() {
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(hostname, port), destination, username, password);
        this.setConnector(connector);
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
