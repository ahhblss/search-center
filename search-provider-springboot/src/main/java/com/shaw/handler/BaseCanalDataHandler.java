package com.shaw.handler;

import com.alibaba.otter.canal.protocol.CanalEntry;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaw on
 * canal数据基本操作类
 */
public abstract class BaseCanalDataHandler<T> {
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


    public abstract boolean handlerData(List<CanalEntry.RowData> rowsDatas, CanalEntry.EventType eventType);

    public abstract T convertToVo(List<CanalEntry.Column> columns);

    public List<T> convertToVoList(List<CanalEntry.RowData> rowsDatas, boolean isBefore) {
        List<T> vos = new ArrayList<T>();
        for (CanalEntry.RowData columns : rowsDatas) {
            T vo;
            if (isBefore) {
                vo = this.convertToVo(columns.getBeforeColumnsList());
            } else {
                vo = convertToVo(columns.getAfterColumnsList());
            }
            vos.add(vo);
        }
        return vos;
    }

    public Integer parseIntegerValue(String value, Integer defaultValue) {
        try {
            if (StringUtils.isBlank(value)) {
                return 0;
            } else {
                return Integer.valueOf(value);
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public List<String> getBeforeListIds(List<CanalEntry.RowData> rowsDatas, String idFieldName) {
        List<String> ids = new ArrayList<String>();
        //只有删除操作需要ids,且删除操作只有 before List
        for (CanalEntry.RowData columns : rowsDatas) {
            for (CanalEntry.Column column : columns.getBeforeColumnsList()) {
                if (idFieldName.equals(column.getName())) {
                    ids.add(column.getValue());
                }
            }
        }
        return ids;
    }


}
