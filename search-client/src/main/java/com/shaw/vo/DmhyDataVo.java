package com.shaw.vo;

import java.io.Serializable;

public class DmhyDataVo implements Serializable {
    private static final long serialVersionUID = 923336378712006176L;
    private Integer id;
    private String time;
    private String classi;
    private String title;
    private String magnetLink;
    private String size;
    private String publisher;
    private Integer seedNum;
    private Integer comNum;
    private Integer downNum;
    private String simpleName;
    private Long createTime;

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getClassi() {
        return classi;
    }

    public void setClassi(String classi) {
        this.classi = classi;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMagnetLink() {
        return magnetLink;
    }

    public void setMagnetLink(String magnetLink) {
        this.magnetLink = magnetLink;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getSeedNum() {
        return seedNum;
    }

    public void setSeedNum(Integer seedNum) {
        this.seedNum = seedNum;
    }

    public Integer getComNum() {
        return comNum;
    }

    public void setComNum(Integer comNum) {
        this.comNum = comNum;
    }

    public Integer getDownNum() {
        return downNum;
    }

    public void setDownNum(Integer downNum) {
        this.downNum = downNum;
    }

    @Override
    public String toString() {
        return "DmhyData [id=" + id + ", time=" + time + ", classi=" + classi + ", title=" + title + ", magnetLink="
                + magnetLink + ", size=" + size + ", publisher=" + publisher + ", seedNum=" + seedNum + ", comNum="
                + comNum + ", downNum=" + downNum + ", simpleName=" + simpleName + "]";
    }

    public static final String ID_FIELDNAME = "id";
    public static final String TIME_FIELDNAME = "time";
    public static final String CLASSI_FIELDNAME = "classi";
    public static final String TITLE_FIELDNAME = "title";
    public static final String MAGNETLINK_FIELDNAME = "magnetLink";
    public static final String SIZE_FIELDNAME = "size";
    public static final String SEEDNUM_FIELDNAME = "seedNum";
    public static final String DOWNNUM_FIELDNAME = "downNum";
    public static final String COMNUM_FIELDNAME = "comNum";
    public static final String PUBLISHER_FIELDNAME = "publisher";
    public static final String CREATETIME_FIELDNAME = "create_time";
}
