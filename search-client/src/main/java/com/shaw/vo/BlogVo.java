package com.shaw.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 博客实体
 *
 * @author shaw
 */
public class BlogVo implements Serializable {

    private static final long serialVersionUID = 4186161649311309864L;

    private Integer id; // 编号
    private String title; // 博客标题
    private String summary; // 摘要
    private Date releaseDate; // 发布日期
    private Integer clickHit; // 查看次数
    private String content; // 博客内容
    private String contentNoTag; // 博客内容 无网页标签 Lucene分词用
    private Integer typeId; // 博客类型
    private String keyWord; // 关键字 空格隔开
    private String releaseDateStr;

    public String getReleaseDateStr() {
        return releaseDateStr;
    }

    public void setReleaseDateStr(String releaseDateStr) {
        this.releaseDateStr = releaseDateStr;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getClickHit() {
        return clickHit;
    }

    public void setClickHit(Integer clickHit) {
        this.clickHit = clickHit;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentNoTag() {
        return contentNoTag;
    }

    public void setContentNoTag(String contentNoTag) {
        this.contentNoTag = contentNoTag;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    //对应数据库字段名
    public static final String ID_FIELDNAME = "id";
    public static final String SUMMARY_FIELDNAME = "summary";
    public static final String TITLE_FIELDNAME = "title";
    public static final String RELEASEDATE_FIELDNAME = "releaseDate";
    public static final String CLICKHIT_FIELDNAME = "clickHit";
    public static final String CONTENT_FIELDNAME = "content";
    public static final String TYPEID_FIELDNAME = "typeId";
    public static final String KEYWORD_FIELDNAME = "keyWord";
}
