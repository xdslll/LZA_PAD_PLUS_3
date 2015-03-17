package com.lza.pad.db.model.pad._old;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/28/15.
 */
@Deprecated
public class PadJournalArticle implements Parcelable {

    private String id;

    private String system_id;

    private String title;

    private String author;

    private String jigou;

    private String keywords;

    private String contents;

    private String bh;

    private String url;

    private String pubdate;

    private String qi;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSystem_id() {
        return system_id;
    }

    public void setSystem_id(String system_id) {
        this.system_id = system_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getJigou() {
        return jigou;
    }

    public void setJigou(String jigou) {
        this.jigou = jigou;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getBh() {
        return bh;
    }

    public void setBh(String bh) {
        this.bh = bh;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getQi() {
        return qi;
    }

    public void setQi(String qi) {
        this.qi = qi;
    }

    public PadJournalArticle() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.system_id);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeString(this.jigou);
        dest.writeString(this.keywords);
        dest.writeString(this.contents);
        dest.writeString(this.bh);
        dest.writeString(this.url);
        dest.writeString(this.pubdate);
        dest.writeString(this.qi);
    }

    private PadJournalArticle(Parcel in) {
        this.id = in.readString();
        this.system_id = in.readString();
        this.title = in.readString();
        this.author = in.readString();
        this.jigou = in.readString();
        this.keywords = in.readString();
        this.contents = in.readString();
        this.bh = in.readString();
        this.url = in.readString();
        this.pubdate = in.readString();
        this.qi = in.readString();
    }

    public static final Creator<PadJournalArticle> CREATOR = new Creator<PadJournalArticle>() {
        public PadJournalArticle createFromParcel(Parcel source) {
            return new PadJournalArticle(source);
        }

        public PadJournalArticle[] newArray(int size) {
            return new PadJournalArticle[size];
        }
    };
}
