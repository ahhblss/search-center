package com.shaw.handler;

/**
 * Created by shaw on 2016/9/14 0014.
 */

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.util.List;

/**
 * DMHY  data 数据处理类
 */
public class DmhyDataHandler extends BaseCanalDataHandler {
    public static final String SCHEMANAME = "test";
    public static final String TABLENAME = "dmhy";
    public static final String HANDLER_KEY = "test_dmhy";


    public DmhyDataHandler() {
        super(DmhyDataHandler.SCHEMANAME, DmhyDataHandler.TABLENAME);
    }

    @Override
    public boolean handlerData(List<CanalEntry.RowData> rwosDatas, CanalEntry.EventType eventType) {
        return false;
    }
}
