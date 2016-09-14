package com.shaw.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by shaw on 2016/9/14 0014.
 */
public class CanalClient {
    private String hostname = AddressUtils.getHostIp();
    private Integer port = 11111;
    private String destination = "example";
    private String username = "";
    private String password = "";
    private Integer batchSize = 1000;

    //启动canal 客户端 监控 mysql binlog 日志
    public void start() {
        // 创建连接canal服务
        CanalConnector connector = CanalConnectors
                .newSingleConnector(new InetSocketAddress(hostname, port), destination, username, password);
        int emptyCount = 0;
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

            int totalEmptyCount = 1200;
            // 获取空数据达1200之后停止监控
            while (emptyCount < totalEmptyCount) {
                // 获取指定数量的数据
                Message message = connector.getWithoutAck(batchSize);
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
                    System.out.println("empty count : " + emptyCount);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    // 正在获取数据，充值emptyCount
                    emptyCount = 0;
                    printEntry(message.getEntries());
                }
                connector.ack(batchId); // 处理数据成功，提交确认
                // connector.rollback(batchId); // 处理失败, 回滚数据
            }
        } finally {
            connector.disconnect();
        }
    }

    //。更新 搜索索引
    private static void printEntry(List<CanalEntry.Entry> entrys) {
        //每个Entry包含一个表的 数据变化 及相关信息。不同表的记录会分为多个Entry
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
            System.out.println(String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(), eventType));

            for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == CanalEntry.EventType.DELETE) {
                    printColumn(rowData.getBeforeColumnsList());
                } else if (eventType == CanalEntry.EventType.INSERT) {
                    printColumn(rowData.getAfterColumnsList());
                } else {
                    System.out.println("-------> before");
                    printColumn(rowData.getBeforeColumnsList());
                    System.out.println("-------> after");
                    printColumn(rowData.getAfterColumnsList());
                }
            }
        }
    }

    // 遍历每个字段的数据变换
    private static void printColumn(List<CanalEntry.Column> columns) {
        for (CanalEntry.Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }

}
