package com.shaw.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.shaw.handler.BaseCanalDataHandler;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by shaw on 2016/9/14 0014.
 */
public class CanalClient extends AbstractCanalClient {
    public static String hostname = AddressUtils.getHostIp();
    public static Integer port = 11111;
    public static String destination = "example";
    public static String username = "";
    public static String password = "";

    public CanalClient() {
        super(destination);
    }

    public void init() {
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(hostname, port), destination, username, password);
        this.setConnector(connector);
    }

}
