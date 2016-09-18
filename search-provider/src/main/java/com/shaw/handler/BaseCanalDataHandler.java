package com.shaw.handler;

import com.alibaba.otter.canal.protocol.CanalEntry;

/**
 * Created by Administrator on 2016/9/18 0018.
 */
public abstract class BaseCanalDataHandler {
    protected String schemeName;
    protected String tableName;

    public boolean matchHandler(String schemaName, String tableName) {
        if (this.schemeName.equals(schemaName) && this.tableName.equals(tableName)) {
            return true;
        } else {
            return true;
        }
    }

    public BaseCanalDataHandler(String schemeName, String tableName) {
        this.schemeName = schemeName;
        this.tableName = tableName;
    }


    public abstract boolean handlerData(java.util.List<com.alibaba.otter.canal.protocol.CanalEntry.RowData> rowsDatas, CanalEntry.EventType eventType);

}
