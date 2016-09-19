package com.shaw.handler;

/**
 * Created by shaw on 2016/9/14 0014.
 */

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.shaw.lucene.BlogIndex;
import com.shaw.utils.TimeUtils;
import com.shaw.vo.BlogVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Blog 数据处理类
 */
public class BlogDataHandler extends BaseCanalDataHandler<BlogVo> {
    public static final String SCHEMANAME = "db_blog";
    public static final String TABLENAME = "t_blog";
    public static final String HANDLER_KEY = "db_blog_t_blog";
    private Logger blogHandlerLogger = LoggerFactory.getLogger(this.getClass());

    public BlogDataHandler() {
        super(BlogDataHandler.SCHEMANAME, BlogDataHandler.TABLENAME);
    }

    @Autowired
    private BlogIndex blogIndex;

    @Override
    public boolean handlerData(List<CanalEntry.RowData> rowsDatas, CanalEntry.EventType eventType) {
        try {
            if (eventType == CanalEntry.EventType.DELETE) { //过滤删除操作
                blogIndex.batchDeleteIndex(getBeforeListIds(rowsDatas, BlogVo.ID_FIELDNAME));
            } else if (eventType == CanalEntry.EventType.INSERT) { //过滤插入操作
                List<BlogVo> insertList = convertToVoList(rowsDatas, false);
                blogIndex.addBlogListIndex(insertList);
            } else if (eventType == CanalEntry.EventType.UPDATE) { //过滤更新操作
                List<BlogVo> updateList = convertToVoList(rowsDatas, false);
                blogIndex.updateBlogListIndex(updateList);
            } else {
                //其他操作，不不做处理
            }
        } catch (Exception e) {
            blogHandlerLogger.error("handler data fail:" + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public BlogVo convertToVo(List<CanalEntry.Column> columns) {
        BlogVo vo = new BlogVo();
        for (CanalEntry.Column column : columns) {
            if (column.getName() != null)
                switch (column.getName()) {
                    case BlogVo.ID_FIELDNAME:
                        vo.setId(Integer.valueOf(column.getValue()));
                        break;
                    case BlogVo.TITLE_FIELDNAME:
                        vo.setTitle(column.getValue());
                        break;
                    case BlogVo.SUMMARY_FIELDNAME:
                        vo.setSummary(column.getValue());
                        break;
                    case BlogVo.RELEASEDATE_FIELDNAME:
                        vo.setReleaseDate(TimeUtils.formatDate(column.getValue(), "yyyy-MM-dd HH:mm:ss"));
                        break;
                    case BlogVo.CLICKHIT_FIELDNAME:
                        vo.setClickHit(Integer.valueOf(column.getValue()));
                        break;
                    case BlogVo.CONTENT_FIELDNAME:
                        vo.setContent(column.getValue());
                        break;
                    case BlogVo.TYPEID_FIELDNAME:
                        vo.setTypeId(Integer.valueOf(column.getValue()));
                        break;
                    case BlogVo.KEYWORD_FIELDNAME:
                        vo.setKeyWord(column.getValue());
                }
        }
        return vo;
    }
}
