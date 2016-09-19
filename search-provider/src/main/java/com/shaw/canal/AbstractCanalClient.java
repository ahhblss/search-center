package com.shaw.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.shaw.handler.BaseCanalDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;


/**
 * Created by shaw on 2016/9/19 0019.
 */
public class AbstractCanalClient {

    protected final static Logger logger = LoggerFactory.getLogger(AbstractCanalClient.class);
    protected volatile boolean running = false;
    protected Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread t, Throwable e) {
            logger.error("parse events has an error", e);
        }
    };
    protected Thread thread = null;
    protected CanalConnector connector;
    private Integer batchSize = 1024;
    private Integer stepLong = 2000;
    private Map<String, BaseCanalDataHandler> handlers;
    private String destination;

    public AbstractCanalClient(String destination) {
        this(destination, null);
    }

    public AbstractCanalClient(String destination, CanalConnector connector) {
        this.destination = destination;
        this.connector = connector;
    }

    protected void start() {
        Assert.notNull(connector, "connector is null");
        thread = new Thread(new Runnable() {
            public void run() {
                process();
            }
        });

        thread.setUncaughtExceptionHandler(handler);
        thread.start();
        running = true;
    }

    protected void destory() {
        if (!running) {
            return;
        }
        running = false;
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // ignore
            }
        }

        MDC.remove("destination");
    }

    protected void process() {
        while (running) {
            try {
                MDC.put("destination", destination);
                connector.connect();
                connector.subscribe(".*\\..*");
                while (running) {
                    Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                    long batchId = message.getId();
                    int size = message.getEntries().size();
                    if (batchId == -1 || size == 0) {
                        try {
                            //设置每2秒向 canal instance 拉取一次数据
                            Thread.sleep(stepLong);
                        } catch (InterruptedException e) {
                            logger.error("canal Client Thread Exception :" + e.getMessage());
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
            } catch (Exception e) {
                logger.error("process error!", e);
            } finally {
                connector.disconnect();
                MDC.remove("destination");
            }
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
                //通过schemaName 和tableName获取对应处理handler
                BaseCanalDataHandler handler = handlers.get(getHandlerKey(schemaName, tableName));
                if (handler != null && handler.matchHandler(schemaName, tableName)) {
                    if (handler.handlerData(rowChage.getRowDatasList(), eventType)) {
                        System.out.println(("handler success:" + String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",
                                entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                                entry.getHeader().getSchemaName(), entry.getHeader().getTableName(), eventType)));
                        continue;
                    } else {
                        throw new RuntimeException("handler data fail:" + (String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",
                                entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                                entry.getHeader().getSchemaName(), entry.getHeader().getTableName(), eventType)));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("deal Entrys fail:" + e.getMessage());
            return false;
        }
        return true;
    }


    public void setConnector(CanalConnector connector) {
        this.connector = connector;
    }

    private String getHandlerKey(String schemeName, String tableName) {
        return String.format("%s_%s", schemeName, tableName);
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Integer getStepLong() {
        return stepLong;
    }

    public void setStepLong(Integer stepLong) {
        this.stepLong = stepLong;
    }

    public Map<String, BaseCanalDataHandler> getHandlers() {
        return handlers;
    }

    public void setHandlers(Map<String, BaseCanalDataHandler> handlers) {
        this.handlers = handlers;
    }

    public static Logger getLogger() {
        return logger;
    }
}
