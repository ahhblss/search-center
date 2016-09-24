package com.shaw.lucene;

import com.alibaba.fastjson.JSONObject;
import com.shaw.constant.Constants;
import com.shaw.utils.TimeUtils;
import com.shaw.vo.DmhyDataVo;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DmhyDataIndex {
    public static final String DEFAULT_PATH = "/search-center/anime";
    private Directory dir;
    private Analyzer analyzer;

    public DmhyDataIndex() throws Exception {
        this.analyzer = new SmartChineseAnalyzer();
        this.dir = FSDirectory.open(Paths.get(DEFAULT_PATH));
    }

    public IndexWriter getWriter() throws Exception {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        return new IndexWriter(dir, config);
    }

    public IndexReader getReader() throws Exception {
        return DirectoryReader.open(dir);
    }

    /**
     * Indexed, not tokenized, omits norms, indexes none, stored.
     */
    public static final FieldType TYPE_NOT_INDEX_STORED = new FieldType();
    public static final FieldType TIME_TYPE = new FieldType();

    static {
        TYPE_NOT_INDEX_STORED.setOmitNorms(true);
        TYPE_NOT_INDEX_STORED.setIndexOptions(IndexOptions.NONE);
        TYPE_NOT_INDEX_STORED.setTokenized(false);
        TYPE_NOT_INDEX_STORED.setStored(true);
        TYPE_NOT_INDEX_STORED.freeze();

        TIME_TYPE.setTokenized(true);
        TIME_TYPE.setOmitNorms(true);
        TIME_TYPE.setStored(false);
        TIME_TYPE.setIndexOptions(IndexOptions.DOCS);
        TIME_TYPE.setNumericType(FieldType.NumericType.LONG);
        TIME_TYPE.setDocValuesType(DocValuesType.NUMERIC);
        TIME_TYPE.freeze();
    }

    /**
     * 依据具体需求，判断字段是否需要索引或存储。 很多字段可以只索引不存储 title classi 用于搜索 索引建立。 time 用于关联时间排序。
     **/
    public void addIndex(DmhyDataVo data) throws Exception {
        IndexWriter writer = getWriter();
        try {
            Document doc = new Document();
            // 索引字段
            doc.add(new TextField("title", data.getTitle(), Field.Store.NO));
            doc.add(new LongField("time", TimeUtils.formatDate(data.getTime(), "yyyy/MM/dd HH:mm").getTime(),
                    TIME_TYPE));
            doc.add(new StringField("id", data.getId().toString(), Field.Store.YES));
            // 存储，但是不做索引
            String luceneData = JSONObject.toJSONString(data);
            doc.add(new Field("data", luceneData, TYPE_NOT_INDEX_STORED));
            writer.addDocument(doc);
        } finally {
            writer.close();
        }
    }

    public void addIndexList(List<DmhyDataVo> dataList) throws Exception {
        IndexWriter writer = getWriter();
        try {
            for (DmhyDataVo data : dataList) {
                Document doc = new Document();
                // 索引字段
                doc.add(new TextField("title", data.getTitle(), Field.Store.NO));
                doc.add(new LongField("time", TimeUtils.formatDate(data.getTime(), "yyyy/MM/dd HH:mm").getTime(),
                        TIME_TYPE));
                doc.add(new StringField("id", data.getId().toString(), Field.Store.YES));
                // 存储，但是不做索引
                String luceneData = JSONObject.toJSONString(data);
                doc.add(new Field("data", luceneData, TYPE_NOT_INDEX_STORED));
                writer.addDocument(doc);
            }
        } finally {
            writer.close();
        }
    }

    public List<DmhyDataVo> searchAnime(String keyword, Integer searchNum) throws Exception {
        if (searchNum == null) {
            searchNum = Constants.DEAFULT_SEARCH_NUM;
        }
        IndexReader reader = getReader();
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser titleParser = new QueryParser("title", this.analyzer);
        Query titleQuery = titleParser.parse(keyword);
        // 按时间和 title 关联度排序 关联度优先
        Sort sort = new Sort(new SortField("title", Type.SCORE), new SortField("time", Type.LONG, true));
        TopDocs hits = searcher.search(titleQuery, searchNum, sort);
        List<DmhyDataVo> datas = new ArrayList<DmhyDataVo>();
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String dataStr = doc.get("data");
            DmhyDataVo dmhyData = JSONObject.parseObject(dataStr, DmhyDataVo.class);
            datas.add(dmhyData);
        }
        return datas;

    }

    public void updateIndex(DmhyDataVo data) throws Exception {
        IndexWriter writer = getWriter();
        try {
            Document doc = new Document();
            // 索引字段 title 用于搜索 ，time 用于搜索排序，id用于维护索引
            doc.add(new TextField("title", data.getTitle(), Field.Store.NO));
            doc.add(new LongField("time", TimeUtils.formatDate(data.getTime(), "yyyy/MM/dd HH:mm").getTime(),
                    TIME_TYPE));
            doc.add(new StringField("id", data.getId().toString(), Field.Store.YES));
            // 存储，但是不做索引
            String luceneData = JSONObject.toJSONString(data);
            doc.add(new Field("data", luceneData, TYPE_NOT_INDEX_STORED));
            writer.updateDocument(new Term("id", data.getId().toString()), doc);
        } finally {
            writer.close();
        }
    }

    public void updateIndexList(List<DmhyDataVo> dataList) throws Exception {
        IndexWriter writer = getWriter();
        try {
            for (DmhyDataVo data : dataList) {
                Document doc = new Document();
                // 索引字段
                doc.add(new TextField("title", data.getTitle(), Field.Store.NO));
                doc.add(new LongField("time", TimeUtils.formatDate(data.getTime(), "yyyy/MM/dd HH:mm").getTime(),
                        TIME_TYPE));
                doc.add(new StringField("id", data.getId().toString(), Field.Store.YES));
                // 存储，但是不做索引
                String luceneData = JSONObject.toJSONString(data);
                doc.add(new Field("data", luceneData, TYPE_NOT_INDEX_STORED));
                writer.updateDocument(new Term("id", data.getId().toString()), doc);
            }
        } finally {
            writer.close();
        }
    }

    public void deleteIndex(String id) throws Exception {
        IndexWriter writer = getWriter();
        try {
            writer.deleteDocuments(new Term("id", id));
            writer.forceMergeDeletes();
            writer.commit();
        } finally {
            writer.close();
        }
    }

    public void batchDeleteIndex(List<String> ids) throws Exception {
        IndexWriter writer = getWriter();
        try {
            for (String id : ids) {
                writer.deleteDocuments(new Term("id", id));
                writer.forceMergeDeletes();
            }
            writer.commit();
        } finally {
            writer.close();
        }
    }

}
