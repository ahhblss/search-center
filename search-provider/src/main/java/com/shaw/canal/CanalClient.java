package com.shaw.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.shaw.handler.BaseCanalDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by shaw on 2016/9/14 0014.
 */
public class CanalClient {
    private Logger canalLog = LoggerFactory.getLogger(this.getClass());
    private String hostname = AddressUtils.getHostIp();
    private Integer port = 11111;
    private String destination = "example";
    private String username = "";
    private String password = "";
    private Integer batchSize = 100;
    private Integer stepLong = 2000;

    private Map<String, BaseCanalDataHandler> handlers;

    //启动canal 客户端 监控 mysql binlog 日志
    public void start() {
        // 创建连接canal服务
        CanalConnector connector = CanalConnectors
                .newSingleConnector(new InetSocketAddress(hostname, port), destination, username, password);
        try {
            // 连接 canal服务，分为集群和简单连接。
            connector.connect();
            // 客户端订阅，重复订阅时会更新对应的filter信息
            connector.subscribe(".*\\..*");
            /****
             * 这里是保证数据一致性的关键。避免 因为搜索中心挂掉导致 索引建立异常
             * */
            // 回滚到未进行 的地方，下次fetch的时候，可以从最后一个没有 获取的地方开始拿
            connector.rollback();
            while (true) {
                // 获取指定数量的数据
                Message message = connector.getWithoutAck(batchSize);
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    try {
                        //设置每2秒向 canal instance 拉取一次数据
                        Thread.sleep(stepLong);
                    } catch (InterruptedException e) {
                        canalLog.error("canal Client Thread Exception :" + e.getMessage());
                    }
                } else {
                    if (dealEntrys(message.getEntries())) {
                        // 处理数据成功，提交确认
                        connector.ack(batchId);
                    } else {
                        // 处理失败, 回滚数据
                        connector.rollback(batchId);
                    }
                }
            }
        } finally {
            connector.disconnect();
        }
    }

    //。更新 搜索索引
    private boolean dealEntrys(List<CanalEntry.Entry> entrys) {
        //每个Entry包含一个表的 数据变化 及相关信息。不同表的记录会分为多个Entry
        try {
            for (CanalEntry.Entry entry : entrys) {
                // 过滤事务开始和提交
                if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN
                        || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                    continue;
                }

                CanalEntry.RowChange rowChage = null;
                try {
                    // 获取数据变化情况
                    rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                } catch (Exception e) {
                    throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                            e);
                }
                CanalEntry.EventType eventType = rowChage.getEventType();
                String schemaName = entry.getHeader().getSchemaName();
                String tableName = entry.getHeader().getTableName();
                BaseCanalDataHandler handler = handlers.get(getHandlerKey(schemaName, tableName));
                if (handler != null && handler.matchHandler(schemaName, tableName)) {
                    if (handler.handlerData(rowChage.getRowDatasList(), eventType)) {
                        continue;
                    } else {
                        throw new RuntimeException("handler data fail:" + (String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",
                                entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                                entry.getHeader().getSchemaName(), entry.getHeader().getTableName(), eventType)));
                    }
                }
            }
        } catch (Exception e) {
            canalLog.error("deal Entrys fail:" + e.getMessage());
            return false;
        }
        return true;
    }

    private String getHandlerKey(String schemeName, String tableName) {
        return String.format("%s_%s", schemeName, tableName);
    }
}
