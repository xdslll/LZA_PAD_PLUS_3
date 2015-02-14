package com.lza.pad.db.model.pad;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/14.
 */
public class PadResource implements Parcelable {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 学校编号
     */
    private String school_bh;

    /**
     * 资源类型
     */
    private String source_type;

    /**
     * 作者
     */
    private String author;

    /**
     * 发布年份
     */
    private String pubdate;

    /**
     * 内容/摘要
     */
    private String contents;

    /**
     * 分类号
     */
    private String clc;

    /**
     * 全文地址
     */
    private String url;

    /**
     * 缩略图
     */
    private String ico;

    /**
     * 图片组
     */
    private String imgs;

    /**
     * ISBN/ISSN
     */
    private String isbn;

    /**
     * 出版社
     */
    private String press;

    /**
     * 全文下载地址
     */
    private String fulltext;

    /**
     * 资源特有的编号
     */
    private String bh;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSchool_bh() {
        return school_bh;
    }

    public void setSchool_bh(String school_bh) {
        this.school_bh = school_bh;
    }

    public String getSource_type() {
        return source_type;
    }

    public void setSource_type(String source_type) {
        this.source_type = source_type;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getClc() {
        return clc;
    }

    public void setClc(String clc) {
        this.clc = clc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIco() {
        return ico;
    }

    public void setIco(String ico) {
        this.ico = ico;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public String getFulltext() {
        return fulltext;
    }

    public void setFulltext(String fulltext) {
        this.fulltext = fulltext;
    }

    public String getBh() {
        return bh;
    }

    public void setBh(String bh) {
        this.bh = bh;
    }

    public PadResource() {}

    public PadResource(Parcel src) {
        id = src.readString();
        title = src.readString();
        school_bh = src.readString();
        source_type = src.readString();
        author = src.readString();
        pubdate = src.readString();
        contents = src.readString();
        clc = src.readString();
        url = src.readString();
        ico = src.readString();
        imgs = src.readString();
        isbn = src.readString();
        press = src.readString();
        fulltext = src.readString();
        bh = src.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(school_bh);
        dest.writeString(source_type);
        dest.writeString(author);
        dest.writeString(pubdate);
        dest.writeString(contents);
        dest.writeString(clc);
        dest.writeString(url);
        dest.writeString(ico);
        dest.writeString(imgs);
        dest.writeString(isbn);
        dest.writeString(press);
        dest.writeString(fulltext);
        dest.writeString(bh);
    }

    public static final Creator<PadResource> CREATOR = new Creator<PadResource>() {
        @Override
        public PadResource createFromParcel(Parcel source) {
            return new PadResource(source);
        }

        @Override
        public PadResource[] newArray(int size) {
            return new PadResource[size];
        }
    };

    /**
     * 资源类型：电子书
     */
    public static final String RESOURCE_EBOOK = "1";

    /**
     * 资源类型：电子教参
     */
    public static final String RESOURCE_EBOOK_JC = "2";

    /**
     * 资源类型：电子期刊
     */
    public static final String RESOURCE_JOURNAL = "3";

    /**
     * 资源类型：图片
     */
    public static final String RESOURCE_IMG = "4";

    /**
     * 资源类型：新闻
     */
    public static final String RESOURCE_NEWS = "5";

    /**
     * 资源类型：新书通知
     */
    public static final String RESOURCE_NEW_BOOK = "6";

    /**
     * 资源类型：热门借阅
     */
    public static final String RESOURCE_HOT_BOOK = "7";

}
