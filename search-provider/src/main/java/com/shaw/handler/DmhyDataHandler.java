package com.shaw.handler;

/**
 * Created by shaw on 2016/9/14 0014.
 */

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.shaw.lucene.DmhyDataIndex;
import com.shaw.vo.DmhyDataVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DMHY  data 数据处理类
 */
public class DmhyDataHandler extends BaseCanalDataHandler<DmhyDataVo> {
    public static final String SCHEMANAME = "test";
    public static final String TABLENAME = "dmhy";
    public static final String HANDLER_KEY = "test_dmhy";
    public Logger dmhyDataHandlerLogger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DmhyDataIndex dmhyDataIndex;


    public DmhyDataHandler() {
        super(DmhyDataHandler.SCHEMANAME, DmhyDataHandler.TABLENAME);
    }

    @Override
    public boolean handlerData(List<CanalEntry.RowData> rowsDatas, CanalEntry.EventType eventType) {
        try {
            if (eventType == CanalEntry.EventType.DELETE) { //过滤删除操作
                dmhyDataIndex.batchDeleteIndex(getBeforeListIds(rowsDatas, DmhyDataVo.ID_FIELDNAME));
            } else if (eventType == CanalEntry.EventType.INSERT) { //过滤插入操作
                List<DmhyDataVo> insertList = convertToVoList(rowsDatas, false);
                dmhyDataIndex.addIndexList(insertList);
            } else if (eventType == CanalEntry.EventType.UPDATE) { //过滤更新操作
                List<DmhyDataVo> updateList = convertToVoList(rowsDatas, false);
                dmhyDataIndex.updateIndexList(updateList);
            } else {
                //其他操作，不不做处理
            }
            System.out.println("deal size:"+rowsDatas.size());
        } catch (Exception e) {
            dmhyDataHandlerLogger.error("handler data fail:" + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public DmhyDataVo convertToVo(List<CanalEntry.Column> columns) {
        Map<String, String> columnMap = new HashMap<String, String>();
        for (CanalEntry.Column col : columns) {
            columnMap.put(col.getName(), StringUtils.isBlank(col.getValue()) ? "" : col.getValue());
        }
        DmhyDataVo vo = new DmhyDataVo();
        vo.setId(Integer.valueOf(columnMap.get(DmhyDataVo.ID_FIELDNAME)));
        vo.setTime(columnMap.get(DmhyDataVo.TIME_FIELDNAME));
        vo.setClassi(columnMap.get(DmhyDataVo.CLASSI_FIELDNAME));
        vo.setTitle(columnMap.get(DmhyDataVo.TITLE_FIELDNAME));
        vo.setMagnetLink(columnMap.get(DmhyDataVo.MAGNETLINK_FIELDNAME));
        vo.setSize(columnMap.get(DmhyDataVo.SIZE_FIELDNAME));
        vo.setDownNum(parseIntegerValue(columnMap.get(DmhyDataVo.DOWNNUM_FIELDNAME), 0));
        vo.setComNum(parseIntegerValue(columnMap.get(DmhyDataVo.COMNUM_FIELDNAME), 0));
        vo.setSeedNum(parseIntegerValue(columnMap.get(DmhyDataVo.SEEDNUM_FIELDNAME), 0));
        vo.setPublisher(columnMap.get(DmhyDataVo.PUBLISHER_FIELDNAME));
        vo.setCreateTime(Long.valueOf(columnMap.get(DmhyDataVo.CREATETIME_FIELDNAME)));
        return vo;
    }


}
